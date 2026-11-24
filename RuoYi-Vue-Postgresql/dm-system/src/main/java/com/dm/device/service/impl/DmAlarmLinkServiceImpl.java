package com.dm.device.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.device.domain.DmAlarmLink;
import com.dm.device.domain.vo.DmAlarmLinkQueryCriteria;
import com.dm.device.mapper.DmAlarmLinkMapper;
import com.dm.device.service.IDmAlarmLinkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 告警队伍关联关系 服务实现类
 *
 * @author zmkts
 * @date 2026-11-11
 */
@Service
public class DmAlarmLinkServiceImpl extends ServiceImpl<DmAlarmLinkMapper, DmAlarmLink> implements IDmAlarmLinkService {

    @Resource
    private DmAlarmLinkMapper dmAlarmLinkMapper;

    @Override
    public IPage<DmAlarmLink> findAll(DmAlarmLinkQueryCriteria criteria, Page<Object> page) {
        return dmAlarmLinkMapper.findAll(criteria, page);
    }

    @Override
    public int dmAlarmLinkAdd(DmAlarmLink dmAlarmLink) {
        // dm_alarm_link.id 无数据库默认值（非 BIGSERIAL），自定义 insert 前需显式生成主键
        if (dmAlarmLink.getId() == null) {
            dmAlarmLink.setId(IdWorker.getId());
        }
        return dmAlarmLinkMapper.dmAlarmLinkAdd(dmAlarmLink);
    }

    @Override
    public int updateDmAlarmLink(DmAlarmLink dmAlarmLink) {
        return dmAlarmLinkMapper.updateDmAlarmLink(dmAlarmLink);
    }

    @Override
    public int deleteDmAlarmLink(Long id) {
        return dmAlarmLinkMapper.deleteDmAlarmLink(id);
    }
}
