import api from './config'

// 认证相关API
export const authApi = {
  // 用户登录
  login(credentials) {
    return api.post('/auth/login', credentials)
  },

  // 用户注册
  register(userData) {
    return api.post('/auth/register', userData)
  },

  // 获取当前用户信息
  getCurrentUser() {
    return api.get('/auth/me')
  },

  // 验证Token
  validateToken() {
    return api.post('/auth/validate')
  },

  // 退出登录 (前端逻辑)
  logout() {
    localStorage.removeItem('token')
    return Promise.resolve()
  }
}

export default authApi 