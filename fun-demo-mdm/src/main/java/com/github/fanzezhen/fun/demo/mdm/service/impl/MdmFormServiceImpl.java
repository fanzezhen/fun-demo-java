package com.github.fanzezhen.fun.demo.mdm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormBO;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmForm;
import com.github.fanzezhen.fun.demo.mdm.mapper.MdmFormMapper;
import com.github.fanzezhen.fun.demo.mdm.service.IMdmFormService;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 动态表单服务实现
 * 演示框架提供的 MyBatis-Plus 能力:
 * - BaseMapper 基础 CRUD
 * - 自动填充（createTime、updateTime、createUserId、updateUserId）
 * - 逻辑删除（@TableLogic）
 * - 乐观锁（@Version）
 *
 * @author Claude
 * @since 2026-04-30
 */
@Service
public class MdmFormServiceImpl implements IMdmFormService {

    @Resource
    private MdmFormMapper mdmFormMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MdmFormBO create(MdmForm entity) {
        // 框架自动填充 createTime、updateTime、createUserId、updateUserId
        mdmFormMapper.insert(entity);
        return entityToBO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MdmFormBO update(MdmForm entity) {
        MdmForm existing = mdmFormMapper.selectById(entity.getId());
        if (existing == null) {
            throw new ServiceException("表单不存在");
        }
        // 乐观锁校验：version 不匹配时更新失败
        int rows = mdmFormMapper.updateById(entity);
        if (rows == 0) {
            throw new ServiceException("表单已被其他用户修改，请刷新后重试");
        }
        return entityToBO(mdmFormMapper.selectById(entity.getId()));
    }

    @Override
    public MdmFormBO getById(Long id) {
        MdmForm entity = mdmFormMapper.selectById(id);
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
    public Page<MdmFormBO> page(Page<MdmForm> page, String name, Boolean released) {
        LambdaQueryWrapper<MdmForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isBlank(), MdmForm::getName, name)
                .eq(released != null, MdmForm::getReleased, released)
                .orderByDesc(MdmForm::getCreateTime);

        Page<MdmForm> entityPage = mdmFormMapper.selectPage(page, wrapper);

        // 转换为 BO 分页对象
        Page<MdmFormBO> boPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        boPage.setRecords(entityPage.getRecords().stream().map(this::entityToBO).toList());
        return boPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean release(Long id) {
        MdmForm entity = mdmFormMapper.selectById(id);
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
    private MdmFormBO entityToBO(MdmForm entity) {
        if (entity == null) {
            return null;
        }
        MdmFormBO bo = new MdmFormBO();
        BeanUtils.copyProperties(entity, bo);
        return bo;
    }
}
