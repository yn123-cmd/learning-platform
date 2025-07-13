package org.example.learn.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 */
@Entity
@Table(name = "courses")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(name = "course_code", unique = true, length = 20)
    private String courseCode;
    
    @Column(length = 500)
    private String subtitle;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "cover_image", length = 255)
    private String coverImage;
    
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @Column(length = 20)
    private String semester;
    
    @Column(nullable = false)
    private Integer credits = 3;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;
    
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel = "BEGINNER";
    
    @Column(name = "duration_hours")
    private Integer durationHours;
    
    @Column(length = 20)
    private String status = "DRAFT"; // DRAFT, PUBLISHED, OFFLINE
    
    @Column(name = "is_free")
    private Boolean isFree = false;
    
    @Column(name = "student_count")
    private Integer studentCount = 0;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Column(name = "rating_count")
    private Integer ratingCount = 0;
    
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
} 