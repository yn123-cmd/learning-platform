<template>
  <div class="code-oj-page">
    <div class="page-header">
      <h1>代码OJ</h1>
      <p>在线编程练习平台，提升编程教学效果</p>
    </div>
    
    <div class="code-oj-content">
      <el-card>
        <div slot="header">
          <span>代码OJ功能</span>
        </div>
        <p>这里将显示代码OJ的功能...</p>
        <ul>
          <li>在线编程环境</li>
          <li>自动代码评测</li>
          <li>编程题库管理</li>
          <li>学生代码分析</li>
        </ul>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CodeOj'
}
</script>

<style scoped>
.code-oj-page {
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

.code-oj-content {
  max-width: 800px;
}

.el-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
</style> 