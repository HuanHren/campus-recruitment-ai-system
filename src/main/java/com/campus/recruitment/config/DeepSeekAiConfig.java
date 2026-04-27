package com.campus.recruitment.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class DeepSeekAiConfig {

    @Bean
    @Conditional(DeepSeekApiKeyPresentCondition.class)
    public ChatClient.Builder deepSeekChatClientBuilder(org.springframework.core.env.Environment environment) {
        String apiKey = environment.getProperty("spring.ai.openai.api-key", "");
        String baseUrl = environment.getProperty("spring.ai.openai.base-url", "https://api.deepseek.com");
        String completionsPath = environment.getProperty("spring.ai.openai.chat.completions-path", "/chat/completions");
        String model = environment.getProperty("spring.ai.openai.chat.options.model", "deepseek-v4-pro");
        Double temperature = environment.getProperty("spring.ai.openai.chat.options.temperature", Double.class, 0.7);
        Integer maxTokens = environment.getProperty("spring.ai.openai.chat.options.max-tokens", Integer.class, 2048);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(10));
        requestFactory.setReadTimeout(Duration.ofSeconds(90));

        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .completionsPath(completionsPath)
                .restClientBuilder(RestClient.builder().requestFactory(requestFactory))
                .build();

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .maxTokens(maxTokens)
                        .build())
                .build();

        return ChatClient.builder(chatModel);
    }

    static class DeepSeekApiKeyPresentCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String apiKey = context.getEnvironment().getProperty("spring.ai.openai.api-key", "");
            return StringUtils.hasText(apiKey);
        }
    }
}
