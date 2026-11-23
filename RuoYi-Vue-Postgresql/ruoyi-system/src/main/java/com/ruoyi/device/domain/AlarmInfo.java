package com.ruoyi.device.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AlarmInfo {

    //设备id
    private String deviceId;
    //设备名称
    private String deviceName;
    //设备报警id
    private String alarmId;
    //报警信息
    private String alarmName;
    //报警内容
    private String alarmContent;
    //报警时间
    private Date alarmDate;
    //通知用户
    private List<String> notiferUsers;
}
