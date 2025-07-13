package org.example.learn.repository;

import org.example.learn.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业数据访问接口
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    /**
     * 根据课程ID查询作业
     */
    List<Assignment> findByCourseId(Long courseId);
    
    /**
     * 根据班级ID查询作业
     */
    List<Assignment> findByClassId(Long classId);
    
    /**
     * 根据创建者查询作业
     */
    List<Assignment> findByCreatedBy(Long createdBy);
    
    /**
     * 根据状态查询作业
     */
    List<Assignment> findByStatus(Assignment.AssignmentStatus status);
    
    /**
     * 查询即将到期的作业（指定时间内）
     */
    @Query("SELECT a FROM Assignment a WHERE a.dueDate BETWEEN :startTime AND :endTime AND a.status = 'PUBLISHED'")
    List<Assignment> findDueSoon(@Param("startTime") LocalDateTime startTime, 
                                 @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询已过期的作业
     */
    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :now AND a.status = 'PUBLISHED'")
    List<Assignment> findOverdue(@Param("now") LocalDateTime now);
    
    /**
     * 根据课程和班级查询作业
     */
    List<Assignment> findByCourseIdAndClassId(Long courseId, Long classId);
    
    /**
     * 按创建时间排序查询作业
     */
    List<Assignment> findByOrderByCreateTimeDesc();
    
    /**
     * 统计某个教师创建的作业数量
     */
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.createdBy = :teacherId")
    Long countByTeacher(@Param("teacherId") Long teacherId);
    
    /**
     * 统计某个课程的作业数量
     */
    Long countByCourseId(Long courseId);
} 