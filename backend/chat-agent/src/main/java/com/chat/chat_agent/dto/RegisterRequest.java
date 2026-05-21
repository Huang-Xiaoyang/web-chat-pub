package com.chat.chat_agent.dto;

public class RegisterRequest {
    private String username;
    private String password;

    public RegisterRequest(){}
    public String getUsername(){
        return username;
    }
    public void serUsername(String username){
        this.username = username;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
