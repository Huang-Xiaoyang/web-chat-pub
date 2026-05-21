package com.chat.chat_agent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat.chat_agent.dto.ChatRequest;
import com.chat.chat_agent.dto.ChatResponse;
import com.chat.chat_agent.dto.HistoryResponse;
import com.chat.chat_agent.service.ChatService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    /**
     * 发送消息，获取 AI 回复
     * POST /api/chat/send
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest request,
                                          HttpServletRequest httpRequest) {
        // 从拦截器中获取用户ID
        Long userId = (Long) httpRequest.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("用户未认证");
        }
        
        // 参数校验
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("消息不能为空");
        }
        
        try {
            // 调用 AI 服务
            String reply = chatService.chat(userId, request.getMessage());
            return ResponseEntity.ok(new ChatResponse(reply));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("AI服务出错: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的历史聊天记录
     * GET /api/chat/history
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(HttpServletRequest httpRequest) {
        // 从拦截器中获取用户ID
        Long userId = (Long) httpRequest.getAttribute("userId");
        
        if (userId == null) {
            return ResponseEntity.status(401).body("用户未认证");
        }
        
        List<HistoryResponse> history = chatService.getHistory(userId);
        return ResponseEntity.ok(history);
    }
}