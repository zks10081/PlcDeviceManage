package com.dm.device.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.device.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import com.dm.device.mapper.DmAlarmSnapshootMapper;
import com.dm.device.service.IDmAlarmSnapshootService;

/**
 * 告警点位快照Service业务层处理
 * 
 * @author zmkts
 * @date 2026-11-03
 */
@Service
public class DmAlarmSnapshootServiceImpl extends ServiceImpl<DmAlarmSnapshootMapper, DmAlarmSnapshoot> implements IDmAlarmSnapshootService
{
    @Autowired
    private DmAlarmSnapshootMapper dmAlarmSnapshootMapper;

    /**
     * 查询告警点位快照
     * 
     * @param id 告警点位快照主键
     * @return 告警点位快照
     */
    @Override
    public DmAlarmSnapshoot selectDmAlarmSnapshootById(Long id)
    {
        return dmAlarmSnapshootMapper.selectDmAlarmSnapshootById(id);
    }

    /**
     * 查询告警点位快照列表
     * 
     * @param dmAlarmSnapshoot 告警点位快照
     * @return 告警点位快照
     */
    @Override
    public List<DmAlarmSnapshoot> selectDmAlarmSnapshootList(DmAlarmSnapshoot dmAlarmSnapshoot)
    {
        return dmAlarmSnapshootMapper.selectDmAlarmSnapshootList(dmAlarmSnapshoot);
    }

    /**
     * 新增告警点位快照
     * 
     * @param dmAlarmSnapshoot 告警点位快照
     * @return 结果
     */
    @Override
    public int insertDmAlarmSnapshoot(DmAlarmSnapshoot dmAlarmSnapshoot)
    {
        return dmAlarmSnapshootMapper.insertDmAlarmSnapshoot(dmAlarmSnapshoot);
    }

    /**
     * 修改告警点位快照
     * 
     * @param dmAlarmSnapshoot 告警点位快照
     * @return 结果
     */
    @Override
    public int updateDmAlarmSnapshoot(DmAlarmSnapshoot dmAlarmSnapshoot)
    {
        return dmAlarmSnapshootMapper.updateDmAlarmSnapshoot(dmAlarmSnapshoot);
    }

    /**
     * 批量删除告警点位快照
     * 
     * @param ids 需要删除的告警点位快照主键
     * @return 结果
     */
    @Override
    public int deleteDmAlarmSnapshootByIds(Long[] ids)
    {
        return dmAlarmSnapshootMapper.deleteDmAlarmSnapshootByIds(ids);
    }

    /**
     * 删除告警点位快照信息
     * 
     * @param id 告警点位快照主键
     * @return 结果
     */
    @Override
    public int deleteDmAlarmSnapshootById(Long id)
    {
        return dmAlarmSnapshootMapper.deleteDmAlarmSnapshootById(id);
    }

    @Override
    public List<DmAlarmSnapshoot> getSnapshoot(DmDevice device, DmAlarm alarm,String snapsshootNo) {
        List<DmAlarmSnapshoot> list = new ArrayList<>();
        for (DmDeviceAttribute attribute : device.getDmDeviceAttributeList()) {
            DmAlarmSnapshoot snapshoot = new DmAlarmSnapshoot();
            snapshoot.setSnapshootNo(snapsshootNo);

            snapshoot.setDeviceId(device.getId());
            snapshoot.setDeviceName(device.getName());

            snapshoot.setAttributeId(attribute.getId());
            snapshoot.setAttributeName(attribute.getName());

            snapshoot.setAlarmId(alarm.getId());
            snapshoot.setAlarmIds(String.valueOf(alarm.getId()));
            snapshoot.setAlarmName(alarm.getTitle());

            snapshoot.setCollectDate(new Date());
            snapshoot.setData(attribute.getData());

            list.add(snapshoot);
        }
        return list;
    }
}
