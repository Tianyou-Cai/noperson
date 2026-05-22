<template>
  <div class="dashboard flyer-dashboard">
    <!-- 顶部导航 -->
    <nav class="dashboard-navbar">
      <div class="navbar-content">
        <div class="navbar-left">
          <div class="logo" @click="router.push('/')">
            <span class="logo-icon">🚁</span>
            <span class="logo-text">农翼通商城</span>
          </div>
        </div>
        <div class="navbar-center">
          <div class="search-box">
            <input type="text" placeholder="搜索服务需求" class="search-input" />
            <button class="search-btn">🔍</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/service-list')">服务市场</button>
          <button class="nav-btn active">我的服务</button>
          <button class="nav-btn" @click="router.push('/flyer/profile')">
            <span class="avatar">👤</span>
          </button>
        </div>
      </div>
    </nav>

    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="user-info">
        <div class="user-avatar">✈️</div>
        <div class="user-detail">
          <h2 class="user-name">{{ userName }}</h2>
          <p class="user-role">✈️ 飞手用户</p>
        </div>
      </div>
      <div class="user-stats">
        <div class="user-stat-item">
          <span class="stat-value">{{ stats.completedOrders }}</span>
          <span class="stat-label">已完成订单</span>
        </div>
        <div class="user-stat-item">
          <span class="stat-value">{{ stats.rating }}</span>
          <span class="stat-label">服务评分</span>
        </div>
        <div class="user-stat-item">
          <span class="stat-value">¥{{ stats.totalEarnings }}</span>
          <span class="stat-label">累计收入</span>
        </div>
      </div>
    </div>

    <!-- 快捷统计 -->
    <div class="stats-section">
      <div class="section-header">
        <h3>服务数据概览</h3>
      </div>
      <div class="stats-grid">
        <div 
          v-for="stat in serviceStats" 
          :key="stat.key"
          :class="['stat-card', stat.key]"
          @click="goToSection(stat.key)"
        >
          <div class="stat-icon">{{ stat.icon }}</div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.count }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div class="stat-arrow">→</div>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="dashboard-content">
      <!-- 待处理订单 -->
      <div class="main-section">
        <div class="section-header">
          <h3>待处理订单</h3>
          <button class="view-all-btn" @click="goToOrders('pending')">查看全部 →</button>
        </div>
        <div class="orders-list">
          <div v-for="order in pendingOrders" :key="order.id" class="order-card">
            <div class="order-header">
              <div class="order-id">订单号: {{ order.id }}</div>
              <span :class="['order-status', order.status]">{{ getStatusLabel(order.status) }}</span>
            </div>
            <div class="order-content">
              <div class="order-info">
                <h4 class="order-title">{{ order.title }}</h4>
                <div class="order-meta">
                  <span class="meta-item">📍 {{ order.location }}</span>
                  <span class="meta-item">📅 {{ order.date }}</span>
                  <span class="meta-item">🌾 {{ order.crop }}</span>
                </div>
                <div class="order-detail">
                  <span>作业面积: {{ order.area }} 亩</span>
                  <span>服务类型: {{ order.serviceType }}</span>
                </div>
              </div>
              <div class="order-price">
                <span class="price-label">订单金额</span>
                <span class="price-value">¥{{ order.price }}</span>
              </div>
            </div>
            <div class="order-actions">
              <button class="action-btn primary" @click="acceptOrder(order.id)">接单</button>
              <button class="action-btn" @click="rejectOrder(order.id)">拒绝</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 侧边栏 -->
      <div class="sidebar">
        <!-- 快捷操作 -->
        <div class="quick-actions">
          <h3>快捷操作</h3>
          <div class="actions-grid">
            <div class="action-card" @click="goToPublishService">
              <div class="action-icon">📤</div>
              <div class="action-content">
                <span class="action-title">发布服务</span>
                <span class="action-desc">发布您的植保服务</span>
              </div>
            </div>
            <div class="action-card" @click="goToServiceList">
              <div class="action-icon">📋</div>
              <div class="action-content">
                <span class="action-title">我的服务</span>
                <span class="action-desc">管理服务列表</span>
              </div>
            </div>
            <div class="action-card" @click="goToEarnings">
              <div class="action-icon">💰</div>
              <div class="action-content">
                <span class="action-title">我的收入</span>
                <span class="action-desc">查看收益明细</span>
              </div>
            </div>
            <div class="action-card" @click="goToReviews">
              <div class="action-icon">⭐</div>
              <div class="action-content">
                <span class="action-title">用户评价</span>
                <span class="action-desc">{{ stats.rating }}分好评</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 进行中的订单 -->
        <div class="ongoing-section">
          <h3>进行中的订单</h3>
          <div class="ongoing-list">
            <div v-for="order in ongoingOrders" :key="order.id" class="ongoing-item">
              <div class="ongoing-info">
                <h4 class="ongoing-title">{{ order.title }}</h4>
                <span class="ongoing-location">{{ order.location }}</span>
              </div>
              <div class="ongoing-progress">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: order.progress + '%' }"></div>
                </div>
                <span class="progress-text">{{ order.progress }}%</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 服务等级 -->
        <div class="level-section">
          <div class="level-card">
            <div class="level-header">
              <span class="level-icon">🏆</span>
              <span class="level-title">飞手等级</span>
            </div>
            <div class="level-content">
              <div class="level-name">{{ userLevel }}</div>
              <div class="level-progress">
                <div class="level-bar">
                  <div class="level-fill" :style="{ width: levelProgress + '%' }"></div>
                </div>
                <span class="level-text">{{ levelProgress }}% 升至下一等级</span>
              </div>
            </div>
            <div class="level-benefits">
              <div class="benefit-item">优先接单权</div>
              <div class="benefit-item">服务曝光+20%</div>
              <div class="benefit-item">专属客服</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'FlyerDashboard',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const userName = computed(() => {
      return userStore.userInfo?.username || userStore.userInfo?.name || '飞手用户'
    })
    
    const userLevel = ref('金牌飞手')
    const levelProgress = ref(75)
    
    const stats = ref({
      completedOrders: 86,
      rating: 4.9,
      totalEarnings: 32680
    })
    
    const serviceStats = ref([
      { key: 'pending', label: '待接单', count: 5, icon: '⏳' },
      { key: 'processing', label: '进行中', count: 2, icon: '🚁' },
      { key: 'completed', label: '已完成', count: 86, icon: '✅' },
      { key: 'reviews', label: '待评价', count: 3, icon: '⭐' }
    ])
    
    const pendingOrders = ref([
      { 
        id: 'ORD20240120005', 
        title: '小麦病虫害防治', 
        location: '山东济南槐荫区',
        date: '2024-01-22',
        crop: '小麦',
        area: 150,
        serviceType: '农药喷洒',
        price: 2250,
        status: 'pending'
      },
      { 
        id: 'ORD20240120006', 
        title: '玉米施肥作业', 
        location: '河南郑州金水区',
        date: '2024-01-23',
        crop: '玉米',
        area: 200,
        serviceType: '叶面肥喷洒',
        price: 3600,
        status: 'pending'
      },
      { 
        id: 'ORD20240120007', 
        title: '果园农药喷洒', 
        location: '陕西西安未央区',
        date: '2024-01-24',
        crop: '苹果',
        area: 80,
        serviceType: '病虫害防治',
        price: 2000,
        status: 'pending'
      }
    ])
    
    const ongoingOrders = ref([
      { id: 'ORD20240119002', title: '玉米病虫害防治', location: '河南郑州', progress: 65 },
      { id: 'ORD20240118008', title: '水稻施肥作业', location: '江苏徐州', progress: 30 }
    ])
    
    const getStatusLabel = (status) => {
      const labels = {
        pending: '待接单',
        processing: '进行中',
        completed: '已完成',
        cancelled: '已取消'
      }
      return labels[status] || status
    }
    
    const goToSection = (key) => {
      if (key === 'pending') {
        router.push('/flyer/order-list?status=pending')
      } else if (key === 'processing') {
        router.push('/flyer/order-list?status=processing')
      } else if (key === 'completed') {
        router.push('/flyer/order-list?status=completed')
      } else if (key === 'reviews') {
        router.push('/flyer/reviews')
      }
    }
    
    const goToOrders = (status) => {
      router.push(`/flyer/order-list?status=${status}`)
    }
    
    const goToPublishService = () => {
      router.push('/flyer/accept-order')
    }
    
    const goToServiceList = () => {
      router.push('/flyer/my-orders')
    }
    
    const goToEarnings = () => {
      router.push('/flyer/wallet')
    }
    
    const goToReviews = () => {
      router.push('/flyer/reviews')
    }
    
    const acceptOrder = (orderId) => {
      ElMessage.success(`订单 ${orderId} 已接单`)
    }
    
    const rejectOrder = (orderId) => {
      ElMessage.info(`订单 ${orderId} 已拒绝`)
    }
    
    onMounted(() => {
      console.log('Flyer dashboard mounted')
    })
    
    return {
      userName,
      userLevel,
      levelProgress,
      stats,
      serviceStats,
      pendingOrders,
      ongoingOrders,
      getStatusLabel,
      goToSection,
      goToOrders,
      goToPublishService,
      goToServiceList,
      goToEarnings,
      goToReviews,
      acceptOrder,
      rejectOrder
    }
  }
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #f8f9fa;
}

