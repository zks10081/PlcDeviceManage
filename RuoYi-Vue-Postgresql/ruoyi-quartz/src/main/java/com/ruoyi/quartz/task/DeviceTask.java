package com.ruoyi.quartz.task;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.*;
import com.ruoyi.device.domain.*;
import com.ruoyi.device.service.*;
import com.ruoyi.user.domain.DmUser;
import com.ruoyi.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Autowired
    private IDmAlarmTeamService alarmTeamService;
    @Autowired
    private IDmAlarmLogService alarmLogService;


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
    public List<String> AlarmType = Arrays.stream("4,5,8".split(",")).collect(Collectors.toList());
    //记录类型
    public List<String> RecordType = Arrays.stream("1,2,4".split(",")).collect(Collectors.toList());
    //记录报警设备，每个设备只快照一份
    public Map<Long, DmDevice> alarmDeviceMap;

    public void attributeDataCalculate(){
        //报警设备初始化
        alarmDeviceMap = new HashMap<>();
        List<DmDeviceAttribute> attributeLists = deviceAttributeService.list(new LambdaQueryWrapper<DmDeviceAttribute>().eq(DmDeviceAttribute::getStatus, "0"));
        BigDecimal size = new BigDecimal(attributeLists.size());

        if(DataUtil.gt(size,ThreadMax.multiply(ThreadChannlMin))){
            ThreadChannlMin = size.divide(ThreadMax,0).add(new BigDecimal(10));
        }

        //切割数据
        List<List<DmDeviceAttribute>> splitList = DataUtil.splitList(attributeLists, ThreadChannlMin.intValue());

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
            if(StrUtil.isEmpty(attribute.getPlcData()))continue;
            //数据有变化，进行更新
            if(!attribute.getPlcData().equals(attribute.getData())){
                attribute.setData(attribute.getPlcData());
                attribute.setUpdateBy("admin");
                attribute.setUpdateTime(DateTimeUtil.toDate(new Date()));
                updateList.add(attribute);

                String type = attribute.getType();
                //日志记录
                if(RecordType.contains(type)){
                    DmDeviceLog devicelog = deviceLogService.getDevicelog(attribute);
                    insertList.add(devicelog);
                }

                //报警处理
                if(AlarmType.contains(type)&&"1".equals(attribute.getPlcData())){
                    //得到报警实体
                    DmAlarm alarm = alarmService.getAlarm(attribute);

                    //保存
                    alarmService.save(alarm);

                    //日志记录
                    DmDeviceLog devicelog = deviceLogService.getDevicelog(attribute);
                    insertList.add(devicelog);

                    //处理报警
                    /**    向服务器发送报警推送     */
                    AlarmInfo info = new AlarmInfo();
                    info.setDeviceId(String.valueOf(attribute.getDeviceId()));
                    info.setDeviceName(attribute.getDeviceName());;
                    info.setAlarmName(StrUtil.format("{}-故障",attribute.getDeviceName()));
                    info.setAlarmContent(attribute.getName());
                    info.setAlarmId(String.valueOf(alarm.getId()));

                    //获取通知人
                    List<String> notiferList = alarmTeamService.selectAlarmUserByDevice(String.valueOf(attribute.getDeviceId()))
                            .stream().map(DmUser::getUsernum).collect(Collectors.toList());
//                    info.setAlarmDate(DataUtil.getDate(DataUtil.DATE_YMD_HMS,"2026-11-04 11:10:00"));
                    info.setAlarmDate(new Date());
                    info.setNotiferUsers(notiferList);

                    //通知用户记录
                    alarmLog(notiferList,alarm.getId());

                    //通过web向相关人员发送报警
                    HttpUtils.sendAlarm(info);
//                    messageHandler.broadcast((ArrayList<String>)info.getNotiferUsers(),info, WebSocketCode.DEVICE_ALARM);

                    //告警快照
                    alarmSnapshoot(alarm,attribute);
                }
            }
        }

        //更新
        deviceAttributeService.updateBatchById(updateList);
        //添加日志
        deviceLogService.saveBatch(insertList);
    }

    public void alarmLog(List<String> list,Long alarmId){
        List<DmAlarmLog> addList = new ArrayList<>();
        for (String userId : list) {
            DmAlarmLog alarmLog = new DmAlarmLog();
            alarmLog.setAlarmId(alarmId);
            alarmLog.setUserId(Long.parseLong(userId));
            alarmLog.setFlag("0");
            alarmLog.setFlagTime(new Date());
            addList.add(alarmLog);
        }
        boolean b = alarmLogService.saveBatch(addList);
    }


    /***
     * 告警设备数据快照
     */
    public void alarmSnapshoot(DmAlarm alarm, DmDeviceAttribute attribute){
        DmDevice device = deviceService.getOne(new LambdaQueryWrapper<DmDevice>().eq(DmDevice::getId,attribute.getDeviceId()));
        //设备只创建一次快照
        if(!alarmDeviceMap.containsKey(alarm.getDeviceId())) {
            //或者同设备下，状态正常的点位
            List<DmDeviceAttribute> deviceAttributeList = deviceAttributeService.list(new LambdaQueryWrapper<DmDeviceAttribute>().eq(DmDeviceAttribute::getDeviceId,attribute.getDeviceId()).eq(DmDeviceAttribute::getStatus,"0"));
            device.setDmDeviceAttributeList(deviceAttributeList);
            //快照编号
            String snapsshootNo = String.valueOf(DataUtil.snowflake());
            List<DmAlarmSnapshoot> snapshootList = alarmSnapshootService.getSnapshoot(device, alarm,snapsshootNo);
            device.setSnapsshootNo(snapsshootNo);
            //保存快照
            alarmSnapshootService.saveBatch(snapshootList);
            alarmDeviceMap.put(attribute.getDeviceId(), device);
        }else {
            //一个设备报警多次清空，快照将拼接报警id
            List<DmAlarmSnapshoot> snapshootList = alarmSnapshootService.list(new LambdaQueryWrapper<DmAlarmSnapshoot>().eq(DmAlarmSnapshoot::getSnapshootNo, device.getSnapsshootNo()));
            for (DmAlarmSnapshoot snapshoot : snapshootList) {
                snapshoot.setAlarmIds(snapshoot.getAlarmIds()+","+alarm.getId());
                if(snapshoot.getAttributeId() == alarm.getAttributeId()){
                    snapshoot.setFlag("1");
                }
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
