<template>
  <div class="dashboard owner-dashboard">
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
            <input type="text" placeholder="搜索设备、服务" class="search-input" />
            <button class="search-btn">🔍</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/equipment-list')">设备市场</button>
          <button class="nav-btn active">我的设备</button>
          <button class="nav-btn" @click="router.push('/owner/profile')">
            <span class="avatar">👤</span>
          </button>
        </div>
      </div>
    </nav>

    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="user-info">
        <div class="user-avatar">🏭</div>
        <div class="user-detail">
          <h2 class="user-name">{{ userName }}</h2>
          <p class="user-role">🏢 机主用户</p>
        </div>
      </div>
      <div class="user-stats">
        <div class="user-stat-item">
          <span class="stat-value">{{ stats.totalEquipment }}</span>
          <span class="stat-label">设备总数</span>
        </div>
        <div class="user-stat-item">
          <span class="stat-value">{{ stats.activeOrders }}</span>
          <span class="stat-label">进行中订单</span>
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
        <h3>业务概览</h3>
      </div>
      <div class="stats-grid">
        <div 
          v-for="stat in businessStats" 
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
      <!-- 设备列表 -->
      <div class="main-section">
        <div class="section-header">
          <h3>我的设备</h3>
          <button class="view-all-btn" @click="goToEquipmentList">查看全部 →</button>
        </div>
        <div class="equipment-grid">
          <div v-for="equipment in equipmentList" :key="equipment.id" class="equipment-card">
            <div class="equipment-image-wrapper">
              <img :src="equipment.image" class="equipment-image" />
              <div v-if="equipment.status === 'available'" class="status-badge available">可租赁</div>
              <div v-else class="status-badge busy">使用中</div>
            </div>
            <div class="equipment-info">
              <h4 class="equipment-name">{{ equipment.name }}</h4>
              <div class="equipment-specs">
                <span class="spec-item">{{ equipment.type }}</span>
                <span class="spec-item">{{ equipment.model }}</span>
              </div>
              <div class="equipment-footer">
                <div class="equipment-price">
                  <span class="currency">¥</span>
                  <span class="price">{{ equipment.price }}</span>
                  <span class="unit">/{{ equipment.unit }}</span>
                </div>
                <button 
                  v-if="equipment.status === 'available'" 
                  class="manage-btn" 
                  @click="manageEquipment(equipment.id)"
                >管理</button>
                <button 
                  v-else 
                  class="manage-btn busy-btn" 
                  @click="viewOrder(equipment.currentOrder)"
                >查看订单</button>
              </div>
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
            <div class="action-card" @click="goToAddEquipment">
              <div class="action-icon">➕</div>
              <div class="action-content">
                <span class="action-title">添加设备</span>
                <span class="action-desc">新增无人机设备</span>
              </div>
            </div>
            <div class="action-card" @click="goToServiceList">
              <div class="action-icon">📋</div>
              <div class="action-content">
                <span class="action-title">设备列表</span>
                <span class="action-desc">管理所有设备</span>
              </div>
            </div>
            <div class="action-card" @click="goToOrders">
              <div class="action-icon">📦</div>
              <div class="action-content">
                <span class="action-title">订单管理</span>
                <span class="action-desc">查看全部订单</span>
              </div>
            </div>
            <div class="action-card" @click="goToEarnings">
              <div class="action-icon">💰</div>
              <div class="action-content">
                <span class="action-title">收益统计</span>
                <span class="action-desc">查看收入明细</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 最近订单 -->
        <div class="recent-orders">
          <h3>最近订单</h3>
          <div class="orders-list">
            <div v-for="order in recentOrders" :key="order.id" class="order-item">
              <div class="order-info">
                <div class="order-id">{{ order.id }}</div>
                <div class="order-equipment">{{ order.equipment }}</div>
              </div>
              <div class="order-amount">
                <span :class="['status-dot', order.status]"></span>
                <span class="amount">¥{{ order.amount }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 租赁收益 -->
        <div class="earnings-card">
          <div class="earnings-header">
            <span class="earnings-icon">📈</span>
            <span class="earnings-title">本月收益</span>
          </div>
          <div class="earnings-content">
            <div class="earnings-amount">¥{{ monthlyEarnings }}</div>
            <div class="earnings-change">
              <span class="change-icon">↑</span>
              <span class="change-value">12.5%</span>
              <span class="change-text">较上月</span>
            </div>
          </div>
          <div class="earnings-breakdown">
            <div class="breakdown-item">
              <span class="breakdown-label">设备租赁</span>
              <span class="breakdown-value">¥6,800</span>
            </div>
            <div class="breakdown-item">
              <span class="breakdown-label">服务收入</span>
              <span class="breakdown-value">¥3,200</span>
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
  name: 'OwnerDashboard',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const userName = computed(() => {
      return userStore.userInfo?.username || userStore.userInfo?.name || '机主用户'
    })
    
    const monthlyEarnings = ref(10000)
    
    const stats = ref({
      totalEquipment: 8,
      activeOrders: 3,
      totalEarnings: 86500
    })
    
    const businessStats = ref([
      { key: 'equipment', label: '设备管理', count: 8, icon: '🚁' },
      { key: 'orders', label: '订单管理', count: 12, icon: '📦' },
      { key: 'earnings', label: '收益统计', count: '¥86.5k', icon: '💰' },
      { key: 'customers', label: '客户管理', count: 28, icon: '👥' }
    ])
    
    const equipmentList = ref([
      { 
        id: 1, 
        name: '大疆 T40 植保无人机', 
        type: '植保无人机',
        model: 'DJI T40',
        image: 'https://picsum.photos/200/150?random=1',
        price: 280,
        unit: '小时',
        status: 'available'
      },
      { 
        id: 2, 
        name: '极飞 P80 农业无人机', 
        type: '植保无人机',
        model: 'XAIRCRAFT P80',
        image: 'https://picsum.photos/200/150?random=2',
        price: 320,
        unit: '小时',
        status: 'busy',
        currentOrder: 'ORD20240120001'
      },
      { 
        id: 3, 
        name: '大疆 T20P 无人机', 
        type: '植保无人机',
        model: 'DJI T20P',
        image: 'https://picsum.photos/200/150?random=3',
        price: 220,
        unit: '小时',
        status: 'available'
      },
      { 
        id: 4, 
        name: '大疆 M300 RTK', 
        type: '测绘无人机',
        model: 'DJI M300',
        image: 'https://picsum.photos/200/150?random=4',
        price: 450,
        unit: '小时',
        status: 'busy',
        currentOrder: 'ORD20240120002'
      }
    ])
    
    const recentOrders = ref([
      { id: 'ORD20240120001', equipment: 'DJI T40', amount: 2800, status: 'processing' },
      { id: 'ORD20240119002', equipment: 'XAIRCRAFT P80', amount: 3200, status: 'completed' },
      { id: 'ORD20240118003', equipment: 'DJI T20P', amount: 1760, status: 'pending' },
      { id: 'ORD20240117004', equipment: 'DJI M300', amount: 4500, status: 'processing' }
    ])
    
    const goToSection = (key) => {
      if (key === 'equipment') {
        router.push('/owner/devices')
      } else if (key === 'orders') {
        router.push('/owner/rental-apply')
      } else if (key === 'earnings') {
        router.push('/owner/wallet')
      } else if (key === 'customers') {
        router.push('/owner/customers')
      }
    }
    
    const goToEquipmentList = () => {
      router.push('/owner/devices')
    }
    
    const goToAddEquipment = () => {
      router.push('/owner/devices/add')
    }
    
    const goToServiceList = () => {
      router.push('/owner/devices')
    }
    
    const goToOrders = () => {
      router.push('/owner/rental-apply')
    }
    
    const goToEarnings = () => {
      router.push('/owner/wallet')
    }
    
    const manageEquipment = (equipmentId) => {
      router.push(`/owner/device/${equipmentId}`)
    }
    
    const viewOrder = (orderId) => {
      router.push(`/order-detail/${orderId}`)
    }
    
    onMounted(() => {
      console.log('Owner dashboard mounted')
    })
    
    return {
      userName,
      monthlyEarnings,
      stats,
      businessStats,
      equipmentList,
      recentOrders,
      goToSection,
      goToEquipmentList,
      goToAddEquipment,
      goToServiceList,
      goToOrders,
      goToEarnings,
      manageEquipment,
      viewOrder
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
  color: #f59e0b;
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
  background: #f59e0b;
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
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.08);
}

