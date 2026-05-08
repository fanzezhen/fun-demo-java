-- 转换为 H2 语法
DROP TABLE IF EXISTS mdm_form;

CREATE TABLE mdm_form (
    id             BIGINT            NOT NULL AUTO_INCREMENT,
    create_user_id BIGINT,
    create_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_user_id BIGINT,
    -- H2 不支持列级别的 ON UPDATE，使用触发器或依赖应用层更新
    update_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag       BIGINT            NOT NULL DEFAULT 0,
    tenant_id      BIGINT,
    name           VARCHAR(50)       NOT NULL,
    remark         CLOB, -- H2 中 TEXT 对应 CLOB
    released       BOOLEAN           NOT NULL DEFAULT FALSE,
    order_num      SMALLINT          NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT UK_DEL_TENANT_NAME UNIQUE (del_flag, tenant_id, name)
);

COMMENT ON TABLE mdm_form IS '动态表单';
COMMENT ON COLUMN mdm_form.id IS '主键';
COMMENT ON COLUMN mdm_form.create_user_id IS '创建人ID';
COMMENT ON COLUMN mdm_form.create_time IS '创建时间';
COMMENT ON COLUMN mdm_form.update_user_id IS '最后更新人ID';
COMMENT ON COLUMN mdm_form.update_time IS '更新时间';
COMMENT ON COLUMN mdm_form.del_flag IS '是否删除（0--否；非0即为删除）';
COMMENT ON COLUMN mdm_form.tenant_id IS '租户ID';
COMMENT ON COLUMN mdm_form.name IS '名称';
COMMENT ON COLUMN mdm_form.remark IS '详细说明';
COMMENT ON COLUMN mdm_form.released IS '是否已发布';
COMMENT ON COLUMN mdm_form.order_num IS '排序优先级';

DROP TABLE IF EXISTS mdm_form_def;

CREATE TABLE mdm_form_def (
    id             BIGINT            NOT NULL AUTO_INCREMENT,
    create_user_id BIGINT,
    create_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_user_id BIGINT,
    update_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag       BIGINT            NOT NULL DEFAULT 0,
    tenant_id      BIGINT,
    form_id        BIGINT            NOT NULL,
    version_code   VARCHAR(50)       NOT NULL,
    data           JSON, -- H2 支持 JSON 类型
    valid          BOOLEAN           NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT UK_DEL_TENANT_FORM_VERSION UNIQUE (del_flag, tenant_id, form_id, version_code)
);

COMMENT ON TABLE mdm_form_def IS '动态表单定义';
COMMENT ON COLUMN mdm_form_def.id IS '主键';
COMMENT ON COLUMN mdm_form_def.create_user_id IS '创建人ID';
COMMENT ON COLUMN mdm_form_def.create_time IS '创建时间';
COMMENT ON COLUMN mdm_form_def.update_user_id IS '最后更新人ID';
COMMENT ON COLUMN mdm_form_def.update_time IS '更新时间';
COMMENT ON COLUMN mdm_form_def.del_flag IS '是否删除（0--否；非0即为删除）';
COMMENT ON COLUMN mdm_form_def.tenant_id IS '租户ID';
COMMENT ON COLUMN mdm_form_def.form_id IS '表单ID';
COMMENT ON COLUMN mdm_form_def.version_code IS '版本标识';
COMMENT ON COLUMN mdm_form_def.data IS '数据';
COMMENT ON COLUMN mdm_form_def.valid IS '是否生效中';

DROP TABLE IF EXISTS mdm_form_data;

CREATE TABLE mdm_form_data (
    id             BIGINT            NOT NULL AUTO_INCREMENT,
    create_user_id BIGINT,
    create_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_user_id BIGINT,
    update_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag       BIGINT            NOT NULL DEFAULT 0,
    tenant_id      BIGINT,
    form_id        BIGINT            NOT NULL,
    form_def_id    BIGINT            NOT NULL,
    PRIMARY KEY (id)
);

-- 在 H2 中创建索引
CREATE INDEX IX_FORM ON mdm_form_data (form_id);
CREATE INDEX IX_FORM_DEF ON mdm_form_data (form_def_id);

COMMENT ON TABLE mdm_form_data IS '动态表单数据';
COMMENT ON COLUMN mdm_form_data.id IS '主键';
COMMENT ON COLUMN mdm_form_data.create_user_id IS '创建人ID';
COMMENT ON COLUMN mdm_form_data.create_time IS '创建时间';
COMMENT ON COLUMN mdm_form_data.update_user_id IS '最后更新人ID';
COMMENT ON COLUMN mdm_form_data.update_time IS '更新时间';
COMMENT ON COLUMN mdm_form_data.del_flag IS '是否删除（0--否；非0即为删除）';
COMMENT ON COLUMN mdm_form_data.tenant_id IS '租户ID';
COMMENT ON COLUMN mdm_form_data.form_id IS '表单ID';
COMMENT ON COLUMN mdm_form_data.form_def_id IS '表单定义ID';

DROP TABLE IF EXISTS mdm_form_item_data;

CREATE TABLE mdm_form_item_data (
    id             BIGINT            NOT NULL AUTO_INCREMENT,
    create_user_id BIGINT,
    create_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_user_id BIGINT,
    update_time    TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag       BIGINT            NOT NULL DEFAULT 0,
    tenant_id      BIGINT,
    form_data_id   BIGINT            NOT NULL,
    form_item_code VARCHAR(50)       NOT NULL,
    seq            SMALLINT          NOT NULL DEFAULT -1,
    `value`          CLOB, -- H2 中 TEXT 对应 CLOB
    value_type     VARCHAR(20),
    format         VARCHAR(20),
    PRIMARY KEY (id),
    CONSTRAINT UK_DEL_TENANT_FORM_ITEM_SEQ UNIQUE (del_flag, tenant_id, form_data_id, form_item_code, seq)
);

COMMENT ON TABLE mdm_form_item_data IS '动态表单字段数据';
COMMENT ON COLUMN mdm_form_item_data.id IS '主键';
COMMENT ON COLUMN mdm_form_item_data.create_user_id IS '创建人ID';
COMMENT ON COLUMN mdm_form_item_data.create_time IS '创建时间';
COMMENT ON COLUMN mdm_form_item_data.update_user_id IS '最后更新人ID';
COMMENT ON COLUMN mdm_form_item_data.update_time IS '更新时间';
COMMENT ON COLUMN mdm_form_item_data.del_flag IS '是否删除（0--否；非0即为删除）';
COMMENT ON COLUMN mdm_form_item_data.tenant_id IS '租户ID';
COMMENT ON COLUMN mdm_form_item_data.form_data_id IS '表单数据ID';
COMMENT ON COLUMN mdm_form_item_data.form_item_code IS '表单字段标识';
COMMENT ON COLUMN mdm_form_item_data.seq IS '字段序号(单值为-1,列表从0开始)';
COMMENT ON COLUMN mdm_form_item_data.`value` IS '值';
COMMENT ON COLUMN mdm_form_item_data.value_type IS '类型';
COMMENT ON COLUMN mdm_form_item_data.format IS '格式';
