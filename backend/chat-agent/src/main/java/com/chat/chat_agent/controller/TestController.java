package com.chat.chat_agent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping
    public String test(@RequestAttribute("username") String username,
                       @RequestAttribute("userId") Long userId) {
        return "认证成功！用户：" + username + "，ID：" + userId;
    }
}