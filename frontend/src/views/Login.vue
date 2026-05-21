<template>
  <div class="login-container">
    <div class="login-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
    
    <div class="login-card">
      <div class="login-wrapper">
        <div class="login-header">
          <div class="logo-container">
            <div class="logo-icon">🚁</div>
            <div class="logo-text">
              <h1>农翼通</h1>
              <p>无人机共享平台</p>
            </div>
          </div>
        </div>
        
        <div class="role-selector">
          <span class="role-label">选择登录角色</span>
          <div class="role-buttons">
            <button 
              v-for="role in roles" 
              :key="role.id"
              :class="['role-btn', { active: selectedRole === role.id }]"
              @click="selectedRole = role.id"
            >
              <span class="role-icon">{{ role.icon }}</span>
              <span class="role-name">{{ role.name }}</span>
              <span class="role-desc">{{ role.desc }}</span>
            </button>
          </div>
        </div>
        
        <div class="login-tabs">
          <div 
            class="login-tab" 
            :class="{ active: loginType === 'sms' }"
            @click="loginType = 'sms'"
          >
            验证码登录
          </div>
          <div 
            class="login-tab" 
            :class="{ active: loginType === 'password' }"
            @click="loginType = 'password'"
          >
            密码登录
          </div>
        </div>
        
        <el-form 
          :model="loginForm" 
          :rules="rules" 
          ref="loginFormRef" 
          class="login-form"
          :validate-on-rule-change="false"
        >
          <div class="form-content">
            <el-form-item v-if="loginType === 'sms'" prop="phone" class="form-item code-form-item">
              <div class="input-wrapper">
                <div class="input-icon phone-icon">📱</div>
                <el-input 
                  v-model="loginForm.phone" 
                  placeholder="请输入手机号"
                  class="custom-input"
                  :prefix-icon="''"
                  @focus="inputFocus('phone')"
                  @blur="inputBlur('phone')"
                  :class="{ 'input-focused': focusField === 'phone', 'input-error': errorFields.phone }"
                ></el-input>
              </div>
              <el-button 
                :disabled="countdown > 0"
                @click="handleSendCode"
                class="code-button"
                :class="{ 'code-button-active': countdown === 0 }"
              >
                {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
              </el-button>
            </el-form-item>
            
            <el-form-item v-if="loginType === 'sms'" prop="code" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon code-icon">🔑</div>
                <el-input 
                  v-model="loginForm.code" 
                  placeholder="请输入验证码"
                  class="custom-input"
                  :prefix-icon="''"
                  @focus="inputFocus('code')"
                  @blur="inputBlur('code')"
                  :class="{ 'input-focused': focusField === 'code', 'input-error': errorFields.code }"
                ></el-input>
              </div>
            </el-form-item>
            
            <el-form-item v-if="loginType === 'password'" prop="password" class="form-item">
              <div class="input-wrapper">
                <div class="input-icon password-icon">🔒</div>
                <el-input 
                  v-model="loginForm.password" 
                  type="password"
                  placeholder="请输入密码"
                  class="custom-input"
                  :prefix-icon="''"
                  @focus="inputFocus('password')"
                  @blur="inputBlur('password')"
                  :class="{ 'input-focused': focusField === 'password', 'input-error': errorFields.password }"
                  show-password
                ></el-input>
              </div>
            </el-form-item>
            
            <el-form-item class="form-item login-btn-container">
              <el-button 
                type="primary" 
                @click="handleLogin"
                class="login-button"
                :loading="loading"
              >
                <span v-if="!loading">登录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
            
          </div>
        </el-form>
        
        <div style="margin-top: 16px; text-align: center;">
          <button @click="handleRegisterClick" class="register-button">
            没有账号？立即注册
          </button>
        </div>
        
        <div class="login-footer">
          <div class="customer-service">
            <div class="service-icon">📞</div>
            <span>如有问题请联系客服：<a href="tel:400-888-8888" class="service-phone">400-888-8888</a></span>
          </div>
          <div class="system-info">
            <span>© 2026 农翼通 版权所有</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onBeforeUnmount, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import axios from '../utils/axios'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const loginFormRef = ref(null)
    const loading = ref(false)
    const countdown = ref(0)
    const focusField = ref('')
    const errorFields = reactive({ phone: false, code: false, password: false })
    let timer = null
    
    const loginType = ref('sms')
    
    const roles = [
      { id: 'farmer', name: '农户', icon: '🌾', desc: '发布作业需求' },
      { id: 'flyer', name: '飞手', icon: '🚁', desc: '接单赚收益' },
      { id: 'owner', name: '机主', icon: '🏭', desc: '共享闲置设备' }
    ]
    
    const selectedRole = ref('farmer')
    
    const loginForm = reactive({
      phone: '',
      code: '',
      password: ''
    })
    
    const rules = computed(() => {
      const baseRules = {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
        ]
      }
      
      if (loginType.value === 'sms') {
        baseRules.code = [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { len: 6, message: '验证码为6位数字', trigger: 'blur' },
          { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' }
        ]
      } else {
        baseRules.password = [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { pattern: /^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]{8,20}$/, message: '密码必须包含数字和字母，长度8-20位', trigger: 'blur' }
        ]
      }
      
      return baseRules
    })
    
    const inputFocus = (field) => {
      focusField.value = field
      errorFields[field] = false
    }
    
    const inputBlur = (field) => {
      focusField.value = ''
      loginFormRef.value?.validateField(field, (error) => {
        if (error) {
          errorFields[field] = true
        }
      })
    }
    
    const handleSendCode = async () => {
      try {
        await loginFormRef.value.validateField('phone')
        
        if (!loginForm.phone) {
          errorFields.phone = true
          return
        }
        
        const response = await axios.post('/auth/send-code', null, {
          params: {
            phone: loginForm.phone,
            role: selectedRole.value
          }
        })
        
        startCountdown()
        
        ElMessage.success({
          message: '验证码已发送，请注意查收',
          duration: 2000,
          showClose: true
        })
        
        setTimeout(() => {
          const codeInput = document.querySelector('.code-input input')
          if (codeInput) codeInput.focus()
        }, 300)
        
      } catch (error) {
        console.error('发送验证码失败:', error)
        ElMessage.error({
          message: error.message || '发送验证码失败，请稍后重试',
          duration: 2000,
          showClose: true
        })
      }
    }
    
    const startCountdown = () => {
      countdown.value = 60
      
      if (timer) {
        clearInterval(timer)
      }
      
      timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
          timer = null
        }
      }, 1000)
    }
    
    const handleLogin = async () => {
      try {
        await loginFormRef.value.validate();
        loading.value = true;
        
        if (!['sms', 'password'].includes(loginType.value)) {
          throw new Error('不支持的登录方式');
        }
        
        const loginData = loginType.value === 'sms' 
          ? { phone: loginForm.phone, code: loginForm.code, role: selectedRole.value, loginType: 'code' }
          : { phone: loginForm.phone, password: loginForm.password, role: selectedRole.value, loginType: 'password' };
        
        await userStore.login(loginData);
        
        ElMessage.success('登录成功，正在跳转...');
        setTimeout(() => {
          router.push('/dashboard');
        }, 1000);
      } catch (error) {
        console.error('登录失败:', error);
        ElMessage.error(error.message || '登录失败，请稍后重试');
        Object.keys(errorFields).forEach(key => {
          errorFields[key] = false;
        });
      } finally {
        loading.value = false;
      }
    }
    
    onBeforeUnmount(() => {
      if (timer) {
        clearInterval(timer)
      }
    })
    
    const handleRegisterClick = () => {
      userStore.logout()
      router.push('/register')
    }
    
    return {
      loginForm,
      rules,
      loginType,
      loginFormRef,
      loading,
      countdown,
      focusField,
      errorFields,
      roles,
      selectedRole,
      handleSendCode,
      handleLogin,
      inputFocus,
      inputBlur,
      handleRegisterClick
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2E8B57 0%, #1E6B43 50%, #3B82F6 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.login-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 8s ease-in-out infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 10%;
  transform: translateY(-50%);
  animation-delay: 4s;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 480px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
}

