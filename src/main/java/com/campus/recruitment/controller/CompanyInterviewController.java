package com.campus.recruitment.controller;

import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.InterviewInvitationRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.InterviewInvitationService;
import com.campus.recruitment.vo.InterviewInvitationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "企业-面试邀请")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company/interviews")
public class CompanyInterviewController {

    private final InterviewInvitationService interviewInvitationService;

    @Operation(summary = "企业发送面试邀请")
    @PostMapping
    public Result<InterviewInvitationVO> send(@AuthenticationPrincipal SecurityUser securityUser,
                                              @Valid @RequestBody InterviewInvitationRequest request) {
        return Result.success(interviewInvitationService.send(securityUser.getUser().getId(), request));
    }
}
