<template>
  <div class="login-container">
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
    </div>
    
    <div class="login-content">
      <div class="login-left">
        <div class="welcome-section">
          <div class="platform-logo">
            <i class="el-icon-edit-outline"></i>
          </div>
          <h1>智慧教学平台</h1>
          <p class="platform-desc">让教学更智能，让备课更高效</p>
          
          <div class="stats-section">
            <div class="stat-item">
              <div class="stat-number">10K+</div>
              <div class="stat-label">活跃教师</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">50K+</div>
              <div class="stat-label">优质课件</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">500+</div>
              <div class="stat-label">合作学校</div>
            </div>
          </div>
          
          <div class="testimonial">
            <p>"这个平台让我的备课效率提升了3倍！"</p>
            <span>- 李老师，北京第一中学</span>
          </div>
        </div>
      </div>
      
      <div class="login-right">
        <div class="login-box">
          <div class="login-header">
            <h2>教师登录</h2>
            <p>欢迎回到您的教学工作台</p>
          </div>
          
          <el-form 
            ref="loginForm" 
            :model="loginForm" 
            :rules="rules" 
            class="login-form"
            @submit.native.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
                :disabled="loading"
              >
                <template slot="prepend">
                  <i class="el-icon-user"></i>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                :disabled="loading"
                show-password
                @keyup.enter.native="handleLogin"
              >
                <template slot="prepend">
                  <i class="el-icon-lock"></i>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
              >
                <i class="el-icon-right-arrow" v-if="!loading"></i>
                {{ loading ? '登录中...' : '立即登录' }}
              </el-button>
            </el-form-item>
            
            <div class="form-footer">
              <span>还没有教师账号？</span>
              <router-link to="/register" class="link">立即注册</router-link>
            </div>
            
            <div class="test-accounts">
              <div class="test-header">
                <i class="el-icon-info"></i>
                <span>演示账号</span>
              </div>
              <div class="test-accounts-grid">
                <div class="test-account-card" @click="fillTestAccount('admin')">
                  <div class="account-info">
                    <div class="account-name">管理员账号</div>
                    <div class="account-credentials">admin / 123456</div>
                  </div>
                  <i class="el-icon-right"></i>
                </div>
                <div class="test-account-card" @click="fillTestAccount('testuser')">
                  <div class="account-info">
                    <div class="account-name">测试教师</div>
                    <div class="account-credentials">testuser / 123456</div>
                  </div>
                  <i class="el-icon-right"></i>
                </div>
              </div>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['isLoading']),
    loading() {
      return this.isLoading
    }
  },
  methods: {
    ...mapActions(['login']),
    
    async handleLogin() {
      try {
        // 表单验证
        const valid = await this.$refs.loginForm.validate()
        if (!valid) return
        
        // 调用登录API
        const result = await this.login(this.loginForm)
        
        if (result.success) {
          this.$message.success('登录成功！欢迎回到教学工作台')
          // 跳转到dashboard或者从哪里来回哪里去
          const redirect = this.$route.query.redirect || '/dashboard'
          this.$router.push(redirect)
        } else {
          this.$message.error(result.message || '登录失败')
        }
      } catch (error) {
        console.error('登录错误:', error)
        this.$message.error('登录过程中发生错误')
      }
    },
    
    // 填充测试账号
    fillTestAccount(type) {
      if (type === 'admin') {
        this.loginForm.username = 'admin'
        this.loginForm.password = '123456'
      } else if (type === 'testuser') {
        this.loginForm.username = 'testuser'
        this.loginForm.password = '123456'
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -50px;
  right: -50px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: -50px;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(180deg); }
}

.login-content {
  display: flex;
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
}

.welcome-section {
  max-width: 500px;
  text-align: center;
}

.platform-logo {
  margin-bottom: 30px;
}

.platform-logo i {
  font-size: 5rem;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.1);
  padding: 40px;
  border-radius: 50%;
  backdrop-filter: blur(10px);
}

.welcome-section h1 {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 20px;
  background: linear-gradient(45deg, #ffffff, #f0f8ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.platform-desc {
  font-size: 1.2rem;
  margin-bottom: 50px;
  opacity: 0.9;
  line-height: 1.6;
}

.stats-section {
  display: flex;
  justify-content: space-around;
  margin-bottom: 50px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 8px;
  color: #f0f8ff;
}

.stat-label {
  font-size: 1rem;
  opacity: 0.8;
}

.testimonial {
  background: rgba(255, 255, 255, 0.1);
  padding: 25px;
  border-radius: 15px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.testimonial p {
  font-size: 1.1rem;
  margin-bottom: 15px;
  font-style: italic;
}

.testimonial span {
  font-size: 0.9rem;
  opacity: 0.8;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 50px;
  border-radius: 24px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 450px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-weight: 600;
  font-size: 1.8rem;
}

.login-header p {
  color: #7f8c8d;
  font-size: 1rem;
}

.login-form .el-form-item {
  margin-bottom: 25px;
}

.login-form .el-input-group__prepend {
  background: #f8f9fa;
  border-color: #e9ecef;
  color: #6c757d;
  padding: 0 15px;
}

.login-form .el-input__inner {
  border-color: #e9ecef;
  transition: all 0.3s;
}

.login-form .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.login-btn i {
  margin-right: 8px;
}

.form-footer {
  text-align: center;
  color: #6c757d;
  font-size: 14px;
  margin-bottom: 30px;
}

.link {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  margin-left: 5px;
  transition: color 0.3s;
}

.link:hover {
  color: #5a6fd8;
  text-decoration: underline;
}

.test-accounts {
  margin-top: 30px;
}

.test-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
  color: #6c757d;
  font-size: 14px;
  font-weight: 500;
}

.test-header i {
  margin-right: 8px;
  color: #409eff;
}

.test-accounts-grid {
  display: grid;
  gap: 10px;
}

.test-account-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e9ecef;
}

.test-account-card:hover {
  background: #e3f2fd;
  border-color: #667eea;
  transform: translateY(-1px);
}

.account-info {
  flex: 1;
}

.account-name {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 4px;
}

.account-credentials {
  font-size: 12px;
  color: #6c757d;
}

.test-account-card i {
  color: #667eea;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login-content {
    flex-direction: column;
  }
  
  .login-left {
    flex: none;
    padding: 30px 20px;
  }
  
  .welcome-section h1 {
    font-size: 2.5rem;
  }
  
  .stats-section {
    margin-bottom: 30px;
  }
  
  .login-right {
    flex: none;
    padding: 20px;
  }
  
  .login-box {
    padding: 30px;
  }
}

@media (max-width: 768px) {
  .stats-section {
    display: none;
  }
  
  .testimonial {
    display: none;
  }
  
  .login-box {
    padding: 25px;
    border-radius: 16px;
  }
  
  .welcome-section h1 {
    font-size: 2rem;
  }
  
  .platform-logo i {
    font-size: 4rem;
    padding: 30px;
  }
}
</style> 