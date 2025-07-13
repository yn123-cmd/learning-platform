package org.example.learn.repository;

import org.example.learn.entity.KnowledgeMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 知识掌握数据访问接口
 */
@Repository
public interface KnowledgeMasteryRepository extends JpaRepository<KnowledgeMastery, Long> {
    
    /**
     * 根据学生ID查询知识掌握情况
     */
    List<KnowledgeMastery> findByStudentId(Long studentId);
    
    /**
     * 根据课程ID查询知识掌握情况
     */
    List<KnowledgeMastery> findByCourseId(Long courseId);
    
    /**
     * 根据知识点查询掌握情况
     */
    List<KnowledgeMastery> findByKnowledgePoint(String knowledgePoint);
    
    /**
     * 根据掌握级别查询
     */
    List<KnowledgeMastery> findByMasteryLevel(KnowledgeMastery.MasteryLevel masteryLevel);
    
    /**
     * 根据学生ID和课程ID查询知识掌握情况
     */
    List<KnowledgeMastery> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    /**
     * 根据学生ID和知识点查询掌握情况（唯一）
     */
    Optional<KnowledgeMastery> findByStudentIdAndKnowledgePoint(Long studentId, String knowledgePoint);
    
    /**
     * 统计指定掌握级别的数量
     */
    Long countByMasteryLevel(KnowledgeMastery.MasteryLevel masteryLevel);
    
    /**
     * 统计某个学生的知识掌握情况
     */
    @Query("SELECT m.masteryLevel, COUNT(m) FROM KnowledgeMastery m WHERE m.studentId = :studentId GROUP BY m.masteryLevel")
    List<Object[]> countMasteryLevelsByStudent(@Param("studentId") Long studentId);
    
    /**
     * 统计某个课程的知识掌握情况
     */
    @Query("SELECT m.masteryLevel, COUNT(m) FROM KnowledgeMastery m WHERE m.courseId = :courseId GROUP BY m.masteryLevel")
    List<Object[]> countMasteryLevelsByCourse(@Param("courseId") Long courseId);
    
    /**
     * 查询某个学生掌握程度高的知识点
     */
    @Query("SELECT m FROM KnowledgeMastery m WHERE m.studentId = :studentId " +
           "AND m.masteryLevel IN ('MASTERED', 'EXPERT') ORDER BY m.updateTime DESC")
    List<KnowledgeMastery> findMasteredByStudent(@Param("studentId") Long studentId);
    
    /**
     * 查询某个学生需要加强的知识点
     */
    @Query("SELECT m FROM KnowledgeMastery m WHERE m.studentId = :studentId " +
           "AND m.masteryLevel IN ('NOT_LEARNED', 'LEARNING') ORDER BY m.updateTime DESC")
    List<KnowledgeMastery> findWeakByStudent(@Param("studentId") Long studentId);
    
    /**
     * 查询最近更新的知识掌握记录
     */
    List<KnowledgeMastery> findByUpdateTimeAfterOrderByUpdateTimeDesc(LocalDateTime since);
    
    /**
     * 计算某个学生的总体掌握率
     */
    @Query("SELECT (COUNT(CASE WHEN m.masteryLevel IN ('MASTERED', 'EXPERT') THEN 1 END) * 100.0 / COUNT(*)) " +
           "FROM KnowledgeMastery m WHERE m.studentId = :studentId")
    Double calculateMasteryRateByStudent(@Param("studentId") Long studentId);
    
    /**
     * 计算某个课程的总体掌握率
     */
    @Query("SELECT (COUNT(CASE WHEN m.masteryLevel IN ('MASTERED', 'EXPERT') THEN 1 END) * 100.0 / COUNT(*)) " +
           "FROM KnowledgeMastery m WHERE m.courseId = :courseId")
    Double calculateMasteryRateByCourse(@Param("courseId") Long courseId);
    
    /**
     * 查询需要复习的知识点（掌握级别较低且很久没更新）
     */
    @Query("SELECT m FROM KnowledgeMastery m WHERE m.studentId = :studentId " +
           "AND m.masteryLevel IN ('NOT_LEARNED', 'LEARNING') " +
           "AND m.updateTime < :threshold ORDER BY m.updateTime ASC")
    List<KnowledgeMastery> findForReview(@Param("studentId") Long studentId, 
                                         @Param("threshold") LocalDateTime threshold);
} 