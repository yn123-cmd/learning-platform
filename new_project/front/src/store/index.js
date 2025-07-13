import Vue from 'vue'
import Vuex from 'vuex'
import { authApi, userApi } from '@/api'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: null,
    token: localStorage.getItem('token') || null,
    courses: [],
    loading: false
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
    },
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    CLEAR_AUTH(state) {
      state.user = null
      state.token = null
      localStorage.removeItem('token')
    },
    SET_COURSES(state, courses) {
      state.courses = courses
    },
    SET_LOADING(state, loading) {
      state.loading = loading
    }
  },
  actions: {
    // 登录
    async login({ commit }, credentials) {
      try {
        commit('SET_LOADING', true)
        const response = await authApi.login(credentials)

        if (response.code === 200 && response.data) {
          const { token, user } = response.data
          commit('SET_TOKEN', token)
          commit('SET_USER', user)
          return { success: true, data: response.data }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        console.error('登录失败:', error)
        return { success: false, message: error.message || '登录失败' }
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // 注册
    async register({ commit }, userData) {
      try {
        commit('SET_LOADING', true)
        const response = await authApi.register(userData)

        if (response.code === 200 && response.data) {
          const { token, user } = response.data
          commit('SET_TOKEN', token)
          commit('SET_USER', user)
          return { success: true, data: response.data }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        console.error('注册失败:', error)
        return { success: false, message: error.message || '注册失败' }
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // 获取当前用户信息
    async getCurrentUser({ commit }) {
      try {
        const response = await authApi.getCurrentUser()
        if (response.code === 200 && response.data) {
          commit('SET_USER', response.data)
          return { success: true, data: response.data }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 如果获取用户信息失败，清除认证状态
        commit('CLEAR_AUTH')
        return { success: false, message: error.message || '获取用户信息失败' }
      }
    },

    // 验证Token
    async validateToken({ commit }) {
      try {
        const response = await authApi.validateToken()
        if (response.code === 200 && response.data && response.data.valid) {
          return { success: true, data: response.data }
        } else {
          commit('CLEAR_AUTH')
          return { success: false, message: 'Token无效' }
        }
      } catch (error) {
        console.error('Token验证失败:', error)
        commit('CLEAR_AUTH')
        return { success: false, message: error.message || 'Token验证失败' }
      }
    },

    // 退出登录
    async logout({ commit }) {
      try {
        await authApi.logout()
        commit('CLEAR_AUTH')
        return { success: true }
      } catch (error) {
        console.error('退出登录失败:', error)
        // 即使API调用失败，也要清除本地状态
        commit('CLEAR_AUTH')
        return { success: false, message: error.message || '退出登录失败' }
      }
    },

    // 获取课程列表 (暂时保留模拟数据，待后端课程接口开发完成)
    fetchCourses({ commit }) {
      const mockCourses = [
        {
          id: 1,
          title: 'Vue.js 基础教程',
          description: '从零开始学习Vue.js框架',
          instructor: '张老师',
          duration: '6小时',
          level: '初级',
          thumbnail: 'https://via.placeholder.com/300x200'
        },
        {
          id: 2,
          title: 'JavaScript 进阶',
          description: '深入理解JavaScript核心概念',
          instructor: '李老师',
          duration: '8小时',
          level: '中级',
          thumbnail: 'https://via.placeholder.com/300x200'
        },
        {
          id: 3,
          title: 'React 开发实战',
          description: '使用React构建现代Web应用',
          instructor: '王老师',
          duration: '10小时',
          level: '高级',
          thumbnail: 'https://via.placeholder.com/300x200'
        }
      ]
      commit('SET_COURSES', mockCourses)
    },

    // 初始化应用 - 检查Token并获取用户信息
    async initApp({ dispatch, commit, state }) {
      if (state.token) {
        // 验证Token
        const validateResult = await dispatch('validateToken')
        if (validateResult.success) {
          // 获取用户信息
          await dispatch('getCurrentUser')
        }
      }
    }
  },
  getters: {
    isAuthenticated: state => !!state.token && !!state.user,
    currentUser: state => state.user,
    allCourses: state => state.courses,
    isLoading: state => state.loading
  }
}) 