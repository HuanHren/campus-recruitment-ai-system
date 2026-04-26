package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiInterviewQuestionVO {

    private List<String> questions;

    private Boolean mock;
}
