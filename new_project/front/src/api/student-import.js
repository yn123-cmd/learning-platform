import axios from 'axios'
import { API_BASE_URL } from './config'

// 下载导入模板
export const downloadImportTemplate = async (templateType = 'student') => {
    try {
        const response = await axios.get(`${API_BASE_URL}/student/import-template`, {
            params: { type: templateType },
            responseType: 'blob',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })

        // 创建下载链接
        const blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `学生信息导入模板.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        return {
            success: true,
            message: '模板下载成功'
        }
    } catch (error) {
        console.error('模板下载失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '下载失败'
        }
    }
}

// 上传学生信息文件
export const uploadStudentFile = async (file, classId) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('classId', classId)

    try {
        const response = await axios.post(`${API_BASE_URL}/student/upload-file`, formData, {
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
        console.error('文件上传失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '上传失败'
        }
    }
}

// 解析上传的文件
export const parseStudentFile = async (fileId) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/student/parse-file`, {
            fileId
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
        console.error('文件解析失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '解析失败'
        }
    }
}

// 验证学生数据
export const validateStudentData = async (students) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/student/validate-data`, {
            students
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
        console.error('数据验证失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '验证失败'
        }
    }
}

// 批量导入学生信息
export const batchImportStudents = async (students, classId) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/student/batch-import`, {
            students,
            classId
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
        console.error('批量导入失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '导入失败'
        }
    }
}

// 获取导入历史
export const getImportHistory = async (classId, page = 1, pageSize = 10) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/student/import-history`, {
            params: { classId, page, pageSize },
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('获取导入历史失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '获取失败'
        }
    }
}

// 导出错误数据
export const exportErrorData = async (errors, fileName = '数据错误列表') => {
    try {
        const response = await axios.post(`${API_BASE_URL}/student/export-errors`, {
            errors
        }, {
            responseType: 'blob',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })

        // 创建下载链接
        const blob = new Blob([response.data], {
            type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `${fileName}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)

        return {
            success: true,
            message: '错误列表导出成功'
        }
    } catch (error) {
        console.error('导出错误列表失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '导出失败'
        }
    }
}

// 获取学生信息字段配置
export const getStudentFieldConfig = async () => {
    try {
        const response = await axios.get(`${API_BASE_URL}/student/field-config`, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('获取字段配置失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '获取失败'
        }
    }
}

// 预览导入数据
export const previewImportData = async (fileId, sheetIndex = 0) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/student/preview-data`, {
            params: { fileId, sheetIndex },
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('预览数据失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '预览失败'
        }
    }
}

// 检查重复学生
export const checkDuplicateStudents = async (students) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/student/check-duplicates`, {
            students
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
        console.error('检查重复失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '检查失败'
        }
    }
}

// 更新学生信息
export const updateStudentInfo = async (studentId, updateData) => {
    try {
        const response = await axios.put(`${API_BASE_URL}/student/${studentId}`, updateData, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        })
        return {
            success: true,
            data: response.data
        }
    } catch (error) {
        console.error('更新学生信息失败:', error)
        return {
            success: false,
            message: error.response?.data?.message || '更新失败'
        }
    }
} 