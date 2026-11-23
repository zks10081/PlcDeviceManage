package com.ruoyi.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.device.domain.DmDeviceLog;
import com.ruoyi.device.domain.vo.DmDeviceLogQueryCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 设备点位日志 Mapper 接口
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Mapper
public interface DmDeviceLogMapper extends BaseMapper<DmDeviceLog> {

    IPage<DmDeviceLog> findAll(@Param("criteria") DmDeviceLogQueryCriteria criteria, Page<Object> page);

    int dmDeviceLogAdd(DmDeviceLog dmDeviceLog);

    int updateDmDeviceLog(DmDeviceLog dmDeviceLog);

    int deleteDmDeviceLog(@Param("id") Long id);
}
