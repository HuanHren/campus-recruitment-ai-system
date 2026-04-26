# AGENTS.md

## Project

This is a graduation design project named:
基于 Spring Boot 与人工智能技术的校园招聘系统设计与实现

## Tech Stack

Backend:
- Java 17
- Spring Boot 3.5.x
- MyBatis-Plus
- MySQL 8
- Spring Security
- JWT
- Spring AI 1.1.x
- Maven

Frontend:
- Vue 3
- Vite
- Element Plus
- Pinia
- Axios
- Vue Router

## Development Rules

1. Use Chinese comments where helpful.
2. Keep controller/service/mapper/entity/dto/vo structure clear.
3. All backend responses must use unified Result format.
4. All APIs should include proper validation and exception handling.
5. Do not hardcode API keys.
6. AI functions must support fallback mock responses when no API key is configured.
7. Frontend UI should be polished and suitable for graduation defense.
8. Keep README updated after every major feature.

## Test Commands

Backend:
```bash
mvn clean package
mvn spring-boot:run