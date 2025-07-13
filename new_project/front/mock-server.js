const express = require('express');
const cors = require('cors');
const app = express();
const port = 8080;

// 启用CORS
app.use(cors({
  origin: 'http://localhost:3000',
  credentials: true
}));

app.use(express.json());

// 模拟数据
const mockData = {
  // 今日课程安排
  schedules: [
    {
      id: 1,
      courseName: "Spring Boot 入门到精通",
      courseCode: "CS101",
      className: "计算机科学2023-1班",
      classroom: "A101",
      startTime: "2024-01-15 09:00:00",
      endTime: "2024-01-15 10:30:00",
      timeRange: "09:00-10:30",
      status: "ONGOING",
      statusText: "进行中",
      lessonTopic: "Spring Boot 项目创建与配置",
      attendanceCount: 42,
      isUpcoming: false,
      isOngoing: true
    },
    {
      id: 2,
      courseName: "Vue.js 前端开发",
      courseCode: "CS102",
      className: "软件工程2023-2班",
      classroom: "B201",
      startTime: "2024-01-15 14:00:00",
      endTime: "2024-01-15 15:30:00",
      timeRange: "14:00-15:30",
      status: "SCHEDULED",
      statusText: "已安排",
      lessonTopic: "组件化开发与数据绑定",
      attendanceCount: 38,
      isUpcoming: true,
      isOngoing: false
    },
    {
      id: 3,
      courseName: "算法设计与分析",
      courseCode: "CS103",
      className: "计算机研2023",
      classroom: "C301",
      startTime: "2024-01-15 16:00:00",
      endTime: "2024-01-15 17:30:00",
      timeRange: "16:00-17:30",
      status: "PREPARING",
      statusText: "准备中",
      lessonTopic: "动态规划算法实战",
      attendanceCount: 0,
      isUpcoming: true,
      isOngoing: false
    }
  ],

  // 学情分析概览
  analytics: {
    totalStudents: 150,
    totalCourses: 12,
    totalAssignments: 45,
    knowledgeMasteryRate: 78.5,
    assignmentCompletionRate: 85.2,
    classParticipationRate: 92.1,
    averageScore: 82.3,
    totalLearningMinutes: 15420,
    averageDailyLearningMinutes: 45.2,
    todayData: {
      todayAttendance: 128,
      todayAssignmentSubmissions: 23,
      todayLearningMinutes: 890,
      activeLearners: 115
    },
    trendData: [
      {
        date: "2024-01-08",
        masteryRate: 75.2,
        completionRate: 82.1,
        participationRate: 89.3,
        learningMinutes: 820
      },
      {
        date: "2024-01-09",
        masteryRate: 76.8,
        completionRate: 83.5,
        participationRate: 90.1,
        learningMinutes: 845
      }
    ]
  },

  // 待办事项
  todos: [
    {
      id: 1,
      title: "准备明天的Vue.js课件",
      description: "准备组件化开发的教学案例和练习题",
      priority: "URGENT",
      category: "教学",
      status: "PENDING",
      dueDate: "2024-01-16 08:00:00"
    },
    {
      id: 2,
      title: "批改作业：Spring Boot项目",
      description: "批改学生提交的Spring Boot入门项目作业",
      priority: "IMPORTANT",
      category: "教学",
      status: "PENDING",
      dueDate: "2024-01-17 17:00:00"
    },
    {
      id: 3,
      title: "更新课程大纲",
      description: "根据新的教学要求更新算法课程大纲",
      priority: "NORMAL",
      category: "教学",
      status: "PENDING",
      dueDate: "2024-01-20 12:00:00"
    },
    {
      id: 4,
      title: "准备期中考试试卷",
      description: "设计前端开发课程的期中考试题目",
      priority: "IMPORTANT",
      category: "考试",
      status: "PENDING",
      dueDate: "2024-01-18 15:00:00"
    }
  ],

  // AI推荐
  recommendations: [
    {
      id: 1,
      title: "Spring Boot微服务架构实战教案",
      description: "包含完整的微服务开发案例和实践项目",
      recommendationType: "教学资源",
      rating: 4.8,
      downloadCount: 1250,
      thumbnailUrl: "/static/thumb1.jpg"
    },
    {
      id: 2,
      title: "Vue.js组件开发视频教程",
      description: "从基础到高级的Vue组件开发完整教程",
      recommendationType: "课程",
      rating: 4.9,
      downloadCount: 890,
      thumbnailUrl: "/static/thumb2.jpg"
    },
    {
      id: 3,
      title: "算法可视化动画合集",
      description: "动态展示常用算法执行过程的动画资源",
      recommendationType: "教学资源",
      rating: 4.7,
      downloadCount: 2100,
      thumbnailUrl: "/static/thumb3.jpg"
    },
    {
      id: 4,
      title: "数据结构习题库",
      description: "涵盖栈、队列、树、图等数据结构的练习题集",
      recommendationType: "习题",
      rating: 4.6,
      downloadCount: 756,
      thumbnailUrl: "/static/thumb4.jpg"
    }
  ]
};

