<template>
  <div class="app-layout">
    <!-- 顶部导航 -->
    <Header />
    
    <div class="main-layout">
      <!-- 左侧导航 -->
      <Sidebar />
      
      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script>
import Header from './Header.vue'
import Sidebar from './Sidebar.vue'

export default {
  name: 'Layout',
  components: {
    Header,
    Sidebar
  }
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: #f8f9fa;
  position: relative;
}

.main-layout {
  display: flex;
  min-height: calc(100vh - 60px);
  position: relative;
  z-index: 1;
}

.main-content {
  padding: 20px;
  background: transparent;
  flex: 1;
}
</style> 