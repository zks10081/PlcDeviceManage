package com.ruoyi.device.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.DeviceBaseEntity;
import lombok.Data;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 告警对象 dm_alarm
 *
 * @author zmkts
 * @date 2026-11-03
 */
@Data
@TableName("dm_alarm")
public class DmAlarm extends DeviceBaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 告警标题 */
    @Excel(name = "告警标题")
    private String title;

    /** 告警类型 */
    private String type;

    /** 告警等级 */
    @Excel(name = "告警等级")
    private String level;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String status;

    /** 告警内容 */
    private String content;

    /** 报警设备 */
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 点位id */
    private Long attributeId;

    /** 点位名称 */
    @Excel(name = "点位名称")
    private String attributeName;

    /** 报警点位值 */
    private String attributeData;

    /** 通知用户 */
    private String userIds;

    /** 查看状态 */
    private String userFlag;

    /** 处理人 */
    private String handler;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date handlerTime;

    /** 报警时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "报警时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTime;

    /** 用户标记（关联查询临时字段） */
    @TableField(exist = false)
    private String temp1;

    @TableField(exist = false)
    private Date temp2;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
}
