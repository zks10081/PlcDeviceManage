package com.ruoyi.device.controller;

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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.device.domain.DmDeviceAttribute;
import com.ruoyi.device.domain.vo.DmDeviceAttributeQueryCriteria;
import com.ruoyi.device.service.IDmDeviceAttributeService;

/**
 * 设备点位Controller
 *
 * @author zmkts
 * @date 2026-11-02
 */
@RestController
@RequestMapping("/device/attribute")
public class DmDeviceAttributeController extends BaseController
{
    @Autowired
    private IDmDeviceAttributeService dmDeviceAttributeService;

    /**
     * 查询设备点位列表（MP 分页）
     */
    @PreAuthorize("@ss.hasPermi('device:attribute:list')")
    @GetMapping("/list")
    public TableDataInfo list(DmDeviceAttributeQueryCriteria criteria,
                              @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize)
    {
        Page<Object> page = new Page<>(pageNum, pageSize);
        IPage<DmDeviceAttribute> result = dmDeviceAttributeService.findAll(criteria, page);
        TableDataInfo rspData = new TableDataInfo(result.getRecords(), (int) result.getTotal());
        rspData.setCode(com.ruoyi.common.constant.HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 导出设备点位列表
     */
    @PreAuthorize("@ss.hasPermi('device:attribute:export')")
    @Log(title = "设备点位", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DmDeviceAttributeQueryCriteria criteria)
    {
        List<DmDeviceAttribute> list = dmDeviceAttributeService.findAll(criteria, new Page<>(1, Integer.MAX_VALUE)).getRecords();
        ExcelUtil<DmDeviceAttribute> util = new ExcelUtil<DmDeviceAttribute>(DmDeviceAttribute.class);
        util.exportExcel(response, list, "设备点位数据");
    }

    /**
     * 获取设备点位详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:attribute:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dmDeviceAttributeService.getById(id));
    }

    /**
     * 新增设备点位
     */
    @PreAuthorize("@ss.hasPermi('device:attribute:add')")
    @Log(title = "设备点位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DmDeviceAttribute dmDeviceAttribute)
    {
        return toAjax(dmDeviceAttributeService.dmDeviceAttributeAdd(dmDeviceAttribute));
    }

    /**
     * 修改设备点位
     */
    @PreAuthorize("@ss.hasPermi('device:attribute:edit')")
    @Log(title = "设备点位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DmDeviceAttribute dmDeviceAttribute)
    {
        return toAjax(dmDeviceAttributeService.updateDmDeviceAttribute(dmDeviceAttribute));
    }

    /**
     * 删除设备点位
     */
    @PreAuthorize("@ss.hasPermi('device:attribute:remove')")
    @Log(title = "设备点位", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(dmDeviceAttributeService.removeByIds(java.util.Arrays.asList(ids)) ? 1 : 0);
    }
}
