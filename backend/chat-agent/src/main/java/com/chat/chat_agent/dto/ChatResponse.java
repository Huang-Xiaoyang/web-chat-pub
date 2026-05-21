package com.chat.chat_agent.dto;

public class ChatResponse {
    private String reply;
    
    // 无参构造
    public ChatResponse() {}
    
    // 带参构造
    public ChatResponse(String reply) {
        this.reply = reply;
    }
    
    // Getter 和 Setter
    public String getReply() {
        return reply;
    }
    
    public void setReply(String reply) {
        this.reply = reply;
    }
}