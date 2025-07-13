-- 初始化用户表数据
INSERT INTO users (username, email, password, nick_name, status, create_time) VALUES
('admin', 'admin@example.com', '$2a$10$X5wFHlw6.8Z7qJ1P2nHqROKLhRzHtNGKzAzPjJ7cL9J5Xz2nJ7VQC', '管理员', 1, NOW()),
('testuser', 'test@example.com', '$2a$10$X5wFHlw6.8Z7qJ1P2nHqROKLhRzHtNGKzAzPjJ7cL9J5Xz2nJ7VQC', '测试用户', 1, NOW())
ON CONFLICT (username) DO NOTHING; 