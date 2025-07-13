package org.example.learn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learn.common.Result;
import org.example.learn.entity.AIRecommendation;
import org.example.learn.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI推荐控制器
 */
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI推荐管理", description = "AI推荐相关接口")
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    @Operation(summary = "获取个性化推荐", description = "获取为当前用户推荐的内容")
    @GetMapping
    public Result<List<AIRecommendation>> getRecommendations(
            @Parameter(description = "限制返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<AIRecommendation> recommendations = recommendationService.getPersonalizedRecommendations(limit);
            return Result.success(recommendations);
        } catch (Exception e) {
            log.error("获取个性化推荐失败", e);
            return Result.error("获取个性化推荐失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "记录推荐点击", description = "记录用户点击推荐内容的行为")
    @PostMapping("/{recommendationId}/click")
    public Result<Boolean> recordClick(
            @Parameter(description = "推荐ID") @PathVariable Long recommendationId) {
        try {
            boolean recorded = recommendationService.recordClick(recommendationId);
            return Result.success("点击记录成功", recorded);
        } catch (Exception e) {
            log.error("记录推荐点击失败: recommendationId={}", recommendationId, e);
            return Result.error("记录推荐点击失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "根据类型获取推荐", description = "根据推荐类型获取推荐内容")
    @GetMapping("/type/{type}")
    public Result<List<AIRecommendation>> getRecommendationsByType(
            @Parameter(description = "推荐类型") @PathVariable String type,
            @Parameter(description = "限制返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            AIRecommendation.RecommendationType recommendationType = 
                AIRecommendation.RecommendationType.valueOf(type.toUpperCase());
            
            List<AIRecommendation> recommendations = 
                recommendationService.getRecommendationsByType(recommendationType, limit);
            
            return Result.success(recommendations);
        } catch (IllegalArgumentException e) {
            log.error("无效的推荐类型: {}", type, e);
            return Result.error("无效的推荐类型: " + type);
        } catch (Exception e) {
            log.error("根据类型获取推荐失败: type={}", type, e);
            return Result.error("获取推荐失败: " + e.getMessage());
        }
    }
} 