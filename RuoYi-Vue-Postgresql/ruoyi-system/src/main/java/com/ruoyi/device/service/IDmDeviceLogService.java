package com.ruoyi.device.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.device.domain.DmDeviceAttribute;
import com.ruoyi.device.domain.DmDeviceLog;

/**
 * 设备点位日志Service接口
 * 
 * @author zmkts
 * @date 2026-11-02
 */
public interface IDmDeviceLogService extends IService<DmDeviceLog>
{
    /**
     * 查询设备点位日志
     * 
     * @param id 设备点位日志主键
     * @return 设备点位日志
     */
    public DmDeviceLog selectDmDeviceLogById(Long id);

    /**
     * 查询设备点位日志列表
     * 
     * @param dmDeviceLog 设备点位日志
     * @return 设备点位日志集合
     */
    public List<DmDeviceLog> selectDmDeviceLogList(DmDeviceLog dmDeviceLog);

    /**
     * 新增设备点位日志
     * 
     * @param dmDeviceLog 设备点位日志
     * @return 结果
     */
    public int insertDmDeviceLog(DmDeviceLog dmDeviceLog);

    /**
     * 修改设备点位日志
     * 
     * @param dmDeviceLog 设备点位日志
     * @return 结果
     */
    public int updateDmDeviceLog(DmDeviceLog dmDeviceLog);

    /**
     * 批量删除设备点位日志
     * 
     * @param ids 需要删除的设备点位日志主键集合
     * @return 结果
     */
    public int deleteDmDeviceLogByIds(Long[] ids);

    /**
     * 删除设备点位日志信息
     * 
     * @param id 设备点位日志主键
     * @return 结果
     */
    public int deleteDmDeviceLogById(Long id);


    public DmDeviceLog getDevicelog(DmDeviceAttribute attribute);
}
