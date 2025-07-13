import api from './config'

// 测试相关API
export const testApi = {
  // 健康检查
  healthCheck() {
    return api.get('/test/health')
  },

  // 获取系统信息
  getSystemInfo() {
    return api.get('/test/info')
  },

  // Echo测试
  echo(data) {
    return api.post('/test/echo', data)
  }
}

export default testApi 