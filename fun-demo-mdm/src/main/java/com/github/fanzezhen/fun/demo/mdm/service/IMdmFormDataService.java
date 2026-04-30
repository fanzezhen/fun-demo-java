package com.github.fanzezhen.fun.demo.mdm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormDataBO;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormItemDataBO;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormData;

import java.util.List;

/**
 * 动态表单数据服务接口
 *
 * @author Claude
 * @since 2026-04-30
 */
public interface IMdmFormDataService {

    /**
     * 提交表单数据
     *
     * @param formId 表单ID
     * @param formDefId 表单定义ID
     * @param itemDataList 字段数据列表
     * @return 表单数据业务对象
     */
    MdmFormDataBO submit(Long formId, Long formDefId, List<MdmFormItemDataBO> itemDataList);

    /**
     * 根据ID查询表单数据（包含字段值列表）
     *
     * @param id 表单数据ID
     * @return 表单数据业务对象
     */
    MdmFormDataBO getById(Long id);

    /**
     * 根据表单ID分页查询表单数据
     *
     * @param page 分页参数
     * @param formId 表单ID
     * @return 分页结果
     */
    Page<MdmFormDataBO> pageByFormId(Page<MdmFormData> page, Long formId);

    /**
     * 根据ID删除表单数据（逻辑删除）
     *
     * @param id 表单数据ID
     * @return 是否删除成功
     */
    Boolean deleteById(Long id);
}
