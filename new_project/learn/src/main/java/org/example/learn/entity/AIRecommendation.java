package org.example.learn.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI推荐记录实体类
 */
@Entity
@Table(name = "ai_recommendations")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class AIRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type")
    private RecommendationType recommendationType;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "content_url", length = 500)
    private String contentUrl;
    
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;
    
    @Column(precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Column(name = "download_count")
    private Integer downloadCount = 0;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "like_count")
    private Integer likeCount = 0;
    
    @Column(name = "click_count")
    private Integer clickCount = 0;
    
    @Column(name = "recommendation_score", precision = 5, scale = 2)
    private BigDecimal recommendationScore;
    
    @Column(name = "algorithm_version", length = 20)
    private String algorithmVersion;
    
    @Column(name = "is_clicked")
    private Boolean isClicked = false;
    
    @Column(name = "is_downloaded")
    private Boolean isDownloaded = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "feedback_score")
    private Integer feedbackScore; // 用户反馈分数(1-5)
    
    @Column(name = "last_click_time")
    private LocalDateTime lastClickTime;
    
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
    
    /**
     * 增加查看次数
     */
    public void incrementViewCount() {
        this.viewCount = this.viewCount + 1;
    }
    
    /**
     * 增加下载次数
     */
    public void incrementDownloadCount() {
        this.downloadCount = this.downloadCount + 1;
        this.isDownloaded = true;
    }
    
    /**
     * 点击推荐
     */
    public void click() {
        this.isClicked = true;
        this.clickCount = this.clickCount + 1;
        this.lastClickTime = LocalDateTime.now();
        incrementViewCount();
    }
    
    public enum RecommendationType {
        LESSON_PLAN, VIDEO, EXERCISE, MATERIAL, COURSE, STUDY_PATH
    }
} 