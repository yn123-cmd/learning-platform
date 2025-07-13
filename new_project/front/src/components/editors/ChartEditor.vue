<template>
  <div class="chart-editor">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-group">
        <span class="group-label">图表类型</span>
        <el-radio-group v-model="chartType" size="mini" @change="updateChart">
          <el-radio-button label="bar">柱状图</el-radio-button>
          <el-radio-button label="line">折线图</el-radio-button>
          <el-radio-button label="pie">饼图</el-radio-button>
          <el-radio-button label="scatter">散点图</el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">操作</span>
        <el-button-group size="mini">
          <el-button @click="addDataSeries">
            <i class="el-icon-plus"></i> 添加系列
          </el-button>
          <el-button @click="importData">
            <i class="el-icon-upload"></i> 导入数据
          </el-button>
          <el-button @click="exportChart">
            <i class="el-icon-download"></i> 导出
          </el-button>
        </el-button-group>
      </div>
      
      <div class="toolbar-group">
        <span class="group-label">AI助手</span>
        <el-button-group size="mini">
          <el-button @click="aiAnalyze" :loading="isAiProcessing">
            <i class="el-icon-data-analysis"></i> 分析
          </el-button>
          <el-button @click="aiSuggestChart" :loading="isAiProcessing">
            <i class="el-icon-magic-stick"></i> 建议
          </el-button>
        </el-button-group>
      </div>
    </div>
    
    <!-- 编辑区域 -->
    <div class="editor-content">
      <div class="editor-panel">
        <!-- 数据编辑面板 -->
        <div class="data-panel">
          <div class="panel-header">
            <h4>数据配置</h4>
            <el-button size="mini" type="text" @click="toggleDataPanel">
              <i :class="showDataPanel ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            </el-button>
          </div>
          
          <div v-show="showDataPanel" class="panel-content">
            <!-- 图表标题 -->
            <el-form-item label="图表标题">
              <el-input v-model="chartConfig.title" @input="updateChart" placeholder="请输入图表标题"></el-input>
            </el-form-item>
            
            <!-- 数据系列 -->
            <div v-for="(series, index) in chartData.series" :key="index" class="data-series">
              <div class="series-header">
                <el-input 
                  v-model="series.name" 
                  @input="updateChart" 
                  placeholder="系列名称" 
                  size="small"
                  style="width: 150px;">
                </el-input>
                <el-color-picker 
                  v-model="series.color" 
                  @change="updateChart"
                  size="mini">
                </el-color-picker>
                <el-button 
                  size="mini" 
                  type="danger" 
                  icon="el-icon-delete"
                  @click="removeDataSeries(index)"
                  :disabled="chartData.series.length <= 1">
                </el-button>
              </div>
              
              <div class="series-data">
                <el-table 
                  :data="series.data" 
                  size="mini"
                  height="200">
                  <el-table-column 
                    prop="label" 
                    label="标签" 
                    width="120">
                    <template slot-scope="scope">
                      <el-input 
                        v-model="scope.row.label" 
                        @input="updateChart"
                        size="mini">
                      </el-input>
                    </template>
                  </el-table-column>
                  <el-table-column 
                    prop="value" 
                    label="数值">
                    <template slot-scope="scope">
                      <el-input-number 
                        v-model="scope.row.value" 
                        @change="updateChart"
                        size="mini"
                        style="width: 100%">
                      </el-input-number>
                    </template>
                  </el-table-column>
                  <el-table-column 
                    width="60"
                    align="center">
                    <template slot-scope="scope">
                      <el-button 
                        size="mini" 
                        type="danger" 
                        icon="el-icon-delete"
                        @click="removeDataPoint(index, scope.$index)">
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                
                <el-button 
                  size="mini" 
                  type="primary" 
                  @click="addDataPoint(index)"
                  style="width: 100%; margin-top: 8px;">
                  <i class="el-icon-plus"></i> 添加数据点
                </el-button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 样式配置面板 -->
        <div class="style-panel">
          <div class="panel-header">
            <h4>样式配置</h4>
            <el-button size="mini" type="text" @click="toggleStylePanel">
              <i :class="showStylePanel ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            </el-button>
          </div>
          
          <div v-show="showStylePanel" class="panel-content">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="主题">
                  <el-select v-model="chartConfig.theme" @change="updateChart" size="small">
                    <el-option label="默认" value="default"></el-option>
                    <el-option label="深色" value="dark"></el-option>
                    <el-option label="明亮" value="light"></el-option>
                    <el-option label="商务" value="business"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="动画">
                  <el-switch v-model="chartConfig.animation" @change="updateChart"></el-switch>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="显示图例">
                  <el-switch v-model="chartConfig.showLegend" @change="updateChart"></el-switch>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="显示网格">
                  <el-switch v-model="chartConfig.showGrid" @change="updateChart"></el-switch>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="背景色">
              <el-color-picker v-model="chartConfig.backgroundColor" @change="updateChart"></el-color-picker>
            </el-form-item>
          </div>
        </div>
      </div>
      
      <!-- 图表预览区域 -->
      <div class="chart-preview">
        <div class="preview-header">
          <h4>图表预览</h4>
          <div class="preview-controls">
            <el-button size="mini" @click="refreshChart">
              <i class="el-icon-refresh"></i>
            </el-button>
            <el-button size="mini" @click="fullscreenChart">
              <i class="el-icon-full-screen"></i>
            </el-button>
          </div>
        </div>
        
        <div class="chart-container" ref="chartContainer">
          <!-- 这里会渲染图表 -->
          <div class="chart-placeholder" v-if="!chartRendered">
            <i class="el-icon-loading"></i>
            <p>正在渲染图表...</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 数据导入对话框 -->
    <el-dialog title="导入数据" :visible.sync="importDialog" width="600px">
      <div class="import-options">
        <el-tabs v-model="importMethod">
          <el-tab-pane label="CSV文件" name="csv">
            <el-upload
              drag
              action="#"
              :auto-upload="false"
              :on-change="handleFileImport"
              accept=".csv">
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">将CSV文件拖到此处，或<em>点击上传</em></div>
            </el-upload>
          </el-tab-pane>
          
          <el-tab-pane label="手动输入" name="manual">
            <el-input
              v-model="manualData"
              type="textarea"
              :rows="8"
              placeholder="请输入数据，格式：标签,数值&#10;例如：&#10;一月,100&#10;二月,200&#10;三月,150">
            </el-input>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <div slot="footer">
        <el-button @click="importDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmImport">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ChartEditor',
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
      chartType: 'bar',
      chartRendered: false,
      showDataPanel: true,
      showStylePanel: false,
      
      // 图表配置
      chartConfig: {
        title: '图表标题',
        theme: 'default',
        animation: true,
        showLegend: true,
        showGrid: true,
        backgroundColor: '#ffffff'
      },
      
      // 图表数据
      chartData: {
        series: [
          {
            name: '系列1',
            color: '#3b82f6',
            data: [
              { label: '类别A', value: 100 },
              { label: '类别B', value: 200 },
              { label: '类别C', value: 150 }
            ]
          }
        ]
      },
      
      // 数据导入
      importDialog: false,
      importMethod: 'csv',
      manualData: '',
      
      // AI处理
      isAiProcessing: false
    }
  },
  
  watch: {
    editorData: {
      immediate: true,
      deep: true,
      handler(newData) {
        if (newData && newData.chartData) {
          this.loadChartData(newData.chartData)
        }
      }
    }
  },
  
  mounted() {
    this.initChart()
  },
  
  methods: {
    // 初始化图表
    initChart() {
      this.$nextTick(() => {
        this.renderChart()
      })
    },
    
    // 加载图表数据
    loadChartData(data) {
      this.chartType = data.type || 'bar'
      this.chartConfig = { ...this.chartConfig, ...data.config }
      this.chartData = data.data || this.chartData
      this.renderChart()
    },
    
    // 渲染图表
    renderChart() {
      // 这里应该使用真正的图表库（如 ECharts, Chart.js 等）
      // 为了演示，我们模拟渲染过程
      this.chartRendered = false
      
      setTimeout(() => {
        this.chartRendered = true
        this.emitChange()
      }, 1000)
    },
    
    // 更新图表
    updateChart() {
      this.renderChart()
    },
    
    // 切换面板显示
    toggleDataPanel() {
      this.showDataPanel = !this.showDataPanel
    },
    
    toggleStylePanel() {
      this.showStylePanel = !this.showStylePanel
    },
    
    // 数据系列操作
    addDataSeries() {
      const newSeries = {
        name: `系列${this.chartData.series.length + 1}`,
        color: this.generateRandomColor(),
        data: [
          { label: '新类别', value: 0 }
        ]
      }
      this.chartData.series.push(newSeries)
      this.updateChart()
    },
    
    removeDataSeries(index) {
      if (this.chartData.series.length > 1) {
        this.chartData.series.splice(index, 1)
        this.updateChart()
      }
    },
    
    // 数据点操作
    addDataPoint(seriesIndex) {
      this.chartData.series[seriesIndex].data.push({
        label: '新数据',
        value: 0
      })
      this.updateChart()
    },
    
    removeDataPoint(seriesIndex, pointIndex) {
      const series = this.chartData.series[seriesIndex]
      if (series.data.length > 1) {
        series.data.splice(pointIndex, 1)
        this.updateChart()
      }
    },
    
    // 生成随机颜色
    generateRandomColor() {
      const colors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b', '#8b5cf6', '#06b6d4']
      return colors[Math.floor(Math.random() * colors.length)]
    },
    
    // 数据导入
    importData() {
      this.importDialog = true
    },
    
    handleFileImport(file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        this.parseCSVData(e.target.result)
      }
      reader.readAsText(file.raw)
    },
    
    parseCSVData(csvText) {
      const lines = csvText.split('\n').filter(line => line.trim())
      const data = lines.map(line => {
        const [label, value] = line.split(',')
        return {
                      label: (label && label.trim()) || '',
            value: parseFloat((value && value.trim()) || 0) || 0
        }
      }).filter(item => item.label && !isNaN(item.value))
      
      if (data.length > 0) {
        this.chartData.series[0].data = data
        this.updateChart()
        this.$message.success('数据导入成功')
      }
    },
    
    confirmImport() {
      if (this.importMethod === 'manual' && this.manualData) {
        this.parseCSVData(this.manualData)
      }
      this.importDialog = false
      this.manualData = ''
    },
    
    // 图表操作
    refreshChart() {
      this.renderChart()
    },
    
    fullscreenChart() {
      this.$message.info('全屏功能开发中')
    },
    
    exportChart() {
      this.$message.success('图表导出功能开发中')
    },
    
    // AI功能
    async aiAnalyze() {
      this.isAiProcessing = true
      try {
        // 模拟AI分析
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        const insights = [
          '数据趋势呈上升状态',
          '建议使用柱状图展示对比效果',
          '可以考虑添加预测数据线',
          '建议调整颜色主题以提高可读性'
        ]
        
        const randomInsight = insights[Math.floor(Math.random() * insights.length)]
        this.$message.success(`AI分析: ${randomInsight}`)
        
      } catch (error) {
        this.$message.error('AI分析失败')
      } finally {
        this.isAiProcessing = false
      }
    },
    
    async aiSuggestChart() {
      this.isAiProcessing = true
      try {
        // 模拟AI建议
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        const suggestions = [
          { type: 'line', reason: '适合展示趋势变化' },
          { type: 'pie', reason: '适合展示占比关系' },
          { type: 'bar', reason: '适合数据对比' },
          { type: 'scatter', reason: '适合相关性分析' }
        ]
        
        const suggestion = suggestions[Math.floor(Math.random() * suggestions.length)]
        
        this.$confirm(`AI建议使用${suggestion.type}图表，原因：${suggestion.reason}。是否应用？`, 'AI建议', {
          confirmButtonText: '应用',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => {
          this.chartType = suggestion.type
          this.updateChart()
        })
        
      } catch (error) {
        this.$message.error('获取AI建议失败')
      } finally {
        this.isAiProcessing = false
      }
    },
    
    // 发送变化事件
    emitChange() {
      const chartData = {
        type: this.chartType,
        config: this.chartConfig,
        data: this.chartData
      }
      
      this.$emit('content-change', {
        chartData,
        seriesCount: this.chartData.series.length,
        dataPointCount: this.chartData.series.reduce((total, series) => total + series.data.length, 0)
      })
    }
  }
}
</script>

<style scoped>
.chart-editor {
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

.editor-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.editor-panel {
  width: 350px;
  background: white;
  border-right: 1px solid #e5e7eb;
  overflow-y: auto;
}

.data-panel, .style-panel {
  border-bottom: 1px solid #e5e7eb;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
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

.data-series {
  margin-bottom: 24px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
}

.series-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.series-data {
  background: white;
  border-radius: 4px;
  padding: 8px;
}

.chart-preview {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.preview-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.preview-controls {
  display: flex;
  gap: 8px;
}

.chart-container {
  flex: 1;
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: #6b7280;
}

.chart-placeholder i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #d1d5db;
}

.import-options {
  min-height: 300px;
}

/* 响应式样式 */
@media (max-width: 1024px) {
  .editor-content {
    flex-direction: column;
  }
  
  .editor-panel {
    width: 100%;
    max-height: 40vh;
  }
  
  .chart-preview {
    min-height: 60vh;
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
  
  .editor-panel {
    max-height: 50vh;
  }
}
</style> 