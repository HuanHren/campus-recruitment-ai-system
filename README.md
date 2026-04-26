# 基于 Spring Boot 与人工智能技术的校园招聘系统设计与实现

## 项目介绍

本项目是一个前后端分离的校园招聘系统毕业设计，围绕高校就业场景，面向管理员、学生、企业和就业老师四类用户，提供用户认证、学生信息、企业信息、招聘岗位、简历、岗位投递、面试邀请、AI 辅助就业和数据统计等功能。

系统后端采用 Spring Boot 3.5.x + Java 17 + MyBatis-Plus + MySQL 8，前端采用 Vue 3 + Vite + Element Plus。AI 能力基于 Spring AI 1.1.x 接入，支持在未配置 API Key 时自动返回模拟数据，保证本地演示和毕业答辩流程可完整运行。

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 后端框架 | Spring Boot 3.5.x |
| 运行环境 | Java 17 |
| 构建工具 | Maven |
| 数据库 | MySQL 8 |
| ORM | MyBatis-Plus |
| 安全认证 | Spring Security + JWT |
| AI 接入 | Spring AI 1.1.x |
| 接口文档 | Knife4j / OpenAPI 3 |
| 其他后端组件 | Lombok、Hibernate Validator |
| 前端框架 | Vue 3 + Vite |
| UI 组件 | Element Plus |
| 状态管理 | Pinia |
| 路由 | Vue Router |
| HTTP 请求 | Axios |

## 功能模块

当前项目已完成以下核心模块，适合作为毕业设计演示版本：

| 模块 | 说明 |
| --- | --- |
| 登录注册与权限控制 | JWT 登录认证、Spring Security 权限控制、四类角色访问控制 |
| 学生信息管理 | 学生新增、修改、查询个人基础信息，管理员查看学生列表 |
| 企业信息管理 | 企业新增、修改、查询企业资料，管理员查看和审核企业 |
| 招聘岗位管理 | 企业发布、修改、下架岗位，管理员审核岗位，学生筛选岗位 |
| 简历管理 | 学生新增、修改、查看个人简历 |
| 岗位投递管理 | 学生投递岗位、查看投递记录，企业查看并更新投递状态 |
| 面试邀请管理 | 企业发送面试邀请，学生接受或拒绝，管理员查看邀请记录 |
| AI 简历优化 | 输入简历内容，返回优化后的简历文本 |
| AI 岗位匹配分析 | 输入简历和岗位要求，返回匹配分数、原因和建议 |
| AI 岗位描述生成 | 企业输入岗位信息，自动生成岗位职责和任职要求 |
| AI 模拟面试题生成 | 根据岗位名称和要求生成 5 道模拟面试题 |
| 数据统计页面 | 前端提供统计卡片、图表和角色首页数据展示 |

## 项目结构

```text
campus-recruitment-ai-system
├─ database
│  └─ campus_recruitment_ai.sql          # 数据库初始化脚本
├─ frontend                              # Vue 3 前端项目
│  ├─ src
│  │  ├─ api / router / stores / views
│  │  └─ styles
│  ├─ package.json
│  └─ vite.config.js
├─ src
│  └─ main
│     ├─ java/com/campus/recruitment
│     │  ├─ common                       # 统一返回、异常、状态码
│     │  ├─ config                       # MyBatis-Plus、Knife4j、Security 配置
│     │  ├─ controller                   # 接口层
│     │  ├─ dto                          # 请求参数对象
│     │  ├─ entity                       # 数据库实体
│     │  ├─ mapper                       # MyBatis-Plus Mapper
│     │  ├─ security                     # JWT 与登录认证
│     │  ├─ service                      # 业务层
│     │  └─ vo                           # 响应视图对象
│     └─ resources
│        ├─ application.yml
│        └─ mapper
├─ AGENTS.md
├─ pom.xml
└─ README.md
```

## 环境要求

请先确认本机已安装：

| 环境 | 建议版本 |
| --- | --- |
| JDK | 17 |
| Maven | 3.8+ |
| MySQL | 8.x |
| Node.js | 18+ |
| npm | 9+ |

## 数据库导入说明

1. 启动 MySQL 8 服务。

2. 在项目根目录执行数据库脚本：

```bash
mysql --default-character-set=utf8mb4 -uroot -p < database/campus_recruitment_ai.sql
```

Windows PowerShell 也可以使用：

```powershell
mysql --default-character-set=utf8mb4 -uroot -p123456 -e "source D:/campus-recruitment-ai-system/database/campus_recruitment_ai.sql"
```

3. 默认数据库名为：

```text
campus_recruitment_ai
```

4. 如本机 MySQL 账号或端口不同，可通过环境变量覆盖：

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=campus_recruitment_ai
DB_USERNAME=root
DB_PASSWORD=123456
```

对应配置位于 [application.yml](src/main/resources/application.yml)：

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:campus_recruitment_ai}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:123456}
```

