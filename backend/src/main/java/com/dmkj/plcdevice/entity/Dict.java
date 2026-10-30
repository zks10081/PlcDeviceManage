package com.dmkj.plcdevice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_dict")
public class Dict implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 字典类型编码，唯一 */
    @TableField("dict_type")
    private String dictType;

    /** 字典类型名称 */
    @TableField("dict_name")
    private String dictName;

    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;
}
