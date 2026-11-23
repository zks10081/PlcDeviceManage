package com.ruoyi.device.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.device.domain.DmDeviceAttribute;
import com.ruoyi.device.domain.DmDeviceLog;
import com.ruoyi.device.domain.vo.DmDeviceLogQueryCriteria;
import com.ruoyi.device.mapper.DmDeviceLogMapper;
import com.ruoyi.device.service.IDmDeviceLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 设备点位日志 服务实现类
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Service
public class DmDeviceLogServiceImpl extends ServiceImpl<DmDeviceLogMapper, DmDeviceLog> implements IDmDeviceLogService {

    @Resource
    private DmDeviceLogMapper dmDeviceLogMapper;

    @Override
    public IPage<DmDeviceLog> findAll(DmDeviceLogQueryCriteria criteria, Page<Object> page) {
        return dmDeviceLogMapper.findAll(criteria, page);
    }

    @Override
    public int dmDeviceLogAdd(DmDeviceLog dmDeviceLog) {
        return dmDeviceLogMapper.insert(dmDeviceLog);
    }

    @Override
    public int updateDmDeviceLog(DmDeviceLog dmDeviceLog) {
        return dmDeviceLogMapper.updateDmDeviceLog(dmDeviceLog);
    }

    @Override
    public int deleteDmDeviceLog(Long id) {
        return dmDeviceLogMapper.deleteDmDeviceLog(id);
    }

    @Override
    public DmDeviceLog getDevicelog(DmDeviceAttribute attribute) {
        DmDeviceLog deviceLog = new DmDeviceLog();
        deviceLog.setDeviceId(attribute.getDeviceId());
        deviceLog.setDeviceName(attribute.getDeviceName());
        deviceLog.setAttributeId(attribute.getId());
        deviceLog.setAttributeName(attribute.getName());
        deviceLog.setCollectDate(new Date());
        deviceLog.setData(attribute.getData());
        return deviceLog;
    }
}
