package com.ruoyi.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.device.domain.DmDevice;
import com.ruoyi.device.domain.vo.DmDeviceQueryCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 设备 Mapper 接口
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Mapper
public interface DmDeviceMapper extends BaseMapper<DmDevice> {

    IPage<DmDevice> findAll(@Param("criteria") DmDeviceQueryCriteria criteria, Page<Object> page);

    int dmDeviceAdd(DmDevice dmDevice);

    int updateDmDevice(DmDevice dmDevice);

    int deleteDmDevice(@Param("id") Long id);
}
