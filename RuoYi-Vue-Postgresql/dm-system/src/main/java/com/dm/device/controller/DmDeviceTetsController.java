package com.dm.device.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dm.common.annotation.Anonymous;
import com.dm.common.core.domain.entity.SysDictData;
import com.dm.common.opc.OpcUtil;
import com.dm.common.opc.ua.OpcUaOperationSupport;
import com.dm.common.utils.DataUtil;
import com.dm.common.utils.DictUtils;
import com.dm.common.utils.DateTimeUtil;
import com.dm.common.websocket.vo.WebSocketCode;
import com.dm.device.domain.AlarmInfo;
import com.dm.device.domain.DmAlarm;
import com.dm.device.domain.DmAlarmLog;
import com.dm.device.domain.DmAlarmSnapshoot;
import com.dm.device.domain.DmDevice;
import com.dm.device.domain.DmDeviceAttribute;
import com.dm.user.domain.DmUser;
import com.dm.device.domain.entity.AlarmItem;
import com.dm.device.model.R;
import com.dm.device.model.Result;
import com.dm.device.service.IDmAlarmLogService;
import com.dm.device.service.IDmAlarmService;
import com.dm.device.service.IDmAlarmSnapshootService;
import com.dm.device.service.IDmDeviceAttributeService;
import com.dm.device.service.IDmDeviceService;
import com.dm.common.websocket.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备/告警 APP 端接口（原 dispatch 项目 DmDeviceTetsController 迁移适配版）
 * 权限：@Auth 已替换为 Dm 的 @Anonymous 匿名放行
 */
@Slf4j
@Controller
@RequestMapping("/device")
public class DmDeviceTetsController {

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private OpcUaOperationSupport opcUaOperationSupport;
    @Autowired
    private IDmAlarmLogService alarmLogService;
    @Autowired
    private IDmAlarmService alarmService;
    @Autowired
    private IDmDeviceService deviceService;
    @Autowired
    private IDmDeviceAttributeService deviceAttributeService;
    @Autowired
    private IDmAlarmSnapshootService alarmSnapshootService;


    // 发送报警通知
    @Anonymous
    @PostMapping("/sendAlarm")
    public String sendTextMessage(@RequestBody AlarmInfo alarmInfo) {
        log.info("{} - 设备报警", alarmInfo.getDeviceName());

        ArrayList<String> members = (ArrayList<String>) alarmInfo.getNotiferUsers();
        log.info("报警信息被广播到{}", members);

        // 广播消息
        messageHandler.broadcast(members, alarmInfo, WebSocketCode.DEVICE_ALARM);

        // 消息保存到数据库成功
        log.info("{} - 设备报警信息推送成功：{}", alarmInfo.getDeviceName(), alarmInfo.getAlarmContent());
        return R.success("设备报警信息推送成功");
    }

    // 计算设备点位
    @Anonymous
    @GetMapping("/mathAttribute")
    public String mathAttribute() {
        log.info("计算设备点位 - {}", DataUtil.dateToString(DataUtil.DATE_YMD_HMS, new Date()));

        log.info("计算设备点位 - 执行完毕：{}", DataUtil.dateToString(DataUtil.DATE_YMD_HMS, new Date()));
        return R.success("计算设备点位执行完毕！");
    }

    // opc 读取点位数据
    @Anonymous
    @GetMapping("/plcRead")
    @ResponseBody
    public String plcRead(@RequestBody DmDeviceAttribute attribute, @RequestParam(value = "opcType", required = false, defaultValue = "DA") String opcType) {
        log.info("opc服务({})读取数据 - {}", opcType, DataUtil.dateToString(DataUtil.DATE_YMD_HMS, new Date()));
        String read = "";
        if ("node".equals(opcType)) {
            opcUaOperationSupport.browseNode();
        }
        if ("DA".equals(opcType)) {
            read = OpcUtil.read(attribute.getAddress());
        } else if ("UA".equals(opcType)) {
            read = opcUaOperationSupport.readPLC(2, attribute.getAddress());
        }

        log.info("opc服务读取数据 - {} - 执行完毕：设备：{}，点位：{},数据：{}",
                DataUtil.dateToString(DataUtil.DATE_YMD_HMS, new Date()),
                attribute.getDeviceName(), attribute.getAddress(), read);
        return R.success("opc服务读取数据完毕！", read);
    }

