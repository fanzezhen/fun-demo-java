package com.github.fanzezhen.fun.demo.mdm.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseTenantGenericBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 动态表单数据业务对象
 * 包含表单数据及其字段值列表
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "动态表单数据业务对象")
public class MdmFormDataBO extends BaseTenantGenericBO<Long> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "表单ID")
    private Long formId;

    @Schema(description = "表单定义ID")
    private Long formDefId;

    @Schema(description = "表单字段数据列表")
    private List<MdmFormItemDataBO> itemDataList;
}
