package org.example.learn.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learn.entity.AIRecommendation;
import org.example.learn.repository.AIRecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AI推荐服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecommendationService {
    
    private final AIRecommendationRepository recommendationRepository;
    
    /**
     * 获取个性化推荐
     */
    @Transactional(readOnly = true)
    public List<AIRecommendation> getPersonalizedRecommendations(Integer limit) {
        // 暂时返回最新的推荐内容
        return recommendationRepository.findActiveRecommendations(limit != null ? limit : 10);
    }
    
    /**
     * 根据推荐类型获取推荐
     */
    @Transactional(readOnly = true)
    public List<AIRecommendation> getRecommendationsByType(AIRecommendation.RecommendationType type, Integer limit) {
        return recommendationRepository.findByRecommendationTypeAndIsActive(type, true)
                .stream()
                .limit(limit != null ? limit : 10)
                .toList();
    }
    
    /**
     * 记录推荐点击
     */
    public boolean recordClick(Long recommendationId) {
        Optional<AIRecommendation> recommendation = recommendationRepository.findById(recommendationId);
        if (recommendation.isPresent()) {
            AIRecommendation rec = recommendation.get();
            rec.setClickCount(rec.getClickCount() + 1);
            rec.setLastClickTime(LocalDateTime.now());
            recommendationRepository.save(rec);
            return true;
        }
        return false;
    }
    
    /**
     * 创建推荐
     */
    public AIRecommendation createRecommendation(AIRecommendation recommendation) {
        // 设置默认值
        if (recommendation.getIsActive() == null) {
            recommendation.setIsActive(true);
        }
        if (recommendation.getClickCount() == null) {
            recommendation.setClickCount(0);
        }
        if (recommendation.getDownloadCount() == null) {
            recommendation.setDownloadCount(0);
        }
        
        return recommendationRepository.save(recommendation);
    }
    
    /**
     * 更新推荐状态
     */
    public boolean updateRecommendationStatus(Long recommendationId, boolean isActive) {
        Optional<AIRecommendation> recommendation = recommendationRepository.findById(recommendationId);
        if (recommendation.isPresent()) {
            AIRecommendation rec = recommendation.get();
            rec.setIsActive(isActive);
            recommendationRepository.save(rec);
            return true;
        }
        return false;
    }
    
    /**
     * 获取热门推荐
     */
    @Transactional(readOnly = true)
    public List<AIRecommendation> getPopularRecommendations(Integer limit) {
        return recommendationRepository.findPopularRecommendations(limit != null ? limit : 10);
    }
    
    /**
     * 根据用户获取推荐
     */
    @Transactional(readOnly = true)
    public List<AIRecommendation> getRecommendationsForUser(Long userId, Integer limit) {
        return recommendationRepository.findByUserIdAndIsActive(userId, true)
                .stream()
                .limit(limit != null ? limit : 10)
                .toList();
    }
    
    /**
     * 获取最新推荐
     */
    @Transactional(readOnly = true)
    public List<AIRecommendation> getLatestRecommendations(Integer limit) {
        return recommendationRepository.findByOrderByCreateTimeDesc()
                .stream()
                .filter(AIRecommendation::getIsActive)
                .limit(limit != null ? limit : 10)
                .toList();
    }
} 