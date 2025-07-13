<template>
  <el-aside class="sidebar" width="240px">
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      background-color="transparent"
      text-color="#6b7280"
      active-text-color="#1f2937"
      router
    >
      <el-menu-item index="/dashboard" route="/dashboard">
        <i class="el-icon-house"></i>
        <span>首页</span>
      </el-menu-item>
      
      <!-- 教学设计子菜单 -->
      <el-submenu index="teaching">
        <template slot="title">
          <i class="el-icon-edit-outline"></i>
          <span>教学设计</span>
        </template>
        <el-menu-item index="/teaching-design/course-positioning" route="/teaching-design/course-positioning">
          <i class="el-icon-position"></i>
          <span>课程定位</span>
        </el-menu-item>
        <el-menu-item index="/teaching-design/goal-generation" route="/teaching-design/goal-generation">
          <i class="el-icon-aim"></i>
          <span>目标生成</span>
        </el-menu-item>
        <el-menu-item index="/teaching-design/key-points" route="/teaching-design/key-points">
          <i class="el-icon-star-on"></i>
          <span>重难点</span>
        </el-menu-item>
        <el-menu-item index="/teaching-design/student-config" route="/teaching-design/student-config">
          <i class="el-icon-user"></i>
          <span>学情配置</span>
        </el-menu-item>
        <el-menu-item index="/teaching-design/lesson-planning" route="/teaching-design/lesson-planning">
          <i class="el-icon-date"></i>
          <span>课时规划</span>
        </el-menu-item>
        <el-menu-item index="/teaching-design/output" route="/teaching-design/output">
          <i class="el-icon-download"></i>
          <span>方案输出</span>
        </el-menu-item>
      </el-submenu>

      <!-- 智能资源生成子菜单 -->
      <el-submenu index="resources">
        <template slot="title">
          <i class="el-icon-magic-stick"></i>
          <span>智能资源生成</span>
        </template>
        <el-menu-item index="/resource-materials" route="/resource-materials">
          <i class="el-icon-document-add"></i>
          <span>多媒体资源</span>
        </el-menu-item>
        <el-menu-item index="/ai-ppt-generator" route="/ai-ppt-generator">
          <i class="el-icon-present-outline"></i> 
          <span>AI生成PPT</span>
        </el-menu-item>
      </el-submenu>

      <!-- 学情分析子菜单 -->
      <el-submenu index="analytics">
        <template slot="title">
          <i class="el-icon-data-analysis"></i>
          <span>学情分析</span>
        </template>
        <el-menu-item index="/analytics/dashboard" route="/analytics/dashboard">
          <i class="el-icon-data-board"></i>
          <span>学生数据看板</span>
        </el-menu-item>
        <el-menu-item index="/analytics/diagnosis" route="/analytics/diagnosis">
          <i class="el-icon-search"></i>
          <span>认知诊断</span>
        </el-menu-item>
        <el-menu-item index="/analytics/tracking" route="/analytics/tracking">
          <i class="el-icon-guide"></i>
          <span>知识追踪</span>
        </el-menu-item>
      </el-submenu>



      <el-menu-item index="/ai-lesson" route="/ai-lesson">
        <i class="el-icon-magic-stick"></i>
        <span>一键个性化备课</span>
      </el-menu-item>
      <el-menu-item index="/code-oj" route="/code-oj">
        <i class="el-icon-cpu"></i>
        <span>代码OJ</span>
      </el-menu-item>
      <el-menu-item index="/history" route="/history">
        <i class="el-icon-time"></i>
        <span>备课历史</span>
      </el-menu-item>
      <el-menu-item index="/collaboration" route="/collaboration">
        <i class="el-icon-coordinate"></i>
        <span>协作空间</span>
      </el-menu-item>
      <el-menu-item index="/profile" route="/profile">
        <i class="el-icon-user"></i>
        <span>个人中心</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script>
export default {
  name: 'Sidebar',
  computed: {
    activeMenu() {
      return this.$route.path
    }
  }
}
</script>

