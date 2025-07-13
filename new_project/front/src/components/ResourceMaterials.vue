<template>
  <div class="resource-materials">
    <!-- 优化后的页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>🎯 智能资源生成</h1>
      <p>基于AI技术的多模态教学资源智能生成平台</p>
    </div>
        <div class="header-actions">
          <el-button 
            type="success" 
            size="medium" 
            @click="showAIAssistant = !showAIAssistant"
            :icon="showAIAssistant ? 'el-icon-close' : 'el-icon-service'">
            {{ showAIAssistant ? '关闭助手' : 'AI助手' }}
          </el-button>
          <el-button 
            type="primary" 
            size="medium" 
            @click="startCollaboration"
            v-if="!collaborationActive">
            <i class="el-icon-user"></i>
            协作模式
          </el-button>
          </div>
          </div>
    </div>

    <!-- 快速启动栏 -->
    <div class="quick-start-bar">
      <div class="quick-actions">
        <div class="action-group">
          <span class="group-label">🚀 快速开始</span>
          <el-button-group>
            <el-button size="small" @click="quickStart('video')">
              <i class="el-icon-video-camera"></i>
              视频
          </el-button>
            <el-button size="small" @click="quickStart('mindmap')">
              <i class="el-icon-share"></i>
              思维导图
            </el-button>
            <el-button size="small" @click="quickStart('chart')">
              <i class="el-icon-data-line"></i>
              图表
            </el-button>
          </el-button-group>
        </div>
        
        <div class="action-group">
          <span class="group-label">📊 智能分析</span>
          <div class="mini-indicators">
            <div class="mini-indicator">
              <span>适应度</span>
              <div class="indicator-bar">
                <div class="indicator-fill" :style="{ width: learnerAdaptability + '%' }"></div>
              </div>
              <span class="indicator-value">{{ learnerAdaptability }}%</span>
              </div>
            <div class="mini-indicator">
              <span>关联度</span>
              <div class="indicator-bar">
                <div class="indicator-fill" :style="{ width: knowledgeRelevance + '%' }"></div>
            </div>
              <span class="indicator-value">{{ knowledgeRelevance }}%</span>
          </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主体内容区 -->
    <div class="main-workspace">
      <!-- 左侧输入面板 -->
      <div class="input-workspace">
        <!-- 输入区域 - 优化为卡片式布局 -->
        <div class="workspace-section">
          <div class="section-header">
            <i class="el-icon-edit"></i>
            <h3>内容输入</h3>
            <div class="section-actions">
              <el-dropdown @command="handleTemplateCommand">
                <el-button type="text" size="mini">
                  模板 <i class="el-icon-arrow-down"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="save">保存模板</el-dropdown-item>
                  <el-dropdown-item command="load">加载模板</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
          
          <div class="input-container">
            <el-tabs v-model="activeInputTab" class="input-tabs">
              <el-tab-pane name="text">
                <span slot="label">
                  <i class="el-icon-edit-outline"></i> 文本
                </span>
                <el-input
                  type="textarea"
                  :rows="6"
                  placeholder="详细描述您的教学内容、知识点要求、学习目标等...&#10;&#10;💡 试试输入：&#10;• 我需要数学二次函数的思维导图&#10;• 制作英语语法的流程图&#10;• 生成化学原子结构的插图&#10;• 创建学生成绩分析图表"
                  v-model="inputContent.text"
                  maxlength="2000"
                  show-word-limit
                  @input="handleInputChange">
                </el-input>
              </el-tab-pane>
              
              <el-tab-pane name="voice">
                <span slot="label">
                  <i class="el-icon-microphone"></i> 语音
                </span>
                <div class="voice-input-area">
                  <el-button 
                    :type="isRecording ? 'danger' : 'primary'" 
                    :icon="isRecording ? 'el-icon-video-pause' : 'el-icon-microphone'"
                    @click="toggleRecording"
                    size="medium"
                    class="voice-btn">
                    {{ isRecording ? '停止录音' : '开始录音' }}
                  </el-button>
                  <div v-if="voiceText" class="voice-result">
                    <div class="result-header">
                      <i class="el-icon-check"></i>
                      <span>识别结果</span>
                    </div>
                    <div class="result-content">{{ voiceText }}</div>
                  </div>
                </div>
              </el-tab-pane>
              
              <el-tab-pane name="upload">
                <span slot="label">
                  <i class="el-icon-upload"></i> 文件
                </span>
                <div class="upload-area">
                <el-upload
                  drag
                  action="#"
                  :auto-upload="false"
                    :on-change="handleFileUpload"
                    accept="image/*,.pdf,.doc,.docx,.ppt,.pptx"
                    class="upload-dragger">
                  <i class="el-icon-upload"></i>
                    <div class="el-upload__text">
                      将文件拖到此处，或<em>点击上传</em>
                    </div>
                    <div class="el-upload__tip" slot="tip">
                      支持图片、PDF、Word、PPT格式
                    </div>
                </el-upload>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>

        <!-- 资源类型选择 - 优化为网格卡片 -->
        <div class="workspace-section">
          <div class="section-header">
            <i class="el-icon-collection"></i>
            <h3>资源类型</h3>
            <div class="section-actions">
              <el-button-group size="mini">
                <el-button 
                  :type="viewMode === 'grid' ? 'primary' : ''" 
                  @click="viewMode = 'grid'"
                  icon="el-icon-s-grid">
                </el-button>
                <el-button 
                  :type="viewMode === 'list' ? 'primary' : ''" 
                  @click="viewMode = 'list'"
                  icon="el-icon-menu">
                </el-button>
              </el-button-group>
            </div>
          </div>
          
          <div :class="['resource-grid', `resource-grid-${viewMode}`]">
            <div 
              v-for="type in resourceTypes" 
              :key="type.value"
              :class="['resource-card', { active: selectedResourceType === type.value }]"
              @click="selectResourceType(type.value)">
              <div class="card-icon">
                <i :class="type.icon"></i>
              </div>
              <div class="card-content">
                <h4>{{ type.label }}</h4>
                <p>{{ type.desc }}</p>
              </div>
              <div v-if="selectedResourceType === type.value" class="card-selected">
                <i class="el-icon-check"></i>
              </div>
            </div>
          </div>
          </div>
          
        <!-- 生成配置 - 紧凑布局 -->
        <div class="workspace-section">
          <div class="section-header">
            <i class="el-icon-setting"></i>
            <h3>生成配置</h3>
            <div class="section-actions">
            <el-button 
              type="text" 
              size="mini" 
                @click="intelligentRecommend">
                <i class="el-icon-magic-stick"></i>
                智能推荐
            </el-button>
            </div>
          </div>
          
          <div class="config-grid">
            <div class="config-item">
              <label>学科</label>
              <el-select v-model="generateConfig.subject" size="small" placeholder="选择学科">
                  <el-option label="数学" value="math"></el-option>
                  <el-option label="语文" value="chinese"></el-option>
                  <el-option label="英语" value="english"></el-option>
                  <el-option label="物理" value="physics"></el-option>
                  <el-option label="化学" value="chemistry"></el-option>
                  <el-option label="生物" value="biology"></el-option>
                  <el-option label="历史" value="history"></el-option>
                  <el-option label="地理" value="geography"></el-option>
                  <el-option label="计算机" value="computer"></el-option>
                </el-select>
            </div>
              
            <div class="config-item">
              <label>年级</label>
              <el-select v-model="generateConfig.grade" size="small" placeholder="选择年级">
                  <el-option label="小学" value="primary"></el-option>
                  <el-option label="初中" value="middle"></el-option>
                  <el-option label="高中" value="high"></el-option>
                  <el-option label="大学" value="university"></el-option>
                </el-select>
            </div>
              
            <div class="config-item config-item-full">
              <label>难度等级</label>
                <el-slider
                  v-model="generateConfig.difficulty"
                  :min="1"
                  :max="5"
                  show-stops
                  show-tooltip
                  :format-tooltip="formatDifficultyTooltip">
                </el-slider>
            </div>
            
            <div class="config-item config-item-full">
              <label>素材风格</label>
              <el-radio-group v-model="generateConfig.style" size="small">
                <el-radio-button label="professional">专业</el-radio-button>
                <el-radio-button label="vivid">生动</el-radio-button>
                <el-radio-button label="simple">简约</el-radio-button>
                <el-radio-button label="creative">创意</el-radio-button>
                </el-radio-group>
              </div>
          </div>
          </div>
      </div>

      <!-- 右侧预览和控制区 -->
      <div class="preview-workspace">
        <!-- 生成控制区 -->
        <div class="generate-panel">
          <div class="generate-button-area">
          <el-button 
            type="primary" 
            size="large" 
            :loading="isGenerating"
            @click="startGenerate"
              :disabled="!canGenerate"
              class="main-generate-btn">
            <i class="el-icon-magic-stick"></i>
            {{ isGenerating ? '正在生成...' : '开始生成' }}
          </el-button>
          </div>
          
          <div class="generate-options">
            <el-radio-group v-model="generateMode" size="small">
              <el-radio-button label="standard">标准模式</el-radio-button>
              <el-radio-button label="creative">创意模式</el-radio-button>
              <el-radio-button label="adaptive">自适应</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <!-- 预览区域 -->
        <div class="preview-panel">
          <div class="preview-header">
            <div class="preview-title">
            <i class="el-icon-view"></i>
              <span>预览</span>
            </div>
            <div class="preview-status">
              <el-tag :type="generateStatusType" size="small">{{ generateStatusText }}</el-tag>
              <span v-if="isGenerating" class="progress-indicator">{{ generateProgress }}%</span>
            </div>
          </div>
          
          <div class="preview-content">
            <!-- 生成进度 -->
            <div v-if="isGenerating" class="generating-overlay">
              <div class="progress-circle">
                <el-progress 
                  type="circle" 
                  :percentage="generateProgress" 
                  :width="80"
                  :status="generateProgress === 100 ? 'success' : null">
                </el-progress>
              </div>
                <p class="progress-text">{{ currentGenerateStep }}</p>
              <div class="progress-details">
                <div class="quality-preview">
                  <div class="quality-item">
                    <span>准确性</span>
                    <div class="quality-bar">
                      <div class="quality-fill" :style="{ width: qualityMetrics.accuracy + '%' }"></div>
                </div>
              </div>
                  <div class="quality-item">
                    <span>美观度</span>
                    <div class="quality-bar">
                      <div class="quality-fill" :style="{ width: qualityMetrics.visual + '%' }"></div>
                  </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-else-if="!generatedContent" class="empty-state">
              <div class="empty-icon">
              <i class="el-icon-picture-outline"></i>
              </div>
              <h4>准备生成您的教学资源</h4>
              <p>请完成左侧配置，然后点击生成按钮</p>
              <div class="empty-tips">
                <div class="tip-item">
                  <i class="el-icon-info"></i>
                  <span>详细描述内容可获得更好效果</span>
            </div>
                <div class="tip-item">
                  <i class="el-icon-info"></i>
                  <span>可上传参考文件辅助生成</span>
                </div>
                </div>
              </div>
              
            <!-- 生成结果 -->
            <div v-else class="result-display">
              <div class="result-header">
                <h4>{{ generatedContent.name }}</h4>
                <div class="result-actions">
                  <el-button size="mini" @click="previewFullscreen">
                    <i class="el-icon-full-screen"></i>
                  </el-button>
                  <el-button size="mini" @click="regenerateContent">
                    <i class="el-icon-refresh"></i>
                  </el-button>
                  <el-button size="mini" type="primary" @click="downloadResult">
                    <i class="el-icon-download"></i>
                  </el-button>
                </div>
              </div>
              
              <div class="result-content">
                <!-- 根据类型显示不同预览 -->
                <div v-if="selectedResourceType === 'video'" class="media-preview">
                  <!-- 在线视频平台嵌入 -->
                  <iframe 
                    v-if="isYouTubeVideo(generatedContent.url)"
                    :src="generatedContent.url" 
                    width="100%" 
                    height="315"
                    frameborder="0" 
                    :allow="getVideoAllowAttributes(generatedContent.url)"
                    allowfullscreen
                    scrolling="no"
                    style="border-radius: 8px;">
                  </iframe>
                  <!-- 本地或其他视频 -->
                  <video 
                    v-else
                    controls 
                    :src="generatedContent.url" 
                    width="100%" 
                    style="border-radius: 8px;"
                    @error="handleVideoError">
                    <source :src="generatedContent.backupUrl" type="video/mp4" v-if="generatedContent.backupUrl">
                    您的浏览器不支持视频播放，请尝试使用现代浏览器
                  </video>
                </div>
              
                <div v-else-if="selectedResourceType === 'audio'" class="media-preview">
                  <audio controls :src="generatedContent.url" style="width: 100%">
                    您的浏览器不支持音频播放
                  </audio>
                    </div>
                
                <div v-else-if="selectedResourceType === 'illustration'" class="media-preview">
                  <img :src="generatedContent.url" alt="生成的插图" style="max-width: 100%">
              </div>
              
                <div v-else-if="selectedResourceType === 'mindmap'" class="media-preview">
                  <img :src="generatedContent.url" alt="生成的思维导图" style="max-width: 100%; height: auto;">
              </div>
              
                <div v-else-if="selectedResourceType === 'chart'" class="media-preview">
                  <img :src="generatedContent.url" alt="生成的图表" style="max-width: 100%; height: auto;">
              </div>
              
                <div v-else-if="selectedResourceType === 'diagram'" class="media-preview">
                  <img :src="generatedContent.url" alt="生成的流程图" style="max-width: 100%; height: auto;">
              </div>
              
              <div v-else class="document-preview">
                  <div class="preview-placeholder">
                    <i :class="getPreviewIcon()"></i>
                    <p>{{ getResourceTypeName() }}预览</p>
                </div>
              </div>
            </div>
              
              <div class="result-info">
                <div class="info-grid">
                  <div class="info-item">
                    <span class="info-label">创建时间</span>
                    <span class="info-value">{{ formatTime(generatedContent.createdAt) }}</span>
          </div>
                  <div class="info-item">
                    <span class="info-label">文件大小</span>
                    <span class="info-value">{{ generatedContent.fileSize }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">质量评分</span>
                    <el-rate v-model="qualityScore" disabled show-score text-color="#ff9900"></el-rate>
                  </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        <!-- 智能建议面板 -->
        <div v-if="suggestedMaterials.length > 0" class="suggestions-panel">
          <div class="panel-header">
            <i class="el-icon-lightbulb"></i>
            <span>智能建议</span>
          </div>
          <div class="suggestions-list">
            <div 
              v-for="material in suggestedMaterials.slice(0, 3)" 
              :key="material.id"
              class="suggestion-card"
              @click="useSuggestedMaterial(material)">
              <i :class="material.icon"></i>
              <div class="suggestion-info">
                <h5>{{ material.title }}</h5>
                <p>{{ material.description }}</p>
              </div>
              <el-button type="text" size="mini">使用</el-button>
            </div>
                </div>
              </div>

        <!-- 历史生成记录面板 -->
        <div class="resource-manager-panel">
          <div class="panel-header">
            <div class="header-left">
              <i class="el-icon-time"></i>
              <span>历史生成记录</span>
              <el-badge :value="savedResources.length" class="resource-count-badge" type="primary"></el-badge>
            </div>
            <div class="header-actions">
              <el-button-group size="mini">
                <el-button 
                  :type="resourceViewMode === 'grid' ? 'primary' : ''" 
                  @click="resourceViewMode = 'grid'"
                  icon="el-icon-s-grid">
                </el-button>
                <el-button 
                  :type="resourceViewMode === 'list' ? 'primary' : ''" 
                  @click="resourceViewMode = 'list'"
                  icon="el-icon-menu">
                </el-button>
              </el-button-group>
              <el-dropdown @command="handleHistoryCommand" trigger="click">
                <el-button type="text" size="mini">
                  更多 <i class="el-icon-arrow-down"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="export">导出记录</el-dropdown-item>
                  <el-dropdown-item command="cleanup">清理记录</el-dropdown-item>
                  <el-dropdown-item command="stats">生成统计</el-dropdown-item>
                  <el-dropdown-item command="backup">备份历史</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>

          <!-- 历史记录筛选和搜索 -->
          <div class="resource-filters">
            <div class="filter-row">
              <div class="search-box">
                <el-input
                  v-model="resourceSearchQuery"
                  placeholder="搜索历史记录、资源名称、描述..."
                  prefix-icon="el-icon-search"
                  size="small"
                  clearable
                  @input="filterResources">
                </el-input>
              </div>
              <el-button 
                type="text" 
                size="small" 
                @click="showAdvancedFilter = !showAdvancedFilter">
                <i class="el-icon-s-operation"></i>
                高级筛选
              </el-button>
            </div>

                         <!-- 高级筛选面板 -->
             <div v-if="showAdvancedFilter" class="advanced-filters">
               <div class="filter-grid">
                 <div class="filter-item">
                   <label>生成类型</label>
                   <el-select v-model="resourceFilter.type" size="small" placeholder="全部类型" @change="filterResources">
                     <el-option label="全部类型" value=""></el-option>
                     <el-option v-for="type in resourceTypes" :key="type.value" :label="type.label" :value="type.value"></el-option>
                   </el-select>
                 </div>
                 <div class="filter-item">
                   <label>学科</label>
                   <el-select v-model="resourceFilter.subject" size="small" placeholder="全部学科" @change="filterResources">
                     <el-option label="全部学科" value=""></el-option>
                     <el-option label="数学" value="math"></el-option>
                     <el-option label="语文" value="chinese"></el-option>
                     <el-option label="英语" value="english"></el-option>
                     <el-option label="物理" value="physics"></el-option>
                     <el-option label="化学" value="chemistry"></el-option>
                     <el-option label="生物" value="biology"></el-option>
                   </el-select>
                 </div>
                 <div class="filter-item">
                   <label>年级</label>
                   <el-select v-model="resourceFilter.grade" size="small" placeholder="全部年级" @change="filterResources">
                     <el-option label="全部年级" value=""></el-option>
                     <el-option label="小学" value="primary"></el-option>
                     <el-option label="初中" value="middle"></el-option>
                     <el-option label="高中" value="high"></el-option>
                     <el-option label="大学" value="university"></el-option>
                   </el-select>
                 </div>
                 <div class="filter-item">
                   <label>生成质量</label>
                   <el-select v-model="resourceFilter.quality" size="small" placeholder="全部等级" @change="filterResources">
                     <el-option label="全部等级" value=""></el-option>
                     <el-option label="优秀 (4.5+)" value="excellent"></el-option>
                     <el-option label="良好 (4.0+)" value="good"></el-option>
                     <el-option label="一般 (3.0+)" value="average"></el-option>
                   </el-select>
                 </div>
                 <div class="filter-item">
                   <label>生成时间</label>
                   <el-select v-model="resourceFilter.timeRange" size="small" placeholder="全部时间" @change="filterResources">
                     <el-option label="全部时间" value=""></el-option>
                     <el-option label="今天" value="today"></el-option>
                     <el-option label="本周" value="week"></el-option>
                     <el-option label="本月" value="month"></el-option>
                     <el-option label="本年" value="year"></el-option>
                   </el-select>
                 </div>
                 <div class="filter-item">
                   <label>标签</label>
                   <el-select v-model="resourceFilter.tags" size="small" placeholder="选择标签" multiple @change="filterResources">
                     <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag"></el-option>
                   </el-select>
                 </div>
               </div>
               <div class="filter-actions">
                 <el-button size="small" @click="resetFilters">重置筛选</el-button>
                 <el-button type="primary" size="small" @click="applyFilters">应用筛选</el-button>
               </div>
             </div>
          </div>

          <!-- 历史记录列表 -->
          <div class="resource-list-container">
            <!-- 排序和统计信息 -->
            <div class="list-toolbar">
              <div class="sort-options">
                <span class="sort-label">排序：</span>
                <el-radio-group v-model="resourceSortBy" size="mini" @change="sortResources">
                  <el-radio-button label="createTime">生成时间</el-radio-button>
                  <el-radio-button label="quality">生成质量</el-radio-button>
                  <el-radio-button label="usage">使用频率</el-radio-button>
                  <el-radio-button label="name">资源名称</el-radio-button>
                </el-radio-group>
                <el-button-group size="mini">
                  <el-button 
                    :type="resourceSortOrder === 'desc' ? 'primary' : ''" 
                    @click="resourceSortOrder = 'desc'; sortResources()"
                    icon="el-icon-sort-down">
                  </el-button>
                  <el-button 
                    :type="resourceSortOrder === 'asc' ? 'primary' : ''" 
                    @click="resourceSortOrder = 'asc'; sortResources()"
                    icon="el-icon-sort-up">
                  </el-button>
                </el-button-group>
              </div>
              <div class="list-stats">
                <span class="stats-text">共 {{ filteredResources.length }} 条生成记录</span>
                <span v-if="resourceSearchQuery" class="search-stats">
                  搜索到 {{ filteredResources.length }} / {{ savedResources.length }} 个结果
                </span>
              </div>
            </div>

            <!-- 资源网格/列表 -->
            <div :class="['resource-list', `resource-list-${resourceViewMode}`]" v-if="filteredResources.length > 0">
              <div 
                v-for="resource in paginatedResources" 
                :key="resource.id"
                :class="['resource-item', `resource-item-${resourceViewMode}`]"
                @click="previewResource(resource)">
                
                <!-- 网格视图 -->
                <template v-if="resourceViewMode === 'grid'">
                  <div class="resource-thumbnail">
                    <img v-if="resource.thumbnail" :src="resource.thumbnail" :alt="resource.name" style="width: 100%; height: 100%; object-fit: cover;">
                    <div v-else class="thumbnail-placeholder">
                      <i :class="getResourceIcon(resource.type)"></i>
                    </div>
                    <!-- 视频播放图标 -->
                    <div v-if="resource.type === 'video'" class="video-play-overlay">
                      <i class="el-icon-video-play"></i>
                    </div>
                    <div class="resource-overlay">
                      <div class="overlay-actions">
                        <el-button type="primary" icon="el-icon-view" size="mini" circle @click.stop="previewResource(resource)"></el-button>
                        <el-button type="success" icon="el-icon-download" size="mini" circle @click.stop="downloadResource(resource)"></el-button>
                        <el-button type="warning" icon="el-icon-edit" size="mini" circle @click.stop="editResource(resource)"></el-button>
                      </div>
                    </div>
                    <!-- 时效性标记 -->
                    <div v-if="isResourceExpiring(resource)" class="expiry-badge">
                      <i class="el-icon-warning"></i>
                      即将过期
                    </div>
                    <!-- 质量标记 -->
                    <div class="quality-badge" :class="getQualityClass(resource.quality)">
                      <i class="el-icon-star-on"></i>
                      {{ resource.quality.toFixed(1) }}
                    </div>
                  </div>
                  <div class="resource-info">
                    <h4 class="resource-title" :title="resource.name">{{ resource.name }}</h4>
                    <div class="resource-meta">
                      <el-tag size="mini" :type="getTypeTagType(resource.type)">{{ getResourceTypeName(resource.type) }}</el-tag>
                      <span class="resource-subject">{{ getSubjectName(resource.subject) }}</span>
                    </div>
                    <div class="resource-stats">
                      <span class="stat-item">
                        <i class="el-icon-time"></i>
                        {{ formatRelativeTime(resource.createdAt) }}
                      </span>
                      <span class="stat-item">
                        <i class="el-icon-view"></i>
                        {{ resource.viewCount || 0 }}次
                      </span>
                    </div>
                    <div v-if="resource.tags && resource.tags.length > 0" class="resource-tags">
                      <el-tag v-for="tag in resource.tags.slice(0, 2)" :key="tag" size="mini" effect="plain">{{ tag }}</el-tag>
                      <span v-if="resource.tags.length > 2" class="more-tags">+{{ resource.tags.length - 2 }}</span>
                    </div>
                  </div>
                </template>

                <!-- 列表视图 -->
                <template v-else>
                  <div class="resource-icon">
                    <i :class="getResourceIcon(resource.type)"></i>
                  </div>
                  <div class="resource-content">
                    <div class="resource-header">
                      <h4 class="resource-title">{{ resource.name }}</h4>
                      <div class="resource-badges">
                        <el-tag size="mini" :type="getTypeTagType(resource.type)">{{ getResourceTypeName(resource.type) }}</el-tag>
                        <span class="quality-score" :class="getQualityClass(resource.quality)">
                          <i class="el-icon-star-on"></i>
                          {{ resource.quality.toFixed(1) }}
                        </span>
                      </div>
                    </div>
                    <div class="resource-description">{{ resource.description || '暂无描述' }}</div>
                    <div class="resource-footer">
                      <div class="resource-meta">
                        <span class="meta-item">
                          <i class="el-icon-user"></i>
                          {{ resource.creator || '系统生成' }}
                        </span>
                        <span class="meta-item">
                          <i class="el-icon-time"></i>
                          {{ formatTime(resource.createdAt) }}
                        </span>
                        <span class="meta-item">
                          <i class="el-icon-view"></i>
                          {{ resource.viewCount || 0 }}次使用
                        </span>
                        <span v-if="isResourceExpiring(resource)" class="meta-item expiring">
                          <i class="el-icon-warning"></i>
                          即将过期
                        </span>
                      </div>
                      <div class="resource-actions">
                                                 <el-button type="text" size="mini" @click.stop="previewResource(resource)">
                           <i class="el-icon-view"></i>
                           查看详情
                         </el-button>
                         <el-button type="text" size="mini" @click.stop="useResource(resource)">
                           <i class="el-icon-refresh-right"></i>
                           重新生成
                         </el-button>
                         <el-button type="text" size="mini" @click.stop="downloadResource(resource)">
                           <i class="el-icon-download"></i>
                           下载
                         </el-button>
                         <el-dropdown @command="(cmd) => handleResourceAction(cmd, resource)" trigger="click">
                           <el-button type="text" size="mini">
                             <i class="el-icon-more"></i>
                           </el-button>
                           <el-dropdown-menu slot="dropdown">
                             <el-dropdown-item command="edit">编辑记录</el-dropdown-item>
                             <el-dropdown-item command="share">分享生成</el-dropdown-item>
                             <el-dropdown-item command="duplicate">复制配置</el-dropdown-item>
                             <el-dropdown-item command="export">导出文件</el-dropdown-item>
                             <el-dropdown-item command="delete" divided>删除记录</el-dropdown-item>
                           </el-dropdown-menu>
                         </el-dropdown>
                      </div>
                    </div>
                  </div>
                </template>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-else class="resource-empty-state">
              <div class="empty-icon">
                <i class="el-icon-time"></i>
              </div>
              <h4>{{ resourceSearchQuery ? '未找到匹配的生成记录' : '暂无生成历史' }}</h4>
              <p>{{ resourceSearchQuery ? '尝试调整搜索条件或筛选器' : '开始生成您的第一个教学资源吧！' }}</p>
              <div class="empty-actions">
                <el-button v-if="resourceSearchQuery" @click="clearSearch" size="small">
                  <i class="el-icon-refresh-left"></i>
                  清除搜索
                </el-button>
                <el-button v-else type="primary" @click="scrollToGenerate" size="small">
                  <i class="el-icon-plus"></i>
                  开始生成
                </el-button>
              </div>
            </div>

            <!-- 分页 -->
            <div v-if="filteredResources.length > resourcePageSize" class="resource-pagination">
              <el-pagination
                @current-change="handleResourcePageChange"
                :current-page="resourceCurrentPage"
                :page-size="resourcePageSize"
                :total="filteredResources.length"
                layout="total, prev, pager, next, jumper"
                small>
              </el-pagination>
            </div>
          </div>
        </div>
            </div>
            </div>

    <!-- AI助手侧边栏 -->
    <transition name="slide-right">
      <div v-if="showAIAssistant" class="ai-sidebar">
        <div class="sidebar-header">
          <div class="assistant-info">
            <i class="el-icon-service"></i>
            <div>
              <h4>AI教学助手</h4>
              <p>{{ assistantStatus }}</p>
          </div>
          </div>
          <el-button type="text" @click="showAIAssistant = false">
            <i class="el-icon-close"></i>
            </el-button>
          </div>
          
        <div class="chat-area">
          <div class="chat-messages" ref="chatMessages">
            <div 
              v-for="message in chatMessages" 
              :key="message.id"
              :class="['chat-message', message.type]">
              <div class="message-avatar">
                <i :class="message.type === 'user' ? 'el-icon-user' : 'el-icon-service'"></i>
            </div>
              <div class="message-bubble">
                <div class="message-text">{{ message.text }}</div>
                <div class="message-time">{{ formatTime(message.timestamp) }}</div>
                </div>
              </div>
            </div>
            
          <div class="chat-input">
            <el-input
              v-model="currentMessage"
              placeholder="询问AI助手..."
              @keyup.enter.native="sendMessage"
              :disabled="isAIThinking">
              <el-button 
                slot="append" 
                @click="sendMessage"
                :loading="isAIThinking"
                type="primary">
                发送
                  </el-button>
            </el-input>
            </div>
          </div>
      </div>
    </transition>

    <!-- 协作状态栏 -->
    <div v-if="collaborationActive" class="collaboration-bar">
      <div class="collab-info">
        <i class="el-icon-user"></i>
        <span>协作模式 ({{ onlineUsers.length }}人在线)</span>
                </div>
      <div class="collab-users">
        <el-avatar 
          v-for="user in onlineUsers.slice(0, 3)" 
          :key="user.id"
          :size="24" 
          :title="user.name">
          {{ user.name.charAt(0) }}
        </el-avatar>
        <span v-if="onlineUsers.length > 3" class="more-users">+{{ onlineUsers.length - 3 }}</span>
              </div>
      <el-button type="text" size="mini" @click="collaborationActive = false">
        退出协作
      </el-button>
    </div>

    <!-- 历史记录详情对话框 -->
    <el-dialog 
      :visible.sync="resourcePreviewDialog" 
      :title="`生成记录详情: ${previewingResource ? previewingResource.name : ''}`"
      width="90%"
      top="5vh"
      :before-close="handlePreviewClose"
      class="resource-preview-dialog">
      
      <div v-if="previewingResource" class="resource-preview-container">
        <!-- 资源信息头部 -->
        <div class="preview-header-info">
          <div class="resource-basic-info">
            <div class="resource-icon-large">
              <i :class="getResourceIcon(previewingResource.type)"></i>
            </div>
            <div class="resource-details">
              <h2>{{ previewingResource.name }}</h2>
              <div class="resource-meta-row">
                <el-tag :type="getTypeTagType(previewingResource.type)" size="medium">
                  {{ getResourceTypeName(previewingResource.type) }}
                </el-tag>
                <span class="meta-divider">|</span>
                <span class="meta-subject">{{ getSubjectName(previewingResource.subject) }}</span>
                <span class="meta-divider">|</span>
                <span class="meta-grade">{{ getGradeName(previewingResource.grade) }}</span>
                <span class="meta-divider">|</span>
                <div class="quality-display" :class="getQualityClass(previewingResource.quality)">
                  <i class="el-icon-star-on"></i>
                  <span>{{ previewingResource.quality.toFixed(1) }}</span>
                </div>
              </div>
              <p class="resource-description">{{ previewingResource.description || '暂无描述' }}</p>
              <div class="resource-tags-preview">
                <el-tag 
                  v-for="tag in previewingResource.tags" 
                  :key="tag" 
                  size="small" 
                  effect="plain"
                  class="tag-item">
                  {{ tag }}
                </el-tag>
              </div>
            </div>
          </div>
          
                     <!-- 历史记录操作按钮 -->
           <div class="preview-actions">
             <el-button type="primary" @click="useResource(previewingResource)">
               <i class="el-icon-refresh-right"></i>
               重新生成
             </el-button>
             <el-button @click="downloadResource(previewingResource)">
               <i class="el-icon-download"></i>
               下载
             </el-button>
             <el-dropdown @command="(cmd) => handleResourceAction(cmd, previewingResource)" trigger="click">
               <el-button>
                 更多操作 <i class="el-icon-arrow-down"></i>
               </el-button>
               <el-dropdown-menu slot="dropdown">
                 <el-dropdown-item command="edit">编辑记录</el-dropdown-item>
                 <el-dropdown-item command="share">分享生成</el-dropdown-item>
                 <el-dropdown-item command="duplicate">复制配置</el-dropdown-item>
                 <el-dropdown-item command="export">导出文件</el-dropdown-item>
               </el-dropdown-menu>
             </el-dropdown>
           </div>
        </div>

        <!-- 资源预览内容 -->
        <div class="preview-content-area">
          <div class="preview-main">
            <!-- 根据资源类型显示不同预览 -->
            <div v-if="previewingResource.type === 'video'" class="media-preview-large">
              <div class="video-container">
                <!-- 在线视频平台嵌入 -->
                <iframe 
                  v-if="isYouTubeVideo(previewingResource.url)"
                  :src="previewingResource.url" 
                  width="100%" 
                  height="450"
                  frameborder="0" 
                  :allow="getVideoAllowAttributes(previewingResource.url)"
                  allowfullscreen
                  scrolling="no"
                  style="border-radius: 8px;">
                </iframe>
                <!-- 本地或其他视频 -->
                <video 
                  v-else
                  controls 
                  :src="previewingResource.url" 
                  width="100%" 
                  style="max-height: 400px; border-radius: 8px;"
                  @error="handleVideoError">
                  <source :src="previewingResource.backupUrl" type="video/mp4" v-if="previewingResource.backupUrl">
                  您的浏览器不支持视频播放，请尝试使用现代浏览器
                </video>
                
                <!-- 视频信息面板 -->
                <div v-if="previewingResource.videoMetadata" class="video-info-panel">
                                     <div class="video-header">
                     <h4>{{ previewingResource.videoMetadata.title }}</h4>
                     <div class="video-meta">
                       <span class="platform" v-if="previewingResource.videoMetadata.platform">
                         <i class="el-icon-link"></i>
                         {{ previewingResource.videoMetadata.platform }}
                         <span class="platform-badge">🇨🇳</span>
                       </span>
                       <span class="duration">
                         <i class="el-icon-time"></i>
                         {{ previewingResource.duration }}
                       </span>
                       <span class="resolution">
                         <i class="el-icon-monitor"></i>
                         {{ previewingResource.resolution }}
                       </span>
                     </div>
                   </div>
                  
                  <div v-if="previewingResource.videoMetadata.chapters" class="video-chapters">
                    <h5>章节目录</h5>
                    <div class="chapters-list">
                      <div 
                        v-for="(chapter, index) in previewingResource.videoMetadata.chapters" 
                        :key="index"
                        class="chapter-item">
                        <span class="chapter-time">{{ chapter.time }}</span>
                        <span class="chapter-title">{{ chapter.title }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-else-if="previewingResource.type === 'audio'" class="media-preview-large">
              <div class="audio-player-container">
                <audio controls :src="previewingResource.url" style="width: 100%;">
                  您的浏览器不支持音频播放
                </audio>
                <div class="audio-visualizer">
                  <div class="wave-placeholder">
                    <i class="el-icon-headset"></i>
                    <p>音频波形图</p>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-else-if="previewingResource.type === 'mindmap'" class="media-preview-large mindmap-preview-container">
              <div class="mindmap-info">
                <div class="info-item">
                  <span class="label">布局方式:</span>
                  <span class="value">{{ getMindmapLayoutName(getMindmapData(previewingResource).layout) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">主题色彩:</span>
                  <span class="value">{{ getMindmapThemeName(getMindmapData(previewingResource).theme) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">分支数量:</span>
                  <span class="value">{{ getMindmapData(previewingResource).branches.length }}</span>
                </div>
              </div>
              
              <div class="mindmap-visualization" :class="`theme-${getMindmapData(previewingResource).theme} layout-${getMindmapData(previewingResource).layout}`">
                <div class="center-node">
                  <i class="el-icon-star-on"></i>
                  {{ getMindmapData(previewingResource).centerTopic || '中心主题' }}
                </div>
                <div class="branches" :class="`layout-${getMindmapData(previewingResource).layout}`">
                  <div 
                    v-for="(branch, index) in getMindmapData(previewingResource).branches" 
                    :key="index" 
                    class="branch-node"
                    :class="getBranchClass(branch.text)"
                    :style="{ animationDelay: `${index * 0.1}s` }">
                    <div class="branch-main">
                      <i :class="getBranchIcon(branch.text)"></i>
                      {{ branch.text || `分支 ${index + 1}` }}
                    </div>
                    <!-- 显示子节点 -->
                    <div v-if="branch.children && branch.children.length > 0" class="branch-children">
                      <div 
                        v-for="(child, childIndex) in branch.children" 
                        :key="childIndex" 
                        class="child-node"
                        :style="{ animationDelay: `${(index * 0.1) + (childIndex * 0.05)}s` }">
                        <i :class="getChildIcon(child)"></i>
                        {{ child }}
                      </div>
                    </div>
                  </div>
                </div>
                <div v-if="getMindmapData(previewingResource).branches.length === 0" class="empty-branches">
                  <i class="el-icon-plus"></i>
                  <span>添加分支内容</span>
                </div>
              </div>
            </div>
            
            <div v-else-if="previewingResource.type === 'chart'" class="media-preview-large">
              <div class="chart-viewer">
                <img :src="generateChartSVG(previewingResource.content || previewingResource.name)" alt="图表预览" style="max-width: 100%; height: auto;">
              </div>
            </div>
            
            <div v-else-if="previewingResource.type === 'illustration'" class="media-preview-large">
              <div class="illustration-viewer">
                <img :src="generateIllustrationSVG(previewingResource.content || previewingResource.name)" alt="插图预览" style="max-width: 100%; height: auto;">
              </div>
            </div>
            
            <div v-else class="document-preview-large">
              <div class="document-placeholder">
                <i :class="getResourceIcon(previewingResource.type)"></i>
                <h4>{{ getResourceTypeName(previewingResource.type) }}预览</h4>
                <p>点击下载按钮获取完整文件</p>
              </div>
            </div>
          </div>

          <!-- 资源详细信息侧边栏 -->
          <div class="preview-sidebar">
            <!-- 生成信息 -->
            <div class="info-section">
              <h4><i class="el-icon-info"></i> 生成信息</h4>
              <div class="info-grid">
                <div class="info-row">
                  <span class="info-label">生成者：</span>
                  <span class="info-value">{{ previewingResource.creator || '系统生成' }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">生成时间：</span>
                  <span class="info-value">{{ formatTime(previewingResource.createdAt) }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">最后查看：</span>
                  <span class="info-value">{{ formatTime(previewingResource.updatedAt) }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">文件大小：</span>
                  <span class="info-value">{{ previewingResource.fileSize }}</span>
                </div>
                <div v-if="previewingResource.duration" class="info-row">
                  <span class="info-label">视频时长：</span>
                  <span class="info-value">{{ previewingResource.duration }}</span>
                </div>
                <div v-if="previewingResource.resolution" class="info-row">
                  <span class="info-label">分辨率：</span>
                  <span class="info-value">{{ previewingResource.resolution }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">查看次数：</span>
                  <span class="info-value">{{ previewingResource.viewCount || 0 }}次</span>
                </div>
                <div class="info-row">
                  <span class="info-label">最后使用：</span>
                  <span class="info-value">{{ formatRelativeTime(previewingResource.lastUsed) }}</span>
                </div>
              </div>
            </div>

            <!-- 生成质量分析 -->
            <div class="info-section">
              <h4><i class="el-icon-data-analysis"></i> 生成质量分析</h4>
              <div class="quality-analysis">
                <div class="quality-meter">
                  <div class="meter-label">整体评分</div>
                  <el-progress 
                    type="circle" 
                    :percentage="previewingResource.quality * 20" 
                    :width="80"
                    :status="previewingResource.quality >= 4.5 ? 'success' : previewingResource.quality >= 4.0 ? '' : 'warning'">
                  </el-progress>
                  <div class="quality-text">{{ previewingResource.quality.toFixed(1) }}/5.0</div>
                </div>
                <div class="quality-metrics">
                  <div class="metric-item">
                    <span class="metric-label">准确性</span>
                    <div class="metric-bar">
                      <div class="metric-fill" :style="{ width: previewingResource.accuracy + '%' }"></div>
                    </div>
                    <span class="metric-value">{{ previewingResource.accuracy }}%</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 时效性信息 -->
            <div class="info-section">
              <h4><i class="el-icon-time"></i> 时效性</h4>
              <div class="expiry-info">
                <div v-if="isResourceExpiring(previewingResource)" class="expiry-warning">
                  <i class="el-icon-warning"></i>
                  <span>资源将在 {{ getDaysUntilExpiry(previewingResource) }} 天后过期</span>
                </div>
                <div v-else class="expiry-normal">
                  <i class="el-icon-circle-check"></i>
                  <span>资源有效期至 {{ formatDate(previewingResource.expiryDate) }}</span>
                </div>
              </div>
            </div>

            <!-- 相关资源推荐 -->
            <div class="info-section">
              <h4><i class="el-icon-connection"></i> 相关资源</h4>
              <div class="related-resources">
                <div 
                  v-for="related in getRelatedResources(previewingResource)" 
                  :key="related.id"
                  class="related-item"
                  @click="previewResource(related)">
                  <i :class="getResourceIcon(related.type)"></i>
                  <div class="related-info">
                    <div class="related-name">{{ related.name }}</div>
                    <div class="related-meta">{{ getResourceTypeName(related.type) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <!-- 资源编辑对话框 -->
    <el-dialog 
      title="编辑资源" 
      :visible.sync="resourceEditorDialog" 
      width="95%" 
      :before-close="handleEditorClose"
      custom-class="resource-editor-dialog">
      <ResourceEditor
        v-if="editingResource && resourceEditorDialog"
        :resource="editingResource"
        :visible="resourceEditorDialog"
        @close="handleEditorClose"
        @save-success="handleEditorSave">
      </ResourceEditor>
    </el-dialog>
  </div>
</template>

<script>
import ResourceEditor from './ResourceEditor.vue'

export default {
  name: 'ResourceMaterials',
  components: {
    ResourceEditor
  },
  data() {
    return {
      activeInputTab: 'text',
      inputContent: {
        text: '',
        voice: '',
        image: null,
        document: null
      },
      isRecording: false,
      voiceText: '',
      selectedResourceType: '',
      viewMode: 'grid', // 默认使用网格视图显示更多内容
      resourceTypes: [
        { value: 'video', label: '教学视频', icon: 'el-icon-video-camera', desc: '生成高质量教学视频' },
        { value: 'audio', label: '音频解说', icon: 'el-icon-headset', desc: '智能语音解说制作' },
        { value: 'animation', label: '互动动画', icon: 'el-icon-picture', desc: '趣味动画教学内容' },
        { value: 'chart', label: '数据图表', icon: 'el-icon-data-line', desc: '可视化数据图表' },
        { value: 'mindmap', label: '思维导图', icon: 'el-icon-share', desc: '知识结构化梳理' },
        { value: 'diagram', label: '流程图示', icon: 'el-icon-guide', desc: '流程步骤可视化' },
        { value: 'case', label: '案例素材', icon: 'el-icon-document', desc: '真实案例教学' },
        { value: 'illustration', label: '教学插图', icon: 'el-icon-picture-outline', desc: '精美教学配图' }
      ],
      generateConfig: {
        subject: '',
        grade: '',
        difficulty: 3,
        style: 'professional',
        outputFormat: '',
        duration: 120,
        resolution: '1080p',
        colorTheme: 'blue',
        fontSize: 'medium'
      },
      showAdvancedConfig: false,
      isGenerating: false,
      generateProgress: 0,
      currentGenerateStep: '',
      estimatedTime: 0,
      qualityMetrics: {
        accuracy: 0,
        visual: 0,
        educational: 0,
        innovation: 0
      },
      generatedContent: null,
      qualityScore: 0,
      suggestedMaterials: [],
      // AI助手相关
      showAIAssistant: false,
      assistantStatus: '准备为您提供专业建议',
      chatMessages: [
        {
          id: 1,
          type: 'assistant',
          text: '您好！我是AI教学助手，可以帮助您优化资源生成。请告诉我您想要创建什么类型的教学资源？',
          timestamp: new Date()
        }
      ],
      currentMessage: '',
      isAIThinking: false,
      // 创新功能相关
      generateMode: 'standard',
      learnerAdaptability: 0,
      knowledgeRelevance: 0,
      // 版本管理
      showVersionHistory: false,
      currentVersionId: 1,
      versionHistory: [],
      // 实时协作
      collaborationActive: false,
      onlineUsers: [
        { id: 1, name: '张老师', avatar: '', status: 'online' },
        { id: 2, name: '李老师', avatar: '', status: 'editing' },
        { id: 3, name: '王老师', avatar: '', status: 'viewing' }
      ],
      recentActivities: [
        { id: 1, user: '张老师', action: '修改了视频配置', icon: 'el-icon-edit', timestamp: new Date() },
        { id: 2, user: '李老师', action: '添加了标签', icon: 'el-icon-price-tag', timestamp: new Date() },
        { id: 3, user: '王老师', action: '发表了评论', icon: 'el-icon-chat-dot-square', timestamp: new Date() }
      ],
      // 3D预览
      show3DPreview: false,
      // 增强质量分析
      improvementSuggestions: [
        { id: 1, text: '建议增加更多互动元素以提高学生参与度', icon: 'el-icon-mouse', priority: 'high' },
        { id: 2, text: '可以添加字幕提高可访问性', icon: 'el-icon-document', priority: 'medium' },
        { id: 3, text: '考虑增加多语言支持', icon: 'el-icon-message-solid', priority: 'low' }
      ],
      
      // 资源管理相关数据
      resourceViewMode: 'grid', // 'grid' | 'list'
      resourceSearchQuery: '',
      showAdvancedFilter: false,
      resourceFilter: {
        type: '',
        subject: '',
        grade: '',
        quality: '',
        timeRange: '',
        tags: []
      },
      resourceSortBy: 'createTime',
      resourceSortOrder: 'desc',
      resourceCurrentPage: 1,
      resourcePageSize: 12,
      filteredResources: [],
      
      // 可用标签
      availableTags: [
        '思维导图', '数学', '函数', '几何', '代数',
        '语文', '作文', '阅读理解', '古诗词',
        '英语', '语法', '词汇', '听力',
        '物理', '力学', '电路', '光学',
        '化学', '有机化学', '无机化学', '实验',
        '生物', '细胞', '遗传', '生态',
        '历史', '古代史', '近代史', '世界史',
        '地理', '自然地理', '人文地理',
        '互动', '动画', '图表', '流程图'
      ],
      
      // 保存的资源列表
      savedResources: [
        {
          id: 1,
          name: '二次函数思维导图',
          type: 'mindmap',
          subject: 'math',
          grade: 'high',
          description: '详细展示二次函数的概念、性质、图像特征和应用的思维导图',
          thumbnail: null,
          url: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCI+PC9zdmc+',
          quality: 4.6,
          viewCount: 23,
          createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
          updatedAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
          creator: '张老师',
          tags: ['数学', '函数', '思维导图', '高中'],
          fileSize: '2.3MB',
          expiryDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // 30天后过期
          accuracy: 95,
          lastUsed: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
          content: '二次函数 y = ax² + bx + c 的基本形式、图像特征、性质和应用',
          metadata: {
            centerTopic: '二次函数 y = ax² + bx + c (a≠0)',
            branches: [
              { 
                text: '解析式与基本概念', 
                children: [
                  '一般式：y = ax² + bx + c (a≠0)',
                  '顶点式：y = a(x - h)² + k',
                  '交点式：y = a(x - x₁)(x - x₂)',
                  '定义域：R (实数集)',
                  '值域：根据开口方向确定',
                  '三种形式的互化转换'
                ] 
              },
              { 
                text: '图像性质与判别式', 
                children: [
                  '图像：开口向上/向下的抛物线',
                  '开口方向：a > 0向上，a < 0向下',
                  '对称轴：x = -b/(2a)',
                  '顶点坐标：(-b/(2a), (4ac-b²)/(4a))',
                  '判别式：Δ = b² - 4ac',
                  'Δ>0两根，Δ=0一根，Δ<0无根',
                  '函数的单调性与最值'
                ] 
              },
              { 
                text: '实际应用与解题方法', 
                children: [
                  '最值问题（经济利润等）',
                  '抛物运动与物理应用',
                  '几何图形面积最值',
                  '一元二次不等式求解',
                  '函数图像的平移变换',
                  '根据条件求解析式',
                  '综合应用与建模'
                ] 
              }
            ],
            layout: 'horizontal',
            theme: 'blue'
          }
        },
        {
          id: 2,
          name: '化学原子结构动画',
          type: 'animation',
          subject: 'chemistry',
          grade: 'middle',
          description: '生动展示原子结构、电子轨道和化学键形成过程的3D动画',
          thumbnail: null,
          url: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCI+PC9zdmc+',
          quality: 4.3,
          viewCount: 45,
          createdAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000),
          updatedAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
          creator: '李老师',
          tags: ['化学', '原子', '动画', '初中'],
          fileSize: '8.7MB',
          expiryDate: new Date(Date.now() + 60 * 24 * 60 * 60 * 1000),
          accuracy: 92,
          lastUsed: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
          content: '化学原子结构、电子轨道和化学键的形成过程'
        },
        {
          id: 3,
          name: '英语语法结构图表',
          type: 'chart',
          subject: 'english',
          grade: 'middle',
          description: '清晰展示英语各种时态和句型结构的对比图表',
          thumbnail: null,
          url: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCI+PC9zdmc+',
          quality: 4.1,
          viewCount: 67,
          createdAt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
          updatedAt: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000),
          creator: '王老师',
          tags: ['英语', '语法', '图表', '时态'],
          fileSize: '1.8MB',
          expiryDate: new Date(Date.now() + 45 * 24 * 60 * 60 * 1000),
          accuracy: 88,
          lastUsed: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
          content: '英语语法结构和时态对比分析数据'
        },
        {
          id: 4,
          name: '物理实验流程演示',
          type: 'video',
          subject: 'physics',
          grade: 'high',
          description: '详细演示物理电路实验的完整操作流程和注意事项，包含欧姆定律验证实验的标准操作',
          thumbnail: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzIwIiBoZWlnaHQ9IjE4MCIgdmlld0JveD0iMCAwIDMyMCAxODAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PGxpbmVhckdyYWRpZW50IGlkPSJiZyIgeDE9IjAiIHkxPSIwIiB4Mj0iMSIgeTI9IjEiPjxzdG9wIHN0b3AtY29sb3I9IiMxZTQwYWYiLz48c3RvcCBvZmZzZXQ9IjEiIHN0b3AtY29sb3I9IiMzYjgyZjYiLz48L2xpbmVhckdyYWRpZW50PjwvZGVmcz48cmVjdCB3aWR0aD0iMzIwIiBoZWlnaHQ9IjE4MCIgZmlsbD0idXJsKCNiZykiLz48Y2lyY2xlIGN4PSI4MCIgY3k9IjcwIiByPSIxNSIgZmlsbD0iI2ZmZjdjYyIgc3Ryb2tlPSIjZmY5ODAwIiBzdHJva2Utd2lkdGg9IjMiLz48Y2lyY2xlIGN4PSIyNDAiIGN5PSI3MCIgcj0iMTUiIGZpbGw9IiNmZmY3Y2MiIHN0cm9rZT0iI2ZmOTgwMCIgc3Ryb2tlLXdpZHRoPSIzIi8+PGxpbmUgeDE9Ijk1IiB5MT0iNzAiIHgyPSIyMjUiIHkyPSI3MCIgc3Ryb2tlPSIjZmY5ODAwIiBzdHJva2Utd2lkdGg9IjQiLz48Y2lyY2xlIGN4PSIxNjAiIGN5PSI3MCIgcj0iMTAiIGZpbGw9IiMxMGI5ODEiLz48cmVjdCB4PSIxNDAiIHk9IjkwIiB3aWR0aD0iNDAiIGhlaWdodD0iMjAiIGZpbGw9IiNmMzU1OWUiIHJ4PSI0Ii8+PGxpbmUgeDE9Ijg1IiB5MT0iODUiIHgyPSIxNDAiIHkyPSIxMDAiIHN0cm9rZT0iI2ZmOTgwMCIgc3Ryb2tlLXdpZHRoPSIzIi8+PGxpbmUgeDE9IjE4MCIgeTE9IjEwMCIgeDI9IjIzNSIgeTI9Ijg1IiBzdHJva2U9IiNmZjk4MDAiIHN0cm9rZS13aWR0aD0iMyIvPjx0ZXh0IHg9IjE2MCIgeT0iMTQwIiBmaWxsPSJ3aGl0ZSIgZm9udC1zaXplPSIxNCIgZm9udC1mYW1pbHk9IkFyaWFsIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj7llK/mkK3mlZnogrLnkIbnsr7lrp7pqozlubPlj7A8L3RleHQ+PHRleHQgeD0iMTYwIiB5PSIzMCIgZmlsbD0id2hpdGUiIGZvbnQtc2l6ZT0iMTgiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC13ZWlnaHQ9ImJvbGQiIHRleHQtYW5jaG9yPSJtaWRkbGUiPueJqeeQhuWunumqjOaVmeWtpuivvue8uzzL3RleHQ+PC9zdmc+',
          url: '/1656055558542.mp4',
          backupUrl: './1656055558542.mp4',
          quality: 4.8,
          viewCount: 34,
          createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
          updatedAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
          creator: '系统生成',
          tags: ['物理', '实验', '视频', '电路', '欧姆定律'],
          fileSize: '15.2MB',
          expiryDate: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000),
          accuracy: 97,
          lastUsed: new Date(),
          content: '物理电路实验的完整操作流程和安全注意事项：连接电路→检查器材→测量电流电压→记录数据→分析结果',
          duration: '7:55',
          resolution: '1080p',
          videoMetadata: {
            title: '物理实验教学本地课程',
            subtitle: '本地视频文件 - 1656055558542.mp4',
            platform: '本地视频 📁',
            chapters: [
              { time: '00:00', title: '视频开始' },
              { time: '02:00', title: '内容片段1' },
              { time: '04:00', title: '内容片段2' },
              { time: '06:00', title: '内容片段3' },
              { time: '08:00', title: '内容片段4' },
              { time: '10:00', title: '内容片段5' },
              { time: '12:00', title: '内容片段6' },
              { time: '14:00', title: '视频结束' }
            ]
          }
        },
        {
          id: 5,
          name: '数学几何证明插图',
          type: 'illustration',
          subject: 'math',
          grade: 'middle',
          description: '精美的几何图形和证明过程的步骤图解',
          thumbnail: null,
          url: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCI+PC9zdmc+',
          quality: 4.4,
          viewCount: 28,
          createdAt: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000),
          updatedAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
          creator: '系统生成',
          tags: ['数学', '几何', '插图', '证明'],
          fileSize: '3.1MB',
          expiryDate: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000), // 即将过期
          accuracy: 94,
          lastUsed: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
          content: '数学几何图形和证明过程的详细步骤'
        },
        {
          id: 6,
          name: '学生成绩分析报告',
          type: 'case',
          subject: 'math',
          grade: 'high',
          description: '基于真实数据的学生数学成绩分析和改进建议',
          thumbnail: null,
          url: 'data:application/pdf;base64,',
          quality: 3.9,
          viewCount: 19,
          createdAt: new Date(Date.now() - 8 * 24 * 60 * 60 * 1000),
          updatedAt: new Date(Date.now() - 6 * 24 * 60 * 60 * 1000),
          creator: '陈老师',
          tags: ['数学', '成绩分析', '报告', '数据'],
          fileSize: '4.5MB',
          expiryDate: new Date(Date.now() + 120 * 24 * 60 * 60 * 1000),
          accuracy: 91,
          lastUsed: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000),
          content: '学生数学成绩分析数据和改进建议报告'
        }
      ],
      
      // 资源预览对话框
      resourcePreviewDialog: false,
      previewingResource: null,
      
      // 资源编辑对话框
      resourceEditorDialog: false,
      editingResource: null
    }
  },
  computed: {
    canGenerate() {
      const hasInput = this.inputContent.text || this.voiceText || this.inputContent.image || this.inputContent.document
      return hasInput && this.selectedResourceType && this.generateConfig.subject && this.generateConfig.grade
    },
    canMultiModalGenerate() {
      // 多模态融合生成需要至少两种输入方式
      const inputCount = [
        this.inputContent.text,
        this.voiceText,
        this.inputContent.image,
        this.inputContent.document
      ].filter(Boolean).length
      return inputCount >= 2 && this.selectedResourceType
    },
    generateStatusType() {
      if (this.isGenerating) return 'warning'
      if (this.generatedContent) return 'success'
      return 'info'
    },
    generateStatusText() {
      if (this.isGenerating) return '生成中'
      if (this.generatedContent) return '生成完成'
      return '等待生成'
    },
    
    // 资源管理相关计算属性
    paginatedResources() {
      const start = (this.resourceCurrentPage - 1) * this.resourcePageSize
      const end = start + this.resourcePageSize
      return this.filteredResources.slice(start, end)
    }
  },
  methods: {
    handleInputTabClick(tab) {
      console.log('切换到:', tab.name)
    },
    // 监听输入内容变化，智能推荐素材和自动选择类型
    watchInputContent() {
      const content = this.inputContent.text || this.voiceText
      if (content && content.length > 10) {
        this.suggestedMaterials = this.generateMaterialSuggestions(content)
        this.autoSelectResourceType(content)
        this.autoConfigureByContent(content)
      } else {
        this.suggestedMaterials = []
      }
    },
    // 智能识别内容并自动选择资源类型
    autoSelectResourceType(content) {
      const lowerContent = content.toLowerCase()
      
      if (lowerContent.includes('思维导图') || lowerContent.includes('脑图') || lowerContent.includes('知识结构')) {
        this.selectedResourceType = 'mindmap'
      } else if (lowerContent.includes('视频') || lowerContent.includes('教学视频') || lowerContent.includes('录像')) {
        this.selectedResourceType = 'video'
      } else if (lowerContent.includes('图表') || lowerContent.includes('数据') || lowerContent.includes('统计')) {
        this.selectedResourceType = 'chart'
      } else if (lowerContent.includes('流程') || lowerContent.includes('步骤') || lowerContent.includes('过程')) {
        this.selectedResourceType = 'diagram'
      } else if (lowerContent.includes('插图') || lowerContent.includes('配图') || lowerContent.includes('图片')) {
        this.selectedResourceType = 'illustration'
      } else if (lowerContent.includes('音频') || lowerContent.includes('录音') || lowerContent.includes('语音')) {
        this.selectedResourceType = 'audio'
      }
    },

    // 根据内容自动配置参数
    autoConfigureByContent(content) {
      const lowerContent = content.toLowerCase()
      
      // 学科识别
      if (lowerContent.includes('数学') || lowerContent.includes('函数') || lowerContent.includes('方程')) {
        this.generateConfig.subject = 'math'
      } else if (lowerContent.includes('语文') || lowerContent.includes('文学') || lowerContent.includes('作文')) {
        this.generateConfig.subject = 'chinese'
      } else if (lowerContent.includes('英语') || lowerContent.includes('english')) {
        this.generateConfig.subject = 'english'
      } else if (lowerContent.includes('物理') || lowerContent.includes('力学') || lowerContent.includes('电路')) {
        this.generateConfig.subject = 'physics'
      } else if (lowerContent.includes('化学') || lowerContent.includes('分子') || lowerContent.includes('反应')) {
        this.generateConfig.subject = 'chemistry'
      }
      
      // 年级识别
      if (lowerContent.includes('小学') || lowerContent.includes('elementary')) {
        this.generateConfig.grade = 'primary'
        this.generateConfig.difficulty = 2
      } else if (lowerContent.includes('初中') || lowerContent.includes('middle')) {
        this.generateConfig.grade = 'middle'
        this.generateConfig.difficulty = 3
      } else if (lowerContent.includes('高中') || lowerContent.includes('high')) {
        this.generateConfig.grade = 'high'
        this.generateConfig.difficulty = 4
      } else if (lowerContent.includes('大学') || lowerContent.includes('university')) {
        this.generateConfig.grade = 'university'
        this.generateConfig.difficulty = 5
      }
    },

    generateMaterialSuggestions(content) {
      // 基于输入内容生成素材建议
      const suggestions = []
      
      if (content.includes('数学') || content.includes('计算')) {
        suggestions.push(
          {
            id: 1,
            title: '数学公式模板',
            description: '常用数学公式和符号模板',
            icon: 'el-icon-edit-outline',
            content: '二次函数: y = ax² + bx + c'
          },
          {
            id: 2,
            title: '几何图形素材',
            description: '基础几何图形绘制素材',
            icon: 'el-icon-picture-outline',
            content: '三角形、圆形、多边形等几何图形'
          }
        )
      }
      
      if (content.includes('英语') || content.includes('语法')) {
        suggestions.push(
          {
            id: 3,
            title: '语法结构图',
            description: '英语语法结构可视化模板',
            icon: 'el-icon-share',
            content: '主语 + 谓语 + 宾语 句型结构'
          }
        )
      }
      
      if (content.includes('物理') || content.includes('实验')) {
        suggestions.push(
          {
            id: 4,
            title: '实验装置图',
            description: '物理实验装置示意图',
            icon: 'el-icon-cpu',
            content: '电路图、力学实验装置等'
          }
        )
      }
      
      return suggestions.slice(0, 3) // 最多显示3个建议
    },
    toggleRecording() {
      this.isRecording = !this.isRecording
      if (this.isRecording) {
        // 开始录音逻辑
        this.startRecording()
      } else {
        // 停止录音逻辑
        this.stopRecording()
      }
    },
    stopRecording() {
      // 停止录音处理
    },
    selectResourceType(type) {
      this.selectedResourceType = type
    },
    async startGenerate() {
      if (!this.canGenerate) {
        this.$message.warning('请完善生成配置后再试')
        return
      }

      this.isGenerating = true
      this.generateProgress = 0
      this.estimatedTime = this.calculateEstimatedTime()
      
      // 重置质量指标
      this.qualityMetrics = {
        accuracy: 0,
        visual: 0,
        educational: 0,
        innovation: 0
      }
      
      try {
        // 模拟生成过程
        await this.simulateGeneration()
        
        // 生成完成后的处理
        const resourceType = this.resourceTypes.find(t => t.value === this.selectedResourceType)
        this.generatedContent = this.createMockContent(resourceType)
        
        this.qualityScore = this.calculateQualityScore()
        this.generateImprovementSuggestions()
        
        this.$message({
          message: '资源生成完成！点击预览查看效果',
          type: 'success',
          duration: 3000
        })
        
        // 自动创建版本
        this.createVersion()
        
      } catch (error) {
        this.$message.error('生成失败，请重试')
      } finally {
        this.isGenerating = false
        this.generateProgress = 0
      }
    },
    simulateGeneration() {
      return new Promise(resolve => {
        const steps = [
          '正在分析输入内容...',
          '正在生成内容结构...',
          '正在优化视觉效果...',
          '正在进行质量检测...',
          '正在生成最终结果...'
        ]
        
        let currentStep = 0
        const progressInterval = setInterval(() => {
          this.generateProgress += Math.random() * 15 + 5
          this.currentGenerateStep = steps[Math.floor(currentStep / 20)]
          this.estimatedTime = Math.max(0, this.estimatedTime - 1)
          
          // 更新质量指标
          if (this.generateProgress > 20) {
            this.qualityMetrics.accuracy = Math.min(95, this.qualityMetrics.accuracy + Math.random() * 10)
          }
          if (this.generateProgress > 40) {
            this.qualityMetrics.visual = Math.min(90, this.qualityMetrics.visual + Math.random() * 8)
          }
          if (this.generateProgress > 60) {
            this.qualityMetrics.educational = Math.min(95, this.qualityMetrics.educational + Math.random() * 12)
          }
          if (this.generateProgress > 80) {
            this.qualityMetrics.innovation = Math.min(88, this.qualityMetrics.innovation + Math.random() * 15)
          }
          
          currentStep++
          if (this.generateProgress >= 100) {
            this.generateProgress = 100
            this.currentGenerateStep = '生成完成'
            clearInterval(progressInterval)
            setTimeout(resolve, 500)
          }
        }, 200)
      })
    },
    // 新增方法
    formatDifficultyTooltip(val) {
      const levels = ['很容易', '容易', '中等', '困难', '很困难']
      return levels[val - 1]
    },
    formatDurationTooltip(val) {
      const minutes = Math.floor(val / 60)
      const seconds = val % 60
      return `${minutes}:${seconds.toString().padStart(2, '0')}`
    },
    getOutputFormats() {
      const formatMap = {
        video: [
          { value: 'mp4', label: 'MP4' },
          { value: 'avi', label: 'AVI' },
          { value: 'mov', label: 'MOV' }
        ],
        audio: [
          { value: 'mp3', label: 'MP3' },
          { value: 'wav', label: 'WAV' },
          { value: 'aac', label: 'AAC' }
        ],
        illustration: [
          { value: 'png', label: 'PNG' },
          { value: 'jpg', label: 'JPG' },
          { value: 'svg', label: 'SVG' }
        ],
        chart: [
          { value: 'png', label: 'PNG' },
          { value: 'svg', label: 'SVG' },
          { value: 'pdf', label: 'PDF' }
        ],
        default: [
          { value: 'pdf', label: 'PDF' },
          { value: 'docx', label: 'Word' },
          { value: 'html', label: 'HTML' }
        ]
      }
      return formatMap[this.selectedResourceType] || formatMap.default
    },
    intelligentRecommend() {
      // 基于输入内容智能推荐配置
      const content = this.inputContent.text || this.voiceText
      if (content) {
        // 简单的关键词匹配推荐
        if (content.includes('小学') || content.includes('儿童')) {
          this.generateConfig.grade = 'primary'
          this.generateConfig.style = 'vivid'
          this.generateConfig.difficulty = 2
        } else if (content.includes('高中') || content.includes('复杂')) {
          this.generateConfig.grade = 'high'
          this.generateConfig.difficulty = 4
        }
        
        if (content.includes('数学') || content.includes('计算')) {
          this.generateConfig.subject = 'math'
        } else if (content.includes('英语') || content.includes('语法')) {
          this.generateConfig.subject = 'english'
        }
        
        this.$message.success('已为您智能推荐配置')
      } else {
        this.$message.warning('请先输入内容再进行智能推荐')
      }
    },
    saveAsTemplate() {
      // 保存当前配置为模板
      const template = {
        name: `${this.generateConfig.subject}_${this.generateConfig.grade}_模板`,
        config: { ...this.generateConfig },
        createdAt: new Date()
      }
      // 这里可以保存到本地存储或发送到后端
      this.$message.success('配置模板已保存')
    },
    previewFullscreen() {
      this.$message.info('全屏预览功能开发中...')
    },
    regenerateContent() {
      this.$confirm('确认重新生成资源吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.generatedContent = null
        this.startGenerate()
      })
    },
    editContent() {
      this.$message.info('编辑功能开发中...')
    },
    useSuggestedMaterial(material) {
      this.inputContent.text += '\n' + material.content
      this.$message.success(`已添加素材：${material.title}`)
    },
    downloadResult() {
      this.$message({
        message: '正在准备下载资源包...',
        type: 'success',
        duration: 3000
      })
    },
    exportToCloud() {
      this.$message({
        message: '正在导出到云端存储...',
        type: 'info',
        duration: 3000
      })
    },
    shareResult() {
      this.$message.info('智能分享链接已复制到剪贴板')
    },
    
    // === 创新功能方法 ===
    
    // AI助手功能
    sendMessage() {
      if (!this.currentMessage.trim()) return
      
      // 添加用户消息
      const userMessage = {
        id: Date.now(),
        type: 'user',
        text: this.currentMessage,
        timestamp: new Date()
      }
      this.chatMessages.push(userMessage)
      
      const userQuestion = this.currentMessage
      this.currentMessage = ''
      this.isAIThinking = true
      this.assistantStatus = '正在思考...'
      
      // 模拟AI回复
      setTimeout(() => {
        const aiResponse = this.generateAIResponse(userQuestion)
        this.chatMessages.push({
          id: Date.now(),
          type: 'assistant',
          text: aiResponse,
          timestamp: new Date()
        })
        this.isAIThinking = false
        this.assistantStatus = '准备为您提供专业建议'
        this.scrollToBottom()
      }, 2000)
    },
    
    generateAIResponse(question) {
      // 基于输入内容的智能AI回复逻辑
      const lowerQuestion = question.toLowerCase()
      const currentContent = this.inputContent.text || this.voiceText || ''
      
      if (lowerQuestion.includes('二次函数') || lowerQuestion.includes('函数')) {
        return `针对二次函数的学习，我建议：\n1. 从基本形式 y=ax²+bx+c 开始\n2. 重点理解图像特征（抛物线、开口方向、对称轴）\n3. 掌握性质分析（单调性、最值、零点）\n4. 联系实际应用问题\n\n您当前选择的${this.getResourceTypeName()}很适合展示这些知识点的结构关系！`
      } else if (lowerQuestion.includes('思维导图') || lowerQuestion.includes('脑图')) {
        return `思维导图是结构化学习的优秀工具！基于您的输入"${currentContent.slice(0, 30)}..."，我建议：\n1. 以核心概念为中心\n2. 按逻辑层次展开分支\n3. 使用关键词而非长句\n4. 适当添加颜色和图标\n\n系统已为您智能配置了相关参数，您可以直接生成了！`
      } else if (lowerQuestion.includes('图表') || lowerQuestion.includes('数据')) {
        return `数据可视化能让抽象概念更直观！我建议：\n1. 选择合适的图表类型（柱状图、折线图、饼图等）\n2. 确保数据准确性\n3. 添加清晰的标题和标签\n4. 考虑目标受众的理解能力\n\n当前系统已智能识别您的需求并推荐了最佳配置。`
      } else if (lowerQuestion.includes('生成') || lowerQuestion.includes('制作')) {
        const resourceType = this.getResourceTypeName()
        return `准备生成${resourceType}了吗？基于您的输入，我检测到：\n✓ 学科：${this.generateConfig.subject || '已识别'}\n✓ 年级：${this.generateConfig.grade || '已设置'}\n✓ 难度：${this.generateConfig.difficulty}级\n\n点击"开始生成"按钮，系统将为您创建高质量的教学资源！预计用时${this.calculateEstimatedTime()}秒。`
      } else if (lowerQuestion.includes('优化') || lowerQuestion.includes('改进')) {
        return `资源优化建议：\n1. 内容方面：确保逻辑清晰、重点突出\n2. 视觉方面：配色协调、布局美观\n3. 交互方面：适当添加互动元素\n4. 适配方面：考虑不同设备的显示效果\n\n您可以查看右侧的质量指标，系统会实时分析并提供改进建议。`
      } else {
        const suggestions = this.getContextualSuggestions()
        return `基于您当前的配置和输入内容，我为您推荐：\n${suggestions}\n\n如果您需要更具体的指导，请告诉我您的具体需求或遇到的问题。`
      }
    },

    // 获取上下文相关建议
    getContextualSuggestions() {
      const suggestions = []
      
      if (!this.selectedResourceType) {
        suggestions.push('• 先选择一个资源类型开始')
      }
      
      if (!this.generateConfig.subject) {
        suggestions.push('• 设置学科以获得更准确的内容')
      }
      
      if (!this.inputContent.text && !this.voiceText) {
        suggestions.push('• 添加详细的内容描述')
      }
      
      if (this.generateConfig.difficulty < 3 && this.generateConfig.grade === 'high') {
        suggestions.push('• 高中内容建议提高难度等级')
      }
      
      if (suggestions.length === 0) {
        suggestions.push('• 配置完善，可以开始生成了！')
      }
      
      return suggestions.join('\n')
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        const chatContainer = this.$refs.chatMessages
        if (chatContainer) {
          chatContainer.scrollTop = chatContainer.scrollHeight
        }
      })
    },
    
    formatTime(timestamp) {
      return new Date(timestamp).toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    },
    
    // 多模态融合生成
    startMultiModalGenerate() {
      this.$message.success('启动多模态融合生成模式！')
      this.generateMode = 'multimodal'
      // 计算多模态适应性
      this.calculateAdaptability()
      this.startGenerate()
    },
    
    calculateAdaptability() {
      // 基于输入内容计算学习者适应度
      let adaptability = 50
      const content = this.inputContent.text || this.voiceText || ''
      
      if (content.includes('小学') || content.includes('儿童')) adaptability += 20
      if (content.includes('互动') || content.includes('游戏')) adaptability += 15
      if (this.inputContent.image) adaptability += 10
      if (this.generateConfig.style === 'vivid') adaptability += 10
      
      this.learnerAdaptability = Math.min(95, adaptability)
      
      // 计算知识关联度
      let relevance = 60
      if (this.generateConfig.subject) relevance += 20
      if (this.generateConfig.difficulty > 2) relevance += 10
      if (content.length > 100) relevance += 10
      
      this.knowledgeRelevance = Math.min(95, relevance)
    },
    
    getAdaptabilityColor(percentage) {
      if (percentage >= 80) return '#67C23A'
      if (percentage >= 60) return '#E6A23C'
      return '#F56C6C'
    },
    
    getRelevanceColor(percentage) {
      if (percentage >= 80) return '#409EFF'
      if (percentage >= 60) return '#909399'
      return '#F56C6C'
    },
    
    // 实时协作功能
    startCollaboration() {
      this.collaborationActive = true
      this.$message.success('已启用实时协作模式，邀请链接已生成')
      // 模拟协作活动
      this.simulateCollaborationActivity()
    },
    
    simulateCollaborationActivity() {
      setInterval(() => {
        if (this.collaborationActive && Math.random() > 0.7) {
          const activities = [
            '查看了预览', '修改了配置', '添加了评论', '分享了想法', '标记了重点'
          ]
          const users = ['张老师', '李老师', '王老师']
          const icons = ['el-icon-view', 'el-icon-edit', 'el-icon-chat-dot-square', 
                        'el-icon-star-on', 'el-icon-price-tag']
          
          this.recentActivities.unshift({
            id: Date.now(),
            user: users[Math.floor(Math.random() * users.length)],
            action: activities[Math.floor(Math.random() * activities.length)],
            icon: icons[Math.floor(Math.random() * icons.length)],
            timestamp: new Date()
          })
          
          // 保持最新10条活动
          if (this.recentActivities.length > 10) {
            this.recentActivities = this.recentActivities.slice(0, 10)
          }
        }
      }, 5000)
    },
    
    inviteCollaborators() {
      this.$prompt('请输入协作者邮箱', '邀请协作', {
        confirmButtonText: '发送邀请',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        this.$message.success(`邀请已发送到 ${value}`)
      })
    },
    
    shareForReview() {
      this.$message.success('评审链接已生成，有效期24小时')
    },
    
    formatActivityTime(timestamp) {
      const now = new Date()
      const diff = now - new Date(timestamp)
      const minutes = Math.floor(diff / 60000)
      
      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      const hours = Math.floor(minutes / 60)
      return `${hours}小时前`
    },
    
    // 版本管理功能
    createVersion() {
      const version = {
        id: Date.now(),
        name: `v${this.versionHistory.length + 1}.0`,
        timestamp: new Date(),
        changes: ['生成配置更新', '内容优化', '质量提升'],
        quality: this.qualityScore * 20,
        innovation: Math.floor(Math.random() * 20) + 70,
        content: { ...this.generatedContent }
      }
      this.versionHistory.unshift(version)
      this.currentVersionId = version.id
    },
    
    switchVersion(versionId) {
      const version = this.versionHistory.find(v => v.id === versionId)
      if (version) {
        this.currentVersionId = versionId
        this.generatedContent = { ...version.content }
        this.$message.success(`已切换到 ${version.name}`)
      }
    },
    
    formatVersionTime(timestamp) {
      return new Date(timestamp).toLocaleString('zh-CN')
    },
    
    // 3D预览功能
    rotate3D() {
      this.$message.info('3D视角旋转中...')
    },
    
    zoom3D() {
      this.$message.info('3D视图缩放中...')
    },
    
    enterVRMode() {
      this.$message.info('VR模式需要VR设备支持')
    },
    
    // 跨平台适配
    adaptToPlatforms() {
      this.$message.success('正在适配到钉钉、腾讯会议、Zoom等平台...')
    },
    
    // 智能优化建议
    applySuggestion(suggestion) {
      this.$message.success(`正在应用建议: ${suggestion.text}`)
      // 从建议列表中移除已应用的建议
      this.improvementSuggestions = this.improvementSuggestions.filter(s => s.id !== suggestion.id)
    },

    // 新增优化方法
    calculateEstimatedTime() {
      let baseTime = 30 // 基础时间30秒
      
      // 根据资源类型调整时间
      const timeMap = {
        'video': 60,
        'audio': 45,
        'animation': 90,
        'chart': 20,
        'mindmap': 25,
        'diagram': 30,
        'case': 35,
        'illustration': 40
      }
      
      baseTime = timeMap[this.selectedResourceType] || baseTime
      
      // 根据难度调整
      baseTime += this.generateConfig.difficulty * 5
      
      // 根据内容长度调整
      const contentLength = (this.inputContent.text || '').length
      if (contentLength > 200) baseTime += 15
      if (contentLength > 500) baseTime += 25
      
      return baseTime
    },

    createMockContent(resourceType) {
      const content = this.inputContent.text || this.voiceText || ''
      
      return {
        type: this.selectedResourceType,
        url: this.generateRealContent(content),
        name: `${resourceType ? resourceType.label : '资源'} - ${this.generateConfig.subject || '通用'}`,
        createdAt: new Date(),
        duration: this.generateConfig.duration ? `${Math.floor(this.generateConfig.duration / 60)}:${(this.generateConfig.duration % 60).toString().padStart(2, '0')}` : null,
        dimensions: this.generateConfig.resolution === '4k' ? '3840×2160' : this.generateConfig.resolution === '1080p' ? '1920×1080' : '1280×720',
        nodeCount: Math.floor(Math.random() * 10) + 8,
        levels: Math.floor(Math.random() * 3) + 2,
        chartType: ['柱状图', '饼图', '折线图', '散点图', '雷达图'][Math.floor(Math.random() * 5)],
        dataPoints: Math.floor(Math.random() * 10) + 5,
        animation: ['淡入效果', '飞入效果', '旋转效果', '缩放效果'][Math.floor(Math.random() * 4)],
        pages: Math.floor(Math.random() * 8) + 3,
        fileSize: `${(Math.random() * 5 + 1).toFixed(1)}MB`,
        description: this.generateContentDescription(),
        content: content || `${resourceType ? resourceType.label : '资源'}的详细内容` // 保存原始内容用于渲染
      }
    },

    // 生成真实内容
    generateRealContent(inputContent) {
      const lowerContent = inputContent.toLowerCase()
      
      if (this.selectedResourceType === 'mindmap') {
        return this.generateMindmapSVG(inputContent)
      } else if (this.selectedResourceType === 'chart') {
        return this.generateChartSVG(inputContent)
      } else if (this.selectedResourceType === 'diagram') {
        return this.generateDiagramSVG(inputContent)
      } else if (this.selectedResourceType === 'illustration') {
        return this.generateIllustrationSVG(inputContent)
      }
      
      // 默认返回placeholder
      return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgdmlld0JveD0iMCAwIDQwMCAzMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSI0MDAiIGhlaWdodD0iMzAwIiBmaWxsPSIjRjNGNEY2Ii8+CjxyZWN0IHg9IjE3NSIgeT0iMTI1IiB3aWR0aD0iNTAiIGhlaWdodD0iNTAiIGZpbGw9IiMzQjgyRjYiLz4KPHRleHQgeD0iMjAwIiB5PSIyMDAiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IiM2QjcyODAiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCI+5qih5ouf6aKE6KeIPC90ZXh0Pgo8L3N2Zz4K'
    },

    // 生成思维导图SVG
    generateMindmapSVG(content) {
      const lowerContent = content.toLowerCase()
      let mindmapData = {}
      
      // 根据内容识别主题并生成相应的思维导图
      if (lowerContent.includes('二次函数') || lowerContent.includes('quadratic')) {
        mindmapData = {
          center: '二次函数',
          branches: [
            {
              label: '基本形式',
              children: ['y = ax² + bx + c', 'a ≠ 0', '标准形式']
            },
            {
              label: '图像特征',
              children: ['抛物线', '开口方向', '对称轴', '顶点']
            },
            {
              label: '性质',
              children: ['单调性', '最值', '零点', '判别式']
            },
            {
              label: '应用',
              children: ['实际问题', '优化问题', '物理运动']
            }
          ]
        }
      } else if (lowerContent.includes('化学') || lowerContent.includes('分子')) {
        mindmapData = {
          center: '化学基础',
          branches: [
            {
              label: '原子结构',
              children: ['质子', '中子', '电子', '电子云']
            },
            {
              label: '化学键',
              children: ['共价键', '离子键', '金属键', '氢键']
            },
            {
              label: '化学反应',
              children: ['氧化还原', '酸碱反应', '沉淀反应']
            }
          ]
        }
      } else {
        // 通用思维导图
        mindmapData = {
          center: '学习主题',
          branches: [
            {
              label: '核心概念',
              children: ['定义', '特点', '分类']
            },
            {
              label: '应用场景',
              children: ['实际应用', '解决问题', '案例分析']
            },
            {
              label: '相关知识',
              children: ['前置知识', '扩展内容', '深入学习']
            }
          ]
        }
      }
      
      return this.createMindmapSVG(mindmapData)
    },

    // 创建思维导图SVG
    createMindmapSVG(data) {
      const width = 800
      const height = 600
      const centerX = width / 2
      const centerY = height / 2
      
      let svg = `<svg width="${width}" height="${height}" viewBox="0 0 ${width} ${height}" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <style>
            .center-node { fill: #667eea; }
            .branch-node { fill: #10b981; }
            .leaf-node { fill: #f59e0b; }
            .node-text { fill: white; font-family: Arial, sans-serif; text-anchor: middle; dominant-baseline: middle; }
            .center-text { font-size: 16px; font-weight: bold; }
            .branch-text { font-size: 12px; font-weight: bold; }
            .leaf-text { font-size: 10px; }
            .connection { stroke: #e5e7eb; stroke-width: 2; }
          </style>
        </defs>
        
        <!-- 背景 -->
        <rect width="${width}" height="${height}" fill="#f8fafc"/>
      `
      
      // 绘制中心节点
      svg += `
        <ellipse cx="${centerX}" cy="${centerY}" rx="80" ry="40" class="center-node"/>
        <text x="${centerX}" y="${centerY}" class="node-text center-text">${data.center}</text>
      `
      
      // 绘制分支
      const angleStep = (2 * Math.PI) / data.branches.length
      data.branches.forEach((branch, i) => {
        const angle = i * angleStep
        const branchX = centerX + Math.cos(angle) * 200
        const branchY = centerY + Math.sin(angle) * 150
        
        // 连接线
        svg += `<line x1="${centerX}" y1="${centerY}" x2="${branchX}" y2="${branchY}" class="connection"/>`
        
        // 分支节点
        svg += `
          <ellipse cx="${branchX}" cy="${branchY}" rx="60" ry="25" class="branch-node"/>
          <text x="${branchX}" y="${branchY}" class="node-text branch-text">${branch.label}</text>
        `
        
        // 子节点
        branch.children.forEach((child, j) => {
          const childAngle = angle + (j - branch.children.length / 2) * 0.3
          const childX = branchX + Math.cos(childAngle) * 120
          const childY = branchY + Math.sin(childAngle) * 80
          
          // 连接线
          svg += `<line x1="${branchX}" y1="${branchY}" x2="${childX}" y2="${childY}" class="connection"/>`
          
          // 子节点
          svg += `
            <ellipse cx="${childX}" cy="${childY}" rx="45" ry="20" class="leaf-node"/>
            <text x="${childX}" y="${childY}" class="node-text leaf-text">${child}</text>
          `
        })
      })
      
      svg += '</svg>'
      
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
    },

    // 生成图表SVG
    generateChartSVG(content) {
      const lowerContent = content.toLowerCase()
      let chartData = {}
      
      if (lowerContent.includes('成绩') || lowerContent.includes('分数') || lowerContent.includes('成绩分析')) {
        chartData = {
          type: 'bar',
          title: '学生成绩分析',
          data: [
            { label: '90-100分', value: 12 },
            { label: '80-89分', value: 25 },
            { label: '70-79分', value: 18 },
            { label: '60-69分', value: 8 },
            { label: '60分以下', value: 3 }
          ]
        }
      } else if (lowerContent.includes('函数') || lowerContent.includes('数学')) {
        chartData = {
          type: 'line',
          title: '二次函数图像',
          data: [
            { x: -3, y: 9 },
            { x: -2, y: 4 },
            { x: -1, y: 1 },
            { x: 0, y: 0 },
            { x: 1, y: 1 },
            { x: 2, y: 4 },
            { x: 3, y: 9 }
          ]
        }
      } else {
        chartData = {
          type: 'pie',
          title: '数据分布',
          data: [
            { label: '分类A', value: 35 },
            { label: '分类B', value: 25 },
            { label: '分类C', value: 20 },
            { label: '分类D', value: 20 }
          ]
        }
      }
      
      return this.createChartSVG(chartData)
    },

    // 创建图表SVG
    createChartSVG(data) {
      const width = 600
      const height = 400
      const margin = { top: 40, right: 40, bottom: 60, left: 60 }
      const chartWidth = width - margin.left - margin.right
      const chartHeight = height - margin.top - margin.bottom
      
      let svg = `<svg width="${width}" height="${height}" viewBox="0 0 ${width} ${height}" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <style>
            .chart-title { font-family: Arial, sans-serif; font-size: 18px; font-weight: bold; text-anchor: middle; fill: #374151; }
            .axis-text { font-family: Arial, sans-serif; font-size: 12px; fill: #6b7280; }
            .bar { fill: #667eea; opacity: 0.8; }
            .bar:hover { opacity: 1; }
            .line { stroke: #10b981; stroke-width: 3; fill: none; }
            .point { fill: #10b981; }
            .grid { stroke: #e5e7eb; stroke-width: 1; }
          </style>
        </defs>
        
        <rect width="${width}" height="${height}" fill="#ffffff"/>
        
        <!-- 标题 -->
        <text x="${width/2}" y="25" class="chart-title">${data.title}</text>
      `
      
      if (data.type === 'bar') {
        const maxValue = Math.max(...data.data.map(d => d.value))
        const barWidth = chartWidth / data.data.length * 0.8
        const barSpacing = chartWidth / data.data.length * 0.2
        
        data.data.forEach((item, i) => {
          const x = margin.left + i * (barWidth + barSpacing) + barSpacing / 2
          const barHeight = (item.value / maxValue) * chartHeight
          const y = margin.top + chartHeight - barHeight
          
          svg += `
            <rect x="${x}" y="${y}" width="${barWidth}" height="${barHeight}" class="bar"/>
            <text x="${x + barWidth/2}" y="${margin.top + chartHeight + 20}" class="axis-text" text-anchor="middle">${item.label}</text>
            <text x="${x + barWidth/2}" y="${y - 5}" class="axis-text" text-anchor="middle">${item.value}</text>
          `
        })
      } else if (data.type === 'line') {
        const xValues = data.data.map(d => d.x)
        const yValues = data.data.map(d => d.y)
        const minX = Math.min(...xValues)
        const maxX = Math.max(...xValues)
        const minY = Math.min(...yValues)
        const maxY = Math.max(...yValues)
        
        // 绘制网格
        for (let i = 0; i <= 5; i++) {
          const y = margin.top + (chartHeight / 5) * i
          svg += `<line x1="${margin.left}" y1="${y}" x2="${margin.left + chartWidth}" y2="${y}" class="grid"/>`
        }
        
        // 绘制线条和点
        let pathD = 'M '
        data.data.forEach((point, i) => {
          const x = margin.left + ((point.x - minX) / (maxX - minX)) * chartWidth
          const y = margin.top + chartHeight - ((point.y - minY) / (maxY - minY)) * chartHeight
          
          pathD += `${x},${y} `
          svg += `<circle cx="${x}" cy="${y}" r="4" class="point"/>`
          
          if (i === 0) pathD += 'L '
        })
        
        svg += `<path d="${pathD}" class="line"/>`
        
        // 坐标轴标签
        svg += `
          <text x="${margin.left + chartWidth/2}" y="${height - 10}" class="axis-text" text-anchor="middle">x</text>
          <text x="15" y="${margin.top + chartHeight/2}" class="axis-text" text-anchor="middle" transform="rotate(-90, 15, ${margin.top + chartHeight/2})">y</text>
        `
      }
      
      svg += '</svg>'
      
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
    },

    // 生成流程图SVG
    generateDiagramSVG(content) {
      const lowerContent = content.toLowerCase()
      let steps = []
      
      if (lowerContent.includes('实验') || lowerContent.includes('experiment')) {
        steps = ['准备实验器材', '设置实验环境', '进行实验操作', '记录实验数据', '分析实验结果', '得出实验结论']
      } else if (lowerContent.includes('解题') || lowerContent.includes('问题')) {
        steps = ['理解题意', '分析条件', '制定策略', '实施方案', '检验结果', '总结方法']
      } else {
        steps = ['开始', '分析问题', '制定计划', '执行计划', '评估结果', '结束']
      }
      
      return this.createDiagramSVG(steps)
    },

    // 创建流程图SVG
    createDiagramSVG(steps) {
      const width = 600
      const height = steps.length * 100 + 100
      const boxWidth = 200
      const boxHeight = 60
      
      let svg = `<svg width="${width}" height="${height}" viewBox="0 0 ${width} ${height}" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <style>
            .process-box { fill: #667eea; stroke: #4f46e5; stroke-width: 2; }
            .start-end-box { fill: #10b981; stroke: #059669; stroke-width: 2; }
            .box-text { fill: white; font-family: Arial, sans-serif; font-size: 12px; text-anchor: middle; dominant-baseline: middle; }
            .arrow { stroke: #374151; stroke-width: 2; fill: #374151; }
          </style>
        </defs>
        
        <rect width="${width}" height="${height}" fill="#f8fafc"/>
      `
      
      steps.forEach((step, i) => {
        const x = width / 2 - boxWidth / 2
        const y = 50 + i * 100
        const isStartEnd = i === 0 || i === steps.length - 1
        
        // 绘制框
        if (isStartEnd) {
          svg += `<rect x="${x}" y="${y}" width="${boxWidth}" height="${boxHeight}" rx="30" ry="30" class="start-end-box"/>`
        } else {
          svg += `<rect x="${x}" y="${y}" width="${boxWidth}" height="${boxHeight}" rx="10" ry="10" class="process-box"/>`
        }
        
        // 绘制文字
        svg += `<text x="${x + boxWidth/2}" y="${y + boxHeight/2}" class="box-text">${step}</text>`
        
        // 绘制箭头 (除了最后一个)
        if (i < steps.length - 1) {
          const arrowY = y + boxHeight + 20
          svg += `
            <line x1="${width/2}" y1="${y + boxHeight}" x2="${width/2}" y2="${arrowY}" class="arrow"/>
            <polygon points="${width/2-5},${arrowY-10} ${width/2+5},${arrowY-10} ${width/2},${arrowY}" class="arrow"/>
          `
        }
      })
      
      svg += '</svg>'
      
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
    },

    // 生成插图SVG
    generateIllustrationSVG(content) {
      const lowerContent = content.toLowerCase()
      
      if (lowerContent.includes('几何') || lowerContent.includes('三角形') || lowerContent.includes('圆')) {
        return this.createGeometryIllustration()
      } else if (lowerContent.includes('原子') || lowerContent.includes('分子')) {
        return this.createAtomIllustration()
      } else {
        return this.createGeneralIllustration()
      }
    },

    // 创建几何图形插图
    createGeometryIllustration() {
      const width = 400
      const height = 300
      
      const svg = `<svg width="${width}" height="${height}" viewBox="0 0 ${width} ${height}" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <style>
            .shape { fill: rgba(102, 126, 234, 0.3); stroke: #667eea; stroke-width: 2; }
            .label { font-family: Arial, sans-serif; font-size: 14px; fill: #374151; text-anchor: middle; }
          </style>
        </defs>
        
        <rect width="${width}" height="${height}" fill="#f8fafc"/>
        
        <!-- 三角形 -->
        <polygon points="100,50 50,150 150,150" class="shape"/>
        <text x="100" y="170" class="label">三角形</text>
        
        <!-- 圆形 -->
        <circle cx="300" cy="100" r="50" class="shape"/>
        <text x="300" y="170" class="label">圆形</text>
        
        <!-- 正方形 -->
        <rect x="50" y="200" width="80" height="80" class="shape"/>
        <text x="90" y="300" class="label">正方形</text>
        
        <!-- 五边形 -->
        <polygon points="300,200 320,220 310,245 290,245 280,220" class="shape"/>
        <text x="300" y="265" class="label">五边形</text>
      </svg>`
      
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
    },

    // 创建原子结构插图
    createAtomIllustration() {
      const width = 300
      const height = 300
      const centerX = width / 2
      const centerY = height / 2
      
      const svg = `<svg width="${width}" height="${height}" viewBox="0 0 ${width} ${height}" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <style>
            .nucleus { fill: #dc2626; }
            .electron { fill: #2563eb; }
            .orbit { fill: none; stroke: #6b7280; stroke-width: 1; stroke-dasharray: 5,5; }
            .label { font-family: Arial, sans-serif; font-size: 12px; fill: #374151; text-anchor: middle; }
          </style>
        </defs>
        
        <rect width="${width}" height="${height}" fill="#f8fafc"/>
        
        <!-- 原子核 -->
        <circle cx="${centerX}" cy="${centerY}" r="15" class="nucleus"/>
        <text x="${centerX}" y="${centerY + 35}" class="label">原子核</text>
        
        <!-- 电子轨道 -->
        <circle cx="${centerX}" cy="${centerY}" r="60" class="orbit"/>
        <circle cx="${centerX}" cy="${centerY}" r="100" class="orbit"/>
        
        <!-- 电子 -->
        <circle cx="${centerX + 60}" cy="${centerY}" r="5" class="electron"/>
        <circle cx="${centerX - 100}" cy="${centerY}" r="5" class="electron"/>
        <circle cx="${centerX}" cy="${centerY - 100}" r="5" class="electron"/>
        
        <text x="${centerX + 60}" y="${centerY + 20}" class="label">电子</text>
      </svg>`
      
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
    },

    // 创建通用插图
    createGeneralIllustration() {
      const width = 400
      const height = 300
      
      const svg = `<svg width="${width}" height="${height}" viewBox="0 0 ${width} ${height}" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <style>
            .element { fill: #667eea; opacity: 0.8; }
            .text { font-family: Arial, sans-serif; font-size: 16px; fill: #374151; text-anchor: middle; }
          </style>
        </defs>
        
        <rect width="${width}" height="${height}" fill="#f8fafc"/>
        
        <rect x="50" y="50" width="300" height="200" rx="20" ry="20" class="element"/>
        <text x="200" y="160" class="text">教学内容图示</text>
      </svg>`
      
      return 'data:image/svg+xml;base64,' + btoa(unescape(encodeURIComponent(svg)))
    },

    generateContentDescription() {
      const textContent = this.inputContent.text && this.inputContent.text.length > 0 
        ? this.inputContent.text.slice(0, 20) 
        : '教学内容'
      
      const descriptions = {
        video: `基于"${textContent}"生成的高质量教学视频，采用${this.generateConfig.style === 'vivid' ? '生动活泼' : '专业严谨'}的演示风格。`,
        audio: `针对${this.generateConfig.grade === 'primary' ? '小学' : this.generateConfig.grade === 'middle' ? '初中' : '高中'}学生定制的音频解说内容。`,
        chart: `智能分析教学数据，生成直观的${this.generateConfig.subject || '学科'}知识图表。`,
        mindmap: `将复杂知识点结构化，形成清晰的思维导图，包含${Math.floor(Math.random() * 5) + 3}个主要分支。`,
        default: `根据您的需求定制生成的优质教学资源。`
      }
      return descriptions[this.selectedResourceType] || descriptions.default
    },

    calculateQualityScore() {
      let score = 3.5 // 基础分数
      
      // 根据配置完整度评分
      if (this.generateConfig.subject) score += 0.3
      if (this.generateConfig.grade) score += 0.3
      if ((this.inputContent.text || '').length > 100) score += 0.4
      if (this.generateConfig.difficulty >= 3) score += 0.2
      if (this.generateConfig.style !== 'professional') score += 0.3
      
      return Math.min(5, Math.max(3, score))
    },

    generateImprovementSuggestions() {
      this.improvementSuggestions = []
      
      if (!this.inputContent.text || this.inputContent.text.length < 50) {
        this.improvementSuggestions.push({
          id: 1,
          text: '建议提供更详细的内容描述以获得更好的生成效果',
          icon: 'el-icon-edit',
          priority: 'high'
        })
      }
      
      if (this.qualityMetrics.interactivity < 80) {
        this.improvementSuggestions.push({
          id: 2,
          text: '添加互动元素可以提高学生参与度',
          icon: 'el-icon-mouse',
          priority: 'medium'
        })
      }
      
      if (this.selectedResourceType === 'video' && !this.generateConfig.resolution) {
        this.improvementSuggestions.push({
          id: 3,
          text: '建议设置视频分辨率以获得最佳观看体验',
          icon: 'el-icon-video-camera',
          priority: 'medium'
        })
      }
      
      if (this.generateConfig.difficulty <= 2) {
        this.improvementSuggestions.push({
          id: 4,
          text: '适当提高难度等级可以增加内容深度',
          icon: 'el-icon-trend-charts',
          priority: 'low'
        })
      }
    },

    // 优化资源类型选择
    selectResourceType(type) {
      this.selectedResourceType = type
      this.calculateAdaptability()
      
      // 根据资源类型智能推荐配置
      this.autoConfigureByType(type)
      
      const selectedType = this.resourceTypes.find(t => t.value === type)
      this.$message({
        message: `已选择${selectedType ? selectedType.label : '资源类型'}`,
        type: 'success',
        duration: 2000
      })
    },

    autoConfigureByType(type) {
      // 根据资源类型自动配置最佳参数
      const configs = {
        video: {
          duration: 300,
          resolution: '1080p',
          colorTheme: 'blue'
        },
        audio: {
          duration: 180,
          colorTheme: 'green'
        },
        chart: {
          colorTheme: 'blue',
          fontSize: 'large'
        },
        mindmap: {
          colorTheme: 'purple',
          fontSize: 'medium'
        }
      }
      
      const config = configs[type]
      if (config) {
        Object.assign(this.generateConfig, config)
      }
    },

    // 时间格式化方法
    formatRelativeTime(timestamp) {
      const now = new Date()
      const date = new Date(timestamp)
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      
      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      const hours = Math.floor(minutes / 60)
      if (hours < 24) return `${hours}小时前`
      return date.toLocaleDateString('zh-CN')
    },

    // 优化语音录制功能
    startRecording() {
      this.$message.info('开始录音...')
      
      // 模拟录音过程
      const duration = Math.floor(Math.random() * 5) + 3 // 3-8秒
      const progressInterval = setInterval(() => {
        // 这里可以添加录音动画效果
      }, 100)
      
      setTimeout(() => {
        clearInterval(progressInterval)
        const sampleTexts = [
          '请生成一个关于数学函数的教学视频',
          '制作初中物理电路图的思维导图',
          '创建英语语法结构的流程图',
          '生成化学实验步骤的动画演示',
          '制作历史时间轴的可视化图表'
        ]
        this.voiceText = sampleTexts[Math.floor(Math.random() * sampleTexts.length)]
        this.inputContent.text = this.voiceText
        this.isRecording = false
        this.watchInputContent()
        this.$message.success('语音识别完成')
      }, duration * 1000)
    },

    // 优化后的新增方法
    quickStart(type) {
      this.selectedResourceType = type
      this.calculateAdaptability()
      
      // 快速配置推荐
      const quickConfigs = {
        video: {
          subject: 'math',
          grade: 'middle',
          difficulty: 3,
          style: 'vivid'
        },
        mindmap: {
          subject: 'chinese',
          grade: 'high',
          difficulty: 3,
          style: 'professional'
        },
        chart: {
          subject: 'math',
          grade: 'high',
          difficulty: 4,
          style: 'simple'
        }
      }
      
      if (quickConfigs[type]) {
        Object.assign(this.generateConfig, quickConfigs[type])
      }
      
      const selectedType = this.resourceTypes.find(t => t.value === type)
      this.$message.success(`已快速配置${selectedType ? selectedType.label : '资源类型'}`)
    },

    handleTemplateCommand(command) {
      if (command === 'save') {
        this.saveAsTemplate()
      } else if (command === 'load') {
        this.$message.info('模板加载功能开发中...')
      }
    },

    handleInputChange() {
      this.watchInputContent()
    },

    getPreviewIcon() {
      const iconMap = {
        mindmap: 'el-icon-share',
        chart: 'el-icon-data-line',
        diagram: 'el-icon-guide',
        case: 'el-icon-document',
        animation: 'el-icon-picture'
      }
      return iconMap[this.selectedResourceType] || 'el-icon-document'
    },

    getResourceTypeName(type) {
      if (type) {
        const resourceType = this.resourceTypes.find(t => t.value === type)
        return resourceType ? resourceType.label : '未知类型'
      }
      const selectedType = this.resourceTypes.find(t => t.value === this.selectedResourceType)
      return selectedType ? selectedType.label : '文档'
    },
    
    // === 资源管理相关方法 ===
    
    // 筛选资源
    filterResources() {
      let filtered = [...this.savedResources]
      
      // 搜索过滤
      if (this.resourceSearchQuery) {
        const query = this.resourceSearchQuery.toLowerCase()
        filtered = filtered.filter(resource => {
          const matchName = resource.name.toLowerCase().includes(query)
          const matchDescription = (resource.description || '').toLowerCase().includes(query)
          const matchTags = resource.tags && resource.tags.some(tag => tag.toLowerCase().includes(query))
          const matchCreator = (resource.creator || '').toLowerCase().includes(query)
          return matchName || matchDescription || matchTags || matchCreator
        })
      }
      
      // 类型过滤
      if (this.resourceFilter.type) {
        filtered = filtered.filter(resource => resource.type === this.resourceFilter.type)
      }
      
      // 学科过滤
      if (this.resourceFilter.subject) {
        filtered = filtered.filter(resource => resource.subject === this.resourceFilter.subject)
      }
      
      // 年级过滤
      if (this.resourceFilter.grade) {
        filtered = filtered.filter(resource => resource.grade === this.resourceFilter.grade)
      }
      
      // 质量等级过滤
      if (this.resourceFilter.quality) {
        const qualityMap = {
          excellent: 4.5,
          good: 4.0,
          average: 3.0
        }
        const minQuality = qualityMap[this.resourceFilter.quality]
        if (minQuality) {
          filtered = filtered.filter(resource => resource.quality >= minQuality)
        }
      }
      
      // 时间范围过滤
      if (this.resourceFilter.timeRange) {
        const now = new Date()
        const ranges = {
          today: 1,
          week: 7,
          month: 30,
          year: 365
        }
        const days = ranges[this.resourceFilter.timeRange]
        if (days) {
          const cutoff = new Date(now.getTime() - days * 24 * 60 * 60 * 1000)
          filtered = filtered.filter(resource => new Date(resource.createdAt) >= cutoff)
        }
      }
      
      // 标签过滤
      if (this.resourceFilter.tags && this.resourceFilter.tags.length > 0) {
        filtered = filtered.filter(resource => {
          return resource.tags && this.resourceFilter.tags.some(tag => resource.tags.includes(tag))
        })
      }
      
      this.filteredResources = filtered
      this.resourceCurrentPage = 1 // 重置到第一页
      this.sortResources() // 应用排序
    },
    
    // 排序资源
    sortResources() {
      const sortBy = this.resourceSortBy
      const order = this.resourceSortOrder
      
      this.filteredResources.sort((a, b) => {
        let aVal, bVal
        
        switch (sortBy) {
          case 'createTime':
            aVal = new Date(a.createdAt).getTime()
            bVal = new Date(b.createdAt).getTime()
            break
          case 'quality':
            aVal = a.quality
            bVal = b.quality
            break
          case 'usage':
            aVal = a.viewCount || 0
            bVal = b.viewCount || 0
            break
          case 'name':
            aVal = a.name.toLowerCase()
            bVal = b.name.toLowerCase()
            break
          default:
            aVal = new Date(a.createdAt).getTime()
            bVal = new Date(b.createdAt).getTime()
        }
        
        if (order === 'desc') {
          return bVal > aVal ? 1 : bVal < aVal ? -1 : 0
        } else {
          return aVal > bVal ? 1 : aVal < bVal ? -1 : 0
        }
      })
    },
    
    // 重置筛选条件
    resetFilters() {
      this.resourceFilter = {
        type: '',
        subject: '',
        grade: '',
        quality: '',
        timeRange: '',
        tags: []
      }
      this.resourceSearchQuery = ''
      this.filterResources()
    },
    
    // 应用筛选
    applyFilters() {
      this.filterResources()
      this.showAdvancedFilter = false
      this.$message.success('筛选条件已应用')
    },
    
    // 清除搜索
    clearSearch() {
      this.resourceSearchQuery = ''
      this.filterResources()
    },
    
    // 滚动到生成区域
    scrollToGenerate() {
      const generateButton = document.querySelector('.main-generate-btn')
      if (generateButton) {
        generateButton.scrollIntoView({ behavior: 'smooth', block: 'center' })
      }
    },
    
    // 分页处理
    handleResourcePageChange(page) {
      this.resourceCurrentPage = page
    },
    
    // 获取资源图标
    getResourceIcon(type) {
      const iconMap = {
        video: 'el-icon-video-camera',
        audio: 'el-icon-headset',
        animation: 'el-icon-picture',
        chart: 'el-icon-data-line',
        mindmap: 'el-icon-share',
        diagram: 'el-icon-guide',
        case: 'el-icon-document',
        illustration: 'el-icon-picture-outline'
      }
      return iconMap[type] || 'el-icon-document'
    },
    
    // 获取类型标签样式
    getTypeTagType(type) {
      const typeMap = {
        video: 'danger',
        audio: 'warning',
        animation: 'success',
        chart: 'primary',
        mindmap: 'info',
        diagram: '',
        case: 'warning',
        illustration: 'success'
      }
      return typeMap[type] || ''
    },
    
    // 获取学科名称
    getSubjectName(subject) {
      const subjectMap = {
        math: '数学',
        chinese: '语文',
        english: '英语',
        physics: '物理',
        chemistry: '化学',
        biology: '生物',
        history: '历史',
        geography: '地理',
        computer: '计算机'
      }
      return subjectMap[subject] || '通用'
    },
    
    // === 思维导图相关方法 ===
    
    // 获取思维导图数据
    getMindmapData(resource) {
      if (!resource) return { centerTopic: '中心主题', branches: [], layout: 'horizontal', theme: 'blue' }
      
      // 如果资源有metadata，直接使用
      if (resource.metadata && resource.metadata.centerTopic) {
        return {
          centerTopic: resource.metadata.centerTopic,
          branches: resource.metadata.branches || [],
          layout: resource.metadata.layout || 'horizontal',
          theme: resource.metadata.theme || 'blue'
        }
      }
      
      // 否则尝试从content解析
      if (resource.content) {
        const parsedData = this.parseMindmapContent(resource.content)
        if (parsedData) {
          return parsedData
        }
      }
      
      // 默认返回
      return {
        centerTopic: resource.name || '中心主题',
        branches: [
          { text: '分支1' },
          { text: '分支2' },
          { text: '分支3' }
        ],
        layout: 'horizontal',
        theme: 'blue'
      }
    },
    
    // 解析思维导图内容
    parseMindmapContent(content) {
      if (!content) return null
      
      const lowerContent = content.toLowerCase()
      
      // 二次函数相关内容解析
      if (lowerContent.includes('二次函数') || lowerContent.includes('quadratic')) {
        return {
          centerTopic: '二次函数 y = ax² + bx + c (a≠0)',
          branches: [
            { 
              text: '解析式与基本概念', 
              children: [
                '一般式：y = ax² + bx + c (a≠0)',
                '顶点式：y = a(x - h)² + k',
                '交点式：y = a(x - x₁)(x - x₂)',
                '定义域：R (实数集)',
                '值域：根据开口方向确定',
                '三种形式的互化转换'
              ] 
            },
            { 
              text: '图像性质与判别式', 
              children: [
                '图像：开口向上/向下的抛物线',
                '开口方向：a > 0向上，a < 0向下',
                '对称轴：x = -b/(2a)',
                '顶点坐标：(-b/(2a), (4ac-b²)/(4a))',
                '判别式：Δ = b² - 4ac',
                'Δ>0两根，Δ=0一根，Δ<0无根',
                '函数的单调性与最值'
              ] 
            },
            { 
              text: '实际应用与解题方法', 
              children: [
                '最值问题（经济利润等）',
                '抛物运动与物理应用',
                '几何图形面积最值',
                '一元二次不等式求解',
                '函数图像的平移变换',
                '根据条件求解析式',
                '综合应用与建模'
              ] 
            }
          ]
        }
      }
      
      // 化学相关内容解析
      if (lowerContent.includes('化学') || lowerContent.includes('原子') || lowerContent.includes('分子')) {
        return {
          centerTopic: '化学基础',
          branches: [
            { text: '原子结构', children: ['质子', '中子', '电子', '电子云'] },
            { text: '化学键', children: ['共价键', '离子键', '金属键', '氢键'] },
            { text: '化学反应', children: ['氧化还原', '酸碱反应', '沉淀反应'] }
          ]
        }
      }
      
      // 英语语法相关内容解析
      if (lowerContent.includes('英语') || lowerContent.includes('语法')) {
        return {
          centerTopic: '英语语法',
          branches: [
            { text: '时态系统', children: ['过去时', '现在时', '将来时', '完成时'] },
            { text: '句型结构', children: ['简单句', '复合句', '复杂句', '并列句'] },
            { text: '词性分类', children: ['名词', '动词', '形容词', '副词'] }
          ]
        }
      }
      
      // 数学几何相关内容解析
      if (lowerContent.includes('几何') || lowerContent.includes('图形')) {
        return {
          centerTopic: '几何图形',
          branches: [
            { text: '平面图形', children: ['三角形', '四边形', '圆形', '多边形'] },
            { text: '立体图形', children: ['正方体', '圆柱体', '球体', '锥体'] },
            { text: '几何性质', children: ['面积', '周长', '体积', '角度'] }
          ]
        }
      }
      
      // 物理相关内容解析
      if (lowerContent.includes('物理') || lowerContent.includes('实验') || lowerContent.includes('电路')) {
        return {
          centerTopic: '物理实验',
          branches: [
            { text: '力学实验', children: ['摩擦力', '重力', '弹力', '运动'] },
            { text: '电学实验', children: ['电路', '电流', '电压', '电阻'] },
            { text: '光学实验', children: ['光的反射', '折射', '干涉', '衍射'] }
          ]
        }
      }
      
      return null
    },
    
    getMindmapLayoutName(layout) {
      const layoutMap = {
        horizontal: '水平布局',
        vertical: '垂直布局',
        radial: '放射布局'
      }
      return layoutMap[layout] || '水平布局'
    },
    
    getMindmapThemeName(theme) {
      const themeMap = {
        blue: '蓝色主题',
        green: '绿色主题',
        orange: '橙色主题',
        purple: '紫色主题'
      }
      return themeMap[theme] || '蓝色主题'
    },
    
    getBranchIcon(branchText) {
      if (!branchText) return 'el-icon-connection'
      
      const text = branchText.toLowerCase()
      if (text.includes('解析式') || text.includes('概念')) {
        return 'el-icon-edit-outline'
      } else if (text.includes('图像') || text.includes('性质') || text.includes('判别式')) {
        return 'el-icon-data-line'
      } else if (text.includes('应用') || text.includes('解题') || text.includes('方法')) {
        return 'el-icon-star-on'
      }
      return 'el-icon-connection'
    },
    
    getBranchClass(branchText) {
      if (!branchText) return ''
      
      const text = branchText.toLowerCase()
      if (text.includes('解析式') || text.includes('概念')) {
        return 'branch-formula'
      } else if (text.includes('图像') || text.includes('性质') || text.includes('判别式')) {
        return 'branch-graph'
      } else if (text.includes('应用') || text.includes('解题') || text.includes('方法')) {
        return 'branch-application'
      }
      return ''
    },
    
    getChildIcon(childText) {
      if (!childText) return 'el-icon-right'
      
      const text = childText.toLowerCase()
      if (text.includes('y =') || text.includes('δ') || text.includes('x =')) {
        return 'el-icon-edit'  // 公式图标
      } else if (text.includes('开口') || text.includes('坐标') || text.includes('图像')) {
        return 'el-icon-pie-chart'  // 图形图标
      } else if (text.includes('定义域') || text.includes('值域') || text.includes('判别式')) {
        return 'el-icon-data-analysis'  // 分析图标
      } else if (text.includes('问题') || text.includes('应用') || text.includes('题')) {
        return 'el-icon-medal'  // 应用图标
      }
      return 'el-icon-right'
    },
    
    // 获取质量等级样式
    getQualityClass(quality) {
      if (quality >= 4.5) return 'quality-excellent'
      if (quality >= 4.0) return 'quality-good'
      if (quality >= 3.0) return 'quality-average'
      return 'quality-poor'
    },
    
    // 检查资源是否即将过期
    isResourceExpiring(resource) {
      if (!resource.expiryDate) return false
      const now = new Date()
      const expiry = new Date(resource.expiryDate)
      const daysUntilExpiry = Math.ceil((expiry - now) / (1000 * 60 * 60 * 24))
      return daysUntilExpiry <= 30 && daysUntilExpiry > 0
    },
    
    // 预览资源
    previewResource(resource) {
      this.previewingResource = resource
      this.resourcePreviewDialog = true
      
      // 增加查看次数
      resource.viewCount = (resource.viewCount || 0) + 1
      resource.lastUsed = new Date()
    },
    
    // 重新生成
    useResource(resource) {
      // 将历史记录的配置加载到当前生成设置中
      this.selectedResourceType = resource.type
      this.generateConfig.subject = resource.subject
      this.generateConfig.grade = resource.grade
      this.inputContent.text = resource.description || `基于历史记录"${resource.name}"重新生成`
      
      // 增加使用次数
      resource.viewCount = (resource.viewCount || 0) + 1
      resource.lastUsed = new Date()
      
      this.$message.success(`已加载"${resource.name}"的生成配置`)
      this.scrollToGenerate()
    },
    
    // 下载资源
    downloadResource(resource) {
      this.$message({
        message: `正在下载"${resource.name}"...`,
        type: 'success',
        duration: 3000
      })
    },
    
    // 编辑历史记录信息
    editResource(resource) {
      console.log('ResourceMaterials: editResource called with:', resource)
      console.log('ResourceMaterials: resource keys:', resource ? Object.keys(resource) : 'resource is null/undefined')
      
      this.editingResource = resource
      this.resourceEditorDialog = true
      
      console.log('ResourceMaterials: Set editingResource to:', this.editingResource)
      console.log('ResourceMaterials: Set resourceEditorDialog to:', this.resourceEditorDialog)
    },
    
    // 处理编辑器关闭
    handleEditorClose() {
      this.resourceEditorDialog = false
      this.editingResource = null
    },
    
    // 处理编辑器保存成功
    handleEditorSave(updatedResource) {
      // 更新资源列表中的资源
      const index = this.savedResources.findIndex(r => r.id === updatedResource.id)
      if (index > -1) {
        // 保留原有的统计信息，更新可编辑的内容
        this.savedResources[index] = { 
          ...this.savedResources[index], 
          ...updatedResource,
          updatedAt: new Date(), // 更新修改时间
          lastUsed: new Date()   // 更新最后使用时间
        }
        this.$message.success('资源编辑保存成功')
        
        // 保存到本地存储
        this.saveToLocalStorage()
      }
      
      // 更新过滤后的资源列表
      this.filterResources()
    },
    
    // 保存到本地存储
    saveToLocalStorage() {
      try {
        localStorage.setItem('smart_teaching_resources', JSON.stringify(this.savedResources))
      } catch (error) {
        console.warn('保存到本地存储失败:', error)
      }
    },
    
    // 从本地存储加载
    loadFromLocalStorage() {
      try {
        const saved = localStorage.getItem('smart_teaching_resources')
        if (saved) {
          const resources = JSON.parse(saved)
          // 合并到现有资源中，避免覆盖示例数据
          resources.forEach(resource => {
            const existingIndex = this.savedResources.findIndex(r => r.id === resource.id)
            if (existingIndex === -1) {
              this.savedResources.push(resource)
            }
          })
        }
      } catch (error) {
        console.warn('从本地存储加载失败:', error)
      }
    },
    
    // 处理历史记录操作
    handleResourceAction(command, resource) {
      switch (command) {
        case 'edit':
          this.editResource(resource)
          break
        case 'share':
          this.$message.success(`"${resource.name}"的分享链接已复制到剪贴板`)
          break
        case 'duplicate':
          const duplicated = {
            ...resource,
            id: Date.now(),
            name: resource.name + ' (复制)',
            createdAt: new Date(),
            viewCount: 0
          }
          this.savedResources.unshift(duplicated)
          this.filterResources()
          this.$message.success('生成配置已复制')
          break
        case 'export':
          this.downloadResource(resource)
          break
        case 'delete':
          this.$confirm(`确认删除生成记录"${resource.name}"吗？`, '删除确认', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            const index = this.savedResources.findIndex(r => r.id === resource.id)
            if (index > -1) {
              this.savedResources.splice(index, 1)
              this.filterResources()
              this.$message.success('生成记录已删除')
            }
          }).catch(() => {
            this.$message.info('已取消删除')
          })
          break
      }
    },
    
    // 处理历史记录命令
    handleHistoryCommand(command) {
      switch (command) {
        case 'export':
          this.$message.success('正在导出历史记录...')
          break
        case 'cleanup':
          const expiredCount = this.savedResources.filter(r => {
            const now = new Date()
            const expiry = new Date(r.expiryDate)
            return expiry < now
          }).length
          
          if (expiredCount > 0) {
            this.$confirm(`发现 ${expiredCount} 条过期记录，是否清理？`, '清理过期记录', {
              confirmButtonText: '清理',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              this.savedResources = this.savedResources.filter(r => {
                const now = new Date()
                const expiry = new Date(r.expiryDate)
                return expiry >= now
              })
              this.filterResources()
              this.$message.success(`已清理 ${expiredCount} 条过期记录`)
            })
          } else {
            this.$message.info('没有发现过期记录')
          }
          break
        case 'stats':
          this.$message.info('生成统计功能开发中...')
          break
        case 'backup':
          this.$message.success('历史记录备份已开始，将在后台进行...')
          break
      }
    },

    // 优化文件上传处理
    handleFileUpload(file) {
      const isImage = file.raw.type.startsWith('image/')
      
      if (isImage) {
        this.inputContent.image = file.raw
      // 模拟图像识别
      setTimeout(() => {
        const imageDescriptions = [
          '识别到数学公式图片，建议生成相关的教学视频',
          '检测到实验装置图片，推荐制作实验流程图',
          '发现手绘草图，适合生成思维导图',
          '识别到文本内容，建议转换为互动动画'
        ]
        const description = imageDescriptions[Math.floor(Math.random() * imageDescriptions.length)]
        this.inputContent.text += `\n\n[图像识别]: ${description}`
          this.handleInputChange()
        this.$message.success('图像识别完成，已提取关键信息')
      }, 2000)
      } else {
      this.inputContent.document = file.raw
      // 模拟文档解析
      setTimeout(() => {
        const docAnalysis = [
          '检测到教学大纲文档，已提取关键知识点',
          '发现课件内容，建议生成配套视频材料',
          '识别到练习题文档，推荐制作解题动画',
          '解析到教学计划，适合生成结构化图表'
        ]
        const analysis = docAnalysis[Math.floor(Math.random() * docAnalysis.length)]
        this.inputContent.text += `\n\n[文档解析]: ${analysis}`
          this.handleInputChange()
        this.$message.success('文档解析完成')
      }, 3000)
      }
    },
    
    // 预览对话框相关方法
    handlePreviewClose() {
      this.resourcePreviewDialog = false
      this.previewingResource = null
    },
    
    // 检测是否为在线视频平台
    isYouTubeVideo(url) {
      if (!url) return false
      return url.includes('youtube.com') || 
             url.includes('youtu.be') || 
             url.includes('bilibili.com') || 
             url.includes('player.bilibili.com') ||
             url.includes('qybee.com') ||
             url.includes('llongwill')
    },

    // 获取视频平台对应的allow属性
    getVideoAllowAttributes(url) {
      if (!url) return ''
      
      if (url.includes('bilibili.com') || url.includes('player.bilibili.com')) {
        return 'accelerometer; autoplay; encrypted-media; fullscreen; gyroscope; picture-in-picture'
      } else if (url.includes('youtube.com') || url.includes('youtu.be')) {
        return 'accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share'
      } else if (url.includes('qybee.com') || url.includes('llongwill')) {
        return 'accelerometer; autoplay; encrypted-media; fullscreen; gyroscope; picture-in-picture'
      }
      
      return 'accelerometer; autoplay; encrypted-media; fullscreen'
    },

    // 处理视频加载错误
    handleVideoError(event) {
      console.warn('视频加载失败，尝试使用备用视频源:', event)
      const video = event.target
      
      // 如果还有备用URL没尝试，就自动切换
      if (video.src.includes('1656055558542.mp4') && !video.src.startsWith('/')) {
        console.log('尝试备用路径: /1656055558542.mp4')
        video.src = '/1656055558542.mp4'
        return
      }
      
      this.$message({
        message: '本地视频文件无法加载，请检查文件路径是否正确',
        type: 'error',
        duration: 5000,
        showClose: true
      })
      
      // 显示详细的错误信息
      this.$notify({
        title: '视频加载失败',
        message: `
          <div>
            <p>视频文件路径: ${video.src}</p>
            <p>请确认文件存在于 public 目录下</p>
            <p>当前尝试路径: ${window.location.origin}/${video.src}</p>
          </div>
        `,
        type: 'warning',
        duration: 8000,
        dangerouslyUseHTMLString: true
      })
    },

    getGradeName(grade) {
      const gradeMap = {
        primary: '小学',
        middle: '初中',
        high: '高中',
        university: '大学'
      }
      return gradeMap[grade] || '未知'
    },
    
    getDaysUntilExpiry(resource) {
      if (!resource.expiryDate) return 0
      const now = new Date()
      const expiry = new Date(resource.expiryDate)
      return Math.ceil((expiry - now) / (1000 * 60 * 60 * 24))
    },
    
    formatDate(date) {
      return new Date(date).toLocaleDateString('zh-CN')
    },
    
    // 获取相关资源
    getRelatedResources(currentResource) {
      return this.savedResources
        .filter(resource => {
          if (resource.id === currentResource.id) return false
          // 根据学科、类型、标签匹配相关资源
          const subjectMatch = resource.subject === currentResource.subject
          const typeMatch = resource.type === currentResource.type
          const tagMatch = resource.tags && currentResource.tags && 
            resource.tags.some(tag => currentResource.tags.includes(tag))
          return subjectMatch || typeMatch || tagMatch
        })
                 .slice(0, 3) // 只显示前3个相关资源
    },
    
    // 自动保存生成记录到历史
    saveGeneratedResource(content) {
      if (!content) return
      
      const newResource = {
        id: Date.now(),
        name: content.name || `${this.getResourceTypeName(this.selectedResourceType)} - ${new Date().toLocaleDateString()}`,
        type: this.selectedResourceType,
        subject: this.generateConfig.subject,
        grade: this.generateConfig.grade,
        description: content.description || this.generateContentDescription(),
        thumbnail: null,
        url: content.url,
        quality: this.qualityScore || 4.0,
        viewCount: 0,
        createdAt: new Date(),
        updatedAt: new Date(),
        creator: '系统生成',
        tags: this.extractTagsFromContent(),
        fileSize: content.fileSize || '未知',
        expiryDate: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000), // 90天后过期
        accuracy: this.qualityMetrics.accuracy || 90,
        lastUsed: new Date()
      }
      
      this.savedResources.unshift(newResource)
      this.filterResources()
      
      this.$message({
        message: `生成记录"${newResource.name}"已保存到历史`,
        type: 'success',
        duration: 3000
      })
    },
    
    // 从内容中提取标签
    extractTagsFromContent() {
      const tags = []
      const content = this.inputContent.text || this.voiceText || ''
      
      // 添加基础标签
      if (this.generateConfig.subject) {
        tags.push(this.getSubjectName(this.generateConfig.subject))
      }
      
      if (this.generateConfig.grade) {
        tags.push(this.getGradeName(this.generateConfig.grade))
      }
      
      if (this.selectedResourceType) {
        tags.push(this.getResourceTypeName(this.selectedResourceType))
      }
      
      // 从内容中提取关键词
      const keywords = ['函数', '几何', '代数', '实验', '语法', '词汇', '化学', '物理', '生物', '历史', '地理']
      keywords.forEach(keyword => {
        if (content.includes(keyword) && !tags.includes(keyword)) {
          tags.push(keyword)
        }
      })
      
      return tags.slice(0, 5) // 最多5个标签
    }
  },
  
  // 生命周期方法
  mounted() {
    // 从本地存储加载已保存的资源
    this.loadFromLocalStorage()
    
    // 初始化智能分析
    this.calculateAdaptability()
    
    // 初始化资源筛选
    this.filterResources()
    
    // 监听输入变化
    this.$watch('inputContent.text', this.watchInputContent)
    this.$watch('voiceText', this.watchInputContent)
    
    // 自动生成版本
    this.$watch('generatedContent', (newContent) => {
      if (newContent) {
        this.createVersion()
        // 自动保存到资源库
        this.saveGeneratedResource(newContent)
      }
    })
  },
  
  beforeDestroy() {
    // 清理定时器等
    if (this.collaborationInterval) {
      clearInterval(this.collaborationInterval)
    }
  }
}
</script>

<style scoped>
.resource-materials {
  padding: 0;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 优化后的页面头部样式 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px 0;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  box-sizing: border-box;
}

.title-section h1 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.title-section p {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 快速启动栏样式 */
.quick-start-bar {
  background: white;
  border-bottom: 1px solid #e5e7eb;
  padding: 16px 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.quick-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  box-sizing: border-box;
}

.action-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.group-label {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
  white-space: nowrap;
}

.mini-indicators {
  display: flex;
  gap: 20px;
}

.mini-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 11px;
  color: #6b7280;
}

.indicator-bar {
  width: 60px;
  height: 4px;
  background: #e5e7eb;
  border-radius: 2px;
  overflow: hidden;
}

.indicator-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #10b981);
  transition: width 0.3s ease;
}

