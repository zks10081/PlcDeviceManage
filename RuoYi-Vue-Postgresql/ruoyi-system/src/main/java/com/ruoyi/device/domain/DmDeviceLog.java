package com.ruoyi.device.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 设备点位日志对象 dm_device_log
 * 
 * @author zmkts
 * @date 2026-11-02
 */
@Data
@TableName("dm_device_log")
public class DmDeviceLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 设备id */
    @Excel(name = "设备id")
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 设备点位id */
    @Excel(name = "设备点位id")
    private Long attributeId;

    /** 设备点位名称 */
    @Excel(name = "设备点位名称")
    private String attributeName;

    /** 数据 */
    @Excel(name = "数据")
    private String data;

    /** 采集日期 */
    @Excel(name = "采集日期")
    private Date collectDate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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

    public String getDeviceName() 
    {
        return deviceName;
    }

    public void setAttributeId(Long attributeId) 
    {
        this.attributeId = attributeId;
    }

    public Long getAttributeId() 
    {
        return attributeId;
    }

    public void setAttributeName(String attributeName) 
    {
        this.attributeName = attributeName;
    }

    public String getAttributeName() 
    {
        return attributeName;
    }

    public void setData(String data) 
    {
        this.data = data;
    }

    public String getData() 
    {
        return data;
    }

    public void setCollectDate(Date collectDate)
    {
        this.collectDate = collectDate;
    }

    public Date getCollectDate()
    {
        return collectDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("attributeId", getAttributeId())
            .append("attributeName", getAttributeName())
            .append("data", getData())
            .append("collectDate", getCollectDate())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
