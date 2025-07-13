<template>
  <div class="ai-ppt-generator">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>🎯 AI智能PPT生成</h1>
          <p>基于您的教学设计数据，智能生成个性化教学PPT</p>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-number">{{ integratedDataCount }}</span>
            <span class="stat-label">数据源</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ estimatedSlides }}</span>
            <span class="stat-label">预计页数</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ estimatedTime }}</span>
            <span class="stat-label">分钟</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据整合状态 -->
    <div class="data-integration-panel">
      <el-card class="integration-card">
        <div slot="header" class="card-header">
          <i class="el-icon-connection"></i>
          <span>数据整合状态</span>
          <el-button type="text" size="small" @click="refreshIntegration">
            <i class="el-icon-refresh"></i> 刷新数据
          </el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="quickStartDemo"
            v-if="integratedDataCount >= 3"
            style="color: #67C23A;"
          >
            <i class="el-icon-video-play"></i> 快速预览
          </el-button>
        </div>
        
        <div class="integration-grid">
          <div 
            v-for="source in dataSources" 
            :key="source.id"
            :class="['integration-item', { 'integrated': source.integrated, 'missing': !source.integrated }]"
          >
            <div class="integration-icon">
              <i :class="source.icon"></i>
            </div>
            <div class="integration-info">
              <h4>{{ source.name }}</h4>
              <p>{{ source.description }}</p>
              <div class="integration-data" v-if="source.integrated">
                <el-tag size="mini" type="success">{{ source.dataCount }}项数据</el-tag>
              </div>
            </div>
            <div class="integration-status">
              <i v-if="source.integrated" class="el-icon-check" style="color: #67C23A;"></i>
              <i v-else class="el-icon-warning" style="color: #E6A23C;"></i>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- PPT生成配置 -->
    <div class="generation-config">
      <el-row :gutter="20">
        <!-- 模板选择 -->
        <el-col :span="12">
          <el-card class="config-card">
            <div slot="header" class="card-header">
              <i class="el-icon-picture-outline"></i>
              <span>智能模板推荐</span>
            </div>
            
            <div class="template-selector">
              <div class="template-grid">
                <div 
                  v-for="template in recommendedTemplates" 
                  :key="template.id"
                  :class="['template-item', { 'selected': selectedTemplate === template.id }]"
                  @click="selectTemplate(template.id)"
                >
                  <div class="template-preview">
                    <!-- CSS生成的预览图 -->
                    <div :class="['template-mock', `template-${template.previewType}`]">
                      <div class="mock-header" :style="{ backgroundColor: template.colors[0] }">
                        <div class="mock-title"></div>
                        <div class="mock-subtitle"></div>
                      </div>
                      <div class="mock-content">
                        <div class="mock-text-block" :style="{ backgroundColor: template.colors[2] }"></div>
                        <div class="mock-text-block" :style="{ backgroundColor: template.colors[2] }"></div>
                        <div class="mock-chart" :style="{ backgroundColor: template.colors[1] }"></div>
                      </div>
                    </div>
                    <div class="template-overlay">
                      <i class="el-icon-view"></i>
                    </div>
                  </div>
                  <div class="template-info">
                    <h5>{{ template.name }}</h5>
                    <div class="template-tags">
                      <el-tag 
                        v-for="tag in template.tags" 
                        :key="tag" 
                        size="mini" 
                        type="info"
                      >
                        {{ tag }}
                      </el-tag>
                    </div>
                    <div class="template-match">
                      <span class="match-score">匹配度: {{ template.matchScore }}%</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 个性化配置 -->
        <el-col :span="12">
          <el-card class="config-card">
            <div slot="header" class="card-header">
              <i class="el-icon-setting"></i>
              <span>个性化配置</span>
            </div>
            
            <div class="personalization-config">
              <div class="config-section">
                <h4>学习风格适配</h4>
                <el-radio-group v-model="learningStyleFocus" size="small">
                  <el-radio-button label="visual">视觉型导向</el-radio-button>
                  <el-radio-button label="auditory">听觉型导向</el-radio-button>
                  <el-radio-button label="kinesthetic">动觉型导向</el-radio-button>
                  <el-radio-button label="mixed">混合型</el-radio-button>
                </el-radio-group>
              </div>

              <div class="config-section">
                <h4>难度调整</h4>
                <el-slider
                  v-model="difficultyLevel"
                  :min="1"
                  :max="5"
                  :marks="difficultyMarks"
                  show-tooltip
                  tooltip-class="difficulty-tooltip"
                ></el-slider>
              </div>

              <div class="config-section">
                <h4>互动元素</h4>
                <el-checkbox-group v-model="interactiveElements">
                  <el-checkbox label="polls">投票问答</el-checkbox>
                  <el-checkbox label="quiz">随堂测验</el-checkbox>
                  <el-checkbox label="discussion">讨论提示</el-checkbox>
                  <el-checkbox label="activity">活动指导</el-checkbox>
                </el-checkbox-group>
              </div>

              <div class="config-section">
                <h4>特殊需求支持</h4>
                <el-switch
                  v-model="specialNeedsSupport"
                  active-text="启用"
                  inactive-text="关闭"
                ></el-switch>
                <p class="config-note">为{{ specialNeedsCount }}名特殊需求学生提供辅助功能</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- PPT结构预览 -->
    <div class="structure-preview">
      <el-card class="preview-card">
        <div slot="header" class="card-header">
          <i class="el-icon-document"></i>
          <span>PPT结构预览</span>
          <el-button type="primary" size="small" @click="generatePPT" :loading="generating">
            <i class="el-icon-magic-stick"></i> 
            {{ generating ? '生成中...' : '生成PPT' }}
          </el-button>
          <el-button 
            type="success" 
            size="small" 
            @click="generateDemoPPT" 
            :loading="generating"
            v-if="integratedDataCount < 3"
          >
            <i class="el-icon-view"></i> 
            {{ generating ? '生成中...' : '快速演示' }}
          </el-button>
        </div>
        
        <div class="slides-structure">
          <div class="slides-toolbar">
            <el-button-group>
              <el-button :type="isEditMode ? 'primary' : 'default'" @click="toggleEditMode">
                <i class="el-icon-edit"></i>
                {{ isEditMode ? '完成编辑' : '编辑结构' }}
              </el-button>
              <el-button @click="addSlide">
                <i class="el-icon-plus"></i>
                添加幻灯片
              </el-button>
              <el-button @click="resetStructure">
                <i class="el-icon-refresh"></i>
                重置结构
              </el-button>
            </el-button-group>
            <div class="toolbar-info">
              <span>总计: {{ slidesStructure.length }} 张幻灯片</span>
              <span>总时长: {{ estimatedTime }} 分钟</span>
            </div>
          </div>
          
          <div class="slides-timeline">
            <div 
              v-for="(slide, index) in slidesStructure" 
              :key="slide.id"
              :class="['slide-node', { 'editing': editingSlide === slide.id }]"
            >
              <div class="slide-connector" v-if="index > 0"></div>
              <div class="slide-circle">
                <span>{{ index + 1 }}</span>
              </div>
              <div class="slide-content">
                <!-- 幻灯片头部 -->
                <div class="slide-header">
                  <div v-if="editingSlide === slide.id" class="slide-header-edit">
                    <el-input 
                      v-model="slide.title" 
                      size="small" 
                      placeholder="输入幻灯片标题"
                      @blur="saveSlideEdit(slide)"
                    ></el-input>
                    <el-input 
                      v-model="slide.content.subtitle" 
                      size="mini" 
                      placeholder="输入子标题"
                      class="subtitle-input"
                    ></el-input>
                  </div>
                  <div v-else>
                  <h5>{{ slide.title }}</h5>
                    <p class="slide-subtitle" v-if="slide.content.subtitle">{{ slide.content.subtitle }}</p>
                  </div>
                  
                  <div class="slide-meta">
                    <el-tag :type="getSlideTypeColor(slide.type)" size="mini">
                      {{ slide.typeLabel }}
                    </el-tag>
                    <div class="time-edit" v-if="editingSlide === slide.id">
                      <el-input-number 
                        v-model="slide.estimatedTime" 
                        size="mini" 
                        :min="1" 
                        :max="30"
                        controls-position="right"
                      ></el-input-number>
                      <span>分钟</span>
                  </div>
                    <span v-else class="slide-time">{{ slide.estimatedTime }}分钟</span>
                </div>
                </div>

                <!-- 幻灯片内容预览 -->
                <div class="slide-preview">
                  <div class="preview-container">
                    <div class="preview-header">内容预览</div>
                    <div class="preview-content">
                      <div class="preview-elements">
                        <div 
                          v-for="(element, idx) in slide.content.elements" 
                          :key="idx"
                          :class="['element-item', `element-${element.type}`]"
                        >
                          <div class="element-icon">
                            <i :class="getElementIcon(element.type)"></i>
                          </div>
                          <div class="element-content">
                            <div v-if="editingSlide === slide.id">
                              <el-input 
                                v-model="element.content" 
                                size="mini" 
                                placeholder="编辑内容"
                              ></el-input>
                            </div>
                            <span v-else>{{ element.content }}</span>
                          </div>
                          <div v-if="editingSlide === slide.id" class="element-actions">
                            <el-button 
                              type="text" 
                              size="mini" 
                              @click="removeElement(slide, idx)"
                            >
                              <i class="el-icon-delete"></i>
                            </el-button>
                          </div>
                        </div>
                      </div>
                      
                      <div v-if="editingSlide === slide.id" class="add-element">
                        <el-dropdown @command="(type) => addElement(slide, type)">
                          <el-button size="mini" type="primary">
                            <i class="el-icon-plus"></i> 添加元素
                          </el-button>
                          <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="text">文本内容</el-dropdown-item>
                            <el-dropdown-item command="image">图片</el-dropdown-item>
                            <el-dropdown-item command="chart">图表</el-dropdown-item>
                            <el-dropdown-item command="quiz">测验</el-dropdown-item>
                            <el-dropdown-item command="animation">动画</el-dropdown-item>
                          </el-dropdown-menu>
                        </el-dropdown>
                      </div>
                    </div>
                    
                    <div class="preview-description">
                      <p>{{ slide.previewContent }}</p>
                    </div>
                  </div>
                </div>

                <!-- 关键点和特色功能 -->
                <div class="slide-details">
                  <div class="slide-points">
                    <div class="points-header">
                      <span>关键要点</span>
                      <el-button 
                        v-if="editingSlide === slide.id" 
                        type="text" 
                        size="mini"
                        @click="addKeyPoint(slide)"
                      >
                        <i class="el-icon-plus"></i>
                      </el-button>
                    </div>
                    <div class="points-list">
                      <div v-for="(point, idx) in slide.keyPoints" :key="idx" class="point-item">
                      <i class="el-icon-arrow-right"></i>
                        <el-input 
                          v-if="editingSlide === slide.id"
                          v-model="slide.keyPoints[idx]"
                          size="mini"
                          placeholder="输入要点"
                        ></el-input>
                        <span v-else>{{ point }}</span>
                        <el-button 
                          v-if="editingSlide === slide.id"
                          type="text" 
                          size="mini"
                          @click="removeKeyPoint(slide, idx)"
                        >
                          <i class="el-icon-delete"></i>
                        </el-button>
                    </div>
                  </div>
                  </div>
                  
                  <div class="slide-features" v-if="slide.features.length > 0">
                    <div class="features-label">特色功能:</div>
                    <div class="features-list">
                      <el-tag 
                        v-for="feature in slide.features" 
                        :key="feature" 
                        size="mini" 
                        type="warning"
                      >
                        {{ feature }}
                      </el-tag>
                    </div>
                  </div>
                </div>

                <!-- 编辑操作按钮 -->
                <div class="slide-actions" v-if="isEditMode">
                  <el-button-group>
                    <el-button 
                      v-if="editingSlide !== slide.id"
                      type="primary" 
                      size="mini"
                      @click="editSlide(slide.id)"
                    >
                      <i class="el-icon-edit"></i> 编辑
                    </el-button>
                    <el-button 
                      v-else
                      type="success" 
                      size="mini"
                      @click="saveSlideEdit(slide)"
                    >
                      <i class="el-icon-check"></i> 保存
                    </el-button>
                    <el-button 
                      type="warning" 
                      size="mini"
                      @click="duplicateSlide(slide)"
                    >
                      <i class="el-icon-copy-document"></i> 复制
                    </el-button>
                    <el-button 
                      type="danger" 
                      size="mini"
                      @click="deleteSlide(slide.id)"
                      :disabled="slidesStructure.length <= 1"
                    >
                      <i class="el-icon-delete"></i> 删除
                    </el-button>
                  </el-button-group>
                  
                  <div class="slide-order">
                    <el-button 
                      type="text" 
                      size="mini"
                      @click="moveSlide(index, -1)"
                      :disabled="index === 0"
                    >
                      <i class="el-icon-top"></i>
                    </el-button>
                    <el-button 
                      type="text" 
                      size="mini"
                      @click="moveSlide(index, 1)"
                      :disabled="index === slidesStructure.length - 1"
                    >
                      <i class="el-icon-bottom"></i>
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 生成进度和结果 -->
    <div v-if="generating || generatedPPT" class="generation-result">
      <el-card class="result-card">
        <div slot="header" class="card-header">
          <i class="el-icon-loading" v-if="generating"></i>
          <i class="el-icon-check" v-else></i>
          <span>{{ generating ? 'PPT生成中...' : 'PPT生成完成' }}</span>
        </div>
        
        <!-- 生成进度 -->
        <div v-if="generating" class="generation-progress">
          <div class="progress-info">
            <h4>{{ generationStage }}</h4>
            <p>{{ generationMessage }}</p>
          </div>
          <el-progress 
            :percentage="generationProgress" 
            :color="progressColors"
            :stroke-width="8"
            text-inside
          ></el-progress>
          <div class="progress-details">
            <div class="detail-item">
              <span>已生成: {{ generatedSlides }}/{{ totalSlides }} 张</span>
            </div>
            <div class="detail-item">
              <span>剩余时间: {{ estimatedRemainingTime }}秒</span>
            </div>
          </div>
        </div>

        <!-- 生成结果 -->
        <div v-if="generatedPPT" class="generation-complete">
          <div class="result-summary">
            <h4>🎉 PPT生成成功！</h4>
            <p>基于您的教学设计，已生成{{ generatedPPT.slideCount }}张个性化PPT</p>
          </div>
          
          <div class="result-actions">
            <el-button-group>
              <el-button type="primary" @click="previewPPT">
                <i class="el-icon-view"></i> 预览PPT
              </el-button>

              <el-button type="success" @click="downloadPPT">
                <i class="el-icon-download"></i> 下载PPT
              </el-button>
              <el-button @click="editPPT">
                <i class="el-icon-edit"></i> 在线编辑
              </el-button>
              <el-button @click="sharePPT">
                <i class="el-icon-share"></i> 分享
              </el-button>
            </el-button-group>
          </div>

          <div class="result-stats">
            <div class="stat-grid">
              <div class="stat-item">
                <span class="stat-label">总页数</span>
                <span class="stat-value">{{ generatedPPT.slideCount }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">文件大小</span>
                <span class="stat-value">{{ generatedPPT.fileSize }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">预计时长</span>
                <span class="stat-value">{{ generatedPPT.duration }}分钟</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">适配度</span>
                <span class="stat-value">{{ generatedPPT.adaptationScore }}%</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- PPT预览对话框 -->
    <el-dialog
      title="PPT预览"
      :visible.sync="pptPreviewVisible"
      width="90%"
      top="5vh"
      custom-class="ppt-preview-dialog"
      :close-on-click-modal="false"
      :show-close="true"
      :modal="true"
      :modal-append-to-body="true"
      :append-to-body="true"
      destroy-on-close
    >
      <div v-if="generatedPPTContent" class="ppt-preview-container">
        <!-- PPT信息头部 -->
        <div class="ppt-info-header">
          <div class="ppt-title-section">
            <h2>{{ generatedPPTContent.title }}</h2>
            <p>{{ generatedPPTContent.subtitle }}</p>
            <div class="ppt-meta">
              <span>模板：{{ generatedPPTContent.templateInfo.name }}</span>
              <span>创建时间：{{ generatedPPTContent.createTime }}</span>
              <span>总计：{{ generatedPPTContent.slides.length }} 张幻灯片</span>
            </div>
          </div>
          <div class="ppt-navigation">
            <el-button-group>
              <el-button 
                :disabled="currentSlideIndex === 0"
                @click="changeSlide(currentSlideIndex - 1)"
              >
                <i class="el-icon-arrow-left"></i> 上一张
              </el-button>
              <el-button 
                :disabled="currentSlideIndex === generatedPPTContent.slides.length - 1"
                @click="changeSlide(currentSlideIndex + 1)"
              >
                下一张 <i class="el-icon-arrow-right"></i>
              </el-button>
            </el-button-group>
            <span class="slide-counter">
              {{ currentSlideIndex + 1 }} / {{ generatedPPTContent.slides.length }}
            </span>
          </div>
        </div>

        <!-- 幻灯片预览区域 -->
        <div class="slide-preview-area">
          <div class="slide-container" v-show="currentSlide">
            <!-- 标准PPT幻灯片 -->
            <div 
              :key="`slide-${currentSlideIndex}`"
              :class="['ppt-slide', `template-${generatedPPTContent.templateInfo.previewType}`]"
            >
              <!-- PPT标题区域 -->
              <div class="ppt-header">
                <h1 class="ppt-title">{{ currentSlide.fullContent.title }}</h1>
                <div class="ppt-subtitle" v-if="currentSlide.fullContent.subtitle">
                  {{ currentSlide.fullContent.subtitle }}
                </div>
              </div>

              <!-- PPT内容区域 -->
              <div class="ppt-content">
                <!-- 导入幻灯片 - PPT格式 -->
                <div v-if="currentSlide.type === 'intro'" class="ppt-intro">
                  <div class="ppt-objectives">
                    <h2 class="section-title">学习目标</h2>
                    <ul class="objectives-list">
                      <li 
                        v-for="(objective, index) in currentSlide.fullContent.learningObjectives" 
                        :key="index"
                        class="objective-item"
                      >
                        {{ objective }}
                      </li>
                    </ul>
                  </div>
                  
                  <div class="ppt-highlight" v-if="currentSlide.fullContent.warmupQuestion">
                    <div class="highlight-title">思考</div>
                    <div class="highlight-content">{{ currentSlide.fullContent.warmupQuestion }}</div>
                  </div>
                </div>

                <!-- 内容讲解幻灯片 - PPT格式 -->
                <div v-if="currentSlide.type === 'content'" class="ppt-content-slide">
                  <!-- 选择显示公式或例题 -->
                  <div v-if="currentSlide.fullContent.keyFormulas && currentSlide.fullContent.keyFormulas.length > 0" class="ppt-formulas">
                    <h2 class="section-title">关键公式</h2>
                    <div class="formula-display">
                      <div 
                        v-for="formula in currentSlide.fullContent.keyFormulas.slice(0, 2)" 
                        :key="formula.name"
                        class="formula-item"
                      >
                        <div class="formula-name">{{ formula.name }}</div>
                        <div class="formula-equation">{{ formula.formula }}</div>
                        <div class="formula-note">{{ formula.description }}</div>
                      </div>
                    </div>
                  </div>
                  
                  <div v-else-if="currentSlide.fullContent.examples && currentSlide.fullContent.examples.length > 0" class="ppt-example">
                    <h2 class="section-title">例题</h2>
                    <div class="example-display">
                      <div class="example-question">
                        {{ currentSlide.fullContent.examples[0].question }}
                      </div>
                      <div class="example-solution">
                        {{ currentSlide.fullContent.examples[0].solution.split('\n')[0] }}
                      </div>
                    </div>
                  </div>
                  
                  <div v-else class="ppt-concepts">
                    <h2 class="section-title">重要概念</h2>
                    <ul class="concept-list">
                      <li 
                        v-for="(point, index) in currentSlide.fullContent.mainContent.slice(0, 4)" 
                        :key="index"
                        class="concept-item"
                      >
                        {{ point.replace(/📐|📊|📍|📈|🎯/g, '') }}
                      </li>
                    </ul>
                  </div>
                </div>

                <!-- 练习幻灯片 - PPT格式 -->
                <div v-if="currentSlide.type === 'practice'" class="ppt-practice">
                  <div v-if="currentSlide.fullContent.exercises && currentSlide.fullContent.exercises.length > 0" class="ppt-exercise">
                    <h2 class="section-title">练习题</h2>
                    <div class="exercise-content">
                      <div class="exercise-question">
                        {{ currentSlide.fullContent.exercises[0].question }}
                      </div>
                      
                      <div v-if="currentSlide.fullContent.exercises[0].options" class="exercise-options">
                        <div 
                          v-for="(option, index) in currentSlide.fullContent.exercises[0].options" 
                          :key="index"
                          class="option-item"
                        >
                          {{ option }}
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div v-else class="ppt-tips">
                    <h2 class="section-title">练习要点</h2>
                    <ul class="tips-list">
                      <li 
                        v-for="(tip, index) in (currentSlide.fullContent.practiceStrategy && currentSlide.fullContent.practiceStrategy.tips && currentSlide.fullContent.practiceStrategy.tips.slice(0, 3)) || ['重点练习配方法', '注意参数讨论', '验证答案合理性']" 
                        :key="index"
                        class="tip-item"
                      >
                        {{ tip.replace(/🔍|📝|📊|✅/g, '') }}
                      </li>
                    </ul>
                  </div>
                </div>

                <!-- 总结幻灯片 - PPT格式 -->
                <div v-if="currentSlide.type === 'summary'" class="ppt-summary">
                  <div class="ppt-key-points">
                    <h2 class="section-title">关键要点</h2>
                    <ul class="key-points-list">
                      <li 
                        v-for="(point, index) in currentSlide.fullContent.keyPoints.slice(0, 4)" 
                        :key="index"
                        class="key-point-item"
                      >
                        {{ point.replace(/🔑/g, '') }}
                      </li>
                    </ul>
                  </div>
                  
                  <div class="ppt-homework" v-if="currentSlide.fullContent.homework">
                    <h2 class="section-title">课后作业</h2>
                    <div class="homework-content">
                      <div 
                        v-for="(hw, index) in currentSlide.fullContent.homework.slice(0, 2)" 
                        :key="index"
                        class="homework-item"
                      >
                        <div v-if="typeof hw === 'object'">
                          {{ hw.content }}
                        </div>
                        <div v-else>
                          {{ hw }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 幻灯片底部信息 -->
              <div class="slide-footer">
                <div class="slide-features">
                  <el-tag 
                    v-for="feature in currentSlide.features" 
                    :key="feature"
                    size="mini"
                    type="info"
                  >
                    {{ feature }}
                  </el-tag>
                </div>
                <div class="slide-time">
                  预计时长：{{ currentSlide.estimatedTime }}分钟
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 幻灯片缩略图导航 -->
        <div class="slide-thumbnails">
          <div class="thumbnails-title">幻灯片导航</div>
          <div class="thumbnails-container">
            <div 
              v-for="(slide, index) in generatedPPTContent.slides" 
              :key="slide.id"
              :class="['thumbnail-item', { 'active': currentSlideIndex === index }]"
              @click="changeSlide(index)"
            >
              <div class="thumbnail-number">{{ index + 1 }}</div>
              <div class="thumbnail-title">{{ slide.title }}</div>
              <div class="thumbnail-type">{{ slide.typeLabel }}</div>
            </div>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="closePPTPreview">关闭预览</el-button>
        <el-button type="primary" @click="downloadPPT">
          <i class="el-icon-download"></i> 下载PPT
        </el-button>
        <el-button type="success" @click="editPPT">
          <i class="el-icon-edit"></i> 编辑PPT
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AiPptGenerator',
  data() {
    return {
      // 数据整合状态
      dataSources: [
        {
          id: 1,
          name: '课程定位',
          description: '学科、年级、教学目标',
          icon: 'el-icon-position',
          integrated: false,
          dataCount: 0
        },
        {
          id: 2,
          name: '教学目标',
          description: '知识、技能、情感目标',
          icon: 'el-icon-aim',
          integrated: false,
          dataCount: 0
        },
        {
          id: 3,
          name: '重难点分析',
          description: '关键知识点和突破策略',
          icon: 'el-icon-star-on',
          integrated: false,
          dataCount: 0
        },
        {
          id: 4,
          name: '学情数据',
          description: '学生特征和能力分析',
          icon: 'el-icon-user',
          integrated: false,
          dataCount: 0
        },
        {
          id: 5,
          name: '教学策略',
          description: '课时规划和教学方法',
          icon: 'el-icon-date',
          integrated: false,
          dataCount: 0
        }
      ],
      
      // 模板推荐
      recommendedTemplates: [
        {
          id: 1,
          name: '现代简约',
          preview: null, // 使用CSS生成预览
          previewType: 'modern',
          tags: ['简洁', '专业', '数学'],
          matchScore: 95,
          description: '适合理科教学，突出逻辑性',
          colors: ['#2c3e50', '#3498db', '#ecf0f1'],
          layout: 'clean'
        },
        {
          id: 2,
          name: '活泼互动',
          preview: null,
          previewType: 'interactive',
          tags: ['互动', '有趣', '学生友好'],
          matchScore: 88,
          description: '适合激发学生兴趣',
          colors: ['#e74c3c', '#f39c12', '#f1c40f'],
          layout: 'colorful'
        },
        {
          id: 3,
          name: '学术专业',
          preview: null,
          previewType: 'academic',
          tags: ['专业', '严谨', '高阶'],
          matchScore: 82,
          description: '适合高难度内容',
          colors: ['#34495e', '#7f8c8d', '#bdc3c7'],
          layout: 'formal'
        }
      ],
      
      selectedTemplate: 1,
      learningStyleFocus: 'visual',
      difficultyLevel: 3,
      difficultyMarks: {
        1: '基础',
        2: '简单',
        3: '适中',
        4: '困难',
        5: '挑战'
      },
      interactiveElements: ['polls', 'discussion'],
      specialNeedsSupport: true,
      specialNeedsCount: 8,
      
      // PPT结构
      slidesStructure: [
        {
          id: 1,
          title: '课程导入',
          type: 'intro',
          typeLabel: '导入',
          estimatedTime: 5,
          keyPoints: ['课程背景', '学习目标', '本节重点'],
          features: ['动态图表', '互动问答'],
          content: {
            subtitle: '探索数学的奥秘',
            elements: [
              { type: 'image', content: '生活中的数学现象图片' },
              { type: 'question', content: '你在生活中遇到过哪些数学问题？' },
              { type: 'animation', content: '数学概念动画演示' }
            ]
          },
          previewContent: '通过生活实例引入新知识，激发学生学习兴趣...'
        },
        {
          id: 2,
          title: '知识讲解',
          type: 'content',
          typeLabel: '内容',
          estimatedTime: 15,
          keyPoints: ['核心概念', '关键公式', '典型例题'],
          features: ['动画演示', '分步解析'],
          content: {
            subtitle: '二次函数的性质',
            elements: [
              { type: 'formula', content: 'y = ax² + bx + c' },
              { type: 'graph', content: '函数图像动态生成' },
              { type: 'example', content: '典型例题分析' }
            ]
          },
          previewContent: '系统讲解核心概念，通过图像和例题帮助理解...'
        },
        {
          id: 3,
          title: '实践练习',
          type: 'practice',
          typeLabel: '练习',
          estimatedTime: 10,
          keyPoints: ['基础练习', '提高练习', '错误分析'],
          features: ['实时反馈', '分层练习'],
          content: {
            subtitle: '巩固提升训练',
            elements: [
              { type: 'quiz', content: '基础选择题(3道)' },
              { type: 'calculation', content: '计算题(2道)' },
              { type: 'application', content: '应用题(1道)' }
            ]
          },
          previewContent: '分层练习设计，及时反馈，巩固学习效果...'
        },
        {
          id: 4,
          title: '总结拓展',
          type: 'summary',
          typeLabel: '总结',
          estimatedTime: 5,
          keyPoints: ['知识回顾', '方法总结', '课后作业'],
          features: ['知识地图', '作业布置'],
          content: {
            subtitle: '知识梳理与拓展',
            elements: [
              { type: 'mindmap', content: '知识结构图' },
              { type: 'summary', content: '重点知识总结' },
              { type: 'homework', content: '分层作业设计' }
            ]
          },
          previewContent: '构建知识体系，布置个性化作业，为下节课做准备...'
        }
      ],
      
      // 编辑状态
      editingSlide: null,
      isEditMode: false,
      
      // 生成状态
      generating: false,
      generationStage: '',
      generationMessage: '',
      generationProgress: 0,
      generatedSlides: 0,
      totalSlides: 0,
      estimatedRemainingTime: 0,
      
      progressColors: [
        { color: '#f56c6c', percentage: 20 },
        { color: '#e6a23c', percentage: 40 },
        { color: '#5daf34', percentage: 60 },
        { color: '#1989fa', percentage: 80 },
        { color: '#6f7ad3', percentage: 100 }
      ],
      
      // 生成结果
      generatedPPT: null,
      
      // PPT预览数据
      pptPreviewVisible: false,
      generatedPPTContent: null,
      currentSlideIndex: 0,
      slideChangeTimer: null
    }
  },
  
  computed: {
    integratedDataCount() {
      return this.dataSources.filter(source => source.integrated).length
    },
    
    estimatedSlides() {
      return this.slidesStructure.length
    },
    
    estimatedTime() {
      return this.slidesStructure.reduce((total, slide) => total + slide.estimatedTime, 0)
    },
    
    currentSlide() {
      if (this.generatedPPTContent && this.generatedPPTContent.slides) {
        return this.generatedPPTContent.slides[this.currentSlideIndex]
      }
      return null
    }
  },
  
  mounted() {
    this.loadIntegratedData()
  },
  
  beforeDestroy() {
    // 清理定时器，防止内存泄漏
    if (this.slideChangeTimer) {
      clearTimeout(this.slideChangeTimer)
    }
  },
  
  methods: {
    // 加载已整合的数据
    loadIntegratedData() {
      // 从localStorage加载各步骤数据
      const dataChecks = [
        { key: 'coursePositioning', sourceId: 1 },
        { key: 'teachingTargets', sourceId: 2 },
        { key: 'contentAnalysis', sourceId: 3 },
        { key: 'studentProfile', sourceId: 4 },
        { key: 'teachingStrategy', sourceId: 5 }
      ]
      
      dataChecks.forEach(check => {
        const data = localStorage.getItem(check.key)
        if (data) {
          const source = this.dataSources.find(s => s.id === check.sourceId)
          if (source) {
            source.integrated = true
            source.dataCount = Object.keys(JSON.parse(data)).length
          }
        }
      })
      
      // 为演示目的，如果数据不足，提供模拟数据
      this.ensureDemoData()
      
      // 基于已有数据调整PPT结构
      this.adjustSlidesStructure()
    },
    
    // 确保有足够的演示数据
    ensureDemoData() {
      const requiredCount = 3
      const currentCount = this.dataSources.filter(source => source.integrated).length
      
      if (currentCount < requiredCount) {
        // 为演示目的，自动设置前3个数据源为已整合状态
        const demoData = [
          { 
            sourceId: 1, 
            dataCount: 5,
            data: {
              subject: '数学',
              grade: '九年级',
              chapter: '二次函数',
              difficulty: '中等',
              duration: '45分钟'
            }
          },
          { 
            sourceId: 2, 
            dataCount: 8,
            data: {
              knowledgeGoals: ['理解二次函数概念', '掌握图像性质'],
              skillGoals: ['会用配方法', '能解决应用问题'],
              emotionGoals: ['培养数学思维', '激发学习兴趣']
            }
          },
          { 
            sourceId: 3, 
            dataCount: 6,
            data: {
              keyPoints: ['二次函数定义', '图像性质', '最值问题'],
              difficulties: ['配方法应用', '参数讨论'],
              strategies: ['数形结合', '分类讨论']
            }
          }
        ]
        
        demoData.forEach((demo, index) => {
          if (index < requiredCount) {
            const source = this.dataSources.find(s => s.id === demo.sourceId)
            if (source && !source.integrated) {
              source.integrated = true
              source.dataCount = demo.dataCount
              // 为演示目的存储一些模拟数据
              localStorage.setItem(
                `demo_${source.id}`, 
                JSON.stringify(demo.data)
              )
            }
          }
        })
        
                 console.log(`为演示目的已自动生成${requiredCount}个数据源`)
         
         // 给用户一个友好的提示
         this.$nextTick(() => {
           this.$message({
             message: '🎉 演示数据已准备完成！您现在可以生成和预览PPT了',
             type: 'success',
             duration: 4000,
             showClose: true
           })
         })
       }
     },
    
    // 调整PPT结构
    adjustSlidesStructure() {
      // 基于实际数据调整slides结构
      const studentData = localStorage.getItem('studentProfile')
      if (studentData) {
        const profile = JSON.parse(studentData)
        if (profile.specialNeedsCount) {
          this.specialNeedsCount = profile.specialNeedsCount
        }
      }
    },
    
    // 刷新数据整合
    refreshIntegration() {
      this.loadIntegratedData()
      this.$message.success('数据整合状态已刷新')
    },
    
    // 快速开始演示
    async quickStartDemo() {
      try {
        this.$message({
          message: '🚀 正在快速生成演示PPT，请稍候...',
          type: 'info',
          duration: 2000
        })
        
        // 如果还没有生成PPT内容，先生成
        if (!this.generatedPPTContent) {
          await this.generatePPT()
        }
        
        // 直接打开预览
        if (this.generatedPPTContent) {
          this.currentSlideIndex = 0
          this.$nextTick(() => {
            this.pptPreviewVisible = true
          })
        }
        
      } catch (error) {
        console.error('快速演示失败:', error)
        this.$message.error('快速演示失败，请重试')
      }
    },
    
    // 选择模板
    selectTemplate(templateId) {
      this.selectedTemplate = templateId
      this.$message.success('模板已选择')
    },
    
    // 获取幻灯片类型颜色
    getSlideTypeColor(type) {
      // 使用条件判断替代对象映射
      if (type === 'intro') return 'primary'
      if (type === 'content') return 'success'
      if (type === 'practice') return 'warning'
      if (type === 'summary') return 'info'
      return 'default'
    },
    
    // 生成演示PPT（无需完整数据）
    async generateDemoPPT() {
      this.$message({
        message: '正在为您生成演示PPT，请稍候...',
        type: 'info',
        duration: 3000
      })
      
      // 强制确保演示数据
      this.ensureDemoData()
      
      // 直接调用生成PPT
      await this.generatePPT()
    },
    
    // 生成PPT
    async generatePPT() {
      if (this.integratedDataCount < 3) {
        this.$message.warning('请先完成至少3个步骤的数据，以确保PPT质量')
        return
      }
      
      this.generating = true
      this.generatedPPT = null
      this.generationProgress = 0
      this.generatedSlides = 0
      this.totalSlides = this.slidesStructure.length
      
      try {
        // 模拟生成过程
        await this.simulateGeneration()
        
        // 生成成功
        this.generatedPPT = {
          slideCount: this.totalSlides,
          fileSize: '8.5MB',
          duration: this.estimatedTime,
          adaptationScore: 92,
          downloadUrl: '/api/ppt/download/generated-ppt.pptx'
        }
        
        // 生成详细的PPT内容数据
        this.generatePPTContent()
        
        this.$message.success('PPT生成成功！')
        
      } catch (error) {
        this.$message.error('PPT生成失败，请重试')
        console.error('PPT生成错误:', error)
      } finally {
        this.generating = false
      }
    },
    
    // 模拟生成过程
    async simulateGeneration() {
      const stages = [
        { stage: '分析教学数据', message: '正在分析您的教学设计数据...', duration: 1000 },
        { stage: '选择适配模板', message: '根据学科特点选择最佳模板...', duration: 800 },
        { stage: '生成PPT内容', message: '基于学情数据生成个性化内容...', duration: 2000 },
        { stage: '添加互动元素', message: '插入互动问答和练习环节...', duration: 1000 },
        { stage: '优化视觉效果', message: '调整版式和动画效果...', duration: 1200 },
        { stage: '质量检查', message: '检查内容完整性和一致性...', duration: 500 }
      ]
      
      for (let i = 0; i < stages.length; i++) {
        const stage = stages[i]
        this.generationStage = stage.stage
        this.generationMessage = stage.message
        
        // 模拟每个阶段的进度
        const stageProgress = Math.floor((i + 1) / stages.length * 100)
        
        await new Promise(resolve => {
          const interval = setInterval(() => {
            this.generationProgress = Math.min(
              this.generationProgress + Math.random() * 10,
              stageProgress
            )
            
            this.generatedSlides = Math.min(
              Math.floor(this.generationProgress / 100 * this.totalSlides),
              this.totalSlides
            )
            
            this.estimatedRemainingTime = Math.max(
              0,
              Math.ceil((100 - this.generationProgress) / 100 * 10)
            )
            
            if (this.generationProgress >= stageProgress) {
              clearInterval(interval)
              resolve()
            }
          }, 100)
        })
        
        await new Promise(resolve => setTimeout(resolve, stage.duration))
      }
      
      this.generationProgress = 100
      this.generatedSlides = this.totalSlides
      this.estimatedRemainingTime = 0
    },
    
    // PPT操作方法
    async previewPPT() {
      // 如果PPT内容已经生成，直接预览
      if (this.generatedPPTContent) {
        this.currentSlideIndex = 0
        this.$nextTick(() => {
          this.pptPreviewVisible = true
        })
        return
      }
      
      // 如果PPT内容未生成，检查是否可以生成
      if (this.integratedDataCount < 3) {
        this.$message({
          message: '请先完成至少3个教学设计步骤，然后生成PPT内容',
          type: 'warning',
          duration: 4000
        })
        return
      }
      
      // 如果正在生成中，提示等待
      if (this.generating) {
        this.$message({
          message: 'PPT正在生成中，请稍候...',
          type: 'info',
          duration: 3000
        })
        return
      }
      
      // 自动生成PPT内容
      try {
        this.$message({
          message: '检测到PPT内容未生成，正在为您自动生成...',
          type: 'info',
          duration: 3000
        })
        
        // 调用生成方法
        await this.generatePPT()
        
        // 生成成功后直接预览
        if (this.generatedPPTContent) {
          this.currentSlideIndex = 0
          this.$nextTick(() => {
            this.pptPreviewVisible = true
          })
        }
        
      } catch (error) {
        console.error('自动生成PPT失败:', error)
        this.$message.error('PPT生成失败，请手动点击"生成PPT"按钮重试')
      }
    },
    
    downloadPPT() {
      this.$message.success('PPT下载已开始...')
    },
    
    editPPT() {
      this.$message.info('在线编辑功能开发中...')
    },
    
    sharePPT() {
      this.$message.info('分享功能开发中...')
    },
    
    // 生成详细PPT内容
    generatePPTContent() {
      const selectedTemplateData = this.recommendedTemplates.find(t => t.id === this.selectedTemplate)
      
      this.generatedPPTContent = {
        templateInfo: selectedTemplateData,
        title: '二次函数的性质与应用',
        subtitle: '高中数学 · 九年级上册 · 智能教学PPT',
        createTime: new Date().toLocaleString(),
        totalDuration: this.estimatedTime,
        slideCount: this.slidesStructure.length,
        slides: this.slidesStructure.map((slide, index) => {
          // 使用 Object.assign 替代展开运算符避免 Babel 转换问题
          const slideData = Object.assign({}, slide, {
            slideNumber: index + 1,
            fullContent: this.generateSlideFullContent(slide, selectedTemplateData),
            visualElements: this.generateSlideVisuals(slide),
            interactiveElements: this.generateSlideInteractions(slide)
          })
          return slideData
        })
      }
      
      // 确保PPT预览能正常显示
      this.$nextTick(() => {
        console.log('PPT内容生成完成:', this.generatedPPTContent)
      })
    },
    
    // 生成幻灯片完整内容
    generateSlideFullContent(slide, template) {
      // 分别定义各种内容模板，避免复杂的嵌套对象字面量
      if (slide.type === 'intro') {
        return {
          title: slide.title,
          subtitle: slide.content.subtitle,
          mainContent: [
            '📚 本节课我们将探索二次函数的奥秘',
            '🎯 学习目标：掌握二次函数的基本性质和图像特征',
            '💡 教学重点：理解二次函数的图像变化规律',
            '🔧 实际应用：运用二次函数解决生活中的优化问题',
            '✨ 学习方法：观察→分析→归纳→应用'
          ],
          backgroundInfo: '二次函数是高中数学的重要内容，它不仅是函数知识的重要组成部分，更是解决实际问题的有力工具。在物理学中的抛物运动、经济学中的利润最大化、工程学中的优化设计等领域都有广泛应用。',
          warmupQuestion: '你在生活中见过抛物线形状的物体吗？比如抛球的轨迹、拱桥的形状、卫星天线的截面等。',
          learningObjectives: [
            '理解二次函数的概念和基本形式',
            '掌握二次函数图像的基本性质',
            '学会用配方法求二次函数的顶点',
            '能够解决简单的二次函数应用问题'
          ]
        }
      } else if (slide.type === 'content') {
        return {
          title: slide.title,
          subtitle: slide.content.subtitle,
          mainContent: [
            '📐 二次函数的标准形式：y = ax^2 + bx + c (a ≠ 0)',
            '📊 参数a的作用：决定开口方向和开口大小',
            '📍 顶点坐标公式：(-b/(2a), (4ac-b^2)/(4a))',
            '📈 对称轴方程：x = -b/(2a)',
            '🎯 判别式：Δ = b^2 - 4ac，决定与x轴交点个数'
          ],
          keyFormulas: [
            { 
              name: '标准式', 
              formula: 'y = ax^2 + bx + c',
              description: '最基本的形式，适合计算函数值'
            },
            { 
              name: '顶点式', 
              formula: 'y = a(x-h)^2 + k',
              description: '顶点坐标(h,k)直接可见，适合分析图像'
            },
            { 
              name: '交点式', 
              formula: 'y = a(x-x1)(x-x2)',
              description: 'x1,x2为与x轴交点横坐标，适合求根'
            }
          ],
          examples: [
            {
              title: '例题1：求二次函数y = x^2 - 4x + 3的顶点坐标和对称轴',
              question: '已知二次函数y = x^2 - 4x + 3，求其顶点坐标和对称轴方程。',
              solution: '解：由y = x^2 - 4x + 3，得a = 1, b = -4, c = 3\n对称轴：x = -b/(2a) = -(-4)/(2×1) = 2\n顶点纵坐标：y = 2^2 - 4×2 + 3 = 4 - 8 + 3 = -1\n因此顶点坐标为(2, -1)，对称轴方程为x = 2',
              keyPoints: ['识别参数a、b、c', '运用顶点坐标公式', '验证计算结果']
            },
            {
              title: '例题2：用配方法将y = x^2 - 4x + 3化为顶点式',
              question: '将二次函数y = x^2 - 4x + 3用配方法化为顶点式。',
              solution: '解：y = x^2 - 4x + 3\n= x^2 - 4x + 4 - 4 + 3\n= (x - 2)^2 - 1\n因此顶点式为y = (x - 2)^2 - 1',
              keyPoints: ['配方法的步骤', '完成平方的技巧', '验证顶点坐标']
            }
          ],
          graphProperties: {
            title: '二次函数图像性质总结',
            properties: [
              { property: '开口方向', rule: 'a > 0 开口向上，a < 0 开口向下' },
              { property: '对称轴', rule: '直线 x = -b/(2a)' },
              { property: '顶点', rule: '(-b/(2a), (4ac-b^2)/(4a))' },
              { property: '最值', rule: 'a > 0 有最小值，a < 0 有最大值' }
            ]
          }
        }
      } else if (slide.type === 'practice') {
        return {
          title: slide.title,
          subtitle: slide.content.subtitle,
          practiceIntro: '通过以下练习，检验你对二次函数的理解程度。请独立完成，然后查看解答。',
          exercises: [
            {
              type: 'choice',
              number: 1,
              question: '函数y = -2x^2 + 4x + 1的开口方向和对称轴分别是？',
              options: [
                'A. 向上，x = 1', 
                'B. 向下，x = 1', 
                'C. 向上，x = -1', 
                'D. 向下，x = -1'
              ],
              answer: 'B',
              explanation: '解析：因为a = -2 < 0，所以开口向下；\n对称轴：x = -b/(2a) = -4/(2×(-2)) = 1\n因此答案是B。',
              difficulty: '基础',
              points: 5
            },
            {
              type: 'calculation',
              number: 2,
              question: '求函数y = x^2 - 6x + 8的顶点坐标、对称轴方程和最小值。',
              answer: '顶点坐标：(3, -1)；对称轴：x = 3；最小值：-1',
              solution: '解：由y = x^2 - 6x + 8，得a = 1, b = -6, c = 8\n∵ a = 1 > 0，∴ 开口向上，有最小值\n对称轴：x = -b/(2a) = -(-6)/(2×1) = 3\n最小值：y = 3^2 - 6×3 + 8 = 9 - 18 + 8 = -1\n∴ 顶点坐标为(3, -1)，对称轴方程为x = 3，最小值为-1',
              difficulty: '中等',
              points: 8
            },
            {
              type: 'application',
              number: 3,
              question: '某商店销售一种商品，成本为每件20元。根据市场调研，当售价为每件x元时，日销售量为(100-2x)件。求当售价为多少元时，商店的日利润最大？最大利润是多少？',
              answer: '售价35元时利润最大，最大利润450元',
              solution: '解：设售价为x元，则每件利润为(x-20)元\n日销售量为(100-2x)件\n∴ 日总利润 P = (x-20)(100-2x)\n= -2x^2 + 140x - 2000\n= -2(x^2 - 70x + 1000)\n= -2(x - 35)^2 + 450\n∵ a = -2 < 0，∴ 当x = 35时，P取最大值450\n答：当售价为35元时，日利润最大，最大利润为450元。',
              difficulty: '综合',
              points: 12,
              practicalApplication: true
            }
          ],
          practiceStrategy: {
            title: '解题策略提示',
            tips: [
              '🔍 仔细识别题目中的二次函数形式',
              '📝 合理选择函数的表示方法（标准式、顶点式等）',
              '📊 画出草图帮助理解问题',
              '✅ 验证答案的合理性'
            ]
          }
        }
      } else if (slide.type === 'summary') {
        return {
          title: slide.title,
          subtitle: slide.content.subtitle,
          keyPoints: [
            '🔑 二次函数的三种表达式：标准式、顶点式、交点式，各有不同用途',
            '🔑 参数a决定开口方向和开口大小，|a|越大开口越窄',
            '🔑 顶点是二次函数图像的关键特征点，决定函数的最值',
            '🔑 对称轴是二次函数图像的重要性质，x = -b/(2a)',
            '🔑 二次函数在实际生活中应用广泛，特别是最值问题'
          ],
          knowledgeMap: {
            center: '二次函数',
            branches: [
              { 
                name: '基本概念', 
                items: ['定义', '定义域', '值域', '单调性'],
                color: '#3498db'
              },
              { 
                name: '解析式', 
                items: ['标准式', '顶点式', '交点式', '参数含义'],
                color: '#2ecc71'
              },
              { 
                name: '图像性质', 
                items: ['开口方向', '对称轴', '顶点', '与坐标轴交点'],
                color: '#e74c3c'
              },
              { 
                name: '实际应用', 
                items: ['最值问题', '抛物运动', '经济优化', '几何问题'],
                color: '#f39c12'
              }
            ]
          },
          studyMethods: {
            title: '学习方法总结',
            methods: [
              {
                method: '数形结合',
                description: '通过图像理解函数性质，用性质描述图像特征'
              },
              {
                method: '分类讨论',
                description: '根据参数a的正负分类讨论开口方向和最值'
              },
              {
                method: '转化思想',
                description: '灵活运用三种表达式的相互转化解决问题'
              }
            ]
          },
          commonMistakes: {
            title: '常见错误提醒',
            mistakes: [
              '忘记二次函数定义中a ≠ 0的条件',
              '对称轴公式记忆错误，写成x = b/(2a)',
              '配方时忘记平衡等式两边',
              '实际问题中忘记考虑自变量的取值范围'
            ]
          },
          homework: [
            {
              type: '基础题',
              content: '练习册P85 第1-5题',
              requirement: '掌握基本概念和公式应用',
              timeEstimate: '20分钟'
            },
            {
              type: '提高题',
              content: '练习册P86 第6-8题',
              requirement: '熟练运用不同表达式解决问题',
              timeEstimate: '25分钟'
            },
            {
              type: '拓展题',
              content: '思考：设计一个生活中的二次函数应用问题',
              requirement: '培养数学建模能力',
              timeEstimate: '15分钟'
            }
          ],
          nextLesson: {
            title: '下节课预告',
            content: '一元二次不等式的解法',
            connection: '结合二次函数图像理解不等式解集的几何意义'
          }
        }
      } else {
        // 默认返回内容类型的模板
        return {
          title: slide.title,
          subtitle: slide.content.subtitle,
          mainContent: ['请编辑幻灯片内容'],
          keyFormulas: [],
          examples: []
        }
      }
    },
    
    // 生成幻灯片视觉元素
    generateSlideVisuals(slide) {
      // 分别定义各种模板，避免复杂的对象字面量
      const introElements = [
        { type: 'image', src: '/images/math-intro.jpg', alt: '数学概念图', position: 'center' },
        { type: 'animation', name: 'fadeIn', duration: '1s', target: '.slide-title' },
        { type: 'icon', name: 'el-icon-star-on', color: '#f39c12', size: '2em' }
      ]
      
      const contentElements = [
        { type: 'chart', chartType: 'function', data: 'y=x^2-4x+3', interactive: true },
        { type: 'formula', latex: 'y = ax^2 + bx + c', highlight: true },
        { type: 'graph', type: 'coordinate', showGrid: true, functions: ['y=x^2-4x+3'] }
      ]
      
      const practiceElements = [
        { type: 'timer', duration: 600, autoStart: false },
        { type: 'progress', total: 3, current: 0 },
        { type: 'feedback', positive: '很好！', negative: '再想想...' }
      ]
      
      const summaryElements = [
        { type: 'mindmap', data: 'knowledge-structure', interactive: false },
        { type: 'checklist', items: ['概念理解', '公式掌握', '应用能力'] }
      ]
      
      // 使用简单的条件判断而不是对象访问
      if (slide.type === 'intro') {
        return introElements
      } else if (slide.type === 'content') {
        return contentElements
      } else if (slide.type === 'practice') {
        return practiceElements
      } else if (slide.type === 'summary') {
        return summaryElements
      } else {
        return []
      }
    },
    
    // 生成幻灯片交互元素
    generateSlideInteractions(slide) {
      // 分别定义各种交互模板，避免复杂的对象字面量
      const introInteractions = [
        { type: 'poll', question: '你对二次函数的了解程度？', options: ['完全不懂', '有些了解', '比较熟悉', '非常熟悉'] },
        { type: 'discussion', prompt: '分享一个你见过的抛物线例子' }
      ]
      
      const contentInteractions = [
        { type: 'quiz', question: '当a>0时，抛物线开口朝向？', answer: '上' },
        { type: 'interactive-demo', tool: 'function-explorer', params: Object.assign({}, { type: 'quadratic' }) }
      ]
      
      const practiceInteractions = [
        { type: 'real-time-quiz', questions: 3, timeLimit: 30 },
        { type: 'peer-review', mode: 'group-work' }
      ]
      
      const summaryInteractions = [
        { type: 'reflection', questions: ['今天学到了什么？', '还有什么疑问？'] },
        { type: 'homework-submission', platform: 'class-system' }
      ]
      
      // 使用简单的条件判断而不是对象访问
      if (slide.type === 'intro') {
        return introInteractions
      } else if (slide.type === 'content') {
        return contentInteractions
      } else if (slide.type === 'practice') {
        return practiceInteractions
      } else if (slide.type === 'summary') {
        return summaryInteractions
      } else {
        return []
      }
    },
    
    // 编辑功能方法
    toggleEditMode() {
      this.isEditMode = !this.isEditMode
      if (!this.isEditMode) {
        this.editingSlide = null
      }
    },
    
    editSlide(slideId) {
      this.editingSlide = slideId
    },
    
    saveSlideEdit(slide) {
      this.editingSlide = null
      this.$message.success('幻灯片内容已保存')
    },
    
    addSlide() {
      const newId = Math.max(...this.slidesStructure.map(s => s.id)) + 1
      const newSlide = {
        id: newId,
        title: '新幻灯片',
        type: 'content',
        typeLabel: '内容',
        estimatedTime: 5,
        keyPoints: ['关键点1', '关键点2'],
        features: [],
        content: {
          subtitle: '请输入子标题',
          elements: [
            { type: 'text', content: '文本内容' }
          ]
        },
        previewContent: '请编辑幻灯片内容...'
      }
      this.slidesStructure.push(newSlide)
      this.$message.success('已添加新幻灯片')
    },
    
    deleteSlide(slideId) {
      this.$confirm('确定要删除这张幻灯片吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = this.slidesStructure.findIndex(s => s.id === slideId)
        if (index > -1) {
          this.slidesStructure.splice(index, 1)
          this.$message.success('幻灯片已删除')
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    
    duplicateSlide(slide) {
      const newId = Math.max(...this.slidesStructure.map(s => s.id)) + 1
      // 使用 Object.assign 替代展开运算符
      const clonedSlide = JSON.parse(JSON.stringify(slide))
      const duplicatedSlide = Object.assign({}, clonedSlide, {
        id: newId,
        title: slide.title + ' (副本)'
      })
      const index = this.slidesStructure.findIndex(s => s.id === slide.id)
      this.slidesStructure.splice(index + 1, 0, duplicatedSlide)
      this.$message.success('幻灯片已复制')
    },
    
    moveSlide(index, direction) {
      const newIndex = index + direction
      if (newIndex >= 0 && newIndex < this.slidesStructure.length) {
        const slide = this.slidesStructure.splice(index, 1)[0]
        this.slidesStructure.splice(newIndex, 0, slide)
        this.$message.success('幻灯片位置已调整')
      }
    },
    
    addElement(slide, type) {
      // 使用更兼容的方式定义元素模板
      let newElement
      if (type === 'text') {
        newElement = { type: 'text', content: '文本内容' }
      } else if (type === 'image') {
        newElement = { type: 'image', content: '图片说明' }
      } else if (type === 'chart') {
        newElement = { type: 'chart', content: '图表描述' }
      } else if (type === 'quiz') {
        newElement = { type: 'quiz', content: '测验题目' }
      } else if (type === 'animation') {
        newElement = { type: 'animation', content: '动画描述' }
      } else {
        newElement = { type: 'text', content: '文本内容' }
      }
      
      slide.content.elements.push(newElement)
      this.$message.success('元素已添加')
    },
    
    removeElement(slide, index) {
      slide.content.elements.splice(index, 1)
      this.$message.success('元素已删除')
    },
    
    addKeyPoint(slide) {
      slide.keyPoints.push('新要点')
    },
    
    removeKeyPoint(slide, index) {
      slide.keyPoints.splice(index, 1)
    },
    
    resetStructure() {
      this.$confirm('确定要重置PPT结构吗？所有自定义修改将丢失。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 重置为初始结构
        this.slidesStructure = [
          {
            id: 1,
            title: '课程导入',
            type: 'intro',
            typeLabel: '导入',
            estimatedTime: 5,
            keyPoints: ['课程背景', '学习目标', '本节重点'],
            features: ['动态图表', '互动问答'],
            content: {
              subtitle: '探索数学的奥秘',
              elements: [
                { type: 'image', content: '生活中的数学现象图片' },
                { type: 'question', content: '你在生活中遇到过哪些数学问题？' },
                { type: 'animation', content: '数学概念动画演示' }
              ]
            },
            previewContent: '通过生活实例引入新知识，激发学生学习兴趣...'
          },
          {
            id: 2,
            title: '知识讲解',
            type: 'content',
            typeLabel: '内容',
            estimatedTime: 15,
            keyPoints: ['核心概念', '关键公式', '典型例题'],
            features: ['动画演示', '分步解析'],
            content: {
              subtitle: '二次函数的性质',
              elements: [
                { type: 'formula', content: 'y = ax^2 + bx + c' },
                { type: 'graph', content: '函数图像动态生成' },
                { type: 'example', content: '典型例题分析' }
              ]
            },
            previewContent: '系统讲解核心概念，通过图像和例题帮助理解...'
          },
          {
            id: 3,
            title: '实践练习',
            type: 'practice',
            typeLabel: '练习',
            estimatedTime: 10,
            keyPoints: ['基础练习', '提高练习', '错误分析'],
            features: ['实时反馈', '分层练习'],
            content: {
              subtitle: '巩固提升训练',
              elements: [
                { type: 'quiz', content: '基础选择题(3道)' },
                { type: 'calculation', content: '计算题(2道)' },
                { type: 'application', content: '应用题(1道)' }
              ]
            },
            previewContent: '分层练习设计，及时反馈，巩固学习效果...'
          },
          {
            id: 4,
            title: '总结拓展',
            type: 'summary',
            typeLabel: '总结',
            estimatedTime: 5,
            keyPoints: ['知识回顾', '方法总结', '课后作业'],
            features: ['知识地图', '作业布置'],
            content: {
              subtitle: '知识梳理与拓展',
              elements: [
                { type: 'mindmap', content: '知识结构图' },
                { type: 'summary', content: '重点知识总结' },
                { type: 'homework', content: '分层作业设计' }
              ]
            },
            previewContent: '构建知识体系，布置个性化作业，为下节课做准备...'
          }
        ]
        this.editingSlide = null
        this.isEditMode = false
        this.$message.success('PPT结构已重置')
      }).catch(() => {
        this.$message.info('已取消重置')
      })
    },
    
    getElementIcon(type) {
      // 使用条件判断替代对象映射
      if (type === 'text') return 'el-icon-document'
      if (type === 'image') return 'el-icon-picture'
      if (type === 'chart') return 'el-icon-pie-chart'
      if (type === 'quiz') return 'el-icon-question'
      if (type === 'animation') return 'el-icon-video-play'
      if (type === 'formula') return 'el-icon-edit-outline'
      if (type === 'graph') return 'el-icon-data-line'
      if (type === 'example') return 'el-icon-notebook-1'
      if (type === 'calculation') return 'el-icon-calculator'
      if (type === 'application') return 'el-icon-files'
      if (type === 'mindmap') return 'el-icon-share'
      if (type === 'summary') return 'el-icon-collection'
      if (type === 'homework') return 'el-icon-folder-checked'
      return 'el-icon-document'
    },
    
    getExerciseTypeLabel(type) {
      // 使用条件判断替代对象映射
      if (type === 'choice') return '选择题'
      if (type === 'calculation') return '计算题'
      if (type === 'application') return '应用题'
      if (type === 'fill') return '填空题'
      if (type === 'essay') return '简答题'
      return '练习题'
    },
    
    // 防抖处理幻灯片切换
    changeSlide(index) {
      if (this.slideChangeTimer) {
        clearTimeout(this.slideChangeTimer)
      }
      
      this.slideChangeTimer = setTimeout(() => {
        this.currentSlideIndex = index
        // 简化的成功提示
        this.$message({
          message: `已切换到第${index + 1}张幻灯片`,
          type: 'success',
          duration: 1500
        })
      }, 100)
    },
    
    // 优化内存使用，关闭预览时清理定时器
    closePPTPreview() {
      this.pptPreviewVisible = false
      if (this.slideChangeTimer) {
        clearTimeout(this.slideChangeTimer)
        this.slideChangeTimer = null
      }
    }
  }
}
</script>

<style scoped>
.ai-ppt-generator {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 页面头部样式 */
.page-header {
  margin-bottom: 30px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 40px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.title-section h1 {
  font-size: 28px;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.title-section p {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.header-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

/* 数据整合面板 */
.data-integration-panel {
  margin-bottom: 30px;
}

.integration-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.card-header i {
  margin-right: 8px;
  color: #409EFF;
}

.integration-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.integration-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.integration-item.integrated {
  border-color: #67C23A;
  background: #f0f9ff;
}

.integration-item.missing {
  border-color: #E6A23C;
  background: #fffbf0;
}

.integration-icon {
  font-size: 24px;
  margin-right: 15px;
  width: 40px;
  text-align: center;
}

.integration-item.integrated .integration-icon {
  color: #67C23A;
}

.integration-item.missing .integration-icon {
  color: #E6A23C;
}

.integration-info {
  flex: 1;
}

.integration-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}

.integration-info p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.integration-data {
  margin-top: 8px;
}

.integration-status {
  font-size: 20px;
}

/* 生成配置样式 */
.generation-config {
  margin-bottom: 30px;
}

.config-card {
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 模板选择器 */
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.template-item {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.template-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.template-item.selected {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.template-preview {
  position: relative;
  height: 120px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.template-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.template-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
  font-size: 24px;
}

.template-item:hover .template-overlay {
  opacity: 1;
}

.template-info {
  padding: 15px;
}

.template-info h5 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.template-tags {
  margin-bottom: 8px;
}

.template-tags .el-tag {
  margin-right: 5px;
}

.match-score {
  font-size: 14px;
  color: #67C23A;
  font-weight: 600;
}

/* 个性化配置 */
.personalization-config {
  space-y: 20px;
}

.config-section {
  margin-bottom: 25px;
}

.config-section h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.config-note {
  margin: 8px 0 0 0;
  font-size: 12px;
  color: #909399;
}

/* PPT结构预览 */
.structure-preview {
  margin-bottom: 30px;
}

.preview-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.slides-timeline {
  position: relative;
}

.slide-node {
  display: flex;
  align-items: flex-start;
  margin-bottom: 30px;
  position: relative;
}

.slide-connector {
  position: absolute;
  left: 20px;
  top: -30px;
  width: 2px;
  height: 30px;
  background: #e4e7ed;
}

.slide-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 20px;
  flex-shrink: 0;
}

.slide-content {
  flex: 1;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
}

.slide-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.slide-header h5 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.slide-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.slide-time {
  font-size: 14px;
  color: #909399;
}

.slide-details {
  margin-top: 15px;
}

.slide-points {
  margin-bottom: 15px;
}

.point-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.point-item i {
  margin-right: 8px;
  color: #409EFF;
}

.slide-features {
  display: flex;
  align-items: center;
  gap: 10px;
}

.features-label {
  font-size: 14px;
  color: #909399;
}

.features-list {
  display: flex;
  gap: 5px;
}

/* 生成结果样式 */
.generation-result {
  margin-bottom: 30px;
}

.result-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.generation-progress {
  padding: 20px 0;
}

.progress-info {
  margin-bottom: 20px;
  text-align: center;
}

.progress-info h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.progress-info p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.progress-details {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  font-size: 14px;
  color: #909399;
}

.generation-complete {
  padding: 20px 0;
}

.result-summary {
  text-align: center;
  margin-bottom: 30px;
}

.result-summary h4 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #67C23A;
}

.result-summary p {
  margin: 0;
  font-size: 16px;
  color: #606266;
}

.result-actions {
  text-align: center;
  margin-bottom: 30px;
}

.result-stats {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-grid .stat-item {
  text-align: center;
  padding: 15px;
  background: white;
  border-radius: 6px;
}

.stat-grid .stat-label {
  display: block;
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-grid .stat-value {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

/* 模板预览样式 */
.template-mock {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 4px;
  overflow: hidden;
}

.mock-header {
  height: 30%;
  padding: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.mock-title {
  height: 8px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 2px;
  margin-bottom: 4px;
  width: 80%;
}

.mock-subtitle {
  height: 6px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 2px;
  width: 60%;
}

.mock-content {
  flex: 1;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  background: rgba(255, 255, 255, 0.1);
}

.mock-text-block {
  height: 12px;
  border-radius: 2px;
  opacity: 0.6;
}

.mock-chart {
  height: 20px;
  border-radius: 2px;
  opacity: 0.8;
  margin-top: auto;
}

/* 不同模板类型的样式 */
.template-modern .mock-content {
  background: #ecf0f1;
}

.template-interactive .mock-content {
  background: linear-gradient(45deg, rgba(231, 76, 60, 0.1), rgba(241, 196, 15, 0.1));
}

.template-academic .mock-content {
  background: #f8f9fa;
}

/* PPT结构工具栏样式 */
.slides-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.toolbar-info {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #606266;
}

/* 编辑状态样式 */
.slide-node.editing .slide-content {
  border: 2px solid #409EFF;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.2);
}

/* 幻灯片头部编辑样式 */
.slide-header-edit {
  margin-bottom: 10px;
}

.subtitle-input {
  margin-top: 8px;
}

.slide-subtitle {
  margin: 5px 0 0 0;
  font-size: 14px;
  color: #909399;
}

.time-edit {
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 幻灯片预览样式 */
.slide-preview {
  margin: 15px 0;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  background: #fafafa;
}

.preview-container {
  padding: 15px;
}

.preview-header {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.preview-content {
  margin-bottom: 15px;
}

.preview-elements {
  space-y: 8px;
}

.element-item {
  display: flex;
  align-items: center;
  padding: 8px;
  background: white;
  border-radius: 4px;
  margin-bottom: 8px;
  border: 1px solid #e4e7ed;
}

.element-icon {
  width: 24px;
  text-align: center;
  margin-right: 10px;
  color: #409EFF;
}

.element-content {
  flex: 1;
  font-size: 13px;
}

.element-actions {
  margin-left: 10px;
}

/* 不同元素类型的样式 */
.element-image .element-icon { color: #67C23A; }
.element-chart .element-icon { color: #E6A23C; }
.element-quiz .element-icon { color: #F56C6C; }
.element-animation .element-icon { color: #909399; }

.add-element {
  text-align: center;
  padding: 10px;
  border: 2px dashed #e4e7ed;
  border-radius: 4px;
  margin-top: 10px;
}

.preview-description {
  padding: 10px;
  background: white;
  border-radius: 4px;
  border-left: 3px solid #409EFF;
}

.preview-description p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}

/* 关键点编辑样式 */
.points-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-weight: 600;
  color: #303133;
}

.points-list {
  space-y: 8px;
}

.point-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.point-item .el-input {
  margin-left: 8px;
  margin-right: 8px;
}

/* 幻灯片操作按钮样式 */
.slide-actions {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.slide-order {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

/* 动画效果 */
.slide-content {
  transition: all 0.3s ease;
}

.element-item {
  transition: all 0.2s ease;
}

.element-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .integration-grid {
    grid-template-columns: 1fr;
  }
  
  .template-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .slides-toolbar {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .slide-actions {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
  
  .element-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .element-actions {
    margin-left: 0;
         margin-top: 5px;
   }
 }
 
 /* PPT预览对话框样式 */
 .ppt-preview-dialog {
   .el-dialog__body {
     padding: 10px 20px;
     max-height: 80vh;
     overflow-y: auto;
   }
 }
 
 .ppt-preview-container {
   display: flex;
   flex-direction: column;
   height: 100%;
 }
 
 /* PPT信息头部 */
 .ppt-info-header {
   display: flex;
   justify-content: space-between;
   align-items: center;
   padding: 20px;
   background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
   color: white;
   border-radius: 8px;
   margin-bottom: 20px;
 }
 
 .ppt-title-section h2 {
   margin: 0 0 5px 0;
   font-size: 24px;
   font-weight: 600;
 }
 
 .ppt-title-section p {
   margin: 0 0 10px 0;
   font-size: 16px;
   opacity: 0.9;
 }
 
 .ppt-meta {
   display: flex;
   gap: 20px;
   font-size: 14px;
   opacity: 0.8;
 }
 
 .ppt-navigation {
   display: flex;
   align-items: center;
   gap: 15px;
 }
 
 .slide-counter {
   font-size: 16px;
   font-weight: 600;
   padding: 5px 15px;
   background: rgba(255, 255, 255, 0.2);
   border-radius: 20px;
 }
 
 /* 幻灯片预览区域 */
 .slide-preview-area {
   flex: 1;
   margin-bottom: 20px;
 }
 
 .slide-container {
   height: 100%;
   display: flex;
   justify-content: center;
   align-items: center;
 }
 
 /* 标准PPT幻灯片样式 - 16:9比例 */
 .ppt-slide {
   width: 960px;
   height: 720px;
   background: white;
   border-radius: 8px;
   box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
   display: flex;
   flex-direction: column;
   overflow: hidden;
   position: relative;
   border: 1px solid #e0e0e0;
 }
 
 /* 不同PPT模板的样式 */
 .ppt-slide.template-modern {
   border-top: 4px solid #2c3e50;
 }
 
 .ppt-slide.template-interactive {
   border-top: 4px solid #e74c3c;
   background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
 }
 
 .ppt-slide.template-academic {
   border-top: 4px solid #34495e;
   background: #fafbfc;
 }
 
 /* PPT头部样式 */
 .ppt-header {
   padding: 30px 50px 15px 50px;
   background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
   color: white;
   text-align: center;
 }
 
 .ppt-title {
   margin: 0 0 12px 0;
   font-size: 36px;
   font-weight: 700;
   line-height: 1.2;
   text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
 }
 
 .ppt-subtitle {
   margin: 0;
   font-size: 18px;
   font-weight: 300;
   opacity: 0.9;
   font-style: italic;
 }
 
 /* PPT内容区域 */
 .ppt-content {
   flex: 1;
   padding: 30px 50px;
   display: flex;
   flex-direction: column;
   justify-content: flex-start;
   overflow-y: auto;
 }
 
 /* PPT通用样式 */
 .section-title {
   margin: 0 0 25px 0;
   font-size: 28px;
   font-weight: 600;
   color: #2c3e50;
   text-align: center;
   border-bottom: 2px solid #3498db;
   padding-bottom: 10px;
 }
 
 /* 导入幻灯片样式 */
 .ppt-intro {
   display: flex;
   flex-direction: column;
   gap: 25px;
   height: 100%;
 }
 
 .ppt-objectives {
   flex: 1;
 }
 
 .objectives-list {
   list-style: none;
   padding: 0;
   margin: 0;
 }
 
 .objective-item {
   font-size: 18px;
   line-height: 1.6;
   margin-bottom: 15px;
   padding: 12px 0 12px 35px;
   position: relative;
   color: #34495e;
   font-weight: 500;
 }
 
 .objective-item::before {
   content: "✓";
   position: absolute;
   left: 0;
   top: 12px;
   color: #27ae60;
   font-size: 20px;
   font-weight: bold;
 }
 
 .ppt-highlight {
   background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
   color: white;
   padding: 20px 25px;
   border-radius: 12px;
   text-align: center;
   box-shadow: 0 4px 15px rgba(243, 156, 18, 0.3);
 }
 
 .highlight-title {
   font-size: 20px;
   font-weight: 600;
   margin-bottom: 12px;
 }
 
 .highlight-content {
   font-size: 16px;
   line-height: 1.5;
   font-weight: 300;
 }


 
 .intro-background, .intro-question {
   background: #f8f9fa;
   padding: 20px;
   border-radius: 6px;
   border-left: 4px solid #3498db;
 }
 
 .intro-background h4, .intro-question h4 {
   margin: 0 0 10px 0;
   color: #2c3e50;
 }
 
 .intro-background p, .intro-question p {
   margin: 0;
   line-height: 1.6;
   color: #5a6c7d;
 }
 
 /* 内容讲解幻灯片样式 */
 .ppt-content-slide {
   display: flex;
   flex-direction: column;
   height: 100%;
 }
 
 /* 公式展示样式 */
 .ppt-formulas {
   text-align: center;
 }
 
 .formula-display {
   display: flex;
   flex-direction: column;
   gap: 30px;
   align-items: center;
 }
 
 .formula-item {
   background: #f8f9fa;
   padding: 30px 40px;
   border-radius: 12px;
   border-left: 6px solid #27ae60;
   min-width: 500px;
   box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
 }
 
 .formula-name {
   font-size: 20px;
   font-weight: 600;
   color: #2c3e50;
   margin-bottom: 15px;
 }
 
 .formula-equation {
   font-family: 'Times New Roman', serif;
   font-size: 32px;
   color: #e74c3c;
   background: white;
   padding: 15px 20px;
   border-radius: 8px;
   margin-bottom: 12px;
   border: 2px solid #e74c3c;
   text-align: center;
 }
 
 .formula-note {
   font-size: 16px;
   color: #7f8c8d;
   font-style: italic;
   text-align: center;
 }
 
 /* 例题展示样式 */
 .ppt-example {
   text-align: left;
 }
 
 .example-display {
   background: #fff8e1;
   padding: 30px;
   border-radius: 12px;
   border-left: 6px solid #ff9800;
   box-shadow: 0 4px 15px rgba(255, 152, 0, 0.2);
 }
 
 .example-question {
   font-size: 20px;
   line-height: 1.6;
   color: #e65100;
   margin-bottom: 20px;
   font-weight: 500;
 }
 
 .example-solution {
   font-size: 18px;
   color: #424242;
   background: white;
   padding: 20px;
   border-radius: 8px;
   border-left: 4px solid #4caf50;
   font-family: 'Courier New', monospace;
 }
 
 /* 概念展示样式 */
 .ppt-concepts {
   text-align: left;
 }
 
 .concept-list {
   list-style: none;
   padding: 0;
   margin: 0;
 }
 
 .concept-item {
   font-size: 16px;
   line-height: 1.6;
   margin-bottom: 15px;
   padding: 12px 0 12px 35px;
   position: relative;
   color: #2c3e50;
   border-bottom: 1px solid #ecf0f1;
 }
 
 .concept-item::before {
   content: "●";
   position: absolute;
   left: 0;
   top: 12px;
   color: #3498db;
   font-size: 20px;
 }
 

 
 .formula-item {
   background: white;
   padding: 15px;
   border-radius: 6px;
   box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
   border-left: 3px solid #27ae60;
 }

 .formula-header {
   margin-bottom: 8px;
 }
 
 .formula-name {
   font-weight: 600;
   color: #2c3e50;
   margin-bottom: 8px;
   font-size: 16px;
 }
 
 .formula-content {
   font-family: 'Courier New', monospace;
   font-size: 16px;
   color: #e74c3c;
   background: #fff5f5;
   padding: 8px;
   border-radius: 4px;
 }

 .formula-description {
   font-size: 13px;
   color: #7f8c8d;
   font-style: italic;
   margin-top: 8px;
   padding: 6px 10px;
   background: #f8f9fa;
   border-radius: 4px;
 }

 /* 图像性质样式 */
 .graph-properties {
   margin-bottom: 30px;
   padding: 20px;
   background: #f0f8ff;
   border-radius: 8px;
   border: 1px solid #e3f2fd;
 }

 .graph-properties h4 {
   margin: 0 0 15px 0;
   color: #1976d2;
   font-size: 18px;
 }

 .properties-grid {
   display: grid;
   grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
   gap: 15px;
 }

 .property-item {
   background: white;
   padding: 12px;
   border-radius: 6px;
   border-left: 3px solid #2196f3;
   box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
 }

 .property-name {
   font-weight: 600;
   color: #1976d2;
   margin-bottom: 4px;
   font-size: 14px;
 }

 .property-rule {
   color: #424242;
   font-size: 13px;
   line-height: 1.4;
 }
 
 .example-item {
   background: white;
   padding: 20px;
   border-radius: 6px;
   box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
   margin-bottom: 15px;
   border-left: 4px solid #ff9800;
 }

 .example-header {
   margin-bottom: 15px;
 }
 
 .example-title {
   font-weight: 600;
   color: #2c3e50;
   margin-bottom: 10px;
   font-size: 16px;
   color: #e65100;
 }

 .example-question {
   background: #fff3e0;
   padding: 12px;
   border-radius: 4px;
   margin-bottom: 10px;
   border-left: 3px solid #ff9800;
   line-height: 1.5;
 }

 .example-question strong {
   color: #e65100;
 }
 
 .example-solution {
   background: #f8f9fa;
   padding: 15px;
   border-radius: 4px;
   margin-bottom: 10px;
   border-left: 3px solid #4caf50;
 }

 .example-solution strong {
   color: #2e7d32;
   margin-bottom: 8px;
   display: block;
 }

 .example-solution pre {
   font-family: inherit;
   white-space: pre-line;
   line-height: 1.4;
   margin: 0;
   color: #424242;
 }

 .example-keypoints {
   background: #e8f5e8;
   padding: 10px;
   border-radius: 4px;
   border-left: 3px solid #4caf50;
 }

 .example-keypoints strong {
   color: #2e7d32;
   margin-right: 8px;
 }

 .keypoint-tag {
   display: inline-block;
   background: #4caf50;
   color: white;
   padding: 2px 8px;
   border-radius: 12px;
   font-size: 12px;
   margin: 2px 4px 2px 0;
 }
 
 /* 练习幻灯片样式 */
 .ppt-practice {
   display: flex;
   flex-direction: column;
   height: 100%;
 }
 
 .ppt-exercise {
   flex: 1;
 }
 
 .exercise-content {
   background: #e8f5e8;
   padding: 30px;
   border-radius: 12px;
   border-left: 6px solid #4caf50;
   box-shadow: 0 4px 15px rgba(76, 175, 80, 0.2);
 }
 
 .exercise-question {
   font-size: 18px;
   line-height: 1.5;
   color: #2e7d32;
   margin-bottom: 20px;
   font-weight: 500;
 }
 
 .exercise-options {
   display: flex;
   flex-direction: column;
   gap: 12px;
 }
 
 .option-item {
   font-size: 16px;
   padding: 12px 15px;
   background: white;
   border-radius: 6px;
   border: 2px solid #c8e6c9;
   transition: all 0.3s ease;
   cursor: pointer;
 }
 
 .option-item:hover {
   background: #f1f8e9;
   border-color: #4caf50;
   transform: translateX(10px);
 }
 
 .ppt-tips {
   flex: 1;
 }
 
 .tips-list {
   list-style: none;
   padding: 0;
   margin: 0;
 }
 
 .tip-item {
   font-size: 16px;
   line-height: 1.6;
   margin-bottom: 15px;
   padding: 12px 0 12px 35px;
   position: relative;
   color: #2c3e50;
   border-bottom: 1px solid #ecf0f1;
 }
 
 .tip-item::before {
   content: "💡";
   position: absolute;
   left: 0;
   top: 12px;
   font-size: 18px;
 }
 
 .exercise-header {
   display: flex;
   justify-content: space-between;
   align-items: center;
   margin-bottom: 15px;
   padding-bottom: 10px;
   border-bottom: 1px solid #eee;
 }

 .exercise-meta {
   display: flex;
   align-items: center;
   gap: 10px;
   flex-wrap: wrap;
 }
 
 .exercise-type {
   background: #3498db;
   color: white;
   padding: 4px 12px;
   border-radius: 12px;
   font-size: 12px;
   font-weight: 600;
 }
 
 .exercise-number {
   font-weight: 600;
   color: #2c3e50;
   font-size: 14px;
 }

 .exercise-difficulty {
   padding: 2px 8px;
   border-radius: 10px;
   font-size: 11px;
   font-weight: 600;
 }

 .exercise-difficulty.difficulty-基础 {
   background: #c8e6c9;
   color: #2e7d32;
 }

 .exercise-difficulty.difficulty-中等 {
   background: #fff3c4;
   color: #f57c00;
 }

 .exercise-difficulty.difficulty-综合 {
   background: #ffcdd2;
   color: #c62828;
 }

 .exercise-points {
   background: #e1bee7;
   color: #7b1fa2;
   padding: 2px 8px;
   border-radius: 10px;
   font-size: 11px;
   font-weight: 600;
 }

 .practical-tag {
   background: linear-gradient(45deg, #ff9800, #ffc107);
   color: white;
   padding: 4px 8px;
   border-radius: 12px;
   font-size: 11px;
   font-weight: 600;
   box-shadow: 0 2px 4px rgba(255, 152, 0, 0.3);
 }
 
 .exercise-question {
   font-size: 16px;
   font-weight: 500;
   color: #2c3e50;
   margin-bottom: 15px;
   line-height: 1.5;
 }
 
 .exercise-options {
   margin: 15px 0;
 }
 
 .option-item {
   padding: 8px 15px;
   margin: 5px 0;
   background: #f8f9fa;
   border-radius: 4px;
   border-left: 3px solid #ddd;
   transition: all 0.2s ease;
 }
 
 .option-item:hover {
   background: #e9ecef;
   border-left-color: #3498db;
 }
 
 .exercise-answer {
   background: #d4edda;
   color: #155724;
   padding: 10px 15px;
   border-radius: 4px;
   margin: 10px 0;
   border-left: 4px solid #28a745;
 }
 
 .exercise-solution {
   background: #f8f9fa;
   padding: 15px;
   border-radius: 4px;
   margin: 10px 0;
   border-left: 4px solid #6c757d;
 }
 
 .exercise-solution pre {
   margin: 5px 0 0 0;
   font-family: inherit;
   white-space: pre-wrap;
   line-height: 1.4;
 }
 
 .exercise-explanation {
   background: #fff3cd;
   color: #856404;
   padding: 10px 15px;
   border-radius: 4px;
   margin: 10px 0;
   border-left: 4px solid #ffc107;
 }


 
 /* 总结幻灯片样式 */
 .ppt-summary {
   display: flex;
   flex-direction: column;
   gap: 25px;
   height: 100%;
 }
 
 .ppt-key-points {
   flex: 1;
 }
 
 .key-points-list {
   list-style: none;
   padding: 0;
   margin: 0;
 }
 
 .key-point-item {
   font-size: 16px;
   line-height: 1.6;
   margin-bottom: 15px;
   padding: 12px 0 12px 35px;
   position: relative;
   color: #2c3e50;
   border-bottom: 1px solid #ecf0f1;
 }
 
 .key-point-item::before {
   content: "★";
   position: absolute;
   left: 0;
   top: 12px;
   color: #f39c12;
   font-size: 20px;
 }
 
 .ppt-homework {
   background: #fff3e0;
   padding: 20px;
   border-radius: 10px;
   border-left: 4px solid #ff9800;
   box-shadow: 0 4px 15px rgba(255, 152, 0, 0.2);
 }
 
 .homework-content {
   display: flex;
   flex-direction: column;
   gap: 12px;
 }
 
 .homework-item {
   font-size: 16px;
   line-height: 1.5;
   color: #e65100;
   background: white;
   padding: 12px 15px;
   border-radius: 6px;
   border-left: 3px solid #ff9800;
 }
 
 .knowledge-map {
   text-align: center;
 }
 
 .map-center {
   display: inline-block;
   background: #3498db;
   color: white;
   padding: 15px 25px;
   border-radius: 25px;
   font-size: 18px;
   font-weight: 600;
   margin-bottom: 20px;
 }
 
 .map-branches {
   display: flex;
   justify-content: space-around;
   flex-wrap: wrap;
   gap: 20px;
 }
 
 .branch-item {
   flex: 1;
   min-width: 150px;
   background: #f8f9fa;
   padding: 15px;
   border-radius: 8px;
   border-top: 3px solid #27ae60;
 }
 
 .branch-name {
   font-weight: 600;
   margin-bottom: 10px;
   color: #2c3e50;
 }
 
 .branch-items {
   display: flex;
   flex-wrap: wrap;
   gap: 5px;
 }
 
 .branch-item-tag {
   background: #27ae60;
   color: white;
   padding: 3px 8px;
   border-radius: 12px;
   font-size: 12px;
 }
 
 .homework-list {
   display: flex;
   flex-direction: column;
   gap: 10px;
 }
 
 .homework-item {
   padding: 12px 15px;
   background: #fff8e1;
   border-radius: 6px;
   border-left: 4px solid #ff9800;
   line-height: 1.5;
   margin-bottom: 10px;
 }

 .homework-structured {
   display: flex;
   flex-direction: column;
   gap: 8px;
 }

 .homework-header {
   display: flex;
   justify-content: space-between;
   align-items: center;
 }

 .homework-type {
   background: #ff9800;
   color: white;
   padding: 2px 8px;
   border-radius: 10px;
   font-size: 12px;
   font-weight: 600;
 }

 .homework-time {
   background: #e0e0e0;
   color: #424242;
   padding: 2px 8px;
   border-radius: 10px;
   font-size: 11px;
 }

 .homework-content {
   font-weight: 600;
   color: #e65100;
 }

 .homework-requirement {
   font-size: 13px;
   color: #757575;
   font-style: italic;
 }

 .homework-simple {
   color: #e65100;
 }

 /* 学习方法样式 */
 .study-methods-section {
   background: #e8f5e8;
   padding: 20px;
   border-radius: 8px;
   margin-bottom: 25px;
   border-left: 4px solid #4caf50;
 }

 .study-methods-section h4 {
   margin: 0 0 15px 0;
   color: #2e7d32;
   font-size: 18px;
 }

 .methods-grid {
   display: grid;
   grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
   gap: 15px;
 }

 .method-item {
   background: white;
   padding: 15px;
   border-radius: 6px;
   border-left: 3px solid #4caf50;
   box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
 }

 .method-title {
   font-weight: 600;
   color: #2e7d32;
   margin-bottom: 8px;
   font-size: 15px;
 }

 .method-description {
   color: #424242;
   font-size: 13px;
   line-height: 1.4;
 }

 /* 常见错误样式 */
 .common-mistakes-section {
   background: #ffebee;
   padding: 20px;
   border-radius: 8px;
   margin-bottom: 25px;
   border-left: 4px solid #f44336;
 }

 .common-mistakes-section h4 {
   margin: 0 0 15px 0;
   color: #c62828;
   font-size: 18px;
 }

 .mistakes-list {
   display: flex;
   flex-direction: column;
   gap: 10px;
 }

 .mistake-item {
   background: white;
   padding: 12px;
   border-radius: 6px;
   border-left: 3px solid #f44336;
   display: flex;
   align-items: flex-start;
   gap: 10px;
 }

 .mistake-number {
   background: #f44336;
   color: white;
   width: 20px;
   height: 20px;
   border-radius: 50%;
   display: flex;
   align-items: center;
   justify-content: center;
   font-size: 11px;
   font-weight: bold;
   flex-shrink: 0;
 }

 .mistake-text {
   flex: 1;
   color: #424242;
   font-size: 14px;
   line-height: 1.4;
 }

 /* 下节课预告样式 */
 .next-lesson-section {
   background: #e3f2fd;
   padding: 20px;
   border-radius: 8px;
   border-left: 4px solid #2196f3;
 }

 .next-lesson-section h4 {
   margin: 0 0 15px 0;
   color: #1976d2;
   font-size: 18px;
 }

 .next-lesson-content {
   display: flex;
   flex-direction: column;
   gap: 10px;
 }

 .next-topic {
   background: white;
   padding: 12px;
   border-radius: 6px;
   border-left: 3px solid #2196f3;
   font-weight: 600;
   color: #1976d2;
   font-size: 16px;
 }

 .next-connection {
   background: #f8f9fa;
   padding: 10px;
   border-radius: 6px;
   color: #424242;
   font-size: 14px;
   line-height: 1.4;
   font-style: italic;
 }
 
 /* 幻灯片底部 */
 .slide-footer {
   display: flex;
   justify-content: space-between;
   align-items: center;
   padding: 15px 40px;
   background: #f8f9fa;
   border-top: 1px solid #eee;
 }
 
 .slide-features {
   display: flex;
   gap: 8px;
 }
 
 .slide-time {
   font-size: 14px;
   color: #6c757d;
   font-weight: 500;
 }
 
 /* 幻灯片缩略图导航 */
 .slide-thumbnails {
   background: white;
   border-radius: 8px;
   padding: 15px;
   box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
 }
 
 .thumbnails-title {
   font-weight: 600;
   color: #2c3e50;
   margin-bottom: 15px;
   font-size: 16px;
 }
 
 .thumbnails-container {
   display: flex;
   gap: 10px;
   overflow-x: auto;
   padding: 10px 0;
 }
 
 .thumbnail-item {
   min-width: 120px;
   padding: 15px;
   background: #f8f9fa;
   border-radius: 6px;
   cursor: pointer;
   transition: all 0.2s ease;
   border: 2px solid transparent;
   text-align: center;
 }
 
 .thumbnail-item:hover {
   background: #e9ecef;
   transform: translateY(-2px);
 }
 
 .thumbnail-item.active {
   background: #e3f2fd;
   border-color: #2196f3;
   box-shadow: 0 2px 8px rgba(33, 150, 243, 0.3);
 }
 
 .thumbnail-number {
   font-size: 20px;
   font-weight: bold;
   color: #3498db;
   margin-bottom: 5px;
 }
 
 .thumbnail-title {
   font-size: 14px;
   font-weight: 600;
   color: #2c3e50;
   margin-bottom: 3px;
   white-space: nowrap;
   overflow: hidden;
   text-overflow: ellipsis;
 }
 
 .thumbnail-type {
   font-size: 12px;
   color: #6c757d;
 }
 
 /* 对话框底部按钮 */
 .dialog-footer {
   display: flex;
   justify-content: space-between;
   align-items: center;
 }
 
 /* PPT响应式适配 */
 @media (max-width: 1200px) {
   .ppt-slide {
     width: 800px;
     height: 600px;
   }
   
   .ppt-title {
     font-size: 32px;
   }
   
   .ppt-subtitle {
     font-size: 16px;
   }
   
   .section-title {
     font-size: 24px;
   }
   
   .ppt-info-header {
     flex-direction: column;
     gap: 15px;
     align-items: stretch;
   }
   
   .ppt-meta {
     flex-direction: column;
     gap: 5px;
   }
 }
 
 @media (max-width: 768px) {
   .ppt-preview-dialog .el-dialog {
     width: 95% !important;
     margin: 0 auto;
   }
   
   .ppt-slide {
     width: 100%;
     height: 500px;
   }
   
   .ppt-header {
     padding: 15px 20px 10px 20px;
   }
   
   .ppt-content {
     padding: 20px 25px;
   }
   
   .ppt-title {
     font-size: 24px;
   }
   
   .ppt-subtitle {
     font-size: 14px;
   }
   
   .section-title {
     font-size: 20px;
     margin-bottom: 15px;
   }
   
   .objective-item, .concept-item, .key-point-item, .tip-item {
     font-size: 14px;
     padding: 10px 0 10px 30px;
     margin-bottom: 12px;
   }
   
   .formula-equation {
     font-size: 24px;
   }
   
   .exercise-question {
     font-size: 18px;
   }
   
   .option-item {
     font-size: 16px;
     padding: 12px 15px;
   }
   
   .thumbnails-container {
     flex-direction: column;
     max-height: 200px;
   }
   
   .thumbnail-item {
     min-width: auto;
     width: 100%;
  }
}
</style> 