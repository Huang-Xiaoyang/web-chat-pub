package com.chat.chat_agent.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.chat_agent.dto.HistoryResponse;
import com.chat.chat_agent.entity.ChatHistory;
import com.chat.chat_agent.repository.ChatHistoryRepository;

@Service
public class ChatService {
    
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;
    
    @Autowired
    private DeepSeekService deepSeekService;  // 新增注入
    
    /**
     * 处理用户消息，返回 AI 回复
     */
    public String chat(Long userId, String userMessage) {
        // 1. 保存用户消息
        ChatHistory userChat = new ChatHistory(userId, "user", userMessage);
        chatHistoryRepository.save(userChat);
        
        // 2. 调用 DeepSeek API 生成 AI 回复
        String aiReply = deepSeekService.chat(userMessage);  // 替换为真实调用
        
        // 3. 保存 AI 回复
        ChatHistory aiChat = new ChatHistory(userId, "assistant", aiReply);
        chatHistoryRepository.save(aiChat);
        
        // 4. 返回 AI 回复
        return aiReply;
    }
    
    /**
     * 获取用户的历史聊天记录
     */
    public List<HistoryResponse> getHistory(Long userId) {
        List<ChatHistory> historyList = chatHistoryRepository.findByUserIdOrderByCreatedAtAsc(userId);
        
        return historyList.stream()
                .map(chat -> new HistoryResponse(
                    chat.getId(),
                    chat.getRole(),
                    chat.getContent(),
                    chat.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}