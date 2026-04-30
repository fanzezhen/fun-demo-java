package com.github.fanzezhen.fun.demo.mdm.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新动态表单请求
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@Schema(description = "更新动态表单请求")
public class MdmFormUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "表单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "表单名称不能为空")
    private String name;

    @Schema(description = "详细说明")
    private String remark;

    @Schema(description = "是否已发布")
    private Boolean released;

    @Schema(description = "排序优先级")
    private Integer orderNum;
}
