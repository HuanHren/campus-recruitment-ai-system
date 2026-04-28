package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.AiJobDescriptionRequest;
import com.campus.recruitment.vo.AiInterviewQuestionVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockAiResponseFactory {

    private final ObjectMapper objectMapper;

    public String resumeJson(String resumeContent) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "title", "AI 简历优化结果",
                    "content", optimizedResumeText() + "\n\n原始内容摘要：\n" + truncate(resumeContent, 1000),
                    "suggestions", resumeSuggestions(),
                    "keywords", resumeKeywords(),
                    "highlights", resumeHighlights(),
                    "weaknesses", resumeWeaknesses(),
                    "recommendedJobs", resumeRecommendedJobs()
            ));
        } catch (Exception exception) {
            log.warn("构造 AI 简历优化模拟 JSON 失败。error={}", exception.getMessage());
            return optimizedResumeText();
        }
    }

    public String optimizedResumeText() {
        return """
                求职意向：Java 后端开发实习生 / 全栈开发实习生 / AI 应用开发实习生

                个人优势：
                具备扎实的 Java 编程基础和较完整的前后端分离项目实践经验，熟悉 Spring Boot、MyBatis-Plus、MySQL、Spring Security、JWT、Vue3、Element Plus、Axios 等技术栈。能够围绕真实业务流程完成数据库建模、接口设计、权限控制、异常处理、分页查询、前端联调和基础部署工作。对校园招聘业务有较清晰理解，了解学生信息、企业认证、岗位发布、简历管理、岗位投递、面试邀请和 AI 辅助就业之间的数据关系，能够将业务需求拆解为可实现的功能模块。

                项目经历：基于 Spring Boot 与人工智能技术的校园招聘系统
                1. 参与系统整体需求分析与模块设计，完成 ADMIN、STUDENT、COMPANY、TEACHER 四类角色的功能边界设计，覆盖学生求职、企业招聘、管理员审核和就业老师辅助管理等场景。
                2. 负责后端基础框架搭建，使用 Spring Boot 3、MyBatis-Plus、MySQL 8、Spring Security 与 JWT 实现统一登录认证、角色权限控制、统一 Result 返回、全局异常处理和分层开发结构。
                3. 负责岗位管理、简历管理、投递记录和面试邀请等核心模块接口开发，设计多表关联关系，支持岗位筛选、投递状态流转、企业查看简历和学生确认面试等业务流程。
                4. 参与前端页面联调，使用 Vue3、Vite、Element Plus、Pinia、Vue Router 和 Axios 完成数据展示、表格筛选、表单提交、状态标签和响应式页面布局。
                5. 接入 Spring AI OpenAI 兼容接口，支持 DeepSeek-V4-Pro 简历优化、岗位匹配度分析、岗位描述生成和模拟面试题生成，并设计无 API Key 或真实接口超时时的模拟数据兜底能力，保证毕业答辩演示稳定。
                6. 在项目中关注工程规范，按 controller、service、mapper、entity、dto、vo 分层组织代码，补充 Knife4j 接口文档和 README 运行说明，便于部署、测试和答辩讲解。

                实习与实践经历：
                曾参与校内软件工程课程设计和企业信息化系统实践，主要负责接口调试、数据表设计、页面联调和测试用例整理。能够根据需求文档完成基础功能开发，遇到问题时会通过日志、接口响应、数据库数据和浏览器调试工具定位原因，并及时记录解决过程。

                自我评价：
                学习能力强，具备较好的沟通协作意识和工程化思维，能够从业务流程出发理解系统设计。对后端开发、前端联调和 AI 应用集成都有实践经验，愿意持续学习缓存、消息队列、容器化部署、性能优化和大模型应用开发等技术方向，希望在校园招聘岗位中继续提升真实项目交付能力。
                """;
    }

    public List<String> resumeSuggestions() {
        return List.of("增加接口数量、数据表数量、模块数量等量化表达", "突出 Spring Boot 项目中的个人负责模块", "补充 MySQL 索引、分页查询和事务处理经验", "强化 Spring Security 与 JWT 权限控制亮点", "增加 Vue3 前后端联调和组件化开发描述", "补充 AI 接口异常兜底与模拟数据设计", "用 STAR 法描述项目难点和解决方案", "优化自我评价，突出学习能力和工程规范意识");
    }

    public List<String> resumeKeywords() {
        return List.of("Java", "Spring Boot", "MyBatis-Plus", "MySQL", "Spring Security", "JWT", "Vue3", "Element Plus", "Spring AI", "DeepSeek-V4-Pro");
    }

    public List<String> resumeHighlights() {
        return List.of("具备完整前后端分离项目开发经验", "熟悉校园招聘业务流程和数据关系", "掌握多角色权限控制与 JWT 认证机制", "具备 AI 接口集成和异常兜底设计能力", "能够完成接口文档、运行说明和答辩演示准备");
    }

    public List<String> resumeWeaknesses() {
        return List.of("项目成果描述还可以进一步量化", "性能优化和缓存实践描述不足", "对复杂并发场景的说明较少", "测试覆盖和自动化部署经验可以补充", "部分技术关键词与目标岗位匹配度仍可加强");
    }

    public List<String> resumeRecommendedJobs() {
        return List.of("Java 后端开发实习生", "全栈开发实习生", "AI 应用开发实习生", "软件工程师助理", "企业信息化系统开发实习生");
    }

    public String jobMatchText() {
        return """
                匹配分数：86
                匹配等级：高度匹配
                匹配原因：
                1. 简历中的 Java、Spring Boot、MyBatis-Plus 与岗位后端技术栈高度一致。
                2. 校园招聘系统项目覆盖企业发布岗位、学生投递、简历管理和面试邀请，业务复杂度适中。
                3. 候选人具备 MySQL 表结构设计和多表关联查询经验，符合企业管理类系统需求。
                4. 简历体现了 Spring Security 与 JWT 权限控制实践，能够支撑多角色系统开发。
                5. 前端使用 Vue3、Element Plus 和 Axios，具备前后端联调能力。
                6. AI 模块接入 DeepSeek-V4-Pro，说明候选人具备基础大模型接口集成经验。
                7. 项目包含接口文档、README 和数据库脚本，工程规范意识较好。
                8. 求职方向与 Java 后端、全栈开发和 AI 应用开发岗位匹配度较高。
                技能匹配标签：
                1. Java：已匹配
                2. Spring Boot：已匹配
                3. MyBatis-Plus：已匹配
                4. MySQL：已匹配
                5. Spring Security：已匹配
                6. Vue3：部分匹配
                7. Redis：待补充
                8. Docker：待补充
                不足建议：
                1. 补充 Redis 缓存、缓存穿透和缓存击穿相关实践。
                2. 增加接口响应时间、数据量、并发量等量化指标。
                3. 准备 MySQL 索引优化、慢查询分析和事务隔离级别案例。
                4. 补充单元测试、接口测试或自动化测试经验。
                5. 进一步说明 AI 接口超时、失败重试和模拟数据兜底策略。
                6. 将项目难点按问题、方案、结果结构化表达。
                学习建议：
                1. 系统复习 Spring Boot 请求链路、过滤器、拦截器和异常处理。
                2. 学习 Redis 常见数据结构与缓存一致性问题。
                3. 梳理 MySQL 索引设计、Explain 分析和分页优化。
                4. 了解 Docker 镜像构建、容器运行和基础部署流程。
                5. 准备 Spring Security 认证授权流程图。
                6. 练习用 STAR 法讲解校园招聘系统项目。
                推荐投递理由：
                1. 岗位技术栈与候选人项目经验高度重合。
                2. 候选人具备从数据库到前端页面的完整开发链路经验。
                3. AI 能力接入可作为校园招聘候选人的差异化亮点。
                """;
    }

    public String jobDescription(AiJobDescriptionRequest request) {
        return """
                岗位职责：
                1. 参与%s相关业务系统的需求分析、接口设计与功能开发。
                2. 配合前端完成接口联调，保障功能稳定上线。
                3. 参与数据库表设计、SQL优化和基础问题排查。
                4. 编写清晰的技术文档，沉淀项目开发经验。

                任职要求：
                1. 薪资范围：%s，适合具备扎实基础和成长潜力的校园招聘候选人。
                2. 熟悉%s，并能够结合项目完成实际开发。
                3. 具备良好的学习能力、沟通能力和代码规范意识。
                """.formatted(request.getJobName(), request.getSalaryRange(), request.getSkillRequirement());
    }

    public String interviewQuestionsText(String jobName) {
        StringBuilder builder = new StringBuilder();
        appendQuestions(builder, "基础题", jobName);
        appendQuestions(builder, "项目题", jobName);
        appendQuestions(builder, "综合题", jobName);
        return builder.toString();
    }

    public List<AiInterviewQuestionVO.QuestionCard> defaultQuestionCards() {
        List<AiInterviewQuestionVO.QuestionCard> cards = new ArrayList<>();
        for (String type : List.of("基础题", "项目题", "综合题")) {
            for (int i = 1; i <= 5; i++) {
                cards.add(AiInterviewQuestionVO.QuestionCard.builder()
                        .type(type)
                        .question(question(type, i, "Java 后端开发工程师"))
                        .answerIdea("建议按照概念说明、项目场景、个人实践和结果总结四个层次回答。")
                        .focus(type.equals("基础题") ? "基础概念与技术理解" : type.equals("项目题") ? "项目经验与工程落地" : "综合分析与问题解决")
                        .build());
            }
        }
        return cards;
    }

    public List<String> defaultSkillTags() {
        return List.of("Java：已匹配", "Spring Boot：已匹配", "MyBatis-Plus：已匹配", "MySQL：已匹配", "Spring Security：已匹配", "Vue3：部分匹配", "Redis：待补充", "Docker：待补充");
    }

    public List<String> jobMatchSuggestions() {
        return List.of("补充 Redis 缓存实践", "增加接口性能优化指标", "完善部署上线说明", "准备 SQL 优化案例", "强化团队协作描述", "补充接口测试经验");
    }

    public List<String> learningSuggestions() {
        return List.of("复习 Spring Security 与 JWT 请求链路", "学习 Redis 缓存穿透和缓存击穿", "整理 MySQL 索引优化案例", "补充 Docker 基础部署流程", "准备 AI API 异常处理说明", "练习 STAR 法项目表达");
    }

    private void appendQuestions(StringBuilder builder, String type, String jobName) {
        builder.append(type).append("：\n");
        for (int i = 1; i <= 5; i++) {
            builder.append(i).append(". 问题：").append(question(type, i, jobName)).append("\n");
            builder.append("参考答题思路：建议按照概念说明、项目场景、个人实践和结果总结四个层次回答。\n");
            builder.append("考察点：").append(type.equals("基础题") ? "基础概念与技术理解" : type.equals("项目题") ? "项目经验与工程落地" : "综合分析与问题解决").append("\n");
        }
    }

    private String question(String type, int index, String jobName) {
        if ("基础题".equals(type)) {
            return switch (index) {
                case 1 -> "请说明 Spring Boot 自动配置的基本原理。";
                case 2 -> "MySQL 索引为什么能提升查询效率？哪些场景会导致索引失效？";
                case 3 -> "JWT 与 Session 登录方案有什么区别？";
                case 4 -> "Controller、Service、Mapper 分层的作用是什么？";
                default -> "RESTful API 设计中常见的状态码和请求方法如何使用？";
            };
        }
        if ("项目题".equals(type)) {
            return switch (index) {
                case 1 -> "请介绍你在校园招聘系统中负责的核心模块。";
                case 2 -> "你如何设计学生、企业、岗位、简历和投递之间的数据关系？";
                case 3 -> "你是如何实现多角色权限控制的？";
                case 4 -> "AI 简历优化接口超时时，你如何设计兜底方案？";
                default -> "如果岗位列表查询变慢，你会如何定位和优化？";
            };
        }
        return switch (index) {
            case 1 -> "如果线上接口突然报错，你会按什么顺序排查？";
            case 2 -> "如果产品要求三天内新增一个招聘统计大屏，你会如何拆解任务？";
            case 3 -> "你如何向非技术老师解释这个毕业设计的创新点？";
            case 4 -> "如果团队成员代码风格不一致，你会如何协作推进？";
            default -> "你为什么认为自己适合投递" + jobName + "岗位？";
        };
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
