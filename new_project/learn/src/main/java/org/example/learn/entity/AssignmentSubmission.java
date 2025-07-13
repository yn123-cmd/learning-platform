package org.example.learn.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 作业提交实体类
 */
@Entity
@Table(name = "assignment_submissions")
@Data
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class AssignmentSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "submission_content", columnDefinition = "TEXT")
    private String submissionContent;
    
    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;
    
    private Integer score;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    @Column(name = "graded_by")
    private Long gradedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status = SubmissionStatus.PENDING;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by", insertable = false, updatable = false)
    private User grader;
    
    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }
    
    public enum SubmissionStatus {
        PENDING, SUBMITTED, GRADED, RETURNED
    }
} 