.dashboard-navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo-icon {
  font-size: 32px;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: #3b82f6;
}

.navbar-center {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
}

.search-box {
  display: flex;
  background: #f3f4f6;
  border-radius: 20px;
  overflow: hidden;
}

.search-input {
  flex: 1;
  border: none;
  padding: 10px 16px;
  font-size: 14px;
  background: transparent;
  outline: none;
}

.search-btn {
  padding: 10px 20px;
  background: #3b82f6;
  border: none;
  font-size: 16px;
  cursor: pointer;
}

.navbar-right {
  display: flex;
  gap: 16px;
  align-items: center;
}

.nav-btn {
  padding: 8px 16px;
  background: transparent;
  border: none;
  font-size: 14px;
  color: #4b5563;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.3s;
}

.nav-btn:hover {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.08);
}

.nav-btn.active {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.15);
  font-weight: 600;
}

.avatar {
  font-size: 20px;
}

.user-card {
  max-width: 1400px;
  margin: 24px auto;
  padding: 0 24px;
}

.user-card {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 16px;
  padding: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
}

.user-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.user-avatar {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
}

.user-name {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px 0;
}

.user-role {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.user-stats {
  display: flex;
  gap: 48px;
}

.user-stat-item {
  text-align: center;
}

.user-stat-item .stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
}

