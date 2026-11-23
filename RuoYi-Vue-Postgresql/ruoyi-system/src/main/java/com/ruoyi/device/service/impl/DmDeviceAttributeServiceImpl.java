package com.ruoyi.device.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.device.domain.DmDeviceAttribute;
import com.ruoyi.device.domain.vo.DmDeviceAttributeQueryCriteria;
import com.ruoyi.device.mapper.DmDeviceAttributeMapper;
import com.ruoyi.device.service.IDmDeviceAttributeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 设备点位 服务实现类
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Service
public class DmDeviceAttributeServiceImpl extends ServiceImpl<DmDeviceAttributeMapper, DmDeviceAttribute> implements IDmDeviceAttributeService {

    @Resource
    private DmDeviceAttributeMapper dmDeviceAttributeMapper;

    @Override
    public IPage<DmDeviceAttribute> findAll(DmDeviceAttributeQueryCriteria criteria, Page<Object> page) {
        return dmDeviceAttributeMapper.findAll(criteria, page);
    }

    @Override
    public int dmDeviceAttributeAdd(DmDeviceAttribute dmDeviceAttribute) {
        return dmDeviceAttributeMapper.insert(dmDeviceAttribute);
    }

    @Override
    public int updateDmDeviceAttribute(DmDeviceAttribute dmDeviceAttribute) {
        return dmDeviceAttributeMapper.updateDmDeviceAttribute(dmDeviceAttribute);
    }

    @Override
    public int deleteDmDeviceAttribute(Long id) {
        return dmDeviceAttributeMapper.deleteDmDeviceAttribute(id);
    }

    @Override
    public int updateBatchPlcData(List<Map<String, String>> list) {
        return dmDeviceAttributeMapper.updateBatchPlcData(list);
    }
}
