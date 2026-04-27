package com.campus.recruitment.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiJobDescriptionVO {

    private String source;

    private String model;

    private String responsibility;

    private String requirement;

    private Boolean mock;

    private String reason;

    private String errorMessage;
}
