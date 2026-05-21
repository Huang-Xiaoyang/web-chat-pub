package com.chat.chat_agent.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/captcha")
@CrossOrigin(origins = "*")
public class CaptchaController {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 获取图形验证码
     * GET /api/captcha/get
     */
    @GetMapping("/get")
    public Map<String, String> getCaptcha() {
        // 生成图形验证码（宽度200，高度80，干扰线5条，干扰点50个）
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 80, 5, 50);
        
        // 获取验证码文本（全小写，方便用户）
        String code = lineCaptcha.getCode().toLowerCase();
        
        // 获取验证码图片（Base64格式）
        String imageBase64 = lineCaptcha.getImageBase64();
        
        // 生成唯一ID
        String captchaId = UUID.randomUUID().toString();
        
        // 存储到 Redis，有效期 5 分钟
        redisTemplate.opsForValue().set(
            "captcha:" + captchaId, 
            code, 
            5, 
            TimeUnit.MINUTES
        );
        
        Map<String, String> response = new HashMap<>();
        response.put("captchaId", captchaId);
        response.put("image", "data:image/png;base64," + imageBase64);
        
        return response;
    }
}