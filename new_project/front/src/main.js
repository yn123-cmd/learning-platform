import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)

Vue.config.productionTip = false

// 创建Vue实例并初始化应用
new Vue({
  el: '#app',
  router,
  store,
  async created() {
    // 初始化应用 - 检查Token并获取用户信息
    await this.$store.dispatch('initApp')
  },
  render: h => h(App)
}) 