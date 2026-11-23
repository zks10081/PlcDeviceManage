package com.ruoyi.device.domain.vo;

import lombok.Data;

/**
 * 设备点位日志 查询条件
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Data
public class DmDeviceLogQueryCriteria {

    /** 设备id */
    private Long deviceId;

    /** 设备名称 */
    private String deviceName;

    /** 设备点位id */
    private Long attributeId;

    /** 设备点位名称 */
    private String attributeName;
}
