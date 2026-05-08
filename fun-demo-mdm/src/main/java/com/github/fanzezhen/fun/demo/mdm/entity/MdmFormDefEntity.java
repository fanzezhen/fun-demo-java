package com.github.fanzezhen.fun.demo.mdm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant.BaseTenantGenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 动态表单定义实体
 * 对应数据库表: mdm_form_def
 * 用于存储表单的版本定义和 JSON 数据
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("mdm_form_def")
public class MdmFormDefEntity extends BaseTenantGenericEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表单ID
     */
    @TableField("form_id")
    private Long formId;

    /**
     * 版本标识
     */
    @TableField("version_code")
    private String versionCode;

    /**
     * 表单定义JSON数据
     * 存储表单字段配置、校验规则等
     */
    @TableField("data")
    private String data;

    /**
     * 是否生效中
     */
    @TableField("valid")
    private Boolean valid;
}
