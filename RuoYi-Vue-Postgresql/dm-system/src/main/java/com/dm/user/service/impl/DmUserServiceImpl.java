package com.dm.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.common.utils.DataUtil;
import com.dm.user.domain.DmUser;
import com.dm.user.domain.vo.DmUserQueryCriteria;
import com.dm.user.mapper.DmUserMapper;
import com.dm.user.service.IDmUserService;
import com.dm.utils.HttpUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * dm_user 服务实现类
 *
 * @author mxf
 * @date 2026-11-02
 */
@Service
public class DmUserServiceImpl extends ServiceImpl<DmUserMapper, DmUser> implements IDmUserService {

    @Resource
    private DmUserMapper dmUserMapper;

    @Override
    public List<String> dmGroupTransfer() {
        return dmUserMapper.dmGroupTransfer();
    }

    @Override
    public List<String> dmGroupTransferPhone() {
        return dmUserMapper.dmGroupTransferPhone();
    }

    @Override
    public List<String> dmGroupTransferBroadcast() {
        return dmUserMapper.dmGroupTransferBroadcast();
    }

    @Override
    public List<String> dmGroupTransferOuterPhone() {
        return dmUserMapper.dmGroupTransferOuterPhone();
    }

    @Override
    public List<DmUser> listUsers() {
        return dmUserMapper.listUsers();
    }

    @Override
    public IPage<DmUser> queryAll(DmUserQueryCriteria criteria, Page<Object> page) {
        return dmUserMapper.queryAll(criteria, page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncUserData() {
        int size = 999;
        int current = 1;
        Map<String, Object> paramMap = new HashMap<>();
        List<DmUser> userList = new ArrayList<>();
        boolean gt;

        // 1. 分页拉取远程 SIP 用户
        do {
            paramMap.put("size", size);
            paramMap.put("current", current);

            IPage<DmUser> page = HttpUtils.getUserAll(paramMap);
            if (page.getRecords() == null || page.getRecords().isEmpty()) {
                break;
            }
            userList.addAll(page.getRecords());
            // 本次未查完，则增加页数再查询一次
            gt = DataUtil.gt(new BigDecimal(page.getTotal()),
                    new BigDecimal(size).multiply(new BigDecimal(current)));
            current++;
        } while (gt);

        // 2. 安全保护：远程返回为空时不做任何删除，避免误清空本地用户
        if (userList.isEmpty()) {
            throw new RuntimeException("同步失败：远程接口未返回任何用户数据");
        }

        List<DmUser> userListOld = dmUserMapper.selectList(new LambdaQueryWrapper<>());

        // 转为 map 方便比较；保留最后一条，避免 usernum 重复导致 toMap 抛异常
        Map<String, DmUser> collect = userList.stream()
                .filter(u -> u.getUsernum() != null)
                .collect(Collectors.toMap(DmUser::getUsernum, Function.identity(), (a, b) -> b));
        Map<String, DmUser> collectOld = userListOld.stream()
                .filter(u -> u.getUsernum() != null)
                .collect(Collectors.toMap(DmUser::getUsernum, Function.identity(), (a, b) -> b));

        List<DmUser> addList = new ArrayList<>();
        List<DmUser> updateList = new ArrayList<>();
        List<Integer> deleteList = new ArrayList<>();

        // 3. 遍历查找新增和更新
        for (DmUser dmUser : userList) {
            String usernum = dmUser.getUsernum();
            if (collectOld.containsKey(usernum)) {
                DmUser old = collectOld.get(usernum);
                // BUG 修复：eq 为 true 表示字段完全相同，不应更新；不同才需要更新
                if (!dmUser.eq(old)) {
                    // 关键：保留本地主键 id，否则 updateById 因 id 缺失/被远程 id 覆盖而更新失败
                    dmUser.setId(old.getId());
                    updateList.add(dmUser);
                }
            } else {
                // dm_user.id 为业务主键（非空、无自增默认值），新增时必须保留远程 id
                if (dmUser.getId() == null) {
                    throw new RuntimeException("同步失败：远程用户 " + usernum + " 缺少 id，无法新增");
                }
                addList.add(dmUser);
            }
        }

        // 4. 遍历出删除数据
        for (DmUser dmUser : userListOld) {
            if (!collect.containsKey(dmUser.getUsernum())) {
                deleteList.add(dmUser.getId());
            }
        }

        // 5. 执行新增、更新、删除
        if (!addList.isEmpty()) {
            for (DmUser dmUser : addList) {
                dmUserMapper.insert(dmUser);
            }
        }
        if (!updateList.isEmpty()) {
            for (DmUser dmUser : updateList) {
                dmUserMapper.updateById(dmUser);
            }
        }
        if (!deleteList.isEmpty()) {
            dmUserMapper.deleteBatchIds(deleteList);
        }

        return true;
    }
}
