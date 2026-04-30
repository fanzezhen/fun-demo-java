package com.github.fanzezhen.fun.demo.mdm.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态表单数据业务对象
 * 包含表单数据及其字段值列表
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@Accessors(chain = true)
@Schema(description = "动态表单数据业务对象")
public class MdmFormDataBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "表单ID")
    private Long formId;

    @Schema(description = "表单定义ID")
    private Long formDefId;

    @Schema(description = "表单字段数据列表")
    private List<MdmFormItemDataBO> itemDataList;
}
