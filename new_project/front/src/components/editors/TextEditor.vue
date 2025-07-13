<template>
  <div class="text-editor">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-group">
        <el-button-group size="mini">
          <el-button :class="{ active: isActive('bold') }" @click="execCommand('bold')">
            <i class="el-icon-bold"></i>
          </el-button>
          <el-button :class="{ active: isActive('italic') }" @click="execCommand('italic')">
            <i class="el-icon-italic"></i>
          </el-button>
          <el-button :class="{ active: isActive('underline') }" @click="execCommand('underline')">
            U
          </el-button>
          <el-button :class="{ active: isActive('strikeThrough') }" @click="execCommand('strikeThrough')">
            S
          </el-button>
        </el-button-group>
      </div>
      
      <div class="toolbar-group">
        <el-select v-model="currentFontSize" size="mini" @change="changeFontSize" style="width: 80px">
          <el-option label="12px" value="12px"></el-option>
          <el-option label="14px" value="14px"></el-option>
          <el-option label="16px" value="16px"></el-option>
          <el-option label="18px" value="18px"></el-option>
          <el-option label="20px" value="20px"></el-option>
          <el-option label="24px" value="24px"></el-option>
        </el-select>
        
        <el-color-picker v-model="textColor" size="mini" @change="changeTextColor"></el-color-picker>
      </div>
      
      <div class="toolbar-group">
        <el-button-group size="mini">
          <el-button @click="execCommand('justifyLeft')">
            <i class="el-icon-s-unfold"></i>
          </el-button>
          <el-button @click="execCommand('justifyCenter')">
            <i class="el-icon-s-fold"></i>
          </el-button>
          <el-button @click="execCommand('justifyRight')">
            <i class="el-icon-s-unfold"></i>
          </el-button>
        </el-button-group>
      </div>
      
      <div class="toolbar-group">
        <el-button-group size="mini">
          <el-button @click="execCommand('insertUnorderedList')">
            <i class="el-icon-menu"></i>
          </el-button>
          <el-button @click="execCommand('insertOrderedList')">
            <i class="el-icon-s-order"></i>
          </el-button>
          <el-button @click="insertTable">
            <i class="el-icon-s-grid"></i>
          </el-button>
        </el-button-group>
      </div>
      
      <div class="toolbar-group">
        <el-button size="mini" @click="insertImage">
          <i class="el-icon-picture"></i>
        </el-button>
        <el-button size="mini" @click="insertLink">
          <i class="el-icon-link"></i>
        </el-button>
        <el-button size="mini" @click="insertVideo">
          <i class="el-icon-video-camera"></i>
        </el-button>
      </div>
      
      <div class="toolbar-group">
        <el-button size="mini" @click="aiRewrite" :loading="isAiProcessing">
          <i class="el-icon-magic-stick"></i> AI改写
        </el-button>
        <el-button size="mini" @click="aiSummarize" :loading="isAiProcessing">
          <i class="el-icon-document"></i> 摘要
        </el-button>
      </div>
    </div>
    
    <!-- 编辑区域 -->
    <div 
      class="editor-content"
      ref="editor"
      contenteditable="true"
      @input="handleInput"
      @paste="handlePaste"
      @keydown="handleKeydown"
      @mouseup="updateSelection"
      @keyup="updateSelection"
      v-html="content">
    </div>
    
    <!-- 插入链接对话框 -->
    <el-dialog title="插入链接" :visible.sync="linkDialog" width="400px">
      <el-form>
        <el-form-item label="链接文本">
          <el-input v-model="linkText" placeholder="请输入链接文本"></el-input>
        </el-form-item>
        <el-form-item label="链接地址">
          <el-input v-model="linkUrl" placeholder="https://"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="linkDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmInsertLink">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 插入表格对话框 -->
    <el-dialog title="插入表格" :visible.sync="tableDialog" width="300px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="行数">
            <el-input-number v-model="tableRows" :min="1" :max="10"></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="列数">
            <el-input-number v-model="tableCols" :min="1" :max="10"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
      <div slot="footer">
        <el-button @click="tableDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmInsertTable">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'TextEditor',
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
      content: '',
      currentFontSize: '16px',
      textColor: '#000000',
      
      // 对话框状态
      linkDialog: false,
      tableDialog: false,
      linkText: '',
      linkUrl: '',
      tableRows: 3,
      tableCols: 3,
      
      // AI处理状态
      isAiProcessing: false,
      
      // 选择状态
      selection: null,
      range: null
    }
  },
  
  watch: {
    editorData: {
      immediate: true,
      deep: true,
      handler(newData) {
        if (newData && newData.content !== this.content) {
          this.content = newData.content || ''
          this.$nextTick(() => {
            this.$refs.editor.innerHTML = this.content
          })
        }
      }
    }
  },
  
  mounted() {
    this.initEditor()
  },
  
  methods: {
    initEditor() {
      const editor = this.$refs.editor
      if (editor) {
        editor.innerHTML = this.content
        editor.focus()
      }
    },
    
    // 处理输入
    handleInput(e) {
      this.content = e.target.innerHTML
      this.emitChange()
    },
    
    // 处理粘贴
    handlePaste(e) {
      e.preventDefault()
      const text = e.clipboardData.getData('text/plain')
      const html = e.clipboardData.getData('text/html')
      
      // 如果有HTML内容，进行清理后插入
      if (html) {
        const cleanHtml = this.cleanPastedHTML(html)
        document.execCommand('insertHTML', false, cleanHtml)
      } else {
        document.execCommand('insertText', false, text)
      }
      
      this.handleInput(e)
    },
    
    // 清理粘贴的HTML
    cleanPastedHTML(html) {
      // 移除危险标签和属性
      const div = document.createElement('div')
      div.innerHTML = html
      
      // 移除script标签
      const scripts = div.querySelectorAll('script')
      scripts.forEach(script => script.remove())
      
      // 移除style属性中的危险内容
      const elements = div.querySelectorAll('*')
      elements.forEach(el => {
        const allowedTags = ['p', 'div', 'span', 'strong', 'em', 'u', 'br', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ul', 'ol', 'li']
        if (!allowedTags.includes(el.tagName.toLowerCase())) {
          el.outerHTML = el.innerHTML
        }
      })
      
      return div.innerHTML
    },
    
    // 处理键盘事件
    handleKeydown(e) {
      // Ctrl+Enter 自动保存
      if (e.ctrlKey && e.key === 'Enter') {
        this.$emit('save-content')
        return
      }
      
      // Tab键处理
      if (e.key === 'Tab') {
        e.preventDefault()
        document.execCommand('insertHTML', false, '&nbsp;&nbsp;&nbsp;&nbsp;')
      }
    },
    
    // 更新选择状态
    updateSelection() {
      this.selection = window.getSelection()
      if (this.selection.rangeCount > 0) {
        this.range = this.selection.getRangeAt(0)
      }
    },
    
    // 执行编辑命令
    execCommand(command, value = null) {
      document.execCommand(command, false, value)
      this.$refs.editor.focus()
      this.handleInput({ target: this.$refs.editor })
    },
    
    // 检查命令是否激活
    isActive(command) {
      return document.queryCommandState(command)
    },
    
    // 改变字体大小
    changeFontSize(size) {
      this.execCommand('fontSize', '7') // 先设为7号字体
      const fontElements = this.$refs.editor.querySelectorAll('font[size="7"]')
      fontElements.forEach(el => {
        el.removeAttribute('size')
        el.style.fontSize = size
      })
      this.handleInput({ target: this.$refs.editor })
    },
    
    // 改变文字颜色
    changeTextColor(color) {
      this.execCommand('foreColor', color)
    },
    
    // 插入图片
    insertImage() {
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = 'image/*'
      input.onchange = (e) => {
        const file = e.target.files[0]
        if (file) {
          const reader = new FileReader()
          reader.onload = (e) => {
            const img = `<img src="${e.target.result}" style="max-width: 100%; height: auto;" alt="插入的图片">`
            this.execCommand('insertHTML', img)
          }
          reader.readAsDataURL(file)
        }
      }
      input.click()
    },
    
    // 插入链接
    insertLink() {
      if (this.selection && this.selection.toString()) {
        this.linkText = this.selection.toString()
      }
      this.linkDialog = true
    },
    
    confirmInsertLink() {
      if (this.linkUrl) {
        const link = `<a href="${this.linkUrl}" target="_blank">${this.linkText || this.linkUrl}</a>`
        this.execCommand('insertHTML', link)
      }
      this.linkDialog = false
      this.linkText = ''
      this.linkUrl = ''
    },
    
    // 插入视频
    insertVideo() {
      this.$prompt('请输入视频地址', '插入视频', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^https?:\/\/.+/,
        inputErrorMessage: '请输入有效的视频地址'
      }).then(({ value }) => {
        const video = `<video controls style="max-width: 100%; height: auto;">
          <source src="${value}" type="video/mp4">
          您的浏览器不支持视频播放
        </video>`
        this.execCommand('insertHTML', video)
      }).catch(() => {})
    },
    
    // 插入表格
    insertTable() {
      this.tableDialog = true
    },
    
    confirmInsertTable() {
      let tableHTML = '<table border="1" style="border-collapse: collapse; width: 100%;">'
      
      for (let i = 0; i < this.tableRows; i++) {
        tableHTML += '<tr>'
        for (let j = 0; j < this.tableCols; j++) {
          tableHTML += '<td style="padding: 8px; border: 1px solid #ddd;">&nbsp;</td>'
        }
        tableHTML += '</tr>'
      }
      
      tableHTML += '</table><br>'
      this.execCommand('insertHTML', tableHTML)
      this.tableDialog = false
    },
    
    // AI功能
    async aiRewrite() {
      if (!this.selection || !this.selection.toString()) {
        this.$message.warning('请先选择要改写的文本')
        return
      }
      
      this.isAiProcessing = true
      try {
        const selectedText = this.selection.toString()
        
        // 模拟AI改写请求
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        // 模拟改写结果
        const rewrittenText = this.mockAiRewrite(selectedText)
        
        this.$confirm(`AI改写建议：<br><br><strong>原文：</strong>${selectedText}<br><br><strong>改写：</strong>${rewrittenText}<br><br>是否应用此改写？`, '改写建议', {
          confirmButtonText: '应用',
          cancelButtonText: '取消',
          type: 'info',
          dangerouslyUseHTMLString: true
        }).then(() => {
          if (this.range) {
            this.range.deleteContents()
            this.range.insertNode(document.createTextNode(rewrittenText))
            this.handleInput({ target: this.$refs.editor })
          }
        })
        
      } catch (error) {
        this.$message.error('AI改写失败')
      } finally {
        this.isAiProcessing = false
      }
    },
    
    async aiSummarize() {
      const content = this.$refs.editor.textContent
      if (!content.trim()) {
        this.$message.warning('请先输入要摘要的内容')
        return
      }
      
      this.isAiProcessing = true
      try {
        // 模拟AI摘要请求
        await new Promise(resolve => setTimeout(resolve, 1500))
        
        const summary = this.mockAiSummarize(content)
        
        this.$confirm(`内容摘要：<br><br>${summary}<br><br>是否插入到文档中？`, '内容摘要', {
          confirmButtonText: '插入',
          cancelButtonText: '关闭',
          type: 'info',
          dangerouslyUseHTMLString: true
        }).then(() => {
          const summaryHTML = `<div style="background: #f0f9ff; border: 1px solid #0ea5e9; border-radius: 4px; padding: 12px; margin: 12px 0;">
            <strong>📝 内容摘要：</strong><br>${summary}
          </div>`
          this.execCommand('insertHTML', summaryHTML)
        })
        
      } catch (error) {
        this.$message.error('AI摘要失败')
      } finally {
        this.isAiProcessing = false
      }
    },
    
    // 模拟AI改写
    mockAiRewrite(text) {
      const templates = [
        `${text}的优化表达版本`,
        `更专业的${text}描述`,
        `${text}的改进写法`,
        `${text}的精炼版本`
      ]
      return templates[Math.floor(Math.random() * templates.length)]
    },
    
    // 模拟AI摘要
    mockAiSummarize(content) {
      const summaries = [
        '本段内容主要介绍了相关概念的定义和应用场景。',
        '文中重点阐述了核心知识点及其实践意义。',
        '内容涵盖了基础理论与实际操作的结合要点。',
        '主要讨论了关键技术原理和应用方法。'
      ]
      return summaries[Math.floor(Math.random() * summaries.length)]
    },
    
    // 发送内容变化事件
    emitChange() {
      this.$emit('content-change', {
        content: this.content,
        textContent: this.$refs.editor.textContent,
        wordCount: this.$refs.editor.textContent.length
      })
    }
  }
}
</script>

