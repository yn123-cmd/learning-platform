<template>
  <div class="dashboard-content">
    <!-- 加载状态遮罩 -->
    <div v-loading="loading" class="content-grid">
      <!-- 今日教学日程 -->
      <el-card class="schedule-card">
        <div slot="header" class="card-header">
          <i class="el-icon-date"></i>
          <span>今日教学日程</span>
          <span class="date-info">{{ currentDate }}</span>
          <el-button type="text" size="small" style="float: right;" @click="loadSchedules">刷新</el-button>
        </div>
        
        <div class="schedule-list" v-if="!scheduleLoading && schedules.length > 0">
          <div 
            v-for="schedule in schedules" 
            :key="schedule.id"
            class="schedule-item"
            :class="{
              'active': schedule.isOngoing,
              'upcoming': schedule.isUpcoming
            }"
          >
            <div class="schedule-time">
              <div class="time-slot">{{ schedule.timeRange }}</div>
              <div class="time-duration">{{ calculateDuration(schedule.startTime, schedule.endTime) }}分钟</div>
            </div>
            <div class="schedule-content">
              <div class="course-title">{{ schedule.lessonTopic || schedule.courseName }}</div>
              <div class="course-info">
                <span class="class-name">{{ schedule.className }}</span>
                <span class="location">{{ schedule.classroom }}</span>
                <span class="student-count">{{ schedule.attendanceCount || 0 }}人</span>
              </div>
            </div>
            <div class="schedule-status" :class="schedule.status.toLowerCase()">
              {{ schedule.statusText }}
            </div>
            <div class="schedule-actions">
              <el-dropdown @command="(command) => handleScheduleAction(schedule.id, command)">
                <el-button type="text" size="small">
                  操作<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item 
                    command="PREPARING" 
                    v-if="schedule.status === 'SCHEDULED'"
                  >
                    开始准备
                  </el-dropdown-item>
                  <el-dropdown-item 
                    command="ONGOING" 
                    v-if="schedule.status === 'PREPARING'"
                  >
                    开始上课
                  </el-dropdown-item>
                  <el-dropdown-item 
                    command="COMPLETED" 
                    v-if="schedule.status === 'ONGOING'"
                  >
                    结束课程
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>
        
        <!-- 无数据状态 -->
        <div v-else-if="!scheduleLoading && schedules.length === 0" class="empty-state">
          <i class="el-icon-calendar"></i>
          <p>今日暂无课程安排</p>
        </div>
        
        <!-- 加载状态 -->
        <div v-else-if="scheduleLoading" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>
      </el-card>

      <!-- 快捷操作 -->
      <el-card class="quick-actions-card">
        <div slot="header" class="card-header">
          <i class="el-icon-magic-stick"></i>
          <span>快捷操作</span>
        </div>
        <div class="quick-actions">
          <!-- 主要功能 -->
          <div class="primary-action" @click="$router.push('/ai-lesson')">
            <div class="action-icon">
              <i class="el-icon-magic-stick"></i>
            </div>
            <div class="action-content">
              <div class="action-title">AI一键备课</div>
              <div class="action-desc">智能生成教案、PPT、习题</div>
            </div>
            <div class="action-arrow">
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>
          
          <!-- 次要功能 -->
          <div class="secondary-actions">
            <div class="action-row">
              <div class="mini-action blue" @click="$router.push('/teaching-design')">
                <i class="el-icon-document"></i>
                <span>教学设计</span>
              </div>
              <div class="mini-action green" @click="$router.push('/resources')">
                <i class="el-icon-edit-outline"></i>
                <span>教学资源</span>
              </div>
            </div>
            <div class="action-row">
              <div class="mini-action orange" @click="$router.push('/code-oj')">
                <i class="el-icon-document-copy"></i>
                <span>代码OJ</span>
              </div>
              <div class="mini-action purple" @click="$router.push('/analytics')">
                <i class="el-icon-edit"></i>
                <span>学情分析</span>
              </div>
            </div>
            <div class="action-row">
              <div class="mini-action red" @click="$router.push('/history')">
                <i class="el-icon-data-analysis"></i>
                <span>备课历史</span>
              </div>
              <div class="mini-action cyan" @click="$router.push('/collaboration')">
                <i class="el-icon-collection"></i>
                <span>协作空间</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 学情分析概要 -->
      <el-card class="overview-card">
        <div slot="header" class="card-header">
          <i class="el-icon-data-board"></i>
          <span>学情分析概要</span>
          <el-button type="text" size="small" style="float: right;" @click="$router.push('/analytics')">详细报告</el-button>
        </div>
        
        <div class="overview-content" v-if="!analyticsLoading && analytics">
          <!-- 核心指标 -->
          <div class="core-metrics">
            <div class="metric-item">
              <div class="metric-chart">
                <el-progress 
                  type="circle" 
                  :percentage="getKnowledgeMasteryRate()" 
                  :width="60" 
                  :show-text="false" 
                  color="#67c23a"
                ></el-progress>
                <div class="progress-center">{{ getKnowledgeMasteryRate() }}%</div>
              </div>
              <div class="metric-info">
                <div class="metric-title">知识掌握率</div>
                <div class="metric-desc">优秀 {{ getKnowledgeExcellent() }}人</div>
              </div>
            </div>
            
            <div class="metric-item">
              <div class="metric-chart">
                <el-progress 
                  type="circle" 
                  :percentage="getHomeworkCompletionRate()" 
                  :width="60" 
                  :show-text="false" 
                  color="#409EFF"
                ></el-progress>
                <div class="progress-center">{{ getHomeworkCompletionRate() }}%</div>
              </div>
              <div class="metric-info">
                <div class="metric-title">作业完成率</div>
                <div class="metric-desc">按时 {{ getHomeworkOnTime() }}%</div>
              </div>
            </div>
            
            <div class="metric-item">
              <div class="metric-chart">
                <el-progress 
                  type="circle" 
                  :percentage="getClassParticipationRate()" 
                  :width="60" 
                  :show-text="false" 
                  color="#e6a23c"
                ></el-progress>
                <div class="progress-center">{{ getClassParticipationRate() }}%</div>
              </div>
              <div class="metric-info">
                <div class="metric-title">课堂参与度</div>
                <div class="metric-desc">出勤 {{ getAttendanceRate() }}%</div>
              </div>
            </div>
          </div>

          <!-- 详细数据分布 -->
          <div class="detailed-metrics">
            <!-- 知识掌握分布 -->
            <div class="knowledge-distribution" v-if="analytics.knowledgeMastery">
              <div class="distribution-title">
                <i class="el-icon-trophy"></i>
                知识掌握分布
              </div>
              <div class="distribution-bars">
                <div class="dist-item excellent">
                  <span class="dist-label">优秀</span>
                  <div class="dist-bar">
                    <div class="dist-fill" :style="{ width: getDistributionPercent('excellent') + '%' }"></div>
                  </div>
                  <span class="dist-count">{{ analytics.knowledgeMastery.excellent || 0 }}人</span>
                </div>
                <div class="dist-item good">
                  <span class="dist-label">良好</span>
                  <div class="dist-bar">
                    <div class="dist-fill" :style="{ width: getDistributionPercent('good') + '%' }"></div>
                  </div>
                  <span class="dist-count">{{ analytics.knowledgeMastery.good || 0 }}人</span>
                </div>
                <div class="dist-item average">
                  <span class="dist-label">一般</span>
                  <div class="dist-bar">
                    <div class="dist-fill" :style="{ width: getDistributionPercent('average') + '%' }"></div>
                  </div>
                  <span class="dist-count">{{ analytics.knowledgeMastery.average || 0 }}人</span>
                </div>
                <div class="dist-item needs-improvement">
                  <span class="dist-label">需提升</span>
                  <div class="dist-bar">
                    <div class="dist-fill" :style="{ width: getDistributionPercent('needImprovement') + '%' }"></div>
                  </div>
                  <span class="dist-count">{{ analytics.knowledgeMastery.needImprovement || 0 }}人</span>
                </div>
              </div>
            </div>

            <!-- 需关注学生 -->
            <div class="attention-students" v-if="analytics.studentsNeedAttention">
              <div class="attention-header">
                <i class="el-icon-warning"></i>
                需关注学生 ({{ analytics.studentsNeedAttention.count || 0 }}人)
              </div>
              <div class="attention-reasons" v-if="analytics.studentsNeedAttention.reasons">
                <div 
                  v-for="reason in analytics.studentsNeedAttention.reasons" 
                  :key="reason.reason"
                  class="reason-item"
                >
                  <span class="reason-text">{{ reason.reason }}</span>
                  <span class="reason-count">{{ reason.studentCount }}人</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 班级状态概览 -->
          <div class="class-summary">
            <div class="summary-title">班级学习状态</div>
            <div class="summary-stats">
              <div class="stat-item">
                <span class="stat-number">{{ analytics.totalStudents || 0 }}</span>
                <span class="stat-label">总人数</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ getActiveStudents() }}</span>
                <span class="stat-label">积极参与</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ getStudentsNeedAttention() }}</span>
                <span class="stat-label">需关注</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-else-if="analyticsLoading" class="loading-state">
          <el-skeleton :rows="4" animated />
        </div>

        <!-- 错误状态 -->
        <div v-else-if="!analytics" class="empty-state">
          <i class="el-icon-warning"></i>
          <p>数据加载失败</p>
          <el-button size="small" @click="loadAnalytics">重试</el-button>
        </div>
      </el-card>

      <!-- AI智能推荐 -->
      <el-card class="ai-recommend-card">
        <div slot="header" class="card-header">
          <i class="el-icon-cpu"></i>
          <span>AI智能推荐</span>
          <el-button type="text" size="small" style="float: right;" @click="loadRecommendations">刷新</el-button>
        </div>
        
        <div class="recommend-list" v-if="!recommendLoading && recommendations.length > 0">
          <div 
            v-for="item in recommendations" 
            :key="item.id"
            class="recommend-item"
            @click="handleRecommendationClick(item)"
          >
            <div class="recommend-type">
              <i :class="getRecommendationIcon(item.recommendationType)"></i>
              <span>{{ item.recommendationType }}</span>
            </div>
            <div class="recommend-content">
              <div class="recommend-title">{{ item.title }}</div>
              <div class="recommend-meta">
                <span>评分: {{ item.rating }}</span>
                <span>下载: {{ item.downloadCount }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 无数据状态 -->
        <div v-else-if="!recommendLoading && recommendations.length === 0" class="empty-state">
          <i class="el-icon-cpu"></i>
          <p>暂无推荐内容</p>
        </div>

        <!-- 加载状态 -->
        <div v-else-if="recommendLoading" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>
      </el-card>

      <!-- 课程数据趋势 -->
      <el-card class="trend-card">
        <div slot="header" class="card-header">
          <i class="el-icon-trending-up"></i>
          <span>课程数据趋势</span>
          <el-radio-group v-model="trendPeriod" size="mini" style="float: right;" @change="loadAnalytics">
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
          </el-radio-group>
        </div>
        
        <div class="trend-content" v-if="!analyticsLoading && trendsData">
          <!-- 关键指标总览 -->
          <div class="key-metrics" v-if="trendsData.keyMetrics">
            <div class="metric-grid">
              <div class="key-metric-item">
                <div class="metric-icon">📊</div>
                <div class="metric-info">
                  <div class="metric-value">{{ getKeyMetricValue('completedCourses') }}</div>
                  <div class="metric-label">完成课程</div>
                </div>
              </div>
              <div class="key-metric-item">
                <div class="metric-icon">⏰</div>
                <div class="metric-info">
                  <div class="metric-value">{{ getKeyMetricValue('studyHours') }}h</div>
                  <div class="metric-label">学习时长</div>
                </div>
              </div>
              <div class="key-metric-item">
                <div class="metric-icon">👥</div>
                <div class="metric-info">
                  <div class="metric-value">{{ getKeyMetricValue('activeStudents') }}</div>
                  <div class="metric-label">活跃学生</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 本周数据 -->
          <div class="period-data" v-if="trendPeriod === 'week' && trendsData.weekly">
            <div class="period-title">📅 本周数据</div>
            
            <!-- 课程完成情况 -->
            <div class="data-section" v-if="trendsData.weekly.classProgress">
              <div class="section-header">
                <span class="section-title">课程完成情况</span>
                <span class="section-value">{{ getWeeklyProgress() }}%</span>
              </div>
              <div class="progress-visual">
                <div class="progress-bar-visual">
                  <div 
                    class="progress-fill-visual" 
                    :style="{ width: getWeeklyProgress() + '%' }"
                  ></div>
                </div>
              </div>
            </div>

            <!-- 作业趋势 -->
            <div class="data-section" v-if="trendsData.weekly.homeworkTrends">
              <div class="section-header">
                <span class="section-title">作业完成趋势</span>
                <span class="section-trend positive" v-if="getHomeworkTrend() > 0">↗ {{ getHomeworkTrend() }}%</span>
                <span class="section-trend negative" v-else-if="getHomeworkTrend() < 0">↘ {{ Math.abs(getHomeworkTrend()) }}%</span>
                <span class="section-trend neutral" v-else>→ 持平</span>
              </div>
            </div>
          </div>

          <!-- 本月数据 -->
          <div class="period-data" v-if="trendPeriod === 'month' && trendsData.monthly">
            <div class="period-title">📅 本月数据</div>
            
            <!-- 教学效果 -->
            <div class="data-section" v-if="trendsData.monthly.teachingEffectiveness">
              <div class="section-header">
                <span class="section-title">教学效果评估</span>
                <span class="section-value">{{ getTeachingEffectiveness() }}分</span>
              </div>
            </div>

            <!-- 学生表现 -->
            <div class="data-section" v-if="trendsData.monthly.studentPerformance">
              <div class="section-header">
                <span class="section-title">学生表现</span>
                <span class="section-value">{{ getStudentPerformance() }}</span>
              </div>
            </div>

            <!-- 学科质量概览 -->
            <div class="data-section" v-if="trendsData.monthly.subjectQuality">
              <div class="section-header">
                <span class="section-title">学科表现</span>
              </div>
              <div class="subject-quality">
                <div 
                  v-for="(subject, name) in trendsData.monthly.subjectQuality"
                  :key="name"
                  class="subject-item"
                >
                  <span class="subject-name">{{ name }}</span>
                  <span class="subject-score">{{ getLatestSubjectScore(subject) }}分</span>
                  <span class="subject-trend" :class="getSubjectTrendClass(subject)">
                    {{ getSubjectTrendText(subject) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 教学目标达成 -->
            <div class="data-section" v-if="trendsData.monthly.teachingGoals">
              <div class="section-header">
                <span class="section-title">教学目标达成</span>
              </div>
              <div class="teaching-goals">
                <div 
                  v-for="goal in trendsData.monthly.teachingGoals"
                  :key="goal.goalType"
                  class="goal-item"
                >
                  <span class="goal-name">{{ goal.goalType }}</span>
                  <div class="goal-progress">
                    <div class="goal-bar">
                      <div 
                        class="goal-fill"
                        :style="{ width: goal.completionRate + '%' }"
                      ></div>
                    </div>
                    <span class="goal-percent">{{ goal.completionRate }}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 教学关注点 -->
          <div class="teaching-concerns" v-if="trendsData.teachingConcerns && trendsData.teachingConcerns.length > 0">
            <div class="concerns-title">⚠️ 需要关注</div>
            <div class="concerns-list">
              <div 
                v-for="(concern, index) in trendsData.teachingConcerns.slice(0, 3)" 
                :key="index"
                class="concern-item"
              >
                <span class="concern-text">{{ concern.issue || concern.description || concern }}</span>
                <span class="concern-priority" :class="getConcernPriorityClass(concern)">
                  {{ concern.priority || '中' }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-else-if="analyticsLoading" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>

        <!-- 无数据状态 -->
        <div v-else class="empty-state">
          <i class="el-icon-data-line"></i>
          <p>暂无趋势数据</p>
        </div>
      </el-card>

      <!-- 待办提醒 -->
      <el-card class="todo-card">
        <div slot="header" class="card-header">
          <i class="el-icon-bell"></i>
          <span>待办提醒</span>
          <el-badge :value="urgentTodos" class="todo-badge" v-if="urgentTodos > 0"></el-badge>
          <el-button type="text" size="small" style="float: right;" @click="loadTodos">刷新</el-button>
        </div>
        
        <div class="todo-list" v-if="!todoLoading && todos.length > 0">
          <div 
            v-for="todo in todos.slice(0, 5)" 
            :key="todo.id"
            class="todo-item"
            :class="todo.priority.toLowerCase()"
          >
            <div class="todo-priority"></div>
            <div class="todo-content">
              <div class="todo-title">{{ todo.title }}</div>
              <div class="todo-time">{{ formatTodoTime(todo.dueDate) }}</div>
            </div>
            <div class="todo-action">
              <el-button size="mini" @click="completeTodo(todo.id)">完成</el-button>
            </div>
          </div>
        </div>

        <!-- 无数据状态 -->
        <div v-else-if="!todoLoading && todos.length === 0" class="empty-state">
          <i class="el-icon-check"></i>
          <p>暂无待办事项</p>
        </div>

        <!-- 加载状态 -->
        <div v-else-if="todoLoading" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { scheduleAPI, analyticsAPI, todoAPI, recommendationAPI } from '@/api'
import '@/styles/dashboard.css'
import '@/styles/dashboard-components.css'
import '@/styles/dashboard-extended.css'

export default {
  name: 'Dashboard',
  data() {
    return {
      // 数据状态
      schedules: [],
      analytics: null,
      trendsData: null,
      todos: [],
      recommendations: [],
      
      // 加载状态
      loading: false,
      scheduleLoading: false,
      analyticsLoading: false,
      todoLoading: false,
      recommendLoading: false,
      
      // 其他状态
      trendPeriod: 'week',
      currentDate: this.formatDate(new Date())
    }
  },
  
  computed: {
    urgentTodos() {
      return this.todos.filter(todo => todo.priority === 'URGENT').length
    }
  },
  
  async created() {
    await this.loadDashboardData()
  },
  
  methods: {
    // 加载所有仪表板数据
    async loadDashboardData() {
      this.loading = true
      try {
        await Promise.all([
          this.loadSchedules(),
          this.loadAnalytics(),
          this.loadTodos(),
          this.loadRecommendations()
        ])
      } catch (error) {
        console.error('加载仪表板数据失败:', error)
        this.$message.error('加载数据失败，请刷新页面重试')
      } finally {
        this.loading = false
      }
    },

    // 加载今日课程安排
    async loadSchedules() {
      this.scheduleLoading = true
      try {
        const result = await scheduleAPI.getTodaySchedules()
        this.schedules = result.data || []
      } catch (error) {
        console.error('加载课程安排失败:', error)
        this.$message.error('获取课程安排失败')
        this.schedules = []
      } finally {
        this.scheduleLoading = false
      }
    },

    // 加载学情分析
    async loadAnalytics() {
      this.analyticsLoading = true
      try {
        // 同时获取概览和趋势数据
        const [overviewResult, trendsResult] = await Promise.all([
          analyticsAPI.getOverview(),
          analyticsAPI.getTrends(this.trendPeriod)
        ])
        
        this.analytics = overviewResult.data
        this.trendsData = trendsResult.data
        
        console.log('学情分析数据:', this.analytics)
        console.log('课程趋势数据:', this.trendsData)
        console.log('时间分布数据:', this.trendsData && this.trendsData.timeDistribution)
      } catch (error) {
        console.error('加载学情分析失败:', error)
        this.$message.error('获取学情分析失败')
        this.analytics = null
        this.trendsData = null
      } finally {
        this.analyticsLoading = false
      }
    },

    // 加载待办事项
    async loadTodos() {
      this.todoLoading = true
      try {
        const result = await todoAPI.getTodos()
        this.todos = (result.data || []).filter(todo => todo.status !== 'COMPLETED')
      } catch (error) {
        console.error('加载待办事项失败:', error)
        this.$message.error('获取待办事项失败')
        this.todos = []
      } finally {
        this.todoLoading = false
      }
    },

    // 加载AI推荐
    async loadRecommendations() {
      this.recommendLoading = true
      try {
        const result = await recommendationAPI.getRecommendations()
        this.recommendations = result.data || []
      } catch (error) {
        console.error('加载推荐内容失败:', error)
        this.$message.error('获取推荐内容失败')
        this.recommendations = []
      } finally {
        this.recommendLoading = false
      }
    },

    // 处理课程状态操作
    async handleScheduleAction(scheduleId, action) {
      try {
        await scheduleAPI.updateScheduleStatus(scheduleId, action)
        this.$message.success('状态更新成功')
        await this.loadSchedules() // 重新加载数据
      } catch (error) {
        console.error('更新课程状态失败:', error)
        this.$message.error('状态更新失败')
      }
    },

    // 完成待办事项
    async completeTodo(todoId) {
      try {
        await todoAPI.updateTodo(todoId, { status: 'COMPLETED' })
        this.$message.success('待办事项已完成')
        await this.loadTodos() // 重新加载数据
      } catch (error) {
        console.error('完成待办事项失败:', error)
        this.$message.error('操作失败')
      }
    },

    // 处理推荐点击
    async handleRecommendationClick(recommendation) {
      try {
        await recommendationAPI.recordRecommendationClick(recommendation.id)
        // 可以在这里添加跳转逻辑
        this.$message.success(`已记录对"${recommendation.title}"的点击`)
      } catch (error) {
        console.error('记录推荐点击失败:', error)
      }
    },

    // 工具方法
    formatDate(date) {
      const options = { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit',
        weekday: 'long'
      }
      return date.toLocaleDateString('zh-CN', options)
    },

    calculateDuration(startTime, endTime) {
      const start = new Date(startTime)
      const end = new Date(endTime)
      return Math.floor((end - start) / (1000 * 60))
    },

    formatTodoTime(dueDate) {
      if (!dueDate) return ''
      const date = new Date(dueDate)
      const now = new Date()
      const diffHours = Math.floor((date - now) / (1000 * 60 * 60))
      
      if (diffHours < 0) return '已过期'
      if (diffHours < 24) return `${diffHours}小时后`
      const diffDays = Math.floor(diffHours / 24)
      return `${diffDays}天后`
    },

    getRecommendationIcon(type) {
      const iconMap = {
        '教学资源': 'el-icon-document',
        '课程': 'el-icon-video-play',
        '习题': 'el-icon-edit',
        '学习路径': 'el-icon-guide',
        '默认': 'el-icon-star-on'
      }
      return iconMap[type] || iconMap['默认']
    },

    // 安全访问嵌套属性的辅助方法
    safeGet(obj, path, defaultValue = null) {
      return path.split('.').reduce((current, key) => {
        return current && current[key] !== undefined ? current[key] : defaultValue
      }, obj)
    },

    // 获取最新完成课程数
    getLatestCompletedCourses() {
      if (!this.trendsData || !this.trendsData.progressTrend || this.trendsData.progressTrend.length === 0) {
        return 0
      }
      const latest = this.trendsData.progressTrend[this.trendsData.progressTrend.length - 1]
      return latest.completedCourses || 0
    },

    // 获取最新学习时长
    getLatestStudyHours() {
      if (!this.trendsData || !this.trendsData.progressTrend || this.trendsData.progressTrend.length === 0) {
        return 0
      }
      const latest = this.trendsData.progressTrend[this.trendsData.progressTrend.length - 1]
      return Math.round((latest.studyHours || 0) * 10) / 10 // 保留一位小数
    },

    // 获取学科最新分数
    getLatestSubjectScore(subject) {
      if (!subject || subject.length === 0) return 0
      const latest = subject[subject.length - 1]
      return Math.round(latest.avgScore || 0)
    },

    // 获取学科趋势样式类
    getSubjectTrendClass(subject) {
      if (!subject || subject.length === 0) return 'trend-neutral'
      const latest = subject[subject.length - 1]
      const improvement = latest.improvement || 0
      if (improvement > 0) return 'trend-up'
      if (improvement < 0) return 'trend-down'
      return 'trend-neutral'
    },

    // 获取学科趋势文本
    getSubjectTrendText(subject) {
      if (!subject || subject.length === 0) return '无数据'
      const latest = subject[subject.length - 1]
      const improvement = latest.improvement || 0
      if (improvement > 0) return `↗ +${Math.round(improvement * 10) / 10}`
      if (improvement < 0) return `↙ ${Math.round(improvement * 10) / 10}`
      return '→ 持平'
    },

    // 获取难度样式类
    getDifficultyClass(level) {
      const classMap = {
        '初级': 'difficulty-easy',
        '中级': 'difficulty-medium',
        '高级': 'difficulty-hard'
      }
      return classMap[level] || 'difficulty-neutral'
    },

    // 获取难度百分比
    getDifficultyPercent(count) {
      if (!this.trendsData || !this.trendsData.difficultyDistribution) return 0
      const total = Object.values(this.trendsData.difficultyDistribution).reduce((sum, val) => sum + val, 0)
      return total > 0 ? Math.round((count / total) * 100) : 0
    },

    // 获取知识掌握率
    getKnowledgeMasteryRate() {
      return this.safeGet(this.analytics, 'knowledgeMastery.overall') || 
             this.safeGet(this.analytics, 'knowledgeMasteryRate') || 0
    },

    // 获取知识掌握优秀人数
    getKnowledgeExcellent() {
      return this.safeGet(this.analytics, 'knowledgeMastery.excellent') || 0
    },

    // 获取作业完成率
    getHomeworkCompletionRate() {
      return this.safeGet(this.analytics, 'homeworkCompletion.overall') || 
             this.safeGet(this.analytics, 'assignmentCompletionRate') || 0
    },

    // 获取作业按时完成率
    getHomeworkOnTime() {
      return this.safeGet(this.analytics, 'homeworkCompletion.onTime') || 0
    },

    // 获取课堂参与度
    getClassParticipationRate() {
      return this.safeGet(this.analytics, 'classParticipation.overall') || 
             this.safeGet(this.analytics, 'classParticipationRate') || 0
    },

    // 获取出勤率
    getAttendanceRate() {
      return this.safeGet(this.analytics, 'classParticipation.attendanceRate') || 0
    },

    // 获取积极参与学生数
    getActiveStudents() {
      return this.safeGet(this.analytics, 'classParticipation.activeStudents') || 
             this.safeGet(this.analytics, 'todayData.activeLearners') || 0
    },

    // 获取需关注学生数
    getStudentsNeedAttention() {
      return this.safeGet(this.analytics, 'studentsNeedAttention.count') || 0
    },

    // 获取知识掌握分布百分比
    getDistributionPercent(level) {
      if (!this.analytics || !this.analytics.knowledgeMastery) return 0
      const total = this.analytics.totalStudents || 
                   (this.analytics.knowledgeMastery.excellent + 
                    this.analytics.knowledgeMastery.good + 
                    this.analytics.knowledgeMastery.average + 
                    this.analytics.knowledgeMastery.needImprovement) || 1
      const count = this.analytics.knowledgeMastery[level] || 0
      return Math.round((count / total) * 100)
    },

    // 获取活跃学习者总数
    getTotalActiveLearners() {
      if (!this.trendsData || !this.trendsData.weekly || !this.trendsData.weekly.participationAnalysis) {
        console.warn('participationAnalysis数据不存在:', this.trendsData)
        return 0
      }
      
      return this.trendsData.weekly.participationAnalysis.activeStudents || 0
    },

    // 获取关键指标值
    getKeyMetricValue(metric) {
      if (!this.trendsData || !this.trendsData.keyMetrics) return 0
      
      // 映射前端显示名称到后端字段名
      const metricMap = {
        'completedCourses': 'learningProgress',
        'studyHours': 'teachingEfficiency', 
        'activeStudents': 'classEngagement'
      }
      
      const backendField = metricMap[metric] || metric
      return this.trendsData.keyMetrics[backendField] || 0
    },

    // 获取本周进度
    getWeeklyProgress() {
      if (!this.trendsData || !this.trendsData.weekly || !this.trendsData.weekly.classProgress) return 0
      
      // 计算本周课程完成率
      const classProgress = this.trendsData.weekly.classProgress
      const totalScheduled = classProgress.reduce((sum, day) => sum + (day.scheduledClasses || 0), 0)
      const totalCompleted = classProgress.reduce((sum, day) => sum + (day.completedClasses || 0), 0)
      
      return totalScheduled > 0 ? Math.round((totalCompleted / totalScheduled) * 100) : 0
    },

    // 获取作业趋势
    getHomeworkTrend() {
      if (!this.trendsData || !this.trendsData.weekly || !this.trendsData.weekly.homeworkTrends) return 0
      
      const trends = this.trendsData.weekly.homeworkTrends
      if (trends.length < 2) return 0
      
      // 计算最近几天的完成率趋势
      const recent = trends.slice(-3).filter(day => day.completionRate > 0)
      if (recent.length < 2) return 0
      
      const latest = recent[recent.length - 1].completionRate
      const previous = recent[recent.length - 2].completionRate
      
      return Math.round(latest - previous)
    },

    // 获取教学效果
    getTeachingEffectiveness() {
      if (!this.trendsData || !this.trendsData.monthly || !this.trendsData.monthly.teachingEffectiveness) return 0
      
      const effectiveness = this.trendsData.monthly.teachingEffectiveness
      if (effectiveness.length === 0) return 0
      
      // 取最新一周的平均课堂评分
      const latest = effectiveness[effectiveness.length - 1]
      return Math.round(latest.avgClassScore || 0)
    },

    // 获取学生表现
    getStudentPerformance() {
      if (!this.trendsData || !this.trendsData.monthly || !this.trendsData.monthly.studentPerformance) return ''
      
      const performance = this.trendsData.monthly.studentPerformance
      return `${performance.excellentStudents || 0}人优秀`
    },

    // 获取教学关注点优先级样式
    getConcernPriorityClass(concern) {
      const priorityMap = {
        '高': 'high',
        '中': 'medium', 
        '低': 'low'
      }
      return priorityMap[concern.priority] || 'medium'
    }
  }
}
</script>