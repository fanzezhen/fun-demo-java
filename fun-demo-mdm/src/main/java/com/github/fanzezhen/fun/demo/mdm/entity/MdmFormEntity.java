package com.github.fanzezhen.fun.demo.mdm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.fanzezhen.fun.framework.mp.base.entity.snowflake.tenant.BaseTenantGenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 动态表单实体
 * 对应数据库表: mdm_form
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("mdm_form")
public class MdmFormEntity extends BaseTenantGenericEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 表单名称
     */
    @TableField("name")
    private String name;

    /**
     * 详细说明
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否已发布
     */
    @TableField("released")
    private Boolean released;

    /**
     * 排序优先级
     */
    @TableField("order_num")
    private Integer orderNum;
}
