package com.dm.device.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.dm.common.core.domain.DeviceBaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.dm.common.annotation.Excel;
import com.dm.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 告警点位快照对象 dm_alarm_snapshoot
 * 
 * @author zmkts
 * @date 2026-11-03
 */
@Data
@TableName("dm_alarm_snapshoot")
public class DmAlarmSnapshoot extends DeviceBaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 告警id */
    private Long alarmId;

    /** 告警标题 */
    @Excel(name = "告警标题")
    private String alarmName;

    /** 设备id */
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 设备点位id */
    private Long attributeId;

    /** 设备点位名称 */
    @Excel(name = "设备点位名称")
    private String attributeName;

    /** 数据 */
    @Excel(name = "数据")
    private String data;

    /** 告警标记 */
    private String flag;

    /** 采集日期 */
    @Excel(name = "采集日期")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date collectDate;

    /** 告警id集合 */
    private String alarmIds;

    /** 快照编号 */
    private String snapshootNo;


}
