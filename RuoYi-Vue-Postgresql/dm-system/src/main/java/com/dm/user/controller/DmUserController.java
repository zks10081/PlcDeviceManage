package com.dm.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dm.common.annotation.Log;
import com.dm.common.constant.HttpStatus;
import com.dm.common.core.controller.BaseController;
import com.dm.common.core.domain.AjaxResult;
import com.dm.common.core.page.TableDataInfo;
import com.dm.common.enums.BusinessType;
import com.dm.device.service.IDmAlarmLogService;
import com.dm.user.domain.DmUser;
import com.dm.user.domain.vo.DmUserQueryCriteria;
import com.dm.user.service.IDmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * SIP 用户管理
 *
 * @author 15807
 */
@RestController
@RequestMapping("/dmUser")
public class DmUserController extends BaseController {

    @Resource
    private IDmUserService dmUserService;
    @Resource
    private IDmAlarmLogService alarmLogService;

    /**
     * 查询用户列表（Dm 分页）
     */
    @PreAuthorize("@ss.hasPermi('user:dmUser:list')")
    @GetMapping("/list")
    public TableDataInfo list(DmUserQueryCriteria criteria,
                              @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize)
    {
        IPage<DmUser> result = dmUserService.queryAll(criteria, new Page<>(pageNum, pageSize));
        TableDataInfo rspData = new TableDataInfo(result.getRecords(), (int) result.getTotal());
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 原有接口：座机 partition = 座机（保留兼容）
     */
    @GetMapping("/queryAll")
    public TableDataInfo queryAll(DmUserQueryCriteria criteria, Page<Object> page) {
        IPage<DmUser> all = dmUserService.queryAll(criteria, page);
        TableDataInfo rspData = new TableDataInfo(all.getRecords(), (int) all.getTotal());
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 同步 SIP 用户数据
     */
    @PreAuthorize("@ss.hasPermi('user:dmUser:sync')")
    @Log(title = "SIP用户同步", businessType = BusinessType.UPDATE)
    @PostMapping("/syncUserData")
    public AjaxResult syncUserData() {
        try {
            Boolean b = dmUserService.syncUserData();
            return toAjax(b);
        } catch (Exception e) {
            return AjaxResult.error("同步失败：" + e.getMessage());
        }
    }

    /***
     * 查询告警所属用户信息
     * @return
     */
    @Log(title = "查询告警所属用户信息", businessType = BusinessType.UPDATE)
    @PostMapping("/queryAlarmUser")
    public AjaxResult queryAlarmUser(@RequestParam String alarmId){
        List<DmUser> dmUsers = alarmLogService.selectUserByAlarm(alarmId);
        return AjaxResult.success("查询成功！",dmUsers);
    }
}
