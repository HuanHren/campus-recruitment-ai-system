package com.campus.recruitment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.campus.recruitment.mapper")
@SpringBootApplication(excludeName = "org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration")
public class CampusRecruitmentAiSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusRecruitmentAiSystemApplication.class, args);
    }
}
