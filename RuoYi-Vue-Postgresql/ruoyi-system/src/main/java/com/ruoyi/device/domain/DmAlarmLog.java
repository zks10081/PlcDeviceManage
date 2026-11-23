package com.ruoyi.device.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.core.domain.DeviceBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 告警日志对象 dm_alarm_log
 *
 * @author zmkts
 * @date 2026-11-11
 */
@Data
@TableName("dm_alarm_log")
public class DmAlarmLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键（库表无默认值，插入前由 Service 生成雪花 ID） */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 告警id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long alarmId;

    /** 用户id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /** 标记（库表默认值 '0'） */
    private String flag = "0";

    /** 标记时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date flagTime;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
