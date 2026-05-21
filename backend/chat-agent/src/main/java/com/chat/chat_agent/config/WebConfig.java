package com.chat.chat_agent.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")                    // 拦截所有 /api/ 开头的请求
                .excludePathPatterns(
                    "/api/auth/register",                      // 注册接口不拦截
                    "/api/auth/login",                          // 登录接口不拦截
                    "/api/captcha/get"
                );
    }
}