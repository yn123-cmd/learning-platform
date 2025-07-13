<template>
  <div class="difficulty-slider">
    <div class="slider-controls">
      <el-button size="small" @click="resetAll">重置所有</el-button>
      <el-button type="primary" size="small" @click="applyToAll">应用到全部</el-button>
    </div>

    <div class="points-sliders">
      <div 
        v-for="point in points" 
        :key="point.id"
        class="point-slider-item"
      >
        <div class="point-info">
          <span class="point-name">{{ point.name }}</span>
          <el-tag 
            :type="getDifficultyType(point.difficulty)" 
            size="mini"
          >
            {{ getDifficultyLabel(point.difficulty) }}
          </el-tag>
        </div>
        
        <div class="slider-container">
          <el-slider
            v-model="point.difficultyLevel"
            :min="1"
            :max="5"
            :marks="difficultyMarks"
            @change="updatePointDifficulty(point)"
            class="difficulty-slider-control"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DifficultySlider',
  props: {
    points: {
      type: Array,
      default: () => []
    },
    difficultyPoints: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      difficultyMarks: {
        1: '很简单',
        2: '简单',
        3: '中等',
        4: '困难',
        5: '很困难'
      }
    }
  },
  methods: {
    updatePointDifficulty(point) {
      const level = point.difficultyLevel
      if (level <= 2) {
        point.difficulty = 'easy'
      } else if (level <= 3) {
        point.difficulty = 'medium'
      } else {
        point.difficulty = 'hard'
      }
      
      this.$emit('difficulty-changed', point.id, point.difficulty)
    },
    
    resetAll() {
      this.points.forEach(point => {
        point.difficultyLevel = 3
        point.difficulty = 'medium'
      })
      this.$message.info('已重置所有难度等级')
    },
    
    applyToAll() {
      // 应用第一个点的难度到所有点
      if (this.points.length > 0) {
        const firstLevel = this.points[0].difficultyLevel
        this.points.forEach(point => {
          point.difficultyLevel = firstLevel
          this.updatePointDifficulty(point)
        })
        this.$message.success('已应用难度等级到所有知识点')
      }
    },
    
    getDifficultyType(difficulty) {
      const types = {
        easy: 'success',
        medium: 'warning',
        hard: 'danger'
      }
      return types[difficulty] || 'info'
    },
    
    getDifficultyLabel(difficulty) {
      const labels = {
        easy: '简单',
        medium: '中等',
        hard: '困难'
      }
      return labels[difficulty] || '未知'
    }
  },
  
  mounted() {
    // 初始化难度等级
    this.points.forEach(point => {
      if (!point.difficultyLevel) {
        point.difficultyLevel = point.difficulty === 'easy' ? 2 : 
                               point.difficulty === 'medium' ? 3 : 4
      }
    })
  }
}
</script>

<style scoped>
.difficulty-slider {
  padding: 20px;
}

.slider-controls {
  text-align: center;
  margin-bottom: 20px;
}

.slider-controls .el-button {
  margin: 0 8px;
}

.points-sliders {
  display: grid;
  gap: 16px;
}

.point-slider-item {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
}

.point-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.point-name {
  font-weight: 600;
  color: #2c3e50;
}

.slider-container {
  padding: 0 16px;
}

.difficulty-slider-control {
  width: 100%;
}
</style> 