package org.example.learn.controller;

import org.example.learn.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    /**
     * 简单的健康检查接口
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("服务正常运行");
    }
    
    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "学习平台后端");
        info.put("version", "1.0.0");
        info.put("status", "运行中");
        info.put("timestamp", System.currentTimeMillis());
        
        return Result.success("系统信息获取成功", info);
    }
    
    /**
     * Echo接口 - 返回发送的数据
     */
    @PostMapping("/echo")
    public Result<Map<String, Object>> echo(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", data);
        response.put("timestamp", System.currentTimeMillis());
        
        return Result.success("数据回显成功", response);
    }
} 