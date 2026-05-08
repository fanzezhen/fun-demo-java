package com.github.fanzezhen.fun.demo.mdm.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseTenantGenericBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 动态表单定义业务对象
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "动态表单定义业务对象")
public class MdmFormDefBO extends BaseTenantGenericBO<Long> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "表单ID")
    private Long formId;

    @Schema(description = "版本标识")
    private String versionCode;

    @Schema(description = "表单定义JSON数据")
    private String data;

    @Schema(description = "是否生效中")
    private Boolean valid;
}
