package com.dm.device.domain.vo;

import lombok.Data;

/**
 * 告警队伍关联关系 查询条件
 *
 * @author zmkts
 * @date 2026-11-11
 */
@Data
public class DmAlarmLinkQueryCriteria {

    /** 队伍id */
    private Long teamId;

    /** 成员id */
    private Long linkId;

    /** 类型：user / device */
    private String type;
}
