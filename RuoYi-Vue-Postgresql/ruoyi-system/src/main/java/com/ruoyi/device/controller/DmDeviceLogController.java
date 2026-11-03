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
import com.ruoyi.device.domain.DmDeviceLog;
import com.ruoyi.device.service.IDmDeviceLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备点位日志Controller
 * 
 * @author zmkts
 * @date 2026-11-02
 */
@RestController
@RequestMapping("/device/log")
public class DmDeviceLogController extends BaseController
{
    @Autowired
    private IDmDeviceLogService dmDeviceLogService;

    /**
     * 查询设备点位日志列表
     */
    @PreAuthorize("@ss.hasPermi('device:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(DmDeviceLog dmDeviceLog)
    {
        startPage();
        List<DmDeviceLog> list = dmDeviceLogService.selectDmDeviceLogList(dmDeviceLog);
        return getDataTable(list);
    }

    /**
     * 导出设备点位日志列表
     */
    @PreAuthorize("@ss.hasPermi('device:log:export')")
    @Log(title = "设备点位日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DmDeviceLog dmDeviceLog)
    {
        List<DmDeviceLog> list = dmDeviceLogService.selectDmDeviceLogList(dmDeviceLog);
        ExcelUtil<DmDeviceLog> util = new ExcelUtil<DmDeviceLog>(DmDeviceLog.class);
        util.exportExcel(response, list, "设备点位日志数据");
    }

    /**
     * 获取设备点位日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:log:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dmDeviceLogService.selectDmDeviceLogById(id));
    }

    /**
     * 新增设备点位日志
     */
    @PreAuthorize("@ss.hasPermi('device:log:add')")
    @Log(title = "设备点位日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DmDeviceLog dmDeviceLog)
    {
        return toAjax(dmDeviceLogService.insertDmDeviceLog(dmDeviceLog));
    }

    /**
     * 修改设备点位日志
     */
    @PreAuthorize("@ss.hasPermi('device:log:edit')")
    @Log(title = "设备点位日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DmDeviceLog dmDeviceLog)
    {
        return toAjax(dmDeviceLogService.updateDmDeviceLog(dmDeviceLog));
    }

    /**
     * 删除设备点位日志
     */
    @PreAuthorize("@ss.hasPermi('device:log:remove')")
    @Log(title = "设备点位日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dmDeviceLogService.deleteDmDeviceLogByIds(ids));
    }
}
