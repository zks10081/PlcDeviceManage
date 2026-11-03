package com.ruoyi.device.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 告警对象 dm_alarm
 * 
 * @author zmkts
 * @date 2026-11-03
 */
@Data
@TableName("dm_alarm")
public class DmAlarm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 告警标题 */
    @Excel(name = "告警标题")
    private String title;

    /** 告警类型 */
    private String type;

    /** 告警等级 */
    @Excel(name = "告警等级")
    private String level;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String status;

    /** 告警内容 */
    private String content;

    /** 报警设备 */
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 点位id */
    private Long attributeId;

    /** 点位名称 */
    @Excel(name = "点位名称")
    private String attributeName;

    /** 报警点位值 */
    private String attributeData;

    /** 通知用户 */
    private String userIds;

    /** 查看状态 */
    private String userFlag;

    /** 报警时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报警时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date alarmTime;

    public void setid(Long id)
    {
        this.id = id;
    }

    public Long getid()
    {
        return id;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setLevel(String level) 
    {
        this.level = level;
    }

    public String getLevel() 
    {
        return level;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
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

    public void setAttributeData(String attributeData) 
    {
        this.attributeData = attributeData;
    }

    public String getAttributeData() 
    {
        return attributeData;
    }

    public void setUserIds(String userIds) 
    {
        this.userIds = userIds;
    }

    public String getUserIds() 
    {
        return userIds;
    }

    public void setUserFlag(String userFlag) 
    {
        this.userFlag = userFlag;
    }

    public String getUserFlag() 
    {
        return userFlag;
    }

    public void setAlarmTime(Date alarmTime) 
    {
        this.alarmTime = alarmTime;
    }

    public Date getAlarmTime() 
    {
        return alarmTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getid())
            .append("title", getTitle())
            .append("type", getType())
            .append("level", getLevel())
            .append("status", getStatus())
            .append("content", getContent())
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("attributeId", getAttributeId())
            .append("attributeName", getAttributeName())
            .append("attributeData", getAttributeData())
            .append("userIds", getUserIds())
            .append("userFlag", getUserFlag())
            .append("alarmTime", getAlarmTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
