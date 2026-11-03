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
import com.ruoyi.device.domain.DmDevice;
import com.ruoyi.device.service.IDmDeviceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备Controller
 * 
 * @author zmkts
 * @date 2026-11-02
 */
@RestController
@RequestMapping("/device/device")
public class DmDeviceController extends BaseController
{
    @Autowired
    private IDmDeviceService dmDeviceService;

    /**
     * 查询设备列表
     */
    @PreAuthorize("@ss.hasPermi('device:device:list')")
    @GetMapping("/list")
    public TableDataInfo list(DmDevice dmDevice)
    {
        startPage();
        List<DmDevice> list = dmDeviceService.selectDmDeviceList(dmDevice);
        return getDataTable(list);
    }

    /**
     * 导出设备列表
     */
    @PreAuthorize("@ss.hasPermi('device:device:export')")
    @Log(title = "设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DmDevice dmDevice)
    {
        List<DmDevice> list = dmDeviceService.selectDmDeviceList(dmDevice);
        ExcelUtil<DmDevice> util = new ExcelUtil<DmDevice>(DmDevice.class);
        util.exportExcel(response, list, "设备数据");
    }

    /**
     * 获取设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:device:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dmDeviceService.selectDmDeviceById(id));
    }

    /**
     * 新增设备
     */
    @PreAuthorize("@ss.hasPermi('device:device:add')")
    @Log(title = "设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DmDevice dmDevice)
    {
        return toAjax(dmDeviceService.insertDmDevice(dmDevice));
    }

    /**
     * 修改设备
     */
    @PreAuthorize("@ss.hasPermi('device:device:edit')")
    @Log(title = "设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DmDevice dmDevice)
    {
        return toAjax(dmDeviceService.updateDmDevice(dmDevice));
    }

    /**
     * 删除设备
     */
    @PreAuthorize("@ss.hasPermi('device:device:remove')")
    @Log(title = "设备", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dmDeviceService.deleteDmDeviceByIds(ids));
    }
}
