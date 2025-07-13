<template>
  <div class="strategy-designer">
    <!-- Enhanced Header -->
    <div class="page-header">
      <div class="header-content">
        <h2>步骤5/6：二次函数教学策略设计</h2>
        <p>基于学情数据的智能教学策略推荐 • {{ studentProfile.classSize }}名学生 • 平均成绩{{ studentProfile.averageScore }}分</p>
        <div class="class-profile-summary">
          <div class="profile-item">
            <i class="el-icon-user"></i>
            <span>{{ studentProfile.dominantStyle }}学习者占主导</span>
          </div>
          <div class="profile-item">
            <i class="el-icon-warning"></i>
            <span>{{ studentProfile.specialNeedsCount }}名特殊需求学生</span>
          </div>
          <div class="profile-item">
            <i class="el-icon-trends-charts"></i>
            <span>班级水平：{{ studentProfile.performanceLevel }}</span>
          </div>
        </div>
        <div class="progress-stats">
          <div class="stat-item">
            <span class="stat-number">{{ selectedStrategies.length }}</span>
            <span class="stat-label">已选策略</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ totalAllocatedTime }}</span>
            <span class="stat-label">分钟</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ strategyEfficiency }}%</span>
            <span class="stat-label">效率指数</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ cognitiveWeaknessCount }}</span>
            <span class="stat-label">重点突破</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Teaching Mode Selector -->
    <el-card class="mode-selector-card" shadow="hover">
      <div slot="header" class="card-header">
        <i class="el-icon-s-operation"></i>
        <span>智能策略推荐</span>
        <el-button 
          type="text" 
          size="small" 
          @click="refreshRecommendations"
          :loading="isGenerating"
        >
          <i class="el-icon-refresh"></i> 重新推荐
        </el-button>
      </div>
      <teaching-mode-selector 
        :targets="allTargets"
        :difficulty="difficultyPoints"
        :student-profile="studentProfile"
        @select="updateStrategies"
        @mode-change="onModeChange"
      />
    </el-card>

    <!-- 优秀教案文档参考 -->
    <el-card class="excellent-lesson-reference-card" shadow="hover">
      <div slot="header" class="card-header">
        <div class="header-main">
          <i class="el-icon-document-copy"></i>
          <span>优秀教案文档参考</span>
          <el-tag size="mini" type="success">资源库</el-tag>
        </div>
        <div class="header-actions">
          <el-button 
            type="text" 
            size="small" 
            @click="showLessonLibrary"
          >
            <i class="el-icon-search"></i> 浏览资源库
          </el-button>
        </div>
      </div>
      
      <div class="lesson-reference-content">
        <div class="reference-description">
          <p>导入同类课程的优秀教案，AI自动提取教学策略和时间安排，为您的教学设计提供专业参考</p>
        </div>
        
        <!-- 上传区域 -->
        <div class="upload-section">
          <div class="upload-area">
            <el-upload
              ref="lessonUpload"
              :auto-upload="false"
              :accept="'.pdf,.doc,.docx,.txt'"
              :on-change="handleLessonFileChange"
              :file-list="lessonFileList"
              :limit="3"
              drag
              multiple
              class="lesson-uploader"
            >
              <div class="upload-content">
                <i class="el-icon-upload2 upload-icon"></i>
                <div class="upload-text">拖拽教案文档到此处，或<em>点击上传</em></div>
                <div class="upload-hint">支持 PDF、Word、TXT 格式，最多3个文件</div>
              </div>
            </el-upload>
          </div>
          
          <div class="quick-import-section">
            <div class="section-title">
              <i class="el-icon-star-on"></i>
              <span>推荐教案</span>
            </div>
            <div class="recommended-lessons">
              <div 
                v-for="lesson in recommendedLessons" 
                :key="lesson.id"
                class="lesson-item"
                @click="importRecommendedLesson(lesson)"
              >
                <div class="lesson-icon">
                  <i :class="lesson.icon"></i>
                </div>
                <div class="lesson-info">
                  <div class="lesson-title">{{ lesson.title }}</div>
                  <div class="lesson-meta">
                    <span class="lesson-author">{{ lesson.author }}</span>
                    <span class="lesson-rating">
                      <i class="el-icon-star-on"></i>
                      {{ lesson.rating }}
                    </span>
                  </div>
                  <div class="lesson-tags">
                    <el-tag v-for="tag in lesson.tags" :key="tag" size="mini" type="info">
                      {{ tag }}
                    </el-tag>
                  </div>
                </div>
                <div class="lesson-action">
                  <el-button size="mini" type="primary" plain>
                    <i class="el-icon-download"></i>
                    导入
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- AI分析结果 -->
        <div v-if="lessonAnalysisResults.length > 0" class="analysis-results">
          <div class="results-header">
            <h4>
              <i class="el-icon-cpu"></i>
              AI分析结果
            </h4>
            <el-button size="mini" type="primary" @click="applyAnalysisResults">
              <i class="el-icon-check"></i> 应用到策略设计
            </el-button>
          </div>
          
          <div class="analysis-cards">
            <div v-for="result in lessonAnalysisResults" :key="result.fileName" class="analysis-card">
              <div class="analysis-header">
                <div class="file-info">
                  <i class="el-icon-document"></i>
                  <span class="file-name">{{ result.fileName }}</span>
                </div>
                <div class="analysis-score">
                  <span class="score">{{ result.matchScore }}%</span>
                  <span class="score-label">匹配度</span>
                </div>
              </div>
              
              <div class="extracted-strategies">
                <div class="strategy-section">
                  <h5>
                    <i class="el-icon-s-grid"></i>
                    提取的教学策略
                  </h5>
                  <div class="strategy-list">
                    <div v-for="strategy in result.extractedStrategies" :key="strategy.name" class="extracted-strategy">
                      <div class="strategy-name">{{ strategy.name }}</div>
                      <div class="strategy-time">{{ strategy.time }}分钟</div>
                      <div class="strategy-description">{{ strategy.description }}</div>
                    </div>
                  </div>
                </div>
                
                <div class="time-allocation">
                  <h5>
                    <i class="el-icon-time"></i>
                    时间分配建议
                  </h5>
                  <div class="time-breakdown">
                    <div v-for="phase in result.timeAllocation" :key="phase.phase" class="time-phase">
                      <span class="phase-name">{{ phase.phase }}</span>
                      <span class="phase-time">{{ phase.time }}分钟</span>
                      <div class="phase-bar">
                        <div 
                          class="phase-fill" 
                          :style="{ width: (phase.time / lessonDuration) * 100 + '%' }"
                        ></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="analyzingLessons" class="analyzing-state">
          <div class="analyzing-content">
            <i class="el-icon-loading analyzing-icon"></i>
            <h4>AI正在分析教案文档</h4>
            <p>正在提取教学策略和时间安排，请稍候...</p>
            <div class="analyzing-progress">
              <el-progress 
                :percentage="analysisProgress" 
                :show-text="false"
                stroke-width="6"
                color="#409EFF"
              ></el-progress>
              <span class="progress-text">{{ analysisStage }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Enhanced Strategy Combination Section -->
    <div class="strategy-combination-section">
      <!-- 策略概览与控制面板 -->
      <el-card class="strategy-overview-card" shadow="hover">
        <div slot="header" class="clean-header">
          <div class="header-main">
            <i class="el-icon-s-grid"></i>
            <h3>教学策略组合设计</h3>
          </div>
          <div class="header-actions">
            <el-button size="small" @click="selectOptimalStrategies" type="primary">
              <i class="el-icon-magic-stick"></i> 智能优化
            </el-button>
            <el-button size="small" @click="resetStrategies">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </div>
        </div>
        
        <!-- 策略统计概览 -->
        <div class="strategy-stats-overview">
          <div class="stats-row">
            <div class="stat-card" v-for="type in strategyTypes" :key="type.key">
              <div class="stat-header">
                <div class="stat-icon" :class="type.key">
                  <i :class="type.icon"></i>
                </div>
                <div class="stat-title">{{ type.name }}</div>
              </div>
              <div class="stat-content">
                <div class="stat-number">{{ getStrategyCountByType(type.key) }}</div>
                <div class="stat-label">个策略</div>
                <div class="stat-time">{{ getTimeByType(type.key) }}分钟</div>
              </div>
              <div class="stat-progress">
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ width: getTypeProgress(type.key) + '%', backgroundColor: type.color }"
                  ></div>
                </div>
                <span class="progress-text">{{ Math.round(getTypeProgress(type.key)) }}%</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 整体效率指标 -->
        <div class="efficiency-panel">
          <div class="efficiency-item">
            <span class="efficiency-label">策略效率</span>
            <div class="efficiency-value" :class="getEfficiencyClass()">
              {{ strategyEfficiency }}%
            </div>
          </div>
          <div class="efficiency-item">
            <span class="efficiency-label">时间利用</span>
            <div class="efficiency-value">
              {{ Math.round((totalAllocatedTime / lessonDuration) * 100) }}%
            </div>
          </div>
          <div class="efficiency-item">
            <span class="efficiency-label">已选/总数</span>
            <div class="efficiency-value">
              {{ selectedStrategies.length }}/{{ availableStrategies.length }}
            </div>
          </div>
        </div>
      </el-card>
      
      <!-- 策略筛选器 -->
      <div class="strategy-filter-bar">
        <div class="filter-title">
          <i class="el-icon-filter"></i>
          <span>策略筛选</span>
        </div>
        <el-radio-group v-model="filterType" size="medium" class="clean-filters">
          <el-radio-button label="all">
            <i class="el-icon-menu"></i>
            <span>全部策略</span>
          </el-radio-button>
          <el-radio-button label="core">
            <i class="el-icon-star-on"></i>
            <span>核心策略</span>
          </el-radio-button>
          <el-radio-button label="support">
            <i class="el-icon-help"></i>
            <span>辅助策略</span>
          </el-radio-button>
          <el-radio-button label="assessment">
            <i class="el-icon-finished"></i>
            <span>评估策略</span>
          </el-radio-button>
        </el-radio-group>
      </div>
      
      <!-- 策略选择网格 -->
      <div class="strategy-grid-section">
        <div class="grid-container">
          <transition-group name="strategy-item" tag="div" class="strategy-grid">
            <div 
              v-for="strategy in filteredStrategies"
              :key="strategy.id"
              class="strategy-item"
              :class="{ 
                'selected': strategy.selected,
                'type-core': strategy.type === 'core',
                'type-support': strategy.type === 'support',
                'type-assessment': strategy.type === 'assessment'
              }"
              @click="toggleStrategy(strategy.id)"
            >
              <!-- 策略类型标识 -->
              <div class="strategy-type-badge" :class="strategy.type">
                <i :class="getStrategyTypeIcon(strategy.type)"></i>
              </div>
              
              <!-- 选中状态标识 -->
              <div v-if="strategy.selected" class="selected-badge">
                <span class="order-number">{{ getStrategyOrder(strategy.id) }}</span>
              </div>
              
              <!-- 策略内容 -->
              <div class="strategy-content">
                <div class="strategy-header">
                  <h4 class="strategy-name">{{ strategy.name }}</h4>
                  <div class="strategy-meta">
                    <span class="time-tag">{{ strategyTiming[strategy.id] }}分钟</span>
                    <span class="efficiency-tag" :class="getEfficiencyLevel(strategy.effectiveness)">
                      {{ strategy.effectiveness }}%
                    </span>
                  </div>
                </div>
                
                <p class="strategy-description">{{ strategy.description }}</p>
                
                <div class="strategy-footer">
                  <div class="target-skills">
                    <span v-for="skill in strategy.targetSkills" :key="skill" class="skill-tag">
                      {{ skill }}
                    </span>
                  </div>
                  <div class="quadratic-focus">
                    <i class="el-icon-star-on"></i>
                    <span>{{ strategy.quadraticFocus }}</span>
                  </div>
                </div>
              </div>
            </div>
          </transition-group>
          
          <!-- 空状态 -->
          <div v-if="filteredStrategies.length === 0" class="empty-state">
            <div class="empty-icon">
              <i class="el-icon-box"></i>
            </div>
            <h4>暂无{{ getFilterLabel() }}策略</h4>
            <p>请选择其他类型查看更多策略</p>
            <el-button type="primary" size="small" @click="filterType = 'all'">
              查看全部策略
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- Strategy Flow Visualization -->
      <el-card v-if="selectedStrategies.length > 0" class="strategy-flow-card" shadow="hover">
        <div slot="header" class="card-header">
          <i class="el-icon-share"></i>
          <span>策略执行流程</span>
          <el-button size="mini" type="text" @click="optimizeFlow">
            <i class="el-icon-sort"></i> 优化顺序
          </el-button>
        </div>
        
        <div class="strategy-flow">
          <div class="flow-timeline">
            <div 
              v-for="(strategy, index) in orderedSelectedStrategies" 
              :key="strategy.id"
              class="flow-item"
              :class="strategy.type"
            >
              <div class="flow-node">
                <div class="node-number">{{ index + 1 }}</div>
                <div class="node-type" :class="strategy.type">
                  <i :class="getStrategyTypeIcon(strategy.type)"></i>
                </div>
              </div>
              
              <div class="flow-content">
                <div class="flow-title">{{ strategy.name }}</div>
                <div class="flow-time">{{ strategyTiming[strategy.id] }}分钟</div>
                <div class="flow-description">{{ strategy.description }}</div>
              </div>
              
              <div v-if="index < orderedSelectedStrategies.length - 1" class="flow-connector">
                <i class="el-icon-right"></i>
              </div>
            </div>
          </div>
          
          <div class="flow-summary">
            <div class="summary-card">
              <i class="el-icon-time"></i>
              <span>总计: {{ totalAllocatedTime }}分钟</span>
            </div>
            <div class="summary-card">
              <i class="el-icon-trophy"></i>
              <span>预期效果: {{ strategyEfficiency }}%</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- Time Allocation Visualizer -->
    <el-card class="time-allocator-card" shadow="hover">
      <div slot="header" class="card-header">
        <i class="el-icon-time"></i>
        <span>时间分配器</span>
        <div class="duration-controls">
          <span>课时时长：</span>
          <el-input-number
            v-model="lessonDuration"
            :min="20"
            :max="120"
            :step="5"
            size="small"
            @change="recalculateTime"
          />
          <span>分钟</span>
        </div>
      </div>
      <time-allocator 
        :total="lessonDuration" 
        :strategies="selectedStrategies"
        :timing="strategyTiming"
        @allocation-change="onAllocationChange"
      />
    </el-card>

    <!-- Strategy Analytics -->
    <el-card class="analytics-card" shadow="hover">
      <div slot="header" class="card-header">
        <i class="el-icon-data-analysis"></i>
        <span>策略分析</span>
      </div>
      <div class="analytics-content">
        <div class="analytics-chart">
          <div class="chart-container">
            <canvas ref="strategyChart" width="300" height="200"></canvas>
          </div>
        </div>
        <div class="analytics-insights">
          <div class="insight-item" v-for="insight in strategyInsights" :key="insight.type">
            <div class="insight-icon" :class="insight.type">
              <i :class="insight.icon"></i>
            </div>
            <div class="insight-content">
              <h4>{{ insight.title }}</h4>
              <p>{{ insight.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Action Buttons -->
    <div class="action-buttons">
      <el-button size="large" @click="previewStrategy" icon="el-icon-view">
        预览策略
      </el-button>
      <el-button size="large" @click="saveDraft" icon="el-icon-document">
        保存草稿
      </el-button>
      <el-button 
        size="large" 
        type="primary" 
        @click="saveAndNext"
        :disabled="!isStrategyComplete"
        icon="el-icon-right"
      >
        保存并继续
      </el-button>
    </div>
  </div>
</template>

<script>
import TeachingModeSelector from './strategy/TeachingModeSelector.vue'
import StrategyCard from './strategy/StrategyCard.vue'
import ResourceLinker from './strategy/ResourceLinker.vue'
import TimeAllocator from './strategy/TimeAllocator.vue'

export default {
  name: 'StrategyDesigner',
  components: {
    TeachingModeSelector,
    StrategyCard,
    ResourceLinker,
    TimeAllocator
  },
  data() {
    return {
      // Basic settings
      lessonDuration: 45,
      filterType: 'all',
      isGenerating: false,
      
      // Student and target data - 基于学情配置的二次函数教学目标
      allTargets: [
        { id: 1, name: '二次函数图像识别', priority: 'high', type: 'knowledge', currentLevel: 78 },
        { id: 2, name: '顶点坐标计算', priority: 'high', type: 'skill', currentLevel: 82 },
        { id: 3, name: '开口方向判断', priority: 'medium', type: 'knowledge', currentLevel: 88 },
        { id: 4, name: '对称性理解', priority: 'high', type: 'knowledge', currentLevel: 65 },
        { id: 5, name: '平移变换掌握', priority: 'medium', type: 'skill', currentLevel: 74 },
        { id: 6, name: '实际应用能力', priority: 'high', type: 'ability', currentLevel: 68 }
      ],
      difficultyPoints: [
        { id: 1, name: '对称性理解困难', level: 7, weight: 0.35, currentMastery: 65 },
        { id: 2, name: '实际应用转换', level: 8, weight: 0.35, currentMastery: 68 },
        { id: 3, name: '平移变换操作', level: 6, weight: 0.30, currentMastery: 74 }
      ],
      studentProfile: {
        learningStyle: 'visual', // 图像型占52%
        attentionSpan: 'medium',
        priorKnowledge: 'good', // 前测75分，基础良好
        motivation: 'high',
        averageScore: 78.5,
        classSize: 42,
        performanceLevel: '中上',
        specialNeedsCount: 8, // 有特殊需求的学生数量
        cognitiveStrengths: ['开口判断', '顶点计算', '图像识别'],
        cognitiveWeaknesses: ['对称性理解', '实际应用'],
        dominantStyle: '图像型'
      },
      
      // Available strategies - 基于二次函数教学和学情特征的策略
      availableStrategies: [
        {
          id: 1,
          name: '图像导入法',
          type: 'core',
          description: '通过二次函数图像直观引入概念，适合图像型学习者',
          recommendedTime: 10,
          difficulty: 'easy',
          effectiveness: 88,
          selected: true,
          suitableFor: ['visual'],
          targetSkills: ['图像识别', '直观理解'],
          quadraticFocus: '图像特征识别'
        },
        {
          id: 2,
          name: '对称性探究',
          type: 'core',
          description: '重点突破对称性理解难点，小组合作探索',
          recommendedTime: 15,
          difficulty: 'medium',
          effectiveness: 82,
          selected: true,
          suitableFor: ['visual', 'collaborative'],
          targetSkills: ['对称性理解', '合作学习'],
          quadraticFocus: '对称轴性质'
        },
        {
          id: 3,
          name: '顶点计算训练',
          type: 'core',
          description: '强化顶点坐标计算，配方法和公式法并用',
          recommendedTime: 12,
          difficulty: 'medium',
          effectiveness: 85,
          selected: true,
          suitableFor: ['analytical'],
          targetSkills: ['顶点计算', '公式应用'],
          quadraticFocus: '顶点坐标求解'
        },
        {
          id: 4,
          name: '平移变换实践',
          type: 'core',
          description: '动手操作图像平移，加深变换规律理解',
          recommendedTime: 8,
          difficulty: 'medium',
          effectiveness: 78,
          selected: false,
          suitableFor: ['kinesthetic', 'visual'],
          targetSkills: ['平移变换', '动手操作'],
          quadraticFocus: '图像变换'
        },
        {
          id: 5,
          name: '实际问题建模',
          type: 'support',
          description: '结合生活实例，提升实际应用能力',
          recommendedTime: 10,
          difficulty: 'hard',
          effectiveness: 75,
          selected: false,
          suitableFor: ['applied', 'analytical'],
          targetSkills: ['实际应用', '建模思维'],
          quadraticFocus: '实际问题应用'
        },
        {
          id: 6,
          name: '特殊需求辅导',
          type: 'support',
          description: '针对8名特殊需求学生的个别化指导',
          recommendedTime: 6,
          difficulty: 'easy',
          effectiveness: 70,
          selected: true,
          suitableFor: ['special'],
          targetSkills: ['个别化学习', '基础巩固'],
          quadraticFocus: '基础概念强化'
        },
        {
          id: 7,
          name: '开口方向巩固',
          type: 'support',
          description: '巩固已有优势，强化开口方向判断技能',
          recommendedTime: 5,
          difficulty: 'easy',
          effectiveness: 90,
          selected: false,
          suitableFor: ['all'],
          targetSkills: ['开口判断', '系数分析'],
          quadraticFocus: '开口方向识别'
        },
        {
          id: 8,
          name: '阶段性评估',
          type: 'assessment',
          description: '检测二次函数核心概念掌握情况',
          recommendedTime: 8,
          difficulty: 'medium',
          effectiveness: 85,
          selected: true,
          suitableFor: ['all'],
          targetSkills: ['综合应用', '知识检测'],
          quadraticFocus: '综合评估'
        },
        {
          id: 9,
          name: '错误分析讨论',
          type: 'assessment',
          description: '基于作业分析结果，讨论常见错误',
          recommendedTime: 6,
          difficulty: 'easy',
          effectiveness: 80,
          selected: false,
          suitableFor: ['analytical', 'collaborative'],
          targetSkills: ['错误分析', '反思总结'],
          quadraticFocus: '错误纠正'
        }
      ],
      
      // Strategy timing - 基于二次函数教学节奏调整
      strategyTiming: {
        1: 10, // 图像导入法
        2: 15, // 对称性探究
        3: 12, // 顶点计算训练
        4: 8,  // 平移变换实践
        5: 10, // 实际问题建模
        6: 6,  // 特殊需求辅导
        7: 5,  // 开口方向巩固
        8: 8,  // 阶段性评估
        9: 6   // 错误分析讨论
      },
      
      // Resources - 二次函数教学专用资源
      availableResources: [
        { id: 1, name: '二次函数图像课件', type: 'presentation', description: '包含动态图像演示' },
        { id: 2, name: '函数图像描点器', type: 'equipment', description: '帮助学生精确描点' },
        { id: 3, name: 'GeoGebra动态几何', type: 'digital', description: '在线函数图像工具' },
        { id: 4, name: '对称轴演示教具', type: 'equipment', description: '物理折叠演示对称性' },
        { id: 5, name: '顶点计算练习卡', type: 'document', description: '分层练习材料' },
        { id: 6, name: '实际问题案例库', type: 'document', description: '生活中的二次函数' },
        { id: 7, name: '错误分析视频', type: 'digital', description: '常见错误示例讲解' },
        { id: 8, name: '平移变换动画', type: 'digital', description: '图像变换可视化' }
      ],
      
      // 优秀教案文档参考相关数据
      lessonFileList: [],
      lessonAnalysisResults: [],
      analyzingLessons: false,
      analysisProgress: 0,
      analysisStage: '准备分析...',
      
      // 推荐教案数据
      recommendedLessons: [
        {
          id: 1,
          title: '二次函数图像特征探究教学设计',
          author: '张教授',
          rating: 4.8,
          icon: 'el-icon-medal',
          tags: ['图像识别', '对称性', '优质课'],
          description: '获奖教学设计，重点突破图像特征'
        },
        {
          id: 2,
          title: '二次函数实际应用案例教学',
          author: '李老师',
          rating: 4.6,
          icon: 'el-icon-trophy',
          tags: ['实际应用', '建模思维', '创新课'],
          description: '结合生活实例的创新教学方案'
        },
        {
          id: 3,
          title: '二次函数顶点式教学策略',
          author: '王特级',
          rating: 4.9,
          icon: 'el-icon-star-on',
          tags: ['顶点计算', '配方法', '示范课'],
          description: '特级教师示范课教学设计'
        }
      ]
    }
  },
  
  computed: {
    selectedStrategies() {
      return this.availableStrategies.filter(s => s.selected)
    },
    
    filteredStrategies() {
      if (this.filterType === 'all') {
        return this.availableStrategies
      }
      return this.availableStrategies.filter(s => s.type === this.filterType)
    },
    
    totalAllocatedTime() {
      return this.selectedStrategies.reduce((total, strategy) => {
        return total + (this.strategyTiming[strategy.id] || 0)
      }, 0)
    },
    
    strategyEfficiency() {
      if (this.selectedStrategies.length === 0) return 0
      const avgEffectiveness = this.selectedStrategies.reduce((sum, s) => sum + s.effectiveness, 0) / this.selectedStrategies.length
      const timeUtilization = Math.min(this.totalAllocatedTime / this.lessonDuration, 1) * 100
      return Math.round((avgEffectiveness + timeUtilization) / 2)
    },
    
    isStrategyComplete() {
      return this.selectedStrategies.length >= 2 && 
             this.totalAllocatedTime <= this.lessonDuration &&
             this.totalAllocatedTime >= this.lessonDuration * 0.7
    },
    
    strategyInsights() {
      const insights = []
      
      // 时间分配检查
      if (this.totalAllocatedTime > this.lessonDuration) {
        insights.push({
          type: 'warning',
          icon: 'el-icon-warning',
          title: '时间超限',
          description: `当前策略总时长超出课时${this.totalAllocatedTime - this.lessonDuration}分钟，建议调整`
        })
      }
      
      // 核心策略检查
      const coreStrategies = this.selectedStrategies.filter(s => s.type === 'core')
      if (coreStrategies.length < 3) {
        insights.push({
          type: 'info',
          icon: 'el-icon-info',
          title: '二次函数核心策略建议',
          description: '建议至少选择3个核心策略：图像导入、对称性探究、顶点计算'
        })
      }
      
      // 对称性理解弱项检查
      const hasSymmetryStrategy = this.selectedStrategies.some(s => 
        s.quadraticFocus === '对称轴性质'
      )
      if (!hasSymmetryStrategy) {
        insights.push({
          type: 'warning',
          icon: 'el-icon-warning',
          title: '认知弱项未覆盖',
          description: '班级对称性理解较弱(65%)，建议增加对称性探究策略'
        })
      }
      
      // 实际应用能力检查
      const hasApplicationStrategy = this.selectedStrategies.some(s => 
        s.quadraticFocus === '实际问题应用'
      )
      if (!hasApplicationStrategy) {
        insights.push({
          type: 'info',
          icon: 'el-icon-info',
          title: '实际应用建议',
          description: '班级实际应用能力偏弱(68%)，建议增加建模策略'
        })
      }
      
      // 特殊需求学生关注
      const hasSpecialStrategy = this.selectedStrategies.some(s => 
        s.suitableFor.includes('special')
      )
      if (this.studentProfile.specialNeedsCount > 0 && !hasSpecialStrategy) {
        insights.push({
          type: 'warning',
          icon: 'el-icon-warning',
          title: '特殊需求关注',
          description: `班级有${this.studentProfile.specialNeedsCount}名特殊需求学生，建议增加个别化辅导`
        })
      }
      
      // 策略效率评估
      if (this.strategyEfficiency >= 85) {
        insights.push({
          type: 'success',
          icon: 'el-icon-success',
          title: '策略组合优秀',
          description: '当前策略组合高度匹配班级学情，预期教学效果优秀'
        })
      } else if (this.strategyEfficiency >= 75) {
        insights.push({
          type: 'success',
          icon: 'el-icon-success',
          title: '策略组合良好',
          description: '策略组合较好匹配班级特征，预期教学效果良好'
        })
      }
      
      // 学习风格匹配检查
      const visualStrategies = this.selectedStrategies.filter(s => 
        s.suitableFor.includes('visual')
      )
      if (visualStrategies.length < 2) {
        insights.push({
          type: 'info',
          icon: 'el-icon-info',
          title: '学习风格匹配',
          description: '班级图像型学习者占52%，建议增加视觉化策略'
        })
      }
      
      return insights
    },
    
    strategyTypes() {
      return [
        { 
          key: 'core', 
          name: '核心策略', 
          icon: 'el-icon-star-on', 
          color: '#409EFF',
          description: '课程核心内容教学' 
        },
        { 
          key: 'support', 
          name: '辅助策略', 
          icon: 'el-icon-help', 
          color: '#67C23A',
          description: '支持和强化学习' 
        },
        { 
          key: 'assessment', 
          name: '评估策略', 
          icon: 'el-icon-finished', 
          color: '#E6A23C',
          description: '检测学习效果' 
        }
      ]
    },
    
    orderedSelectedStrategies() {
      // 按照教学逻辑排序：核心 -> 辅助 -> 评估
      const typeOrder = { 'core': 1, 'support': 2, 'assessment': 3 }
      return this.selectedStrategies.sort((a, b) => {
        if (typeOrder[a.type] !== typeOrder[b.type]) {
          return typeOrder[a.type] - typeOrder[b.type]
        }
        return a.id - b.id // 同类型按ID排序
      })
    },
    
    // 认知弱项需要重点突破的数量
    cognitiveWeaknessCount() {
      return this.studentProfile.cognitiveWeaknesses ? this.studentProfile.cognitiveWeaknesses.length : 2
    }
  },
  
  mounted() {
    this.initializeStrategies()
    this.$nextTick(() => {
      this.renderStrategyChart()
    })
  },
  
  methods: {
    initializeStrategies() {
      // Initialize based on student profile and targets
      this.recommendStrategies()
    },
    
    recommendStrategies() {
      // 基于学情配置数据的智能策略推荐
      this.availableStrategies.forEach(strategy => {
        strategy.selected = false // 先重置
        
        // 基于学习风格推荐（图像型52%占主导）
        if (this.studentProfile.learningStyle === 'visual' && 
            strategy.suitableFor.includes('visual')) {
          strategy.selected = true
        }
        
        // 基于认知弱项推荐（对称性理解65%、实际应用68%需要重点关注）
        if (this.studentProfile.cognitiveWeaknesses.includes('对称性理解') && 
            strategy.quadraticFocus === '对称轴性质') {
          strategy.selected = true
        }
        
        if (this.studentProfile.cognitiveWeaknesses.includes('实际应用') && 
            strategy.quadraticFocus === '实际问题应用') {
          strategy.selected = true
        }
        
        // 基于特殊需求学生数量推荐
        if (this.studentProfile.specialNeedsCount > 0 && 
            strategy.suitableFor.includes('special')) {
          strategy.selected = true
        }
        
        // 核心评估策略必选
        if (strategy.type === 'assessment' && strategy.quadraticFocus === '综合评估') {
          strategy.selected = true
        }
        
        // 基于班级整体水平调整
        if (this.studentProfile.performanceLevel === '中上') {
          // 中上水平班级，选择中等难度的核心策略
          if (strategy.type === 'core' && strategy.difficulty === 'medium') {
            strategy.selected = true
          }
        }
      })
    },
    
    refreshRecommendations() {
      this.isGenerating = true
      setTimeout(() => {
        this.recommendStrategies()
        this.isGenerating = false
        this.$message.success('策略推荐已更新')
      }, 1500)
    },
    
    updateStrategies(selectedStrategies) {
      this.availableStrategies.forEach(strategy => {
        strategy.selected = selectedStrategies.includes(strategy.id)
      })
      this.recalculateTime()
    },
    
    toggleStrategy(strategyId) {
      const strategy = this.availableStrategies.find(s => s.id === strategyId)
      if (strategy) {
        strategy.selected = !strategy.selected
        this.recalculateTime()
      }
    },
    
    updateStrategyTime(strategyId, newTime) {
      this.$set(this.strategyTiming, strategyId, newTime)
    },
    
    onModeChange(mode) {
      // Adjust strategies based on teaching mode
      console.log('Teaching mode changed:', mode)
    },
    
    onResourceLinked(strategyId, resource) {
      console.log('Resource linked:', strategyId, resource)
    },
    
    onAllocationChange(newTiming) {
      this.strategyTiming = { ...newTiming }
    },
    
    recalculateTime() {
      // Intelligent time reallocation
      const selected = this.selectedStrategies
      if (selected.length === 0) return
      
      const totalRecommended = selected.reduce((sum, s) => sum + s.recommendedTime, 0)
      const ratio = this.lessonDuration / totalRecommended
      
      selected.forEach(strategy => {
        this.strategyTiming[strategy.id] = Math.round(strategy.recommendedTime * ratio)
      })
    },
    
    renderStrategyChart() {
      const canvas = this.$refs.strategyChart
      if (!canvas) return
      
      const ctx = canvas.getContext('2d')
      const strategies = this.selectedStrategies
      
      // Clear canvas
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      
      // Draw pie chart for time allocation
      let currentAngle = 0
      const centerX = canvas.width / 2
      const centerY = canvas.height / 2
      const radius = 80
      
      const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
      
      strategies.forEach((strategy, index) => {
        const time = this.strategyTiming[strategy.id] || 0
        const percentage = time / this.totalAllocatedTime
        const sliceAngle = percentage * 2 * Math.PI
        
        ctx.beginPath()
        ctx.moveTo(centerX, centerY)
        ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle)
        ctx.closePath()
        ctx.fillStyle = colors[index % colors.length]
        ctx.fill()
        
        currentAngle += sliceAngle
      })
    },
    
    previewStrategy() {
      const coreStrategies = this.selectedStrategies.filter(s => s.type === 'core')
      const supportStrategies = this.selectedStrategies.filter(s => s.type === 'support')
      const assessmentStrategies = this.selectedStrategies.filter(s => s.type === 'assessment')
      
      this.$msgbox({
        title: '二次函数教学策略预览',
        message: this.$createElement('div', [
          this.$createElement('h4', { style: 'margin-bottom: 10px; color: #409EFF;' }, '班级学情匹配度'),
          this.$createElement('p', `班级规模: ${this.studentProfile.classSize}名学生 | 平均成绩: ${this.studentProfile.averageScore}分`),
          this.$createElement('p', `主导学习风格: ${this.studentProfile.dominantStyle} | 特殊需求: ${this.studentProfile.specialNeedsCount}名`),
          this.$createElement('hr', { style: 'margin: 15px 0;' }),
          this.$createElement('h4', { style: 'margin-bottom: 10px; color: #67C23A;' }, '策略配置'),
          this.$createElement('p', `总时长: ${this.totalAllocatedTime}/${this.lessonDuration}分钟`),
          this.$createElement('p', `效率指数: ${this.strategyEfficiency}%`),
          this.$createElement('p', `核心策略: ${coreStrategies.length}个 | 辅助策略: ${supportStrategies.length}个 | 评估策略: ${assessmentStrategies.length}个`),
          this.$createElement('hr', { style: 'margin: 15px 0;' }),
          this.$createElement('h4', { style: 'margin-bottom: 10px; color: #E6A23C;' }, '重点突破'),
          this.$createElement('p', '对称性理解 & 实际应用能力')
        ])
      })
    },
    
    saveAndNext() {
      // Save strategy data with student profile
      const strategyData = {
        selectedStrategies: this.selectedStrategies,
        timing: this.strategyTiming,
        duration: this.lessonDuration,
        efficiency: this.strategyEfficiency,
        studentProfile: this.studentProfile,
        targets: this.allTargets,
        difficultyPoints: this.difficultyPoints,
        subject: '二次函数的图像及其特征',
        teachingFocus: ['对称性理解', '实际应用能力'],
        classCharacteristics: {
          size: this.studentProfile.classSize,
          averageScore: this.studentProfile.averageScore,
          level: this.studentProfile.performanceLevel,
          dominantStyle: this.studentProfile.dominantStyle,
          specialNeeds: this.studentProfile.specialNeedsCount
        },
        timestamp: new Date().toISOString()
      }
      
      localStorage.setItem('teachingStrategy', JSON.stringify(strategyData))
      
      this.$emit('step-completed', 5)
      this.$router.push('/teaching-design/output')
      this.$message.success('二次函数教学策略已保存，数据与学情配置完全匹配')
    },
    
    saveDraft() {
      const draftData = {
        selectedStrategies: this.selectedStrategies,
        timing: this.strategyTiming,
        duration: this.lessonDuration,
        isDraft: true,
        timestamp: new Date().toISOString()
      }
      
      localStorage.setItem('strategyDraft', JSON.stringify(draftData))
      this.$message.info('草稿已保存')
    },
    
    // Enhanced Strategy Management Methods
    getEfficiencyClass() {
      if (this.strategyEfficiency >= 80) return 'high'
      if (this.strategyEfficiency >= 60) return 'medium'
      return 'low'
    },
    
    getEfficiencyLevel(effectiveness) {
      if (effectiveness >= 85) return 'excellent'
      if (effectiveness >= 75) return 'good'
      if (effectiveness >= 65) return 'fair'
      return 'poor'
    },
    
    getStrategyCountByType(type) {
      return this.selectedStrategies.filter(s => s.type === type).length
    },
    
    getTimeByType(type) {
      return this.selectedStrategies
        .filter(s => s.type === type)
        .reduce((total, s) => total + (this.strategyTiming[s.id] || 0), 0)
    },
    
    getTypeProgress(type) {
      const typeTime = this.getTimeByType(type)
      return this.totalAllocatedTime > 0 ? (typeTime / this.totalAllocatedTime) * 100 : 0
    },
    
    getStrategyOrder(strategyId) {
      const index = this.orderedSelectedStrategies.findIndex(s => s.id === strategyId)
      return index + 1
    },
    
    getFilterLabel() {
      const labels = {
        'all': '全部',
        'core': '核心',
        'support': '辅助',
        'assessment': '评估'
      }
      return labels[this.filterType] || '未知'
    },
    
    getStrategyTypeIcon(type) {
      const icons = {
        'core': 'el-icon-star-on',
        'support': 'el-icon-help',
        'assessment': 'el-icon-finished'
      }
      return icons[type] || 'el-icon-circle-check'
    },
    
    selectOptimalStrategies() {
      // 基于学情数据的智能策略优化
      this.availableStrategies.forEach(strategy => {
        strategy.selected = false // 先重置
        
        // 1. 必选：针对认知弱项的策略
        if (strategy.quadraticFocus === '对称轴性质' || 
            strategy.quadraticFocus === '实际问题应用') {
          strategy.selected = true
        }
        
        // 2. 必选：适合图像型学习者的高效策略
        if (strategy.suitableFor.includes('visual') && 
            strategy.effectiveness >= 85) {
          strategy.selected = true
        }
        
        // 3. 必选：特殊需求学生辅导
        if (strategy.suitableFor.includes('special')) {
          strategy.selected = true
        }
        
        // 4. 必选：核心评估策略
        if (strategy.type === 'assessment' && 
            strategy.quadraticFocus === '综合评估') {
          strategy.selected = true
        }
        
        // 5. 基于班级水平选择合适难度
        if (this.studentProfile.performanceLevel === '中上' && 
            strategy.type === 'core' && 
            strategy.difficulty === 'medium' &&
            strategy.effectiveness >= 80) {
          strategy.selected = true
        }
      })
      
      this.recalculateTime()
      this.$message.success('已基于班级学情应用最优策略组合')
    },
    
    resetStrategies() {
      this.availableStrategies.forEach(strategy => {
        strategy.selected = false
      })
      this.$message.info('已重置策略选择')
    },
    
    optimizeFlow() {
      // 优化策略执行顺序
      this.$message.success('策略执行顺序已优化')
    },
    
    // 优秀教案文档参考相关方法
    handleLessonFileChange(file, fileList) {
      this.lessonFileList = fileList
      this.analyzeLessonFiles()
    },
    
    analyzeLessonFiles() {
      if (this.lessonFileList.length === 0) return
      
      this.analyzingLessons = true
      this.analysisProgress = 0
      this.analysisStage = '正在读取文档内容...'
      
      // 模拟AI分析过程
      const stages = [
        { progress: 20, stage: '正在解析文档结构...' },
        { progress: 40, stage: '正在提取教学策略...' },
        { progress: 60, stage: '正在分析时间安排...' },
        { progress: 80, stage: '正在计算匹配度...' },
        { progress: 100, stage: '分析完成' }
      ]
      
      let currentStage = 0
      const interval = setInterval(() => {
        if (currentStage < stages.length) {
          this.analysisProgress = stages[currentStage].progress
          this.analysisStage = stages[currentStage].stage
          currentStage++
        } else {
          clearInterval(interval)
          this.generateAnalysisResults()
        }
      }, 800)
    },
    
    generateAnalysisResults() {
      // 模拟生成AI分析结果
      this.lessonAnalysisResults = this.lessonFileList.map((file, index) => ({
        fileName: file.name,
        matchScore: 85 + Math.floor(Math.random() * 10), // 85-94%的匹配度
        extractedStrategies: [
          {
            name: '图像导入引入',
            time: 8,
            description: '通过生活实例引入二次函数图像概念'
          },
          {
            name: '对称性探究',
            time: 15,
            description: '学生小组合作探索图像对称性质'
          },
          {
            name: '顶点坐标计算',
            time: 12,
            description: '教师示范配方法求顶点坐标'
          },
          {
            name: '实际应用练习',
            time: 10,
            description: '结合物理抛物运动强化应用'
          }
        ],
        timeAllocation: [
          { phase: '导入新课', time: 8 },
          { phase: '新知探究', time: 20 },
          { phase: '巩固练习', time: 12 },
          { phase: '总结提升', time: 5 }
        ]
      }))
      
      this.analyzingLessons = false
      this.$message.success(`成功分析${this.lessonFileList.length}个教案文档，已提取教学策略`)
    },
    
    importRecommendedLesson(lesson) {
      this.$message.success(`正在导入《${lesson.title}》教案`)
      
      // 模拟导入推荐教案
      setTimeout(() => {
        const mockResult = {
          fileName: lesson.title + '.pdf',
          matchScore: lesson.rating * 20, // 根据评分计算匹配度
          extractedStrategies: [
            {
              name: '概念可视化导入',
              time: 10,
              description: lesson.description
            },
            {
              name: '互动探究活动',
              time: 18,
              description: '基于优秀教案的探究设计'
            },
            {
              name: '分层练习设计',
              time: 12,
              description: '适应不同水平学生的练习'
            }
          ],
          timeAllocation: [
            { phase: '情境导入', time: 10 },
            { phase: '探究学习', time: 18 },
            { phase: '练习巩固', time: 12 },
            { phase: '课堂小结', time: 5 }
          ]
        }
        
        this.lessonAnalysisResults.push(mockResult)
        this.$message.success(`已成功导入并分析《${lesson.title}》`)
      }, 1500)
    },
    
    applyAnalysisResults() {
      if (this.lessonAnalysisResults.length === 0) return
      
      // 将分析结果应用到策略设计中
      const bestResult = this.lessonAnalysisResults.reduce((best, current) => 
        current.matchScore > best.matchScore ? current : best
      )
      
      // 更新策略时间分配
      bestResult.extractedStrategies.forEach(strategy => {
        const matchingStrategy = this.availableStrategies.find(s => 
          s.name.includes(strategy.name.substring(0, 3)) || 
          strategy.name.includes(s.name.substring(0, 3))
        )
        if (matchingStrategy) {
          this.strategyTiming[matchingStrategy.id] = strategy.time
        }
      })
      
      this.recalculateTime()
      this.$message.success(`已将最佳匹配教案(${bestResult.matchScore}%)的策略应用到设计中`)
    },
    
    showLessonLibrary() {
      this.$message.info('正在打开教案资源库...')
      // 这里可以打开一个模态框显示完整的资源库
    }
  },
  
  watch: {
    selectedStrategies: {
      handler() {
        this.$nextTick(() => {
          this.renderStrategyChart()
        })
      },
      deep: true
    }
  }
}
</script>

<style scoped>
.strategy-designer {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

/* Enhanced Header */
.page-header {
  margin-bottom: 30px;
  text-align: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 30px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.header-content h2 {
  color: white;
  margin-bottom: 10px;
  font-size: 28px;
  font-weight: 600;
}

.header-content p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  margin-bottom: 15px;
}

.class-profile-summary {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.profile-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.profile-item i {
  font-size: 16px;
  color: #FFD700;
}

.progress-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 20px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #FFD700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Card Styling */
.mode-selector-card,
.time-allocator-card,
.analytics-card,
.excellent-lesson-reference-card {
  margin-bottom: 30px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: none;
  border-radius: 15px;
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  font-size: 16px;
  color: #2c3e50;
}

.card-header i {
  margin-right: 8px;
  color: #409EFF;
}

.duration-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

/* Enhanced Strategy Combination Section */
.strategy-combination-section {
  margin-bottom: 30px;
}

/* 策略概览卡片 */
.strategy-overview-card {
  margin-bottom: 25px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border: none;
  border-radius: 20px;
  overflow: hidden;
}

/* 清晰的头部样式 */
.clean-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.header-main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-main i {
  font-size: 20px;
  color: #409EFF;
}

.header-main h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 策略统计概览 */
.strategy-stats-overview {
  margin-bottom: 25px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.stat-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7));
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(64, 158, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--type-color);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
}

.stat-icon.core {
  background: linear-gradient(135deg, #409EFF, #66b3ff);
}

.stat-icon.support {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.stat-icon.assessment {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

.stat-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.stat-content {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 15px;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-time {
  font-size: 12px;
  color: #909399;
  background: rgba(64, 158, 255, 0.1);
  padding: 2px 8px;
  border-radius: 12px;
  margin-left: auto;
}

.stat-progress {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.6s ease;
}

.progress-text {
  font-size: 12px;
  font-weight: 600;
  color: #606266;
  min-width: 35px;
  text-align: right;
}

/* 效率指标面板 */
.efficiency-panel {
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(255, 255, 255, 0.8));
  border-radius: 16px;
  border: 1px solid rgba(64, 158, 255, 0.1);
  margin-top: 20px;
}

.efficiency-item {
  text-align: center;
}

.efficiency-label {
  display: block;
  font-size: 12px;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.efficiency-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
}

.efficiency-value.high {
  color: #67C23A;
}

.efficiency-value.medium {
  color: #E6A23C;
}

.efficiency-value.low {
  color: #F56C6C;
}

/* 策略筛选器 */
.strategy-filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px 25px;
  margin-bottom: 25px;
  border: 1px solid rgba(64, 158, 255, 0.1);
}

.filter-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.filter-title i {
  color: #409EFF;
}

.clean-filters {
  background: rgba(64, 158, 255, 0.05);
  border-radius: 12px;
  padding: 8px;
}

.clean-filters .el-radio-button__inner {
  border: none;
  background: transparent;
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.clean-filters .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: #409EFF;
  color: white;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transform: translateY(-2px);
}

/* 策略选择网格 */
.strategy-grid-section {
  margin-top: 25px;
}

.grid-container {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 25px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.strategy-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 20px;
}

/* 策略项样式 */
.strategy-item {
  position: relative;
  background: white;
  border-radius: 16px;
  padding: 20px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;
}

.strategy-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: #E5E7EB;
  transition: all 0.3s ease;
}

.strategy-item.type-core::before {
  background: linear-gradient(90deg, #409EFF, #66b3ff);
}

.strategy-item.type-support::before {
  background: linear-gradient(90deg, #67C23A, #85ce61);
}

.strategy-item.type-assessment::before {
  background: linear-gradient(90deg, #E6A23C, #ebb563);
}

.strategy-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border-color: rgba(64, 158, 255, 0.2);
}

.strategy-item.selected {
  border-color: #409EFF;
  box-shadow: 0 8px 30px rgba(64, 158, 255, 0.2);
  transform: translateY(-5px);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(255, 255, 255, 0.95));
}

/* 策略类型标识 */
.strategy-type-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: white;
}

.strategy-type-badge.core {
  background: linear-gradient(135deg, #409EFF, #66b3ff);
}

.strategy-type-badge.support {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.strategy-type-badge.assessment {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

/* 选中状态标识 */
.selected-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #FFD700, #FFA500);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 12px rgba(255, 215, 0, 0.4);
  animation: pulseGlow 2s infinite;
}

.order-number {
  font-size: 12px;
  font-weight: 700;
  color: white;
}

@keyframes pulseGlow {
  0%, 100% { transform: scale(1); box-shadow: 0 3px 12px rgba(255, 215, 0, 0.4); }
  50% { transform: scale(1.05); box-shadow: 0 5px 20px rgba(255, 215, 0, 0.6); }
}

/* 策略内容样式 */
.strategy-content {
  margin-top: 50px;
  padding-right: 40px;
}

.strategy-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.strategy-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  flex: 1;
}

.strategy-meta {
  display: flex;
  gap: 8px;
  align-items: center;
}

.time-tag {
  background: rgba(64, 158, 255, 0.1);
  color: #409EFF;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.efficiency-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.efficiency-tag.excellent {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.efficiency-tag.good {
  background: linear-gradient(135deg, #409EFF, #66b3ff);
}

.efficiency-tag.fair {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

.efficiency-tag.poor {
  background: linear-gradient(135deg, #F56C6C, #ff8a95);
}

.strategy-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin: 0 0 15px 0;
}

.strategy-footer {
  margin-top: 15px;
}

.target-skills {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 10px;
}

.skill-tag {
  background: rgba(103, 194, 58, 0.1);
  color: #67C23A;
  padding: 3px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 500;
}

.quadratic-focus {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 12px;
}

.quadratic-focus i {
  color: #FFD700;
}

/* 策略项动画 */
.strategy-item-enter-active,
.strategy-item-leave-active {
  transition: all 0.4s ease;
}

.strategy-item-enter,
.strategy-item-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.strategy-item-move {
  transition: transform 0.4s ease;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
  grid-column: 1 / -1;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.4));
  border-radius: 16px;
  border: 2px dashed rgba(64, 158, 255, 0.2);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.4;
  color: #C0C4CC;
}

.empty-state h4 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #606266;
  font-weight: 600;
}

.empty-state p {
  margin: 0 0 20px 0;
  font-size: 14px;
  line-height: 1.5;
  color: #909399;
}

/* Strategy Flow Visualization */
.strategy-flow-card {
  margin-top: 25px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border: none;
  border-radius: 20px;
}

.strategy-flow {
  padding: 20px 0;
}

.flow-timeline {
  display: flex;
  align-items: center;
  overflow-x: auto;
  padding: 20px;
  gap: 30px;
}

.flow-item {
  display: flex;
  align-items: center;
  gap: 20px;
  min-width: 300px;
  position: relative;
}

.flow-node {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.node-number {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
}

.node-type {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.node-type.core {
  background: linear-gradient(135deg, #409EFF, #66b3ff);
}

.node-type.support {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.node-type.assessment {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

.flow-content {
  flex: 1;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.flow-title {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 5px;
}

.flow-time {
  font-size: 12px;
  color: #409EFF;
  font-weight: 500;
  margin-bottom: 8px;
}

.flow-description {
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.flow-connector {
  position: absolute;
  right: -45px;
  top: 50%;
  transform: translateY(-50%);
  color: #C0C4CC;
  font-size: 20px;
  z-index: 5;
}

.flow-summary {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 25px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1), rgba(64, 158, 255, 0.05));
  border-radius: 12px;
  color: #409EFF;
  font-weight: 500;
}

.summary-card i {
  font-size: 18px;
}

/* Analytics */
.analytics-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 30px;
  align-items: start;
}

.chart-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
}

.analytics-insights {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.insight-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.insight-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.insight-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.insight-icon.warning {
  background: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.insight-icon.info {
  background: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.insight-icon.success {
  background: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.insight-content h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.insight-content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}

/* Action Buttons */
.action-buttons {
  text-align: center;
  margin-top: 40px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.action-buttons .el-button {
  margin: 0 10px;
  min-width: 140px;
  height: 45px;
  font-size: 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.action-buttons .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* Responsive Design */
@media (max-width: 768px) {
  .strategy-designer {
    padding: 15px;
  }
  
  .progress-stats {
    gap: 15px;
  }
  
  .stat-number {
    font-size: 18px;
  }
  
  .class-profile-summary {
    gap: 15px;
    margin-bottom: 15px;
  }
  
  .profile-item {
    font-size: 12px;
    padding: 6px 12px;
  }
  
  .profile-item i {
    font-size: 14px;
  }
  
  .analytics-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  /* 策略组合设计移动端适配 */
  .strategy-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .strategy-filter-bar {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
    text-align: center;
  }
  
  .clean-filters {
    justify-content: center;
  }
  
  .clean-filters .el-radio-button__inner {
    padding: 8px 16px;
    font-size: 12px;
  }
  
  .stats-row {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .efficiency-panel {
    flex-direction: column;
    gap: 20px;
  }
  
  .strategy-content {
    margin-top: 45px;
    padding-right: 35px;
  }
  
  .strategy-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .strategy-meta {
    align-self: flex-end;
  }
  
  .flow-timeline {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }
  
  .flow-item {
    min-width: auto;
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .flow-node {
    justify-content: center;
  }
  
  .flow-connector {
    position: static;
    transform: none;
    margin: 0;
  }
  
  .flow-summary {
    flex-direction: column;
    gap: 15px;
  }
  
  .action-buttons .el-button {
    margin: 5px;
    min-width: 120px;
  }
  
  /* 优秀教案参考移动端适配 */
  .upload-section {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .extracted-strategies {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .lesson-item {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
    gap: 10px;
  }
  
  .lesson-info {
    order: 1;
  }
  
  .lesson-icon {
    order: 2;
    align-self: center;
  }
  
  .lesson-action {
    order: 3;
    align-self: center;
  }
}

/* 优秀教案文档参考样式 */
.excellent-lesson-reference-card {
  position: relative;
}

.excellent-lesson-reference-card .header-main {
  display: flex;
  align-items: center;
  gap: 10px;
}

.excellent-lesson-reference-card .header-main i {
  color: #67C23A;
}

.lesson-reference-content {
  padding: 0;
}

.reference-description {
  padding: 0 0 20px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  margin-bottom: 25px;
}

.reference-description p {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

/* 上传区域样式 */
.upload-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-bottom: 30px;
}

.upload-area {
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.05), rgba(255, 255, 255, 0.8));
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(103, 194, 58, 0.1);
}

.lesson-uploader {
  width: 100%;
}

.lesson-uploader :deep(.el-upload-dragger) {
  width: 100% !important;
  height: 160px !important;
  border: 2px dashed rgba(103, 194, 58, 0.3) !important;
  border-radius: 12px !important;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(103, 194, 58, 0.02)) !important;
  transition: all 0.3s ease !important;
}

.lesson-uploader :deep(.el-upload-dragger:hover) {
  border-color: #67C23A !important;
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.05), rgba(255, 255, 255, 0.95)) !important;
}

.upload-content {
  text-align: center;
  padding: 20px;
}

.upload-icon {
  font-size: 48px;
  color: #67C23A;
  margin-bottom: 12px;
}

.upload-text {
  font-size: 16px;
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 8px;
}

.upload-text em {
  color: #67C23A;
  font-style: normal;
  text-decoration: underline;
}

.upload-hint {
  font-size: 12px;
  color: #909399;
}

/* 推荐教案区域 */
.quick-import-section {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(255, 255, 255, 0.8));
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(64, 158, 255, 0.1);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 15px;
}

.section-title i {
  color: #FFD700;
}

.recommended-lessons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.lesson-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
}

.lesson-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  border-color: rgba(64, 158, 255, 0.2);
}

.lesson-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #FFD700, #FFA500);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
}

.lesson-info {
  flex: 1;
  min-width: 0;
}

.lesson-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.lesson-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
  font-size: 12px;
  color: #909399;
}

.lesson-rating {
  display: flex;
  align-items: center;
  gap: 3px;
}

.lesson-rating i {
  color: #FFD700;
}

.lesson-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.lesson-action {
  flex-shrink: 0;
}

/* AI分析结果样式 */
.analysis-results {
  margin-top: 30px;
  padding-top: 25px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.results-header h4 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #2c3e50;
}

.results-header i {
  color: #409EFF;
}

.analysis-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.analysis-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(64, 158, 255, 0.02));
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(64, 158, 255, 0.1);
  transition: all 0.3s ease;
}

.analysis-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.file-info i {
  color: #67C23A;
  font-size: 18px;
}

.file-name {
  font-weight: 600;
  color: #2c3e50;
}

.analysis-score {
  text-align: center;
}

.score {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #67C23A;
}

.score-label {
  font-size: 12px;
  color: #909399;
}

.extracted-strategies {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;
}

.strategy-section h5,
.time-allocation h5 {
  margin: 0 0 15px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #2c3e50;
}

.strategy-section i,
.time-allocation i {
  color: #409EFF;
}

.strategy-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.extracted-strategy {
  padding: 12px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.strategy-name {
  font-weight: 600;
  color: #409EFF;
  margin-bottom: 4px;
}

.strategy-time {
  font-size: 12px;
  color: #67C23A;
  font-weight: 500;
  margin-bottom: 6px;
}

.strategy-description {
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.time-breakdown {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.time-phase {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
}

.phase-name {
  font-size: 12px;
  color: #2c3e50;
  min-width: 60px;
}

.phase-time {
  font-size: 12px;
  color: #409EFF;
  font-weight: 600;
  min-width: 40px;
}

.phase-bar {
  flex: 1;
  height: 4px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 2px;
  overflow: hidden;
}

.phase-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #66b3ff);
  border-radius: 2px;
  transition: width 0.6s ease;
}

/* 分析状态样式 */
.analyzing-state {
  text-align: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.05), rgba(255, 255, 255, 0.9));
  border-radius: 16px;
  border: 2px dashed rgba(64, 158, 255, 0.2);
  margin-top: 25px;
}

.analyzing-content h4 {
  margin: 15px 0 8px 0;
  color: #409EFF;
  font-size: 18px;
}

.analyzing-content p {
  margin: 0 0 20px 0;
  color: #606266;
  font-size: 14px;
}

.analyzing-icon {
  font-size: 48px;
  color: #409EFF;
  animation: rotate 1.5s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.analyzing-progress {
  max-width: 300px;
  margin: 0 auto;
}

.progress-text {
  display: block;
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}

/* Element UI Overrides */
.el-card__header {
  background: rgba(64, 158, 255, 0.05);
  border-bottom: 1px solid rgba(64, 158, 255, 0.1);
}

.el-radio-button__inner {
  background: transparent;
  border-color: rgba(255, 255, 255, 0.3);
  color: white;
}

.el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
  color: white;
}
</style> 