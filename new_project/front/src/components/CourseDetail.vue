<template>
  <div class="course-detail">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <h2>学习平台</h2>
        </div>
        <div class="nav-menu">
          <router-link to="/dashboard" class="nav-item">首页</router-link>
          <router-link to="/courses" class="nav-item">课程</router-link>
        </div>
        <div class="user-menu">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :src="user.avatar" size="medium"></el-avatar>
              <span class="username">{{ user.username }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人资料</el-dropdown-item>
              <el-dropdown-item command="settings">设置</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主要内容 -->
    <div class="main-content" v-if="course">
      <!-- 课程介绍区域 -->
      <div class="course-intro">
        <div class="course-info">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/courses' }">课程</el-breadcrumb-item>
              <el-breadcrumb-item>{{ course.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <h1 class="course-title">{{ course.title }}</h1>
          <p class="course-description">{{ course.description }}</p>
          <div class="course-meta">
            <div class="meta-item">
              <i class="el-icon-user"></i>
              <span>{{ course.instructor }}</span>
            </div>
            <div class="meta-item">
              <i class="el-icon-time"></i>
              <span>{{ course.duration }}</span>
            </div>
            <div class="meta-item">
              <i class="el-icon-medal"></i>
              <span>{{ course.level }}</span>
            </div>
            <div class="meta-item">
              <el-rate
                v-model="course.rating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}"
              ></el-rate>
            </div>
          </div>
          <el-button type="primary" size="large" class="start-btn">
            开始学习
          </el-button>
        </div>
        <div class="course-thumbnail">
          <img :src="course.thumbnail" :alt="course.title">
        </div>
      </div>

      <!-- 课程内容 -->
      <div class="course-content-section">
        <div class="content-tabs">
          <el-tabs v-model="activeTab" type="border-card">
            <el-tab-pane label="课程大纲" name="outline">
              <div class="course-outline">
                <div
                  v-for="(chapter, index) in courseOutline"
                  :key="index"
                  class="chapter"
                >
                  <div class="chapter-header">
                    <h3>{{ chapter.title }}</h3>
                    <span class="chapter-duration">{{ chapter.duration }}</span>
                  </div>
                  <div class="lessons">
                    <div
                      v-for="(lesson, lessonIndex) in chapter.lessons"
                      :key="lessonIndex"
                      class="lesson"
                    >
                      <i class="el-icon-video-camera"></i>
                      <span class="lesson-title">{{ lesson.title }}</span>
                      <span class="lesson-duration">{{ lesson.duration }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="课程介绍" name="description">
              <div class="course-description-detail">
                <h3>课程简介</h3>
                <p>{{ course.description }}</p>
                
                <h3>学习目标</h3>
                <ul class="learning-objectives">
                  <li>掌握Vue.js框架的核心概念和基本语法</li>
                  <li>学会使用Vue组件进行模块化开发</li>
                  <li>理解Vue的响应式数据绑定原理</li>
                  <li>熟练使用Vue Router进行路由管理</li>
                  <li>掌握Vuex状态管理工具的使用方法</li>
                </ul>

                <h3>适合人群</h3>
                <ul class="target-audience">
                  <li>具备HTML、CSS、JavaScript基础的前端开发者</li>
                  <li>希望学习现代前端框架的开发人员</li>
                  <li>对Vue.js感兴趣的学习者</li>
                </ul>
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="学员评价" name="reviews">
              <div class="course-reviews">
                <div class="review-summary">
                  <div class="overall-rating">
                    <div class="rating-score">4.5</div>
                    <el-rate v-model="course.rating" disabled></el-rate>
                    <div class="rating-count">基于168个评价</div>
                  </div>
                </div>
                
                <div class="reviews-list">
                  <div
                    v-for="(review, index) in reviews"
                    :key="index"
                    class="review-item"
                  >
                    <div class="reviewer-info">
                      <el-avatar :src="review.avatar" size="small"></el-avatar>
                      <span class="reviewer-name">{{ review.name }}</span>
                      <el-rate v-model="review.rating" disabled size="mini"></el-rate>
                    </div>
                    <p class="review-content">{{ review.content }}</p>
                    <div class="review-date">{{ review.date }}</div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CourseDetail',
  data() {
    return {
      activeTab: 'outline',
      courseOutline: [
        {
          title: '第一章：Vue.js入门',
          duration: '2小时',
          lessons: [
            { title: 'Vue.js简介', duration: '15分钟' },
            { title: '环境搭建', duration: '20分钟' },
            { title: '第一个Vue应用', duration: '25分钟' },
            { title: '模板语法', duration: '30分钟' }
          ]
        },
        {
          title: '第二章：Vue组件',
          duration: '2.5小时',
          lessons: [
            { title: '组件基础', duration: '25分钟' },
            { title: '组件通信', duration: '35分钟' },
            { title: '插槽和作用域插槽', duration: '30分钟' },
            { title: '动态组件', duration: '20分钟' }
          ]
        },
        {
          title: '第三章：路由管理',
          duration: '1.5小时',
          lessons: [
            { title: 'Vue Router基础', duration: '20分钟' },
            { title: '动态路由', duration: '25分钟' },
            { title: '嵌套路由', duration: '20分钟' },
            { title: '路由守卫', duration: '15分钟' }
          ]
        }
      ],
      reviews: [
        {
          name: '张小明',
          avatar: 'https://via.placeholder.com/40x40',
          rating: 5,
          content: '课程内容非常详细，老师讲解清晰，对于初学者来说很友好。',
          date: '2024-01-15'
        },
        {
          name: '李小红',
          avatar: 'https://via.placeholder.com/40x40',
          rating: 4,
          content: '很好的入门课程，实例丰富，跟着练习可以很快上手Vue。',
          date: '2024-01-10'
        },
        {
          name: '王小刚',
          avatar: 'https://via.placeholder.com/40x40',
          rating: 5,
          content: '系统性很强，从基础到进阶都有涉及，值得推荐！',
          date: '2024-01-08'
        }
      ]
    }
  },
  computed: {
    user() {
      return this.$store.getters.currentUser
    },
    course() {
      const courseId = parseInt(this.$route.params.id)
      const course = this.$store.getters.allCourses.find(c => c.id === courseId)
      return course ? { ...course, rating: 4.5 } : null
    }
  },
  created() {
    this.$store.dispatch('fetchCourses')
  },
  methods: {
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$message.info('个人资料功能开发中')
          break
        case 'settings':
          this.$message.info('设置功能开发中')
          break
        case 'logout':
          this.$store.dispatch('logout')
          this.$message.success('已退出登录')
          this.$router.push('/login')
          break
      }
    }
  }
}
</script>

<style scoped>
.course-detail {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 64px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo h2 {
  color: #409EFF;
  margin: 0;
}

.nav-menu {
  display: flex;
  gap: 30px;
}

.nav-item {
  color: #606266;
  text-decoration: none;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s;
}

.nav-item:hover,
.nav-item.active {
  color: #409EFF;
  background-color: #ecf5ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.username {
  font-weight: 500;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.course-intro {
  background: white;
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 30px;
  display: flex;
  gap: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.course-info {
  flex: 1;
}

.breadcrumb {
  margin-bottom: 16px;
}

.course-title {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 2.2rem;
  font-weight: 700;
}

.course-description {
  margin: 0 0 24px 0;
  color: #606266;
  font-size: 1.1rem;
  line-height: 1.6;
}

.course-meta {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.meta-item i {
  color: #409EFF;
}

.start-btn {
  padding: 12px 32px;
  font-size: 16px;
}

.course-thumbnail {
  width: 400px;
  height: 250px;
  border-radius: 8px;
  overflow: hidden;
}

.course-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-content-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.content-tabs {
  padding: 0;
}

.course-outline {
  padding: 20px;
}

.chapter {
  margin-bottom: 24px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.chapter-header {
  background: #f8f9fa;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chapter-header h3 {
  margin: 0;
  color: #303133;
  font-size: 1.1rem;
}

.chapter-duration {
  color: #909399;
  font-size: 0.9rem;
}

.lessons {
  padding: 0;
}

.lesson {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  transition: background-color 0.3s;
}

.lesson:hover {
  background: #f8f9fa;
}

.lesson i {
  color: #409EFF;
}

.lesson-title {
  flex: 1;
  color: #303133;
}

.lesson-duration {
  color: #909399;
  font-size: 0.9rem;
}

.course-description-detail {
  padding: 20px;
}

.course-description-detail h3 {
  margin: 20px 0 12px 0;
  color: #303133;
}

.course-description-detail p {
  color: #606266;
  line-height: 1.6;
}

.learning-objectives,
.target-audience {
  color: #606266;
  line-height: 1.8;
}

.course-reviews {
  padding: 20px;
}

.review-summary {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.overall-rating {
  display: flex;
  align-items: center;
  gap: 16px;
}

.rating-score {
  font-size: 3rem;
  font-weight: bold;
  color: #409EFF;
}

.rating-count {
  color: #909399;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.reviewer-name {
  font-weight: 500;
  color: #303133;
}

.review-content {
  margin: 0 0 8px 0;
  color: #606266;
  line-height: 1.5;
}

.review-date {
  color: #909399;
  font-size: 0.9rem;
}
</style> 