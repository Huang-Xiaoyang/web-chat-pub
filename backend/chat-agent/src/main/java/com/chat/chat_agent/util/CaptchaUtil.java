package com.chat.chat_agent.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CaptchaUtil {
    
    private static final Random random = new Random();
    
    /**
     * 生成算术验证码
     * @return Map 包含 expression（表达式）和 result（结果）
     */
    public static Map<String, Object> generateArithmeticCaptcha() {
        int num1 = random.nextInt(50) + 1;  // 1-50
        int num2 = random.nextInt(50) + 1;  // 1-50
        int operator = random.nextInt(2);    // 0=加法, 1=减法
        
        String expression;
        int result;
        
        if (operator == 0) {
            expression = num1 + " + " + num2;
            result = num1 + num2;
        } else {
            // 确保被减数大于等于减数
            if (num1 < num2) {
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }
            expression = num1 + " - " + num2;
            result = num1 - num2;
        }
        
        Map<String, Object> captcha = new HashMap<>();
        captcha.put("expression", expression);
        captcha.put("result", result);
        
        return captcha;
    }
}