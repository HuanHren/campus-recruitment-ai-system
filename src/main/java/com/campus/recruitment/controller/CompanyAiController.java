package com.campus.recruitment.controller;

import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.AiJobDescriptionRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.AiService;
import com.campus.recruitment.vo.AiJobDescriptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "企业-AI招聘助手", description = "使用 Spring AI OpenAI ChatModel 接入 DeepSeek OpenAI 兼容接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company/ai")
public class CompanyAiController {

    private final AiService aiService;

    @Operation(summary = "AI生成岗位描述")
    @PostMapping("/job-description")
    public Result<AiJobDescriptionVO> generateJobDescription(@AuthenticationPrincipal SecurityUser securityUser,
                                                             @Valid @RequestBody AiJobDescriptionRequest request) {
        return Result.success(aiService.generateJobDescription(securityUser.getUser().getId(), request));
    }
}
