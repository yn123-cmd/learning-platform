<template>
  <div class="register-container">
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
    
    <div class="register-content">
      <div class="register-left">
        <div class="welcome-section">
          <div class="platform-logo">
            <i class="el-icon-edit-outline"></i>
          </div>
          <h1>加入智慧教学平台</h1>
          <p class="platform-desc">专为教师打造的智能备课与教学管理平台</p>
          
          <div class="features-list">
            <div class="feature-item">
              <i class="el-icon-document"></i>
              <span>智能备课工具</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-data-analysis"></i>
              <span>教学数据分析</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-user-solid"></i>
              <span>学生管理系统</span>
            </div>
            <div class="feature-item">
              <i class="el-icon-share"></i>
              <span>资源共享平台</span>
            </div>
          </div>
          
          <div class="quote-section">
            <blockquote>
              "让每一位教师都能享受到现代科技带来的教学便利"
            </blockquote>
          </div>
        </div>
      </div>
      
      <div class="register-right">
        <div class="register-box">
          <div class="register-header">
            <h2>创建教师账号</h2>
            <p>开启您的智慧教学之旅</p>
          </div>
          
          <el-form 
            ref="registerForm" 
            :model="registerForm" 
            :rules="rules" 
            class="register-form"
            @submit.native.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
                :disabled="loading"
                @blur="checkUsername"
              >
                <template slot="prepend">用户名</template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                prefix-icon="el-icon-message"
                size="large"
                :disabled="loading"
                @blur="checkEmail"
              >
                <template slot="prepend">邮箱</template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="nickName">
              <el-input
                v-model="registerForm.nickName"
                placeholder="请输入真实姓名"
                prefix-icon="el-icon-user-solid"
                size="large"
                :disabled="loading"
              >
                <template slot="prepend">姓名</template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请设置登录密码"
                prefix-icon="el-icon-lock"
                size="large"
                :disabled="loading"
                show-password
              >
                <template slot="prepend">密码</template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次确认密码"
                prefix-icon="el-icon-lock"
                size="large"
                :disabled="loading"
                show-password
                @keyup.enter.native="handleRegister"
              >
                <template slot="prepend">确认</template>
              </el-input>
            </el-form-item>
            
            <el-form-item prop="agree" class="agreement-item">
              <el-checkbox v-model="registerForm.agree" :disabled="loading">
                我已阅读并同意
                <a href="#" @click.prevent class="link">《教师服务协议》</a>
                和
                <a href="#" @click.prevent class="link">《隐私保护政策》</a>
              </el-checkbox>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                class="register-btn"
                :loading="loading"
                @click="handleRegister"
              >
                <i class="el-icon-check" v-if="!loading"></i>
                {{ loading ? '正在创建账号...' : '立即注册' }}
              </el-button>
            </el-form-item>
            
            <div class="form-footer">
              <span>已有教师账号？</span>
              <router-link to="/login" class="link">立即登录</router-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { userApi } from '@/api'

