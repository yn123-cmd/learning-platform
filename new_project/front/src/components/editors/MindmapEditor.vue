<template>
  <div class="mindmap-editor">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-group">
        <span class="group-label">节点操作</span>
        <el-button-group size="mini">
          <el-button @click="addChildNode" :disabled="!selectedNode">
            <i class="el-icon-plus"></i> 子节点
          </el-button>
          <el-button @click="addSiblingNode" :disabled="!selectedNode || selectedNode.id === 'root'">
            <i class="el-icon-circle-plus"></i> 同级节点
          </el-button>
          <el-button @click="deleteNode" :disabled="!selectedNode || selectedNode.id === 'root'">
            <i class="el-icon-delete"></i> 删除
          </el-button>
        </el-button-group>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">样式</span>
        <el-select v-model="nodeStyle.shape" size="mini" @change="updateNodeStyle" style="width: 100px">
          <el-option label="圆角矩形" value="rounded"></el-option>
          <el-option label="矩形" value="rect"></el-option>
          <el-option label="圆形" value="circle"></el-option>
          <el-option label="椭圆" value="ellipse"></el-option>
        </el-select>
        
        <el-color-picker v-model="nodeStyle.backgroundColor" size="mini" @change="updateNodeStyle"></el-color-picker>
        <el-color-picker v-model="nodeStyle.textColor" size="mini" @change="updateNodeStyle"></el-color-picker>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">布局</span>
        <el-radio-group v-model="layoutType" size="mini" @change="updateLayout">
          <el-radio-button label="horizontal">水平</el-radio-button>
          <el-radio-button label="vertical">垂直</el-radio-button>
          <el-radio-button label="radial">放射</el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">操作</span>
        <el-button-group size="mini">
          <el-button @click="zoomIn">
            <i class="el-icon-zoom-in"></i>
          </el-button>
          <el-button @click="zoomOut">
            <i class="el-icon-zoom-out"></i>
          </el-button>
          <el-button @click="resetView">
            <i class="el-icon-refresh"></i>
          </el-button>
          <el-button @click="autoLayout">
            <i class="el-icon-s-grid"></i> 自动布局
          </el-button>
        </el-button-group>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">AI助手</span>
        <el-button-group size="mini">
          <el-button @click="aiExpand" :loading="isAiProcessing">
            <i class="el-icon-magic-stick"></i> 扩展
          </el-button>
          <el-button @click="aiOptimize" :loading="isAiProcessing">
            <i class="el-icon-setting"></i> 优化
          </el-button>
        </el-button-group>
      </div>
    </div>
    
    <!-- 思维导图画布 -->
    <div class="mindmap-canvas" ref="canvas" @click="clearSelection">
      <svg 
        :width="canvasWidth" 
        :height="canvasHeight"
        :viewBox="`0 0 ${canvasWidth} ${canvasHeight}`"
        @wheel="handleWheel"
        @mousedown="startPan"
        @mousemove="handlePan"
        @mouseup="endPan">
        
        <!-- 连接线 -->
        <g class="connections">
          <path 
            v-for="connection in connections" 
            :key="connection.id"
            :d="connection.path"
            :stroke="connection.color || '#64748b'"
            :stroke-width="connection.width || 2"
            fill="none"
            stroke-linecap="round">
          </path>
        </g>
        
        <!-- 节点 -->
        <g class="nodes">
          <g 
            v-for="node in nodes" 
            :key="node.id"
            :transform="`translate(${node.x}, ${node.y})`"
            :class="['node', { selected: selectedNode && selectedNode.id === node.id }]"
            @click.stop="selectNode(node)"
            @mousedown="startDrag($event, node)"
            @dblclick="editNode(node)">
            
            <!-- 节点背景 -->
            <rect 
              v-if="node.style.shape === 'rect' || node.style.shape === 'rounded'"
              :x="-node.width/2" 
              :y="-node.height/2"
              :width="node.width" 
              :height="node.height"
              :rx="node.style.shape === 'rounded' ? 8 : 0"
              :fill="node.style.backgroundColor"
              :stroke="node.style.borderColor"
              :stroke-width="node.style.borderWidth">
            </rect>
            
            <circle 
              v-else-if="node.style.shape === 'circle'"
              :r="Math.max(node.width, node.height)/2"
              :fill="node.style.backgroundColor"
              :stroke="node.style.borderColor"
              :stroke-width="node.style.borderWidth">
            </circle>
            
            <ellipse 
              v-else-if="node.style.shape === 'ellipse'"
              :rx="node.width/2" 
              :ry="node.height/2"
              :fill="node.style.backgroundColor"
              :stroke="node.style.borderColor"
              :stroke-width="node.style.borderWidth">
            </ellipse>
            
            <!-- 节点文本 -->
            <text 
              :x="0" 
              :y="5"
              text-anchor="middle"
              :fill="node.style.textColor"
              :font-size="node.style.fontSize"
              :font-weight="node.style.fontWeight">
              {{ node.text }}
            </text>
            
            <!-- 节点控制点 -->
            <g v-if="selectedNode && selectedNode.id === node.id" class="node-controls">
              <circle cx="0" cy="0" r="3" fill="#3b82f6" stroke="white" stroke-width="1"></circle>
            </g>
          </g>
        </g>
      </svg>
    </div>
    
    <!-- 节点编辑对话框 -->
    <el-dialog title="编辑节点" :visible.sync="nodeEditDialog" width="500px">
      <el-form v-if="editingNode" label-width="80px">
        <el-form-item label="节点文本">
          <el-input v-model="editingNode.text" placeholder="请输入节点文本"></el-input>
        </el-form-item>
        
        <el-form-item label="节点形状">
          <el-select v-model="editingNode.style.shape" style="width: 100%">
            <el-option label="圆角矩形" value="rounded"></el-option>
            <el-option label="矩形" value="rect"></el-option>
            <el-option label="圆形" value="circle"></el-option>
            <el-option label="椭圆" value="ellipse"></el-option>
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="背景色">
              <el-color-picker v-model="editingNode.style.backgroundColor"></el-color-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文字色">
              <el-color-picker v-model="editingNode.style.textColor"></el-color-picker>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="字体大小">
              <el-input-number v-model="editingNode.style.fontSize" :min="10" :max="24"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="字体粗细">
              <el-select v-model="editingNode.style.fontWeight">
                <el-option label="正常" value="normal"></el-option>
                <el-option label="粗体" value="bold"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <div slot="footer">
        <el-button @click="cancelEditNode">取消</el-button>
        <el-button type="primary" @click="confirmEditNode">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- AI扩展建议 -->
    <el-dialog title="AI扩展建议" :visible.sync="aiExpandDialog" width="600px">
      <div class="ai-suggestions">
        <div v-for="suggestion in aiSuggestions" :key="suggestion.id" class="suggestion-item">
          <div class="suggestion-header">
            <span class="suggestion-text">{{ suggestion.text }}</span>
            <el-tag size="mini" :type="suggestion.confidence > 0.8 ? 'success' : 'info'">
              {{ Math.round(suggestion.confidence * 100) }}%
            </el-tag>
          </div>
          <div class="suggestion-actions">
            <el-button size="mini" type="primary" @click="applySuggestion(suggestion)">
              添加
            </el-button>
            <el-button size="mini" @click="ignoreSuggestion(suggestion)">
              忽略
            </el-button>
          </div>
        </div>
      </div>
      
      <div slot="footer">
        <el-button @click="aiExpandDialog = false">关闭</el-button>
        <el-button type="primary" @click="applyAllSuggestions">应用所有建议</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'MindmapEditor',
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
      // 画布设置
      canvasWidth: 1200,
      canvasHeight: 800,
      scale: 1,
      panX: 0,
      panY: 0,
      
      // 思维导图数据
      nodes: [],
      connections: [],
      selectedNode: null,
      layoutType: 'horizontal',
      
      // 节点样式
      nodeStyle: {
        shape: 'rounded',
        backgroundColor: '#e0f2fe',
        textColor: '#0f172a',
        fontSize: 14,
        fontWeight: 'normal',
        borderColor: '#0ea5e9',
        borderWidth: 2
      },
      
      // 编辑状态
      nodeEditDialog: false,
      editingNode: null,
      
      // 拖拽状态
      isDragging: false,
      dragNode: null,
      dragStart: { x: 0, y: 0 },
      
      // 平移状态
      isPanning: false,
      panStart: { x: 0, y: 0 },
      
      // AI功能
      isAiProcessing: false,
      aiExpandDialog: false,
      aiSuggestions: []
    }
  },
  
  watch: {
    editorData: {
      immediate: true,
      deep: true,
      handler(newData) {
        if (newData && newData.mindmapData) {
          this.loadMindmapData(newData.mindmapData)
        } else {
          this.initDefaultMindmap()
        }
      }
    }
  },
  
  mounted() {
    this.initCanvas()
    document.addEventListener('mousemove', this.handleGlobalMouseMove)
    document.addEventListener('mouseup', this.handleGlobalMouseUp)
  },
  
  beforeDestroy() {
    document.removeEventListener('mousemove', this.handleGlobalMouseMove)
    document.removeEventListener('mouseup', this.handleGlobalMouseUp)
  },
  
  methods: {
    // 初始化画布
    initCanvas() {
      const canvas = this.$refs.canvas
      if (canvas) {
        const rect = canvas.getBoundingClientRect()
        this.canvasWidth = rect.width || 1200
        this.canvasHeight = rect.height || 800
      }
    },
    
    // 初始化默认思维导图
    initDefaultMindmap() {
      const rootNode = {
        id: 'root',
        text: '主题',
        x: this.canvasWidth / 2,
        y: this.canvasHeight / 2,
        width: 120,
        height: 40,
        level: 0,
        parent: null,
        children: [],
        style: { ...this.nodeStyle }
      }
      
      this.nodes = [rootNode]
      this.connections = []
      this.selectedNode = rootNode
    },
    
    // 加载思维导图数据
    loadMindmapData(data) {
      this.nodes = data.nodes || []
      this.connections = data.connections || []
      this.layoutType = data.layoutType || 'horizontal'
      
      if (this.nodes.length === 0) {
        this.initDefaultMindmap()
      }
    },
    
    // 选择节点
    selectNode(node) {
      this.selectedNode = node
      this.nodeStyle = { ...node.style }
    },
    
    // 清除选择
    clearSelection() {
      this.selectedNode = null
    },
    
    // 添加子节点
    addChildNode() {
      if (!this.selectedNode) return
      
      const newNode = {
        id: Date.now().toString(),
        text: '新节点',
        x: this.selectedNode.x + 150,
        y: this.selectedNode.y,
        width: 100,
        height: 30,
        level: this.selectedNode.level + 1,
        parent: this.selectedNode.id,
        children: [],
        style: { ...this.nodeStyle }
      }
      
      this.nodes.push(newNode)
      this.selectedNode.children.push(newNode.id)
      
      // 创建连接
      this.createConnection(this.selectedNode, newNode)
      this.updateLayout()
      this.emitChange()
    },
    
    // 添加同级节点
    addSiblingNode() {
      if (!this.selectedNode || this.selectedNode.id === 'root') return
      
      const parentNode = this.nodes.find(n => n.id === this.selectedNode.parent)
      if (!parentNode) return
      
      const newNode = {
        id: Date.now().toString(),
        text: '新节点',
        x: this.selectedNode.x,
        y: this.selectedNode.y + 80,
        width: 100,
        height: 30,
        level: this.selectedNode.level,
        parent: parentNode.id,
        children: [],
        style: { ...this.nodeStyle }
      }
      
      this.nodes.push(newNode)
      parentNode.children.push(newNode.id)
      
      // 创建连接
      this.createConnection(parentNode, newNode)
      this.updateLayout()
      this.emitChange()
    },
    
    // 删除节点
    deleteNode() {
      if (!this.selectedNode || this.selectedNode.id === 'root') return
      
      // 递归删除子节点
      this.deleteNodeRecursive(this.selectedNode)
      
      // 从父节点中移除
      const parentNode = this.nodes.find(n => n.id === this.selectedNode.parent)
      if (parentNode) {
        const index = parentNode.children.indexOf(this.selectedNode.id)
        if (index > -1) {
          parentNode.children.splice(index, 1)
        }
      }
      
      this.selectedNode = null
      this.updateLayout()
      this.emitChange()
    },
    
    // 递归删除节点
    deleteNodeRecursive(node) {
      // 删除子节点
      node.children.forEach(childId => {
        const childNode = this.nodes.find(n => n.id === childId)
        if (childNode) {
          this.deleteNodeRecursive(childNode)
        }
      })
      
      // 删除连接
      this.connections = this.connections.filter(c => 
        c.from !== node.id && c.to !== node.id
      )
      
      // 删除节点
      const index = this.nodes.findIndex(n => n.id === node.id)
      if (index > -1) {
        this.nodes.splice(index, 1)
      }
    },
    
    // 创建连接
    createConnection(fromNode, toNode) {
      const connection = {
        id: `${fromNode.id}-${toNode.id}`,
        from: fromNode.id,
        to: toNode.id,
        path: this.calculateConnectionPath(fromNode, toNode),
        color: '#64748b',
        width: 2
      }
      
      this.connections.push(connection)
    },
    
    // 计算连接路径
    calculateConnectionPath(fromNode, toNode) {
      const fromX = fromNode.x
      const fromY = fromNode.y
      const toX = toNode.x
      const toY = toNode.y
      
      // 创建曲线路径
      const midX = (fromX + toX) / 2
      return `M ${fromX} ${fromY} Q ${midX} ${fromY} ${toX} ${toY}`
    },
    
    // 更新布局
    updateLayout() {
      if (this.nodes.length === 0) return
      
      const rootNode = this.nodes.find(n => n.id === 'root')
      if (!rootNode) return
      
      switch (this.layoutType) {
        case 'horizontal':
          this.layoutHorizontal(rootNode)
          break
        case 'vertical':
          this.layoutVertical(rootNode)
          break
        case 'radial':
          this.layoutRadial(rootNode)
          break
      }
      
      this.updateConnections()
    },
    
    // 水平布局
    layoutHorizontal(rootNode) {
      rootNode.x = this.canvasWidth / 2
      rootNode.y = this.canvasHeight / 2
      
      this.layoutChildrenHorizontal(rootNode, 0)
    },
    
    layoutChildrenHorizontal(node, angle) {
      const children = node.children.map(id => this.nodes.find(n => n.id === id)).filter(Boolean)
      
      if (children.length === 0) return
      
      const spacing = 100
      const startY = node.y - (children.length - 1) * spacing / 2
      
      children.forEach((child, index) => {
        child.x = node.x + 200
        child.y = startY + index * spacing
        
        this.layoutChildrenHorizontal(child, angle)
      })
    },
    
    // 垂直布局
    layoutVertical(rootNode) {
      rootNode.x = this.canvasWidth / 2
      rootNode.y = 100
      
      this.layoutChildrenVertical(rootNode, 1)
    },
    
    layoutChildrenVertical(node, level) {
      const children = node.children.map(id => this.nodes.find(n => n.id === id)).filter(Boolean)
      
      if (children.length === 0) return
      
      const spacing = 150
      const startX = node.x - (children.length - 1) * spacing / 2
      
      children.forEach((child, index) => {
        child.x = startX + index * spacing
        child.y = node.y + 120
        
        this.layoutChildrenVertical(child, level + 1)
      })
    },
    
    // 放射布局
    layoutRadial(rootNode) {
      rootNode.x = this.canvasWidth / 2
      rootNode.y = this.canvasHeight / 2
      
      this.layoutChildrenRadial(rootNode, 0, 2 * Math.PI, 150)
    },
    
    layoutChildrenRadial(node, startAngle, angleRange, radius) {
      const children = node.children.map(id => this.nodes.find(n => n.id === id)).filter(Boolean)
      
      if (children.length === 0) return
      
      const angleStep = angleRange / children.length
      
      children.forEach((child, index) => {
        const angle = startAngle + index * angleStep
        child.x = node.x + Math.cos(angle) * radius
        child.y = node.y + Math.sin(angle) * radius
        
        this.layoutChildrenRadial(child, angle - angleStep/4, angleStep/2, radius * 0.8)
      })
    },
    
    // 更新连接
    updateConnections() {
      this.connections.forEach(connection => {
        const fromNode = this.nodes.find(n => n.id === connection.from)
        const toNode = this.nodes.find(n => n.id === connection.to)
        
        if (fromNode && toNode) {
          connection.path = this.calculateConnectionPath(fromNode, toNode)
        }
      })
    },
    
    // 自动布局
    autoLayout() {
      this.updateLayout()
      this.$message.success('自动布局完成')
    },
    
    // 缩放功能
    zoomIn() {
      this.scale = Math.min(this.scale * 1.2, 3)
    },
    
    zoomOut() {
      this.scale = Math.max(this.scale / 1.2, 0.3)
    },
    
    resetView() {
      this.scale = 1
      this.panX = 0
      this.panY = 0
    },
    
    // 鼠标滚轮缩放
    handleWheel(e) {
      e.preventDefault()
      const delta = e.deltaY > 0 ? 0.9 : 1.1
      this.scale = Math.max(0.3, Math.min(3, this.scale * delta))
    },
    
    // 拖拽节点
    startDrag(e, node) {
      if (e.button === 0) { // 左键
        this.isDragging = true
        this.dragNode = node
        this.dragStart = { x: e.clientX - node.x, y: e.clientY - node.y }
      }
    },
    
    handleGlobalMouseMove(e) {
      if (this.isDragging && this.dragNode) {
        this.dragNode.x = e.clientX - this.dragStart.x
        this.dragNode.y = e.clientY - this.dragStart.y
        this.updateConnections()
      } else if (this.isPanning) {
        this.panX = e.clientX - this.panStart.x
        this.panY = e.clientY - this.panStart.y
      }
    },
    
    handleGlobalMouseUp() {
      if (this.isDragging) {
        this.isDragging = false
        this.dragNode = null
        this.emitChange()
      }
      
      if (this.isPanning) {
        this.isPanning = false
      }
    },
    
    // 平移画布
    startPan(e) {
      if (e.button === 1 || (e.button === 0 && e.target.tagName === 'svg')) { // 中键或点击空白区域
        this.isPanning = true
        this.panStart = { x: e.clientX - this.panX, y: e.clientY - this.panY }
      }
    },
    
    handlePan(e) {
      // 已在handleGlobalMouseMove中处理
    },
    
    endPan() {
      // 已在handleGlobalMouseUp中处理
    },
    
    // 编辑节点
    editNode(node) {
      this.editingNode = { ...node, style: { ...node.style } }
      this.nodeEditDialog = true
    },
    
    confirmEditNode() {
      if (this.editingNode) {
        const nodeIndex = this.nodes.findIndex(n => n.id === this.editingNode.id)
        if (nodeIndex > -1) {
          // 更新节点数据
          this.nodes[nodeIndex] = { ...this.editingNode }
          
          // 重新计算节点尺寸
          this.calculateNodeSize(this.nodes[nodeIndex])
          
          this.updateConnections()
          this.emitChange()
        }
      }
      
      this.nodeEditDialog = false
      this.editingNode = null
    },
    
    cancelEditNode() {
      this.nodeEditDialog = false
      this.editingNode = null
    },
    
    // 计算节点尺寸
    calculateNodeSize(node) {
      const textLength = node.text.length
      node.width = Math.max(80, textLength * 12 + 20)
      node.height = Math.max(30, 40)
    },
    
    // 更新节点样式
    updateNodeStyle() {
      if (this.selectedNode) {
        this.selectedNode.style = { ...this.nodeStyle }
        this.emitChange()
      }
    },
    
    // AI功能
    async aiExpand() {
      if (!this.selectedNode) {
        this.$message.warning('请先选择一个节点')
        return
      }
      
      this.isAiProcessing = true
      try {
        // 模拟AI扩展请求
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        // 生成AI建议
        this.aiSuggestions = this.generateAiSuggestions(this.selectedNode)
        this.aiExpandDialog = true
        
      } catch (error) {
        this.$message.error('AI扩展失败')
      } finally {
        this.isAiProcessing = false
      }
    },
    
    async aiOptimize() {
      this.isAiProcessing = true
      try {
        // 模拟AI优化请求
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        // 优化布局
        this.updateLayout()
        this.$message.success('AI优化完成')
        
      } catch (error) {
        this.$message.error('AI优化失败')
      } finally {
        this.isAiProcessing = false
      }
    },
    
    // 生成AI建议
    generateAiSuggestions(node) {
      const suggestions = [
        { id: 1, text: '相关概念', confidence: 0.9 },
        { id: 2, text: '应用场景', confidence: 0.85 },
        { id: 3, text: '注意事项', confidence: 0.8 },
        { id: 4, text: '扩展阅读', confidence: 0.75 }
      ]
      
      return suggestions.map(s => ({
        ...s,
        text: `${node.text} - ${s.text}`,
        parentId: node.id
      }))
    },
    
    // 应用AI建议
    applySuggestion(suggestion) {
      const parentNode = this.nodes.find(n => n.id === suggestion.parentId)
      if (!parentNode) return
      
      const newNode = {
        id: Date.now().toString(),
        text: suggestion.text,
        x: parentNode.x + 150,
        y: parentNode.y + parentNode.children.length * 60,
        width: 120,
        height: 30,
        level: parentNode.level + 1,
        parent: parentNode.id,
        children: [],
        style: { ...this.nodeStyle }
      }
      
      this.nodes.push(newNode)
      parentNode.children.push(newNode.id)
      this.createConnection(parentNode, newNode)
      
      // 从建议列表中移除
      const index = this.aiSuggestions.findIndex(s => s.id === suggestion.id)
      if (index > -1) {
        this.aiSuggestions.splice(index, 1)
      }
      
      this.updateLayout()
      this.emitChange()
    },
    
    ignoreSuggestion(suggestion) {
      const index = this.aiSuggestions.findIndex(s => s.id === suggestion.id)
      if (index > -1) {
        this.aiSuggestions.splice(index, 1)
      }
    },
    
    applyAllSuggestions() {
      this.aiSuggestions.forEach(suggestion => {
        this.applySuggestion(suggestion)
      })
      this.aiExpandDialog = false
    },
    
    // 发送数据变化事件
    emitChange() {
      const mindmapData = {
        nodes: this.nodes,
        connections: this.connections,
        layoutType: this.layoutType
      }
      
      this.$emit('content-change', {
        mindmapData,
        nodeCount: this.nodes.length,
        connectionCount: this.connections.length
      })
    }
  }
}
</script>

<style scoped>
.mindmap-editor {
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

.mindmap-canvas {
  flex: 1;
  overflow: hidden;
  cursor: grab;
}

.mindmap-canvas:active {
  cursor: grabbing;
}

svg {
  width: 100%;
  height: 100%;
  background: white;
}

.node {
  cursor: pointer;
  transition: all 0.2s ease;
}

.node:hover {
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.1));
}

.node.selected {
  filter: drop-shadow(0 4px 12px rgba(59, 130, 246, 0.3));
}

.node text {
  pointer-events: none;
  user-select: none;
}

.node-controls {
  pointer-events: none;
}

.connections path {
  transition: stroke 0.2s ease;
}

.connections path:hover {
  stroke-width: 3;
}

/* AI建议样式 */
.ai-suggestions {
  max-height: 400px;
  overflow-y: auto;
}

.suggestion-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 8px;
  background: #f9fafb;
}

.suggestion-header {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.suggestion-text {
  font-weight: 500;
  color: #374151;
}

.suggestion-actions {
  display: flex;
  gap: 8px;
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
}
</style>
</rewritten_file>