package com.chat.chat_agent.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chat.chat_agent.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 根据用户名查询用户（用于登录）
    Optional<User> findByUsername(String username);
    
    // 检查用户名是否已存在（用于注册）
    boolean existsByUsername(String username);
}
