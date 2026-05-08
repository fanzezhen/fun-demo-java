package com.github.fanzezhen.fun.demo.mdm.bo;

import com.github.fanzezhen.fun.framework.core.model.bo.BaseTenantGenericBO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 动态表单业务对象
 * 用于 Service 返回给 Controller
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "动态表单业务对象")
public class MdmFormBO extends BaseTenantGenericBO<Long> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "表单名称")
    private String name;

    @Schema(description = "详细说明")
    private String remark;

    @Schema(description = "是否已发布")
    private Boolean released;

    @Schema(description = "排序优先级")
    private Integer orderNum;
}
