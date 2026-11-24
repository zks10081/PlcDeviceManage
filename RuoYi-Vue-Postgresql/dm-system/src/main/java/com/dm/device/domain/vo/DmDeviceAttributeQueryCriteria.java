package com.dm.device.domain.vo;

import lombok.Data;

/**
 * 设备点位 查询条件
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Data
public class DmDeviceAttributeQueryCriteria {

    /** 点位名称 */
    private String name;

    /** 设备id */
    private Long deviceId;

    /** 设备名称 */
    private String deviceName;

    /** 点位类型 */
    private String type;

    /** 点位状态 */
    private String status;
}
