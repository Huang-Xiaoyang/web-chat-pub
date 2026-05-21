package com.chat.chat_agent.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DeepSeekService {
    
    private static final Logger log = LoggerFactory.getLogger(DeepSeekService.class);
    
    @Autowired
    private RestClient deepSeekRestClient;
    
    @Value("${DEEPSEEK_MODEL:deepseek-chat}")
    private String model;
    
    @Value("${deepseek.api.max-tokens:2000}")
    private Integer maxTokens;
    
    @Value("${deepseek.api.temperature:0.7}")
    private Double temperature;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public String chat(String userMessage) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "你是一个友好的AI助手，帮助用户解答问题。"),
                Map.of("role", "user", "content", userMessage)
            ));
            
            String response = deepSeekRestClient.post()
                    .uri("/v1/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
            
            return parseResponse(response);
            
        } catch (Exception e) {
            log.error("DeepSeek API 调用失败", e);
            return "抱歉，AI服务暂时不可用，请稍后再试。";
        }
    }
    
    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).path("message");
                return message.path("content").asText();
            }
            return "无法解析AI回复";
        } catch (Exception e) {
            log.error("解析 DeepSeek 响应失败: {}", e.getMessage(), e);
            return "解析AI回复失败";
        }
    }
}