# 基于 Spring Boot 与人工智能技术的校园招聘系统设计与实现

## 项目介绍

本项目是一个前后端分离的毕业设计项目，题目为《基于 Spring Boot 与人工智能技术的校园招聘系统设计与实现》。

系统面向高校就业服务场景，覆盖管理员、学生、企业、就业老师四类角色，提供用户认证、学生信息管理、企业信息管理、招聘岗位管理、简历管理、岗位投递、面试邀请、数据统计和 AI 辅助就业等功能。项目内置较完整的演示数据，适合本地开发、毕业答辩演示和 GitHub 项目展示。

AI 能力基于 Spring AI OpenAI ChatModel 接入 DeepSeek OpenAI 兼容接口，默认模型为 `deepseek-v4-pro`。如果没有配置 `DEEPSEEK_API_KEY`，系统会自动返回结构化模拟数据，保证答辩演示流程稳定可用。

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 后端框架 | Spring Boot 3.5.x |
| JDK | Java 17 |
| 构建工具 | Maven |
| 数据库 | MySQL 8 |
| ORM | MyBatis-Plus |
| 安全认证 | Spring Security + JWT |
| AI 接入 | Spring AI 1.1.x + DeepSeek OpenAI 兼容接口 |
| 接口文档 | Knife4j / OpenAPI 3 |
| 后端组件 | Lombok、Hibernate Validator |
| 前端框架 | Vue 3 + Vite |
| UI 组件 | Element Plus |
| 状态管理 | Pinia |
| 路由 | Vue Router |
| 请求库 | Axios |
| 样式增强 | UnoCSS、自定义设计 token |
| 图表 | ECharts / vue-echarts |
| 动效 | @formkit/auto-animate |

## 功能模块

| 模块 | 说明 |
| --- | --- |
| 登录注册与权限控制 | JWT 登录认证、Spring Security 权限控制、四类角色访问控制 |
| 学生信息管理 | 学生新增、修改、查询个人基础信息，管理员查看学生列表 |
| 企业信息管理 | 企业新增、修改、查询企业资料，管理员查看和审核企业 |
| 招聘岗位管理 | 企业发布、修改、下架岗位，管理员审核岗位，学生筛选岗位 |
| 简历管理 | 学生新增、修改、查看个人简历 |
| 岗位投递管理 | 学生投递岗位、查看投递记录，企业查看并更新投递状态 |
| 面试邀请管理 | 企业发送面试邀请，学生接受或拒绝，管理员查看全平台记录 |
| 数据统计看板 | 管理员首页、统计页、学生首页、企业首页联动演示数据 |
| AI 简历优化 | 输入简历内容，返回优化文本、建议、关键词、亮点和推荐岗位 |
| AI 岗位匹配 | 输入简历和岗位要求，返回匹配分数、原因、不足建议和学习建议 |
| AI 岗位描述生成 | 企业输入岗位名称、薪资和技能要求，生成岗位职责和任职要求 |
| AI 模拟面试题生成 | 根据岗位名称和岗位要求生成基础题、项目题、综合题 |

## 项目结构

```text
campus-recruitment-ai-system
├─ database/
│  ├─ campus_recruitment_ai.sql       # 完整数据库初始化脚本，含表结构、默认账号和演示数据
│  └─ demo_data.sql                   # 仅演示数据脚本，适合已有表结构时重新导入
├─ docs/
│  └─ database.sql                    # 数据库演示数据备份说明脚本
├─ frontend/
│  ├─ src/mock/                       # 前端演示 mock 数据
│  ├─ src/views/                      # 页面视图
│  ├─ src/components/                 # 通用组件
│  ├─ package.json
│  └─ vite.config.js
├─ src/main/java/com/campus/recruitment
│  ├─ common/                         # 统一返回、异常、状态码
│  ├─ config/                         # MyBatis-Plus、Knife4j、Security、AI 配置
│  ├─ controller/                     # REST 接口
│  ├─ dto/                            # 请求 DTO
│  ├─ entity/                         # 实体类
│  ├─ mapper/                         # MyBatis-Plus Mapper
│  ├─ security/                       # JWT 与认证过滤器
│  ├─ service/                        # 业务服务
│  └─ vo/                             # 响应 VO
├─ src/main/resources/
│  ├─ application.yml
│  └─ sql/init.sql                    # 演示数据初始化脚本
├─ AGENTS.md
├─ pom.xml
└─ README.md
```

