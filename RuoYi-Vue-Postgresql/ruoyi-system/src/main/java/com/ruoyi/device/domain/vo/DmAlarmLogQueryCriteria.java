package com.ruoyi.device.domain.vo;

import lombok.Data;

/**
 * 告警日志 查询条件
 *
 * @author zmkts
 * @date 2026-11-11
 */
@Data
public class DmAlarmLogQueryCriteria {

    /** 告警id */
    private Long alarmId;

    /** 用户id */
    private Long userId;

    /** 标记 */
    private String flag;

    /** 创建人 */
    private String createBy;
}
