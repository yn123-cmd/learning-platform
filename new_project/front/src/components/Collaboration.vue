<template>
  <div class="collaboration-page">
    <div class="page-header">
      <h1>协作空间</h1>
      <p>与同事协作，共同打造精品课程</p>
    </div>
    
    <div class="collaboration-content">
      <el-card>
        <div slot="header">
          <span>协作空间功能</span>
        </div>
        <p>这里将显示协作空间的功能...</p>
        <ul>
          <li>团队协作备课</li>
          <li>资源共享交流</li>
          <li>实时协同编辑</li>
          <li>教研讨论区</li>
        </ul>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Collaboration'
}
</script>

<style scoped>
.collaboration-page {
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

.collaboration-content {
  max-width: 800px;
}

.el-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
</style> 