## 后端运行说明

在项目根目录执行：

```bash
mvn clean package
mvn spring-boot:run
```

启动成功后可访问：

| 地址 | 说明 |
| --- | --- |
| `http://localhost:8080/api/health` | 后端健康检查 |
| `http://localhost:8080/doc.html` | Knife4j 接口文档 |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON |

后端默认端口为 `8080`，可在 `application.yml` 的 `server.port` 中修改。

## 前端运行说明

进入前端目录并安装依赖：

```bash
cd frontend
npm install
npm run dev
```

启动成功后访问：

```text
http://127.0.0.1:5173
```

前端已在 [vite.config.js](frontend/vite.config.js) 中配置 `/api` 代理到后端：

```text
http://localhost:8080
```

生产构建命令：

```bash
cd frontend
npm run build
```

## AI 功能配置说明

项目不会在代码中硬编码任何 AI API Key。AI 配置通过环境变量读取。

### 不配置 API Key

如果不配置 `DEEPSEEK_API_KEY`，AI 接口会自动返回模拟数据，并在响应中标记 `mock=true`。这种方式适合本地开发、课堂展示和毕业答辩。

### 配置 DeepSeek 真实模型

本项目使用 Spring AI 的 OpenAI ChatModel 接入 DeepSeek OpenAI 兼容接口，默认配置如下：

| 配置项 | 默认值 |
| --- | --- |
| base-url | `https://api.deepseek.com` |
| model | `deepseek-v4-pro` |
| API Key 环境变量 | `DEEPSEEK_API_KEY` |

如需调用真实 DeepSeek 模型，可配置：

```bash
DEEPSEEK_API_KEY=你的DeepSeek API Key
SPRING_AI_MODEL_CHAT=openai
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-v4-pro
AI_MOCK_ENABLED=false
```

Windows PowerShell 示例：

```powershell
$env:DEEPSEEK_API_KEY="你的DeepSeek API Key"
$env:SPRING_AI_MODEL_CHAT="openai"
$env:DEEPSEEK_BASE_URL="https://api.deepseek.com"
$env:DEEPSEEK_MODEL="deepseek-v4-pro"
$env:AI_MOCK_ENABLED="false"
mvn spring-boot:run
```

说明：

- `DEEPSEEK_API_KEY`：DeepSeek API Key，不要提交到 GitHub。
- `SPRING_AI_MODEL_CHAT`：默认值为 `none`，配置为 `openai` 后启用 Spring AI OpenAI ChatModel。
- `DEEPSEEK_BASE_URL`：DeepSeek OpenAI 兼容接口地址，默认 `https://api.deepseek.com`。
- `DEEPSEEK_MODEL`：使用的模型名称，默认 `deepseek-v4-pro`。
- `AI_MOCK_ENABLED`：为 `true` 时优先保证演示可用。

## 默认账号密码

系统启动和数据库初始化后可使用以下账号登录：

| 用户名 | 密码 | 角色 | 说明 |
| --- | --- | --- | --- |
| `admin` | `123456` | ADMIN | 管理员 |
| `student` | `123456` | STUDENT | 学生 |
| `company` | `123456` | COMPANY | 企业 |
| `teacher` | `123456` | TEACHER | 就业老师 |

## 主要接口说明

完整接口请访问 Knife4j：`http://localhost:8080/doc.html`。

| 模块 | 接口 |
| --- | --- |
| 认证 | `POST /api/auth/register`、`POST /api/auth/login`、`GET /api/auth/me` |
| 学生信息 | `POST /api/student/info`、`PUT /api/student/info`、`GET /api/student/info` |
| 企业信息 | `POST /api/company/info`、`PUT /api/company/info`、`GET /api/company/info` |
| 管理员学生管理 | `GET /api/admin/students` |
| 管理员企业管理 | `GET /api/admin/companies`、`PUT /api/admin/companies/{id}/audit` |
| 企业岗位管理 | `POST /api/company/jobs`、`PUT /api/company/jobs/{id}`、`PUT /api/company/jobs/{id}/offline`、`GET /api/company/jobs` |
| 管理员岗位审核 | `GET /api/admin/jobs`、`PUT /api/admin/jobs/{id}/audit` |
| 学生岗位浏览 | `GET /api/student/jobs`、`GET /api/student/jobs/{id}` |
| 简历管理 | `POST /api/student/resume`、`PUT /api/student/resume`、`GET /api/student/resume` |
| 岗位投递 | `POST /api/student/jobs/{jobId}/apply`、`GET /api/student/applications` |
| 企业投递管理 | `GET /api/company/applications`、`PUT /api/company/applications/{id}/status` |
| 面试邀请 | `POST /api/company/interviews`、`GET /api/student/interviews`、`PUT /api/student/interviews/{id}/reply`、`GET /api/admin/interviews` |
| AI 功能 | `POST /api/student/ai/resume-optimize`、`POST /api/student/ai/job-match`、`POST /api/student/ai/interview-questions`、`POST /api/company/ai/job-description` |

