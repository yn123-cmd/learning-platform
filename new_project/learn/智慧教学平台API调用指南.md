# 智慧教学平台 API 调用指南

## 概述

智慧教学平台提供了完整的RESTful API接口，支持今日教学日程、学情分析、待办事项、AI推荐等核心功能。所有接口都遵循统一的响应格式。

**基础URL**: `http://localhost:8080`

**API文档地址**: `http://localhost:8080/swagger-ui/index.html`

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1704067200000
}
```

## 接口分类

### 1. 课程安排管理 (今日教学日程)

#### 1.1 获取今日课程安排

**接口地址**: `GET /api/schedules/today`

**请求示例**:
```javascript
// JavaScript/TypeScript
async function getTodaySchedules() {
  const response = await fetch('/api/schedules/today', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  });
  
  const result = await response.json();
  
  if (result.code === 200) {
    console.log('今日课程安排:', result.data);
    return result.data;
  } else {
    console.error('获取失败:', result.message);
    throw new Error(result.message);
  }
}

// Vue.js 示例
export default {
  data() {
    return {
      todaySchedules: []
    }
  },
  async mounted() {
    try {
      this.todaySchedules = await getTodaySchedules();
    } catch (error) {
      this.$message.error('获取今日课程安排失败');
    }
  }
}
```

**响应数据**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "courseId": 1,
      "courseName": "Spring Boot 入门到精通",
      "courseCode": "CS101",
      "classId": 1,
      "className": "计算机科学2023-1班",
      "teacherId": 2,
      "teacherName": "张老师",
      "startTime": "2024-01-15 08:00:00",
      "endTime": "2024-01-15 09:40:00",
      "classroom": "A101",
      "lessonTopic": "Spring Boot 项目创建与配置",
      "lessonContent": "学习Spring Boot项目的创建和基本配置",
      "status": "SCHEDULED",
      "attendanceCount": 0,
      "timeRange": "08:00-09:40",
      "statusText": "已安排",
      "isUpcoming": true,
      "isOngoing": false,
      "minutesToStart": 45
    }
  ]
}
```

#### 1.2 获取指定日期的课程安排

**接口地址**: `GET /api/schedules/date/{date}`

**请求示例**:
```javascript
// 获取指定日期的课程安排
async function getSchedulesByDate(date) {
  // date格式: yyyy-MM-dd，如 '2024-01-15'
  const response = await fetch(`/api/schedules/date/${date}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  });
  
  return await response.json();
}

// React Hook 示例
import { useState, useEffect } from 'react';

function useSchedules(date) {
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    async function fetchSchedules() {
      setLoading(true);
      setError(null);
      
      try {
        const result = await getSchedulesByDate(date);
        if (result.code === 200) {
          setSchedules(result.data);
        } else {
          setError(result.message);
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    
    if (date) {
      fetchSchedules();
    }
  }, [date]);
  
  return { schedules, loading, error };
}
```

#### 1.3 获取本周课程安排

**接口地址**: `GET /api/schedules/week`

**请求示例**:
```javascript
// Angular Service 示例
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private apiUrl = '/api/schedules';
  
  constructor(private http: HttpClient) {}
  
  getWeekSchedules(): Observable<any> {
    return this.http.get(`${this.apiUrl}/week`);
  }
  
  getTeacherSchedules(teacherId: number, date?: string): Observable<any> {
    const params = date ? `?date=${date}` : '';
    return this.http.get(`${this.apiUrl}/teacher/${teacherId}${params}`);
  }
  
  getClassSchedules(classId: number, date?: string): Observable<any> {
    const params = date ? `?date=${date}` : '';
    return this.http.get(`${this.apiUrl}/class/${classId}${params}`);
  }
  
  updateScheduleStatus(scheduleId: number, status: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${scheduleId}/status?status=${status}`, {});
  }
}
```

#### 1.4 获取正在进行的课程

**接口地址**: `GET /api/schedules/ongoing`

#### 1.5 获取即将开始的课程

**接口地址**: `GET /api/schedules/upcoming`

#### 1.6 更新课程状态

**接口地址**: `PUT /api/schedules/{scheduleId}/status`

**请求参数**: `status` (查询参数)
- 可选值: `SCHEDULED`, `PREPARING`, `ONGOING`, `COMPLETED`, `CANCELLED`

**请求示例**:
```javascript
// 更新课程状态
async function updateScheduleStatus(scheduleId, status) {
  const response = await fetch(`/api/schedules/${scheduleId}/status?status=${status}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  });
  
  return await response.json();
}

