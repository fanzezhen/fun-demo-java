package com.github.fanzezhen.fun.demo.mdm.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态表单业务对象
 * 用于 Service 返回给 Controller
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@Accessors(chain = true)
@Schema(description = "动态表单业务对象")
public class MdmFormBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新人ID")
    private Long updateUserId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "表单名称")
    private String name;

    @Schema(description = "详细说明")
    private String remark;

    @Schema(description = "是否已发布")
    private Boolean released;

    @Schema(description = "排序优先级")
    private Integer orderNum;
}
