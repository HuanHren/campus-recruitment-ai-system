package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiInterviewQuestionVO {

    private String source;

    private String model;

    private List<String> questions;

    private List<QuestionCard> questionCards;

    private Boolean mock;

    private String reason;

    private String errorMessage;

    @Data
    @Builder
    public static class QuestionCard {

        private String type;

        private String question;

        private String answerIdea;

        private String focus;
    }
}