// 使用示例
try {
  const result = await updateScheduleStatus(1, 'ONGOING');
  if (result.code === 200) {
    console.log('课程状态更新成功');
  }
} catch (error) {
  console.error('更新失败:', error);
}
```

### 2. 学情分析管理

#### 2.1 获取学情分析概览

**接口地址**: `GET /api/analytics/overview`

**请求示例**:
```javascript
// 获取学情分析概览
async function getAnalyticsOverview() {
  const response = await fetch('/api/analytics/overview', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  });
  
  return await response.json();
}

// Vue.js 组件示例
<template>
  <div class="analytics-dashboard">
    <div class="stats-grid">
      <div class="stat-card">
        <h3>知识掌握率</h3>
        <div class="stat-value">{{ overview.knowledgeMasteryRate }}%</div>
      </div>
      <div class="stat-card">
        <h3>作业完成率</h3>
        <div class="stat-value">{{ overview.assignmentCompletionRate }}%</div>
      </div>
      <div class="stat-card">
        <h3>课堂参与度</h3>
        <div class="stat-value">{{ overview.classParticipationRate }}%</div>
      </div>
      <div class="stat-card">
        <h3>平均成绩</h3>
        <div class="stat-value">{{ overview.averageScore }}</div>
      </div>
    </div>
    
    <div class="today-data">
      <h3>今日数据</h3>
      <p>出勤人数: {{ overview.todayData?.todayAttendance }}</p>
      <p>作业提交: {{ overview.todayData?.todayAssignmentSubmissions }}</p>
      <p>活跃学习者: {{ overview.todayData?.activeLearners }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      overview: {}
    }
  },
  async created() {
    await this.loadAnalyticsOverview();
  },
  methods: {
    async loadAnalyticsOverview() {
      try {
        const result = await getAnalyticsOverview();
        if (result.code === 200) {
          this.overview = result.data;
        }
      } catch (error) {
        this.$message.error('获取学情分析失败');
      }
    }
  }
}
</script>
```

**响应数据**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalStudents": 150,
    "totalCourses": 12,
    "totalAssignments": 45,
    "knowledgeMasteryRate": 78.5,
    "assignmentCompletionRate": 85.2,
    "classParticipationRate": 92.1,
    "averageScore": 82.3,
    "totalLearningMinutes": 15420,
    "averageDailyLearningMinutes": 45.2,
    "todayData": {
      "todayAttendance": 128,
      "todayAssignmentSubmissions": 23,
      "todayLearningMinutes": 890,
      "activeLearners": 115
    },
    "trendData": [
      {
        "date": "2024-01-08",
        "masteryRate": 75.2,
        "completionRate": 82.1,
        "participationRate": 89.3,
        "learningMinutes": 820
      }
    ]
  }
}
```

### 3. 待办事项管理

#### 3.1 获取待办列表

**接口地址**: `GET /api/todos`

**请求示例**:
```javascript
// 获取待办事项列表
async function getTodos() {
  const response = await fetch('/api/todos', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  });
  
  return await response.json();
}

// React 组件示例
import React, { useState, useEffect } from 'react';

function TodoList() {
  const [todos, setTodos] = useState([]);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    loadTodos();
  }, []);
  
  const loadTodos = async () => {
    try {
      const result = await getTodos();
      if (result.code === 200) {
        setTodos(result.data);
      }
    } catch (error) {
      console.error('获取待办事项失败:', error);
    } finally {
      setLoading(false);
    }
  };
  
  const updateTodoStatus = async (todoId, status) => {
    try {
      const result = await updateTodo(todoId, { status });
      if (result.code === 200) {
        loadTodos(); // 重新加载列表
      }
    } catch (error) {
      console.error('更新失败:', error);
    }
  };
  
  if (loading) return <div>加载中...</div>;
  
  return (
    <div className="todo-list">
      {todos.map(todo => (
        <div key={todo.id} className={`todo-item priority-${todo.priority}`}>
          <h4>{todo.title}</h4>
          <p>{todo.description}</p>
          <div className="todo-meta">
            <span className="priority">{todo.priority}</span>
            <span className="status">{todo.status}</span>
            <span className="due-date">{todo.dueDate}</span>
          </div>
          <button onClick={() => updateTodoStatus(todo.id, 'COMPLETED')}>
            完成
          </button>
        </div>
      ))}
    </div>
  );
}
```

