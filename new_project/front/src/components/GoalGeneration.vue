<template>
  <div class="objective-builder">
    <!-- 页面头部 - 优化样式 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2>
            <i class="el-icon-trophy"></i>
            三维目标设定
          </h2>
      <p>基于布卢姆认知层级和课程标准，制定完整的教学目标体系</p>
        </div>
        <div class="step-indicator">
          <div class="step-badge">
            <span class="step-number">2</span>
            <span class="step-total">/6</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 智能导入区域 - 新增 -->
    <div class="import-section">
      <div class="section-card">
        <div class="section-header">
          <div class="header-left">
            <i class="el-icon-upload2"></i>
            <div class="header-text">
              <h3>智能导入目标</h3>
              <p>通过多种智能化方式快速导入和生成教学目标，提升工作效率</p>
            </div>
          </div>
          <div class="header-right">
            <el-tag v-if="importStatus" :type="importStatus.type" effect="dark">
              {{ importStatus.text }}
            </el-tag>
          </div>
        </div>
        
        <div class="import-methods-container">
          <!-- 文档导入 -->
          <div class="import-card modern-card document-import">
            <div class="card-header-modern">
              <div class="icon-section">
                <div class="icon-wrapper document-icon">
                  <i class="el-icon-document"></i>
                </div>
                <div class="header-content">
                  <h4>文档导入</h4>
                  <p>支持Word/PDF文档智能解析</p>
                </div>
              </div>
              <div class="card-badge">
                <span>AI解析</span>
              </div>
            </div>
            
            <div class="card-body-modern">
              <div class="upload-zone" @click="triggerDocumentUpload">
                <div class="upload-icon-area">
                  <i class="el-icon-upload2"></i>
                </div>
                <div class="upload-text">
                  <p class="primary-text">拖拽文档到此处或点击上传</p>
                  <span class="support-text">支持 .docx, .pdf 格式，最大10MB</span>
                </div>
              </div>
              
              <input 
                ref="documentUpload" 
                type="file" 
                accept=".docx,.pdf" 
                @change="handleDocumentUpload"
                style="display: none"
              />
              
              <div v-if="documentUploadProgress" class="progress-section">
                <div class="progress-header">
                  <span>{{ documentUploadStatus }}</span>
                  <span>{{ documentUploadProgress }}%</span>
                </div>
                <el-progress 
                  :percentage="documentUploadProgress" 
                  :stroke-width="8"
                  status="success"
                  :show-text="false"
                />
              </div>
            </div>
            
            <div class="card-footer-modern">
              <el-button 
                class="action-button"
                type="primary" 
                @click="triggerDocumentUpload"
                :loading="documentProcessing"
              >
                <i class="el-icon-folder-opened"></i>
                选择文档
              </el-button>
            </div>
          </div>

          <!-- 语音识别 -->
          <div class="import-card modern-card voice-import">
            <div class="card-header-modern">
              <div class="icon-section">
                <div class="icon-wrapper voice-icon">
                  <i class="el-icon-microphone"></i>
                </div>
                <div class="header-content">
                  <h4>语音描述</h4>
                  <p>语音转文字智能分类目标</p>
                </div>
              </div>
              <div class="card-badge">
                <span>语音识别</span>
              </div>
            </div>
            
            <div class="card-body-modern">
              <div class="voice-section">
                <div class="record-area">
                  <button 
                    :class="['record-btn-modern', { 'recording': isRecording }]"
                    @click="toggleRecording"
                    :disabled="voiceProcessing"
                  >
                    <div class="record-pulse" v-if="isRecording"></div>
                    <i :class="isRecording ? 'el-icon-video-pause' : 'el-icon-microphone'"></i>
                  </button>
                  <div class="record-info">
                    <p class="status-text">
                      {{ isRecording ? '正在录音中...' : '点击开始录音描述目标' }}
                    </p>
                    <div v-if="recordingTime > 0" class="time-display">
                      <i class="el-icon-time"></i>
                      {{ formatTime(recordingTime) }}
                    </div>
                  </div>
                </div>
                
                <div v-if="voiceText" class="voice-result">
                  <div class="result-header">
                    <h6><i class="el-icon-chat-dot-round"></i> 识别内容</h6>
                  </div>
                  <div class="voice-content">{{ voiceText }}</div>
                  <div class="result-actions">
                    <el-button 
                      type="primary"
                      size="mini" 
                      @click="processVoiceTargets"
                      :loading="voiceProcessing"
                    >
                      <i class="el-icon-magic-stick"></i> 智能分类
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="card-footer-modern">
              <el-button 
                class="action-button"
                type="primary" 
                @click="toggleRecording"
                :loading="voiceProcessing"
                :disabled="isRecording"
              >
                <i class="el-icon-microphone"></i>
                {{ isRecording ? '停止录音' : '开始录音' }}
              </el-button>
            </div>
          </div>

          <!-- 图片OCR -->
          <div class="import-card modern-card image-import">
            <div class="card-header-modern">
              <div class="icon-section">
                <div class="icon-wrapper image-icon">
                  <i class="el-icon-camera"></i>
                </div>
                <div class="header-content">
                  <h4>图片识别</h4>
                  <p>OCR文字识别智能提取</p>
                </div>
              </div>
              <div class="card-badge">
                <span>OCR识别</span>
              </div>
            </div>
            
            <div class="card-body-modern">
              <div class="image-section">
                <div class="upload-options-modern">
                  <div class="option-card" @click="triggerImageUpload">
                    <i class="el-icon-picture-outline"></i>
                    <span>选择图片</span>
                  </div>
                  <div class="option-card" @click="openCamera">
                    <i class="el-icon-camera-solid"></i>
                    <span>拍照上传</span>
                  </div>
                </div>
                
                <input 
                  ref="imageUpload" 
                  type="file" 
                  accept="image/*" 
                  @change="handleImageUpload"
                  style="display: none"
                />
                
                <div v-if="uploadedImage" class="image-preview-modern">
                  <div class="preview-container">
                    <img :src="uploadedImage" alt="预览图片" />
                    <div class="image-overlay">
                      <el-button 
                        circle
                        type="primary" 
                        size="mini" 
                        @click="processImageOCR"
                        :loading="ocrProcessing"
                      >
                        <i class="el-icon-view"></i>
                      </el-button>
                      <el-button 
                        circle
                        type="danger" 
                        size="mini" 
                        @click="clearImage"
                      >
                        <i class="el-icon-delete"></i>
                      </el-button>
                    </div>
                  </div>
                </div>
                
                <div v-if="ocrResult" class="ocr-result-modern">
                  <div class="result-header">
                    <h6><i class="el-icon-view"></i> 识别结果</h6>
                  </div>
                  <div class="ocr-content">{{ ocrResult }}</div>
                  <div class="result-actions">
                    <el-button 
                      type="primary"
                      size="mini" 
                      @click="processOCRTargets"
                      :loading="ocrProcessing"
                    >
                      <i class="el-icon-magic-stick"></i> 智能分类
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="card-footer-modern">
              <el-button 
                class="action-button"
                type="primary" 
                @click="triggerImageUpload"
                :loading="ocrProcessing"
              >
                <i class="el-icon-picture"></i>
                上传图片
              </el-button>
            </div>
          </div>


        </div>
        
        <!-- 导入历史记录 -->
        <div v-if="importHistory.length > 0" class="import-history">
          <div class="history-header">
            <h4>
              <i class="el-icon-time"></i>
              最近导入记录
            </h4>
            <div class="history-actions-header">
              <el-button 
                type="text" 
                size="mini" 
                class="clear-all-btn"
                @click="clearAllHistory"
              >
                <i class="el-icon-delete"></i> 清空所有
              </el-button>
            </div>
          </div>
          <div class="history-list">
            <div 
              v-for="(record, index) in importHistory" 
              :key="index"
              class="history-item"
              @click="applyHistoryRecord(record)"
            >
              <div class="history-icon">
                <i :class="getHistoryIcon(record.type)"></i>
              </div>
              <div class="history-info">
                <h6>{{ record.name }}</h6>
                <p>{{ record.timestamp }} · {{ getTypeText(record.type) }}</p>
              </div>
              <div class="history-actions">
                <el-button type="text" size="mini" @click.stop="applyHistoryRecord(record)">
                  <i class="el-icon-refresh-left"></i> 应用
                </el-button>
                <el-button 
                  type="text" 
                  size="mini" 
                  class="delete-btn"
                  @click.stop="deleteHistoryRecord(index)"
                >
                  <i class="el-icon-delete"></i> 删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 布卢姆认知层级选择器 - 优化布局 -->
    <div class="bloom-section">
      <div class="section-card">
      <div class="section-header">
          <div class="header-left">
          <i class="el-icon-trophy"></i>
          <div class="header-text">
            <h3>布卢姆认知层级选择</h3>
            <p>选择目标的认知复杂度层级，指导教学目标的制定</p>
          </div>
        </div>
      </div>
      
      <div class="bloom-selector-container">
        <div class="bloom-pyramid-compact">
          <div 
            v-for="(level, index) in bloomLevels" 
            :key="level.name"
            :class="['pyramid-level-compact', `level-${index}`, { active: selectedBloomLevel === level.name }]"
            @click="selectBloomLevel(level.name)"
          >
            <div class="level-content-compact">
              <div class="level-header">
                <h5>{{ level.name }}</h5>
                <span class="level-number">{{ 6 - index }}</span>
              </div>
              <p class="level-desc">{{ level.description }}</p>
            </div>
          </div>
        </div>
        
        <!-- 右侧关键词展示 -->
        <div class="bloom-keywords-panel">
          <h4>
            <i class="el-icon-star-on"></i>
            认知行为关键词
          </h4>
          
          <!-- 当前层级信息 -->
          <div class="current-level-info">
            <div class="level-badge">
              <span class="badge-number">{{ getCurrentLevelNumber() }}</span>
              <span class="badge-name">{{ getCurrentLevelName() }}</span>
            </div>
          </div>
          
          <!-- 认知关键词 -->
          <div class="keywords-section">
            <h5>认知行为动词</h5>
            <div class="current-keywords">
              <el-tag 
                v-for="keyword in currentLevelKeywords" 
                :key="keyword" 
                class="keyword-tag"
                effect="dark"
              >
                {{ keyword }}
              </el-tag>
            </div>
          </div>
          
          <!-- 层级说明 -->
          <div class="level-explanation">
            <h5>层级特征</h5>
            <p>{{ currentLevelExplanation }}</p>
          </div>
          
          <!-- 教学应用建议 -->
          <div class="teaching-suggestions">
            <h5>教学应用建议</h5>
            <ul class="suggestion-list">
              <li v-for="suggestion in getCurrentLevelSuggestions()" :key="suggestion">
                {{ suggestion }}
              </li>
            </ul>
          </div>
          
          <!-- 评价指标 -->
          <div class="assessment-indicators">
            <h5>评价指标</h5>
            <div class="indicators">
              <div 
                v-for="indicator in getCurrentLevelIndicators()" 
                :key="indicator.name"
                class="indicator-item"
              >
                <span class="indicator-name">{{ indicator.name }}</span>
                <div class="indicator-bar">
                  <div 
                    class="indicator-fill" 
                    :style="{ width: indicator.level + '%' }"
                  ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 三维目标编辑器 - 优化布局 -->
    <div class="targets-section">
      <div class="section-card">
      <div class="section-header">
          <div class="header-left">
          <i class="el-icon-edit"></i>
          <div class="header-text">
            <h3>三维目标设定</h3>
            <p>基于新课程标准，全面设计知识与技能、过程与方法、情感态度价值观三个维度的教学目标</p>
          </div>
        </div>
      </div>
      
            <div class="dimension-editors-horizontal">
        <!-- 知识与技能目标 -->
        <div class="target-card-horizontal knowledge-card">
          <div class="card-header">
            <div class="header-left">
              <i class="el-icon-notebook-1"></i>
              <span>知识与技能</span>
            </div>
            <el-button type="text" size="small" @click="generateKnowledgeTargets" class="ai-assist">
              <i class="el-icon-magic-stick"></i> AI生成
            </el-button>
          </div>
          <div class="card-content">
            <div class="form-section">
              <el-form size="small">
                <el-form-item label="目标描述">
                  <el-input
                    type="textarea"
                    :rows="3"
                    placeholder="请描述学生在知识和技能方面应达到的具体目标..."
                    v-model="targets.knowledge.description"
                  />
                </el-form-item>
                <el-form-item label="评价标准">
                  <el-select v-model="targets.knowledge.criteria" placeholder="选择评价标准" style="width: 100%">
                    <el-option label="了解/知道" value="understand" />
                    <el-option label="理解/掌握" value="master" />
                    <el-option label="运用/应用" value="apply" />
                    <el-option label="分析/解决" value="analyze" />
                  </el-select>
                </el-form-item>
              </el-form>
            </div>
            <div class="examples-section">
              <h6>参考示例</h6>
              <div class="example-tags-compact">
                <el-tag 
                  v-for="example in knowledgeExamples" 
                  :key="example"
                  @click="insertExample('knowledge', example)"
                  class="clickable-tag-small"
                  size="mini"
                >
                  {{ example }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 过程与方法目标 -->
        <div class="target-card-horizontal process-card">
          <div class="card-header">
            <div class="header-left">
              <i class="el-icon-setting"></i>
              <span>过程与方法</span>
            </div>
            <el-button type="text" size="small" @click="generateProcessTargets" class="ai-assist">
              <i class="el-icon-magic-stick"></i> AI生成
            </el-button>
          </div>
          <div class="card-content">
            <div class="form-section">
              <el-form size="small">
                <el-form-item label="目标描述">
                  <el-input
                    type="textarea"
                    :rows="3"
                    placeholder="请描述学生在学习过程和方法方面应达到的目标..."
                    v-model="targets.process.description"
                  />
                </el-form-item>
                <el-form-item label="方法类型">
                  <el-checkbox-group v-model="targets.process.methods" size="mini">
                    <el-checkbox label="观察探究">观察探究</el-checkbox>
                    <el-checkbox label="合作学习">合作学习</el-checkbox>
                    <el-checkbox label="问题解决">问题解决</el-checkbox>
                    <el-checkbox label="实验操作">实验操作</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
              </el-form>
            </div>
            <div class="examples-section">
              <h6>参考示例</h6>
              <div class="example-tags-compact">
                <el-tag 
                  v-for="example in processExamples" 
                  :key="example"
                  @click="insertExample('process', example)"
                  class="clickable-tag-small"
                  size="mini"
                >
                  {{ example }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 情感态度价值观目标 -->
        <div class="target-card-horizontal attitude-card">
          <div class="card-header">
            <div class="header-left">
              <i class="el-icon-star-on"></i>
              <span>情感态度价值观</span>
            </div>
            <el-button type="text" size="small" @click="generateAttitudeTargets" class="ai-assist">
              <i class="el-icon-magic-stick"></i> AI生成
            </el-button>
          </div>
          <div class="card-content">
            <div class="form-section">
              <el-form size="small">
                <el-form-item label="目标描述">
                  <el-input
                    type="textarea"
                    :rows="3"
                    placeholder="请描述学生在情感态度和价值观方面应达到的目标..."
                    v-model="targets.attitude.description"
                  />
                </el-form-item>
                <el-form-item label="情感倾向">
                  <el-rate v-model="targets.attitude.intensity" :max="5" show-text />
                </el-form-item>
              </el-form>
            </div>
            <div class="examples-section">
              <h6>参考示例</h6>
              <div class="example-tags-compact">
                <el-tag 
                  v-for="example in attitudeExamples" 
                  :key="example"
                  @click="insertExample('attitude', example)"
                  class="clickable-tag-small"
                  size="mini"
                >
                  {{ example }}
                </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 智能分析与评估 - 全面优化 -->
    <div class="analysis-section">
      <div class="section-card">
      <div class="section-header">
          <div class="header-left">
          <i class="el-icon-data-analysis"></i>
          <div class="header-text">
            <h3>智能分析与评估</h3>
            <p>基于AI算法的多维度目标质量分析，提供科学的改进建议</p>
          </div>
          </div>
          <div class="header-right">
            <el-tag v-if="analysisStatus" :type="analysisStatus.type" effect="dark">
              {{ analysisStatus.text }}
            </el-tag>
        </div>
      </div>
      
      <!-- 分析卡片 -->
      <div class="analysis-cards-grid">
        <!-- 课程标准一致性检测 -->
        <div class="analysis-card compatibility-card">
          <div class="card-header">
            <div class="header-info">
              <i class="el-icon-success"></i>
              <div>
                <h4>课程标准一致性</h4>
                <p>检测目标与国家课程标准的匹配度</p>
              </div>
            </div>
            <el-button 
              type="primary" 
              size="mini" 
              @click="runCompatibilityCheck"
              :loading="compatibilityLoading"
            >
              {{ compatibilityLoading ? '检测中' : '重新检测' }}
            </el-button>
          </div>
          
          <div class="card-content">
            <div class="standard-selector">
              <el-select 
                v-model="selectedStandard" 
                placeholder="选择课程标准" 
                style="width: 100%"
                @change="onStandardChange"
              >
                <el-option-group label="数学课程标准">
                  <el-option 
                    label="义务教育数学课程标准(2022版)" 
                    value="math2022" 
                  />
                  <el-option 
                    label="普通高中数学课程标准(2017版)" 
                    value="highmath2017" 
                  />
                </el-option-group>
                <el-option-group label="其他学科标准">
                  <el-option 
                    label="义务教育物理课程标准(2022版)" 
                    value="physics2022" 
                  />
                  <el-option 
                    label="义务教育语文课程标准(2022版)" 
                    value="chinese2022" 
                  />
                </el-option-group>
              </el-select>
            </div>
            
            <div class="compatibility-results" v-if="compatibilityResults">
              <div class="overall-score">
                <div class="score-circle">
                  <svg width="80" height="80" viewBox="0 0 80 80">
                    <circle cx="40" cy="40" r="35" fill="none" stroke="#f0f0f0" stroke-width="6"/>
                    <circle 
                      cx="40" cy="40" r="35" 
                      fill="none" 
                      :stroke="getOverallScoreColor()" 
                      stroke-width="6"
                      stroke-linecap="round"
                      :stroke-dasharray="220"
                      :stroke-dashoffset="220 - (220 * overallCompatibilityScore / 100)"
                      transform="rotate(-90 40 40)"
                    />
                  </svg>
                  <div class="score-text">
                    <span class="score-number">{{ overallCompatibilityScore }}</span>
                    <span class="score-unit">%</span>
                  </div>
                </div>
                <div class="score-label">
                  <h5>综合匹配度</h5>
                  <p>{{ getCompatibilityLevel(overallCompatibilityScore) }}</p>
                </div>
              </div>
              
              <div class="detailed-results">
                <div class="result-item" v-for="result in compatibilityResults" :key="result.dimension">
                  <div class="result-header">
                    <span class="dimension-name">{{ result.dimension }}</span>
                    <div class="score-badge" :class="getScoreBadgeClass(result.score)">
                      {{ result.score }}%
                    </div>
                  </div>
                  <div class="progress-container">
                    <el-progress 
                      :percentage="result.score" 
                      :color="getProgressColor(result.score)"
                      :show-text="false"
                      stroke-width="6"
                    />
                  </div>
                  <p class="result-suggestion">
                    <i class="el-icon-lightbulb"></i>
                    {{ result.suggestion }}
                  </p>
                </div>
              </div>
            </div>
            
            <div v-else class="no-results">
              <i class="el-icon-warning-outline"></i>
              <p>请选择课程标准并完善三维目标后进行检测</p>
            </div>
          </div>
        </div>

        <!-- 目标复杂度评估 -->
        <div class="analysis-card complexity-card">
          <div class="card-header">
            <div class="header-info">
              <i class="el-icon-pie-chart"></i>
              <div>
                <h4>目标复杂度评估</h4>
                <p>分析目标设定的认知难度和挑战性</p>
              </div>
            </div>
            <el-dropdown @command="exportComplexityReport">
              <el-button type="text" size="mini">
                导出报告<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="pdf">PDF报告</el-dropdown-item>
                <el-dropdown-item command="excel">Excel数据</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          
          <div class="card-content">
            <div class="complexity-overview">
              <div class="complexity-gauge">
                <div class="gauge-container">
                  <svg width="160" height="100" viewBox="0 0 160 100">
                    <defs>
                      <linearGradient id="complexityGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" style="stop-color:#67C23A;stop-opacity:1" />
                        <stop offset="30%" style="stop-color:#E6A23C;stop-opacity:1" />
                        <stop offset="70%" style="stop-color:#F56C6C;stop-opacity:1" />
                        <stop offset="100%" style="stop-color:#F56C6C;stop-opacity:1" />
                      </linearGradient>
                    </defs>
                    <path d="M 20 80 A 60 60 0 0 1 140 80" stroke="url(#complexityGradient)" stroke-width="12" fill="none"/>
                    <line 
                      :x1="gaugeNeedleX" 
                      :y1="gaugeNeedleY" 
                      x2="80" 
                      y2="80" 
                      stroke="#2c3e50" 
                      stroke-width="3"
                      stroke-linecap="round"
                    />
                    <circle cx="80" cy="80" r="6" fill="#2c3e50"/>
                  </svg>
                  <div class="gauge-labels">
                    <span class="label-left">简单</span>
                    <span class="label-right">复杂</span>
                  </div>
                </div>
                <div class="complexity-score">
                  <span class="score-main">{{ complexityScore }}</span>
                  <span class="score-total">/100</span>
                  <p class="complexity-level">{{ getComplexityLevel(complexityScore) }}</p>
                </div>
              </div>
            </div>
            
            <div class="complexity-dimensions">
              <h5>维度分析</h5>
              <div class="dimension-grid">
                <div class="dimension-item">
                  <div class="dimension-icon bloom-icon">
                    <i class="el-icon-trophy"></i>
                  </div>
                  <div class="dimension-info">
                    <h6>认知层级</h6>
                    <div class="dimension-score">
                      <span>{{ bloomComplexity }}%</span>
                      <div class="mini-progress">
                        <div 
                          class="progress-fill" 
                            :style="{ width: bloomComplexity + '%', background: '#667eea' }"
                        ></div>
                      </div>
                    </div>
                    <p>{{ getBloomLevelText() }}</p>
                  </div>
                </div>
                
                <div class="dimension-item">
                  <div class="dimension-icon quantity-icon">
                    <i class="el-icon-collection"></i>
                  </div>
                  <div class="dimension-info">
                    <h6>目标数量</h6>
                    <div class="dimension-score">
                      <span>{{ quantityComplexity }}%</span>
                      <div class="mini-progress">
                        <div 
                          class="progress-fill" 
                          :style="{ width: quantityComplexity + '%', background: '#67C23A' }"
                        ></div>
                      </div>
                    </div>
                    <p>{{ getQuantityText() }}</p>
                  </div>
                </div>
                
                <div class="dimension-item">
                  <div class="dimension-icon depth-icon">
                    <i class="el-icon-document"></i>
                  </div>
                  <div class="dimension-info">
                    <h6>内容深度</h6>
                    <div class="dimension-score">
                      <span>{{ depthComplexity }}%</span>
                      <div class="mini-progress">
                        <div 
                          class="progress-fill" 
                          :style="{ width: depthComplexity + '%', background: '#E6A23C' }"
                        ></div>
                      </div>
                    </div>
                    <p>{{ getDepthText() }}</p>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="complexity-suggestions">
              <h5>优化建议</h5>
              <div class="suggestion-cards">
                <div 
                  v-for="suggestion in getComplexitySuggestions()" 
                  :key="suggestion.type"
                  class="suggestion-card"
                  :class="suggestion.type"
                >
                  <i :class="suggestion.icon"></i>
                  <p>{{ suggestion.text }}</p>
                  </div>
                </div>
              </div>
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
      <el-button size="large" @click="previewTargets">
        <i class="el-icon-view"></i> 预览目标
      </el-button>
      <el-button size="large" @click="exportTargets">
        <i class="el-icon-download"></i> 导出Word
      </el-button>
      <el-button size="large" @click="saveAndNext" type="primary">
        <i class="el-icon-right"></i> 保存并继续
      </el-button>
    </div>

    <!-- 预览对话框 -->
    <el-dialog title="教学目标预览" :visible.sync="previewVisible" width="70%">
      <div class="preview-content">
        <h3>教学目标</h3>
        <div class="preview-section">
          <h4>知识与技能目标</h4>
          <p>{{ targets.knowledge.description || '暂未设定' }}</p>
        </div>
        <div class="preview-section">
          <h4>过程与方法目标</h4>
          <p>{{ targets.process.description || '暂未设定' }}</p>
        </div>
        <div class="preview-section">
          <h4>情感态度价值观目标</h4>
          <p>{{ targets.attitude.description || '暂未设定' }}</p>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button type="primary" @click="confirmTargets">确认目标</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'GoalGeneration',
  data() {
    return {
      selectedBloomLevel: 'remember',
      selectedStandard: '',
      previewVisible: false,
      saveTimer: null, // 防抖计时器
      compatibilityLoading: false,
      analysisStatus: null,
      
      // 智能导入相关状态
      importStatus: null,
      
      // 文档导入
      documentProcessing: false,
      documentUploadProgress: 0,
      documentUploadStatus: '',
      
      // 语音识别
      isRecording: false,
      voiceProcessing: false,
      recordingTime: 0,
      voiceText: '',
      mediaRecorder: null,
      recordingInterval: null,
      
      // 图片OCR
      ocrProcessing: false,
      uploadedImage: null,
      ocrResult: '',
      

      
      // 导入历史
      importHistory: [],
      
      // 布卢姆认知层级
      bloomLevels: [
        {
          name: 'create',
          description: '创造：产生新的想法或作品',
          keywords: ['设计', '创作', '构建', '发明', '制作']
        },
        {
          name: 'evaluate', 
          description: '评价：根据标准做出判断',
          keywords: ['评估', '判断', '批评', '推荐', '证明']
        },
        {
          name: 'analyze',
          description: '分析：分解材料找出关系',
          keywords: ['分析', '比较', '分类', '区分', '推断']
        },
        {
          name: 'apply',
          description: '运用：在新情境中使用知识',
          keywords: ['应用', '实施', '操作', '解决', '使用']
        },
        {
          name: 'understand',
          description: '理解：构建意义和解释',
          keywords: ['解释', '总结', '推理', '比较', '举例']
        },
        {
          name: 'remember',
          description: '记忆：回忆或识别信息',
          keywords: ['识别', '列举', '描述', '检索', '命名']
        }
      ],
      
      // 三维目标
      targets: {
        knowledge: {
          description: '',
          criteria: ''
        },
        process: {
          description: '',
          methods: []
        },
        attitude: {
          description: '',
          intensity: 3
        }
      },
      
      // 示例文本 - 数学7年级函数
      knowledgeExamples: [
        '理解函数的概念，识别函数关系',
        '掌握一次函数的表示方法', 
        '理解一次函数图像的特征和性质',
        '能用函数模型解决实际问题'
      ],
      processExamples: [
        '通过观察探索函数的变化规律',
        '运用数形结合思想分析函数',
        '培养从特殊到一般的思维方法',
        '学会建立函数模型的过程'
      ],
      attitudeExamples: [
        '感受函数在生活中的应用价值',
        '培养严谨的数学思维习惯',
        '增强用数学解决问题的信心',
        '体验数学探究的乐趣'
      ],
      
      // 一致性检测结果
      compatibilityResults: null
    }
  },
  
  computed: {
    // 当前选中层级的关键词
    currentLevelKeywords() {
      var self = this
      var level = this.bloomLevels.find(function(l) { 
        return l.name === self.selectedBloomLevel 
      })
      return level ? level.keywords : []
    },
    
    // 当前层级的详细说明
    currentLevelExplanation() {
      const explanations = {
        'create': '学生能够综合各种元素形成一个有机整体，或者为解决问题而制定行动计划。这是最高层次的认知活动，要求学生具备创新思维和综合能力。',
        'evaluate': '学生能够根据明确的标准做出价值判断，包括内部证据或外部标准。需要批判性思维和评价能力。',
        'analyze': '学生能够将材料分解为其组成部分，并探讨各部分之间及其与整体结构的关系。培养逻辑分析能力。',
        'apply': '学生能够在新的情境中执行或使用某个程序，包括规则、方法、概念等的应用。强调知识的迁移运用。',
        'understand': '学生能够理解所学内容的意义，能够解释、举例说明、分类、总结等。是深度学习的基础。',
        'remember': '学生能够提取、识别和回忆相关知识，是其他认知过程的基础。为后续学习奠定根基。'
      }
      return explanations[this.selectedBloomLevel] || ''
    },
    
    // 复杂度评估
    bloomComplexity() {
      const levelMap = {
        'remember': 20, 'understand': 35, 'apply': 50,
        'analyze': 65, 'evaluate': 80, 'create': 95
      }
      return levelMap[this.selectedBloomLevel] || 50
    },
    
    quantityComplexity() {
      var totalTargets = [
        this.targets.knowledge.description,
        this.targets.process.description, 
        this.targets.attitude.description
      ].filter(function(t) { return t && t.length > 10 }).length
      return Math.min(totalTargets * 30, 90)
    },
    
    depthComplexity() {
      var filteredTargets = [
        this.targets.knowledge.description,
        this.targets.process.description,
        this.targets.attitude.description
      ].filter(function(t) { return t })
      
      var totalLength = filteredTargets.map(function(t) { return t.length }).reduce(function(a, b) { return a + b }, 0)
      var avgLength = totalLength / 3
      return Math.min(avgLength / 2, 100)
    },
    
    complexityScore() {
      return Math.round((this.bloomComplexity + this.quantityComplexity + this.depthComplexity) / 3)
    },
    
    gaugeNeedleX() {
      const angle = (this.complexityScore / 100) * Math.PI
      return 100 + 70 * Math.cos(Math.PI - angle)
    },
    
    gaugeNeedleY() {
      const angle = (this.complexityScore / 100) * Math.PI  
      return 80 - 50 * Math.sin(Math.PI - angle)
    },
    
    // 综合一致性得分
    overallCompatibilityScore() {
      if (!this.compatibilityResults) return 0
      var total = this.compatibilityResults.reduce(function(sum, result) { 
        return sum + result.score 
      }, 0)
      return Math.round(total / this.compatibilityResults.length)
    }
  },
  
  watch: {
    selectedStandard(newValue) {
      if (newValue) {
        this.checkCompatibility()
      }
      // 实时保存课程标准选择
      this.saveToLocalStorage()
    },
    
    selectedBloomLevel() {
      // 实时保存布卢姆层级选择
      this.saveToLocalStorage()
    },
    
    targets: {
      handler() {
        if (this.selectedStandard) {
          this.checkCompatibility()
        }
        // 实时保存目标内容
        this.saveToLocalStorage()
      },
      deep: true
    }
  },
  
  methods: {
    getCurrentLevelNumber() {
      const levelMap = {
        'create': 6, 'evaluate': 5, 'analyze': 4,
        'apply': 3, 'understand': 2, 'remember': 1
      }
      return levelMap[this.selectedBloomLevel] || 1
    },
    
    getCurrentLevelName() {
      const levelNames = {
        'create': '创造', 'evaluate': '评价', 'analyze': '分析',
        'apply': '运用', 'understand': '理解', 'remember': '记忆'
      }
      return levelNames[this.selectedBloomLevel] || '理解'
    },
    
    getCurrentLevelSuggestions() {
      const suggestions = {
        'create': [
          '设计项目式学习活动，让学生创造新作品',
          '提供开放性问题，鼓励多元化解决方案',
          '组织头脑风暴和创意思维训练',
          '引导学生制定学习计划和研究方案'
        ],
        'evaluate': [
          '设置评价标准，训练学生判断能力',
          '组织辩论活动，培养批判性思维',
          '引导学生进行同伴评价和自我评价',
          '提供多角度分析的案例材料'
        ],
        'analyze': [
          '引导学生分解复杂问题和概念',
          '使用思维导图等工具进行结构分析',
          '对比分析不同观点和方法',
          '培养学生的逻辑推理能力'
        ],
        'apply': [
          '提供真实情境的应用练习',
          '设计多样化的实践活动',
          '鼓励学生解决实际问题',
          '强化知识在新情境中的迁移'
        ],
        'understand': [
          '使用多种表征方式呈现知识',
          '鼓励学生用自己的话解释概念',
          '设计概念映射和归纳活动',
          '提供丰富的实例和反例'
        ],
        'remember': [
          '采用多感官记忆策略',
          '设计有意义的重复练习',
          '使用助记符和记忆技巧',
          '建立知识之间的联系'
        ]
      }
      return suggestions[this.selectedBloomLevel] || []
    },
    
    getCurrentLevelIndicators() {
      const indicators = {
        'create': [
          { name: '原创性', level: 95 },
          { name: '完整性', level: 90 },
          { name: '实用性', level: 85 },
          { name: '创新性', level: 92 }
        ],
        'evaluate': [
          { name: '判断准确性', level: 88 },
          { name: '标准运用', level: 85 },
          { name: '论证合理性', level: 90 },
          { name: '批判深度', level: 87 }
        ],
        'analyze': [
          { name: '分解完整性', level: 82 },
          { name: '关系识别', level: 85 },
          { name: '逻辑清晰', level: 88 },
          { name: '结构理解', level: 80 }
        ],
        'apply': [
          { name: '应用正确性', level: 75 },
          { name: '情境适应', level: 78 },
          { name: '迁移能力', level: 72 },
          { name: '解决效果', level: 80 }
        ],
        'understand': [
          { name: '概念理解', level: 68 },
          { name: '解释能力', level: 70 },
          { name: '举例说明', level: 65 },
          { name: '总结归纳', level: 72 }
        ],
        'remember': [
          { name: '准确识别', level: 55 },
          { name: '完整回忆', level: 60 },
          { name: '快速提取', level: 58 },
          { name: '长期保持', level: 62 }
        ]
      }
      return indicators[this.selectedBloomLevel] || []
    },
    
    // 新增方法：获取兼容性等级描述
    getCompatibilityLevel(score) {
      if (score >= 90) return '高度匹配'
      if (score >= 80) return '良好匹配'
      if (score >= 70) return '基本匹配'
      if (score >= 60) return '需要改进'
      return '明显偏差'
    },
    
    // 获取综合评分颜色
    getOverallScoreColor() {
      const score = this.overallCompatibilityScore
      if (score >= 85) return '#67C23A'
      if (score >= 70) return '#E6A23C'
      return '#F56C6C'
    },
    
    // 获取评分徽章样式
    getScoreBadgeClass(score) {
      if (score >= 85) return 'score-excellent'
      if (score >= 70) return 'score-good'
      return 'score-poor'
    },
    
    // 获取复杂度等级
    getComplexityLevel(score) {
      if (score >= 80) return '高复杂度'
      if (score >= 60) return '中等复杂度'
      if (score >= 40) return '适中复杂度'
      return '低复杂度'
    },
    
    // 获取布卢姆层级文本描述
    getBloomLevelText() {
      const texts = {
        'create': '创造层级：具有挑战性',
        'evaluate': '评价层级：需要判断能力', 
        'analyze': '分析层级：需要逻辑思维',
        'apply': '运用层级：实践性强',
        'understand': '理解层级：概念性学习',
        'remember': '记忆层级：基础知识'
      }
      return texts[this.selectedBloomLevel] || '适中难度'
    },
    
    // 获取数量文本描述
    getQuantityText() {
      var count = [
        this.targets.knowledge.description,
        this.targets.process.description,
        this.targets.attitude.description
      ].filter(function(t) { return t && t.length > 10 }).length
      
      if (count === 3) return '三维目标完整'
      if (count === 2) return '目标相对完整'
      if (count === 1) return '目标需要补充'
      return '目标尚未设定'
    },
    
    // 获取深度文本描述
    getDepthText() {
      var filteredTargets = [
        this.targets.knowledge.description,
        this.targets.process.description,
        this.targets.attitude.description
      ].filter(function(t) { return t })
      
      var totalLength = filteredTargets.map(function(t) { return t.length }).reduce(function(a, b) { return a + b }, 0)
      var avgLength = totalLength / 3
      
      if (avgLength > 50) return '内容详细具体'
      if (avgLength > 30) return '内容相对充实'
      if (avgLength > 15) return '内容基本合理'
      return '内容需要完善'
    },
    
    // 获取复杂度优化建议
    getComplexitySuggestions() {
      const suggestions = []
      
      if (this.bloomComplexity < 50) {
        suggestions.push({
          type: 'bloom-improve',
          icon: 'el-icon-top',
          text: '建议选择更高认知层级，提升思维挑战性'
        })
      }
      
      if (this.quantityComplexity < 60) {
        suggestions.push({
          type: 'quantity-improve',
          icon: 'el-icon-plus',
          text: '建议完善三维目标设定，确保目标全面性'
        })
      }
      
      if (this.depthComplexity < 40) {
        suggestions.push({
          type: 'depth-improve',
          icon: 'el-icon-edit',
          text: '建议丰富目标描述内容，提升目标具体性'
        })
      }
      
      if (this.complexityScore > 85) {
        suggestions.push({
          type: 'complexity-good',
          icon: 'el-icon-check',
          text: '目标复杂度设置合理，符合学习规律'
        })
      }
      
      return suggestions
    },
    
    selectBloomLevel(level) {
      this.selectedBloomLevel = level
    },
    
    insertExample(type, example) {
      if (this.targets[type].description) {
        this.targets[type].description += '；' + example
      } else {
        this.targets[type].description = example
      }
    },
    
    generateKnowledgeTargets() {
      this.$message.info('AI正在生成知识与技能目标...')
      // 模拟AI生成 - 数学7年级函数
      var self = this
      setTimeout(function() {
        self.targets.knowledge.description = '理解函数的概念，掌握一次函数的表示方法和图像特征，能够建立函数模型解决实际问题，熟练运用函数关系式进行计算和推理'
        self.targets.knowledge.criteria = 'master'
        self.$message.success('AI生成完成')
      }, 1500)
    },
    
    generateProcessTargets() {
      this.$message.info('AI正在生成过程与方法目标...')
      var self = this
      setTimeout(function() {
        self.targets.process.description = '通过观察、实验探索函数规律，运用数形结合思想分析问题，培养逻辑推理和抽象思维能力，学会从特殊到一般的研究方法'
        self.targets.process.methods = ['观察探究', '问题解决', '合作学习']
        self.$message.success('AI生成完成')
      }, 1500)
    },
    
    generateAttitudeTargets() {
      this.$message.info('AI正在生成情感态度目标...')
      var self = this
      setTimeout(function() {
        self.targets.attitude.description = '感受数学的逻辑美和应用价值，培养严谨治学的科学态度，增强用数学解决实际问题的信心，体验数学探究的乐趣和成就感'
        self.targets.attitude.intensity = 4
        self.$message.success('AI生成完成')
      }, 1500)
    },
    
    checkCompatibility() {
      this.$message.info('正在检测与课程标准的一致性...')
      var self = this
      setTimeout(function() {
        self.compatibilityResults = [
          {
            dimension: '知识与技能',
            score: 85,
            suggestion: '与课程标准要求高度吻合，建议增加实际应用环节'
          },
          {
            dimension: '过程与方法', 
            score: 78,
            suggestion: '方法选择合适，建议强化探究性学习活动'
          },
          {
            dimension: '情感态度',
            score: 92,
            suggestion: '完全符合课程标准的育人目标'
          }
        ]
        self.updateAnalysisStatus()
        self.$message.success('一致性检测完成')
      }, 2000)
    },
    
    // 新增方法：运行一致性检测
    runCompatibilityCheck() {
      if (!this.selectedStandard) {
        this.$message.warning('请先选择课程标准')
        return
      }
      
      const hasTargets = this.targets.knowledge.description || 
                         this.targets.process.description || 
                         this.targets.attitude.description
      
      if (!hasTargets) {
        this.$message.warning('请先设定教学目标')
        return
      }
      
      this.compatibilityLoading = true
      this.analysisStatus = { type: 'warning', text: '检测中...' }
      
      setTimeout(() => {
        this.checkCompatibility()
        this.compatibilityLoading = false
      }, 1500)
    },
    
    // 课程标准变更处理
    onStandardChange() {
      if (this.compatibilityResults) {
        this.runCompatibilityCheck()
      }
    },
    
    // 导出复杂度报告
    exportComplexityReport(format) {
      this.$message.info('正在生成' + format.toUpperCase() + '报告...')
      var self = this
      setTimeout(function() {
        self.$message.success(format.toUpperCase() + '报告已生成并下载')
      }, 1500)
    },
    
    // 更新分析状态
    updateAnalysisStatus() {
      const score = this.overallCompatibilityScore
      if (score >= 85) {
        this.analysisStatus = { type: 'success', text: '目标质量优秀' }
      } else if (score >= 70) {
        this.analysisStatus = { type: 'warning', text: '目标质量良好' }
      } else {
        this.analysisStatus = { type: 'danger', text: '目标需要优化' }
      }
    },
    
    getCompatibilityType(score) {
      if (score >= 85) return 'success'
      if (score >= 70) return 'warning' 
      return 'danger'
    },
    
    getProgressColor(score) {
      if (score >= 85) return '#67C23A'
      if (score >= 70) return '#E6A23C'
      return '#F56C6C'
    },
    
    previewTargets() {
      this.previewVisible = true
    },
    
    exportTargets() {
      this.$message.info('正在生成Word文档...')
      setTimeout(() => {
        this.$message.success('目标文档已导出')
      }, 1000)
    },
    
    confirmTargets() {
      this.previewVisible = false
      this.saveAndNext()
    },
    
    saveDraft() {
      this.saveToLocalStorageNow()
      this.$message.success('草稿已保存')
    },
    
    saveAndNext() {
      // 验证目标是否完整
      const hasKnowledge = this.targets.knowledge.description.length > 0
      const hasProcess = this.targets.process.description.length > 0
      const hasAttitude = this.targets.attitude.description.length > 0
      
      if (!hasKnowledge || !hasProcess || !hasAttitude) {
        this.$message.warning('请完善所有三维目标设定')
        return
      }
      
      // 保存数据到本地存储
      this.saveToLocalStorageNow()
      
      this.$emit('step-completed', 2)
      this.$router.push('/teaching-design/key-points')
      this.$message.success('目标设定已保存')
    },
    
    // 保存数据到本地存储（带防抖）
    saveToLocalStorage() {
      // 清除之前的定时器
      if (this.saveTimer) {
        clearTimeout(this.saveTimer)
      }
      
      // 设置新的定时器，500ms后执行保存
      var self = this
      this.saveTimer = setTimeout(function() {
        var dataToSave = {
          bloomLevel: self.selectedBloomLevel,
          targets: self.targets,
          standard: self.selectedStandard,
          compatibility: self.compatibilityResults,
          timestamp: new Date().getTime()
        }
        localStorage.setItem('teachingTargets', JSON.stringify(dataToSave))
        console.log('数据已自动保存')
      }, 500)
    },
    
    // 立即保存数据到本地存储
    saveToLocalStorageNow() {
      const dataToSave = {
        bloomLevel: this.selectedBloomLevel,
        targets: this.targets,
        standard: this.selectedStandard,
        compatibility: this.compatibilityResults,
        timestamp: new Date().getTime()
      }
      localStorage.setItem('teachingTargets', JSON.stringify(dataToSave))
    },
    
    // 从本地存储加载数据
    loadFromLocalStorage() {
      try {
        const saved = localStorage.getItem('teachingTargets')
        if (saved) {
          const data = JSON.parse(saved)
          
          // 恢复布卢姆层级选择
          if (data.bloomLevel) {
            this.selectedBloomLevel = data.bloomLevel
          }
          
          // 恢复三维目标内容
          if (data.targets) {
            this.targets = {
              ...this.targets,
              ...data.targets
            }
          }
          
          // 恢复课程标准选择
          if (data.standard) {
            this.selectedStandard = data.standard
          }
          
          // 恢复一致性检测结果
          if (data.compatibility) {
            this.compatibilityResults = data.compatibility
          }
          
          console.log('数据加载成功:', data)
        }
      } catch (error) {
        console.error('加载保存数据失败:', error)
      }
    },
    
    handleBeforeUnload() {
      // 页面卸载前立即保存数据
      this.saveToLocalStorageNow()
    },
    
    // ========== 智能导入功能方法 ==========
    
    // 文档导入功能
    triggerDocumentUpload() {
      this.$refs.documentUpload.click()
    },
    
    async handleDocumentUpload(event) {
      var file = event.target.files[0]
      if (!file) return
      
      // 📄 文件类型验证
      var allowedTypes = ['application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/msword']
      var allowedExtensions = ['.pdf', '.docx', '.doc']
      var fileName = file.name.toLowerCase()
      var isValidType = allowedTypes.includes(file.type) || allowedExtensions.some(function(ext) {
        return fileName.endsWith(ext)
      })
      
      if (!isValidType) {
        this.$message.error('请选择有效的文档格式（.pdf, .docx, .doc）')
        event.target.value = ''
        return
      }
      
      // 📏 文件大小验证（10MB限制）
      var maxSize = 10 * 1024 * 1024 // 10MB
      if (file.size > maxSize) {
        this.$message.error('文件大小不能超过10MB')
        event.target.value = ''
        return
      }
      
      this.documentProcessing = true
      this.documentUploadProgress = 0
      this.documentUploadStatus = '正在上传文档...'
      this.importStatus = { type: 'warning', text: '文档解析中' }
      
      try {
        // 🚀 模拟上传进度
        var self = this
        var uploadInterval = setInterval(function() {
          if (self.documentUploadProgress < 85) {
            self.documentUploadProgress += Math.random() * 15 + 5 // 随机进度增长
          }
        }, 150)
        
        // 📖 模拟文档解析过程
        await this.simulateDocumentParsing(file)
        
        clearInterval(uploadInterval)
        this.documentUploadProgress = 100
        this.documentUploadStatus = '文档解析完成'
        
        // 🤖 模拟AI解析结果
        var parsedTargets = await this.parseDocument(file)
        
        // 🔗 分析布卢姆层级并应用结果
        this.applyParsedTargets(parsedTargets)
        
        this.$message.success('文档解析完成，目标已自动填充')
        
        // 💾 添加到历史记录
        this.addToHistory({
          type: 'document',
          name: file.name,
          timestamp: this.formatTimestamp(new Date()),
          targets: parsedTargets
        })
        
      } catch (error) {
        this.importStatus = { type: 'danger', text: '导入失败' }
        this.$message.error('文档解析失败：' + error.message)
      } finally {
        this.documentProcessing = false
        // 🔄 重置文件输入
        event.target.value = ''
      }
    },
    
    async simulateDocumentParsing(file) {
      // 模拟文档解析时间
      return new Promise(function(resolve) {
        setTimeout(resolve, 2000)
      })
    },
    
    async parseDocument(file) {
      // 模拟AI解析文档内容为数学7年级函数的三维目标
      return {
        knowledge: {
          description: '理解函数的概念，掌握一次函数的表示方法和图像特征，能够建立函数模型解决实际问题，熟练运用函数关系式进行计算和推理',
          criteria: 'master'
        },
        process: {
          description: '通过观察、实验探索函数规律，运用数形结合思想分析问题，培养逻辑推理和抽象思维能力，学会从特殊到一般的研究方法',
          methods: ['观察探究', '问题解决', '合作学习']
        },
        attitude: {
          description: '感受数学的逻辑美和应用价值，培养严谨治学的科学态度，增强用数学解决实际问题的信心，体验数学探究的乐趣和成就感',
          intensity: 4
        }
      }
    },
    
    // 语音识别功能
    async toggleRecording() {
      if (this.isRecording) {
        this.stopRecording()
      } else {
        await this.startRecording()
      }
    },
    
    async startRecording() {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
        this.mediaRecorder = new MediaRecorder(stream)
        
        const audioChunks = []
        var self = this
        this.mediaRecorder.ondataavailable = function(event) {
          audioChunks.push(event.data)
        }
        
        this.mediaRecorder.onstop = async function() {
          var audioBlob = new Blob(audioChunks, { type: 'audio/wav' })
          await self.processAudioToText(audioBlob)
        }
        
        this.mediaRecorder.start()
        this.isRecording = true
        this.recordingTime = 0
        this.importStatus = { type: 'warning', text: '正在录音' }
        
        // 开始计时
        var self = this
        this.recordingInterval = setInterval(function() {
          self.recordingTime++
        }, 1000)
        
        this.$message.info('开始录音，请口述您的教学目标')
        
      } catch (error) {
        this.$message.error('无法访问麦克风：' + error.message)
      }
    },
    
    stopRecording() {
      if (this.mediaRecorder && this.isRecording) {
        this.mediaRecorder.stop()
        this.isRecording = false
        clearInterval(this.recordingInterval)
        
        // 停止媒体流
        this.mediaRecorder.stream.getTracks().forEach(function(track) { 
          track.stop() 
        })
        
        this.importStatus = { type: 'info', text: '处理录音中' }
        this.$message.info('录音结束，正在识别语音...')
      }
    },
    
    async processAudioToText(audioBlob) {
      this.voiceProcessing = true
      
      try {
        // 模拟语音识别API调用
        await new Promise(function(resolve) { setTimeout(resolve, 2000) })
        
        // 模拟识别结果 - 数学7年级函数
        this.voiceText = "学生能够理解函数的概念，掌握一次函数的表示方法和图像特征，能够建立函数模型解决实际问题，通过观察实验探索函数规律，培养数形结合思想和逻辑推理能力，感受数学的逻辑美和应用价值，增强用数学解决问题的信心"
        
        this.importStatus = { type: 'success', text: '语音识别完成' }
        this.$message.success('语音识别完成，请点击智能分类按钮')
        
      } catch (error) {
        this.$message.error('语音识别失败：' + error.message)
        this.importStatus = { type: 'danger', text: '识别失败' }
      } finally {
        this.voiceProcessing = false
      }
    },
    
    async processVoiceTargets() {
      this.voiceProcessing = true
      
      try {
        // 模拟AI分类处理
        await new Promise(function(resolve) { setTimeout(resolve, 1500) })
        
        var classifiedTargets = this.classifyVoiceText(this.voiceText)
        
        // 🔗 分析布卢姆层级
        var detectedLevel = this.detectBloomLevel(this.voiceText)
        
        this.applyParsedTargets(classifiedTargets)
        
        this.$message.success('语音内容已智能分类并填充到三维目标')
        this.importStatus = { 
          type: 'success', 
          text: detectedLevel ? ('分类完成 - 检测到"' + this.getBloomLevelName(detectedLevel) + '"层级') : '分类完成'
        }
        
        // 添加到历史记录
        this.addToHistory({
          type: 'voice',
          name: '语音描述_' + this.formatTimestamp(new Date()),
          timestamp: this.formatTimestamp(new Date()),
          targets: classifiedTargets
        })
        
      } catch (error) {
        this.$message.error('智能分类失败：' + error.message)
      } finally {
        this.voiceProcessing = false
      }
    },
    
    classifyVoiceText(text) {
      // 固定返回数学7年级函数的教学目标
      return {
        knowledge: {
          description: '理解函数的概念，掌握一次函数的表示方法和图像特征，能够建立函数模型解决实际问题，熟练运用函数关系式进行计算和推理',
          criteria: 'master'
        },
        process: {
          description: '通过观察、实验探索函数规律，运用数形结合思想分析问题，培养逻辑推理和抽象思维能力，学会从特殊到一般的研究方法',
          methods: ['观察探究', '问题解决', '合作学习']
        },
        attitude: {
          description: '感受数学的逻辑美和应用价值，培养严谨治学的科学态度，增强用数学解决实际问题的信心，体验数学探究的乐趣和成就感',
          intensity: 4
        }
      }
    },
    
    extractByKeywords(text, keywords) {
      // 简单的关键词提取逻辑
      var sentences = text.split(/[，。、]/);
      var relevantSentences = sentences.filter(function(sentence) {
        return keywords.some(function(keyword) {
          return sentence.includes(keyword)
        })
      });
      return relevantSentences.join('，') || null;
    },
    
    // 图片OCR功能
    triggerImageUpload() {
      this.$refs.imageUpload.click()
    },
    
    openCamera() {
      // 📱 移动端相机拍照支持
      var input = this.$refs.imageUpload
      
      // 检测是否为移动设备
      var isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
      
      if (isMobile) {
        input.setAttribute('capture', 'camera')
        input.setAttribute('accept', 'image/*')
        this.$message.info('正在启动相机...')
      } else {
        this.$message.warning('相机功能主要适用于移动设备，桌面端请选择图片文件')
      }
      
      input.click()
    },
    
    handleImageUpload(event) {
      var file = event.target.files[0]
      if (!file) return
      
      // 🖼️ 图片类型验证
      var allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/bmp', 'image/webp']
      if (!allowedTypes.includes(file.type)) {
        this.$message.error('请选择有效的图片格式（JPEG, PNG, GIF, BMP, WEBP）')
        event.target.value = ''
        return
      }
      
      // 📏 文件大小验证（5MB限制）
      var maxSize = 5 * 1024 * 1024 // 5MB
      if (file.size > maxSize) {
        this.$message.error('图片大小不能超过5MB')
        event.target.value = ''
        return
      }
      
      // 🎯 清除之前的结果
      this.ocrResult = ''
      this.importStatus = { type: 'info', text: '图片上传成功' }
      
      // 📸 预览图片
      var self = this
      var reader = new FileReader()
      reader.onload = function(e) {
        self.uploadedImage = e.target.result
        self.$message.success('图片上传成功，点击识别按钮开始OCR分析')
      }
      reader.onerror = function() {
        self.$message.error('图片读取失败，请重新选择')
        self.uploadedImage = null
      }
      reader.readAsDataURL(file)
      
      // 🔄 重置文件输入
      event.target.value = ''
    },
    
    async processImageOCR() {
      if (!this.uploadedImage) {
        this.$message.warning('请先上传图片')
        return
      }
      
      this.ocrProcessing = true
      this.importStatus = { type: 'warning', text: 'OCR识别中' }
      
      try {
        // 🔄 模拟OCR处理进度
        this.$message.info('正在分析图片内容...')
        
        // 📊 模拟处理时间（基于图片复杂度）
        var processingTime = Math.random() * 1000 + 1500 // 1.5-2.5秒
        await new Promise(function(resolve) { 
          setTimeout(resolve, processingTime) 
        })
        
        // 🤖 模拟OCR识别结果 - 数学7年级函数
        this.ocrResult = "教学目标：1. 知识与技能：学生能够理解函数的概念，掌握一次函数的表示方法和图像特征，能够建立函数模型解决实际问题，熟练运用函数关系式进行计算和推理。2. 过程与方法：通过观察、实验探索函数规律，运用数形结合思想分析问题，培养逻辑推理和抽象思维能力，学会从特殊到一般的研究方法。3. 情感态度价值观：感受数学的逻辑美和应用价值，培养严谨治学的科学态度，增强用数学解决实际问题的信心，体验数学探究的乐趣和成就感。"
        
        this.importStatus = { type: 'success', text: 'OCR识别完成' }
        this.$message.success('图片文字识别完成，请点击智能分类按钮')
        
      } catch (error) {
        this.$message.error('OCR识别失败：' + error.message)
        this.importStatus = { type: 'danger', text: '识别失败' }
        this.ocrResult = ''
      } finally {
        this.ocrProcessing = false
      }
    },
    
    async processOCRTargets() {
      if (!this.ocrResult) {
        this.$message.warning('请先进行OCR识别')
        return
      }
      
      this.ocrProcessing = true
      this.importStatus = { type: 'warning', text: '正在智能分类...' }
      
      try {
        // 🤖 模拟AI分类处理
        this.$message.info('正在智能分析OCR内容...')
        await new Promise(function(resolve) { setTimeout(resolve, 1500) })
        
        // 📖 解析OCR文本为三维目标
        var classifiedTargets = this.parseOCRText(this.ocrResult)
        
        // 🔗 关键修复：应用目标并触发布卢姆层级分析
        this.applyParsedTargets(classifiedTargets)
        
        this.$message.success('OCR内容已智能分类并填充到三维目标')
        
        // 💾 添加到历史记录
        this.addToHistory({
          type: 'image',
          name: '图片识别_' + this.formatTimestamp(new Date()),
          timestamp: this.formatTimestamp(new Date()),
          targets: classifiedTargets
        })
        
      } catch (error) {
        this.$message.error('智能分类失败：' + error.message)
        this.importStatus = { type: 'danger', text: '分类失败' }
      } finally {
        this.ocrProcessing = false
      }
    },
    
    parseOCRText(text) {
      // 🤖 返回包含多种布卢姆层级关键词的教学目标，用于测试层级检测
      return {
        knowledge: {
          description: '理解函数的概念，掌握一次函数的表示方法和图像特征，能够运用函数模型解决实际问题，熟练应用函数关系式进行计算和推理',
          criteria: 'master'
        },
        process: {
          description: '通过观察分析函数规律，运用数形结合思想解决问题，培养逻辑推理和抽象思维能力，学会从特殊到一般的研究方法',
          methods: ['观察探究', '问题解决', '合作学习']
        },
        attitude: {
          description: '感受数学的逻辑美和应用价值，培养严谨治学的科学态度，增强用数学解决问题的信心，体验数学探究的乐趣和成就感',
          intensity: 4
        }
      }
    },
    
    clearImage() {
      this.uploadedImage = null
      this.ocrResult = ''
      this.importStatus = null
      this.$message.info('已清除图片和识别结果')
    },
    

    
    // 通用方法
    applyParsedTargets(parsedTargets) {
      // 应用解析的目标到当前编辑器
      if (parsedTargets.knowledge) {
        this.targets.knowledge = { ...this.targets.knowledge, ...parsedTargets.knowledge }
      }
      if (parsedTargets.process) {
        this.targets.process = { ...this.targets.process, ...parsedTargets.process }
      }
      if (parsedTargets.attitude) {
        this.targets.attitude = { ...this.targets.attitude, ...parsedTargets.attitude }
      }
      
      // 🔗 新增：智能分析导入内容的布卢姆层级
      this.analyzeBloomLevelFromTargets(parsedTargets)
    },
    
    // 🔗 新增：从导入目标中分析布卢姆认知层级
    analyzeBloomLevelFromTargets(targets) {
      var allContent = [
        (targets.knowledge && targets.knowledge.description) || '',
        (targets.process && targets.process.description) || '', 
        (targets.attitude && targets.attitude.description) || ''
      ].join(' ')
      
      // 🔍 调试信息：显示分析的内容
      console.log('🔍 分析内容:', allContent)
      console.log('🎯 当前层级:', this.selectedBloomLevel)
      
      var detectedLevel = this.detectBloomLevel(allContent)
      console.log('🤖 检测到层级:', detectedLevel)
      
      if (detectedLevel && detectedLevel !== this.selectedBloomLevel) {
        // 📊 更新导入状态显示检测结果
        var detectedLevelName = this.getBloomLevelName(detectedLevel)
        this.importStatus = { 
          type: 'success', 
          text: '导入成功 - 检测到"' + detectedLevelName + '"层级'
        }
        
        // 💡 显示层级建议对话框
        var self = this
        var currentLevelName = this.getBloomLevelName(this.selectedBloomLevel)
        var matchedKeywords = this.getMatchedKeywords(allContent, detectedLevel).join('、')
        
        console.log('🏷️ 匹配关键词:', matchedKeywords)
        
        this.$confirm(
          '检测到导入内容主要体现"' + detectedLevelName + '"层级特征，是否切换到该层级？', 
          '布卢姆层级建议', 
          {
            confirmButtonText: '切换到建议层级',
            cancelButtonText: '保持当前层级',
            type: 'info',
            dangerouslyUseHTMLString: true,
            message: '<div style="text-align: left;">' +
              '<p><strong>AI分析结果：</strong></p>' +
              '<p>• 检测到的层级：<span style="color: #409EFF; font-weight: bold;">' + detectedLevelName + '</span></p>' +
              '<p>• 当前选择层级：<span style="color: #666;">' + currentLevelName + '</span></p>' +
              '<p>• 匹配关键词：<span style="color: #67C23A;">' + matchedKeywords + '</span></p>' +
              '</div>'
          }
        ).then(function() {
          self.selectedBloomLevel = detectedLevel
          self.$message.success('已切换到"' + detectedLevelName + '"层级')
          
          // ✨ 高亮显示布卢姆层级变化
          self.highlightBloomLevelChange()
        }).catch(function() {
          self.$message.info('保持当前层级设置')
        })
      } else if (detectedLevel === this.selectedBloomLevel) {
        // 🎯 检测到相同层级时的状态更新
        this.importStatus = { type: 'success', text: '导入成功 - 层级匹配当前设置' }
        console.log('✅ 检测层级与当前层级相同')
      } else {
        // 🎯 未检测到层级时的状态更新
        this.importStatus = { type: 'success', text: '导入成功' }
        console.log('⚠️ 未检测到明确的布卢姆层级')
      }
    },
    
    // 🔗 新增：检测文本中的布卢姆层级
    detectBloomLevel(text) {
      var levelKeywords = {
        create: ['设计', '创作', '构建', '发明', '制作', '创造', '开发', '规划', '组织'],
        evaluate: ['评估', '判断', '批评', '推荐', '证明', '评价', '检验', '论证', '审查'],
        analyze: ['分析', '比较', '分类', '区分', '推断', '解剖', '探究', '研究', '归纳'],
        apply: ['应用', '实施', '操作', '解决', '使用', '运用', '实践', '执行', '处理'],
        understand: ['解释', '总结', '推理', '比较', '举例', '理解', '掌握', '阐述', '说明'],
        remember: ['识别', '列举', '描述', '检索', '命名', '记住', '回忆', '背诵', '复述']
      }
      
      var scores = {}
      var self = this
      
      // 🧮 计算每个层级的匹配分数
      Object.keys(levelKeywords).forEach(function(level) {
        scores[level] = 0
        levelKeywords[level].forEach(function(keyword) {
          var matches = (text.match(new RegExp(keyword, 'g')) || []).length
          scores[level] += matches
        })
      })
      
      // 🔍 调试：输出匹配分数
      console.log('🔢 各层级得分:', scores)
      
      // 🏆 找出得分最高的层级
      var scoresArray = Object.keys(scores).map(function(level) {
        return scores[level]
      })
      var maxScore = Math.max.apply(Math, scoresArray)
      
      console.log('🏅 最高得分:', maxScore)
      
      if (maxScore === 0) {
        console.log('❌ 未找到任何匹配关键词')
        return null
      }
      
      // 🎯 返回得分最高的层级
      var topLevel = Object.keys(scores).find(function(level) { 
        return scores[level] === maxScore 
      })
      
      console.log('🎯 检测到的层级:', topLevel)
      return topLevel
    },
    
    // 🔗 新增：获取匹配的关键词
    getMatchedKeywords(text, level) {
      var levelKeywords = {
        create: ['设计', '创作', '构建', '发明', '制作', '创造', '开发', '规划', '组织'],
        evaluate: ['评估', '判断', '批评', '推荐', '证明', '评价', '检验', '论证', '审查'],
        analyze: ['分析', '比较', '分类', '区分', '推断', '解剖', '探究', '研究', '归纳'],
        apply: ['应用', '实施', '操作', '解决', '使用', '运用', '实践', '执行', '处理'],
        understand: ['解释', '总结', '推理', '比较', '举例', '理解', '掌握', '阐述', '说明'],
        remember: ['识别', '列举', '描述', '检索', '命名', '记住', '回忆', '背诵', '复述']
      }
      
      var matched = []
      if (levelKeywords[level]) {
        levelKeywords[level].forEach(function(keyword) {
          if (text.includes(keyword)) {
            matched.push(keyword)
          }
        })
      }
      
      return matched.slice(0, 3) // 🎯 只返回前3个匹配的关键词
    },
    
    // 🔗 新增：获取布卢姆层级中文名
    getBloomLevelName(level) {
      var names = {
        create: '创造', evaluate: '评价', analyze: '分析',
        apply: '运用', understand: '理解', remember: '记忆'
      }
      return names[level] || level
    },
    
    // 🔗 新增：高亮布卢姆层级变化效果
    highlightBloomLevelChange() {
      // 添加高亮动画效果
      var self = this
      this.$nextTick(function() {
        var activeLevel = document.querySelector('.pyramid-level-compact.active')
        if (activeLevel) {
          activeLevel.style.animation = 'levelChangeHighlight 2s ease-in-out'
          setTimeout(function() {
            activeLevel.style.animation = ''
          }, 2000)
        }
      })
    },
    
    addToHistory(record) {
      this.importHistory.unshift(record)
      // 只保留最近10条记录
      if (this.importHistory.length > 10) {
        this.importHistory = this.importHistory.slice(0, 10)
      }
      // 保存到本地存储
      localStorage.setItem('importHistory', JSON.stringify(this.importHistory))
    },
    
    applyHistoryRecord(record) {
      this.applyParsedTargets(record.targets)
      this.$message.success('历史记录已应用')
    },
    
    // 删除单个历史记录
    deleteHistoryRecord(index) {
      var self = this
      this.$confirm('确定要删除这条导入记录吗？', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(function() {
        self.importHistory.splice(index, 1)
        // 更新本地存储
        localStorage.setItem('importHistory', JSON.stringify(self.importHistory))
        self.$message.success('记录已删除')
      }).catch(function() {
        // 用户取消删除
      })
    },
    
    // 清空所有历史记录
    clearAllHistory() {
      var self = this
      this.$confirm('确定要清空所有导入记录吗？此操作不可恢复！', '确认清空', {
        confirmButtonText: '确定清空',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        message: '<p>确定要清空所有导入记录吗？</p><p style="color: #f56c6c; font-size: 12px;">此操作不可恢复！</p>'
      }).then(function() {
        self.importHistory = []
        // 清空本地存储
        localStorage.removeItem('importHistory')
        self.$message.success('所有记录已清空')
      }).catch(function() {
        // 用户取消清空
      })
    },
    
    getHistoryIcon(type) {
      var icons = {
        document: 'el-icon-document',
        voice: 'el-icon-microphone',
        image: 'el-icon-camera'
      }
      return icons[type] || 'el-icon-document'
    },
    
    getTypeText(type) {
      var texts = {
        document: '文档导入',
        voice: '语音识别',
        image: '图片识别'
      }
      return texts[type] || '未知类型'
    },
    
    formatTime(seconds) {
      var mins = Math.floor(seconds / 60)
      var secs = seconds % 60
      var minsStr = mins.toString()
      var secsStr = secs.toString()
      
      // 手动实现 padStart 功能
      if (minsStr.length < 2) minsStr = '0' + minsStr
      if (secsStr.length < 2) secsStr = '0' + secsStr
      
      return minsStr + ':' + secsStr
    },
    
    formatTimestamp(date) {
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    
    // 加载导入历史记录
    loadImportHistory() {
      try {
        var history = localStorage.getItem('importHistory')
        if (history) {
          this.importHistory = JSON.parse(history)
        }
      } catch (error) {
        console.error('加载导入历史失败:', error)
      }
    }
  },
  
  mounted() {
    // 加载已保存的数据
    this.loadFromLocalStorage()
    
    // 加载导入历史记录
    this.loadImportHistory()
    
    // 添加页面卸载前的保存逻辑
    window.addEventListener('beforeunload', this.handleBeforeUnload)
  },
  
  beforeDestroy() {
    // 组件销毁前保存数据
    this.saveToLocalStorageNow()
    
    // 移除事件监听器
    window.removeEventListener('beforeunload', this.handleBeforeUnload)
  }
}
</script>

<style scoped>
.objective-builder {
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

/* 通用章节样式 */
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
  border-bottom: 1px solid rgba(64, 158, 255, 0.1);
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

/* 布卢姆认知层级优化样式 */
.bloom-section {
  margin-bottom: 32px;
}

.bloom-selector-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  align-items: start;
  padding: 30px;
}

.bloom-pyramid-compact {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pyramid-level-compact {
  width: calc(100% - var(--level-offset, 0px));
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.8);
  border: 2px solid rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  margin: 0 auto;
  backdrop-filter: blur(8px);
}

.pyramid-level-compact.level-0 { --level-offset: 0px; }
.pyramid-level-compact.level-1 { --level-offset: 30px; }
.pyramid-level-compact.level-2 { --level-offset: 60px; }
.pyramid-level-compact.level-3 { --level-offset: 90px; }
.pyramid-level-compact.level-4 { --level-offset: 120px; }
.pyramid-level-compact.level-5 { --level-offset: 150px; }

.pyramid-level-compact:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 12px 25px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
  background: rgba(255, 255, 255, 0.95);
}

.pyramid-level-compact.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 15px 30px rgba(102, 126, 234, 0.3);
  border-color: #667eea;
}

