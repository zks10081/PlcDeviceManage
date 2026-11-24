package com.dm.device.domain.vo;

import lombok.Data;

/**
 * 设备 查询条件
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Data
public class DmDeviceQueryCriteria {

    /** 设备名称 */
    private String name;

    /** ip */
    private String ip;

    /** 设备类型 */
    private String type;

    /** 设备种类（子类型） */
    private String subtype;

    /** 设备状态（字典 device_status） */
    private String status;

    /** 关联监控id */
    private Long cameraId;
}
