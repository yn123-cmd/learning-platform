import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    console.log('发送请求:', config.method.toUpperCase(), config.url, config.data)
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    console.log('收到响应:', res)

    // 根据API文档，检查响应码
    if (res && res.code === 200) {
      return res
    } else if (res && res.code) {
      // 显示错误消息
      Message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      // 如果没有标准的响应格式，直接返回数据
      return response
    }
  },
  error => {
    console.error('响应错误:', error)

    let message = '网络错误'

    if (error.response) {
      const status = error.response.status
      const responseData = error.response.data

      switch (status) {
        case 401:
          message = 'Token无效或已过期，请重新登录'
          // 清除token并跳转到登录页
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 403:
          message = '禁止访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          // 检查是否是JWT配置错误
          if (responseData && responseData.message && responseData.message.includes('signing key')) {
            message = '服务器JWT配置错误，请联系管理员'
          } else {
            message = '服务器内部错误'
          }
          break
        default:
          message = (responseData && responseData.message) || `请求失败 (${status})`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    } else if (error.message && error.message.includes('signing key')) {
      // 特殊处理JWT签名密钥错误
      message = '服务器JWT配置错误，请检查后端密钥配置'
    }

    Message.error(message)
    return Promise.reject(error)
  }
)

export default api 