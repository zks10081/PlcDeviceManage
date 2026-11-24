package com.dm.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dm.device.domain.DmDeviceAttribute;
import com.dm.device.domain.vo.DmDeviceAttributeQueryCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 设备点位 Mapper 接口
 *
 * @author zmkts
 * @date 2026-11-02
 */
@Mapper
public interface DmDeviceAttributeMapper extends BaseMapper<DmDeviceAttribute> {

    IPage<DmDeviceAttribute> findAll(@Param("criteria") DmDeviceAttributeQueryCriteria criteria, Page<Object> page);

    int dmDeviceAttributeAdd(DmDeviceAttribute dmDeviceAttribute);

    int updateDmDeviceAttribute(DmDeviceAttribute dmDeviceAttribute);

    int deleteDmDeviceAttribute(@Param("id") Long id);

    int updatePlcDataByAddress(@Param("address") String address, @Param("plcData") String plcData);

    /**
     * 按 address 批量更新 plc_data
     * @param list 每项需包含 address、plcData 两个 key
     */
    int updateBatchPlcData(@Param("list") List<Map<String, String>> list);
}
