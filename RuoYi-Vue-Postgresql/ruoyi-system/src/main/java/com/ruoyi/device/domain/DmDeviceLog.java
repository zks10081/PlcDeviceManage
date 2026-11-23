package com.ruoyi.device.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 设备点位日志对象 dm_device_log
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Data
@TableName("dm_device_log")
public class DmDeviceLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 设备id */
    private Long deviceId;

    /** 设备名称 */
    private String deviceName;

    /** 设备点位id */
    private Long attributeId;

    /** 设备点位名称 */
    private String attributeName;

    /** 数据 */
    private String data;

    /** 采集日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date collectDate;
}
