package org.example.learn.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

/**
 * 今日课程安排数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayScheduleDTO {
    
    private Long id;
    
    private Long courseId;
    private String courseName;
    private String courseCode;
    
    private Long classId;
    private String className;
    
    private Long teacherId;
    private String teacherName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private String classroom;
    private String lessonTopic;
    private String lessonContent;
    private String status;
    private Integer attendanceCount;
    
    // 计算属性
    private String timeRange; // 时间段，如 "08:00-09:40"
    private String statusText; // 状态文本
    private Boolean isUpcoming; // 是否即将开始
    private Boolean isOngoing; // 是否正在进行
    private Long minutesToStart; // 距离开始的分钟数
    
    private String title;
    private String description;
    private LocalTime scheduleTime;
    private LocalDate scheduleDate;
    private String type;
    private String location;
    
    public String getTimeRange() {
        if (startTime != null && endTime != null) {
            return String.format("%02d:%02d-%02d:%02d", 
                startTime.getHour(), startTime.getMinute(),
                endTime.getHour(), endTime.getMinute());
        }
        return "";
    }
    
    public String getStatusText() {
        if (status == null) return "未知";
        return switch (status.toUpperCase()) {
            case "SCHEDULED" -> "已安排";
            case "PREPARING" -> "准备中";
            case "ONGOING" -> "进行中";
            case "COMPLETED" -> "已完成";
            case "CANCELLED" -> "已取消";
            default -> status;
        };
    }
    
    public Boolean getIsUpcoming() {
        if (startTime == null) return false;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime soon = now.plusMinutes(30); // 30分钟内即将开始
        return startTime.isAfter(now) && startTime.isBefore(soon);
    }
    
    public Boolean getIsOngoing() {
        if (startTime == null || endTime == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    public Long getMinutesToStart() {
        if (startTime == null) return null;
        LocalDateTime now = LocalDateTime.now();
        if (startTime.isBefore(now)) return 0L;
        
        return java.time.Duration.between(now, startTime).toMinutes();
    }
} 