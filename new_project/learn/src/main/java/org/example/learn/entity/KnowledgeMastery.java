package org.example.learn.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 知识点掌握情况实体类
 */
@Entity
@Table(name = "knowledge_mastery")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class KnowledgeMastery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "knowledge_point", nullable = false, length = 100)
    private String knowledgePoint;
    
    @Column(name = "knowledge_category", length = 50)
    private String knowledgeCategory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "mastery_level", nullable = false)
    private MasteryLevel masteryLevel = MasteryLevel.NOT_LEARNED;
    
    @Column(name = "test_score")
    private Integer testScore;
    
    @Column(name = "test_count")
    private Integer testCount = 0;
    
    @Column(name = "mistake_count")
    private Integer mistakeCount = 0;
    
    @Column(name = "last_test_time")
    private LocalDateTime lastTestTime;
    
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;
    
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
     * 计算掌握度百分比
     */
    public double getMasteryPercentage() {
        if (testCount == 0) return 0.0;
        
        double baseScore = testScore != null ? testScore : 0;
        double mistakePenalty = mistakeCount * 5; // 每次错误扣5分
        
        return Math.max(0, Math.min(100, baseScore - mistakePenalty));
    }
    
    public enum MasteryLevel {
        NOT_LEARNED, LEARNING, MASTERED, EXPERT
    }
} 