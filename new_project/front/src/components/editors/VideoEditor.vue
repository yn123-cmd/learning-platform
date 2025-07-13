<template>
  <div class="video-editor">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-group">
        <span class="group-label">播放控制</span>
        <el-button-group size="mini">
          <el-button @click="playPause">
            <i :class="isPlaying ? 'el-icon-video-pause' : 'el-icon-video-play'"></i>
          </el-button>
          <el-button @click="stop">
            <i class="el-icon-switch-button"></i>
          </el-button>
        </el-button-group>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">编辑工具</span>
        <el-button-group size="mini">
          <el-button @click="showCutDialog = true">
            <i class="el-icon-scissors"></i> 剪切
          </el-button>
          <el-button @click="showSubtitleDialog = true">
            <i class="el-icon-chat-dot-square"></i> 字幕
          </el-button>
          <el-button @click="showEffectDialog = true">
            <i class="el-icon-magic-stick"></i> 特效
          </el-button>
        </el-button-group>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">音量</span>
        <el-slider v-model="volume" :min="0" :max="100" style="width: 100px" @change="updateVolume"></el-slider>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">速度</span>
        <el-select v-model="playbackRate" size="mini" @change="updatePlaybackRate" style="width: 80px">
          <el-option label="0.5x" :value="0.5"></el-option>
          <el-option label="1x" :value="1"></el-option>
          <el-option label="1.5x" :value="1.5"></el-option>
          <el-option label="2x" :value="2"></el-option>
        </el-select>
      </div>
    </div>
    
    <!-- 视频播放区域 -->
    <div class="video-container">
      <video 
        ref="video"
        :src="videoUrl"
        @loadedmetadata="onVideoLoaded"
        @timeupdate="onTimeUpdate"
        @ended="onVideoEnded"
        controls>
      </video>
      
      <!-- 字幕叠加层 -->
      <div class="subtitle-overlay" v-if="currentSubtitle">
        <div class="subtitle-text" :style="subtitleStyle">
          {{ currentSubtitle.text }}
        </div>
      </div>
    </div>
    
    <!-- 时间轴 -->
    <div class="timeline">
      <div class="timeline-header">
        <span>时间轴</span>
        <div class="timeline-controls">
          <el-button size="mini" @click="zoomIn">
            <i class="el-icon-zoom-in"></i>
          </el-button>
          <el-button size="mini" @click="zoomOut">
            <i class="el-icon-zoom-out"></i>
          </el-button>
        </div>
      </div>
      
      <div class="timeline-content" ref="timeline">
        <!-- 时间刻度 -->
        <div class="time-ruler">
          <div 
            v-for="(mark, index) in timeMarks" 
            :key="index"
            class="time-mark"
            :style="{ left: mark.position + 'px' }">
            <span>{{ formatTime(mark.time) }}</span>
          </div>
        </div>
        
        <!-- 视频轨道 -->
        <div class="track video-track">
          <div class="track-label">视频</div>
          <div class="track-content">
            <div 
              class="video-clip"
              :style="{ 
                left: startTime * timeScale + 'px',
                width: (endTime - startTime) * timeScale + 'px'
              }">
              <div class="clip-content">视频片段</div>
            </div>
          </div>
        </div>
        
        <!-- 字幕轨道 -->
        <div class="track subtitle-track">
          <div class="track-label">字幕</div>
          <div class="track-content">
            <div 
              v-for="subtitle in subtitles"
              :key="subtitle.id"
              class="subtitle-clip"
              :style="{ 
                left: subtitle.startTime * timeScale + 'px',
                width: (subtitle.endTime - subtitle.startTime) * timeScale + 'px'
              }"
              @click="selectSubtitle(subtitle)">
              <div class="clip-content">{{ subtitle.text }}</div>
            </div>
          </div>
        </div>
        
        <!-- 播放头 -->
        <div 
          class="playhead"
          :style="{ left: currentTime * timeScale + 'px' }"
          @mousedown="startDragPlayhead">
        </div>
      </div>
    </div>
    
    <!-- 属性面板 -->
    <div class="properties-panel">
      <div class="panel-header">
        <h4>视频属性</h4>
      </div>
      
      <div class="panel-content">
        <el-form size="mini">
          <el-form-item label="总时长">
            <span>{{ formatTime(duration) }}</span>
          </el-form-item>
          
          <el-form-item label="当前时间">
            <span>{{ formatTime(currentTime) }}</span>
          </el-form-item>
          
          <el-form-item label="剪切范围">
            <el-row :gutter="10">
              <el-col :span="12">
                <el-input-number 
                  v-model="startTime" 
                  :min="0" 
                  :max="duration"
                  :precision="2"
                  @change="updateClipRange">
                </el-input-number>
              </el-col>
              <el-col :span="12">
                <el-input-number 
                  v-model="endTime" 
                  :min="startTime" 
                  :max="duration"
                  :precision="2"
                  @change="updateClipRange">
                </el-input-number>
              </el-col>
            </el-row>
          </el-form-item>
          
          <el-form-item v-if="selectedSubtitle" label="字幕设置">
            <el-input v-model="selectedSubtitle.text" @input="updateSubtitle" placeholder="字幕文本"></el-input>
            <div style="margin-top: 8px;">
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-input-number 
                    v-model="selectedSubtitle.startTime" 
                    :min="0" 
                    :max="duration"
                    :precision="2"
                    size="mini">
                  </el-input-number>
                </el-col>
                <el-col :span="12">
                  <el-input-number 
                    v-model="selectedSubtitle.endTime" 
                    :min="selectedSubtitle.startTime" 
                    :max="duration"
                    :precision="2"
                    size="mini">
                  </el-input-number>
                </el-col>
              </el-row>
            </div>
            <el-button size="mini" type="danger" @click="deleteSubtitle" style="margin-top: 8px;">
              删除字幕
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <!-- 剪切对话框 -->
    <el-dialog title="视频剪切" :visible.sync="showCutDialog" width="400px">
      <el-form>
        <el-form-item label="开始时间">
          <el-input-number v-model="cutStartTime" :min="0" :max="duration" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-input-number v-model="cutEndTime" :min="cutStartTime" :max="duration" :precision="2"></el-input-number>
        </el-form-item>
      </el-form>
      
      <div slot="footer">
        <el-button @click="showCutDialog = false">取消</el-button>
        <el-button type="primary" @click="applyCut">应用剪切</el-button>
      </div>
    </el-dialog>
    
    <!-- 字幕对话框 -->
    <el-dialog title="添加字幕" :visible.sync="showSubtitleDialog" width="500px">
      <el-form>
        <el-form-item label="字幕文本">
          <el-input v-model="newSubtitleText" type="textarea" rows="3"></el-input>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-input-number v-model="newSubtitleStart" :min="0" :max="duration" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="结束时间">
          <el-input-number v-model="newSubtitleEnd" :min="newSubtitleStart" :max="duration" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-slider v-model="newSubtitleSize" :min="12" :max="48"></el-slider>
        </el-form-item>
        <el-form-item label="字体颜色">
          <el-color-picker v-model="newSubtitleColor"></el-color-picker>
        </el-form-item>
      </el-form>
      
      <div slot="footer">
        <el-button @click="showSubtitleDialog = false">取消</el-button>
        <el-button type="primary" @click="addSubtitle">添加字幕</el-button>
      </div>
    </el-dialog>
    
    <!-- 特效对话框 -->
    <el-dialog title="视频特效" :visible.sync="showEffectDialog" width="400px">
      <el-form>
        <el-form-item label="特效类型">
          <el-select v-model="selectedEffect" style="width: 100%">
            <el-option label="无特效" value="none"></el-option>
            <el-option label="模糊" value="blur"></el-option>
            <el-option label="黑白" value="grayscale"></el-option>
            <el-option label="复古" value="sepia"></el-option>
            <el-option label="反色" value="invert"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="特效强度">
          <el-slider v-model="effectIntensity" :min="0" :max="100"></el-slider>
        </el-form-item>
      </el-form>
      
      <div slot="footer">
        <el-button @click="showEffectDialog = false">取消</el-button>
        <el-button type="primary" @click="applyEffect">应用特效</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'VideoEditor',
  props: {
    resource: {
      type: Object,
      required: true
    },
    editorData: {
      type: Object,
      required: true
    }
  },
  
  data() {
    return {
      // 视频基本属性
      videoUrl: '',
      duration: 0,
      currentTime: 0,
      volume: 50,
      playbackRate: 1,
      isPlaying: false,
      
      // 剪切范围
      startTime: 0,
      endTime: 0,
      
      // 字幕数据
      subtitles: [],
      selectedSubtitle: null,
      currentSubtitle: null,
      
      // 时间轴
      timeScale: 50, // 每秒对应的像素数
      timeMarks: [],
      
      // 对话框
      showCutDialog: false,
      showSubtitleDialog: false,
      showEffectDialog: false,
      
      // 剪切设置
      cutStartTime: 0,
      cutEndTime: 0,
      
      // 新字幕设置
      newSubtitleText: '',
      newSubtitleStart: 0,
      newSubtitleEnd: 0,
      newSubtitleSize: 16,
      newSubtitleColor: '#ffffff',
      
      // 特效设置
      selectedEffect: 'none',
      effectIntensity: 50,
      
      // 拖拽状态
      isDraggingPlayhead: false
    }
  },
  
  computed: {
    subtitleStyle() {
      if (!this.currentSubtitle) return {}
      
      return {
        fontSize: `${this.currentSubtitle.size || 16}px`,
        color: this.currentSubtitle.color || '#ffffff',
        textShadow: '2px 2px 4px rgba(0,0,0,0.8)'
      }
    }
  },
  
  watch: {
    editorData: {
      immediate: true,
      deep: true,
      handler(newData) {
        if (newData && newData.videoData) {
          this.loadVideoData(newData.videoData)
        } else if (this.resource && this.resource.url) {
          this.videoUrl = this.resource.url
        }
      }
    }
  },
  
  mounted() {
    this.initEventListeners()
  },
  
  beforeDestroy() {
    this.removeEventListeners()
  },
  
  methods: {
    // 初始化事件监听
    initEventListeners() {
      document.addEventListener('mousemove', this.onMouseMove)
      document.addEventListener('mouseup', this.onMouseUp)
    },
    
    removeEventListeners() {
      document.removeEventListener('mousemove', this.onMouseMove)
      document.removeEventListener('mouseup', this.onMouseUp)
    },
    
    // 加载视频数据
    loadVideoData(data) {
      this.videoUrl = data.url || this.resource.url
      this.startTime = data.startTime || 0
      this.endTime = data.endTime || 0
      this.subtitles = data.subtitles || []
      this.volume = data.volume || 50
      this.playbackRate = data.playbackRate || 1
      this.selectedEffect = data.effect || 'none'
      this.effectIntensity = data.effectIntensity || 50
    },
    
    // 视频加载完成
    onVideoLoaded() {
      const video = this.$refs.video
      this.duration = video.duration
      this.endTime = this.endTime || this.duration
      this.generateTimeMarks()
      this.updateVolume()
      this.updatePlaybackRate()
      this.emitChange()
    },
    
    // 时间更新
    onTimeUpdate() {
      const video = this.$refs.video
      this.currentTime = video.currentTime
      this.updateCurrentSubtitle()
    },
    
    // 视频结束
    onVideoEnded() {
      this.isPlaying = false
    },
    
    // 播放控制
    playPause() {
      const video = this.$refs.video
      if (this.isPlaying) {
        video.pause()
      } else {
        video.play()
      }
      this.isPlaying = !this.isPlaying
    },
    
    stop() {
      const video = this.$refs.video
      video.pause()
      video.currentTime = this.startTime
      this.isPlaying = false
    },
    
    updateVolume() {
      const video = this.$refs.video
      if (video) {
        video.volume = this.volume / 100
        this.emitChange()
      }
    },
    
    updatePlaybackRate() {
      const video = this.$refs.video
      if (video) {
        video.playbackRate = this.playbackRate
        this.emitChange()
      }
    },
    
    // 生成时间刻度
    generateTimeMarks() {
      this.timeMarks = []
      const interval = 5 // 每5秒一个刻度
      
      for (let time = 0; time <= this.duration; time += interval) {
        this.timeMarks.push({
          time,
          position: time * this.timeScale
        })
      }
    },
    
    // 格式化时间显示
    formatTime(seconds) {
      const minutes = Math.floor(seconds / 60)
      const remainingSeconds = Math.floor(seconds % 60)
      return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
    },
    
    // 时间轴缩放
    zoomIn() {
      this.timeScale = Math.min(this.timeScale * 1.5, 200)
      this.generateTimeMarks()
    },
    
    zoomOut() {
      this.timeScale = Math.max(this.timeScale / 1.5, 10)
      this.generateTimeMarks()
    },
    
    // 播放头拖拽
    startDragPlayhead(e) {
      this.isDraggingPlayhead = true
      e.preventDefault()
    },
    
    onMouseMove(e) {
      if (this.isDraggingPlayhead) {
        const timeline = this.$refs.timeline
        const rect = timeline.getBoundingClientRect()
        const x = e.clientX - rect.left
        const time = Math.max(0, Math.min(x / this.timeScale, this.duration))
        
        const video = this.$refs.video
        video.currentTime = time
        this.currentTime = time
      }
    },
    
    onMouseUp() {
      this.isDraggingPlayhead = false
    },
    
    // 剪切功能
    applyCut() {
      this.startTime = this.cutStartTime
      this.endTime = this.cutEndTime
      this.showCutDialog = false
      this.emitChange()
    },
    
    updateClipRange() {
      this.emitChange()
    },
    
    // 字幕功能
    addSubtitle() {
      const subtitle = {
        id: Date.now(),
        text: this.newSubtitleText,
        startTime: this.newSubtitleStart,
        endTime: this.newSubtitleEnd,
        size: this.newSubtitleSize,
        color: this.newSubtitleColor
      }
      
      this.subtitles.push(subtitle)
      this.subtitles.sort((a, b) => a.startTime - b.startTime)
      
      this.showSubtitleDialog = false
      this.resetSubtitleForm()
      this.emitChange()
    },
    
    selectSubtitle(subtitle) {
      this.selectedSubtitle = subtitle
    },
    
    updateSubtitle() {
      this.emitChange()
    },
    
    deleteSubtitle() {
      if (this.selectedSubtitle) {
        const index = this.subtitles.findIndex(sub => sub.id === this.selectedSubtitle.id)
        if (index > -1) {
          this.subtitles.splice(index, 1)
          this.selectedSubtitle = null
          this.emitChange()
        }
      }
    },
    
    updateCurrentSubtitle() {
      this.currentSubtitle = this.subtitles.find(subtitle => 
        this.currentTime >= subtitle.startTime && this.currentTime <= subtitle.endTime
      ) || null
    },
    
    resetSubtitleForm() {
      this.newSubtitleText = ''
      this.newSubtitleStart = Math.floor(this.currentTime)
      this.newSubtitleEnd = Math.floor(this.currentTime) + 3
      this.newSubtitleSize = 16
      this.newSubtitleColor = '#ffffff'
    },
    
    // 特效功能
    applyEffect() {
      const video = this.$refs.video
      let filterString = ''
      
      const intensity = this.effectIntensity / 100
      
      switch (this.selectedEffect) {
        case 'blur':
          filterString = `blur(${intensity * 5}px)`
          break
        case 'grayscale':
          filterString = `grayscale(${intensity})`
          break
        case 'sepia':
          filterString = `sepia(${intensity})`
          break
        case 'invert':
          filterString = `invert(${intensity})`
          break
        default:
          filterString = 'none'
      }
      
      video.style.filter = filterString
      this.showEffectDialog = false
      this.emitChange()
    },
    
    // 发送变化事件
    emitChange() {
      const videoData = {
        url: this.videoUrl,
        duration: this.duration,
        startTime: this.startTime,
        endTime: this.endTime,
        subtitles: this.subtitles,
        volume: this.volume,
        playbackRate: this.playbackRate,
        effect: this.selectedEffect,
        effectIntensity: this.effectIntensity
      }
      
      this.$emit('content-change', {
        videoData,
        clipDuration: this.endTime - this.startTime,
        subtitleCount: this.subtitles.length
      })
    }
  }
}
</script>

