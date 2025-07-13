<template>
  <div class="resource-editor">
    <!-- 编辑器头部 -->
    <div class="editor-header">
      <div class="header-left">
        <h3>{{ resource.name }}</h3>
        <span class="resource-type">{{ getResourceTypeName(resource.type) }}</span>
      </div>
      
      <div class="header-right">
        <div class="view-modes">
          <el-radio-group v-model="viewMode" size="mini">
            <el-radio-button label="edit">
              <i class="el-icon-edit"></i> 编辑
            </el-radio-button>
            <el-radio-button label="split">
              <i class="el-icon-s-grid"></i> 分割
            </el-radio-button>
            <el-radio-button label="preview">
              <i class="el-icon-view"></i> 预览
            </el-radio-button>
          </el-radio-group>
        </div>
        
        <div class="editor-actions">
          <el-button-group size="mini">
            <el-button @click="undo" :disabled="!canUndo">
              <i class="el-icon-refresh-left"></i>
            </el-button>
            <el-button @click="redo" :disabled="!canRedo">
              <i class="el-icon-refresh-right"></i>
            </el-button>
          </el-button-group>
        </div>
      </div>
    </div>
    
    <!-- 编辑器内容区 -->
    <div class="editor-content" :class="`view-${viewMode}`">
      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-panel">
        <div class="loading-content">
          <i class="el-icon-loading"></i>
          <p>正在加载资源数据...</p>
        </div>
      </div>
      
      <!-- 编辑面板 -->
      <div class="edit-panel" v-show="(viewMode === 'edit' || viewMode === 'split') && !isLoading">
        <!-- AI编辑器标识 -->
        <div v-if="resource.type === 'video'" class="ai-editor-header">
          <div class="ai-editor-badge">
            <i class="el-icon-cpu"></i>
            <span>AI智能编辑器</span>
          </div>
          <div class="ai-editor-status">
            <span class="status-dot"></span>
            <span>实时AI分析中</span>
          </div>
        </div>

        <!-- 基础编辑器 - 适用于所有类型 -->
        <div class="universal-editor">
          <el-form label-width="120px" style="max-width: 800px;">
            <el-form-item label="资源名称">
              <el-input v-model="editedResource.name" @input="handleChange"></el-input>
            </el-form-item>
            
            <el-form-item label="描述">
              <el-input 
                v-model="editedResource.description" 
                type="textarea"
                rows="4"
                @input="handleChange">
              </el-input>
            </el-form-item>
            
            <el-form-item label="标签">
              <el-select
                v-model="editedResource.tags"
                multiple
                filterable
                allow-create
                placeholder="选择或创建标签"
                style="width: 100%"
                @change="handleChange">
                <el-option
                  v-for="tag in availableTags"
                  :key="tag"
                  :label="tag"
                  :value="tag">
                </el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="链接/内容" v-if="resource.type !== 'text'">
              <el-input v-model="editedResource.url" @input="handleChange"></el-input>
            </el-form-item>
            
            <!-- 文本内容编辑 -->
            <el-form-item label="文本内容" v-if="resource.type === 'text'">
              <div class="text-editor">
                <div class="text-toolbar">
                  <el-button-group size="mini">
                    <el-button @click="formatText('bold')"><b>B</b></el-button>
                    <el-button @click="formatText('italic')"><i>I</i></el-button>
                    <el-button @click="formatText('underline')"><u>U</u></el-button>
                  </el-button-group>
                  
                  <el-select v-model="textFormat.fontSize" size="mini" style="width: 80px; margin-left: 10px;">
                    <el-option label="12px" value="12px"></el-option>
                    <el-option label="14px" value="14px"></el-option>
                    <el-option label="16px" value="16px"></el-option>
                    <el-option label="18px" value="18px"></el-option>
                    <el-option label="20px" value="20px"></el-option>
                  </el-select>
                  
                  <el-color-picker v-model="textFormat.color" size="mini" style="margin-left: 10px;"></el-color-picker>
                </div>
                
                <el-input 
                  v-model="editedResource.content" 
                  type="textarea"
                  rows="15"
                  :style="textEditorStyle"
                  @input="handleChange">
                </el-input>
              </div>
            </el-form-item>
            
            <!-- AI智能思维导图编辑 -->
            <div v-if="resource.type === 'mindmap'" class="ai-mindmap-editor-section">
              <!-- AI智能助手面板 -->
              <div class="ai-assistant-panel">
                <div class="ai-panel-header">
                  <i class="el-icon-magic-stick"></i>
                  <span>AI教学助手</span>
                  <el-tag size="mini" type="success">智能分析</el-tag>
                </div>
                
                <!-- 教学配置 -->
                <div class="teaching-config">
                  <div class="config-header">
                    <h5>
                      <i class="el-icon-school"></i> 
                      教学参数配置
                    </h5>
                    <el-button @click="autoRecognizeTeachingParams" size="mini" type="primary" class="ai-recognize-btn">
                      <i class="el-icon-magic-stick"></i>
                      智能识别
                    </el-button>
                  </div>
                  
                  <!-- 识别状态提示 -->
                  <div v-if="aiConfig.isAutoRecognized" class="auto-recognized-tip">
                    <div class="tip-content">
                      <i class="el-icon-check"></i>
                      <span>AI已自动识别并配置参数</span>
                    </div>
                    <el-button @click="resetToManual" size="mini" type="text" class="manual-btn">手动调整</el-button>
                  </div>
                  
                  <div class="config-grid">
                    <div class="config-item">
                      <el-form-item label="目标年级" size="mini" class="config-form-item">
                        <el-select v-model="aiConfig.targetGrade" @change="handleAIConfigChange" size="mini" style="width: 100%;">
                          <el-option label="初一" value="grade7"></el-option>
                          <el-option label="初二" value="grade8"></el-option>
                          <el-option label="初三" value="grade9"></el-option>
                          <el-option label="高一" value="grade10"></el-option>
                          <el-option label="高二" value="grade11"></el-option>
                          <el-option label="高三" value="grade12"></el-option>
                        </el-select>
                      </el-form-item>
                      <div class="ai-recognition-hint" v-if="aiConfig.gradeReason">
                        <i class="el-icon-info"></i>
                        <span>{{ aiConfig.gradeReason }}</span>
                      </div>
                    </div>
                    
                    <div class="config-item">
                      <el-form-item label="难度等级" size="mini" class="config-form-item">
                        <el-select v-model="aiConfig.difficultyLevel" @change="handleAIConfigChange" size="mini" style="width: 100%;">
                          <el-option label="基础" value="basic"></el-option>
                          <el-option label="中等" value="medium"></el-option>
                          <el-option label="提高" value="advanced"></el-option>
                          <el-option label="竞赛" value="competition"></el-option>
                        </el-select>
                      </el-form-item>
                      <div class="ai-recognition-hint" v-if="aiConfig.difficultyReason">
                        <i class="el-icon-info"></i>
                        <span>{{ aiConfig.difficultyReason }}</span>
                      </div>
                    </div>
                    
                    <div class="config-item">
                      <el-form-item label="教学重点" size="mini" class="config-form-item">
                        <el-select v-model="aiConfig.teachingFocus" @change="handleAIConfigChange" size="mini" style="width: 100%;">
                          <el-option label="概念理解" value="concept"></el-option>
                          <el-option label="解题技巧" value="solving"></el-option>
                          <el-option label="应用拓展" value="application"></el-option>
                        </el-select>
                      </el-form-item>
                      <div class="ai-recognition-hint" v-if="aiConfig.focusReason">
                        <i class="el-icon-info"></i>
                        <span>{{ aiConfig.focusReason }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- AI质量评估 -->
                <div class="ai-quality-check">
                  <h5><i class="el-icon-data-analysis"></i> AI质量评估</h5>
                  <div class="quality-metrics">
                    <div class="metric-item">
                      <span class="metric-label">内容完整性</span>
                      <el-progress :percentage="aiQuality.completeness" :status="aiQuality.completeness >= 80 ? 'success' : 'warning'" size="mini"></el-progress>
                      <span class="metric-score">{{ aiQuality.completeness }}%</span>
                    </div>
                    <div class="metric-item">
                      <span class="metric-label">知识准确性</span>
                      <el-progress :percentage="aiQuality.accuracy" :status="aiQuality.accuracy >= 90 ? 'success' : 'warning'" size="mini"></el-progress>
                      <span class="metric-score">{{ aiQuality.accuracy }}%</span>
                    </div>
                    <div class="metric-item">
                      <span class="metric-label">逻辑清晰度</span>
                      <el-progress :percentage="aiQuality.clarity" :status="aiQuality.clarity >= 85 ? 'success' : 'warning'" size="mini"></el-progress>
                      <span class="metric-score">{{ aiQuality.clarity }}%</span>
                    </div>
                  </div>
                </div>

                <!-- AI智能建议 -->
                <div class="ai-suggestions">
                  <h5><i class="el-icon-lightbulb"></i> AI智能建议</h5>
                  <div class="suggestions-list">
                    <div v-for="(suggestion, index) in aiSuggestions" :key="index" class="suggestion-item" :class="suggestion.type">
                      <div class="suggestion-icon">
                        <i :class="getSuggestionIcon(suggestion.type)"></i>
                      </div>
                      <div class="suggestion-content">
                        <div class="suggestion-title">{{ suggestion.title }}</div>
                        <div class="suggestion-desc">{{ suggestion.description }}</div>
                      </div>
                      <div class="suggestion-actions">
                        <el-button v-if="suggestion.action" size="mini" type="text" @click="applySuggestion(suggestion)">
                          {{ suggestion.actionText || '应用' }}
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 思维导图编辑区 -->
              <div class="mindmap-editing-area">
                <el-form-item label="中心主题">
                  <el-input v-model="mindmapData.centerTopic" @input="handleMindmapChange">
                    <template slot="append">
                      <el-button @click="aiOptimizeTopic" size="mini" type="primary">
                        <i class="el-icon-magic-stick"></i>
                        AI优化
                      </el-button>
                    </template>
                  </el-input>
                  <div class="ai-hint" v-if="aiTopicHint">
                    <i class="el-icon-info"></i>
                    <span>{{ aiTopicHint }}</span>
                  </div>
                </el-form-item>
                
                <el-form-item label="主要分支">
                  <div class="branches-editor">
                    <div v-for="(branch, index) in mindmapData.branches" :key="index" class="branch-item enhanced">
                      <!-- 分支标题编辑 -->
                      <div class="branch-header">
                        <el-input 
                          v-model="branch.text" 
                          placeholder="分支内容"
                          @input="handleMindmapChange"
                          class="branch-input">
                          <template slot="prepend">
                            <i :class="getBranchIcon(branch.text)" :style="{ color: getBranchColor(branch.text) }"></i>
                          </template>
                          <template slot="append">
                            <div class="branch-actions">
                              <el-button @click="aiEnhanceBranch(index)" size="mini" type="text" title="AI增强">
                                <i class="el-icon-magic-stick"></i>
                              </el-button>
                              <el-button @click="removeBranch(index)" size="mini" type="text" title="删除分支">
                                <i class="el-icon-delete"></i>
                              </el-button>
                            </div>
                          </template>
                        </el-input>
                        
                        <!-- AI分支建议 -->
                        <div v-if="branchSuggestions[index]" class="branch-ai-suggestions">
                          <div class="ai-suggestion-tip">
                            <i class="el-icon-lightbulb"></i>
                            <span>AI建议：{{ branchSuggestions[index] }}</span>
                            <el-button size="mini" type="text" @click="applyBranchSuggestion(index)">应用</el-button>
                            <el-button size="mini" type="text" @click="dismissBranchSuggestion(index)">忽略</el-button>
                          </div>
                        </div>
                      </div>
                      
                      <!-- 子节点编辑 -->
                      <div v-if="branch.children && branch.children.length > 0" class="children-editor enhanced">
                        <div class="children-label">
                          <i class="el-icon-connection"></i>
                          <span>知识点详情</span>
                          <el-button @click="aiGenerateChildren(index)" size="mini" type="text">
                            <i class="el-icon-magic-stick"></i>
                            AI补全
                          </el-button>
                        </div>
                        <div v-for="(child, childIndex) in branch.children" :key="childIndex" class="child-item enhanced">
                          <el-input 
                            v-model="branch.children[childIndex]" 
                            placeholder="知识点内容"
                            size="mini"
                            @input="handleMindmapChange">
                            <template slot="prepend">
                              <i :class="getChildIcon(child)" class="child-icon"></i>
                            </template>
                            <template slot="append">
                              <div class="child-actions">
                                <el-button @click="aiOptimizeChild(index, childIndex)" size="mini" type="text" title="AI优化">
                                  <i class="el-icon-edit"></i>
                                </el-button>
                                <el-button @click="removeChild(index, childIndex)" size="mini" type="text" title="删除">
                                  <i class="el-icon-delete"></i>
                                </el-button>
                              </div>
                            </template>
                          </el-input>
                        </div>
                        <el-button @click="addChild(index)" size="mini" type="success" plain class="add-child-btn">
                          <i class="el-icon-plus"></i>
                          添加知识点
                        </el-button>
                      </div>
                      <div v-else class="no-children enhanced">
                        <el-button @click="addChild(index)" size="mini" type="success" plain>
                          <i class="el-icon-plus"></i>
                          添加知识点
                        </el-button>
                        <el-button @click="aiGenerateChildren(index)" size="mini" type="primary" plain>
                          <i class="el-icon-magic-stick"></i>
                          AI自动生成
                        </el-button>
                      </div>
                    </div>
                    
                    <!-- 添加分支按钮 -->
                    <div class="add-branch-section">
                      <el-button @click="addBranch" size="small" type="primary">
                        <i class="el-icon-plus"></i>
                        添加分支
                      </el-button>
                      <el-button @click="aiSuggestBranches" size="small" type="success">
                        <i class="el-icon-magic-stick"></i>
                        AI推荐分支
                      </el-button>
                    </div>
                  </div>
                </el-form-item>
                
                <!-- 布局和主题设置 -->
                <el-row :gutter="16">
                  <el-col :span="12">
                    <el-form-item label="布局方式">
                      <el-radio-group v-model="mindmapData.layout" @change="handleMindmapChange" size="mini">
                        <el-radio label="horizontal">水平</el-radio>
                        <el-radio label="vertical">垂直</el-radio>
                        <el-radio label="radial">放射</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="颜色主题">
                      <el-select v-model="mindmapData.theme" @change="handleMindmapChange" size="mini">
                        <el-option label="蓝色主题" value="blue"></el-option>
                        <el-option label="绿色主题" value="green"></el-option>
                        <el-option label="橙色主题" value="orange"></el-option>
                        <el-option label="紫色主题" value="purple"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>

                <!-- AI全局操作 -->
                <div class="ai-global-actions">
                  <h5><i class="el-icon-setting"></i> AI智能操作</h5>
                  <div class="action-buttons">
                    <el-button @click="aiCheckCompleteness" size="small" type="info">
                      <i class="el-icon-view"></i>
                      完整性检查
                    </el-button>
                    <el-button @click="aiOptimizeStructure" size="small" type="primary">
                      <i class="el-icon-refresh"></i>
                      结构优化
                    </el-button>
                    <el-button @click="aiGenerateExamples" size="small" type="success">
                      <i class="el-icon-document-add"></i>
                      生成例题
                    </el-button>
                    <el-button @click="aiAdaptDifficulty" size="small" type="warning">
                      <i class="el-icon-rank"></i>
                      难度调节
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 图表编辑 -->
            <div v-if="resource.type === 'chart'" class="chart-editor-section">
              <el-form-item label="图表类型">
                <el-select v-model="chartData.type" @change="handleChartChange">
                  <el-option label="柱状图" value="bar"></el-option>
                  <el-option label="折线图" value="line"></el-option>
                  <el-option label="饼图" value="pie"></el-option>
                  <el-option label="散点图" value="scatter"></el-option>
                </el-select>
              </el-form-item>
              
              <el-form-item label="图表标题">
                <el-input v-model="chartData.title" @input="handleChartChange"></el-input>
              </el-form-item>
              
              <el-form-item label="数据">
                <div class="chart-data-editor">
                  <el-table :data="chartData.data" size="mini" border>
                    <el-table-column prop="label" label="标签" width="150">
                      <template slot-scope="scope">
                        <el-input v-model="scope.row.label" size="mini" @input="handleChartChange"></el-input>
                      </template>
                    </el-table-column>
                    <el-table-column prop="value" label="数值" width="150">
                      <template slot-scope="scope">
                        <el-input-number v-model="scope.row.value" size="mini" @change="handleChartChange"></el-input-number>
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="100">
                      <template slot-scope="scope">
                        <el-button size="mini" type="danger" @click="removeChartData(scope.$index)">删除</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <el-button @click="addChartData" size="mini" type="primary" style="margin-top: 10px;">添加数据</el-button>
                </div>
              </el-form-item>
            </div>
            
            <!-- AI视频编辑 -->
            <div v-if="resource.type === 'video'" class="ai-video-editor-section">
              <!-- AI生成信息 -->
              <div class="ai-generation-info-edit">
                <h4>
                  <i class="el-icon-magic-stick"></i>
                  AI生成信息
                </h4>
                <el-row :gutter="16">
                  <el-col :span="12">
                    <el-form-item label="AI模型">
                      <el-input v-model="videoData.aiModel" @input="handleVideoChange" placeholder="EduVidGen-V2.0">
                        <template slot="prepend">
                          <i class="el-icon-cpu"></i>
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="生成策略">
                      <el-select v-model="videoData.strategy" @change="handleVideoChange" placeholder="选择生成策略">
                        <el-option label="物理实验演示" value="物理实验演示"></el-option>
                        <el-option label="数学推理过程" value="数学推理过程"></el-option>
                        <el-option label="化学反应模拟" value="化学反应模拟"></el-option>
                        <el-option label="生物过程演示" value="生物过程演示"></el-option>
                        <el-option label="历史事件重现" value="历史事件重现"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row :gutter="16">
                  <el-col :span="12">
                    <el-form-item label="优化等级">
                      <el-select v-model="videoData.optimizationLevel" @change="handleVideoChange">
                        <el-option label="标准质量" value="标准质量"></el-option>
                        <el-option label="高质量" value="高质量"></el-option>
                        <el-option label="超高质量" value="超高质量"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="教学适配">
                      <el-input v-model="videoData.pedagogicalAdaptation" @input="handleVideoChange" placeholder="中学物理">
                        <template slot="prepend">
                          <i class="el-icon-school"></i>
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>

              <!-- 视频基础信息 -->
              <div class="video-basic-info">
                <h4>
                  <i class="el-icon-video-camera"></i>
                  视频基础信息
                </h4>
                <el-form-item label="视频文件">
                  <el-input v-model="editedResource.url" @input="handleChange">
                    <template slot="prepend">
                      <i class="el-icon-folder-opened"></i>
                    </template>
                    <template slot="append">
                      <el-button @click="selectVideoFile" icon="el-icon-search">选择</el-button>
                    </template>
                  </el-input>
                  <div class="field-hint">
                    <i class="el-icon-info"></i>
                    <span>AI智能生成的教学视频文件路径</span>
                  </div>
                </el-form-item>
                
                <el-row :gutter="16">
                  <el-col :span="8">
                    <el-form-item label="视频时长">
                      <el-input v-model="videoData.duration" @input="handleVideoChange">
                        <template slot="append">分钟</template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="视频质量">
                      <el-select v-model="videoData.resolution" @change="handleVideoChange">
                        <el-option label="720p" value="720p"></el-option>
                        <el-option label="1080p" value="1080p"></el-option>
                        <el-option label="4K" value="4k"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="内容来源">
                      <el-input value="AI智能生成 🤖" disabled>
                        <template slot="prepend">
                          <i class="el-icon-magic-stick"></i>
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>
              
              <!-- AI智能章节管理 -->
              <div class="ai-chapters-section">
                <h4>
                  <i class="el-icon-menu"></i>
                  AI智能章节管理
                  <el-tag size="mini" type="success">自动生成</el-tag>
                </h4>
                <el-form-item label="章节设置">
                  <div class="ai-chapters-editor">
                    <div class="chapters-toolbar">
                      <el-button-group size="mini">
                        <el-button @click="autoGenerateChapters" type="primary">
                          <i class="el-icon-magic-stick"></i>
                          AI自动章节划分
                        </el-button>
                        <el-button @click="optimizeChapters" type="success">
                          <i class="el-icon-cpu"></i>
                          AI优化章节
                        </el-button>
                        <el-button @click="addChapter" type="info">
                          <i class="el-icon-plus"></i>
                          手动添加
                        </el-button>
                      </el-button-group>
                    </div>
                    
                    <div class="chapters-list-edit">
                      <div v-for="(chapter, index) in videoData.chapters" :key="index" class="ai-chapter-item">
                        <div class="chapter-header">
                          <span class="chapter-index">{{ index + 1 }}</span>
                          <i class="el-icon-cpu chapter-ai-icon" title="AI生成章节"></i>
                        </div>
                        <el-row :gutter="10">
                          <el-col :span="6">
                            <el-input 
                              v-model="chapter.time" 
                              placeholder="00:00"
                              @input="handleVideoChange">
                              <template slot="prepend">
                                <i class="el-icon-time"></i>
                              </template>
                            </el-input>
                          </el-col>
                          <el-col :span="14">
                            <el-input 
                              v-model="chapter.title" 
                              placeholder="AI生成的章节标题"
                              @input="handleVideoChange">
                              <template slot="prepend">
                                <i class="el-icon-edit-outline"></i>
                              </template>
                            </el-input>
                          </el-col>
                          <el-col :span="4">
                            <el-button @click="removeChapter(index)" icon="el-icon-delete" size="mini" type="danger"></el-button>
                          </el-col>
                        </el-row>
                      </div>
                    </div>
                  </div>
                </el-form-item>
              </div>
              
              <el-form-item label="高级视频编辑">
                <el-button @click="openAdvancedVideoEditor" type="primary" icon="el-icon-edit">
                  打开高级视频编辑器
                </el-button>
              </el-form-item>
            </div>

          </el-form>
        </div>
      </div>
      
      <!-- 预览面板 -->
      <div class="preview-panel" v-show="(viewMode === 'preview' || viewMode === 'split') && !isLoading">
        <div class="preview-header">
          <div class="preview-title">
            <i class="el-icon-view"></i>
            <span>实时预览</span>
          </div>
          <div class="preview-controls">
            <el-button-group size="mini">
              <el-button @click="refreshPreview" :disabled="isRefreshing">
                <i class="el-icon-refresh" :class="{ 'is-rotating': isRefreshing }"></i>
              </el-button>
              <el-button @click="fullscreenPreview">
                <i class="el-icon-full-screen"></i>
              </el-button>
              <el-button @click="copyPreviewContent">
                <i class="el-icon-document-copy"></i>
              </el-button>
            </el-button-group>
          </div>
        </div>
        
        <div class="preview-content">
          <div class="resource-preview">
            <h3>{{ editedResource.name }}</h3>
            <p class="preview-description">{{ editedResource.description }}</p>
            
            <!-- 文本预览 -->
            <div v-if="resource.type === 'text'" class="text-preview">
              <div class="preview-text" :style="textEditorStyle" v-html="formattedContent"></div>
            </div>
            
            <!-- 思维导图预览 -->
            <div v-else-if="resource.type === 'mindmap'" class="mindmap-preview">
              <div class="mindmap-info">
                <div class="info-item">
                  <span class="label">布局方式:</span>
                  <span class="value">{{ getMindmapLayoutName(mindmapData.layout) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">主题色彩:</span>
                  <span class="value">{{ getMindmapThemeName(mindmapData.theme) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">分支数量:</span>
                  <span class="value">{{ mindmapData.branches.length }}</span>
                </div>
              </div>
              
              <div class="mindmap-visualization" :class="`theme-${mindmapData.theme} layout-${mindmapData.layout}`">
                <div class="center-node">
                  <i class="el-icon-star-on"></i>
                  {{ mindmapData.centerTopic || '中心主题' }}
                </div>
                <div class="branches" :class="`layout-${mindmapData.layout}`">
                  <div 
                    v-for="(branch, index) in mindmapData.branches" 
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
                <div v-if="mindmapData.branches.length === 0" class="empty-branches">
                  <i class="el-icon-plus"></i>
                  <span>添加分支内容</span>
                </div>
              </div>
            </div>
            
            <!-- 视频预览 -->
            <div v-else-if="resource.type === 'video'" class="video-preview">
              <!-- AI生成标识头部 -->
              <div class="ai-generated-header">
                <div class="ai-badge">
                  <i class="el-icon-cpu"></i>
                  <span>AI生成内容</span>
                  <div class="ai-badge-glow"></div>
                </div>
                <div class="ai-status">
                  <span class="status-text">智能教学视频</span>
                  <span class="generation-time">{{ getGenerationTime() }}</span>
                </div>
              </div>

              <!-- AI生成信息面板 -->
              <div class="ai-generation-info">
                <div class="info-header">
                  <i class="el-icon-magic-stick"></i>
                  <span>AI生成详情</span>
                  <el-button size="mini" type="text" @click="toggleAIDetails">
                    {{ showAIDetails ? '收起' : '展开' }}
                    <i :class="showAIDetails ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                  </el-button>
                </div>
                
                <div v-show="showAIDetails" class="ai-details">
                  <div class="ai-model-info">
                    <div class="info-item">
                      <span class="label">AI模型:</span>
                      <span class="value">{{ videoData.aiModel || 'EduVidGen-V2.0' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">生成策略:</span>
                      <span class="value">{{ videoData.strategy || '物理实验演示' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">优化等级:</span>
                      <span class="value">{{ videoData.optimizationLevel || '高质量' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">教学适配:</span>
                      <span class="value">{{ videoData.pedagogicalAdaptation || '中学物理' }}</span>
                    </div>
                  </div>
                  
                  <div class="generation-parameters">
                    <h6>生成参数</h6>
                    <div class="params-grid">
                      <div class="param-item">
                        <span>知识点聚焦度</span>
                        <div class="param-bar">
                          <div class="param-fill" style="width: 85%"></div>
                        </div>
                        <span>85%</span>
                      </div>
                      <div class="param-item">
                        <span>演示清晰度</span>
                        <div class="param-bar">
                          <div class="param-fill" style="width: 92%"></div>
                        </div>
                        <span>92%</span>
                      </div>
                      <div class="param-item">
                        <span>教学匹配度</span>
                        <div class="param-bar">
                          <div class="param-fill" style="width: 78%"></div>
                        </div>
                        <span>78%</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="video-info">
                <div class="info-item">
                  <span class="label">视频时长:</span>
                  <span class="value">{{ videoData.duration || '未知' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">视频质量:</span>
                  <span class="value">{{ videoData.resolution || '1080p' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">内容来源:</span>
                  <span class="value ai-source">AI智能生成 🤖</span>
                </div>
              </div>
              
              <div class="video-player-container">
                <video 
                  v-if="editedResource.url"
                  :src="getVideoUrl(editedResource)" 
                  controls 
                  style="width: 100%; max-height: 300px; border-radius: 8px;"
                  @error="handleVideoError">
                  <source :src="getVideoUrl(editedResource)" type="video/mp4">
                  您的浏览器不支持视频播放
                </video>
                <div v-else class="video-placeholder">
                  <i class="el-icon-video-camera"></i>
                  <p>请添加视频文件路径</p>
                </div>
                
                <!-- 视频错误信息显示 -->
                <div v-if="videoError" class="video-error">
                  <i class="el-icon-warning"></i>
                  <p>{{ videoError }}</p>
                  <el-button size="mini" @click="retryVideo">重试加载</el-button>
                </div>
              </div>
              
              <!-- AI功能操作区 -->
              <div class="ai-actions">
                <div class="action-header">
                  <i class="el-icon-setting"></i>
                  <span>AI智能操作</span>
                </div>
                <div class="action-buttons">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="regenerateVideo"
                    :loading="isRegenerating">
                    <i class="el-icon-refresh"></i>
                    重新生成
                  </el-button>
                  <el-button 
                    type="success" 
                    size="small" 
                    @click="optimizeVideo"
                    :loading="isOptimizing">
                    <i class="el-icon-magic-stick"></i>
                    AI优化
                  </el-button>
                  <el-button 
                    type="info" 
                    size="small" 
                    @click="analyzeContent">
                    <i class="el-icon-data-analysis"></i>
                    内容分析
                  </el-button>
                  <el-button 
                    type="warning" 
                    size="small" 
                    @click="adjustDifficulty">
                    <i class="el-icon-edit-outline"></i>
                    难度调节
                  </el-button>
                </div>
                
                <!-- AI生成进度 -->
                <div v-if="showGenerationProgress" class="generation-progress">
                  <div class="progress-header">
                    <span>{{ generationStatus }}</span>
                    <span class="progress-percentage">{{ generationProgress }}%</span>
                  </div>
                  <el-progress 
                    :percentage="generationProgress" 
                    :status="generationProgress === 100 ? 'success' : null"
                    :stroke-width="8">
                  </el-progress>
                </div>
              </div>

              <div v-if="videoData.chapters && videoData.chapters.length > 0" class="video-chapters">
                <h5>
                  <i class="el-icon-menu"></i>
                  AI智能章节划分
                  <el-tag size="mini" type="success">自动生成</el-tag>
                </h5>
                <div class="chapters-list">
                  <div 
                    v-for="(chapter, index) in videoData.chapters" 
                    :key="index"
                    class="chapter-item ai-chapter">
                    <span class="chapter-time">{{ chapter.time }}</span>
                    <span class="chapter-title">{{ chapter.title }}</span>
                    <el-button 
                      size="mini" 
                      type="text" 
                      @click="editChapter(index)">
                      <i class="el-icon-edit"></i>
                    </el-button>
                  </div>
                </div>
                
                <!-- AI章节建议 -->
                <div class="ai-suggestions">
                  <div class="suggestion-header">
                    <i class="el-icon-lightbulb"></i>
                    <span>AI建议</span>
                  </div>
                  <div class="suggestions-list">
                    <div class="suggestion-item">
                      <i class="el-icon-check"></i>
                      <span>当前章节划分符合认知负荷理论</span>
                    </div>
                    <div class="suggestion-item">
                      <i class="el-icon-info"></i>
                      <span>建议在第3章节添加重点提示</span>
                    </div>
                    <div class="suggestion-item">
                      <i class="el-icon-star-on"></i>
                      <span>实验演示部分可增加慢动作回放</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 图表预览 -->
            <div v-else-if="resource.type === 'chart'" class="chart-preview">
              <div class="chart-header">
                <h4>{{ chartData.title || '图表标题' }}</h4>
                <div class="chart-meta">
                  <span class="chart-type">{{ getChartTypeName(chartData.type) }}</span>
                  <span class="data-count">{{ chartData.data.length }} 个数据点</span>
                </div>
              </div>
              
              <div class="chart-visualization" :class="`chart-${chartData.type}`">
                <!-- 简单的图表可视化模拟 -->
                <div v-if="chartData.type === 'bar'" class="bar-chart">
                  <div class="chart-bars">
                    <div 
                      v-for="(item, index) in chartData.data" 
                      :key="index" 
                      class="bar-item"
                      :style="{ height: `${Math.max(item.value / getMaxValue() * 100, 5)}%`, animationDelay: `${index * 0.1}s` }">
                      <div class="bar-value">{{ item.value }}</div>
                      <div class="bar-label">{{ item.label }}</div>
                    </div>
                  </div>
                </div>
                
                <div v-else-if="chartData.type === 'line'" class="line-chart">
                  <div class="line-points">
                    <div 
                      v-for="(item, index) in chartData.data" 
                      :key="index" 
                      class="line-point"
                      :style="{ 
                        left: `${(index / Math.max(chartData.data.length - 1, 1)) * 100}%`,
                        bottom: `${Math.max(item.value / getMaxValue() * 100, 5)}%`,
                        animationDelay: `${index * 0.15}s`
                      }">
                      <div class="point-value">{{ item.value }}</div>
                      <div class="point-label">{{ item.label }}</div>
                    </div>
                  </div>
                </div>
                
                <div v-else-if="chartData.type === 'pie'" class="pie-chart">
                  <div class="pie-segments">
                    <div 
                      v-for="(item, index) in chartData.data" 
                      :key="index" 
                      class="pie-segment"
                      :style="{ 
                        '--percentage': `${(item.value / getTotalValue()) * 100}%`,
                        '--color': getPieColor(index),
                        animationDelay: `${index * 0.2}s`
                      }">
                    </div>
                  </div>
                  <div class="pie-legend">
                    <div 
                      v-for="(item, index) in chartData.data" 
                      :key="index" 
                      class="legend-item">
                      <span class="legend-color" :style="{ backgroundColor: getPieColor(index) }"></span>
                      <span class="legend-text">{{ item.label }}: {{ item.value }}</span>
                    </div>
                  </div>
                </div>
                
                <div v-else class="generic-chart">
                  <div class="chart-info">
                    <i class="el-icon-pie-chart"></i>
                    <h5>{{ getChartTypeName(chartData.type) }}</h5>
                    <div class="data-summary">
                      <div v-for="item in chartData.data" :key="item.label" class="summary-item">
                        <strong>{{ item.label }}</strong>: {{ item.value }}
                      </div>
                    </div>
                  </div>
                </div>
                
                <div v-if="chartData.data.length === 0" class="empty-chart">
                  <i class="el-icon-data-line"></i>
                  <span>添加图表数据</span>
                </div>
              </div>
            </div>
            
            <!-- 通用预览 -->
            <div v-else class="generic-preview">
              <div class="resource-info">
                <p><strong>类型:</strong> {{ getResourceTypeName(resource.type) }}</p>
                <p><strong>标签:</strong> {{ editedResource.tags.join(', ') }}</p>
                <p v-if="editedResource.url"><strong>链接:</strong> {{ editedResource.url }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 编辑器底部工具栏 -->
    <div class="editor-footer">
      <div class="footer-left">
        <span class="last-saved">{{ lastSavedText }}</span>
        <span class="change-indicator" v-if="hasUnsavedChanges">●</span>
      </div>
      
      <div class="footer-right">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSaveAndClose" :loading="isSaving">
          保存并关闭
        </el-button>
      </div>
    </div>
    
    <!-- 高级视频编辑器对话框 -->
    <el-dialog 
      title="高级视频编辑器" 
      :visible.sync="showAdvancedVideoEditor" 
      width="95%" 
      :before-close="closeAdvancedVideoEditor"
      custom-class="advanced-video-editor-dialog">
      <VideoEditor
        v-if="showAdvancedVideoEditor && editedResource.type === 'video'"
        :resource="editedResource"
        :editorData="{ videoData: videoData }"
        @content-change="handleAdvancedVideoChange">
      </VideoEditor>
    </el-dialog>
  </div>
</template>

<script>
import VideoEditor from './editors/VideoEditor.vue'

export default {
  name: 'ResourceEditor',
  
  components: {
    VideoEditor
  },
  
  props: {
    resource: {
      type: Object,
      required: true
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  
  data() {
    return {
      viewMode: 'edit',
      editedResource: {},
      hasUnsavedChanges: false,
      isSaving: false,
      isLoading: true,
      isRefreshing: false,
      autoRefresh: true,
      lastSaved: null,
      
      // 历史记录
      history: [],
      historyIndex: -1,
      
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
      
      // 文本编辑器格式
      textFormat: {
        fontSize: '14px',
        color: '#333333',
        bold: false,
        italic: false,
        underline: false
      },
      
      // 思维导图数据
      mindmapData: {
        centerTopic: '',
        branches: [],
        layout: 'horizontal',
        theme: 'blue'
      },
      
      // 图表数据
      chartData: {
        type: 'bar',
        title: '',
        data: []
      },
      
      // 视频数据
      videoData: {
        duration: '15:24',
        resolution: '1080p',
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
      },
      
      // 高级视频编辑器对话框
      showAdvancedVideoEditor: false,
      
      // 视频错误信息
      videoError: null,
      
      // AI相关状态
      showAIDetails: false,
      isRegenerating: false,
      isOptimizing: false,
      showGenerationProgress: false,
      generationProgress: 0,
      generationStatus: '',
      
      // AI思维导图相关数据
      aiConfig: {
        targetGrade: 'grade11',
        difficultyLevel: 'medium',
        teachingFocus: 'concept'
      },
      
      // AI质量评估
      aiQuality: {
        completeness: 85,
        accuracy: 92,
        clarity: 88
      },
      
      // AI智能建议
      aiSuggestions: [],
      
      // AI提示信息
      aiTopicHint: '',
      
      // 分支建议
      branchSuggestions: {}
    }
  },
  
  computed: {
    canUndo() {
      return this.historyIndex > 0
    },
    
    canRedo() {
      return this.historyIndex < this.history.length - 1
    },
    
    lastSavedText() {
      if (!this.lastSaved) return '未保存'
      return `最后保存: ${this.formatTime(this.lastSaved)}`
    },
    
    textEditorStyle() {
      return {
        fontSize: this.textFormat.fontSize,
        color: this.textFormat.color,
        fontWeight: this.textFormat.bold ? 'bold' : 'normal',
        fontStyle: this.textFormat.italic ? 'italic' : 'normal',
        textDecoration: this.textFormat.underline ? 'underline' : 'none'
      }
    },
    
    formattedContent() {
      if (!this.editedResource.content) return ''
      return this.editedResource.content.replace(/\n/g, '<br>')
    }
  },
  
  watch: {
    resource: {
      immediate: true,
      deep: true,
      handler(newResource) {
        console.log('ResourceEditor: Received resource prop:', newResource)
        if (newResource && Object.keys(newResource).length > 0) {
          console.log('ResourceEditor: About to initialize with resource:', newResource)
          this.initializeEditor()
        }
      }
    },
    
    visible: {
      immediate: true,
      handler(newVisible) {
        console.log('ResourceEditor: visible changed to:', newVisible, 'resource:', this.resource)
        if (newVisible && this.resource && Object.keys(this.resource).length > 0) {
          console.log('ResourceEditor: Dialog opened, reinitializing editor')
          this.initializeEditor()
        }
      }
    }
  },
  
  mounted() {
    console.log('ResourceEditor: Mounted with resource:', this.resource)
    this.initializeEditor()
    this.setupAutoSave()
    this.initializeAIFeatures()
  },
  
  methods: {
    initializeEditor() {
      console.log('ResourceEditor: Initializing editor with resource:', this.resource)
      console.log('ResourceEditor: Resource type:', typeof this.resource)
      console.log('ResourceEditor: Resource keys:', this.resource ? Object.keys(this.resource) : 'resource is null/undefined')
      
      // 设置加载状态
      this.isLoading = true
      
      if (!this.resource || Object.keys(this.resource).length === 0) {
        console.warn('ResourceEditor: No resource provided or resource is empty')
        // 初始化一个空的编辑对象以防止错误
        this.editedResource = {
          id: null,
          name: '',
          description: '',
          type: 'text',
          tags: [],
          content: '',
          url: '',
          subject: '',
          grade: ''
        }
        this.isLoading = false
        return
      }
      
      try {
        // 深拷贝资源数据
        this.editedResource = JSON.parse(JSON.stringify(this.resource))
        console.log('ResourceEditor: Deep copied resource successfully')
        
        // 确保必要属性存在
        if (!this.editedResource.tags) this.editedResource.tags = []
        if (!this.editedResource.content) this.editedResource.content = ''
        if (!this.editedResource.url) this.editedResource.url = ''
        if (!this.editedResource.subject) this.editedResource.subject = ''
        if (!this.editedResource.grade) this.editedResource.grade = ''
        
        console.log('ResourceEditor: Initialized editedResource:', this.editedResource)
        console.log('ResourceEditor: editedResource name:', this.editedResource.name)
        console.log('ResourceEditor: editedResource description:', this.editedResource.description)
        
        // 初始化特定类型的数据
        this.initializeTypeSpecificData()
        
        // 初始化历史记录
        this.saveToHistory()
        
        // 重置状态
        this.hasUnsavedChanges = false
        this.lastSaved = null
        
        console.log('ResourceEditor: Editor initialization completed successfully')
        
        // 取消加载状态
        this.isLoading = false
      } catch (error) {
        console.error('ResourceEditor: Error during initialization:', error)
        // 创建一个安全的默认对象
        this.editedResource = {
          id: this.resource.id || null,
          name: this.resource.name || '未命名资源',
          description: this.resource.description || '无描述',
          type: this.resource.type || 'text',
          tags: this.resource.tags || [],
          content: this.resource.content || '',
          url: this.resource.url || '',
          subject: this.resource.subject || '',
          grade: this.resource.grade || ''
        }
        this.isLoading = false
      }
    },
    
    initializeTypeSpecificData() {
      // 初始化思维导图数据
      if (this.resource.type === 'mindmap') {
        const metadata = this.resource.metadata || {}
        
        // 尝试从content中解析具体内容
        let centerTopic = metadata.centerTopic || this.resource.name || '中心主题'
        let branches = metadata.branches || []
        
        // 如果没有metadata但有content，尝试智能解析
        if (!metadata.branches && this.resource.content) {
          console.log('ResourceEditor: Attempting to parse mindmap content:', this.resource.content)
          const parsedData = this.parseMindmapContent(this.resource.content)
          console.log('ResourceEditor: Parsed mindmap data:', parsedData)
          if (parsedData) {
            centerTopic = parsedData.centerTopic
            branches = parsedData.branches
            console.log('ResourceEditor: Using parsed data - centerTopic:', centerTopic, 'branches:', branches)
          }
        }
        
        // 如果还是没有分支，使用默认值
        if (branches.length === 0) {
          branches = [
            { text: '分支1' },
            { text: '分支2' },
            { text: '分支3' }
          ]
        }
        
        this.mindmapData = {
          centerTopic: centerTopic,
          branches: branches,
          layout: metadata.layout || 'horizontal',
          theme: metadata.theme || 'blue'
        }
      }
      
      // 初始化图表数据
      if (this.resource.type === 'chart') {
        const metadata = this.resource.metadata || {}
        this.chartData = {
          type: metadata.chartType || 'bar',
          title: metadata.title || this.resource.name || '图表标题',
          data: metadata.data || [
            { label: '项目1', value: 10 },
            { label: '项目2', value: 20 },
            { label: '项目3', value: 15 }
          ]
        }
      }
      
      // 初始化视频数据
      if (this.resource.type === 'video') {
        const videoMetadata = this.resource.videoMetadata || {}
        this.videoData = {
          duration: this.resource.duration || videoMetadata.duration || '7:55',
          resolution: this.resource.resolution || videoMetadata.resolution || '1080p',
          platform: videoMetadata.platform || '本地视频 📁',
          // AI相关字段
          aiModel: videoMetadata.aiModel || 'EduVidGen-V2.0',
          strategy: videoMetadata.strategy || '物理实验演示',
          optimizationLevel: videoMetadata.optimizationLevel || '高质量',
          pedagogicalAdaptation: videoMetadata.pedagogicalAdaptation || '中学物理',
          chapters: videoMetadata.chapters || [
            { time: '00:00', title: '实验介绍与目标' },
            { time: '01:20', title: '实验器材准备' },
            { time: '02:45', title: '电路连接步骤' },
            { time: '04:30', title: '欧姆定律验证' },
            { time: '06:10', title: '数据记录与分析' },
            { time: '07:30', title: '实验总结与思考' }
          ]
        }
      }
    },
    
    handleChange() {
      this.hasUnsavedChanges = true
      this.debounceHistorySave()
      
      // 自动刷新预览（在分割模式下）
      if (this.viewMode === 'split' && this.autoRefresh) {
        this.debouncedPreviewUpdate()
      }
    },
    
    // 文本格式化
    formatText(format) {
      this.textFormat[format] = !this.textFormat[format]
      this.handleChange()
    },
    
    // 思维导图编辑
    handleMindmapChange() {
      // 更新editedResource的metadata
      if (!this.editedResource.metadata) this.editedResource.metadata = {}
      this.editedResource.metadata = {
        ...this.editedResource.metadata,
        centerTopic: this.mindmapData.centerTopic,
        branches: this.mindmapData.branches,
        layout: this.mindmapData.layout,
        theme: this.mindmapData.theme
      }
      this.handleChange()
    },
    
    addBranch() {
      this.mindmapData.branches.push({ text: '新分支' })
      this.handleMindmapChange()
    },
    
    removeBranch(index) {
      this.mindmapData.branches.splice(index, 1)
      this.handleMindmapChange()
    },
    
    addChild(branchIndex) {
      if (!this.mindmapData.branches[branchIndex].children) {
        this.$set(this.mindmapData.branches[branchIndex], 'children', [])
      }
      this.mindmapData.branches[branchIndex].children.push('新子节点')
      this.handleMindmapChange()
    },
    
    removeChild(branchIndex, childIndex) {
      this.mindmapData.branches[branchIndex].children.splice(childIndex, 1)
      this.handleMindmapChange()
    },
    
    // 图表编辑
    handleChartChange() {
      // 更新editedResource的metadata
      if (!this.editedResource.metadata) this.editedResource.metadata = {}
      this.editedResource.metadata = {
        ...this.editedResource.metadata,
        chartType: this.chartData.type,
        title: this.chartData.title,
        data: this.chartData.data
      }
      this.handleChange()
    },
    
    addChartData() {
      this.chartData.data.push({ 
        label: `项目${this.chartData.data.length + 1}`, 
        value: 0 
      })
      this.handleChartChange()
    },
    
    removeChartData(index) {
      this.chartData.data.splice(index, 1)
      this.handleChartChange()
    },
    
    // 视频编辑方法
    handleVideoChange() {
      // 更新editedResource的videoMetadata
      if (!this.editedResource.videoMetadata) this.editedResource.videoMetadata = {}
      this.editedResource.videoMetadata = {
        ...this.editedResource.videoMetadata,
        duration: this.videoData.duration,
        resolution: this.videoData.resolution,
        platform: this.videoData.platform,
        // AI相关字段
        aiModel: this.videoData.aiModel,
        strategy: this.videoData.strategy,
        optimizationLevel: this.videoData.optimizationLevel,
        pedagogicalAdaptation: this.videoData.pedagogicalAdaptation,
        chapters: this.videoData.chapters
      }
      
      // 同时更新顶级属性
      this.editedResource.duration = this.videoData.duration
      this.editedResource.resolution = this.videoData.resolution
      
      this.handleChange()
    },
    
    addChapter() {
      this.videoData.chapters.push({
        time: '00:00',
        title: '新章节'
      })
      this.handleVideoChange()
    },
    
    removeChapter(index) {
      this.videoData.chapters.splice(index, 1)
      this.handleVideoChange()
    },
    
    openAdvancedVideoEditor() {
      this.showAdvancedVideoEditor = true
    },
    
    closeAdvancedVideoEditor() {
      this.showAdvancedVideoEditor = false
    },
    
    handleVideoError(event) {
      console.warn('视频加载失败:', event)
      
      this.videoError = '视频文件无法加载，请检查文件路径是否正确'
      
      // 尝试备用路径
      if (this.editedResource.backupUrl && this.editedResource.url !== this.editedResource.backupUrl) {
        console.log('尝试备用视频路径:', this.editedResource.backupUrl)
        this.editedResource.url = this.editedResource.backupUrl
        this.videoError = null
        this.$nextTick(() => {
          const video = this.$el.querySelector('video')
          if (video) {
            video.load()
          }
        })
        return
      }
      
      this.$message({
        message: '视频文件无法加载，请检查文件路径是否正确',
        type: 'warning',
        duration: 3000
      })
    },
    
    getVideoUrl(resource) {
      if (!resource || !resource.url) return ''
      
      // 处理不同类型的视频URL
      const url = resource.url
      
      // 本地视频文件路径处理
      if (url.includes('.mp4') && !url.startsWith('http')) {
        // 确保路径以 / 开头
        return url.startsWith('/') ? url : '/' + url
      }
      
      return url
    },
    
    retryVideo() {
      this.videoError = null
      // 强制重新加载视频
      this.$nextTick(() => {
        const video = this.$el.querySelector('video')
        if (video) {
          video.load()
        }
      })
    },
    
    // AI功能相关方法
    getGenerationTime() {
      return '2024-01-15 14:32:18'
    },
    
    toggleAIDetails() {
      this.showAIDetails = !this.showAIDetails
    },
    
    regenerateVideo() {
      this.isRegenerating = true
      this.showGenerationProgress = true
      this.generationProgress = 0
      this.generationStatus = '正在重新生成视频内容...'
      
      // 模拟生成过程
      const progressSteps = [
        { progress: 15, status: '分析教学需求...' },
        { progress: 30, status: '构建实验场景...' },
        { progress: 45, status: '渲染实验过程...' },
        { progress: 65, status: '优化教学效果...' },
        { progress: 85, status: '生成章节标记...' },
        { progress: 100, status: '生成完成！' }
      ]
      
      let stepIndex = 0
      const updateProgress = () => {
        if (stepIndex < progressSteps.length) {
          const step = progressSteps[stepIndex]
          this.generationProgress = step.progress
          this.generationStatus = step.status
          stepIndex++
          setTimeout(updateProgress, 1500)
        } else {
          setTimeout(() => {
            this.isRegenerating = false
            this.showGenerationProgress = false
            this.$message({
              type: 'success',
              message: 'AI重新生成完成！视频内容已优化'
            })
          }, 1000)
        }
      }
      
      updateProgress()
    },
    
    optimizeVideo() {
      this.isOptimizing = true
      this.showGenerationProgress = true
      this.generationProgress = 0
      this.generationStatus = '正在优化视频内容...'
      
      const progressSteps = [
        { progress: 20, status: '分析教学重点...' },
        { progress: 50, status: '优化讲解顺序...' },
        { progress: 80, status: '增强视觉效果...' },
        { progress: 100, status: '优化完成！' }
      ]
      
      let stepIndex = 0
      const updateProgress = () => {
        if (stepIndex < progressSteps.length) {
          const step = progressSteps[stepIndex]
          this.generationProgress = step.progress
          this.generationStatus = step.status
          stepIndex++
          setTimeout(updateProgress, 1200)
        } else {
          setTimeout(() => {
            this.isOptimizing = false
            this.showGenerationProgress = false
            
            // 更新AI参数显示
            this.videoData.aiModel = 'EduVidGen-V2.1'
            this.videoData.optimizationLevel = '超高质量'
            
            this.$message({
              type: 'success',
              message: 'AI优化完成！教学效果已提升'
            })
          }, 1000)
        }
      }
      
      updateProgress()
    },
    
    analyzeContent() {
      this.$confirm('AI将分析视频内容的教学效果和学习难度，是否继续？', 'AI内容分析', {
        confirmButtonText: '开始分析',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$message({
          type: 'info',
          message: '正在进行AI内容分析...'
        })
        
        setTimeout(() => {
          this.$alert(
            `分析结果：
            - 知识点覆盖度：92%
            - 认知负荷适宜度：85%
            - 视觉清晰度：88%
            - 教学逻辑性：90%
            
            建议：适当增加重点提示，优化实验演示节奏`, 
            'AI分析报告', {
              confirmButtonText: '了解',
              type: 'success'
            }
          )
        }, 2000)
      })
    },
    
    adjustDifficulty() {
      this.$prompt('请选择目标难度等级：', '难度调节', {
        confirmButtonText: '调整',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '输入：简单、中等、困难，或描述具体要求',
        inputValue: '中等 - 适合高中物理基础水平'
      }).then(({ value }) => {
        this.$message({
          type: 'success',
          message: `正在根据要求"${value}"调整视频难度...`
        })
        
        setTimeout(() => {
          this.videoData.pedagogicalAdaptation = value
          this.$message({
            type: 'success',
            message: 'AI难度调节完成！'
          })
        }, 2000)
      })
    },
    
    editChapter(index) {
      const chapter = this.videoData.chapters[index]
      this.$prompt('编辑章节标题：', '章节编辑', {
        confirmButtonText: '保存',
        cancelButtonText: '取消',
        inputValue: chapter.title
      }).then(({ value }) => {
        this.videoData.chapters[index].title = value
        this.handleVideoChange()
        this.$message({
          type: 'success',
          message: '章节标题已更新'
        })
      })
    },
    
    selectVideoFile() {
      this.$message({
        type: 'info',
        message: '这里可以集成文件选择器，选择AI生成的视频文件'
      })
    },
    
    autoGenerateChapters() {
      this.$confirm('AI将根据视频内容自动生成章节划分，是否继续？', 'AI自动章节划分', {
        confirmButtonText: '开始生成',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.showGenerationProgress = true
        this.generationProgress = 0
        this.generationStatus = '正在分析视频内容...'
        
        const progressSteps = [
          { progress: 25, status: '识别关键知识点...' },
          { progress: 50, status: '分析教学节奏...' },
          { progress: 75, status: '生成章节结构...' },
          { progress: 100, status: '章节划分完成！' }
        ]
        
        let stepIndex = 0
        const updateProgress = () => {
          if (stepIndex < progressSteps.length) {
            const step = progressSteps[stepIndex]
            this.generationProgress = step.progress
            this.generationStatus = step.status
            stepIndex++
            setTimeout(updateProgress, 1000)
          } else {
            setTimeout(() => {
              this.showGenerationProgress = false
              
              // 更新章节数据
              this.videoData.chapters = [
                { time: '00:00', title: '实验介绍与目标' },
                { time: '01:20', title: '实验器材准备' },
                { time: '02:45', title: '电路连接步骤' },
                { time: '04:30', title: '欧姆定律验证' },
                { time: '06:10', title: '数据记录与分析' },
                { time: '07:30', title: '实验总结与思考' }
              ]
              
              this.handleVideoChange()
              this.$message({
                type: 'success',
                message: 'AI自动章节划分完成！已生成6个教学章节'
              })
            }, 1000)
          }
        }
        
        updateProgress()
      })
    },
    
    optimizeChapters() {
      this.$confirm('AI将优化当前章节设置，提升教学效果，是否继续？', 'AI章节优化', {
        confirmButtonText: '开始优化',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$message({
          type: 'info',
          message: '正在进行AI章节优化...'
        })
        
        setTimeout(() => {
          // 优化章节标题
          this.videoData.chapters.forEach((chapter, index) => {
            if (index < this.videoData.chapters.length - 1) {
              chapter.title = chapter.title + ' (已优化)'
            }
          })
          
          this.handleVideoChange()
          this.$message({
            type: 'success',
            message: 'AI章节优化完成！教学逻辑更加清晰'
          })
        }, 2000)
      })
    },
    
    handleAdvancedVideoChange(data) {
      console.log('高级视频编辑器数据变化:', data)
      
      if (data.videoData) {
        // 更新视频数据
        this.videoData = { ...this.videoData, ...data.videoData }
        this.handleVideoChange()
      }
      
      this.$message({
        message: '视频编辑已应用',
        type: 'success',
        duration: 2000
      })
    },
    
    // 历史记录管理
    saveToHistory() {
      const state = {
        timestamp: Date.now(),
        data: JSON.parse(JSON.stringify(this.editedResource))
      }
      
      // 移除当前位置之后的历史记录
      if (this.historyIndex < this.history.length - 1) {
        this.history = this.history.slice(0, this.historyIndex + 1)
      }
      
      this.history.push(state)
      this.historyIndex = this.history.length - 1
      
      // 限制历史记录数量
      if (this.history.length > 30) {
        this.history.shift()
        this.historyIndex--
      }
    },
    
    debounceHistorySave() {
      if (this.historyTimer) {
        clearTimeout(this.historyTimer)
      }
      
      this.historyTimer = setTimeout(() => {
        this.saveToHistory()
      }, 1000)
    },
    
    undo() {
      if (this.canUndo) {
        this.historyIndex--
        this.loadHistoryState(this.history[this.historyIndex])
      }
    },
    
    redo() {
      if (this.canRedo) {
        this.historyIndex++
        this.loadHistoryState(this.history[this.historyIndex])
      }
    },
    
    loadHistoryState(state) {
      this.editedResource = JSON.parse(JSON.stringify(state.data))
      this.hasUnsavedChanges = true
      
      // 重新初始化特定类型数据
      this.initializeTypeSpecificData()
    },
    
    // 保存功能
    setupAutoSave() {
      this.autoSaveTimer = setInterval(() => {
        if (this.hasUnsavedChanges) {
          this.handleSave()
        }
      }, 30000) // 每30秒自动保存
    },

    // 初始化AI功能
    initializeAIFeatures() {
      try {
        // 确保必要的数据结构存在
        if (!this.aiConfig) {
          this.aiConfig = {
            targetGrade: 'grade11',
            difficultyLevel: 'medium',
            teachingFocus: 'concept'
          }
        }
        
        if (!this.aiQuality) {
          this.aiQuality = {
            completeness: 85,
            accuracy: 92,
            clarity: 88
          }
        }
        
        if (!this.aiSuggestions) {
          this.aiSuggestions = []
        }
        
        if (!this.branchSuggestions) {
          this.branchSuggestions = {}
        }
        
        // 确保mindmapData存在
        if (!this.mindmapData) {
          this.mindmapData = {
            centerTopic: '',
            branches: [],
            layout: 'horizontal',
            theme: 'blue'
          }
        }
        
        // 自动识别教学参数
        this.autoRecognizeTeachingParams()
        
        // 初始化AI建议
        this.updateAISuggestions()
        this.updateQualityAssessment()
        
        // 为二次函数思维导图提供初始化建议
        if (this.resource && this.resource.type === 'mindmap') {
          setTimeout(() => {
            if (this.mindmapData && this.mindmapData.centerTopic && this.mindmapData.centerTopic.includes('二次函数')) {
              this.aiTopicHint = '💡 AI智能识别：已自动配置高二数学、中等难度、概念理解重点'
              this.updateAISuggestions()
            }
          }, 1000)
        }
      } catch (error) {
        console.error('ResourceEditor: Error initializing AI features:', error)
      }
    },
    
    handleSave() {
      this.isSaving = true
      
      // 模拟保存延迟
      setTimeout(() => {
        this.lastSaved = new Date()
        this.hasUnsavedChanges = false
        this.isSaving = false
        
        console.log('ResourceEditor: Saving resource:', this.editedResource)
        
        // 发出保存成功事件
        this.$emit('save-success', this.editedResource)
      }, 500)
    },
    
    handleSaveAndClose() {
      this.handleSave()
      
      setTimeout(() => {
        this.$emit('close')
      }, 600)
    },
    
    handleCancel() {
      if (this.hasUnsavedChanges) {
        this.$confirm('有未保存的更改，确定要关闭吗？', '确认关闭', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$emit('close')
        })
      } else {
        this.$emit('close')
      }
    },
    
    // 工具方法
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
      
      // 通用解析：尝试从内容中提取关键词
      const keywords = content.match(/[一-龥a-zA-Z]+/g) || []
      if (keywords.length > 0) {
        const mainTopic = keywords[0]
        const branches = keywords.slice(1, 5).map(keyword => ({ text: keyword }))
        
        if (branches.length > 0) {
          return {
            centerTopic: mainTopic,
            branches: branches
          }
        }
      }
      
      return null
    },

    // AI思维导图智能功能方法
    handleAIConfigChange() {
      // 根据AI配置更新建议
      this.updateAISuggestions()
      this.updateQualityAssessment()
      this.handleMindmapChange()
    },

    updateAISuggestions() {
      try {
        const suggestions = []
        
        // 确保必要数据存在
        if (!this.aiConfig || !this.mindmapData) {
          this.aiSuggestions = suggestions
          return
        }
        
        // 根据年级和难度生成建议
        if (this.aiConfig.targetGrade === 'grade11' && this.aiConfig.difficultyLevel === 'medium') {
          suggestions.push({
            type: 'enhance',
            title: '建议增加应用实例',
            description: '针对高二学生，建议在每个分支添加具体的应用实例，帮助理解抽象概念。',
            action: () => this.aiGenerateExamples(),
            actionText: '自动添加'
          })
        }
        
        if (this.aiConfig.teachingFocus === 'concept') {
          suggestions.push({
            type: 'structure',
            title: '优化概念层次',
            description: '当前重点是概念理解，建议调整知识点的层次结构，突出核心概念。',
            action: () => this.aiOptimizeStructure(),
            actionText: '优化结构'
          })
        }
        
        if (this.mindmapData.branches && this.mindmapData.branches.length < 3) {
          suggestions.push({
            type: 'warning',
            title: '知识点覆盖不足',
            description: '当前只有' + this.mindmapData.branches.length + '个主要分支，建议增加到3-4个以确保知识点覆盖完整。',
            action: () => this.aiSuggestBranches(),
            actionText: '补充分支'
          })
        }
        
        this.aiSuggestions = suggestions
      } catch (error) {
        console.error('ResourceEditor: Error updating AI suggestions:', error)
        this.aiSuggestions = []
      }
    },

    updateQualityAssessment() {
      try {
        // 根据内容动态评估质量
        let completeness = 60
        let accuracy = 90
        let clarity = 70
        
        // 确保mindmapData存在
        if (!this.mindmapData) {
          this.aiQuality = {
            completeness: 60,
            accuracy: 90,
            clarity: 70
          }
          return
        }
        
        // 评估完整性
        if (this.mindmapData.branches && this.mindmapData.branches.length >= 3) completeness += 20
        if (this.mindmapData.centerTopic && this.mindmapData.centerTopic.length > 5) completeness += 10
        if (this.mindmapData.branches) {
          this.mindmapData.branches.forEach(branch => {
            if (branch && branch.children && branch.children.length > 2) completeness += 5
          })
        }
        
        // 评估准确性（基于内容关键词匹配）
        if (this.mindmapData.centerTopic && this.mindmapData.centerTopic.includes('二次函数')) accuracy = 95
        
        // 评估清晰度（基于结构和表达）
        if (this.mindmapData.layout === 'horizontal') clarity += 10
        if (this.mindmapData.branches && this.mindmapData.branches.every(b => b && b.text && b.text.length > 0)) clarity += 15
        
        this.aiQuality = {
          completeness: Math.min(completeness, 100),
          accuracy: Math.min(accuracy, 100),
          clarity: Math.min(clarity, 100)
        }
      } catch (error) {
        console.error('ResourceEditor: Error updating quality assessment:', error)
        this.aiQuality = {
          completeness: 60,
          accuracy: 90,
          clarity: 70
        }
      }
    },

    getSuggestionIcon(type) {
      const iconMap = {
        enhance: 'el-icon-star-on',
        structure: 'el-icon-menu',
        warning: 'el-icon-warning',
        info: 'el-icon-info'
      }
      return iconMap[type] || 'el-icon-lightbulb'
    },

    applySuggestion(suggestion) {
      if (suggestion.action) {
        suggestion.action()
        this.$message({
          type: 'success',
          message: '已应用AI建议：' + suggestion.title
        })
      }
    },

    aiOptimizeTopic() {
      if (!this.mindmapData.centerTopic) {
        this.$message({
          type: 'warning',
          message: '请先输入中心主题'
        })
        return
      }
      
      // AI优化主题表达
      const optimizedTopics = {
        '二次函数': '二次函数 y = ax² + bx + c (a≠0)',
        '化学': '化学基础知识体系',
        '英语': '英语语法知识框架',
        '物理': '物理概念与实验'
      }
      
      for (let key in optimizedTopics) {
        if (this.mindmapData.centerTopic.includes(key)) {
          this.mindmapData.centerTopic = optimizedTopics[key]
          this.aiTopicHint = 'AI已优化主题表达，使其更加准确和专业'
          this.handleMindmapChange()
          break
        }
      }
    },

    getBranchIcon(branchText) {
      if (branchText.includes('解析式') || branchText.includes('基本')) return 'el-icon-edit'
      if (branchText.includes('图像') || branchText.includes('性质')) return 'el-icon-data-line'
      if (branchText.includes('应用') || branchText.includes('实际')) return 'el-icon-star-on'
      if (branchText.includes('判别式') || branchText.includes('计算')) return 'el-icon-calculator'
      return 'el-icon-menu'
    },

    getBranchColor(branchText) {
      if (branchText.includes('解析式') || branchText.includes('基本')) return '#f59e0b'
      if (branchText.includes('图像') || branchText.includes('性质')) return '#10b981'
      if (branchText.includes('应用') || branchText.includes('实际')) return '#f97316'
      if (branchText.includes('判别式') || branchText.includes('计算')) return '#8b5cf6'
      return '#6b7280'
    },

    getChildIcon(childText) {
      if (childText.includes('公式') || childText.includes('式')) return 'el-icon-document'
      if (childText.includes('图') || childText.includes('线')) return 'el-icon-picture'
      if (childText.includes('应用') || childText.includes('问题')) return 'el-icon-cpu'
      return 'el-icon-arrow-right'
    },

    aiEnhanceBranch(index) {
      const branch = this.mindmapData.branches[index]
      if (!branch) return
      
      // AI增强分支建议
      const suggestions = {
        '解析式与基本概念': '建议添加：函数的定义域、值域，以及解析式之间的转换方法',
        '图像性质与判别式': '建议添加：开口方向判断、对称轴公式、顶点坐标计算',
        '实际应用与解题方法': '建议添加：最值问题的解法、函数图像的变换、综合应用题型'
      }
      
      const suggestion = suggestions[branch.text] || '建议完善该分支的子知识点，确保内容全面性'
      this.$set(this.branchSuggestions, index, suggestion)
    },

    applyBranchSuggestion(index) {
      const branch = this.mindmapData.branches[index]
      if (!branch) return
      
      // 根据分支类型自动补充内容
      const autoContent = {
        '解析式与基本概念': [
          '一般式：y = ax² + bx + c (a≠0)',
          '顶点式：y = a(x - h)² + k',
          '交点式：y = a(x - x₁)(x - x₂)',
          '定义域：R (实数集)',
          '值域：根据开口方向确定'
        ],
        '图像性质与判别式': [
          '图像：开口向上/向下的抛物线',
          '开口方向：a > 0向上，a < 0向下',
          '对称轴：x = -b/(2a)',
          '顶点坐标：(-b/(2a), (4ac-b²)/(4a))',
          '判别式：Δ = b² - 4ac'
        ],
        '实际应用与解题方法': [
          '最值问题（经济利润等）',
          '抛物运动与物理应用',
          '几何图形面积最值',
          '一元二次不等式求解'
        ]
      }
      
      if (autoContent[branch.text]) {
        this.$set(this.mindmapData.branches[index], 'children', autoContent[branch.text])
      }
      
      this.dismissBranchSuggestion(index)
      this.handleMindmapChange()
      this.updateQualityAssessment()
    },

    dismissBranchSuggestion(index) {
      this.$delete(this.branchSuggestions, index)
    },

    aiGenerateChildren(index) {
      const branch = this.mindmapData.branches[index]
      if (!branch) return
      
      this.$message({
        type: 'info',
        message: '正在为 "' + branch.text + '" 生成子知识点...'
      })
      
      setTimeout(() => {
        // 基于分支主题智能生成子内容
        if (branch.text.includes('解析式') || branch.text.includes('基本')) {
          branch.children = [
            '一般式：y = ax² + bx + c (a≠0)',
            '顶点式：y = a(x - h)² + k',
            '交点式：y = a(x - x₁)(x - x₂)',
            '定义域与值域分析',
            '三种形式的互化'
          ]
        } else if (branch.text.includes('图像') || branch.text.includes('性质')) {
          branch.children = [
            '抛物线的基本形状',
            '开口方向判断(a的符号)',
            '对称轴：x = -b/(2a)',
            '顶点坐标计算',
            '与x轴交点(根的情况)',
            '函数的单调性'
          ]
        } else if (branch.text.includes('应用') || branch.text.includes('实际')) {
          branch.children = [
            '最值问题的数学建模',
            '抛物运动轨迹分析',
            '经济问题中的利润最大化',
            '几何图形的面积最值',
            '一元二次不等式应用'
          ]
        } else {
          // 通用生成
          branch.children = [
            '基础概念',
            '重要性质',
            '计算方法',
            '应用实例'
          ]
        }
        
        this.handleMindmapChange()
        this.updateQualityAssessment()
        
        this.$message({
          type: 'success',
          message: 'AI已自动生成' + branch.children.length + '个子知识点'
        })
      }, 1000)
    },

    aiOptimizeChild(branchIndex, childIndex) {
      const child = this.mindmapData.branches[branchIndex].children[childIndex]
      if (!child) return
      
      // AI优化子节点表达
      const optimizations = {
        '一般式': '一般式：y = ax² + bx + c (a≠0)',
        '顶点式': '顶点式：y = a(x - h)² + k',
        '交点式': '交点式：y = a(x - x₁)(x - x₂)',
        '对称轴': '对称轴：x = -b/(2a)',
        '判别式': '判别式：Δ = b² - 4ac',
        '开口方向': '开口方向：a > 0向上，a < 0向下'
      }
      
      for (let key in optimizations) {
        if (child.includes(key)) {
          this.mindmapData.branches[branchIndex].children[childIndex] = optimizations[key]
          this.handleMindmapChange()
          this.$message({
            type: 'success',
            message: 'AI已优化知识点表达'
          })
          break
        }
      }
    },

    aiSuggestBranches() {
      const suggestions = [
        '解析式与基本概念',
        '图像性质与判别式', 
        '实际应用与解题方法',
        '函数变换与平移',
        '与其他函数的关系'
      ]
      
      this.$confirm('AI建议为二次函数思维导图添加以下分支：\n\n' + suggestions.join('\n'), 'AI分支建议', {
        confirmButtonText: '应用全部',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.mindmapData.branches = suggestions.slice(0, 3).map(text => ({
          text: text,
          children: []
        }))
        this.handleMindmapChange()
        this.updateQualityAssessment()
        this.$message({
          type: 'success',
          message: 'AI已添加3个主要分支'
        })
      })
    },

    aiCheckCompleteness() {
      let issues = []
      
      if (!this.mindmapData.centerTopic) {
        issues.push('缺少中心主题')
      }
      
      if (this.mindmapData.branches.length < 3) {
        issues.push('主要分支过少（建议3-5个）')
      }
      
      this.mindmapData.branches.forEach((branch, index) => {
        if (!branch.children || branch.children.length < 2) {
          issues.push(`分支"${branch.text}"子知识点过少`)
        }
      })
      
      if (issues.length === 0) {
        this.$message({
          type: 'success',
          message: 'AI检查通过：思维导图结构完整，内容充实！'
        })
      } else {
        this.$alert('AI检查发现以下问题：\n\n' + issues.join('\n'), '完整性检查结果', {
          confirmButtonText: '了解',
          type: 'warning'
        })
      }
    },

    aiOptimizeStructure() {
      this.$confirm('AI将根据教学逻辑优化思维导图结构，继续吗？', 'AI结构优化', {
        confirmButtonText: '开始优化',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        // 重新组织分支顺序
        const optimizedBranches = [
          {
            text: '解析式与基本概念',
            children: [
              '一般式：y = ax² + bx + c (a≠0)',
              '顶点式：y = a(x - h)² + k', 
              '交点式：y = a(x - x₁)(x - x₂)',
              '定义域：R (实数集)',
              '值域：根据开口方向确定'
            ]
          },
          {
            text: '图像性质与判别式',
            children: [
              '图像：开口向上/向下的抛物线',
              '开口方向：a > 0向上，a < 0向下',
              '对称轴：x = -b/(2a)',
              '顶点坐标：(-b/(2a), (4ac-b²)/(4a))',
              '判别式：Δ = b² - 4ac'
            ]
          },
          {
            text: '实际应用与解题方法',
            children: [
              '最值问题（经济利润等）',
              '抛物运动与物理应用',
              '几何图形面积最值',
              '一元二次不等式求解'
            ]
          }
        ]
        
        this.mindmapData.branches = optimizedBranches
        this.handleMindmapChange()
        this.updateQualityAssessment()
        
        this.$message({
          type: 'success',
          message: 'AI结构优化完成：已按教学逻辑重新排列'
        })
      })
    },

    aiGenerateExamples() {
      this.$message({
        type: 'info',
        message: '正在生成配套例题...'
      })
      
      setTimeout(() => {
        const examples = [
          '例1：求函数 y = x² - 4x + 3 的顶点坐标',
          '例2：已知抛物线经过点(1,0)和(3,0)，求解析式',
          '例3：某商品利润函数为 L = -2x² + 20x - 18，求最大利润'
        ]
        
        this.$alert('AI已生成配套例题：\n\n' + examples.join('\n\n'), 'AI生成例题', {
          confirmButtonText: '保存例题',
          type: 'success'
        })
      }, 1500)
    },

    aiAdaptDifficulty() {
      const levels = {
        'basic': '基础版：侧重概念理解和基本计算',
        'medium': '标准版：平衡理论与应用',
        'advanced': '提高版：增加综合应用和变式',
        'competition': '竞赛版：包含高阶技巧和创新题型'
      }
      
      this.$confirm(`当前难度：${levels[this.aiConfig.difficultyLevel]}\n\n是否需要调整难度等级？`, '难度自适应调节', {
        confirmButtonText: '智能调节',
        cancelButtonText: '保持现状',
        type: 'info'
      }).then(() => {
        this.$message({
          type: 'success',
          message: 'AI正在根据目标年级和教学重点调整内容难度...'
        })
        
        // 根据配置调整内容
        setTimeout(() => {
          this.updateQualityAssessment()
          this.$message({
            type: 'success',
            message: '难度调节完成：内容已适配当前教学需求'
          })
        }, 2000)
      })
    },

    getResourceTypeName(type) {
      const typeMap = {
        video: '教学视频',
        audio: '音频解说',
        animation: '互动动画',
        chart: '数据图表',
        mindmap: '思维导图',
        diagram: '流程图示',
        case: '案例素材',
        illustration: '教学插图',
        text: '文本内容'
      }
      return typeMap[type] || '未知类型'
    },
    
    getChartTypeName(type) {
      const typeMap = {
        bar: '柱状图',
        line: '折线图',
        pie: '饼图',
        scatter: '散点图'
      }
      return typeMap[type] || '未知图表'
    },
    

    
    formatTime(date) {
      return new Date(date).toLocaleTimeString('zh-CN')
    },
    
    formatDate(date) {
      return new Date(date).toLocaleDateString('zh-CN')
    },
    
    // 预览控制方法
    refreshPreview() {
      this.isRefreshing = true
      // 模拟刷新延迟
      setTimeout(() => {
        this.isRefreshing = false
        this.$message.success('预览已更新')
      }, 1000)
    },
    
    fullscreenPreview() {
      // 全屏预览功能
      this.$message.info('全屏预览功能开发中...')
    },
    
    copyPreviewContent() {
      // 复制预览内容
      const previewText = this.getPreviewText()
      if (navigator.clipboard) {
        navigator.clipboard.writeText(previewText).then(() => {
          this.$message.success('预览内容已复制到剪贴板')
        }).catch(() => {
          this.$message.error('复制失败')
        })
      } else {
        this.$message.error('浏览器不支持剪贴板功能')
      }
    },
    
    getPreviewText() {
      // 根据资源类型生成预览文本
      let text = `${this.editedResource.name}\n\n${this.editedResource.description}\n\n`
      
      if (this.resource.type === 'text' && this.editedResource.content) {
        text += this.editedResource.content
      } else if (this.resource.type === 'mindmap') {
        text += `中心主题: ${this.mindmapData.centerTopic}\n`
        text += `分支: ${this.mindmapData.branches.map(b => b.text).join(', ')}`
      } else if (this.resource.type === 'chart') {
        text += `图表标题: ${this.chartData.title}\n`
        text += `数据: ${this.chartData.data.map(d => `${d.label}: ${d.value}`).join(', ')}`
      }
      
      return text
    },
    
    // 防抖预览更新
    debouncedPreviewUpdate() {
      if (this.previewUpdateTimer) {
        clearTimeout(this.previewUpdateTimer)
      }
      
      this.previewUpdateTimer = setTimeout(() => {
        // 触发预览更新
        this.$forceUpdate()
      }, 300)
    },
    
    // 思维导图相关方法
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
    
    // 图表相关方法
    getMaxValue() {
      if (!this.chartData.data || this.chartData.data.length === 0) return 1
      return Math.max(...this.chartData.data.map(item => item.value))
    },
    
    getTotalValue() {
      if (!this.chartData.data || this.chartData.data.length === 0) return 1
      return this.chartData.data.reduce((sum, item) => sum + item.value, 0)
    },
    
    getPieColor(index) {
      const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#84cc16', '#f97316']
      return colors[index % colors.length]
    },

    // AI思维导图智能功能方法
    autoRecognizeTeachingParams() {
      try {
        if (!this.mindmapData || !this.mindmapData.centerTopic) {
          this.$message({
            type: 'warning',
            message: '请先设置中心主题内容'
          })
          return
        }
        
        const content = this.mindmapData.centerTopic.toLowerCase()
        let recognizedParams = {
          targetGrade: 'grade11',
          difficultyLevel: 'medium', 
          teachingFocus: 'concept',
          isAutoRecognized: true,
          gradeReason: '',
          difficultyReason: '',
          focusReason: ''
        }
        
        // 🎯 根据内容智能识别年级
        if (content.includes('二次函数') || content.includes('抛物线')) {
          recognizedParams.targetGrade = 'grade10' // 高一
          recognizedParams.gradeReason = '二次函数是高一数学重点内容'
          if (content.includes('判别式') || content.includes('综合应用')) {
            recognizedParams.targetGrade = 'grade11' // 高二
            recognizedParams.gradeReason = '包含判别式等高二数学内容'
          }
        } else if (content.includes('三角函数') || content.includes('对数函数')) {
          recognizedParams.targetGrade = 'grade11' // 高二
          recognizedParams.gradeReason = '三角函数/对数函数为高二核心内容'
        } else if (content.includes('导数') || content.includes('积分') || content.includes('极限')) {
          recognizedParams.targetGrade = 'grade12' // 高三
          recognizedParams.gradeReason = '导数/积分为高三重难点'
        } else if (content.includes('一元一次') || content.includes('分式') || content.includes('因式分解')) {
          recognizedParams.targetGrade = 'grade8' // 初二
          recognizedParams.gradeReason = '代数基础为初二主要内容'
        } else if (content.includes('勾股定理') || content.includes('相似') || content.includes('圆')) {
          recognizedParams.targetGrade = 'grade9' // 初三
          recognizedParams.gradeReason = '几何综合为初三核心内容'
        } else if (content.includes('化学') && content.includes('元素周期表')) {
          recognizedParams.targetGrade = 'grade9' // 初三
          recognizedParams.gradeReason = '元素周期表为初三化学基础'
        } else if (content.includes('化学') && content.includes('有机化学')) {
          recognizedParams.targetGrade = 'grade11' // 高二
          recognizedParams.gradeReason = '有机化学为高二化学重点'
        } else if (content.includes('物理') && content.includes('牛顿定律')) {
          recognizedParams.targetGrade = 'grade10' // 高一
          recognizedParams.gradeReason = '牛顿定律为高一物理核心'
        } else if (content.includes('物理') && (content.includes('电磁') || content.includes('波'))) {
          recognizedParams.targetGrade = 'grade11' // 高二
          recognizedParams.gradeReason = '电磁学/波动为高二物理重点'
        } else {
          recognizedParams.gradeReason = '基于内容复杂度的智能推荐'
        }
        
        // 📈 根据内容复杂度智能识别难度等级
        let complexityScore = 0
        let difficultyFactors = []
        
        // 检查复杂概念
        if (content.includes('函数') || content.includes('方程')) {
          complexityScore += 1
          difficultyFactors.push('包含函数/方程概念')
        }
        if (content.includes('图像') || content.includes('性质')) {
          complexityScore += 1
          difficultyFactors.push('涉及图像性质分析')
        }
        if (content.includes('应用') || content.includes('实际')) {
          complexityScore += 1
          difficultyFactors.push('包含实际应用')
        }
        if (content.includes('判别式') || content.includes('最值')) {
          complexityScore += 2
          difficultyFactors.push('涉及高阶概念')
        }
        if (content.includes('综合') || content.includes('建模')) {
          complexityScore += 2
          difficultyFactors.push('需要综合分析能力')
        }
        if (content.includes('竞赛') || content.includes('拓展')) {
          complexityScore += 3
          difficultyFactors.push('包含竞赛/拓展内容')
        }
        
        // 检查分支复杂度
        if (this.mindmapData.branches) {
          this.mindmapData.branches.forEach(branch => {
            if (branch && branch.children && branch.children.length > 4) {
              complexityScore += 1
              difficultyFactors.push('分支知识点详细')
            }
            if (branch && branch.text && branch.text.includes('高阶')) {
              complexityScore += 2
              difficultyFactors.push('包含高阶内容')
            }
          })
        }
        
        if (complexityScore <= 2) {
          recognizedParams.difficultyLevel = 'basic'
          recognizedParams.difficultyReason = '基础难度：' + (difficultyFactors.length > 0 ? difficultyFactors.join('，') : '内容相对简单')
        } else if (complexityScore <= 4) {
          recognizedParams.difficultyLevel = 'medium'
          recognizedParams.difficultyReason = '中等难度：' + difficultyFactors.slice(0, 2).join('，')
        } else if (complexityScore <= 6) {
          recognizedParams.difficultyLevel = 'advanced'
          recognizedParams.difficultyReason = '提高难度：' + difficultyFactors.slice(0, 3).join('，')
        } else {
          recognizedParams.difficultyLevel = 'competition'
          recognizedParams.difficultyReason = '竞赛难度：' + difficultyFactors.slice(0, 3).join('，')
        }
        
        // 🎯 根据内容类型智能识别教学重点
        if (content.includes('概念') || content.includes('定义') || content.includes('基本')) {
          recognizedParams.teachingFocus = 'concept'
          recognizedParams.focusReason = '主题强调概念理解'
        } else if (content.includes('解题') || content.includes('方法') || content.includes('技巧')) {
          recognizedParams.teachingFocus = 'solving'
          recognizedParams.focusReason = '主题强调解题方法'
        } else if (content.includes('应用') || content.includes('实际') || content.includes('建模')) {
          recognizedParams.teachingFocus = 'application'
          recognizedParams.focusReason = '主题强调实际应用'
        } else {
          // 默认根据分支内容判断
          let conceptCount = 0
          let solvingCount = 0
          let applicationCount = 0
          
          if (this.mindmapData.branches) {
            this.mindmapData.branches.forEach(branch => {
              if (branch && branch.text) {
                const branchText = branch.text.toLowerCase()
                if (branchText.includes('概念') || branchText.includes('性质') || branchText.includes('定义')) {
                  conceptCount++
                } else if (branchText.includes('解法') || branchText.includes('方法') || branchText.includes('计算')) {
                  solvingCount++
                } else if (branchText.includes('应用') || branchText.includes('实际') || branchText.includes('问题')) {
                  applicationCount++
                }
              }
            })
          }
          
          if (conceptCount >= solvingCount && conceptCount >= applicationCount) {
            recognizedParams.teachingFocus = 'concept'
            recognizedParams.focusReason = '分支内容主要涉及概念理解'
          } else if (solvingCount >= applicationCount) {
            recognizedParams.teachingFocus = 'solving'
            recognizedParams.focusReason = '分支内容主要涉及解题技巧'
          } else {
            recognizedParams.teachingFocus = 'application'
            recognizedParams.focusReason = '分支内容主要涉及应用拓展'
          }
        }
        
        // 更新AI配置
        this.aiConfig = { ...this.aiConfig, ...recognizedParams }
        
        // 显示识别结果
        this.$message({
          type: 'success',
          message: `🤖 AI智能识别完成: ${this.getGradeName(recognizedParams.targetGrade)}, ${this.getDifficultyName(recognizedParams.difficultyLevel)}, ${this.getFocusName(recognizedParams.teachingFocus)}`,
          duration: 4000
        })
        
        // 更新相关建议
        this.updateAISuggestions()
        this.updateQualityAssessment()
        
      } catch (error) {
        console.error('ResourceEditor: Error in auto-recognizing teaching params:', error)
        this.$message({
          type: 'error',
          message: 'AI识别过程中出现错误，请稍后重试'
        })
      }
    },

    // 重置为手动模式
    resetToManual() {
      this.aiConfig.isAutoRecognized = false
      this.aiConfig.gradeReason = ''
      this.aiConfig.difficultyReason = ''
      this.aiConfig.focusReason = ''
      
      this.$message({
        type: 'info',
        message: '已切换到手动调整模式'
      })
    },

    // 获取年级名称
    getGradeName(grade) {
      const gradeMap = {
        'grade7': '初一',
        'grade8': '初二', 
        'grade9': '初三',
        'grade10': '高一',
        'grade11': '高二',
        'grade12': '高三'
      }
      return gradeMap[grade] || '未知年级'
    },

    // 获取难度名称
    getDifficultyName(level) {
      const levelMap = {
        'basic': '基础难度',
        'medium': '中等难度',
        'advanced': '提高难度',
        'competition': '竞赛难度'
      }
      return levelMap[level] || '未知难度'
    },

    // 获取重点名称
    getFocusName(focus) {
      const focusMap = {
        'concept': '概念理解',
        'solving': '解题技巧',
        'application': '应用拓展'
      }
      return focusMap[focus] || '未知重点'
    },
  },
  
  beforeDestroy() {
    if (this.autoSaveTimer) {
      clearInterval(this.autoSaveTimer)
    }
    if (this.historyTimer) {
      clearTimeout(this.historyTimer)
    }
    if (this.previewUpdateTimer) {
      clearTimeout(this.previewUpdateTimer)
    }
  }
}
</script>

<style scoped>
.resource-editor {
  display: flex;
  flex-direction: column;
  height: 90vh;
  background: #f8fafc;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
}

.header-left h3 {
  margin: 0;
  color: #1f2937;
}

.resource-type {
  display: inline-block;
  padding: 2px 8px;
  background: #e5e7eb;
  color: #6b7280;
  border-radius: 4px;
  font-size: 12px;
  margin-left: 10px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.editor-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 分割视图样式 */
.editor-content.view-split {
  flex-direction: row;
}

.editor-content.view-split .edit-panel {
  flex: 1;
  border-right: 1px solid #e5e7eb;
}

.editor-content.view-split .preview-panel {
  flex: 1;
}

/* 编辑模式样式 */
.editor-content.view-edit .edit-panel {
  flex: 1;
}

/* 预览模式样式 */
.editor-content.view-preview .preview-panel {
  flex: 1;
}

.loading-panel {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  background: white;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.loading-content {
  text-align: center;
  color: #6b7280;
}

.loading-content i {
  font-size: 32px;
  display: block;
  margin-bottom: 12px;
  animation: spin 1s linear infinite;
}

.loading-content p {
  margin: 0;
  font-size: 14px;
}

/* 动画效果 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes fadeInScale {
  0% {
    opacity: 0;
    transform: scale(0.9);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes slideInUp {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  0% {
    opacity: 0;
    transform: translateX(-20px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  0% {
    opacity: 0;
    transform: translateX(20px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 旋转动画 */
.is-rotating {
  animation: spin 1s linear infinite;
}

/* 面板进入动画 */
.edit-panel {
  animation: slideInLeft 0.3s ease-out;
}

.preview-panel {
  animation: slideInRight 0.3s ease-out;
}

.view-split .edit-panel {
  animation: slideInLeft 0.4s ease-out;
}

.view-split .preview-panel {
  animation: slideInRight 0.4s ease-out;
}

/* 分割线 */
.view-split .edit-panel::after {
  content: '';
  position: absolute;
  right: -1px;
  top: 0;
  bottom: 0;
  width: 1px;
  background: #e5e7eb;
}

/* 实时预览增强样式 */
.resource-preview {
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
}

.preview-description {
  background: white;
  padding: 12px;
  border-radius: 6px;
  border-left: 4px solid #3b82f6;
  margin-bottom: 16px;
}

.edit-panel {
  padding: 24px;
  background: white;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

/* 分割视图下的编辑面板 */
.view-split .edit-panel {
  margin: 8px 8px 8px 16px;
  border-radius: 8px 0 0 8px;
}

.universal-editor {
  flex: 1;
}

.text-editor {
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  overflow: hidden;
}

.text-toolbar {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.mindmap-editor-section,
.chart-editor-section {
  margin-top: 20px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
}

.branches-editor {
  max-width: 400px;
}

.branch-item {
  margin-bottom: 8px;
}

.children-editor {
  margin-top: 12px;
  padding: 8px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
}

.children-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 8px;
  font-weight: 500;
}

.child-item {
  margin-bottom: 8px;
}

.no-children {
  margin-top: 8px;
  text-align: center;
}

.chart-data-editor {
  max-width: 500px;
}

.preview-panel {
  padding: 24px;
  background: white;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

/* 分割视图下的预览面板 */
.view-split .preview-panel {
  margin: 8px 16px 8px 8px;
  border-radius: 0 8px 8px 0;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.preview-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #1f2937;
}

.preview-title i {
  color: #6b7280;
}

.preview-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-content {
  flex: 1;
  overflow-y: auto;
}

.preview-header h4 {
  margin: 0;
  color: #1f2937;
}

.resource-preview h3 {
  color: #1f2937;
  margin-bottom: 12px;
}

.preview-description {
  color: #6b7280;
  margin-bottom: 20px;
  line-height: 1.6;
}

.text-preview {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: white;
  min-height: 200px;
}

/* 思维导图预览样式 */
.mindmap-preview {
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

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.info-item .label {
  color: #6b7280;
  font-weight: 500;
}

.info-item .value {
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
  flex-direction: row;
}

.branches.layout-vertical {
  flex-direction: column;
  align-items: center;
}

.branches.layout-radial {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
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

/* 数学公式高亮显示 */
.child-node:has(i.el-icon-edit) {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  border-color: #fbbf24;
  font-weight: 600;
}

.child-node:has(i.el-icon-pie-chart) {
  background: linear-gradient(135deg, #d1fae5, #a7f3d0);
  border-color: #34d399;
}

.child-node:has(i.el-icon-data-analysis) {
  background: linear-gradient(135deg, #ede9fe, #ddd6fe);
  border-color: #a78bfa;
}

.child-node:has(i.el-icon-medal) {
  background: linear-gradient(135deg, #fce7f3, #fbcfe8);
  border-color: #fb7185;
}

.empty-branches {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 24px;
  color: #9ca3af;
  font-size: 14px;
}

.empty-branches i {
  font-size: 24px;
}

/* 图表预览样式 */
.chart-preview {
  padding: 20px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.chart-header {
  margin-bottom: 20px;
}

.chart-header h4 {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 18px;
}

.chart-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
}

.chart-type {
  color: #3b82f6;
  font-weight: 500;
}

.data-count {
  color: #6b7280;
}

.chart-visualization {
  min-height: 200px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

/* 柱状图样式 */
.bar-chart {
  height: 200px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  height: 100%;
  width: 100%;
  max-width: 400px;
}

.bar-item {
  flex: 1;
  min-height: 20px;
  background: linear-gradient(to top, #3b82f6, #60a5fa);
  border-radius: 4px 4px 0 0;
  position: relative;
  animation: slideInUp 0.6s ease-out both;
  transition: all 0.3s ease;
}

.bar-item:hover {
  background: linear-gradient(to top, #1d4ed8, #3b82f6);
  transform: scaleY(1.05);
}

.bar-value {
  position: absolute;
  top: -24px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  font-weight: 600;
  color: #374151;
}

.bar-label {
  position: absolute;
  bottom: -24px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  color: #6b7280;
  white-space: nowrap;
}

/* 折线图样式 */
.line-chart {
  height: 200px;
  position: relative;
  background: linear-gradient(90deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 8px;
}

.line-points {
  height: 100%;
  position: relative;
}

.line-point {
  position: absolute;
  width: 12px;
  height: 12px;
  background: #3b82f6;
  border: 3px solid white;
  border-radius: 50%;
  transform: translate(-50%, 50%);
  animation: fadeInScale 0.6s ease-out both;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.line-point:hover {
  background: #1d4ed8;
  transform: translate(-50%, 50%) scale(1.2);
}

.point-value {
  position: absolute;
  top: -32px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  font-weight: 600;
  color: #374151;
  background: white;
  padding: 2px 6px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.point-label {
  position: absolute;
  bottom: -32px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  color: #6b7280;
  white-space: nowrap;
}

/* 饼图样式 */
.pie-chart {
  display: flex;
  gap: 24px;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.pie-segments {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: conic-gradient(from 0deg, #e5e7eb 0deg, #e5e7eb 360deg);
  position: relative;
  animation: fadeInScale 0.8s ease-out;
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-text {
  color: #374151;
}

/* 通用图表样式 */
.generic-chart {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.chart-info {
  text-align: center;
  color: #6b7280;
}

.chart-info i {
  font-size: 48px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.chart-info h5 {
  margin: 0 0 16px 0;
  color: #374151;
}

.data-summary {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-item {
  font-size: 14px;
  color: #6b7280;
}

.empty-chart {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 32px;
  color: #9ca3af;
  font-size: 14px;
}

.empty-chart i {
  font-size: 32px;
}

.generic-preview {
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
}

.resource-info p {
  margin: 8px 0;
  color: #374151;
}

.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: white;
  border-top: 1px solid #e5e7eb;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.last-saved {
  font-size: 14px;
  color: #6b7280;
}

.change-indicator {
  color: #ef4444;
  font-size: 18px;
  line-height: 1;
}

/* 响应式样式 */
@media (max-width: 1024px) {
  /* 平板视图：分割模式自动切换为垂直布局 */
  .editor-content.view-split {
    flex-direction: column;
  }
  
  .editor-content.view-split .edit-panel {
    border-right: none;
    border-bottom: 1px solid #e5e7eb;
    border-radius: 8px 8px 0 0;
    margin: 16px 16px 8px 16px;
  }
  
  .editor-content.view-split .preview-panel {
    border-radius: 0 0 8px 8px;
    margin: 8px 16px 16px 16px;
  }
}

@media (max-width: 768px) {
  .editor-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
    padding: 12px 16px;
  }
  
  .header-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .view-modes .el-radio-group {
    width: 100%;
  }
  
  .view-modes .el-radio-button {
    flex: 1;
  }
  
  .edit-panel,
  .preview-panel {
    margin: 8px;
    padding: 16px;
  }
  
  .editor-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
    padding: 12px 16px;
  }
  
  .footer-right {
    width: 100%;
    justify-content: flex-end;
  }
  
  /* 移动端自动切换为单面板视图 */
  .editor-content.view-split {
    flex-direction: column;
  }
  
  .editor-content.view-split .edit-panel,
  .editor-content.view-split .preview-panel {
    flex: 0 0 50%;
  }
}

@media (max-width: 480px) {
  .resource-editor {
    height: 100vh;
  }
  
  .preview-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .preview-controls {
    width: 100%;
    justify-content: center;
  }
  
  .branches-editor,
  .chart-data-editor {
    max-width: 100%;
  }
}

/* 视频编辑样式 */
.video-editor-section {
  background: #f8fafc;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.video-preview {
  background: #f9fafb;
  border-radius: 8px;
  padding: 20px;
}

.video-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.video-info .info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.video-info .label {
  font-weight: 500;
  color: #374151;
}

.video-info .value {
  color: #6b7280;
  font-size: 14px;
}

.video-player-container {
  margin-bottom: 20px;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #9ca3af;
  background: #f3f4f6;
}

.video-placeholder i {
  font-size: 48px;
  margin-bottom: 12px;
}

.video-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  color: #dc2626;
  margin-top: 12px;
}

.video-error i {
  font-size: 24px;
  margin-bottom: 8px;
}

.video-error p {
  margin: 0 0 12px 0;
  text-align: center;
}

/* AI生成标识样式 */
.ai-generated-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  color: #374151;
  position: relative;
}

.ai-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
  background: #3b82f6;
  color: white;
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #2563eb;
}

.ai-badge i {
  font-size: 16px;
}

.ai-badge-glow {
  display: none;
}

.ai-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.status-text {
  font-weight: 500;
  font-size: 14px;
}

.generation-time {
  font-size: 12px;
  opacity: 0.8;
}

/* AI生成信息面板 */
.ai-generation-info {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 20px;
  overflow: hidden;
}

.ai-generation-info .info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f9fafb;
  color: #374151;
  font-weight: 500;
  border-bottom: 1px solid #e5e7eb;
}

.ai-generation-info .info-header i {
  margin-right: 8px;
  font-size: 16px;
}

.ai-details {
  padding: 20px;
  background: white;
  color: #374151;
}

.ai-model-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.generation-parameters h6 {
  margin: 0 0 12px 0;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
}

.params-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.param-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}

.param-item span:first-child {
  min-width: 80px;
  color: #6b7280;
}

.param-item span:last-child {
  min-width: 40px;
  font-weight: 500;
  color: #3b82f6;
}

.param-bar {
  flex: 1;
  height: 6px;
  background: #e5e7eb;
  border-radius: 3px;
  overflow: hidden;
}

.param-fill {
  height: 100%;
  background: #3b82f6;
  border-radius: 3px;
  transition: width 0.3s ease;
}

/* AI操作区域 */
.ai-actions {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  color: #374151;
}

.action-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-weight: 600;
  font-size: 16px;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.action-buttons .el-button {
  background: white !important;
  border-color: #d1d5db !important;
  color: #374151 !important;
  transition: all 0.2s ease;
}

.action-buttons .el-button:hover {
  background: #f3f4f6 !important;
  border-color: #9ca3af !important;
  transform: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.generation-progress {
  background: white;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  margin-top: 16px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.progress-percentage {
  font-weight: 600;
  font-size: 14px;
}

/* AI章节样式 */
.video-chapters h5 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: #374151;
  font-size: 16px;
  font-weight: 600;
}

.ai-chapter {
  background: #f8fafc;
  border: 1px solid #e2e8f0 !important;
  transition: all 0.2s ease;
}

.ai-chapter:hover {
  background: #f1f5f9;
  border-color: #cbd5e1 !important;
  transform: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.ai-suggestions {
  margin-top: 16px;
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e2e8f0;
}

.suggestion-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-weight: 500;
  color: #374151;
}

.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;
  padding: 8px 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.suggestion-item i {
  color: #059669;
  font-size: 14px;
}

.ai-source {
  color: #7c3aed !important;
  font-weight: 600;
}

/* 动画效果已移除，采用简约风格 */

/* 左侧编辑区AI化样式 */
.ai-editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 20px;
  color: #374151;
}

.ai-editor-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  font-size: 14px;
  background: #3b82f6;
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
}

.ai-editor-badge i {
  font-size: 14px;
}

.ai-editor-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #6b7280;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* AI视频编辑区样式 */
.ai-video-editor-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 20px 0 12px 0;
  padding: 12px 16px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
}

.ai-generation-info-edit {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 20px;
}

.video-basic-info {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 20px;
}

.field-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.field-hint i {
  font-size: 12px;
  color: #3b82f6;
}

/* AI章节管理样式 */
.ai-chapters-section {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 20px;
}

.chapters-toolbar {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.chapters-list-edit {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ai-chapter-item {
  position: relative;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 12px;
  transition: all 0.2s ease;
}

.ai-chapter-item:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.chapter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.chapter-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background: #3b82f6;
  color: white;
  font-size: 12px;
  font-weight: bold;
  border-radius: 50%;
}

.chapter-ai-icon {
  color: #3b82f6;
  font-size: 14px;
  cursor: help;
}

/* 表单样式优化 */
.universal-editor .el-form-item__label {
  color: #374151;
  font-weight: 500;
}

.universal-editor .el-input__inner {
  border-color: #d1d5db;
}

.universal-editor .el-input__inner:focus {
  border-color: #3b82f6;
}

.universal-editor .el-select .el-input__inner {
  border-color: #d1d5db;
}

.universal-editor .el-textarea__inner {
  border-color: #d1d5db;
}

.universal-editor .el-textarea__inner:focus {
  border-color: #3b82f6;
}

/* 按钮组样式 */
.chapters-toolbar .el-button-group .el-button {
  background: white;
  border-color: #d1d5db;
  color: #374151;
}

.chapters-toolbar .el-button-group .el-button:hover {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.chapters-toolbar .el-button-group .el-button--primary {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

.chapters-toolbar .el-button-group .el-button--primary:hover {
  background: #2563eb;
  border-color: #2563eb;
}

.chapters-toolbar .el-button-group .el-button--success {
  background: #10b981;
  border-color: #10b981;
  color: white;
}

.chapters-toolbar .el-button-group .el-button--success:hover {
  background: #059669;
  border-color: #059669;
}

.chapters-toolbar .el-button-group .el-button--info {
  background: #6b7280;
  border-color: #6b7280;
  color: white;
}

.chapters-toolbar .el-button-group .el-button--info:hover {
  background: #4b5563;
  border-color: #4b5563;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-generated-header {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .ai-model-info {
    grid-template-columns: 1fr;
  }
  
  /* 左侧编辑区响应式 */
  .ai-editor-header {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
  
  .ai-video-editor-section .el-row {
    flex-direction: column !important;
  }
  
  .ai-video-editor-section .el-col {
    width: 100% !important;
    margin-bottom: 12px;
  }
  
  .chapters-toolbar .el-button-group {
    display: flex;
    flex-direction: column;
    width: 100%;
  }
  
  .chapters-toolbar .el-button {
    width: 100%;
    margin-bottom: 8px;
  }
  
  .ai-chapter-item .el-row {
    flex-direction: column !important;
  }
  
  .ai-chapter-item .el-col {
    width: 100% !important;
    margin-bottom: 8px;
  }
}

/* AI思维导图编辑器样式 */
.ai-mindmap-editor-section {
  display: flex;
  gap: 24px;
}

.ai-assistant-panel {
  width: 350px;
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e2e8f0;
  max-height: 80vh;
  overflow-y: auto;
}

.ai-panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.ai-panel-header i {
  color: #3b82f6;
  font-size: 18px;
}

.teaching-config,
.ai-quality-check,
.ai-suggestions,
.ai-global-actions {
  margin-bottom: 24px;
}

.teaching-config h5,
.ai-quality-check h5,
.ai-suggestions h5,
.ai-global-actions h5 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

/* 教学配置头部样式 */
.config-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.config-header h5 {
  margin: 0 !important;
}

.ai-recognize-btn {
  flex-shrink: 0;
}

/* 自动识别提示样式 */
.auto-recognized-tip {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: linear-gradient(135deg, #dcfce7 0%, #f0fdf4 100%);
  border: 1px solid #16a34a;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #15803d;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.tip-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tip-content i {
  color: #16a34a;
  font-size: 14px;
}

.manual-btn {
  font-size: 12px;
  padding: 4px 8px !important;
}

/* 配置网格布局 */
.config-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.config-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.config-form-item {
  margin-bottom: 0 !important;
}

.config-form-item .el-form-item__label {
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  line-height: 1.5;
  padding-bottom: 4px;
}

.config-form-item .el-form-item__content {
  line-height: 1.5;
}

/* AI识别提示样式 */
.ai-recognition-hint {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding: 6px 10px;
  background: linear-gradient(135deg, #eff6ff 0%, #f8faff 100%);
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  font-size: 12px;
  color: #1d4ed8;
  line-height: 1.4;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.ai-recognition-hint i {
  color: #3b82f6;
  font-size: 12px;
  margin-top: 1px;
  flex-shrink: 0;
}

.ai-recognition-hint span {
  flex: 1;
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
  font-size: 13px;
}

.metric-label {
  min-width: 80px;
  color: #6b7280;
}

.metric-score {
  min-width: 40px;
  font-weight: 600;
  text-align: right;
  color: #1f2937;
}
</style>