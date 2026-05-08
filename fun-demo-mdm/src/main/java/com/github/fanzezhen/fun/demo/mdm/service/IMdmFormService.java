package com.github.fanzezhen.fun.demo.mdm.service;

import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormBO;
import com.github.fanzezhen.fun.demo.mdm.condition.MdmFormPageCondition;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormEntity;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;

/**
 * 动态表单服务接口
 * 遵循框架分层规范：Service 层使用 PageCondition 入参，返回 PageDTO<BO>
 *
 * @author Claude
 * @since 4.0.6
 */
public interface IMdmFormService {

    /**
     * 创建表单
     *
     * @param entity 表单实体
     * @return 表单业务对象
     */
    MdmFormBO create(MdmFormEntity entity);

    /**
     * 更新表单
     *
     * @param entity 表单实体
     * @return 表单业务对象
     */
    MdmFormBO update(MdmFormEntity entity);

    /**
     * 根据ID查询表单
     *
     * @param id 主键ID
     * @return 表单业务对象
     */
    MdmFormBO getById(Long id);

    /**
     * 根据ID删除表单（逻辑删除）
     *
     * @param id 主键ID
     * @return 是否删除成功
     */
    Boolean deleteById(Long id);

    /**
     * 分页查询表单列表
     *
     * @param condition 分页查询条件（包含分页参数和查询条件）
     * @return 分页结果
     */
    PageDTO<MdmFormBO> page(MdmFormPageCondition condition);

    /**
     * 发布表单
     *
     * @param id 表单ID
     * @return 是否发布成功
     */
    Boolean release(Long id);
}
