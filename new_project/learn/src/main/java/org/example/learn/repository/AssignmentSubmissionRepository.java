package org.example.learn.repository;

import org.example.learn.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 作业提交数据访问接口
 */
@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    
    /**
     * 根据作业ID查询提交
     */
    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);
    
    /**
     * 根据学生ID查询提交
     */
    List<AssignmentSubmission> findByStudentId(Long studentId);
    
    /**
     * 根据状态查询提交
     */
    List<AssignmentSubmission> findByStatus(AssignmentSubmission.SubmissionStatus status);
    
    /**
     * 根据作业ID和学生ID查询提交（唯一）
     */
    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    
    /**
     * 统计指定状态的提交数量
     */
    Long countByStatus(AssignmentSubmission.SubmissionStatus status);
    
    /**
     * 统计某个作业的提交数量
     */
    Long countByAssignmentId(Long assignmentId);
    
    /**
     * 统计某个学生的提交数量
     */
    Long countByStudentId(Long studentId);
    
    /**
     * 查询指定时间范围内的提交
     */
    List<AssignmentSubmission> findBySubmittedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内的提交数量
     */
    Long countBySubmittedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 计算平均分数
     */
    @Query("SELECT AVG(s.score) FROM AssignmentSubmission s WHERE s.score IS NOT NULL")
    Double findAverageScore();
    
    /**
     * 计算某个作业的平均分数
     */
    @Query("SELECT AVG(s.score) FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.score IS NOT NULL")
    Double findAverageScoreByAssignment(@Param("assignmentId") Long assignmentId);
    
    /**
     * 查询需要批改的提交（已提交但未批改）
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.status = 'SUBMITTED' AND s.score IS NULL")
    List<AssignmentSubmission> findPendingGrading();
    
    /**
     * 查询迟交的提交
     */
    @Query("SELECT s FROM AssignmentSubmission s JOIN Assignment a ON s.assignmentId = a.id " +
           "WHERE s.submittedAt > a.dueDate")
    List<AssignmentSubmission> findLateSubmissions();
    
    /**
     * 统计某个学生的平均分数
     */
    @Query("SELECT AVG(s.score) FROM AssignmentSubmission s WHERE s.studentId = :studentId AND s.score IS NOT NULL")
    Double findAverageScoreByStudent(@Param("studentId") Long studentId);
} 