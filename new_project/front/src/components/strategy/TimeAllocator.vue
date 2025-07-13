<template>
  <div class="time-allocator">
    <!-- 时间总览 -->
    <div class="time-overview">
      <div class="overview-header">
        <h4>时间分配概览</h4>
        <div class="time-summary">
          <span class="used-time" :class="{ overtime: totalUsedTime > total }">
            {{ totalUsedTime }}/{{ total }}分钟
          </span>
          <span class="remaining-time" :class="getTimeStatus()">
            剩余{{ remainingTime }}分钟
          </span>
        </div>
      </div>
      
      <!-- 时间进度条 -->
      <div class="time-progress">
        <el-progress 
          :percentage="timeUtilization" 
          :stroke-width="12"
          :color="getProgressColor()"
          :show-text="false"
        ></el-progress>
        <div class="progress-markers">
          <span class="marker optimal" style="left: 70%">建议最低</span>
          <span class="marker warning" style="left: 90%">接近上限</span>
        </div>
      </div>
    </div>

    <!-- 策略时间分配列表 -->
    <div class="strategy-allocation-list">
      <div class="allocation-header">
        <span>策略</span>
        <span>时间分配</span>
        <span>建议时间</span>
        <span>操作</span>
      </div>
      
      <div class="allocation-items">
        <div 
          v-for="strategy in strategies" 
          :key="strategy.id"
          class="allocation-item"
          :class="{ 'over-recommended': timing[strategy.id] > strategy.recommendedTime * 1.5 }"
        >
          <div class="strategy-info">
            <div class="strategy-icon" :class="strategy.type">
              <i :class="getStrategyIcon(strategy.type)"></i>
            </div>
            <div class="strategy-details">
              <span class="strategy-name">{{ strategy.name }}</span>
              <span class="strategy-type">{{ getTypeLabel(strategy.type) }}</span>
            </div>
          </div>
          
          <div class="time-control">
            <el-input-number
              v-model="localTiming[strategy.id]"
              :min="1"
              :max="total - getTotalOtherTime(strategy.id)"
              :step="1"
              size="small"
              @change="updateTime(strategy.id, $event)"
            />
            <span class="time-unit">分钟</span>
          </div>
          
          <div class="recommended-time">
            <span class="recommended-value">{{ strategy.recommendedTime }}分钟</span>
            <el-button 
              size="mini" 
              type="text" 
              @click="setRecommendedTime(strategy.id)"
              :disabled="localTiming[strategy.id] === strategy.recommendedTime"
            >
              应用推荐
            </el-button>
          </div>
          
          <div class="allocation-actions">
            <el-tooltip content="智能优化时间分配" placement="top">
              <el-button 
                size="mini" 
                type="primary" 
                icon="el-icon-magic-stick"
                @click="optimizeStrategy(strategy.id)"
              >
              </el-button>
            </el-tooltip>
            <el-tooltip content="查看时间分布详情" placement="top">
              <el-button 
                size="mini" 
                type="info" 
                icon="el-icon-pie-chart"
                @click="showStrategyBreakdown(strategy.id)"
              >
              </el-button>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>

    <!-- 智能分配工具 -->
    <div class="allocation-tools">
      <div class="tools-header">
        <h4>智能分配工具</h4>
      </div>
      
      <div class="tools-content">
        <div class="auto-allocation">
          <div class="allocation-modes">
            <el-radio-group v-model="allocationMode" @change="applyAllocationMode">
              <el-radio-button label="balanced">均衡分配</el-radio-button>
              <el-radio-button label="priority">优先级分配</el-radio-button>
              <el-radio-button label="difficulty">难度导向</el-radio-button>
              <el-radio-button label="efficiency">效率优先</el-radio-button>
            </el-radio-group>
          </div>
          
          <el-button 
            type="primary" 
            icon="el-icon-refresh"
            @click="autoAllocateTime"
            :loading="isOptimizing"
          >
            智能重新分配
          </el-button>
        </div>
        
        <div class="allocation-insights">
          <div class="insight" v-for="insight in allocationInsights" :key="insight.type">
            <div class="insight-icon" :class="insight.type">
              <i :class="insight.icon"></i>
            </div>
            <div class="insight-content">
              <span class="insight-title">{{ insight.title }}</span>
              <span class="insight-description">{{ insight.description }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 时间分布可视化 -->
    <div class="time-visualization">
      <div class="visualization-header">
        <h4>时间分布可视化</h4>
        <el-switch
          v-model="showPercentage"
          active-text="百分比"
          inactive-text="时间"
        ></el-switch>
      </div>
      
      <div class="visualization-content">
        <!-- 饼图 -->
        <div class="pie-chart-container">
          <canvas ref="pieChart" width="200" height="200"></canvas>
          <div class="chart-legend">
            <div 
              v-for="(strategy, index) in strategies" 
              :key="strategy.id"
              class="legend-item"
            >
              <div 
                class="legend-color" 
                :style="{ backgroundColor: getStrategyColor(index) }"
              ></div>
              <span class="legend-label">{{ strategy.name }}</span>
              <span class="legend-value">
                {{ showPercentage 
                  ? Math.round((localTiming[strategy.id] / totalUsedTime) * 100) + '%'
                  : localTiming[strategy.id] + '分钟'
                }}
              </span>
            </div>
          </div>
        </div>
        
        <!-- 时间轴 -->
        <div class="timeline-container">
          <div class="timeline-header">
            <span>课程时间轴</span>
            <span>{{ total }}分钟课程</span>
          </div>
          <div class="timeline">
            <div 
              v-for="(strategy, index) in strategies" 
              :key="strategy.id"
              class="timeline-segment"
              :style="{ 
                width: (localTiming[strategy.id] / total * 100) + '%',
                backgroundColor: getStrategyColor(index)
              }"
              @click="showStrategyDetails(strategy)"
            >
              <div class="segment-label">
                {{ strategy.name }}
                <br>
                {{ localTiming[strategy.id] }}分钟
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 策略详情对话框 -->
    <el-dialog
      :title="selectedStrategy ? selectedStrategy.name + ' - 时间分配详情' : ''"
      :visible.sync="showStrategyDialog"
      width="50%"
    >
      <div v-if="selectedStrategy" class="strategy-breakdown">
        <div class="breakdown-section">
          <h5>活动分解</h5>
          <div class="activity-list">
            <div 
              v-for="activity in getStrategyActivities(selectedStrategy)" 
              :key="activity.name"
              class="activity-item"
            >
              <span class="activity-name">{{ activity.name }}</span>
              <span class="activity-time">{{ activity.time }}分钟</span>
              <div class="activity-progress">
                <el-progress 
                  :percentage="(activity.time / localTiming[selectedStrategy.id]) * 100"
                  :stroke-width="6"
                  :show-text="false"
                ></el-progress>
              </div>
            </div>
          </div>
        </div>
        
        <div class="breakdown-section">
          <h5>时间优化建议</h5>
          <ul class="optimization-suggestions">
            <li v-for="suggestion in getOptimizationSuggestions(selectedStrategy)" :key="suggestion">
              {{ suggestion }}
            </li>
          </ul>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'TimeAllocator',
  props: {
    total: {
      type: Number,
      default: 45
    },
    strategies: {
      type: Array,
      default: () => []
    },
    timing: {
      type: Object,
      default: () => ({})
    }
  },
  
  data() {
    return {
      localTiming: { ...this.timing },
      allocationMode: 'balanced',
      isOptimizing: false,
      showPercentage: false,
      showStrategyDialog: false,
      selectedStrategy: null,
      
      strategyColors: [
        '#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399',
        '#3f51b5', '#00bcd4', '#4caf50', '#ff9800', '#9c27b0'
      ]
    }
  },
  
  computed: {
    totalUsedTime() {
      return Object.values(this.localTiming).reduce((sum, time) => sum + (time || 0), 0)
    },
    
    remainingTime() {
      return Math.max(0, this.total - this.totalUsedTime)
    },
    
    timeUtilization() {
      return Math.min((this.totalUsedTime / this.total) * 100, 100)
    },
    
    allocationInsights() {
      const insights = []
      
      if (this.totalUsedTime > this.total) {
        insights.push({
          type: 'error',
          icon: 'el-icon-warning',
          title: '时间超限',
          description: `超出${this.totalUsedTime - this.total}分钟，需要调整分配`
        })
      } else if (this.totalUsedTime < this.total * 0.7) {
        insights.push({
          type: 'warning',
          icon: 'el-icon-info',
          title: '时间利用不足',
          description: `还有${this.remainingTime}分钟可以利用`
        })
      } else {
        insights.push({
          type: 'success',
          icon: 'el-icon-success',
          title: '分配合理',
          description: '时间分配符合教学节奏要求'
        })
      }
      
      // 检查是否有策略时间过长
      const overTimeStrategies = this.strategies.filter(s => 
        this.localTiming[s.id] > s.recommendedTime * 2
      )
      if (overTimeStrategies.length > 0) {
        insights.push({
          type: 'warning',
          icon: 'el-icon-time',
          title: '部分策略时间过长',
          description: `${overTimeStrategies.map(s => s.name).join('、')}时间分配可能过多`
        })
      }
      
      return insights
    }
  },
  
  watch: {
    timing: {
      handler(newTiming) {
        this.localTiming = { ...newTiming }
      },
      deep: true
    },
    
    localTiming: {
      handler() {
        this.$nextTick(() => {
          this.renderPieChart()
        })
      },
      deep: true
    }
  },
  
  mounted() {
    this.initializeTiming()
    this.$nextTick(() => {
      this.renderPieChart()
    })
  },
  
  methods: {
    initializeTiming() {
      this.strategies.forEach(strategy => {
        if (!(strategy.id in this.localTiming)) {
          this.$set(this.localTiming, strategy.id, strategy.recommendedTime || 10)
        }
      })
    },
    
    updateTime(strategyId, newTime) {
      this.$set(this.localTiming, strategyId, newTime)
      this.$emit('allocation-change', this.localTiming)
    },
    
    getTotalOtherTime(excludeId) {
      return Object.entries(this.localTiming)
        .filter(([id]) => parseInt(id) !== excludeId)
        .reduce((sum, [, time]) => sum + time, 0)
    },
    
    setRecommendedTime(strategyId) {
      const strategy = this.strategies.find(s => s.id === strategyId)
      if (strategy) {
        this.updateTime(strategyId, strategy.recommendedTime)
      }
    },
    
    optimizeStrategy(strategyId) {
      const strategy = this.strategies.find(s => s.id === strategyId)
      if (!strategy) return
      
      // 基于策略类型和重要性优化时间
      let optimizedTime = strategy.recommendedTime
      
      if (strategy.type === 'core') {
        optimizedTime = Math.ceil(strategy.recommendedTime * 1.2)
      } else if (strategy.type === 'assessment') {
        optimizedTime = Math.ceil(strategy.recommendedTime * 0.8)
      }
      
      // 确保不超过总时间限制
      const maxAllowedTime = this.total - this.getTotalOtherTime(strategyId)
      optimizedTime = Math.min(optimizedTime, maxAllowedTime)
      
      this.updateTime(strategyId, optimizedTime)
      this.$message.success(`已优化 ${strategy.name} 的时间分配`)
    },
    
    applyAllocationMode() {
      // 根据选择的模式重新分配时间
      this.autoAllocateTime()
    },
    
    autoAllocateTime() {
      this.isOptimizing = true
      
      setTimeout(() => {
        const totalRecommended = this.strategies.reduce((sum, s) => sum + s.recommendedTime, 0)
        const ratio = this.total / totalRecommended
        
        switch (this.allocationMode) {
          case 'balanced':
            this.strategies.forEach(strategy => {
              this.updateTime(strategy.id, Math.round(strategy.recommendedTime * ratio))
            })
            break
            
          case 'priority':
            this.allocateByPriority()
            break
            
          case 'difficulty':
            this.allocateByDifficulty()
            break
            
          case 'efficiency':
            this.allocateByEfficiency()
            break
        }
        
        this.isOptimizing = false
        this.$message.success('时间分配已优化')
      }, 1000)
    },
    
    allocateByPriority() {
      const coreStrategies = this.strategies.filter(s => s.type === 'core')
      const otherStrategies = this.strategies.filter(s => s.type !== 'core')
      
      const coreTime = Math.floor(this.total * 0.7)
      const otherTime = this.total - coreTime
      
      // 核心策略分配更多时间
      coreStrategies.forEach(strategy => {
        const time = Math.floor(coreTime / coreStrategies.length)
        this.updateTime(strategy.id, time)
      })
      
      otherStrategies.forEach(strategy => {
        const time = Math.floor(otherTime / otherStrategies.length)
        this.updateTime(strategy.id, time)
      })
    },
    
    allocateByDifficulty() {
      // 根据策略难度分配时间，困难的分配更多时间
      const difficultyWeights = {
        easy: 1,
        medium: 1.3,
        hard: 1.6
      }
      
      const totalWeight = this.strategies.reduce((sum, s) => {
        return sum + (difficultyWeights[s.difficulty] || 1)
      }, 0)
      
      this.strategies.forEach(strategy => {
        const weight = difficultyWeights[strategy.difficulty] || 1
        const time = Math.round(this.total * (weight / totalWeight))
        this.updateTime(strategy.id, time)
      })
    },
    
    allocateByEfficiency() {
      // 根据策略效果分配时间，高效策略分配更多时间
      const totalEffectiveness = this.strategies.reduce((sum, s) => sum + s.effectiveness, 0)
      
      this.strategies.forEach(strategy => {
        const ratio = strategy.effectiveness / totalEffectiveness
        const time = Math.round(this.total * ratio)
        this.updateTime(strategy.id, time)
      })
    },
    
    showStrategyBreakdown(strategyId) {
      this.selectedStrategy = this.strategies.find(s => s.id === strategyId)
      this.showStrategyDialog = true
    },
    
    showStrategyDetails(strategy) {
      this.showStrategyBreakdown(strategy.id)
    },
    
    getStrategyActivities(strategy) {
      // 根据策略类型返回活动分解
      const timeDistribution = {
        1: [ // 概念导入
          { name: '导入活动', time: 2 },
          { name: '概念解释', time: 4 },
          { name: '案例分析', time: 2 }
        ],
        2: [ // 互动讨论
          { name: '问题提出', time: 2 },
          { name: '小组讨论', time: 6 },
          { name: '成果分享', time: 4 }
        ],
        3: [ // 实践操作
          { name: '操作演示', time: 3 },
          { name: '学生实践', time: 10 },
          { name: '结果检查', time: 2 }
        ]
      }
      
      return timeDistribution[strategy.id] || [
        { name: '主要活动', time: this.localTiming[strategy.id] }
      ]
    },
    
    getOptimizationSuggestions(strategy) {
      const suggestions = []
      const currentTime = this.localTiming[strategy.id]
      const recommendedTime = strategy.recommendedTime
      
      if (currentTime > recommendedTime * 1.5) {
        suggestions.push('当前时间分配较多，可考虑精简活动内容')
        suggestions.push('建议将部分内容转移到其他环节')
      } else if (currentTime < recommendedTime * 0.7) {
        suggestions.push('时间分配较少，可能影响教学效果')
        suggestions.push('建议增加互动或练习环节')
      } else {
        suggestions.push('时间分配合理，符合教学节奏')
      }
      
      return suggestions
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
    
    getTimeStatus() {
      if (this.totalUsedTime > this.total) return 'overtime'
      if (this.remainingTime < this.total * 0.1) return 'warning'
      return 'normal'
    },
    
    getProgressColor() {
      if (this.totalUsedTime > this.total) return '#F56C6C'
      if (this.timeUtilization >= 90) return '#E6A23C'
      if (this.timeUtilization >= 70) return '#67C23A'
      return '#409EFF'
    },
    
    getStrategyColor(index) {
      return this.strategyColors[index % this.strategyColors.length]
    },
    
    renderPieChart() {
      const canvas = this.$refs.pieChart
      if (!canvas || this.strategies.length === 0) return
      
      const ctx = canvas.getContext('2d')
      const centerX = canvas.width / 2
      const centerY = canvas.height / 2
      const radius = 80
      
      // 清空画布
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      
      let currentAngle = 0
      const total = this.totalUsedTime || 1
      
      this.strategies.forEach((strategy, index) => {
        const time = this.localTiming[strategy.id] || 0
        const sliceAngle = (time / total) * 2 * Math.PI
        
        // 绘制扇形
        ctx.beginPath()
        ctx.moveTo(centerX, centerY)
        ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle)
        ctx.closePath()
        ctx.fillStyle = this.getStrategyColor(index)
        ctx.fill()
        ctx.strokeStyle = '#fff'
        ctx.lineWidth = 2
        ctx.stroke()
        
        currentAngle += sliceAngle
      })
    }
  }
}
</script>

