package com.dmkj.plcdevice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_dict_data")
public class DictData implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属字典类型编码 */
    @TableField("dict_type")
    private String dictType;

    /** 显示标签 */
    private String label;

    /** 字典值 */
    private String value;

    /** 排序 */
    private Integer sort = 0;

    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;
}