<style scoped>
.video-editor {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f8fafc;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  flex-wrap: wrap;
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.group-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
  margin-right: 8px;
}

.video-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
  position: relative;
}

video {
  max-width: 100%;
  max-height: 100%;
}

.subtitle-overlay {
  position: absolute;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  pointer-events: none;
}

.subtitle-text {
  background: rgba(0, 0, 0, 0.7);
  padding: 8px 16px;
  border-radius: 4px;
  text-align: center;
}

.timeline {
  height: 200px;
  background: white;
  border-top: 1px solid #e5e7eb;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.timeline-content {
  position: relative;
  height: calc(100% - 40px);
  overflow-x: auto;
  overflow-y: hidden;
}

.time-ruler {
  height: 30px;
  position: relative;
  background: #f3f4f6;
  border-bottom: 1px solid #e5e7eb;
}

.time-mark {
  position: absolute;
  top: 0;
  height: 100%;
  border-left: 1px solid #d1d5db;
  padding-left: 4px;
}

.time-mark span {
  font-size: 11px;
  color: #6b7280;
}

.track {
  height: 60px;
  display: flex;
  border-bottom: 1px solid #e5e7eb;
}

.track-label {
  width: 100px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  background: #f9fafb;
  border-right: 1px solid #e5e7eb;
  font-size: 12px;
  font-weight: 500;
  color: #374151;
}

.track-content {
  flex: 1;
  position: relative;
  background: #fff;
}

.video-clip, .subtitle-clip {
  position: absolute;
  top: 8px;
  height: 44px;
  border-radius: 4px;
  cursor: pointer;
}

.video-clip {
  background: #3b82f6;
  border: 1px solid #2563eb;
}

.subtitle-clip {
  background: #10b981;
  border: 1px solid #059669;
}

.clip-content {
  padding: 4px 8px;
  color: white;
  font-size: 11px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.playhead {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #ef4444;
  cursor: ew-resize;
  z-index: 10;
}

.playhead::before {
  content: '';
  position: absolute;
  top: -5px;
  left: -6px;
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 10px solid #ef4444;
}

.properties-panel {
  width: 100%;
  max-height: 300px;
  background: white;
  border-top: 1px solid #e5e7eb;
  overflow-y: auto;
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.panel-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.panel-content {
  padding: 16px;
}

/* 响应式样式 */
@media (max-width: 768px) {
  .toolbar {
    padding: 8px 12px;
    gap: 8px;
  }
  
  .toolbar-group {
    gap: 4px;
  }
  
  .group-label {
    display: none;
  }
  
  .track-label {
    width: 60px;
    padding: 0 8px;
  }
}
</style> 