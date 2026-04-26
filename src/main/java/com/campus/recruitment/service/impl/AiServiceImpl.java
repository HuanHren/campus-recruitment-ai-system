package com.campus.recruitment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.recruitment.dto.AiInterviewQuestionRequest;
import com.campus.recruitment.dto.AiJobDescriptionRequest;
import com.campus.recruitment.dto.AiJobMatchRequest;
import com.campus.recruitment.dto.AiResumeOptimizeRequest;
import com.campus.recruitment.entity.AiFunctionType;
import com.campus.recruitment.entity.AiRecord;
import com.campus.recruitment.mapper.AiRecordMapper;
import com.campus.recruitment.service.AiService;
import com.campus.recruitment.vo.AiInterviewQuestionVO;
import com.campus.recruitment.vo.AiJobDescriptionVO;
import com.campus.recruitment.vo.AiJobMatchVO;
import com.campus.recruitment.vo.AiResumeOptimizeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AiServiceImpl extends ServiceImpl<AiRecordMapper, AiRecord> implements AiService {

    private static final Pattern SCORE_PATTERN = Pattern.compile("(\\d{1,3})");

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;

    @Value("${DEEPSEEK_API_KEY:}")
    private String deepSeekApiKey;

    @Value("${ai.mock-enabled:true}")
    private Boolean mockEnabled;

    @Override
    public AiResumeOptimizeVO optimizeResume(Long userId, AiResumeOptimizeRequest request) {
        String prompt = """
                你是一名高校就业指导老师，请优化下面的学生简历。
                要求：
                1. 使用中文；
                2. 保留真实经历，不虚构学校、公司和项目；
                3. 强化表达、结构和成果描述；
                4. 直接返回优化后的简历文本。

                简历内容：
                %s
                """.formatted(request.getResumeContent());

        AiCallResult result = callAi(prompt, mockResume(request.getResumeContent()));
        record(userId, AiFunctionType.RESUME_OPTIMIZE, request.getResumeContent(), result);
        return AiResumeOptimizeVO.builder()
                .optimizedContent(result.content())
                .mock(result.mock())
                .build();
    }

    @Override
    public AiJobMatchVO analyzeJobMatch(Long userId, AiJobMatchRequest request) {
        String prompt = """
                你是一名校园招聘岗位匹配分析专家。请根据学生简历和岗位要求做匹配度分析。
                请严格按以下格式返回：
                匹配分数：0-100的整数
                匹配原因：一段中文说明
                不足建议：
                1. ...
                2. ...
                3. ...

                学生简历：
                %s

                岗位要求：
                %s
                """.formatted(request.getResumeContent(), request.getJobRequirement());

        AiCallResult result = callAi(prompt, mockJobMatchText());
        record(userId, AiFunctionType.JOB_MATCH, request.getResumeContent() + "\n\n" + request.getJobRequirement(), result);
        return parseJobMatch(result.content(), result.mock());
    }

    @Override
    public AiJobDescriptionVO generateJobDescription(Long userId, AiJobDescriptionRequest request) {
        String prompt = """
                你是一名企业招聘 HR。请根据岗位名称、薪资和技能要求生成岗位描述。
                请严格按以下格式返回：
                岗位职责：
                1. ...
                2. ...
                任职要求：
                1. ...
                2. ...

                岗位名称：%s
                薪资范围：%s
                技能要求：%s
                """.formatted(request.getJobName(), request.getSalaryRange(), request.getSkillRequirement());

        AiCallResult result = callAi(prompt, mockJobDescription(request));
        record(userId, AiFunctionType.JOB_DESCRIPTION, prompt, result);
        return parseJobDescription(result.content(), result.mock());
    }

    @Override
    public AiInterviewQuestionVO generateInterviewQuestions(Long userId, AiInterviewQuestionRequest request) {
        String prompt = """
                你是一名技术面试官。请根据岗位名称和岗位要求生成5道面试题。
                要求：
                1. 问题要适合校园招聘；
                2. 覆盖基础知识、项目经验、问题排查和学习能力；
                3. 只返回5道编号题目。

                岗位名称：%s
                岗位要求：%s
                """.formatted(request.getJobName(), request.getJobRequirement());

        AiCallResult result = callAi(prompt, mockInterviewQuestionsText(request.getJobName()));
        record(userId, AiFunctionType.INTERVIEW_QUESTION, prompt, result);
        return AiInterviewQuestionVO.builder()
                .questions(parseQuestions(result.content()))
                .mock(result.mock())
                .build();
    }

    private AiCallResult callAi(String prompt, String mockContent) {
        if (shouldUseMock()) {
            return new AiCallResult(mockContent, true, true, null);
        }
        try {
            ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
            if (builder == null) {
                return new AiCallResult(mockContent, true, true, "Spring AI ChatClient未启用，已返回模拟数据");
            }
            String content = builder.build()
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();
            if (!StringUtils.hasText(content)) {
                return new AiCallResult(mockContent, true, true, "AI返回内容为空，已返回模拟数据");
            }
            return new AiCallResult(content, false, true, null);
        } catch (Exception exception) {
            return new AiCallResult(mockContent, true, false, exception.getMessage());
        }
    }

    private boolean shouldUseMock() {
        return Boolean.TRUE.equals(mockEnabled) && !StringUtils.hasText(deepSeekApiKey);
    }

    private void record(Long userId, AiFunctionType functionType, String requestContent, AiCallResult result) {
        AiRecord record = new AiRecord();
        record.setUserId(userId);
        record.setFunctionType(functionType.name());
        record.setRequestContent(truncate(requestContent, 8000));
        record.setResponseContent(truncate(result.content(), 8000));
        record.setMockFlag(result.mock() ? 1 : 0);
        record.setSuccess(result.success() ? 1 : 0);
        record.setErrorMessage(truncate(result.errorMessage(), 500));
        save(record);
    }

    private AiJobMatchVO parseJobMatch(String content, boolean mock) {
        int score = 78;
        Matcher matcher = SCORE_PATTERN.matcher(content);
        if (matcher.find()) {
            score = Math.max(0, Math.min(100, Integer.parseInt(matcher.group(1))));
        }
        return AiJobMatchVO.builder()
                .matchScore(score)
                .matchReason(extractSection(content, "匹配原因", "不足建议", "简历与岗位要求整体匹配，具备进一步沟通价值。"))
                .suggestions(parseSuggestions(content))
                .mock(mock)
                .build();
    }

    private AiJobDescriptionVO parseJobDescription(String content, boolean mock) {
        return AiJobDescriptionVO.builder()
                .responsibility(extractSection(content, "岗位职责", "任职要求", content))
                .requirement(extractSection(content, "任职要求", null, "具备岗位相关基础能力，学习能力强，沟通协作良好。"))
                .mock(mock)
                .build();
    }

    private String extractSection(String content, String start, String end, String fallback) {
        int startIndex = content.indexOf(start);
        if (startIndex < 0) {
            return fallback;
        }
        startIndex = content.indexOf("：", startIndex);
        if (startIndex < 0) {
            startIndex = content.indexOf(":", content.indexOf(start));
        }
        if (startIndex < 0) {
            return fallback;
        }
        int contentStart = startIndex + 1;
        int endIndex = end == null ? -1 : content.indexOf(end, contentStart);
        String value = endIndex < 0 ? content.substring(contentStart) : content.substring(contentStart, endIndex);
        return StringUtils.hasText(value) ? value.trim() : fallback;
    }

    private List<String> parseSuggestions(String content) {
        List<String> suggestions = new ArrayList<>();
        for (String line : content.split("\\R")) {
            String normalized = line.replaceFirst("^\\s*\\d+[.、)]\\s*", "").trim();
            if (StringUtils.hasText(normalized) && suggestions.size() < 3
                    && (line.trim().matches("^\\d+[.、)].*") || line.contains("建议"))) {
                suggestions.add(normalized);
            }
        }
        if (suggestions.isEmpty()) {
            suggestions.add("补充与岗位技能直接相关的项目成果，突出可量化结果。");
            suggestions.add("强化实习或课程设计中的技术难点、解决方案和个人贡献。");
            suggestions.add("准备岗位核心技能的面试案例，提升表达的结构化程度。");
        }
        return suggestions;
    }

    private List<String> parseQuestions(String content) {
        List<String> questions = new ArrayList<>();
        for (String line : content.split("\\R")) {
            String normalized = line.replaceFirst("^\\s*\\d+[.、)]\\s*", "").trim();
            if (StringUtils.hasText(normalized)) {
                questions.add(normalized);
            }
            if (questions.size() == 5) {
                break;
            }
        }
        while (questions.size() < 5) {
            questions.add("请结合你的项目经历，说明一次遇到技术问题并解决的过程。");
        }
        return questions;
    }

    private String mockResume(String resumeContent) {
        return """
                【AI模拟优化结果】
                个人优势：具备扎实的专业基础，熟悉 Java、Spring Boot、MySQL 等技术栈，能够完成后端接口开发、数据库设计和基础联调工作。

                项目经历优化：
                在校园招聘系统项目中，参与用户认证、岗位管理、简历投递等核心模块开发，负责接口设计、业务逻辑实现和数据表结构设计。通过统一返回格式、全局异常处理和权限控制提升系统稳定性与可维护性。

                表达建议：
                建议在简历中补充项目访问量、接口数量、数据库表数量、性能优化结果等可量化描述，让招聘方更直观看到你的工程能力。

                原始内容摘要：
                %s
                """.formatted(truncate(resumeContent, 1000));
    }

    private String mockJobMatchText() {
        return """
                匹配分数：82
                匹配原因：简历中的 Java、Spring Boot、MySQL 和项目开发经历与岗位要求匹配度较高，具备校园招聘岗位所需的后端基础能力。
                不足建议：
                1. 补充缓存、消息队列或性能优化相关实践。
                2. 增加项目难点和个人贡献的量化描述。
                3. 面试前准备 Spring Security、事务、索引优化等高频问题。
                """;
    }

    private String mockJobDescription(AiJobDescriptionRequest request) {
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

    private String mockInterviewQuestionsText(String jobName) {
        return """
                1. 请介绍一个你参与过的项目，并说明你在其中负责的模块和技术难点。
                2. %s岗位常用的核心技术有哪些？你掌握最熟的是哪一项？
                3. 如果接口响应变慢，你会从哪些方面排查问题？
                4. 请说明一次你在项目中遇到 Bug 并最终解决的过程。
                5. 如果入职后遇到不熟悉的技术任务，你会如何学习并推进？
                """.formatted(jobName);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private record AiCallResult(String content, boolean mock, boolean success, String errorMessage) {
    }
}
