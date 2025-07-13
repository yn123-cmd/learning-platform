-- 学习平台数据库初始化脚本
-- 使用方法：在MySQL命令行中执行 source create_database.sql

-- 创建数据库
CREATE DATABASE IF NOT EXISTS learn_platform 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE learn_platform;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nick_name VARCHAR(50),
    status INT DEFAULT 1 COMMENT '1:正常 0:禁用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_time TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入初始用户数据（密码为123456的BCrypt加密结果）
INSERT IGNORE INTO users (username, email, password, nick_name, status, create_time) VALUES
('admin', 'admin@example.com', '$2a$10$N.zmPFWs.Zi9g2czCDvWN.6NqN2xhyTZkLVz4wt8yj7VZEWfP4M8a', '管理员', 1, NOW()),
('testuser', 'test@example.com', '$2a$10$N.zmPFWs.Zi9g2czCDvWN.6NqN2xhyTZkLVz4wt8yj7VZEWfP4M8a', '测试用户', 1, NOW());

-- 显示创建结果
SELECT 'Database and tables created successfully!' as message;
SELECT * FROM users; 