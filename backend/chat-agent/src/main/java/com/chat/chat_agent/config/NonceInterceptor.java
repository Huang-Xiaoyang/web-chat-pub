package com.chat.chat_agent.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class NonceInterceptor implements HandlerInterceptor {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    // Nonce 有效期（秒）
    private static final long NONCE_TTL_SECONDS = 300; // 5分钟
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                             Object handler) throws Exception {
        
        String path = request.getRequestURI();
        
        // 只对发送消息接口进行防重放检查
        if (!path.contains("/chat/send")) {
            return true;
        }
        
        // 只对 POST 请求检查
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 从请求头获取 Nonce
        String nonce = request.getHeader("X-Nonce");
        
        if (nonce == null || nonce.trim().isEmpty()) {
            response.setStatus(400);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"缺少防重放标识(Nonce)\"}");
            return false;
        }
        
        // 检查 Nonce 是否已被使用
        String redisKey = "nonce:" + nonce;
        Boolean isUsed = redisTemplate.opsForValue()
            .setIfAbsent(redisKey, "1", NONCE_TTL_SECONDS, TimeUnit.SECONDS);
        
        if (Boolean.FALSE.equals(isUsed)) {
            // Nonce 已存在，说明是重放攻击
            response.setStatus(409);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"请求重复，可能是重放攻击\"}");
            return false;
        }
        
        return true;
    }
}