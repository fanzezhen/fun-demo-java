package com.github.fanzezhen.fun.demo.mdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormDataBO;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormItemDataBO;
import com.github.fanzezhen.fun.demo.mdm.condition.MdmFormDataPageCondition;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormDataEntity;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormItemDataEntity;
import com.github.fanzezhen.fun.demo.mdm.mapper.MdmFormDataMapper;
import com.github.fanzezhen.fun.demo.mdm.mapper.MdmFormItemDataMapper;
import com.github.fanzezhen.fun.demo.mdm.service.IMdmFormDataService;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 动态表单数据服务实现
 * 演示复杂业务场景:
 * - 一主多从结构（一条表单数据对应多个字段数据）
 * - 事务管理
 * - 批量插入
 * - 统一分页模型（Controller 转换 Request → Condition，Service 使用 Condition → DAO）
 *
 * @author Claude
 * @since 4.0.6
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
        MdmFormDataEntity formData = new MdmFormDataEntity();
        formData.setFormId(formId);
        formData.setFormDefId(formDefId);
        mdmFormDataMapper.insert(formData);

        // 2. 批量插入字段数据
        if (itemDataList != null && !itemDataList.isEmpty()) {
            for (MdmFormItemDataBO itemBO : itemDataList) {
                MdmFormItemDataEntity itemData = MapperFacadeUtil.map(itemBO, MdmFormItemDataEntity.class);
                itemData.setFormDataId(formData.getId());
                mdmFormItemDataMapper.insert(itemData);
            }
        }

        // 3. 查询并返回完整数据
        return getById(formData.getId());
    }

    @Override
    public MdmFormDataBO getById(Long id) {
        MdmFormDataEntity formData = mdmFormDataMapper.selectById(id);
        if (formData == null) {
            throw new ServiceException("表单数据不存在");
        }

        // 查询关联的字段数据列表
        LambdaQueryWrapper<MdmFormItemDataEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmFormItemDataEntity::getFormDataId, id)
                .orderByAsc(MdmFormItemDataEntity::getFormItemCode)
                .orderByAsc(MdmFormItemDataEntity::getSeq);
        List<MdmFormItemDataEntity> itemDataList = mdmFormItemDataMapper.selectList(wrapper);

        // 组装 BO
        MdmFormDataBO bo = MapperFacadeUtil.map(formData, MdmFormDataBO.class);
        bo.setItemDataList(itemDataList.stream().map(this::itemEntityToBO).toList());
        return bo;
    }

    @Override
    public PageDTO<MdmFormDataBO> page(MdmFormDataPageCondition condition) {
        // 1. 调用 DAO 层查询，获取 PageDTO<Entity>
        PageDTO<MdmFormDataEntity> entityPage = mdmFormDataMapper.page(condition);

        // 2. 使用 PageDTO.convert() 转换为 BO（不加载字段数据列表，提升列表查询性能）
        return entityPage.convert(this::entityToBO);
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
        LambdaQueryWrapper<MdmFormItemDataEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MdmFormItemDataEntity::getFormDataId, id);
        mdmFormItemDataMapper.delete(wrapper);

        return true;
    }

    /**
     * Entity 转 BO（不包含字段列表）
     */
    private MdmFormDataBO entityToBO(MdmFormDataEntity entity) {
        return MapperFacadeUtil.map(entity, MdmFormDataBO.class);
    }

    /**
     * 字段 Entity 转 BO
     */
    private MdmFormItemDataBO itemEntityToBO(MdmFormItemDataEntity entity) {
        return MapperFacadeUtil.map(entity, MdmFormItemDataBO.class);
    }
}
