package org.example.learn.repository;

import org.example.learn.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程数据访问接口
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    /**
     * 根据教师ID查询课程
     */
    List<Course> findByTeacherId(Long teacherId);
    
    /**
     * 根据状态查询课程
     */
    List<Course> findByStatus(String status);
    
    /**
     * 根据课程代码查询课程
     */
    Course findByCourseCode(String courseCode);
    
    /**
     * 查询免费课程
     */
    List<Course> findByIsFreeTrue();
    
    /**
     * 根据难度级别查询课程
     */
    List<Course> findByDifficultyLevel(String difficultyLevel);
    
    /**
     * 查询热门课程（按学生数量排序）
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' ORDER BY c.studentCount DESC")
    List<Course> findPopularCourses();
    
    /**
     * 查询评分最高的课程
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' AND c.ratingCount > 0 ORDER BY c.rating DESC")
    List<Course> findTopRatedCourses();
    
    /**
     * 根据分类查询课程
     */
    List<Course> findByCategoryId(Long categoryId);
    
    /**
     * 搜索课程（按标题）
     */
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' AND c.title LIKE %:keyword%")
    List<Course> searchByTitle(@Param("keyword") String keyword);
} 