<style scoped>
.time-allocator {
  padding: 20px;
}

/* 时间总览 */
.time-overview {
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.05));
  border-radius: 12px;
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.overview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.overview-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.time-summary {
  display: flex;
  gap: 15px;
  align-items: center;
}

.used-time {
  font-size: 18px;
  font-weight: 600;
  color: #409EFF;
}

.used-time.overtime {
  color: #F56C6C;
}

.remaining-time {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 12px;
  background: #67C23A;
  color: white;
}

.remaining-time.warning {
  background: #E6A23C;
}

.remaining-time.overtime {
  background: #F56C6C;
}

.time-progress {
  position: relative;
}

.progress-markers {
  position: absolute;
  top: 20px;
  width: 100%;
  height: 20px;
}

.marker {
  position: absolute;
  font-size: 10px;
  color: #909399;
  transform: translateX(-50%);
}

.marker.optimal {
  color: #67C23A;
}

.marker.warning {
  color: #E6A23C;
}

/* 策略分配列表 */
.strategy-allocation-list {
  margin-bottom: 30px;
}

.allocation-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
  margin-bottom: 10px;
}

.allocation-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.allocation-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 15px;
  padding: 15px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  align-items: center;
  transition: all 0.3s ease;
}

.allocation-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.allocation-item.over-recommended {
  border-color: #E6A23C;
  background: rgba(230, 162, 60, 0.05);
}

