CREATE DATABASE IF NOT EXISTS campus_recruitment_ai
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_recruitment_ai;

-- 第一阶段仅创建数据库与通用版本记录表，业务表将在后续模块阶段补充。
CREATE TABLE IF NOT EXISTS system_schema_version
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    version     VARCHAR(50)  NOT NULL COMMENT '版本号',
    description VARCHAR(255) NOT NULL COMMENT '版本说明',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_system_schema_version_version (version)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '数据库结构版本记录表';

INSERT INTO system_schema_version (version, description)
VALUES ('1.0.0', '项目第一阶段基础框架初始化')
ON DUPLICATE KEY UPDATE description = VALUES(description);

CREATE TABLE IF NOT EXISTS sys_user
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username   VARCHAR(50)  NOT NULL COMMENT '用户名',
    password   VARCHAR(255) NOT NULL COMMENT '密码密文',
    real_name  VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    role       VARCHAR(20)  NOT NULL COMMENT '角色：ADMIN/STUDENT/COMPANY/TEACHER',
    phone      VARCHAR(20)           DEFAULT NULL COMMENT '手机号',
    email      VARCHAR(100)          DEFAULT NULL COMMENT '邮箱',
    status     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    deleted    TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_user_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '系统用户表';

INSERT INTO system_schema_version (version, description)
VALUES ('2.0.0', '第二阶段登录注册与权限控制：新增sys_user表')
ON DUPLICATE KEY UPDATE description = VALUES(description);

CREATE TABLE IF NOT EXISTS student_info
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生信息ID',
    user_id     BIGINT       NOT NULL COMMENT '关联用户ID',
    student_no  VARCHAR(50)  NOT NULL COMMENT '学号',
    name        VARCHAR(50)  NOT NULL COMMENT '学生姓名',
    gender      VARCHAR(10)           DEFAULT NULL COMMENT '性别',
    school      VARCHAR(100) NOT NULL COMMENT '学校',
    college     VARCHAR(100)          DEFAULT NULL COMMENT '学院',
    major       VARCHAR(100) NOT NULL COMMENT '专业',
    grade       VARCHAR(20)           DEFAULT NULL COMMENT '年级',
    education   VARCHAR(30)  NOT NULL COMMENT '学历',
    phone       VARCHAR(20)           DEFAULT NULL COMMENT '联系电话',
    email       VARCHAR(100)          DEFAULT NULL COMMENT '邮箱',
    expected_job VARCHAR(100)         DEFAULT NULL COMMENT '期望岗位',
    self_intro  VARCHAR(1000)         DEFAULT NULL COMMENT '个人简介',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_info_user_id (user_id),
    UNIQUE KEY uk_student_info_student_no (student_no)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '学生基础信息表';

