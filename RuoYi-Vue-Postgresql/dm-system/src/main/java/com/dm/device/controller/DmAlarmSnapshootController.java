package com.dm.device.controller;

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
import com.dm.common.annotation.Log;
import com.dm.common.core.controller.BaseController;
import com.dm.common.core.domain.AjaxResult;
import com.dm.common.enums.BusinessType;
import com.dm.device.domain.DmAlarmSnapshoot;
import com.dm.device.service.IDmAlarmSnapshootService;
import com.dm.common.utils.poi.ExcelUtil;
import com.dm.common.core.page.TableDataInfo;

/**
 * 告警点位快照Controller
 * 
 * @author zmkts
 * @date 2026-11-03
 */
@RestController
@RequestMapping("/device/snapshoot")
public class DmAlarmSnapshootController extends BaseController
{
    @Autowired
    private IDmAlarmSnapshootService dmAlarmSnapshootService;

    /**
     * 查询告警点位快照列表
     */
    @PreAuthorize("@ss.hasPermi('device:snapshoot:list')")
    @GetMapping("/list")
    public TableDataInfo list(DmAlarmSnapshoot dmAlarmSnapshoot)
    {
        startPage();
        List<DmAlarmSnapshoot> list = dmAlarmSnapshootService.selectDmAlarmSnapshootList(dmAlarmSnapshoot);
        return getDataTable(list);
    }

    /**
     * 导出告警点位快照列表
     */
    @PreAuthorize("@ss.hasPermi('device:snapshoot:export')")
    @Log(title = "告警点位快照", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DmAlarmSnapshoot dmAlarmSnapshoot)
    {
        List<DmAlarmSnapshoot> list = dmAlarmSnapshootService.selectDmAlarmSnapshootList(dmAlarmSnapshoot);
        ExcelUtil<DmAlarmSnapshoot> util = new ExcelUtil<DmAlarmSnapshoot>(DmAlarmSnapshoot.class);
        util.exportExcel(response, list, "告警点位快照数据");
    }

    /**
     * 获取告警点位快照详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:snapshoot:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dmAlarmSnapshootService.selectDmAlarmSnapshootById(id));
    }

    /**
     * 新增告警点位快照
     */
    @PreAuthorize("@ss.hasPermi('device:snapshoot:add')")
    @Log(title = "告警点位快照", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DmAlarmSnapshoot dmAlarmSnapshoot)
    {
        return toAjax(dmAlarmSnapshootService.insertDmAlarmSnapshoot(dmAlarmSnapshoot));
    }

    /**
     * 修改告警点位快照
     */
    @PreAuthorize("@ss.hasPermi('device:snapshoot:edit')")
    @Log(title = "告警点位快照", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DmAlarmSnapshoot dmAlarmSnapshoot)
    {
        return toAjax(dmAlarmSnapshootService.updateDmAlarmSnapshoot(dmAlarmSnapshoot));
    }

    /**
     * 删除告警点位快照
     */
    @PreAuthorize("@ss.hasPermi('device:snapshoot:remove')")
    @Log(title = "告警点位快照", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dmAlarmSnapshootService.deleteDmAlarmSnapshootByIds(ids));
    }
}
