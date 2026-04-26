package com.campus.recruitment.controller;

import com.campus.recruitment.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "系统健康检查")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Operation(summary = "获取系统运行状态")
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("name", "campus-recruitment-ai-system");
        data.put("time", LocalDateTime.now());
        return Result.success(data);
    }
}
