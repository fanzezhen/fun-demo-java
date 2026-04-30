package com.github.fanzezhen.fun.demo.mdm.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 提交表单数据请求
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@Schema(description = "提交表单数据请求")
public class MdmFormDataSubmitRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "表单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "表单ID不能为空")
    private Long formId;

    @Schema(description = "表单定义ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "表单定义ID不能为空")
    private Long formDefId;

    @Schema(description = "表单字段数据列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "表单字段数据不能为空")
    @Valid
    private List<FormItemDataRequest> itemDataList;

    /**
     * 表单字段数据
     */
    @Data
    @Schema(description = "表单字段数据")
    public static class FormItemDataRequest implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "表单字段标识", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "字段标识不能为空")
        private String formItemCode;

        @Schema(description = "字段序号（单值为-1，列表从0开始）", defaultValue = "-1")
        private Integer seq = -1;

        @Schema(description = "字段值")
        private String value;

        @Schema(description = "值类型")
        private String valueType;

        @Schema(description = "格式")
        private String format;
    }
}
