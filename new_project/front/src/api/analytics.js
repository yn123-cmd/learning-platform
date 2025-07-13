import api from './config'

// 学情分析管理API
export const analyticsAPI = {
  // 获取学情分析概览
  getOverview() {
    return api.get('/auth/real/analytics/overview')
  },

  // 获取课程趋势数据
  getTrends(timeRange = 'week') {
    return api.get(`/auth/real/analytics/trends?timeRange=${timeRange}`)
  },

  // 获取知识掌握情况
  getKnowledgeMastery() {
    return api.get('/auth/real/analytics/knowledge-mastery')
  },

  // 获取班级学习状态
  getClassSummary() {
    return api.get('/auth/real/analytics/class-summary')
  },

  // 获取学生学习行为分析
  getStudentBehavior(studentId, startDate = null, endDate = null) {
    let params = []
    if (startDate) params.push(`startDate=${startDate}`)
    if (endDate) params.push(`endDate=${endDate}`)
    const query = params.length > 0 ? `?${params.join('&')}` : ''
    return api.get(`/auth/real/analytics/student/${studentId}/behavior${query}`)
  },

  // 获取作业完成情况统计
  getAssignmentStats(classId = null, courseId = null) {
    let params = []
    if (classId) params.push(`classId=${classId}`)
    if (courseId) params.push(`courseId=${courseId}`)
    const query = params.length > 0 ? `?${params.join('&')}` : ''
    return api.get(`/auth/real/analytics/assignments${query}`)
  },

  // 获取课堂参与度统计
  getParticipationStats(classId = null, timeRange = 'week') {
    let params = [`timeRange=${timeRange}`]
    if (classId) params.push(`classId=${classId}`)
    const query = `?${params.join('&')}`
    return api.get(`/auth/real/analytics/participation${query}`)
  }
}

export default analyticsAPI 