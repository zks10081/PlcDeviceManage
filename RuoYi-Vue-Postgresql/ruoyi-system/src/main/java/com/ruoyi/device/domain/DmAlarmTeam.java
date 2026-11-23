package com.ruoyi.device.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.DeviceBaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 告警队伍配置对象 dm_alarm_team
 *
 * @author zmkts
 * @date 2026-11-10
 */
@Data
@TableName("dm_alarm_team")
public class DmAlarmTeam implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键（库表无默认值，插入前由 Service 生成雪花 ID） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 分组名称 */
    @Excel(name = "队伍名称")
    private String teamName;

    /** 负责人id */
    @Excel(name = "负责人id")
    private String leaderId;

    /** 负责人名称 */
    @Excel(name = "负责人名称")
    private String leaderName;

    /** 状态（字典 team_status） */
    @Excel(name = "状态")
    private String status;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 修改人 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;
}
