<template>
  <div class="dashboard owner-dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">机主仪表盘</h1>
      <p class="dashboard-subtitle">欢迎回来，{{ userName }}！查看您的设备和收益概览</p>
    </div>
    
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon device-icon">🛠️</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.deviceCount }}</div>
          <div class="stat-label">设备总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon online-icon">🟢</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.onlineCount }}</div>
          <div class="stat-label">在线设备</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rental-icon">📤</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.rentalCount }}</div>
          <div class="stat-label">租借中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon earnings-icon">💰</div>
        <div class="stat-content">
          <div class="stat-value">¥{{ stats.totalEarnings }}</div>
          <div class="stat-label">累计收益</div>
        </div>
      </div>
    </div>
    
    <div class="dashboard-content">
      <div class="recent-devices">
        <div class="section-header">
          <h2>设备列表</h2>
          <el-button type="primary" class="add-device-btn" @click="goToAddDevice">
            <el-icon class="plus-icon"><Plus /></el-icon>
            添加设备
          </el-button>
        </div>
        <el-table :data="devices" border class="device-table">
          <el-table-column prop="name" label="设备名称" min-width="150">
            <template #default="scope">
              <span class="device-name">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="model" label="设备型号" width="120" align="center">
            <template #default="scope">
              <span class="model-value">{{ scope.row.model }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="renter" label="当前租客" width="120">
            <template #default="scope">
              <span class="renter-name">{{ scope.row.renter || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="income" label="累计收入" width="120" align="right">
            <template #default="scope">
              <span class="income-value">¥{{ scope.row.income }}</span>
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
          <div class="action-card" @click="goToDevices">
            <div class="action-icon">📋</div>
            <div class="action-title">设备管理</div>
            <div class="action-desc">管理所有设备</div>
          </div>
          <div class="action-card" @click="goToAddDevice">
            <div class="action-icon">➕</div>
            <div class="action-title">添加设备</div>
            <div class="action-desc">添加新设备</div>
          </div>
          <div class="action-card" @click="goToRentalApply">
            <div class="action-icon">📝</div>
            <div class="action-title">租借申请</div>
            <div class="action-desc">处理租借申请</div>
          </div>
          <div class="action-card" @click="goToDeviceActivities">
            <div class="action-icon">📈</div>
            <div class="action-title">设备动态</div>
            <div class="action-desc">查看设备活动</div>
          </div>
        </div>
        
        <div class="rental-summary">
          <div class="rental-header">
            <h3>本月租金收入</h3>
            <span class="rental-month">2024年1月</span>
          </div>
          <div class="rental-amount">¥{{ monthlyRental }}</div>
          <div class="rental-stats">
            <div class="rental-stat-item">
              <span class="rental-stat-value">{{ activeRentals }}</span>
              <span class="rental-stat-label">进行中</span>
            </div>
            <div class="rental-stat-item">
              <span class="rental-stat-value">{{ pendingApplies }}</span>
              <span class="rental-stat-label">待审核</span>
            </div>
            <div class="rental-stat-item">
              <span class="rental-stat-value">{{ completedCount }}</span>
              <span class="rental-stat-label">已完成</span>
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
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'OwnerDashboard',
  components: {
    Plus
  },
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const userName = computed(() => {
      return userStore.userInfo?.username || userStore.userInfo?.name || '机主'
    })
    
    const stats = ref({
      deviceCount: 8,
      onlineCount: 6,
      rentalCount: 3,
      totalEarnings: 28500
    })
    
    const monthlyRental = ref(4500)
    const activeRentals = ref(3)
    const pendingApplies = ref(2)
    const completedCount = ref(15)
    
    const devices = ref([
      { id: 1, name: 'DJI Mavic 3 Pro', model: 'Mavic 3 Pro', status: 'rental', renter: '张飞手', income: 6800 },
      { id: 2, name: 'DJI Phantom 4 RTK', model: 'Phantom 4 RTK', status: 'online', renter: '', income: 8200 },
      { id: 3, name: 'DJI Inspire 3', model: 'Inspire 3', status: 'rental', renter: '李飞手', income: 5500 },
      { id: 4, name: 'DJI Mini 3 Pro', model: 'Mini 3 Pro', status: 'online', renter: '', income: 3200 },
      { id: 5, name: 'DJI Air 3', model: 'Air 3', status: 'rental', renter: '王飞手', income: 4800 }
    ])
    
    const getStatusType = (status) => {
      const types = {
        online: 'success',
        offline: 'danger',
        rental: 'primary',
        maintenance: 'warning'
      }
      return types[status] || 'info'
    }
    
    const getStatusLabel = (status) => {
      const labels = {
        online: '在线',
        offline: '离线',
        rental: '租借中',
        maintenance: '维护中'
      }
      return labels[status] || status
    }
    
    const goToDevices = () => {
      router.push('/devices')
    }
    
    const goToAddDevice = () => {
      router.push('/devices/add')
    }
    
    const goToDetail = (id) => {
      router.push(`/device/${id}`)
    }
    
    const goToRentalApply = () => {
      router.push('/rental-apply')
    }
    
    const goToDeviceActivities = () => {
      router.push('/device-activities')
    }
    
    onMounted(() => {
      console.log('Owner dashboard mounted')
    })
    
    return {
      userName,
      stats,
      monthlyRental,
      activeRentals,
      pendingApplies,
      completedCount,
      devices,
      getStatusType,
      getStatusLabel,
      goToDevices,
      goToAddDevice,
      goToDetail,
      goToRentalApply,
      goToDeviceActivities
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

.device-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.online-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.rental-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
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

.add-device-btn {
  display: flex;
  align-items: center;
  gap: 6px;
}

.plus-icon {
  font-size: 14px;
}

.recent-devices {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.device-table {
  width: 100%;
}

.device-name {
  font-weight: 500;
  color: #1f2937;
}

.model-value {
  color: #6b7280;
}

.renter-name {
  color: #3b82f6;
}

.income-value {
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

.rental-summary {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 16px;
  padding: 24px;
  color: #fff;
}

.rental-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.rental-header h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.rental-month {
  font-size: 13px;
  opacity: 0.8;
}

.rental-amount {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 20px;
}

.rental-stats {
  display: flex;
  justify-content: space-between;
}

.rental-stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.rental-stat-value {
  font-size: 24px;
  font-weight: 700;
}

.rental-stat-label {
  font-size: 12px;
  opacity: 0.8;
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
}
</style>