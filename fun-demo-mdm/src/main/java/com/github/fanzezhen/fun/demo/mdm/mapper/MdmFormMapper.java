package com.github.fanzezhen.fun.demo.mdm.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.fanzezhen.fun.demo.mdm.condition.MdmFormPageCondition;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormEntity;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.mp.PageUtil;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态表单 Mapper
 * 演示 DAO 层分页规范：使用 PageCondition 入参，返回 PageDTO<Entity>
 *
 * @author Claude
 * @since 4.0.6
 */
@Mapper
public interface MdmFormMapper extends BaseMapper<MdmFormEntity> {

    /**
     * 分页查询表单列表
     * DAO 层标准实现：PageCondition → MyBatis Page → PageDTO
     *
     * @param condition 分页查询条件
     * @return 分页结果（框架统一模型）
     */
    default PageDTO<MdmFormEntity> page(MdmFormPageCondition condition) {
        // 1. 构建查询条件
        LambdaQueryWrapper<MdmFormEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(condition.getName() != null && !condition.getName().isBlank(),
                        MdmFormEntity::getName, condition.getName())
                .eq(condition.getReleased() != null, MdmFormEntity::getReleased, condition.getReleased())
                .orderByDesc(MdmFormEntity::getCreateTime);

        // 2. 创建 MyBatis Page（启用 count 查询）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MdmFormEntity> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(condition.getCurrent(), condition.getSize());
        page.setSearchCount(true); // 确保执行 count 查询

        // 3. 执行查询
        // 4. MyBatis IPage → PageDTO（框架统一模型）
        return PageUtil.toPageResult(selectPage(page, wrapper));
    }
}
