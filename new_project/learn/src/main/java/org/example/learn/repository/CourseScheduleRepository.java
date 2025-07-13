package org.example.learn.repository;

import org.example.learn.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 课程安排数据访问接口
 */
@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {
    
    /**
     * 查询指定时间范围内的课程安排，按开始时间排序
     */
    List<CourseSchedule> findByStartTimeBetweenOrderByStartTime(
        LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询指定日期的课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE " +
           "cs.startTime >= :dayStart AND cs.startTime < :dayEnd " +
           "ORDER BY cs.startTime")
    List<CourseSchedule> findByScheduleDate(@Param("dayStart") LocalDateTime dayStart, 
                                           @Param("dayEnd") LocalDateTime dayEnd);
    
    /**
     * 查询指定日期范围的课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE " +
           "cs.startTime >= :startDateTime AND cs.startTime < :endDateTime " +
           "ORDER BY cs.startTime")
    List<CourseSchedule> findByScheduleDateBetween(@Param("startDateTime") LocalDateTime startDateTime, 
                                                   @Param("endDateTime") LocalDateTime endDateTime);
    
    /**
     * 查询教师在指定日期的课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE " +
           "cs.teacherId = :teacherId AND " +
           "cs.startTime >= :dayStart AND cs.startTime < :dayEnd " +
           "ORDER BY cs.startTime")
    List<CourseSchedule> findByTeacherIdAndScheduleDate(@Param("teacherId") Long teacherId, 
                                                       @Param("dayStart") LocalDateTime dayStart,
                                                       @Param("dayEnd") LocalDateTime dayEnd);
    
    /**
     * 查询班级在指定日期的课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE " +
           "cs.classId = :classId AND " +
           "cs.startTime >= :dayStart AND cs.startTime < :dayEnd " +
           "ORDER BY cs.startTime")
    List<CourseSchedule> findByClassIdAndScheduleDate(@Param("classId") Long classId, 
                                                     @Param("dayStart") LocalDateTime dayStart,
                                                     @Param("dayEnd") LocalDateTime dayEnd);
    
    /**
     * 查询指定时间正在进行的课程
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE " +
           ":currentTime BETWEEN cs.startTime AND cs.endTime " +
           "ORDER BY cs.startTime")
    List<CourseSchedule> findOngoingSchedulesAtTime(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 查询指定日期和时间范围的课程
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE " +
           "cs.startTime >= :startDateTime AND cs.startTime <= :endDateTime " +
           "ORDER BY cs.startTime")
    List<CourseSchedule> findByScheduleDateAndStartTimeBetween(@Param("startDateTime") LocalDateTime startDateTime, 
                                                              @Param("endDateTime") LocalDateTime endDateTime);
    
    /**
     * 查询教师在指定时间范围内的课程安排
     */
    List<CourseSchedule> findByTeacherIdAndStartTimeBetweenOrderByStartTime(
        Long teacherId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询班级在指定时间范围内的课程安排
     */
    List<CourseSchedule> findByClassIdAndStartTimeBetweenOrderByStartTime(
        Long classId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询课程在指定时间范围内的安排
     */
    List<CourseSchedule> findByCourseIdAndStartTimeBetweenOrderByStartTime(
        Long courseId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询正在进行的课程（当前时间在开始和结束时间之间）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE :now BETWEEN cs.startTime AND cs.endTime " +
           "ORDER BY cs.startTime")
    List<CourseSchedule> findOngoingSchedules(@Param("now") LocalDateTime now);
    
    /**
     * 查询即将开始的课程（在指定时间范围内开始）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.startTime BETWEEN :now AND :soon " +
           "ORDER BY cs.startTime")
    List<CourseSchedule> findUpcomingSchedules(@Param("now") LocalDateTime now, 
                                               @Param("soon") LocalDateTime soon);
    
    /**
     * 查询教师今日课程数量
     */
    @Query("SELECT COUNT(cs) FROM CourseSchedule cs WHERE " +
           "cs.teacherId = :teacherId AND " +
           "cs.startTime >= :dayStart AND cs.startTime < :dayEnd")
    Long countTodaySchedulesByTeacher(@Param("teacherId") Long teacherId, 
                                      @Param("dayStart") LocalDateTime dayStart,
                                      @Param("dayEnd") LocalDateTime dayEnd);
    
    /**
     * 查询班级今日课程数量
     */
    @Query("SELECT COUNT(cs) FROM CourseSchedule cs WHERE " +
           "cs.classId = :classId AND " +
           "cs.startTime >= :dayStart AND cs.startTime < :dayEnd")
    Long countTodaySchedulesByClass(@Param("classId") Long classId, 
                                    @Param("dayStart") LocalDateTime dayStart,
                                    @Param("dayEnd") LocalDateTime dayEnd);
    
    /**
     * 查询指定状态的课程安排
     */
    List<CourseSchedule> findByStatusOrderByStartTime(CourseSchedule.ScheduleStatus status);
    
    /**
     * 查询教师指定状态的课程安排
     */
    List<CourseSchedule> findByTeacherIdAndStatusOrderByStartTime(
        Long teacherId, CourseSchedule.ScheduleStatus status);
    
    /**
     * 查询本周统计数据
     */
    @Query("SELECT " +
           "COUNT(cs) as totalSchedules, " +
           "COUNT(CASE WHEN cs.status = 'COMPLETED' THEN 1 END) as completedSchedules, " +
           "AVG(cs.attendanceCount) as avgAttendance " +
           "FROM CourseSchedule cs " +
           "WHERE cs.startTime BETWEEN :weekStart AND :weekEnd")
    Object[] findWeeklyStats(@Param("weekStart") LocalDateTime weekStart, 
                           @Param("weekEnd") LocalDateTime weekEnd);
} 