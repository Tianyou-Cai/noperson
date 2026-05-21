<template>
  <el-header class="navbar">
    <div class="navbar-gradient"></div>
    <div class="navbar-content">
      <div class="left-section">
        <el-button 
          type="text" 
          class="menu-button"
          @click="toggleSidebar"
          size="large"
        >
          <el-icon class="menu-icon"><Menu /></el-icon>
        </el-button>
        <h1 class="page-title" v-if="currentPageTitle">
          <span class="title-icon">{{ getPageIcon(currentPageTitle) }}</span>
          <span>{{ currentPageTitle }}</span>
        </h1>
      </div>
      
      <div class="right-section">
        <el-dropdown trigger="click" class="role-switch-dropdown" placement="bottom" v-if="userStore.isLoggedIn">
          <div class="role-switch-btn">
            <el-icon class="role-icon"><SwitchButton /></el-icon>
            <span class="role-text">{{ userStore.roleLabel }}</span>
            <el-icon class="arrow-icon"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="role-switch-menu">
              <div class="role-switch-header">
                <span>切换角色</span>
              </div>
              <el-dropdown-item 
                v-for="role in roles" 
                :key="role.id"
                :class="{ 'role-active': userStore.role === role.id }"
                @click="handleSwitchRole(role.id)"
              >
                <span class="role-item-icon">{{ role.icon }}</span>
                <span class="role-item-name">{{ role.name }}</span>
                <span class="role-item-desc">{{ role.desc }}</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-dropdown trigger="click" class="notification-dropdown" placement="bottom-end" v-if="userStore.isLoggedIn">
          <div class="notification-button">
            <el-icon class="bell-icon"><Bell /></el-icon>
            <el-badge 
              :value="notificationCount" 
              class="notification-badge" 
              :max="99"
              v-if="notificationCount > 0"
            />
          </div>
          <template #dropdown>
            <el-dropdown-menu class="notification-dropdown-menu">
              <div class="notification-header">
                <span class="notification-title">通知消息</span>
                <el-button 
                  type="text" 
                  size="small" 
                  @click="markAllAsRead"
                  class="mark-all-btn"
                >
                  全部已读
                </el-button>
              </div>
              
              <div v-if="notifications.length === 0" class="empty-notification">
                <el-empty description="暂无通知" class="empty-content" />
              </div>
              
              <transition-group name="notification">
                <el-dropdown-item
                  v-for="notification in notifications"
                  :key="notification.id"
                  class="notification-item"
                  :class="{ 'unread': !notification.read }"
                  @click="markAsRead(notification.id)"
                >
                  <div class="notification-content">
                    <div class="notification-badge-dot" v-if="!notification.read"></div>
                    <div class="notification-body">
                      <div class="notification-title-text">{{ notification.title }}</div>
                      <div class="notification-desc">{{ notification.description }}</div>
                      <div class="notification-time">{{ formatTime(notification.time) }}</div>
                    </div>
                  </div>
                </el-dropdown-item>
              </transition-group>
              
              <div class="notification-footer" v-if="notifications.length > 0">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="viewAllNotifications"
                  class="view-all-btn"
                >
                  查看全部
                </el-button>
              </div>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-dropdown trigger="click" class="user-dropdown" placement="bottom-end">
          <div class="user-info" @click.stop>
            <el-avatar 
              :size="40" 
              :src="userAvatar"
              class="user-avatar"
              :class="{ 'not-logged-avatar': !userStore.isLoggedIn }"
            >
              {{ userInitial }}
            </el-avatar>
            <div class="user-details">
              <span class="user-name">{{ userName }}</span>
              <span class="user-role" v-if="userStore.roleLabel">{{ userStore.roleLabel }}后台</span>
            </div>
            <el-icon class="arrow-icon"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown-menu">
              <template v-if="userStore.isLoggedIn">
                <el-dropdown-item @click="navigateToProfile" class="dropdown-item">
                  <el-icon class="item-icon"><User /></el-icon>
                  <span>个人资料</span>
                </el-dropdown-item>
                <el-dropdown-item @click="navigateToSettings" class="dropdown-item">
                  <el-icon class="item-icon"><Setting /></el-icon>
                  <span>系统设置</span>
                </el-dropdown-item>
                <el-dropdown-item class="dropdown-item" @click="showAbout">
                  <el-icon class="item-icon"><InfoFilled /></el-icon>
                  <span>关于系统</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout" class="dropdown-item logout-item">
                  <el-icon class="item-icon logout-icon"><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </template>
              <template v-else>
                <el-dropdown-item @click="handleLogin" class="dropdown-item login-item">
                  <el-icon class="item-icon"><User /></el-icon>
                  <span>登录</span>
                </el-dropdown-item>
                <el-dropdown-item @click="handleRegister" class="dropdown-item">
                  <el-icon class="item-icon"><Plus /></el-icon>
                  <span>注册</span>
                </el-dropdown-item>
                <el-dropdown-item class="dropdown-item" @click="showAbout">
                  <el-icon class="item-icon"><InfoFilled /></el-icon>
                  <span>关于系统</span>
                </el-dropdown-item>
              </template>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { Bell, ArrowDown, User, Setting, SwitchButton, Menu, InfoFilled, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'Navbar',
  components: {
    Bell,
    ArrowDown,
    User,
    Setting,
    SwitchButton,
    Menu,
    InfoFilled,
    Plus
  },
  emits: ['toggle-sidebar'],
  setup(props, { emit }) {
    const route = useRoute()
    const router = useRouter()
    const userStore = useUserStore()
    
    const roles = [
      { id: 'farmer', name: '农户', icon: '🌾', desc: '发布作业需求' },
      { id: 'flyer', name: '飞手', icon: '🚁', desc: '接单赚收益' },
      { id: 'owner', name: '机主', icon: '🏭', desc: '共享闲置设备' }
    ]
    
    const handleLogin = () => {
      router.push('/login')
    }
    
    const handleRegister = () => {
      router.push('/register')
    }
    
    const notificationCount = ref(3)
    const notifications = ref([
      {
        id: 1,
        title: '资质审核通过',
        description: '您的无人机驾驶证已通过审核，可以开始飞行作业',
        time: '2024-01-20 14:30:00',
        read: false
      },
      {
        id: 2,
        title: '设备固件更新',
        description: '您的设备DJI Mavic 3有新的固件版本可以更新',
        time: '2024-01-19 09:15:00',
        read: true
      },
      {
        id: 3,
        title: '飞行区域限制',
        description: '您计划的飞行区域已被临时限制，请重新规划路线',
        time: '2024-01-18 16:45:00',
        read: false
      }
    ])
    
    const currentPageTitle = computed(() => {
      const path = route.path
      const titleMap = {
        '/dashboard': userStore.isFarmer ? '农户仪表盘' : userStore.isFlyer ? '飞手仪表盘' : userStore.isOwner ? '机主仪表盘' : '仪表盘',
        '/profile': '个人资料',
        '/qualification': '资质管理',
        '/devices': '设备管理',
        '/devices/add': '添加设备',
        '/device-activities': '设备动态',
        '/demand-list': '需求列表',
        '/publish-demand': '发布需求',
        '/my-demands': '我的需求',
        '/order-list': '订单列表',
        '/accept-order': '接单中心',
        '/equipment': '设备租用',
        '/rental-apply': '租借申请',
        '/chat': '消息中心',
        '/wallet': '钱包'
      }
      
      for (const [key, title] of Object.entries(titleMap)) {
        if (path.startsWith(key)) {
          return title
        }
      }
      return '农翼通'
    })
    
    const userName = computed(() => {
      if (userStore.isLoggedIn && userStore.userInfo) {
        return userStore.userInfo.username || userStore.userInfo.name || '用户'
      }
      return '未登录'
    })
    
    const userAvatar = computed(() => {
      return userStore.userInfo?.avatar || ''
    })
    
    const userInitial = computed(() => {
      if (userStore.userInfo && userStore.userInfo.username) {
        return userStore.userInfo.username.charAt(0).toUpperCase()
      }
      return 'U'
    })
    
    const getPageIcon = (title) => {
      const iconMap = {
        '农户仪表盘': '🌾',
        '飞手仪表盘': '🚁',
        '机主仪表盘': '🏭',
        '仪表盘': '📊',
        '个人资料': '👤',
        '资质管理': '📋',
        '设备管理': '🛠️',
        '添加设备': '➕',
        '设备动态': '📈',
        '需求列表': '📝',
        '发布需求': '✏️',
        '我的需求': '📋',
        '订单列表': '📦',
        '接单中心': '🤝',
        '设备租用': '🔧',
        '租借申请': '📝',
        '消息中心': '💬',
        '钱包': '💰'
      }
      return iconMap[title] || ''
    }
    
    const toggleSidebar = () => {
      emit('toggle-sidebar')
      const button = event.target.closest('.menu-button')
      if (button) {
        button.classList.add('menu-button--clicked')
        setTimeout(() => {
          button.classList.remove('menu-button--clicked')
        }, 200)
      }
    }
    
    const markAsRead = (id) => {
      const notification = notifications.value.find(n => n.id === id)
      if (notification && !notification.read) {
        const item = document.querySelector(`[key="${id}"]`)
        if (item) {
          item.style.transition = 'all 0.3s ease'
          item.style.opacity = '0.6'
          setTimeout(() => {
            item.style.opacity = '1'
          }, 300)
        }
        
        notification.read = true
        notificationCount.value = notifications.value.filter(n => !n.read).length
        ElMessage.success('已标记为已读', { duration: 1500 })
      }
    }
    
    const markAllAsRead = () => {
      notifications.value.forEach(n => {
        n.read = true
      })
      notificationCount.value = 0
      ElMessage.success('全部已标记为已读', { duration: 1500 })
    }
    
    const viewAllNotifications = () => {
      ElMessage.info('跳转到通知中心')
    }
    
    const navigateToProfile = () => {
      router.push('/profile')
    }
    
    const navigateToSettings = () => {
      ElMessage.info('系统设置功能开发中')
    }
    
    const showAbout = () => {
      ElMessageBox.alert(
        '<div class="about-content">' +
        '<h3>农翼通 - 无人机共享平台</h3>' +
        '<p>版本: 1.0.0</p>' +
        '<p>© 2026 农翼通团队</p>' +
        '<p>连接农户、飞手与机主，一站式共享无人机服务</p>' +
        '</div>',
        '关于系统',
        {
          dangerouslyUseHTMLString: true,
          confirmButtonText: '确定',
          center: true
        }
      )
    }
    
    const handleLogout = () => {
      ElMessageBox.confirm(
        '确定要退出登录吗？',
        '退出确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          center: true
        }
      ).then(() => {
        userStore.logout()
        router.push('/login')
        ElMessage.success('已退出登录', { duration: 1500 })
      }).catch(() => {
      })
    }
    
    const handleSwitchRole = async (roleId) => {
      if (roleId === userStore.role) {
        return
      }
      try {
        await userStore.switchRole(roleId)
      } catch (error) {
        ElMessage.error(error.message || '切换角色失败')
      }
    }
    
    const formatTime = (timeStr) => {
      const date = new Date(timeStr)
      const now = new Date()
      const diffMs = now - date
      const diffMins = Math.floor(diffMs / 60000)
      const diffHours = Math.floor(diffMins / 60)
      const diffDays = Math.floor(diffHours / 24)
      
      if (diffMins < 1) {
        return '刚刚'
      } else if (diffMins < 60) {
        return `${diffMins}分钟前`
      } else if (diffHours < 24) {
        return `${diffHours}小时前`
      } else if (diffDays < 7) {
        return `${diffDays}天前`
      } else {
        return date.toLocaleDateString()
      }
    }
    
    watch(() => route.path, (newPath) => {
    })
    
    return {
      currentPageTitle,
      userName,
      userAvatar,
      userInitial,
      notificationCount,
      notifications,
      roles,
      toggleSidebar,
      markAsRead,
      markAllAsRead,
      viewAllNotifications,
      navigateToProfile,
      navigateToSettings,
      showAbout,
      handleLogout,
      handleSwitchRole,
      formatTime,
      getPageIcon,
      userStore,
      handleLogin,
      handleRegister
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 56px;
  background-color: #ffffff;
  position: relative;
  z-index: 100;
  transition: all 0.3s ease;
  overflow: hidden;
}

.navbar-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.9) 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  z-index: -1;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 24px;
  position: relative;
  z-index: 1;
}

