package com.chat.chat_agent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.chat.chat_agent.entity.AuditLog;
import com.chat.chat_agent.repository.AuditLogRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuditLogService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Async
    public void logRequest(HttpServletRequest request, Long userId, String username, 
                           Integer responseStatus, Long executionTime) {
        String ip = getClientIp(request);
        
        AuditLog log = new AuditLog(
            request.getRequestURI(),
            request.getMethod(),
            ip,
            userId,
            username,
            responseStatus,
            executionTime
        );
        
        auditLogRepository.save(log);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}