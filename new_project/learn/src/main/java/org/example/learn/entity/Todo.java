package org.example.learn.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 待办事项实体类
 */
@Entity
@Table(name = "todos")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class Todo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.NORMAL;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoStatus status = TodoStatus.PENDING;
    
    @Column(length = 50)
    private String category;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "reminder_time")
    private LocalDateTime reminderTime;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
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
        if (status == TodoStatus.COMPLETED && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
    
    public enum Priority {
        URGENT, IMPORTANT, NORMAL, LOW
    }
    
    public enum TodoStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }
} 