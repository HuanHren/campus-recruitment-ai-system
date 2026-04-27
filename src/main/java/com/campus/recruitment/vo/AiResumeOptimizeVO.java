package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiResumeOptimizeVO {

    private String source;

    private String model;

    private String title;

    private String content;

    private String optimizedContent;

    private List<String> suggestions;

    private List<String> keywords;

    private List<String> skillKeywords;

    private List<String> highlights;

    private List<String> weaknesses;

    private List<String> improvements;

    private List<String> recommendedJobs;

    private List<String> recommendedPositions;

    private Boolean mock;

    private String reason;

    private String errorMessage;
}
