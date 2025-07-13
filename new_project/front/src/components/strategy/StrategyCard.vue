<template>
  <div class="strategy-card" :class="{ selected: isSelected, disabled: !isAvailable }">
    <!-- 卡片头部 -->
    <div class="card-header">
      <div class="strategy-info">
        <div class="strategy-icon" :class="strategy.type">
          <i :class="getStrategyIcon(strategy.type)"></i>
        </div>
        <div class="strategy-title">
          <h4>{{ strategy.name }}</h4>
          <div class="strategy-badges">
            <el-tag :type="getTypeColor(strategy.type)" size="mini">
              {{ getTypeLabel(strategy.type) }}
            </el-tag>
            <el-tag v-if="strategy.difficulty" size="mini" :type="getDifficultyType(strategy.difficulty)">
              {{ getDifficultyLabel(strategy.difficulty) }}
            </el-tag>
          </div>
        </div>
      </div>
      <div class="card-actions">
        <el-tooltip content="选择/取消此策略" placement="top">
          <el-switch
            v-model="isSelected"
            @change="toggleStrategy"
            :disabled="!isAvailable"
          ></el-switch>
        </el-tooltip>
      </div>
    </div>

    <!-- 策略描述 -->
    <div class="strategy-description">
      <p>{{ strategy.description }}</p>
    </div>

    <!-- 效果指标 -->
    <div class="effectiveness-metrics">
      <div class="metric-item">
        <span class="metric-label">教学效果：</span>
        <el-progress 
          :percentage="strategy.effectiveness" 
          :stroke-width="8"
          :show-text="false"
          :color="getEffectivenessColor(strategy.effectiveness)"
        ></el-progress>
        <span class="metric-value">{{ strategy.effectiveness }}%</span>
      </div>
      
      <div class="metric-item" v-if="strategy.engagementLevel">
        <span class="metric-label">参与度：</span>
        <el-rate 
          :value="strategy.engagementLevel" 
          :max="5" 
          disabled 
          size="small"
          :colors="['#F7BA2A', '#F7BA2A', '#67C23A']"
        ></el-rate>
      </div>
    </div>

    <!-- 适用条件 -->
    <div class="suitable-conditions" v-if="strategy.suitableFor">
      <div class="condition-label">适用学习风格：</div>
      <div class="condition-tags">
        <el-tag 
          v-for="style in strategy.suitableFor" 
          :key="style"
          size="mini"
          type="info"
        >
          {{ getStyleLabel(style) }}
        </el-tag>
      </div>
    </div>

    <!-- 时间分配 -->
    <div class="time-allocation" v-if="isSelected">
      <div class="time-header">
        <span class="time-label">时间分配：</span>
        <span class="time-display">{{ time }}分钟</span>
      </div>
      <div class="time-controls">
        <el-slider
          :value="time"
          :min="5"
          :max="30"
          :step="1"
          @input="updateTime"
          show-input
          :show-input-controls="false"
        ></el-slider>
      </div>
      <div class="time-recommendations">
        <el-button 
          size="mini" 
          type="text" 
          @click="setRecommendedTime"
          :disabled="time === strategy.recommendedTime"
        >
          推荐：{{ strategy.recommendedTime }}分钟
        </el-button>
      </div>
    </div>

    <!-- 策略配置 -->
    <div class="strategy-config" v-if="isSelected && strategy.configurable">
      <div class="config-section">
        <div class="config-label">策略参数：</div>
        <div class="config-options">
          <div v-if="strategy.type === 'core' && strategy.name.includes('互动')" class="config-item">
            <span>分组方式：</span>
            <el-select v-model="localConfig.grouping" size="small">
              <el-option label="随机分组" value="random"></el-option>
              <el-option label="能力分组" value="ability"></el-option>
              <el-option label="兴趣分组" value="interest"></el-option>
            </el-select>
          </div>
          <div v-if="strategy.type === 'core' && strategy.name.includes('实践')" class="config-item">
            <span>实践方式：</span>
            <el-select v-model="localConfig.practiceMode" size="small">
              <el-option label="个人操作" value="individual"></el-option>
              <el-option label="协作实践" value="collaborative"></el-option>
              <el-option label="竞赛模式" value="competition"></el-option>
            </el-select>
          </div>
        </div>
      </div>
    </div>

    <!-- 资源需求 -->
    <div class="resource-requirements" v-if="strategy.requiredResources">
      <div class="resource-label">所需资源：</div>
      <div class="resource-list">
        <div 
          v-for="resource in strategy.requiredResources" 
          :key="resource.id"
          class="resource-item"
          :class="{ available: resource.available }"
        >
          <i :class="getResourceIcon(resource.type)"></i>
          <span>{{ resource.name }}</span>
          <el-tooltip v-if="!resource.available" content="资源不可用" placement="top">
            <i class="el-icon-warning-outline resource-warning"></i>
          </el-tooltip>
        </div>
      </div>
    </div>

    <!-- 策略输出预览 -->
    <div class="strategy-output" v-if="isSelected">
      <div class="output-label">预期输出：</div>
      <ul class="output-list">
        <li v-for="output in strategy.expectedOutputs" :key="output">
          {{ output }}
        </li>
      </ul>
    </div>

    <!-- 插槽：资源链接器 -->
    <div class="resource-linker-container" v-if="isSelected">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StrategyCard',
  props: {
    strategy: {
      type: Object,
      required: true,
      default: () => ({})
    },
    time: {
      type: Number,
      default: 10
    },
    isSelected: {
      type: Boolean,
      default: false
    },
    isAvailable: {
      type: Boolean,
      default: true
    }
  },
  
  data() {
    return {
      localConfig: {
        grouping: 'random',
        practiceMode: 'individual',
        assessmentType: 'formative'
      }
    }
  },
  
  computed: {
    // 根据策略类型和效果评估可用性
    canBeSelected() {
      return this.isAvailable && !this.hasResourceConflicts
    },
    
    hasResourceConflicts() {
      if (!this.strategy.requiredResources) return false
      return this.strategy.requiredResources.some(r => !r.available)
    }
  },
  
  watch: {
    localConfig: {
      handler(newConfig) {
        this.$emit('config-change', this.strategy.id, newConfig)
      },
      deep: true
    }
  },
  
  methods: {
    toggleStrategy() {
      if (this.canBeSelected || this.isSelected) {
        this.$emit('toggle', this.strategy.id)
      }
    },
    
    updateTime(newTime) {
      this.$emit('time-change', this.strategy.id, newTime)
    },
    
    setRecommendedTime() {
      this.updateTime(this.strategy.recommendedTime)
    },
    
    getStrategyIcon(type) {
      const icons = {
        core: 'el-icon-star-on',
        support: 'el-icon-s-tools',
        assessment: 'el-icon-document-checked',
        extension: 'el-icon-plus'
      }
      return icons[type] || 'el-icon-collection'
    },
    
    getTypeLabel(type) {
      const labels = {
        core: '核心',
        support: '辅助',
        assessment: '评估',
        extension: '拓展'
      }
      return labels[type] || type
    },
    
    getTypeColor(type) {
      const colors = {
        core: 'primary',
        support: 'success',
        assessment: 'warning',
        extension: 'info'
      }
      return colors[type] || 'info'
    },
    
    getDifficultyLabel(difficulty) {
      const labels = {
        easy: '简单',
        medium: '中等',
        hard: '困难'
      }
      return labels[difficulty] || difficulty
    },
    
    getDifficultyType(difficulty) {
      const types = {
        easy: 'success',
        medium: 'warning',
        hard: 'danger'
      }
      return types[difficulty] || 'info'
    },
    
    getEffectivenessColor(effectiveness) {
      if (effectiveness >= 80) return '#67C23A'
      if (effectiveness >= 60) return '#E6A23C'
      return '#F56C6C'
    },
    
    getStyleLabel(style) {
      const labels = {
        visual: '视觉',
        auditory: '听觉',
        kinesthetic: '动觉',
        reading: '读写',
        all: '通用'
      }
      return labels[style] || style
    },
    
    getResourceIcon(type) {
      const icons = {
        presentation: 'el-icon-document',
        equipment: 'el-icon-box',
        digital: 'el-icon-monitor',
        document: 'el-icon-folder',
        media: 'el-icon-video-camera'
      }
      return icons[type] || 'el-icon-collection'
    }
  }
}
</script>

