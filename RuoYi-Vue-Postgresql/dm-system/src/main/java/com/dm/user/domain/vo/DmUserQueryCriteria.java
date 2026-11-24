package com.dm.user.domain.vo;

import lombok.Data;

/**
 * @author mxf
 * @apiNote  查询条件实体类
 */
@Data
public class DmUserQueryCriteria {
    /** 分机用户 */
    private String username;
    /** 在线 1在 0不在 */
    private String type;
    /** 类型 */
    private String partition;
}