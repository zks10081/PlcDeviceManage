package com.dm.device.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.dm.common.core.domain.DeviceBaseEntity;
import lombok.Data;
import com.dm.common.annotation.Excel;
import com.dm.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 设备点位对象 dm_device_attribute
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Data
@TableName("dm_device_attribute")
public class DmDeviceAttribute extends DeviceBaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 点位名称 */
    @Excel(name = "点位名称")
    private String name;

    /** 设备id */
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 控制类型 */
    private String controlType;

    /** 点位类型 */
    private String type;

    /** 设备点位 */
    private String address;

    /** 点位状态 */
    private String status;

    /** 点位数据 */
    private String data;

    /** plc推送数据 */
    private String plcData;

    /** 数据采集时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date collectDate;

    /** 临时字段1 */
    private String temp1;

    /** 临时字段2 */
    private String temp2;

    private String updateBy;

    private String createBy;
}
