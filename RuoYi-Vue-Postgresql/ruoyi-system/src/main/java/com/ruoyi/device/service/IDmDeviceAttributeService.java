package com.ruoyi.device.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.device.domain.DmDeviceAttribute;

/**
 * 设备点位Service接口
 * 
 * @author zmkts
 * @date 2026-11-02
 */
public interface IDmDeviceAttributeService extends IService<DmDeviceAttribute>
{
    /**
     * 查询设备点位
     * 
     * @param id 设备点位主键
     * @return 设备点位
     */
    public DmDeviceAttribute selectDmDeviceAttributeById(Long id);

    /**
     * 查询设备点位列表
     * 
     * @param dmDeviceAttribute 设备点位
     * @return 设备点位集合
     */
    public List<DmDeviceAttribute> selectDmDeviceAttributeList(DmDeviceAttribute dmDeviceAttribute);

    /**
     * 新增设备点位
     * 
     * @param dmDeviceAttribute 设备点位
     * @return 结果
     */
    public int insertDmDeviceAttribute(DmDeviceAttribute dmDeviceAttribute);

    /**
     * 修改设备点位
     * 
     * @param dmDeviceAttribute 设备点位
     * @return 结果
     */
    public int updateDmDeviceAttribute(DmDeviceAttribute dmDeviceAttribute);

    /**
     * 批量删除设备点位
     * 
     * @param ids 需要删除的设备点位主键集合
     * @return 结果
     */
    public int deleteDmDeviceAttributeByIds(Long[] ids);

    /**
     * 删除设备点位信息
     * 
     * @param id 设备点位主键
     * @return 结果
     */
    public int deleteDmDeviceAttributeById(Long id);
}
