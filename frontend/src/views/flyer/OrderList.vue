<template>
  <div class="order-list-page">
    <!-- 顶部导航 -->
    <nav class="page-navbar">
      <div class="navbar-content">
        <div class="navbar-left">
          <div class="logo" @click="router.push('/')">
            <span class="logo-icon">🚁</span>
            <span class="logo-text">农翼通商城</span>
          </div>
        </div>
        <div class="navbar-center">
          <div class="search-box">
            <input type="text" placeholder="搜索订单号、服务名称" class="search-input" />
            <button class="search-btn">🔍</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/service-list')">服务市场</button>
          <button class="nav-btn active">订单管理</button>
          <button class="nav-btn" @click="router.push('/profile')">
            <span class="avatar">👤</span>
          </button>
        </div>
      </div>
    </nav>

    <!-- 主内容区域 -->
    <div class="page-content">
      <!-- 订单状态筛选 -->
      <div class="filter-section">
        <div class="filter-tabs">
          <button 
            v-for="tab in tabs" 
            :key="tab.key"
            :class="['filter-tab', { active: activeTab === tab.key }]"
            @click="activeTab = tab.key"
          >
            <span class="tab-icon">{{ tab.icon }}</span>
            <span class="tab-label">{{ tab.label }}</span>
            <span v-if="tab.count" class="tab-count">{{ tab.count }}</span>
          </button>
        </div>
      </div>

      <!-- 订单列表 -->
      <div class="orders-container">
        <div v-if="filteredOrders.length === 0" class="empty-state">
          <div class="empty-icon">📦</div>
          <p>暂无订单</p>
        </div>
        
        <div v-else class="orders-list">
          <div v-for="order in filteredOrders" :key="order.id" class="order-card" @click="goToOrderDetail(order.id)">
            <div class="order-header">
              <div class="order-id">订单号: {{ order.id }}</div>
              <span :class="['order-status', order.status]">{{ getStatusLabel(order.status) }}</span>
            </div>
            <div class="order-body">
              <div class="order-service">
                <img :src="order.image" class="service-image" />
                <div class="service-info">
                  <h4 class="service-title">{{ order.title }}</h4>
                  <div class="service-meta">
                    <span class="meta-item">📍 {{ order.location }}</span>
                    <span class="meta-item">📅 {{ order.date }}</span>
                  </div>
                  <div class="service-specs">
                    <span class="spec-tag">作物: {{ order.crop }}</span>
                    <span class="spec-tag">面积: {{ order.area }}亩</span>
                    <span class="spec-tag">类型: {{ order.serviceType }}</span>
                  </div>
                </div>
              </div>
              <div class="order-summary">
                <div class="order-price">
                  <span class="price-label">订单金额</span>
                  <span class="price-value">¥{{ order.total }}</span>
                </div>
              </div>
            </div>
            <div class="order-footer">
              <div class="order-customer">
                <span class="customer-label">客户:</span>
                <span class="customer-name">{{ order.customer }}</span>
              </div>
              <div class="order-actions">
                <button 
                  v-if="order.status === 'pending'" 
                  class="action-btn primary" 
                  @click.stop="acceptOrder(order.id)"
                >接单</button>
                <button 
                  v-if="order.status === 'pending'" 
                  class="action-btn" 
                  @click.stop="rejectOrder(order.id)"
                >拒绝</button>
                <button 
                  v-if="order.status === 'processing'" 
                  class="action-btn" 
                  @click.stop="completeOrder(order.id)"
                >完成服务</button>
                <button 
                  v-if="order.status === 'completed'" 
                  class="action-btn" 
                  @click.stop="viewReview(order.id)"
                >查看评价</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-section">
        <button class="page-btn" :disabled="currentPage === 1">上一页</button>
        <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
        <button class="page-btn" :disabled="currentPage === totalPages">下一页</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'FlyerOrderList',
  setup() {
    const router = useRouter()
    
    const activeTab = ref('all')
    const currentPage = ref(1)
    const totalPages = ref(3)
    
    const tabs = ref([
      { key: 'all', label: '全部订单', icon: '📦', count: 12 },
      { key: 'pending', label: '待接单', icon: '⏳', count: 3 },
      { key: 'processing', label: '进行中', icon: '🚁', count: 2 },
      { key: 'completed', label: '已完成', icon: '✅', count: 7 }
    ])
    
    const orders = ref([
      { 
        id: 'ORD20240120001', 
        title: '小麦病虫害防治', 
        image: 'https://picsum.photos/100/80?random=1',
        location: '山东济南槐荫区',
        date: '2024-01-22',
        crop: '小麦',
        area: 150,
        serviceType: '农药喷洒',
        total: 2250,
        customer: '张农户',
        status: 'pending'
      },
      { 
        id: 'ORD20240119002', 
        title: '玉米施肥作业', 
        image: 'https://picsum.photos/100/80?random=2',
        location: '河南郑州金水区',
        date: '2024-01-20',
        crop: '玉米',
        area: 200,
        serviceType: '叶面肥喷洒',
        total: 3600,
        customer: '李农户',
        status: 'processing'
      },
      { 
        id: 'ORD20240118003', 
        title: '水稻病虫害防治', 
        image: 'https://picsum.photos/100/80?random=3',
        location: '江苏徐州',
        date: '2024-01-18',
        crop: '水稻',
        area: 300,
        serviceType: '农药喷洒',
        total: 5400,
        customer: '王农户',
        status: 'completed'
      },
      { 
        id: 'ORD20240117004', 
        title: '果园农药喷洒', 
        image: 'https://picsum.photos/100/80?random=4',
        location: '陕西西安',
        date: '2024-01-24',
        crop: '苹果',
        area: 80,
        serviceType: '病虫害防治',
        total: 2000,
        customer: '赵农户',
        status: 'pending'
      },
      { 
        id: 'ORD20240116005', 
        title: '棉花脱叶剂喷洒', 
        image: 'https://picsum.photos/100/80?random=5',
        location: '新疆石河子',
        date: '2024-01-15',
        crop: '棉花',
        area: 500,
        serviceType: '脱叶剂喷洒',
        total: 7500,
        customer: '孙农户',
        status: 'processing'
      },
      { 
        id: 'ORD20240115006', 
        title: '蔬菜大棚喷药', 
        image: 'https://picsum.photos/100/80?random=6',
        location: '山东寿光',
        date: '2024-01-12',
        crop: '番茄',
        area: 50,
        serviceType: '病虫害防治',
        total: 1500,
        customer: '周农户',
        status: 'completed'
      }
    ])
    
    const filteredOrders = computed(() => {
      if (activeTab.value === 'all') {
        return orders.value
      }
      return orders.value.filter(order => order.status === activeTab.value)
    })
    
    const getStatusLabel = (status) => {
      const labels = {
        pending: '待接单',
        processing: '进行中',
        completed: '已完成',
        cancelled: '已取消'
      }
      return labels[status] || status
    }
    
    const goToOrderDetail = (orderId) => {
      router.push(`/order-detail/${orderId}`)
    }
    
    const acceptOrder = (orderId) => {
      ElMessage.success(`订单 ${orderId} 已接单`)
    }
    
    const rejectOrder = (orderId) => {
      ElMessage.info(`订单 ${orderId} 已拒绝`)
    }
    
    const completeOrder = (orderId) => {
      ElMessage.success(`订单 ${orderId} 服务已完成`)
    }
    
    const viewReview = (orderId) => {
      router.push(`/review/${orderId}`)
    }
    
    onMounted(() => {
      console.log('Flyer order list mounted')
    })
    
    return {
      activeTab,
      currentPage,
      totalPages,
      tabs,
      orders,
      filteredOrders,
      getStatusLabel,
      goToOrderDetail,
      acceptOrder,
      rejectOrder,
      completeOrder,
      viewReview
    }
  }
}
</script>

