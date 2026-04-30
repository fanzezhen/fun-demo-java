package com.github.fanzezhen.fun.demo.mdm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态表单定义实体
 * 对应数据库表: mdm_form_def
 * 用于存储表单的版本定义和 JSON 数据
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mdm_form_def")
public class MdmFormDef implements Serializable {

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
