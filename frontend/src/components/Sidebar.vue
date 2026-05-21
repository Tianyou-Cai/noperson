<template>
  <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar" :class="{ 'sidebar--collapsed': isCollapse }">
    <div class="logo-wrapper">
      <div class="logo" :class="{ 'logo--collapsed': isCollapse }">
        <svg class="logo-icon" viewBox="0 0 1024 1024" version="1.1">
          <path d="M512 0C229.23 0 0 229.23 0 512s229.23 512 512 512 512-229.23 512-512S794.77 0 512 0z" fill="#409EFF"/>
          <path d="M721.067 517.12a16 16 0 0 0 0-28.224L544 320l-177.067 168.896a16 16 0 0 0 0 28.224L544 640l177.067-122.88z" fill="#FFFFFF"/>
          <path d="M282.667 405.333c-17.728 0-32 14.272-32 32s14.272 32 32 32 32-14.272 32-32-14.272-32-32-32z" fill="#FFFFFF"/>
          <path d="M736 405.333c-17.728 0-32 14.272-32 32s14.272 32 32 32 32-14.272 32-32-14.272-32-32-32z" fill="#FFFFFF"/>
          <path d="M512 704c-17.728 0-32 14.272-32 32s14.272 32 32 32 32-14.272 32-32-14.272-32-32-32z" fill="#FFFFFF"/>
        </svg>
        <span v-if="!isCollapse" class="logo-text">农翼通</span>
      </div>
    </div>
    
    <el-menu
      :collapse="isCollapse"
      :default-active="activePath"
      class="sidebar-menu"
      @select="handleSelect"
      :router="true"
      :collapse-transition="true"
      background-color="transparent"
      text-color="rgba(255, 255, 255, 0.65)"
      active-text-color="#409EFF"
    >
      <template v-for="menuItem in currentMenu" :key="menuItem.path">
        <el-menu-item v-if="!menuItem.children" :index="menuItem.path" class="menu-item">
          <template #icon>
            <el-icon class="menu-icon"><component :is="getIcon(menuItem.icon)" /></el-icon>
          </template>
          <template #title>
            <span class="menu-text">{{ menuItem.title }}</span>
          </template>
        </el-menu-item>
        
        <el-sub-menu v-else :index="menuItem.path" class="menu-item">
          <template #title>
            <el-icon class="menu-icon"><component :is="getIcon(menuItem.icon)" /></el-icon>
            <span class="menu-text">{{ menuItem.title }}</span>
          </template>
          <el-menu-item v-for="child in menuItem.children" :key="child.path" :index="child.path" class="submenu-item">
            <span class="menu-text">{{ child.title }}</span>
          </el-menu-item>
        </el-sub-menu>
      </template>
    </el-menu>
    
    <div class="toggle-button" @click="toggleCollapse" :class="{ 'toggle-button--collapsed': isCollapse }">
      <el-icon class="toggle-icon" :class="{ 'rotate': isCollapse }">
        <ArrowLeft />
      </el-icon>
    </div>
  </el-aside>
</template>

<script>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../store/user'
import {
  Document,
  User,
  Wallet,
  Setting,
  ChatDotRound,
  ArrowLeft,
  Message,
  Box,
  Plus,
  List,
  Briefcase,
  Bicycle
} from '@element-plus/icons-vue'

const iconMap = {
  Dashboard: Document,
  Profile: User,
  Wallet: Wallet,
  Setting: Setting,
  Chat: Message,
  FileText: Box,
  Plus: Plus,
  List: List,
  ClipboardList: Briefcase,
  Truck: Bicycle
}

const menus = {
  farmer: [
    { path: '/dashboard', title: '仪表盘', icon: 'Dashboard' },
    { path: '/profile', title: '个人资料', icon: 'User' },
    { path: '/demand-list', title: '需求列表', icon: 'List' },
    { path: '/publish-demand', title: '发布需求', icon: 'Plus' },
    { path: '/my-demands', title: '我的需求', icon: 'ClipboardList' },
    { path: '/chat', title: '消息中心', icon: 'Chat' },
    { path: '/wallet', title: '钱包', icon: 'Wallet' }
  ],
  flyer: [
    { path: '/dashboard', title: '仪表盘', icon: 'Dashboard' },
    { path: '/profile', title: '个人资料', icon: 'User' },
    { path: '/qualification', title: '资质管理', icon: 'FileText' },
    { path: '/order-list', title: '订单列表', icon: 'List' },
    { path: '/accept-order', title: '接单中心', icon: 'ClipboardList' },
    { path: '/equipment', title: '设备租用', icon: 'Truck' },
    { path: '/chat', title: '消息中心', icon: 'Chat' },
    { path: '/wallet', title: '钱包', icon: 'Wallet' }
  ],
  owner: [
    { path: '/dashboard', title: '仪表盘', icon: 'Dashboard' },
    { path: '/profile', title: '个人资料', icon: 'User' },
    {
      path: '/devices',
      title: '设备管理',
      icon: 'Setting',
      children: [
        { path: '/devices', title: '设备列表' },
        { path: '/devices/add', title: '添加设备' },
        { path: '/device-activities', title: '设备动态' }
      ]
    },
    { path: '/rental-apply', title: '租借申请', icon: 'ClipboardList' },
    { path: '/chat', title: '消息中心', icon: 'Chat' },
    { path: '/wallet', title: '钱包', icon: 'Wallet' }
  ]
}

