package org.example.learn.repository;

import org.example.learn.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待办事项数据访问接口
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    
    /**
     * 根据状态查询待办事项
     */
    List<Todo> findByStatus(Todo.TodoStatus status);
    
    /**
     * 根据优先级查询待办事项
     */
    List<Todo> findByPriority(Todo.Priority priority);
    
    /**
     * 根据分类查询待办事项
     */
    List<Todo> findByCategory(String category);
    
    /**
     * 根据用户ID查询待办事项
     */
    List<Todo> findByUserId(Long userId);
    
    /**
     * 按创建时间倒序查询所有待办事项
     */
    List<Todo> findByOrderByCreateTimeDesc();
    
    /**
     * 按优先级和创建时间排序查询待办事项
     */
    @Query("SELECT t FROM Todo t ORDER BY " +
           "CASE t.priority " +
           "  WHEN 'URGENT' THEN 1 " +
           "  WHEN 'IMPORTANT' THEN 2 " +
           "  WHEN 'NORMAL' THEN 3 " +
           "  WHEN 'LOW' THEN 4 " +
           "END, t.createTime DESC")
    List<Todo> findByOrderByPriorityAndCreateTime();
    
    /**
     * 查询即将到期的待办事项
     */
    @Query("SELECT t FROM Todo t WHERE t.dueDate BETWEEN :startTime AND :endTime " +
           "AND t.status IN ('PENDING', 'IN_PROGRESS')")
    List<Todo> findDueSoon(@Param("startTime") LocalDateTime startTime, 
                           @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询已过期的待办事项
     */
    @Query("SELECT t FROM Todo t WHERE t.dueDate < :now " +
           "AND t.status IN ('PENDING', 'IN_PROGRESS')")
    List<Todo> findOverdue(@Param("now") LocalDateTime now);
    
    /**
     * 统计某状态的待办事项数量
     */
    Long countByStatus(Todo.TodoStatus status);
    
    /**
     * 统计某用户的待办事项数量
     */
    Long countByUserId(Long userId);
    
    /**
     * 查询今日的待办事项
     */
    @Query("SELECT t FROM Todo t WHERE DATE(t.dueDate) = CURRENT_DATE " +
           "AND t.status IN ('PENDING', 'IN_PROGRESS')")
    List<Todo> findTodayTodos();
    
    /**
     * 查询本周的待办事项
     */
    @Query("SELECT t FROM Todo t WHERE t.dueDate BETWEEN :startOfWeek AND :endOfWeek " +
           "AND t.status IN ('PENDING', 'IN_PROGRESS')")
    List<Todo> findWeekTodos(@Param("startOfWeek") LocalDateTime startOfWeek,
                             @Param("endOfWeek") LocalDateTime endOfWeek);
    
    /**
     * 查询最近完成的待办事项
     */
    @Query("SELECT t FROM Todo t WHERE t.status = 'COMPLETED' " +
           "ORDER BY t.completedAt DESC")
    List<Todo> findRecentCompleted();
} 