.left-section {
  display: flex;
  align-items: center;
  flex: 1;
}

.menu-button {
  margin-right: 20px;
  color: #606266;
  padding: 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
  border: none;
  background: transparent;
}

.menu-button:hover {
  background-color: #f5f7fa;
  color: #409EFF;
  transform: scale(1.05);
}

.menu-button--clicked {
  transform: scale(0.95);
}

.menu-icon {
  font-size: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 22px;
  opacity: 0.85;
}

.right-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.role-switch-dropdown {
  order: 1;
}

.role-switch-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.1) 0%, rgba(64, 158, 255, 0.05) 100%);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.role-switch-btn:hover {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.15) 0%, rgba(64, 158, 255, 0.1) 100%);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.role-icon {
  font-size: 16px;
  color: #409EFF;
}

.role-text {
  font-size: 14px;
  font-weight: 500;
  color: #409EFF;
}

.role-switch-menu {
  width: 280px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border: none;
  overflow: hidden;
}

.role-switch-header {
  padding: 12px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.role-switch-menu .el-dropdown-item {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f5f7fa;
}

.role-switch-menu .el-dropdown-item:last-child {
  border-bottom: none;
}

.role-switch-menu .el-dropdown-item:hover {
  background: #fafafa;
  padding-left: 20px;
}

.role-switch-menu .el-dropdown-item.role-active {
  background: rgba(64, 158, 255, 0.05);
}

.role-switch-menu .el-dropdown-item.role-active::before {
  content: '✓';
  color: #409EFF;
  font-weight: 600;
}

.role-item-icon {
  font-size: 24px;
}

.role-item-name {
  font-weight: 500;
  color: #303133;
}

.role-item-desc {
  font-size: 12px;
  color: #909399;
  margin-left: auto;
}

.notification-button {
  position: relative;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #606266;
}

.notification-button:hover {
  background-color: #f5f7fa;
  color: #409EFF;
  transform: scale(1.05);
}

.bell-icon {
  font-size: 20px;
}

.notification-badge {
  position: absolute;
  top: 0;
  right: 0;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(245, 108, 108, 0.4);
  }
  70% {
    transform: scale(1.05);
    box-shadow: 0 0 0 6px rgba(245, 108, 108, 0);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(245, 108, 108, 0);
  }
}

