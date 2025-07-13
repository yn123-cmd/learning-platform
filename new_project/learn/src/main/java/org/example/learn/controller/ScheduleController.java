package org.example.learn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learn.common.Result;
import org.example.learn.dto.TodayScheduleDTO;
import org.example.learn.entity.CourseSchedule;
import org.example.learn.service.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程安排控制器
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "课程安排管理", description = "今日教学日程相关接口")
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    @Operation(summary = "获取今日课程安排", description = "获取当前日期的所有课程安排")
    @GetMapping("/today")
    public Result<List<TodayScheduleDTO>> getTodaySchedules() {
        try {
            List<TodayScheduleDTO> schedules = scheduleService.getTodaySchedules();
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取今日课程安排失败", e);
            return Result.error("获取今日课程安排失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取指定日期的课程安排", description = "获取指定日期的课程安排")
    @GetMapping("/date/{date}")
    public Result<List<TodayScheduleDTO>> getSchedulesByDate(
            @Parameter(description = "日期，格式：yyyy-MM-dd") 
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            List<TodayScheduleDTO> schedules = scheduleService.getSchedulesByDate(date);
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取{}课程安排失败", date, e);
            return Result.error("获取课程安排失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取本周课程安排", description = "获取当前周的所有课程安排")
    @GetMapping("/week")
    public Result<List<TodayScheduleDTO>> getWeekSchedules() {
        try {
            List<TodayScheduleDTO> schedules = scheduleService.getWeekSchedules();
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取本周课程安排失败", e);
            return Result.error("获取本周课程安排失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取教师的课程安排", description = "获取指定教师在指定日期的课程安排")
    @GetMapping("/teacher/{teacherId}")
    public Result<List<TodayScheduleDTO>> getTeacherSchedules(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "日期，格式：yyyy-MM-dd，不传则为今日") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            LocalDate queryDate = date != null ? date : LocalDate.now();
            List<TodayScheduleDTO> schedules = scheduleService.getTeacherSchedules(teacherId, queryDate);
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取教师{}课程安排失败", teacherId, e);
            return Result.error("获取教师课程安排失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取班级的课程安排", description = "获取指定班级在指定日期的课程安排")
    @GetMapping("/class/{classId}")
    public Result<List<TodayScheduleDTO>> getClassSchedules(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "日期，格式：yyyy-MM-dd，不传则为今日") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            LocalDate queryDate = date != null ? date : LocalDate.now();
            List<TodayScheduleDTO> schedules = scheduleService.getClassSchedules(classId, queryDate);
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取班级{}课程安排失败", classId, e);
            return Result.error("获取班级课程安排失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取正在进行的课程", description = "获取当前时间正在进行的课程")
    @GetMapping("/ongoing")
    public Result<List<TodayScheduleDTO>> getOngoingSchedules() {
        try {
            List<TodayScheduleDTO> schedules = scheduleService.getOngoingSchedules();
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取正在进行的课程失败", e);
            return Result.error("获取正在进行的课程失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取即将开始的课程", description = "获取30分钟内即将开始的课程")
    @GetMapping("/upcoming")
    public Result<List<TodayScheduleDTO>> getUpcomingSchedules() {
        try {
            List<TodayScheduleDTO> schedules = scheduleService.getUpcomingSchedules();
            return Result.success(schedules);
        } catch (Exception e) {
            log.error("获取即将开始的课程失败", e);
            return Result.error("获取即将开始的课程失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "更新课程状态", description = "更新指定课程安排的状态")
    @PutMapping("/{scheduleId}/status")
    public Result<Boolean> updateScheduleStatus(
            @Parameter(description = "课程安排ID") @PathVariable Long scheduleId,
            @Parameter(description = "新状态") @RequestParam String status) {
        try {
            CourseSchedule.ScheduleStatus scheduleStatus = 
                CourseSchedule.ScheduleStatus.valueOf(status.toUpperCase());
            
            boolean success = scheduleService.updateScheduleStatus(scheduleId, scheduleStatus);
            
            if (success) {
                return Result.success("课程状态更新成功", true);
            } else {
                return Result.error("课程状态更新失败");
            }
        } catch (IllegalArgumentException e) {
            log.error("无效的课程状态: {}", status, e);
            return Result.error("无效的课程状态: " + status);
        } catch (Exception e) {
            log.error("更新课程状态失败: scheduleId={}, status={}", scheduleId, status, e);
            return Result.error("更新课程状态失败: " + e.getMessage());
        }
    }
} 