package com.github.fanzezhen.fun.demo.mdm.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseTenantGenericBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 动态表单字段数据业务对象
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "动态表单字段数据业务对象")
public class MdmFormItemDataBO extends BaseTenantGenericBO<Long> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "表单数据ID")
    private Long formDataId;

    @Schema(description = "表单字段标识")
    private String formItemCode;

    @Schema(description = "字段序号（单值为-1，列表从0开始）")
    private Integer seq;

    @Schema(description = "字段值")
    private String value;

    @Schema(description = "值类型")
    private String valueType;

    @Schema(description = "格式")
    private String format;
}
