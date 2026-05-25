package com.chat.chat_agent.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.chat.chat_agent.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuditLogInterceptor implements HandlerInterceptor {
    
    @Autowired
    private AuditLogService auditLogService;
    
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        startTime.set(System.currentTimeMillis());
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        Long start = startTime.get();
        if (start != null) {
            Long executionTime = System.currentTimeMillis() - start;
            
            Long userId = (Long) request.getAttribute("userId");
            String username = (String) request.getAttribute("username");
            
            auditLogService.logRequest(request, userId, username, 
                                       response.getStatus(), executionTime);
            startTime.remove();
        }
    }
}