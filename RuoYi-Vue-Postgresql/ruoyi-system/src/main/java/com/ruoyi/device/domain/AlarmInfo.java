package com.ruoyi.device.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date alarmDate;
    //通知用户
    private List<String> notiferUsers;
}
