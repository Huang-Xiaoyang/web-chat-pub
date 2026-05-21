package com.chat.chat_agent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chat.chat_agent.dto.LoginRequest;
import com.chat.chat_agent.dto.LoginResponse;
import com.chat.chat_agent.dto.RegisterRequest;
import com.chat.chat_agent.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")  // 允许前端跨域访问
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("密码长度不能少于6位");
        }
        
        // 调用注册服务
        boolean success = userService.register(request.getUsername(), request.getPassword());
        
        if (success) {
            return ResponseEntity.ok("注册成功");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("用户名已存在");
        }
    }
    
    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 参数校验
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("密码不能为空");
        }
        
        // 调用登录服务
        String token = userService.login(
            request.getUsername(), 
            request.getPassword(),
            request.getCaptchaId(),
            request.getCaptchaAnswer()
        );
        
        if (token != null) {
            LoginResponse response = new LoginResponse(token, request.getUsername(), null);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误，或账户已锁定");
        }
    }
}