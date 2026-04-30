package com.github.fanzezhen.fun.demo.mdm.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态表单定义业务对象
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@Accessors(chain = true)
@Schema(description = "动态表单定义业务对象")
public class MdmFormDefBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "表单ID")
    private Long formId;

    @Schema(description = "版本标识")
    private String versionCode;

    @Schema(description = "表单定义JSON数据")
    private String data;

    @Schema(description = "是否生效中")
    private Boolean valid;
}
