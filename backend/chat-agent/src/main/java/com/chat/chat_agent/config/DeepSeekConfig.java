package com.chat.chat_agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;

@Configuration
public class DeepSeekConfig {
    
    @Value("${DEEPSEEK_API_KEY}")
    private String apiKey;

    @Value("${DEEPSEEK_BASE_URL}")
    private String baseUrl;
    
    @Bean
    public RestClient deepSeekRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @PostConstruct
    public void logApiKey() {
        System.out.println("API Key loaded: " + (apiKey != null && !apiKey.isEmpty()));
        System.out.println("API Base URL: " + baseUrl);
    }
}