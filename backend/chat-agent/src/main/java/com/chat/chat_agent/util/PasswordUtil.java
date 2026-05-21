package com.chat.chat_agent.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/* 慢哈希+随机盐 */
@Component
public class PasswordUtil {
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    /**
     * 加密密码
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    /**
     * 验证密码是否匹配
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}