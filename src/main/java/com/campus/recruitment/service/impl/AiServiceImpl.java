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
    private final MockAiResponseFactory mockAiResponseFactory;

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

        AiCallResult result = callAi(AiFunctionType.RESUME_OPTIMIZE, prompt, mockAiResponseFactory.resumeJson(request.getResumeContent()));
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

        AiCallResult result = callAi(AiFunctionType.JOB_MATCH, prompt, mockAiResponseFactory.jobMatchText());
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

        AiCallResult result = callAi(AiFunctionType.JOB_DESCRIPTION, prompt, mockAiResponseFactory.jobDescription(request));
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

        AiCallResult result = callAi(AiFunctionType.INTERVIEW_QUESTION, prompt, mockAiResponseFactory.interviewQuestionsText(request.getJobName()));
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
        AiCallResult result = new AiCallResult(mockAiResponseFactory.resumeJson(request.getResumeContent()), true, errorMessage == null, reason, errorMessage);
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
            List<String> suggestions = jsonList(json, "suggestions", mockAiResponseFactory.resumeSuggestions());
            List<String> keywords = jsonList(json, "keywords", mockAiResponseFactory.resumeKeywords());
            List<String> highlights = jsonList(json, "highlights", mockAiResponseFactory.resumeHighlights());
            List<String> weaknesses = jsonList(json, "weaknesses", mockAiResponseFactory.resumeWeaknesses());
            List<String> recommendedJobs = jsonList(json, "recommendedJobs", mockAiResponseFactory.resumeRecommendedJobs());
            String optimizedContent = jsonText(json, "content", mockAiResponseFactory.optimizedResumeText());
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
        List<String> suggestions = extractListSection(content, "优化建议", "技能关键词提取", mockAiResponseFactory.resumeSuggestions());
        List<String> keywords = extractListSection(content, "技能关键词提取", "简历亮点总结", mockAiResponseFactory.resumeKeywords());
        List<String> highlights = extractListSection(content, "简历亮点总结", "可改进问题", mockAiResponseFactory.resumeHighlights());
        List<String> weaknesses = extractListSection(content, "可改进问题", "推荐岗位方向", mockAiResponseFactory.resumeWeaknesses());
        List<String> recommendedJobs = extractListSection(content, "推荐岗位方向", null, mockAiResponseFactory.resumeRecommendedJobs());
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
                .skillTags(extractListSection(content, "技能匹配标签", "不足建议", mockAiResponseFactory.defaultSkillTags()))
                .suggestions(extractListSection(content, "不足建议", "学习建议", mockAiResponseFactory.jobMatchSuggestions()))
                .learningSuggestions(extractListSection(content, "学习建议", "推荐投递理由", mockAiResponseFactory.learningSuggestions()))
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
            if ((normalized.startsWith("考察点：") || normalized.startsWith("考察点:")) && !cards.isEmpty()) {
                String focus = normalized.substring(normalized.indexOf("：") >= 0 ? normalized.indexOf("：") + 1 : normalized.indexOf(":") + 1).trim();
                cards.get(cards.size() - 1).setFocus(focus);
                continue;
            }
            if (!normalized.startsWith("参考") && normalized.length() > 6 && cards.size() < 9) {
                currentQuestion = normalized;
            }
        }
        if (cards.isEmpty()) {
            cards.addAll(mockAiResponseFactory.defaultQuestionCards());
        }
        return cards.size() > 9 ? cards.subList(0, 9) : cards;
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

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private record AiCallResult(String content, boolean mock, boolean success, String reason, String errorMessage) {
    }
}
