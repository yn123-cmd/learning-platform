package org.example.learn.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级实体类
 */
@Entity
@Table(name = "classes")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class Class {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_name", nullable = false, length = 100)
    private String className;
    
    @Column(name = "class_code", unique = true, length = 20)
    private String classCode;
    
    @Column(length = 20)
    private String grade;
    
    @Column(length = 50)
    private String major;
    
    @Column(name = "teacher_id")
    private Long teacherId;
    
    @Column(name = "student_count")
    private Integer studentCount = 0;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Integer status = 1;
    
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private User teacher;
    
    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private List<Student> students;
    
    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private List<CourseSchedule> schedules;
    
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