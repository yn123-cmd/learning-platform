<template>
  <div class="course-positioning">
    <div class="page-header">
      <h2>步骤1/6：课程基本信息配置</h2>
      <p>建立教学框架，配置课程基础信息</p>
    </div>
    
    <!-- 多媒体文档输入区域 -->
    <div class="multimedia-input-section">
      <el-card class="multimedia-card">
        <div slot="header" class="card-header">
          <i class="el-icon-upload"></i>
          <span>智能文档输入助手</span>
          <el-tag type="info" size="small">支持多种输入方式</el-tag>
        </div>
        
        <el-tabs v-model="activeInputTab" @tab-click="handleTabClick">
          <!-- 文档上传 -->
          <el-tab-pane label="教学大纲" name="document">
            <div class="input-panel document-panel">
              <div class="panel-header">
                <i class="el-icon-document"></i>
                <h4>上传教学大纲文档</h4>
                <p>支持PDF、Word格式，AI自动解析课程标准和教学要求</p>
              </div>
              
              <!-- 功能实例展示 -->
              <div class="feature-demo">
                <h5><i class="el-icon-star-on"></i> 功能实例展示</h5>
                <div class="demo-gallery">
                  <div class="demo-item" @click="loadDocumentExample(1)">
                    <div class="demo-preview">
                      <i class="el-icon-document"></i>
                      <span>九年级数学二次函数教学大纲.pdf</span>
                    </div>
                    <div class="demo-result">
                      <span class="result-tag subject">数学</span>
                      <span class="result-tag grade">九年级</span>
                      <span class="result-tag standard">2022版课标</span>
                    </div>
                    <div class="demo-details">
                      <small>识别关键词：二次函数、抛物线、图像性质 | 置信度：98%</small>
                    </div>
                  </div>
                  <div class="demo-item" @click="loadDocumentExample(2)">
                    <div class="demo-preview">
                      <i class="el-icon-document"></i>
                      <span>英语课程计划.docx</span>
                    </div>
                    <div class="demo-result">
                      <span class="result-tag subject">英语</span>
                      <span class="result-tag grade">五年级</span>
                      <span class="result-tag standard">义务教育标准</span>
                    </div>
                    <div class="demo-details">
                      <small>识别关键词：阅读、词汇、语法 | 置信度：97%</small>
                    </div>
                  </div>
                  <div class="demo-item" @click="loadDocumentExample(3)">
                    <div class="demo-preview">
                      <i class="el-icon-document"></i>
                      <span>物理实验指导.pdf</span>
                    </div>
                    <div class="demo-result">
                      <span class="result-tag subject">物理</span>
                      <span class="result-tag grade">八年级</span>
                      <span class="result-tag standard">实验课程</span>
                    </div>
                    <div class="demo-details">
                      <small>识别关键词：力学、实验、测量 | 置信度：96%</small>
                    </div>
                  </div>
                </div>
              </div>
              
              <el-upload
                class="document-uploader"
                :action="uploadUrl"
                accept=".pdf,.doc,.docx"
                :before-upload="beforeDocumentUpload"
                :on-success="handleDocumentSuccess"
                :on-error="handleDocumentError"
                :show-file-list="true"
                :limit="3"
                drag
                multiple
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">
                  将文件拖拽到此处，或<em>点击上传</em>
                </div>
                <div class="el-upload__tip" slot="tip">
                  支持PDF、Word格式，单个文件大小不超过10MB
                </div>
              </el-upload>
              
              <!-- AI处理过程演示 -->
              <div v-if="showDocumentProcessing" class="processing-demo">
                <h5><i class="el-icon-loading"></i> AI解析过程</h5>
                <div class="processing-steps">
                  <div class="step-item" :class="{ active: processingStep >= 1, completed: processingStep > 1 }">
                    <div class="step-icon">1</div>
                    <div class="step-content">
                      <h6>文档内容提取</h6>
                      <p>正在提取PDF/Word文档中的文字内容...</p>
                    </div>
                  </div>
                  <div class="step-item" :class="{ active: processingStep >= 2, completed: processingStep > 2 }">
                    <div class="step-icon">2</div>
                    <div class="step-content">
                      <h6>学科识别分析</h6>
                      <p>基于关键词和内容结构识别学科类型...</p>
                    </div>
                  </div>
                  <div class="step-item" :class="{ active: processingStep >= 3, completed: processingStep > 3 }">
                    <div class="step-icon">3</div>
                    <div class="step-content">
                      <h6>年级推测</h6>
                      <p>根据教学内容难度和知识点分析适用年级...</p>
                    </div>
                  </div>
                  <div class="step-item" :class="{ active: processingStep >= 4, completed: processingStep > 4 }">
                    <div class="step-icon">4</div>
                    <div class="step-content">
                      <h6>课标匹配</h6>
                      <p>匹配对应的课程标准和教学要求...</p>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 解析结果展示 -->
              <div v-if="documentAnalysis.length > 0" class="analysis-results">
                <h5>文档解析结果</h5>
                <div class="analysis-items">
                  <div 
                    v-for="item in documentAnalysis" 
                    :key="item.id"
                    class="analysis-item"
                    @click="applyAnalysisResult(item)"
                  >
                    <div class="item-header">
                      <span class="item-type">{{ item.type }}</span>
                      <span class="confidence">置信度: {{ item.confidence }}%</span>
                    </div>
                    <p class="item-content">{{ item.content }}</p>
                    <div class="item-details" v-if="item.details">
                      <el-tag 
                        v-for="detail in item.details" 
                        :key="detail"
                        size="mini"
                        style="margin-right: 4px;"
                      >
                        {{ detail }}
                      </el-tag>
                    </div>
                    <el-button type="text" size="mini">应用到表单</el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <!-- 语音录入 -->
          <el-tab-pane label="语音描述" name="voice">
            <div class="input-panel voice-panel">
              <div class="panel-header">
                <i class="el-icon-microphone"></i>
                <h4>语音描述课程背景</h4>
                <p>口述课程背景和教学理念，系统自动转文字并智能匹配</p>
              </div>
              
              <!-- 语音功能实例展示 -->
              <div class="feature-demo">
                <h5><i class="el-icon-star-on"></i> 语音示例</h5>
                <div class="voice-examples">
                  <div class="voice-example" @click="playVoiceExample(1)">
                    <div class="example-header">
                      <i class="el-icon-video-play"></i>
                      <span>九年级二次函数图像课程</span>
                      <span class="duration">52s</span>
                    </div>
                    <p class="example-text">"我准备给九年级学生讲解二次函数的图像与性质，重点是抛物线的开口方向、对称轴和顶点坐标..."</p>
                    <div class="example-tags">
                      <el-tag type="success" size="mini">数学</el-tag>
                      <el-tag type="warning" size="mini">九年级</el-tag>
                      <el-tag type="info" size="mini">二次函数</el-tag>
                    </div>
                  </div>
                  
                  <div class="voice-example" @click="playVoiceExample(2)">
                    <div class="example-header">
                      <i class="el-icon-video-play"></i>
                      <span>英语阅读理解课程</span>
                      <span class="duration">38s</span>
                    </div>
                    <p class="example-text">"今天我要为五年级的学生设计一节英语阅读理解课，重点培养学生的阅读技巧..."</p>
                    <div class="example-tags">
                      <el-tag type="success" size="mini">英语</el-tag>
                      <el-tag type="warning" size="mini">五年级</el-tag>
                      <el-tag type="info" size="mini">阅读理解</el-tag>
                    </div>
                  </div>
                  
                  <div class="voice-example" @click="playVoiceExample(3)">
                    <div class="example-header">
                      <i class="el-icon-video-play"></i>
                      <span>物理力学实验课</span>
                      <span class="duration">52s</span>
                    </div>
                    <p class="example-text">"这节课我们要通过实验来理解力的作用效果，让八年级学生亲手操作..."</p>
                    <div class="example-tags">
                      <el-tag type="success" size="mini">物理</el-tag>
                      <el-tag type="warning" size="mini">八年级</el-tag>
                      <el-tag type="info" size="mini">力学实验</el-tag>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="voice-recorder">
                <div class="recorder-controls">
                  <el-button 
                    :type="isRecording ? 'danger' : 'primary'"
                    :icon="isRecording ? 'el-icon-video-pause' : 'el-icon-microphone'"
                    size="large"
                    @click="toggleRecording"
                    :loading="isProcessingVoice"
                  >
                    {{ isRecording ? '停止录音' : '开始录音' }}
                  </el-button>
                  
                  <div v-if="isRecording" class="recording-indicator">
                    <div class="recording-wave">
                      <span></span>
                      <span></span>
                      <span></span>
                      <span></span>
                    </div>
                    <span class="recording-time">{{ recordingTime }}s</span>
                  </div>
                </div>
                
                <!-- 录音历史 -->
                <div v-if="voiceRecordings.length > 0" class="voice-history">
                  <h5>录音记录</h5>
                  <div class="recording-list">
                    <div 
                      v-for="recording in voiceRecordings" 
                      :key="recording.id"
                      class="recording-item"
                    >
                      <div class="recording-info">
                        <span class="recording-name">录音 {{ recording.id }}</span>
                        <span class="recording-duration">{{ recording.duration }}s</span>
                      </div>
                      <div class="recording-actions">
                        <el-button type="text" size="mini" @click="playRecording(recording)">
                          <i class="el-icon-video-play"></i>
                        </el-button>
                        <el-button type="text" size="mini" @click="transcribeRecording(recording)">
                          转文字
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
                
                <!-- 语音转文字结果 -->
                <div v-if="voiceTranscription" class="transcription-result">
                  <h5>语音识别结果</h5>
                  <div class="transcription-content">
                    <p>{{ voiceTranscription.text }}</p>
                    <div class="transcription-actions">
                      <el-button type="primary" size="small" @click="analyzeVoiceContent">
                        智能解析
                      </el-button>
                      <el-button type="text" size="small" @click="editTranscription">
                        编辑
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <!-- 思维导图识别 -->
          <el-tab-pane label="思维导图" name="mindmap">
            <div class="input-panel mindmap-panel">
              <div class="panel-header">
                <i class="el-icon-picture-outline"></i>
                <h4>上传课程结构图</h4>
                <p>支持手绘或软件制作的思维导图，AI识别并转换为知识点关联图谱</p>
              </div>
              
              <!-- 思维导图实例展示 -->
              <div class="feature-demo">
                <h5><i class="el-icon-star-on"></i> 思维导图实例</h5>
                <div class="mindmap-examples">
                  <div class="mindmap-example" @click="loadMindmapExample(1)">
                    <div class="example-image">
                      <div class="mock-mindmap math">
                        <div class="central-node">二次函数</div>
                        <div class="branch-node top-left">开口方向</div>
                        <div class="branch-node top-right">对称轴</div>
                        <div class="branch-node bottom-left">顶点坐标</div>
                        <div class="branch-node bottom-right">图像性质</div>
                      </div>
                    </div>
                    <div class="example-info">
                      <h6>二次函数图像思维导图</h6>
                      <p>识别: 5个核心概念，抛物线性质清晰</p>
                      <div class="confidence-score">置信度: 96%</div>
                    </div>
                  </div>
                  
                  <div class="mindmap-example" @click="loadMindmapExample(2)">
                    <div class="example-image">
                      <div class="mock-mindmap english">
                        <div class="central-node">Reading Skills</div>
                        <div class="branch-node top-left">Skimming</div>
                        <div class="branch-node top-right">Scanning</div>
                        <div class="branch-node bottom-left">Inference</div>
                        <div class="branch-node bottom-right">Summary</div>
                      </div>
                    </div>
                    <div class="example-info">
                      <h6>英语阅读技能图</h6>
                      <p>识别: 5个技能节点，层级结构清晰</p>
                      <div class="confidence-score">置信度: 94%</div>
                    </div>
                  </div>
                  
                  <div class="mindmap-example" @click="loadMindmapExample(3)">
                    <div class="example-image">
                      <div class="mock-mindmap physics">
                        <div class="central-node">力学基础</div>
                        <div class="branch-node top-left">力的概念</div>
                        <div class="branch-node top-right">力的测量</div>
                        <div class="branch-node bottom-left">力的效果</div>
                        <div class="branch-node bottom-right">力的平衡</div>
                      </div>
                    </div>
                    <div class="example-info">
                      <h6>物理力学概念图</h6>
                      <p>识别: 5个核心概念，实验导向</p>
                      <div class="confidence-score">置信度: 92%</div>
                    </div>
                  </div>
                </div>
              </div>
              
              <el-upload
                class="image-uploader"
                :action="uploadUrl"
                accept=".jpg,.jpeg,.png,.gif,.webp"
                :before-upload="beforeImageUpload"
                :on-success="handleImageSuccess"
                :show-file-list="false"
                drag
              >
                <img v-if="mindmapImage" :src="mindmapImage" class="uploaded-image">
                <div v-else class="upload-placeholder">
                  <i class="el-icon-plus"></i>
                  <div class="upload-text">上传思维导图</div>
                </div>
              </el-upload>
              
              <!-- 图像识别结果 -->
              <div v-if="mindmapAnalysis" class="mindmap-analysis">
                <h5>图像识别结果</h5>
                <div class="analysis-preview">
                  <div class="recognized-elements">
                    <div 
                      v-for="element in mindmapAnalysis.elements" 
                      :key="element.id"
                      class="element-item"
                      :class="element.type"
                    >
                      <span class="element-text">{{ element.text }}</span>
                      <span class="element-confidence">{{ element.confidence }}%</span>
                    </div>
                  </div>
                  
                  <div class="analysis-actions">
                    <el-button type="primary" @click="generateKnowledgeGraph">
                      生成知识图谱
                    </el-button>
                    <el-button type="text" @click="editRecognitionResults">
                      编辑结果
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <!-- 视频分析 -->
          <el-tab-pane label="示范课视频" name="video">
            <div class="input-panel video-panel">
              <div class="panel-header">
                <i class="el-icon-video-camera"></i>
                <h4>上传示范课视频</h4>
                <p>自动提取教学要点和方法特征，生成课程定位参考</p>
              </div>
              
              <!-- 视频分析实例展示 -->
              <div class="feature-demo">
                <h5><i class="el-icon-star-on"></i> 视频分析实例</h5>
                <div class="video-examples">
                  <div class="video-example" @click="loadVideoExample(1)">
                    <div class="video-thumbnail">
                      <i class="el-icon-video-play"></i>
                      <div class="video-info">
                        <span class="video-title">九年级二次函数图像课.mp4</span>
                        <span class="video-duration">45:30</span>
                      </div>
                    </div>
                    <div class="analysis-preview">
                      <div class="key-points">
                        <span class="point-tag">抛物线特征</span>
                        <span class="point-tag">对称轴求法</span>
                        <span class="point-tag">顶点坐标</span>
                      </div>
                      <div class="teaching-methods">
                        <span class="method-tag">图像教学</span>
                        <span class="method-tag">对比分析</span>
                        <span class="method-tag">探究学习</span>
                      </div>
                    </div>
                  </div>
                  
                  <div class="video-example" @click="loadVideoExample(2)">
                    <div class="video-thumbnail">
                      <i class="el-icon-video-play"></i>
                      <div class="video-info">
                        <span class="video-title">五年级英语阅读课.mp4</span>
                        <span class="video-duration">38:20</span>
                      </div>
                    </div>
                    <div class="analysis-preview">
                      <div class="key-points">
                        <span class="point-tag">阅读策略</span>
                        <span class="point-tag">词汇学习</span>
                        <span class="point-tag">理解检测</span>
                      </div>
                      <div class="teaching-methods">
                        <span class="method-tag">情境教学</span>
                        <span class="method-tag">小组合作</span>
                        <span class="method-tag">游戏化学习</span>
                      </div>
                    </div>
                  </div>
                  
                  <div class="video-example" @click="loadVideoExample(3)">
                    <div class="video-thumbnail">
                      <i class="el-icon-video-play"></i>
                      <div class="video-info">
                        <span class="video-title">八年级物理实验课.mp4</span>
                        <span class="video-duration">45:30</span>
                      </div>
                    </div>
                    <div class="analysis-preview">
                      <div class="key-points">
                        <span class="point-tag">实验操作</span>
                        <span class="point-tag">数据记录</span>
                        <span class="point-tag">结论总结</span>
                      </div>
                      <div class="teaching-methods">
                        <span class="method-tag">实验教学</span>
                        <span class="method-tag">观察法</span>
                        <span class="method-tag">探究学习</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <el-upload
                class="video-uploader"
                :action="uploadUrl"
                accept=".mp4,.avi,.mov,.wmv"
                :before-upload="beforeVideoUpload"
                :on-success="handleVideoSuccess"
                :on-progress="handleVideoProgress"
                :show-file-list="false"
                drag
              >
                <div v-if="uploadingVideo" class="uploading-state">
                  <el-progress 
                    :percentage="videoUploadProgress" 
                    :status="videoUploadProgress === 100 ? 'success' : undefined"
                  ></el-progress>
                  <p>正在上传视频...</p>
                </div>
                <div v-else-if="uploadedVideo" class="video-preview">
                  <i class="el-icon-video-play"></i>
                  <p>{{ uploadedVideo.name }}</p>
                  <el-button type="text" @click="startVideoAnalysis">开始分析</el-button>
                </div>
                <div v-else class="upload-placeholder">
                  <i class="el-icon-upload"></i>
                  <div class="upload-text">
                    将视频文件拖拽到此处，或<em>点击上传</em>
                  </div>
                  <div class="upload-tip">支持MP4、AVI等格式，建议文件大小不超过500MB</div>
                </div>
              </el-upload>
              
              <!-- 视频分析结果 -->
              <div v-if="videoAnalysis" class="video-analysis">
                <h5>视频分析结果</h5>
                <div class="analysis-sections">
                  <div class="analysis-section">
                    <h6>教学要点</h6>
                    <ul>
                      <li v-for="point in videoAnalysis.keyPoints" :key="point">{{ point }}</li>
                    </ul>
                  </div>
                  
                  <div class="analysis-section">
                    <h6>教学方法</h6>
                    <el-tag 
                      v-for="method in videoAnalysis.methods" 
                      :key="method"
                      type="info"
                      size="small"
                      style="margin-right: 8px; margin-bottom: 8px;"
                    >
                      {{ method }}
                    </el-tag>
                  </div>
                  
                  <div class="analysis-section">
                    <h6>适用年级</h6>
                    <p>{{ videoAnalysis.suggestedGrade }}</p>
                  </div>
                  
                  <div class="analysis-section">
                    <h6>学科匹配</h6>
                    <p>{{ videoAnalysis.suggestedSubject }}</p>
                  </div>
                </div>
                
                <el-button type="primary" @click="applyVideoAnalysis">
                  应用分析结果
                </el-button>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
    
    <div class="form-section">
      <el-row :gutter="20">
        <!-- 学科选择器 -->
        <el-col :span="8">
          <div class="form-item">
            <h3>学科选择</h3>
            <el-select v-model="formData.subject" placeholder="请选择学科" size="large" @change="onSubjectChange">
              <el-option
                v-for="subject in subjects"
                :key="subject.value"
                :label="subject.label"
                :value="subject.value"
              >
                <i :class="subject.icon"></i>
                <span style="margin-left: 10px;">{{ subject.label }}</span>
              </el-option>
            </el-select>
          </div>
        </el-col>
        
        <!-- 年级选择器 -->
        <el-col :span="8">
          <div class="form-item">
            <h3>年级选择</h3>
            <el-select v-model="formData.grade" placeholder="请选择年级" size="large" @change="onGradeChange">
              <el-option
                v-for="grade in grades"
                :key="grade.value"
                :label="grade.label"
                :value="grade.value"
              />
            </el-select>
          </div>
        </el-col>
        
        <!-- 课程标准选择器 -->
        <el-col :span="8">
          <div class="form-item">
            <h3>课程标准</h3>
            <el-select v-model="formData.standard" placeholder="请选择课程标准" size="large" :disabled="!formData.subject">
              <el-option
                v-for="standard in filteredStandards"
                :key="standard.value"
                :label="standard.label"
                :value="standard.value"
              />
            </el-select>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- AI助手推荐 -->
    <div class="ai-assistant">
      <el-card class="ai-recommendation-card">
        <div slot="header" class="card-header">
          <i class="el-icon-magic-stick"></i>
          <span>AI课时主题推荐</span>
          <el-button type="text" size="small" @click="refreshRecommendations" :loading="loadingRecommendations">
            刷新推荐
          </el-button>
        </div>
        
        <div v-if="suggestedTopics.length > 0" class="topic-suggestions">
          <div
            v-for="topic in suggestedTopics"
            :key="topic.id"
            class="topic-suggestion-card"
            :class="{ 'selected': selectedTopic && selectedTopic.id === topic.id }"
            @click="setSelectedTopic(topic)"
          >
            <div class="topic-header">
              <h4>{{ topic.title }}</h4>
              <el-tag :type="topic.difficulty === '简单' ? 'success' : topic.difficulty === '中等' ? 'warning' : 'danger'" size="small">
                {{ topic.difficulty }}
              </el-tag>
            </div>
            <p class="topic-description">{{ topic.description }}</p>
            <div class="topic-meta">
              <span class="duration">
                <i class="el-icon-time"></i>
                {{ topic.duration }}课时
              </span>
              <span class="knowledge-points">
                <i class="el-icon-collection-tag"></i>
                {{ topic.knowledgePoints.length }}个知识点
              </span>
            </div>
          </div>
        </div>
        
        <div v-else-if="loadingRecommendations" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else class="empty-state">
          <el-empty description="请先选择学科和年级获取AI推荐" />
        </div>
      </el-card>
    </div>
    
    <!-- 知识体系关联图谱 -->
    <div class="knowledge-map">
      <el-card>
        <div slot="header" class="card-header">
          <i class="el-icon-share"></i>
          <span>知识体系关联图谱</span>
        </div>
        
        <div v-if="knowledgeNodes.length > 0" class="knowledge-graph-container">
          <div class="graph-controls">
            <el-button-group size="mini">
              <el-button @click="resetGraphView" icon="el-icon-refresh">重置视图</el-button>
              <el-button @click="toggleGraphLayout" icon="el-icon-switch-button">
                {{ currentLayout === 'force' ? '圆形布局' : '力导向布局' }}
              </el-button>
            </el-button-group>
          </div>
          <div id="knowledge-graph" class="knowledge-graph" ref="knowledgeGraph"></div>
          <div class="graph-legend">
            <div class="legend-item">
              <span class="legend-color prerequisite"></span>
              <span>前置知识</span>
            </div>
            <div class="legend-item">
              <span class="legend-color current"></span>
              <span>当前主题</span>
            </div>
            <div class="legend-item">
              <span class="legend-color extended"></span>
              <span>扩展知识</span>
            </div>
            <div class="legend-item">
              <span class="legend-color related"></span>
              <span>相关知识</span>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-graph">
          <el-empty description="请选择课程主题查看知识关联图谱" />
        </div>
      </el-card>
    </div>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button size="large" @click="saveAndNext" type="primary" :disabled="!isFormValid">
        保存并继续
      </el-button>
      <el-button size="large" @click="saveDraft">
        保存草稿
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CoursePositioning',
  data() {
    return {
      // 多媒体输入相关数据
      activeInputTab: 'document',
      uploadUrl: '/api/upload',
      
      // 文档上传相关
      documentAnalysis: [],
      showDocumentProcessing: false,
      processingStep: 0,
      
      // 语音录入相关
      isRecording: false,
      isProcessingVoice: false,
      recordingTime: 0,
      voiceRecordings: [],
      voiceTranscription: null,
      recordingTimer: null,
      mediaRecorder: null,
      showVoiceProcessing: false,
      currentTranscription: '',
      
      // 思维导图识别相关
      mindmapImage: '',
      mindmapAnalysis: null,
      showOcrProcessing: false,
      ocrStep: 0,
      
      // 视频分析相关
      uploadingVideo: false,
      videoUploadProgress: 0,
      uploadedVideo: null,
      videoAnalysis: null,
      showVideoProcessing: false,
      videoProcessStep: 0,
      
      formData: {
        subject: '',
        grade: '',
        standard: '',
        courseType: 'regular',
        estimatedHours: 2,
        difficulty: 3
      },
      expandedPanels: [],
      difficultyTexts: ['入门', '简单', '中等', '困难', '专业'],
      subjects: [
        { label: '数学', value: 'math', icon: 'el-icon-coordinate', color: '#667eea', hot: true },
        { label: '语文', value: 'chinese', icon: 'el-icon-edit-outline', color: '#f39c12' },
        { label: '英语', value: 'english', icon: 'el-icon-chat-line-round', color: '#2ecc71', hot: true },
        { label: '物理', value: 'physics', icon: 'el-icon-lightning', color: '#e74c3c' },
        { label: '化学', value: 'chemistry', icon: 'el-icon-cpu', color: '#9b59b6' },
        { label: '生物', value: 'biology', icon: 'el-icon-grape', color: '#27ae60' },
        { label: '历史', value: 'history', icon: 'el-icon-collection', color: '#34495e' },
        { label: '地理', value: 'geography', icon: 'el-icon-location', color: '#16a085' }
      ],
      gradeGroups: [
        {
          label: '小学阶段',
          options: [
            { label: '一年级', value: 1, stage: '小学' },
            { label: '二年级', value: 2, stage: '小学' },
            { label: '三年级', value: 3, stage: '小学' },
            { label: '四年级', value: 4, stage: '小学' },
            { label: '五年级', value: 5, stage: '小学' },
            { label: '六年级', value: 6, stage: '小学' }
          ]
        },
        {
          label: '初中阶段',
          options: [
            { label: '七年级', value: 7, stage: '初中' },
            { label: '八年级', value: 8, stage: '初中' },
            { label: '九年级', value: 9, stage: '初中' }
          ]
        }
      ],
      standards: [
        { label: '义务教育数学课程标准(2022版)', value: 'math_2022', subject: 'math', year: '2022' },
        { label: '义务教育语文课程标准(2022版)', value: 'chinese_2022', subject: 'chinese', year: '2022' },
        { label: '义务教育英语课程标准(2022版)', value: 'english_2022', subject: 'english', year: '2022' },
        { label: '义务教育物理课程标准(2022版)', value: 'physics_2022', subject: 'physics', year: '2022' },
        { label: '义务教育化学课程标准(2022版)', value: 'chemistry_2022', subject: 'chemistry', year: '2022' },
        { label: '义务教育生物课程标准(2022版)', value: 'biology_2022', subject: 'biology', year: '2022' },
        { label: '义务教育历史课程标准(2022版)', value: 'history_2022', subject: 'history', year: '2022' },
        { label: '义务教育地理课程标准(2022版)', value: 'geography_2022', subject: 'geography', year: '2022' }
      ],
      suggestedTopics: [],
      selectedTopic: null,
      knowledgeNodes: [],
      knowledgeLinks: [],
      loadingRecommendations: false,
      currentLayout: 'force'
    }
  },
  computed: {
    filteredStandards() {
      if (!this.formData.subject) return []
      return this.standards.filter(standard => standard.subject === this.formData.subject)
    },
    isFormValid() {
      return this.formData.subject && this.formData.grade && this.formData.standard
    },
    formProgress() {
      let progress = 0
      if (this.formData.subject) progress += 33
      if (this.formData.grade) progress += 33
      if (this.formData.standard) progress += 34
      return progress
    }
  },
  methods: {
    // 多媒体输入处理方法
    handleTabClick(tab) {
      console.log('切换到标签页:', tab.name)
    },
    
    // 文档上传处理
    beforeDocumentUpload(file) {
      const isPdf = file.type === 'application/pdf'
      const isDoc = file.type === 'application/msword' || file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isPdf && !isDoc) {
        this.$message.error('上传文件只能是 PDF 或 Word 格式!')
        return false
      }
      if (!isLt10M) {
        this.$message.error('上传文件大小不能超过 10MB!')
        return false
      }
      
      this.$message.info('正在上传文档...')
      return true
    },
    
    handleDocumentSuccess(response, file) {
      this.$message.success('文档上传成功，开始AI解析...')
      
      // 模拟AI文档解析
      setTimeout(() => {
        this.documentAnalysis = [
          {
            id: 1,
            type: '学科识别',
            content: '数学',
            confidence: 95
          },
          {
            id: 2,
            type: '年级推测',
            content: '七年级',
            confidence: 87
          },
          {
            id: 3,
            type: '课程标准',
            content: '义务教育数学课程标准(2022版)',
            confidence: 92
          },
          {
            id: 4,
            type: '教学要点',
            content: '函数概念、一次函数、图像与性质',
            confidence: 89
          }
        ]
        this.$message.success('文档解析完成')
      }, 3000)
    },
    
    handleDocumentError() {
      this.$message.error('文档上传失败，请重试')
    },
    
    applyAnalysisResult(item) {
      switch (item.type) {
        case '学科识别':
          const subject = this.subjects.find(s => s.label === item.content)
          if (subject) {
            this.formData.subject = subject.value
            this.onSubjectChange()
          }
          break
        case '年级推测':
          // 简化处理，实际应该更智能地匹配
          const gradeMap = {
            '七年级': 7, '八年级': 8, '九年级': 9,
            '一年级': 1, '二年级': 2, '三年级': 3,
            '四年级': 4, '五年级': 5, '六年级': 6
          }
          if (gradeMap[item.content]) {
            this.formData.grade = gradeMap[item.content]
            this.onGradeChange()
          }
          break
        case '课程标准':
          const standard = this.standards.find(s => s.label === item.content)
          if (standard) {
            this.formData.standard = standard.value
          }
          break
      }
      this.$message.success('已应用分析结果')
    },
    
    // 语音录入处理
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
        this.mediaRecorder.ondataavailable = event => {
          audioChunks.push(event.data)
        }
        
        this.mediaRecorder.onstop = () => {
          const audioBlob = new Blob(audioChunks, { type: 'audio/wav' })
          const recording = {
            id: this.voiceRecordings.length + 1,
            blob: audioBlob,
            duration: this.recordingTime,
            timestamp: new Date()
          }
          this.voiceRecordings.push(recording)
          
          // 停止所有音频流
          stream.getTracks().forEach(track => track.stop())
        }
        
        this.mediaRecorder.start()
        this.isRecording = true
        this.recordingTime = 0
        
        // 开始计时
        this.recordingTimer = setInterval(() => {
          this.recordingTime++
          if (this.recordingTime >= 300) { // 最长5分钟
            this.stopRecording()
          }
        }, 1000)
        
      } catch (error) {
        this.$message.error('无法访问麦克风，请检查权限设置')
        console.error('录音失败:', error)
      }
    },
    
    stopRecording() {
      if (this.mediaRecorder && this.mediaRecorder.state === 'recording') {
        this.mediaRecorder.stop()
      }
      this.isRecording = false
      clearInterval(this.recordingTimer)
    },
    
    playRecording(recording) {
      const audio = new Audio(URL.createObjectURL(recording.blob))
      audio.play()
    },
    
    transcribeRecording(recording) {
      this.isProcessingVoice = true
      this.$message.info('正在转换语音为文字...')
      
      // 模拟语音转文字API调用
      setTimeout(() => {
        this.voiceTranscription = {
          text: '我准备给九年级学生讲解二次函数的图像与性质，重点是抛物线的开口方向、对称轴和顶点坐标。通过图像变换让学生理解参数对抛物线形状和位置的影响，掌握二次函数的基本性质和实际应用。',
          confidence: 0.94,
          recordingId: recording.id
        }
        this.isProcessingVoice = false
        this.$message.success('语音转文字完成')
      }, 2000)
    },
    
    analyzeVoiceContent() {
      this.$message.info('正在分析语音内容...')
      
      // 模拟AI内容分析
      setTimeout(() => {
        // 基于语音内容自动填充表单
        this.formData.subject = 'math'
        this.formData.grade = 9
        this.onSubjectChange()
        this.onGradeChange()
        
        this.$message.success('语音内容分析完成，已自动填充相关信息')
      }, 1500)
    },
    
    editTranscription() {
      this.$prompt('编辑识别文本', '语音转文字结果', {
        inputValue: this.voiceTranscription.text,
        inputType: 'textarea'
      }).then(({ value }) => {
        this.voiceTranscription.text = value
        this.$message.success('文本已更新')
      }).catch(() => {
        this.$message.info('取消编辑')
      })
    },
    
    // 思维导图处理
    beforeImageUpload(file) {
      const isImage = file.type.indexOf('image/') === 0
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isImage) {
        this.$message.error('上传文件必须是图片格式!')
        return false
      }
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!')
        return false
      }
      return true
    },
    
    handleImageSuccess(response, file) {
      this.mindmapImage = URL.createObjectURL(file.raw)
      this.$message.success('图片上传成功，开始识别...')
      
      // 模拟OCR和AI分析
      setTimeout(() => {
        this.mindmapAnalysis = {
          elements: [
            { id: 1, text: '函数概念', type: 'central', confidence: 96 },
            { id: 2, text: '定义域', type: 'branch', confidence: 94 },
            { id: 3, text: '值域', type: 'branch', confidence: 92 },
            { id: 4, text: '函数图像', type: 'branch', confidence: 89 },
            { id: 5, text: '实际应用', type: 'branch', confidence: 87 },
            { id: 6, text: '一次函数', type: 'sub-branch', confidence: 85 },
            { id: 7, text: '二次函数', type: 'sub-branch', confidence: 83 }
          ]
        }
        this.$message.success('思维导图识别完成')
      }, 3000)
    },
    
    generateKnowledgeGraph() {
      if (!this.mindmapAnalysis) return
      
      // 根据识别结果生成知识图谱
      const elements = this.mindmapAnalysis.elements
      const centralElement = elements.find(e => e.type === 'central')
      
      if (centralElement) {
        // 创建主题并生成相应的知识图谱
        const topic = {
          id: 999,
          title: centralElement.text,
          description: '基于思维导图生成的课程主题',
          difficulty: '中等',
          duration: 2,
          knowledgePoints: elements.filter(e => e.type === 'branch').map(e => e.text)
        }
        
        this.setSelectedTopic(topic)
        this.$message.success('知识图谱已生成')
      }
    },
    
    editRecognitionResults() {
      this.$message.info('打开编辑器功能开发中...')
    },
    
    // 视频分析处理
    beforeVideoUpload(file) {
      const isVideo = file.type.indexOf('video/') === 0
      const isLt500M = file.size / 1024 / 1024 < 500

      if (!isVideo) {
        this.$message.error('上传文件必须是视频格式!')
        return false
      }
      if (!isLt500M) {
        this.$message.error('上传视频大小不能超过 500MB!')
        return false
      }
      
      this.uploadingVideo = true
      this.videoUploadProgress = 0
      return true
    },
    
    handleVideoProgress(event) {
      this.videoUploadProgress = Math.round((event.loaded / event.total) * 100)
    },
    
    handleVideoSuccess(response, file) {
      this.uploadingVideo = false
      this.uploadedVideo = file
      this.$message.success('视频上传成功')
    },
    
    startVideoAnalysis() {
      this.$message.info('开始分析视频内容...')
      
      // 模拟视频AI分析
      setTimeout(() => {
        this.videoAnalysis = {
          keyPoints: [
            '函数的定义和概念讲解',
            '通过实例展示函数关系',
            '函数图像的绘制方法',
            '实际问题中的函数应用',
            '学生互动和问答环节'
          ],
          methods: [
            '讲授法',
            '演示法',
            '问答法',
            '实例分析法',
            '图像教学法'
          ],
          suggestedGrade: '七年级',
          suggestedSubject: '数学',
          duration: '45分钟',
          difficulty: '中等'
        }
        this.$message.success('视频分析完成')
      }, 5000)
    },
    
    applyVideoAnalysis() {
      if (!this.videoAnalysis) return
      
      // 根据视频分析结果自动填充表单
      const subjectMap = {
        '数学': 'math',
        '语文': 'chinese',
        '英语': 'english',
        '物理': 'physics'
      }
      
      const gradeMap = {
        '九年级': 9, '七年级': 7, '八年级': 8,
        '一年级': 1, '二年级': 2, '三年级': 3,
        '四年级': 4, '五年级': 5, '六年级': 6
      }
      
      if (subjectMap[this.videoAnalysis.suggestedSubject]) {
        this.formData.subject = subjectMap[this.videoAnalysis.suggestedSubject]
        this.onSubjectChange()
      }
      
      if (gradeMap[this.videoAnalysis.suggestedGrade]) {
        this.formData.grade = gradeMap[this.videoAnalysis.suggestedGrade]
        this.onGradeChange()
      }
      
      this.$message.success('视频分析结果已应用到表单')
    },
    
    // 实例展示方法
    loadDocumentExample(exampleId) {
      this.showDocumentProcessing = true
      this.processingStep = 0
      
      // 模拟处理过程
      const processSteps = [1, 2, 3, 4]
      processSteps.forEach((step, index) => {
        setTimeout(() => {
          this.processingStep = step
          if (step === 4) {
            // 处理完成，显示结果
            setTimeout(() => {
              this.showDocumentProcessing = false
              this.documentAnalysis = this.getDocumentExampleResult(exampleId)
              this.$message.success('示例文档解析完成')
            }, 1000)
          }
        }, (index + 1) * 1500)
      })
    },
    
    getDocumentExampleResult(exampleId) {
      const examples = {
        1: [
          { id: 1, type: '学科识别', content: '数学', confidence: 98, details: ['二次函数', '抛物线', '图像性质'] },
          { id: 2, type: '年级推测', content: '九年级', confidence: 95, details: ['初中阶段', '高阶概念'] },
          { id: 3, type: '课程标准', content: '义务教育数学课程标准(2022版)', confidence: 96 },
          { id: 4, type: '教学要点', content: '二次函数图像、对称轴、顶点坐标、开口方向', confidence: 92, details: ['抛物线特征', '图像变换', '性质分析'] }
        ],
        2: [
          { id: 1, type: '学科识别', content: '英语', confidence: 97, details: ['阅读理解', '词汇学习', '语法应用'] },
          { id: 2, type: '年级推测', content: '五年级', confidence: 93, details: ['小学高年级', '综合能力'] },
          { id: 3, type: '课程标准', content: '义务教育英语课程标准(2022版)', confidence: 95 },
          { id: 4, type: '教学要点', content: '阅读策略、词汇积累、理解技巧、表达能力', confidence: 89, details: ['技能培养', '策略训练', '能力提升'] }
        ],
        3: [
          { id: 1, type: '学科识别', content: '物理', confidence: 96, details: ['力学基础', '实验操作', '概念理解'] },
          { id: 2, type: '年级推测', content: '八年级', confidence: 94, details: ['初中物理', '实验导向'] },
          { id: 3, type: '课程标准', content: '义务教育物理课程标准(2022版)', confidence: 97 },
          { id: 4, type: '教学要点', content: '力的概念、测量方法、实验技能、安全规范', confidence: 91, details: ['基础概念', '实验技能', '安全意识'] }
        ]
      }
      return examples[exampleId] || []
    },
    
    playVoiceExample(exampleId) {
      this.showVoiceProcessing = true
      this.currentTranscription = ''
      
      const examples = {
        1: {
          text: "我准备给九年级学生讲解二次函数的图像与性质，重点是抛物线的开口方向、对称轴和顶点坐标。通过图像变换让学生理解参数对抛物线形状和位置的影响，掌握二次函数的基本性质和实际应用。",
          analysis: { subject: '数学', grade: '九年级', topic: '二次函数图像', method: '图像教学' }
        },
        2: {
          text: "今天我要为五年级的学生设计一节英语阅读理解课，重点培养学生的阅读技巧和理解能力。通过精选的阅读材料，让学生学会运用略读、精读等不同的阅读策略。",
          analysis: { subject: '英语', grade: '五年级', topic: '阅读理解', method: '策略教学' }
        },
        3: {
          text: "这节课我们要通过实验来理解力的作用效果，让八年级学生亲手操作实验器材，观察现象，记录数据，从而深入理解物理概念。安全操作是实验课的重中之重。",
          analysis: { subject: '物理', grade: '八年级', topic: '力学实验', method: '实验教学' }
        }
      }
      
      const example = examples[exampleId]
      if (!example) return
      
      // 模拟实时转录
      let index = 0
      const interval = setInterval(() => {
        if (index < example.text.length) {
          this.currentTranscription += example.text[index]
          index++
        } else {
          clearInterval(interval)
          setTimeout(() => {
            this.showVoiceProcessing = false
            this.voiceTranscription = {
              text: example.text,
              analysis: example.analysis,
              confidence: 0.95
            }
            this.$message.success('语音示例分析完成')
          }, 1000)
        }
      }, 50)
    },
    
    loadMindmapExample(exampleId) {
      this.showOcrProcessing = true
      this.ocrStep = 0
      
      // 模拟OCR处理步骤
      const steps = [1, 2, 3, 4]
      steps.forEach((step, index) => {
        setTimeout(() => {
          this.ocrStep = step
          if (step === 4) {
            setTimeout(() => {
              this.showOcrProcessing = false
              this.mindmapAnalysis = this.getMindmapExampleResult(exampleId)
              this.$message.success('思维导图识别完成')
            }, 1000)
          }
        }, (index + 1) * 1200)
      })
    },
    
    getMindmapExampleResult(exampleId) {
      const examples = {
        1: {
          centralTopic: '二次函数',
          branches: ['开口方向', '对称轴', '顶点坐标', '图像性质'],
          elements: [
            { id: 1, text: '二次函数', type: 'central', confidence: 98 },
            { id: 2, text: '开口方向', type: 'branch', confidence: 96 },
            { id: 3, text: '对称轴', type: 'branch', confidence: 94 },
            { id: 4, text: '顶点坐标', type: 'branch', confidence: 92 },
            { id: 5, text: '图像性质', type: 'branch', confidence: 90 },
            { id: 6, text: 'y=ax²+bx+c', type: 'sub-branch', confidence: 88 },
            { id: 7, text: '抛物线', type: 'sub-branch', confidence: 86 }
          ]
        },
        2: {
          centralTopic: 'Reading Skills',
          branches: ['Skimming', 'Scanning', 'Inference', 'Summary'],
          elements: [
            { id: 1, text: 'Reading Skills', type: 'central', confidence: 97 },
            { id: 2, text: 'Skimming', type: 'branch', confidence: 95 },
            { id: 3, text: 'Scanning', type: 'branch', confidence: 93 },
            { id: 4, text: 'Inference', type: 'branch', confidence: 91 },
            { id: 5, text: 'Summary', type: 'branch', confidence: 89 },
            { id: 6, text: 'Key Words', type: 'sub-branch', confidence: 87 },
            { id: 7, text: 'Main Idea', type: 'sub-branch', confidence: 85 }
          ]
        },
        3: {
          centralTopic: '力学基础',
          branches: ['力的概念', '力的测量', '力的效果', '力的平衡'],
          elements: [
            { id: 1, text: '力学基础', type: 'central', confidence: 96 },
            { id: 2, text: '力的概念', type: 'branch', confidence: 94 },
            { id: 3, text: '力的测量', type: 'branch', confidence: 92 },
            { id: 4, text: '力的效果', type: 'branch', confidence: 90 },
            { id: 5, text: '力的平衡', type: 'branch', confidence: 88 },
            { id: 6, text: '弹簧测力计', type: 'sub-branch', confidence: 86 },
            { id: 7, text: '牛顿', type: 'sub-branch', confidence: 84 }
          ]
        }
      }
      return examples[exampleId] || null
    },
    
    loadVideoExample(exampleId) {
      this.showVideoProcessing = true
      this.videoProcessStep = 0
      
      // 模拟视频分析步骤
      const steps = [1, 2, 3, 4]
      steps.forEach((step, index) => {
        setTimeout(() => {
          this.videoProcessStep = step
          if (step === 4) {
            setTimeout(() => {
              this.showVideoProcessing = false
              this.videoAnalysis = this.getVideoExampleResult(exampleId)
              this.$message.success('视频分析完成')
            }, 1500)
          }
        }, (index + 1) * 2000)
      })
    },
    
    getVideoExampleResult(exampleId) {
      const examples = {
        1: {
          keyPoints: ['二次函数图像特征分析', '抛物线开口方向判断', '对称轴公式推导', '顶点坐标计算方法', '图像性质总结归纳'],
          methods: ['图像教学法', '对比分析法', '探究学习法', '变式训练法', '归纳总结法'],
          suggestedGrade: '九年级',
          suggestedSubject: '数学',
          duration: '45分30秒',
          difficulty: '中等',
          timeSegments: [
            { name: '复习导入', duration: '6:00', percentage: 13 },
            { name: '图像探究', duration: '22:00', percentage: 48 },
            { name: '性质归纳', duration: '12:00', percentage: 26 },
            { name: '练习总结', duration: '5:30', percentage: 13 }
          ]
        },
        2: {
          keyPoints: ['阅读策略介绍', '词汇预习和讲解', '文本理解指导', '阅读技巧训练', '理解检测与反馈'],
          methods: ['情境教学', '小组合作', '游戏化学习', '任务驱动', '互动讨论'],
          suggestedGrade: '五年级',
          suggestedSubject: '英语',
          duration: '38分20秒',
          difficulty: '中等',
          timeSegments: [
            { name: '热身活动', duration: '5:00', percentage: 13 },
            { name: '词汇学习', duration: '8:20', percentage: 22 },
            { name: '阅读训练', duration: '18:00', percentage: 47 },
            { name: '总结评价', duration: '7:00', percentage: 18 }
          ]
        },
        3: {
          keyPoints: ['实验安全规范讲解', '实验器材介绍使用', '力的测量实验操作', '数据记录和分析', '实验结论总结讨论'],
          methods: ['实验教学', '观察法', '探究学习', '小组实验', '讨论总结'],
          suggestedGrade: '八年级',
          suggestedSubject: '物理',
          duration: '45分30秒',
          difficulty: '中等',
          timeSegments: [
            { name: '安全讲解', duration: '5:30', percentage: 12 },
            { name: '器材介绍', duration: '8:00', percentage: 18 },
            { name: '实验操作', duration: '25:00', percentage: 55 },
            { name: '总结讨论', duration: '7:00', percentage: 15 }
          ]
        }
      }
      return examples[exampleId] || null
    },
    
    getWaveHeight(index) {
      // 生成动态波形高度
      const base = 10
      const variation = Math.sin((Date.now() / 100 + index * 0.5)) * 15
      return Math.max(5, base + variation)
    },

    onSubjectChange() {
      this.formData.standard = ''
      this.refreshRecommendations()
    },
    
    onGradeChange() {
      this.refreshRecommendations()
    },
    
    async refreshRecommendations() {
      if (!this.formData.subject || !this.formData.grade) return
      
      this.loadingRecommendations = true
      
      // 模拟API调用
      setTimeout(() => {
        this.suggestedTopics = this.generateMockTopics()
        this.loadingRecommendations = false
      }, 1500)
    },
    
    generateMockTopics() {
      const mockTopics = [
        {
          id: 1,
          title: '函数的概念与性质',
          description: '通过实际问题引入函数概念，理解函数的定义域、值域等基本性质',
          difficulty: '中等',
          duration: 2,
          knowledgePoints: ['函数定义', '定义域', '值域', '函数图像']
        },
        {
          id: 2,
          title: '一次函数及其应用',
          description: '学习一次函数的图像特征，掌握用函数解决实际问题的方法',
          difficulty: '简单',
          duration: 3,
          knowledgePoints: ['一次函数', '直线方程', '实际应用']
        },
        {
          id: 3,
          title: '二次函数的图像与性质',
          description: '深入理解二次函数的图像特征，掌握配方法等关键技能',
          difficulty: '困难',
          duration: 4,
          knowledgePoints: ['二次函数', '抛物线', '配方法', '顶点坐标']
        }
      ]
      
      return mockTopics
    },
    
    setSelectedTopic(topic) {
      this.selectedTopic = topic
      this.generateKnowledgeGraph(topic)
    },
    
    generateKnowledgeGraph(topic) {
      // 根据不同主题生成不同的知识图谱数据
      if (topic.title === '函数的概念与性质') {
        this.knowledgeNodes = [
          { id: 'algebra', name: '代数基础', type: 'prerequisite', category: 0, symbolSize: 40 },
          { id: 'equation', name: '方程与不等式', type: 'prerequisite', category: 0, symbolSize: 35 },
          { id: 'coordinate', name: '坐标系', type: 'prerequisite', category: 0, symbolSize: 30 },
          { id: 'function', name: '函数的概念与性质', type: 'current', category: 1, symbolSize: 60 },
          { id: 'domain', name: '定义域', type: 'related', category: 1, symbolSize: 25 },
          { id: 'range', name: '值域', type: 'related', category: 1, symbolSize: 25 },
          { id: 'monotonicity', name: '单调性', type: 'related', category: 1, symbolSize: 25 },
          { id: 'extremum', name: '最值', type: 'related', category: 1, symbolSize: 25 },
          { id: 'linear', name: '一次函数', type: 'extended', category: 2, symbolSize: 45 },
          { id: 'quadratic', name: '二次函数', type: 'extended', category: 2, symbolSize: 45 },
          { id: 'inverse', name: '反比例函数', type: 'extended', category: 2, symbolSize: 40 },
          { id: 'composite', name: '复合函数', type: 'extended', category: 2, symbolSize: 35 }
        ]
        
        this.knowledgeLinks = [
          { source: 'algebra', target: 'function', relation: '基础' },
          { source: 'equation', target: 'function', relation: '基础' },
          { source: 'coordinate', target: 'function', relation: '基础' },
          { source: 'function', target: 'domain', relation: '包含' },
          { source: 'function', target: 'range', relation: '包含' },
          { source: 'function', target: 'monotonicity', relation: '性质' },
          { source: 'function', target: 'extremum', relation: '性质' },
          { source: 'function', target: 'linear', relation: '具体化' },
          { source: 'function', target: 'quadratic', relation: '具体化' },
          { source: 'function', target: 'inverse', relation: '具体化' },
          { source: 'domain', target: 'linear', relation: '应用' },
          { source: 'range', target: 'quadratic', relation: '应用' },
          { source: 'linear', target: 'composite', relation: '组合' },
          { source: 'quadratic', target: 'composite', relation: '组合' }
        ]
      } else if (topic.title === '一次函数及其应用') {
        this.knowledgeNodes = [
          { id: 'function_basic', name: '函数基础', type: 'prerequisite', category: 0, symbolSize: 40 },
          { id: 'coordinate_sys', name: '直角坐标系', type: 'prerequisite', category: 0, symbolSize: 35 },
          { id: 'linear_func', name: '一次函数', type: 'current', category: 1, symbolSize: 60 },
          { id: 'slope', name: '斜率', type: 'related', category: 1, symbolSize: 30 },
          { id: 'intercept', name: '截距', type: 'related', category: 1, symbolSize: 30 },
          { id: 'graph', name: '函数图像', type: 'related', category: 1, symbolSize: 35 },
          { id: 'application', name: '实际应用', type: 'extended', category: 2, symbolSize: 45 },
          { id: 'system', name: '方程组', type: 'extended', category: 2, symbolSize: 40 }
        ]
        
        this.knowledgeLinks = [
          { source: 'function_basic', target: 'linear_func', relation: '基础' },
          { source: 'coordinate_sys', target: 'linear_func', relation: '工具' },
          { source: 'linear_func', target: 'slope', relation: '性质' },
          { source: 'linear_func', target: 'intercept', relation: '性质' },
          { source: 'linear_func', target: 'graph', relation: '表示' },
          { source: 'slope', target: 'graph', relation: '决定' },
          { source: 'intercept', target: 'graph', relation: '决定' },
          { source: 'linear_func', target: 'application', relation: '应用' },
          { source: 'linear_func', target: 'system', relation: '扩展' }
        ]
      } else {
        // 默认图谱
        this.knowledgeNodes = [
          { id: 'basic', name: '基础知识', type: 'prerequisite', category: 0, symbolSize: 35 },
          { id: 'current', name: topic.title, type: 'current', category: 1, symbolSize: 60 },
          { id: 'advanced', name: '进阶内容', type: 'extended', category: 2, symbolSize: 40 }
        ]
        
        this.knowledgeLinks = [
          { source: 'basic', target: 'current', relation: '基础' },
          { source: 'current', target: 'advanced', relation: '发展' }
        ]
      }
      
      this.$nextTick(() => {
        this.renderGraph()
      })
    },
    
    renderGraph() {
      if (!this.$refs.knowledgeGraph) return
      
      // 使用SVG渲染知识图谱，确保兼容性
      this.renderSVGGraph()
    },
    
    saveAndNext() {
      if (!this.isFormValid) {
        this.$message.warning('请完成所有必填项')
        return
      }
      
      console.log('保存并跳转 - 开始执行')
      
      // 保存数据到状态管理或发送到服务器
      this.$message.success('课程定位信息已保存')
      
      console.log('触发步骤完成事件:', 1)
      // 通知父组件步骤完成
      this.$emit('step-completed', 1)
      
      console.log('准备跳转到:', '/teaching-design/goal-generation')
      console.log('当前路由:', this.$route.path)
      console.log('路由器对象:', this.$router)
      
      // 跳转到下一步
      this.$router.push('/teaching-design/goal-generation').then(() => {
        console.log('路由跳转成功')
      }).catch(err => {
        console.error('路由跳转失败:', err)
      })
    },
    
    saveDraft() {
      this.$message.info('草稿已保存')
    },
    
    renderSVGGraph() {
      const container = this.$refs.knowledgeGraph
      const width = 800
      const height = 400
      
      // 计算节点位置
      const positions = this.calculateNodePositions(width, height)
      
      // 构建SVG
      let svg = `
        <svg width="100%" height="100%" viewBox="0 0 ${width} ${height}" style="background: transparent;">
          <defs>
            <marker id="arrowhead" markerWidth="10" markerHeight="7" 
                    refX="9" refY="3.5" orient="auto">
              <polygon points="0 0, 10 3.5, 0 7" fill="#666" />
            </marker>
            <filter id="drop-shadow">
              <feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.3"/>
            </filter>
          </defs>
      `
      
      // 绘制连接线
      this.knowledgeLinks.forEach(link => {
        const sourcePos = positions[link.source]
        const targetPos = positions[link.target]
        
        if (sourcePos && targetPos) {
          const midX = (sourcePos.x + targetPos.x) / 2
          const midY = (sourcePos.y + targetPos.y) / 2
          
          svg += `
            <line x1="${sourcePos.x}" y1="${sourcePos.y}" 
                  x2="${targetPos.x}" y2="${targetPos.y}" 
                  stroke="#ddd" stroke-width="2" marker-end="url(#arrowhead)" />
            <text x="${midX}" y="${midY - 5}" fill="#999" font-size="10" text-anchor="middle">
              ${link.relation}
            </text>
          `
        }
      })
      
      // 绘制节点
      this.knowledgeNodes.forEach(node => {
        const pos = positions[node.id]
        if (!pos) return
        
        const radius = (node.symbolSize || 30) / 2
        const colors = {
          prerequisite: '#e6a23c',
          current: '#409EFF',
          related: '#909399', 
          extended: '#67c23a'
        }
        const color = colors[node.type] || '#ccc'
        const strokeWidth = node.type === 'current' ? 3 : 2
        const strokeColor = node.type === 'current' ? '#409EFF' : '#fff'
        
        svg += `
          <circle cx="${pos.x}" cy="${pos.y}" r="${radius}" 
                  fill="${color}" stroke="${strokeColor}" stroke-width="${strokeWidth}"
                  filter="url(#drop-shadow)" style="cursor: pointer;">
            <title>${node.name}</title>
          </circle>
          <text x="${pos.x}" y="${pos.y + 4}" fill="white" font-size="11" 
                text-anchor="middle" font-weight="${node.type === 'current' ? 'bold' : 'normal'}">
            ${this.wrapText(node.name, radius)}
          </text>
        `
      })
      
      svg += '</svg>'
      container.innerHTML = svg
      
      // 添加点击事件
      container.addEventListener('click', (e) => {
        if (e.target.tagName === 'circle') {
          const nodeId = Object.keys(positions).find(id => {
            const pos = positions[id]
            const rect = container.getBoundingClientRect()
            const x = e.clientX - rect.left
            const y = e.clientY - rect.top
            const scale = width / rect.width
            const distance = Math.sqrt((x * scale - pos.x) ** 2 + (y * scale - pos.y) ** 2)
            return distance <= 30
          })
          
          if (nodeId) {
            const node = this.knowledgeNodes.find(n => n.id === nodeId)
            if (node) {
              this.$message.info(`点击了知识点: ${node.name}`)
            }
          }
        }
      })
    },
    
    calculateNodePositions(width, height) {
      const positions = {}
      
      if (this.currentLayout === 'force') {
        // 力导向布局的简化版本
        if (this.knowledgeNodes.length <= 3) {
          // 简单线性布局
          this.knowledgeNodes.forEach((node, index) => {
            positions[node.id] = {
              x: (width / (this.knowledgeNodes.length + 1)) * (index + 1),
              y: height / 2
            }
          })
        } else {
          // 分层布局
          const layers = { prerequisite: [], current: [], related: [], extended: [] }
          this.knowledgeNodes.forEach(node => {
            layers[node.type].push(node)
          })
          
          let currentY = 80
          Object.keys(layers).forEach(type => {
            const nodes = layers[type]
            if (nodes.length > 0) {
              nodes.forEach((node, index) => {
                positions[node.id] = {
                  x: (width / (nodes.length + 1)) * (index + 1),
                  y: currentY
                }
              })
              currentY += 100
            }
          })
        }
      } else {
        // 圆形布局
        const centerX = width / 2
        const centerY = height / 2
        const radius = Math.min(width, height) / 3
        
        this.knowledgeNodes.forEach((node, index) => {
          const angle = (index / this.knowledgeNodes.length) * Math.PI * 2
          positions[node.id] = {
            x: centerX + Math.cos(angle) * radius,
            y: centerY + Math.sin(angle) * radius
          }
        })
      }
      
      return positions
    },
    
    wrapText(text, maxRadius) {
      if (text.length <= 4) return text
      const half = Math.ceil(text.length / 2)
      return text.substring(0, half)
    },
    
    resetGraphView() {
      this.renderGraph()
    },
    
    toggleGraphLayout() {
      this.currentLayout = this.currentLayout === 'force' ? 'circular' : 'force'
      this.renderGraph()
    },
    
    // 新增的辅助方法
    getSubjectLabel(value) {
      const subject = this.subjects.find(s => s.value === value)
      return subject ? subject.label : ''
    },
    
    getGradeLabel(value) {
      for (let group of this.gradeGroups) {
        const grade = group.options.find(g => g.value === value)
        if (grade) return grade.label
      }
      return ''
    },
    
    showGuide() {
      this.$message.info('配置指南功能开发中...')
    },
    
    resetForm() {
      this.$confirm('确定要重置所有配置吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.formData = {
          subject: '',
          grade: '',
          standard: '',
          courseType: 'regular',
          estimatedHours: 2,
          difficulty: 3
        }
        this.selectedTopic = null
        this.suggestedTopics = []
        this.knowledgeNodes = []
        this.knowledgeLinks = []
        this.$message.success('配置已重置')
      }).catch(() => {
        this.$message.info('已取消重置')
      })
    }
  },
  
  mounted() {
    // 从localStorage恢复上次的选择
    const savedData = localStorage.getItem('coursePositioning')
    if (savedData) {
      this.formData = { ...this.formData, ...JSON.parse(savedData) }
      if (this.formData.subject && this.formData.grade) {
        this.refreshRecommendations()
      }
    }
  },
  
  beforeDestroy() {
    // 保存当前状态
    localStorage.setItem('coursePositioning', JSON.stringify(this.formData))
  }
}
</script>