#### 3.2 创建待办事项

**接口地址**: `POST /api/todos`

**请求示例**:
```javascript
// 创建待办事项
async function createTodo(todoData) {
  const response = await fetch('/api/todos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    },
    body: JSON.stringify(todoData)
  });
  
  return await response.json();
}

// 使用示例
const newTodo = {
  title: '准备明天的课件',
  description: '准备Vue.js组件开发的课件',
  priority: 'IMPORTANT',
  category: '教学',
  dueDate: '2024-01-16 08:00:00'
};

try {
  const result = await createTodo(newTodo);
  if (result.code === 200) {
    console.log('待办事项创建成功');
  }
} catch (error) {
  console.error('创建失败:', error);
}
```

#### 3.3 更新待办事项

**接口地址**: `PUT /api/todos/{todoId}`

#### 3.4 删除待办事项

**接口地址**: `DELETE /api/todos/{todoId}`

### 4. AI推荐管理

#### 4.1 获取个性化推荐

**接口地址**: `GET /api/recommendations`

**请求示例**:
```javascript
// 获取AI推荐
async function getRecommendations() {
  const response = await fetch('/api/recommendations', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem('token')
    }
  });
  
  return await response.json();
}

// 推荐组件示例
function RecommendationList() {
  const [recommendations, setRecommendations] = useState([]);
  
  useEffect(() => {
    loadRecommendations();
  }, []);
  
  const loadRecommendations = async () => {
    try {
      const result = await getRecommendations();
      if (result.code === 200) {
        setRecommendations(result.data);
      }
    } catch (error) {
      console.error('获取推荐失败:', error);
    }
  };
  
  const handleRecommendationClick = async (id) => {
    try {
      await recordRecommendationClick(id);
      // 处理点击事件
    } catch (error) {
      console.error('记录点击失败:', error);
    }
  };
  
  return (
    <div className="recommendation-list">
      {recommendations.map(item => (
        <div key={item.id} className="recommendation-card" 
             onClick={() => handleRecommendationClick(item.id)}>
          <img src={item.thumbnailUrl} alt={item.title} />
          <h4>{item.title}</h4>
          <p>{item.description}</p>
          <div className="recommendation-meta">
            <span>评分: {item.rating}</span>
            <span>下载: {item.downloadCount}</span>
            <span>类型: {item.recommendationType}</span>
          </div>
        </div>
      ))}
    </div>
  );
}
```

### 5. 用户认证

#### 5.1 用户登录

**接口地址**: `POST /api/auth/login`

**请求示例**:
```javascript
// 用户登录
async function login(username, password) {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      username,
      password
    })
  });
  
  const result = await response.json();
  
  if (result.code === 200) {
    // 保存token
    localStorage.setItem('token', result.data.token);
    localStorage.setItem('user', JSON.stringify(result.data.user));
    return result.data;
  } else {
    throw new Error(result.message);
  }
}

// 登录表单组件
function LoginForm() {
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const userData = await login(formData.username, formData.password);
      console.log('登录成功:', userData.user);
      // 跳转到主页
      window.location.href = '/dashboard';
    } catch (error) {
      alert('登录失败: ' + error.message);
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="用户名"
        value={formData.username}
        onChange={(e) => setFormData({...formData, username: e.target.value})}
        required
      />
      <input
        type="password"
        placeholder="密码"
        value={formData.password}
        onChange={(e) => setFormData({...formData, password: e.target.value})}
        required
      />
      <button type="submit">登录</button>
    </form>
  );
}
```

#### 5.2 获取当前用户信息

**接口地址**: `GET /api/auth/me`

## 前端集成示例

### 1. Axios封装