CREATE TABLE IF NOT EXISTS company_info
(
    id                          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '企业信息ID',
    user_id                     BIGINT       NOT NULL COMMENT '关联用户ID',
    company_name                VARCHAR(100) NOT NULL COMMENT '企业名称',
    unified_social_credit_code  VARCHAR(50)           DEFAULT NULL COMMENT '统一社会信用代码',
    industry                    VARCHAR(100)          DEFAULT NULL COMMENT '所属行业',
    company_scale               VARCHAR(50)           DEFAULT NULL COMMENT '企业规模',
    address                     VARCHAR(255)          DEFAULT NULL COMMENT '企业地址',
    contact_person              VARCHAR(50)  NOT NULL COMMENT '联系人',
    contact_phone               VARCHAR(20)  NOT NULL COMMENT '联系电话',
    contact_email               VARCHAR(100)          DEFAULT NULL COMMENT '联系邮箱',
    description                 VARCHAR(1500)         DEFAULT NULL COMMENT '企业简介',
    audit_status                VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING待审核，APPROVED已通过，REJECTED已驳回',
    audit_remark                VARCHAR(255)          DEFAULT NULL COMMENT '审核备注',
    deleted                     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    created_at                  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at                  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_company_info_user_id (user_id),
    KEY idx_company_info_audit_status (audit_status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '企业基础信息表';

INSERT INTO system_schema_version (version, description)
VALUES ('3.0.0', '第三阶段学生和企业基础信息管理：新增student_info与company_info表')
ON DUPLICATE KEY UPDATE description = VALUES(description);

CREATE TABLE IF NOT EXISTS job_info
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    company_id       BIGINT        NOT NULL COMMENT '企业信息ID',
    company_user_id  BIGINT        NOT NULL COMMENT '企业用户ID',
    job_name         VARCHAR(100)  NOT NULL COMMENT '岗位名称',
    city             VARCHAR(50)   NOT NULL COMMENT '工作城市',
    job_type         VARCHAR(50)            DEFAULT NULL COMMENT '岗位类型',
    salary_min       DECIMAL(10, 2) NOT NULL COMMENT '最低薪资，单位元/月',
    salary_max       DECIMAL(10, 2) NOT NULL COMMENT '最高薪资，单位元/月',
    education        VARCHAR(30)   NOT NULL COMMENT '学历要求',
    experience       VARCHAR(50)            DEFAULT NULL COMMENT '经验要求',
    headcount        INT           NOT NULL DEFAULT 1 COMMENT '招聘人数',
    job_description  VARCHAR(2000) NOT NULL COMMENT '岗位描述',
    job_requirement  VARCHAR(2000)          DEFAULT NULL COMMENT '岗位要求',
    welfare          VARCHAR(500)           DEFAULT NULL COMMENT '福利待遇',
    contact_person   VARCHAR(50)            DEFAULT NULL COMMENT '联系人',
    contact_phone    VARCHAR(20)            DEFAULT NULL COMMENT '联系电话',
    audit_status     VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING待审核，APPROVED已通过，REJECTED已驳回',
    audit_remark     VARCHAR(255)           DEFAULT NULL COMMENT '审核备注',
    publish_status   VARCHAR(20)   NOT NULL DEFAULT 'ONLINE' COMMENT '发布状态：ONLINE已发布，OFFLINE已下架',
    deleted          TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_job_info_company_id (company_id),
    KEY idx_job_info_company_user_id (company_user_id),
    KEY idx_job_info_audit_publish (audit_status, publish_status),
    KEY idx_job_info_city_education (city, education)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '招聘岗位信息表';

INSERT INTO system_schema_version (version, description)
VALUES ('4.0.0', '第四阶段招聘岗位管理：新增job_info表')
ON DUPLICATE KEY UPDATE description = VALUES(description);

CREATE TABLE IF NOT EXISTS resume_info
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '简历ID',
    student_user_id   BIGINT        NOT NULL COMMENT '学生用户ID',
    resume_name       VARCHAR(100)  NOT NULL COMMENT '简历名称',
    real_name         VARCHAR(50)   NOT NULL COMMENT '姓名',
    gender            VARCHAR(10)            DEFAULT NULL COMMENT '性别',
    phone             VARCHAR(20)   NOT NULL COMMENT '联系电话',
    email             VARCHAR(100)           DEFAULT NULL COMMENT '邮箱',
    school            VARCHAR(100)  NOT NULL COMMENT '学校',
    major             VARCHAR(100)  NOT NULL COMMENT '专业',
    education         VARCHAR(30)   NOT NULL COMMENT '学历',
    graduation_year   VARCHAR(20)            DEFAULT NULL COMMENT '毕业年份',
    expected_position VARCHAR(100)           DEFAULT NULL COMMENT '期望岗位',
    expected_city     VARCHAR(50)            DEFAULT NULL COMMENT '期望城市',
    skills            VARCHAR(1000)          DEFAULT NULL COMMENT '专业技能',
    project_experience VARCHAR(2000)         DEFAULT NULL COMMENT '项目经历',
    internship_experience VARCHAR(2000)      DEFAULT NULL COMMENT '实习经历',
    self_evaluation   VARCHAR(1000)          DEFAULT NULL COMMENT '自我评价',
    deleted           TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_resume_info_student_user_id (student_user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '学生简历信息表';

CREATE TABLE IF NOT EXISTS job_apply
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '投递ID',
    job_id          BIGINT      NOT NULL COMMENT '岗位ID',
    company_id      BIGINT      NOT NULL COMMENT '企业信息ID',
    company_user_id BIGINT      NOT NULL COMMENT '企业用户ID',
    student_user_id BIGINT      NOT NULL COMMENT '学生用户ID',
    resume_id       BIGINT      NOT NULL COMMENT '简历ID',
    apply_status    VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '投递状态：PENDING待查看，VIEWED已查看，INTERVIEW邀请面试，REJECTED已拒绝',
    remark          VARCHAR(255)         DEFAULT NULL COMMENT '企业处理备注',
    deleted         TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    created_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_job_apply_job_student (job_id, student_user_id),
    KEY idx_job_apply_student_user_id (student_user_id),
    KEY idx_job_apply_company_user_id (company_user_id),
    KEY idx_job_apply_status (apply_status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '岗位投递记录表';

INSERT INTO system_schema_version (version, description)
VALUES ('5.0.0', '第五阶段简历与岗位投递：新增resume_info与job_apply表')
ON DUPLICATE KEY UPDATE description = VALUES(description);

CREATE TABLE IF NOT EXISTS interview_invitation
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '面试邀请ID',
    apply_id          BIGINT       NOT NULL COMMENT '投递ID',
    job_id            BIGINT       NOT NULL COMMENT '岗位ID',
    company_id        BIGINT       NOT NULL COMMENT '企业信息ID',
    company_user_id   BIGINT       NOT NULL COMMENT '企业用户ID',
    student_user_id   BIGINT       NOT NULL COMMENT '学生用户ID',
    resume_id         BIGINT       NOT NULL COMMENT '简历ID',
    interview_time    DATETIME     NOT NULL COMMENT '面试时间',
    interview_type    VARCHAR(50)  NOT NULL COMMENT '面试方式：线上/线下/电话等',
    interview_address VARCHAR(255) NOT NULL COMMENT '面试地点或会议链接',
    contact_person    VARCHAR(50)           DEFAULT NULL COMMENT '联系人',
    contact_phone     VARCHAR(20)           DEFAULT NULL COMMENT '联系电话',
    content           VARCHAR(1000)         DEFAULT NULL COMMENT '邀请说明',
    invitation_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '邀请状态：PENDING待确认，ACCEPTED已接受，REJECTED已拒绝',
    reply_remark      VARCHAR(255)          DEFAULT NULL COMMENT '学生回复备注',
    deleted           TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_interview_invitation_apply_id (apply_id),
    KEY idx_interview_student_user_id (student_user_id),
    KEY idx_interview_company_user_id (company_user_id),
    KEY idx_interview_status (invitation_status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '面试邀请表';

INSERT INTO system_schema_version (version, description)
VALUES ('6.0.0', '第六阶段面试邀请管理：新增interview_invitation表')
ON DUPLICATE KEY UPDATE description = VALUES(description);

CREATE TABLE IF NOT EXISTS ai_record
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'AI调用记录ID',
    user_id          BIGINT       NOT NULL COMMENT '调用用户ID',
    function_type    VARCHAR(50)  NOT NULL COMMENT '功能类型：RESUME_OPTIMIZE/JOB_MATCH/JOB_DESCRIPTION/INTERVIEW_QUESTION',
    request_content  TEXT         NOT NULL COMMENT '请求内容',
    response_content TEXT         NOT NULL COMMENT '响应内容',
    mock_flag        TINYINT      NOT NULL DEFAULT 1 COMMENT '是否模拟数据：1是，0否',
    success          TINYINT      NOT NULL DEFAULT 1 COMMENT '是否成功：1成功，0失败',
    error_message    VARCHAR(500)          DEFAULT NULL COMMENT '错误信息',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_ai_record_user_id (user_id),
    KEY idx_ai_record_function_type (function_type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'AI功能调用记录表';

INSERT INTO system_schema_version (version, description)
VALUES ('7.0.0', '第七阶段AI功能模块：新增ai_record表')
ON DUPLICATE KEY UPDATE description = VALUES(description);
