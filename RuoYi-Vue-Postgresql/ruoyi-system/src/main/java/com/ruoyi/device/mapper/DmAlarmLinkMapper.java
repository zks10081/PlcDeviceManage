package com.ruoyi.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.device.domain.DmAlarmLink;
import com.ruoyi.device.domain.vo.DmAlarmLinkQueryCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 告警队伍关联关系 Mapper 接口
 *
 * @author zmkts
 * @date 2026-11-11
 */
@Mapper
public interface DmAlarmLinkMapper extends BaseMapper<DmAlarmLink> {

    IPage<DmAlarmLink> findAll(@Param("criteria") DmAlarmLinkQueryCriteria criteria, Page<Object> page);

    int dmAlarmLinkAdd(DmAlarmLink dmAlarmLink);

    int updateDmAlarmLink(DmAlarmLink dmAlarmLink);

    int deleteDmAlarmLink(@Param("id") Long id);
}
