<template>
  <div class="difficulty-predictor">
    <div class="predictor-header">
      <div class="prediction-stats">
        <el-row :gutter="12">
          <el-col :span="8">
            <div class="stat-card easy">
              <div class="stat-value">{{ easyCount }}</div>
              <div class="stat-label">简单</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-card medium">
              <div class="stat-value">{{ mediumCount }}</div>
              <div class="stat-label">中等</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-card hard">
              <div class="stat-value">{{ hardCount }}</div>
              <div class="stat-label">困难</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <div class="prediction-chart">
      <div class="chart-container" ref="chartContainer">
        <canvas ref="difficultyChart" width="400" height="200"></canvas>
      </div>
    </div>

    <div class="points-list">
      <div 
        v-for="point in points" 
        :key="point.id"
        class="difficulty-point"
        :class="`difficulty-${point.difficulty}`"
      >
        <div class="point-content">
          <div class="point-name">{{ point.name }}</div>
          <div class="difficulty-controls">
            <el-rate 
              v-model="point.difficultyLevel" 
              :max="5" 
              show-text
              :texts="difficultyTexts"
              @change="updateDifficulty(point)"
            />
          </div>
        </div>
        <div class="prediction-reason">
          <el-tooltip :content="getPredictionReason(point)" placement="top">
            <i class="el-icon-question"></i>
          </el-tooltip>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DifficultyPredictor',
  props: {
    points: {
      type: Array,
      default: () => []
    },
    grade: {
      type: Number,
      default: 9
    },
    subject: {
      type: String,
      default: 'math'
    }
  },
  data() {
    return {
      difficultyTexts: ['很简单', '简单', '中等', '困难', '很困难']
    }
  },
  computed: {
    easyCount() {
      return this.points.filter(p => p.difficulty === 'easy').length
    },
    mediumCount() {
      return this.points.filter(p => p.difficulty === 'medium').length
    },
    hardCount() {
      return this.points.filter(p => p.difficulty === 'hard').length
    }
  },
  methods: {
    updateDifficulty(point) {
      // 根据评分更新难度等级
      if (point.difficultyLevel <= 2) {
        point.difficulty = 'easy'
      } else if (point.difficultyLevel <= 3) {
        point.difficulty = 'medium'
      } else {
        point.difficulty = 'hard'
      }
      this.$emit('difficulty-updated', this.points)
    },
    
    getPredictionReason(point) {
      const reasons = {
        concept: '概念理解需要一定的抽象思维能力',
        skill: '技能掌握需要大量练习和应用',
        application: '实际应用需要综合运用多个知识点'
      }
      return reasons[point.type] || '基于历史学习数据预测'
    },
    
    drawChart() {
      // 简单的图表绘制
      const canvas = this.$refs.difficultyChart
      const ctx = canvas.getContext('2d')
      
      // 清空画布
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      
      // 绘制条形图
      const data = [this.easyCount, this.mediumCount, this.hardCount]
      const colors = ['#27ae60', '#f39c12', '#e74c3c']
      const labels = ['简单', '中等', '困难']
      
      const barWidth = 80
      const barSpacing = 40
      const maxHeight = 150
      const maxValue = Math.max(...data) || 1
      
      data.forEach((value, index) => {
        const x = index * (barWidth + barSpacing) + 50
        const height = (value / maxValue) * maxHeight
        const y = canvas.height - height - 30
        
        // 绘制条形
        ctx.fillStyle = colors[index]
        ctx.fillRect(x, y, barWidth, height)
        
        // 绘制标签
        ctx.fillStyle = '#333'
        ctx.font = '14px Arial'
        ctx.textAlign = 'center'
        ctx.fillText(labels[index], x + barWidth / 2, canvas.height - 10)
        
        // 绘制数值
        ctx.fillText(value.toString(), x + barWidth / 2, y - 10)
      })
    }
  },
  
  mounted() {
    // 初始化难度等级
    this.points.forEach(point => {
      if (!point.difficultyLevel) {
        point.difficultyLevel = point.difficulty === 'easy' ? 2 : point.difficulty === 'medium' ? 3 : 4
      }
    })
    
    this.$nextTick(() => {
      this.drawChart()
    })
  },
  
  watch: {
    points: {
      handler() {
        this.$nextTick(() => {
          this.drawChart()
        })
      },
      deep: true
    }
  }
}
</script>

<style scoped>
.difficulty-predictor {
  padding: 20px;
}

.predictor-header {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 16px;
  border-radius: 8px;
  color: white;
  font-weight: 600;
}

.stat-card.easy {
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
}

.stat-card.medium {
  background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
}

.stat-card.hard {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
}

.stat-value {
  font-size: 24px;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
}

.prediction-chart {
  margin-bottom: 30px;
  text-align: center;
}

.chart-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.points-list {
  display: grid;
  gap: 12px;
}

.difficulty-point {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid;
}

.difficulty-point.difficulty-easy {
  background: rgba(46, 204, 113, 0.1);
  border-left-color: #2ecc71;
}

.difficulty-point.difficulty-medium {
  background: rgba(243, 156, 18, 0.1);
  border-left-color: #f39c12;
}

.difficulty-point.difficulty-hard {
  background: rgba(231, 76, 60, 0.1);
  border-left-color: #e74c3c;
}

.point-content {
  flex: 1;
}

.point-name {
  font-weight: 600;
  margin-bottom: 8px;
  color: #2c3e50;
}

.difficulty-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.prediction-reason {
  margin-left: 16px;
  color: #909399;
  cursor: help;
}
</style> 