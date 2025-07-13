<template>
  <div class="teaching-design-page">
    <div class="page-header">
      <h1>教学设计工作台</h1>
      <p>智能化教学设计流程，助您打造高质量课程</p>
    </div>
    
    <!-- 步骤导航 -->
    <div class="design-steps">
      <el-steps :active="currentStep - 1" align-center>
        <el-step 
          title="课程定位" 
          description="建立教学框架"
          icon="el-icon-position"
          @click.native="goToStep(1)"
          :class="{ clickable: true }"
        />
        <el-step 
          title="目标生成" 
          description="制定学习目标"
          icon="el-icon-aim"
          @click.native="goToStep(2)"
          :class="{ clickable: currentStep >= 1 }"
        />
        <el-step 
          title="重难点" 
          description="识别关键内容"
          icon="el-icon-star-on"
          @click.native="goToStep(3)"
          :class="{ clickable: currentStep >= 2 }"
        />
        <el-step 
          title="学情配置" 
          description="分析学生情况"
          icon="el-icon-user"
          @click.native="goToStep(4)"
          :class="{ clickable: currentStep >= 3 }"
        />
        <el-step 
          title="课时规划" 
          description="安排教学进度"
          icon="el-icon-date"
          @click.native="goToStep(5)"
          :class="{ clickable: currentStep >= 4 }"
        />
        <el-step 
          title="方案输出" 
          description="生成教学方案"
          icon="el-icon-download"
          @click.native="goToStep(6)"
          :class="{ clickable: currentStep >= 5 }"
        />
      </el-steps>
    </div>
    
    <!-- 当前步骤内容区域 -->
    <div class="step-content">
      <router-view @step-completed="updateStepStatus" @step-uncompleted="updateStepUncompleted" />
    </div>
    
    <!-- 导航按钮 -->
    <div class="navigation-buttons">
      <el-button 
        v-if="currentStep > 1" 
        @click="prevStep" 
        size="large"
        icon="el-icon-arrow-left"
      >
        上一步
      </el-button>
      
      <el-button 
        v-if="currentStep < 6" 
        type="primary" 
        @click="nextStep"
        size="large"
        :disabled="!isStepComplete"
      >
        {{ isStepComplete ? '下一步' : '请完成当前步骤' }}
        <i class="el-icon-arrow-right"></i>
      </el-button>
      
      <el-button 
        v-else 
        type="success" 
        @click="exportDesign"
        size="large"
        icon="el-icon-download"
      >
        导出教学方案
      </el-button>
    </div>
    
    <!-- 进度保存提示 -->
    <div class="progress-info">
      <el-alert
        title="进度自动保存"
        description="您的教学设计进度将自动保存，可随时返回继续编辑"
        type="info"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script>
