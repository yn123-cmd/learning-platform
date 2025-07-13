package org.example.learn.controller;

import org.example.learn.common.Result;
import org.example.learn.config.DataInitializer;
import org.example.learn.repository.UserRepository;
import org.example.learn.repository.CourseRepository;
import org.example.learn.repository.CourseScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数据管理控制器
 */
@RestController
@RequestMapping("/data")
@CrossOrigin(origins = "*")
public class DataController {
    
    @Autowired
    private DataInitializer dataInitializer;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseScheduleRepository courseScheduleRepository;
    
    /**
     * 手动触发数据初始化
     */
    @PostMapping("/init")
    public Result<String> initData() {
        try {
            System.out.println("手动触发数据初始化...");
            dataInitializer.run();
            return Result.success("数据初始化完成");
        } catch (Exception e) {
            System.err.println("数据初始化失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("数据初始化失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空所有数据
     */
    @DeleteMapping("/clear")
    public Result<String> clearData() {
        try {
            System.out.println("开始清空数据...");
            courseScheduleRepository.deleteAll();
            courseRepository.deleteAll();
            userRepository.deleteAll();
            System.out.println("数据清空完成");
            return Result.success("数据清空完成");
        } catch (Exception e) {
            System.err.println("数据清空失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("数据清空失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取数据统计信息
     */
    @GetMapping("/stats")
    public Result<Object> getDataStats() {
        try {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("userCount", userRepository.count());
            stats.put("courseCount", courseRepository.count());
            stats.put("scheduleCount", courseScheduleRepository.count());
            
            System.out.println("数据统计：用户=" + stats.get("userCount") + 
                             ", 课程=" + stats.get("courseCount") + 
                             ", 课程安排=" + stats.get("scheduleCount"));
            
            return Result.success("获取数据统计成功", stats);
        } catch (Exception e) {
            return Result.error("获取数据统计失败: " + e.getMessage());
        }
    }
} 