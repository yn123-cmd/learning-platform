import Vue from 'vue'
import Router from 'vue-router'
import store from '@/store'

// 导入组件
import Login from '@/components/Login'
import Register from '@/components/Register'
import Layout from '@/components/Layout'
import Dashboard from '@/components/Dashboard'
import CourseList from '@/components/CourseList'
import CourseDetail from '@/components/CourseDetail'
import ApiTest from '@/components/ApiTest'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: {
        requiresAuth: false,
        title: '登录'
      }
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: {
        requiresAuth: false,
        title: '注册'
      }
    },
    {
      path: '/',
      component: Layout,
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: Dashboard,
          meta: {
            requiresAuth: true,
            title: '仪表板'
          }
        },
        {
          path: 'courses',
          name: 'CourseList',
          component: CourseList,
          meta: {
            requiresAuth: true,
            title: '课程列表'
          }
        },
        {
          path: 'course/:id',
          name: 'CourseDetail',
          component: CourseDetail,
          meta: {
            requiresAuth: true,
            title: '课程详情'
          }
        },
        {
          path: 'api-test',
          name: 'ApiTest',
          component: ApiTest,
          meta: {
            requiresAuth: true,
            title: 'API测试'
          }
        },
        {
          path: 'teaching-design',
          component: () => import('@/components/TeachingDesign'),
          meta: {
            requiresAuth: true,
            title: '教学设计'
          },
          children: [
            {
              path: '',
              redirect: 'course-positioning'
            },
            {
              path: 'course-positioning',
              name: 'CoursePositioning',
              component: () => import('@/components/CoursePositioning'),
              meta: {
                requiresAuth: true,
                title: '课程定位 - 教学设计'
              }
            },
            {
              path: 'goal-generation',
              name: 'GoalGeneration',
              component: () => import('@/components/GoalGeneration'),
              meta: {
                requiresAuth: true,
                title: '目标生成 - 教学设计'
              }
            },
            {
              path: 'key-points',
              name: 'KeyPoints',
              component: () => import('@/components/KeyPoints'),
              meta: {
                requiresAuth: true,
                title: '重难点 - 教学设计'
              }
            },
            {
              path: 'student-config',
              name: 'StudentConfig',
              component: () => import('@/components/StudentConfig'),
              meta: {
                requiresAuth: true,
                title: '学情配置 - 教学设计'
              }
            },
            {
              path: 'lesson-planning',
              name: 'LessonPlanning',
              component: () => import('@/components/LessonPlanning'),
              meta: {
                requiresAuth: true,
                title: '课时规划 - 教学设计'
              }
            },
            {
              path: 'output',
              name: 'DesignOutput',
              component: () => import('@/components/DesignOutput'),
              meta: {
                requiresAuth: true,
                title: '方案输出 - 教学设计'
              }
            }
          ]
        },
        {
          path: 'analytics',
          name: 'Analytics',
          component: () => import('@/components/Analytics'),
          meta: {
            requiresAuth: true,
            title: '学情分析'
          }
        },
        {
          path: 'resource-materials',
          name: 'ResourceMaterials',
          component: () => import('@/components/ResourceMaterials'),
          meta: {
            requiresAuth: true,
            title: '智能资源生成'
          }
        },
        {
          path: 'ai-ppt-generator',
          name: 'AiPptGenerator',
          component: () => import('@/components/AiPptGenerator'),
          meta: {
            requiresAuth: false,
            title: 'AI生成PPT'
          }
        },
        {
          path: 'ai-lesson',
          name: 'AiLesson',
          component: () => import('@/components/AiLesson'),
          meta: {
            requiresAuth: true,
            title: '一键个性化备课'
          }
        },
        {
          path: 'code-oj',
          name: 'CodeOj',
          component: () => import('@/components/CodeOj'),
          meta: {
            requiresAuth: true,
            title: '代码OJ'
          }
        },
        {
          path: 'history',
          name: 'History',
          component: () => import('@/components/History'),
          meta: {
            requiresAuth: true,
            title: '备课历史'
          }
        },
        {
          path: 'collaboration',
          name: 'Collaboration',
          component: () => import('@/components/Collaboration'),
          meta: {
            requiresAuth: true,
            title: '协作空间'
          }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/components/Profile'),
          meta: {
            requiresAuth: true,
            title: '个人中心'
          }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 学习平台`
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 检查是否已登录
    if (!store.getters.isAuthenticated) {
      // 如果有token但是没有用户信息，尝试获取用户信息
      if (store.state.token) {
        try {
          await store.dispatch('getCurrentUser')
          if (store.getters.isAuthenticated) {
            next()
            return
          }
        } catch (error) {
          console.error('获取用户信息失败:', error)
        }
      }

      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    // 不需要认证的页面
    if (store.getters.isAuthenticated && (to.path === '/login' || to.path === '/register')) {
      // 已登录用户访问登录/注册页面时重定向到dashboard
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router 