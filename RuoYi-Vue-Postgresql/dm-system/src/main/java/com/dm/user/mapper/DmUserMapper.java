package com.dm.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dm.device.domain.DmDeviceAttribute;
import com.dm.user.domain.DmUser;
import com.dm.user.domain.vo.DmUserQueryCriteria;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * dm_user Mapper 接口（仅迁移告警队伍所需查询）
 *
 * @author mxf
 * @date 2026-11-02
 */
@Mapper
public interface DmUserMapper  extends BaseMapper<DmUser> {

    /** 座机候选 */
    List<String> dmGroupTransfer();

    /** 手机候选 */
    List<String> dmGroupTransferPhone();

    /** 广播候选 */
    List<String> dmGroupTransferBroadcast();

    /** 外机候选 */
    List<String> dmGroupTransferOuterPhone();

    /** 负责人候选（全部用户） */
    List<DmUser> listUsers();

    IPage<DmUser> queryAll(@Param("criteria") DmUserQueryCriteria criteria, Page<Object> page);
}
