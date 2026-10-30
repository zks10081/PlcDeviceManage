package com.dmkj.plcdevice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 创建时可空（默认密码），修改时为空表示不修改密码 */
    private String password;

    private String nickname;

    @javax.validation.constraints.Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    @Pattern(regexp = "admin|user", message = "角色只能是 admin 或 user")
    private String role;

    /** 状态：1 启用，0 禁用 */
    private Integer status;
}