.notification-dropdown-menu {
  width: 380px;
  padding: 0;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border: none;
  overflow: hidden;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fafafa;
}

.notification-title {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
}

.mark-all-btn {
  color: #409EFF;
  padding: 0;
  font-size: 12px;
  height: auto;
  line-height: normal;
}

.mark-all-btn:hover {
  color: #66b1ff;
  background-color: transparent;
}

.empty-notification {
  padding: 40px 20px;
  text-align: center;
}

.empty-content {
  margin: 0;
}

.notification-item {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.notification-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background-color: transparent;
  transition: background-color 0.3s ease;
}

.notification-item:hover {
  background-color: #fafafa;
  transform: translateX(2px);
}

.notification-item.unread {
  background-color: #f0f9ff;
}

.notification-item.unread::before {
  background-color: #409EFF;
}

.notification-content {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.notification-badge-dot {
  width: 8px;
  height: 8px;
  background-color: #409EFF;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
}

.notification-body {
  flex: 1;
  min-width: 0;
}

.notification-title-text {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  font-size: 14px;
}

.notification-desc {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-footer {
  padding: 16px 20px;
  text-align: center;
  background-color: #fafafa;
}

.view-all-btn {
  width: 100%;
  font-size: 14px;
}

.notification-enter-active,
.notification-leave-active {
  transition: all 0.3s ease;
}

.notification-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.notification-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.user-info:hover {
  background-color: #f5f7fa;
  transform: translateY(-1px);
}

.user-avatar {
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.user-info:hover .user-avatar {
  transform: scale(1.05);
  border-color: #409EFF;
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
}

.user-role {
  font-size: 12px;
  color: #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
  padding: 1px 6px;
  border-radius: 10px;
}

.arrow-icon {
  font-size: 16px;
  color: #909399;
  transition: transform 0.3s ease;
}

.user-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border: none;
  overflow: hidden;
  padding: 4px 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #606266;
}

.dropdown-item:hover {
  background-color: #f5f7fa;
  color: #409EFF;
  padding-left: 24px;
}

.item-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.logout-item {
  color: #f56c6c;
}

.logout-item:hover {
  background-color: #fef0f0;
  color: #f56c6c;
}

.logout-icon {
  color: #f56c6c;
}

:deep(.about-content) {
  text-align: center;
}

:deep(.about-content h3) {
  margin-bottom: 12px;
  color: #303133;
}

:deep(.about-content p) {
  margin-bottom: 8px;
  color: #606266;
  line-height: 1.6;
}

@media (max-width: 1200px) {
  .notification-dropdown-menu {
    width: 340px;
  }
}

@media (max-width: 768px) {
  .navbar-content {
    padding: 0 16px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  .title-icon {
    font-size: 20px;
  }
  
  .right-section {
    gap: 12px;
  }
  
  .role-switch-btn {
    padding: 6px 12px;
  }
  
  .role-text {
    display: none;
  }
  
  .user-details {
    display: none;
  }
  
  .notification-dropdown-menu {
    width: 300px;
    max-width: 90vw;
  }
  
  .notification-header {
    padding: 12px 16px;
  }
  
  .notification-item {
    padding: 12px 16px;
  }
}

@media (max-width: 480px) {
  .navbar-content {
    padding: 0 12px;
  }
  
  .page-title {
    font-size: 16px;
    gap: 8px;
  }
  
  .menu-button {
    margin-right: 12px;
    padding: 6px;
  }
  
  .right-section {
    gap: 8px;
  }
  
  .role-switch-btn {
    padding: 4px 8px;
  }
  
  .role-icon {
    font-size: 14px;
  }
}

.not-logged-avatar {
  background-color: #dcdfe6 !important;
  color: #909399 !important;
}

.login-item {
  color: var(--primary-color) !important;
}

.login-item:hover {
  background-color: var(--primary-color-light) !important;
}
</style>