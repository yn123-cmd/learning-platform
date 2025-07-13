package org.example.learn.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 学情分析概览数据传输对象
 */
@Data
public class AnalyticsOverviewDTO {
    
    // 总体统计
    private Integer totalStudents; // 总学生数
    private Integer totalCourses; // 总课程数
    private Integer totalAssignments; // 总作业数
    
    // 核心指标
    private Double knowledgeMasteryRate; // 知识掌握率 (%)
    private Double assignmentCompletionRate; // 作业完成率 (%)
    private Double classParticipationRate; // 课堂参与度 (%)
    private Double averageScore; // 平均成绩
    
    // 学习时长统计
    private Long totalLearningMinutes; // 总学习时长（分钟）
    private Double averageDailyLearningMinutes; // 日均学习时长（分钟）
    
    // 今日数据
    private TodayDataDTO todayData;
    
    // 趋势数据
    private List<TrendDataDTO> trendData;
    
    // 课程统计
    private List<CourseStatDTO> courseStats;
    
    // 班级排名
    private List<ClassRankDTO> classRanking;
    
    @Data
    public static class TodayDataDTO {
        private Integer todayAttendance; // 今日出勤人数
        private Integer todayAssignmentSubmissions; // 今日作业提交数
        private Long todayLearningMinutes; // 今日学习时长
        private Integer activeLearners; // 今日活跃学习者
    }
    
    @Data
    public static class TrendDataDTO {
        private LocalDate date;
        private Double masteryRate;
        private Double completionRate;
        private Double participationRate;
        private Long learningMinutes;
    }
    
    @Data
    public static class CourseStatDTO {
        private Long courseId;
        private String courseName;
        private String courseCode;
        private Integer studentCount;
        private Double avgScore;
        private Double completionRate;
        private String difficulty;
    }
    
    @Data
    public static class ClassRankDTO {
        private Long classId;
        private String className;
        private Double avgScore;
        private Double masteryRate;
        private Integer rank;
        private String trend; // "up", "down", "stable"
    }
} 