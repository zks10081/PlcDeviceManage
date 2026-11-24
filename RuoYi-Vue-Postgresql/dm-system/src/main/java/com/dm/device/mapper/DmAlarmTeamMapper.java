package com.dm.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dm.device.domain.DmAlarmTeam;
import com.dm.device.domain.DmDevice;
import com.dm.user.domain.DmUser;
import com.dm.device.domain.vo.DmAlarmTeamQueryCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 告警队伍配置 Mapper 接口
 *
 * @author zmkts
 * @date 2026-11-10
 */
@Mapper
public interface DmAlarmTeamMapper extends BaseMapper<DmAlarmTeam> {

    IPage<DmAlarmTeam> findAll(@Param("criteria") DmAlarmTeamQueryCriteria criteria, Page<Object> page);

    int dmAlarmTeamAdd(DmAlarmTeam dmAlarmTeam);

    int updateDmAlarmTeam(DmAlarmTeam dmAlarmTeam);

    int deleteDmAlarmTeam(@Param("id") Long id);

    List<Map<String, Object>> selectDeviceSum(@Param("teamId") String teamId);

    List<String> selectTeamUserId(@Param("teamId") String teamId);

    List<DmDevice> selectAlarmDeviceByUser(@Param("userId") String userId);

    List<DmUser> selectAlarmUserByDevice(@Param("deviceId") String deviceId);
}
