package org.example.learn.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learn.dto.AnalyticsOverviewDTO;
import org.example.learn.entity.KnowledgeMastery;
import org.example.learn.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 学情分析服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AnalyticsService {
    
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final KnowledgeMasteryRepository knowledgeMasteryRepository;
    private final LearningBehaviorRepository learningBehaviorRepository;
    
    /**
     * 获取学情分析概览
     */
    @Transactional(readOnly = true)
    public AnalyticsOverviewDTO getAnalyticsOverview() {
        AnalyticsOverviewDTO overview = new AnalyticsOverviewDTO();
        
        // 基础统计
        overview.setTotalStudents(getTotalStudentsCount());
        overview.setTotalCourses(getTotalCoursesCount());
        overview.setTotalAssignments(getTotalAssignmentsCount());
        
        // 核心指标
        overview.setKnowledgeMasteryRate(calculateKnowledgeMasteryRate());
        overview.setAssignmentCompletionRate(calculateAssignmentCompletionRate());
        overview.setClassParticipationRate(calculateClassParticipationRate());
        overview.setAverageScore(calculateAverageScore());
        
        // 学习时长统计
        overview.setTotalLearningMinutes(getTotalLearningMinutes());
        overview.setAverageDailyLearningMinutes(getAverageDailyLearningMinutes());
        
        // 今日数据
        overview.setTodayData(getTodayData());
        
        // 趋势数据（最近7天）
        overview.setTrendData(getTrendData(7));
        
        // 课程统计
        overview.setCourseStats(getCourseStats());
        
        // 班级排名
        overview.setClassRanking(getClassRanking());
        
        return overview;
    }
    
    /**
     * 计算知识掌握率
     */
    private Double calculateKnowledgeMasteryRate() {
        try {
            long totalKnowledgePoints = knowledgeMasteryRepository.count();
            if (totalKnowledgePoints == 0) return 0.0;
            
            long masteredPoints = knowledgeMasteryRepository.countByMasteryLevel(
                KnowledgeMastery.MasteryLevel.MASTERED);
            
            return (double) masteredPoints / totalKnowledgePoints * 100;
        } catch (Exception e) {
            log.error("计算知识掌握率失败", e);
            return 0.0;
        }
    }
    
    /**
     * 计算作业完成率
     */
    private Double calculateAssignmentCompletionRate() {
        try {
            long totalAssignments = assignmentRepository.count();
            if (totalAssignments == 0) return 0.0;
            
            long submittedAssignments = submissionRepository.countByStatus(
                org.example.learn.entity.AssignmentSubmission.SubmissionStatus.SUBMITTED);
            
            return (double) submittedAssignments / totalAssignments * 100;
        } catch (Exception e) {
            log.error("计算作业完成率失败", e);
            return 0.0;
        }
    }
    
    /**
     * 计算课堂参与度
     */
    private Double calculateClassParticipationRate() {
        try {
            // 基于学习行为数据计算参与度
            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            long totalBehaviors = learningBehaviorRepository.countByCreateTimeAfter(weekAgo);
            long totalStudents = getTotalStudentsCount();
            
            if (totalStudents == 0) return 0.0;
            
            // 简单的参与度计算：平均每个学生的行为数量
            double avgBehaviorsPerStudent = (double) totalBehaviors / totalStudents;
            
            // 将参与度转换为百分比（假设5个行为为100%参与度）
            return Math.min(100.0, avgBehaviorsPerStudent / 5 * 100);
        } catch (Exception e) {
            log.error("计算课堂参与度失败", e);
            return 0.0;
        }
    }
    
    /**
     * 计算平均成绩
     */
    private Double calculateAverageScore() {
        try {
            return submissionRepository.findAverageScore();
        } catch (Exception e) {
            log.error("计算平均成绩失败", e);
            return 0.0;
        }
    }
    
    /**
     * 获取总学生数
     */
    private Integer getTotalStudentsCount() {
        try {
            return Math.toIntExact(userRepository.countByRole("USER"));
        } catch (Exception e) {
            log.error("获取学生总数失败", e);
            return 0;
        }
    }
    
    /**
     * 获取总课程数
     */
    private Integer getTotalCoursesCount() {
        try {
            return Math.toIntExact(courseRepository.count());
        } catch (Exception e) {
            log.error("获取课程总数失败", e);
            return 0;
        }
    }
    
    /**
     * 获取总作业数
     */
    private Integer getTotalAssignmentsCount() {
        try {
            return Math.toIntExact(assignmentRepository.count());
        } catch (Exception e) {
            log.error("获取作业总数失败", e);
            return 0;
        }
    }
    
    /**
     * 获取总学习时长
     */
    private Long getTotalLearningMinutes() {
        try {
            return learningBehaviorRepository.sumAllDuration();
        } catch (Exception e) {
            log.error("获取总学习时长失败", e);
            return 0L;
        }
    }
    
    /**
     * 获取日均学习时长
     */
    private Double getAverageDailyLearningMinutes() {
        try {
            Long totalMinutes = getTotalLearningMinutes();
            Integer totalStudents = getTotalStudentsCount();
            
            if (totalStudents == 0) return 0.0;
            
            // 假设统计最近30天的数据
            return totalMinutes.doubleValue() / totalStudents / 30;
        } catch (Exception e) {
            log.error("计算日均学习时长失败", e);
            return 0.0;
        }
    }
    
    /**
     * 获取今日数据
     */
    private AnalyticsOverviewDTO.TodayDataDTO getTodayData() {
        AnalyticsOverviewDTO.TodayDataDTO todayData = new AnalyticsOverviewDTO.TodayDataDTO();
        
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
            
            // 今日出勤人数（基于今日学习行为）
            todayData.setTodayAttendance(
                Math.toIntExact(learningBehaviorRepository.countDistinctStudentsByCreateTimeBetween(
                    startOfDay, endOfDay)));
            
            // 今日作业提交数
            todayData.setTodayAssignmentSubmissions(
                Math.toIntExact(submissionRepository.countBySubmittedAtBetween(
                    startOfDay, endOfDay)));
            
            // 今日学习时长
            todayData.setTodayLearningMinutes(
                learningBehaviorRepository.sumDurationByCreateTimeBetween(startOfDay, endOfDay));
            
            // 今日活跃学习者
            todayData.setActiveLearners(todayData.getTodayAttendance());
            
        } catch (Exception e) {
            log.error("获取今日数据失败", e);
            // 设置默认值
            todayData.setTodayAttendance(0);
            todayData.setTodayAssignmentSubmissions(0);
            todayData.setTodayLearningMinutes(0L);
            todayData.setActiveLearners(0);
        }
        
        return todayData;
    }
    
    /**
     * 获取趋势数据
     */
    private List<AnalyticsOverviewDTO.TrendDataDTO> getTrendData(int days) {
        List<AnalyticsOverviewDTO.TrendDataDTO> trendData = new ArrayList<>();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            
            AnalyticsOverviewDTO.TrendDataDTO trend = new AnalyticsOverviewDTO.TrendDataDTO();
            trend.setDate(date);
            
            try {
                // 计算当日的各项指标
                trend.setMasteryRate(calculateDailyMasteryRate(date));
                trend.setCompletionRate(calculateDailyCompletionRate(date));
                trend.setParticipationRate(calculateDailyParticipationRate(date));
                trend.setLearningMinutes(calculateDailyLearningMinutes(date));
            } catch (Exception e) {
                log.error("计算{}的趋势数据失败", date, e);
                // 设置默认值
                trend.setMasteryRate(0.0);
                trend.setCompletionRate(0.0);
                trend.setParticipationRate(0.0);
                trend.setLearningMinutes(0L);
            }
            
            trendData.add(trend);
        }
        
        return trendData;
    }
    
    /**
     * 获取课程统计数据
     */
    private List<AnalyticsOverviewDTO.CourseStatDTO> getCourseStats() {
        List<AnalyticsOverviewDTO.CourseStatDTO> courseStats = new ArrayList<>();
        
        try {
            // 这里应该从数据库查询课程统计数据
            // 简化实现，返回空列表
            log.info("获取课程统计数据");
        } catch (Exception e) {
            log.error("获取课程统计数据失败", e);
        }
        
        return courseStats;
    }
    
    /**
     * 获取班级排名
     */
    private List<AnalyticsOverviewDTO.ClassRankDTO> getClassRanking() {
        List<AnalyticsOverviewDTO.ClassRankDTO> classRanking = new ArrayList<>();
        
        try {
            // 这里应该从数据库查询班级排名数据
            // 简化实现，返回空列表
            log.info("获取班级排名数据");
        } catch (Exception e) {
            log.error("获取班级排名数据失败", e);
        }
        
        return classRanking;
    }
    
    // 辅助方法
    private Double calculateDailyMasteryRate(LocalDate date) {
        // 计算指定日期的知识掌握率
        return 0.0; // 简化实现
    }
    
    private Double calculateDailyCompletionRate(LocalDate date) {
        // 计算指定日期的作业完成率
        return 0.0; // 简化实现
    }
    
    private Double calculateDailyParticipationRate(LocalDate date) {
        // 计算指定日期的参与度
        return 0.0; // 简化实现
    }
    
    private Long calculateDailyLearningMinutes(LocalDate date) {
        // 计算指定日期的学习时长
        return 0L; // 简化实现
    }
} 