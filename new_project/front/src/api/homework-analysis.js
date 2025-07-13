import axios from 'axios'
import { API_BASE_URL } from './config'

// 作业图片上传
export const uploadHomeworkImage = async (file, classId) => {
    const formData = new FormData()
    formData.append('image', file)
    formData.append('classId', classId)

    try {
        const response = await axios.post(`${API_BASE_URL}/homework/upload`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('作业图片上传失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '上传失败'
        }
    }
}

// AI分析作业图片
export const analyzeHomeworkImage = async (imageId) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/homework/analyze`, {
            imageId
        }, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('AI分析失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '分析失败'
        }
    }
}

// 批量分析作业
export const batchAnalyzeHomework = async (imageIds) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/homework/batch-analyze`, {
            imageIds
        }, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('批量分析失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '批量分析失败'
        }
    }
}

// 获取班级作业分析报告
export const getHomeworkAnalysisReport = async (classId, dateRange = null) => {
    try {
        const params = { classId }
        if (dateRange) {
            params.startDate = dateRange.start
            params.endDate = dateRange.end
        }

        const response = await axios.get(`${API_BASE_URL}/homework/analysis-report`, {
            params,
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('获取分析报告失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '获取报告失败'
        }
    }
}

// 导出作业分析报告
export const exportHomeworkReport = async (classId, format = 'pdf') => {
    try {
        const response = await axios.get(`${API_BASE_URL}/homework/export-report`, {
            params: { classId, format },
            responseType: 'blob',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })

        // 创建下载链接
        const blob = new Blob([response.data])
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `作业分析报告_${classId}_${new Date().toISOString().split('T')[0]}.${format}`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        return {
            success: true,
            message: '报告导出成功'
        }
    } catch (error) {
        console.error('导出报告失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '导出失败'
        }
    }
}

// 获取常见错误类型
export const getCommonErrorTypes = async (subject = 'math') => {
    try {
        const response = await axios.get(`${API_BASE_URL}/homework/common-errors`, {
            params: { subject },
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('获取错误类型失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '获取失败'
        }
    }
}

// 获取学习建议
export const getLearningRecommendations = async (analysisResult) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/homework/recommendations`, {
            analysisResult
        }, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('获取学习建议失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '获取建议失败'
        }
    }
} 