## 项目截图说明

提交毕业设计或 GitHub 仓库时，建议在 `docs/screenshots` 目录保存以下截图，并在论文或答辩 PPT 中引用：

| 建议文件名 | 截图内容 |
| --- | --- |
| `01-login.png` | 登录页，展示渐变背景和居中登录卡片 |
| `02-admin-dashboard.png` | 管理员首页，展示审核、统计和数据概览 |
| `03-student-dashboard.png` | 学生首页，展示 AI 推荐和投递进度 |
| `04-company-dashboard.png` | 企业首页，展示岗位发布和简历筛选入口 |
| `05-job-list.png` | 岗位列表页，展示搜索筛选和岗位卡片 |
| `06-resume-ai.png` | AI 简历优化页面，展示模拟或真实 AI 返回结果 |
| `07-interview.png` | 面试邀请页面，展示邀请状态流转 |
| `08-statistics.png` | 数据统计页面，展示图表和统计卡片 |

如仓库暂不提交截图，也可保留本节作为截图采集清单。

## 答辩演示流程

建议答辩时按以下顺序演示，流程完整且容易体现系统设计价值：

1. 导入数据库脚本，启动后端和前端。
2. 打开登录页，分别介绍四类角色：管理员、学生、企业、就业老师。
3. 使用 `admin / 123456` 登录，展示管理员首页、企业审核、岗位审核和统计入口。
4. 使用 `company / 123456` 登录，维护企业资料，发布招聘岗位。
5. 回到管理员端审核企业和岗位，使岗位对学生可见。
6. 使用 `student / 123456` 登录，维护学生信息和简历。
7. 学生在岗位列表中按岗位名称、城市、薪资、学历筛选岗位，并完成投递。
8. 企业查看收到的投递记录，修改投递状态，并发送面试邀请。
9. 学生查看面试邀请并选择接受或拒绝。
10. 演示 AI 简历优化、岗位匹配分析、岗位描述生成和模拟面试题生成。
11. 说明未配置 AI Key 时系统返回模拟数据，配置 Key 后可接入真实模型。
12. 展示 Knife4j 接口文档，说明后端接口统一返回 `Result`，并具有全局异常处理和 JWT 权限控制。

## 常见问题

### 1. 后端启动提示数据库连接失败

请确认 MySQL 服务已启动，数据库脚本已导入，并检查 `DB_HOST`、`DB_PORT`、`DB_NAME`、`DB_USERNAME`、`DB_PASSWORD` 是否正确。

### 2. 访问前端后接口请求失败

请确认后端已启动在 `http://localhost:8080`，前端开发服务器已启动在 `http://127.0.0.1:5173`。Vite 已配置 `/api` 代理，正常情况下不需要手动修改请求地址。

### 3. Knife4j 文档打不开

先访问 `http://localhost:8080/api/health` 确认后端是否启动成功，再访问 `http://localhost:8080/doc.html`。

### 4. AI 接口没有真实模型结果

如果未配置 `DEEPSEEK_API_KEY`，系统会返回模拟数据，这是项目的答辩兜底设计。需要真实模型时，请设置 `DEEPSEEK_API_KEY` 并将 `SPRING_AI_MODEL_CHAT` 设置为 `openai`。

### 5. 登录后提示无权限

请确认当前账号角色是否匹配接口路径。例如 `/api/admin/**` 仅允许管理员访问，`/api/student/**` 仅允许学生访问。

### 6. Maven 或 npm 下载依赖失败

请检查网络环境，也可以配置 Maven 镜像仓库或 npm 镜像源后重新执行安装和构建命令。

### 7. 端口被占用

后端可修改 `application.yml` 中的 `server.port`，前端可执行：

```bash
npm run dev -- --port 5174
```

## GitHub 提交前检查

提交前建议确认：

- `target/`、`node_modules/`、`frontend/dist/` 不提交到仓库。
- 不提交真实 `DEEPSEEK_API_KEY`、数据库密码等敏感信息。
- README 中的启动命令、默认账号、接口地址与当前代码保持一致。
- 数据库脚本 [campus_recruitment_ai.sql](database/campus_recruitment_ai.sql) 可重新导入。
- 后端至少通过 `mvn clean package`。
- 前端至少通过 `npm run build`。

## 许可证与说明

本项目用于毕业设计、课程设计和学习演示。若用于生产环境，请进一步补充日志审计、文件上传安全、权限细粒度控制、接口限流、数据脱敏和自动化测试。
