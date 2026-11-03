package com.ruoyi.device.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备点位对象 dm_device_attribute
 * 
 * @author zmkts
 * @date 2026-11-02
 */
@Data
@TableName("dm_device_attribute")
public class DmDeviceAttribute extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 点位名称 */
    @Excel(name = "点位名称")
    private String name;

    /** 设备id */
    @Excel(name = "设备id")
    private Long deviceId;

    /** 设备id */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 控制类型 */
    @Excel(name = "控制类型")
    private String controlType;

    /** 点位类型 */
    @Excel(name = "点位类型")
    private String type;

    /** 设备点位 */
    @Excel(name = "设备点位")
    private String address;

    /** 点位状态 */
    @Excel(name = "点位状态")
    private String status;

    /** 点位数据 */
    @Excel(name = "点位数据")
    private String data;

    /** plc推送数据 */
    @Excel(name = "plc推送数据")
    private String plcData;

    /** 数据采集时间 */
    @Excel(name = "数据采集时间")
    private String collectDate;

    /** 临时字段1 */
    @Excel(name = "临时字段1")
    private String temp1;

    /** 临时字段2 */
    @Excel(name = "临时字段2")
    private String temp2;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public void setControlType(String controlType) 
    {
        this.controlType = controlType;
    }

    public String getControlType() 
    {
        return controlType;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setData(String data) 
    {
        this.data = data;
    }

    public String getData() 
    {
        return data;
    }

    public void setPlcData(String plcData) 
    {
        this.plcData = plcData;
    }

    public String getPlcData() 
    {
        return plcData;
    }

    public void setCollectDate(String collectDate) 
    {
        this.collectDate = collectDate;
    }

    public String getCollectDate() 
    {
        return collectDate;
    }

    public void setTemp1(String temp1) 
    {
        this.temp1 = temp1;
    }

    public String getTemp1() 
    {
        return temp1;
    }

    public void setTemp2(String temp2) 
    {
        this.temp2 = temp2;
    }

    public String getTemp2() 
    {
        return temp2;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("deviceId", getDeviceId())
            .append("controlType", getControlType())
            .append("type", getType())
            .append("address", getAddress())
            .append("status", getStatus())
            .append("data", getData())
            .append("plcData", getPlcData())
            .append("collectDate", getCollectDate())
            .append("temp1", getTemp1())
            .append("temp2", getTemp2())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