.strategy-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.strategy-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
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

.strategy-details {
  display: flex;
  flex-direction: column;
}

.strategy-name {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 2px;
}

.strategy-type {
  font-size: 12px;
  color: #909399;
}

.time-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-unit {
  font-size: 12px;
  color: #909399;
}

.recommended-time {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.recommended-value {
  font-size: 13px;
  color: #606266;
}

.allocation-actions {
  display: flex;
  gap: 5px;
}

/* 智能分配工具 */
.allocation-tools {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.tools-header h4 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.tools-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.auto-allocation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.allocation-insights {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.insight {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border-left: 4px solid transparent;
}

.insight.error {
  border-left-color: #F56C6C;
}

.insight.warning {
  border-left-color: #E6A23C;
}

.insight.success {
  border-left-color: #67C23A;
}

.insight-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
}

.insight-icon.error {
  background: #F56C6C;
}

.insight-icon.warning {
  background: #E6A23C;
}

.insight-icon.success {
  background: #67C23A;
}

.insight-content {
  display: flex;
  flex-direction: column;
}

.insight-title {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 2px;
}

.insight-description {
  font-size: 12px;
  color: #606266;
}

/* 时间分布可视化 */
.time-visualization {
  margin-bottom: 30px;
}

.visualization-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.visualization-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.visualization-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.pie-chart-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.chart-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 6px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 3px;
}

.legend-label {
  flex: 1;
  font-size: 13px;
  color: #2c3e50;
}

.legend-value {
  font-size: 13px;
  font-weight: 600;
  color: #409EFF;
}

.timeline-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
}

.timeline {
  display: flex;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.timeline-segment {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.timeline-segment:hover {
  transform: scaleY(1.1);
  z-index: 1;
}

.segment-label {
  color: white;
  font-size: 11px;
  font-weight: 500;
  text-align: center;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  line-height: 1.2;
}

/* 策略详情对话框 */
.strategy-breakdown {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.breakdown-section h5 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.activity-item {
  display: grid;
  grid-template-columns: 2fr 1fr 3fr;
  gap: 15px;
  align-items: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

.activity-name {
  font-size: 13px;
  color: #2c3e50;
}

.activity-time {
  font-size: 13px;
  font-weight: 600;
  color: #409EFF;
}

.optimization-suggestions {
  margin: 0;
  padding-left: 20px;
}

.optimization-suggestions li {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .time-summary {
    flex-direction: column;
    gap: 8px;
  }
  
  .allocation-header,
  .allocation-item {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .auto-allocation {
    flex-direction: column;
    align-items: stretch;
  }
  
  .visualization-content {
    grid-template-columns: 1fr;
  }
  
  .activity-item {
    grid-template-columns: 1fr;
    text-align: center;
  }
}
</style> 