package com.ruoyi.device.domain.entity;

import com.ruoyi.device.domain.DmAlarm;
import com.ruoyi.common.utils.DataUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * APP 端告警列表展示项
 *
 * @author zmkts
 * @date 2026-11-11
 */
@Data
public class AlarmItem implements Serializable {

    private static final long serialVersionUID = 1L;

    //id
    private String id;
    //标题
    private String title;
    //条件
    private String condition;
    //时间
    private String time;
    //处理人
    private String handler;
    //设备
    private String device;
    //备注
    private String desc;
    //告警等级
    private String level;
    //读取状态
    private String read;
    //处理状态
    private String handled;

    public AlarmItem() {
    }

    public AlarmItem(DmAlarm alarm) {
        this.id = String.valueOf(alarm.getId());
        this.title = alarm.getTitle();
        this.condition = alarm.getContent();
        this.time = DataUtil.dateToString(DataUtil.DATE_YMD_HMS, alarm.getAlarmTime());
        this.level = alarm.getLevel();
        this.read = alarm.getTemp1();
        this.handled = alarm.getStatus();
        this.handler = alarm.getHandler();
        this.device = alarm.getDeviceName();
        this.desc = alarm.getContent();
    }
}
