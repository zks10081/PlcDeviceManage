package com.dm.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dm.device.domain.DmAlarmLink;
import com.dm.device.domain.vo.DmAlarmLinkQueryCriteria;

/**
 * 告警队伍关联关系 服务类
 *
 * @author zmkts
 * @date 2026-11-11
 */
public interface IDmAlarmLinkService extends IService<DmAlarmLink> {

    /** 分页查询关联关系 */
    IPage<DmAlarmLink> findAll(DmAlarmLinkQueryCriteria criteria, Page<Object> page);

    /** 添加关联关系 */
    int dmAlarmLinkAdd(DmAlarmLink dmAlarmLink);

    /** 修改关联关系 */
    int updateDmAlarmLink(DmAlarmLink dmAlarmLink);

    /** 删除关联关系 */
    int deleteDmAlarmLink(Long id);
}
