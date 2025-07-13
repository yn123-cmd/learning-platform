<template>
  <div class="resource-questions">
    <div class="page-header">
      <h2>智能出题</h2>
      <p>AI驱动的智能题目生成系统</p>
    </div>
    
    <div class="coming-soon">
      <el-card shadow="hover">
        <div class="content">
          <i class="el-icon-help"></i>
          <h3>智能出题功能</h3>
          <p>该功能正在开发中，敬请期待...</p>
          <el-button type="primary" disabled>即将上线</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ResourceQuestions'
}
</script>

<style scoped>
.resource-questions {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.coming-soon {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.content {
  text-align: center;
  padding: 40px;
}

.content i {
  font-size: 64px;
  color: #67C23A;
  margin-bottom: 20px;
}

.content h3 {
  color: #2c3e50;
  margin-bottom: 12px;
  font-size: 20px;
}

.content p {
  color: #606266;
  margin-bottom: 24px;
  font-size: 14px;
}
</style> 