export default {
  name: 'Register',
  data() {
    // 确认密码验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      registerForm: {
        username: '',
        email: '',
        nickName: '',
        password: '',
        confirmPassword: '',
        agree: false
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        nickName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        agree: [
          { 
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error('请同意教师服务协议和隐私保护政策'))
              } else {
                callback()
              }
            }, 
            trigger: 'change' 
          }
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
    ...mapActions(['register']),
    
    // 检查用户名是否存在
    async checkUsername() {
      if (!this.registerForm.username || this.registerForm.username.length < 3) return
      
      try {
        const response = await userApi.checkUsername(this.registerForm.username)
        if (response.code === 200 && response.data === true) {
          this.$message.warning('用户名已存在，请选择其他用户名')
        }
      } catch (error) {
        console.error('检查用户名失败:', error)
      }
    },
    
    // 检查邮箱是否存在
    async checkEmail() {
      if (!this.registerForm.email || !/\S+@\S+\.\S+/.test(this.registerForm.email)) return
      
      try {
        const response = await userApi.checkEmail(this.registerForm.email)
        if (response.code === 200 && response.data === true) {
          this.$message.warning('邮箱已被注册，请使用其他邮箱')
        }
      } catch (error) {
        console.error('检查邮箱失败:', error)
      }
    },
    
    async handleRegister() {
      try {
        // 表单验证
        const valid = await this.$refs.registerForm.validate()
        if (!valid) return
        
        // 再次检查用户名和邮箱
        const [usernameCheck, emailCheck] = await Promise.all([
          userApi.checkUsername(this.registerForm.username),
          userApi.checkEmail(this.registerForm.email)
        ])
        
        if (usernameCheck.code === 200 && usernameCheck.data === true) {
          this.$message.error('用户名已存在，请选择其他用户名')
          return
        }
        
        if (emailCheck.code === 200 && emailCheck.data === true) {
          this.$message.error('邮箱已被注册，请使用其他邮箱')
          return
        }
        
        // 准备注册数据
        const userData = {
          username: this.registerForm.username,
          email: this.registerForm.email,
          nickName: this.registerForm.nickName,
          password: this.registerForm.password
        }
        
        // 调用注册API
        const result = await this.register(userData)
        
        if (result.success) {
          this.$message.success('注册成功！欢迎加入智慧教学平台')
          // 跳转到dashboard
          this.$router.push('/dashboard')
        } else {
          this.$message.error(result.message || '注册失败')
        }
      } catch (error) {
        console.error('注册错误:', error)
        this.$message.error('注册过程中发生错误')
      }
    }
  }
}
</script>

<style scoped>
.register-container {
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
  width: 200px;
  height: 200px;
  top: 10%;
  right: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  bottom: 15%;
  left: 5%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  top: 50%;
  left: 15%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

.register-content {
  display: flex;
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

.register-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
}

.welcome-section {
  max-width: 500px;
}

.platform-logo {
  text-align: center;
  margin-bottom: 30px;
}

.platform-logo i {
  font-size: 4rem;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.1);
  padding: 30px;
  border-radius: 50%;
  backdrop-filter: blur(10px);
}

.welcome-section h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 15px;
  text-align: center;
  background: linear-gradient(45deg, #ffffff, #f0f8ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.platform-desc {
  font-size: 1.1rem;
  text-align: center;
  margin-bottom: 40px;
  opacity: 0.9;
  line-height: 1.6;
}

.features-list {
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 1rem;
  opacity: 0.9;
}

.feature-item i {
  font-size: 1.5rem;
  margin-right: 15px;
  color: #f0f8ff;
}

.quote-section {
  text-align: center;
  margin-top: 40px;
}

.quote-section blockquote {
  font-style: italic;
  font-size: 1rem;
  opacity: 0.8;
  margin: 0;
  padding: 20px;
  border-left: 3px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.1);
  border-radius: 0 8px 8px 0;
}

.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.register-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  padding: 50px;
  border-radius: 24px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 480px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-header h2 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-weight: 600;
  font-size: 1.8rem;
}

.register-header p {
  color: #7f8c8d;
  font-size: 1rem;
}

.register-form .el-form-item {
  margin-bottom: 25px;
}

.register-form .el-input-group__prepend {
  background: #f8f9fa;
  border-color: #e9ecef;
  color: #6c757d;
  font-weight: 500;
  min-width: 80px;
}

.register-form .el-input__inner {
  border-color: #e9ecef;
  transition: all 0.3s;
}

.register-form .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.agreement-item {
  margin-bottom: 30px;
}

.agreement-item .el-checkbox__label {
  color: #6c757d;
  font-size: 14px;
}

.register-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.register-btn i {
  margin-right: 8px;
}

.form-footer {
  text-align: center;
  color: #6c757d;
  font-size: 14px;
  margin-top: 20px;
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

/* 响应式设计 */
@media (max-width: 1024px) {
  .register-content {
    flex-direction: column;
  }
  
  .register-left {
    flex: none;
    padding: 30px 20px;
  }
  
  .welcome-section h1 {
    font-size: 2rem;
  }
  
  .register-right {
    flex: none;
    padding: 20px;
  }
  
  .register-box {
    padding: 30px;
  }
}

@media (max-width: 768px) {
  .features-list {
    display: none;
  }
  
  .quote-section {
    display: none;
  }
  
  .register-box {
    padding: 25px;
    border-radius: 16px;
  }
  
  .welcome-section h1 {
    font-size: 1.8rem;
  }
}
</style> 