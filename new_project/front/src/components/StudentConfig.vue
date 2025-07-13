<template>
  <div class="student-profiling">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2>
            <i class="el-icon-user"></i>
            学习者特征配置
          </h2>
          <p>深度分析学生学情，制定个性化教学策略，提升教学针对性</p>
        </div>
        <div class="step-indicator">
          <div class="step-badge">
            <span class="step-number">4</span>
            <span class="step-total">/6</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 班级概况选择器 -->
    <div class="class-selector-section">
      <div class="section-card">
        <div class="section-header">
          <div class="header-left">
            <i class="el-icon-school"></i>
            <div class="header-text">
              <h3>班级概况选择</h3>
              <p>选择目标班级，加载学生学情数据</p>
            </div>
          </div>
          <div class="header-right">
            <el-button type="text" size="small" @click="refreshClassData" :loading="loadingClass">
              <i class="el-icon-refresh"></i> 刷新数据
            </el-button>
          </div>
        </div>
        
        <div class="class-selector-container">
          <div class="selector-form">
            <el-form :model="classForm" label-position="top" size="medium">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="学校">
                    <el-select v-model="classForm.school" placeholder="选择学校" style="width: 100%">
                      <el-option label="实验中学" value="experimental" />
                      <el-option label="第一中学" value="first" />
                      <el-option label="师范附中" value="normal" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="年级">
                    <el-select v-model="classForm.grade" placeholder="选择年级" style="width: 100%">
                      <el-option label="九年级" value="9" />
                      <el-option label="八年级" value="8" />
                      <el-option label="七年级" value="7" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="班级">
                    <el-select v-model="classForm.classId" placeholder="选择班级" style="width: 100%" @change="loadStudentData">
                      <el-option 
                        v-for="cls in availableClasses" 
                        :key="cls.id" 
                        :label="cls.name" 
                        :value="cls.id" 
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="学科">
                    <el-select v-model="classForm.subject" placeholder="选择学科" style="width: 100%">
                      <el-option label="数学" value="math" />
                      <el-option label="物理" value="physics" />
                      <el-option label="化学" value="chemistry" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
          
          <!-- 班级基本信息 -->
          <div v-if="currentClass" class="class-info">
                      <div class="info-cards">
            <div class="info-card">
              <div class="card-icon students-icon">
                <i class="el-icon-user"></i>
              </div>
              <div class="card-content">
                <div class="card-number">{{ currentClass.studentCount }}</div>
                <div class="card-label">学生总数</div>
              </div>
            </div>
            <div class="info-card">
              <div class="card-icon average-icon">
                <i class="el-icon-trophy"></i>
              </div>
              <div class="card-content">
                <div class="card-number">{{ currentClass.averageScore }}</div>
                <div class="card-label">平均成绩</div>
              </div>
            </div>
            <div class="info-card">
              <div class="card-icon performance-icon">
                <i class="el-icon-data-line"></i>
              </div>
              <div class="card-content">
                <div class="card-number">{{ currentClass.performanceLevel }}</div>
                <div class="card-label">整体水平</div>
              </div>
            </div>
            <div class="info-card">
              <div class="card-icon special-icon">
                <i class="el-icon-star-on"></i>
              </div>
              <div class="card-content">
                <div class="card-number">{{ specialNeedsStudents.length }}</div>
                <div class="card-label">特殊关注</div>
              </div>
            </div>
          </div>
          
          <!-- 学情配置完整性提示 -->
          <div class="completeness-indicator">
            <div class="indicator-header">
              <h4><i class="el-icon-data-analysis"></i>学情配置完整性</h4>
              <div class="completeness-score">
                <span class="score">{{ completenessScore }}%</span>
                <span class="status" :class="completenessLevel">{{ completenessText }}</span>
              </div>
            </div>
            <div class="indicator-items">
              <div class="indicator-item" :class="{ completed: true }">
                <i class="el-icon-circle-check"></i>
                <span>班级基础信息</span>
              </div>
              <div class="indicator-item" :class="{ completed: homeworkAnalysisResults.length > 0 }">
                <i :class="homeworkAnalysisResults.length > 0 ? 'el-icon-circle-check' : 'el-icon-circle-close'"></i>
                <span>作业图片分析</span>
              </div>
              <div class="indicator-item" :class="{ completed: importPreviewData.filter(s => s.isValid).length > 0 }">
                <i :class="importPreviewData.filter(s => s.isValid).length > 0 ? 'el-icon-circle-check' : 'el-icon-circle-close'"></i>
                <span>学习档案导入</span>
              </div>
              <div class="indicator-item" :class="{ completed: specialNeedsStudents.length > 0 }">
                <i :class="specialNeedsStudents.length > 0 ? 'el-icon-circle-check' : 'el-icon-circle-close'"></i>
                <span>特殊需求识别</span>
              </div>
            </div>
            <div v-if="completenessScore < 100" class="improvement-suggestions">
              <p><strong>完善建议:</strong></p>
              <ul>
                <li v-if="homeworkAnalysisResults.length === 0">上传学生作业图片进行AI分析，获取真实学习水平数据</li>
                <li v-if="importPreviewData.filter(s => s.isValid).length === 0">批量导入学生档案，完善个人学习特征信息</li>
                <li v-if="specialNeedsStudents.length === 0">识别并标注有特殊学习需求的学生</li>
              </ul>
            </div>
          </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 学情分析仪表盘 -->
    <div v-if="currentClass" class="dashboard-section">
      <div class="section-card">
        <div class="section-header">
          <div class="header-left">
            <i class="el-icon-data-analysis"></i>
            <div class="header-text">
              <h3>学情分析仪表盘</h3>
              <p>多维度分析学生认知能力、知识基础和学习风格</p>
            </div>
          </div>
        </div>
        
        <div class="dashboard-container">
          <el-row :gutter="24">
            <!-- 认知能力雷达图 -->
            <el-col :span="8">
              <div class="chart-card">
                <div class="chart-header">
                  <h4><i class="el-icon-pie-chart"></i>班级认知能力分布</h4>
                </div>
                <div class="chart-content">
                  <div ref="abilityRadarChart" class="chart-container"></div>
                  <div class="chart-legend">
                    <div class="legend-item" v-for="item in cognitiveStats" :key="item.name">
                      <span class="legend-color" :style="{ backgroundColor: item.color }"></span>
                      <span class="legend-label">{{ item.name }}: {{ item.value }}%</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-col>
            
            <!-- 前测知识水平量表 -->
            <el-col :span="8">
              <div class="chart-card">
                <div class="chart-header">
                  <h4><i class="el-icon-document-checked"></i>前测知识水平</h4>
                  <el-button type="text" size="mini" @click="generatePreTest">
                    <i class="el-icon-plus"></i> 生成前测
                  </el-button>
                </div>
                <div class="chart-content">
                  <div class="knowledge-gauge">
                    <div class="gauge-container">
                      <svg width="200" height="120" viewBox="0 0 200 120">
                        <defs>
                          <linearGradient id="knowledgeGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                            <stop offset="0%" style="stop-color:#F56C6C;stop-opacity:1" />
                            <stop offset="50%" style="stop-color:#E6A23C;stop-opacity:1" />
                            <stop offset="100%" style="stop-color:#67C23A;stop-opacity:1" />
                          </linearGradient>
                        </defs>
                        <path d="M 30 90 A 70 70 0 0 1 170 90" stroke="url(#knowledgeGradient)" stroke-width="16" fill="none"/>
                        <line 
                          :x1="knowledgeNeedleX" 
                          :y1="knowledgeNeedleY" 
                          x2="100" 
                          y2="90" 
                          stroke="#2c3e50" 
                          stroke-width="4"
                          stroke-linecap="round"
                        />
                        <circle cx="100" cy="90" r="8" fill="#2c3e50"/>
                      </svg>
                      <div class="gauge-labels">
                        <span class="label-left">基础薄弱</span>
                        <span class="label-right">基础扎实</span>
                      </div>
                    </div>
                    <div class="gauge-score">
                      <span class="score-main">{{ preKnowledgeScore }}</span>
                      <span class="score-unit">分</span>
                      <p class="score-desc">{{ getKnowledgeLevel(preKnowledgeScore) }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </el-col>
            
            <!-- 学习风格分布 -->
            <el-col :span="8">
              <div class="chart-card">
                <div class="chart-header">
                  <h4><i class="el-icon-view"></i>学习风格分布</h4>
                </div>
                <div class="chart-content">
                  <div ref="learningStyleChart" class="chart-container"></div>
                  <div class="style-summary">
                    <div class="summary-item" v-for="style in learningStyles" :key="style.type">
                      <div class="style-header">
                        <span class="style-name">{{ style.name }}</span>
                        <span class="style-percentage">{{ style.percentage }}%</span>
                      </div>
                      <div class="style-bar">
                        <div 
                          class="style-fill" 
                          :style="{ 
                            width: style.percentage + '%', 
                            backgroundColor: style.color 
                          }"
                        ></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    
    <!-- 作业图片分析模块 -->
    <div v-if="currentClass" class="homework-analysis-section">
      <div class="section-card">
        <div class="section-header">
          <div class="header-left">
            <i class="el-icon-picture-outline"></i>
            <div class="header-text">
              <h3>学生作业图片分析</h3>
              <p>上传学生作业照片，AI智能分析学习水平和常见错误</p>
            </div>
          </div>
          <div class="header-right">
            <el-button type="text" size="small" @click="clearHomeworkAnalysis">
              <i class="el-icon-delete"></i> 清空分析
            </el-button>
          </div>
        </div>
        
        <div class="homework-container">
          <!-- 图片上传区域 -->
          <div class="upload-section">
            <div class="upload-header">
              <h4>📸 作业照片上传</h4>
              <p class="upload-description">支持JPG、PNG格式，最多20张图片，AI将自动识别和分析</p>
            </div>
            <el-upload
              ref="homeworkUpload"
              :auto-upload="false"
              :multiple="true"
              :accept="'image/*'"
              :on-change="handleFileChange"
              :on-remove="handleFileRemove"
              :file-list="homeworkFileList"
              list-type="picture-card"
              :limit="20"
              class="homework-uploader"
            >
              <div class="upload-trigger-compact">
                <i class="el-icon-plus"></i>
                <div class="upload-text-compact">上传作业照片</div>
              </div>
            </el-upload>
          </div>
          
          <!-- AI分析结果 -->
          <div v-if="homeworkAnalysisResults.length > 0" class="analysis-results">
            <div class="results-header">
              <h4><i class="el-icon-data-analysis"></i>AI分析结果</h4>
              <el-button type="primary" size="small" @click="generateHomeworkReport" :loading="generatingReport">
                <i class="el-icon-document"></i> 生成分析报告
              </el-button>
            </div>
            
            <div class="analysis-summary">
              <el-row :gutter="20">
                <el-col :span="6">
                  <div class="summary-card">
                    <div class="summary-icon difficulty-icon">
                      <i class="el-icon-warning"></i>
                    </div>
                    <div class="summary-content">
                      <div class="summary-number">{{ analysisSummary.averageDifficulty }}</div>
                      <div class="summary-label">平均难度</div>
                    </div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="summary-card">
                    <div class="summary-icon accuracy-icon">
                      <i class="el-icon-circle-check"></i>
                    </div>
                    <div class="summary-content">
                      <div class="summary-number">{{ analysisSummary.averageAccuracy }}%</div>
                      <div class="summary-label">平均正确率</div>
                    </div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="summary-card">
                    <div class="summary-icon error-icon">
                      <i class="el-icon-close"></i>
                    </div>
                    <div class="summary-content">
                      <div class="summary-number">{{ analysisSummary.commonErrors.length }}</div>
                      <div class="summary-label">常见错误类型</div>
                    </div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="summary-card">
                    <div class="summary-icon knowledge-icon">
                      <i class="el-icon-star-on"></i>
                    </div>
                    <div class="summary-content">
                      <div class="summary-number">{{ analysisSummary.weakKnowledgePoints.length }}</div>
                      <div class="summary-label">薄弱知识点</div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
            
            <!-- 详细分析结果 -->
            <div class="detailed-analysis">
              <el-tabs v-model="activeAnalysisTab" class="analysis-tabs">
                <el-tab-pane label="错误分析" name="errors">
                  <div class="error-analysis">
                    <div class="error-item" v-for="error in analysisSummary.commonErrors" :key="error.type">
                      <div class="error-header">
                        <span class="error-type">{{ error.type }}</span>
                        <span class="error-frequency">出现频率: {{ error.frequency }}%</span>
                      </div>
                      <div class="error-description">{{ error.description }}</div>
                      <div class="error-suggestions">
                        <span class="suggestion-label">改进建议:</span>
                        <span class="suggestion-text">{{ error.suggestion }}</span>
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
                
                <el-tab-pane label="知识点分析" name="knowledge">
                  <div class="knowledge-analysis">
                    <div class="knowledge-item" v-for="point in analysisSummary.weakKnowledgePoints" :key="point.name">
                      <div class="knowledge-header">
                        <span class="knowledge-name">{{ point.name }}</span>
                        <el-progress :percentage="point.mastery" :color="getProgressColor(point.mastery)"></el-progress>
                      </div>
                      <div class="knowledge-details">
                        <span class="detail-label">掌握情况:</span>
                        <span class="detail-text">{{ point.description }}</span>
                      </div>
                    </div>
                  </div>
                </el-tab-pane>
                
                <el-tab-pane label="学习建议" name="suggestions">
                  <div class="learning-suggestions">
                    <div class="suggestion-category" v-for="category in learningRecommendations" :key="category.type">
                      <h5 class="category-title">
                        <i :class="category.icon"></i>
                        {{ category.title }}
                      </h5>
                      <ul class="suggestion-list">
                        <li v-for="suggestion in category.suggestions" :key="suggestion">{{ suggestion }}</li>
                      </ul>
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 学习档案批量导入模块 -->
    <div v-if="currentClass" class="batch-import-section">
      <div class="section-card">
        <div class="section-header">
          <div class="header-left">
            <i class="el-icon-upload2"></i>
            <div class="header-text">
              <h3>学习档案批量导入</h3>
              <p>支持Excel/CSV格式的学生信息批量导入，快速建立班级档案</p>
            </div>
          </div>
          <div class="header-right">
            <el-button type="text" size="small" @click="downloadTemplate">
              <i class="el-icon-download"></i> 下载模板
            </el-button>
          </div>
        </div>
        
        <div class="import-container">
          <div class="import-steps">
            <el-steps :active="importStep" align-center>
              <el-step title="下载模板" description="下载标准导入模板"></el-step>
              <el-step title="填写数据" description="按模板格式填写学生信息"></el-step>
              <el-step title="上传文件" description="上传填写完成的文件"></el-step>
              <el-step title="数据验证" description="系统验证数据格式"></el-step>
              <el-step title="导入完成" description="确认导入学生档案"></el-step>
            </el-steps>
          </div>
          
          <div class="import-content">
            <!-- 文件上传区域 -->
            <div class="file-upload-section">
              <el-upload
                ref="fileUpload"
                :auto-upload="false"
                :accept="'.xlsx,.xls,.csv'"
                :on-change="handleImportFileChange"
                :file-list="importFileList"
                :limit="1"
                drag
                class="file-uploader"
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                <div class="el-upload__tip">支持 Excel (.xlsx, .xls) 和 CSV 格式文件</div>
              </el-upload>
            </div>
            
            <!-- 数据预览 -->
            <div v-if="importPreviewData.length > 0" class="data-preview">
              <div class="preview-header">
                <h4><i class="el-icon-view"></i>数据预览</h4>
                <div class="preview-stats">
                  <span class="stat-item">
                    <i class="el-icon-user"></i>
                    总记录数: {{ importPreviewData.length }}
                  </span>
                  <span class="stat-item valid">
                    <i class="el-icon-circle-check"></i>
                    有效: {{ validImportCount }}
                  </span>
                  <span class="stat-item invalid" v-if="invalidImportCount > 0">
                    <i class="el-icon-warning"></i>
                    错误: {{ invalidImportCount }}
                  </span>
                </div>
              </div>
              
              <el-table 
                :data="importPreviewData.slice(0, 10)" 
                border 
                class="preview-table"
                max-height="300"
              >
                <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
                <el-table-column prop="name" label="姓名" width="100"></el-table-column>
                <el-table-column prop="gender" label="性别" width="80"></el-table-column>
                <el-table-column prop="previousScore" label="入学成绩" width="100"></el-table-column>
                <el-table-column prop="learningStyle" label="学习风格" width="120"></el-table-column>
                <el-table-column prop="specialNeeds" label="特殊需求" min-width="150"></el-table-column>
                <el-table-column label="状态" width="100">
                  <template slot-scope="scope">
                    <el-tag :type="scope.row.isValid ? 'success' : 'danger'" size="mini">
                      {{ scope.row.isValid ? '有效' : '错误' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
              
              <div v-if="importPreviewData.length > 10" class="preview-more">
                显示前10条记录，共{{ importPreviewData.length }}条
              </div>
            </div>
            
            <!-- 错误信息 -->
            <div v-if="importErrors.length > 0" class="import-errors">
              <div class="errors-header">
                <h4><i class="el-icon-warning"></i>数据错误</h4>
                <el-button type="text" size="small" @click="exportErrors">
                  <i class="el-icon-download"></i> 导出错误列表
                </el-button>
              </div>
              <div class="error-list">
                <div v-for="error in importErrors.slice(0, 5)" :key="error.row" class="error-item">
                  <span class="error-row">第{{ error.row }}行:</span>
                  <span class="error-message">{{ error.message }}</span>
                </div>
                <div v-if="importErrors.length > 5" class="more-errors">
                  还有{{ importErrors.length - 5 }}个错误...
                </div>
              </div>
            </div>
            
            <!-- 导入操作 -->
            <div v-if="importPreviewData.length > 0" class="import-actions">
              <el-button @click="cancelImport">取消导入</el-button>
              <el-button 
                type="primary" 
                @click="confirmImport" 
                :disabled="invalidImportCount > 0"
                :loading="importingData"
              >
                <i class="el-icon-upload2"></i>
                确认导入 ({{ validImportCount }}条记录)
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button size="large" @click="saveDraft">
        <i class="el-icon-document"></i> 保存草稿
      </el-button>
      <el-button size="large" @click="exportConfiguration">
        <i class="el-icon-download"></i> 导出学情报告
      </el-button>
      <el-button size="large" @click="saveAndNext" type="primary" :disabled="!currentClass">
        <i class="el-icon-right"></i> 保存并继续
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StudentProfiling',
  data() {
    return {
      loadingClass: false,
      
      // 班级表单
      classForm: {
        school: '',
        grade: '',
        classId: '',
        subject: 'math'
      },
      
      // 可选班级
      availableClasses: [
        { id: 'class1', name: '九年级1班' },
        { id: 'class2', name: '九年级2班' },
        { id: 'class3', name: '九年级3班' }
      ],
      
      // 当前班级信息
      currentClass: null,
      
      // 二次函数认知能力统计
      cognitiveStats: [
        { name: '图像识别', value: 78, color: '#667eea' },
        { name: '对称性理解', value: 65, color: '#764ba2' },
        { name: '顶点计算', value: 82, color: '#f093fb' },
        { name: '开口判断', value: 88, color: '#f5576c' },
        { name: '平移变换', value: 74, color: '#4facfe' },
        { name: '实际应用', value: 68, color: '#43e97b' }
      ],
      
      // 前测知识得分
      preKnowledgeScore: 75,
      
      // 二次函数学习风格分布
      learningStyles: [
        { 
          type: 'visual', 
          name: '图像型', 
          percentage: 52, 
          color: '#667eea',
          description: '通过函数图像理解性质'
        },
        { 
          type: 'analytical', 
          name: '解析型', 
          percentage: 31, 
          color: '#764ba2',
          description: '通过解析式分析特征'
        },
        { 
          type: 'applied', 
          name: '应用型', 
          percentage: 17, 
          color: '#f093fb',
          description: '结合实际问题学习'
        }
      ],
      
      // 特殊需求学生
      specialNeedsStudents: [],
      
      // 作业图片分析相关
      homeworkFileList: [],
      homeworkAnalysisResults: [],
      activeAnalysisTab: 'errors',
      generatingReport: false,
      
      // 批量导入相关
      importStep: 0,
      importFileList: [],
      importPreviewData: [],
      importErrors: [],
      importingData: false
    }
  },
  
  computed: {
    knowledgeNeedleX() {
      const angle = ((this.preKnowledgeScore / 100) * Math.PI)
      return 100 + 60 * Math.cos(Math.PI - angle)
    },
    
    knowledgeNeedleY() {
      const angle = ((this.preKnowledgeScore / 100) * Math.PI)
      return 90 - 60 * Math.sin(Math.PI - angle)
    },
    
    validImportCount() {
      return this.importPreviewData.filter(item => item.isValid).length
    },
    
    invalidImportCount() {
      return this.importPreviewData.filter(item => !item.isValid).length
    },
    
    // 学情配置完整性评分
    completenessScore() {
      let score = 25 // 基础班级信息（必须）
      
      // 作业图片分析（25分）
      if (this.homeworkAnalysisResults.length > 0) {
        score += 25
      }
      
      // 学习档案导入（25分）
      if (this.importPreviewData.filter(s => s.isValid).length > 0) {
        score += 25
      }
      
      // 特殊需求识别（25分）
      if (this.specialNeedsStudents.length > 0) {
        score += 25
      }
      
      return score
    },
    
    // 完整性等级
    completenessLevel() {
      if (this.completenessScore >= 100) return 'excellent'
      if (this.completenessScore >= 75) return 'good'
      if (this.completenessScore >= 50) return 'fair'
      return 'poor'
    },
    
    // 完整性文本描述
    completenessText() {
      switch(this.completenessLevel) {
        case 'excellent': return '完善'
        case 'good': return '良好'
        case 'fair': return '一般'
        case 'poor': return '待完善'
        default: return '待完善'
      }
    },

    // 📝 动态生成作业分析汇总
    analysisSummary() {
      if (this.homeworkAnalysisResults.length === 0) {
        return this.getDefaultAnalysisSummary()
      }

      return this.generateDynamicAnalysisSummary()
    },

    // 💡 动态生成学习建议
    learningRecommendations() {
      if (this.homeworkAnalysisResults.length === 0) {
        return this.getDefaultLearningRecommendations()
      }

      return this.generateDynamicLearningRecommendations()
    }
  },
  
  methods: {
    // 加载学生数据
    loadStudentData() {
      if (!this.classForm.classId) return
      
      this.loadingClass = true
      
      // 模拟API调用
      setTimeout(() => {
        const selectedClass = this.availableClasses.find(c => c.id === this.classForm.classId)
        this.currentClass = {
          id: this.classForm.classId,
          name: selectedClass ? selectedClass.name : '',
          studentCount: 42,
          averageScore: 78.5,
          performanceLevel: '中上'
        }
        
        // 生成特殊需求学生数据
        this.specialNeedsStudents = [
          {
            id: 'special_1',
            name: '李小华',
            specialNeeds: ['学习困难', '注意力不集中']
          },
          {
            id: 'special_2', 
            name: '王思思',
            specialNeeds: ['情绪调节困难']
          }
        ]
        
        this.loadingClass = false
        this.$message.success('班级数据加载完成')
      }, 1500)
    },
    
    // 获取知识水平描述
    getKnowledgeLevel(score) {
      if (score >= 85) return '基础扎实'
      if (score >= 70) return '基础良好'
      if (score >= 60) return '基础一般'
      return '基础薄弱'
    },
    
    // 刷新班级数据
    refreshClassData() {
      if (this.classForm.classId) {
        this.loadStudentData()
      } else {
        this.$message.warning('请先选择班级')
      }
    },
    
    // 生成前测
    generatePreTest() {
      this.$message.info('正在基于知识点生成前测题目...')
      setTimeout(() => {
        this.$message.success('前测题目生成完成')
      }, 2000)
    },
    
    // 导出配置
    exportConfiguration() {
      this.$message.info('正在生成学情分析报告...')
      setTimeout(() => {
        this.$message.success('学情报告已生成并下载')
      }, 2000)
    },
    
    // 保存相关方法
    saveDraft() {
      const draftData = {
        classForm: this.classForm,
        currentClass: this.currentClass,
        specialNeedsStudents: this.specialNeedsStudents
      }
      localStorage.setItem('studentProfilingDraft', JSON.stringify(draftData))
      this.$message.success('草稿已保存')
    },
    
    saveAndNext() {
      if (!this.currentClass) {
        this.$message.warning('请先选择班级并加载学生数据')
        return
      }
      
      // 整合所有学情配置数据
      const profilingData = {
        // 基础班级信息
        classInfo: this.currentClass,
        
        // 认知能力分析（可能已被作业分析更新）
        cognitiveStats: this.cognitiveStats,
        
        // 知识水平评估（整合前测和作业分析结果）
        knowledgeAssessment: {
          preTestScore: this.preKnowledgeScore,
          weakKnowledgePoints: this.analysisSummary.weakKnowledgePoints,
          assessmentMethod: this.getAssessmentMethods()
        },
        
        // 学习风格分布（可能已被批量导入更新）
        learningStyles: this.learningStyles,
        
        // 特殊需求学生（可能包含导入的学生）
        specialNeeds: this.specialNeedsStudents,
        
        // 作业分析结果
        homeworkAnalysis: {
          hasAnalysis: this.homeworkAnalysisResults.length > 0,
          results: this.homeworkAnalysisResults,
          summary: this.analysisSummary,
          recommendations: this.learningRecommendations
        },
        
        // 批量导入信息
        batchImport: {
          hasImported: this.importPreviewData.length > 0,
          importedCount: this.importPreviewData.filter(s => s.isValid).length,
          lastImportDate: new Date().toISOString()
        },
        
        // 综合学情评估
        overallAssessment: this.generateOverallAssessment(),
        
        // 配置时间戳
        configuredAt: new Date().toISOString(),
        configuredBy: (() => {
          const userInfo = localStorage.getItem('userInfo')
          return userInfo ? JSON.parse(userInfo).name : 'Unknown'
        })()
      }
      
      // 保存到本地存储
      localStorage.setItem('studentProfiling', JSON.stringify(profilingData))
      
      // 保存到服务器（如果需要）
      this.saveToServer(profilingData)
      
      this.$message.success('学情配置已保存，包含作业分析和导入数据')
      
      // 触发步骤完成事件，传递完整数据
      this.$emit('step-completed', {
        step: 4,
        data: profilingData,
        next: 'lesson-planning'
      })
      
      // 跳转到下一步
      this.$router.push('/teaching-design/lesson-planning')
    },
    
    // 生成综合学情评估
    generateOverallAssessment() {
      const assessment = {
        classLevel: (this.currentClass && this.currentClass.performanceLevel) || '中等',
        studentCount: (this.currentClass && this.currentClass.studentCount) || 0,
        averageScore: (this.currentClass && this.currentClass.averageScore) || 0,
        
        // 基于认知能力的评估
        cognitiveStrengths: this.cognitiveStats
          .filter(stat => stat.value >= 75)
          .map(stat => stat.name),
        cognitiveWeaknesses: this.cognitiveStats
          .filter(stat => stat.value < 60)
          .map(stat => stat.name),
        
        // 基于作业分析的评估
        homeworkInsights: this.homeworkAnalysisResults.length > 0 ? {
          averageAccuracy: this.analysisSummary.averageAccuracy,
          mainErrorTypes: this.analysisSummary.commonErrors.slice(0, 3).map(e => e.type),
          criticalWeakPoints: this.analysisSummary.weakKnowledgePoints
            .filter(point => point.mastery < 60)
            .map(point => point.name)
        } : null,
        
        // 学习风格特征
        dominantLearningStyle: this.learningStyles.reduce((prev, current) => 
          prev.percentage > current.percentage ? prev : current
        ).name,
        
        // 特殊关注建议
        recommendations: this.generateClassRecommendations()
      }
      
      return assessment
    },
    
    // 生成班级建议
    generateClassRecommendations() {
      const recommendations = []
      
      // 基于平均分的建议
      if (this.currentClass && this.currentClass.averageScore < 60) {
        recommendations.push('建议加强基础知识巩固，采用分层教学策略')
      } else if (this.currentClass && this.currentClass.averageScore > 85) {
        recommendations.push('班级基础良好，可增加拓展性和挑战性内容')
      }
      
      // 基于特殊需求学生的建议
      if (this.specialNeedsStudents.length > 0) {
        recommendations.push(`需要特别关注${this.specialNeedsStudents.length}名特殊需求学生`)
      }
      
      // 基于学习风格的建议
      const visualStyle = this.learningStyles.find(s => s.name === '图像型')
      const visualLearners = (visualStyle && visualStyle.percentage) || 0
      if (visualLearners > 50) {
        recommendations.push('建议多使用图表、图像等视觉化教学工具')
      }
      
      // 基于作业分析的建议
      if (this.homeworkAnalysisResults.length > 0) {
        const errorTypes = this.analysisSummary.commonErrors
        if (errorTypes.some(e => e.type === '计算错误' && e.frequency > 30)) {
          recommendations.push('需要加强基础计算训练')
        }
        if (errorTypes.some(e => e.type === '概念理解错误' && e.frequency > 25)) {
          recommendations.push('概念教学需要更多实例和应用场景')
        }
      }
      
      return recommendations
    },
    
    // 获取评估方法
    getAssessmentMethods() {
      const methods = ['前测评估']
      
      if (this.homeworkAnalysisResults.length > 0) {
        methods.push('作业分析')
      }
      
      if (this.importPreviewData.length > 0) {
        methods.push('历史数据导入')
      }
      
      return methods
    },
    
    // 保存到服务器
    async saveToServer(data) {
      try {
        // 这里可以调用API保存到服务器
        // await saveStudentProfilingData(data)
        console.log('学情配置数据已准备保存到服务器:', data)
      } catch (error) {
        console.error('保存到服务器失败:', error)
        // 不影响主流程，只记录错误
      }
    },
    
    // 作业图片分析相关方法
    handleFileChange(file, fileList) {
      // 验证文件类型和大小
      if (!file.raw.type.startsWith('image/')) {
        this.$message.error('只能上传图片文件')
        return
      }
      
      if (file.raw.size / 1024 / 1024 > 10) {
        this.$message.error('图片大小不能超过10MB')
        return
      }
      
      // 更新文件列表
      this.homeworkFileList = fileList
      
      // 显示上传成功消息
      this.$message.success('图片上传成功，正在进行AI分析...')
      
      // 模拟AI分析过程
      setTimeout(() => {
        this.performHomeworkAnalysis(file)
      }, 1500)
    },
    
    handleFileRemove(file, fileList) {
      // 移除对应的分析结果
      this.homeworkAnalysisResults = this.homeworkAnalysisResults.filter(
        result => result.fileId !== file.uid
      )
      
      // 更新文件列表
      this.homeworkFileList = fileList
      

      
      this.$message.info('已移除图片及其分析结果')
    },
    
    performHomeworkAnalysis(file) {
      // 模拟完整的AI分析过程
      this.$message.info('正在进行OCR文字识别...')
      
      setTimeout(() => {
        this.$message.info('正在分析题目类型和难度...')
        
        setTimeout(() => {
          this.$message.info('正在评估答案正确性...')
          
          setTimeout(() => {
            const analysisResult = this.generateDetailedAnalysis(file)
            this.homeworkAnalysisResults.push(analysisResult)
            
            // 根据作业分析结果动态更新学情数据
            this.updateLearnerProfileFromHomework(analysisResult)
            

            
            this.$message.success(`AI分析完成！识别到${analysisResult.questions.length}道题目，学情数据已更新`)
          }, 800)
        }, 600)
      }, 400)
    },
    
    // 生成详细的分析结果
    generateDetailedAnalysis(file) {
      const fileName = file.name.toLowerCase()
      
      // 基于文件名和随机数生成种子，确保结果一致性
      const seed = this.hashCode(fileName + file.size)
      const random = this.seededRandom(seed)
      
      // 模拟识别的题目数量（使用正态分布）
      const questionCount = Math.max(1, Math.round(this.normalRandom(8, 3, random)))
      
      // 生成题目列表
      const questions = []
      for (let i = 0; i < questionCount; i++) {
        questions.push(this.generateQuestionAnalysis(i + 1, random))
      }
      
      // 计算整体统计
      const totalQuestions = questions.length
      const correctCount = questions.filter(q => q.isCorrect).length
      const accuracy = Math.round((correctCount / totalQuestions) * 100)
      
      // 分析难度分布
      const difficultyStats = this.calculateDifficultyStats(questions)
      const avgDifficulty = this.getAverageDifficulty(difficultyStats)
      
      // 收集错误类型
      const errorTypes = this.collectErrorTypes(questions)
      
      // 识别薄弱知识点
      const weakKnowledgePoints = this.identifyWeakKnowledgePoints(questions)
      
      return {
        fileId: file.uid,
        fileName: file.name,
        fileSize: file.size,
        analysisTime: new Date().toISOString(),
        
        // 基础统计
        totalQuestions: totalQuestions,
        correctCount: correctCount,
        accuracy: accuracy,
        difficulty: avgDifficulty,
        
        // 详细分析
        questions: questions,
        difficultyStats: difficultyStats,
        errorTypes: errorTypes,
        weakKnowledgePoints: weakKnowledgePoints,
        
        // OCR识别信息
        ocrConfidence: Math.round(85 + random() * 10), // 85-95%
        recognizedText: this.generateMockOCRText(questions),
        
        // 分析建议
        recommendations: this.generateQuestionRecommendations(questions, errorTypes)
      }
    },
    
    // 生成单个题目的分析
    generateQuestionAnalysis(questionIndex, random) {
      // 定义二次函数题目类型和知识点
      const questionTypes = [
        { type: '顶点坐标计算', knowledge: '顶点坐标求解', difficulty: 2 },
        { type: '对称轴求解', knowledge: '对称轴性质', difficulty: 1 },
        { type: '开口方向判断', knowledge: '开口方向判断', difficulty: 1 },
        { type: '图像平移', knowledge: '函数图像平移', difficulty: 3 },
        { type: '最值求解', knowledge: '最值问题求解', difficulty: 4 },
        { type: '解析式转换', knowledge: '解析式转换', difficulty: 3 },
        { type: '图像绘制', knowledge: '图像描点绘制', difficulty: 2 },
        { type: '实际应用', knowledge: '实际应用建模', difficulty: 5 }
      ]
      
      const questionType = questionTypes[Math.floor(random() * questionTypes.length)]
      
      // 基于题目难度计算正确率（使用逻辑函数）
      const difficultyFactor = questionType.difficulty / 5
      const baseAccuracy = 0.8 - difficultyFactor * 0.3 // 基础正确率
      const personalFactor = 0.5 + random() * 0.3 // 个人能力因子
      const correctProbability = Math.max(0.1, Math.min(0.95, baseAccuracy * personalFactor))
      
      const isCorrect = random() < correctProbability
      
      // 生成模拟的题目内容
      const questionContent = this.generateMockQuestion(questionType, questionIndex)
      
      // 如果答错，生成错误分析
      const errorAnalysis = isCorrect ? null : this.generateErrorAnalysis(questionType, random)
      
      // 计算用时（基于难度的对数分布）
      const baseTime = 60 + questionType.difficulty * 120 // 基础用时
      const timeVariation = random() * 0.5 + 0.75 // 0.75-1.25倍
      const solveTime = Math.round(baseTime * timeVariation)
      
      return {
        questionIndex: questionIndex,
        type: questionType.type,
        knowledge: questionType.knowledge,
        difficulty: questionType.difficulty,
        content: questionContent,
        isCorrect: isCorrect,
        confidence: Math.round(80 + random() * 15), // OCR识别置信度
        solveTime: solveTime,
        errorAnalysis: errorAnalysis,
        score: isCorrect ? 100 : Math.round(20 + random() * 30) // 部分分
      }
    },
    
    // 生成二次函数相关题目内容
    generateMockQuestion(questionType, index) {
      const templates = {
        '顶点坐标计算': [
          `求二次函数y = x² - ${this.randomInt(2, 8)}x + ${this.randomInt(1, 6)}的顶点坐标`,
          `已知抛物线y = ${this.randomInt(2, 5)}x² + ${this.randomInt(4, 12)}x + ${this.randomInt(1, 8)}，求其顶点坐标`,
          `将二次函数y = x² + ${this.randomInt(2, 6)}x - ${this.randomInt(1, 5)}配方，并求顶点坐标`
        ],
        '对称轴求解': [
          `求抛物线y = ${this.randomInt(2, 4)}x² - ${this.randomInt(6, 16)}x + ${this.randomInt(1, 8)}的对称轴方程`,
          `已知二次函数过点(${this.randomInt(1, 4)}, ${this.randomInt(5, 15)})和(${this.randomInt(6, 10)}, ${this.randomInt(5, 15)})，求对称轴`,
          `抛物线y = -x² + ${this.randomInt(4, 10)}x + ${this.randomInt(2, 6)}的对称轴是什么？`
        ],
        '开口方向判断': [
          `判断抛物线y = ${this.randomInt(-5, -1)}x² + ${this.randomInt(2, 8)}x + ${this.randomInt(1, 6)}的开口方向`,
          `二次函数y = ${this.randomInt(1, 5)}x² - ${this.randomInt(4, 12)}x + ${this.randomInt(2, 8)}开口向哪个方向？`,
          `已知抛物线开口向下，写出一个符合条件的二次函数解析式`
        ],
        '图像平移': [
          `将抛物线y = x²向右平移${this.randomInt(2, 5)}个单位，再向上平移${this.randomInt(1, 4)}个单位，求新函数解析式`,
          `抛物线y = 2x²经过怎样的平移得到y = 2(x-${this.randomInt(1, 4)})² + ${this.randomInt(2, 6)}？`,
          `函数y = -x² + ${this.randomInt(2, 6)}可以看作y = -x²经过怎样的变换得到？`
        ],
        '最值求解': [
          `求二次函数y = x² - ${this.randomInt(4, 8)}x + ${this.randomInt(3, 9)}的最小值`,
          `当x∈[${this.randomInt(0, 2)}, ${this.randomInt(4, 6)}]时，求y = -x² + ${this.randomInt(4, 8)}x - ${this.randomInt(1, 3)}的最大值`,
          `已知二次函数y = ${this.randomInt(2, 4)}x² + ${this.randomInt(4, 12)}x + ${this.randomInt(5, 15)}，求其最值`
        ],
        '解析式转换': [
          `将y = x² - ${this.randomInt(4, 8)}x + ${this.randomInt(3, 9)}化为顶点式`,
          `已知抛物线顶点为(${this.randomInt(1, 5)}, ${this.randomInt(-3, 3)})，过点(0, ${this.randomInt(2, 8)})，求解析式`,
          `抛物线与x轴交点为(${this.randomInt(1, 3)}, 0)和(${this.randomInt(5, 8)}, 0)，与y轴交点为(0, ${this.randomInt(6, 12)})，求解析式`
        ],
        '图像绘制': [
          `描点绘制函数y = x² - ${this.randomInt(2, 4)}x的图像`,
          `画出抛物线y = -(x-${this.randomInt(1, 3)})² + ${this.randomInt(2, 5)}的图像`,
          `利用平移方法画出y = 2x² + ${this.randomInt(4, 8)}x + ${this.randomInt(2, 6)}的图像`
        ],
        '实际应用': [
          `某商品每件成本${this.randomInt(20, 40)}元，售价x元，每天销量为(${this.randomInt(100, 200)}-${this.randomInt(2, 5)}x)件，求利润关于售价的函数关系`,
          `足球被踢出后的高度h(米)与时间t(秒)满足h = -5t² + ${this.randomInt(15, 25)}t，求足球的最大高度`,
          `某拱桥的拱顶距水面${this.randomInt(8, 15)}米，桥宽${this.randomInt(20, 40)}米，建立坐标系求拱桥的函数解析式`
        ]
      }
      
      const typeTemplates = templates[questionType.type] || ['二次函数题目识别中...']
      return `第${index}题: ` + typeTemplates[Math.floor(Math.random() * typeTemplates.length)]
    },
    
    // 生成二次函数错误分析
    generateErrorAnalysis(questionType, random) {
      const errorTypes = {
        '顶点坐标计算': [
          { type: '顶点坐标计算错误', desc: '顶点坐标公式记忆或应用错误', suggestion: '熟练掌握顶点坐标公式' },
          { type: '配方法错误', desc: '配方过程中计算出现错误', suggestion: '加强配方法练习' },
          { type: '公式替换错误', desc: '将系数代入顶点公式时出错', suggestion: '仔细核对系数代入' }
        ],
        '对称轴求解': [
          { type: '对称轴识别错误', desc: '对称轴公式x=-b/(2a)记忆错误', suggestion: '加强对称轴公式记忆' },
          { type: '系数识别错误', desc: '无法正确识别二次项和一次项系数', suggestion: '重点练习系数识别' },
          { type: '计算错误', desc: '对称轴计算过程中出现计算错误', suggestion: '提高计算准确性' }
        ],
        '开口方向判断': [
          { type: '开口方向判断错误', desc: '无法正确判断抛物线开口方向', suggestion: '理解二次项系数a的作用' },
          { type: '系数符号理解错误', desc: '对a值正负性与开口方向关系理解错误', suggestion: '牢记a>0开口向上，a<0开口向下' },
          { type: '特殊情况判断错误', desc: '对a=0等特殊情况处理错误', suggestion: '注意二次函数的定义条件' }
        ],
        '图像平移': [
          { type: '图像平移错误', desc: '平移方向和距离判断错误', suggestion: '掌握"左加右减，上加下减"规律' },
          { type: '顶点式理解错误', desc: '对顶点式中平移参数理解错误', suggestion: '深入理解顶点式的意义' },
          { type: '复合平移错误', desc: '多步平移过程中出现错误', suggestion: '逐步分析每个平移变换' }
        ],
        '最值求解': [
          { type: '最值问题错误', desc: '无法正确求解最大值或最小值', suggestion: '结合顶点坐标理解最值' },
          { type: '定义域限制错误', desc: '忽略自变量取值范围的限制', suggestion: '注意实际问题中的定义域' },
          { type: '最值性质理解错误', desc: '对开口方向与最值关系理解错误', suggestion: '理解开口方向决定最值类型' }
        ],
        '解析式转换': [
          { type: '解析式转换错误', desc: '三种形式间转换出现错误', suggestion: '加强三种形式的转换练习' },
          { type: '待定系数法错误', desc: '使用待定系数法时设立方程错误', suggestion: '掌握待定系数法的步骤' },
          { type: '形式选择错误', desc: '不知道何时使用哪种形式', suggestion: '理解各形式的适用场景' }
        ],
        '图像绘制': [
          { type: '图像描点错误', desc: '描点位置不准确或描点数量不够', suggestion: '提高描点精度，增加关键点' },
          { type: '图像连线错误', desc: '抛物线连线不光滑或形状不正确', suggestion: '练习抛物线的绘制技巧' },
          { type: '对称性利用错误', desc: '未能利用抛物线的对称性', suggestion: '充分利用对称轴对称的特点' }
        ],
        '实际应用': [
          { type: '实际应用错误', desc: '无法将实际问题转化为二次函数模型', suggestion: '加强实际问题建模练习' },
          { type: '变量设置错误', desc: '自变量和因变量设置不当', suggestion: '仔细分析问题中的变量关系' },
          { type: '实际意义理解错误', desc: '对函数结果的实际意义理解错误', suggestion: '注意数学结果的实际意义' }
        ]
      }
      
      const typeErrors = errorTypes[questionType.type] || errorTypes['计算题']
      const error = typeErrors[Math.floor(random() * typeErrors.length)]
      
      return {
        errorType: error.type,
        description: error.desc,
        suggestion: error.suggestion,
        severity: Math.floor(random() * 3) + 1, // 1-3级错误严重程度
        frequency: Math.round(20 + random() * 40) // 该错误类型在班级中的出现频率
      }
    },
    
    // 计算难度统计
    calculateDifficultyStats(questions) {
      const stats = { easy: 0, medium: 0, hard: 0 }
      
      questions.forEach(q => {
        if (q.difficulty <= 2) stats.easy++
        else if (q.difficulty <= 3) stats.medium++
        else stats.hard++
      })
      
      const total = questions.length
      return {
        easy: { count: stats.easy, percentage: Math.round((stats.easy / total) * 100) },
        medium: { count: stats.medium, percentage: Math.round((stats.medium / total) * 100) },
        hard: { count: stats.hard, percentage: Math.round((stats.hard / total) * 100) }
      }
    },
    
    // 获取平均难度
    getAverageDifficulty(difficultyStats) {
      if (difficultyStats.hard.percentage > 40) return '困难'
      if (difficultyStats.easy.percentage > 60) return '简单'
      return '中等'
    },
    
    // 收集错误类型
    collectErrorTypes(questions) {
      const errorMap = new Map()
      
      questions.forEach(q => {
        if (!q.isCorrect && q.errorAnalysis) {
          const errorType = q.errorAnalysis.errorType
          if (errorMap.has(errorType)) {
            errorMap.set(errorType, errorMap.get(errorType) + 1)
          } else {
            errorMap.set(errorType, 1)
          }
        }
      })
      
      const totalErrors = Array.from(errorMap.values()).reduce((sum, count) => sum + count, 0)
      
      return Array.from(errorMap.entries()).map(([type, count]) => ({
        type: type,
        count: count,
        frequency: Math.round((count / totalErrors) * 100) || 0
      })).sort((a, b) => b.count - a.count)
    },
    
    // 识别薄弱知识点
    identifyWeakKnowledgePoints(questions) {
      const knowledgeMap = new Map()
      
      questions.forEach(q => {
        const knowledge = q.knowledge
        if (!knowledgeMap.has(knowledge)) {
          knowledgeMap.set(knowledge, { total: 0, correct: 0 })
        }
        
        const stats = knowledgeMap.get(knowledge)
        stats.total++
        if (q.isCorrect) stats.correct++
      })
      
      return Array.from(knowledgeMap.entries())
        .map(([knowledge, stats]) => ({
          name: knowledge,
          mastery: Math.round((stats.correct / stats.total) * 100),
          totalQuestions: stats.total,
          correctCount: stats.correct
        }))
        .filter(point => point.mastery < 80) // 只显示掌握度低于80%的知识点
        .sort((a, b) => a.mastery - b.mastery)
    },
    
    // 生成OCR模拟文本
    generateMockOCRText(questions) {
      return questions.map(q => `${q.content} [识别置信度: ${q.confidence}%]`).join('\n\n')
    },
    
    // 生成题目建议
    generateQuestionRecommendations(questions, errorTypes) {
      const recommendations = []
      
      // 基于正确率的建议
      const accuracy = (questions.filter(q => q.isCorrect).length / questions.length) * 100
      if (accuracy < 60) {
        recommendations.push('整体正确率较低，建议加强基础知识复习')
      } else if (accuracy > 90) {
        recommendations.push('掌握情况良好，可以尝试更有挑战性的题目')
      }
      
      // 基于错误类型的建议
      if (errorTypes.length > 0) {
        const mainError = errorTypes[0]
        recommendations.push(`主要错误类型是"${mainError.type}"，建议重点关注相关知识点`)
      }
      
      // 基于难度分布的建议
      const hardQuestions = questions.filter(q => q.difficulty >= 4)
      if (hardQuestions.length > 0) {
        const hardAccuracy = (hardQuestions.filter(q => q.isCorrect).length / hardQuestions.length) * 100
        if (hardAccuracy < 40) {
          recommendations.push('困难题目掌握不够，建议循序渐进提高难度')
        }
      }
      
      return recommendations
    },
    

    
    // 辅助函数
    hashCode(str) {
      let hash = 0
      for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i)
        hash = ((hash << 5) - hash) + char
        hash = hash & hash // Convert to 32bit integer
      }
      return Math.abs(hash)
    },
    
    seededRandom(seed) {
      let value = seed
      return function() {
        value = (value * 9301 + 49297) % 233280
        return value / 233280
      }
    },
    
    normalRandom(mean, stdDev, random) {
      // Box-Muller变换生成正态分布随机数
      const u1 = random()
      const u2 = random()
      const z0 = Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2)
      return z0 * stdDev + mean
    },
    
    randomInt(min, max) {
      return Math.floor(Math.random() * (max - min + 1)) + min
    },
    
    getErrorDescription(errorType) {
      const descriptions = {
        '顶点坐标计算错误': '二次函数顶点坐标公式应用不准确，特别是配方法和公式法混淆',
        '对称轴识别错误': '对称轴方程书写不规范，或者无法正确识别对称轴位置',
        '开口方向判断错误': '无法正确判断抛物线开口方向，或者a值正负性理解错误',
        '图像平移错误': '函数图像平移规律掌握不清，左右平移和上下平移容易搞混',
        '最值问题错误': '无法正确求解二次函数的最大值或最小值',
        '解析式转换错误': '一般式、顶点式、交点式之间的转换出现错误',
        '图像描点错误': '绘制二次函数图像时描点不准确或连线不光滑',
        '实际应用错误': '无法将实际问题转化为二次函数模型'
      }
      return descriptions[errorType] || '二次函数相关错误，需要进一步分析'
    },
    
    getErrorSuggestion(errorType) {
      const suggestions = {
        '顶点坐标计算错误': '熟练掌握顶点坐标公式，多练习配方法求顶点坐标',
        '对称轴识别错误': '加强对称轴方程的记忆和应用，注意x=-b/(2a)公式',
        '开口方向判断错误': '重点理解二次项系数a的作用，a>0开口向上，a<0开口向下',
        '图像平移错误': '通过大量图像对比练习，掌握"左加右减，上加下减"规律',
        '最值问题错误': '结合顶点坐标和对称轴，理解二次函数的最值特性',
        '解析式转换错误': '加强三种形式间的转换练习，理解各形式的特点和适用场景',
        '图像描点错误': '提高描点精度，注意对称点的利用，多练习手绘抛物线',
        '实际应用错误': '加强建模能力，多做抛物线实际应用题，如抛物运动、利润问题等'
      }
      return suggestions[errorType] || '建议重点复习二次函数基础概念'
    },
    
    getKnowledgeDescription(knowledgeName) {
      const descriptions = {
        '二次函数基本形式': '对y=ax²+bx+c的基本结构理解不够深入',
        '顶点式应用': '顶点式y=a(x-h)²+k的变换规律掌握不熟练',
        '交点式理解': '交点式y=a(x-x₁)(x-x₂)与图像的关系理解不清',
        '对称轴性质': '对称轴x=-b/(2a)的计算和应用存在困难',
        '顶点坐标求解': '顶点坐标的多种求法掌握不够熟练',
        '开口方向判断': '二次项系数a对图像开口的影响理解不足',
        '函数图像平移': '图像平移变换规律理解不够清晰',
        '最值问题求解': '利用二次函数求最值的方法掌握不够',
        '与坐标轴交点': '函数与x轴、y轴交点的求解方法不熟练',
        '实际应用建模': '二次函数在实际问题中的应用能力有待提高'
      }
      return descriptions[knowledgeName] || '该二次函数知识点需要重点关注'
    },
    
    // 根据作业分析更新学习者特征
    updateLearnerProfileFromHomework(analysisResult) {
      // 更新前测知识得分（根据作业表现调整）
      const actualScore = analysisResult.accuracy
      this.preKnowledgeScore = Math.round((this.preKnowledgeScore + actualScore) / 2)
      
      // 更新认知能力统计（基于作业表现）
      if (analysisResult.difficulty === '困难' && analysisResult.accuracy > 80) {
        // 困难题目高准确率，提升问题解决能力
        const problemSolvingIndex = this.cognitiveStats.findIndex(item => item.name === '问题解决')
        if (problemSolvingIndex >= 0) {
          this.cognitiveStats[problemSolvingIndex].value = Math.min(100, this.cognitiveStats[problemSolvingIndex].value + 5)
        }
      }
      
      // 根据错误类型调整认知能力
      if (analysisResult.errorTypes && analysisResult.errorTypes.length > 0) {
        analysisResult.errorTypes.forEach(error => {
          if (error.type === '计算错误') {
            const computingIndex = this.cognitiveStats.findIndex(item => item.name === '计算能力')
            if (computingIndex >= 0) {
              this.cognitiveStats[computingIndex].value = Math.max(0, this.cognitiveStats[computingIndex].value - 3)
            }
          } else if (error.type === '概念理解错误') {
            const conceptIndex = this.cognitiveStats.findIndex(item => item.name === '概念理解')
            if (conceptIndex >= 0) {
              this.cognitiveStats[conceptIndex].value = Math.max(0, this.cognitiveStats[conceptIndex].value - 5)
            }
          }
        })
      }
      
      // 更新薄弱知识点掌握度
      if (analysisResult.weakKnowledgePoints && analysisResult.weakKnowledgePoints.length > 0) {
        analysisResult.weakKnowledgePoints.forEach(point => {
          const existingPoint = this.analysisSummary.weakKnowledgePoints.find(wp => wp.name === point.name)
          if (existingPoint) {
            existingPoint.mastery = Math.max(0, existingPoint.mastery - 5)
          }
        })
      }
      
      // 触发学情更新事件
      this.$emit('learner-profile-updated', {
        source: 'homework-analysis',
        data: {
          knowledgeScore: this.preKnowledgeScore,
          cognitiveStats: this.cognitiveStats,
          weakKnowledgePoints: this.analysisSummary.weakKnowledgePoints
        }
      })
    },
    
    clearHomeworkAnalysis() {
      this.$confirm('确定要清空所有分析结果吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.homeworkFileList = []
        this.homeworkAnalysisResults = []
        this.$refs.homeworkUpload.clearFiles()
        this.$message.success('已清空分析结果')
      })
    },
    
    generateHomeworkReport() {
      this.generatingReport = true
      setTimeout(() => {
        this.generatingReport = false
        this.$message.success('作业分析报告已生成并下载')
      }, 3000)
    },
    
    getProgressColor(percentage) {
      if (percentage < 60) return '#F56C6C'
      if (percentage < 80) return '#E6A23C'
      return '#67C23A'
    },
    
    // 批量导入相关方法
    downloadTemplate() {
      // 创建标准模板数据（与 student_template.csv 一致）
      const templateData = [
        ['学号', '姓名', '性别', '入学成绩', '学习风格', '特殊需求', '备注'],
        ['2024001', '张三', '男', '85', '图像型', '无', '数学基础较好'],
        ['2024002', '李小红', '女', '78', '解析型', '注意力不集中', '需要额外关注'],
        ['2024003', '王明', '男', '92', '应用型', '无', '理解能力强'],
        ['2024004', '陈美丽', '女', '67', '图像型', '学习困难', '需要个别辅导'],
        ['2024005', '刘强', '男', '88', '解析型', '无', '逻辑思维能力强']
      ]
      
      // 模拟下载
      this.$message.success('标准模板文件已下载，包含示例数据格式')
      this.importStep = 1
    },
    
    handleImportFileChange(file, fileList) {
      const isExcel = file.raw.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                     file.raw.type === 'application/vnd.ms-excel'
      const isCSV = file.raw.type === 'text/csv' || file.name.endsWith('.csv')
      const isLt5M = file.raw.size / 1024 / 1024 < 5
      
      if (!isExcel && !isCSV) {
        this.$message.error('只能上传Excel或CSV文件')
        return
      }
      if (!isLt5M) {
        this.$message.error('文件大小不能超过5MB')
        return
      }
      
      // 更新文件列表
      this.importFileList = fileList
      this.importStep = 2
      
      // 模拟上传成功
      setTimeout(() => {
        this.importStep = 3
        this.$message.success('文件上传成功，正在解析数据...')
        
        // 模拟数据解析
        setTimeout(() => {
          this.parseImportData(file)
        }, 2000)
      }, 500)
    },
    
    parseImportData(file) {
      // 使用 student_template.csv 标准模板数据
      const mockData = [
        {
          studentId: '2024001',
          name: '张三',
          gender: '男',
          previousScore: 85,
          learningStyle: '图像型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024002',
          name: '李小红',
          gender: '女',
          previousScore: 78,
          learningStyle: '解析型',
          specialNeeds: '注意力不集中',
          isValid: true
        },
        {
          studentId: '2024003',
          name: '王明',
          gender: '男',
          previousScore: 92,
          learningStyle: '应用型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024004',
          name: '陈美丽',
          gender: '女',
          previousScore: 67,
          learningStyle: '图像型',
          specialNeeds: '学习困难',
          isValid: true
        },
        {
          studentId: '2024005',
          name: '刘强',
          gender: '男',
          previousScore: 88,
          learningStyle: '解析型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024006',
          name: '赵雪',
          gender: '女',
          previousScore: 72,
          learningStyle: '图像型',
          specialNeeds: '视力问题',
          isValid: true
        },
        {
          studentId: '2024007',
          name: '孙浩',
          gender: '男',
          previousScore: 95,
          learningStyle: '应用型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024008',
          name: '周婷婷',
          gender: '女',
          previousScore: 81,
          learningStyle: '解析型',
          specialNeeds: '情绪调节困难',
          isValid: true
        },
        {
          studentId: '2024009',
          name: '吴杰',
          gender: '男',
          previousScore: 76,
          learningStyle: '图像型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024010',
          name: '郑芳',
          gender: '女',
          previousScore: 89,
          learningStyle: '应用型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024011',
          name: '马小军',
          gender: '男',
          previousScore: 64,
          learningStyle: '图像型',
          specialNeeds: '计算困难',
          isValid: true
        },
        {
          studentId: '2024012',
          name: '林静',
          gender: '女',
          previousScore: 83,
          learningStyle: '解析型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024013',
          name: '黄勇',
          gender: '男',
          previousScore: 77,
          learningStyle: '应用型',
          specialNeeds: '听力问题',
          isValid: true
        },
        {
          studentId: '2024014',
          name: '许梅',
          gender: '女',
          previousScore: 90,
          learningStyle: '图像型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024015',
          name: '曹亮',
          gender: '男',
          previousScore: 86,
          learningStyle: '解析型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024016',
          name: '丁小华',
          gender: '女',
          previousScore: 70,
          learningStyle: '图像型',
          specialNeeds: '家庭困难',
          isValid: true
        },
        {
          studentId: '2024017',
          name: '范伟',
          gender: '男',
          previousScore: 79,
          learningStyle: '应用型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024018',
          name: '高玲',
          gender: '女',
          previousScore: 84,
          learningStyle: '解析型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024019',
          name: '韩磊',
          gender: '男',
          previousScore: 73,
          learningStyle: '图像型',
          specialNeeds: '注意力缺陷',
          isValid: true
        },
        {
          studentId: '2024020',
          name: '江雨',
          gender: '女',
          previousScore: 87,
          learningStyle: '应用型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024021',
          name: '孔明',
          gender: '男',
          previousScore: 91,
          learningStyle: '解析型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024022',
          name: '梁小燕',
          gender: '女',
          previousScore: 75,
          learningStyle: '图像型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024023',
          name: '宁强',
          gender: '男',
          previousScore: 82,
          learningStyle: '应用型',
          specialNeeds: '轻微自闭',
          isValid: true
        },
        {
          studentId: '2024024',
          name: '欧阳雪',
          gender: '女',
          previousScore: 88,
          learningStyle: '解析型',
          specialNeeds: '无',
          isValid: true
        },
        {
          studentId: '2024025',
          name: '潘杰',
          gender: '男',
          previousScore: 69,
          learningStyle: '图像型',
          specialNeeds: '学习障碍',
          isValid: true
        }
      ]
      
      this.importPreviewData = mockData
      this.importErrors = [] // 标准模板没有错误
      
      this.importStep = 4
      this.$message.success('数据解析完成，共25条学生记录，全部有效')
    },
    
    exportErrors() {
      this.$message.info('正在导出错误列表...')
      setTimeout(() => {
        this.$message.success('错误列表已导出')
      }, 1000)
    },
    
    cancelImport() {
      this.$confirm('确定要取消导入吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.resetImport()
      })
    },
    
    confirmImport() {
      if (this.invalidImportCount > 0) {
        this.$message.warning('请先处理数据错误')
        return
      }
      
      this.importingData = true
      
      // 模拟导入过程
      setTimeout(() => {
        this.importingData = false
        this.importStep = 5
        this.$message.success(`成功导入${this.validImportCount}条学生档案，班级总人数已更新`)
        
        // 根据导入数据更新班级和学情统计
        this.updateClassDataFromImport()
        
        setTimeout(() => {
          this.resetImport()
        }, 3000)
      }, 3000)
    },
    
    // 根据导入数据更新班级和学情统计
    updateClassDataFromImport() {
      const validStudents = this.importPreviewData.filter(student => student.isValid)
      
      if (this.currentClass && validStudents.length > 0) {
        // 更新班级学生数量
        this.currentClass.studentCount += validStudents.length
        
        // 计算新的平均成绩
        const importedScores = validStudents
          .filter(s => s.previousScore && !isNaN(s.previousScore))
          .map(s => Number(s.previousScore))
        
        if (importedScores.length > 0) {
          const importedAverage = importedScores.reduce((sum, score) => sum + score, 0) / importedScores.length
          const totalStudents = this.currentClass.studentCount
          const existingStudents = totalStudents - validStudents.length
          const existingAverage = this.currentClass.averageScore
          
          // 加权平均计算新的班级平均分
          this.currentClass.averageScore = Number((
            (existingAverage * existingStudents + importedAverage * validStudents.length) / totalStudents
          ).toFixed(1))
        }
        
        // 更新学习风格分布
        this.updateLearningStyleDistribution(validStudents)
        
        // 统计特殊需求学生
        this.updateSpecialNeedsStudents(validStudents)
        
        // 重新评估班级整体水平
        this.updateClassPerformanceLevel()
        
        // 触发班级数据更新事件
        this.$emit('class-data-updated', {
          source: 'batch-import',
          classInfo: this.currentClass,
          importedCount: validStudents.length
        })
        
        this.$message.success('班级学情数据已更新')
      }
    },
    
    // 更新学习风格分布
    updateLearningStyleDistribution(importedStudents) {
      const styleCount = {
        '图像型': 0,
        '解析型': 0,
        '应用型': 0
      }
      
      // 统计导入学生的学习风格
      importedStudents.forEach(student => {
        if (student.learningStyle && styleCount.hasOwnProperty(student.learningStyle)) {
          styleCount[student.learningStyle]++
        }
      })
      
      const totalImported = importedStudents.length
      const existingTotal = this.currentClass.studentCount - totalImported
      
      // 更新学习风格百分比
      this.learningStyles.forEach(style => {
        const styleName = style.name
        if (styleCount[styleName]) {
          const existingCount = Math.round((style.percentage / 100) * existingTotal)
          const newCount = existingCount + styleCount[styleName]
          style.percentage = Math.round((newCount / this.currentClass.studentCount) * 100)
        }
      })
    },
    
    // 更新特殊需求学生列表
    updateSpecialNeedsStudents(importedStudents) {
      const newSpecialNeeds = importedStudents
        .filter(student => student.specialNeeds && student.specialNeeds !== '无')
        .map(student => ({
          id: `imported_${student.studentId}`,
          name: student.name,
          specialNeeds: [student.specialNeeds]
        }))
      
      this.specialNeedsStudents = [...this.specialNeedsStudents, ...newSpecialNeeds]
    },
    
    // 重新评估班级整体水平
    updateClassPerformanceLevel() {
      const avgScore = this.currentClass.averageScore
      if (avgScore >= 85) {
        this.currentClass.performanceLevel = '优秀'
      } else if (avgScore >= 75) {
        this.currentClass.performanceLevel = '良好'
      } else if (avgScore >= 65) {
        this.currentClass.performanceLevel = '中等'
      } else if (avgScore >= 55) {
        this.currentClass.performanceLevel = '中下'
      } else {
        this.currentClass.performanceLevel = '待提升'
      }
    },
    
    resetImport() {
      this.importStep = 0
      this.importFileList = []
      this.importPreviewData = []
      this.importErrors = []
      this.importingData = false
      if (this.$refs.fileUpload) {
        this.$refs.fileUpload.clearFiles()
      }
    },

    // 📝 默认作业分析汇总（未上传图片时显示）
    getDefaultAnalysisSummary() {
      return {
        averageDifficulty: '待分析',
        averageAccuracy: 0,
        commonErrors: [],
        weakKnowledgePoints: []
      }
    },

    // 📝 动态生成作业分析汇总
    generateDynamicAnalysisSummary() {
      const allQuestions = this.homeworkAnalysisResults.flatMap(result => result.questions)
      const totalQuestions = allQuestions.length
      
      if (totalQuestions === 0) {
        return this.getDefaultAnalysisSummary()
      }

      // 计算平均正确率
      const correctCount = allQuestions.filter(q => q.isCorrect).length
      const averageAccuracy = Math.round((correctCount / totalQuestions) * 100)

      // 计算平均难度
      const avgDifficultyValue = allQuestions.reduce((sum, q) => sum + q.difficulty, 0) / totalQuestions
      let averageDifficulty
      if (avgDifficultyValue <= 2) {
        averageDifficulty = '简单'
      } else if (avgDifficultyValue <= 3) {
        averageDifficulty = '中等'
      } else {
        averageDifficulty = '困难'
      }

      // 统计常见错误类型
      const commonErrors = this.calculateCommonErrors()

      // 统计薄弱知识点
      const weakKnowledgePoints = this.calculateWeakKnowledgePoints()

      return {
        averageDifficulty,
        averageAccuracy,
        commonErrors,
        weakKnowledgePoints
      }
    },

    // 🔍 计算常见错误类型
    calculateCommonErrors() {
      const errorStats = new Map()
      
      // 收集所有错误类型
      this.homeworkAnalysisResults.forEach(result => {
        if (result.errorTypes) {
          result.errorTypes.forEach(error => {
            if (errorStats.has(error.type)) {
              errorStats.set(error.type, errorStats.get(error.type) + error.count)
            } else {
              errorStats.set(error.type, error.count)
            }
          })
        }
      })

      const totalErrors = Array.from(errorStats.values()).reduce((sum, count) => sum + count, 0)

      if (totalErrors === 0) return []

      // 转换为分析结果格式
      return Array.from(errorStats.entries())
        .map(([type, count]) => ({
          type: type,
          frequency: Math.round((count / totalErrors) * 100),
          description: this.getErrorDescription(type),
          suggestion: this.getErrorSuggestion(type)
        }))
        .sort((a, b) => b.frequency - a.frequency)
        .slice(0, 5) // 只保留前5个最常见的错误
    },

    // 📊 计算薄弱知识点
    calculateWeakKnowledgePoints() {
      const knowledgeStats = new Map()
      
      // 收集所有知识点掌握情况
      this.homeworkAnalysisResults.forEach(result => {
        if (result.weakKnowledgePoints) {
          result.weakKnowledgePoints.forEach(point => {
            if (knowledgeStats.has(point.name)) {
              const existing = knowledgeStats.get(point.name)
              existing.totalQuestions += point.totalQuestions
              existing.correctCount += point.correctCount
            } else {
              knowledgeStats.set(point.name, {
                totalQuestions: point.totalQuestions,
                correctCount: point.correctCount
              })
            }
          })
        }
      })

      // 转换为分析结果格式
      return Array.from(knowledgeStats.entries())
        .map(([name, stats]) => ({
          name: name,
          mastery: Math.round((stats.correctCount / stats.totalQuestions) * 100),
          description: this.getKnowledgeDescription(name)
        }))
        .filter(point => point.mastery < 75) // 只显示掌握度低于75%的知识点
        .sort((a, b) => a.mastery - b.mastery) // 按掌握度从低到高排序
    },

    // 💡 默认二次函数学习建议（未上传图片时显示）
    getDefaultLearningRecommendations() {
      return [
        {
          type: 'foundation',
          title: '二次函数基础知识',
          icon: 'el-icon-collection',
          suggestions: [
            '上传学生作业图片，获取个性化二次函数学习建议',
            '掌握二次函数的三种基本形式：一般式、顶点式、交点式',
            '熟记顶点坐标公式和对称轴公式',
            '理解二次项系数a对图像开口方向的影响'
          ]
        },
        {
          type: 'practice',
          title: '二次函数练习方法',
          icon: 'el-icon-edit',
          suggestions: [
            '每日练习描点绘制抛物线图像，提高图像识别能力',
            '多做图像平移变换练习，掌握平移规律',
            '结合实际问题练习二次函数建模',
            '建立二次函数错题本，分类整理常见错误'
          ]
        },
        {
          type: 'method',
          title: '学习方法建议',
          icon: 'el-icon-star-on',
          suggestions: [
            '通过图像与解析式结合的方式理解函数性质',
            '利用对称性简化计算和绘图过程',
            '从实际问题中体会二次函数的应用价值'
          ]
        }
      ]
    },

    // 💡 动态生成学习建议
    generateDynamicLearningRecommendations() {
      const recommendations = []
      const analysisSummary = this.analysisSummary
      
      // 基于常见错误生成建议
      if (analysisSummary.commonErrors.length > 0) {
        const foundationSuggestions = []
        const practiceSuggestions = []
        
        analysisSummary.commonErrors.forEach(error => {
          if (error.frequency > 30) { // 高频错误
            if (error.type.includes('顶点') || error.type.includes('坐标')) {
              foundationSuggestions.push(`针对"${error.type}"，重点练习顶点坐标公式和配方法`)
              practiceSuggestions.push('每日练习5道顶点坐标计算题，建立熟练度')
            } else if (error.type.includes('对称轴')) {
              foundationSuggestions.push(`加强对称轴公式x=-b/(2a)的记忆和应用`)
              practiceSuggestions.push('通过图像结合公式，深化对称轴理解')
            } else if (error.type.includes('开口') || error.type.includes('方向')) {
              foundationSuggestions.push(`重点理解二次项系数a与开口方向的关系`)
              practiceSuggestions.push('多做开口方向判断练习，强化记忆')
            } else if (error.type.includes('平移') || error.type.includes('图像')) {
              foundationSuggestions.push(`掌握"左加右减，上加下减"的平移规律`)
              practiceSuggestions.push('通过图像对比练习，理解平移变换')
            } else if (error.type.includes('最值')) {
              foundationSuggestions.push(`结合顶点坐标和开口方向理解最值问题`)
              practiceSuggestions.push('多做实际应用中的最值问题')
            } else if (error.type.includes('转换') || error.type.includes('解析式')) {
              foundationSuggestions.push(`加强三种解析式形式间的转换练习`)
              practiceSuggestions.push('理解一般式、顶点式、交点式的特点和适用场景')
            }
          }
        })

        if (foundationSuggestions.length > 0) {
          recommendations.push({
            type: 'foundation',
            title: '错误纠正建议',
            icon: 'el-icon-warning',
            suggestions: foundationSuggestions.slice(0, 3) // 最多3条建议
          })
        }

        if (practiceSuggestions.length > 0) {
          recommendations.push({
            type: 'practice',
            title: '练习改进建议',
            icon: 'el-icon-edit',
            suggestions: practiceSuggestions.slice(0, 3)
          })
        }
      }

      // 基于薄弱知识点生成建议
      if (analysisSummary.weakKnowledgePoints.length > 0) {
        const knowledgeSuggestions = []
        
        analysisSummary.weakKnowledgePoints.forEach(point => {
          if (point.mastery < 60) { // 掌握度很低
            knowledgeSuggestions.push(`重点加强"${point.name}"的学习，建议寻求教师个别指导`)
          } else if (point.mastery < 75) { // 掌握度一般
            knowledgeSuggestions.push(`继续巩固"${point.name}"，多做相关练习题`)
          }
        })

        if (knowledgeSuggestions.length > 0) {
          recommendations.push({
            type: 'knowledge',
            title: '知识点强化建议',
            icon: 'el-icon-star-on',
            suggestions: knowledgeSuggestions.slice(0, 3)
          })
        }
      }

      // 基于整体表现生成方法建议
      const methodSuggestions = []
      
      if (analysisSummary.averageAccuracy < 60) {
        methodSuggestions.push('整体正确率偏低，建议放慢学习节奏，确保基础扎实')
        methodSuggestions.push('增加基础题目的练习量，逐步提升难度')
      } else if (analysisSummary.averageAccuracy > 85) {
        methodSuggestions.push('基础掌握良好，可以挑战更有难度的题目')
        methodSuggestions.push('尝试一题多解，培养发散性思维')
      }

      if (analysisSummary.averageDifficulty === '困难' && analysisSummary.averageAccuracy > 70) {
        methodSuggestions.push('能够处理较难题目，建议参加数学竞赛或拓展活动')
      }

      if (methodSuggestions.length > 0) {
        recommendations.push({
          type: 'method',
          title: '学习方法建议',
          icon: 'el-icon-light-rain',
          suggestions: methodSuggestions.slice(0, 3)
        })
      }

      // 如果没有生成任何建议，返回默认建议
      if (recommendations.length === 0) {
        return this.getDefaultLearningRecommendations()
      }

      return recommendations
    }
    },
  
  mounted() {
    // 从localStorage恢复数据
    const savedData = localStorage.getItem('studentProfiling')
    if (savedData) {
      const data = JSON.parse(savedData)
      if (data.classInfo) {
        this.currentClass = data.classInfo
        this.specialNeedsStudents = data.specialNeeds || []
      }
    }
  }
}
</script>

