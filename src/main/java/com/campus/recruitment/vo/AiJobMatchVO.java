package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiJobMatchVO {

    private Integer matchScore;

    private String matchReason;

    private List<String> suggestions;

    private Boolean mock;
}