```javascript
// api.js - API请求封装
import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 添加token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 统一处理响应
api.interceptors.response.use(
  (response) => {
    const { data } = response;
    if (data.code === 200) {
      return data;
    } else {
      throw new Error(data.message || '请求失败');
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      // token失效，跳转到登录页
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// API方法
export const scheduleAPI = {
  // 获取今日课程安排
  getTodaySchedules: () => api.get('/api/schedules/today'),
  
  // 获取指定日期的课程安排
  getSchedulesByDate: (date) => api.get(`/api/schedules/date/${date}`),
  
  // 获取本周课程安排
  getWeekSchedules: () => api.get('/api/schedules/week'),
  
  // 更新课程状态
  updateScheduleStatus: (scheduleId, status) => 
    api.put(`/api/schedules/${scheduleId}/status?status=${status}`),
  
  // 获取教师课程安排
  getTeacherSchedules: (teacherId, date) => {
    const params = date ? `?date=${date}` : '';
    return api.get(`/api/schedules/teacher/${teacherId}${params}`);
  }
};

export const analyticsAPI = {
  // 获取学情分析概览
  getOverview: () => api.get('/api/analytics/overview'),
  
  // 获取知识掌握情况
  getKnowledgeMastery: () => api.get('/api/analytics/knowledge-mastery'),
  
  // 获取班级学习状态
  getClassSummary: () => api.get('/api/analytics/class-summary')
};

export const todoAPI = {
  // 获取待办列表
  getTodos: () => api.get('/api/todos'),
  
  // 创建待办事项
  createTodo: (todoData) => api.post('/api/todos', todoData),
  
  // 更新待办事项
  updateTodo: (todoId, todoData) => api.put(`/api/todos/${todoId}`, todoData),
  
  // 删除待办事项
  deleteTodo: (todoId) => api.delete(`/api/todos/${todoId}`)
};

export const authAPI = {
  // 用户登录
  login: (username, password) => api.post('/api/auth/login', { username, password }),
  
  // 获取当前用户信息
  getCurrentUser: () => api.get('/api/auth/me'),
  
  // 验证token
  validateToken: () => api.post('/api/auth/validate')
};
```

### 2. Vue.js 完整示例

