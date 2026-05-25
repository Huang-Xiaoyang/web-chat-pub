package com.chat.chat_agent.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;
    
    @Autowired
    private AuditLogInterceptor auditLogInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 审计日志拦截器（记录所有请求）
        registry.addInterceptor(auditLogInterceptor)
                .addPathPatterns("/**")
                .order(1);
        
        // JWT 认证拦截器
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/captcha/get"
                )
                .order(2);
    }
}