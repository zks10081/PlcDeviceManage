package com.dm.device.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dm.common.annotation.Log;
import com.dm.common.core.controller.BaseController;
import com.dm.common.core.domain.AjaxResult;
import com.dm.common.core.page.TableDataInfo;
import com.dm.common.enums.BusinessType;
import com.dm.common.utils.poi.ExcelUtil;
import com.dm.device.domain.DmDevice;
import com.dm.device.domain.vo.DmDeviceQueryCriteria;
import com.dm.device.service.IDmDeviceService;

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
     * 查询设备列表（MP 分页）
     */
    @PreAuthorize("@ss.hasPermi('device:device:list')")
    @GetMapping("/list")
    public TableDataInfo list(DmDeviceQueryCriteria criteria,
                              @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize)
    {
        Page<Object> page = new Page<>(pageNum, pageSize);
        IPage<DmDevice> result = dmDeviceService.findAll(criteria, page);
        TableDataInfo rspData = new TableDataInfo(result.getRecords(), (int) result.getTotal());
        rspData.setCode(com.dm.common.constant.HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 导出设备列表
     */
    @PreAuthorize("@ss.hasPermi('device:device:export')")
    @Log(title = "设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DmDeviceQueryCriteria criteria)
    {
        List<DmDevice> list = dmDeviceService.findAll(criteria, new Page<>(1, Integer.MAX_VALUE)).getRecords();
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
        return success(dmDeviceService.getById(id));
    }

    /**
     * 新增设备
     */
    @PreAuthorize("@ss.hasPermi('device:device:add')")
    @Log(title = "设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DmDevice dmDevice)
    {
        return toAjax(dmDeviceService.dmDeviceAdd(dmDevice));
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
        return toAjax(dmDeviceService.removeByIds(java.util.Arrays.asList(ids)) ? 1 : 0);
    }
}
