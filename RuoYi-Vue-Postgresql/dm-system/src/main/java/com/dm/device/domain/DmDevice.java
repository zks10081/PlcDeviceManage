package com.dm.device.domain;

import java.util.List;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.dm.common.core.domain.DeviceBaseEntity;
import lombok.Data;
import com.dm.common.annotation.Excel;
import com.dm.common.core.domain.BaseEntity;

/**
 * 设备对象 dm_device
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Data
@TableName("dm_device")
public class DmDevice extends DeviceBaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 设备序号 */
    @Excel(name = "设备序号")
    private String serialNo;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String name;

    /** ip */
    @Excel(name = "ip")
    private String ip;

    /** 端口号 */
    private String port;

    /** 最后上线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "最后上线时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastOnline;

    /** 设备类型 */
    @Excel(name = "设备类型")
    private String type;

    /** 设备种类（子类型） */
    private String subtype;

    /** 设备状态（字典 device_status） */
    private String status;

    /** 关联监控id */
    @Excel(name = "关联监控id")
    private Long cameraId;

    /** 设备点位信息 */
    @TableField(exist = false)
    private List<DmDeviceAttribute> dmDeviceAttributeList;

    @TableField(exist = false)
    private List<DmAlarmSnapshoot> snapshootList;

    @TableField(exist = false)
    private String snapsshootNo;
}
