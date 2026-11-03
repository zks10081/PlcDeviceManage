package com.ruoyi.device.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 告警点位快照对象 dm_alarm_snapshoot
 * 
 * @author zmkts
 * @date 2026-11-03
 */
@Data
@TableName("dm_alarm_snapshoot")
public class DmAlarmSnapshoot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
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

    /** 采集日期 */
    @Excel(name = "采集日期")
    private Date collectDate;

    /** 告警id集合 */
    private String alarmIds;

    /** 快照编号 */
    private String snapsshootNo;


}
