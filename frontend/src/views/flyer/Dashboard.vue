<template>
  <div class="dashboard flyer-dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">飞手仪表盘</h1>
      <p class="dashboard-subtitle">欢迎回来，{{ userName }}！查看您的订单和收入概览</p>
    </div>
    
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon order-icon">📦</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.orderCount }}</div>
          <div class="stat-label">总订单</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon pending-icon">⏳</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.acceptedCount }}</div>
          <div class="stat-label">待执行</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon processing-icon">🚁</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.processingCount }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon earnings-icon">💰</div>
        <div class="stat-content">
          <div class="stat-value">¥{{ stats.totalEarnings }}</div>
          <div class="stat-label">累计收入</div>
        </div>
      </div>
    </div>
    
    <div class="dashboard-content">
      <div class="recent-orders">
        <div class="section-header">
          <h2>最近订单</h2>
          <el-button type="text" class="view-all-btn" @click="goToOrderList">查看全部 →</el-button>
        </div>
        <el-table :data="recentOrders" border class="order-table">
          <el-table-column prop="title" label="订单标题" min-width="200">
            <template #default="scope">
              <span class="order-title">{{ scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="farmer" label="农户" width="120" align="center">
            <template #default="scope">
              <span class="farmer-name">{{ scope.row.farmer }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="area" label="面积(亩)" width="100" align="center">
            <template #default="scope">
              <span class="area-value">{{ scope.row.area }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="金额" width="100" align="right">
            <template #default="scope">
              <span class="price-value">¥{{ scope.row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="text" size="small" @click="goToDetail(scope.row.id)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="quick-actions">
        <div class="section-header">
          <h2>快捷操作</h2>
        </div>
        <div class="action-grid">
          <div class="action-card" @click="goToAcceptOrder">
            <div class="action-icon">🤝</div>
            <div class="action-title">接单中心</div>
            <div class="action-desc">浏览并接受需求</div>
          </div>
          <div class="action-card" @click="goToMyOrders">
            <div class="action-icon">📋</div>
            <div class="action-title">我的订单</div>
            <div class="action-desc">查看订单状态</div>
          </div>
          <div class="action-card" @click="goToEquipment">
            <div class="action-icon">🔧</div>
            <div class="action-title">设备租用</div>
            <div class="action-desc">租用无人机设备</div>
          </div>
          <div class="action-card" @click="goToQualification">
            <div class="action-icon">📝</div>
            <div class="action-title">资质管理</div>
            <div class="action-desc">管理资质认证</div>
          </div>
        </div>
        
        <div class="earnings-summary">
          <div class="earnings-header">
            <h3>本月收入</h3>
            <span class="earnings-month">2024年1月</span>
          </div>
          <div class="earnings-amount">¥{{ monthlyEarnings }}</div>
          <div class="earnings-chart">
            <div class="chart-bar">
              <div class="bar-fill" style="height: 75%"></div>
            </div>
            <div class="chart-labels">
              <span>1月</span>
              <span>2月</span>
              <span>3月</span>
              <span>4月</span>
              <span>5月</span>
              <span>6月</span>
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

export default {
  name: 'FlyerDashboard',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const userName = computed(() => {
      return userStore.userInfo?.username || userStore.userInfo?.name || '飞手'
    })
    
    const stats = ref({
      orderCount: 28,
      acceptedCount: 5,
      processingCount: 2,
      totalEarnings: 15680
    })
    
    const monthlyEarnings = ref(3280)
    
    const recentOrders = ref([
      { id: 1, title: '小麦喷洒作业', farmer: '王农户', area: 200, price: 1200, status: 'processing', createTime: '2024-01-20 14:30:00' },
      { id: 2, title: '玉米病虫害防治', farmer: '李农户', area: 150, price: 900, status: 'accepted', createTime: '2024-01-19 09:15:00' },
      { id: 3, title: '水稻施肥作业', farmer: '张农户', area: 300, price: 1800, status: 'completed', createTime: '2024-01-18 16:45:00' },
      { id: 4, title: '果园农药喷洒', farmer: '赵农户', area: 80, price: 480, status: 'accepted', createTime: '2024-01-17 10:20:00' },
      { id: 5, title: '棉花脱叶剂喷洒', farmer: '孙农户', area: 250, price: 1500, status: 'completed', createTime: '2024-01-16 15:30:00' }
    ])
    
    const getStatusType = (status) => {
      const types = {
        accepted: 'warning',
        processing: 'primary',
        completed: 'success',
        cancelled: 'danger'
      }
      return types[status] || 'info'
    }
    
    const getStatusLabel = (status) => {
      const labels = {
        accepted: '待执行',
        processing: '进行中',
        completed: '已完成',
        cancelled: '已取消'
      }
      return labels[status] || status
    }
    
    const goToOrderList = () => {
      router.push('/order-list')
    }
    
    const goToDetail = (id) => {
      router.push(`/order-detail/${id}`)
    }
    
    const goToAcceptOrder = () => {
      router.push('/accept-order')
    }
    
    const goToMyOrders = () => {
      router.push('/my-orders')
    }
    
    const goToEquipment = () => {
      router.push('/equipment')
    }
    
    const goToQualification = () => {
      router.push('/qualification')
    }
    
    onMounted(() => {
      console.log('Flyer dashboard mounted')
    })
    
    return {
      userName,
      stats,
      monthlyEarnings,
      recentOrders,
      getStatusType,
      getStatusLabel,
      goToOrderList,
      goToDetail,
      goToAcceptOrder,
      goToMyOrders,
      goToEquipment,
      goToQualification
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 24px;
  min-height: 100%;
}

.dashboard-header {
  margin-bottom: 32px;
}

.dashboard-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.dashboard-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
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

.order-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.pending-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.processing-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.earnings-icon {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  margin-top: 4px;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.view-all-btn {
  color: #3b82f6;
  font-size: 13px;
  padding: 0;
}

.view-all-btn:hover {
  color: #2563eb;
}

.recent-orders {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.order-table {
  width: 100%;
}

.order-title {
  font-weight: 500;
  color: #1f2937;
}

.farmer-name {
  color: #6b7280;
}

.area-value {
  font-weight: 600;
  color: #3b82f6;
}

.price-value {
  font-weight: 600;
  color: #10b981;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.action-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.action-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.action-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.action-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px;
  color: #9ca3af;
}

.earnings-summary {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 16px;
  padding: 24px;
  color: #fff;
}

.earnings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.earnings-header h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.earnings-month {
  font-size: 13px;
  opacity: 0.8;
}

.earnings-amount {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 20px;
}

.earnings-chart {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chart-bar {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  height: 60px;
}

.bar-fill {
  flex: 1;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  transition: height 0.5s ease;
}

.chart-labels {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  opacity: 0.7;
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .dashboard-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-grid {
    grid-template-columns: 1fr;
  }
  
  .dashboard-title {
    font-size: 24px;
  }