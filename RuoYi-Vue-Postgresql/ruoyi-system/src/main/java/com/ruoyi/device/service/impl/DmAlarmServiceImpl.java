package com.ruoyi.device.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.ruoyi.common.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.device.domain.DmDeviceAttribute;
import com.ruoyi.device.domain.DmDeviceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import com.ruoyi.device.mapper.DmAlarmMapper;
import com.ruoyi.device.domain.DmAlarm;
import com.ruoyi.device.service.IDmAlarmService;

/**
 * 告警Service业务层处理
 * 
 * @author zmkts
 * @date 2026-11-03
 */
@Service
public class DmAlarmServiceImpl extends ServiceImpl<DmAlarmMapper, DmAlarm> implements IDmAlarmService
{
    @Autowired
    private DmAlarmMapper dmAlarmMapper;

    /**
     * 查询告警
     * 
     * @param id 告警主键
     * @return 告警
     */
    @Override
    public DmAlarm selectDmAlarmByid(Long id)
    {
        return dmAlarmMapper.selectDmAlarmByid(id);
    }

    /**
     * 查询告警列表
     * 
     * @param dmAlarm 告警
     * @return 告警
     */
    @Override
    public List<DmAlarm> selectDmAlarmList(DmAlarm dmAlarm)
    {
        return dmAlarmMapper.selectDmAlarmList(dmAlarm);
    }

    /**
     * 新增告警
     * 
     * @param dmAlarm 告警
     * @return 结果
     */
    @Override
    public int insertDmAlarm(DmAlarm dmAlarm)
    {
        dmAlarm.setCreateTime(DateUtils.getNowDate());
        return dmAlarmMapper.insertDmAlarm(dmAlarm);
    }

    /**
     * 修改告警
     * 
     * @param dmAlarm 告警
     * @return 结果
     */
    @Override
    public int updateDmAlarm(DmAlarm dmAlarm)
    {
        dmAlarm.setUpdateTime(DateUtils.getNowDate());
        return dmAlarmMapper.updateDmAlarm(dmAlarm);
    }

    /**
     * 批量删除告警
     * 
     * @param ids 需要删除的告警主键
     * @return 结果
     */
    @Override
    public int deleteDmAlarmByids(Long[] ids)
    {
        return dmAlarmMapper.deleteDmAlarmByids(ids);
    }

    /**
     * 删除告警信息
     * 
     * @param id 告警主键
     * @return 结果
     */
    @Override
    public int deleteDmAlarmByid(Long id)
    {
        return dmAlarmMapper.deleteDmAlarmByid(id);
    }

    @Override
    public DmAlarm getAlarm(DmDeviceAttribute attribute) {
        DmAlarm alarm = new DmAlarm();
        alarm.setDeviceId(attribute.getDeviceId());
        alarm.setDeviceName(attribute.getDeviceName());
        alarm.setAttributeId(attribute.getId());
        alarm.setAttributeName(attribute.getName());

        alarm.setTitle("");
        alarm.setContent(StringUtils.format("设备：{}的点位：{}告急，请注意！"));
        alarm.setAlarmTime(new Date());

        alarm.setType("0");
        alarm.setLevel("0");
        alarm.setStatus("0");
        return alarm;
    }

    @Override
    public List<Map<String, Object>> alarmCountByDayTrunc(String dateType, String deviceId, String startTime, String endTime) {
        return dmAlarmMapper.alarmCountByDayTrunc(dateType, deviceId, startTime, endTime);
    }
}