.login-wrapper {
  padding: 40px;
  position: relative;
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
  position: relative;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.logo-icon {
  font-size: 48px;
  background: linear-gradient(135deg, #409EFF, #66B1FF);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3);
  animation: bounce 2s ease-in-out infinite;
}

.logo-text h1 {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 4px 0;
  background: linear-gradient(135deg, #303133, #606266);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-text p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.role-selector {
  margin-bottom: 20px;
}

.role-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  display: block;
  text-align: center;
}

.role-buttons {
  display: flex;
  gap: 10px;
}

.role-btn {
  flex: 1;
  padding: 14px 8px;
  background: #fafafa;
  border: 2px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.role-btn.active {
  background: rgba(46, 139, 87, 0.1);
  border-color: #2E8B57;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(46, 139, 87, 0.2);
}

.role-icon {
  font-size: 28px;
}

.role-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.role-btn.active .role-name {
  color: #2E8B57;
}

.role-desc {
  font-size: 11px;
  color: #909399;
}

.login-tabs {
  display: flex;
  margin-bottom: 24px;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 4px;
}

.login-tab {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.login-tab.active {
  background: #ffffff;
  color: #2E8B57;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
}

.login-tab:hover:not(.active) {
  color: #2E8B57;
}

.login-form {
  width: 100%;
}

.form-content {
  position: relative;
}

.form-item {
  margin-bottom: 24px !important;
  position: relative;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.3s ease;
  height: 50px;
  overflow: hidden;
}

.input-wrapper:focus-within {
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.input-icon {
  position: absolute;
  left: 16px;
  z-index: 1;
  font-size: 20px;
  color: #909399;
  transition: all 0.3s ease;
}

.custom-input {
  height: 100%;
  border: none;
  border-radius: 12px;
  padding-left: 50px !important;
  padding-right: 16px;
  font-size: 15px;
  color: #303133;
  background: transparent;
  transition: all 0.3s ease;
}

.custom-input:focus {
  box-shadow: none;
  border-color: transparent;
}

.custom-input::placeholder {
  color: #c0c4cc;
  font-size: 14px;
}

.custom-input.input-focused {
  color: #2E8B57;
}

.custom-input.input-error {
  color: #f56c6c;
}

.input-wrapper:focus-within .input-icon {
  color: #2E8B57;
}

.code-form-item {
  display: flex;
  gap: 12px;
  align-items: center;
}

.code-form-item .input-wrapper {
  flex: 1;
}

.code-button {
  height: 44px;
  padding: 0 20px;
  border-radius: 8px;
  font-size: 14px;
  color: #909399;
  background: #f0f0f0;
  border: none;
  transition: all 0.3s ease;
  min-width: 130px;
}

.code-button:hover:not(:disabled) {
  background: #ecfdf5;
  color: #2E8B57;
}

.code-button-active {
  background: #f0fdf4;
  color: #2E8B57;
}

.login-btn-container {
  margin-bottom: 24px !important;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #2E8B57, #1E6B43);
  border: none;
  box-shadow: 0 4px 16px rgba(46, 139, 87, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.login-button:hover::before {
  left: 100%;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(46, 139, 87, 0.4);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.register-button {
  color: #2E8B57;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 10;
  cursor: pointer;
  display: inline-block;
  background: none;
  border: none;
}

.register-button:hover {
  color: #16A34A;
  background-color: rgba(46, 139, 87, 0.05);
}

.login-footer {
  text-align: center;
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
  margin-top: 16px;
}

.customer-service {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #606266;
}

.service-icon {
  font-size: 14px;
}

.service-phone {
  color: #2E8B57;
  text-decoration: none;
  font-weight: 500;
}

.service-phone:hover {
  color: #16A34A;
  text-decoration: underline;
}

.system-info {
  font-size: 12px;
  color: #909399;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@media (max-width: 768px) {
  .login-container {
    padding: 16px;
  }
  
  .login-wrapper {
    padding: 32px 24px;
  }
  
  .logo-container {
    flex-direction: column;
    gap: 12px;
  }
  
  .logo-icon {
    font-size: 40px;
    padding: 12px;
  }
  
  .logo-text h1 {
    font-size: 20px;
  }
  
  .role-btn {
    padding: 12px 6px;
  }
  
  .role-icon {
    font-size: 24px;
  }
  
  .role-name {
    font-size: 13px;
  }
  
  .role-desc {
    font-size: 10px;
  }
  
  .form-item {
    margin-bottom: 20px !important;
  }
  
  .input-wrapper {
    height: 46px;
  }
  
  .custom-input {
    font-size: 14px;
  }
  
  .login-button {
    height: 46px;
    font-size: 15px;
  }
  
  .code-button {
    min-width: 110px;
    font-size: 12px;
  }
  
  .login-footer {
    padding-top: 16px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 12px;
  }
  
  .login-wrapper {
    padding: 24px 20px;
  }
  
  .logo-icon {
    font-size: 36px;
    padding: 10px;
  }
  
  .logo-text h1 {
    font-size: 18px;
  }
  
  .role-buttons {
    gap: 6px;
  }
  
  .role-btn {
    padding: 10px 4px;
  }
  
  .role-icon {
    font-size: 20px;
  }
  
  .role-name {
    font-size: 12px;
  }
  
  .role-desc {
    font-size: 9px;
  }
  
  .form-item {
    margin-bottom: 16px !important;
  }
  
  .input-wrapper {
    height: 44px;
  }
  
  .login-button {
    height: 44px;
    font-size: 14px;
  }
  
  .login-btn-container {
    margin-bottom: 20px !important;
  }
  
  .customer-service {
    font-size: 12px;
  }
  
  .system-info {
    font-size: 11px;
  }
}
</style>