<template>
  <div class="dashboard farmer-dashboard">
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
            <input type="text" placeholder="搜索服务、设备" class="search-input" />
            <button class="search-btn">🔍</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/service-list')">找服务</button>
          <button class="nav-btn" @click="router.push('/equipment-list')">租设备</button>
          <button class="nav-btn active">我的订单</button>
          <button class="nav-btn" @click="router.push('/farmer/profile')">
            <span class="avatar">👤</span>
          </button>
        </div>
      </div>
    </nav>

    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="user-info">
        <div class="user-avatar">🌾</div>
        <div class="user-detail">
          <h2 class="user-name">{{ userName }}</h2>
          <p class="user-role">👨‍🌾 农户用户</p>
        </div>
      </div>
      <div class="user-stats">
        <div class="user-stat-item">
          <span class="stat-value">{{ stats.totalOrders }}</span>
          <span class="stat-label">累计订单</span>
        </div>
        <div class="user-stat-item">
          <span class="stat-value">¥{{ stats.totalSpent }}</span>
          <span class="stat-label">累计消费</span>
        </div>
        <div class="user-stat-item">
          <span class="stat-value">{{ stats.coupons }}</span>
          <span class="stat-label">优惠券</span>
        </div>
      </div>
    </div>

    <!-- 订单统计卡片 -->
    <div class="stats-section">
      <div class="section-header">
        <h3>订单概览</h3>
      </div>
      <div class="stats-grid">
        <div 
          v-for="stat in orderStats" 
          :key="stat.key"
          :class="['stat-card', stat.key]"
          @click="goToOrders(stat.key)"
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

    <!-- 订单列表和快捷操作 -->
    <div class="dashboard-content">
      <!-- 最近订单 -->
      <div class="orders-section">
        <div class="section-header">
          <h3>最近订单</h3>
          <button class="view-all-btn" @click="goToAllOrders">查看全部 →</button>
        </div>
        <div class="orders-list">
          <div v-for="order in recentOrders" :key="order.id" class="order-card" @click="goToOrderDetail(order.id)">
            <div class="order-header">
              <div class="order-id">订单号: {{ order.id }}</div>
              <span :class="['order-status', order.status]">{{ getStatusLabel(order.status) }}</span>
            </div>
            <div class="order-content">
              <div class="order-service">
                <img :src="order.image" class="service-image" />
                <div class="service-info">
                  <h4 class="service-title">{{ order.title }}</h4>
                  <div class="service-provider">
                    <span :class="['provider-badge', order.providerType]">
                      {{ order.providerType === 'flyer' ? 'C2C' : 'B2C' }}
                    </span>
                    <span class="provider-name">{{ order.provider }}</span>
                  </div>
                </div>
              </div>
              <div class="order-summary">
                <div class="order-meta">
                  <span class="meta-item">📍 {{ order.location }}</span>
                  <span class="meta-item">📅 {{ order.date }}</span>
                </div>
                <div class="order-price-row">
                  <span class="order-area">{{ order.area }} 亩</span>
                  <span class="order-total">¥{{ order.total }}</span>
                </div>
              </div>
            </div>
            <div class="order-actions">
              <button v-if="order.status === 'pending'" class="action-btn primary" @click.stop="cancelOrder(order.id)">取消订单</button>
              <button v-if="order.status === 'completed'" class="action-btn" @click.stop="goToReview(order.id)">去评价</button>
              <button v-if="order.status === 'processing'" class="action-btn" @click.stop="goToContact(order.provider)">联系商家</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="sidebar">
        <!-- 快捷入口 -->
        <div class="quick-actions">
          <h3>快捷操作</h3>
          <div class="actions-grid">
            <div class="action-card" @click="goToPublishDemand">
              <div class="action-icon">✏️</div>
              <div class="action-content">
                <span class="action-title">发布需求</span>
                <span class="action-desc">填写地块信息，匹配服务</span>
              </div>
            </div>
            <div class="action-card" @click="goToOrderList">
              <div class="action-icon">📋</div>
              <div class="action-content">
                <span class="action-title">我的订单</span>
                <span class="action-desc">查看所有订单</span>
              </div>
            </div>
            <div class="action-card" @click="goToWallet">
              <div class="action-icon">💰</div>
              <div class="action-content">
                <span class="action-title">我的钱包</span>
                <span class="action-desc">查看余额和账单</span>
              </div>
            </div>
            <div class="action-card" @click="goToCoupons">
              <div class="action-icon">🎫</div>
              <div class="action-content">
                <span class="action-title">优惠券</span>
                <span class="action-desc">{{ stats.coupons }}张可用</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 推荐服务 -->
        <div class="recommend-section">
          <h3>为您推荐</h3>
          <div class="recommend-list">
            <div v-for="service in recommendServices" :key="service.id" class="recommend-item" @click="goToServiceDetail(service.id)">
              <img :src="service.image" class="recommend-image" />
              <div class="recommend-info">
                <h4 class="recommend-title">{{ service.title }}</h4>
                <div class="recommend-price">
                  <span class="price">¥{{ service.price }}</span>
                  <span class="unit">/{{ service.unit }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 服务保障 -->
        <div class="guarantee-section">
          <h3>服务保障</h3>
          <div class="guarantee-list">
            <div class="guarantee-item">
              <span class="icon">✅</span>
              <span>平台担保交易</span>
            </div>
            <div class="guarantee-item">
              <span class="icon">🔒</span>
              <span>资金安全保障</span>
            </div>
            <div class="guarantee-item">
              <span class="icon">⭐</span>
              <span>售后服务无忧</span>
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
  name: 'FarmerDashboard',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const userName = computed(() => {
      return userStore.userInfo?.username || userStore.userInfo?.name || '农户用户'
    })
    
    const stats = ref({
      totalOrders: 12,
      totalSpent: 5860,
      coupons: 3
    })
    
    const orderStats = ref([
      { key: 'all', label: '全部订单', count: 12, icon: '📦' },
      { key: 'pending', label: '待接单', count: 3, icon: '⏳' },
      { key: 'processing', label: '进行中', count: 2, icon: '🚁' },
      { key: 'completed', label: '已完成', count: 7, icon: '✅' }
    ])
    
    const recentOrders = ref([
      { 
        id: 'ORD20240120001', 
        title: '小麦喷洒作业需求', 
        image: 'https://picsum.photos/100/80?random=1',
        provider: '绿翼植保服务队',
        providerType: 'flyer',
        location: '山东济南',
        date: '2024-01-20',
        area: 200,
        total: 3000,
        status: 'pending'
      },
      { 
        id: 'ORD20240119002', 
        title: '玉米病虫害防治', 
        image: 'https://picsum.photos/100/80?random=2',
        provider: '丰收获服合作社',
        providerType: 'owner',
        location: '河南郑州',
        date: '2024-01-19',
        area: 150,
        total: 3750,
        status: 'processing'
      },
      { 
        id: 'ORD20240118003', 
        title: '水稻施肥作业', 
        image: 'https://picsum.photos/100/80?random=3',
        provider: '天宇农业服务',
        providerType: 'flyer',
        location: '江苏徐州',
        date: '2024-01-18',
        area: 300,
        total: 5400,
        status: 'completed'
      },
      { 
        id: 'ORD20240117004', 
        title: '果园农药喷洒', 
        image: 'https://picsum.photos/100/80?random=4',
        provider: '惠农植保中心',
        providerType: 'owner',
        location: '陕西西安',
        date: '2024-01-17',
        area: 80,
        total: 2800,
        status: 'pending'
      }
    ])

    const recommendServices = ref([
      { id: 1, title: '专业农药喷洒服务', image: 'https://picsum.photos/80/60?random=10', price: 15, unit: '亩' },
      { id: 2, title: '精准施肥作业', image: 'https://picsum.photos/80/60?random=11', price: 18, unit: '亩' },
      { id: 3, title: '病虫害统防统治', image: 'https://picsum.photos/80/60?random=12', price: 25, unit: '亩' }
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
    
    const goToOrders = (status) => {
      router.push(`/farmer/my-demands?status=${status}`)
    }
    
    const goToAllOrders = () => {
      router.push('/farmer/my-demands')
    }
    
    const goToOrderDetail = (orderId) => {
      router.push(`/farmer/demand-detail/${orderId}`)
    }
    
    const goToPublishDemand = () => {
      router.push('/farmer/publish-demand')
    }
    
    const goToOrderList = () => {
      router.push('/farmer/my-demands')
    }
    
    const goToWallet = () => {
      router.push('/farmer/wallet')
    }
    
    const goToCoupons = () => {
      router.push('/farmer/coupons')
    }
    
    const goToServiceDetail = (serviceId) => {
      router.push(`/service-detail/${serviceId}`)
    }
    
    const goToReview = (orderId) => {
      router.push(`/farmer/review/${orderId}`)
    }
    
    const goToContact = (provider) => {
      ElMessage.info(`正在联系 ${provider}...`)
    }
    
    const cancelOrder = (orderId) => {
      ElMessage.success(`订单 ${orderId} 已取消`)
    }
    
    onMounted(() => {
      console.log('Farmer dashboard mounted')
    })
    
    return {
      userName,
      stats,
      orderStats,
      recentOrders,
      recommendServices,
      getStatusLabel,
      goToOrders,
      goToAllOrders,
      goToOrderDetail,
      goToPublishDemand,
      goToOrderList,
      goToWallet,
      goToCoupons,
      goToServiceDetail,
      goToReview,
      goToContact,
      cancelOrder
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
  color: #10b981;
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
  background: #10b981;
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
  color: #10b981;
  background: rgba(16, 185, 129, 0.08);
}

.nav-btn.active {
  color: #10b981;
  background: rgba(16, 185, 129, 0.15);
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
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
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
  color: #10b981;
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

.stat-card.all .stat-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.stat-card.pending .stat-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-card.processing .stat-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-card.completed .stat-icon {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
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

.orders-section {
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
  border-color: #10b981;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.1);
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
  gap: 16px;
}

.order-service {
  display: flex;
  gap: 12px;
  flex: 1;
}

.service-image {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
}

.service-info {
  display: flex;
  flex-direction: column;
}

.service-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.service-provider {
  display: flex;
  gap: 8px;
  align-items: center;
}

.provider-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.provider-badge.flyer {
  background: #dbeafe;
  color: #1e40af;
}

.provider-badge.owner {
  background: #fef3c7;
  color: #92400e;
}

.provider-name {
  font-size: 13px;
  color: #6b7280;
}

.order-summary {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.order-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-item {
  font-size: 12px;
  color: #9ca3af;
}

.order-price-row {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.order-area {
  font-size: 12px;
  color: #6b7280;
}

.order-total {
  font-size: 20px;
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
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
  color: #4b5563;
}

.action-btn:hover {
  border-color: #10b981;
  color: #10b981;
}

.action-btn.primary {
  background: #10b981;
  border-color: #10b981;
  color: #fff;
}

.action-btn.primary:hover {
  background: #059669;
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
  background: #f0fdf4;
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

.recommend-section {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.recommend-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommend-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.recommend-item:hover {
  background: #f0fdf4;
}

.recommend-image {
  width: 60px;
  height: 50px;
  object-fit: cover;
  border-radius: 6px;
}

.recommend-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.recommend-title {
  font-size: 13px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.recommend-price {
  display: flex;
  align-items: baseline;
}

.recommend-price .price {
  font-size: 16px;
  font-weight: 700;
  color: #ef4444;
}

.recommend-price .unit {
  font-size: 12px;
  color: #6b7280;
}

.guarantee-section {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.guarantee-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.guarantee-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.guarantee-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #4b5563;
}

.guarantee-item .icon {
  font-size: 18px;
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
  }
  
  .order-summary {
    align-items: flex-start;
  }
  
  .order-actions {
    justify-content: flex-start;
  }
}
</style>