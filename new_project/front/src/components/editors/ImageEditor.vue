<template>
  <div class="image-editor">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-group">
        <span class="group-label">基础工具</span>
        <el-button-group size="mini">
          <el-button :class="{ active: activeTool === 'select' }" @click="setTool('select')">
            <i class="el-icon-mouse"></i> 选择
          </el-button>
          <el-button :class="{ active: activeTool === 'text' }" @click="setTool('text')">
            <i class="el-icon-edit"></i> 文字
          </el-button>
          <el-button :class="{ active: activeTool === 'arrow' }" @click="setTool('arrow')">
            <i class="el-icon-right"></i> 箭头
          </el-button>
          <el-button :class="{ active: activeTool === 'rect' }" @click="setTool('rect')">
            <i class="el-icon-crop"></i> 矩形
          </el-button>
        </el-button-group>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">样式</span>
        <el-color-picker v-model="strokeColor" size="mini" @change="updateStyle"></el-color-picker>
        <el-slider v-model="strokeWidth" :min="1" :max="10" style="width: 100px" @change="updateStyle"></el-slider>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">滤镜</span>
        <el-select v-model="selectedFilter" size="mini" @change="applyFilter" style="width: 120px">
          <el-option label="无滤镜" value="none"></el-option>
          <el-option label="模糊" value="blur"></el-option>
          <el-option label="锐化" value="sharpen"></el-option>
          <el-option label="怀旧" value="sepia"></el-option>
          <el-option label="黑白" value="grayscale"></el-option>
        </el-select>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">操作</span>
        <el-button-group size="mini">
          <el-button @click="undo" :disabled="!canUndo">
            <i class="el-icon-refresh-left"></i>
          </el-button>
          <el-button @click="redo" :disabled="!canRedo">
            <i class="el-icon-refresh-right"></i>
          </el-button>
          <el-button @click="clearAnnotations">
            <i class="el-icon-delete"></i> 清除标注
          </el-button>
        </el-button-group>
      </div>
    </div>
    
    <!-- 编辑区域 -->
    <div class="editor-content">
      <div class="canvas-container">
        <canvas 
          ref="canvas" 
          @mousedown="startDrawing"
          @mousemove="draw"
          @mouseup="stopDrawing"
          @click="handleCanvasClick">
        </canvas>
        
        <!-- 原始图像 -->
        <img 
          ref="image" 
          :src="imageUrl" 
          @load="onImageLoad"
          style="display: none;">
      </div>
      
      <!-- 属性面板 -->
      <div class="properties-panel">
        <div class="panel-header">
          <h4>属性设置</h4>
        </div>
        
        <div class="panel-content">
          <el-form size="mini">
            <el-form-item label="图像尺寸">
              <el-row :gutter="10">
                <el-col :span="12">
                  <el-input-number v-model="imageWidth" @change="resizeImage" :min="100" :max="2000">
                  </el-input-number>
                </el-col>
                <el-col :span="12">
                  <el-input-number v-model="imageHeight" @change="resizeImage" :min="100" :max="2000">
                  </el-input-number>
                </el-col>
              </el-row>
            </el-form-item>
            
            <el-form-item label="透明度">
              <el-slider v-model="imageOpacity" :min="0" :max="100" @change="updateOpacity"></el-slider>
            </el-form-item>
            
            <el-form-item label="旋转角度">
              <el-slider v-model="rotationAngle" :min="0" :max="360" @change="rotateImage"></el-slider>
            </el-form-item>
            
            <el-form-item v-if="selectedAnnotation" label="选中标注">
              <el-input v-model="selectedAnnotation.text" @input="updateAnnotation" placeholder="标注文字"></el-input>
              <el-button size="mini" type="danger" @click="deleteAnnotation" style="margin-top: 8px;">
                删除标注
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
    
    <!-- 文字标注对话框 -->
    <el-dialog title="添加文字标注" :visible.sync="textDialog" width="400px">
      <el-form>
        <el-form-item label="标注文字">
          <el-input v-model="newAnnotationText" placeholder="请输入标注内容"></el-input>
        </el-form-item>
        <el-form-item label="字体大小">
          <el-slider v-model="newAnnotationSize" :min="12" :max="48"></el-slider>
        </el-form-item>
      </el-form>
      
      <div slot="footer">
        <el-button @click="textDialog = false">取消</el-button>
        <el-button type="primary" @click="addTextAnnotation">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ImageEditor',
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
      // 工具状态
      activeTool: 'select',
      strokeColor: '#ff0000',
      strokeWidth: 2,
      selectedFilter: 'none',
      
      // 图像属性
      imageUrl: '',
      imageWidth: 400,
      imageHeight: 300,
      imageOpacity: 100,
      rotationAngle: 0,
      
      // 画布状态
      isDrawing: false,
      startX: 0,
      startY: 0,
      
      // 标注数据
      annotations: [],
      selectedAnnotation: null,
      
      // 历史记录
      history: [],
      historyIndex: -1,
      
      // 对话框
      textDialog: false,
      newAnnotationText: '',
      newAnnotationSize: 16,
      pendingTextPosition: null
    }
  },
  
  computed: {
    canUndo() {
      return this.historyIndex > 0
    },
    
    canRedo() {
      return this.historyIndex < this.history.length - 1
    }
  },
  
  watch: {
    editorData: {
      immediate: true,
      deep: true,
      handler(newData) {
        if (newData && newData.imageData) {
          this.loadImageData(newData.imageData)
        } else if (this.resource && this.resource.url) {
          this.imageUrl = this.resource.url
        }
      }
    }
  },
  
  mounted() {
    this.initCanvas()
  },
  
  methods: {
    // 初始化画布
    initCanvas() {
      const canvas = this.$refs.canvas
      if (canvas) {
        canvas.width = 800
        canvas.height = 600
        this.redrawCanvas()
      }
    },
    
    // 加载图像数据
    loadImageData(data) {
      this.imageUrl = data.url || this.resource.url
      this.imageWidth = data.width || 400
      this.imageHeight = data.height || 300
      this.imageOpacity = data.opacity || 100
      this.rotationAngle = data.rotation || 0
      this.annotations = data.annotations || []
      this.selectedFilter = data.filter || 'none'
      
      this.$nextTick(() => {
        this.redrawCanvas()
      })
    },
    
    // 图像加载完成
    onImageLoad() {
      const img = this.$refs.image
      if (img) {
        this.imageWidth = img.naturalWidth
        this.imageHeight = img.naturalHeight
        this.redrawCanvas()
        this.saveToHistory()
      }
    },
    
    // 重绘画布
    redrawCanvas() {
      const canvas = this.$refs.canvas
      const ctx = canvas.getContext('2d')
      const img = this.$refs.image
      
      if (!img || !img.complete) return
      
      // 清空画布
      ctx.clearRect(0, 0, canvas.width, canvas.height)
      
      // 设置透明度
      ctx.globalAlpha = this.imageOpacity / 100
      
      // 保存状态并应用旋转
      ctx.save()
      ctx.translate(canvas.width / 2, canvas.height / 2)
      ctx.rotate((this.rotationAngle * Math.PI) / 180)
      
      // 绘制图像
      ctx.drawImage(
        img,
        -this.imageWidth / 2,
        -this.imageHeight / 2,
        this.imageWidth,
        this.imageHeight
      )
      
      ctx.restore()
      
      // 应用滤镜
      this.applyCanvasFilter(ctx)
      
      // 绘制标注
      this.drawAnnotations(ctx)
      
      // 重置透明度
      ctx.globalAlpha = 1
    },
    
    // 应用画布滤镜
    applyCanvasFilter(ctx) {
      if (this.selectedFilter === 'none') return
      
      const canvas = this.$refs.canvas
      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
      const data = imageData.data
      
      switch (this.selectedFilter) {
        case 'grayscale':
          for (let i = 0; i < data.length; i += 4) {
            const gray = data[i] * 0.299 + data[i + 1] * 0.587 + data[i + 2] * 0.114
            data[i] = gray
            data[i + 1] = gray
            data[i + 2] = gray
          }
          break
        case 'sepia':
          for (let i = 0; i < data.length; i += 4) {
            const r = data[i]
            const g = data[i + 1]
            const b = data[i + 2]
            data[i] = Math.min(255, r * 0.393 + g * 0.769 + b * 0.189)
            data[i + 1] = Math.min(255, r * 0.349 + g * 0.686 + b * 0.168)
            data[i + 2] = Math.min(255, r * 0.272 + g * 0.534 + b * 0.131)
          }
          break
      }
      
      ctx.putImageData(imageData, 0, 0)
    },
    
    // 绘制标注
    drawAnnotations(ctx) {
      this.annotations.forEach(annotation => {
        ctx.save()
        
        if (annotation === this.selectedAnnotation) {
          ctx.strokeStyle = '#0066cc'
          ctx.lineWidth = 3
        } else {
          ctx.strokeStyle = annotation.color || this.strokeColor
          ctx.lineWidth = annotation.width || this.strokeWidth
        }
        
        switch (annotation.type) {
          case 'text':
            ctx.font = `${annotation.size || 16}px Arial`
            ctx.fillStyle = annotation.color || this.strokeColor
            ctx.fillText(annotation.text, annotation.x, annotation.y)
            break
          case 'rect':
            ctx.strokeRect(annotation.x, annotation.y, annotation.width, annotation.height)
            break
          case 'arrow':
            this.drawArrow(ctx, annotation.startX, annotation.startY, annotation.endX, annotation.endY)
            break
        }
        
        ctx.restore()
      })
    },
    
    // 绘制箭头
    drawArrow(ctx, startX, startY, endX, endY) {
      const headLength = 15
      const angle = Math.atan2(endY - startY, endX - startX)
      
      // 绘制线条
      ctx.beginPath()
      ctx.moveTo(startX, startY)
      ctx.lineTo(endX, endY)
      ctx.stroke()
      
      // 绘制箭头头部
      ctx.beginPath()
      ctx.moveTo(endX, endY)
      ctx.lineTo(
        endX - headLength * Math.cos(angle - Math.PI / 6),
        endY - headLength * Math.sin(angle - Math.PI / 6)
      )
      ctx.moveTo(endX, endY)
      ctx.lineTo(
        endX - headLength * Math.cos(angle + Math.PI / 6),
        endY - headLength * Math.sin(angle + Math.PI / 6)
      )
      ctx.stroke()
    },
    
    // 设置工具
    setTool(tool) {
      this.activeTool = tool
      this.selectedAnnotation = null
    },
    
    // 鼠标事件处理
    startDrawing(e) {
      const rect = this.$refs.canvas.getBoundingClientRect()
      this.startX = e.clientX - rect.left
      this.startY = e.clientY - rect.top
      this.isDrawing = true
    },
    
    draw(e) {
      if (!this.isDrawing) return
      
      const rect = this.$refs.canvas.getBoundingClientRect()
      const currentX = e.clientX - rect.left
      const currentY = e.clientY - rect.top
      
      // 这里可以添加实时绘制预览
    },
    
    stopDrawing(e) {
      if (!this.isDrawing) return
      
      const rect = this.$refs.canvas.getBoundingClientRect()
      const endX = e.clientX - rect.left
      const endY = e.clientY - rect.top
      
      this.isDrawing = false
      
      // 根据工具类型创建标注
      switch (this.activeTool) {
        case 'rect':
          this.addRectAnnotation(this.startX, this.startY, endX - this.startX, endY - this.startY)
          break
        case 'arrow':
          this.addArrowAnnotation(this.startX, this.startY, endX, endY)
          break
      }
    },
    
    handleCanvasClick(e) {
      if (this.activeTool === 'text') {
        const rect = this.$refs.canvas.getBoundingClientRect()
        this.pendingTextPosition = {
          x: e.clientX - rect.left,
          y: e.clientY - rect.top
        }
        this.textDialog = true
      } else if (this.activeTool === 'select') {
        this.selectAnnotation(e)
      }
    },
    
    // 添加不同类型的标注
    addTextAnnotation() {
      if (this.pendingTextPosition && this.newAnnotationText) {
        const annotation = {
          id: Date.now(),
          type: 'text',
          text: this.newAnnotationText,
          x: this.pendingTextPosition.x,
          y: this.pendingTextPosition.y,
          size: this.newAnnotationSize,
          color: this.strokeColor
        }
        
        this.annotations.push(annotation)
        this.redrawCanvas()
        this.saveToHistory()
        this.emitChange()
      }
      
      this.textDialog = false
      this.newAnnotationText = ''
      this.pendingTextPosition = null
    },
    
    addRectAnnotation(x, y, width, height) {
      const annotation = {
        id: Date.now(),
        type: 'rect',
        x,
        y,
        width,
        height,
        color: this.strokeColor,
        width: this.strokeWidth
      }
      
      this.annotations.push(annotation)
      this.redrawCanvas()
      this.saveToHistory()
      this.emitChange()
    },
    
    addArrowAnnotation(startX, startY, endX, endY) {
      const annotation = {
        id: Date.now(),
        type: 'arrow',
        startX,
        startY,
        endX,
        endY,
        color: this.strokeColor,
        width: this.strokeWidth
      }
      
      this.annotations.push(annotation)
      this.redrawCanvas()
      this.saveToHistory()
      this.emitChange()
    },
    
    // 选择标注
    selectAnnotation(e) {
      const rect = this.$refs.canvas.getBoundingClientRect()
      const x = e.clientX - rect.left
      const y = e.clientY - rect.top
      
      // 简单的点击检测（实际应用中需要更精确的碰撞检测）
      const annotation = this.annotations.find(ann => {
        switch (ann.type) {
          case 'text':
            return Math.abs(x - ann.x) < 50 && Math.abs(y - ann.y) < 20
          case 'rect':
            return x >= ann.x && x <= ann.x + ann.width && y >= ann.y && y <= ann.y + ann.height
          default:
            return false
        }
      })
      
      this.selectedAnnotation = annotation
      this.redrawCanvas()
    },
    
    // 更新标注
    updateAnnotation() {
      this.redrawCanvas()
      this.emitChange()
    },
    
    deleteAnnotation() {
      if (this.selectedAnnotation) {
        const index = this.annotations.findIndex(ann => ann.id === this.selectedAnnotation.id)
        if (index > -1) {
          this.annotations.splice(index, 1)
          this.selectedAnnotation = null
          this.redrawCanvas()
          this.saveToHistory()
          this.emitChange()
        }
      }
    },
    
    // 清除所有标注
    clearAnnotations() {
      this.$confirm('确认清除所有标注吗？', '清除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.annotations = []
        this.selectedAnnotation = null
        this.redrawCanvas()
        this.saveToHistory()
        this.emitChange()
      })
    },
    
    // 图像属性调整
    resizeImage() {
      this.redrawCanvas()
      this.saveToHistory()
      this.emitChange()
    },
    
    updateOpacity() {
      this.redrawCanvas()
      this.emitChange()
    },
    
    rotateImage() {
      this.redrawCanvas()
      this.saveToHistory()
      this.emitChange()
    },
    
    updateStyle() {
      if (this.selectedAnnotation) {
        this.selectedAnnotation.color = this.strokeColor
        this.selectedAnnotation.width = this.strokeWidth
        this.redrawCanvas()
        this.emitChange()
      }
    },
    
    applyFilter() {
      this.redrawCanvas()
      this.saveToHistory()
      this.emitChange()
    },
    
    // 历史记录
    saveToHistory() {
      const state = {
        annotations: JSON.parse(JSON.stringify(this.annotations)),
        imageWidth: this.imageWidth,
        imageHeight: this.imageHeight,
        imageOpacity: this.imageOpacity,
        rotationAngle: this.rotationAngle,
        selectedFilter: this.selectedFilter
      }
      
      if (this.historyIndex < this.history.length - 1) {
        this.history = this.history.slice(0, this.historyIndex + 1)
      }
      
      this.history.push(state)
      this.historyIndex = this.history.length - 1
      
      // 限制历史记录数量
      if (this.history.length > 50) {
        this.history.shift()
        this.historyIndex--
      }
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
      this.annotations = JSON.parse(JSON.stringify(state.annotations))
      this.imageWidth = state.imageWidth
      this.imageHeight = state.imageHeight
      this.imageOpacity = state.imageOpacity
      this.rotationAngle = state.rotationAngle
      this.selectedFilter = state.selectedFilter
      this.selectedAnnotation = null
      
      this.redrawCanvas()
      this.emitChange()
    },
    
    // 发送变化事件
    emitChange() {
      const imageData = {
        url: this.imageUrl,
        width: this.imageWidth,
        height: this.imageHeight,
        opacity: this.imageOpacity,
        rotation: this.rotationAngle,
        filter: this.selectedFilter,
        annotations: this.annotations
      }
      
      this.$emit('content-change', {
        imageData,
        annotationCount: this.annotations.length
      })
    }
  }
}
</script>

<style scoped>
.image-editor {
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

.toolbar-group .el-button.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

.editor-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.canvas-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  position: relative;
}

canvas {
  border: 1px solid #ddd;
  background: white;
  cursor: crosshair;
}

.properties-panel {
  width: 300px;
  background: white;
  border-left: 1px solid #e5e7eb;
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
@media (max-width: 1024px) {
  .editor-content {
    flex-direction: column;
  }
  
  .properties-panel {
    width: 100%;
    max-height: 40vh;
  }
}

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
}
</style> 