## 演示数据说明

系统内置了适合毕业答辩展示的校园招聘演示数据，并且前端 mock 与后端 SQL 已同步更新。

| 数据类型 | 数量 | 说明 |
| --- | ---: | --- |
| 企业数据 | 25 家 | 覆盖互联网、AI、云计算、智能硬件、新能源汽车等方向 |
| 学生数据 | 50 名 | 以 2026 届应届毕业生和 2027 届实习生为主 |
| 招聘岗位 | 60 条 | 包含实习生、应届生、初级工程师、管培生岗位 |
| 简历数据 | 50 份 | 每名学生对应一份演示简历 |
| 投递记录 | 120 条 | 已投递、已查看、邀请面试、已录用、已拒绝均衡分布 |
| 面试邀请 | 30 条 | 覆盖线上视频面试、线下面试、电话面试 |

当前重点展示岗位包括：

| 企业 | 岗位 | 城市 | 薪资 | 技能标签 | AI 匹配度 |
| --- | --- | --- | --- | --- | ---: |
| 腾讯 | Java 后端开发实习生 | 深圳 | 8k-12k | Spring Boot / MySQL / Redis / 微服务 | 92% |
| 阿里巴巴 | 大模型应用开发实习生 | 杭州 | 12k-18k | Java / Spring AI / LangChain / RAG | 88% |
| 小米 | Vue 前端开发实习生 | 北京 | 6k-10k | Vue3 / TypeScript / Element Plus | 85% |
| 华为 | 软件测试工程师 | 深圳 | 8k-13k | 测试用例 / 自动化测试 / Linux | 81% |
| 字节跳动 | 数据分析实习生 | 北京 | 10k-15k | SQL / Python / 数据可视化 | 86% |

AI 匹配度覆盖高、中、低不同区间，岗位城市覆盖北京、上海、深圳、广州、杭州、南京、成都、武汉、西安、郑州、苏州、长沙、重庆等城市，避免页面看起来像空壳系统。

## 演示企业说明

演示企业数据仅用于毕业设计展示和系统功能演示，不代表真实合作关系，也不代表项目与相关企业存在授权、招聘、合作或商业关系。

演示企业名称包含腾讯、阿里巴巴、小米、华为、字节跳动、百度、美团、京东、网易、快手、哔哩哔哩、拼多多、蚂蚁集团、联想、大疆、蔚来、小鹏汽车、理想汽车、携程、滴滴、Microsoft、Google、Amazon、Apple、Tesla 等。

为避免侵权，项目不使用企业官方 Logo、官网图片或品牌素材。前端仅使用文字 Logo、首字母 Logo、渐变头像或模拟色块作为演示视觉元素。

## 数据初始化说明

### 完整初始化数据库

完整 SQL 文件路径：

```text
database/campus_recruitment_ai.sql
```

该脚本包含：

- 数据库创建
- 表结构
- 默认账号
- 企业、学生、岗位、简历、投递记录、面试邀请等演示数据

导入方式：

```bash
mysql --default-character-set=utf8mb4 -uroot -p < database/campus_recruitment_ai.sql
```

Windows PowerShell 示例：

```powershell
mysql --default-character-set=utf8mb4 -uroot -p123456 -e "source D:/campus-recruitment-ai-system/database/campus_recruitment_ai.sql"
```

### 清空旧数据并重新初始化

如果希望完全清空旧数据并重新导入，建议先删除数据库，再执行完整初始化脚本：

```sql
DROP DATABASE IF EXISTS campus_recruitment_ai;
```

然后重新导入：

```bash
mysql --default-character-set=utf8mb4 -uroot -p < database/campus_recruitment_ai.sql
```

