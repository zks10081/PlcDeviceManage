package com.ruoyi.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.device.domain.DmDevice;
import com.ruoyi.device.domain.vo.DmDeviceQueryCriteria;

/**
 * 设备 服务类
 *
 * @author zmkts
 * @date 2026-11-02
 */
public interface IDmDeviceService extends IService<DmDevice> {

    /**
     * 分页查询设备列表
     * @param criteria 查询条件
     * @param page 分页
     */
    IPage<DmDevice> findAll(DmDeviceQueryCriteria criteria, Page<Object> page);

    /**
     * 添加设备
     * @param dmDevice 设备
     */
    int dmDeviceAdd(DmDevice dmDevice);

    /**
     * 修改设备
     * @param dmDevice 设备
     */
    int updateDmDevice(DmDevice dmDevice);

    /**
     * 删除设备
     * @param id id
     */
    int deleteDmDevice(Long id);
}
