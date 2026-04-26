package com.campus.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_record")
public class AiRecord {

    @TableId
    private Long id;

    private Long userId;

    private String functionType;

    private String requestContent;

    private String responseContent;

    private Integer mockFlag;

    private Integer success;

    private String errorMessage;

    private LocalDateTime createdAt;
}
