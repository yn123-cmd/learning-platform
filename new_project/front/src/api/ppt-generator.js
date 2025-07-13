import api from './config'

/**
 * PPT生成相关API
 */

// 分析教学设计数据，为PPT生成提供基础
export const analyzeTeachingData = (data) => {
    return api.post('/ppt/analyze-teaching-data', data)
}

// 获取智能模板推荐
export const getTemplateRecommendations = (teachingContext) => {
    return api.post('/ppt/recommend-templates', teachingContext)
}

// 生成PPT结构预览
export const generatePPTStructure = (config) => {
    return api.post('/ppt/generate-structure', config)
}

// 开始PPT生成任务
export const startPPTGeneration = (generationConfig) => {
    return api.post('/ppt/generate', generationConfig)
}

// 查询PPT生成进度
export const getPPTGenerationProgress = (taskId) => {
    return api.get(`/ppt/generation-progress/${taskId}`)
}

// 下载生成的PPT
export const downloadGeneratedPPT = (taskId) => {
    return api.get(`/ppt/download/${taskId}`, {
        responseType: 'blob'
    })
}

// 预览PPT内容
export const previewPPT = (taskId) => {
    return api.get(`/ppt/preview/${taskId}`)
}

// 获取PPT编辑URL
export const getPPTEditUrl = (taskId) => {
    return api.get(`/ppt/edit-url/${taskId}`)
}

// 分享PPT
export const sharePPT = (taskId, shareConfig) => {
    return api.post(`/ppt/share/${taskId}`, shareConfig)
}

// 保存PPT配置模板
export const savePPTTemplate = (templateData) => {
    return api.post('/ppt/save-template', templateData)
}

// 获取用户的PPT历史记录
export const getPPTHistory = (params) => {
    return api.get('/ppt/history', { params })
}

// 删除PPT
export const deletePPT = (taskId) => {
    return api.delete(`/ppt/${taskId}`)
}

// 获取PPT模板库
export const getPPTTemplates = (filters) => {
    return api.get('/ppt/templates', { params: filters })
}

// 上传自定义模板
export const uploadCustomTemplate = (templateFile) => {
    const formData = new FormData()
    formData.append('template', templateFile)

    return api.post('/ppt/upload-template', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 获取PPT生成统计数据
export const getPPTGenerationStats = () => {
    return api.get('/ppt/stats')
}

// 评价生成的PPT质量
export const ratePPTQuality = (taskId, rating) => {
    return api.post(`/ppt/rate/${taskId}`, { rating })
}

// 获取AI优化建议
export const getAIOptimizationSuggestions = (pptData) => {
    return api.post('/ppt/ai-suggestions', pptData)
}

// 批量生成多个版本的PPT
export const batchGeneratePPT = (batchConfig) => {
    return api.post('/ppt/batch-generate', batchConfig)
}

// 将PPT转换为其他格式
export const convertPPTFormat = (taskId, targetFormat) => {
    return api.post(`/ppt/convert/${taskId}`, { format: targetFormat })
}

// 获取PPT生成失败的详细信息
export const getPPTGenerationError = (taskId) => {
    return api.get(`/ppt/generation-error/${taskId}`)
}

export default {
    analyzeTeachingData,
    getTemplateRecommendations,
    generatePPTStructure,
    startPPTGeneration,
    getPPTGenerationProgress,
    downloadGeneratedPPT,
    previewPPT,
    getPPTEditUrl,
    sharePPT,
    savePPTTemplate,
    getPPTHistory,
    deletePPT,
    getPPTTemplates,
    uploadCustomTemplate,
    getPPTGenerationStats,
    ratePPTQuality,
    getAIOptimizationSuggestions,
    batchGeneratePPT,
    convertPPTFormat,
    getPPTGenerationError
} 