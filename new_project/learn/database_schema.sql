-- 学习平台完整数据库架构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS learn_platform 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE learn_platform;

-- 1. 用户表 (已实现)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nick_name VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    bio TEXT COMMENT '个人简介',
    status INT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色：ADMIN管理员 TEACHER教师 USER学生',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time TIMESTAMP NULL COMMENT '最后登录时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 班级表 (新增)
CREATE TABLE IF NOT EXISTS classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    class_code VARCHAR(20) UNIQUE COMMENT '班级代码',
    grade VARCHAR(20) COMMENT '年级',
    major VARCHAR(50) COMMENT '专业',
    teacher_id BIGINT COMMENT '班主任ID',
    student_count INT DEFAULT 0 COMMENT '学生数量',
    description TEXT COMMENT '班级描述',
    status INT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_grade (grade),
    INDEX idx_status (status),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- 3. 学生扩展信息表 (新增)
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    student_number VARCHAR(20) UNIQUE COMMENT '学号',
    class_id BIGINT COMMENT '班级ID',
    enrollment_year INT COMMENT '入学年份',
    status ENUM('active', 'inactive', 'graduated') DEFAULT 'active' COMMENT '学生状态',
    parent_contact VARCHAR(50) COMMENT '家长联系方式',
    address TEXT COMMENT '地址',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_class_id (class_id),
    INDEX idx_student_number (student_number),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (class_id) REFERENCES classes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生扩展信息表';

-- 4. 课程分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程分类表';

