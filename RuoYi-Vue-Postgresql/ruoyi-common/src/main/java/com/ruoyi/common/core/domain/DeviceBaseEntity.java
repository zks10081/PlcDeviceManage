package com.ruoyi.common.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author mxf
 * @apiNote  公共填充 配置
 */
@Data
public class DeviceBaseEntity implements Serializable {

    /*创建时间*/
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    /*更新时间*/
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape= JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;
}
