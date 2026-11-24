package com.dm.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dm.device.domain.DmAlarm;
import com.dm.device.domain.DmAlarmLog;
import com.dm.user.domain.DmUser;
import com.dm.device.domain.vo.DmAlarmLogQueryCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 告警日志 Mapper 接口
 *
 * @author zmkts
 * @date 2026-11-11
 */
@Mapper
public interface DmAlarmLogMapper extends BaseMapper<DmAlarmLog> {

    IPage<DmAlarmLog> findAll(@Param("criteria") DmAlarmLogQueryCriteria criteria, Page<Object> page);

    int dmAlarmLogAdd(DmAlarmLog dmAlarmLog);

    int updateDmAlarmLog(DmAlarmLog dmAlarmLog);

    int deleteDmAlarmLog(@Param("id") Long id);

    IPage<DmAlarm> selectAlarmByUser(@Param("userId") String userId, @Param("deviceName") String deviceName, Page<Object> page);

    List<DmAlarm> selectAlarmByUser(@Param("userId") String userId, @Param("deviceName") String deviceName);

    List<DmUser> selectUserByAlarm(@Param("alarmId") String alarmId);
}
