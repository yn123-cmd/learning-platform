package org.example.learn.repository;

import org.example.learn.entity.LearningBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习行为数据访问接口
 */
@Repository
public interface LearningBehaviorRepository extends JpaRepository<LearningBehavior, Long> {
    
    /**
     * 根据学生ID查询学习行为
     */
    List<LearningBehavior> findByStudentId(Long studentId);
    
    /**
     * 根据课程ID查询学习行为
     */
    List<LearningBehavior> findByCourseId(Long courseId);
    
    /**
     * 根据行为类型查询
     */
    List<LearningBehavior> findByBehaviorType(LearningBehavior.BehaviorType behaviorType);
    
    /**
     * 根据时间范围查询学习行为
     */
    List<LearningBehavior> findByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据学生ID和时间范围查询
     */
    List<LearningBehavior> findByStudentIdAndCreateTimeBetween(Long studentId, 
                                                               LocalDateTime startTime, 
                                                               LocalDateTime endTime);
    
    /**
     * 统计指定时间后的学习行为数量
     */
    Long countByCreateTimeAfter(LocalDateTime since);
    
    /**
     * 统计指定时间范围内的学习行为数量
     */
    Long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内的不同学生数量
     */
    @Query("SELECT COUNT(DISTINCT b.studentId) FROM LearningBehavior b WHERE b.createTime BETWEEN :startTime AND :endTime")
    Long countDistinctStudentsByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);
    
    /**
     * 计算总学习时长
     */
    @Query("SELECT COALESCE(SUM(b.duration), 0) FROM LearningBehavior b WHERE b.duration IS NOT NULL")
    Long sumAllDuration();
    
    /**
     * 计算指定时间范围内的学习时长
     */
    @Query("SELECT COALESCE(SUM(b.duration), 0) FROM LearningBehavior b WHERE b.createTime BETWEEN :startTime AND :endTime AND b.duration IS NOT NULL")
    Long sumDurationByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                        @Param("endTime") LocalDateTime endTime);
    
    /**
     * 计算某个学生的总学习时长
     */
    @Query("SELECT COALESCE(SUM(b.duration), 0) FROM LearningBehavior b WHERE b.studentId = :studentId AND b.duration IS NOT NULL")
    Long sumDurationByStudent(@Param("studentId") Long studentId);
    
    /**
     * 计算某个课程的总学习时长
     */
    @Query("SELECT COALESCE(SUM(b.duration), 0) FROM LearningBehavior b WHERE b.courseId = :courseId AND b.duration IS NOT NULL")
    Long sumDurationByCourse(@Param("courseId") Long courseId);
    
    /**
     * 查询最活跃的学生（按行为次数）
     */
    @Query("SELECT b.studentId, COUNT(b) as behaviorCount FROM LearningBehavior b " +
           "WHERE b.createTime > :since GROUP BY b.studentId ORDER BY behaviorCount DESC")
    List<Object[]> findMostActiveStudents(@Param("since") LocalDateTime since);
    
    /**
     * 查询某个学生的行为统计（按类型分组）
     */
    @Query("SELECT b.behaviorType, COUNT(b) FROM LearningBehavior b WHERE b.studentId = :studentId GROUP BY b.behaviorType")
    List<Object[]> countBehaviorTypesByStudent(@Param("studentId") Long studentId);
    
    /**
     * 查询某个课程的行为统计（按类型分组）
     */
    @Query("SELECT b.behaviorType, COUNT(b) FROM LearningBehavior b WHERE b.courseId = :courseId GROUP BY b.behaviorType")
    List<Object[]> countBehaviorTypesByCourse(@Param("courseId") Long courseId);
    
    /**
     * 查询最近的学习行为
     */
    List<LearningBehavior> findByOrderByCreateTimeDesc();
    
    /**
     * 查询某个学生最近的学习行为
     */
    List<LearningBehavior> findByStudentIdOrderByCreateTimeDesc(Long studentId);
    
    /**
     * 查询学习时间较长的行为记录
     */
    @Query("SELECT b FROM LearningBehavior b WHERE b.duration > :minDuration ORDER BY b.duration DESC")
    List<LearningBehavior> findLongDurationBehaviors(@Param("minDuration") Integer minDuration);
    
    /**
     * 按日期统计学习行为
     */
    @Query("SELECT DATE(b.createTime), COUNT(b) FROM LearningBehavior b " +
           "WHERE b.createTime BETWEEN :startTime AND :endTime " +
           "GROUP BY DATE(b.createTime) ORDER BY DATE(b.createTime)")
    List<Object[]> countByDateRange(@Param("startTime") LocalDateTime startTime, 
                                    @Param("endTime") LocalDateTime endTime);
} 