<style scoped>
.sidebar {
  background: #f8fafc;
  border-right: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.sidebar-menu {
  border: none;
  height: 100%;
  background: transparent;
  padding: 16px 0;
}

.sidebar-menu .el-menu-item {
  height: 48px;
  line-height: 48px;
  margin: 2px 16px;
  border-radius: 10px;
  transition: all 0.2s ease;
  position: relative;
  font-weight: 500;
  font-size: 14px;
  color: #6b7280;
}

.sidebar-menu .el-menu-item::before {
  display: none;
}

.sidebar-menu .el-menu-item:hover {
  background: #f1f5f9;
  color: #374151;
  transform: translateX(2px);
}

.sidebar-menu .el-menu-item.is-active {
  background: #f1f5f9;
  color: #1f2937;
  font-weight: 600;
  border-left: 3px solid #3b82f6;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.sidebar-menu .el-menu-item i {
  margin-right: 12px;
  width: 20px;
  text-align: center;
  font-size: 16px;
  color: #9ca3af;
  transition: color 0.2s ease;
}

.sidebar-menu .el-menu-item:hover i {
  color: #6b7280;
}

.sidebar-menu .el-menu-item.is-active i {
  color: #3b82f6;
}

/* 子菜单样式 */
.sidebar-menu .el-submenu {
  margin: 2px 16px;
  border-radius: 10px;
  overflow: hidden;
}

.sidebar-menu .el-submenu__title {
  height: 48px;
  line-height: 48px;
  padding: 0 16px !important;
  border-radius: 10px;
  transition: all 0.2s ease;
  font-weight: 500;
  font-size: 14px;
  color: #6b7280;
}

.sidebar-menu .el-submenu__title:hover {
  background: #f1f5f9;
  color: #374151;
  transform: translateX(2px);
}

.sidebar-menu .el-submenu.is-active .el-submenu__title {
  background: #f1f5f9;
  color: #1f2937;
  font-weight: 600;
}

.sidebar-menu .el-submenu .el-submenu__icon-arrow {
  right: 16px;
  margin-top: -3px;
  font-size: 12px;
  transition: transform 0.3s ease;
  color: #9ca3af;
}

.sidebar-menu .el-submenu.is-opened .el-submenu__icon-arrow {
  transform: rotateZ(180deg);
  color: #6b7280;
}

/* 子菜单项样式 */
.sidebar-menu .el-submenu .el-menu {
  background: transparent;
  margin: 4px 0 8px 0;
  border-radius: 0;
}

.sidebar-menu .el-submenu .el-menu-item {
  height: 40px;
  line-height: 40px;
  margin: 1px 20px 1px 32px;
  padding-left: 36px !important;
  border-radius: 8px;
  font-size: 13px;
  background: transparent;
  color: #9ca3af;
  font-weight: 400;
}

.sidebar-menu .el-submenu .el-menu-item:hover {
  background: #f8fafc;
  color: #6b7280;
  transform: translateX(2px);
}

.sidebar-menu .el-submenu .el-menu-item.is-active {
  background: #eff6ff;
  color: #3b82f6;
  font-weight: 600;
  border-left: 2px solid #3b82f6;
  transform: none;
}

.sidebar-menu .el-submenu .el-menu-item i {
  margin-right: 8px;
  width: 14px;
  font-size: 14px;
  color: #d1d5db;
}

.sidebar-menu .el-submenu .el-menu-item:hover i {
  color: #9ca3af;
}

.sidebar-menu .el-submenu .el-menu-item.is-active i {
  color: #3b82f6;
}

/* 子菜单标题图标样式 */
.sidebar-menu .el-submenu__title i {
  margin-right: 12px;
  width: 20px;
  text-align: center;
  font-size: 16px;
  color: #9ca3af;
  transition: color 0.2s ease;
}

.sidebar-menu .el-submenu__title:hover i {
  color: #6b7280;
}

.sidebar-menu .el-submenu.is-active .el-submenu__title i {
  color: #3b82f6;
}

/* 添加一些细节优化 */
.sidebar-menu .el-menu-item span,
.sidebar-menu .el-submenu__title span {
  transition: all 0.2s ease;
}

/* 去除Element UI默认的选中状态样式 */
.sidebar-menu .el-menu-item::after {
  display: none;
}
</style> 