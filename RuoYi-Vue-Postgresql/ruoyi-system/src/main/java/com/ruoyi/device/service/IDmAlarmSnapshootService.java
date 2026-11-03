package com.ruoyi.device.service;

import java.util.List;

import com.ruoyi.device.domain.*;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 告警点位快照Service接口
 * 
 * @author zmkts
 * @date 2026-11-03
 */
public interface IDmAlarmSnapshootService extends IService<DmAlarmSnapshoot>
{
    /**
     * 查询告警点位快照
     * 
     * @param id 告警点位快照主键
     * @return 告警点位快照
     */
    public DmAlarmSnapshoot selectDmAlarmSnapshootById(Long id);

    /**
     * 查询告警点位快照列表
     * 
     * @param dmAlarmSnapshoot 告警点位快照
     * @return 告警点位快照集合
     */
    public List<DmAlarmSnapshoot> selectDmAlarmSnapshootList(DmAlarmSnapshoot dmAlarmSnapshoot);

    /**
     * 新增告警点位快照
     * 
     * @param dmAlarmSnapshoot 告警点位快照
     * @return 结果
     */
    public int insertDmAlarmSnapshoot(DmAlarmSnapshoot dmAlarmSnapshoot);

    /**
     * 修改告警点位快照
     * 
     * @param dmAlarmSnapshoot 告警点位快照
     * @return 结果
     */
    public int updateDmAlarmSnapshoot(DmAlarmSnapshoot dmAlarmSnapshoot);

    /**
     * 批量删除告警点位快照
     * 
     * @param ids 需要删除的告警点位快照主键集合
     * @return 结果
     */
    public int deleteDmAlarmSnapshootByIds(Long[] ids);

    /**
     * 删除告警点位快照信息
     * 
     * @param id 告警点位快照主键
     * @return 结果
     */
    public int deleteDmAlarmSnapshootById(Long id);


    public List<DmAlarmSnapshoot> getSnapshoot(DmDevice device, DmAlarm alarm,String snapsshootNo);
}
