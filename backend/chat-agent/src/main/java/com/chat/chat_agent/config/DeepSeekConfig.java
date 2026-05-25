package com.chat.chat_agent.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;

@Configuration
public class DeepSeekConfig {
    
    private static final Logger log = LoggerFactory.getLogger(DeepSeekConfig.class);
    @Value("${deepseek.api.api-key:}")
    private String apiKey;

    @Value("${deepseek.api.base-url:}")
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
    public void logConfig() {
        log.debug("API Key loaded: {}", apiKey != null && !apiKey.isEmpty());
        log.debug("API Base URL: {}", baseUrl);
    }
}