    // opc 写入点位数据
    @Anonymous
    @GetMapping("/plcWrite")
    @ResponseBody
    public String plcWrite(@RequestBody DmDeviceAttribute attribute, @RequestParam(value = "opcType", required = false, defaultValue = "DA") String opcType) {
        log.info("opc服务({})写入数据 - {}", opcType, DataUtil.dateToString(DataUtil.DATE_YMD_HMS, new Date()));
        String write = "false";
        if ("DA".equals(opcType)) {
            Boolean result = OpcUtil.write(attribute.getAddress(), attribute.getData());
            write = String.valueOf(result);
        } else if ("UA".equals(opcType)) {
            write = opcUaOperationSupport.writePLC(2, attribute.getAddress(), attribute.getType(), attribute.getData());
        }

        log.info("opc服务写入数据 - {} - 执行完毕：设备：{}，点位：{},执行结果：{}",
                DataUtil.dateToString(DataUtil.DATE_YMD_HMS, new Date()),
                attribute.getDeviceName(), attribute.getAddress(), write);
        return R.success("opc服务写入数据完毕！", write);
    }

    // 获取设备列表
    @Anonymous
    @ResponseBody
    @GetMapping("/deviceTree")
    public Result<Map<String, List<DmDevice>>> getDeviceList() {
        List<DmDevice> deviceList = deviceService.list();
        Map<String, String> deviceType = selectDictValueToLabel("device_type");
        Map<String, List<DmDevice>> listMap = new HashMap<>();
        for (DmDevice device : deviceList) {
            String type = deviceType.get(device.getType());
            type = DataUtil.isNotNull(type) ? type : "未分类";
            if (listMap.containsKey(type)) {
                listMap.get(type).add(device);
            } else {
                List<DmDevice> list = new ArrayList<>();
                list.add(device);
                listMap.put(type, list);
            }
        }
        return Result.success("数据查询完毕", listMap);
    }

    // 获取设备数据
    @Anonymous
    @ResponseBody
    @GetMapping("/deviceInfo")
    public Result<JSONObject> getDeviceInfo(@RequestParam String deviceId, @RequestParam(value = "type", required = false, defaultValue = "0") String type) {
        JSONObject result = new JSONObject();

        DmDevice device = deviceService.getById(Long.valueOf(deviceId));
        result.put("device", device);

        int alarmCount = (int) alarmService.count(new LambdaQueryWrapper<DmAlarm>().eq(DmAlarm::getDeviceId, Long.valueOf(deviceId)));
        result.put("allCount", alarmCount);

        //日月年数量
        JSONObject alarmCountByDate = new JSONObject();
        if ("1".equals(type)) {
            alarmCountByDate.put("day", getdataByType("day", deviceId, null, 1));
            alarmCountByDate.put("week", getdataByType("day", deviceId, null, 7));
            alarmCountByDate.put("month", getdataByType("day", deviceId, null, 30));
        }
        result.put("dateCount", alarmCountByDate);

        //属性点位
        List<DmDeviceAttribute> switchList = deviceAttributeService.list(new LambdaQueryWrapper<DmDeviceAttribute>()
                .eq(DmDeviceAttribute::getDeviceId, Long.valueOf(deviceId)).eq(DmDeviceAttribute::getStatus, "0")
                .in(DmDeviceAttribute::getType, "1,2,3,4,5,6".split(",")));
        result.put("switch", switchList);
        List<DmDeviceAttribute> dataList = deviceAttributeService.list(new LambdaQueryWrapper<DmDeviceAttribute>()
                .eq(DmDeviceAttribute::getDeviceId, Long.valueOf(deviceId)).eq(DmDeviceAttribute::getStatus, "0")
                .in(DmDeviceAttribute::getType, "20".split(",")));
        result.put("data", dataList);

        return Result.success("数据查询完毕！", result);
    }

    public JSONObject getdataByType(String dateType, String deviceId, LocalDateTime date, Integer count) {
        int size = (count == null || count < 1) ? 1 : count;
        if (date == null) {
            date = LocalDateTime.now();
        }

        //起始时间对齐到统计粒度边界，保证首段统计完整，且统一成完整时间戳供 ::timestamp 解析
        LocalDateTime start;
        switch (dateType) {
            case "hour":
                start = date.truncatedTo(ChronoUnit.HOURS).minusHours(size - 1);
                break;
            case "month":
                start = date.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).minusMonths(size - 1);
                break;
            case "year":
                start = date.withDayOfYear(1).truncatedTo(ChronoUnit.DAYS).minusYears(size - 1);
                break;
            case "day":
            default:
                start = date.toLocalDate().atStartOfDay().minusDays(size - 1);
                break;
        }
        String startStr = DateTimeUtil.toDate(start, "yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> maps = alarmService.alarmCountByDayTrunc(dateType, deviceId, startStr, null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("all", size);
        jsonObject.put("count", maps.size());
        jsonObject.put("data", maps);

        return jsonObject;
    }

