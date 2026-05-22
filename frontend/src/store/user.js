import { defineStore } from 'pinia'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'
import router, { addRoleRoutes } from '../router'

export const ROLES = {
  FARMER: 'farmer',
  FLYER: 'flyer',
  OWNER: 'owner'
}

export const ROLE_LABELS = {
  [ROLES.FARMER]: '农户',
  [ROLES.FLYER]: '飞手',
  [ROLES.OWNER]: '机主'
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    isLoggedIn: !!localStorage.getItem('token'),
    role: localStorage.getItem('role') || ''
  }),
  
  getters: {
    roleLabel: (state) => {
      return ROLE_LABELS[state.role] || '未知角色'
    },
    
    isFarmer: (state) => state.role === ROLES.FARMER,
    isFlyer: (state) => state.role === ROLES.FLYER,
    isOwner: (state) => state.role === ROLES.OWNER
  },
  
  actions: {
    async login(userData) {
      try {
        const response = await axios.post('/auth/login', userData)
        
        let token, userInfo, role
        
        if (response.data.data) {
          token = response.data.data.token
          userInfo = response.data.data.user || response.data.data.userInfo
          role = response.data.data.role || userData.role || 'farmer'
        } else {
          token = response.data.token
          userInfo = response.data.user || response.data.userInfo
          role = response.data.role || userData.role || 'farmer'
        }
        
        if (userInfo && userInfo.id && !userInfo.userId) {
          userInfo.userId = userInfo.id
        }
        
        console.log('登录成功获取到的token:', token)
        console.log('登录成功获取到的userInfo:', userInfo)
        console.log('登录角色:', role)
        
        this.token = token
        this.userInfo = userInfo || {}
        this.isLoggedIn = !!token
        this.role = role
        
        localStorage.setItem('token', token)
        localStorage.setItem('userInfo', JSON.stringify(userInfo || {}))
        localStorage.setItem('role', role)
        
        addRoleRoutes(role)
        
        console.log('登录状态已设置:', this.isLoggedIn)
        return response
      } catch (error) {
        console.error('登录API调用失败:', error)
        const errorMsg = error.response?.data?.message || error.response?.data?.msg || error.message || '登录失败'
        ElMessage.error(errorMsg)
        throw new Error(errorMsg)
      }
    },
    
    async sendCode(phone, role) {
      try {
        const response = await axios.post('/auth/send-code', null, {
          params: { phone, role }
        })
        ElMessage.success(response.msg || '验证码发送成功')
        return response
      } catch (error) {
        throw error
      }
    },
    
    logout() {
      this.token = ''
      this.userInfo = {}
      this.isLoggedIn = false
      this.role = ''
      
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('role')
      
      router.push('/login')
    },
    
    checkToken() {
      return this.checkTokenValidity(localStorage.getItem('token'))
    },
    
    checkTokenValidity(token) {
      return new Promise((resolve, reject) => {
        if (!token) {
          this.isLoggedIn = false
          this.userInfo = {}
          this.role = ''
          resolve(false)
          return
        }
        
        if (import.meta.env.DEV) {
          console.log('开发环境：信任非空token')
          this.isLoggedIn = true
          this.role = localStorage.getItem('role') || 'farmer'
          
          if (!this.userInfo.userId) {
            this.userInfo = {
              userId: 1,
              name: '测试用户',
              phone: '13800138000'
            }
          }
          localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
          localStorage.setItem('role', this.role)
          
          addRoleRoutes(this.role)
          resolve(true)
          return
        }
        
        if (token === 'mock-token') {
          this.isLoggedIn = true
          this.role = localStorage.getItem('role') || 'farmer'
          this.userInfo = {
            userId: '1',
            name: '张三',
            phone: '13800138000'
          }
          localStorage.setItem('role', this.role)
          addRoleRoutes(this.role)
          resolve(true)
        } else if (token && token.length > 10) {
          this.isLoggedIn = true
          this.role = localStorage.getItem('role') || 'farmer'
          addRoleRoutes(this.role)
          resolve(true)
        } else {
          this.isLoggedIn = false
          this.userInfo = {}
          this.role = ''
          localStorage.removeItem('token')
          resolve(false)
        }
      })
    },
    
    async getUserInfo() {
      try {
        const response = await axios.get('/user/info')
        const userInfoData = response.data?.data || response.data
        
        if (userInfoData && !userInfoData.address && userInfoData.location) {
          userInfoData.address = userInfoData.location
        }
        
        if (userInfoData && userInfoData.id && !userInfoData.userId) {
          userInfoData.userId = userInfoData.id
        }
        
        this.userInfo = userInfoData
        localStorage.setItem('userInfo', JSON.stringify(userInfoData))
        return response
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.logout()
        throw error
      }
    },
    
    async switchRole(newRole) {
      if (!Object.values(ROLES).includes(newRole)) {
        throw new Error('无效的角色')
      }
      
      this.role = newRole
      localStorage.setItem('role', newRole)
      
      addRoleRoutes(newRole)
      
      await router.push(`/${newRole}/dashboard`)
      
      ElMessage.success(`已切换为${ROLE_LABELS[newRole]}角色`)
    }
  }
})