export default {
  name: 'Sidebar',
  components: {
    Document,
    User,
    Wallet,
    Setting,
    ChatDotRound,
    ArrowLeft,
    Message,
    Box,
    Plus,
    List,
    Briefcase,
    Bicycle
  },
  setup() {
    const route = useRoute()
    const userStore = useUserStore()
    const isCollapse = ref(false)
    
    const currentMenu = computed(() => {
      return menus[userStore.role] || menus.farmer
    })
    
    const activePath = computed(() => {
      let path = route.path
      if (path.startsWith('/devices/')) {
        return '/devices'
      }
      if (path.startsWith('/device/') && !path.endsWith('/edit')) {
        return '/devices'
      }
      return path
    })
    
    const getIcon = (iconName) => {
      return iconMap[iconName] || Document
    }
    
    const toggleCollapse = () => {
      isCollapse.value = !isCollapse.value
      localStorage.setItem('sidebarCollapse', String(isCollapse.value))
    }
    
    const handleSelect = (key, keyPath) => {
      console.log('Menu selected:', key, keyPath)
    }
    
    onMounted(() => {
      const savedCollapse = localStorage.getItem('sidebarCollapse')
      if (savedCollapse !== null) {
        isCollapse.value = savedCollapse === 'true'
      }
    })
    
    watch(() => route.path, (newPath) => {
      console.log('Route changed to:', newPath)
    })
    
    watch(() => userStore.role, () => {
      console.log('Role changed, menu updated')
    })
    
    return {
      isCollapse,
      activePath,
      currentMenu,
      toggleCollapse,
      handleSelect,
      getIcon
    }
  }
}
</script>

<style scoped>
.sidebar {
  height: 100vh;
  background: linear-gradient(135deg, #001529 0%, #002855 100%);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at top right, rgba(64, 158, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.logo-wrapper {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  position: relative;
  z-index: 1;
}

.logo {
  display: flex;
  align-items: center;
  width: 100%;
  transition: all 0.3s ease;
  padding: 8px 0;
}

.logo--collapsed {
  justify-content: center;
}

.logo-icon {
  width: 32px;
  height: 32px;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.sidebar--collapsed .logo-icon {
  width: 28px;
  height: 28px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  white-space: nowrap;
  margin-left: 12px;
  background: linear-gradient(135deg, #fff 0%, #e0f7fa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 64px);
  padding: 16px 0;
  background: transparent;
}

.menu-item {
  transition: all 0.3s ease;
  position: relative;
  background: transparent !important;
  margin: 4px 0;
}

.menu-item:hover {
  background: rgba(64, 158, 255, 0.1) !important;
}

.menu-item.is-active {
  background: rgba(64, 158, 255, 0.15) !important;
}

.menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: #409EFF;
}

.menu-icon {
  font-size: 18px !important;
  transition: all 0.3s ease;
}

.menu-text {
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.submenu-item {
  padding-left: 50px !important;
  background: rgba(255, 255, 255, 0.02) !important;
  transition: all 0.3s ease;
}

.submenu-item:hover {
  background: rgba(255, 255, 255, 0.05) !important;
}

:deep(.el-menu) {
  background: transparent !important;
}

:deep(.el-sub-menu .el-menu) {
  background: rgba(0, 0, 0, 0.15) !important;
  border-right: none;
}

:deep(.el-sub-menu__title:hover) {
  background: rgba(64, 158, 255, 0.1) !important;
}

:deep(.el-sub-menu__title.is-active) {
  background: rgba(64, 158, 255, 0.15) !important;
}

:deep(.el-menu-item.is-active) {
  color: #409EFF !important;
}

.toggle-button {
  position: absolute;
  right: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 40px;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 100;
  box-shadow: 2px 0 8px rgba(64, 158, 255, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  outline: none;
}

.toggle-button:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #409EFF 100%);
  box-shadow: 2px 0 12px rgba(64, 158, 255, 0.4);
  transform: translateY(-50%) scale(1.05);
}

.toggle-button:active {
  transform: translateY(-50%) scale(0.95);
}

.toggle-icon {
  color: #ffffff;
  transition: transform 0.3s ease;
  font-size: 16px;
}

.toggle-icon.rotate {
  transform: rotate(180deg);
}

:deep(.el-menu-vertical__content) {
  overflow-y: auto;
  overflow-x: hidden;
}

:deep(.el-menu-vertical__content)::-webkit-scrollbar {
  width: 4px;
}

:deep(.el-menu-vertical__content)::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 2px;
}

:deep(.el-menu-vertical__content)::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  transition: background 0.3s ease;
}

:deep(.el-menu-vertical__content)::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
    transform: translateX(-100%);
  }
  
  .sidebar--mobile-open {
    transform: translateX(0);
  }
  
  .toggle-button {
    display: none;
  }
}
</style>