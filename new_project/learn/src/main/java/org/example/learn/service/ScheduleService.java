package org.example.learn.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learn.dto.TodayScheduleDTO;
import org.example.learn.entity.CourseSchedule;
import org.example.learn.repository.CourseScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程安排服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    
    private final CourseScheduleRepository courseScheduleRepository;
    
    /**
     * 获取今日课程安排
     */
    public List<TodayScheduleDTO> getTodaySchedules() {
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();
        
        log.info("查询今日课程安排: {} ({} 到 {})", today, dayStart, dayEnd);
        
        List<CourseSchedule> schedules = courseScheduleRepository.findByScheduleDate(dayStart, dayEnd);
        log.info("找到{}条今日课程安排", schedules.size());
        
        return schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取指定日期的课程安排
     */
    public List<TodayScheduleDTO> getSchedulesByDate(LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        
        log.info("查询指定日期课程安排: {} ({} 到 {})", date, dayStart, dayEnd);
        
        List<CourseSchedule> schedules = courseScheduleRepository.findByScheduleDate(dayStart, dayEnd);
        log.info("找到{}条{}的课程安排", schedules.size(), date);
        
        return schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取教师的课程安排
     */
    public List<TodayScheduleDTO> getTeacherSchedules(Long teacherId, LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        
        log.info("查询教师{}在{}的课程安排", teacherId, date);
        
        List<CourseSchedule> schedules = courseScheduleRepository
                .findByTeacherIdAndScheduleDate(teacherId, dayStart, dayEnd);
        log.info("找到{}条教师课程安排", schedules.size());
        
        return schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取班级的课程安排
     */
    public List<TodayScheduleDTO> getClassSchedules(Long classId, LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        
        log.info("查询班级{}在{}的课程安排", classId, date);
        
        List<CourseSchedule> schedules = courseScheduleRepository
                .findByClassIdAndScheduleDate(classId, dayStart, dayEnd);
        log.info("找到{}条班级课程安排", schedules.size());
        
        return schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取本周课程安排
     */
    public List<TodayScheduleDTO> getWeekSchedules() {
        LocalDate startOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        LocalDateTime weekStart = startOfWeek.atStartOfDay();
        LocalDateTime weekEnd = endOfWeek.plusDays(1).atStartOfDay();
        
        log.info("查询本周课程安排: {} 到 {} ({} 到 {})", startOfWeek, endOfWeek, weekStart, weekEnd);
        
        List<CourseSchedule> schedules = courseScheduleRepository
                .findByScheduleDateBetween(weekStart, weekEnd);
        log.info("找到{}条本周课程安排", schedules.size());
        
        return schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 更新课程状态
     */
    public boolean updateScheduleStatus(Long scheduleId, CourseSchedule.ScheduleStatus status) {
        log.info("更新课程安排{}状态为: {}", scheduleId, status);
        
        return courseScheduleRepository.findById(scheduleId)
                .map(schedule -> {
                    schedule.setStatus(status);
                    courseScheduleRepository.save(schedule);
                    log.info("课程安排状态更新成功");
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * 获取正在进行的课程
     */
    public List<TodayScheduleDTO> getOngoingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        
        log.info("查询正在进行的课程: {}", now);
        
        List<CourseSchedule> schedules = courseScheduleRepository
                .findOngoingSchedulesAtTime(now);
        log.info("找到{}条正在进行的课程", schedules.size());
        
        return schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取即将开始的课程（30分钟内）
     */
    public List<TodayScheduleDTO> getUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soon = now.plusMinutes(30);
        
        log.info("查询即将开始的课程: {} 到 {}", now, soon);
        
        List<CourseSchedule> schedules = courseScheduleRepository
                .findByScheduleDateAndStartTimeBetween(now, soon);
        log.info("找到{}条即将开始的课程", schedules.size());
        
        return schedules.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换实体为DTO
     */
    private TodayScheduleDTO convertToDTO(CourseSchedule schedule) {
        return TodayScheduleDTO.builder()
                .id(schedule.getId())
                .courseId(schedule.getCourse() != null ? schedule.getCourse().getId() : null)
                .courseName(schedule.getCourse() != null ? schedule.getCourse().getTitle() : "")
                .courseCode(schedule.getCourse() != null ? schedule.getCourse().getCourseCode() : "")
                .classId(schedule.getClassEntity() != null ? schedule.getClassEntity().getId() : null)
                .className(schedule.getClassEntity() != null ? schedule.getClassEntity().getClassName() : "")
                .teacherId(schedule.getTeacher() != null ? schedule.getTeacher().getId() : null)
                .teacherName(schedule.getTeacher() != null ? schedule.getTeacher().getNickName() : "")
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .classroom(schedule.getClassroom())
                .lessonTopic(schedule.getLessonTopic())
                .lessonContent(schedule.getLessonContent())
                .status(schedule.getStatus() != null ? schedule.getStatus().name() : "SCHEDULED")
                .attendanceCount(schedule.getAttendanceCount())
                .title(schedule.getCourse() != null ? schedule.getCourse().getTitle() : "课程")
                .description(schedule.getLessonTopic())
                .scheduleDate(schedule.getStartTime() != null ? schedule.getStartTime().toLocalDate() : null)
                .type("COURSE")
                .location(schedule.getClassroom())
                .build();
    }
} 