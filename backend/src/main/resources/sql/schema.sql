-- ============================================================
-- DM_PLCDevice 后端表结构（PostgreSQL）
-- 由 spring.sql.init 在应用启动时自动执行（CREATE TABLE IF NOT EXISTS）
-- ============================================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(100),
    email       VARCHAR(100),
    phone       VARCHAR(50),
    role        VARCHAR(20)  DEFAULT 'user',
    status      SMALLINT     DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_user_username ON sys_user (username);

-- 字典类型表
CREATE TABLE IF NOT EXISTS sys_dict (
    id          BIGSERIAL PRIMARY KEY,
    dict_type   VARCHAR(50)  NOT NULL,
    dict_name   VARCHAR(100) NOT NULL,
    remark      VARCHAR(255),
    create_time TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_dict_type ON sys_dict (dict_type);

-- 字典数据表
CREATE TABLE IF NOT EXISTS sys_dict_data (
    id          BIGSERIAL PRIMARY KEY,
    dict_type   VARCHAR(50) NOT NULL,
    label       VARCHAR(100),
    value       VARCHAR(100),
    sort        INT         DEFAULT 0,
    remark      VARCHAR(255),
    create_time TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_dict_data_type_value ON sys_dict_data (dict_type, value);
