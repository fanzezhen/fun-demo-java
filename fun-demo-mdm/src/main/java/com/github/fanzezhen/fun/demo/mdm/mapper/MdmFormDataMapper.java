package com.github.fanzezhen.fun.demo.mdm.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fanzezhen.fun.demo.mdm.condition.MdmFormDataPageCondition;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormDataEntity;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.mp.PageUtil;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态表单数据 Mapper
 * 演示 DAO 层分页规范：使用 PageCondition 入参，返回 PageDTO<Entity>
 *
 * @author Claude
 * @since 4.0.6
 */
@Mapper
public interface MdmFormDataMapper extends BaseMapper<MdmFormDataEntity> {

    /**
     * 分页查询表单数据
     * DAO 层标准实现：PageCondition → MyBatis Page → PageDTO
     *
     * @param condition 分页查询条件
     * @return 分页结果（框架统一模型）
     */
    default PageDTO<MdmFormDataEntity> page(MdmFormDataPageCondition condition) {
        // 1. 构建查询条件
        LambdaQueryWrapper<MdmFormDataEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(condition.getFormId() != null, MdmFormDataEntity::getFormId, condition.getFormId())
                .orderByDesc(MdmFormDataEntity::getCreateTime);

        // 2. 创建 MyBatis Page（启用 count 查询）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MdmFormDataEntity> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(condition.getCurrent(), condition.getSize());
        page.setSearchCount(true); // 确保执行 count 查询

        // 3. 执行查询 → MyBatis IPage → PageDTO
        return PageUtil.toPageResult(selectPage(page, wrapper));
    }
}
