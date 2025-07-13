<template>
  <div class="misconception-list">
    <div class="list-header">
      <div class="filter-controls">
        <el-select v-model="selectedType" placeholder="筛选类型" size="small" clearable>
          <el-option label="概念误区" value="conceptual"></el-option>
          <el-option label="过程误区" value="procedural"></el-option>
          <el-option label="自定义误区" value="custom"></el-option>
        </el-select>
        <el-select v-model="selectedFrequency" placeholder="筛选频率" size="small" clearable>
          <el-option label="高频" value="high"></el-option>
          <el-option label="中频" value="medium"></el-option>
          <el-option label="低频" value="low"></el-option>
        </el-select>
      </div>
    </div>

    <div class="misconceptions-grid">
      <div
        v-for="misconception in filteredMisconceptions"
        :key="misconception.id"
        class="misconception-card"
        :class="`frequency-${misconception.frequency}`"
        @click="selectMisconception(misconception)"
      >
        <div class="card-header">
          <div class="misconception-title">
            <i :class="getTypeIcon(misconception.type)"></i>
            {{ misconception.title }}
          </div>
          <div class="misconception-meta">
            <el-tag 
              :type="getFrequencyType(misconception.frequency)" 
              size="mini"
            >
              {{ getFrequencyLabel(misconception.frequency) }}
            </el-tag>
            <el-tag 
              :type="getTypeTagType(misconception.type)" 
              size="mini"
            >
              {{ getTypeLabel(misconception.type) }}
            </el-tag>
          </div>
        </div>
        
        <div class="misconception-description">
          {{ misconception.description }}
        </div>
        
        <div class="misconception-solution">
          <div class="solution-label">
            <i class="el-icon-lightbulb"></i>
            解决方案:
          </div>
          <div class="solution-content">
            {{ misconception.solution }}
          </div>
        </div>
        
        <div class="card-actions">
          <el-button 
            type="text" 
            size="mini"
            @click.stop="editMisconception(misconception)"
          >
            <i class="el-icon-edit"></i>
            编辑
          </el-button>
          <el-button 
            type="text" 
            size="mini"
            @click.stop="addToStrategy(misconception)"
          >
            <i class="el-icon-plus"></i>
            加入策略
          </el-button>
        </div>
      </div>
    </div>

    <div v-if="filteredMisconceptions.length === 0" class="empty-state">
      <el-empty description="暂无匹配的误区记录">
        <el-button type="primary" @click="resetFilters">重置筛选</el-button>
      </el-empty>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      title="编辑误区"
      :visible.sync="editDialogVisible"
      width="500px"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input 
            type="textarea" 
            v-model="editForm.description"
            :rows="3"
          ></el-input>
        </el-form-item>
        <el-form-item label="解决方案">
          <el-input 
            type="textarea" 
            v-model="editForm.solution"
            :rows="3"
          ></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.type" style="width: 100%">
            <el-option label="概念误区" value="conceptual"></el-option>
            <el-option label="过程误区" value="procedural"></el-option>
            <el-option label="自定义误区" value="custom"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="频率">
          <el-select v-model="editForm.frequency" style="width: 100%">
            <el-option label="高频" value="high"></el-option>
            <el-option label="中频" value="medium"></el-option>
            <el-option label="低频" value="low"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'MisconceptionList',
  props: {
    topic: {
      type: Object,
      default: () => ({})
    },
    misconceptions: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      selectedType: '',
      selectedFrequency: '',
      editDialogVisible: false,
      editForm: {
        id: '',
        title: '',
        description: '',
        solution: '',
        type: '',
        frequency: ''
      },
      editIndex: -1
    }
  },
  computed: {
    filteredMisconceptions() {
      let filtered = [...this.misconceptions]
      
      if (this.selectedType) {
        filtered = filtered.filter(m => m.type === this.selectedType)
      }
      
      if (this.selectedFrequency) {
        filtered = filtered.filter(m => m.frequency === this.selectedFrequency)
      }
      
      return filtered
    }
  },
  methods: {
    selectMisconception(misconception) {
      this.$emit('misconception-selected', misconception)
    },
    
    editMisconception(misconception) {
      this.editForm = { ...misconception }
      this.editIndex = this.misconceptions.findIndex(m => m.id === misconception.id)
      this.editDialogVisible = true
    },
    
    saveEdit() {
      if (this.editIndex >= 0) {
        // 更新现有误区
        this.$set(this.misconceptions, this.editIndex, { ...this.editForm })
        this.$message.success('误区已更新')
      }
      this.editDialogVisible = false
    },
    
    addToStrategy(misconception) {
      this.$message.success(`已将"${misconception.title}"加入教学策略`)
      // 这里可以触发事件将误区添加到策略中
    },
    
    resetFilters() {
      this.selectedType = ''
      this.selectedFrequency = ''
    },
    
    getTypeIcon(type) {
      const icons = {
        conceptual: 'el-icon-bulb',
        procedural: 'el-icon-setting',
        custom: 'el-icon-user'
      }
      return icons[type] || 'el-icon-warning'
    },
    
    getFrequencyType(frequency) {
      const types = {
        high: 'danger',
        medium: 'warning',
        low: 'info'
      }
      return types[frequency] || 'info'
    },
    
    getFrequencyLabel(frequency) {
      const labels = {
        high: '高频',
        medium: '中频',
        low: '低频'
      }
      return labels[frequency] || '未知'
    },
    
    getTypeTagType(type) {
      const types = {
        conceptual: 'primary',
        procedural: 'success',
        custom: 'warning'
      }
      return types[type] || 'info'
    },
    
    getTypeLabel(type) {
      const labels = {
        conceptual: '概念',
        procedural: '过程',
        custom: '自定义'
      }
      return labels[type] || '未知'
    }
  }
}
</script>

<style scoped>
.misconception-list {
  padding: 20px;
}

.list-header {
  margin-bottom: 20px;
}

.filter-controls {
  display: flex;
  gap: 12px;
}

.misconceptions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.misconception-card {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid;
}

.misconception-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.misconception-card.frequency-high {
  border-left-color: #e74c3c;
}

.misconception-card.frequency-medium {
  border-left-color: #f39c12;
}

.misconception-card.frequency-low {
  border-left-color: #3498db;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.misconception-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #2c3e50;
  flex: 1;
}

.misconception-meta {
  display: flex;
  gap: 4px;
  flex-direction: column;
}

.misconception-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 16px;
}

.misconception-solution {
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.solution-label {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #667eea;
  font-weight: 600;
  font-size: 13px;
  margin-bottom: 6px;
}

.solution-content {
  color: #5a6c7d;
  font-size: 13px;
  line-height: 1.4;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.dialog-footer {
  text-align: right;
}

@media (max-width: 768px) {
  .misconceptions-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-controls {
    flex-direction: column;
  }
  
  .card-header {
    flex-direction: column;
    gap: 8px;
  }
  
  .misconception-meta {
    flex-direction: row;
  }
}
</style> 