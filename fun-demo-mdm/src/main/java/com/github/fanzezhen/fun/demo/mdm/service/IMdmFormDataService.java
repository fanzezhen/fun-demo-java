package com.github.fanzezhen.fun.demo.mdm.service;

import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormDataBO;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormItemDataBO;
import com.github.fanzezhen.fun.demo.mdm.condition.MdmFormDataPageCondition;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;

import java.util.List;

/**
 * 动态表单数据服务接口
 * 遵循框架分层规范：Service 层使用 PageCondition 入参，返回 PageDTO<BO>
 *
 * @author Claude
 * @since 4.0.6
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
     * 分页查询表单数据
     *
     * @param condition 分页查询条件（包含分页参数和查询条件）
     * @return 分页结果
     */
    PageDTO<MdmFormDataBO> page(MdmFormDataPageCondition condition);

    /**
     * 根据ID删除表单数据（逻辑删除）
     *
     * @param id 表单数据ID
     * @return 是否删除成功
     */
    Boolean deleteById(Long id);
}