.level-content-compact {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.level-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.level-header h5 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 700;
  text-transform: capitalize;
}

.level-desc {
  margin: 0;
  font-size: 13px;
  opacity: 0.8;
  max-width: 200px;
}

.level-number {
  font-size: 24px;
  font-weight: 900;
  opacity: 0.3;
  font-family: 'Arial Black', sans-serif;
}

.pyramid-level-compact.active .level-number {
  opacity: 0.6;
  color: rgba(255, 255, 255, 0.8);
}

/* 关键词面板样式 */
.bloom-keywords-panel {
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(10px);
  height: fit-content;
  position: sticky;
  top: 20px;
}

.bloom-keywords-panel h4 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.bloom-keywords-panel h4 i {
  color: #667eea;
}

/* 当前层级信息 */
.current-level-info {
  margin-bottom: 20px;
}

.level-badge {
  display: inline-flex;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  gap: 6px;
}

.badge-number {
  font-size: 16px;
  font-weight: 900;
  background: rgba(255, 255, 255, 0.2);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.badge-name {
  font-size: 14px;
}

/* 子区域样式 */
.keywords-section,
.level-explanation,
.teaching-suggestions,
.assessment-indicators {
  margin-bottom: 20px;
}

.keywords-section h5,
.level-explanation h5,
.teaching-suggestions h5,
.assessment-indicators h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 6px;
}

