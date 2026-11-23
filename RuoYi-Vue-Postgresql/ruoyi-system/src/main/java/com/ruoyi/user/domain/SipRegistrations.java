package com.ruoyi.user.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author mxf
 * @apiNote sip_subscriptions 在线状态表
 */
@Data
public class SipRegistrations implements Serializable {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String callId;
    private String sipUser;
    private String sipHost;
    private String presenceHosts;
    private String contact;
    private String status;
    private String pingStatus;
    private Integer pingCount;
    private Integer pingTime;
    private Integer forcePing;
    private String rpid;
    private Integer expires;
    private Integer pingExpires;
    private String userAgent;
    private String serverUser;
    private String serverHost;
    private String profileName;
    private String hostname;
    private String networkIp;
    private String networkPort;
    private String sipUsername;
    private String sipRealm;
    private String mwiUser;
    private String mwiHost;
    private String origServerHost;
    private String origHostname;
    private String subHost;

    public void copy(SipRegistrations source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
