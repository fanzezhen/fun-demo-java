package com.github.fanzezhen.fun.demo.mdm.service.impl;

import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormBO;
import com.github.fanzezhen.fun.demo.mdm.condition.MdmFormPageCondition;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormEntity;
import com.github.fanzezhen.fun.demo.mdm.mapper.MdmFormMapper;
import com.github.fanzezhen.fun.demo.mdm.service.IMdmFormService;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 动态表单服务实现
 * 演示框架提供的能力:
 * - BaseMapper 基础 CRUD
 * - 自动填充（createTime、updateTime、createUserId、updateUserId）
 * - 逻辑删除（@TableLogic）
 * - 统一分页模型（Controller 转换 Request → Condition，Service 使用 Condition → DAO）
 *
 * @author Claude
 * @since 4.0.6
 */
@Service
public class MdmFormServiceImpl implements IMdmFormService {

    @Resource
    private MdmFormMapper mdmFormMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MdmFormBO create(MdmFormEntity entity) {
        // 框架自动填充 createTime、updateTime、createUserId、updateUserId
        mdmFormMapper.insert(entity);
        return entityToBO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MdmFormBO update(MdmFormEntity entity) {
        MdmFormEntity existing = mdmFormMapper.selectById(entity.getId());
        if (existing == null) {
            throw new ServiceException("表单不存在");
        }
        // 更新表单
        int rows = mdmFormMapper.updateById(entity);
        if (rows == 0) {
            throw new ServiceException("更新失败");
        }
        return entityToBO(mdmFormMapper.selectById(entity.getId()));
    }

    @Override
    public MdmFormBO getById(Long id) {
        MdmFormEntity entity = mdmFormMapper.selectById(id);
        if (entity == null) {
            throw new ServiceException("表单不存在");
        }
        return entityToBO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(Long id) {
        // 逻辑删除：@TableLogic 会将 DELETE 语句转换为 UPDATE del_flag
        int rows = mdmFormMapper.deleteById(id);
        return rows > 0;
    }

    @Override
    public PageDTO<MdmFormBO> page(MdmFormPageCondition condition) {
        // 1. 调用 DAO 层查询，获取 PageDTO<Entity>
        PageDTO<MdmFormEntity> entityPage = mdmFormMapper.page(condition);

        // 2. 使用 PageDTO.convert() 转换为 BO
        return entityPage.convert(this::entityToBO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean release(Long id) {
        MdmFormEntity entity = mdmFormMapper.selectById(id);
        if (entity == null) {
            throw new ServiceException("表单不存在");
        }
        if (Boolean.TRUE.equals(entity.getReleased())) {
            throw new ServiceException("表单已发布，无需重复操作");
        }
        entity.setReleased(true);
        mdmFormMapper.updateById(entity);
        return true;
    }

    /**
     * Entity 转 BO
     * 实际项目可使用 MapStruct 或框架提供的对象转换工具
     */
    private MdmFormBO entityToBO(MdmFormEntity entity) {
        if (entity == null) {
            return null;
        }
        MdmFormBO bo = new MdmFormBO();
        BeanUtils.copyProperties(entity, bo);
        return bo;
    }
}
