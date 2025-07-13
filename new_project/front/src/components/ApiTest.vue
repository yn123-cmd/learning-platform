<template>
  <div class="api-test">
    <h1>API 连接测试</h1>
    
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 课程安排API测试 -->
      <el-tab-pane label="课程安排" name="schedule">
        <div class="test-section">
          <h3>今日课程安排</h3>
          <el-button @click="testTodaySchedules" :loading="loading.schedule">
            获取今日课程安排
          </el-button>
          <div v-if="results.schedules" class="result-container">
            <h4>响应结果：</h4>
            <pre>{{ JSON.stringify(results.schedules, null, 2) }}</pre>
          </div>
          
          <h3>更新课程状态</h3>
          <el-row :gutter="10">
            <el-col :span="8">
              <el-input-number v-model="testData.scheduleId" placeholder="课程ID" :min="1"></el-input-number>
            </el-col>
            <el-col :span="8">
              <el-select v-model="testData.scheduleStatus" placeholder="选择状态">
                <el-option label="已安排" value="SCHEDULED"></el-option>
                <el-option label="准备中" value="PREPARING"></el-option>
                <el-option label="进行中" value="ONGOING"></el-option>
                <el-option label="已完成" value="COMPLETED"></el-option>
                <el-option label="已取消" value="CANCELLED"></el-option>
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-button @click="testUpdateScheduleStatus" :loading="loading.schedule">
                更新状态
              </el-button>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>
      
      <!-- 学情分析API测试 -->
      <el-tab-pane label="学情分析" name="analytics">
        <div class="test-section">
          <h3>学情分析概览</h3>
          <el-button @click="testAnalyticsOverview" :loading="loading.analytics">
            获取学情分析概览
          </el-button>
          <div v-if="results.analytics" class="result-container">
            <h4>响应结果：</h4>
            <pre>{{ JSON.stringify(results.analytics, null, 2) }}</pre>
          </div>
        </div>
      </el-tab-pane>
      
      <!-- 待办事项API测试 -->
      <el-tab-pane label="待办事项" name="todo">
        <div class="test-section">
          <h3>获取待办列表</h3>
          <el-button @click="testGetTodos" :loading="loading.todo">
            获取待办列表
          </el-button>
          <div v-if="results.todos" class="result-container">
            <h4>响应结果：</h4>
            <pre>{{ JSON.stringify(results.todos, null, 2) }}</pre>
          </div>
          
          <h3>创建待办事项</h3>
          <el-form :model="newTodo" label-width="80px" size="small">
            <el-form-item label="标题">
              <el-input v-model="newTodo.title" placeholder="输入待办标题"></el-input>
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="newTodo.description" placeholder="输入描述"></el-input>
            </el-form-item>
            <el-form-item label="优先级">
              <el-select v-model="newTodo.priority">
                <el-option label="紧急" value="URGENT"></el-option>
                <el-option label="重要" value="IMPORTANT"></el-option>
                <el-option label="普通" value="NORMAL"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="类别">
              <el-input v-model="newTodo.category" placeholder="输入类别"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button @click="testCreateTodo" :loading="loading.todo" type="primary">
                创建待办
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      
      <!-- AI推荐API测试 -->
      <el-tab-pane label="AI推荐" name="recommendation">
        <div class="test-section">
          <h3>获取推荐内容</h3>
          <el-button @click="testGetRecommendations" :loading="loading.recommendation">
            获取推荐内容
          </el-button>
          <div v-if="results.recommendations" class="result-container">
            <h4>响应结果：</h4>
            <pre>{{ JSON.stringify(results.recommendations, null, 2) }}</pre>
          </div>
        </div>
      </el-tab-pane>
      
      <!-- 用户认证API测试 -->
      <el-tab-pane label="用户认证" name="auth">
        <div class="test-section">
          <h3>获取当前用户信息</h3>
          <el-button @click="testGetCurrentUser" :loading="loading.auth">
            获取当前用户
          </el-button>
          <div v-if="results.user" class="result-container">
            <h4>响应结果：</h4>
            <pre>{{ JSON.stringify(results.user, null, 2) }}</pre>
          </div>
          
          <h3>验证Token</h3>
          <el-button @click="testValidateToken" :loading="loading.auth">
            验证Token
          </el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 错误信息显示 -->
    <div v-if="error" class="error-container">
      <h4>错误信息：</h4>
      <pre>{{ error }}</pre>
    </div>
  </div>
</template>

<script>
import { scheduleAPI, analyticsAPI, todoAPI, recommendationAPI, authApi } from '@/api'

