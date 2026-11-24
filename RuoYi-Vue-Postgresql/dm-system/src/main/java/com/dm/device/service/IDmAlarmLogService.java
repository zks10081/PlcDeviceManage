package com.dm.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.device.domain.DmAlarm;
import com.dm.device.domain.DmAlarmLog;
import com.dm.user.domain.DmUser;
import com.dm.device.domain.vo.DmAlarmLogQueryCriteria;

import java.util.List;

/**
 * 告警日志 服务类
 *
 * @author zmkts
 * @date 2026-11-11
 */
public interface IDmAlarmLogService extends IService<DmAlarmLog> {

    /** 分页查询告警日志 */
    IPage<DmAlarmLog> findAll(DmAlarmLogQueryCriteria criteria, Page<Object> page);

    /** 添加告警日志 */
    int dmAlarmLogAdd(DmAlarmLog dmAlarmLog);

    /** 修改告警日志 */
    int updateDmAlarmLog(DmAlarmLog dmAlarmLog);

    /** 删除告警日志 */
    int deleteDmAlarmLog(Long id);

    /** 根据用户查询告警及查看状态 */
    IPage<DmAlarm> selectAlarmByUser(String userId, String deviceName, Page<Object> page);

    List<DmAlarm> selectAlarmByUser(String userId, String deviceName);

    /** 根据告警查询用户及查看状态 */
    List<DmUser> selectUserByAlarm(String alarmId);

    int chanageReadStatus(String alarmId, String userId);
}
