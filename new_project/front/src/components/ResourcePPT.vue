<template>
  <div class="resource-ppt">
    <div class="page-header">
      <h2>PPT生成功能升级</h2>
      <p>我们推出了全新的AI智能PPT生成器！</p>
    </div>
    
    <div class="upgrade-notice">
      <el-card shadow="hover" class="upgrade-card">
        <div class="upgrade-content">
          <i class="el-icon-rocket upgrade-icon"></i>
          <h3>🎉 全新AI智能PPT生成器已上线！</h3>
          <p>基于您的完整教学设计数据，AI自动生成个性化教学PPT</p>
          
          <div class="features-list">
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>智能整合教学设计6步骤数据</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>根据学情分析个性化定制</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>3分钟生成专业级PPT</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-check"></i>
              <span>支持多种学习风格适配</span>
            </div>
          </div>
          
          <div class="upgrade-actions">
            <el-button type="primary" size="large" @click="goToAiGenerator">
              <i class="el-icon-right"></i>
              立即体验AI PPT生成器
            </el-button>
            <el-button size="large" @click="learnMore">
              <i class="el-icon-question"></i>
              了解更多功能
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ResourcePPT',
  methods: {
    goToAiGenerator() {
      this.$router.push('/ai-ppt-generator')
    },
    
    learnMore() {
      this.$message.info('功能介绍页面开发中...')
    }
  }
}
</script>

<style scoped>
.resource-ppt {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-header h2 {
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.page-header p {
  color: #606266;
  font-size: 16px;
  margin: 0;
}

.upgrade-notice {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 500px;
}

.upgrade-card {
  max-width: 600px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.upgrade-content {
  text-align: center;
  padding: 40px;
}

.upgrade-icon {
  font-size: 64px;
  color: #67C23A;
  margin-bottom: 20px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.upgrade-content h3 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 24px;
  font-weight: 600;
}

.upgrade-content p {
  color: #606266;
  margin-bottom: 30px;
  font-size: 16px;
  line-height: 1.6;
}

.features-list {
  margin-bottom: 30px;
  text-align: left;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 15px;
  color: #303133;
}

.feature-item i {
  color: #67C23A;
  font-size: 16px;
  margin-right: 10px;
  font-weight: bold;
}

.upgrade-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: center;
}

.upgrade-actions .el-button {
  min-width: 200px;
}

@media (min-width: 768px) {
  .upgrade-actions {
    flex-direction: row;
    justify-content: center;
  }
}
</style> 