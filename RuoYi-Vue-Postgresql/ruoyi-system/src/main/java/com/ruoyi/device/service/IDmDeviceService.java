package com.ruoyi.device.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.device.domain.DmDevice;

/**
 * 设备Service接口
 * 
 * @author zmkts
 * @date 2026-11-02
 */
public interface IDmDeviceService extends IService<DmDevice>
{
    /**
     * 查询设备
     * 
     * @param id 设备主键
     * @return 设备
     */
    public DmDevice selectDmDeviceById(Long id);

    /**
     * 查询设备列表
     * 
     * @param dmDevice 设备
     * @return 设备集合
     */
    public List<DmDevice> selectDmDeviceList(DmDevice dmDevice);

    /**
     * 新增设备
     * 
     * @param dmDevice 设备
     * @return 结果
     */
    public int insertDmDevice(DmDevice dmDevice);

    /**
     * 修改设备
     * 
     * @param dmDevice 设备
     * @return 结果
     */
    public int updateDmDevice(DmDevice dmDevice);

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的设备主键集合
     * @return 结果
     */
    public int deleteDmDeviceByIds(Long[] ids);

    /**
     * 删除设备信息
     * 
     * @param id 设备主键
     * @return 结果
     */
    public int deleteDmDeviceById(Long id);
}
