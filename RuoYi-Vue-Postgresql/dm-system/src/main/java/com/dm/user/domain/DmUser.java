package com.dm.user.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * dm_user 用户表
 *
 * @author mxf
 * @date 2026-11-02
 */
@Data
public class DmUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private String usernum;
    private String level;
    private String password;
    private String partition;
    private String userState;
    private String recording;

    //广播摄像头
    private String cameraIp;
    private String cameraPort;
    private String cameraAccount;
    private String cameraPassword;
    private String cameraStatus;

    /** 标记（关联查询临时字段） */
    @TableField(exist = false)
    private String temp1;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField(exist = false)
    private Date temp2;

    /**
     * 判断除主键外的业务字段是否完全一致（用于同步时判断是否需要更新）
     */
    public boolean eq(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DmUser dmUser = (DmUser) o;
        return Objects.equals(username, dmUser.username)
                && Objects.equals(usernum, dmUser.usernum)
                && Objects.equals(level, dmUser.level)
                && Objects.equals(password, dmUser.password)
                && Objects.equals(partition, dmUser.partition)
                && Objects.equals(userState, dmUser.userState)
                && Objects.equals(recording, dmUser.recording)
                && Objects.equals(cameraIp, dmUser.cameraIp)
                && Objects.equals(cameraPort, dmUser.cameraPort)
                && Objects.equals(cameraAccount, dmUser.cameraAccount)
                && Objects.equals(cameraPassword, dmUser.cameraPassword)
                && Objects.equals(cameraStatus, dmUser.cameraStatus);
    }

}
