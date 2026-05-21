package com.chat.chat_agent.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.chat.chat_agent.entity.User;
import com.chat.chat_agent.repository.UserRepository;
import com.chat.chat_agent.util.JwtUtil;
import com.chat.chat_agent.util.PasswordUtil;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordUtil passwordUtil;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 用户注册
     * @param username 用户名
     * @param password 明文密码
     * @return 注册是否成功
     */
    public boolean register(String username, String password) {
        // 1. 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            return false;  // 用户名已存在
        }
        
        // 2. 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordUtil.encode(password));
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        
        // 3. 保存到数据库
        userRepository.save(user);
        return true;
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 明文密码
     * @param captchaId 验证码ID
     * @param captchaAnswer 用户输入的验证码答案
     * @return JWT Token，登录失败返回 null
     */
    public String login(String username, String password, String captchaId, String captchaAnswer) {
        // 1. 验证验证码
        String storedAnswer = redisTemplate.opsForValue().get("captcha:" + captchaId);
        if (storedAnswer == null) {
            return null;  // 验证码已过期
        }
        
        if (!storedAnswer.equalsIgnoreCase(captchaAnswer.trim())) {
            System.out.println("验证码错误: 期望 " + storedAnswer + ", 实际 " + captchaAnswer);
            return null;
        }
        
        // 验证通过后，删除验证码（一次性使用）
        redisTemplate.delete("captcha:" + captchaId);
        
        // 2. 查询用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return null;  // 用户不存在
        }
        
        User user = userOpt.get();
        
        // 3. 检查账户是否被锁定
        if (isAccountLocked(user)) {
            return null;  // 账户已锁定
        }
        
        // 4. 验证密码
        if (!passwordUtil.matches(password, user.getPasswordHash())) {
            // 密码错误，增加失败次数
            handleFailedLogin(user);
            return null;
        }
        
        // 5. 登录成功，重置失败次数
        resetFailedAttempts(user);
        
        // 6. 生成 JWT Token
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }
    
    /**
     * 检查账户是否被锁定
     */
    private boolean isAccountLocked(User user) {
        if (!user.getAccountLocked()) {
            return false;
        }
        
        // 检查锁定时间是否已过
        if (user.getLockExpireTime() != null && 
            LocalDateTime.now().isAfter(user.getLockExpireTime())) {
            // 锁定已过期，解锁账户
            user.setAccountLocked(false);
            user.setFailedAttempts(0);
            user.setLockExpireTime(null);
            userRepository.save(user);
            return false;
        }
        
        return true;
    }
    
    /**
     * 处理登录失败
     */
    private void handleFailedLogin(User user) {
        Integer currentAttempts = user.getFailedAttempts();
        int failedAttempts = (currentAttempts == null ? 0 : currentAttempts) + 1;
        user.setFailedAttempts(failedAttempts);
        
        // 失败3次后锁定账户5分钟
        if (failedAttempts >= 3) {
            user.setAccountLocked(true);
            user.setLockExpireTime(LocalDateTime.now().plusMinutes(5));
        }
        
        userRepository.save(user);
    }
    
    /**
     * 重置失败次数（登录成功后）
     */
    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setAccountLocked(false);
        user.setLockExpireTime(null);
        userRepository.save(user);
    }
}