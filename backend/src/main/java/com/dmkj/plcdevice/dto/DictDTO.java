package com.dmkj.plcdevice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class DictDTO implements Serializable {
    private Long id;

    @NotBlank(message = "字典类型编码不能为空")
    private String dictType;

    @NotBlank(message = "字典类型名称不能为空")
    private String dictName;

    private String remark;
}
