# AGENTS.md

## Project

This repository is a graduation design project named:

`基于 Spring Boot 与人工智能技术的校园招聘系统设计与实现`

It is a frontend-backend separated campus recruitment system with four roles:

- ADMIN 管理员
- STUDENT 学生
- COMPANY 企业
- TEACHER 就业老师

## Tech Stack

Backend:

- Java 17
- Spring Boot 3.5.x
- Maven
- MySQL 8
- MyBatis-Plus
- Spring Security
- JWT
- Spring AI 1.1.x
- Lombok
- Knife4j / OpenAPI 3

Frontend:

- Vue 3
- Vite
- Element Plus
- Pinia
- Axios
- Vue Router

## Repository Layout

```text
database/                         Database initialization SQL
frontend/                         Vue 3 frontend application
src/main/java/com/campus/recruitment
  common/                         Result, result codes, exceptions
  config/                         Framework and security configuration
  controller/                     REST controllers
  dto/                            Request DTOs
  entity/                         Database entities
  mapper/                         MyBatis-Plus mappers
  security/                       JWT and security classes
  service/                        Business services
  vo/                             Response VOs
src/main/resources/application.yml
README.md
```

## Development Rules

1. Use Chinese comments where they help explain graduation-design business logic.
2. Keep the `controller/service/mapper/entity/dto/vo` structure clear.
3. All backend responses must use the unified `Result` format.
4. APIs should include validation and consistent exception handling.
5. Do not hardcode API keys, database passwords, or other secrets.
6. AI functions must support mock fallback responses when no AI API Key is configured.
7. Frontend UI should remain polished and suitable for graduation defense.
8. Keep `README.md` updated after every major feature or runbook change.
9. Do not remove existing functionality while improving pages or documentation.
10. Prefer small, focused changes that match the existing project style.

## Environment Variables

Backend database:

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=campus_recruitment_ai
DB_USERNAME=root
DB_PASSWORD=123456
```

JWT:

```bash
JWT_SECRET=campus-recruitment-ai-system-jwt-secret-please-change
JWT_EXPIRE=7200
```

AI:

```bash
DEEPSEEK_API_KEY=
SPRING_AI_MODEL_CHAT=none
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-v4-pro
AI_MOCK_ENABLED=true
```

Never commit real keys to the repository.

## Run Commands

Backend:

```bash
mvn clean package
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
npm run build
```

Database import:

```bash
mysql --default-character-set=utf8mb4 -uroot -p < database/campus_recruitment_ai.sql
```

## Useful URLs

- Backend health: `http://localhost:8080/api/health`
- Knife4j docs: `http://localhost:8080/doc.html`
- Frontend dev server: `http://127.0.0.1:5173`

## Default Accounts

| Username | Password | Role |
| --- | --- | --- |
| admin | 123456 | ADMIN |
| student | 123456 | STUDENT |
| company | 123456 | COMPANY |
| teacher | 123456 | TEACHER |

## Git Hygiene

- Do not commit `target/`, `node_modules/`, `frontend/dist/`, logs, or local IDE files.
- Do not commit real API keys or local-only configuration files.
- Before GitHub submission, verify that the README run instructions are still accurate.
