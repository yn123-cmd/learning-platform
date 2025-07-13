<template>
  <div class="resource-linker">
    <div class="linker-header">
      <div class="header-title">
        <i class="el-icon-link"></i>
        <span>资源链接</span>
      </div>
      <el-button 
        size="mini" 
        type="primary" 
        icon="el-icon-plus"
        @click="showDialog = true"
      >
        添加资源
      </el-button>
    </div>

    <!-- 已链接资源 -->
    <div v-if="linkedResources.length > 0" class="linked-resources">
      <div 
        v-for="resource in linkedResources" 
        :key="resource.id"
        class="resource-item"
      >
        <div class="resource-info">
          <i :class="getResourceIcon(resource.type)" class="resource-icon"></i>
          <div class="resource-details">
            <div class="resource-name">{{ resource.name }}</div>
            <div class="resource-description">{{ resource.description }}</div>
          </div>
        </div>
        <el-button 
          size="mini" 
          type="text" 
          icon="el-icon-close" 
          @click="removeResource(resource.id)"
        ></el-button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <i class="el-icon-folder-opened"></i>
      <p>暂无关联资源</p>
    </div>

    <!-- 推荐资源 -->
    <div v-if="recommendedResources.length > 0" class="recommended-section">
      <h5>推荐资源</h5>
      <div 
        v-for="resource in recommendedResources" 
        :key="resource.id"
        class="recommended-item"
        @click="addRecommended(resource)"
      >
        <i :class="getResourceIcon(resource.type)"></i>
        <span>{{ resource.name }}</span>
        <el-button size="mini" type="primary">添加</el-button>
      </div>
    </div>

    <!-- 资源选择对话框 -->
    <el-dialog title="选择资源" :visible.sync="showDialog" width="60%">
      <div class="dialog-content">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索资源" 
          prefix-icon="el-icon-search"
        ></el-input>
        
        <div class="resource-grid">
          <div 
            v-for="resource in filteredResources" 
            :key="resource.id"
            class="resource-option"
            @click="addResource(resource)"
          >
            <i :class="getResourceIcon(resource.type)"></i>
            <div class="option-info">
              <div class="option-name">{{ resource.name }}</div>
              <div class="option-desc">{{ resource.description }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ResourceLinker',
  props: {
    strategy: {
      type: Object,
      required: true
    },
    availableResources: {
      type: Array,
      default: () => []
    }
  },
  
  data() {
    return {
      showDialog: false,
      searchKeyword: '',
      linkedResources: [],
      
      resourceLibrary: [
        {
          id: 1,
          name: 'PPT模板',
          description: '精美的演示文稿模板',
          type: 'presentation'
        },
        {
          id: 2,
          name: '教学视频',
          description: '配套教学演示视频',
          type: 'media'
        },
        {
          id: 3,
          name: '在线工具',
          description: '互动教学数字工具',
          type: 'digital'
        },
        {
          id: 4,
          name: '参考资料',
          description: '相关学习资料文档',
          type: 'document'
        }
      ]
    }
  },
  
  computed: {
    recommendedResources() {
      // 基于策略类型推荐资源
      const recommendations = []
      
      if (this.strategy.type === 'core') {
        recommendations.push({
          id: 'rec1',
          name: '核心概念图表',
          type: 'presentation'
        })
      }
      
      if (this.strategy.name && this.strategy.name.includes('互动')) {
        recommendations.push({
          id: 'rec2',
          name: '互动白板工具',
          type: 'digital'
        })
      }
      
      return recommendations
    },
    
    filteredResources() {
      return this.resourceLibrary.filter(resource => {
        if (!this.searchKeyword) return true
        return resource.name.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
               resource.description.toLowerCase().includes(this.searchKeyword.toLowerCase())
      })
    }
  },
  
  methods: {
    getResourceIcon(type) {
      const icons = {
        presentation: 'el-icon-document',
        media: 'el-icon-video-camera',
        digital: 'el-icon-monitor',
        document: 'el-icon-folder',
        equipment: 'el-icon-box'
      }
      return icons[type] || 'el-icon-collection'
    },
    
    addResource(resource) {
      const newResource = { ...resource, id: Date.now() }
      this.linkedResources.push(newResource)
      this.showDialog = false
      this.$message.success(`已添加资源：${resource.name}`)
      this.$emit('resource-linked', this.strategy.id, newResource)
    },
    
    addRecommended(resource) {
      this.addResource(resource)
    },
    
    removeResource(resourceId) {
      this.linkedResources = this.linkedResources.filter(r => r.id !== resourceId)
      this.$message.success('已移除资源')
      this.$emit('resource-unlinked', this.strategy.id, resourceId)
    }
  }
}
</script>

<style scoped>
.resource-linker {
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.linker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #2c3e50;
}

.linked-resources {
  margin-bottom: 15px;
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 6px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.resource-item:hover {
  transform: translateX(5px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.resource-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.resource-icon {
  font-size: 20px;
  color: #409EFF;
}

.resource-name {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 2px;
}

.resource-description {
  font-size: 12px;
  color: #606266;
}

.empty-state {
  text-align: center;
  padding: 30px;
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 10px;
  opacity: 0.5;
}

.recommended-section {
  margin-top: 20px;
  padding: 15px;
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.1), rgba(255, 215, 0, 0.05));
  border-radius: 8px;
}

.recommended-section h5 {
  margin: 0 0 10px 0;
  color: #E6A23C;
  font-size: 14px;
}

.recommended-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background: white;
  border-radius: 6px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.recommended-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.recommended-item i {
  color: #FFD700;
  font-size: 18px;
}

.dialog-content {
  max-height: 400px;
  overflow-y: auto;
}

.dialog-content .el-input {
  margin-bottom: 20px;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.resource-option {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.resource-option:hover {
  border-color: #409EFF;
  background: rgba(64, 158, 255, 0.05);
  transform: translateY(-2px);
}

.resource-option i {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 10px;
}

.option-name {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 5px;
}

.option-desc {
  font-size: 12px;
  color: #606266;
}

@media (max-width: 768px) {
  .resource-grid {
    grid-template-columns: 1fr;
  }
  
  .resource-item {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .linker-header {
    flex-direction: column;
    gap: 10px;
  }
}
</style> 