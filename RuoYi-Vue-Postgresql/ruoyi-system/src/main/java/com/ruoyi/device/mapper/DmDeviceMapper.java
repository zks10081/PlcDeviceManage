package com.ruoyi.device.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.device.domain.DmDevice;
import com.ruoyi.device.domain.DmDeviceAttribute;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备Mapper接口
 * 
 * @author zmkts
 * @date 2026-11-02
 */
@Mapper
public interface DmDeviceMapper extends BaseMapper<DmDevice>
{
    /**
     * 查询设备
     * 
     * @param id 设备主键
     * @return 设备
     */
    public DmDevice selectDmDeviceById(Long id);

    /**
     * 查询设备列表
     * 
     * @param dmDevice 设备
     * @return 设备集合
     */
    public List<DmDevice> selectDmDeviceList(DmDevice dmDevice);

    /**
     * 新增设备
     * 
     * @param dmDevice 设备
     * @return 结果
     */
    public int insertDmDevice(DmDevice dmDevice);

    /**
     * 修改设备
     * 
     * @param dmDevice 设备
     * @return 结果
     */
    public int updateDmDevice(DmDevice dmDevice);

    /**
     * 删除设备
     * 
     * @param id 设备主键
     * @return 结果
     */
    public int deleteDmDeviceById(Long id);

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDmDeviceByIds(Long[] ids);

    /**
     * 批量删除设备点位
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDmDeviceAttributeByDeviceIds(Long[] ids);
    
    /**
     * 批量新增设备点位
     * 
     * @param dmDeviceAttributeList 设备点位列表
     * @return 结果
     */
    public int batchDmDeviceAttribute(List<DmDeviceAttribute> dmDeviceAttributeList);
    

    /**
     * 通过设备主键删除设备点位信息
     * 
     * @param id 设备ID
     * @return 结果
     */
    public int deleteDmDeviceAttributeByDeviceId(Long id);
}
