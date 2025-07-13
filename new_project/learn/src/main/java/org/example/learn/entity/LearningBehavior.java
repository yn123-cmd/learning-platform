package org.example.learn.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 学习行为记录实体类
 */
@Entity
@Table(name = "learning_behaviors")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class LearningBehavior {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "course_id")
    private Long courseId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "behavior_type")
    private BehaviorType behaviorType;
    
    @Column(name = "behavior_data", columnDefinition = "JSON")
    private String behaviorData;
    
    @Column(name = "duration")
    private Integer duration; // 学习时长（分钟）
    
    @Column(name = "device_type", length = 20)
    private String deviceType;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
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
    }
    
    public enum BehaviorType {
        LOGIN, VIEW_MATERIAL, SUBMIT_ASSIGNMENT, PARTICIPATE_DISCUSSION, WATCH_VIDEO, COMPLETE_QUIZ
    }
} 