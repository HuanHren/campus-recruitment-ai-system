package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiJobMatchVO {

    private String source;

    private String model;

    private Integer matchScore;

    private String matchLevel;

    private String matchReason;

    private List<String> skillTags;

    private List<String> suggestions;

    private List<String> learningSuggestions;

    private String recommendReason;

    private Boolean mock;

    private String reason;

    private String errorMessage;
}
