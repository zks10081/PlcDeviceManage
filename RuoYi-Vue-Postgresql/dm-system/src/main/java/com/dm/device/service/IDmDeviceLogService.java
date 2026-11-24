package com.dm.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.device.domain.DmDeviceAttribute;
import com.dm.device.domain.DmDeviceLog;
import com.dm.device.domain.vo.DmDeviceLogQueryCriteria;

/**
 * 设备点位日志 服务类
 *
 * @author zmkts
 * @date 2026-11-02
 */
public interface IDmDeviceLogService extends IService<DmDeviceLog> {

    /**
     * 分页查询设备点位日志列表
     * @param criteria 查询条件
     * @param page 分页
     */
    IPage<DmDeviceLog> findAll(DmDeviceLogQueryCriteria criteria, Page<Object> page);

    /**
     * 添加设备点位日志
     * @param dmDeviceLog 设备点位日志
     */
    int dmDeviceLogAdd(DmDeviceLog dmDeviceLog);

    /**
     * 修改设备点位日志
     * @param dmDeviceLog 设备点位日志
     */
    int updateDmDeviceLog(DmDeviceLog dmDeviceLog);

    /**
     * 删除设备点位日志
     * @param id id
     */
    int deleteDmDeviceLog(Long id);

    /**
     * 根据点位构建日志实体
     */
    DmDeviceLog getDevicelog(DmDeviceAttribute attribute);
}
