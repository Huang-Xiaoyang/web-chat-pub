package com.chat.chat_agent.util;

import java.security.SecureRandom;
import java.util.Base64;

public class NonceUtil {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * 生成随机 Nonce（32字节，Base64 URL-Safe 编码）
     */
    public static String generateNonce() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}