    //获取用户的告警列表
    @Anonymous
    @GetMapping("/alarm/selectByUser")
    @ResponseBody
    public String SlectAlarmByUser(@RequestParam String userId, @RequestParam(value = "deviceName", required = false) String deviceName, Page<Object> page) {
        IPage<DmAlarm> dmAlarmIPage = alarmLogService.selectAlarmByUser(userId, deviceName, page);
        IPage<AlarmItem> convert = dmAlarmIPage.convert(e -> {
            if (e == null || e.getId() == null) {
                return null;
            }
            AlarmItem alarmItem = new AlarmItem(e);
            return alarmItem;
        });
        long handerCount = 0;
        long readCount = 0;
        List<DmAlarm> dmAlarms = alarmLogService.selectAlarmByUser(userId, deviceName);
        for (DmAlarm dmAlarm : dmAlarms) {
            if("1".equals(dmAlarm.getStatus())){
                handerCount++;
            }
            if("1".equals(dmAlarm.getTemp1())){
                readCount++;
            }
        }
        JSONObject object = JSONObject.from(convert);
        object.put("handerCount",handerCount);
        object.put("readCount",readCount);
        return R.success("查询告警列表成功！", object);
    }

    //改变用户告警信息状态
    @Anonymous
    @ResponseBody
    @PostMapping("/alarm/changeStatus")
    public Result<Boolean> chanageAlarmStatus(@RequestParam String userId, @RequestParam String alarmId, @RequestParam String type) {
        boolean update = false;
        if ("read".equals(type)) {
            update = alarmLogService.update(new LambdaUpdateWrapper<DmAlarmLog>()
                    .eq(DmAlarmLog::getAlarmId, Long.valueOf(alarmId)).eq(DmAlarmLog::getUserId, Long.valueOf(userId)).set(DmAlarmLog::getFlag, "1").set(DmAlarmLog::getFlagTime, new Date()));
        } else if ("hander".equals(type)) {
            update = alarmService.update(new LambdaUpdateWrapper<DmAlarm>().eq(DmAlarm::getId, Long.valueOf(alarmId))
                    .set(DmAlarm::getStatus, "1").set(DmAlarm::getHandler, userId)
                    .set(DmAlarm::getHandlerTime, new Date()));
        }
        return Result.success("告警信息状态已更正！", update);
    }

    //查询告警详细信息
    @Anonymous
    @ResponseBody
    @GetMapping("/alarm/alarmInfo")
    public Result<JSONObject> getAlarmInfo(@RequestParam String alarmId, @RequestParam(value = "userId", required = false) String userId) {
        //告警数据
        DmAlarm alarm = null;
        if (userId == null || userId.isEmpty()) {
            alarm = alarmService.getById(alarmId);
        } else {
            alarm = alarmLogService.selectAlarmByUser(userId, null)
                    .stream().filter(e -> alarmId.equals(String.valueOf(e.getId()))).findFirst().orElse(null);
        }

        if (alarm == null) {
            return Result.fail("告警信息不存在：" + alarmId);
        }
        JSONObject result = new JSONObject();
        result.put("alarm", alarm);

        //实时点位数据
        List<DmDeviceAttribute> attributeList = deviceAttributeService.list(new LambdaQueryWrapper<DmDeviceAttribute>()
                .eq(DmDeviceAttribute::getDeviceId, alarm.getDeviceId()).eq(DmDeviceAttribute::getStatus, "0"));
        result.put("attributeList", attributeList);

        //快照数据
        List<DmAlarmSnapshoot> snapshootList = alarmSnapshootService.list(new LambdaQueryWrapper<DmAlarmSnapshoot>().eq(DmAlarmSnapshoot::getAlarmId, Long.valueOf(alarmId)));
        result.put("Snapshoot", snapshootList);

        //人员查看详情
        List<DmUser> dmUsers = alarmLogService.selectUserByAlarm(alarmId);
        result.put("user", dmUsers);

        return Result.success("查询完毕！", result);
    }

    /**
     * 原 dispatch DmDictDataService.SelectDictValueToLabel 的 Dm 字典适配实现
     * 从 Dm 字典缓存读取 value -> label 映射
     */
    private Map<String, String> selectDictValueToLabel(String dictType) {
        Map<String, String> map = new HashMap<>();
        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        if (dictDatas != null) {
            for (SysDictData dictData : dictDatas) {
                map.put(dictData.getDictValue(), dictData.getDictLabel());
            }
        }
        return map;
    }
}