// API路由
app.get('/api/schedules/today', (req, res) => {
  console.log('📅 请求今日课程安排');
  res.json({
    code: 200,
    message: "操作成功",
    data: mockData.schedules,
    timestamp: Date.now()
  });
});

app.get('/api/analytics/overview', (req, res) => {
  console.log('📊 请求学情分析概览');
  res.json({
    code: 200,
    message: "操作成功",
    data: mockData.analytics,
    timestamp: Date.now()
  });
});

app.get('/api/todos', (req, res) => {
  console.log('📝 请求待办事项列表');
  const { status, priority } = req.query;
  let todos = mockData.todos;

  if (status) {
    todos = todos.filter(todo => todo.status === status);
  }
  if (priority) {
    todos = todos.filter(todo => todo.priority === priority);
  }

  res.json({
    code: 200,
    message: "操作成功",
    data: todos,
    timestamp: Date.now()
  });
});

app.get('/api/recommendations', (req, res) => {
  console.log('🤖 请求AI推荐内容');
  const { type, limit = 10 } = req.query;
  let recommendations = mockData.recommendations;

  if (type) {
    recommendations = recommendations.filter(item => item.recommendationType === type);
  }

  recommendations = recommendations.slice(0, parseInt(limit));

  res.json({
    code: 200,
    message: "操作成功",
    data: recommendations,
    timestamp: Date.now()
  });
});

// 更新课程状态
app.put('/api/schedules/:id/status', (req, res) => {
  const { id } = req.params;
  const { status } = req.query;

  console.log(`🔄 更新课程${id}状态为: ${status}`);

  const schedule = mockData.schedules.find(s => s.id === parseInt(id));
  if (schedule) {
    schedule.status = status;
    schedule.statusText = {
      'SCHEDULED': '已安排',
      'PREPARING': '准备中',
      'ONGOING': '进行中',
      'COMPLETED': '已完成',
      'CANCELLED': '已取消'
    }[status] || status;

    schedule.isOngoing = status === 'ONGOING';
    schedule.isUpcoming = ['SCHEDULED', 'PREPARING'].includes(status);
  }

  res.json({
    code: 200,
    message: "状态更新成功",
    data: schedule,
    timestamp: Date.now()
  });
});

// 创建待办事项
app.post('/api/todos', (req, res) => {
  console.log('➕ 创建新待办事项:', req.body);

  const newTodo = {
    id: mockData.todos.length + 1,
    ...req.body,
    status: 'PENDING'
  };

  mockData.todos.push(newTodo);

  res.json({
    code: 200,
    message: "创建成功",
    data: newTodo,
    timestamp: Date.now()
  });
});

// 更新待办事项
app.put('/api/todos/:id', (req, res) => {
  const { id } = req.params;
  console.log(`📝 更新待办事项${id}:`, req.body);

  const todoIndex = mockData.todos.findIndex(t => t.id === parseInt(id));
  if (todoIndex !== -1) {
    mockData.todos[todoIndex] = { ...mockData.todos[todoIndex], ...req.body };
    res.json({
      code: 200,
      message: "更新成功",
      data: mockData.todos[todoIndex],
      timestamp: Date.now()
    });
  } else {
    res.status(404).json({
      code: 404,
      message: "待办事项不存在",
      timestamp: Date.now()
    });
  }
});

// 记录推荐点击
app.post('/api/recommendations/:id/click', (req, res) => {
  const { id } = req.params;
  console.log(`👆 记录推荐${id}的点击`);

  res.json({
    code: 200,
    message: "点击记录成功",
    timestamp: Date.now()
  });
});

// 认证相关
app.get('/api/auth/me', (req, res) => {
  console.log('👤 请求当前用户信息');
  res.json({
    code: 200,
    message: "操作成功",
    data: {
      id: 1,
      username: "teacher001",
      name: "张老师",
      email: "teacher@example.com",
      role: "TEACHER",
      avatar: "/static/avatar.jpg"
    },
    timestamp: Date.now()
  });
});

app.post('/api/auth/validate', (req, res) => {
  console.log('🔐 验证Token');
  res.json({
    code: 200,
    message: "Token有效",
    timestamp: Date.now()
  });
});

// 启动服务器
app.listen(port, () => {
  console.log('🚀 模拟后端服务器启动成功!');
  console.log(`📡 服务地址: http://localhost:${port}`);
  console.log('📋 可用API接口:');
  console.log('   GET  /api/schedules/today     - 今日课程安排');
  console.log('   GET  /api/analytics/overview  - 学情分析概览');
  console.log('   GET  /api/todos              - 待办事项列表');
  console.log('   GET  /api/recommendations    - AI推荐内容');
  console.log('   PUT  /api/schedules/:id/status - 更新课程状态');
  console.log('   POST /api/todos              - 创建待办事项');
  console.log('   GET  /api/auth/me            - 获取用户信息');
  console.log('\n✨ 现在可以测试前后端联通了!');
}); 