也可以在 MySQL 命令行中执行：

```sql
DROP DATABASE IF EXISTS campus_recruitment_ai;
SOURCE D:/campus-recruitment-ai-system/database/campus_recruitment_ai.sql;
```

### 仅刷新演示数据

如果数据库表结构已经存在，只想重新导入演示数据，可以使用：

```text
database/demo_data.sql
src/main/resources/sql/init.sql
docs/database.sql
```

推荐使用：

```bash
mysql --default-character-set=utf8mb4 -uroot -p campus_recruitment_ai < database/demo_data.sql
```

注意：如果旧岗位数据没有清理，重复导入可能造成部分岗位数据重复。毕业答辩前建议使用完整初始化方式，保证数据最干净。

### 前端 mock 数据说明

前端也内置了同一批演示数据，用于后端未启动、接口暂时无数据或答辩前端展示时兜底。

mock 数据路径：

```text
frontend/src/mock/companies.ts
frontend/src/mock/students.ts
frontend/src/mock/jobs.ts
frontend/src/mock/resumes.ts
frontend/src/mock/applications.ts
frontend/src/mock/interviews.ts
frontend/src/mock/dashboard.ts
frontend/src/mock/ai.ts
```

这些 mock 数据用于：

- 管理员首页统计
- 学生首页推荐岗位
- 企业首页候选人和趋势
- 岗位列表分页展示
- 投递记录分页展示
- 面试邀请分页展示
- AI 功能演示兜底

如果后续修改后端 SQL 演示数据，应同步更新前端 `src/mock` 目录，避免前后端展示不一致。

## 环境变量

后端数据库：

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=campus_recruitment_ai
DB_USERNAME=root
DB_PASSWORD=123456
```

JWT：

```bash
JWT_SECRET=campus-recruitment-ai-system-jwt-secret-please-change
JWT_EXPIRE=7200
```

AI：

```bash
DEEPSEEK_API_KEY=
SPRING_AI_MODEL_CHAT=none
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-v4-pro
AI_MOCK_ENABLED=true
```

不要把真实 API Key、数据库密码或生产环境密钥提交到 GitHub。

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

后端默认端口为 `8080`，可在 [application.yml](src/main/resources/application.yml) 中修改：

```yaml
server:
  port: 8080
```

## 前端运行说明

进入前端目录：

```bash
cd frontend
npm install
npm run dev
```

默认访问地址：

```text
http://127.0.0.1:5173
```

前端已在 [vite.config.js](frontend/vite.config.js) 中配置 `/api` 代理到后端：

```text
http://localhost:8080
```

生产构建：

```bash
cd frontend
npm run build
```

## AI 功能配置说明

项目使用 Spring AI 的 OpenAI ChatModel 接入 DeepSeek OpenAI 兼容接口。

默认配置：

| 配置项 | 值 |
| --- | --- |
| base-url | `https://api.deepseek.com` |
| completions-path | `/v1/chat/completions` 或项目配置中的兼容路径 |
| model | `deepseek-v4-pro` |
| API Key 环境变量 | `DEEPSEEK_API_KEY` |

### 不配置 API Key

如果没有配置 `DEEPSEEK_API_KEY`，AI 接口会自动返回模拟数据，并在响应结构中标记：

```json
{
  "source": "mock",
  "model": "deepseek-v4-pro"
}
```

该模式适合本地开发、课堂展示和毕业答辩，能避免外部模型接口不可用或超时影响演示。

### 配置真实 DeepSeek API Key

Linux / macOS：

```bash
export DEEPSEEK_API_KEY=你的DeepSeek API Key
export SPRING_AI_MODEL_CHAT=openai
export AI_MOCK_ENABLED=false
mvn spring-boot:run
```

Windows PowerShell：

```powershell
$env:DEEPSEEK_API_KEY="你的DeepSeek API Key"
$env:SPRING_AI_MODEL_CHAT="openai"
$env:AI_MOCK_ENABLED="false"
mvn spring-boot:run
```