.user-stat-item .stat-label {
  font-size: 13px;
  opacity: 0.8;
}

.stats-section {
  max-width: 1400px;
  margin: 0 auto 24px;
  padding: 0 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.view-all-btn {
  background: none;
  border: none;
  color: #3b82f6;
  font-size: 14px;
  cursor: pointer;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-card.pending .stat-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-card.processing .stat-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.stat-card.completed .stat-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-card.reviews .stat-icon {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
}

.stat-content {
  flex: 1;
}

.stat-content .stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.stat-content .stat-label {
  font-size: 13px;
  color: #6b7280;
}

.stat-arrow {
  color: #9ca3af;
  font-size: 18px;
}

.dashboard-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.main-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.3s;
}

.order-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-id {
  font-size: 13px;
  color: #6b7280;
}

.order-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.order-status.pending {
  background: #fef3c7;
  color: #92400e;
}

.order-status.processing {
  background: #dbeafe;
  color: #1e40af;
}

.order-status.completed {
  background: #dcfce7;
  color: #166534;
}

.order-content {
  display: flex;
  justify-content: space-between;
}

.order-info {
  flex: 1;
}

.order-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.order-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.meta-item {
  font-size: 13px;
  color: #6b7280;
}

.order-detail {
  display: flex;
  gap: 16px;
}

.order-detail span {
  font-size: 13px;
  color: #9ca3af;
}

.order-price {
  text-align: right;
}

.price-label {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.action-btn {
  padding: 8px 24px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
  color: #4b5563;
}

.action-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.action-btn.primary {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #fff;
}

.action-btn.primary:hover {
  background: #2563eb;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.quick-actions {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.quick-actions h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-card {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  background: #eff6ff;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 28px;
}

.action-content {
  display: flex;
  flex-direction: column;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.action-desc {
  font-size: 12px;
  color: #9ca3af;
}

.ongoing-section {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.ongoing-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.ongoing-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ongoing-item {
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
}

.ongoing-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.ongoing-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.ongoing-location {
  font-size: 12px;
  color: #6b7280;
}

.ongoing-progress {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: #e5e7eb;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #3b82f6;
  border-radius: 3px;
  transition: width 0.3s;
}

.progress-text {
  font-size: 12px;
  color: #3b82f6;
  font-weight: 600;
}

.level-section {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.level-card {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  border-radius: 12px;
  padding: 20px;
  color: #fff;
}

.level-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.level-icon {
  font-size: 24px;
}

.level-title {
  font-size: 14px;
  font-weight: 600;
}

.level-content {
  margin-bottom: 16px;
}

.level-name {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 8px;
}

.level-progress {
  display: flex;
  align-items: center;
  gap: 8px;
}

.level-bar {
  flex: 1;
  height: 8px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  overflow: hidden;
}

.level-fill {
  height: 100%;
  background: #fff;
  border-radius: 4px;
}

.level-text {
  font-size: 12px;
  opacity: 0.9;
}

.level-benefits {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.benefit-item {
  font-size: 13px;
  padding: 6px 10px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .dashboard-content {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    order: -1;
  }
}

@media (max-width: 768px) {
  .navbar-center {
    display: none;
  }
  
  .navbar-right {
    gap: 8px;
  }
  
  .nav-btn {
    padding: 6px 10px;
    font-size: 13px;
  }
  
  .user-card {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .user-info {
    flex-direction: column;
  }
  
  .user-stats {
    gap: 24px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .order-content {
    flex-direction: column;
    gap: 12px;
  }
  
  .order-price {
    text-align: left;
  }
  
  .order-actions {
    justify-content: flex-start;
  }
}
</style>