package com.github.fanzezhen.fun.demo.mdm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 动态表单字段数据实体
 * 对应数据库表: mdm_form_item_data
 * 存储表单提交的具体字段值
 *
 * @author Claude
 * @since 2026-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mdm_form_item_data")
public class MdmFormItemData implements Serializable {

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
     * 表单数据ID
     */
    @TableField("form_data_id")
    private Long formDataId;

    /**
     * 表单字段标识
     */
    @TableField("form_item_code")
    private String formItemCode;

    /**
     * 字段序号（单值为-1，列表从0开始）
     */
    @TableField("seq")
    private Integer seq;

    /**
     * 字段值
     */
    @TableField("value")
    private String value;

    /**
     * 值类型（如 string、number、date 等）
     */
    @TableField("value_type")
    private String valueType;

    /**
     * 格式（如日期格式、数字精度等）
     */
    @TableField("format")
    private String format;
}
