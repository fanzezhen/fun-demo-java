package com.github.fanzezhen.fun.demo.mdm.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建动态表单请求
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@Schema(description = "创建动态表单请求")
public class MdmFormCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "表单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "表单名称不能为空")
    private String name;

    @Schema(description = "详细说明")
    private String remark;

    @Schema(description = "是否已发布", defaultValue = "false")
    private Boolean released = false;

    @Schema(description = "排序优先级", defaultValue = "1")
    @NotNull(message = "排序优先级不能为空")
    private Integer orderNum = 1;
}
