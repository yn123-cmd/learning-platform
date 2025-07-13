import api from './config'

// 待办事项管理API
export const todoAPI = {
  // 获取待办列表
  getTodos(status = null, priority = null) {
    let params = []
    if (status) params.push(`status=${status}`)
    if (priority) params.push(`priority=${priority}`)
    const query = params.length > 0 ? `?${params.join('&')}` : ''
    return api.get(`/auth/real/todos${query}`)
  },

  // 获取今日待办
  getTodayTodos() {
    return api.get('/auth/real/todos/today')
  },

  // 获取紧急待办
  getUrgentTodos() {
    return api.get('/auth/real/todos/urgent')
  },

  // 创建待办事项
  createTodo(todoData) {
    return api.post('/auth/real/todos', todoData)
  },

  // 更新待办事项
  updateTodo(todoId, todoData) {
    return api.put(`/auth/real/todos/${todoId}`, todoData)
  },

  // 删除待办事项
  deleteTodo(todoId) {
    return api.delete(`/auth/real/todos/${todoId}`)
  },

  // 完成待办事项
  completeTodo(todoId) {
    return api.put(`/auth/real/todos/${todoId}/complete`)
  },

  // 批量操作待办事项
  batchUpdateTodos(todoIds, action) {
    return api.put('/auth/real/todos/batch', {
      todoIds,
      action
    })
  }
}

export default todoAPI 