.nav-btn.active {
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.15);
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
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
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
  color: #f59e0b;
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

.stat-card.equipment .stat-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.stat-card.orders .stat-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-card.earnings .stat-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-card.customers .stat-icon {
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

.main-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.equipment-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.equipment-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.equipment-card:hover {
  border-color: #f59e0b;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.1);
}

.equipment-image-wrapper {
  position: relative;
}

.equipment-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.available {
  background: #dcfce7;
  color: #166534;
}

.status-badge.busy {
  background: #fef3c7;
  color: #92400e;
}

.equipment-info {
  padding: 16px;
}

.equipment-name {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.equipment-specs {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.spec-item {
  padding: 4px 10px;
  background: #f3f4f6;
  border-radius: 4px;
  font-size: 12px;
  color: #6b7280;
}

.equipment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.equipment-price {
  display: flex;
  align-items: baseline;
}

.equipment-price .currency {
  font-size: 14px;
  color: #ef4444;
  font-weight: 600;
}

.equipment-price .price {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
}

.equipment-price .unit {
  font-size: 13px;
  color: #6b7280;
  margin-left: 4px;
}

.manage-btn {
  padding: 8px 20px;
  background: #f59e0b;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.manage-btn:hover {
  background: #d97706;
}

.manage-btn.busy-btn {
  background: #6b7280;
}

.manage-btn.busy-btn:hover {
  background: #4b5563;
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
  background: #fffbeb;
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

.recent-orders {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.recent-orders h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
}

.order-info {
  display: flex;
  flex-direction: column;
}

.order-id {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.order-equipment {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.order-amount {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-dot.pending {
  background: #f59e0b;
}

.status-dot.processing {
  background: #3b82f6;
}

.status-dot.completed {
  background: #10b981;
}

.amount {
  font-size: 16px;
  font-weight: 700;
  color: #ef4444;
}

.earnings-card {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border-radius: 16px;
  padding: 20px;
  color: #fff;
}

.earnings-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.earnings-icon {
  font-size: 20px;
}

.earnings-title {
  font-size: 14px;
  font-weight: 600;
}

.earnings-content {
  margin-bottom: 16px;
}

.earnings-amount {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.earnings-change {
  display: flex;
  align-items: center;
  gap: 4px;
}

.change-icon {
  color: #10b981;
}

.change-value {
  font-weight: 600;
  color: #10b981;
}

.change-text {
  font-size: 13px;
  opacity: 0.8;
}

.earnings-breakdown {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
}

.breakdown-label {
  font-size: 13px;
  opacity: 0.9;
}

.breakdown-value {
  font-size: 13px;
  font-weight: 600;
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
  
  .equipment-grid {
    grid-template-columns: 1fr;
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
}
</style>