package com.campus.recruitment.controller;

import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.AiInterviewQuestionRequest;
import com.campus.recruitment.dto.AiJobMatchRequest;
import com.campus.recruitment.dto.AiResumeOptimizeRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.AiService;
import com.campus.recruitment.vo.AiInterviewQuestionVO;
import com.campus.recruitment.vo.AiJobMatchVO;
import com.campus.recruitment.vo.AiResumeOptimizeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "学生-AI就业助手", description = "使用 Spring AI OpenAI ChatModel 接入 DeepSeek OpenAI 兼容接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/ai")
public class StudentAiController {

    private final AiService aiService;

    @Operation(summary = "AI简历优化")
    @PostMapping("/resume-optimize")
    public Result<AiResumeOptimizeVO> optimizeResume(@AuthenticationPrincipal SecurityUser securityUser,
                                                     @Valid @RequestBody AiResumeOptimizeRequest request) {
        return Result.success(aiService.optimizeResume(securityUser.getUser().getId(), request));
    }

    @Operation(summary = "AI岗位匹配度分析")
    @PostMapping("/job-match")
    public Result<AiJobMatchVO> analyzeJobMatch(@AuthenticationPrincipal SecurityUser securityUser,
                                                @Valid @RequestBody AiJobMatchRequest request) {
        return Result.success(aiService.analyzeJobMatch(securityUser.getUser().getId(), request));
    }

    @Operation(summary = "AI模拟面试题生成")
    @PostMapping("/interview-questions")
    public Result<AiInterviewQuestionVO> generateInterviewQuestions(@AuthenticationPrincipal SecurityUser securityUser,
                                                                    @Valid @RequestBody AiInterviewQuestionRequest request) {
        return Result.success(aiService.generateInterviewQuestions(securityUser.getUser().getId(), request));
    }
}
