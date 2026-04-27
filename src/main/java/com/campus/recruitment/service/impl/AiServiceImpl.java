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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl extends ServiceImpl<AiRecordMapper, AiRecord> implements AiService {

    private static final Pattern SCORE_PATTERN = Pattern.compile("(\\d{1,3})");
    private static final String MODEL = "deepseek-v4-pro";

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Value("${ai.mock-enabled:true}")
    private Boolean mockEnabled;

    @Override
    public AiResumeOptimizeVO optimizeResume(Long userId, AiResumeOptimizeRequest request) {
        String prompt = """
                你是一名高校就业指导老师，请优化下面的学生简历。
                请只返回 JSON，不要返回 Markdown 代码块，不要额外解释。JSON 字段必须严格使用：
                {
                  "title": "AI 简历优化结果",
                  "content": "完整优化后的简历内容，至少包含求职意向、个人优势、项目经历、自我评价等段落",
                  "suggestions": ["5 条优化建议"],
                  "keywords": ["技能关键词"],
                  "highlights": ["简历亮点总结"],
                  "weaknesses": ["可改进问题"],
                  "recommendedJobs": ["推荐岗位方向"]
                }

                重要要求：
                1. 使用中文；
                2. 保留真实经历，不虚构学校、公司和项目；
                3. content 必须是完整润写后的简历，不要只给一段短文本；
                4. suggestions 固定返回 5 条；
                5. keywords、highlights、weaknesses、recommendedJobs 都必须返回数组；
                6. 适合校园招聘和毕业设计答辩演示。

                简历内容：
                %s
                """.formatted(request.getResumeContent());

        AiCallResult result = callAi(AiFunctionType.RESUME_OPTIMIZE, prompt, mockResumeJson(request.getResumeContent()));
        record(userId, AiFunctionType.RESUME_OPTIMIZE, request.getResumeContent(), result);
        return buildResumeOptimizeVO(result);
    }

    @Override
    public SseEmitter streamOptimizeResume(Long userId, AiResumeOptimizeRequest request) {
        SseEmitter emitter = new SseEmitter(120_000L);
        CompletableFuture.runAsync(() -> doStreamOptimizeResume(userId, request, emitter));
        return emitter;
    }

    private void doStreamOptimizeResume(Long userId, AiResumeOptimizeRequest request, SseEmitter emitter) {
        long start = System.currentTimeMillis();
        if (shouldUseMock()) {
            streamMockResume(userId, request, emitter, "no-api-key", null);
            return;
        }
        String prompt = """
                你是一名高校就业指导老师，请润写下面的学生简历。
                请直接输出完整优化后的简历正文，不要返回 JSON，不要返回 Markdown 代码块。
                内容至少包含：求职意向、个人优势、项目经历、自我评价。
                要求使用中文，保留真实经历，不虚构学校、公司和项目，突出校园招聘系统、Spring Boot、Vue3、MySQL、权限控制和 AI 能力。

                简历内容：
                %s
                """.formatted(request.getResumeContent());
        StringBuilder content = new StringBuilder();
        try {
            sendSse(emitter, "meta", Map.of(
                    "source", "deepseek",
                    "model", MODEL,
                    "title", "AI 简历优化结果"
            ));
            ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
            if (builder == null) {
                long cost = System.currentTimeMillis() - start;
                log.error("DeepSeek AI 流式调用失败：ChatClient 未启用。model={}, type={}, cost={}ms, error={}",
                        MODEL, AiFunctionType.RESUME_OPTIMIZE, cost, "Spring AI ChatClient未启用");
                streamMockResume(userId, request, emitter, "chat-client-disabled", "Spring AI ChatClient未启用");
                return;
            }
            builder.build()
                    .prompt()
                    .user(prompt)
                    .stream()
                    .content()
                    .doOnNext(chunk -> {
                        if (StringUtils.hasText(chunk)) {
                            content.append(chunk);
                            sendSse(emitter, "delta", Map.of("content", chunk));
                        }
                    })
                    .blockLast();

            if (!StringUtils.hasText(content)) {
                long cost = System.currentTimeMillis() - start;
                log.error("DeepSeek AI 流式调用失败：模型返回内容为空。model={}, type={}, cost={}ms",
                        MODEL, AiFunctionType.RESUME_OPTIMIZE, cost);
                streamMockResume(userId, request, emitter, "empty-response", "AI返回内容为空");
                return;
            }

            AiCallResult result = new AiCallResult(content.toString(), false, true, null, null);
            record(userId, AiFunctionType.RESUME_OPTIMIZE, request.getResumeContent(), result);
            long cost = System.currentTimeMillis() - start;
            log.info("DeepSeek AI 流式调用完成。model={}, type={}, cost={}ms", MODEL, AiFunctionType.RESUME_OPTIMIZE, cost);
            sendSse(emitter, "done", buildResumeOptimizeVO(result));
            emitter.complete();
        } catch (Exception exception) {
            long cost = System.currentTimeMillis() - start;
            String reason = isTimeoutException(exception) ? "timeout" : "error";
            log.error("DeepSeek AI 流式调用异常，已切换模拟数据。model={}, type={}, cost={}ms, reason={}, error={}",
                    MODEL, AiFunctionType.RESUME_OPTIMIZE, cost, reason, exception.getMessage(), exception);
            streamMockResume(userId, request, emitter, reason, exception.getMessage());
        }
    }

    @Override
    public AiJobMatchVO analyzeJobMatch(Long userId, AiJobMatchRequest request) {
        String prompt = """
                你是一名校园招聘岗位匹配分析专家。请根据学生简历和岗位要求做匹配度分析。
                请严格按以下格式返回：
                匹配分数：0-100的整数
                匹配等级：高度匹配/较匹配/一般匹配/匹配不足
                匹配原因：一段中文说明
                技能匹配标签：
                1. Java：已匹配
                2. ...
                不足建议：
                1. ...
                2. ...
                3. ...
                学习建议：
                1. ...
                2. ...
                推荐投递理由：一段中文说明

                学生简历：
                %s

                岗位要求：
                %s
                """.formatted(request.getResumeContent(), request.getJobRequirement());

        AiCallResult result = callAi(AiFunctionType.JOB_MATCH, prompt, mockJobMatchText());
        record(userId, AiFunctionType.JOB_MATCH, request.getResumeContent() + "\n\n" + request.getJobRequirement(), result);
        return parseJobMatch(result);
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

        AiCallResult result = callAi(AiFunctionType.JOB_DESCRIPTION, prompt, mockJobDescription(request));
        record(userId, AiFunctionType.JOB_DESCRIPTION, prompt, result);
        return parseJobDescription(result);
    }

    @Override
    public AiInterviewQuestionVO generateInterviewQuestions(Long userId, AiInterviewQuestionRequest request) {
        String prompt = """
                你是一名技术面试官。请根据岗位名称和岗位要求生成模拟面试题。
                要求：
                1. 生成3道基础题、3道项目题、3道综合题；
                2. 每道题必须带参考答题思路；
                3. 适合校园招聘和毕业设计答辩演示；
                4. 按“基础题/项目题/综合题”分组返回。

                岗位名称：%s
                岗位要求：%s
                """.formatted(request.getJobName(), request.getJobRequirement());

        AiCallResult result = callAi(AiFunctionType.INTERVIEW_QUESTION, prompt, mockInterviewQuestionsText(request.getJobName()));
        record(userId, AiFunctionType.INTERVIEW_QUESTION, prompt, result);
        List<AiInterviewQuestionVO.QuestionCard> questionCards = parseQuestionCards(result.content());
        return AiInterviewQuestionVO.builder()
                .source(source(result))
                .model(MODEL)
                .questions(questionCards.stream().map(AiInterviewQuestionVO.QuestionCard::getQuestion).toList())
                .questionCards(questionCards)
                .mock(result.mock())
                .reason(result.reason())
                .errorMessage(result.errorMessage())
                .build();
    }

    private void streamMockResume(Long userId, AiResumeOptimizeRequest request, SseEmitter emitter, String reason, String errorMessage) {
        AiCallResult result = new AiCallResult(mockResumeJson(request.getResumeContent()), true, errorMessage == null, reason, errorMessage);
        AiResumeOptimizeVO finalResult = buildResumeOptimizeVO(result);
        try {
            sendSse(emitter, "meta", Map.of(
                    "source", "mock",
                    "model", MODEL,
                    "title", finalResult.getTitle(),
                    "reason", reason == null ? "mock" : reason
            ));
            streamText(emitter, finalResult.getContent());
            record(userId, AiFunctionType.RESUME_OPTIMIZE, request.getResumeContent(), result);
            sendSse(emitter, "done", finalResult);
            emitter.complete();
        } catch (Exception exception) {
            log.error("AI 简历优化 SSE 模拟数据发送失败。model={}, type={}, reason={}, error={}",
                    MODEL, AiFunctionType.RESUME_OPTIMIZE, reason, exception.getMessage(), exception);
            emitter.completeWithError(exception);
        }
    }

    private void streamText(SseEmitter emitter, String content) throws InterruptedException {
        if (!StringUtils.hasText(content)) {
            return;
        }
        int chunkSize = 24;
        for (int index = 0; index < content.length(); index += chunkSize) {
            String chunk = content.substring(index, Math.min(index + chunkSize, content.length()));
            sendSse(emitter, "delta", Map.of("content", chunk));
            Thread.sleep(25);
        }
    }

    private void sendSse(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (IOException exception) {
            throw new IllegalStateException("SSE消息发送失败", exception);
        }
    }

    private AiCallResult callAi(AiFunctionType functionType, String prompt, String mockContent) {
        long start = System.currentTimeMillis();
        if (shouldUseMock()) {
            return new AiCallResult(mockContent, true, true, "no-api-key", null);
        }
        try {
            ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
            if (builder == null) {
                long cost = System.currentTimeMillis() - start;
                log.error("DeepSeek AI 调用失败：ChatClient 未启用。model={}, type={}, cost={}ms, error={}",
                        MODEL, functionType, cost, "Spring AI ChatClient未启用");
                return new AiCallResult(mockContent, true, true, "chat-client-disabled", "Spring AI ChatClient未启用，已返回模拟数据");
            }
            String content = builder.build()
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();
            if (!StringUtils.hasText(content)) {
                long cost = System.currentTimeMillis() - start;
                log.error("DeepSeek AI 调用失败：模型返回内容为空。model={}, type={}, cost={}ms",
                        MODEL, functionType, cost);
                return new AiCallResult(mockContent, true, true, "empty-response", "AI返回内容为空，已返回模拟数据");
            }
            long cost = System.currentTimeMillis() - start;
            log.info("DeepSeek AI 调用成功。model={}, type={}, cost={}ms", MODEL, functionType, cost);
            return new AiCallResult(content, false, true, null, null);
        } catch (Exception exception) {
            long cost = System.currentTimeMillis() - start;
            String reason = isTimeoutException(exception) ? "timeout" : "error";
            log.error("DeepSeek AI 调用异常，已返回模拟数据。model={}, type={}, cost={}ms, reason={}, error={}",
                    MODEL, functionType, cost, reason, exception.getMessage(), exception);
            return new AiCallResult(mockContent, true, false, reason, exception.getMessage());
        }
    }

    private boolean isTimeoutException(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String name = current.getClass().getName().toLowerCase();
            String message = current.getMessage() == null ? "" : current.getMessage().toLowerCase();
            if (name.contains("timeout") || message.contains("timeout") || message.contains("timed out")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private boolean shouldUseMock() {
        return !StringUtils.hasText(apiKey);
    }

    private void record(Long userId, AiFunctionType functionType, String requestContent, AiCallResult result) {
        AiRecord record = new AiRecord();
        record.setUserId(userId);
        record.setFunctionType(functionType.name());
        record.setRequestContent(truncate(requestContent, 8000));
        record.setResponseContent(truncate(result.content(), 8000));
        record.setMockFlag(result.mock() ? 1 : 0);
        record.setSuccess(result.success() ? 1 : 0);
        record.setErrorMessage(truncate(result.reason() == null ? result.errorMessage() : result.reason() + ": " + result.errorMessage(), 500));
        save(record);
    }

    private AiResumeOptimizeVO buildResumeOptimizeVO(AiCallResult result) {
        String content = result.content();
        JsonNode json = readJsonObject(content);
        if (json != null) {
            List<String> suggestions = jsonList(json, "suggestions", resumeSuggestions());
            List<String> keywords = jsonList(json, "keywords", resumeKeywords());
            List<String> highlights = jsonList(json, "highlights", resumeHighlights());
            List<String> weaknesses = jsonList(json, "weaknesses", resumeWeaknesses());
            List<String> recommendedJobs = jsonList(json, "recommendedJobs", resumeRecommendedJobs());
            String optimizedContent = jsonText(json, "content", mockOptimizedResumeText());
            return AiResumeOptimizeVO.builder()
                    .source(source(result))
                    .model(MODEL)
                    .title(jsonText(json, "title", "AI 简历优化结果"))
                    .content(optimizedContent)
                    .optimizedContent(optimizedContent)
                    .suggestions(suggestions)
                    .keywords(keywords)
                    .skillKeywords(keywords)
                    .highlights(highlights)
                    .weaknesses(weaknesses)
                    .improvements(weaknesses)
                    .recommendedJobs(recommendedJobs)
                    .recommendedPositions(recommendedJobs)
                    .mock(result.mock())
                    .reason(result.reason())
                    .errorMessage(result.errorMessage())
                    .build();
        }

        String optimizedContent = extractSection(content, "优化后的完整简历文本", "优化建议", content);
        List<String> suggestions = extractListSection(content, "优化建议", "技能关键词提取", resumeSuggestions());
        List<String> keywords = extractListSection(content, "技能关键词提取", "简历亮点总结", resumeKeywords());
        List<String> highlights = extractListSection(content, "简历亮点总结", "可改进问题", resumeHighlights());
        List<String> weaknesses = extractListSection(content, "可改进问题", "推荐岗位方向", resumeWeaknesses());
        List<String> recommendedJobs = extractListSection(content, "推荐岗位方向", null, resumeRecommendedJobs());
        return AiResumeOptimizeVO.builder()
                .source(source(result))
                .model(MODEL)
                .title("AI 简历优化结果")
                .content(optimizedContent)
                .optimizedContent(optimizedContent)
                .suggestions(suggestions)
                .keywords(keywords)
                .skillKeywords(keywords)
                .highlights(highlights)
                .weaknesses(weaknesses)
                .improvements(weaknesses)
                .recommendedJobs(recommendedJobs)
                .recommendedPositions(recommendedJobs)
                .mock(result.mock())
                .reason(result.reason())
                .errorMessage(result.errorMessage())
                .build();
    }

    private AiJobMatchVO parseJobMatch(AiCallResult result) {
        String content = result.content();
        int score = 78;
        Matcher matcher = SCORE_PATTERN.matcher(content);
        if (matcher.find()) {
            score = Math.max(0, Math.min(100, Integer.parseInt(matcher.group(1))));
        }
        return AiJobMatchVO.builder()
                .source(source(result))
                .model(MODEL)
                .matchScore(score)
                .matchLevel(extractSection(content, "匹配等级", "匹配原因", matchLevel(score)))
                .matchReason(extractSection(content, "匹配原因", "不足建议", "简历与岗位要求整体匹配，具备进一步沟通价值。"))
                .skillTags(extractListSection(content, "技能匹配标签", "不足建议", defaultSkillTags()))
                .suggestions(extractListSection(content, "不足建议", "学习建议", jobMatchSuggestions()))
                .learningSuggestions(extractListSection(content, "学习建议", "推荐投递理由", learningSuggestions()))
                .recommendReason(extractSection(content, "推荐投递理由", null, "该岗位与学生当前项目经历、后端技术基础和求职方向匹配，适合作为优先投递目标。"))
                .mock(result.mock())
                .reason(result.reason())
                .errorMessage(result.errorMessage())
                .build();
    }

    private AiJobDescriptionVO parseJobDescription(AiCallResult result) {
        String content = result.content();
        return AiJobDescriptionVO.builder()
                .source(source(result))
                .model(MODEL)
                .responsibility(extractSection(content, "岗位职责", "任职要求", content))
                .requirement(extractSection(content, "任职要求", null, "具备岗位相关基础能力，学习能力强，沟通协作良好。"))
                .mock(result.mock())
                .reason(result.reason())
                .errorMessage(result.errorMessage())
                .build();
    }

    private JsonNode readJsonObject(String content) {
        if (!StringUtils.hasText(content)) {
            return null;
        }
        String normalized = content.trim();
        if (normalized.startsWith("```")) {
            normalized = normalized.replaceFirst("^```(?:json)?\\s*", "")
                    .replaceFirst("\\s*```$", "")
                    .trim();
        }
        int objectStart = normalized.indexOf('{');
        int objectEnd = normalized.lastIndexOf('}');
        if (objectStart >= 0 && objectEnd > objectStart) {
            normalized = normalized.substring(objectStart, objectEnd + 1);
        }
        try {
            JsonNode node = objectMapper.readTree(normalized);
            return node != null && node.isObject() ? node : null;
        } catch (Exception exception) {
            log.warn("AI 简历优化结果不是标准 JSON，将使用分段文本解析。error={}", exception.getMessage());
            return null;
        }
    }

    private String jsonText(JsonNode node, String field, String fallback) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return fallback;
        }
        String text = value.asText();
        return StringUtils.hasText(text) ? text.trim() : fallback;
    }

    private List<String> jsonList(JsonNode node, String field, List<String> fallback) {
        JsonNode value = node.get(field);
        if (value == null || !value.isArray()) {
            return fallback;
        }
        List<String> list = new ArrayList<>();
        value.forEach(item -> {
            String text = item.asText();
            if (StringUtils.hasText(text)) {
                list.add(text.trim());
            }
        });
        return list.isEmpty() ? fallback : list;
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

    private List<AiInterviewQuestionVO.QuestionCard> parseQuestionCards(String content) {
        List<AiInterviewQuestionVO.QuestionCard> cards = new ArrayList<>();
        String currentType = "综合题";
        String currentQuestion = null;
        for (String rawLine : content.split("\\R")) {
            String line = rawLine.trim();
            if (!StringUtils.hasText(line)) {
                continue;
            }
            if (line.contains("基础题")) {
                currentType = "基础题";
                continue;
            }
            if (line.contains("项目题")) {
                currentType = "项目题";
                continue;
            }
            if (line.contains("综合题")) {
                currentType = "综合题";
                continue;
            }
            String normalized = line.replaceFirst("^\\s*\\d+[.、)]\\s*", "").trim();
            if (normalized.startsWith("问题：") || normalized.startsWith("问题:")) {
                currentQuestion = normalized.substring(normalized.indexOf("：") >= 0 ? normalized.indexOf("：") + 1 : normalized.indexOf(":") + 1).trim();
                continue;
            }
            if ((normalized.startsWith("参考答题思路：") || normalized.startsWith("参考答题思路:")) && StringUtils.hasText(currentQuestion)) {
                String answerIdea = normalized.substring(normalized.indexOf("：") >= 0 ? normalized.indexOf("：") + 1 : normalized.indexOf(":") + 1).trim();
                cards.add(AiInterviewQuestionVO.QuestionCard.builder()
                        .type(currentType)
                        .question(currentQuestion)
                        .answerIdea(answerIdea)
                        .build());
                currentQuestion = null;
                continue;
            }
            if (!normalized.startsWith("参考") && normalized.length() > 6 && cards.size() < 9) {
                currentQuestion = normalized;
            }
        }
        if (cards.isEmpty()) {
            cards.addAll(defaultQuestionCards());
        }
        return cards.size() > 9 ? cards.subList(0, 9) : cards;
    }

    private String mockResume(String resumeContent) {
        return """
                优化后的完整简历文本：
                求职意向：Java 后端开发工程师 / 校园招聘系统研发方向

                个人优势：
                具备扎实的 Java 后端开发基础，熟悉 Spring Boot、MyBatis-Plus、MySQL、Spring Security 与 JWT 认证机制，能够独立完成接口设计、数据库表结构设计和基础联调工作。具备前后端分离项目经验，理解 Vue 3、Axios 与 RESTful API 的协作方式。

                项目经历：
                校园招聘智能平台项目
                1. 参与用户登录注册、JWT 权限控制、岗位管理、简历管理、岗位投递、面试邀请和 AI 辅助就业等核心模块开发。
                2. 负责后端接口设计、业务逻辑实现和 MySQL 数据表设计，使用统一 Result 返回结构和全局异常处理提升接口规范性。
                3. 基于 Spring Security 实现 ADMIN、STUDENT、COMPANY、TEACHER 四类角色权限隔离，保证不同角色访问边界清晰。
                4. 接入 Spring AI OpenAI 兼容接口，支持 DeepSeek-V4-Pro 简历优化、岗位匹配和模拟面试题生成，并提供无 API Key 的模拟数据兜底方案。

                自我评价：
                学习能力强，具备良好的工程规范意识和问题定位能力，能够围绕业务场景完成从数据库、接口到前端联调的完整开发流程。

                优化建议：
                1. 增加接口数量、数据表数量、模块数量等量化表达，让项目经历更具说服力。
                2. 将“做过项目”改为“负责模块、解决问题、产生结果”的结构化表达。
                3. 补充权限控制、异常处理、统一返回、分页查询等工程化能力。
                4. 在项目经历中突出 AI 功能接入和模拟数据兜底设计，体现毕业设计亮点。
                5. 针对目标岗位补充 Redis、索引优化、事务控制等后端高频关键词。

                技能关键词提取：
                1. Java
                2. Spring Boot
                3. MyBatis-Plus
                4. MySQL
                5. Spring Security
                6. JWT
                7. Vue 3
                8. Spring AI

                简历亮点总结：
                1. 项目模块完整，覆盖招聘系统核心业务闭环。
                2. 权限控制清晰，能够体现后端安全设计能力。
                3. AI 能力与传统招聘业务结合，符合毕业设计创新点。
                4. 具备前后端分离联调经验，工程实践链路完整。

                可改进问题：
                1. 当前经历中的量化指标不足。
                2. 对项目难点和解决方案的描述还可以进一步展开。
                3. 可补充性能优化、缓存设计或 SQL 优化相关实践。

                推荐岗位方向：
                1. Java 后端开发工程师
                2. 校园招聘系统研发实习生
                3. 企业信息化系统开发工程师
                4. AI 应用开发助理工程师

                原始内容摘要：
                %s
                """.formatted(truncate(resumeContent, 1000));
    }

    private String mockResumeJson(String resumeContent) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "title", "AI 简历优化结果",
                    "content", mockOptimizedResumeText() + "\n\n原始内容摘要：\n" + truncate(resumeContent, 1000),
                    "suggestions", resumeSuggestions(),
                    "keywords", resumeKeywords(),
                    "highlights", resumeHighlights(),
                    "weaknesses", resumeWeaknesses(),
                    "recommendedJobs", resumeRecommendedJobs()
            ));
        } catch (Exception exception) {
            log.warn("构造 AI 简历优化模拟 JSON 失败，将使用分段 mock。error={}", exception.getMessage());
            return mockResume(resumeContent);
        }
    }

    private String mockJobMatchText() {
        return """
                匹配分数：82
                匹配等级：高度匹配
                匹配原因：简历中的 Java、Spring Boot、MySQL 和项目开发经历与岗位要求匹配度较高，具备校园招聘岗位所需的后端基础能力。
                技能匹配标签：
                1. Java：已匹配
                2. Spring Boot：已匹配
                3. MySQL：已匹配
                4. Spring Security：部分匹配
                5. Redis：待补充
                不足建议：
                1. 补充缓存、消息队列或性能优化相关实践。
                2. 增加项目难点和个人贡献的量化描述。
                3. 面试前准备 Spring Security、事务、索引优化等高频问题。
                学习建议：
                1. 系统复习 Spring Boot 请求链路、拦截器、过滤器和异常处理机制。
                2. 补充 MySQL 索引设计、慢查询分析和事务隔离级别知识。
                3. 学习 Redis 缓存穿透、缓存击穿和缓存雪崩等常见问题。
                推荐投递理由：该岗位与学生已有校园招聘系统项目高度相关，能够展示后端接口设计、数据库建模、权限控制和 AI 应用接入能力，适合作为优先投递岗位。
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
                基础题：
                1. 问题：Java 中 List、Set、Map 的区别是什么？
                参考答题思路：从数据结构、是否允许重复、是否有序、典型实现类和使用场景展开回答。
                2. 问题：Spring Boot 中 Controller、Service、Mapper 分层的作用是什么？
                参考答题思路：说明职责拆分、降低耦合、便于测试维护，并结合校园招聘系统举例。
                3. 问题：MySQL 索引的作用是什么？什么情况下索引可能失效？
                参考答题思路：说明索引提升查询效率，同时举例函数处理、左模糊查询、类型不一致等情况。

                项目题：
                1. 问题：请介绍你在校园招聘系统中负责的核心模块。
                参考答题思路：按背景、职责、技术方案、遇到问题、最终结果的结构回答。
                2. 问题：你是如何实现 JWT 登录认证和多角色权限控制的？
                参考答题思路：说明登录成功生成 Token、请求过滤器校验 Token、Security 根据角色限制接口访问。
                3. 问题：如果岗位列表查询变慢，你会如何优化？
                参考答题思路：从分页查询、索引设计、查询条件、SQL 执行计划、缓存热点数据等方面回答。

                综合题：
                1. 问题：如果企业反馈 AI 生成岗位描述失败，你会如何排查？
                参考答题思路：先看接口日志、API Key、base-url、模型名称、网络响应，再检查异常兜底和前端展示。
                2. 问题：如果你入职后接触到不熟悉的技术任务，你会如何推进？
                参考答题思路：拆解需求、查阅官方文档、做最小验证、记录问题、及时同步进度。
                3. 问题：你认为这个%s项目最大的亮点是什么？
                参考答题思路：突出招聘业务闭环、四类角色权限控制、AI 能力接入、无 Key 模拟数据兜底和前后端分离实现。
                """.formatted(jobName);
    }

    private List<String> extractListSection(String content, String start, String end, List<String> fallback) {
        String section = extractSection(content, start, end, "");
        if (!StringUtils.hasText(section)) {
            return fallback;
        }
        List<String> list = new ArrayList<>();
        for (String line : section.split("\\R")) {
            String normalized = line.replaceFirst("^\\s*\\d+[.、)]\\s*", "").trim();
            if (StringUtils.hasText(normalized)) {
                list.add(normalized);
            }
        }
        return list.isEmpty() ? fallback : list;
    }

    private String source(AiCallResult result) {
        return result.mock() ? "mock" : "deepseek";
    }

    private String matchLevel(int score) {
        if (score >= 85) {
            return "高度匹配";
        }
        if (score >= 70) {
            return "较匹配";
        }
        if (score >= 55) {
            return "一般匹配";
        }
        return "匹配不足";
    }

    private List<String> resumeSuggestions() {
        return List.of("增加量化成果", "突出项目职责", "补充技术难点", "强化岗位关键词", "优化自我评价");
    }

    private List<String> resumeKeywords() {
        return List.of("Spring Boot", "Vue3", "MySQL", "Redis", "AI 招聘系统", "Spring Security", "JWT", "DeepSeek-V4-Pro");
    }

    private List<String> resumeHighlights() {
        return List.of("具备完整前后端项目开发经验", "熟悉校园招聘业务流程", "掌握基础 AI 接口集成能力");
    }

    private List<String> resumeWeaknesses() {
        return List.of("项目成果描述不够量化", "技术栈关键词密度不足", "项目难点与解决方案还可以进一步展开");
    }

    private List<String> resumeRecommendedJobs() {
        return List.of("Java 后端开发实习生", "全栈开发实习生", "软件工程师助理");
    }

    private String mockOptimizedResumeText() {
        return """
                求职意向：Java 后端开发实习生 / 全栈开发实习生

                个人优势：
                熟悉 Java、Spring Boot、MyBatis-Plus、MySQL、Spring Security、JWT 与 Vue3，具备前后端分离项目开发经验。能够围绕校园招聘业务场景完成接口设计、数据库建模、权限控制、页面联调和基础 AI 能力接入。

                项目经历：AI 校园招聘系统
                1. 参与学生、企业、岗位、简历、投递、面试邀请和 AI 辅助就业等核心模块设计与实现，理解校园招聘业务从岗位发布到面试邀约的完整流程。
                2. 基于 Spring Boot 与 MyBatis-Plus 完成后端接口开发，使用统一 Result、全局异常处理和分层结构提升代码规范性。
                3. 基于 Spring Security 与 JWT 实现 ADMIN、STUDENT、COMPANY、TEACHER 四类角色权限控制，保证不同端功能边界清晰。
                4. 接入 Spring AI OpenAI 兼容接口，支持 DeepSeek-V4-Pro 简历优化、岗位匹配和模拟面试题生成，并设计无 API Key 时的模拟数据兜底方案。

                自我评价：
                学习能力强，具备良好的工程化意识和问题定位能力，能够结合业务需求推进从数据库、后端接口到前端页面的完整开发流程。
                """;
    }

    private List<String> defaultSkillTags() {
        return List.of("Java：已匹配", "Spring Boot：已匹配", "MySQL：已匹配", "Spring Security：部分匹配", "Redis：待补充");
    }

    private List<String> jobMatchSuggestions() {
        return List.of("补充缓存或性能优化相关实践", "增加项目难点和个人贡献描述", "准备权限控制和数据库优化面试案例");
    }

    private List<String> learningSuggestions() {
        return List.of("复习 Spring Security 与 JWT 请求链路", "学习 MySQL 索引优化和事务隔离级别", "补充 Redis 常见缓存问题");
    }

    private List<AiInterviewQuestionVO.QuestionCard> defaultQuestionCards() {
        List<AiInterviewQuestionVO.QuestionCard> cards = new ArrayList<>();
        cards.add(AiInterviewQuestionVO.QuestionCard.builder().type("基础题").question("Java 中 List、Set、Map 的区别是什么？").answerIdea("从是否有序、是否允许重复、键值结构、常见实现类和使用场景回答。").build());
        cards.add(AiInterviewQuestionVO.QuestionCard.builder().type("基础题").question("Spring Boot 分层结构的作用是什么？").answerIdea("说明 Controller、Service、Mapper 的职责边界和可维护性价值。").build());
        cards.add(AiInterviewQuestionVO.QuestionCard.builder().type("基础题").question("MySQL 索引的作用和失效场景有哪些？").answerIdea("说明索引提升查询效率，并举例左模糊、函数处理、类型不一致等情况。").build());
        cards.add(AiInterviewQuestionVO.QuestionCard.builder().type("项目题").question("请介绍你负责的校园招聘系统模块。").answerIdea("按背景、职责、技术方案、问题和结果回答。").build());
        cards.add(AiInterviewQuestionVO.QuestionCard.builder().type("项目题").question("JWT 权限控制是如何实现的？").answerIdea("说明登录生成 Token、过滤器校验、Security 根据角色授权。").build());
        cards.add(AiInterviewQuestionVO.QuestionCard.builder().type("项目题").question("岗位列表查询变慢如何优化？").answerIdea("从分页、索引、SQL、执行计划和缓存角度回答。").build());
        cards.add(AiInterviewQuestionVO.QuestionCard.builder().type("综合题").question("AI 接口调用失败如何排查？").answerIdea("检查日志、Key、base-url、模型、网络响应和兜底逻辑。").build());
        cards.add(AiInterviewQuestionVO.QuestionCard.builder().type("综合题").question("遇到不熟悉技术任务如何推进？").answerIdea("拆解需求、查官方文档、做最小验证、记录问题并同步进度。").build());
        cards.add(AiInterviewQuestionVO.QuestionCard.builder().type("综合题").question("该项目最大的亮点是什么？").answerIdea("突出业务闭环、四角色权限、AI 接入和无 Key 兜底演示。").build());
        return cards;
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private record AiCallResult(String content, boolean mock, boolean success, String reason, String errorMessage) {
    }
}
