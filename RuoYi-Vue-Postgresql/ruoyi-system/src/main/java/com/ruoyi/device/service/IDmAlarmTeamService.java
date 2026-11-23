package com.ruoyi.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.device.domain.DmAlarmTeam;
import com.ruoyi.device.domain.DmDevice;
import com.ruoyi.user.domain.DmUser;
import com.ruoyi.device.domain.vo.DmAlarmTeamQueryCriteria;

import java.util.List;
import java.util.Map;

/**
 * 告警队伍配置 服务类
 *
 * @author zmkts
 * @date 2026-11-10
 */
public interface IDmAlarmTeamService extends IService<DmAlarmTeam> {

    /** 分页查询告警队伍配置 */
    IPage<DmAlarmTeam> findAll(DmAlarmTeamQueryCriteria criteria, Page<Object> page);

    /** 添加告警队伍配置 */
    int dmAlarmTeamAdd(DmAlarmTeam dmAlarmTeam);

    /** 修改告警队伍配置 */
    int updateDmAlarmTeam(DmAlarmTeam dmAlarmTeam);

    /** 删除告警队伍配置 */
    int deleteDmAlarmTeam(Long id);

    /** 查找设备选择情况 */
    List<Map<String, Object>> selectDeviceSum(String teamId);

    /** 查询队伍下的用户id */
    List<String> selectTeamUserId(String teamId);

    /** 查询用户下告警设备 */
    List<DmDevice> selectAlarmDeviceByUser(String userId);

    /** 查询设备下的告警用户 */
    List<DmUser> selectAlarmUserByDevice(String deviceId);
}