<style scoped>
.text-editor {
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: white;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
  flex-wrap: wrap;
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar-group .el-button-group .el-button.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

.editor-content {
  flex: 1;
  padding: 20px;
  min-height: 300px;
  overflow-y: auto;
  outline: none;
  line-height: 1.6;
  font-size: 16px;
  color: #374151;
}

.editor-content:empty:before {
  content: '开始输入内容...';
  color: #9ca3af;
  font-style: italic;
}

/* 编辑器内容样式 */
.editor-content h1, .editor-content h2, .editor-content h3,
.editor-content h4, .editor-content h5, .editor-content h6 {
  margin: 16px 0 8px 0;
  font-weight: 600;
}

.editor-content h1 { font-size: 24px; }
.editor-content h2 { font-size: 20px; }
.editor-content h3 { font-size: 18px; }
.editor-content h4 { font-size: 16px; }
.editor-content h5 { font-size: 14px; }
.editor-content h6 { font-size: 12px; }

.editor-content p {
  margin: 8px 0;
}

.editor-content ul, .editor-content ol {
  margin: 8px 0;
  padding-left: 24px;
}

.editor-content li {
  margin: 4px 0;
}

.editor-content table {
  border-collapse: collapse;
  width: 100%;
  margin: 12px 0;
}

.editor-content table td, .editor-content table th {
  border: 1px solid #d1d5db;
  padding: 8px;
  text-align: left;
}

.editor-content table th {
  background-color: #f3f4f6;
  font-weight: 600;
}

.editor-content a {
  color: #3b82f6;
  text-decoration: underline;
}

.editor-content img {
  max-width: 100%;
  height: auto;
  margin: 8px 0;
  border-radius: 4px;
}

.editor-content video {
  max-width: 100%;
  height: auto;
  margin: 8px 0;
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
  
  .editor-content {
    padding: 16px;
    font-size: 14px;
  }
}
</style> 