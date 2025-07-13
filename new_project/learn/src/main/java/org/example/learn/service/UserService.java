package org.example.learn.service;

import org.example.learn.entity.User;
import org.example.learn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @PostConstruct
    public void initTestUsers() {
        // 如果没有管理员用户，则创建
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@example.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNickName("管理员");
            admin.setStatus(1);
            userRepository.save(admin);
        }
        
        // 如果没有测试用户，则创建
        if (!userRepository.existsByUsername("testuser")) {
            User testUser = new User("testuser", "test@example.com");
            testUser.setPassword(passwordEncoder.encode("123456"));
            testUser.setNickName("测试用户");
            testUser.setStatus(1);
            userRepository.save(testUser);
        }
    }
    
    /**
     * 用户注册
     */
    public User register(String username, String email, String password, String nickName) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建新用户
        User user = new User(username, email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickName(nickName);
        user.setStatus(1);
        
        // 保存到数据库
        User savedUser = userRepository.save(user);
        
        // 返回时不包含密码
        User result = new User(savedUser.getUsername(), savedUser.getEmail());
        result.setId(savedUser.getId());
        result.setNickName(savedUser.getNickName());
        result.setStatus(savedUser.getStatus());
        result.setCreateTime(savedUser.getCreateTime());
        
        return result;
    }
    
    /**
     * 用户登录验证
     */
    public User authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        if (user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 返回用户信息（不包含密码）
        User result = new User(user.getUsername(), user.getEmail());
        result.setId(user.getId());
        result.setNickName(user.getNickName());
        result.setStatus(user.getStatus());
        result.setCreateTime(user.getCreateTime());
        result.setLastLoginTime(user.getLastLoginTime());
        
        return result;
    }
    
    /**
     * 根据ID获取用户
     */
    public Optional<User> findById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        // 返回不包含密码的用户信息
        User result = new User(user.getUsername(), user.getEmail());
        result.setId(user.getId());
        result.setNickName(user.getNickName());
        result.setStatus(user.getStatus());
        result.setCreateTime(user.getCreateTime());
        result.setLastLoginTime(user.getLastLoginTime());
        
        return Optional.of(result);
    }
    
    /**
     * 根据用户名获取用户
     */
    public Optional<User> findByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        // 返回不包含密码的用户信息
        User result = new User(user.getUsername(), user.getEmail());
        result.setId(user.getId());
        result.setNickName(user.getNickName());
        result.setStatus(user.getStatus());
        result.setCreateTime(user.getCreateTime());
        result.setLastLoginTime(user.getLastLoginTime());
        
        return Optional.of(result);
    }
    
    /**
     * 获取所有用户列表
     */
    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        // 移除密码字段
        return users.stream().map(user -> {
            User safeUser = new User(user.getUsername(), user.getEmail());
            safeUser.setId(user.getId());
            safeUser.setNickName(user.getNickName());
            safeUser.setStatus(user.getStatus());
            safeUser.setCreateTime(user.getCreateTime());
            safeUser.setLastLoginTime(user.getLastLoginTime());
            return safeUser;
        }).toList();
    }
    
    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * 检查邮箱是否存在
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
} 