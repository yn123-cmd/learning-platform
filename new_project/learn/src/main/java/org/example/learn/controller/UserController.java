package org.example.learn.controller;

import org.example.learn.common.Result;
import org.example.learn.entity.User;
import org.example.learn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户列表
     */
    @GetMapping
    public Result<List<User>> getUserList() {
        try {
            List<User> userList = userService.findAll();
            return Result.success(userList);
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                return Result.success(userOpt.get());
            } else {
                return Result.notFound("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        try {
            // TODO: 实现用户创建逻辑
            // 设置一个示例ID
            user.setId(System.currentTimeMillis());
            return Result.success("用户创建成功", user);
        } catch (Exception e) {
            return Result.error("创建用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            // TODO: 实现用户更新逻辑
            user.setId(id);
            return Result.success("用户更新成功", user);
        } catch (Exception e) {
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        try {
            // TODO: 实现用户删除逻辑
            return Result.success("用户删除成功");
        } catch (Exception e) {
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                return Result.success(userOpt.get());
            } else {
                return Result.notFound("用户不存在");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        try {
            boolean exists = userService.existsByUsername(username);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error("检查用户名失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error("检查邮箱失败: " + e.getMessage());
        }
    }
} 