AI 请求可能耗时较长，前端 AI 请求已使用更长超时时间，并提供友好提示。若真实 AI 调用失败，后端会记录错误原因并返回丰富 mock 数据兜底。

## 默认演示账号

| 角色 | 用户名 | 密码 | 说明 |
| --- | --- | --- | --- |
| 管理员 | `admin` | `123456` | 查看首页统计、审核企业、审核岗位、查看面试记录 |
| 学生 | `student` | `123456` | 查看推荐岗位、维护简历、投递岗位、使用 AI 功能 |
| 企业 | `company` | `123456` | 维护企业信息、发布岗位、处理投递、发送面试邀请 |
| 就业老师 | `teacher` | `123456` | 查看就业统计和就业指导相关页面 |

## 主要接口说明

完整接口请访问 Knife4j：

```text
http://localhost:8080/doc.html
```

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
| AI 功能 | `POST /api/student/ai/resume-optimize`、`GET /api/student/ai/resume-optimize/stream`、`POST /api/student/ai/job-match`、`POST /api/student/ai/interview-questions`、`POST /api/company/ai/job-description` |

## 答辩演示流程

建议按以下顺序演示：

1. 导入数据库脚本，启动后端和前端。
2. 打开登录页，说明系统支持管理员、学生、企业、就业老师四类角色。
3. 使用 `admin / 123456` 登录管理员端。
4. 展示管理员首页统计，包括注册学生数、入驻企业数、发布岗位数、今日投递数、待审核企业、待审核岗位。
5. 进入岗位列表，展示 60 条岗位分页、城市筛选、岗位类型筛选和审核状态筛选。
6. 说明演示企业名称仅用于毕业设计演示，不代表真实合作关系。
7. 使用 `student / 123456` 登录学生端。
8. 展示学生首页推荐岗位，例如腾讯 Java 后端开发实习生、阿里巴巴大模型应用开发实习生、小米 Vue 前端开发实习生等。
9. 展示简历管理和岗位投递流程。
10. 展示 AI 岗位匹配，说明匹配度有高有低，能体现简历与岗位要求的差异。
11. 展示 AI 简历优化，查看优化结果、建议、关键词、亮点、不足和推荐岗位方向。
12. 展示 AI 模拟面试题生成，说明基础题、项目题、综合题和参考答题思路。
13. 使用 `company / 123456` 登录企业端。
14. 展示企业首页、岗位发布、收到的简历、投递状态处理。
15. 发送面试邀请，并切换到学生端查看邀请。
16. 使用 `teacher / 123456` 登录就业老师端，展示统计和就业指导入口。
17. 打开 Knife4j，说明后端接口统一返回 `Result`，并具备全局异常处理和 JWT 权限控制。

## 页面截图说明

建议在 `docs/screenshots` 目录保存截图，并在论文或答辩 PPT 中引用。

| 建议文件名 | 截图内容 |
| --- | --- |
| `01-login.png` | 登录页截图，展示左右分栏、角色切换和 AI 校园招聘风格 |
| `02-admin-dashboard.png` | 管理员首页截图，展示统计卡片、图表、待办事项和 AI 状态 |
| `03-job-list.png` | 岗位列表截图，展示搜索栏、分页、状态标签和重点岗位 |
| `04-student-dashboard.png` | 学生端首页截图，展示 AI 推荐岗位、简历完整度和投递进度 |
| `05-company-dashboard.png` | 企业端首页截图，展示岗位发布、候选人列表和招聘趋势 |
| `06-ai-resume.png` | AI 简历优化截图，展示输入区、优化结果和建议标签 |
| `07-ai-match.png` | AI 岗位匹配截图，展示匹配分数、匹配原因和不足建议 |
| `08-ai-interview.png` | AI 模拟面试截图，展示基础题、项目题、综合题 |
| `09-applications.png` | 投递记录截图，展示状态筛选和分页效果 |
| `10-interviews.png` | 面试邀请截图，展示邀请状态和面试方式 |

截图占位示例：

