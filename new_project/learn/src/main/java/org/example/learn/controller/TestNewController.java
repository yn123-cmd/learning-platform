package org.example.learn.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestNewController {
    
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from new controller!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "success");
        return response;
    }
    
    @GetMapping("/schedules/today")
    public Map<String, Object> testSchedulesToday() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test schedules today endpoint");
        response.put("data", "No data - this is a test");
        response.put("status", "success");
        return response;
    }
} 