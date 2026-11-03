package com.ruoyi.device.service.impl;

import java.util.*;
import java.util.function.Function;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.device.domain.DmDeviceAttribute;
import com.ruoyi.device.mapper.DmDeviceMapper;
import com.ruoyi.device.domain.DmDevice;
import com.ruoyi.device.service.IDmDeviceService;

/**
 * 设备Service业务层处理
 * 
 * @author zmkts
 * @date 2026-11-02
 */
@Service
public class DmDeviceServiceImpl extends ServiceImpl<DmDeviceMapper, DmDevice> implements IDmDeviceService
{
    @Autowired
    private DmDeviceMapper dmDeviceMapper;

    /**
     * 查询设备
     * 
     * @param id 设备主键
     * @return 设备
     */
    @Override
    public DmDevice selectDmDeviceById(Long id)
    {
        return dmDeviceMapper.selectDmDeviceById(id);
    }

    /**
     * 查询设备列表
     * 
     * @param dmDevice 设备
     * @return 设备
     */
    @Override
    public List<DmDevice> selectDmDeviceList(DmDevice dmDevice)
    {
        return dmDeviceMapper.selectDmDeviceList(dmDevice);
    }

    /**
     * 新增设备
     * 
     * @param dmDevice 设备
     * @return 结果
     */
    @Transactional
    @Override
    public int insertDmDevice(DmDevice dmDevice)
    {
        dmDevice.setCreateTime(DateUtils.getNowDate());
        int rows = dmDeviceMapper.insertDmDevice(dmDevice);
        insertDmDeviceAttribute(dmDevice);
        return rows;
    }

    /**
     * 修改设备
     * 
     * @param dmDevice 设备
     * @return 结果
     */
    @Transactional
    @Override
    public int updateDmDevice(DmDevice dmDevice)
    {
        dmDevice.setUpdateTime(DateUtils.getNowDate());
        dmDeviceMapper.deleteDmDeviceAttributeByDeviceId(dmDevice.getId());
        insertDmDeviceAttribute(dmDevice);
        return dmDeviceMapper.updateDmDevice(dmDevice);
    }

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的设备主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDmDeviceByIds(Long[] ids)
    {
        dmDeviceMapper.deleteDmDeviceAttributeByDeviceIds(ids);
        return dmDeviceMapper.deleteDmDeviceByIds(ids);
    }

    /**
     * 删除设备信息
     * 
     * @param id 设备主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDmDeviceById(Long id)
    {
        dmDeviceMapper.deleteDmDeviceAttributeByDeviceId(id);
        return dmDeviceMapper.deleteDmDeviceById(id);
    }

    /**
     * 新增设备点位信息
     * 
     * @param dmDevice 设备对象
     */
    public void insertDmDeviceAttribute(DmDevice dmDevice)
    {
        List<DmDeviceAttribute> dmDeviceAttributeList = dmDevice.getDmDeviceAttributeList();
        Long id = dmDevice.getId();
        if (StringUtils.isNotNull(dmDeviceAttributeList))
        {
            List<DmDeviceAttribute> list = new ArrayList<DmDeviceAttribute>();
            for (DmDeviceAttribute dmDeviceAttribute : dmDeviceAttributeList)
            {
                dmDeviceAttribute.setDeviceId(id);
                list.add(dmDeviceAttribute);
            }
            if (list.size() > 0)
            {
                dmDeviceMapper.batchDmDeviceAttribute(list);
            }
        }
    }


}
