<template>
  <div class="course-list">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <h2>学习平台</h2>
        </div>
        <div class="nav-menu">
          <router-link to="/dashboard" class="nav-item">首页</router-link>
          <router-link to="/courses" class="nav-item active">课程</router-link>
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
    <div class="main-content">
      <!-- 页面标题和搜索 -->
      <div class="page-header">
        <div class="page-title">
          <h1>课程中心</h1>
          <p>发现优质学习内容，提升专业技能</p>
        </div>
        <div class="search-area">
          <el-input
            v-model="searchQuery"
            placeholder="搜索课程..."
            prefix-icon="el-icon-search"
            size="large"
            class="search-input"
          ></el-input>
        </div>
      </div>

      <!-- 筛选器 -->
      <div class="filters">
        <div class="filter-group">
          <span class="filter-label">难度：</span>
          <el-button-group>
            <el-button
              :type="selectedLevel === '全部' ? 'primary' : 'default'"
              @click="selectedLevel = '全部'"
              size="small"
            >
              全部
            </el-button>
            <el-button
              :type="selectedLevel === '初级' ? 'primary' : 'default'"
              @click="selectedLevel = '初级'"
              size="small"
            >
              初级
            </el-button>
            <el-button
              :type="selectedLevel === '中级' ? 'primary' : 'default'"
              @click="selectedLevel = '中级'"
              size="small"
            >
              中级
            </el-button>
            <el-button
              :type="selectedLevel === '高级' ? 'primary' : 'default'"
              @click="selectedLevel = '高级'"
              size="small"
            >
              高级
            </el-button>
          </el-button-group>
        </div>
        <div class="filter-group">
          <span class="filter-label">排序：</span>
          <el-select v-model="sortBy" size="small">
            <el-option label="最新发布" value="newest"></el-option>
            <el-option label="最受欢迎" value="popular"></el-option>
            <el-option label="评分最高" value="rating"></el-option>
          </el-select>
        </div>
      </div>

      <!-- 课程网格 -->
      <div class="courses-grid">
        <div
          v-for="course in filteredCourses"
          :key="course.id"
          class="course-card"
          @click="$router.push(`/course/${course.id}`)"
        >
          <div class="course-thumbnail">
            <img :src="course.thumbnail" :alt="course.title">
            <div class="course-level">{{ course.level }}</div>
          </div>
          <div class="course-content">
            <h3 class="course-title">{{ course.title }}</h3>
            <p class="course-description">{{ course.description }}</p>
            <div class="course-meta">
              <div class="instructor">
                <i class="el-icon-user"></i>
                {{ course.instructor }}
              </div>
              <div class="duration">
                <i class="el-icon-time"></i>
                {{ course.duration }}
              </div>
            </div>
            <div class="course-footer">
              <div class="rating">
                <el-rate
                  v-model="course.rating"
                  disabled
                  show-score
                  text-color="#ff9900"
                  score-template="{value}"
                >
                </el-rate>
              </div>
              <el-button type="primary" size="small">开始学习</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CourseList',
  data() {
    return {
      searchQuery: '',
      selectedLevel: '全部',
      sortBy: 'newest'
    }
  },
  computed: {
    user() {
      return this.$store.getters.currentUser
    },
    courses() {
      return this.$store.getters.allCourses.map(course => ({
        ...course,
        rating: 4.5
      }))
    },
    filteredCourses() {
      let filtered = this.courses

      // 按搜索查询过滤
      if (this.searchQuery) {
        filtered = filtered.filter(course =>
          course.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          course.description.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      }

      // 按难度级别过滤
      if (this.selectedLevel !== '全部') {
        filtered = filtered.filter(course => course.level === this.selectedLevel)
      }

      // 排序
      switch (this.sortBy) {
        case 'popular':
          filtered.sort((a, b) => b.rating - a.rating)
          break
        case 'rating':
          filtered.sort((a, b) => b.rating - a.rating)
          break
        case 'newest':
        default:
          filtered.sort((a, b) => b.id - a.id)
      }

      return filtered
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
.course-list {
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title h1 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 2rem;
}

.page-title p {
  margin: 0;
  color: #606266;
}

.search-input {
  width: 300px;
}

.filters {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 24px;
  display: flex;
  gap: 40px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  color: #606266;
  font-weight: 500;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.course-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  cursor: pointer;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.course-thumbnail {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.course-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-level {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(64, 158, 255, 0.9);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.course-content {
  padding: 20px;
}

.course-title {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 1.1rem;
  font-weight: 600;
}

.course-description {
  margin: 0 0 16px 0;
  color: #606266;
  font-size: 0.9rem;
  line-height: 1.5;
}

.course-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.instructor,
.duration {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 0.85rem;
}

.course-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rating {
  font-size: 0.8rem;
}
</style> 