<style scoped>
.student-profiling {
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
  margin: 0 0 8px 0;
}

.title-section p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin: 0;
}

.step-indicator {
  min-width: 120px;
  text-align: center;
}

.step-badge {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 16px 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.step-number {
  font-size: 32px;
  font-weight: 900;
  color: white;
  display: block;
  line-height: 1;
}

.step-total {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 600;
}

/* 通用卡片样式 */
.section-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  overflow: hidden;
  margin-bottom: 24px;
  position: relative;
}

.section-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
}

.section-header {
  padding: 25px 30px 20px;
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.header-left i {
  font-size: 28px;
  color: #667eea;
  margin-top: 5px;
  flex-shrink: 0;
}

.header-text h3 {
  margin: 0 0 8px 0;
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.header-text p {
  margin: 0;
  font-size: 15px;
  color: #64748b;
  line-height: 1.6;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 班级选择器样式 */
.class-selector-section {
  margin-bottom: 24px;
}

.class-selector-container {
  padding: 30px;
}

.selector-form {
  margin-bottom: 24px;
}

.class-info {
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  padding-top: 24px;
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.info-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 255, 0.9) 100%);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.info-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.students-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.average-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.performance-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.special-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.card-content {
  flex: 1;
}

.card-number {
  font-size: 24px;
  font-weight: 900;
  color: #1e293b;
  line-height: 1;
  margin-bottom: 4px;
}

  .card-label {
    font-size: 14px;
    color: #64748b;
    font-weight: 500;
  }

  /* 学情配置完整性指示器 */
  .completeness-indicator {
    margin-top: 24px;
    padding: 20px;
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border: 1px solid rgba(14, 165, 233, 0.2);
    border-radius: 12px;
  }

  .indicator-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid rgba(14, 165, 233, 0.1);
  }

  .indicator-header h4 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .indicator-header i {
    color: #0ea5e9;
  }

  .completeness-score {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .score {
    font-size: 20px;
    font-weight: 900;
    color: #0f172a;
  }

  .status {
    font-size: 14px;
    font-weight: 600;
    padding: 4px 8px;
    border-radius: 6px;
  }

  .status.excellent {
    background: #dcfce7;
    color: #166534;
  }

  .status.good {
    background: #dbeafe;
    color: #1d4ed8;
  }

  .status.fair {
    background: #fef3c7;
    color: #92400e;
  }

  .status.poor {
    background: #fee2e2;
    color: #dc2626;
  }

  .indicator-items {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    margin-bottom: 16px;
  }

  .indicator-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    background: rgba(255, 255, 255, 0.7);
    border-radius: 8px;
    font-size: 14px;
    transition: all 0.3s ease;
  }

  .indicator-item.completed {
    background: rgba(34, 197, 94, 0.1);
    color: #166534;
  }

  .indicator-item.completed i {
    color: #22c55e;
  }

  .indicator-item:not(.completed) {
    color: #64748b;
  }

  .indicator-item:not(.completed) i {
    color: #ef4444;
  }

  .improvement-suggestions {
    background: rgba(255, 255, 255, 0.8);
    border-radius: 8px;
    padding: 16px;
  }

  .improvement-suggestions p {
    margin: 0 0 8px 0;
    font-weight: 600;
    color: #0f172a;
  }

  .improvement-suggestions ul {
    margin: 0;
    padding-left: 20px;
  }

  .improvement-suggestions li {
    color: #64748b;
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 4px;
  }

/* 仪表盘样式 */
.dashboard-section {
  margin-bottom: 24px;
}

.dashboard-container {
  padding: 30px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 16px;
  padding: 24px;
  height: 100%;
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
}

.chart-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-header i {
  color: #667eea;
}

.chart-container {
  height: 200px;
  margin-bottom: 16px;
}

.chart-legend {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  flex-shrink: 0;
}

.legend-label {
  color: #64748b;
  font-weight: 500;
}

/* 前测知识量表样式 */
.knowledge-gauge {
  text-align: center;
  margin-bottom: 20px;
}

.gauge-container {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.gauge-labels {
  display: flex;
  justify-content: space-between;
  position: absolute;
  top: 80px;
  left: 15px;
  right: 15px;
  font-size: 10px;
  color: #64748b;
  font-weight: 500;
}

.gauge-score {
  text-align: center;
}

.score-main {
  font-size: 28px;
  font-weight: 900;
  color: #1e293b;
  line-height: 1;
}

.score-unit {
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
  margin-left: 4px;
}

.score-desc {
  margin: 8px 0 0 0;
  font-size: 14px;
  color: #667eea;
  font-weight: 600;
}

/* 学习风格样式 */
.style-summary {
  display: grid;
  gap: 16px;
}

.summary-item {
  background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  padding: 12px;
}

.style-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.style-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.style-percentage {
  font-size: 13px;
  font-weight: 700;
  color: #667eea;
}

.style-bar {
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 8px;
}

.style-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.8s ease;
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

  /* 作业图片分析样式 */
  .homework-analysis-section {
    margin-bottom: 24px;
  }

  .homework-container {
    padding: 24px 30px;
  }

  .upload-section {
    margin-bottom: 20px;
  }

  .upload-header {
    margin-bottom: 16px;
  }

  .upload-header h4 {
    margin: 0 0 8px 0;
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .upload-description {
    margin: 0;
    font-size: 14px;
    color: #64748b;
    line-height: 1.5;
  }

  .homework-uploader {
    width: 100%;
  }

  /* 调整picture-card的大小 */
  .homework-uploader :deep(.el-upload-list--picture-card .el-upload-list__item) {
    width: 120px;
    height: 120px;
    margin: 0 8px 8px 0;
  }

  .homework-uploader :deep(.el-upload--picture-card) {
    width: 120px;
    height: 120px;
  }

  .homework-uploader :deep(.el-upload-list--picture-card .el-upload-list__item-thumbnail) {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  /* 紧凑型上传触发器 */
  .upload-trigger-compact {
    border: 2px dashed rgba(102, 126, 234, 0.3);
    border-radius: 8px;
    padding: 0;
    text-align: center;
    background: linear-gradient(135deg, rgba(248, 250, 255, 0.8) 0%, rgba(255, 255, 255, 0.9) 100%);
    transition: all 0.3s ease;
    cursor: pointer;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .upload-trigger-compact:hover {
    border-color: #667eea;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(255, 255, 255, 0.95) 100%);
  }

  .upload-trigger-compact i {
    font-size: 24px;
    color: #667eea;
    margin-bottom: 6px;
    display: block;
  }

  .upload-text-compact {
    font-size: 12px;
    color: #1e293b;
    font-weight: 600;
    line-height: 1.3;
  }

  /* 传统上传触发器（保留备用） */
  .upload-trigger {
    border: 2px dashed rgba(102, 126, 234, 0.3);
    border-radius: 12px;
    padding: 40px 20px;
    text-align: center;
    background: linear-gradient(135deg, rgba(248, 250, 255, 0.8) 0%, rgba(255, 255, 255, 0.9) 100%);
    transition: all 0.3s ease;
    cursor: pointer;
  }

  .upload-trigger:hover {
    border-color: #667eea;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(255, 255, 255, 0.95) 100%);
  }

  .upload-trigger i {
    font-size: 48px;
    color: #667eea;
    margin-bottom: 16px;
    display: block;
  }

  .upload-text {
    font-size: 16px;
    color: #1e293b;
    font-weight: 600;
    margin-bottom: 8px;
  }

  .upload-hint {
    font-size: 14px;
    color: #64748b;
  }

  .analysis-results {
    border-top: 1px solid rgba(102, 126, 234, 0.1);
    padding-top: 20px;
    margin-top: 20px;
  }

  .results-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  .results-header h4 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #1e293b;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .analysis-summary {
    margin-bottom: 24px;
  }

  .summary-card {
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 255, 0.9) 100%);
    border: 1px solid rgba(102, 126, 234, 0.1);
    border-radius: 12px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s ease;
  }

  .summary-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
  }

  .summary-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    color: white;
  }

  .difficulty-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
  .accuracy-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
  .error-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
  .knowledge-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }

  .summary-content {
    flex: 1;
  }

  .summary-number {
    font-size: 24px;
    font-weight: 900;
    color: #1e293b;
    line-height: 1;
    margin-bottom: 4px;
  }

  .summary-label {
    font-size: 14px;
    color: #64748b;
    font-weight: 500;
  }

  .detailed-analysis {
    background: rgba(255, 255, 255, 0.95);
    border: 1px solid rgba(102, 126, 234, 0.1);
    border-radius: 12px;
    padding: 24px;
  }

  .analysis-tabs {
    background: transparent;
  }

  .error-analysis,
  .knowledge-analysis,
  .learning-suggestions {
    margin-top: 16px;
  }

  .error-item {
    background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
    border: 1px solid rgba(102, 126, 234, 0.1);
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 16px;
  }

  .error-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
  }

  .error-type {
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
  }

  .error-frequency {
    font-size: 14px;
    color: #667eea;
    font-weight: 600;
  }

  .error-description {
    color: #64748b;
    margin-bottom: 12px;
    line-height: 1.6;
  }

  .error-suggestions {
    display: flex;
    gap: 8px;
    align-items: flex-start;
  }

  .suggestion-label {
    font-weight: 600;
    color: #1e293b;
    white-space: nowrap;
  }

  .suggestion-text {
    color: #667eea;
    line-height: 1.6;
  }

  .knowledge-item {
    background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
    border: 1px solid rgba(102, 126, 234, 0.1);
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 16px;
  }

  .knowledge-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }

  .knowledge-name {
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
  }

  .knowledge-details {
    display: flex;
    gap: 8px;
    align-items: flex-start;
  }

  .detail-label {
    font-weight: 600;
    color: #1e293b;
    white-space: nowrap;
  }

  .detail-text {
    color: #64748b;
    line-height: 1.6;
  }

  .suggestion-category {
    margin-bottom: 24px;
  }

  .category-title {
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .category-title i {
    color: #667eea;
  }

  .suggestion-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .suggestion-list li {
    padding: 8px 0;
    padding-left: 20px;
    position: relative;
    color: #64748b;
    line-height: 1.6;
  }

  .suggestion-list li::before {
    content: "•";
    color: #667eea;
    position: absolute;
    left: 0;
    font-weight: bold;
  }

  /* 批量导入样式 */
  .batch-import-section {
    margin-bottom: 24px;
  }

  .import-container {
    padding: 30px;
  }

  .import-steps {
    margin-bottom: 32px;
    padding: 24px;
    background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
    border-radius: 12px;
    border: 1px solid rgba(102, 126, 234, 0.1);
  }

  .file-upload-section {
    margin-bottom: 24px;
  }

  .file-uploader {
    width: 100%;
  }

  /* Element UI 拖拽上传器样式覆盖 */
  .file-uploader :deep(.el-upload-dragger) {
    width: 100% !important;
    height: auto !important;
    min-height: 180px;
    border: 2px dashed rgba(102, 126, 234, 0.3) !important;
    border-radius: 12px !important;
    background: linear-gradient(135deg, rgba(248, 250, 255, 0.8) 0%, rgba(255, 255, 255, 0.9) 100%) !important;
    transition: all 0.3s ease !important;
  }

  .file-uploader :deep(.el-upload-dragger:hover) {
    border-color: #667eea !important;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(255, 255, 255, 0.95) 100%) !important;
  }

  .file-uploader :deep(.el-upload-dragger.is-dragover) {
    border-color: #667eea !important;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(255, 255, 255, 0.95) 100%) !important;
  }

  /* 覆盖Element UI默认的上传图标样式 */
  .file-uploader :deep(.el-icon-upload) {
    font-size: 64px !important;
    color: #667eea !important;
    margin: 30px 0 16px !important;
    line-height: 1 !important;
    display: block !important;
  }

  /* 覆盖Element UI默认的上传文本样式 */
  .file-uploader :deep(.el-upload__text) {
    font-size: 18px !important;
    color: #1e293b !important;
    font-weight: 600 !important;
    margin-bottom: 12px !important;
    line-height: 1.4 !important;
  }

  .file-uploader :deep(.el-upload__text em) {
    color: #667eea !important;
    font-style: normal !important;
    text-decoration: underline !important;
  }

  /* 覆盖Element UI默认的tip样式 */
  .file-uploader :deep(.el-upload__tip) {
    font-size: 14px !important;
    color: #64748b !important;
    margin-top: 8px !important;
    margin-bottom: 20px !important;
    line-height: 1.4 !important;
  }

  .data-preview {
    margin-bottom: 24px;
  }

  .preview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  }

  .preview-header h4 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .preview-stats {
    display: flex;
    gap: 20px;
  }

  .stat-item {
    font-size: 14px;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .stat-item.valid {
    color: #67C23A;
  }

  .stat-item.invalid {
    color: #F56C6C;
  }

  .preview-table {
    margin-bottom: 12px;
  }

  .preview-more {
    text-align: center;
    color: #64748b;
    font-size: 14px;
    padding: 12px;
    background: #f8f9ff;
    border-radius: 8px;
  }

  .import-errors {
    margin-bottom: 24px;
    background: linear-gradient(135deg, #fef2f2 0%, #ffffff 100%);
    border: 1px solid rgba(245, 108, 108, 0.2);
    border-radius: 12px;
    padding: 20px;
  }

  .errors-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .errors-header h4 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #F56C6C;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .error-list {
    display: grid;
    gap: 8px;
  }

  .import-errors .error-item {
    background: rgba(245, 108, 108, 0.05);
    border: 1px solid rgba(245, 108, 108, 0.1);
    border-radius: 6px;
    padding: 12px;
    display: flex;
    gap: 12px;
    margin-bottom: 0;
  }

  .error-row {
    font-weight: 600;
    color: #F56C6C;
    white-space: nowrap;
  }

  .error-message {
    color: #64748b;
  }

  .more-errors {
    text-align: center;
    color: #F56C6C;
    font-size: 14px;
    padding: 8px;
  }

  .import-actions {
    text-align: center;
    padding: 24px;
    background: linear-gradient(135deg, rgba(248, 250, 255, 0.8) 0%, rgba(255, 255, 255, 0.9) 100%);
    border-radius: 12px;
    border: 1px solid rgba(102, 126, 234, 0.1);
  }

  .import-actions .el-button {
    margin: 0 8px;
    min-width: 140px;
    height: 40px;
    border-radius: 8px;
    font-weight: 600;
  }

/* 响应式设计 */
@media (max-width: 1200px) {
  .student-profiling {
    max-width: 100%;
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .info-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .dashboard-container {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 24px 16px;
  }
  
  .title-section h2 {
    font-size: 24px;
  }
  
  .section-header {
    padding: 20px;
  }
  
  .header-left {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .info-cards {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .dashboard-container {
    padding: 15px;
  }
  
  .chart-legend {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    padding: 24px 16px;
  }
  
  .action-buttons .el-button {
    margin: 8px;
    min-width: 120px;
  }
  
  .homework-container,
  .import-container {
    padding: 16px;
  }
  
  .analysis-summary {
    margin-bottom: 20px;
  }
  
  .summary-card {
    padding: 16px;
    gap: 12px;
  }
  
  .summary-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .summary-number {
    font-size: 20px;
  }
  
  .upload-trigger {
    padding: 30px 16px;
  }
  
  .upload-trigger i {
    font-size: 36px;
  }
  
  /* 移动端文件上传器样式调整 */
  .file-uploader :deep(.el-upload-dragger) {
    min-height: 150px !important;
    padding: 20px 16px !important;
  }
  
  .file-uploader :deep(.el-icon-upload) {
    font-size: 48px !important;
    margin: 20px 0 12px !important;
  }
  
  .file-uploader :deep(.el-upload__text) {
    font-size: 16px !important;
    margin-bottom: 8px !important;
  }
  
  .file-uploader :deep(.el-upload__tip) {
    font-size: 12px !important;
    margin-bottom: 15px !important;
  }
  
  .preview-stats {
    flex-direction: column;
    gap: 8px;
  }
  
  .import-actions .el-button {
    margin: 4px;
    min-width: 100px;
    font-size: 14px;
  }
}
</style> 