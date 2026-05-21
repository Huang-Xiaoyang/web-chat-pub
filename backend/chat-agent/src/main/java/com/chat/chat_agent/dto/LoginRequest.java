package com.chat.chat_agent.dto;

public class LoginRequest {
    private String username;
    private String password;
    private String captchaId;      // 新增
    private String captchaAnswer;  // 新增
    
    // 无参构造
    public LoginRequest() {}
    
    // Getter 和 Setter
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getCaptchaId() {
        return captchaId;
    }
    
    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }
    
    public String getCaptchaAnswer() {
        return captchaAnswer;
    }
    
    public void setCaptchaAnswer(String captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
    }
}