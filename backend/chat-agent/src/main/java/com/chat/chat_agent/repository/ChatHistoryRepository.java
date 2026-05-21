package com.chat.chat_agent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chat.chat_agent.entity.ChatHistory;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    
    // 查询某个用户的聊天记录，按时间正序
    List<ChatHistory> findByUserIdOrderByCreatedAtAsc(Long userId);
    
    // 查询某个用户的最近 N 条记录
    List<ChatHistory> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
}