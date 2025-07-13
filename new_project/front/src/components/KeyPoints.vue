<template>
  <div class="content-analysis">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2>
            <i class="el-icon-view"></i>
            教学重难点分析
          </h2>
          <p>智能解构教学内容，精准识别重难点，制定突破策略</p>
        </div>
        <div class="header-stats">
          <el-row :gutter="16">
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-number">{{ knowledgePoints.length }}</div>
                <div class="stat-label">知识点</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-number">{{ keyPoints.length }}</div>
                <div class="stat-label">重点</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="stat-item">
                <div class="stat-number">{{ difficultyPoints.length }}</div>
                <div class="stat-label">难点</div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    
    <!-- 知识点智能分解器 -->
    <div class="knowledge-decomposer-section">
      <el-card class="analysis-card">
        <div slot="header" class="card-header">
          <div class="header-left">
            <i class="el-icon-share"></i>
            <span>知识点智能分解</span>
            <el-tag type="info" size="small">AI驱动</el-tag>
          </div>
          <div class="header-actions">
            <el-button type="text" icon="el-icon-refresh" @click="redecompose" :loading="decomposing">
              重新分解
            </el-button>
            <el-button type="text" icon="el-icon-download" @click="exportMindMap">
              导出思维导图
            </el-button>
          </div>
        </div>
        
        <knowledge-decomposer 
          :topic="selectedTopic"
          :knowledge-points="knowledgePoints"
          @decompose="setKnowledgePoints"
          @point-selected="onPointSelected"
        />
      </el-card>
    </div>

    <!-- 难度预测与重点标记 -->
    <div class="difficulty-analysis-section">
      <el-row :gutter="20">
        <el-col :span="14">
          <el-card class="analysis-card">
            <div slot="header" class="card-header">
              <div class="header-left">
                <i class="el-icon-cpu"></i>
                <span>学习难度预测</span>
                <el-tag type="warning" size="small">智能算法</el-tag>
              </div>
            </div>
            
            <difficulty-predictor 
              :points="knowledgePoints"
              :grade="selectedGrade"
              :subject="selectedSubject"
              @difficulty-updated="onDifficultyUpdated"
            />
          </el-card>
        </el-col>
        
        <el-col :span="10">
          <el-card class="analysis-card">
            <div slot="header" class="card-header">
              <div class="header-left">
                <i class="el-icon-star-on"></i>
                <span>重点标记</span>
              </div>
            </div>
            
            <key-point-tagger 
              :points="knowledgePoints" 
              :key-points="keyPoints"
              @points-tagged="onPointsTagged"
            />
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 难度调节器 -->
    <div class="difficulty-slider-section">
      <el-card class="analysis-card">
        <div slot="header" class="card-header">
          <div class="header-left">
            <i class="el-icon-rank"></i>
            <span>难度等级调节</span>
          </div>
          <div class="difficulty-overview">
            <span class="difficulty-stats">
              简单: <span class="count easy">{{ getDifficultyCount('easy') }}</span>
              中等: <span class="count medium">{{ getDifficultyCount('medium') }}</span>
              困难: <span class="count hard">{{ getDifficultyCount('hard') }}</span>
            </span>
          </div>
        </div>
        
        <difficulty-slider 
          :points="knowledgePoints"
          :difficulty-points="difficultyPoints"
          @difficulty-changed="onDifficultyChanged"
        />
      </el-card>
    </div>

    <!-- 常见学习误区 -->
    <div class="misconceptions-section">
      <el-card class="analysis-card">
        <div slot="header" class="card-header">
          <div class="header-left">
            <i class="el-icon-warning-outline"></i>
            <span>常见学习误区</span>
            <el-tag type="danger" size="small">预警系统</el-tag>
          </div>
          <div class="header-actions">
            <el-button type="text" icon="el-icon-plus" @click="addCustomMisconception">
              添加误区
            </el-button>
          </div>
        </div>
        
        <misconception-list 
          :topic="selectedTopic"
          :misconceptions="misconceptions"
          @misconception-selected="onMisconceptionSelected"
        />
      </el-card>
    </div>

    <!-- 分析结果汇总 -->
    <div class="analysis-summary">
      <el-card class="summary-card">
        <div slot="header" class="card-header">
          <div class="header-left">
            <i class="el-icon-document-checked"></i>
            <span>分析结果汇总</span>
          </div>
          <div class="header-actions">
            <el-button type="text" icon="el-icon-view" @click="previewReport">
              预览报告
            </el-button>
          </div>
        </div>
        
        <div class="summary-content">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="summary-section">
                <h4><i class="el-icon-star-on"></i>教学重点</h4>
                <div class="point-list">
                  <el-tag
                    v-for="point in keyPoints"
                    :key="point.id"
                    type="success"
                    class="point-tag"
                  >
                    {{ point.name }}
                  </el-tag>
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="summary-section">
                <h4><i class="el-icon-warning"></i>教学难点</h4>
                <div class="point-list">
                  <el-tag
                    v-for="point in difficultyPoints"
                    :key="point.id"
                    type="danger"
                    class="point-tag"
                  >
                    {{ point.name }}
                  </el-tag>
                </div>
              </div>
            </el-col>
          </el-row>
          
          <div class="breakthrough-strategies">
            <h4><i class="el-icon-magic-stick"></i>突破策略建议</h4>
            <div class="strategy-list">
              <div
                v-for="strategy in breakthroughStrategies"
                :key="strategy.id"
                class="strategy-item"
              >
                <div class="strategy-header">
                  <span class="strategy-title">{{ strategy.title }}</span>
                  <el-tag :type="strategy.type" size="mini">{{ strategy.category }}</el-tag>
                </div>
                <p class="strategy-description">{{ strategy.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button size="large" @click="saveAndNext" type="primary" :disabled="!isAnalysisComplete">
        保存并继续
      </el-button>
      <el-button size="large" @click="saveDraft">
        保存草稿
      </el-button>
      <el-button size="large" @click="exportAnalysis" icon="el-icon-download">
        导出分析
      </el-button>
    </div>
  </div>
</template>

<script>
// 导入子组件
import KnowledgeDecomposer from './analysis/KnowledgeDecomposer.vue'
import DifficultyPredictor from './analysis/DifficultyPredictor.vue'
import KeyPointTagger from './analysis/KeyPointTagger.vue'
import DifficultySlider from './analysis/DifficultySlider.vue'
import MisconceptionList from './analysis/MisconceptionList.vue'

export default {
  name: 'ContentAnalysis',
  components: {
    KnowledgeDecomposer,
    DifficultyPredictor,
    KeyPointTagger,
    DifficultySlider,
    MisconceptionList
  },
  data() {
    return {
      selectedTopic: {
        id: 1,
        title: '二次函数的图像与性质',
        subject: 'math',
        grade: 9
      },
      selectedGrade: 9,
      selectedSubject: 'math',
      decomposing: false,
      knowledgePoints: [
        {
          id: 'kp1',
          name: '二次函数的定义',
          description: '理解二次函数的一般形式 y=ax²+bx+c',
          difficulty: 'easy',
          importance: 'high',
          type: 'concept'
        },
        {
          id: 'kp2',
          name: '抛物线的开口方向',
          description: '根据a的正负判断抛物线开口方向',
          difficulty: 'medium',
          importance: 'high',
          type: 'concept'
        },
        {
          id: 'kp3',
          name: '顶点坐标计算',
          description: '利用配方法或公式求抛物线顶点坐标',
          difficulty: 'hard',
          importance: 'high',
          type: 'skill'
        },
        {
          id: 'kp4',
          name: '对称轴方程',
          description: '掌握抛物线对称轴的求法',
          difficulty: 'medium',
          importance: 'medium',
          type: 'skill'
        },
        {
          id: 'kp5',
          name: '函数值的增减性',
          description: '分析二次函数在不同区间的单调性',
          difficulty: 'hard',
          importance: 'medium',
          type: 'analysis'
        },
        {
          id: 'kp6',
          name: '实际问题应用',
          description: '用二次函数解决实际问题',
          difficulty: 'hard',
          importance: 'high',
          type: 'application'
        }
      ],
      keyPoints: [],
      difficultyPoints: [],
      misconceptions: [
        {
          id: 'm1',
          title: '混淆开口方向',
          description: '学生容易混淆a>0和a<0时抛物线的开口方向',
          frequency: 'high',
          type: 'conceptual',
          solution: '通过具体例子和图像对比加强理解'
        },
        {
          id: 'm2',
          title: '顶点坐标计算错误',
          description: '配方法计算顶点坐标时符号处理错误',
          frequency: 'medium',
          type: 'procedural',
          solution: '强化配方法的步骤训练和符号规律记忆'
        },
        {
          id: 'm3',
          title: '对称轴理解偏差',
          description: '认为对称轴是一个点而不是直线',
          frequency: 'medium',
          type: 'conceptual',
          solution: '明确对称轴是直线的概念，结合图像说明'
        }
      ],
      breakthroughStrategies: []
    }
  },
  computed: {
    isAnalysisComplete() {
      return this.knowledgePoints.length > 0 && 
             this.keyPoints.length > 0 && 
             this.difficultyPoints.length > 0
    }
  },
  methods: {
    // 知识点分解相关方法
    setKnowledgePoints(points) {
      this.knowledgePoints = points
      this.autoGenerateKeyPoints()
      this.autoGenerateDifficultyPoints()
      this.generateBreakthroughStrategies()
    },
    
    redecompose() {
      this.decomposing = true
      // 模拟AI重新分解过程
      setTimeout(() => {
        this.knowledgePoints = this.generateAlternativeDecomposition()
        this.decomposing = false
        this.$message.success('知识点重新分解完成')
      }, 2000)
    },
    
    onPointSelected(point) {
      this.$message.info(`选中知识点: ${point.name}`)
    },
    
    // 难度预测相关方法
    onDifficultyUpdated(updatedPoints) {
      this.knowledgePoints = updatedPoints
      this.autoGenerateDifficultyPoints()
    },
    
    onDifficultyChanged(pointId, newDifficulty) {
      const point = this.knowledgePoints.find(p => p.id === pointId)
      if (point) {
        point.difficulty = newDifficulty
        this.autoGenerateDifficultyPoints()
      }
    },
    
    // 重点标记相关方法
    onPointsTagged(taggedPoints) {
      this.keyPoints = taggedPoints
    },
    
    // 误区处理相关方法
    onMisconceptionSelected(misconception) {
      this.$message.info(`选中误区: ${misconception.title}`)
    },
    
    addCustomMisconception() {
      this.$prompt('请输入误区描述', '添加自定义误区', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (value) {
          // 生成唯一的ID，避免重复
          const maxId = this.misconceptions.reduce((max, item) => {
            const num = parseInt(item.id.substring(1))
            return isNaN(num) ? max : Math.max(max, num)
          }, 0)
          
          this.misconceptions.push({
            id: `m${maxId + 1}`,
            title: '自定义误区',
            description: value,
            frequency: 'low',
            type: 'custom',
            solution: '待完善解决方案'
          })
          this.$message.success('误区添加成功')
        }
      }).catch(() => {
        this.$message.info('已取消添加')
      })
    },
    
    // 统计方法
    getDifficultyCount(level) {
      return this.knowledgePoints.filter(p => p.difficulty === level).length
    },
    
    // 自动生成方法
    autoGenerateKeyPoints() {
      this.keyPoints = this.knowledgePoints.filter(p => p.importance === 'high')
    },
    
    autoGenerateDifficultyPoints() {
      this.difficultyPoints = this.knowledgePoints.filter(p => p.difficulty === 'hard')
    },
    
    generateBreakthroughStrategies() {
      this.breakthroughStrategies = [
        {
          id: 's1',
          title: '图像可视化教学',
          description: '通过动态图像演示二次函数性质变化，帮助学生建立直观理解',
          category: '教学方法',
          type: 'success'
        },
        {
          id: 's2',
          title: '分层递进练习',
          description: '从简单的标准形式开始，逐步过渡到复杂的实际应用问题',
          category: '练习设计',
          type: 'info'
        },
        {
          id: 's3',
          title: '错误分析讨论',
          description: '收集学生常见错误，组织课堂讨论，从错误中学习',
          category: '互动教学',
          type: 'warning'
        }
      ]
    },
    
    generateAlternativeDecomposition() {
      // 生成替代的知识点分解方案
      return [
        {
          id: 'kp1',
          name: '二次函数基本概念',
          description: '掌握二次函数的定义域和值域概念',
          difficulty: 'easy',
          importance: 'high',
          type: 'concept'
        },
        {
          id: 'kp2',
          name: '图像特征识别',
          description: '识别抛物线的基本特征和关键点',
          difficulty: 'medium',
          importance: 'high',
          type: 'recognition'
        },
        {
          id: 'kp3',
          name: '参数影响分析',
          description: '分析a、b、c对抛物线形状和位置的影响',
          difficulty: 'hard',
          importance: 'high',
          type: 'analysis'
        },
        {
          id: 'kp4',
          name: '函数变换理解',
          description: '理解平移、伸缩变换对函数图像的影响',
          difficulty: 'medium',
          importance: 'medium',
          type: 'transformation'
        }
      ]
    },
    
    // 导出功能
    exportMindMap() {
      this.$message.info('思维导图导出功能开发中...')
    },
    
    exportAnalysis() {
      this.$message.info('正在生成分析报告...')
    },
    
    previewReport() {
      this.$message.info('报告预览功能开发中...')
    },
    
    // 保存相关方法
    saveAndNext() {
      if (!this.isAnalysisComplete) {
        this.$message.warning('请完成内容分析后再继续')
        return
      }
      
      // 保存分析数据
      const analysisData = {
        knowledgePoints: this.knowledgePoints,
        keyPoints: this.keyPoints,
        difficultyPoints: this.difficultyPoints,
        misconceptions: this.misconceptions,
        breakthroughStrategies: this.breakthroughStrategies
      }
      
      localStorage.setItem('contentAnalysis', JSON.stringify(analysisData))
      this.$message.success('内容分析已保存')
      
      // 触发步骤完成事件
      this.$emit('step-completed', 3)
      
      // 跳转到下一步
      this.$router.push('/teaching-design/student-config')
    },
    
    saveDraft() {
      const draftData = {
        knowledgePoints: this.knowledgePoints,
        keyPoints: this.keyPoints,
        difficultyPoints: this.difficultyPoints,
        misconceptions: this.misconceptions
      }
      localStorage.setItem('contentAnalysisDraft', JSON.stringify(draftData))
      this.$message.info('草稿已保存')
    }
  },
  
  mounted() {
    // 从localStorage恢复数据
    const savedData = localStorage.getItem('contentAnalysis')
    if (savedData) {
      const data = JSON.parse(savedData)
      this.knowledgePoints = data.knowledgePoints || []
      this.keyPoints = data.keyPoints || []
      this.difficultyPoints = data.difficultyPoints || []
      this.misconceptions = [...this.misconceptions, ...(data.misconceptions || [])]
      this.breakthroughStrategies = data.breakthroughStrategies || []
    } else {
      // 初始化分析
      this.autoGenerateKeyPoints()
      this.autoGenerateDifficultyPoints()
      this.generateBreakthroughStrategies()
    }
  }
}
</script>

<style scoped>
.content-analysis {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
  box-sizing: border-box;
}

/* 页面头部 */
.page-header {
  margin-bottom: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.25);
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="2" fill="rgba(255,255,255,0.1)"/></svg>') repeat;
  background-size: 20px 20px;
  opacity: 0.3;
  pointer-events: none;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.title-section h2 {
  color: white;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-section p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin: 0;
}

.header-stats {
  min-width: 300px;
}

.stat-item {
  text-align: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 16px;
  backdrop-filter: blur(10px);
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: white;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

/* 分析卡片通用样式 */
.analysis-card,
.summary-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  overflow: hidden;
  margin-bottom: 24px;
}

.analysis-card::before,
.summary-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
  padding: 16px 20px;
  border-bottom: 1px solid rgba(64, 158, 255, 0.1);
  position: relative;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left i {
  color: #667eea;
  font-size: 18px;
}

.header-left span {
  font-weight: 600;
  color: #2c3e50;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

/* 各个分析区域 */
.knowledge-decomposer-section,
.difficulty-analysis-section,
.difficulty-slider-section,
.misconceptions-section {
  margin-bottom: 24px;
}

/* 难度统计 */
.difficulty-overview {
  display: flex;
  align-items: center;
  gap: 16px;
}

.difficulty-stats {
  font-size: 14px;
  color: #5a6c7d;
}

.count {
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 4px;
}

.count.easy {
  background: rgba(46, 204, 113, 0.1);
  color: #27ae60;
}

.count.medium {
  background: rgba(243, 156, 18, 0.1);
  color: #f39c12;
}

.count.hard {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

/* 分析结果汇总 */
.analysis-summary {
  margin-bottom: 32px;
}

.summary-content {
  padding: 24px;
}

.summary-section {
  margin-bottom: 24px;
}

.summary-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.point-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.point-tag {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
}

.breakthrough-strategies {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
}

.strategy-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
  margin-top: 16px;
}

.strategy-item {
  background: linear-gradient(135deg, #f8fafe 0%, #ffffff 100%);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  padding: 16px;
  transition: all 0.3s ease;
}

.strategy-item:hover {
  border-color: #667eea;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.strategy-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.strategy-title {
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
}

.strategy-description {
  color: #5a6c7d;
  font-size: 14px;
  line-height: 1.5;
  margin: 0;
}

/* 操作按钮 */
.action-buttons {
  text-align: center;
  padding: 32px 20px;
  background: linear-gradient(135deg, rgba(248, 250, 255, 0.8) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.action-buttons .el-button {
  margin: 0 12px;
  min-width: 140px;
  height: 48px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.action-buttons .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.action-buttons .el-button--primary:hover:not(.is-disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.4);
}

.action-buttons .el-button--primary.is-disabled {
  background: linear-gradient(135deg, #bbb 0%, #999 100%);
  box-shadow: none;
}

.action-buttons .el-button:not(.el-button--primary) {
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(102, 126, 234, 0.2);
  color: #667eea;
}

.action-buttons .el-button:not(.el-button--primary):hover {
  background: rgba(102, 126, 234, 0.05);
  border-color: #667eea;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .content-analysis {
    max-width: 100%;
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .header-stats {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 16px;
  }
  
  .title-section h2 {
    font-size: 24px;
  }
  
  .difficulty-analysis-section .el-col {
    margin-bottom: 16px;
  }
  
  .strategy-list {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    padding: 24px 16px;
  }
  
  .action-buttons .el-button {
    margin: 8px;
  min-width: 120px;
  }
}

@media (max-width: 480px) {
  .point-list {
    flex-direction: column;
  }
  
  .difficulty-stats {
    font-size: 12px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style> 