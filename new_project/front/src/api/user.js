import api from './config'

// 用户管理相关API
export const userApi = {
  // 获取用户列表
  getUserList() {
    return api.get('/users')
  },

  // 根据ID获取用户
  getUserById(id) {
    return api.get(`/users/${id}`)
  },

  // 根据用户名获取用户
  getUserByUsername(username) {
    return api.get(`/users/username/${username}`)
  },

  // 检查用户名是否存在
  checkUsername(username) {
    return api.get('/users/check-username', {
      params: { username }
    })
  },

  // 检查邮箱是否存在
  checkEmail(email) {
    return api.get('/users/check-email', {
      params: { email }
    })
  }
}

export default userApi 