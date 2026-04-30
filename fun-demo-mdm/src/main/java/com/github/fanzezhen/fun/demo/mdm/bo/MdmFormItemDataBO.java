package com.github.fanzezhen.fun.demo.mdm.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 动态表单字段数据业务对象
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@Accessors(chain = true)
@Schema(description = "动态表单字段数据业务对象")
public class MdmFormItemDataBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

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