export default {
  name: 'TeachingDesign',
  data() {
    return {
      stepRoutes: [
        '/teaching-design/course-positioning',
        '/teaching-design/goal-generation', 
        '/teaching-design/key-points',
        '/teaching-design/student-config',
        '/teaching-design/lesson-planning',
        '/teaching-design/output'
      ],
      stepCompletionStatus: {
        1: false,
        2: false,
        3: false,
        4: false,
        5: false,
        6: false
      }
    }
  },
  computed: {
    currentStep() {
      const currentRoute = this.$route.path
      const stepIndex = this.stepRoutes.findIndex(route => currentRoute.includes(route))
      return stepIndex !== -1 ? stepIndex + 1 : 1
    },
    isStepComplete() {
      return this.stepCompletionStatus[this.currentStep]
    }
  },
  methods: {
    goToStep(step) {
      // 检查是否可以跳转到该步骤
      if (step > this.currentStep + 1) {
        this.$message.warning('请按顺序完成各步骤')
        return
      }
      
      const targetRoute = this.stepRoutes[step - 1]
      if (targetRoute) {
        this.$router.push(targetRoute)
      }
    },
    
    nextStep() {
      if (!this.isStepComplete) {
        this.$message.warning('请先完成当前步骤的必填内容')
        return
      }
      
      if (this.currentStep < 6) {
        this.goToStep(this.currentStep + 1)
      }
    },
    
    prevStep() {
      if (this.currentStep > 1) {
        this.goToStep(this.currentStep - 1)
      }
    },
    
    exportDesign() {
      this.$message.success('教学方案导出功能开发中...')
      // 实现导出逻辑
    },
    
    // 更新步骤完成状态
    updateStepStatus(step, completed = true) {
      console.log('接收到步骤完成事件:', step, '完成状态:', completed)
      this.stepCompletionStatus[step] = completed
      this.saveProgress()
      console.log('步骤完成状态已更新:', this.stepCompletionStatus)
    },
    
    // 更新步骤未完成状态
    updateStepUncompleted(step) {
      this.stepCompletionStatus[step] = false
      this.saveProgress()
    },
    
    // 保存进度到本地存储
    saveProgress() {
      localStorage.setItem('teachingDesignProgress', JSON.stringify({
        currentStep: this.currentStep,
        completionStatus: this.stepCompletionStatus,
        lastSaved: new Date().toISOString()
      }))
    },
    
    // 恢复进度
    restoreProgress() {
      const savedProgress = localStorage.getItem('teachingDesignProgress')
      if (savedProgress) {
        const progress = JSON.parse(savedProgress)
        this.stepCompletionStatus = { ...this.stepCompletionStatus, ...progress.completionStatus }
      }
    }
  },
  
  mounted() {
    // 恢复之前的进度
    this.restoreProgress()
    
    // 如果当前路由是主路由，重定向到第一步
    if (this.$route.path === '/teaching-design') {
      this.$router.replace('/teaching-design/course-positioning')
    }
  },
  
  // 监听子组件的完成状态更新
  created() {
    // 子组件事件现在通过 router-view 的 @event 监听
  }
}
</script>

<style scoped>
.teaching-design-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 140px);
}

.page-header {
  margin-bottom: 40px;
  text-align: center;
}

.page-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 28px;
  font-weight: 700;
}

.page-header p {
  color: #606266;
  font-size: 16px;
}

.design-steps {
  margin-bottom: 40px;
  padding: 30px;
  background: #f8fafc;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.design-steps .el-steps {
  max-width: 100%;
}

.step-content {
  min-height: 500px;
  margin-bottom: 30px;
}

.navigation-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.navigation-buttons .el-button {
  min-width: 120px;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

.progress-info {
  margin-top: 20px;
}

/* 步骤点击样式 */
.clickable {
  cursor: pointer;
}

.clickable:hover {
  opacity: 0.8;
}

/* 自定义步骤条样式 */
.design-steps ::v-deep .el-step__head {
  transition: all 0.3s ease;
}

.design-steps ::v-deep .el-step__head:hover {
  transform: scale(1.05);
}

.design-steps ::v-deep .el-step__title {
  font-weight: 600;
  font-size: 14px;
}

.design-steps ::v-deep .el-step__description {
  font-size: 12px;
  color: #909399;
}

.design-steps ::v-deep .el-step.is-process .el-step__head {
  color: #409EFF;
  border-color: #409EFF;
}

.design-steps ::v-deep .el-step.is-finish .el-step__head {
  color: #67c23a;
  border-color: #67c23a;
}

.design-steps ::v-deep .el-step.is-finish .el-step__title {
  color: #67c23a;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .teaching-design-page {
    padding: 15px;
  }
  
  .design-steps {
    padding: 20px 10px;
  }
  
  .design-steps ::v-deep .el-step__title {
    font-size: 12px;
  }
  
  .design-steps ::v-deep .el-step__description {
    display: none;
  }
  
  .navigation-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .navigation-buttons .el-button {
    width: 200px;
  }
}
</style> 