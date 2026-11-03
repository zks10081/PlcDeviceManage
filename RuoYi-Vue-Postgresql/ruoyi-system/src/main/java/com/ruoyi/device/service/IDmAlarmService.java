package com.ruoyi.device.service;

import java.util.List;
import com.ruoyi.device.domain.DmAlarm;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.device.domain.DmDeviceAttribute;

/**
 * 告警Service接口
 * 
 * @author zmkts
 * @date 2026-11-03
 */
public interface IDmAlarmService extends IService<DmAlarm>
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
     * 批量删除告警
     * 
     * @param ids 需要删除的告警主键集合
     * @return 结果
     */
    public int deleteDmAlarmByids(Long[] ids);

    /**
     * 删除告警信息
     * 
     * @param id 告警主键
     * @return 结果
     */
    public int deleteDmAlarmByid(Long id);

    public DmAlarm getAlarm(DmDeviceAttribute attribute);
}
