package com.ruoyi.device.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.device.mapper.DmDeviceAttributeMapper;
import com.ruoyi.device.domain.DmDeviceAttribute;
import com.ruoyi.device.service.IDmDeviceAttributeService;

/**
 * 设备点位Service业务层处理
 * 
 * @author zmkts
 * @date 2026-11-02
 */
@Service
public class DmDeviceAttributeServiceImpl extends ServiceImpl<DmDeviceAttributeMapper, DmDeviceAttribute> implements IDmDeviceAttributeService
{
    @Autowired
    private DmDeviceAttributeMapper dmDeviceAttributeMapper;

    /**
     * 查询设备点位
     * 
     * @param id 设备点位主键
     * @return 设备点位
     */
    @Override
    public DmDeviceAttribute selectDmDeviceAttributeById(Long id)
    {
        return dmDeviceAttributeMapper.selectDmDeviceAttributeById(id);
    }

    /**
     * 查询设备点位列表
     * 
     * @param dmDeviceAttribute 设备点位
     * @return 设备点位
     */
    @Override
    public List<DmDeviceAttribute> selectDmDeviceAttributeList(DmDeviceAttribute dmDeviceAttribute)
    {
        return dmDeviceAttributeMapper.selectDmDeviceAttributeList(dmDeviceAttribute);
    }

    /**
     * 新增设备点位
     * 
     * @param dmDeviceAttribute 设备点位
     * @return 结果
     */
    @Override
    public int insertDmDeviceAttribute(DmDeviceAttribute dmDeviceAttribute)
    {
        dmDeviceAttribute.setCreateTime(DateUtils.getNowDate());
        return dmDeviceAttributeMapper.insertDmDeviceAttribute(dmDeviceAttribute);
    }

    /**
     * 修改设备点位
     * 
     * @param dmDeviceAttribute 设备点位
     * @return 结果
     */
    @Override
    public int updateDmDeviceAttribute(DmDeviceAttribute dmDeviceAttribute)
    {
        dmDeviceAttribute.setUpdateTime(DateUtils.getNowDate());
        return dmDeviceAttributeMapper.updateDmDeviceAttribute(dmDeviceAttribute);
    }

    /**
     * 批量删除设备点位
     * 
     * @param ids 需要删除的设备点位主键
     * @return 结果
     */
    @Override
    public int deleteDmDeviceAttributeByIds(Long[] ids)
    {
        return dmDeviceAttributeMapper.deleteDmDeviceAttributeByIds(ids);
    }

    /**
     * 删除设备点位信息
     * 
     * @param id 设备点位主键
     * @return 结果
     */
    @Override
    public int deleteDmDeviceAttributeById(Long id)
    {
        return dmDeviceAttributeMapper.deleteDmDeviceAttributeById(id);
    }
}
