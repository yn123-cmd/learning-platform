<template>
  <div class="ai-lesson-page">
    <div class="page-header">
      <h1>AI一键备课</h1>
      <p>智能生成教案、PPT、习题，让备课更轻松高效</p>
    </div>
    
    <div class="ai-lesson-content">
      <el-card>
        <div slot="header">
          <span>AI备课功能</span>
        </div>
        <p>这里将显示AI一键备课的功能...</p>
        <ul>
          <li>智能教案生成</li>
          <li>自动PPT制作</li>
          <li>习题库智能推荐</li>
          <li>个性化教学设计</li>
        </ul>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AiLesson'
}
</script>

<style scoped>
.ai-lesson-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.page-header p {
  color: #606266;
  font-size: 14px;
}

.ai-lesson-content {
  max-width: 800px;
}

.el-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
</style> 