<template>
  <div class="teaching-mode-selector">
    <!-- 学情分析 -->
    <div class="student-analysis">
      <h4>学情分析</h4>
      <div class="analysis-grid">
        <div class="analysis-item">
          <span class="label">学习风格：</span>
          <el-select v-model="localStudentProfile.learningStyle" size="small" @change="onProfileChange">
            <el-option label="视觉型" value="visual"></el-option>
            <el-option label="听觉型" value="auditory"></el-option>
            <el-option label="动觉型" value="kinesthetic"></el-option>
            <el-option label="读写型" value="reading"></el-option>
          </el-select>
        </div>
        <div class="analysis-item">
          <span class="label">注意力持续时间：</span>
          <el-select v-model="localStudentProfile.attentionSpan" size="small" @change="onProfileChange">
            <el-option label="短期(5-10分钟)" value="short"></el-option>
            <el-option label="中等(10-20分钟)" value="medium"></el-option>
            <el-option label="长期(20分钟以上)" value="long"></el-option>
          </el-select>
        </div>
        <div class="analysis-item">
          <span class="label">已有基础：</span>
          <el-select v-model="localStudentProfile.priorKnowledge" size="small" @change="onProfileChange">
            <el-option label="初学者" value="beginner"></el-option>
            <el-option label="中等基础" value="intermediate"></el-option>
            <el-option label="较好基础" value="advanced"></el-option>
          </el-select>
        </div>
        <div class="analysis-item">
          <span class="label">学习动机：</span>
          <el-rate v-model="motivationLevel" :max="5" size="small" @change="onMotivationChange"></el-rate>
        </div>
      </div>
    </div>

    <!-- 教学目标匹配 -->
    <div class="target-matching">
      <h4>教学目标权重分配</h4>
      <div class="target-sliders">
        <div v-for="target in targets" :key="target.id" class="target-slider">
          <div class="target-info">
            <span class="target-name">{{ target.name }}</span>
            <span class="target-type" :class="target.type">{{ getTypeLabel(target.type) }}</span>
          </div>
          <el-slider
            v-model="targetWeights[target.id]"
            :min="0"
            :max="100"
            :step="5"
            @change="onWeightChange"
            show-tooltip
          >
            <template slot-scope="{ value }">
              {{ value }}%
            </template>
          </el-slider>
        </div>
      </div>
    </div>

    <!-- 推荐教学模式 -->
    <div class="mode-recommendations">
      <h4>智能推荐教学模式</h4>
      <div class="mode-grid">
        <div 
          v-for="mode in recommendedModes" 
          :key="mode.id"
          class="mode-card"
          :class="{ active: selectedMode === mode.id, recommended: mode.isRecommended }"
          @click="selectMode(mode.id)"
        >
          <div class="mode-header">
            <div class="mode-icon">
              <i :class="mode.icon"></i>
            </div>
            <div class="mode-title">
              <h5>{{ mode.name }}</h5>
              <span v-if="mode.isRecommended" class="recommend-badge">AI推荐</span>
            </div>
          </div>
          <div class="mode-description">
            {{ mode.description }}
          </div>
          <div class="mode-features">
            <el-tag 
              v-for="feature in mode.features" 
              :key="feature"
              size="mini"
              type="info"
            >
              {{ feature }}
            </el-tag>
          </div>
          <div class="mode-metrics">
            <div class="metric">
              <span class="metric-label">适配度：</span>
              <el-progress 
                :percentage="mode.matchScore" 
                :stroke-width="6" 
                :show-text="false"
                :color="getProgressColor(mode.matchScore)"
              ></el-progress>
              <span class="metric-value">{{ mode.matchScore }}%</span>
            </div>
            <div class="metric">
              <span class="metric-label">难度系数：</span>
              <el-rate 
                :value="mode.difficulty" 
                :max="5" 
                disabled 
                size="small"
              ></el-rate>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 策略组合预览 -->
    <div class="strategy-preview" v-if="selectedMode">
      <h4>策略组合预览</h4>
      <div class="preview-timeline">
        <div 
          v-for="(strategy, index) in previewStrategies" 
          :key="index"
          class="timeline-item"
        >
          <div class="timeline-dot" :style="{ backgroundColor: strategy.color }"></div>
          <div class="timeline-content">
            <div class="strategy-name">{{ strategy.name }}</div>
            <div class="strategy-time">{{ strategy.time }}分钟</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TeachingModeSelector',
  props: {
    targets: {
      type: Array,
      default: () => []
    },
    difficulty: {
      type: Array,
      default: () => []
    },
    studentProfile: {
      type: Object,
      default: () => ({})
    }
  },
  
  data() {
    return {
      selectedMode: null,
      motivationLevel: 3,
      localStudentProfile: { ...this.studentProfile },
      targetWeights: {},
      
      availableModes: [
        {
          id: 1,
          name: '讲授式教学',
          description: '教师主导，系统性知识传授，适合理论基础建立',
          icon: 'el-icon-s-promotion',
          features: ['系统性强', '效率高', '知识完整'],
          difficulty: 2,
          suitableFor: ['auditory', 'reading'],
          timeDistribution: [
            { name: '知识讲解', time: 25, color: '#409EFF' },
            { name: '问答互动', time: 10, color: '#67C23A' },
            { name: '总结梳理', time: 10, color: '#E6A23C' }
          ]
        },
        {
          id: 2,
          name: '探究式学习',
          description: '学生主动探索，培养问题解决能力和创新思维',
          icon: 'el-icon-search',
          features: ['主动性强', '深度学习', '能力培养'],
          difficulty: 4,
          suitableFor: ['kinesthetic', 'visual'],
          timeDistribution: [
            { name: '问题提出', time: 8, color: '#409EFF' },
            { name: '小组探究', time: 20, color: '#67C23A' },
            { name: '成果展示', time: 12, color: '#E6A23C' },
            { name: '总结提升', time: 5, color: '#F56C6C' }
          ]
        },
        {
          id: 3,
          name: '混合式教学',
          description: '结合多种教学方法，平衡效率与参与度',
          icon: 'el-icon-s-grid',
          features: ['灵活多样', '适应性强', '平衡发展'],
          difficulty: 3,
          suitableFor: ['visual', 'auditory', 'kinesthetic'],
          timeDistribution: [
            { name: '概念导入', time: 8, color: '#409EFF' },
            { name: '互动讨论', time: 12, color: '#67C23A' },
            { name: '实践操作', time: 15, color: '#E6A23C' },
            { name: '反思总结', time: 10, color: '#F56C6C' }
          ]
        },
        {
          id: 4,
          name: '项目式学习',
          description: '围绕真实项目展开，提高综合应用能力',
          icon: 'el-icon-s-cooperation',
          features: ['实用性强', '综合能力', '团队协作'],
          difficulty: 5,
          suitableFor: ['kinesthetic'],
          timeDistribution: [
            { name: '项目介绍', time: 5, color: '#409EFF' },
            { name: '分组规划', time: 10, color: '#67C23A' },
            { name: '项目实施', time: 25, color: '#E6A23C' },
            { name: '展示评估', time: 5, color: '#F56C6C' }
          ]
        }
      ]
    }
  },
  
  computed: {
    recommendedModes() {
      return this.availableModes.map(mode => {
        const matchScore = this.calculateMatchScore(mode)
        return {
          ...mode,
          matchScore,
          isRecommended: matchScore >= 75
        }
      }).sort((a, b) => b.matchScore - a.matchScore)
    },
    
    previewStrategies() {
      const selectedModeData = this.availableModes.find(m => m.id === this.selectedMode)
      return selectedModeData ? selectedModeData.timeDistribution : []
    }
  },
  
  watch: {
    studentProfile: {
      handler(newProfile) {
        this.localStudentProfile = { ...newProfile }
      },
      deep: true
    },
    
    targets: {
      handler(newTargets) {
        newTargets.forEach(target => {
          if (!(target.id in this.targetWeights)) {
            this.$set(this.targetWeights, target.id, 33)
          }
        })
      },
      immediate: true
    }
  },
  
  mounted() {
    this.initializeWeights()
    this.autoSelectBestMode()
  },
  
  methods: {
    initializeWeights() {
      this.targets.forEach(target => {
        this.$set(this.targetWeights, target.id, 33)
      })
    },
    
    calculateMatchScore(mode) {
      let score = 60 // 基础分
      
      // 学习风格匹配
      if (mode.suitableFor.includes(this.localStudentProfile.learningStyle)) {
        score += 20
      }
      
      // 注意力持续时间匹配
      const attentionBonus = {
        short: mode.difficulty <= 2 ? 10 : -10,
        medium: mode.difficulty <= 4 ? 10 : 0,
        long: 5
      }
      score += attentionBonus[this.localStudentProfile.attentionSpan] || 0
      
      // 基础水平匹配
      const knowledgeBonus = {
        beginner: mode.difficulty <= 2 ? 10 : -15,
        intermediate: mode.difficulty <= 4 ? 10 : -5,
        advanced: mode.difficulty >= 3 ? 10 : 0
      }
      score += knowledgeBonus[this.localStudentProfile.priorKnowledge] || 0
      
      // 学习动机影响
      if (this.motivationLevel >= 4) {
        score += mode.difficulty >= 3 ? 10 : 0
      } else if (this.motivationLevel <= 2) {
        score += mode.difficulty <= 2 ? 10 : -10
      }
      
      return Math.min(Math.max(score, 0), 100)
    },
    
    autoSelectBestMode() {
      const bestMode = this.recommendedModes[0]
      if (bestMode && bestMode.matchScore >= 70) {
        this.selectedMode = bestMode.id
        this.$emit('mode-change', bestMode)
      }
    },
    
    selectMode(modeId) {
      this.selectedMode = modeId
      const mode = this.availableModes.find(m => m.id === modeId)
      this.$emit('mode-change', mode)
      
      // 根据选择的模式推荐策略
      const recommendedStrategies = this.getRecommendedStrategies(mode)
      this.$emit('select', recommendedStrategies)
    },
    
    getRecommendedStrategies(mode) {
      // 根据教学模式返回推荐的策略ID列表
      const strategyMap = {
        1: [1, 4, 5], // 讲授式
        2: [2, 3, 5], // 探究式
        3: [1, 2, 3, 4], // 混合式
        4: [2, 3] // 项目式
      }
      return strategyMap[mode.id] || []
    },
    
    onProfileChange() {
      this.$emit('profile-change', this.localStudentProfile)
      // 重新计算推荐模式
      this.$forceUpdate()
    },
    
    onMotivationChange(value) {
      this.localStudentProfile.motivation = value >= 4 ? 'high' : value >= 3 ? 'medium' : 'low'
      this.onProfileChange()
    },
    
    onWeightChange() {
      this.$emit('weight-change', this.targetWeights)
    },
    
    getTypeLabel(type) {
      const labels = {
        knowledge: '知识',
        skill: '技能',
        ability: '能力'
      }
      return labels[type] || type
    },
    
    getProgressColor(percentage) {
      if (percentage >= 80) return '#67C23A'
      if (percentage >= 60) return '#E6A23C'
      return '#F56C6C'
    }
  }
}
</script>

