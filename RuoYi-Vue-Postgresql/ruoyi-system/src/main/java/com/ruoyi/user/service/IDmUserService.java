package com.ruoyi.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.device.domain.DmDeviceAttribute;
import com.ruoyi.user.domain.DmUser;
import com.ruoyi.user.domain.vo.DmUserQueryCriteria;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * dm_user 服务类（告警队伍依赖的用户查询）
 *
 * @author mxf
 * @date 2026-11-02
 */
public interface IDmUserService extends IService<DmUser> {

    /** 座机候选号码 */
    List<String> dmGroupTransfer();

    /** 手机候选号码 */
    List<String> dmGroupTransferPhone();

    /** 广播候选号码 */
    List<String> dmGroupTransferBroadcast();

    /** 外机候选号码 */
    List<String> dmGroupTransferOuterPhone();

    /** 全部用户（负责人下拉） */
    List<DmUser> listUsers();
    /**
     * 查询用户列表
     * @param criteria 查询条件
     * @param page 分页
     */
    IPage<DmUser> queryAll(@Param("criteria") DmUserQueryCriteria criteria, Page<Object> page);

    Boolean syncUserData();
}
