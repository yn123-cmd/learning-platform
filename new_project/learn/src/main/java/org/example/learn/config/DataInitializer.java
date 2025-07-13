package org.example.learn.config;

import org.example.learn.entity.User;
import org.example.learn.entity.Course;
import org.example.learn.entity.CourseSchedule;
import org.example.learn.repository.UserRepository;
import org.example.learn.repository.CourseRepository;
import org.example.learn.repository.CourseScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 数据初始化器 - 用于添加测试数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseScheduleRepository courseScheduleRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("开始插入测试数据...");
        
        // 插入教师用户
        insertTeachers();
        
        // 插入课程
        insertCourses();
        
        // 插入课程安排
        insertCourseSchedules();
        
        System.out.println("测试数据插入完成！");
    }
    
    private void insertTeachers() {
        // 检查是否已存在教师数据
        if (userRepository.count() > 0) {
            System.out.println("用户数据已存在，跳过用户数据插入");
            return;
        }
        
        // 创建教师用户
        User teacher1 = new User();
        teacher1.setUsername("teacher_zhang");
        teacher1.setEmail("zhang@school.edu");
        teacher1.setPassword(passwordEncoder.encode("123456"));
        teacher1.setNickName("张教授");
        teacher1.setRole("TEACHER");
        teacher1.setBio("高等数学教授，具有20年教学经验");
        teacher1.setPhone("13800138001");
        userRepository.save(teacher1);
        
        User teacher2 = new User();
        teacher2.setUsername("teacher_li");
        teacher2.setEmail("li@school.edu");
        teacher2.setPassword(passwordEncoder.encode("123456"));
        teacher2.setNickName("李老师");
        teacher2.setRole("TEACHER");
        teacher2.setBio("英语专业教师，专注于英语阅读和写作教学");
        teacher2.setPhone("13800138002");
        userRepository.save(teacher2);
        
        User teacher3 = new User();
        teacher3.setUsername("teacher_wang");
        teacher3.setEmail("wang@school.edu");
        teacher3.setPassword(passwordEncoder.encode("123456"));
        teacher3.setNickName("王博士");
        teacher3.setRole("TEACHER");
        teacher3.setBio("计算机科学博士，专业从事程序设计和算法教学");
        teacher3.setPhone("13800138003");
        userRepository.save(teacher3);
        
        User teacher4 = new User();
        teacher4.setUsername("teacher_liu");
        teacher4.setEmail("liu@school.edu");
        teacher4.setPassword(passwordEncoder.encode("123456"));
        teacher4.setNickName("刘教授");
        teacher4.setRole("TEACHER");
        teacher4.setBio("物理学教授，专注于基础物理和实验物理教学");
        teacher4.setPhone("13800138004");
        userRepository.save(teacher4);
        
        System.out.println("插入了4名教师用户");
    }
    
    private void insertCourses() {
        // 检查是否已存在课程数据
        if (courseRepository.count() > 0) {
            System.out.println("课程数据已存在，跳过课程数据插入");
            return;
        }
        
        System.out.println("开始插入课程数据...");
        
        // 获取教师ID
        Optional<User> teacher1 = userRepository.findByUsername("teacher_zhang");
        Optional<User> teacher2 = userRepository.findByUsername("teacher_li");
        Optional<User> teacher3 = userRepository.findByUsername("teacher_wang");
        Optional<User> teacher4 = userRepository.findByUsername("teacher_liu");
        
        System.out.println("查找教师结果：");
        System.out.println("teacher_zhang: " + (teacher1.isPresent() ? "找到 ID=" + teacher1.get().getId() : "未找到"));
        System.out.println("teacher_li: " + (teacher2.isPresent() ? "找到 ID=" + teacher2.get().getId() : "未找到"));
        System.out.println("teacher_wang: " + (teacher3.isPresent() ? "找到 ID=" + teacher3.get().getId() : "未找到"));
        System.out.println("teacher_liu: " + (teacher4.isPresent() ? "找到 ID=" + teacher4.get().getId() : "未找到"));
        
        if (teacher1.isEmpty() || teacher2.isEmpty() || teacher3.isEmpty() || teacher4.isEmpty()) {
            System.out.println("教师数据不存在，无法插入课程数据");
            
            // 输出所有用户数据用于调试
            List<User> allUsers = userRepository.findAll();
            System.out.println("数据库中的所有用户：");
            for (User user : allUsers) {
                System.out.println("ID=" + user.getId() + ", username=" + user.getUsername() + ", role=" + user.getRole());
            }
            return;
        }
        
        // 创建课程
        Course course1 = new Course();
        course1.setTitle("高等数学");
        course1.setCourseCode("MATH001");
        course1.setSubtitle("微积分基础与应用");
        course1.setDescription("本课程主要讲解微积分的基本概念、理论和方法，培养学生的数学思维能力和解决实际问题的能力。");
        course1.setTeacherId(teacher1.get().getId());
        course1.setSemester("2024-2025学年第二学期");
        course1.setCredits(4);
        course1.setDifficultyLevel("INTERMEDIATE");
        course1.setDurationHours(64);
        course1.setStatus("PUBLISHED");
        course1.setStudentCount(120);
        course1.setRating(BigDecimal.valueOf(4.5));
        course1.setRatingCount(45);
        courseRepository.save(course1);
        System.out.println("插入课程：" + course1.getTitle());
        
        Course course2 = new Course();
        course2.setTitle("英语阅读");
        course2.setCourseCode("ENG001");
        course2.setSubtitle("大学英语阅读理解与技巧");
        course2.setDescription("提高学生英语阅读理解能力，掌握各种阅读技巧和策略，扩大词汇量。");
        course2.setTeacherId(teacher2.get().getId());
        course2.setSemester("2024-2025学年第二学期");
        course2.setCredits(3);
        course2.setDifficultyLevel("BEGINNER");
        course2.setDurationHours(48);
        course2.setStatus("PUBLISHED");
        course2.setStudentCount(80);
        course2.setRating(BigDecimal.valueOf(4.3));
        course2.setRatingCount(32);
        courseRepository.save(course2);
        System.out.println("插入课程：" + course2.getTitle());
        
        Course course3 = new Course();
        course3.setTitle("程序设计基础");
        course3.setCourseCode("CS001");
        course3.setSubtitle("Java编程入门与实践");
        course3.setDescription("Java编程语言的基础语法、面向对象程序设计思想和编程实践。");
        course3.setTeacherId(teacher3.get().getId());
        course3.setSemester("2024-2025学年第二学期");
        course3.setCredits(4);
        course3.setDifficultyLevel("INTERMEDIATE");
        course3.setDurationHours(72);
        course3.setStatus("PUBLISHED");
        course3.setStudentCount(95);
        course3.setRating(BigDecimal.valueOf(4.7));
        course3.setRatingCount(58);
        courseRepository.save(course3);
        System.out.println("插入课程：" + course3.getTitle());
        
        Course course4 = new Course();
        course4.setTitle("大学物理");
        course4.setCourseCode("PHY001");
        course4.setSubtitle("力学与热学基础");
        course4.setDescription("经典力学、热力学基本定律和应用，培养学生的物理思维和实验能力。");
        course4.setTeacherId(teacher4.get().getId());
        course4.setSemester("2024-2025学年第二学期");
        course4.setCredits(4);
        course4.setDifficultyLevel("INTERMEDIATE");
        course4.setDurationHours(64);
        course4.setStatus("PUBLISHED");
        course4.setStudentCount(110);
        course4.setRating(BigDecimal.valueOf(4.2));
        course4.setRatingCount(41);
        courseRepository.save(course4);
        System.out.println("插入课程：" + course4.getTitle());
        
        System.out.println("成功插入了4门课程");
    }
    
    private void insertCourseSchedules() {
        // 检查是否已存在课程安排数据
        if (courseScheduleRepository.count() > 0) {
            System.out.println("课程安排数据已存在，跳过课程安排数据插入");
            return;
        }
        
        System.out.println("开始插入课程安排数据...");
        
        // 获取课程和教师数据
        List<Course> allCourses = courseRepository.findAll();
        System.out.println("数据库中的所有课程：");
        for (Course course : allCourses) {
            System.out.println("ID=" + course.getId() + ", courseCode=" + course.getCourseCode() + ", title=" + course.getTitle());
        }
        
        Course course1 = allCourses.stream().filter(c -> "MATH001".equals(c.getCourseCode())).findFirst().orElse(null);
        Course course2 = allCourses.stream().filter(c -> "ENG001".equals(c.getCourseCode())).findFirst().orElse(null);
        Course course3 = allCourses.stream().filter(c -> "CS001".equals(c.getCourseCode())).findFirst().orElse(null);
        Course course4 = allCourses.stream().filter(c -> "PHY001".equals(c.getCourseCode())).findFirst().orElse(null);
        
        System.out.println("查找课程结果：");
        System.out.println("MATH001: " + (course1 != null ? "找到 ID=" + course1.getId() : "未找到"));
        System.out.println("ENG001: " + (course2 != null ? "找到 ID=" + course2.getId() : "未找到"));
        System.out.println("CS001: " + (course3 != null ? "找到 ID=" + course3.getId() : "未找到"));
        System.out.println("PHY001: " + (course4 != null ? "找到 ID=" + course4.getId() : "未找到"));
        
        if (course1 == null || course2 == null || course3 == null || course4 == null) {
            System.out.println("课程数据不存在，无法插入课程安排数据");
            return;
        }
        
        // 今天的日期
        LocalDate today = LocalDate.now();
        System.out.println("今天的日期：" + today);
        
        // 今日课程安排
        // 高等数学 - 上午8:00-9:40
        CourseSchedule schedule1 = new CourseSchedule();
        schedule1.setCourseId(course1.getId());
        schedule1.setTeacherId(course1.getTeacherId());
        schedule1.setStartTime(today.atTime(8, 0));
        schedule1.setEndTime(today.atTime(9, 40));
        schedule1.setClassroom("A101");
        schedule1.setWeekDay(today.getDayOfWeek().getValue());
        schedule1.setLessonTopic("极限与连续");
        schedule1.setLessonContent("学习函数极限的概念和性质，掌握极限的计算方法，理解函数连续性的定义。");
        schedule1.setStatus(CourseSchedule.ScheduleStatus.SCHEDULED);
        courseScheduleRepository.save(schedule1);
        System.out.println("插入课程安排：" + course1.getTitle() + " " + schedule1.getStartTime());
        
        // 英语阅读 - 上午10:00-11:40
        CourseSchedule schedule2 = new CourseSchedule();
        schedule2.setCourseId(course2.getId());
        schedule2.setTeacherId(course2.getTeacherId());
        schedule2.setStartTime(today.atTime(10, 0));
        schedule2.setEndTime(today.atTime(11, 40));
        schedule2.setClassroom("B205");
        schedule2.setWeekDay(today.getDayOfWeek().getValue());
        schedule2.setLessonTopic("学术文章阅读技巧");
        schedule2.setLessonContent("学习如何快速理解学术文章的结构，掌握关键信息提取技巧。");
        schedule2.setStatus(CourseSchedule.ScheduleStatus.SCHEDULED);
        courseScheduleRepository.save(schedule2);
        System.out.println("插入课程安排：" + course2.getTitle() + " " + schedule2.getStartTime());
        
        // 程序设计基础 - 下午2:00-3:40
        CourseSchedule schedule3 = new CourseSchedule();
        schedule3.setCourseId(course3.getId());
        schedule3.setTeacherId(course3.getTeacherId());
        schedule3.setStartTime(today.atTime(14, 0));
        schedule3.setEndTime(today.atTime(15, 40));
        schedule3.setClassroom("C301");
        schedule3.setWeekDay(today.getDayOfWeek().getValue());
        schedule3.setLessonTopic("面向对象编程");
        schedule3.setLessonContent("学习Java中类和对象的概念，掌握封装、继承和多态的基本原理。");
        schedule3.setStatus(CourseSchedule.ScheduleStatus.SCHEDULED);
        courseScheduleRepository.save(schedule3);
        System.out.println("插入课程安排：" + course3.getTitle() + " " + schedule3.getStartTime());
        
        // 大学物理 - 下午4:00-5:40
        CourseSchedule schedule4 = new CourseSchedule();
        schedule4.setCourseId(course4.getId());
        schedule4.setTeacherId(course4.getTeacherId());
        schedule4.setStartTime(today.atTime(16, 0));
        schedule4.setEndTime(today.atTime(17, 40));
        schedule4.setClassroom("D102");
        schedule4.setWeekDay(today.getDayOfWeek().getValue());
        schedule4.setLessonTopic("牛顿运动定律");
        schedule4.setLessonContent("深入理解牛顿三大运动定律，学会应用定律解决实际物理问题。");
        schedule4.setStatus(CourseSchedule.ScheduleStatus.SCHEDULED);
        courseScheduleRepository.save(schedule4);
        System.out.println("插入课程安排：" + course4.getTitle() + " " + schedule4.getStartTime());
        
        // 明天的课程安排
        LocalDate tomorrow = today.plusDays(1);
        System.out.println("明天的日期：" + tomorrow);
        
        // 高等数学 - 明天上午8:00-9:40
        CourseSchedule schedule5 = new CourseSchedule();
        schedule5.setCourseId(course1.getId());
        schedule5.setTeacherId(course1.getTeacherId());
        schedule5.setStartTime(tomorrow.atTime(8, 0));
        schedule5.setEndTime(tomorrow.atTime(9, 40));
        schedule5.setClassroom("A101");
        schedule5.setWeekDay(tomorrow.getDayOfWeek().getValue());
        schedule5.setLessonTopic("导数的概念与计算");
        schedule5.setLessonContent("学习导数的定义和几何意义，掌握基本函数的导数计算方法。");
        schedule5.setStatus(CourseSchedule.ScheduleStatus.SCHEDULED);
        courseScheduleRepository.save(schedule5);
        System.out.println("插入课程安排：" + course1.getTitle() + " " + schedule5.getStartTime());
        
        // 程序设计基础 - 明天上午10:00-11:40
        CourseSchedule schedule6 = new CourseSchedule();
        schedule6.setCourseId(course3.getId());
        schedule6.setTeacherId(course3.getTeacherId());
        schedule6.setStartTime(tomorrow.atTime(10, 0));
        schedule6.setEndTime(tomorrow.atTime(11, 40));
        schedule6.setClassroom("C301");
        schedule6.setWeekDay(tomorrow.getDayOfWeek().getValue());
        schedule6.setLessonTopic("异常处理机制");
        schedule6.setLessonContent("学习Java异常处理的基本概念，掌握try-catch-finally语句块的使用。");
        schedule6.setStatus(CourseSchedule.ScheduleStatus.SCHEDULED);
        courseScheduleRepository.save(schedule6);
        System.out.println("插入课程安排：" + course3.getTitle() + " " + schedule6.getStartTime());
        
        System.out.println("成功插入了6条课程安排（今天4条，明天2条）");
    }
} 