<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>个人中心</h1>
      <p>管理您的个人信息和账户设置</p>
    </div>
    
    <div class="profile-content">
      <el-card>
        <div slot="header">
          <span>个人中心功能</span>
        </div>
        <p>这里将显示个人中心的功能...</p>
        <ul>
          <li>个人信息管理</li>
          <li>账户安全设置</li>
          <li>教学偏好配置</li>
          <li>使用统计报告</li>
        </ul>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Profile'
}
</script>

<style scoped>
.profile-page {
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

.profile-content {
  max-width: 800px;
}

.el-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
</style> 