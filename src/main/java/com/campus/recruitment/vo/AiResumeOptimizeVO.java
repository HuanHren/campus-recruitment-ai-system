package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiResumeOptimizeVO {

    private String optimizedContent;

    private Boolean mock;
}
