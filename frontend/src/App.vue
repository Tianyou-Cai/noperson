<template>
  <div class="app">
    <!-- 首页 -->
    <template v-if="isHomePage">
      <router-view />
    </template>
    <!-- 登录/注册页面 -->
    <template v-else-if="isLoginPage">
      <router-view />
    </template>
    <!-- 其他页面显示布局组件 -->
    <template v-else>
      <el-container class="app-container">
        <!-- 侧边栏 -->
        <Sidebar />
        
        <el-container class="main-container">
          <!-- 顶部导航栏 -->
          <Navbar @toggle-sidebar="handleToggleSidebar" />
          
          <!-- 主内容区 -->
          <el-main class="content-area">
            <!-- 所有路由使用router-view -->
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </template>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'

export default {
  name: 'App',
  components: {
    Sidebar,
    Navbar
  },
  setup() {
    const route = useRoute()
    
    // 判断当前是否为首页
    const isHomePage = computed(() => {
      return route.path === '/'
    })
    
    // 判断当前是否为登录或注册页面
    const isLoginPage = computed(() => {
      return route.path === '/login' || route.path === '/register'
    })
    
    // 缓存的页面组件名称
    const cachedViews = computed(() => {
      return ['Dashboard', 'Profile', 'Devices', 'Qualification']
    })
    
    // 处理侧边栏折叠
    const handleToggleSidebar = () => {
      // 侧边栏的折叠状态由Sidebar组件内部管理
      console.log('Toggle sidebar')
    }
    
    return {
      isHomePage,
      isLoginPage,
      cachedViews,
      handleToggleSidebar,
      route
    }
  }
}</script>

<style>
/* 全局CSS变量 */
:root {
  --primary-color: #409EFF;
  --primary-hover: #66b1ff;
  --primary-active: #3a8ee6;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --info-color: #909399;
  --bg-color: #f5f7fa;
  --card-bg: #ffffff;
  --text-primary: #303133;
  --text-regular: #606266;
  --text-secondary: #909399;
  --text-placeholder: #c0c4cc;
  --border-color: #ebeef5;
  --box-shadow-base: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  --box-shadow-light: 0 1px 4px rgba(0, 0, 0, 0.08);
  --transition-base: all 0.3s ease;
}

/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-primary);
}

#app {
  min-height: 100vh;
  background-color: var(--bg-color);
}

.app {
  min-height: 100vh;
}

/* 过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 应用容器 */
.app-container {
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: row;
}

/* 主容器 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

/* 内容区域 */
.content-area {
  padding: 24px;
  overflow-y: auto;
  height: calc(100vh - 64px);
  background: linear-gradient(135deg, var(--bg-color) 0%, #eef2f7 100%);
  transition: all 0.3s ease;
}

/* 卡片样式 */
.card {
  background: var(--card-bg);
  border-radius: 8px;
  box-shadow: var(--box-shadow-light);
  padding: 20px;
  transition: var(--transition-base);
}

.card:hover {
  box-shadow: var(--box-shadow-base);
  transform: translateY(-2px);
}

/* 按钮样式增强 */
.el-button {
  transition: var(--transition-base);
  font-weight: 500;
}

.el-button--primary {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.el-button--primary:hover {
  background-color: var(--primary-hover);
  border-color: var(--primary-hover);
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
  transition: background 0.3s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-area {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .content-area {
    padding: 16px;
  }
  
  .card {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .content-area {
    padding: 12px;
  }
  
  .card {
    padding: 12px;
    border-radius: 6px;
  }
}

/* 工具类 */
.text-center {
  text-align: center;
}

.mt-20 {
  margin-top: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.p-0 {
  padding: 0 !important;
}
</style>