package com.chat.chat_agent.dto;

public class ChatRequest {
    private String message;
    
    // 无参构造
    public ChatRequest() {}
    
    // Getter 和 Setter
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}