<template>
  <div class="preview-export">
    <!-- Enhanced Header -->
    <div class="page-header">
      <div class="header-content">
        <h2>步骤6/6：生成教学方案</h2>
        <p>整合输出成果，生成完整教学设计方案</p>
        <div class="progress-indicators">
          <div class="indicator completed">
            <i class="el-icon-success"></i>
            <span>数据收集完成</span>
          </div>
          <div class="indicator completed">
            <i class="el-icon-success"></i>
            <span>策略制定完成</span>
          </div>
          <div class="indicator current">
            <i class="el-icon-loading"></i>
            <span>方案生成中</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Data Integration Summary -->
    <el-card class="integration-section" shadow="hover">
      <div slot="header" class="section-header">
        <div class="header-left">
          <i class="el-icon-connection"></i>
          <span>数据整合概览</span>
        </div>
        <div class="integration-stats">
          <span>已整合：{{ integratedSteps }}/5 个步骤</span>
        </div>
      </div>

      <div class="integration-content">
        <div class="integration-grid">
          <div class="integration-item" v-for="step in integrationStatus" :key="step.id">
            <div class="step-icon" :class="{ integrated: step.integrated }">
              <i :class="step.icon"></i>
            </div>
            <div class="step-info">
              <h5>{{ step.name }}</h5>
              <p class="step-data" v-if="step.integrated">{{ step.dataInfo }}</p>
              <p class="step-missing" v-else>数据未加载</p>
            </div>
            <div class="integration-indicator" :class="{ integrated: step.integrated }">
              <i :class="step.integrated ? 'el-icon-success' : 'el-icon-warning'"></i>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Multi-View Preview Section -->
    <el-card class="preview-section" shadow="hover">
      <div slot="header" class="section-header">
        <div class="header-left">
          <i class="el-icon-view"></i>
          <span>多视图预览</span>
        </div>
        <div class="view-controls">
          <el-radio-group v-model="activeView" size="small">
            <el-radio-button label="document">
              <i class="el-icon-document"></i> 标准教案
            </el-radio-button>
            <el-radio-button label="timeline">
              <i class="el-icon-time"></i> 教学流程图
            </el-radio-button>
            <el-radio-button label="slides">
              <i class="el-icon-picture-outline"></i> 课件大纲
            </el-radio-button>
          </el-radio-group>
          <el-button size="small" @click="refreshPreview" :loading="isGenerating">
            <i class="el-icon-refresh"></i> 刷新预览
          </el-button>
        </div>
      </div>

      <div class="preview-container">
        <!-- Document View -->
        <div v-if="activeView === 'document'" class="document-view">
          <div class="document-header">
            <h3>{{ lessonPlan.title }}</h3>
            <div class="document-meta">
              <span>学科：{{ lessonPlan.subject }}</span>
              <span>年级：{{ lessonPlan.grade }}</span>
              <span>课时：{{ lessonPlan.duration }}分钟</span>
              <span>生成时间：{{ lessonPlan.generateTime }}</span>
            </div>
          </div>
          
          <div class="document-content">
            <div class="section" v-for="section in documentSections" :key="section.id">
              <h4>{{ section.title }}</h4>
              <div class="section-content" v-html="section.content"></div>
            </div>
          </div>
        </div>

        <!-- Timeline View -->
        <div v-if="activeView === 'timeline'" class="timeline-view">
          <div class="timeline-header">
            <h3>教学流程时间轴</h3>
            <div class="timeline-stats">
              <span>总时长：{{ totalDuration }}分钟</span>
              <span>环节数：{{ timelineSteps.length }}个</span>
            </div>
          </div>
          
          <div class="timeline-content">
            <div class="timeline-item" v-for="(step, index) in timelineSteps" :key="step.id">
              <div class="timeline-node" :class="step.type">
                <div class="node-number">{{ index + 1 }}</div>
                <div class="node-icon">
                  <i :class="getStepIcon(step.type)"></i>
                </div>
              </div>
              <div class="timeline-card">
                <div class="step-header">
                  <h5>{{ step.title }}</h5>
                  <span class="step-duration">{{ step.duration }}分钟</span>
                </div>
                <p class="step-description">{{ step.description }}</p>
                <div class="step-resources" v-if="step.resources.length > 0">
                  <el-tag v-for="resource in step.resources" :key="resource" size="mini">
                    {{ resource }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Slides Preview -->
        <div v-if="activeView === 'slides'" class="slides-view">
          <div class="slides-header">
            <h3>课件大纲预览</h3>
            <div class="slides-stats">
              <span>幻灯片：{{ slideOutline.length }}张</span>
              <span>预计时长：{{ slidesTotalTime }}分钟</span>
            </div>
          </div>
          
          <div class="slides-grid">
            <div class="slide-item" v-for="(slide, index) in slideOutline" :key="slide.id">
              <div class="slide-number">{{ index + 1 }}</div>
              <div class="slide-preview">
                <div class="slide-content">
                  <h6>{{ slide.title }}</h6>
                  <ul class="slide-points">
                    <li v-for="point in slide.keyPoints" :key="point">{{ point }}</li>
                  </ul>
                </div>
                <div class="slide-type" :class="slide.type">
                  {{ getSlideTypeLabel(slide.type) }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Quality Assessment Report -->
    <el-card class="quality-section" shadow="hover">
      <div slot="header" class="section-header">
        <div class="header-left">
          <i class="el-icon-data-analysis"></i>
          <span>设计质量评估</span>
        </div>
        <div class="quality-score">
          <span>综合评分：</span>
          <span class="score-value" :class="getScoreClass(designScore)">{{ designScore }}/100</span>
        </div>
      </div>

      <div class="quality-content">
        <div class="quality-dimensions">
          <div class="dimension-item" v-for="dimension in qualityDimensions" :key="dimension.name">
            <div class="dimension-header">
              <span class="dimension-name">{{ dimension.name }}</span>
              <span class="dimension-score">{{ dimension.score }}/100</span>
            </div>
            <el-progress 
              :percentage="dimension.score" 
              :color="getDimensionColor(dimension.score)"
              :show-text="false"
            ></el-progress>
            <p class="dimension-feedback">{{ dimension.feedback }}</p>
          </div>
        </div>

        <div class="compatibility-issues" v-if="compatibilityIssues.length > 0">
          <h4>兼容性检查</h4>
          <div class="issue-item" v-for="issue in compatibilityIssues" :key="issue.id">
            <div class="issue-icon" :class="issue.level">
              <i :class="getIssueIcon(issue.level)"></i>
            </div>
            <div class="issue-content">
              <h5>{{ issue.title }}</h5>
              <p>{{ issue.description }}</p>
              <div class="issue-suggestion" v-if="issue.suggestion">
                <strong>建议：</strong>{{ issue.suggestion }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Export Panel -->
    <el-card class="export-section" shadow="hover">
      <div slot="header" class="section-header">
        <div class="header-left">
          <i class="el-icon-download"></i>
          <span>导出与发布</span>
        </div>
      </div>

      <div class="export-content">
        <!-- Export Format Selector -->
        <div class="export-formats">
          <h4>选择导出格式</h4>
          <div class="format-grid">
            <div 
              class="format-item"
              v-for="format in exportFormats"
              :key="format.type"
              :class="{ active: selectedFormats.includes(format.type) }"
              @click="toggleFormat(format.type)"
            >
              <div class="format-icon">
                <i :class="format.icon"></i>
              </div>
              <div class="format-info">
                <h5>{{ format.name }}</h5>
                <p>{{ format.description }}</p>
                <span class="format-size">{{ format.estimatedSize }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Collaboration Tools -->
        <div class="collaboration-section">
          <h4>协作与审阅</h4>
          <div class="collaboration-options">
            <el-card class="collab-card">
              <div class="collab-icon">
                <i class="el-icon-share"></i>
              </div>
              <h5>团队协作</h5>
              <p>邀请同事协作编辑和审阅</p>
              <el-button size="small" type="primary">邀请协作</el-button>
            </el-card>
            
            <el-card class="collab-card">
              <div class="collab-icon">
                <i class="el-icon-chat-line-square"></i>
              </div>
              <h5>专家评审</h5>
              <p>提交给教研专家进行评审</p>
              <el-button size="small">申请评审</el-button>
            </el-card>
            
            <el-card class="collab-card">
              <div class="collab-icon">
                <i class="el-icon-collection"></i>
              </div>
              <h5>资源库</h5>
              <p>保存到个人资源库</p>
              <el-button size="small">保存到库</el-button>
            </el-card>
          </div>
        </div>

        <!-- Publish Options -->
        <div class="publish-section">
          <h4>发布选项</h4>
          <div class="publish-options">
            <el-checkbox-group v-model="publishSettings">
              <el-checkbox label="public">公开发布到教学资源平台</el-checkbox>
              <el-checkbox label="school">仅限本校教师访问</el-checkbox>
              <el-checkbox label="grade">仅限同年级教师访问</el-checkbox>
              <el-checkbox label="backup">自动备份到云端</el-checkbox>
            </el-checkbox-group>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Action Buttons -->
    <div class="action-buttons">
      <el-button 
        size="large" 
        @click="previewFinal" 
        icon="el-icon-view"
      >
        最终预览
      </el-button>
      
      <el-button 
        size="large" 
        @click="exportSelected"
        type="success" 
        icon="el-icon-download"
        :disabled="selectedFormats.length === 0"
        :loading="isExporting"
      >
        导出方案 ({{ selectedFormats.length }})
      </el-button>
      
      <el-button 
        size="large" 
        @click="publishPlan"
        type="primary" 
        icon="el-icon-upload"
        :disabled="publishSettings.length === 0"
      >
        发布方案
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PreviewExport',
  data() {
    return {
      activeView: 'document',
      isGenerating: false,
      isExporting: false,
      selectedFormats: ['pdf'],
      publishSettings: ['backup'],
      
      // Data Integration Status
      integrationStatus: [
        {
          id: 1,
          name: '课程定位',
          icon: 'el-icon-position',
          integrated: false,
          dataInfo: ''
        },
        {
          id: 2,
          name: '教学目标',
          icon: 'el-icon-aim',
          integrated: false,
          dataInfo: ''
        },
        {
          id: 3,
          name: '重难点分析',
          icon: 'el-icon-star-on',
          integrated: false,
          dataInfo: ''
        },
        {
          id: 4,
          name: '学情配置',
          icon: 'el-icon-user',
          integrated: false,
          dataInfo: ''
        },
        {
          id: 5,
          name: '教学策略',
          icon: 'el-icon-s-grid',
          integrated: false,
          dataInfo: ''
        }
      ],
      
      // Lesson Plan Data
      lessonPlan: {
        title: '九年级数学：二次函数的图像与性质',
        subject: '数学',
        grade: '九年级',
        duration: 45,
        generateTime: new Date().toLocaleString('zh-CN')
      },
      
      // Document Sections - 将在loadPreviousData中动态更新
      documentSections: [
        {
          id: 1,
          title: '一、教学目标',
          content: `
            <p><strong>知识目标：</strong>掌握二次函数图像的基本特征和性质</p>
            <p><strong>技能目标：</strong>能够绘制二次函数图像，分析其性质</p>
            <p><strong>情感目标：</strong>培养数学思维，感受函数之美</p>
          `
        },
        {
          id: 2,
          title: '二、教学重难点',
          content: `
            <p><strong>教学重点：</strong>二次函数图像的对称性和顶点坐标</p>
            <p><strong>教学难点：</strong>抛物线性质的深入理解和实际应用</p>
          `
        },
        {
          id: 3,
          title: '三、学情分析与教学过程',
          content: `
            <p><strong>导入环节（8分钟）：</strong>通过生活实例引入二次函数概念</p>
            <p><strong>新课教学（25分钟）：</strong>系统讲解二次函数图像性质</p>
            <p><strong>练习巩固（10分钟）：</strong>针对性练习和小组讨论</p>
            <p><strong>总结提升（2分钟）：</strong>梳理知识点和解题方法</p>
          `
        }
      ],
      
      // Timeline Steps - 将在loadPreviousData中根据策略数据更新
      timelineSteps: [
        {
          id: 1,
          title: '图像导入法',
          type: 'core',
          duration: 10,
          description: '通过抛物线实例引入二次函数概念，结合图像特点激发学习兴趣',
          resources: ['二次函数图像课件', 'GeoGebra动态几何']
        },
        {
          id: 2,
          title: '对称性探究',
          type: 'core',
          duration: 15,
          description: '引导学生探究抛物线的对称性质，理解对称轴概念',
          resources: ['对称轴演示教具', '函数图像描点器']
        },
        {
          id: 3,
          title: '顶点计算训练',
          type: 'core',
          duration: 12,
          description: '通过配方法和公式法，掌握顶点坐标的计算方法',
          resources: ['顶点计算练习卡', '计算工具']
        },
        {
          id: 4,
          title: '实际问题建模',
          type: 'support',
          duration: 8,
          description: '运用二次函数解决生活中的实际问题，提升应用能力',
          resources: ['实际问题案例库', '建模指导卡']
        }
      ],
      
      // Slide Outline - 基于二次函数教学内容
      slideOutline: [
        {
          id: 1,
          title: '二次函数导入',
          type: 'intro',
          keyPoints: ['抛物线实例展示', '二次函数定义', '学习目标明确']
        },
        {
          id: 2,
          title: '图像性质分析',
          type: 'content',
          keyPoints: ['开口方向判断', '对称轴位置', '顶点坐标特征']
        },
        {
          id: 3,
          title: '计算方法讲解',
          type: 'method',
          keyPoints: ['配方法应用', '公式法使用', '常见错误分析']
        },
        {
          id: 4,
          title: '实际应用练习',
          type: 'practice',
          keyPoints: ['建模问题解析', '分层练习设计', '个别辅导要点']
        },
        {
          id: 5,
          title: '知识系统总结',
          type: 'summary',
          keyPoints: ['性质归纳总结', '方法对比梳理', '拓展作业布置']
        }
      ],
      
      // Quality Assessment - 基于前面步骤数据的综合评估
      designScore: 91,
      qualityDimensions: [
        {
          name: '目标明确性',
          score: 94,
          feedback: '三维目标设定清晰具体，与布卢姆认知层级完全匹配，符合课程标准要求'
        },
        {
          name: '内容科学性',
          score: 92,
          feedback: '二次函数教学内容准确无误，重难点分析透彻，知识点分解合理'
        },
        {
          name: '学情适切性',
          score: 89,
          feedback: '充分考虑九年级学生认知特点，针对42人班级的视觉型学习者特征设计'
        },
        {
          name: '策略有效性',
          score: 91,
          feedback: '教学策略组合科学，效率指数高，特别关注8名特殊需求学生'
        },
        {
          name: '资源丰富性',
          score: 88,
          feedback: '教学资源配置合理，数字化工具与传统教具相结合，支持个性化学习'
        },
        {
          name: '评估完整性',
          score: 87,
          feedback: '评估方式多样化，覆盖认知弱项突破，包含形成性和总结性评估'
        }
      ],
      
      // Compatibility Issues - 基于实际教学情况的兼容性检查
      compatibilityIssues: [
        {
          id: 1,
          level: 'info',
          title: '学情差异化处理',
          description: '班级中8名特殊需求学生可能需要额外的个别辅导时间',
          suggestion: '建议课后安排10-15分钟个别答疑，或设计分层作业'
        },
        {
          id: 2,
          level: 'warning',
          title: '数字化工具依赖',
          description: 'GeoGebra等数字化工具需要稳定的网络和设备支持',
          suggestion: '准备离线版本或传统教具作为备用方案'
        },
        {
          id: 3,
          level: 'info',
          title: '认知弱项突破时间',
          description: '对称性理解和实际应用是认知弱项，可能需要更多练习时间',
          suggestion: '考虑将部分基础练习调整为课前预习或课后巩固'
        }
      ],
      
      // Export Formats
      exportFormats: [
        {
          type: 'pdf',
          name: 'PDF文档',
          description: '适合打印和分享的标准格式',
          icon: 'el-icon-document',
          estimatedSize: '2.5MB'
        },
        {
          type: 'docx',
          name: 'Word文档',
          description: '可编辑的教案文档格式',
          icon: 'el-icon-edit-outline',
          estimatedSize: '1.8MB'
        },
        {
          type: 'pptx',
          name: 'PowerPoint',
          description: '课件演示文稿格式',
          icon: 'el-icon-picture-outline',
          estimatedSize: '3.2MB'
        },
        {
          type: 'html',
          name: '网页格式',
          description: '便于在线查看和分享',
          icon: 'el-icon-link',
          estimatedSize: '1.2MB'
        }
      ]
    }
  },
  
  computed: {
    totalDuration() {
      return this.timelineSteps.reduce((sum, step) => sum + step.duration, 0)
    },
    
    slidesTotalTime() {
      return Math.round(this.slideOutline.length * 2.5) // 假设每张幻灯片2.5分钟
    },

    integratedSteps() {
      return this.integrationStatus.filter(step => step.integrated).length
    }
  },
  
  mounted() {
    this.loadPreviousData()
  },
  
  methods: {
    loadPreviousData() {
      // 从localStorage加载之前步骤的数据
      try {
        // 加载课程定位数据
        const courseData = localStorage.getItem('coursePositioning')
        if (courseData) {
          const data = JSON.parse(courseData)
          this.lessonPlan.subject = data.subject === 'math' ? '数学' : data.subject
          this.lessonPlan.grade = data.grade === '9' ? '九年级' : data.grade
          this.lessonPlan.title = `${this.lessonPlan.grade}${this.lessonPlan.subject}：二次函数的图像与性质`
          
          // 更新整合状态
          this.integrationStatus[0].integrated = true
          this.integrationStatus[0].dataInfo = `${this.lessonPlan.subject} • ${this.lessonPlan.grade} • 难度${data.difficulty}/5`
        }

        // 加载教学目标数据
        const targetData = localStorage.getItem('teachingTargets')
        if (targetData) {
          const data = JSON.parse(targetData)
          this.updateDocumentFromTargets(data)
          
          // 更新整合状态
          this.integrationStatus[1].integrated = true
          this.integrationStatus[1].dataInfo = `三维目标 • ${this.getBloomLevelName(data.bloomLevel)}层级 • 标准匹配`
        }

        // 加载重难点分析数据
        const analysisData = localStorage.getItem('contentAnalysis')
        if (analysisData) {
          const data = JSON.parse(analysisData)
          this.updateDocumentFromAnalysis(data)
          
          // 更新整合状态
          this.integrationStatus[2].integrated = true
          this.integrationStatus[2].dataInfo = `${data.keyPoints.length}个重点 • ${data.difficultyPoints.length}个难点 • ${data.breakthroughStrategies.length}个突破策略`
        }

        // 加载学情配置数据
        const studentData = localStorage.getItem('studentProfiling')
        if (studentData) {
          const data = JSON.parse(studentData)
          this.updateDocumentFromStudentData(data)
          
          // 更新整合状态
          this.integrationStatus[3].integrated = true
          this.integrationStatus[3].dataInfo = `${data.classInfo.studentCount}人班级 • ${data.overallAssessment.dominantLearningStyle} • ${data.specialNeeds.length}名特殊需求`
        }

        // 加载教学策略数据
        const strategyData = localStorage.getItem('teachingStrategy')
        if (strategyData) {
          const data = JSON.parse(strategyData)
          this.updatePreviewFromStrategy(data)
          this.updateDocumentFromStrategy(data)
          
          // 更新整合状态
          this.integrationStatus[4].integrated = true
          this.integrationStatus[4].dataInfo = `${data.selectedStrategies.length}个策略 • 效率${data.efficiency}% • ${data.duration}分钟`
        }
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    
    updateDocumentFromTargets(targetData) {
      // 根据目标数据更新教案内容
      if (targetData.targets) {
        const targets = targetData.targets
        this.documentSections[0].content = `
          <p><strong>知识目标：</strong>${targets.knowledge.description || '掌握二次函数图像的基本特征和性质'}</p>
          <p><strong>技能目标：</strong>${targets.process.description || '能够绘制二次函数图像，分析其性质'}</p>
          <p><strong>情感目标：</strong>${targets.attitude.description || '培养数学思维，感受函数之美'}</p>
          <p><strong>布卢姆层级：</strong>${this.getBloomLevelName(targetData.bloomLevel)}</p>
          <p><strong>课程标准：</strong>${targetData.standard || '义务教育数学课程标准'}</p>
        `
      }
    },

    updateDocumentFromAnalysis(analysisData) {
      // 根据分析数据更新重难点部分
      if (analysisData.keyPoints && analysisData.difficultyPoints) {
        const keyPoints = analysisData.keyPoints.map(point => point.name).join('、')
        const difficulties = analysisData.difficultyPoints.map(point => point.name).join('、')
        
        this.documentSections[1].content = `
          <p><strong>教学重点：</strong>${keyPoints}</p>
          <p><strong>教学难点：</strong>${difficulties}</p>
          <p><strong>突破策略：</strong></p>
          <ul>
            ${analysisData.breakthroughStrategies ? analysisData.breakthroughStrategies.map(strategy => 
              `<li>${strategy.name}：${strategy.description}</li>`
            ).join('') : ''}
          </ul>
        `
      }
    },

    updateDocumentFromStudentData(studentData) {
      // 根据学情数据更新教学过程
      const classInfo = studentData.classInfo
      const assessment = studentData.overallAssessment
      
      if (classInfo && assessment) {
        // 更新教学过程，体现学情特点
        this.documentSections[2].content = `
          <p><strong>学情分析：</strong></p>
          <ul>
            <li>班级规模：${classInfo.studentCount}人，平均成绩${classInfo.averageScore}分</li>
            <li>认知优势：${assessment.cognitiveStrengths.join('、')}</li>
            <li>认知弱项：${assessment.cognitiveWeaknesses.join('、')}</li>
            <li>学习风格：${assessment.dominantLearningStyle}占主导</li>
            ${studentData.specialNeeds.length > 0 ? `<li>特殊需求：${studentData.specialNeeds.length}名学生需要个别辅导</li>` : ''}
          </ul>
          <p><strong>教学过程设计：</strong></p>
          <p><strong>导入环节（8分钟）：</strong>基于${assessment.dominantLearningStyle}学习特点，通过${assessment.dominantLearningStyle === '视觉型' ? '图像演示' : '实例讲解'}引入二次函数概念</p>
          <p><strong>新课教学（25分钟）：</strong>针对认知弱项"${assessment.cognitiveWeaknesses[0] || '对称性理解'}"，采用分层教学法</p>
          <p><strong>练习巩固（10分钟）：</strong>设计差异化练习，照顾${studentData.specialNeeds.length}名特殊需求学生</p>
          <p><strong>总结提升（2分钟）：</strong>强化认知优势"${assessment.cognitiveStrengths[0] || '图像识别'}"，建立知识框架</p>
        `
      }
    },

    updateDocumentFromStrategy(strategyData) {
      // 根据策略数据更新教学方法部分
      if (strategyData.selectedStrategies) {
        const strategies = strategyData.selectedStrategies
        const methodSection = {
          id: 4,
          title: '四、教学方法与策略',
          content: `
            <p><strong>教学模式：</strong>基于学情的个性化教学</p>
            <p><strong>主要策略：</strong></p>
            <ul>
              ${strategies.map(strategy => 
                `<li><strong>${strategy.name}</strong>（${strategyData.timing[strategy.id]}分钟）：${strategy.description}</li>`
              ).join('')}
            </ul>
            <p><strong>策略效率：</strong>${strategyData.efficiency}%</p>
            <p><strong>教学重点：</strong>${strategyData.teachingFocus.join('、')}</p>
          `
        }
        
        // 添加或更新方法策略部分
        if (this.documentSections.length < 4) {
          this.documentSections.push(methodSection)
        } else {
          this.documentSections[3] = methodSection
        }

        // 添加学情适应性分析
        const adaptSection = {
          id: 5,
          title: '五、学情适应性分析',
          content: `
            <p><strong>班级特征：</strong></p>
            <ul>
              <li>班级规模：${strategyData.classCharacteristics.size}人</li>
              <li>平均成绩：${strategyData.classCharacteristics.averageScore}分</li>
              <li>水平层次：${strategyData.classCharacteristics.level}</li>
              <li>学习风格：${strategyData.classCharacteristics.dominantStyle}占主导</li>
              <li>特殊需求：${strategyData.classCharacteristics.specialNeeds}名学生</li>
            </ul>
            <p><strong>个性化措施：</strong></p>
            <ul>
              <li>针对${strategyData.classCharacteristics.dominantStyle}学习者，采用视觉化教学手段</li>
              <li>对${strategyData.classCharacteristics.specialNeeds}名特殊需求学生实施个别辅导</li>
              <li>根据${strategyData.classCharacteristics.level}水平，调整教学难度和节奏</li>
            </ul>
          `
        }
        
        if (this.documentSections.length < 5) {
          this.documentSections.push(adaptSection)
        } else {
          this.documentSections[4] = adaptSection
        }
      }
    },
    
    updatePreviewFromStrategy(strategyData) {
      // 根据策略数据更新时间轴预览
      if (strategyData.selectedStrategies) {
        this.timelineSteps = strategyData.selectedStrategies.map((strategy, index) => ({
          id: strategy.id,
          title: strategy.name,
          type: strategy.type,
          duration: strategyData.timing[strategy.id] || 0,
          description: strategy.description,
          resources: this.getStrategyResources(strategy.type)
        }))
      }
    },

    getStrategyResources(type) {
      const resourceMap = {
        'core': ['二次函数图像课件', 'GeoGebra动态几何', '函数图像描点器'],
        'support': ['对称轴演示教具', '顶点计算练习卡', '平移变换动画'],
        'assessment': ['阶段性测评题', '错误分析视频', '学习反馈表']
      }
      return resourceMap[type] || ['相关教学资源']
    },

    getBloomLevelName(level) {
      const levelNames = {
        'remember': '记忆',
        'understand': '理解', 
        'apply': '运用',
        'analyze': '分析',
        'evaluate': '评价',
        'create': '创造'
      }
      return levelNames[level] || '理解'
    },
    
    refreshPreview() {
      this.isGenerating = true
      setTimeout(() => {
        this.isGenerating = false
        this.$message.success('预览已更新')
      }, 2000)
    },
    
    getStepIcon(type) {
      const icons = {
        intro: 'el-icon-sunrise',
        interaction: 'el-icon-chat-dot-round',
        practice: 'el-icon-edit',
        summary: 'el-icon-collection',
        assessment: 'el-icon-finished'
      }
      return icons[type] || 'el-icon-circle-check'
    },
    
    getSlideTypeLabel(type) {
      const labels = {
        intro: '导入',
        content: '内容',
        method: '方法',
        practice: '练习',
        summary: '总结'
      }
      return labels[type] || '其他'
    },
    
    getScoreClass(score) {
      if (score >= 90) return 'excellent'
      if (score >= 80) return 'good'
      if (score >= 70) return 'fair'
      return 'poor'
    },
    
    getDimensionColor(score) {
      if (score >= 90) return '#67C23A'
      if (score >= 80) return '#E6A23C'
      if (score >= 70) return '#F56C6C'
      return '#909399'
    },
    
    getIssueIcon(level) {
      const icons = {
        error: 'el-icon-close',
        warning: 'el-icon-warning',
        info: 'el-icon-info'
      }
      return icons[level] || 'el-icon-info'
    },
    
    toggleFormat(formatType) {
      const index = this.selectedFormats.indexOf(formatType)
      if (index > -1) {
        this.selectedFormats.splice(index, 1)
      } else {
        this.selectedFormats.push(formatType)
      }
    },
    
    previewFinal() {
      this.$msgbox({
        title: '最终预览',
        message: this.$createElement('div', [
          this.$createElement('p', '教学方案预览将在新窗口中打开'),
          this.$createElement('p', '包含完整的教案、流程图和课件内容')
        ])
      })
    },
    
    exportSelected() {
      if (this.selectedFormats.length === 0) {
        this.$message.warning('请至少选择一种导出格式')
        return
      }
      
      this.isExporting = true
      setTimeout(() => {
        this.isExporting = false
        this.$message.success(`已成功导出 ${this.selectedFormats.length} 种格式的教学方案`)
        this.$emit('step-completed', 6)
      }, 3000)
    },
    
    publishPlan() {
      if (this.publishSettings.length === 0) {
        this.$message.warning('请选择发布选项')
        return
      }
      
      this.$message.success('教学方案发布成功！')
    }
  }
}
</script>

<style scoped>
.preview-export {
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
  margin-bottom: 20px;
}

.progress-indicators {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 20px;
}

.indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 15px;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.indicator.completed {
  background: rgba(103, 194, 58, 0.2);
  color: #67C23A;
}

.indicator.current {
  background: rgba(255, 215, 0, 0.2);
  color: #FFD700;
}

/* Card Styling */
.integration-section,
.preview-section,
.quality-section,
.export-section {
  margin-bottom: 25px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border: none;
  border-radius: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 16px;
  color: #2c3e50;
}

.header-left i {
  font-size: 20px;
  color: #409EFF;
}

.view-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.integration-stats {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
  color: #409EFF;
}

.quality-score {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
}

.score-value {
  font-size: 18px;
  font-weight: bold;
}

.score-value.excellent {
  color: #67C23A;
}

.score-value.good {
  color: #409EFF;
}

.score-value.fair {
  color: #E6A23C;
}

.score-value.poor {
  color: #F56C6C;
}

/* Preview Container */
.preview-container {
  padding: 20px 0;
}

/* Document View */
.document-view {
  max-height: 600px;
  overflow-y: auto;
}

.document-header {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e4e7ed;
}

.document-header h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 24px;
}

.document-meta {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.document-content .section {
  margin-bottom: 25px;
}

.document-content h4 {
  color: #409EFF;
  margin-bottom: 10px;
  font-size: 16px;
}

.section-content {
  line-height: 1.6;
  color: #606266;
}

/* Timeline View */
.timeline-view {
  max-height: 600px;
  overflow-y: auto;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e4e7ed;
}

.timeline-header h3 {
  margin: 0;
  color: #2c3e50;
}

.timeline-stats {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.timeline-content {
  position: relative;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 25px;
  position: relative;
}

.timeline-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: 40px;
  top: 80px;
  bottom: -25px;
  width: 2px;
  background: linear-gradient(to bottom, #409EFF, transparent);
}

.timeline-node {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.node-number {
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
}

.node-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  background: linear-gradient(135deg, #409EFF, #66b3ff);
}

.timeline-card {
  flex: 1;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.step-header h5 {
  margin: 0;
  color: #2c3e50;
  font-size: 16px;
}

.step-duration {
  color: #409EFF;
  font-weight: 500;
  font-size: 14px;
}

.step-description {
  color: #606266;
  line-height: 1.5;
  margin-bottom: 10px;
}

.step-resources {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* Slides View */
.slides-view {
  max-height: 600px;
  overflow-y: auto;
}

.slides-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e4e7ed;
}

.slides-header h3 {
  margin: 0;
  color: #2c3e50;
}

.slides-stats {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.slides-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.slide-item {
  position: relative;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.slide-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.slide-number {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 25px;
  height: 25px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.slide-preview {
  padding: 40px 20px 20px 20px;
}

.slide-content h6 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.slide-points {
  margin: 0;
  padding-left: 15px;
  color: #606266;
  font-size: 12px;
  line-height: 1.5;
}

.slide-points li {
  margin-bottom: 5px;
}

.slide-type {
  position: absolute;
  bottom: 10px;
  right: 10px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 500;
  background: #E6A23C;
  color: white;
}

/* Quality Assessment */
.quality-content {
  padding: 20px 0;
}

.quality-dimensions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.dimension-item {
  padding: 20px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.dimension-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.dimension-name {
  font-weight: 600;
  color: #2c3e50;
}

.dimension-score {
  font-weight: bold;
  color: #409EFF;
}

.dimension-feedback {
  margin-top: 10px;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.compatibility-issues {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.compatibility-issues h4 {
  margin-bottom: 20px;
  color: #2c3e50;
}

.issue-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  margin-bottom: 15px;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.issue-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.issue-icon.error {
  background: rgba(245, 108, 108, 0.1);
  color: #F56C6C;
}

.issue-icon.warning {
  background: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.issue-icon.info {
  background: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.issue-content h5 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 14px;
}

.issue-content p {
  margin: 0 0 10px 0;
  color: #606266;
  font-size: 13px;
  line-height: 1.4;
}

.issue-suggestion {
  font-size: 12px;
  color: #909399;
}

/* Export Section */
.export-content {
  padding: 20px 0;
}

.export-content h4 {
  margin-bottom: 20px;
  color: #2c3e50;
  font-size: 16px;
}

.format-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.format-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.6);
  border: 2px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.format-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.format-item.active {
  border-color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
}

.format-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #409EFF, #66b3ff);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.format-info h5 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 14px;
}

.format-info p {
  margin: 0 0 5px 0;
  color: #606266;
  font-size: 12px;
  line-height: 1.4;
}

.format-size {
  font-size: 11px;
  color: #909399;
}

.collaboration-options {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.collab-card {
  text-align: center;
  padding: 20px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 12px;
}

.collab-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #67C23A, #85ce61);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px auto;
  color: white;
  font-size: 24px;
}

.collab-card h5 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.collab-card p {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 12px;
  line-height: 1.4;
}

.publish-options {
  background: rgba(255, 255, 255, 0.6);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
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

/* Integration Section */
.integration-content {
  padding: 20px 0;
}

.integration-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.integration-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.integration-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.step-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
  transition: all 0.3s ease;
}

.step-icon.integrated {
  background: linear-gradient(135deg, #67C23A, #85ce61);
  color: white;
}

.step-info {
  flex: 1;
}

.step-info h5 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.step-data {
  margin: 0;
  color: #606266;
  font-size: 12px;
  line-height: 1.4;
}

.step-missing {
  margin: 0;
  color: #F56C6C;
  font-size: 12px;
  font-style: italic;
}

.integration-indicator {
  width: 25px;
  height: 25px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(245, 108, 108, 0.1);
  color: #F56C6C;
  font-size: 14px;
}

.integration-indicator.integrated {
  background: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

/* Responsive Design */
@media (max-width: 768px) {
  .preview-export {
    padding: 15px;
  }
  
  .progress-indicators {
    flex-direction: column;
    gap: 15px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .view-controls {
    flex-direction: column;
    gap: 10px;
  }
  
  .document-meta,
  .timeline-stats,
  .slides-stats {
    flex-direction: column;
    gap: 10px;
  }
  
  .timeline-item {
    flex-direction: column;
    text-align: center;
  }
  
  .step-header {
    flex-direction: column;
    gap: 5px;
    text-align: center;
  }
  
  .slides-grid {
    grid-template-columns: 1fr;
  }
  
  .quality-dimensions {
    grid-template-columns: 1fr;
  }
  
  .format-grid {
    grid-template-columns: 1fr;
  }
  
  .collaboration-options {
    grid-template-columns: 1fr;
  }
  
  .integration-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons .el-button {
    margin: 5px;
    min-width: 120px;
  }
}
</style> 