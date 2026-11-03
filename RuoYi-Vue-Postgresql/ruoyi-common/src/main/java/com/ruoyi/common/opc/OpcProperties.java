package com.ruoyi.common.opc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "opc")
public class OpcProperties {

    //是否启用
    public static boolean enable;

    //ip地址
    public static String ip;

    //账户
    public static String user;

    //密码
    public static String password;

    //密钥
    public static String code;


}