<style scoped>
.teaching-mode-selector {
  padding: 20px;
}

/* 学情分析 */
.student-analysis {
  margin-bottom: 30px;
}

.student-analysis h4 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.analysis-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.analysis-item .label {
  font-size: 14px;
  color: #606266;
  min-width: 100px;
}

/* 目标权重 */
.target-matching {
  margin-bottom: 30px;
}

.target-matching h4 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}

.target-slider {
  margin-bottom: 20px;
}

.target-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.target-name {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.target-type {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  color: white;
}

.target-type.knowledge { background: #409EFF; }
.target-type.skill { background: #67C23A; }
.target-type.ability { background: #E6A23C; }

/* 模式推荐 */
.mode-recommendations h4 {
  color: #2c3e50;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 600;
}

.mode-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.mode-card {
  background: white;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.mode-card:hover {
  border-color: #409EFF;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.mode-card.active {
  border-color: #409EFF;
  background: linear-gradient(135deg, #409EFF15, #409EFF05);
}

.mode-card.recommended::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, #67C23A, #409EFF);
  border-radius: 12px;
  z-index: -1;
}

.mode-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.mode-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #409EFF, #667eea);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.mode-title h5 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.recommend-badge {
  background: linear-gradient(45deg, #67C23A, #85ce61);
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 500;
}

.mode-description {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  margin-bottom: 15px;
}

.mode-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.mode-metrics {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.metric {
  display: flex;
  align-items: center;
  gap: 10px;
}

.metric-label {
  font-size: 12px;
  color: #909399;
  min-width: 60px;
}

.metric-value {
  font-size: 12px;
  color: #2c3e50;
  font-weight: 600;
  min-width: 30px;
}

/* 策略预览 */
.strategy-preview h4 {
  color: #2c3e50;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}

.preview-timeline {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
}

.timeline-item {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
  position: relative;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: 8px;
  top: 24px;
  width: 2px;
  height: 20px;
  background: #e4e7ed;
}

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  flex-shrink: 0;
}

.timeline-content {
  flex: 1;
}

.strategy-name {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 2px;
}

.strategy-time {
  font-size: 12px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .analysis-grid {
    grid-template-columns: 1fr;
  }
  
  .mode-grid {
    grid-template-columns: 1fr;
  }
  
  .metric {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style> 