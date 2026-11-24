package com.dm.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.device.domain.DmDeviceAttribute;
import com.dm.device.domain.vo.DmDeviceAttributeQueryCriteria;

import java.util.List;
import java.util.Map;

/**
 * 设备点位 服务类
 *
 * @author zmkts
 * @date 2026-11-02
 */
public interface IDmDeviceAttributeService extends IService<DmDeviceAttribute> {

    /**
     * 分页查询设备点位列表
     * @param criteria 查询条件
     * @param page 分页
     */
    IPage<DmDeviceAttribute> findAll(DmDeviceAttributeQueryCriteria criteria, Page<Object> page);

    /**
     * 添加设备点位
     * @param dmDeviceAttribute 设备点位
     */
    int dmDeviceAttributeAdd(DmDeviceAttribute dmDeviceAttribute);

    /**
     * 修改设备点位
     * @param dmDeviceAttribute 设备点位
     */
    int updateDmDeviceAttribute(DmDeviceAttribute dmDeviceAttribute);

    /**
     * 删除设备点位
     * @param id id
     */
    int deleteDmDeviceAttribute(Long id);

    /**
     * 批量更新plc数据
     * @param list 每项需包含 address、plcData
     */
    int updateBatchPlcData(List<Map<String, String>> list);
}
