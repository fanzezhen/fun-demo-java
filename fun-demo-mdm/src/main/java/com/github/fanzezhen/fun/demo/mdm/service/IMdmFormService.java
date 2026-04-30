package com.github.fanzezhen.fun.demo.mdm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormBO;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmForm;

/**
 * 动态表单服务接口
 *
 * @author Claude
 * @since 2026-04-30
 */
public interface IMdmFormService {

    /**
     * 创建表单
     *
     * @param entity 表单实体
     * @return 表单业务对象
     */
    MdmFormBO create(MdmForm entity);

    /**
     * 更新表单
     *
     * @param entity 表单实体
     * @return 表单业务对象
     */
    MdmFormBO update(MdmForm entity);

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
     * @param page 分页参数
     * @param name 表单名称（模糊查询）
     * @param released 是否已发布
     * @return 分页结果
     */
    Page<MdmFormBO> page(Page<MdmForm> page, String name, Boolean released);

    /**
     * 发布表单
     *
     * @param id 表单ID
     * @return 是否发布成功
     */
    Boolean release(Long id);
}