-- 5. 课程表 (扩展)
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '课程标题',
    course_code VARCHAR(20) UNIQUE COMMENT '课程代码',
    subtitle VARCHAR(500) COMMENT '课程副标题',
    description TEXT COMMENT '课程描述',
    cover_image VARCHAR(255) COMMENT '封面图片',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    category_id BIGINT COMMENT '分类ID',
    semester VARCHAR(20) COMMENT '学期',
    credits INT DEFAULT 3 COMMENT '学分',
    price DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    difficulty_level VARCHAR(20) DEFAULT 'BEGINNER' COMMENT '难度：BEGINNER初级 INTERMEDIATE中级 ADVANCED高级',
    duration_hours INT COMMENT '课程总时长（小时）',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT草稿 PUBLISHED发布 OFFLINE下线',
    is_free BOOLEAN DEFAULT FALSE COMMENT '是否免费',
    student_count INT DEFAULT 0 COMMENT '学生数量',
    rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '评分',
    rating_count INT DEFAULT 0 COMMENT '评分人数',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_is_free (is_free),
    INDEX idx_course_code (course_code),
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 6. 课程安排表 (新增 - 今日教学日程核心表)
CREATE TABLE IF NOT EXISTS course_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL COMMENT '课程ID',
    class_id BIGINT COMMENT '班级ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    classroom VARCHAR(50) COMMENT '教室',
    week_day INT COMMENT '星期几(1-7)',
    lesson_topic VARCHAR(200) COMMENT '课程主题',
    lesson_content TEXT COMMENT '课程内容',
    status ENUM('scheduled', 'preparing', 'ongoing', 'completed', 'cancelled') DEFAULT 'scheduled' COMMENT '状态',
    attendance_count INT DEFAULT 0 COMMENT '出勤人数',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_course_id (course_id),
    INDEX idx_class_id (class_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_start_time (start_time),
    INDEX idx_status (status),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程安排表';

-- 7. 作业表 (新增)
CREATE TABLE IF NOT EXISTS assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL COMMENT '课程ID',
    class_id BIGINT COMMENT '班级ID',
    title VARCHAR(200) NOT NULL COMMENT '作业标题',
    description TEXT COMMENT '作业描述',
    content TEXT COMMENT '作业内容',
    attachment_url VARCHAR(500) COMMENT '附件URL',
    total_score INT DEFAULT 100 COMMENT '总分',
    due_date DATETIME COMMENT '截止时间',
    created_by BIGINT NOT NULL COMMENT '创建者ID',
    status ENUM('draft', 'published', 'closed') DEFAULT 'draft' COMMENT '状态',
    submission_count INT DEFAULT 0 COMMENT '提交数量',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_course_id (course_id),
    INDEX idx_class_id (class_id),
    INDEX idx_created_by (created_by),
    INDEX idx_due_date (due_date),
    INDEX idx_status (status),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业表';

-- 8. 学生作业提交表 (新增)
CREATE TABLE IF NOT EXISTS assignment_submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assignment_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    submission_content TEXT COMMENT '提交内容',
    attachment_url VARCHAR(500) COMMENT '附件URL',
    score INT COMMENT '得分',
    feedback TEXT COMMENT '反馈',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    graded_at TIMESTAMP NULL COMMENT '批改时间',
    graded_by BIGINT COMMENT '批改者ID',
    status ENUM('pending', 'submitted', 'graded', 'returned') DEFAULT 'pending' COMMENT '状态',
    UNIQUE KEY uk_assignment_student (assignment_id, student_id),
    INDEX idx_assignment_id (assignment_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status),
    FOREIGN KEY (assignment_id) REFERENCES assignments(id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (graded_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生作业提交表';

-- 9. 学习行为记录表 (新增 - 学情分析核心表)
CREATE TABLE IF NOT EXISTS learning_behaviors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT COMMENT '课程ID',
    behavior_type ENUM('login', 'view_material', 'submit_assignment', 'participate_discussion', 'watch_video', 'complete_quiz') COMMENT '行为类型',
    behavior_data JSON COMMENT '行为数据(JSON格式)',
    duration INT COMMENT '学习时长（分钟）',
    device_type VARCHAR(20) COMMENT '设备类型',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_behavior_type (behavior_type),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习行为记录表';

-- 10. 知识点掌握情况表 (新增 - 学情分析核心表)
CREATE TABLE IF NOT EXISTS knowledge_mastery (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    knowledge_point VARCHAR(100) NOT NULL COMMENT '知识点',
    knowledge_category VARCHAR(50) COMMENT '知识点分类',
    mastery_level ENUM('not_learned', 'learning', 'mastered', 'expert') DEFAULT 'not_learned' COMMENT '掌握程度',
    test_score INT COMMENT '测试得分',
    test_count INT DEFAULT 0 COMMENT '测试次数',
    mistake_count INT DEFAULT 0 COMMENT '错误次数',
    last_test_time TIMESTAMP NULL COMMENT '最后测试时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_student_course_knowledge (student_id, course_id, knowledge_point),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_mastery_level (mastery_level),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识点掌握情况表';

-- 11. 待办事项表 (新增)
CREATE TABLE IF NOT EXISTS todos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '待办标题',
    description TEXT COMMENT '待办描述',
    priority ENUM('urgent', 'important', 'normal', 'low') DEFAULT 'normal' COMMENT '优先级',
    status ENUM('pending', 'in_progress', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '状态',
    category VARCHAR(50) COMMENT '分类',
    due_date DATETIME COMMENT '截止时间',
    reminder_time DATETIME COMMENT '提醒时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_due_date (due_date),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项表';

-- 12. AI推荐记录表 (新增)
CREATE TABLE IF NOT EXISTS ai_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    recommendation_type ENUM('lesson_plan', 'video', 'exercise', 'material', 'course', 'study_path') COMMENT '推荐类型',
    title VARCHAR(200) NOT NULL COMMENT '推荐标题',
    description TEXT COMMENT '推荐描述',
    content_url VARCHAR(500) COMMENT '内容URL',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    rating DECIMAL(2,1) DEFAULT 0.0 COMMENT '评分',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    recommendation_score DECIMAL(5,2) COMMENT '推荐分数',
    algorithm_version VARCHAR(20) COMMENT '算法版本',
    is_clicked BOOLEAN DEFAULT FALSE COMMENT '是否被点击',
    is_downloaded BOOLEAN DEFAULT FALSE COMMENT '是否被下载',
    feedback_score INT COMMENT '用户反馈分数(1-5)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_recommendation_type (recommendation_type),
    INDEX idx_create_time (create_time),
    INDEX idx_recommendation_score (recommendation_score),
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI推荐记录表';

-- 13. 章节表
CREATE TABLE IF NOT EXISTS chapters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '章节标题',
    description TEXT COMMENT '章节描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_free BOOLEAN DEFAULT FALSE COMMENT '是否免费',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_course_id (course_id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节表';

-- 14. 课时表
CREATE TABLE IF NOT EXISTS lessons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '课时标题',
    content_type VARCHAR(20) DEFAULT 'VIDEO' COMMENT '内容类型：VIDEO视频 DOCUMENT文档 QUIZ测验',
    video_url VARCHAR(500) COMMENT '视频URL',
    video_duration INT COMMENT '视频时长（秒）',
    content TEXT COMMENT '文档内容',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_free BOOLEAN DEFAULT FALSE COMMENT '是否免费',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_chapter_id (chapter_id),
    INDEX idx_course_id (course_id),
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课时表';

-- 15. 学生选课表
CREATE TABLE IF NOT EXISTS enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    enrollment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    payment_amount DECIMAL(10,2) COMMENT '支付金额',
    payment_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '支付状态：PENDING待支付 PAID已支付 REFUNDED已退款',
    progress DECIMAL(5,2) DEFAULT 0.00 COMMENT '学习进度（百分比）',
    last_learn_time TIMESTAMP NULL COMMENT '最后学习时间',
    completion_time TIMESTAMP NULL COMMENT '完成时间',
    UNIQUE KEY uk_user_course (user_id, course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_course_id (course_id),
    INDEX idx_payment_status (payment_status),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生选课表';

-- 16. 学习进度表
CREATE TABLE IF NOT EXISTS learning_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    lesson_id BIGINT NOT NULL COMMENT '课时ID',
    is_completed BOOLEAN DEFAULT FALSE COMMENT '是否完成',
    watch_duration INT DEFAULT 0 COMMENT '观看时长（秒）',
    last_watch_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后观看时间',
    UNIQUE KEY uk_user_course_lesson (user_id, course_id, lesson_id),
    INDEX idx_user_id (user_id),
    INDEX idx_course_id (course_id),
    INDEX idx_lesson_id (lesson_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (lesson_id) REFERENCES lessons(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习进度表';

-- 17. 课程评价表
CREATE TABLE IF NOT EXISTS course_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    rating INT NOT NULL COMMENT '评分 1-5',
    content TEXT COMMENT '评价内容',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_course (user_id, course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_course_id (course_id),
    INDEX idx_rating (rating),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程评价表';

-- 插入初始数据

-- 插入管理员和测试用户
INSERT IGNORE INTO users (username, email, password, nick_name, role, status) VALUES
('admin', 'admin@example.com', '$2a$10$N.zmPFWs.Zi9g2czCDvWN.6NqN2xhyTZkLVz4wt8yj7VZEWfP4M8a', '管理员', 'ADMIN', 1),
('teacher1', 'teacher1@example.com', '$2a$10$N.zmPFWs.Zi9g2czCDvWN.6NqN2xhyTZkLVz4wt8yj7VZEWfP4M8a', '张老师', 'TEACHER', 1),
('teacher2', 'teacher2@example.com', '$2a$10$N.zmPFWs.Zi9g2czCDvWN.6NqN2xhyTZkLVz4wt8yj7VZEWfP4M8a', '李老师', 'TEACHER', 1),
('student1', 'student1@example.com', '$2a$10$N.zmPFWs.Zi9g2czCDvWN.6NqN2xhyTZkLVz4wt8yj7VZEWfP4M8a', '小明', 'USER', 1),
('student2', 'student2@example.com', '$2a$10$N.zmPFWs.Zi9g2czCDvWN.6NqN2xhyTZkLVz4wt8yj7VZEWfP4M8a', '小红', 'USER', 1),
('testuser', 'test@example.com', '$2a$10$N.zmPFWs.Zi9g2czCDvWN.6NqN2xhyTZkLVz4wt8yj7VZEWfP4M8a', '测试用户', 'USER', 1);

-- 插入班级数据
INSERT IGNORE INTO classes (id, class_name, class_code, grade, major, teacher_id) VALUES
(1, '计算机科学2023-1班', 'CS2023-1', '2023级', '计算机科学与技术', 2),
(2, '计算机科学2023-2班', 'CS2023-2', '2023级', '计算机科学与技术', 3),
(3, '软件工程2023-1班', 'SE2023-1', '2023级', '软件工程', 2);

-- 插入学生信息
INSERT IGNORE INTO students (user_id, student_number, class_id, enrollment_year) VALUES
(4, '2023001001', 1, 2023),
(5, '2023001002', 1, 2023),
(6, '2023001003', 2, 2023);

-- 插入课程分类
INSERT IGNORE INTO categories (id, name, description, parent_id) VALUES
(1, '编程开发', '编程开发相关课程', 0),
(2, '前端开发', '前端开发技术', 1),
(3, '后端开发', '后端开发技术', 1),
(4, '移动开发', '移动应用开发', 1),
(5, '数据科学', '数据科学与分析', 0),
(6, '设计创作', '设计与创意', 0);

-- 插入示例课程
INSERT IGNORE INTO courses (id, title, course_code, subtitle, description, teacher_id, category_id, semester, credits, price, difficulty_level, is_free) VALUES
(1, 'Spring Boot 入门到精通', 'CS101', '从零开始学习Spring Boot框架', 'Spring Boot是目前最流行的Java Web开发框架，本课程将带你从零开始，逐步掌握Spring Boot的核心概念和实战技能。', 2, 3, '2024春', 4, 299.00, 'BEGINNER', false),
(2, 'Vue.js 3.0 完整教程', 'FE201', '现代前端框架Vue.js最新版本', '学习Vue.js 3.0的新特性，包括Composition API、响应式系统改进等，适合有JavaScript基础的学员。', 2, 2, '2024春', 3, 199.00, 'INTERMEDIATE', false),
(3, '免费Java基础课程', 'CS001', 'Java编程语言基础入门', '完全免费的Java基础课程，适合编程初学者，从基本语法到面向对象编程。', 3, 3, '2024春', 4, 0.00, 'BEGINNER', true),
(4, '数据结构与算法', 'CS201', '计算机科学核心课程', '深入学习数据结构与算法，包括链表、树、图、排序和搜索算法等。', 2, 1, '2024春', 4, 399.00, 'INTERMEDIATE', false);

-- 插入课程安排 (今日和本周的教学日程)
INSERT IGNORE INTO course_schedules (course_id, class_id, teacher_id, start_time, end_time, classroom, week_day, lesson_topic, status) VALUES
(1, 1, 2, '2024-01-15 08:00:00', '2024-01-15 09:40:00', 'A101', 1, 'Spring Boot 项目创建与配置', 'scheduled'),
(1, 1, 2, '2024-01-15 10:00:00', '2024-01-15 11:40:00', 'A101', 1, 'Spring Boot Web 开发基础', 'scheduled'),
(2, 1, 2, '2024-01-15 14:00:00', '2024-01-15 15:40:00', 'B201', 1, 'Vue.js 组件开发', 'scheduled'),
(3, 2, 3, '2024-01-15 08:00:00', '2024-01-15 09:40:00', 'C301', 1, 'Java 面向对象编程', 'scheduled'),
(4, 1, 2, '2024-01-16 10:00:00', '2024-01-16 11:40:00', 'A102', 2, '数组与链表', 'scheduled');

-- 插入作业数据
INSERT IGNORE INTO assignments (course_id, class_id, title, description, total_score, due_date, created_by, status) VALUES
(1, 1, 'Spring Boot 初体验', '创建一个简单的Spring Boot Web应用，实现用户注册和登录功能', 100, '2024-01-20 23:59:59', 2, 'published'),
(2, 1, 'Vue.js 组件练习', '使用Vue.js创建一个待办事项列表组件', 80, '2024-01-18 23:59:59', 2, 'published'),
(3, 2, 'Java基础练习', '完成Java基础语法练习题', 60, '2024-01-17 23:59:59', 3, 'published');

-- 插入待办事项
INSERT IGNORE INTO todos (user_id, title, description, priority, status, category, due_date) VALUES
(2, '批改作业', '批改Spring Boot作业，共30份', 'important', 'pending', '教学', '2024-01-16 18:00:00'),
(2, '准备明天课件', '准备Vue.js组件开发的课件', 'urgent', 'in_progress', '教学', '2024-01-16 08:00:00'),
(2, '参加教学会议', '讨论新学期教学计划', 'normal', 'pending', '会议', '2024-01-17 14:00:00'),
(4, '完成Java作业', '完成Java基础语法练习', 'important', 'pending', '学习', '2024-01-17 23:59:59'),
(5, '复习Vue.js', '准备明天的Vue.js课程', 'normal', 'pending', '学习', '2024-01-15 20:00:00');

-- 显示创建结果
SELECT 'Database schema created successfully!' as message;
SELECT '当前用户:' as info, username, email, role FROM users;
SELECT '当前班级:' as info, class_name, grade, major FROM classes;
SELECT '当前课程:' as info, title, course_code, semester, credits FROM courses;
SELECT '今日课程安排:' as info, COUNT(*) as count FROM course_schedules WHERE DATE(start_time) = CURDATE();
SELECT '作业数量:' as info, COUNT(*) as count FROM assignments;
SELECT '待办事项:' as info, COUNT(*) as count FROM todos; 