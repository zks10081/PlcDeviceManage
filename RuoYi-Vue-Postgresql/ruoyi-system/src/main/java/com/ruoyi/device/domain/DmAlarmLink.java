package com.ruoyi.device.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.core.domain.DeviceBaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警队伍关联关系对象 dm_alarm_link
 *
 * @author zmkts
 * @date 2026-11-11
 */
@Data
@TableName("dm_alarm_link")
public class DmAlarmLink extends DeviceBaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键（库表无默认值，插入前由 Service 生成雪花 ID） */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 队伍id（关联 dm_alarm_team.id） */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long teamId;

    /** 成员id：type=user 时为用户id，type=device 时为设备id */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long linkId;

    /** 类型：user / device */
    private String type;
}