```vue
<!-- Dashboard.vue -->
<template>
  <div class="dashboard">
    <!-- 今日课程安排 -->
    <div class="section">
      <h2>今日教学日程</h2>
      <div class="schedule-list">
        <div v-for="schedule in todaySchedules" :key="schedule.id" 
             class="schedule-card"
             :class="{ 
               'upcoming': schedule.isUpcoming, 
               'ongoing': schedule.isOngoing 
             }">
          <div class="schedule-time">{{ schedule.timeRange }}</div>
          <div class="schedule-info">
            <h3>{{ schedule.courseName }}</h3>
            <p>{{ schedule.lessonTopic }}</p>
            <div class="schedule-meta">
              <span>{{ schedule.className }}</span>
              <span>{{ schedule.classroom }}</span>
              <span>{{ schedule.statusText }}</span>
            </div>
          </div>
          <div class="schedule-actions">
            <button v-if="schedule.status === 'SCHEDULED'" 
                    @click="updateStatus(schedule.id, 'PREPARING')">
              开始准备
            </button>
            <button v-if="schedule.status === 'PREPARING'" 
                    @click="updateStatus(schedule.id, 'ONGOING')">
              开始上课
            </button>
            <button v-if="schedule.status === 'ONGOING'" 
                    @click="updateStatus(schedule.id, 'COMPLETED')">
              结束课程
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 学情分析 -->
    <div class="section">
      <h2>学情分析</h2>
      <div class="analytics-grid">
        <div class="metric-card">
          <h3>知识掌握率</h3>
          <div class="metric-value">{{ analytics.knowledgeMasteryRate }}%</div>
          <div class="metric-trend">↗ +2.5%</div>
        </div>
        <div class="metric-card">
          <h3>作业完成率</h3>
          <div class="metric-value">{{ analytics.assignmentCompletionRate }}%</div>
          <div class="metric-trend">↗ +1.8%</div>
        </div>
        <div class="metric-card">
          <h3>课堂参与度</h3>
          <div class="metric-value">{{ analytics.classParticipationRate }}%</div>
          <div class="metric-trend">→ 0.0%</div>
        </div>
        <div class="metric-card">
          <h3>平均成绩</h3>
          <div class="metric-value">{{ analytics.averageScore }}</div>
          <div class="metric-trend">↗ +0.5</div>
        </div>
      </div>
    </div>
    
    <!-- 待办事项 -->
    <div class="section">
      <h2>待办事项</h2>
      <div class="todo-list">
        <div v-for="todo in todos" :key="todo.id" 
             class="todo-item"
             :class="`priority-${todo.priority.toLowerCase()}`">
          <div class="todo-content">
            <h4>{{ todo.title }}</h4>
            <p>{{ todo.description }}</p>
            <div class="todo-meta">
              <span>{{ todo.category }}</span>
              <span>{{ formatDate(todo.dueDate) }}</span>
            </div>
          </div>
          <div class="todo-actions">
            <button @click="completeTodo(todo.id)">完成</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { scheduleAPI, analyticsAPI, todoAPI } from '@/api';

export default {
  name: 'Dashboard',
  data() {
    return {
      todaySchedules: [],
      analytics: {},
      todos: [],
      loading: false
    }
  },
  async created() {
    await this.loadDashboardData();
  },
  methods: {
    async loadDashboardData() {
      this.loading = true;
      try {
        // 并行加载数据
        const [schedulesRes, analyticsRes, todosRes] = await Promise.all([
          scheduleAPI.getTodaySchedules(),
          analyticsAPI.getOverview(),
          todoAPI.getTodos()
        ]);
        
        this.todaySchedules = schedulesRes.data;
        this.analytics = analyticsRes.data;
        this.todos = todosRes.data.filter(todo => todo.status !== 'COMPLETED');
        
      } catch (error) {
        this.$message.error('加载数据失败: ' + error.message);
      } finally {
        this.loading = false;
      }
    },
    
    async updateStatus(scheduleId, status) {
      try {
        await scheduleAPI.updateScheduleStatus(scheduleId, status);
        this.$message.success('状态更新成功');
        await this.loadDashboardData(); // 重新加载数据
      } catch (error) {
        this.$message.error('状态更新失败: ' + error.message);
      }
    },
    
    async completeTodo(todoId) {
      try {
        await todoAPI.updateTodo(todoId, { status: 'COMPLETED' });
        this.$message.success('待办事项已完成');
        await this.loadDashboardData(); // 重新加载数据
      } catch (error) {
        this.$message.error('操作失败: ' + error.message);
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return '';
      return new Date(dateString).toLocaleString('zh-CN');
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.section {
  margin-bottom: 30px;
}

.schedule-card {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-bottom: 10px;
  transition: all 0.3s ease;
}

.schedule-card.upcoming {
  border-left: 4px solid #ff9800;
  background-color: #fff3e0;
}

.schedule-card.ongoing {
  border-left: 4px solid #4caf50;
  background-color: #e8f5e8;
}

.analytics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.metric-card {
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  text-align: center;
}

.metric-value {
  font-size: 2em;
  font-weight: bold;
  color: #2196f3;
  margin: 10px 0;
}

.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.todo-item.priority-urgent {
  background-color: #ffebee;
  border-left: 4px solid #f44336;
}

.todo-item.priority-important {
  background-color: #fff3e0;
  border-left: 4px solid #ff9800;
}

.todo-item.priority-normal {
  background-color: #f5f5f5;
  border-left: 4px solid #9e9e9e;
}
</style>
```

## 错误处理

```javascript
// 统一错误处理
class APIError extends Error {
  constructor(message, code, response) {
    super(message);
    this.code = code;
    this.response = response;
  }
}

// 错误处理工具函数
export function handleAPIError(error) {
  if (error.response) {
    // 服务器返回错误状态码
    const { status, data } = error.response;
    switch (status) {
      case 401:
        return '未授权，请重新登录';
      case 403:
        return '权限不足';
      case 404:
        return '资源不存在';
      case 500:
        return '服务器内部错误';
      default:
        return data?.message || '请求失败';
    }
  } else if (error.request) {
    // 网络错误
    return '网络连接失败，请检查网络设置';
  } else {
    // 其他错误
    return error.message || '未知错误';
  }
}
```

## 注意事项

1. **认证**: 除了登录接口外，所有接口都需要在请求头中携带JWT token
2. **时间格式**: 所有时间字段统一使用 `yyyy-MM-dd HH:mm:ss` 格式
3. **分页**: 列表接口支持分页参数 `page` 和 `size`
4. **错误处理**: 统一错误格式，前端应统一处理
5. **CORS**: 确保后端配置了正确的CORS策略
6. **实时更新**: 建议使用WebSocket或定时轮询获取最新数据

这份API文档涵盖了智慧教学平台的所有核心功能，提供了详细的前端调用示例，可以直接用于前端开发集成。 