<style scoped>
.strategy-card {
  background: white;
  border: 2px solid #e4e7ed;
  border-radius: 15px;
  padding: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.strategy-card:hover {
  border-color: #409EFF;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.strategy-card.selected {
  border-color: #409EFF;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(64, 158, 255, 0.02));
}

.strategy-card.selected::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, #409EFF, #67C23A);
}

.strategy-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.strategy-info {
  display: flex;
  gap: 15px;
  flex: 1;
}

.strategy-icon {
  width: 45px;
  height: 45px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.strategy-icon.core {
  background: linear-gradient(135deg, #409EFF, #667eea);
}

.strategy-icon.support {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.strategy-icon.assessment {
  background: linear-gradient(135deg, #E6A23C, #f39c12);
}

.strategy-icon.extension {
  background: linear-gradient(135deg, #909399, #b3b3b3);
}

.strategy-title h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.3;
}

.strategy-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.card-actions {
  margin-left: 15px;
}

/* 策略描述 */
.strategy-description {
  margin-bottom: 15px;
}

.strategy-description p {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin: 0;
}

/* 效果指标 */
.effectiveness-metrics {
  margin-bottom: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.metric-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.metric-item:last-child {
  margin-bottom: 0;
}

.metric-label {
  font-size: 13px;
  color: #909399;
  min-width: 70px;
}

.metric-value {
  font-size: 13px;
  color: #2c3e50;
  font-weight: 600;
  min-width: 35px;
}

/* 适用条件 */
.suitable-conditions {
  margin-bottom: 15px;
}

.condition-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  font-weight: 500;
}

.condition-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

/* 时间分配 */
.time-allocation {
  margin-bottom: 15px;
  padding: 15px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(64, 158, 255, 0.02));
  border-radius: 8px;
  border: 1px solid rgba(64, 158, 255, 0.1);
}

.time-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.time-label {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.time-display {
  font-size: 16px;
  color: #409EFF;
  font-weight: 600;
}

.time-controls {
  margin-bottom: 10px;
}

.time-recommendations {
  text-align: right;
}

/* 策略配置 */
.strategy-config {
  margin-bottom: 15px;
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
}

.config-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 10px;
  font-weight: 500;
}

.config-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.config-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.config-item span {
  color: #606266;
  min-width: 70px;
}

/* 资源需求 */
.resource-requirements {
  margin-bottom: 15px;
}

.resource-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  font-weight: 500;
}

.resource-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.resource-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
  padding: 6px 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.resource-item.available {
  color: #67C23A;
  background: rgba(103, 194, 58, 0.1);
}

.resource-warning {
  color: #E6A23C;
  margin-left: auto;
}

/* 策略输出 */
.strategy-output {
  margin-bottom: 15px;
}

.output-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  font-weight: 500;
}

.output-list {
  margin: 0;
  padding-left: 20px;
}

.output-list li {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 4px;
}

/* 资源链接器容器 */
.resource-linker-container {
  border-top: 1px solid #e4e7ed;
  padding-top: 15px;
  margin-top: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .strategy-card {
    padding: 15px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .card-actions {
    margin-left: 0;
    align-self: flex-end;
  }
  
  .metric-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .time-header {
    flex-direction: column;
    gap: 5px;
    text-align: center;
  }
  
  .config-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}

/* Element UI 覆盖样式 */
.el-progress-bar__outer {
  background-color: #e4e7ed;
}

.el-slider__runway {
  background-color: #e4e7ed;
}

.el-switch.is-checked .el-switch__core {
  background-color: #409EFF;
}
</style> 