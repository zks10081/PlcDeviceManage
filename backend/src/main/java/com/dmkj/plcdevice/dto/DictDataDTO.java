package com.dmkj.plcdevice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class DictDataDTO implements Serializable {
    private Long id;

    @NotBlank(message = "所属字典类型不能为空")
    private String dictType;

    @NotBlank(message = "显示标签不能为空")
    private String label;

    @NotBlank(message = "字典值不能为空")
    private String value;

    private Integer sort;

    private String remark;
}
