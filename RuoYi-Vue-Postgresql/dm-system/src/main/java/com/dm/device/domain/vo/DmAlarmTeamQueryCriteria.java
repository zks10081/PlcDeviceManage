package com.dm.device.domain.vo;

import lombok.Data;

/**
 * 告警队伍配置 查询条件
 *
 * @author zmkts
 * @date 2026-11-10
 */
@Data
public class DmAlarmTeamQueryCriteria {

    /** 队伍名称 */
    private String teamName;

    /** 负责人id */
    private String leaderId;

    /** 负责人名称 */
    private String leaderName;

    /** 状态（字典 team_status） */
    private String status;

    /** 备注 */
    private String remark;
}
