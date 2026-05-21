package com.chat.chat_agent.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.chat.chat_agent.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 放行 OPTIONS 请求（预检请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 从请求头中获取 Token
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"未提供Token，请先登录\"}");
            return false;
        }
        
        String token = authHeader.substring(7);  // 去掉 "Bearer " 前缀
        
        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"Token无效或已过期，请重新登录\"}");
            return false;
        }
        
        // 从 Token 中提取用户信息，存入请求属性（供后续使用）
        String username = jwtUtil.getUsernameFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("username", username);
        request.setAttribute("userId", userId);
        
        return true;
    }
}