<style scoped>
.course-positioning {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
  box-sizing: border-box;
}

.page-header {
  margin-bottom: 40px;
  text-align: center;
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

.page-header h2 {
  color: white;
  margin-bottom: 12px;
  font-size: 28px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.page-header p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  line-height: 1.5;
  position: relative;
  z-index: 1;
}

.form-section {
  margin-bottom: 32px;
}

.form-item {
  margin-bottom: 24px;
  position: relative;
}

.form-item h3 {
  margin-bottom: 12px;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-item h3::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.form-item .el-select {
  width: 100%;
  transition: all 0.3s ease;
}

.form-item .el-select:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.ai-assistant {
  margin-bottom: 32px;
}

.ai-recommendation-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  overflow: hidden;
}

.ai-recommendation-card::before {
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
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
  padding: 16px 20px;
  border-bottom: 1px solid rgba(64, 158, 255, 0.1);
  position: relative;
}

.card-header i {
  margin-right: 8px;
  color: #667eea;
  font-size: 18px;
}

.card-header span {
  font-weight: 600;
  color: #2c3e50;
  font-size: 16px;
}

.topic-suggestions {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-top: 24px;
  padding: 0 4px;
}

.topic-suggestion-card {
  border: 2px solid rgba(64, 158, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  background: linear-gradient(135deg, #ffffff 0%, #f8fafe 100%);
  position: relative;
  overflow: hidden;
}

.topic-suggestion-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.topic-suggestion-card:hover {
  border-color: #667eea;
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
  transform: translateY(-4px) scale(1.02);
}

.topic-suggestion-card:hover::before {
  opacity: 1;
}

.topic-suggestion-card.selected {
  border-color: #667eea;
  background: linear-gradient(135deg, #f0f4ff 0%, #e6efff 100%);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.25);
}

.topic-suggestion-card.selected::after {
  content: '✓';
  position: absolute;
  top: 12px;
  right: 12px;
  width: 24px;
  height: 24px;
  background: #667eea;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
}

.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  position: relative;
  z-index: 1;
}

.topic-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.topic-description {
  color: #5a6c7d;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
}

.topic-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #7c8db5;
  position: relative;
  z-index: 1;
}

.topic-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  background: rgba(102, 126, 234, 0.08);
  border-radius: 6px;
  font-weight: 500;
}

.topic-meta i {
  font-size: 14px;
  color: #667eea;
}

.knowledge-map {
  margin-bottom: 32px;
}

.knowledge-map .el-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  overflow: hidden;
}

