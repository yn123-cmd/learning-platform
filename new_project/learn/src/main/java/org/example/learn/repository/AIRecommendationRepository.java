package org.example.learn.repository;

import org.example.learn.entity.AIRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI推荐数据访问接口
 */
@Repository
public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, Long> {
    
    /**
     * 根据推荐类型查询推荐
     */
    List<AIRecommendation> findByRecommendationType(AIRecommendation.RecommendationType recommendationType);
    
    /**
     * 根据推荐类型和状态查询推荐
     */
    List<AIRecommendation> findByRecommendationTypeAndIsActive(AIRecommendation.RecommendationType recommendationType, Boolean isActive);
    
    /**
     * 根据用户ID查询推荐
     */
    List<AIRecommendation> findByUserId(Long userId);
    
    /**
     * 根据用户ID和状态查询推荐
     */
    List<AIRecommendation> findByUserIdAndIsActive(Long userId, Boolean isActive);
    
    /**
     * 根据是否激活查询推荐
     */
    List<AIRecommendation> findByIsActive(Boolean isActive);
    
    /**
     * 按创建时间倒序查询推荐
     */
    List<AIRecommendation> findByOrderByCreateTimeDesc();
    
    /**
     * 查询激活的推荐（带限制数量）
     */
    @Query("SELECT r FROM AIRecommendation r WHERE r.isActive = true ORDER BY r.createTime DESC LIMIT :limit")
    List<AIRecommendation> findActiveRecommendations(@Param("limit") Integer limit);
    
    /**
     * 查询热门推荐（按点击量排序）
     */
    @Query("SELECT r FROM AIRecommendation r WHERE r.isActive = true ORDER BY r.clickCount DESC LIMIT :limit")
    List<AIRecommendation> findPopularRecommendations(@Param("limit") Integer limit);
    
    /**
     * 统计某类型的推荐数量
     */
    Long countByRecommendationType(AIRecommendation.RecommendationType recommendationType);
    
    /**
     * 统计某用户的推荐数量
     */
    Long countByUserId(Long userId);
    
    /**
     * 统计激活状态的推荐数量
     */
    Long countByIsActive(Boolean isActive);
    
    /**
     * 查询某用户某类型的推荐
     */
    List<AIRecommendation> findByUserIdAndRecommendationType(Long userId, AIRecommendation.RecommendationType recommendationType);
    
    /**
     * 查询评分最高的推荐
     */
    @Query("SELECT r FROM AIRecommendation r WHERE r.isActive = true AND r.rating IS NOT NULL ORDER BY r.rating DESC LIMIT :limit")
    List<AIRecommendation> findTopRatedRecommendations(@Param("limit") Integer limit);
    
    /**
     * 查询下载最多的推荐
     */
    @Query("SELECT r FROM AIRecommendation r WHERE r.isActive = true ORDER BY r.downloadCount DESC LIMIT :limit")
    List<AIRecommendation> findMostDownloadedRecommendations(@Param("limit") Integer limit);
} 