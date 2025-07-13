<template>
  <div class="knowledge-decomposer">
    <div class="decomposer-controls">
      <el-button-group>
        <el-button 
          :type="viewMode === 'tree' ? 'primary' : ''"
          @click="setViewMode('tree')"
          icon="el-icon-share"
          size="small"
        >
          树形图
        </el-button>
        <el-button 
          :type="viewMode === 'mind' ? 'primary' : ''"
          @click="setViewMode('mind')"
          icon="el-icon-connection"
          size="small"
        >
          思维导图
        </el-button>
        <el-button 
          :type="viewMode === 'list' ? 'primary' : ''"
          @click="setViewMode('list')"
          icon="el-icon-menu"
          size="small"
        >
          列表视图
        </el-button>
      </el-button-group>
      
      <div class="auto-decompose">
        <el-button type="primary" size="small" @click="autoDecompose" :loading="loading">
          <i class="el-icon-magic-stick"></i>
          AI智能分解
        </el-button>
      </div>
    </div>

    <!-- 树形图视图 -->
    <div v-show="viewMode === 'tree'" class="tree-view">
      <el-tree
        :data="treeData"
        :props="{ children: 'children', label: 'name' }"
        show-checkbox
        node-key="id"
        :default-expanded-keys="expandedKeys"
        @check-change="onCheckChange"
        @node-click="onNodeClick"
      >
        <span class="tree-node" slot-scope="{ node, data }">
          <span class="node-label">
            <i :class="getNodeIcon(data.type)"></i>
            {{ data.name }}
          </span>
          <span class="node-tags">
            <el-tag 
              :type="getDifficultyType(data.difficulty)" 
              size="mini"
              v-if="data.difficulty"
            >
              {{ data.difficulty }}
            </el-tag>
            <el-tag 
              :type="getImportanceType(data.importance)" 
              size="mini"
              v-if="data.importance"
            >
              {{ data.importance }}
            </el-tag>
          </span>
        </span>
      </el-tree>
    </div>

    <!-- 思维导图视图 -->
    <div v-show="viewMode === 'mind'" class="mind-map-view">
      <div class="mind-map-container" ref="mindMapContainer">
        <svg :width="mindMapWidth" :height="mindMapHeight" class="mind-map-svg">
          <!-- 连接线 -->
          <g class="connections">
            <path
              v-for="connection in connections"
              :key="connection.id"
              :d="connection.path"
              stroke="#667eea"
              stroke-width="2"
              fill="none"
              class="connection-line"
            />
          </g>
          
          <!-- 节点 -->
          <g class="nodes">
            <g
              v-for="node in mindMapNodes"
              :key="node.id"
              :transform="`translate(${node.x}, ${node.y})`"
              class="mind-node"
              @click="selectNode(node)"
            >
              <rect
                :width="node.width"
                :height="node.height"
                :rx="8"
                :class="['node-rect', `node-${node.type}`, { selected: selectedNodeId === node.id }]"
              />
              <text
                :x="node.width / 2"
                :y="node.height / 2 + 4"
                text-anchor="middle"
                class="node-text"
              >
                {{ node.name }}
              </text>
            </g>
          </g>
        </svg>
      </div>
    </div>

    <!-- 列表视图 -->
    <div v-show="viewMode === 'list'" class="list-view">
      <div class="knowledge-points-list">
        <div
          v-for="point in knowledgePoints"
          :key="point.id"
          class="knowledge-point-item"
          :class="{ selected: selectedNodeId === point.id }"
          @click="selectPoint(point)"
        >
          <div class="point-header">
            <div class="point-info">
              <i :class="getNodeIcon(point.type)"></i>
              <span class="point-name">{{ point.name }}</span>
            </div>
            <div class="point-tags">
              <el-tag :type="getDifficultyType(point.difficulty)" size="mini">
                {{ point.difficulty }}
              </el-tag>
              <el-tag :type="getImportanceType(point.importance)" size="mini">
                {{ point.importance }}
              </el-tag>
            </div>
          </div>
          <p class="point-description">{{ point.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'KnowledgeDecomposer',
  props: {
    topic: {
      type: Object,
      default: () => ({})
    },
    knowledgePoints: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      viewMode: 'mind',
      loading: false,
      selectedNodeId: null,
      mindMapWidth: 800,
      mindMapHeight: 500,
      expandedKeys: []
    }
  },
  computed: {
    treeData() {
      return this.buildTreeStructure(this.knowledgePoints)
    },
    mindMapNodes() {
      return this.calculateMindMapLayout()
    },
    connections() {
      return this.calculateConnections()
    }
  },
  methods: {
    setViewMode(mode) {
      this.viewMode = mode
    },
    
    autoDecompose() {
      this.loading = true
      // 模拟AI分解过程
      setTimeout(() => {
        this.loading = false
        this.$emit('decompose', this.knowledgePoints)
        this.$message.success('知识点自动分解完成')
      }, 2000)
    },
    
    buildTreeStructure(points) {
      // 构建树形结构
      const tree = []
      const conceptPoints = points.filter(p => p.type === 'concept')
      const skillPoints = points.filter(p => p.type === 'skill')
      const applicationPoints = points.filter(p => p.type === 'application')
      
      if (conceptPoints.length > 0) {
        tree.push({
          id: 'concepts',
          name: '核心概念',
          type: 'category',
          children: conceptPoints
        })
      }
      
      if (skillPoints.length > 0) {
        tree.push({
          id: 'skills',
          name: '技能方法',
          type: 'category',
          children: skillPoints
        })
      }
      
      if (applicationPoints.length > 0) {
        tree.push({
          id: 'applications',
          name: '实际应用',
          type: 'category',
          children: applicationPoints
        })
      }
      
      return tree
    },
    
    calculateMindMapLayout() {
      const nodes = []
      const centerX = this.mindMapWidth / 2
      const centerY = this.mindMapHeight / 2
      
      // 中心节点
      nodes.push({
        id: 'center',
        name: this.topic.title || '知识点',
        x: centerX - 80,
        y: centerY - 20,
        width: 160,
        height: 40,
        type: 'center'
      })
      
      // 分支节点
      const branches = this.knowledgePoints
      const angleStep = (2 * Math.PI) / branches.length
      const radius = 150
      
      branches.forEach((point, index) => {
        const angle = index * angleStep
        const x = centerX + Math.cos(angle) * radius - 60
        const y = centerY + Math.sin(angle) * radius - 15
        
        nodes.push({
          id: point.id,
          name: point.name,
          x,
          y,
          width: 120,
          height: 30,
          type: point.type,
          ...point
        })
      })
      
      return nodes
    },
    
    calculateConnections() {
      const connections = []
      const centerNode = this.mindMapNodes.find(n => n.id === 'center')
      
      this.mindMapNodes.forEach(node => {
        if (node.id !== 'center') {
          const path = this.createCurvedPath(
            centerNode.x + centerNode.width / 2,
            centerNode.y + centerNode.height / 2,
            node.x + node.width / 2,
            node.y + node.height / 2
          )
          
          connections.push({
            id: `${centerNode.id}-${node.id}`,
            path
          })
        }
      })
      
      return connections
    },
    
    createCurvedPath(x1, y1, x2, y2) {
      const dx = x2 - x1
      const dy = y2 - y1
      const dr = Math.sqrt(dx * dx + dy * dy) * 0.3
      
      return `M ${x1} ${y1} Q ${x1 + dx * 0.3} ${y1 + dy * 0.3} ${x2} ${y2}`
    },
    
    getNodeIcon(type) {
      const icons = {
        concept: 'el-icon-bulb',
        skill: 'el-icon-setting',
        application: 'el-icon-cpu',
        analysis: 'el-icon-view',
        category: 'el-icon-folder'
      }
      return icons[type] || 'el-icon-document'
    },
    
    getDifficultyType(difficulty) {
      const types = {
        easy: 'success',
        medium: 'warning',
        hard: 'danger'
      }
      return types[difficulty] || 'info'
    },
    
    getImportanceType(importance) {
      const types = {
        high: 'danger',
        medium: 'warning',
        low: 'info'
      }
      return types[importance] || 'info'
    },
    
    selectNode(node) {
      this.selectedNodeId = node.id
      this.$emit('point-selected', node)
    },
    
    selectPoint(point) {
      this.selectedNodeId = point.id
      this.$emit('point-selected', point)
    },
    
    onCheckChange(data, checked, indeterminate) {
      // 处理节点选中状态变化
    },
    
    onNodeClick(data) {
      this.selectPoint(data)
    }
  },
  
  mounted() {
    this.expandedKeys = ['concepts', 'skills', 'applications']
  }
}
</script>

