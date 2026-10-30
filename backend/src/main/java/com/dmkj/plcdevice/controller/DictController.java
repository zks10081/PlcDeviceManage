package com.dmkj.plcdevice.controller;

import com.dmkj.plcdevice.common.Result;
import com.dmkj.plcdevice.dto.DictDTO;
import com.dmkj.plcdevice.dto.DictDataDTO;
import com.dmkj.plcdevice.entity.Dict;
import com.dmkj.plcdevice.entity.DictData;
import com.dmkj.plcdevice.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dicts")
public class DictController {

    @Autowired
    private DictService dictService;

    /* 字典类型 */
    @GetMapping
    public Result<?> pageDict(@RequestParam(required = false) String dictType,
                              @RequestParam(required = false) String dictName,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        return Result.success(dictService.pageDict(dictType, dictName, page, size));
    }

    @GetMapping("/options")
    public Result<List<Dict>> options() {
        return Result.success(dictService.options());
    }

    @GetMapping("/{id}")
    public Result<Dict> getDict(@PathVariable Long id) {
        return Result.success(dictService.getDict(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Dict> createDict(@RequestBody DictDTO dto) {
        return Result.success(dictService.createDict(dto));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Dict> updateDict(@RequestBody DictDTO dto) {
        return Result.success(dictService.updateDict(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteDict(@PathVariable Long id) {
        dictService.deleteDict(id);
        return Result.success();
    }

    /* 字典数据 */
    @GetMapping("/data/page")
    public Result<?> pageDictData(@RequestParam(required = false) String dictType,
                                  @RequestParam(required = false) String label,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return Result.success(dictService.pageDictData(dictType, label, page, size));
    }

    @GetMapping("/by-type/{dictType}")
    public Result<List<DictData>> listByType(@PathVariable String dictType) {
        return Result.success(dictService.listByType(dictType));
    }

    @PostMapping("/data")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<DictData> createDictData(@RequestBody DictDataDTO dto) {
        return Result.success(dictService.createDictData(dto));
    }

    @PutMapping("/data")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<DictData> updateDictData(@RequestBody DictDataDTO dto) {
        return Result.success(dictService.updateDictData(dto));
    }

    @DeleteMapping("/data/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteDictData(@PathVariable Long id) {
        dictService.deleteDictData(id);
        return Result.success();
    }
}
