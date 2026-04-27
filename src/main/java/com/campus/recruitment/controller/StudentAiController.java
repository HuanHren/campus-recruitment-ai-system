package com.campus.recruitment.controller;

import com.campus.recruitment.common.BusinessException;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.AiInterviewQuestionRequest;
import com.campus.recruitment.dto.AiJobMatchRequest;
import com.campus.recruitment.dto.AiResumeOptimizeRequest;
import com.campus.recruitment.security.CustomUserDetailsService;
import com.campus.recruitment.security.JwtUtil;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.AiService;
import com.campus.recruitment.vo.AiInterviewQuestionVO;
import com.campus.recruitment.vo.AiJobMatchVO;
import com.campus.recruitment.vo.AiResumeOptimizeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "学生-AI就业助手", description = "使用 Spring AI OpenAI ChatModel 接入 DeepSeek OpenAI 兼容接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/ai")
public class StudentAiController {

    private final AiService aiService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Operation(summary = "AI简历优化")
    @PostMapping("/resume-optimize")
    public Result<AiResumeOptimizeVO> optimizeResume(@AuthenticationPrincipal SecurityUser securityUser,
                                                     @Valid @RequestBody AiResumeOptimizeRequest request) {
        return Result.success(aiService.optimizeResume(securityUser.getUser().getId(), request));
    }

    @Operation(summary = "AI简历优化流式输出")
    @GetMapping(value = "/resume-optimize/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOptimizeResume(@RequestParam String resumeContent,
                                           @RequestParam String token) {
        SecurityUser securityUser = authenticateStreamToken(token);
        AiResumeOptimizeRequest request = new AiResumeOptimizeRequest();
        request.setResumeContent(resumeContent);
        return aiService.streamOptimizeResume(securityUser.getUser().getId(), request);
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

    private SecurityUser authenticateStreamToken(String token) {
        try {
            String username = jwtUtil.getUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtil.validateToken(token, userDetails) || !(userDetails instanceof SecurityUser securityUser)) {
                throw new BusinessException(401, "Token无效或已过期");
            }
            if (!"STUDENT".equals(securityUser.getUser().getRole())) {
                throw new BusinessException(403, "无权访问学生AI接口");
            }
            return securityUser;
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(401, "Token无效或已过期");
        }
    }
}