.current-keywords {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.keyword-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  color: white !important;
  font-weight: 500;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.level-explanation {
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
  border-radius: 8px;
  padding: 12px;
  border-left: 3px solid #667eea;
}

.level-explanation p {
  margin: 0;
  font-size: 13px;
  color: #475569;
  line-height: 1.6;
}

/* 教学建议样式 */
.suggestion-list {
  margin: 0;
  padding-left: 0;
  list-style: none;
}

.suggestion-list li {
  position: relative;
  padding-left: 20px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
}

.suggestion-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #667eea;
  font-weight: bold;
}

/* 评价指标样式 */
.indicators {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.indicator-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.indicator-name {
  font-size: 12px;
  color: #64748b;
  min-width: 60px;
  text-align: right;
}

.indicator-bar {
  flex: 1;
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
}

.indicator-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px;
  transition: width 0.8s ease;
}

/* 三维目标编辑器横向布局样式 */
.targets-section {
  margin-bottom: 32px;
}

.dimension-editors-horizontal {
  display: flex;
  gap: 20px;
  justify-content: space-between;
  padding: 30px;
}

.target-card-horizontal {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  overflow: hidden;
  min-height: 320px;
}

.target-card-horizontal:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.3);
}

.card-header {
  padding: 16px 20px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-left i {
  font-size: 18px;
}

.knowledge-card .card-header {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.process-card .card-header {
  background: linear-gradient(135deg, #10b981 0%, #047857 100%);
}

.attitude-card .card-header {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.card-content {
  padding: 20px;
  height: calc(100% - 56px);
  display: flex;
  flex-direction: column;
}

.form-section {
  flex: 1;
  margin-bottom: 16px;
}

.examples-section {
  border-top: 1px solid #e2e8f0;
  padding-top: 16px;
}

.examples-section h6 {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
}

.example-tags-compact {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.clickable-tag-small {
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 12px !important;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.clickable-tag-small:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.ai-assist {
  color: white !important;
  opacity: 0.9;
}

.ai-assist:hover {
  opacity: 1;
  background: rgba(255,255,255,0.1) !important;
}

/* 智能分析与评估 - 全新样式 */
.analysis-section {
  margin-bottom: 32px;
}

.analysis-cards-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  padding: 30px;
}

.analysis-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(10px);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.analysis-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 50px rgba(102, 126, 234, 0.2);
}

.analysis-card .card-header {
  padding: 25px 30px 20px;
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-info {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.header-info i {
  font-size: 24px;
  color: #667eea;
  margin-top: 3px;
}

.header-info h4 {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.header-info p {
  margin: 0;
  font-size: 13px;
  color: #64748b;
  line-height: 1.4;
}

.analysis-card .card-content {
  padding: 30px;
}

/* 一致性检测样式 */
.standard-selector {
  margin-bottom: 25px;
}

.overall-score {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 25px;
  padding: 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
  border: 1px solid #e2e8f0;
}

.score-circle {
  position: relative;
  flex-shrink: 0;
}

.score-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.score-number {
  font-size: 22px;
  font-weight: 900;
  color: #1e293b;
  display: block;
  line-height: 1;
}

.score-unit {
  font-size: 12px;
  color: #64748b;
  font-weight: 600;
}

.score-label h5 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.score-label p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.detailed-results {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.result-item {
  padding: 18px;
  background: #fafbfc;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.dimension-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.score-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}

.score-excellent {
  background: #dcfce7;
  color: #166534;
}

.score-good {
  background: #fef3c7;
  color: #92400e;
}

.score-poor {
  background: #fecaca;
  color: #991b1b;
}

.progress-container {
  margin-bottom: 10px;
}

.result-suggestion {
  margin: 0;
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  display: flex;
  align-items: flex-start;
  gap: 6px;
}

.result-suggestion i {
  color: #fbbf24;
  margin-top: 2px;
  flex-shrink: 0;
}

.no-results {
  text-align: center;
  padding: 40px 20px;
  color: #64748b;
}

.no-results i {
  font-size: 48px;
  color: #cbd5e1;
  margin-bottom: 15px;
  display: block;
}

/* 复杂度评估样式 */
.complexity-overview {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  padding: 25px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
}

.complexity-gauge {
  text-align: center;
}

.gauge-container {
  position: relative;
  display: inline-block;
  margin-bottom: 15px;
}

.gauge-labels {
  display: flex;
  justify-content: space-between;
  position: absolute;
  top: 70px;
  left: 10px;
  right: 10px;
  font-size: 11px;
  color: #64748b;
  font-weight: 500;
}

.complexity-score {
  text-align: center;
}

.score-main {
  font-size: 32px;
  font-weight: 900;
  color: #1e293b;
  line-height: 1;
}

.score-total {
  font-size: 16px;
  color: #64748b;
  font-weight: 600;
}

.complexity-level {
  margin: 8px 0 0 0;
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
}

.complexity-dimensions {
  margin-bottom: 25px;
}

.complexity-dimensions h5 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 10px;
}

.dimension-grid {
  display: grid;
  gap: 15px;
}

.dimension-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 16px;
  background: #fafbfc;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
}

.dimension-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
  flex-shrink: 0;
}

.bloom-icon {
  background: linear-gradient(135deg, #409EFF 0%, #1e90ff 100%);
}

.quantity-icon {
  background: linear-gradient(135deg, #67C23A 0%, #52c41a 100%);
}

.depth-icon {
  background: linear-gradient(135deg, #E6A23C 0%, #fa8c16 100%);
}

.dimension-info {
  flex: 1;
}

.dimension-info h6 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.dimension-score {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.dimension-score span {
  font-size: 13px;
  font-weight: 700;
  color: #1e293b;
  min-width: 35px;
}

.mini-progress {
  flex: 1;
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.8s ease;
}

.dimension-info p {
  margin: 0;
  font-size: 12px;
  color: #64748b;
}

.complexity-suggestions h5 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 10px;
}

.suggestion-cards {
  display: grid;
  gap: 12px;
}

.suggestion-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 15px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: white;
}

.suggestion-card.bloom-improve {
  background: linear-gradient(135deg, #fef7cd 0%, #fef3c7 100%);
  border-color: #fbbf24;
}

.suggestion-card.quantity-improve {
  background: linear-gradient(135deg, #dcfce7 0%, #d1fae5 100%);
  border-color: #10b981;
}

.suggestion-card.depth-improve {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  border-color: #f87171;
}

.suggestion-card.complexity-good {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border-color: #3b82f6;
}

.suggestion-card i {
  font-size: 16px;
  margin-top: 2px;
  flex-shrink: 0;
}

.bloom-improve i { color: #d97706; }
.quantity-improve i { color: #059669; }
.depth-improve i { color: #dc2626; }
.complexity-good i { color: #2563eb; }

.suggestion-card p {
  margin: 0;
  font-size: 13px;
  color: #374151;
  line-height: 1.5;
  font-weight: 500;
}

/* 预览对话框 */
.preview-content {
  padding: 20px;
}

.preview-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.preview-section h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 16px;
}

.preview-section p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
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

.action-buttons .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.4);
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

/* 响应式设计优化 */
@media (max-width: 1200px) {
  .objective-builder {
    max-width: 100%;
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .bloom-selector-container {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .dimension-editors-horizontal {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }
  
  .target-card-horizontal {
    min-height: auto;
  }
  
  .card-content {
    height: auto;
  }
  
  .analysis-cards-grid {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .objective-builder {
    padding: 15px;
  }
  
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
  
  .header-text h3 {
    font-size: 20px;
  }
  
  .bloom-selector-container {
    grid-template-columns: 1fr;
    gap: 15px;
    padding: 15px;
  }
  
  .pyramid-level-compact {
    --level-offset: 0px !important;
  }
  
  .dimension-editors-horizontal {
    flex-direction: column;
    gap: 15px;
    padding: 15px;
  }
  
  .target-card-horizontal {
    min-height: auto;
  }
  
  .analysis-cards-grid {
    grid-template-columns: 1fr;
    gap: 15px;
    padding: 15px;
  }
  
  .analysis-card .card-header {
    padding: 20px 25px 15px;
  }
  
  .analysis-card .card-content {
    padding: 25px;
  }
  
  .overall-score {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .dimension-grid {
    gap: 12px;
  }
  
  .dimension-item {
    padding: 14px;
  }
  
  .bloom-keywords-panel {
    padding: 20px;
  }
  
  .action-buttons {
    padding: 24px 16px;
  }
  
  .action-buttons .el-button {
    min-width: 120px;
    margin: 8px;
  }
}

/* ========== 智能导入功能样式 - 现代化重设计 ========== */

.import-section {
  margin-bottom: 32px;
}

.import-methods-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
  padding: 30px;
}

.import-card {
  position: relative;
  background: #ffffff;
  border-radius: 20px;
  border: 1px solid #e8ecf0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  min-height: 360px;
  display: flex;
  flex-direction: column;
}

.import-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, 
    var(--card-gradient-start, #667eea) 0%, 
    var(--card-gradient-end, #764ba2) 100%);
  z-index: 1;
}

.import-card.document-import {
  --card-gradient-start: #667eea;
  --card-gradient-end: #764ba2;
}

.import-card.voice-import {
  --card-gradient-start: #06d6a0;
  --card-gradient-end: #118ab2;
}

.import-card.image-import {
  --card-gradient-start: #f72585;
  --card-gradient-end: #b5179e;
}



.import-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  border-color: var(--card-gradient-start);
}

.card-header-modern {
  padding: 24px 28px 20px;
  background: linear-gradient(135deg, #fafbff 0%, #f0f4ff 100%);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
}

.icon-section {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 26px;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.icon-wrapper::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, 
    var(--card-gradient-start) 0%, 
    var(--card-gradient-end) 100%);
  opacity: 0.9;
}

.icon-wrapper i {
  position: relative;
  z-index: 1;
}

.header-content h4 {
  margin: 0 0 6px 0;
  font-size: 18px;
  font-weight: 700;
  color: #1a202c;
  letter-spacing: -0.2px;
}

.header-content p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.4;
}

.card-badge {
  background: linear-gradient(135deg, 
    var(--card-gradient-start) 0%, 
    var(--card-gradient-end) 100%);
  color: white;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.card-body-modern {
  flex: 1;
  padding: 28px;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

.card-footer-modern {
  padding: 20px 28px 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-top: 1px solid #e8ecf0;
}

.action-button {
  width: 100%;
  height: 44px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 15px;
  border: none;
  background: linear-gradient(135deg, 
    var(--card-gradient-start) 0%, 
    var(--card-gradient-end) 100%) !important;
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

/* 文档导入样式 */
.upload-zone {
  border: 2px dashed #d1d5db;
  border-radius: 16px;
  padding: 40px 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  flex: 1;
  position: relative;
  overflow: hidden;
}

.upload-zone::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, 
    rgba(102, 126, 234, 0.03) 0%, 
    transparent 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.upload-zone:hover {
  border-color: var(--card-gradient-start);
  background: linear-gradient(135deg, #fafbff 0%, #f0f4ff 100%);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.upload-zone:hover::before {
  opacity: 1;
}

.upload-icon-area {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 50%;
  background: linear-gradient(135deg, 
    var(--card-gradient-start) 0%, 
    var(--card-gradient-end) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.upload-text .primary-text {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
  line-height: 1.4;
}

.upload-text .support-text {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.progress-section {
  margin-top: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 12px;
  border: 1px solid #bae6fd;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #0c4a6e;
}

/* 语音识别样式 */
.voice-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  flex: 1;
}

.record-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  border-radius: 16px;
  border: 1px solid #e5e7eb;
}

.record-btn-modern {
  position: relative;
  width: 88px;
  height: 88px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, 
    var(--card-gradient-start) 0%, 
    var(--card-gradient-end) 100%);
  color: white;
  font-size: 32px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 25px rgba(6, 214, 160, 0.3);
  overflow: hidden;
}

.record-btn-modern:hover:not(:disabled) {
  transform: scale(1.1);
  box-shadow: 0 12px 40px rgba(6, 214, 160, 0.4);
}

.record-btn-modern.recording {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  box-shadow: 0 6px 25px rgba(239, 68, 68, 0.4);
  animation: recordPulse 2s infinite;
}

.record-pulse {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  border: 2px solid #ef4444;
  border-radius: 50%;
  animation: pulse 2s infinite;
  opacity: 0.6;
}

@keyframes recordPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

@keyframes pulse {
  0% { transform: scale(0.9); opacity: 1; }
  100% { transform: scale(1.3); opacity: 0; }
}

/* 🔗 布卢姆层级变化高亮动画 */
@keyframes levelChangeHighlight {
  0% { 
    transform: translateY(-3px) scale(1.05);
    box-shadow: 0 15px 30px rgba(102, 126, 234, 0.3);
  }
  50% { 
    transform: translateY(-6px) scale(1.08);
    box-shadow: 0 20px 40px rgba(102, 126, 234, 0.5);
    border-color: #67C23A;
    background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  }
  100% { 
    transform: translateY(-3px) scale(1.05);
    box-shadow: 0 15px 30px rgba(102, 126, 234, 0.3);
  }
}

.record-btn-modern:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.record-info {
  text-align: center;
}

.status-text {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #374151;
}

.time-display {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid #fbbf24;
}

.voice-result {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border: 1px solid #7dd3fc;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(14, 165, 233, 0.1);
}

.result-header {
  margin-bottom: 12px;
}

.result-header h6 {
  margin: 0;
  font-size: 14px;
  font-weight: 700;
  color: #0c4a6e;
  display: flex;
  align-items: center;
  gap: 8px;
}

.voice-content {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
  border: 1px solid #e0f2fe;
  box-shadow: 0 2px 8px rgba(14, 165, 233, 0.05);
}

.result-actions {
  text-align: center;
}

/* 图片OCR样式 */
.image-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  flex: 1;
}

.upload-options-modern {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.option-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 16px;
  border: 2px dashed #d1d5db;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  position: relative;
  overflow: hidden;
}

.option-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    var(--card-gradient-start) 0%, 
    var(--card-gradient-end) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.option-card:hover {
  border-color: var(--card-gradient-start);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.option-card:hover::before {
  opacity: 0.05;
}

.option-card i {
  font-size: 28px;
  color: var(--card-gradient-start);
  position: relative;
  z-index: 1;
}

.option-card span {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  position: relative;
  z-index: 1;
}

.image-preview-modern {
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #e5e7eb;
}

.preview-container {
  position: relative;
  display: inline-block;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.preview-container img {
  max-width: 100%;
  max-height: 140px;
  border-radius: 12px;
  display: block;
}

.image-overlay {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.preview-container:hover .image-overlay {
  opacity: 1;
}

.ocr-result-modern {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border: 1px solid #7dd3fc;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(14, 165, 233, 0.1);
}

.ocr-content {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #374151;
  line-height: 1.6;
  border: 1px solid #e0f2fe;
  max-height: 120px;
  overflow-y: auto;
  box-shadow: 0 2px 8px rgba(14, 165, 233, 0.05);
}



/* 导入历史记录样式 */
.import-history {
  margin-top: 24px;
  padding: 24px 30px;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.import-history h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
}

.import-history h4 i {
  color: #667eea;
}

.history-actions-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.clear-all-btn {
  color: #f56c6c !important;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.clear-all-btn:hover {
  background: rgba(245, 108, 108, 0.1) !important;
  color: #f56c6c !important;
}

.history-list {
  display: grid;
  gap: 12px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.history-item:hover {
  background: linear-gradient(135deg, #eef1ff 0%, #e0e7ff 100%);
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.1);
}

.history-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
}

.history-info {
  flex: 1;
}

.history-info h6 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.history-info p {
  margin: 0;
  font-size: 12px;
  color: #64748b;
}

.history-actions {
  flex-shrink: 0;
  display: flex;
  gap: 8px;
  align-items: center;
}

.history-actions .el-button {
  padding: 6px 8px;
  font-size: 12px;
  border-radius: 6px;
}

.delete-btn {
  color: #f56c6c !important;
}

.delete-btn:hover {
  background: rgba(245, 108, 108, 0.1) !important;
  color: #f56c6c !important;
}

/* 响应式优化 - 智能导入现代化 */
@media (max-width: 1200px) {
  .import-methods-container {
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
    padding: 24px;
  }
  
  .import-card {
    min-height: 340px;
  }
  
  .card-header-modern {
    padding: 20px 24px 16px;
  }
  
  .card-body-modern {
    padding: 24px;
  }
  
  .card-footer-modern {
    padding: 16px 24px 20px;
  }
}

@media (max-width: 768px) {
  .import-methods-container {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .import-card {
    min-height: auto;
  }
  
  .card-header-modern {
    padding: 20px;
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .icon-section {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .card-badge {
    align-self: center;
  }
  
  .card-body-modern {
    padding: 20px;
  }
  
  .card-footer-modern {
    padding: 16px 20px 20px;
  }
  
  .upload-zone {
    padding: 32px 20px;
  }
  
  .upload-icon-area {
    width: 56px;
    height: 56px;
    font-size: 24px;
  }
  
  .upload-options-modern {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .option-card {
    padding: 20px;
  }
  
  .record-btn-modern {
    width: 76px;
    height: 76px;
    font-size: 28px;
  }
  
  .record-area {
    padding: 20px;
  }
  
  .import-history {
    padding: 20px;
    margin-top: 20px;
  }
  
  .history-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .history-actions-header {
    align-self: flex-end;
  }
  
  .history-item {
    padding: 16px;
    gap: 12px;
    flex-wrap: wrap;
  }
  
  .history-info {
    flex: 1;
    min-width: 150px;
  }
  
  .history-actions {
    flex-direction: column;
    gap: 6px;
    min-width: 60px;
  }
  
  .history-actions .el-button {
    padding: 4px 6px;
    font-size: 11px;
    min-width: 50px;
  }
  
  .history-icon {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
}
</style> 