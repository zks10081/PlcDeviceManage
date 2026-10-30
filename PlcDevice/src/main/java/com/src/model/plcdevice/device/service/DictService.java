package com.src.model.plcdevice.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.src.model.plcdevice.common.result.BusinessException;
import com.src.model.plcdevice.common.result.PageResult;
import com.src.model.plcdevice.device.entity.Dict;
import com.src.model.plcdevice.device.entity.DictData;
import com.src.model.plcdevice.device.mapper.DictDataMapper;
import com.src.model.plcdevice.device.mapper.DictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DictService {

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private DictDataMapper dictDataMapper;

    /* ---------- 字典类型 ---------- */

    public PageResult<Dict> pageDict(String dictType, String dictName, int page, int size) {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dictType)) {
            wrapper.like(Dict::getDictType, dictType);
        }
        if (StringUtils.hasText(dictName)) {
            wrapper.like(Dict::getDictName, dictName);
        }
        wrapper.orderByDesc(Dict::getId);
        IPage<Dict> result = dictMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public List<Dict> options() {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Dict::getDictType);
        return dictMapper.selectList(wrapper);
    }

    public Dict getDict(Long id) {
        Dict dict = dictMapper.selectById(id);
        if (dict == null) {
            throw new BusinessException("字典类型不存在");
        }
        return dict;
    }

    public long countDict() {
        return dictMapper.selectCount(new LambdaQueryWrapper<>());
    }

    @Transactional
    public Dict createDict(DictDTO dto) {
        if (dictMapper.selectCount(new LambdaQueryWrapper<Dict>().eq(Dict::getDictType, dto.getDictType())) > 0) {
            throw new BusinessException("字典类型编码已存在");
        }
        Dict dict = new Dict();
        dict.setDictType(dto.getDictType());
        dict.setDictName(dto.getDictName());
        dict.setRemark(dto.getRemark());
        dict.setCreateTime(LocalDateTime.now());
        dictMapper.insert(dict);
        return dict;
    }

    @Transactional
    public Dict updateDict(DictDTO dto) {
        Dict dict = dictMapper.selectById(dto.getId());
        if (dict == null) {
            throw new BusinessException("字典类型不存在");
        }
        if (!dict.getDictType().equals(dto.getDictType())
                && dictMapper.selectCount(new LambdaQueryWrapper<Dict>().eq(Dict::getDictType, dto.getDictType())) > 0) {
            throw new BusinessException("字典类型编码已存在");
        }
        dict.setDictType(dto.getDictType());
        dict.setDictName(dto.getDictName());
        dict.setRemark(dto.getRemark());
        dictMapper.updateById(dict);
        return dict;
    }

    @Transactional
    public void deleteDict(Long id) {
        Dict dict = dictMapper.selectById(id);
        if (dict == null) {
            throw new BusinessException("字典类型不存在");
        }
        long count = dictDataMapper.selectCount(new LambdaQueryWrapper<DictData>().eq(DictData::getDictType, dict.getDictType()));
        if (count > 0) {
            throw new BusinessException("该字典类型下存在字典数据，无法删除");
        }
        dictMapper.deleteById(id);
    }

    /* ---------- 字典数据 ---------- */

    public PageResult<DictData> pageDictData(String dictType, String label, int page, int size) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dictType)) {
            wrapper.eq(DictData::getDictType, dictType);
        }
        if (StringUtils.hasText(label)) {
            wrapper.like(DictData::getLabel, label);
        }
        wrapper.orderByAsc(DictData::getSort);
        IPage<DictData> result = dictDataMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public List<DictData> listByType(String dictType) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType).orderByAsc(DictData::getSort);
        return dictDataMapper.selectList(wrapper);
    }

    @Transactional
    public DictData createDictData(DictDataDTO dto) {
        if (dictDataMapper.selectCount(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDictType, dto.getDictType())
                .eq(DictData::getValue, dto.getValue())) > 0) {
            throw new BusinessException("该字典类型下字典值已存在");
        }
        DictData data = new DictData();
        data.setDictType(dto.getDictType());
        data.setLabel(dto.getLabel());
        data.setValue(dto.getValue());
        data.setSort(dto.getSort() == null ? 0 : dto.getSort());
        data.setRemark(dto.getRemark());
        data.setCreateTime(LocalDateTime.now());
        dictDataMapper.insert(data);
        return data;
    }

    @Transactional
    public DictData updateDictData(DictDataDTO dto) {
        DictData data = dictDataMapper.selectById(dto.getId());
        if (data == null) {
            throw new BusinessException("字典数据不存在");
        }
        if (!data.getDictType().equals(dto.getDictType())
                && dictDataMapper.selectCount(new LambdaQueryWrapper<DictData>()
                    .eq(DictData::getDictType, dto.getDictType())
                    .eq(DictData::getValue, dto.getValue())) > 0) {
            throw new BusinessException("该字典类型下字典值已存在");
        }
        data.setDictType(dto.getDictType());
        data.setLabel(dto.getLabel());
        data.setValue(dto.getValue());
        data.setSort(dto.getSort() == null ? 0 : dto.getSort());
        data.setRemark(dto.getRemark());
        dictDataMapper.updateById(data);
        return data;
    }

    @Transactional
    public void deleteDictData(Long id) {
        if (dictDataMapper.selectById(id) == null) {
            throw new BusinessException("字典数据不存在");
        }
        dictDataMapper.deleteById(id);
    }
}
