package com.ruoyi.common.opc;

import com.ruoyi.common.utils.HttpClientUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * OPC 配置。
 * 字段保持 static 供非Spring管理的类（new OpcDA()）直接访问；
 * Spring 绑定走下方非静态 getter/setter，把 yml 值写入静态字段。
 */
@Component
@ConfigurationProperties(prefix = "opc")
public class OpcProperties {

    //是否启用
    public static boolean enable;

    //ip地址
    public static String ip;

    //端口
    public static String port;

    //是否开启日志
    public static boolean islog = false;

    //账户
    public static String user;

    //密码
    public static String password;

    //密钥
    public static String code;



    //调度Ip
    public static String dispatchIp;

    //调度端口
    public static String dispatchPort;

    // ---------------- Spring 绑定入口 ----------------

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { OpcProperties.enable = enable; }

    public String getIp() { return ip; }
    public void setIp(String ip) { OpcProperties.ip = ip; }

    public String getPort() { return port; }
    public void setPort(String port) { OpcProperties.port = port; }

    public boolean getIsLog() { return islog; }
    public void setIsLog(boolean islog) { OpcProperties.islog = islog; }

    public String getUser() { return user; }
    public void setUser(String user) { OpcProperties.user = user; }

    public String getPassword() { return password; }
    public void setPassword(String password) { OpcProperties.password = password; }

    public String getCode() { return code; }
    public void setCode(String code) { OpcProperties.code = code; }

    public String getDispatchIp() {  return dispatchIp; }
    public void setDispatchIp(String dispatchIp) { OpcProperties.dispatchIp = dispatchIp; }

    public String getDispatchPort() { return dispatchPort; }
    public void setDispatchPort(String dispatchPort) { OpcProperties.dispatchPort = dispatchPort; }

    /** 配置绑定完成后，项目启动时初始化远程系统 token */
    @PostConstruct
    public void initHttpClientToken() {
        HttpClientUtils.initToken();
    }
}
