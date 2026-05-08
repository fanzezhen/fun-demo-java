package com.github.fanzezhen.fun.demo.mdm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant.BaseTenantGenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 动态表单数据实体
 * 对应数据库表: mdm_form_data
 * 表单的一次提交记录
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("mdm_form_data")
public class MdmFormDataEntity extends BaseTenantGenericEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表单ID
     */
    @TableField("form_id")
    private Long formId;

    /**
     * 表单定义ID（关联具体的表单版本）
     */
    @TableField("form_def_id")
    private Long formDefId;
}
