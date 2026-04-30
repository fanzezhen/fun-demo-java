package com.github.fanzezhen.fun.demo.mdm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态表单实体
 * 对应数据库表: mdm_form
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mdm_form")
public class MdmForm implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建人ID
     */
    @TableField(value = "create_user_id", fill = FieldFill.INSERT)
    private Long createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最后更新人ID
     */
    @TableField(value = "update_user_id", fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除（0--否；非0即为删除）
     */
    @TableLogic
    @TableField("del_flag")
    private Long delFlag;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

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
