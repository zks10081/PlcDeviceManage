package com.dm.device.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.device.domain.DmDevice;
import com.dm.device.domain.vo.DmDeviceQueryCriteria;
import com.dm.device.mapper.DmDeviceMapper;
import com.dm.device.service.IDmDeviceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 设备 服务实现类
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Service
public class DmDeviceServiceImpl extends ServiceImpl<DmDeviceMapper, DmDevice> implements IDmDeviceService {

    @Resource
    private DmDeviceMapper dmDeviceMapper;

    @Override
    public IPage<DmDevice> findAll(DmDeviceQueryCriteria criteria, Page<Object> page) {
        return dmDeviceMapper.findAll(criteria, page);
    }

    @Override
    public int dmDeviceAdd(DmDevice dmDevice) {
        return dmDeviceMapper.insert(dmDevice);
    }

    @Override
    public int updateDmDevice(DmDevice dmDevice) {
        return dmDeviceMapper.updateDmDevice(dmDevice);
    }

    @Override
    public int deleteDmDevice(Long id) {
        return dmDeviceMapper.deleteDmDevice(id);
    }
}
