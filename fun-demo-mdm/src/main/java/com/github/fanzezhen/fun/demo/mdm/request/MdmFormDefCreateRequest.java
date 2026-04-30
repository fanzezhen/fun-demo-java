package com.github.fanzezhen.fun.demo.mdm.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建表单定义请求
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@Schema(description = "创建表单定义请求")
public class MdmFormDefCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "表单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "表单ID不能为空")
    private Long formId;

    @Schema(description = "版本标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "版本标识不能为空")
    private String versionCode;

    @Schema(description = "表单定义JSON数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "表单定义数据不能为空")
    private String data;

    @Schema(description = "是否生效", defaultValue = "false")
    private Boolean valid = false;
}
