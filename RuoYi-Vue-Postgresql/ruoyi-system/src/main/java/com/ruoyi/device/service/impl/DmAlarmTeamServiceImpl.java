package com.ruoyi.device.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.device.domain.DmAlarmTeam;
import com.ruoyi.device.domain.DmDevice;
import com.ruoyi.user.domain.DmUser;
import com.ruoyi.device.domain.vo.DmAlarmTeamQueryCriteria;
import com.ruoyi.device.mapper.DmAlarmTeamMapper;
import com.ruoyi.device.service.IDmAlarmTeamService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 告警队伍配置 服务实现类
 *
 * @author zmkts
 * @date 2026-11-10
 */
@Service
public class DmAlarmTeamServiceImpl extends ServiceImpl<DmAlarmTeamMapper, DmAlarmTeam> implements IDmAlarmTeamService {

    @Resource
    private DmAlarmTeamMapper dmAlarmTeamMapper;

    @Override
    public IPage<DmAlarmTeam> findAll(DmAlarmTeamQueryCriteria criteria, Page<Object> page) {
        return dmAlarmTeamMapper.findAll(criteria, page);
    }

    @Override
    public int dmAlarmTeamAdd(DmAlarmTeam dmAlarmTeam) {
        // dm_alarm_team.id 无数据库默认值（非 BIGSERIAL），自定义 insert 前需显式生成主键
        if (dmAlarmTeam.getId() == null) {
            dmAlarmTeam.setId(IdWorker.getId());
        }
        return dmAlarmTeamMapper.dmAlarmTeamAdd(dmAlarmTeam);
    }

    @Override
    public int updateDmAlarmTeam(DmAlarmTeam dmAlarmTeam) {
        return dmAlarmTeamMapper.updateDmAlarmTeam(dmAlarmTeam);
    }

    @Override
    public int deleteDmAlarmTeam(Long id) {
        return dmAlarmTeamMapper.deleteDmAlarmTeam(id);
    }

    @Override
    public List<Map<String, Object>> selectDeviceSum(String teamId) {
        return dmAlarmTeamMapper.selectDeviceSum(teamId);
    }

    @Override
    public List<String> selectTeamUserId(String teamId) {
        return dmAlarmTeamMapper.selectTeamUserId(teamId);
    }

    @Override
    public List<DmDevice> selectAlarmDeviceByUser(String userId) {
        return dmAlarmTeamMapper.selectAlarmDeviceByUser(userId);
    }

    @Override
    public List<DmUser> selectAlarmUserByDevice(String deviceId) {
        return dmAlarmTeamMapper.selectAlarmUserByDevice(deviceId);
    }
}