```markdown
![登录页截图](docs/screenshots/01-login.png)
![管理员首页截图](docs/screenshots/02-admin-dashboard.png)
![岗位列表截图](docs/screenshots/03-job-list.png)
![学生端首页截图](docs/screenshots/04-student-dashboard.png)
![企业端首页截图](docs/screenshots/05-company-dashboard.png)
![AI 简历优化截图](docs/screenshots/06-ai-resume.png)
![AI 岗位匹配截图](docs/screenshots/07-ai-match.png)
```

## 常见问题

### 1. 8080 端口被占用怎么办？

可以修改 [application.yml](src/main/resources/application.yml)：

```yaml
server:
  port: 8081
```

或者临时指定端口启动：

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

修改后，前端代理地址也需要同步调整 [vite.config.js](frontend/vite.config.js) 中的 target。

### 2. MySQL 数据库不存在怎么办？

直接执行完整初始化脚本即可，脚本会创建 `campus_recruitment_ai` 数据库：

```bash
mysql --default-character-set=utf8mb4 -uroot -p < database/campus_recruitment_ai.sql
```

如果数据库账号、密码或端口不同，请通过环境变量覆盖：

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=campus_recruitment_ai
DB_USERNAME=root
DB_PASSWORD=123456
```

### 3. DeepSeek API Key 未配置怎么办？

这是项目的正常兜底设计。未配置 `DEEPSEEK_API_KEY` 时，AI 接口会返回 mock 数据，页面仍然可以完整演示简历优化、岗位匹配和面试题生成。

需要真实模型时，配置：

```bash
DEEPSEEK_API_KEY=你的DeepSeek API Key
SPRING_AI_MODEL_CHAT=openai
AI_MOCK_ENABLED=false
```

### 4. AI 生成超时怎么办？

真实 AI 生成可能超过普通接口请求时间。项目已为 AI 请求配置更长超时时间，并在后端失败时记录错误原因。

如果仍然超时，可以：

- 检查网络是否能访问 `https://api.deepseek.com`
- 检查 `DEEPSEEK_API_KEY` 是否有效
- 减少输入文本长度后重试
- 临时启用 `AI_MOCK_ENABLED=true` 使用模拟数据演示

### 5. 前端 mock 数据和后端 SQL 数据不同步怎么办？

本项目同时维护后端 SQL 演示数据和前端 mock 演示数据。

后端数据路径：

```text
database/campus_recruitment_ai.sql
database/demo_data.sql
src/main/resources/sql/init.sql
docs/database.sql
```

前端数据路径：

```text
frontend/src/mock/
```

如果修改了岗位、企业、学生、投递或面试数据，建议同步更新以上两处。答辩前推荐重新导入完整 SQL，并运行前端构建检查：

```bash
mysql --default-character-set=utf8mb4 -uroot -p < database/campus_recruitment_ai.sql
cd frontend
npm run build
```

### 6. 前端页面访问后接口请求失败怎么办？

请确认后端已启动在：

```text
http://localhost:8080
```

前端 Vite 代理默认配置 `/api` 到后端。如果后端端口改为 8081，需要同步修改 [vite.config.js](frontend/vite.config.js)。

### 7. npm 或 Maven 依赖下载失败怎么办？

请检查网络环境，或配置国内镜像源。依赖安装完成后再执行：

```bash
mvn clean package
cd frontend
npm run build
```

## GitHub 提交前检查

提交前建议确认：

- 不提交 `target/`、`node_modules/`、`frontend/dist/`。
- 不提交真实 `DEEPSEEK_API_KEY`、数据库密码或生产密钥。
- README 中的运行命令、默认账号、接口地址与当前项目一致。
- 数据库脚本可以重新导入。
- 后端至少通过 `mvn clean package`。
- 前端至少通过 `npm run build`。

## 说明

本项目仅用于毕业设计、课程设计和学习演示。若用于生产环境，请进一步补充日志审计、文件上传安全、细粒度权限控制、接口限流、数据脱敏、自动化测试和部署监控。