<style scoped>
.knowledge-decomposer {
  padding: 20px;
}

.decomposer-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px;
  background: rgba(248, 250, 255, 0.6);
  border-radius: 8px;
}

.tree-view {
  min-height: 400px;
}

.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 4px 0;
}

.node-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-tags {
  display: flex;
  gap: 4px;
}

.mind-map-view {
  min-height: 500px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.mind-map-container {
  width: 100%;
  height: 500px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafe 100%);
  overflow: hidden;
}

.mind-map-svg {
  width: 100%;
  height: 100%;
}

.connection-line {
  opacity: 0.7;
  transition: opacity 0.3s ease;
}

.connection-line:hover {
  opacity: 1;
  stroke-width: 3;
}

.mind-node {
  cursor: pointer;
  transition: all 0.3s ease;
}

.mind-node:hover {
  transform: scale(1.05);
}

.node-rect {
  fill: #ffffff;
  stroke: #667eea;
  stroke-width: 2;
  transition: all 0.3s ease;
}

.node-rect.node-center {
  fill: #667eea;
  stroke: #764ba2;
}

.node-rect.node-concept {
  fill: #e8f4f8;
  stroke: #409EFF;
}

.node-rect.node-skill {
  fill: #f0f9ff;
  stroke: #67c23a;
}

.node-rect.node-application {
  fill: #fef0e6;
  stroke: #e6a23c;
}

.node-rect.selected {
  stroke-width: 3;
  filter: drop-shadow(0 4px 8px rgba(102, 126, 234, 0.3));
}

.node-text {
  fill: #2c3e50;
  font-size: 12px;
  font-weight: 500;
}

.node-rect.node-center + .node-text {
  fill: white;
  font-weight: 600;
}

.list-view {
  min-height: 400px;
}

.knowledge-points-list {
  display: grid;
  gap: 12px;
}

.knowledge-point-item {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafe 100%);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.knowledge-point-item:hover {
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.knowledge-point-item.selected {
  border-color: #667eea;
  background: linear-gradient(135deg, #f0f4ff 0%, #e6efff 100%);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
}

.point-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.point-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.point-name {
  font-weight: 600;
  color: #2c3e50;
}

.point-tags {
  display: flex;
  gap: 4px;
}

.point-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin: 0;
}

@media (max-width: 768px) {
  .decomposer-controls {
    flex-direction: column;
    gap: 12px;
  }
  
  .mind-map-container {
    height: 400px;
  }
  
  .point-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style> 