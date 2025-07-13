<template>
  <div class="key-point-tagger">
    <div class="tagger-header">
      <div class="auto-tag-section">
        <el-button type="primary" size="small" @click="autoTagKeyPoints">
          <i class="el-icon-magic-stick"></i>
          智能标记
        </el-button>
        <el-button size="small" @click="clearAllTags">
          <i class="el-icon-delete"></i>
          清除标记
        </el-button>
      </div>
    </div>

    <div class="points-container">
      <div 
        v-for="point in points" 
        :key="point.id"
        class="point-item"
        :class="{ 
          'is-key-point': isKeyPoint(point.id),
          'high-importance': point.importance === 'high'
        }"
        @click="toggleKeyPoint(point)"
      >
        <div class="point-checkbox">
          <el-checkbox 
            :value="isKeyPoint(point.id)" 
            @change="toggleKeyPoint(point)"
          ></el-checkbox>
        </div>
        
        <div class="point-content">
          <div class="point-name">
            <i :class="getPointIcon(point.type)"></i>
            {{ point.name }}
          </div>
          
          <div class="point-meta">
            <el-tag 
              :type="getImportanceType(point.importance)" 
              size="mini"
            >
              重要度: {{ point.importance }}
            </el-tag>
            <el-tag 
              :type="getDifficultyType(point.difficulty)" 
              size="mini"
            >
              难度: {{ point.difficulty }}
            </el-tag>
          </div>
          
          <div class="point-description">
            {{ point.description }}
          </div>
        </div>
        
        <div class="point-actions">
          <el-button 
            type="text" 
            size="mini"
            @click.stop="setAsKeyPoint(point)"
            :disabled="isKeyPoint(point.id)"
          >
            <i class="el-icon-star-on"></i>
          </el-button>
        </div>
      </div>
    </div>

    <div class="tagging-summary">
      <div class="summary-stats">
        <div class="stat-item">
          <span class="stat-label">已标记重点:</span>
          <span class="stat-value">{{ keyPoints.length }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">总知识点:</span>
          <span class="stat-value">{{ points.length }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">标记比例:</span>
          <span class="stat-value">{{ taggingRatio }}%</span>
        </div>
      </div>
      
      <div class="key-points-preview">
        <h4>重点预览</h4>
        <div class="key-points-list">
          <el-tag
            v-for="keyPoint in keyPoints"
            :key="keyPoint.id"
            type="success"
            closable
            @close="removeKeyPoint(keyPoint)"
          >
            {{ keyPoint.name }}
          </el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'KeyPointTagger',
  props: {
    points: {
      type: Array,
      default: () => []
    },
    keyPoints: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    taggingRatio() {
      return this.points.length > 0 
        ? Math.round((this.keyPoints.length / this.points.length) * 100)
        : 0
    }
  },
  methods: {
    isKeyPoint(pointId) {
      return this.keyPoints.some(kp => kp.id === pointId)
    },
    
    toggleKeyPoint(point) {
      if (this.isKeyPoint(point.id)) {
        this.removeKeyPoint(point)
      } else {
        this.addKeyPoint(point)
      }
    },
    
    addKeyPoint(point) {
      const newKeyPoints = [...this.keyPoints, point]
      this.$emit('points-tagged', newKeyPoints)
    },
    
    removeKeyPoint(point) {
      const newKeyPoints = this.keyPoints.filter(kp => kp.id !== point.id)
      this.$emit('points-tagged', newKeyPoints)
    },
    
    setAsKeyPoint(point) {
      if (!this.isKeyPoint(point.id)) {
        this.addKeyPoint(point)
      }
    },
    
    autoTagKeyPoints() {
      // 基于重要度和难度自动标记重点
      const autoKeyPoints = this.points.filter(point => {
        return point.importance === 'high' || 
               (point.importance === 'medium' && point.difficulty === 'hard')
      })
      
      this.$emit('points-tagged', autoKeyPoints)
      this.$message.success(`自动标记了 ${autoKeyPoints.length} 个重点`)
    },
    
    clearAllTags() {
      this.$emit('points-tagged', [])
      this.$message.info('已清除所有重点标记')
    },
    
    getPointIcon(type) {
      const icons = {
        concept: 'el-icon-bulb',
        skill: 'el-icon-setting',
        application: 'el-icon-cpu',
        analysis: 'el-icon-view'
      }
      return icons[type] || 'el-icon-document'
    },
    
    getImportanceType(importance) {
      const types = {
        high: 'danger',
        medium: 'warning',
        low: 'info'
      }
      return types[importance] || 'info'
    },
    
    getDifficultyType(difficulty) {
      const types = {
        easy: 'success',
        medium: 'warning',
        hard: 'danger'
      }
      return types[difficulty] || 'info'
    }
  }
}
</script>

<style scoped>
.key-point-tagger {
  padding: 20px;
}

.tagger-header {
  margin-bottom: 20px;
  text-align: center;
}

.auto-tag-section {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.points-container {
  margin-bottom: 24px;
  max-height: 400px;
  overflow-y: auto;
}

.point-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.point-item:hover {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.point-item.is-key-point {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-color: #0ea5e9;
}

.point-item.high-importance {
  border-left: 4px solid #ef4444;
}

.point-checkbox {
  margin-top: 2px;
}

.point-content {
  flex: 1;
}

.point-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.point-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.point-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.point-actions {
  display: flex;
  align-items: center;
}

.tagging-summary {
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.summary-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  color: #606266;
  font-size: 14px;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  color: #667eea;
  font-size: 20px;
  font-weight: 600;
}

.key-points-preview h4 {
  color: #2c3e50;
  margin-bottom: 12px;
  font-size: 16px;
}

.key-points-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.key-points-list .el-tag {
  margin: 0;
}

@media (max-width: 768px) {
  .point-item {
    flex-direction: column;
    gap: 8px;
  }
  
  .point-actions {
    align-self: flex-end;
  }
  
  .summary-stats {
    flex-direction: column;
    gap: 12px;
  }
}
</style> 