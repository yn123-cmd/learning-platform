<template>
  <el-header class="header">
    <div class="header-content">
      <div class="logo">
        <i class="el-icon-edit-outline"></i>
        <h2>AI备课平台</h2>
      </div>
      <div class="header-search">
        <el-input
          placeholder="搜索课程、资源、教案、知识点..."
          suffix-icon="el-icon-search"
          size="medium"
          style="width: 450px;"
          class="search-input"
        >
          <template slot="prepend">
            <el-dropdown @command="handleSearchType">
              <span class="search-type">
                {{ searchType.label }}<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="all">全部</el-dropdown-item>
                <el-dropdown-item command="course">课程</el-dropdown-item>
                <el-dropdown-item command="resource">资源</el-dropdown-item>
                <el-dropdown-item command="plan">教案</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-input>
      </div>
      <div class="header-right">
        <el-badge :value="3" class="notification-badge">
          <el-button icon="el-icon-bell" circle size="medium"></el-button>
        </el-badge>
        <el-button type="primary" size="medium" icon="el-icon-edit">AI备课</el-button>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :src="user.avatar" size="medium">
              <i class="el-icon-user-solid"></i>
            </el-avatar>
            <span class="username">{{ user.nickName || user.username }}</span>
            <i class="el-icon-arrow-down"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">个人资料</el-dropdown-item>
            <el-dropdown-item command="settings">设置</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script>
export default {
  name: 'Header',
  data() {
    return {
      searchType: { label: '全部', value: 'all' }
    }
  },
  computed: {
    user() {
      return this.$store.getters.currentUser
    }
  },
  methods: {
    handleSearchType(command) {
      const types = {
        'all': { label: '全部', value: 'all' },
        'course': { label: '课程', value: 'course' },
        'resource': { label: '资源', value: 'resource' },
        'plan': { label: '教案', value: 'plan' }
      }
      this.searchType = types[command]
    },
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$message.info('个人资料功能开发中')
          break
        case 'settings':
          this.$message.info('设置功能开发中')
          break
        case 'logout':
          this.$store.dispatch('logout')
          this.$message.success('已退出登录')
          this.$router.push('/login')
          break
      }
    }
  }
}
</script>

<style scoped>
.header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 0;
  height: 60px;
  position: relative;
  z-index: 10;
  border-bottom: 1px solid rgba(228, 231, 237, 0.6);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo i {
  font-size: 1.8rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.logo h2 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  font-size: 1.3rem;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.header-search .el-input__inner {
  border-radius: 25px;
  border: 1px solid rgba(102, 126, 234, 0.2);
  background: rgba(255, 255, 255, 0.9);
  transition: all 0.3s;
}

.header-search .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: white;
}

.search-type {
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  padding: 0 8px;
}

.search-type:hover {
  color: #409EFF;
}

.notification-badge {
  margin-right: 12px;
}

.notification-badge .el-button {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(102, 126, 234, 0.2);
  color: #606266;
}

.notification-badge .el-button:hover {
  background: rgba(102, 126, 234, 0.1);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-right .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.header-right .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #606266;
  padding: 8px 15px;
  border-radius: 25px;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.8);
}

.user-info:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-1px);
}

.username {
  font-weight: 600;
}
</style> 