.knowledge-map .el-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
}

.knowledge-graph-container {
  position: relative;
  background: rgba(248, 250, 255, 0.8);
  border-radius: 12px;
  overflow: hidden;
}

.knowledge-graph {
  height: 520px;
  border: 2px solid rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafe 100%);
  position: relative;
  overflow: hidden;
  box-shadow: inset 0 4px 16px rgba(102, 126, 234, 0.08);
}

.knowledge-graph::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 50%, rgba(102, 126, 234, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(118, 75, 162, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 40% 80%, rgba(102, 126, 234, 0.02) 0%, transparent 50%);
  pointer-events: none;
}

.knowledge-graph.three-d {
  background: linear-gradient(135deg, #1e2139 0%, #2a2d5a 100%);
}

.graph-legend {
  display: flex;
  gap: 24px;
  margin-top: 16px;
  justify-content: center;
  padding: 16px;
  background: rgba(248, 250, 255, 0.6);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #5a6c7d;
  font-weight: 500;
  padding: 8px 12px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.legend-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.legend-color {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.legend-color.prerequisite {
  background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
}

.legend-color.current {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.legend-color.extended {
  background: linear-gradient(135deg, #2ecc71 0%, #27ae60 100%);
}

.legend-color.related {
  background: linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%);
}

.graph-controls {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 10;
  background: rgba(255, 255, 255, 0.95);
  padding: 12px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.graph-controls .el-button-group .el-button {
  border-color: rgba(102, 126, 234, 0.2);
  transition: all 0.3s ease;
}

.graph-controls .el-button-group .el-button:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
  transform: translateY(-1px);
}

.empty-state,
.empty-graph {
  padding: 60px 40px;
  text-align: center;
  background: linear-gradient(135deg, #f8fafe 0%, #ffffff 100%);
  border-radius: 12px;
  margin: 20px;
}

.empty-state .el-empty,
.empty-graph .el-empty {
  filter: grayscale(0.3);
}

.loading-state {
  padding: 40px 20px;
}

.loading-state .el-skeleton {
  background: linear-gradient(135deg, #f8fafe 0%, #ffffff 100%);
  border-radius: 8px;
  padding: 20px;
  margin: 10px 0;
}

.action-buttons {
  text-align: center;
  margin-top: 48px;
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

/* 多媒体输入区域样式 */
.multimedia-input-section {
  margin-bottom: 32px;
}

.multimedia-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  overflow: hidden;
}

.multimedia-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 50%, #667eea 100%);
}

.input-panel {
  padding: 24px;
  min-height: 300px;
}

.panel-header {
  text-align: center;
  margin-bottom: 24px;
}

.panel-header i {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 12px;
  display: block;
}

.panel-header h4 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

.panel-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

/* 文档上传样式 */
.document-uploader {
  margin-bottom: 24px;
}

.analysis-results {
  margin-top: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.analysis-results h5 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.analysis-items {
  display: grid;
  gap: 12px;
}

.analysis-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.analysis-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.item-type {
  font-weight: 600;
  color: #667eea;
  font-size: 14px;
}

.confidence {
  font-size: 12px;
  color: #909399;
}

.item-content {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 14px;
}

/* 语音录入样式 */
.voice-recorder {
  text-align: center;
}

.recorder-controls {
  margin-bottom: 24px;
}

.recording-indicator {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  margin-left: 16px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
  border-radius: 20px;
  font-weight: 500;
}

.recording-wave {
  display: flex;
  gap: 3px;
}

.recording-wave span {
  width: 3px;
  height: 12px;
  background: white;
  border-radius: 2px;
  animation: wave 1.5s ease-in-out infinite;
}

.recording-wave span:nth-child(2) { animation-delay: 0.1s; }
.recording-wave span:nth-child(3) { animation-delay: 0.2s; }
.recording-wave span:nth-child(4) { animation-delay: 0.3s; }

@keyframes wave {
  0%, 100% { transform: scaleY(1); }
  50% { transform: scaleY(2); }
}

.voice-history {
  margin-top: 24px;
  text-align: left;
}

.voice-history h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.recording-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recording-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.recording-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.recording-name {
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
}

.recording-duration {
  font-size: 12px;
  color: #909399;
}

.recording-actions {
  display: flex;
  gap: 8px;
}

.transcription-result {
  margin-top: 24px;
  text-align: left;
}

.transcription-result h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.transcription-content {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
}

.transcription-content p {
  margin: 0 0 12px 0;
  color: #2c3e50;
  line-height: 1.6;
}

.transcription-actions {
  display: flex;
  gap: 8px;
}

/* 思维导图样式 */
.image-uploader {
  margin-bottom: 24px;
}

.uploaded-image {
  width: 100%;
  max-width: 400px;
  max-height: 300px;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px;
  color: #909399;
}

.upload-placeholder i {
  font-size: 48px;
}

.upload-text {
  font-size: 16px;
  font-weight: 500;
}

.mindmap-analysis {
  margin-top: 24px;
  text-align: left;
}

.mindmap-analysis h5 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.recognized-elements {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.element-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 12px;
  border: 1px solid;
}

.element-item.central {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
  font-weight: 600;
}

.element-item.branch {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  color: white;
  border-color: #67C23A;
}

.element-item.sub-branch {
  background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);
  color: white;
  border-color: #E6A23C;
}

.element-confidence {
  opacity: 0.8;
  font-size: 10px;
}

.analysis-actions {
  display: flex;
  gap: 8px;
}

/* 视频上传样式 */
.video-uploader {
  margin-bottom: 24px;
}

.uploading-state {
  padding: 40px;
  text-align: center;
}

.uploading-state p {
  margin: 16px 0 0 0;
  color: #606266;
}

.video-preview {
  padding: 40px;
  text-align: center;
  color: #606266;
}

.video-preview i {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 12px;
  display: block;
}

.video-preview p {
  margin: 0 0 16px 0;
  font-weight: 500;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.video-analysis {
  margin-top: 24px;
  text-align: left;
}

.video-analysis h5 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.analysis-sections {
  display: grid;
  gap: 16px;
  margin-bottom: 20px;
}

.analysis-section {
  background: white;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.analysis-section h6 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
}

.analysis-section ul {
  margin: 0;
  padding-left: 20px;
}

.analysis-section li {
  margin-bottom: 4px;
  color: #2c3e50;
  font-size: 14px;
}

.analysis-section p {
  margin: 0;
  color: #2c3e50;
  font-size: 14px;
}

/* 实例展示样式 */
.feature-demo {
  margin-bottom: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #f0f4ff 0%, #e6efff 100%);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.15);
}

.feature-demo h5 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
  display: flex;
  align-items: center;
  gap: 8px;
}

.feature-demo h5 i {
  color: #f39c12;
}

/* 文档示例样式 */
.demo-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.demo-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.demo-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.demo-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.demo-preview i {
  font-size: 24px;
  color: #667eea;
}

.demo-preview span {
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
}

.demo-result {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.result-tag {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.result-tag.subject {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.result-tag.grade {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  color: white;
}

.result-tag.standard {
  background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%);
  color: white;
}

/* 处理过程演示样式 */
.processing-demo {
  margin-top: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #fafbff 0%, #f0f4ff 100%);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.processing-demo h5 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
  display: flex;
  align-items: center;
  gap: 8px;
}

.processing-steps {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  opacity: 0.5;
  transition: all 0.3s ease;
}

.step-item.active {
  opacity: 1;
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.step-item.completed {
  border-color: #67C23A;
  background: linear-gradient(135deg, #f0f9ff 0%, #ecfdf5 100%);
}

.step-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #e2e8f0;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.step-item.active .step-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.step-item.completed .step-icon {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
}

.step-content h6 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.step-content p {
  margin: 0;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

/* 语音示例样式 */
.voice-examples {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.voice-example {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.voice-example:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.example-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.example-header i {
  color: #667eea;
  margin-right: 8px;
}

.example-header span:first-of-type {
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
}

.duration {
  font-size: 12px;
  color: #909399;
  background: rgba(102, 126, 234, 0.1);
  padding: 2px 6px;
  border-radius: 8px;
}

.example-text {
  margin: 8px 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.example-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

/* 语音处理演示样式 */
.voice-processing-demo {
  margin-top: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #fafbff 0%, #f0f4ff 100%);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.voice-processing-demo h5 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
  display: flex;
  align-items: center;
  gap: 8px;
}

.processing-visual {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
}

.wave-form {
  display: flex;
  align-items: end;
  justify-content: center;
  gap: 2px;
  height: 60px;
  margin-bottom: 16px;
}

.wave-bar {
  width: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  transition: height 0.3s ease;
  animation: wave 1.5s ease-in-out infinite;
}

.wave-bar:nth-child(odd) {
  animation-delay: 0.1s;
}

.processing-text {
  min-height: 60px;
  padding: 12px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 8px;
}

.processing-text p {
  margin: 0;
  font-size: 14px;
  color: #2c3e50;
  line-height: 1.6;
}

.transcription-analysis {
  margin-top: 16px;
  padding: 12px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 8px;
}

.transcription-analysis h6 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
}

.analysis-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-item {
  padding: 4px 8px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 12px;
  color: #2c3e50;
}

.tag-item.subject { border-color: #667eea; color: #667eea; }
.tag-item.grade { border-color: #67C23A; color: #67C23A; }
.tag-item.topic { border-color: #E6A23C; color: #E6A23C; }
.tag-item.method { border-color: #F56C6C; color: #F56C6C; }

/* 思维导图示例样式 */
.mindmap-examples {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.mindmap-example {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.mindmap-example:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.example-image {
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  position: relative;
}

.mock-mindmap {
  width: 100%;
  height: 100%;
  position: relative;
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
}

.central-node {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 8px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  min-width: 80px;
}

.branch-node {
  position: absolute;
  background: white;
  border: 2px solid #667eea;
  color: #667eea;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 500;
  text-align: center;
}

.branch-node.top-left { top: 15%; left: 15%; }
.branch-node.top-right { top: 15%; right: 15%; }
.branch-node.bottom-left { bottom: 15%; left: 15%; }
.branch-node.bottom-right { bottom: 15%; right: 15%; }

.mock-mindmap.math .central-node { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.mock-mindmap.english .central-node { background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%); }
.mock-mindmap.physics .central-node { background: linear-gradient(135deg, #E6A23C 0%, #ebb563 100%); }

.mock-mindmap.math .branch-node { border-color: #667eea; color: #667eea; }
.mock-mindmap.english .branch-node { border-color: #67C23A; color: #67C23A; }
.mock-mindmap.physics .branch-node { border-color: #E6A23C; color: #E6A23C; }

.example-info h6 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.example-info p {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #606266;
}

.confidence-score {
  font-size: 12px;
  color: #67C23A;
  font-weight: 500;
}

/* OCR处理演示样式 */
.ocr-processing-demo {
  margin-top: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #fafbff 0%, #f0f4ff 100%);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.ocr-processing-demo h5 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ocr-steps {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.ocr-step {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 8px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  opacity: 0.5;
  transition: all 0.3s ease;
}

.ocr-step.active {
  opacity: 1;
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.ocr-step i {
  font-size: 24px;
  color: #667eea;
}

.ocr-step span {
  font-size: 12px;
  color: #2c3e50;
  font-weight: 500;
  text-align: center;
}

/* 结构分析样式 */
.structure-analysis {
  margin-top: 16px;
  padding: 16px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 8px;
}

.structure-analysis h6 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
}

.relationship-tree {
  display: flex;
  justify-content: center;
}

.tree-node.central {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 16px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  position: relative;
}

.tree-branches {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.tree-branch {
  background: white;
  border: 2px solid #667eea;
  color: #667eea;
  padding: 6px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

/* 视频示例样式 */
.video-examples {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.video-example {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.video-example:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

.video-thumbnail {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding: 12px;
  background: linear-gradient(135deg, #f8f9ff 0%, #eef1ff 100%);
  border-radius: 8px;
}

.video-thumbnail i {
  font-size: 32px;
  color: #667eea;
}

.video-info {
  flex: 1;
}

.video-title {
  display: block;
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
  margin-bottom: 4px;
}

.video-duration {
  font-size: 12px;
  color: #909399;
  background: rgba(102, 126, 234, 0.1);
  padding: 2px 6px;
  border-radius: 8px;
}

.analysis-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.key-points {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.point-tag {
  padding: 4px 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.teaching-methods {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.method-tag {
  padding: 4px 8px;
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  color: white;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

/* 视频处理演示样式 */
.video-processing-demo {
  margin-top: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #fafbff 0%, #f0f4ff 100%);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.video-processing-demo h5 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #667eea;
  display: flex;
  align-items: center;
  gap: 8px;
}

.processing-timeline {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  opacity: 0.5;
  transition: all 0.3s ease;
}

.timeline-item.active {
  opacity: 1;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #e2e8f0;
  margin-top: 6px;
  flex-shrink: 0;
}

.timeline-item.active .timeline-dot {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.timeline-content {
  flex: 1;
  background: white;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.timeline-item.active .timeline-content {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.timeline-content h6 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

.timeline-content p {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #606266;
}

.progress-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.progress-detail span {
  font-size: 12px;
  color: #909399;
  background: rgba(102, 126, 234, 0.1);
  padding: 2px 6px;
  border-radius: 8px;
  display: inline-block;
  width: fit-content;
}

/* 时间分布样式 */
.time-distribution {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.time-segment {
  display: flex;
  align-items: center;
  gap: 12px;
}

.segment-name {
  font-size: 12px;
  color: #2c3e50;
  font-weight: 500;
  min-width: 80px;
}

.segment-bar {
  flex: 1;
  height: 8px;
  background: #f0f2f5;
  border-radius: 4px;
  overflow: hidden;
}

.segment-fill {
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.segment-time {
  font-size: 12px;
  color: #909399;
  min-width: 40px;
  text-align: right;
}

/* 详细信息展示 */
.item-details {
  margin: 8px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.demo-details {
  margin-top: 8px;
  padding: 8px 0;
  border-top: 1px solid #f0f2f5;
}

.demo-details small {
  color: #909399;
  font-size: 11px;
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-positioning {
    padding: 16px;
  }
  
  .input-panel {
    padding: 16px;
    min-height: auto;
  }
  
  .panel-header i {
    font-size: 36px;
  }
  
  .panel-header h4 {
    font-size: 18px;
  }
  
  .analysis-items {
    grid-template-columns: 1fr;
  }
  
  .recognized-elements {
    justify-content: center;
  }
  
  .analysis-sections {
    grid-template-columns: 1fr;
  }
  
  .page-header {
    padding: 24px 16px;
    margin-bottom: 24px;
  }
  
  .page-header h2 {
    font-size: 24px;
  }
  
  .topic-suggestions {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .knowledge-graph {
    height: 400px;
  }
  
  .graph-legend {
    flex-wrap: wrap;
    gap: 12px;
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
  .graph-controls {
    position: static;
    margin-bottom: 16px;
    text-align: center;
  }
  
  .legend-item {
    padding: 6px 8px;
    font-size: 12px;
  }
}
</style> 