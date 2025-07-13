// API服务主入口文件
export { default as api } from './config'
export { authApi } from './auth'
export { scheduleAPI } from './schedule'
export { analyticsAPI } from './analytics'
export { todoAPI } from './todo'
export { recommendationAPI } from './recommendation'
export { userApi } from './user'

// 统一导出（便于批量导入）
export default {
  auth: require('./auth').authApi,
  schedule: require('./schedule').scheduleAPI,
  analytics: require('./analytics').analyticsAPI,
  todo: require('./todo').todoAPI,
  recommendation: require('./recommendation').recommendationAPI,
  user: require('./user').userApi
} 