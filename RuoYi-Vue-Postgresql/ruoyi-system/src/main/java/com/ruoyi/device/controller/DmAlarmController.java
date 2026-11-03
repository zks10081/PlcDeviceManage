package com.ruoyi.device.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.device.domain.DmAlarm;
import com.ruoyi.device.service.IDmAlarmService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 告警Controller
 * 
 * @author zmkts
 * @date 2026-11-03
 */
@RestController
@RequestMapping("/device/alarm")
public class DmAlarmController extends BaseController
{
    @Autowired
    private IDmAlarmService dmAlarmService;

    /**
     * 查询告警列表
     */
    @PreAuthorize("@ss.hasPermi('device:alarm:list')")
    @GetMapping("/list")
    public TableDataInfo list(DmAlarm dmAlarm)
    {
        startPage();
        List<DmAlarm> list = dmAlarmService.selectDmAlarmList(dmAlarm);
        return getDataTable(list);
    }

    /**
     * 导出告警列表
     */
    @PreAuthorize("@ss.hasPermi('device:alarm:export')")
    @Log(title = "告警", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DmAlarm dmAlarm)
    {
        List<DmAlarm> list = dmAlarmService.selectDmAlarmList(dmAlarm);
        ExcelUtil<DmAlarm> util = new ExcelUtil<DmAlarm>(DmAlarm.class);
        util.exportExcel(response, list, "告警数据");
    }

    /**
     * 获取告警详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:alarm:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dmAlarmService.selectDmAlarmByid(id));
    }

    /**
     * 新增告警
     */
    @PreAuthorize("@ss.hasPermi('device:alarm:add')")
    @Log(title = "告警", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DmAlarm dmAlarm)
    {
        return toAjax(dmAlarmService.insertDmAlarm(dmAlarm));
    }

    /**
     * 修改告警
     */
    @PreAuthorize("@ss.hasPermi('device:alarm:edit')")
    @Log(title = "告警", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DmAlarm dmAlarm)
    {
        return toAjax(dmAlarmService.updateDmAlarm(dmAlarm));
    }

    /**
     * 删除告警
     */
    @PreAuthorize("@ss.hasPermi('device:alarm:remove')")
    @Log(title = "告警", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dmAlarmService.deleteDmAlarmByids(ids));
    }
}
