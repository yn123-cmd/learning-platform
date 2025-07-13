# 学习平台后端项目

这是一个基于Spring Boot开发的在线学习平台后端系统，支持用户管理、课程管理、章节管理等功能。

## 技术栈

- **框架**: Spring Boot 3.5.0
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **安全**: Spring Security + JWT
- **ORM**: Spring Data JPA
- **构建工具**: Maven
- **开发语言**: Java 17

## 功能模块

### 已实现功能

1. **用户管理**
   - 用户注册/登录
   - 用户信息管理
   - 角色权限管理

2. **课程管理**
   - 课程CRUD操作
   - 课程分类和搜索
   - 课程发布/下架

3. **章节管理**
   - 章节结构管理
   - 课时内容管理
   - 学习进度跟踪

4. **系统配置**
   - Spring Security配置
   - Redis缓存配置
   - 数据库连接配置

### 待实现功能

1. **认证授权**
   - JWT Token生成和验证
   - 用户权限控制
   - 登录状态管理

2. **业务逻辑**
   - 用户注册邮箱验证
   - 课程购买和支付
   - 学习进度统计

3. **系统优化**
   - 接口性能优化
   - 缓存策略优化
   - 日志系统完善

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

## 快速开始

### 1. 数据库配置

创建MySQL数据库：

```sql
CREATE DATABASE learn_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 修改配置文件

编辑 `src/main/resources/application.properties`，修改数据库连接信息：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/learn_platform?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码

# Redis配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=你的Redis密码（如果有的话）
```

### 3. 安装依赖

```bash
mvn clean install
```

### 4. 运行项目

```bash
mvn spring-boot:run
```

项目启动后，访问 http://localhost:8080/api

## API接口文档

### 用户管理

| 方法 | 路径 | 描述 |
|-----|------|------|
| GET | /api/users | 获取用户列表 |
| GET | /api/users/{id} | 获取用户详情 |
| POST | /api/users | 创建用户 |
| PUT | /api/users/{id} | 更新用户信息 |
| DELETE | /api/users/{id} | 删除用户 |

### 认证相关

| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |
| GET | /api/auth/me | 获取当前用户信息 |

### 课程管理

| 方法 | 路径 | 描述 |
|-----|------|------|
| GET | /api/courses | 获取课程列表 |
| GET | /api/courses/{id} | 获取课程详情 |
| POST | /api/courses | 创建课程 |
| PUT | /api/courses/{id} | 更新课程 |
| DELETE | /api/courses/{id} | 删除课程 |
| POST | /api/courses/{id}/publish | 发布课程 |
| POST | /api/courses/{id}/unpublish | 下架课程 |

## 数据库表结构

### 用户表 (users)
- id: 主键
- username: 用户名
- password: 密码（加密）
- email: 邮箱
- nick_name: 昵称
- phone: 手机号
- avatar: 头像
- status: 状态
- create_time: 创建时间
- update_time: 更新时间

### 角色表 (roles)
- id: 主键
- name: 角色名
- display_name: 显示名称
- description: 描述
- status: 状态

### 课程表 (courses)
- id: 主键
- title: 课程标题
- description: 课程描述
- cover_image: 封面图片
- price: 价格
- category_id: 分类ID
- instructor_id: 讲师ID
- difficulty_level: 难度等级
- duration_hours: 课程时长
- student_count: 学生数量
- rating: 评分
- status: 状态

### 章节表 (chapters)
- id: 主键
- title: 章节标题
- description: 章节描述
- course_id: 课程ID
- sort_order: 排序
- duration_minutes: 时长

### 课时表 (lessons)
- id: 主键
- title: 课时标题
- content: 内容
- video_url: 视频地址
- chapter_id: 章节ID
- lesson_type: 课时类型
- is_free: 是否免费

## 开发规范

1. **代码规范**: 使用阿里巴巴Java开发手册
2. **提交规范**: 使用语义化提交信息
3. **接口规范**: RESTful API设计
4. **响应格式**: 统一使用Result包装器

## 与前端配合

本项目为后端API，推荐与以下前端技术配合：

- **Vue.js 2/3**: 适合快速开发
- **React**: 适合复杂交互
- **Angular**: 适合大型项目

### 前端配置示例

```javascript
// axios配置
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
});

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

## 部署说明

### Docker部署

1. 构建镜像
```bash
docker build -t learn-platform-backend .
```

2. 运行容器
```bash
docker run -p 8080:8080 learn-platform-backend
```

### 生产环境配置

1. 修改数据库连接池配置
2. 配置Redis集群
3. 启用HTTPS
4. 配置日志收集
5. 设置监控告警

## 贡献指南

1. Fork 本仓库
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License 