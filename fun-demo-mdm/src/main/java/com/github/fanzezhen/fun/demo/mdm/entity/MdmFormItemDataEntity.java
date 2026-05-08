package com.github.fanzezhen.fun.demo.mdm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant.BaseTenantGenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 动态表单字段数据实体
 * 对应数据库表: mdm_form_item_data
 * 存储表单提交的具体字段值
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("mdm_form_item_data")
public class MdmFormItemDataEntity extends BaseTenantGenericEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表单数据ID
     */
    private Long formDataId;

    /**
     * 表单字段标识
     */
    private String formItemCode;

    /**
     * 字段序号（单值为-1，列表从0开始）
     */
    private Integer seq;

    /**
     * 字段值
     */
    @TableField("`value`")
    private String value;

    /**
     * 值类型（如 string、number、date 等）
     */
    private String valueType;

    /**
     * 格式（如日期格式、数字精度等）
     */
    private String format;
}
