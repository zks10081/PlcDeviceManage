package com.ruoyi.device.service.impl;

import java.util.*;
import java.util.function.Function;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.device.domain.DmDeviceAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.device.mapper.DmDeviceLogMapper;
import com.ruoyi.device.domain.DmDeviceLog;
import com.ruoyi.device.service.IDmDeviceLogService;

/**
 * 设备点位日志Service业务层处理
 * 
 * @author zmkts
 * @date 2026-11-02
 */
@Service
public class DmDeviceLogServiceImpl extends ServiceImpl<DmDeviceLogMapper, DmDeviceLog> implements IDmDeviceLogService
{
    @Autowired
    private DmDeviceLogMapper dmDeviceLogMapper;

    /**
     * 查询设备点位日志
     * 
     * @param id 设备点位日志主键
     * @return 设备点位日志
     */
    @Override
    public DmDeviceLog selectDmDeviceLogById(Long id)
    {
        return dmDeviceLogMapper.selectDmDeviceLogById(id);
    }

    /**
     * 查询设备点位日志列表
     * 
     * @param dmDeviceLog 设备点位日志
     * @return 设备点位日志
     */
    @Override
    public List<DmDeviceLog> selectDmDeviceLogList(DmDeviceLog dmDeviceLog)
    {
        return dmDeviceLogMapper.selectDmDeviceLogList(dmDeviceLog);
    }

    /**
     * 新增设备点位日志
     * 
     * @param dmDeviceLog 设备点位日志
     * @return 结果
     */
    @Override
    public int insertDmDeviceLog(DmDeviceLog dmDeviceLog)
    {
        dmDeviceLog.setCreateTime(DateUtils.getNowDate());
        return dmDeviceLogMapper.insertDmDeviceLog(dmDeviceLog);
    }

    /**
     * 修改设备点位日志
     * 
     * @param dmDeviceLog 设备点位日志
     * @return 结果
     */
    @Override
    public int updateDmDeviceLog(DmDeviceLog dmDeviceLog)
    {
        dmDeviceLog.setUpdateTime(DateUtils.getNowDate());
        return dmDeviceLogMapper.updateDmDeviceLog(dmDeviceLog);
    }

    /**
     * 批量删除设备点位日志
     * 
     * @param ids 需要删除的设备点位日志主键
     * @return 结果
     */
    @Override
    public int deleteDmDeviceLogByIds(Long[] ids)
    {
        return dmDeviceLogMapper.deleteDmDeviceLogByIds(ids);
    }

    /**
     * 删除设备点位日志信息
     * 
     * @param id 设备点位日志主键
     * @return 结果
     */
    @Override
    public int deleteDmDeviceLogById(Long id)
    {
        return dmDeviceLogMapper.deleteDmDeviceLogById(id);
    }

    @Override
    public DmDeviceLog getDevicelog(DmDeviceAttribute attribute) {
        DmDeviceLog deviceLog = new DmDeviceLog();
        deviceLog.setDeviceId(attribute.getDeviceId());
        deviceLog.setDeviceName(attribute.getDeviceName());
        deviceLog.setAttributeId(attribute.getId());
        deviceLog.setAttributeName(attribute.getName());
        deviceLog.setCollectDate(new Date());
        deviceLog.setData(attribute.getData());
        return deviceLog;
    }


}
