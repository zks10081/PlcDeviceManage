package com.ruoyi.device.mapper;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.device.domain.DmAlarm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 告警Mapper接口
 * 
 * @author zmkts
 * @date 2026-11-03
 */
@Mapper
public interface DmAlarmMapper extends BaseMapper<DmAlarm>
{
    /**
     * 查询告警
     * 
     * @param id 告警主键
     * @return 告警
     */
    public DmAlarm selectDmAlarmByid(Long id);

    /**
     * 查询告警列表
     * 
     * @param dmAlarm 告警
     * @return 告警集合
     */
    public List<DmAlarm> selectDmAlarmList(DmAlarm dmAlarm);

    /**
     * 新增告警
     * 
     * @param dmAlarm 告警
     * @return 结果
     */
    public int insertDmAlarm(DmAlarm dmAlarm);

    /**
     * 修改告警
     * 
     * @param dmAlarm 告警
     * @return 结果
     */
    public int updateDmAlarm(DmAlarm dmAlarm);

    /**
     * 删除告警
     * 
     * @param id 告警主键
     * @return 结果
     */
    public int deleteDmAlarmByid(Long id);

    /**
     * 批量删除告警
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDmAlarmByids(Long[] ids);

    /**
     * 按时间粒度统计告警数（hour/day/month/year）
     */
    public List<Map<String, Object>> alarmCountByDayTrunc(@Param("dateType") String dateType, @Param("deviceId") String deviceId, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
