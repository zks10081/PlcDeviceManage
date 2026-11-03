package com.ruoyi.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.MathUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.device.domain.*;
import com.ruoyi.device.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@Slf4j
@Component("DeviceTask")
public class DeviceTask {

    @Autowired
    private IDmDeviceService deviceService;

    @Autowired
    private IDmDeviceAttributeService deviceAttributeService;

    @Autowired
    private IDmDeviceLogService deviceLogService;
    @Autowired
    private IDmAlarmService alarmService;
    @Autowired
    private IDmAlarmSnapshootService alarmSnapshootService;

    //线程最大数量
    private BigDecimal ThreadMax = new BigDecimal(8);
    //单个线程最小数量
    private BigDecimal ThreadChannlMin = new BigDecimal(50);
    // 定义线程池，参数根据服务器调整
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4,          // 核心线程数
            8,          // 最大线程数
            10L,        // 空闲线程存活时间
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100), // 等待队列
            new ThreadPoolExecutor.CallerRunsPolicy() // 队列满了，主线程自己执行，防止丢数据
    );

    //报警类型
    List<String> AlarmType = Arrays.stream("4,8".split(",")).collect(Collectors.toList());
    //记录类型
    List<String> RecordType = Arrays.stream("4,8".split(",")).collect(Collectors.toList());
    //记录报警设备，每个设备只快照一份
    Map<Long, DmDevice> alarmDeviceMap;
    List<DmDevice> deviceList;


    /***
     * 设备点位值进行计算转录
     */
    public void attributeDataCalculate(){
        //报警设备初始化
        alarmDeviceMap = new HashMap<>();
        deviceList = deviceService.selectDmDeviceList(new DmDevice());
        List<DmDeviceAttribute> attributeLists = deviceAttributeService.list(new LambdaQueryWrapper<DmDeviceAttribute>().eq(DmDeviceAttribute::getStatus, "Y"));
        BigDecimal size = new BigDecimal(attributeLists.size()); size = new BigDecimal(attributeLists.size());

        if(MathUtils.gt(size,ThreadMax.multiply(ThreadChannlMin))){
            ThreadChannlMin = size.divide(ThreadMax,0).add(new BigDecimal(10));
        }

        //切割数据
        List<List<DmDeviceAttribute>> splitList = ListUtils.splitList(attributeLists, ThreadChannlMin.intValue());

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (List<DmDeviceAttribute> subList : splitList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // 子分片执行批量更新
                AnsyUpdate(subList);
            }, executor).exceptionally(ex ->{
                // 记录日志 ex.printStackTrace()
                log.error("分片更新失败", ex);
                return null;
            });
            futureList.add(future);
        }

        // 等待所有任务全部完成，任意一个异常会抛出
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();


    }

    /***
     * 更新运算转录逻辑
     * @param list
     */
    public void AnsyUpdate(List<DmDeviceAttribute> list){
        List<DmDeviceAttribute> updateList = new ArrayList<>();
        List<DmDeviceLog> insertList = new ArrayList<>();
        for (DmDeviceAttribute attribute : list) {
            //plc读数为null，则不处理
            if(StringUtils.isEmpty(attribute.getPlcData()))continue;
            //数据有变化，进行更新
            if(!attribute.getPlcData().equals(attribute.getData())){
                attribute.setData(attribute.getPlcData());
                attribute.setUpdateBy("admin");
                attribute.setUpdateTime(new Date());
                updateList.add(attribute);

                String type = attribute.getType();
                //日志记录
                if(RecordType.contains(type)){
                    DmDeviceLog devicelog = deviceLogService.getDevicelog(attribute);
                    insertList.add(devicelog);
                }
                //报警处理
                if(AlarmType.contains(type)){
                    //得到报警实体
                    DmAlarm alarm = alarmService.getAlarm(attribute);
                    //处理报警
                    /**    向服务器发送报警推送     */

                    //保存
                    alarmService.save(alarm);
                    //告警快照
                    alarmSnapshoot(alarm,attribute);
                }
            }
        }

        //更新
        deviceAttributeService.updateBatchById(updateList);
        //添加日志
        deviceLogService.saveOrUpdateBatch(insertList);
    }


    /***
     * 告警设备数据快照
     */
    public void alarmSnapshoot(DmAlarm alarm,DmDeviceAttribute attribute){ //设备只创建一次快照
        if(alarmDeviceMap.containsKey(alarm.getDeviceId())) {
            DmDevice device = deviceList.stream().filter(e -> e.equals(attribute.getDeviceId())).findFirst().orElse(null);
            //快照编号
            String snapsshootNo = MathUtils.timestamp4Random();
            List<DmAlarmSnapshoot> snapshootList = alarmSnapshootService.getSnapshoot(device, alarm,snapsshootNo);
            device.setSnapsshootNo(snapsshootNo);
            //保存快照
            alarmSnapshootService.saveOrUpdateBatch(snapshootList);
            alarmDeviceMap.put(attribute.getDeviceId(), device);
        }else {
            //一个设备报警多次清空，快照将拼接报警id
            DmDevice device = alarmDeviceMap.get(attribute.getDeviceId());
            List<DmAlarmSnapshoot> snapshootList = alarmSnapshootService.list(new LambdaQueryWrapper<DmAlarmSnapshoot>().eq(DmAlarmSnapshoot::getSnapsshootNo, device.getSnapsshootNo()));
            for (DmAlarmSnapshoot snapshoot : snapshootList) {
                snapshoot.setAlarmIds(snapshoot.getAlarmIds()+","+alarm.getid());
            }
            //更新快照
            alarmSnapshootService.updateBatchById(snapshootList);
        }

    }


    //项目销毁时
    @PreDestroy
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
