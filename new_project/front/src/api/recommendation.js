import api from './config'

// AI推荐管理API
export const recommendationAPI = {
  // 获取个性化推荐
  getRecommendations(type = null, limit = 10) {
    let params = [`limit=${limit}`]
    if (type) params.push(`type=${type}`)
    const query = `?${params.join('&')}`
    return api.get(`/auth/real/recommendations${query}`)
  },

  // 获取教学资源推荐
  getTeachingResourceRecommendations() {
    return api.get('/auth/real/recommendations/teaching-resources')
  },

  // 获取课程推荐
  getCourseRecommendations() {
    return api.get('/auth/real/recommendations/courses')
  },

  // 获取学习路径推荐
  getLearningPathRecommendations(studentId) {
    return api.get(`/auth/real/recommendations/learning-path/${studentId}`)
  },

  // 记录推荐点击
  recordRecommendationClick(recommendationId) {
    return api.post(`/auth/real/recommendations/${recommendationId}/click`)
  },

  // 推荐反馈
  submitRecommendationFeedback(recommendationId, feedback) {
    return api.post(`/auth/real/recommendations/${recommendationId}/feedback`, {
      feedback
    })
  },

  // 获取推荐统计
  getRecommendationStats() {
    return api.get('/auth/real/recommendations/stats')
  }
}

export default recommendationAPI 