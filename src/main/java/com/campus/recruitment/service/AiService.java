package com.campus.recruitment.service;

import com.campus.recruitment.dto.AiInterviewQuestionRequest;
import com.campus.recruitment.dto.AiJobDescriptionRequest;
import com.campus.recruitment.dto.AiJobMatchRequest;
import com.campus.recruitment.dto.AiResumeOptimizeRequest;
import com.campus.recruitment.vo.AiInterviewQuestionVO;
import com.campus.recruitment.vo.AiJobDescriptionVO;
import com.campus.recruitment.vo.AiJobMatchVO;
import com.campus.recruitment.vo.AiResumeOptimizeVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiService {

    AiResumeOptimizeVO optimizeResume(Long userId, AiResumeOptimizeRequest request);

    SseEmitter streamOptimizeResume(Long userId, AiResumeOptimizeRequest request);

    AiJobMatchVO analyzeJobMatch(Long userId, AiJobMatchRequest request);

    AiJobDescriptionVO generateJobDescription(Long userId, AiJobDescriptionRequest request);

    AiInterviewQuestionVO generateInterviewQuestions(Long userId, AiInterviewQuestionRequest request);
}
