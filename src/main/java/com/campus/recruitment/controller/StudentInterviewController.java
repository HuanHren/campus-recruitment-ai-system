package com.campus.recruitment.controller;

import com.campus.recruitment.common.PageResult;
import com.campus.recruitment.common.Result;
import com.campus.recruitment.dto.InterviewReplyRequest;
import com.campus.recruitment.security.SecurityUser;
import com.campus.recruitment.service.InterviewInvitationService;
import com.campus.recruitment.vo.InterviewInvitationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "学生-面试邀请")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/interviews")
public class StudentInterviewController {

    private final InterviewInvitationService interviewInvitationService;

    @Operation(summary = "学生查看面试邀请")
    @GetMapping
    public Result<PageResult<InterviewInvitationVO>> page(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "邀请状态：PENDING、ACCEPTED、REJECTED") @RequestParam(required = false) String invitationStatus) {
        return Result.success(interviewInvitationService.pageStudentInvitations(
                securityUser.getUser().getId(),
                current,
                size,
                invitationStatus
        ));
    }

    @Operation(summary = "学生接受或拒绝面试邀请")
    @PutMapping("/{id}/reply")
    public Result<InterviewInvitationVO> reply(@AuthenticationPrincipal SecurityUser securityUser,
                                               @PathVariable Long id,
                                               @Valid @RequestBody InterviewReplyRequest request) {
        return Result.success(interviewInvitationService.reply(securityUser.getUser().getId(), id, request));
    }
}