<style scoped>
.order-list-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-content {
  max-width: 1200px;
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
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #3b82f6;
}

.navbar-center {
  flex: 1;
  max-width: 400px;
  margin: 0 30px;
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
  padding: 8px 14px;
  font-size: 13px;
  background: transparent;
  outline: none;
}

.search-btn {
  padding: 8px 16px;
  background: #3b82f6;
  border: none;
  font-size: 14px;
  cursor: pointer;
}

.navbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.nav-btn {
  padding: 6px 14px;
  background: transparent;
  border: none;
  font-size: 13px;
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
  font-size: 18px;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.filter-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.filter-tabs {
  display: flex;
  gap: 12px;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f3f4f6;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tab:hover {
  background: #e5e7eb;
}

.filter-tab.active {
  background: #3b82f6;
  color: #fff;
}

.tab-icon {
  font-size: 16px;
}

.tab-label {
  font-weight: 500;
}

.tab-count {
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  font-size: 12px;
}

.orders-container {
  margin-bottom: 30px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  color: #6b7280;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s;
}

.order-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
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

.order-body {
  display: flex;
  gap: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.order-service {
  display: flex;
  gap: 16px;
  flex: 1;
}

.service-image {
  width: 100px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.service-info {
  flex: 1;
}

.service-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.service-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 8px;
}

.meta-item {
  font-size: 13px;
  color: #6b7280;
}

.service-specs {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.spec-tag {
  padding: 4px 10px;
  background: #f3f4f6;
  border-radius: 4px;
  font-size: 12px;
  color: #6b7280;
}

.order-summary {
  text-align: right;
}

.order-price {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.price-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.order-customer {
  display: flex;
  align-items: center;
  gap: 6px;
}

.customer-label {
  font-size: 13px;
  color: #6b7280;
}

.customer-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.order-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  padding: 8px 20px;
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

.pagination-section {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.page-btn {
  padding: 8px 20px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  background: #fff;
  color: #4b5563;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #6b7280;
}

@media (max-width: 768px) {
  .navbar-center {
    display: none;
  }
  
  .navbar-right {
    gap: 8px;
  }
  
  .filter-tabs {
    flex-wrap: wrap;
  }
  
  .order-body {
    flex-direction: column;
  }
  
  .order-service {
    flex-direction: column;
  }
  
  .service-image {
    width: 100%;
    height: 150px;
  }
  
  .service-specs {
    flex-wrap: wrap;
  }
  
  .order-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .order-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>