.indicator-value {
  font-weight: 600;
  color: #374151;
}

/* 主工作区样式 */
.main-workspace {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 32px;
  gap: 32px;
  min-height: calc(100vh - 200px);
  box-sizing: border-box;
}

.input-workspace {
  flex: 0 0 420px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.preview-workspace {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 工作区节/卡片样式 */
.workspace-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

.workspace-section:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
}

.section-header i {
  color: #667eea;
  font-size: 18px;
  margin-right: 8px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
}

.section-actions {
  display: flex;
  gap: 8px;
}

/* 输入容器样式 */
.input-container {
  padding: 24px;
}

.input-tabs .el-tabs__header {
  margin-bottom: 20px;
}

.voice-input-area {
    display: flex;
  flex-direction: column;
    align-items: center;
  gap: 16px;
  padding: 32px 20px;
}

.voice-btn {
  border-radius: 50px;
  padding: 12px 32px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.voice-result {
  width: 100%;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border: 1px solid #bae6fd;
  border-radius: 12px;
  padding: 16px;
  margin-top: 16px;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
    font-weight: 600;
  color: #0c4a6e;
}

.result-content {
  color: #1e40af;
  line-height: 1.5;
}

.upload-area {
  padding: 20px;
}

.upload-dragger {
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  background: #fafbfc;
  transition: all 0.3s ease;
}

.upload-dragger:hover {
  border-color: #667eea;
  background: #f8fafc;
}

/* 资源网格样式 */
.resource-grid {
  padding: 24px;
  display: grid;
  gap: 16px;
}

.resource-grid-grid {
  grid-template-columns: repeat(2, 1fr);
}

.resource-grid-list {
  grid-template-columns: 1fr;
}

/* 资源卡片样式 */
.resource-card {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 16px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
}

.resource-card:hover {
  border-color: #667eea;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.resource-card.active {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.resource-grid-grid .resource-card {
  flex-direction: column;
  text-align: center;
  padding: 24px 16px;
  min-height: 140px;
}

.card-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.resource-card.active .card-icon {
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.card-icon i {
  font-size: 24px;
  color: #667eea;
}

.resource-card.active .card-icon i {
  color: white;
}

.card-content {
  flex: 1;
}

.card-content h4 {
  margin: 0 0 6px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.resource-card.active .card-content h4 {
  color: white;
}

.card-content p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.4;
}

.resource-card.active .card-content p {
  color: rgba(255, 255, 255, 0.9);
}

.card-selected {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 24px;
  height: 24px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: checkmark 0.3s ease-out;
}

.card-selected i {
  color: #667eea;
  font-size: 14px;
}

@keyframes checkmark {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 配置网格样式 */
.config-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  padding: 24px;
}

.config-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.config-item-full {
  grid-column: 1 / -1;
}

.config-item label {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

/* 生成控制面板样式 */
.generate-panel {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
}

.generate-button-area {
  text-align: center;
  margin-bottom: 16px;
}

.main-generate-btn {
  padding: 16px 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 50px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.main-generate-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.generate-options {
  display: flex;
  justify-content: center;
}

/* 预览面板样式 */
.preview-panel {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  overflow: hidden;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
}

.preview-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1f2937;
}

.preview-title i {
  color: #667eea;
  font-size: 18px;
}

.preview-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-indicator {
  font-size: 12px;
  color: #f59e0b;
  font-weight: 600;
  animation: pulse 2s infinite;
}

.preview-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 24px;
}

/* 生成进度覆盖层样式 */
.generating-overlay {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px 20px;
}

.progress-circle {
  margin-bottom: 24px;
}

.progress-text {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 20px;
}

.progress-details {
  width: 100%;
  max-width: 300px;
}

.quality-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quality-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.quality-item span {
  width: 60px;
  font-size: 12px;
  color: #6b7280;
}

.quality-bar {
  flex: 1;
  height: 6px;
  background: #f3f4f6;
  border-radius: 3px;
  overflow: hidden;
}

.quality-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px;
  transition: width 0.8s ease;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 60px 40px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.empty-icon i {
  font-size: 32px;
  color: #9ca3af;
}

.empty-state h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #374151;
}

.empty-state p {
  margin: 0 0 24px 0;
  color: #6b7280;
  font-size: 14px;
}

.empty-tips {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;
}

.tip-item i {
  color: #667eea;
  font-size: 14px;
}

/* 结果显示样式 */
.result-display {
  width: 100%;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.result-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.result-actions {
  display: flex;
  gap: 8px;
}

.result-content {
  margin-bottom: 20px;
}

.media-preview {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.media-preview video,
.media-preview audio,
.media-preview img {
  width: 100%;
  border-radius: 12px;
}

.document-preview {
  padding: 40px;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  border-radius: 12px;
  border: 2px dashed #d1d5db;
}

.preview-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #6b7280;
}

.preview-placeholder i {
  font-size: 48px;
  margin-bottom: 12px;
  color: #9ca3af;
}

.result-info {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.info-value {
  font-size: 14px;
  color: #1f2937;
  font-weight: 600;
}

/* 预览小贴士样式 */
.preview-tips {
  margin-top: 24px;
  padding: 16px;
  background: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 8px;
  text-align: left;
}

.preview-tips h4 {
  margin: 0 0 8px 0;
  color: #1e40af;
  font-size: 14px;
}

.preview-tips ul {
  margin: 0;
  padding-left: 16px;
  color: #1e40af;
}

.preview-tips li {
  margin-bottom: 4px;
  font-size: 12px;
}

/* 预览头部样式 */
.preview-header {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.content-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.content-description {
  margin: 0 0 16px 0;
  color: #64748b;
  font-size: 14px;
  line-height: 1.5;
}

.content-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.stat-item i {
  color: #3b82f6;
}

/* 预览工具栏样式 */
.preview-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px 16px;
}

.toolbar-buttons {
  display: flex;
  gap: 8px;
}

.quality-badge {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 预览信息样式 */
.preview-info {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 6px;
  font-size: 12px;
  color: #606266;
}

/* 音频波形图样式 */
.waveform-placeholder,
.mindmap-placeholder,
.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 120px;
  background: #f5f7fa;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  color: #9ca3af;
  margin-top: 12px;
}

.waveform-placeholder i,
.mindmap-placeholder i,
.chart-placeholder i {
  font-size: 32px;
  margin-bottom: 8px;
}

/* 状态指示器样式 */
.generate-progress {
  margin-left: 8px;
  font-size: 12px;
  color: #f59e0b;
  font-weight: 600;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
}

/* 改进按钮样式 */
.generate-control .el-button {
  padding: 12px 32px;
  font-size: 16px;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.generate-control .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.3);
}

/* 语音输入动画 */
.voice-input .el-button.is-loading {
  animation: voiceRecord 1.5s infinite;
}

@keyframes voiceRecord {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* 上传区域优化 */
.el-upload-dragger {
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  background: #fafafa;
  transition: all 0.3s ease;
}

.el-upload-dragger:hover {
  border-color: #3B82F6;
  background: #f0f9ff;
}

/* 配置面板动画 */
.config-panel .el-form-item {
  transition: all 0.3s ease;
}

.config-panel .el-form-item:hover {
  background: #f8fafc;
  border-radius: 8px;
  padding: 8px;
  margin: -8px;
}

/* 智能推荐按钮 */
.el-button.is-primary {
  background: linear-gradient(135deg, #3B82F6 0%, #1E40AF 100%);
  border: none;
}

.el-button.is-success {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  border: none;
}

/* 模拟内容预览优化 */
.preview-result video,
.preview-result audio,
.preview-result img,
.preview-result iframe {
  border-radius: 8px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

/* 质量指标动画 */
.quality-metrics .el-progress {
  transition: all 0.8s ease;
}

.metric-value {
  transition: all 0.5s ease;
  font-family: 'Monaco', 'Menlo', monospace;
}

/* 加载状态优化 */
.generating-state {
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 智能建议面板样式 */
.suggestions-panel {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  font-weight: 600;
  color: #1f2937;
}

.panel-header i {
  color: #667eea;
  font-size: 16px;
}

.suggestions-list {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.suggestion-card:hover {
  border-color: #667eea;
  background: linear-gradient(135deg, #f8fafc 0%, #f0f9ff 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.suggestion-card i {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.suggestion-info {
  flex: 1;
}

.suggestion-info h5 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.suggestion-info p {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
  line-height: 1.4;
}

/* AI助手侧边栏样式 */
.ai-sidebar {
  position: fixed;
  right: 0;
  top: 0;
  bottom: 0;
  width: 380px;
  background: white;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 1000;
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.3s ease;
}

.slide-right-enter,
.slide-right-leave-to {
  transform: translateX(100%);
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.assistant-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.assistant-info i {
  font-size: 24px;
}

.assistant-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.assistant-info p {
  margin: 0;
  font-size: 12px;
  opacity: 0.9;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8fafc;
}

.chat-message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.chat-message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.chat-message.user .message-avatar {
  background: #667eea;
  color: white;
}

.chat-message.assistant .message-avatar {
  background: #10b981;
  color: white;
}

.message-bubble {
  max-width: 70%;
}

.message-text {
  background: white;
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  word-wrap: break-word;
}

.chat-message.user .message-text {
  background: #667eea;
  color: white;
}

.message-time {
  font-size: 11px;
  color: #9ca3af;
  margin-top: 6px;
  text-align: right;
}

.chat-message.user .message-time {
  text-align: left;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #e5e7eb;
  background: white;
}

/* 创新功能工具栏样式 */
.innovation-toolbar {
  background: linear-gradient(135deg, #f0f9ff 0%, #f8fafc 100%);
  border: 1px solid #e1f5fe;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.toolbar-section {
  margin-bottom: 16px;
}

.toolbar-section:last-child {
  margin-bottom: 0;
}

.toolbar-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #1e40af;
}

.innovation-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.analysis-indicators {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.indicator {
  display: flex;
  align-items: center;
  gap: 12px;
}

.indicator-label {
  width: 80px;
  font-size: 12px;
  color: #606266;
}

.indicator .el-progress {
  flex: 1;
}

.indicator-value {
  width: 40px;
  text-align: right;
  font-size: 12px;
  font-weight: 600;
  color: #2c3e50;
}

/* 生成模式选择样式 */
.generate-modes {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}

/* 版本管理样式 */
.version-history {
  max-height: 300px;
  overflow-y: auto;
}

.version-timeline {
  position: relative;
  padding-left: 20px;
}

.version-timeline::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e5e7eb;
}

.version-item {
  position: relative;
  padding: 12px 0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.version-item:hover {
  background: #f8fafc;
  border-radius: 8px;
  margin: 0 -8px;
  padding-left: 20px;
}

.version-item.active {
  background: #f0f9ff;
  border-radius: 8px;
  margin: 0 -8px;
  padding-left: 20px;
}

.version-indicator {
  position: absolute;
  left: -12px;
  top: 16px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #3B82F6;
  border: 2px solid white;
  box-shadow: 0 0 0 2px #e5e7eb;
}

.version-item.active .version-indicator {
  background: #10B981;
}

.version-header {
  display: flex;
  justify-content: between;
  align-items: center;
  margin-bottom: 8px;
}

.version-name {
  font-weight: 600;
  color: #2c3e50;
}

.version-time {
  font-size: 11px;
  color: #909399;
  margin-left: auto;
}

.version-changes {
  margin-bottom: 8px;
}

.version-changes .el-tag {
  margin-right: 4px;
  margin-bottom: 4px;
}

.version-metrics {
  display: flex;
  gap: 16px;
}

.metric {
  font-size: 11px;
  color: #606266;
}

/* 协作面板样式 */
.collaboration-content {
  padding: 16px;
}

.online-users {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.user-avatar {
  position: relative;
  cursor: pointer;
}

.user-status {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  border: 2px solid white;
}

.user-status.online {
  background: #67C23A;
}

.user-status.editing {
  background: #E6A23C;
}

.user-status.viewing {
  background: #909399;
}

.activity-feed {
  max-height: 150px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border-radius: 6px;
  margin-bottom: 4px;
  font-size: 12px;
}

.activity-item:hover {
  background: #f8fafc;
}

.activity-item i {
  color: #3B82F6;
  width: 16px;
}

.activity-time {
  margin-left: auto;
  color: #909399;
}

.collaboration-controls {
  display: flex;
  gap: 8px;
}

/* 增强质量分析样式 */
.enhanced-quality-analysis {
  padding: 16px 0;
}

.quality-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.quality-header h4 {
  margin: 0;
  font-size: 16px;
  color: #2c3e50;
}

.quality-radar {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 20px;
}

.radar-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.radar-item span {
  width: 60px;
  font-size: 12px;
  color: #606266;
}

.improvement-suggestions {
  background: #f8fafc;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.improvement-suggestions h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #2c3e50;
}

.improvement-suggestions ul {
  margin: 0;
  padding: 0;
  list-style: none;
}

.improvement-suggestions li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
}

.improvement-suggestions li:last-child {
  border-bottom: none;
}

.improvement-suggestions i {
  color: #3B82F6;
  width: 16px;
}

/* 3D预览样式 */
.preview-3d-dialog .el-dialog__body {
  padding: 0;
}

.preview-3d-container {
  height: 500px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-placeholder-3d {
  text-align: center;
  color: white;
}

.preview-placeholder-3d h3 {
  margin: 16px 0;
  font-size: 24px;
}

.preview-placeholder-3d p {
  margin-bottom: 24px;
  opacity: 0.9;
}

.preview-controls-3d {
  display: flex;
  gap: 12px;
  justify-content: center;
}

/* 协作状态栏样式 */
.collaboration-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 999;
}

.collab-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
}

.collab-info i {
  font-size: 16px;
}

.collab-users {
  display: flex;
  align-items: center;
  gap: 8px;
}

.more-users {
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

/* 动画效果 */
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.workspace-section {
  animation: fadeIn 0.6s ease-out;
}

.workspace-section:nth-child(1) { animation-delay: 0.1s; }
.workspace-section:nth-child(2) { animation-delay: 0.2s; }
.workspace-section:nth-child(3) { animation-delay: 0.3s; }

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-workspace {
    flex-direction: column;
    padding: 20px;
    gap: 24px;
  }
  
  .input-workspace {
    flex: none;
    width: 100%;
  }
  
  .resource-grid-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .config-grid {
    grid-template-columns: 1fr;
  }
  
  .info-grid {
    grid-template-columns: 1fr 1fr;
  }
  
  .ai-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .quick-actions {
    flex-direction: column;
    gap: 16px;
  }
  
  .action-group {
    justify-content: center;
  }
  
  .mini-indicators {
    flex-direction: column;
    gap: 12px;
  }
  
  .main-workspace {
    padding: 16px;
  }
  
  .resource-grid-grid {
    grid-template-columns: 1fr;
  }
  
  .config-grid {
    padding: 16px;
  }
  
  .result-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .suggestion-card {
    flex-direction: column;
    text-align: center;
    gap: 8px;
  }
  
  .collaboration-bar {
    flex-direction: column;
    gap: 8px;
    padding: 16px;
  }
  
  .collab-users {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .page-header {
    padding: 20px 16px;
  }
  
  .quick-start-bar {
    padding: 12px 16px;
  }
  
  .main-workspace {
    padding: 12px;
    gap: 16px;
  }
  
  .workspace-section {
    border-radius: 12px;
  }
  
  .section-header {
    padding: 16px;
  }
  
  .input-container,
  .config-grid {
    padding: 16px;
  }
  
  .ai-sidebar {
    width: 100vw;
  }
}

/* 资源管理面板样式 */
.resource-manager-panel {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  overflow: hidden;
  margin-top: 24px;
}

.resource-manager-panel .panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  color: #1f2937;
}

.header-left i {
  color: #667eea;
  font-size: 18px;
}

.resource-count-badge {
  margin-left: 8px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 资源筛选样式 */
.resource-filters {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: #fafbfc;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.advanced-filters {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  margin-top: 16px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-item label {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 资源列表容器样式 */
.resource-list-container {
  padding: 20px 24px;
}

.list-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

.list-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #6b7280;
}

.search-stats {
  color: #3b82f6;
  font-weight: 500;
}

/* 资源列表样式 */
.resource-list {
  display: grid;
  gap: 16px;
}

.resource-list-grid {
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
}

.resource-list-list {
  grid-template-columns: 1fr;
}

/* 资源项样式 */
.resource-item {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;
}

.resource-item:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

/* 网格视图资源项 */
.resource-item-grid {
  display: flex;
  flex-direction: column;
}

.resource-thumbnail {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #f8fafc;
}

.resource-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
}

.thumbnail-placeholder i {
  font-size: 48px;
  color: #9ca3af;
}

.video-play-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  opacity: 0.9;
  transition: all 0.3s ease;
}

.video-play-overlay:hover {
  background: rgba(0, 0, 0, 0.9);
  transform: translate(-50%, -50%) scale(1.1);
}

.resource-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.resource-item:hover .resource-overlay {
  opacity: 1;
}

.overlay-actions {
  display: flex;
  gap: 8px;
}

.expiry-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #f59e0b;
  color: white;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

.quality-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  color: white;
  display: flex;
  align-items: center;
  gap: 4px;
}

.quality-excellent {
  background: #10b981;
}

.quality-good {
  background: #3b82f6;
}

.quality-average {
  background: #f59e0b;
}

.quality-poor {
  background: #ef4444;
}

.resource-info {
  padding: 16px;
}

.resource-title {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.resource-subject {
  font-size: 12px;
  color: #6b7280;
}

.resource-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #9ca3af;
}

.stat-item i {
  font-size: 12px;
}

.resource-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;
}

.more-tags {
  font-size: 11px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 2px 6px;
  border-radius: 4px;
}

/* 列表视图资源项 */
.resource-item-list {
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 16px;
}

.resource-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.resource-content {
  flex: 1;
  min-width: 0;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.resource-badges {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quality-score {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 4px;
  color: white;
}

.resource-description {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.resource-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #9ca3af;
}

.meta-item.expiring {
  color: #f59e0b;
  font-weight: 600;
}

.resource-actions {
  display: flex;
  gap: 8px;
}

/* 空状态样式 */
.resource-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 80px 40px;
}

.resource-empty-state .empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.resource-empty-state .empty-icon i {
  font-size: 32px;
  color: #9ca3af;
}

.resource-empty-state h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #374151;
}

.resource-empty-state p {
  margin: 0 0 24px 0;
  color: #6b7280;
  font-size: 14px;
}

.empty-actions {
  display: flex;
  gap: 12px;
}

/* 分页样式 */
.resource-pagination {
  margin-top: 24px;
  text-align: center;
}

/* 资源预览对话框样式 */
.resource-preview-dialog .el-dialog {
  margin-top: 5vh !important;
  margin-bottom: 5vh !important;
  max-height: 90vh;
  overflow: hidden;
}

.resource-preview-dialog .el-dialog__body {
  padding: 0;
  max-height: calc(90vh - 120px);
  overflow: auto;
}

.resource-preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.preview-header-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
}

.resource-basic-info {
  display: flex;
  gap: 20px;
  flex: 1;
}

.resource-icon-large {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
  flex-shrink: 0;
}

.resource-details {
  flex: 1;
}

.resource-details h2 {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.resource-meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.meta-divider {
  color: #d1d5db;
}

.meta-subject,
.meta-grade {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.quality-display {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  color: white;
}

.resource-tags-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-item {
  margin: 0;
}

.preview-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 20px;
}

.preview-content-area {
  display: flex;
  flex: 1;
  min-height: 500px;
}

.preview-main {
  flex: 1;
  padding: 24px;
  background: #fafbfc;
}

.media-preview-large {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 视频容器样式 */
.video-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.video-info-panel {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
}

.video-header h4 {
  margin: 0 0 8px 0;
  color: #1e293b;
  font-size: 18px;
  font-weight: 600;
}

.video-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #64748b;
}

.video-meta .duration,
.video-meta .resolution,
.video-meta .platform {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

.video-meta .platform {
  background: #e0f2fe;
  color: #0277bd;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.video-meta .platform i {
  font-size: 12px;
}

.platform-badge {
  margin-left: 4px;
  font-size: 14px;
}

.video-chapters {
  margin-top: 16px;
}

.video-chapters h5 {
  margin: 0 0 12px 0;
  color: #334155;
  font-size: 14px;
  font-weight: 600;
}

.chapters-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chapter-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
  cursor: pointer;
}

.chapter-item:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  transform: translateX(2px);
}

.chapter-time {
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 12px;
  color: #3b82f6;
  font-weight: 600;
  min-width: 50px;
}

.chapter-title {
  font-size: 13px;
  color: #475569;
  font-weight: 500;
}

.audio-player-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.audio-visualizer {
  height: 120px;
  background: #f3f4f6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.wave-placeholder {
  text-align: center;
  color: #9ca3af;
}

.wave-placeholder i {
  font-size: 32px;
  margin-bottom: 8px;
  display: block;
}

.mindmap-viewer,
.chart-viewer,
.illustration-viewer {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.document-preview-large {
  background: white;
  border-radius: 12px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.document-placeholder i {
  font-size: 64px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.document-placeholder h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #374151;
}

.document-placeholder p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

/* 预览侧边栏样式 */
.preview-sidebar {
  width: 320px;
  background: white;
  border-left: 1px solid #e5e7eb;
  padding: 24px;
  overflow-y: auto;
}

.info-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f1f5f9;
}

.info-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.info-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-section i {
  color: #667eea;
  font-size: 16px;
}

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

.info-value {
  font-size: 13px;
  color: #1f2937;
  font-weight: 600;
  text-align: right;
  max-width: 60%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 质量分析样式 */
.quality-analysis {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.quality-meter {
  text-align: center;
}

.meter-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 8px;
}

.quality-text {
  font-size: 12px;
  color: #374151;
  font-weight: 600;
  margin-top: 8px;
}

.quality-metrics {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.metric-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.metric-label {
  width: 50px;
  font-size: 12px;
  color: #6b7280;
}

.metric-bar {
  flex: 1;
  height: 6px;
  background: #f3f4f6;
  border-radius: 3px;
  overflow: hidden;
}

.metric-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px;
  transition: width 0.8s ease;
}

.metric-value {
  width: 35px;
  text-align: right;
  font-size: 12px;
  font-weight: 600;
  color: #374151;
}

/* 时效性信息样式 */
.expiry-info {
  padding: 12px;
  border-radius: 8px;
}

.expiry-warning {
  background: #fef3c7;
  border: 1px solid #f59e0b;
  color: #92400e;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 500;
}

.expiry-normal {
  background: #d1fae5;
  border: 1px solid #10b981;
  color: #047857;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 500;
}

/* 相关资源样式 */
.related-resources {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.related-item:hover {
  border-color: #667eea;
  background: #f8fafc;
}

.related-item i {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  font-size: 14px;
}

.related-info {
  flex: 1;
  min-width: 0;
}

.related-name {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.related-meta {
  font-size: 11px;
  color: #9ca3af;
}

/* 响应式样式 */
@media (max-width: 1200px) {
  .resource-list-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
  
  .filter-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .preview-content-area {
    flex-direction: column;
  }
  
  .preview-sidebar {
    width: 100%;
    border-left: none;
    border-top: 1px solid #e5e7eb;
  }
}

@media (max-width: 768px) {
  .resource-manager-panel .panel-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: space-between;
  }
  
  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-grid {
    grid-template-columns: 1fr;
  }
  
  .list-toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .sort-options {
    flex-direction: column;
    gap: 8px;
    align-items: stretch;
  }
  
  .resource-list-grid {
    grid-template-columns: 1fr;
  }
  
  .resource-item-list {
    flex-direction: column;
    gap: 12px;
  }
  
  .resource-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .resource-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .resource-actions {
    justify-content: center;
  }
  
  .preview-header-info {
    flex-direction: column;
    gap: 20px;
  }
  
  .resource-basic-info {
    flex-direction: column;
    gap: 16px;
  }
  
  .preview-actions {
    flex-direction: row;
    justify-content: center;
  }
  
  .resource-meta-row {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .resource-preview-dialog .el-dialog {
    width: 95% !important;
    margin: 2.5vh auto !important;
  }
}

/* 打印样式 */
@media print {
  .header-actions,
  .quick-start-bar,
  .ai-sidebar,
  .collaboration-bar,
  .suggestions-panel,
  .resource-manager-panel {
    display: none !important;
  }
  
  .main-workspace {
    flex-direction: column;
  }
  
  .preview-workspace {
    break-inside: avoid;
  }
}

/* 资源编辑器对话框样式 */
.resource-editor-dialog {
  margin-top: 5vh !important;
  margin-bottom: 5vh !important;
}

.resource-editor-dialog .el-dialog {
  max-height: 90vh;
  overflow: hidden;
  border-radius: 12px;
}

.resource-editor-dialog .el-dialog__body {
  padding: 0;
  height: 85vh;
  overflow: hidden;
}

.resource-editor-dialog .el-dialog__header {
  padding: 16px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.resource-editor-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

/* === 思维导图预览样式 === */
.mindmap-preview-container {
  padding: 20px;
}

.mindmap-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
  padding: 12px;
  background: #f3f4f6;
  border-radius: 6px;
}

.mindmap-info .info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.mindmap-info .info-item .label {
  color: #6b7280;
  font-weight: 500;
}

.mindmap-info .info-item .value {
  color: #1f2937;
  font-weight: 600;
}

.mindmap-visualization {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  min-height: 300px;
  position: relative;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 12px;
  padding: 20px;
}

.center-node {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 20px 32px;
  background: linear-gradient(135deg, #1e40af, #3b82f6);
  color: white;
  border-radius: 16px;
  font-weight: bold;
  font-size: 18px;
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.4);
  animation: fadeInScale 0.5s ease-out;
  text-align: center;
  line-height: 1.4;
  min-width: 280px;
  border: 3px solid rgba(255, 255, 255, 0.2);
}

.center-node i {
  font-size: 20px;
  color: #fbbf24;
}

.branches {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  justify-content: center;
  max-width: 900px;
  width: 100%;
}

.branches.layout-horizontal {
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.branches.layout-vertical {
  grid-template-columns: 1fr;
  max-width: 400px;
}

.branches.layout-radial {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.branch-node {
  display: flex;
  flex-direction: column;
  background: white;
  border: 3px solid #e5e7eb;
  border-radius: 12px;
  font-size: 14px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  animation: slideInUp 0.5s ease-out both;
  cursor: default;
  overflow: hidden;
  min-height: 180px;
}

.branch-node:hover {
  border-color: #3b82f6;
  box-shadow: 0 8px 25px rgba(59, 130, 246, 0.2);
  transform: translateY(-3px);
}

.branch-main {
  padding: 16px 20px;
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  font-weight: bold;
  font-size: 16px;
  color: #1e293b;
  border-bottom: 2px solid #cbd5e1;
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 60px;
}

.branch-main i {
  color: #3b82f6;
  font-size: 18px;
}

.branch-children {
  padding: 12px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.child-node {
  display: flex;
  align-items: flex-start;
  padding: 8px 12px;
  margin: 0;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 13px;
  color: #475569;
  animation: slideInRight 0.3s ease-out;
  line-height: 1.4;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  transition: all 0.2s ease;
}

.child-node:hover {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  border-color: #3b82f6;
  transform: translateX(4px);
}

.child-node i {
  margin-right: 8px;
  font-size: 12px;
  color: #3b82f6;
  margin-top: 2px;
  flex-shrink: 0;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 分支主题样式 */
.theme-blue .branch-node {
  border-color: #bfdbfe;
}

.theme-green .branch-node {
  border-color: #bbf7d0;
}

.theme-orange .branch-node {
  border-color: #fed7aa;
}

.theme-purple .branch-node {
  border-color: #e9d5ff;
}

/* 数学教学分支特殊样式 */
.branch-formula {
  border-color: #fbbf24 !important;
}

.branch-formula .branch-main {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #92400e;
}

.branch-formula .branch-main i {
  color: #d97706;
}

.branch-graph {
  border-color: #34d399 !important;
}

.branch-graph .branch-main {
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  color: #065f46;
}

.branch-graph .branch-main i {
  color: #059669;
}

.branch-concept {
  border-color: #a78bfa !important;
}

.branch-concept .branch-main {
  background: linear-gradient(135deg, #ede9fe, #ddd6fe);
  color: #5b21b6;
}

.branch-concept .branch-main i {
  color: #7c3aed;
}

.branch-application {
  border-color: #fb7185 !important;
}

.branch-application .branch-main {
  background: linear-gradient(135deg, #fce7f3, #fbcfe8);
  color: #be185d;
}

.branch-application .branch-main i {
  color: #e11d48;
}

.empty-branches {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px;
  color: #9ca3af;
  font-size: 14px;
}

.empty-branches i {
  font-size: 24px;
  color: #d1d5db;
}
</style> 