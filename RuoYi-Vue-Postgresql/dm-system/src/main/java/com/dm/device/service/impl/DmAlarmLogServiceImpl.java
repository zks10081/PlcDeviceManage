package com.dm.device.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.device.domain.DmAlarm;
import com.dm.device.domain.DmAlarmLog;
import com.dm.user.domain.DmUser;
import com.dm.device.domain.vo.DmAlarmLogQueryCriteria;
import com.dm.device.mapper.DmAlarmLogMapper;
import com.dm.device.service.IDmAlarmLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 告警日志 服务实现类
 *
 * @author zmkts
 * @date 2026-11-11
 */
@Service
public class DmAlarmLogServiceImpl extends ServiceImpl<DmAlarmLogMapper, DmAlarmLog> implements IDmAlarmLogService {

    @Resource
    private DmAlarmLogMapper dmAlarmLogMapper;

    @Override
    public IPage<DmAlarmLog> findAll(DmAlarmLogQueryCriteria criteria, Page<Object> page) {
        return dmAlarmLogMapper.findAll(criteria, page);
    }

    @Override
    public int dmAlarmLogAdd(DmAlarmLog dmAlarmLog) {
        // dm_alarm_log.id 无数据库默认值（非 BIGSERIAL），自定义 insert 前需显式生成主键
        if (dmAlarmLog.getId() == null) {
            dmAlarmLog.setId(IdWorker.getId());
        }
        return dmAlarmLogMapper.dmAlarmLogAdd(dmAlarmLog);
    }

    @Override
    public int updateDmAlarmLog(DmAlarmLog dmAlarmLog) {
        return dmAlarmLogMapper.updateDmAlarmLog(dmAlarmLog);
    }

    @Override
    public int deleteDmAlarmLog(Long id) {
        return dmAlarmLogMapper.deleteDmAlarmLog(id);
    }

    @Override
    public IPage<DmAlarm> selectAlarmByUser(String userId, String deviceName, Page<Object> page) {
        return dmAlarmLogMapper.selectAlarmByUser(userId, deviceName, page);
    }

    @Override
    public List<DmAlarm> selectAlarmByUser(String userId, String deviceName) {
        return dmAlarmLogMapper.selectAlarmByUser(userId, deviceName);
    }

    @Override
    public List<DmUser> selectUserByAlarm(String alarmId) {
        return dmAlarmLogMapper.selectUserByAlarm(alarmId);
    }

    @Override
    public int chanageReadStatus(String alarmId, String userId) {
        return 1;
    }
}