export default {
  name: 'ApiTest',
  data() {
    return {
      activeTab: 'schedule',
      loading: {
        schedule: false,
        analytics: false,
        todo: false,
        recommendation: false,
        auth: false
      },
      results: {
        schedules: null,
        analytics: null,
        todos: null,
        recommendations: null,
        user: null
      },
      testData: {
        scheduleId: 1,
        scheduleStatus: 'PREPARING'
      },
      newTodo: {
        title: '',
        description: '',
        priority: 'NORMAL',
        category: '教学',
        dueDate: new Date().toISOString().slice(0, 16)
      },
      error: null
    }
  },
  
  methods: {
    // 清除错误信息
    clearError() {
      this.error = null
    },
    
    // 显示错误信息
    showError(error) {
      this.error = error.message || error.toString()
      console.error('API测试错误:', error)
    },
    
    // 测试今日课程安排
    async testTodaySchedules() {
      this.clearError()
      this.loading.schedule = true
      try {
        const result = await scheduleAPI.getTodaySchedules()
        this.results.schedules = result
        this.$message.success('获取今日课程安排成功')
      } catch (error) {
        this.showError(error)
        this.$message.error('获取今日课程安排失败')
      } finally {
        this.loading.schedule = false
      }
    },
    
    // 测试更新课程状态
    async testUpdateScheduleStatus() {
      if (!this.testData.scheduleId || !this.testData.scheduleStatus) {
        this.$message.warning('请输入课程ID和状态')
        return
      }
      
      this.clearError()
      this.loading.schedule = true
      try {
        const result = await scheduleAPI.updateScheduleStatus(
          this.testData.scheduleId, 
          this.testData.scheduleStatus
        )
        this.results.schedules = result
        this.$message.success('更新课程状态成功')
      } catch (error) {
        this.showError(error)
        this.$message.error('更新课程状态失败')
      } finally {
        this.loading.schedule = false
      }
    },
    
    // 测试学情分析概览
    async testAnalyticsOverview() {
      this.clearError()
      this.loading.analytics = true
      try {
        const result = await analyticsAPI.getOverview()
        this.results.analytics = result
        this.$message.success('获取学情分析概览成功')
      } catch (error) {
        this.showError(error)
        this.$message.error('获取学情分析概览失败')
      } finally {
        this.loading.analytics = false
      }
    },
    
    // 测试获取待办列表
    async testGetTodos() {
      this.clearError()
      this.loading.todo = true
      try {
        const result = await todoAPI.getTodos()
        this.results.todos = result
        this.$message.success('获取待办列表成功')
      } catch (error) {
        this.showError(error)
        this.$message.error('获取待办列表失败')
      } finally {
        this.loading.todo = false
      }
    },
    
    // 测试创建待办事项
    async testCreateTodo() {
      if (!this.newTodo.title) {
        this.$message.warning('请输入待办标题')
        return
      }
      
      this.clearError()
      this.loading.todo = true
      try {
        const todoData = {
          ...this.newTodo,
          dueDate: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString() // 明天
        }
        const result = await todoAPI.createTodo(todoData)
        this.results.todos = result
        this.$message.success('创建待办事项成功')
        
        // 清空表单
        this.newTodo = {
          title: '',
          description: '',
          priority: 'NORMAL',
          category: '教学'
        }
      } catch (error) {
        this.showError(error)
        this.$message.error('创建待办事项失败')
      } finally {
        this.loading.todo = false
      }
    },
    
    // 测试获取推荐内容
    async testGetRecommendations() {
      this.clearError()
      this.loading.recommendation = true
      try {
        const result = await recommendationAPI.getRecommendations()
        this.results.recommendations = result
        this.$message.success('获取推荐内容成功')
      } catch (error) {
        this.showError(error)
        this.$message.error('获取推荐内容失败')
      } finally {
        this.loading.recommendation = false
      }
    },
    
    // 测试获取当前用户
    async testGetCurrentUser() {
      this.clearError()
      this.loading.auth = true
      try {
        const result = await authApi.getCurrentUser()
        this.results.user = result
        this.$message.success('获取当前用户信息成功')
      } catch (error) {
        this.showError(error)
        this.$message.error('获取当前用户信息失败')
      } finally {
        this.loading.auth = false
      }
    },
    
    // 测试验证Token
    async testValidateToken() {
      this.clearError()
      this.loading.auth = true
      try {
        const result = await authApi.validateToken()
        this.results.user = result
        this.$message.success('Token验证成功')
      } catch (error) {
        this.showError(error)
        this.$message.error('Token验证失败')
      } finally {
        this.loading.auth = false
      }
    }
  }
}
</script>

<style scoped>
.api-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-section {
  padding: 20px;
}

.test-section h3 {
  margin-bottom: 15px;
  color: #2c3e50;
  border-bottom: 2px solid #ecf0f1;
  padding-bottom: 8px;
}

.result-container {
  margin-top: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
}

.result-container h4 {
  margin-bottom: 10px;
  color: #2c3e50;
}

.result-container pre {
  background: white;
  border: 1px solid #e0e6ed;
  border-radius: 4px;
  padding: 15px;
  font-size: 12px;
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.error-container {
  margin-top: 20px;
  background: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 8px;
  padding: 15px;
}

.error-container h4 {
  margin-bottom: 10px;
  color: #f56c6c;
}

.error-container pre {
  background: white;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
  padding: 15px;
  font-size: 12px;
  color: #f56c6c;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.el-form {
  max-width: 500px;
}

.el-row {
  margin-bottom: 15px;
}
</style> 