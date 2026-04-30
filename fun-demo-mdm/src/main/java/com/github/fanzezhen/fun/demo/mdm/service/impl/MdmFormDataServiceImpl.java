package com.github.fanzezhen.fun.demo.mdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormDataBO;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormItemDataBO;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormData;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormItemData;
import com.github.fanzezhen.fun.demo.mdm.mapper.MdmFormDataMapper;
import com.github.fanzezhen.fun.demo.mdm.mapper.MdmFormItemDataMapper;
import com.github.fanzezhen.fun.demo.mdm.service.IMdmFormDataService;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 动态表单数据服务实现
 * 演示复杂业务场景:
 * - 一主多从结构（一条表单数据对应多个字段数据）
 * - 事务管理
 * - 批量插入
 *
 * @author Claude
 * @since 2026-04-30
 */
@Service
public class MdmFormDataServiceImpl implements IMdmFormDataService {

    @Resource
    private MdmFormDataMapper mdmFormDataMapper;

    @Resource
    private MdmFormItemDataMapper mdmFormItemDataMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MdmFormDataBO submit(Long formId, Long formDefId, List<MdmFormItemDataBO> itemDataList) {
        // 1. 创建表单数据主记录
        MdmFormData formData = new MdmFormData();
        formData.setFormId(formId);
        formData.setFormDefId(formDefId);
        mdmFormDataMapper.insert(formData);

        // 2. 批量插入字段数据
        if (itemDataList != null && !itemDataList.isEmpty()) {
            for (MdmFormItemDataBO itemBO : itemDataList) {
                MdmFormItemData itemData = new MdmFormItemData();
                BeanUtils.copyProperties(itemBO, itemData);
                itemData.setFormDataId(formData.getId());
                mdmFormItemDataMapper.insert(itemData);
            }
        }

        // 3. 查询并返回完整数据
        return getById(formData.getId());
    }

    @Override
    public MdmFormDataBO getById(Long id) {
        MdmFormData formData = mdmFormDataMapper.selectById(id);
        if (formData == null) {
            throw new ServiceException("表单数据不存在");
        }

        // 查询关联的字段数据列表
        LambdaQueryWrapper<MdmFormItemData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmFormItemData::getFormDataId, id)
                .orderByAsc(MdmFormItemData::getFormItemCode)
                .orderByAsc(MdmFormItemData::getSeq);
        List<MdmFormItemData> itemDataList = mdmFormItemDataMapper.selectList(wrapper);

        // 组装 BO
        MdmFormDataBO bo = new MdmFormDataBO();
        BeanUtils.copyProperties(formData, bo);
        bo.setItemDataList(itemDataList.stream().map(this::itemEntityToBO).toList());
        return bo;
    }

    @Override
    public Page<MdmFormDataBO> pageByFormId(Page<MdmFormData> page, Long formId) {
        LambdaQueryWrapper<MdmFormData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmFormData::getFormId, formId)
                .orderByDesc(MdmFormData::getCreateTime);

        Page<MdmFormData> entityPage = mdmFormDataMapper.selectPage(page, wrapper);

        // 转换为 BO（不加载字段数据列表，提升列表查询性能）
        Page<MdmFormDataBO> boPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        boPage.setRecords(entityPage.getRecords().stream().map(this::entityToBO).toList());
        return boPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(Long id) {
        // 1. 删除表单数据主记录（逻辑删除）
        int rows = mdmFormDataMapper.deleteById(id);
        if (rows == 0) {
            return false;
        }

        // 2. 删除关联的字段数据（逻辑删除）
        LambdaQueryWrapper<MdmFormItemData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmFormItemData::getFormDataId, id);
        mdmFormItemDataMapper.delete(wrapper);

        return true;
    }

    /**
     * Entity 转 BO（不包含字段列表）
     */
    private MdmFormDataBO entityToBO(MdmFormData entity) {
        if (entity == null) {
            return null;
        }
        MdmFormDataBO bo = new MdmFormDataBO();
        BeanUtils.copyProperties(entity, bo);
        return bo;
    }

    /**
     * 字段 Entity 转 BO
     */
    private MdmFormItemDataBO itemEntityToBO(MdmFormItemData entity) {
        if (entity == null) {
            return null;
        }
        MdmFormItemDataBO bo = new MdmFormItemDataBO();
        BeanUtils.copyProperties(entity, bo);
        return bo;
    }
}
