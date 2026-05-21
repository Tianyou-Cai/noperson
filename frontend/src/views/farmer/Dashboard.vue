<template>
  <div class="dashboard farmer-dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">农户仪表盘</h1>
      <p class="dashboard-subtitle">欢迎回来，{{ userName }}！查看您的作业需求和账户概览</p>
    </div>
    
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon demand-icon">📋</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.demandCount }}</div>
          <div class="stat-label">发布的需求</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon pending-icon">⏳</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.pendingCount }}</div>
          <div class="stat-label">待接单</div>
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
        <div class="stat-icon completed-icon">✅</div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.completedCount }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
    </div>
    
    <div class="dashboard-content">
      <div class="recent-demands">
        <div class="section-header">
          <h2>最近发布的需求</h2>
          <el-button type="text" class="view-all-btn" @click="goToDemandList">查看全部 →</el-button>
        </div>
        <el-table :data="recentDemands" border class="demand-table">
          <el-table-column prop="title" label="需求标题" min-width="200">
            <template #default="scope">
              <span class="demand-title">{{ scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="area" label="作业面积(亩)" width="120" align="center">
            <template #default="scope">
              <span class="area-value">{{ scope.row.area }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="160">
            <template #default="scope">
              <span class="time-text">{{ formatTime(scope.row.createTime) }}</span>
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
          <div class="action-card" @click="goToPublishDemand">
            <div class="action-icon">✏️</div>
            <div class="action-title">发布需求</div>
            <div class="action-desc">填写地块信息，匹配飞手</div>
          </div>
          <div class="action-card" @click="goToMyDemands">
            <div class="action-icon">📝</div>
            <div class="action-title">我的需求</div>
            <div class="action-desc">查看需求进度</div>
          </div>
          <div class="action-card" @click="goToWallet">
            <div class="action-icon">💰</div>
            <div class="action-title">我的钱包</div>
            <div class="action-desc">查看余额和账单</div>
          </div>
          <div class="action-card" @click="goToProfile">
            <div class="action-icon">👤</div>
            <div class="action-title">个人资料</div>
            <div class="action-desc">完善个人信息</div>
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
  name: 'FarmerDashboard',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const userName = computed(() => {
      return userStore.userInfo?.username || userStore.userInfo?.name || '农户'
    })
    
    const stats = ref({
      demandCount: 12,
      pendingCount: 3,
      processingCount: 2,
      completedCount: 7
    })
    
    const recentDemands = ref([
      { id: 1, title: '小麦喷洒作业需求', area: 200, status: 'pending', createTime: '2024-01-20 14:30:00' },
      { id: 2, title: '玉米病虫害防治', area: 150, status: 'processing', createTime: '2024-01-19 09:15:00' },
      { id: 3, title: '水稻施肥作业', area: 300, status: 'completed', createTime: '2024-01-18 16:45:00' },
      { id: 4, title: '果园农药喷洒', area: 80, status: 'pending', createTime: '2024-01-17 10:20:00' },
      { id: 5, title: '棉花脱叶剂喷洒', area: 250, status: 'completed', createTime: '2024-01-16 15:30:00' }
    ])
    
    const getStatusType = (status) => {
      const types = {
        pending: 'warning',
        processing: 'primary',
        completed: 'success',
        cancelled: 'danger'
      }
      return types[status] || 'info'
    }
    
    const getStatusLabel = (status) => {
      const labels = {
        pending: '待接单',
        processing: '进行中',
        completed: '已完成',
        cancelled: '已取消'
      }
      return labels[status] || status
    }
    
    const formatTime = (timeStr) => {
      const date = new Date(timeStr)
      const now = new Date()
      const diffMs = now - date
      const diffMins = Math.floor(diffMs / 60000)
      const diffHours = Math.floor(diffMins / 60)
      const diffDays = Math.floor(diffHours / 24)
      
      if (diffMins < 1) return '刚刚'
      if (diffMins < 60) return `${diffMins}分钟前`
      if (diffHours < 24) return `${diffHours}小时前`
      if (diffDays < 7) return `${diffDays}天前`
      return date.toLocaleDateString()
    }
    
    const goToDemandList = () => {
      router.push('/demand-list')
    }
    
    const goToDetail = (id) => {
      router.push(`/demand-detail/${id}`)
    }
    
    const goToPublishDemand = () => {
      router.push('/publish-demand')
    }
    
    const goToMyDemands = () => {
      router.push('/my-demands')
    }
    
    const goToWallet = () => {
      router.push('/wallet')
    }
    
    const goToProfile = () => {
      router.push('/profile')
    }
    
    onMounted(() => {
      console.log('Farmer dashboard mounted')
    })
    
    return {
      userName,
      stats,
      recentDemands,
      getStatusType,
      getStatusLabel,
      formatTime,
      goToDemandList,
      goToDetail,
      goToPublishDemand,
      goToMyDemands,
      goToWallet,
      goToProfile
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

.demand-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.pending-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.processing-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.completed-icon {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
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

.recent-demands {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.demand-table {
  width: 100%;
}

.demand-title {
  font-weight: 500;
  color: #1f2937;
}

.area-value {
  font-weight: 600;
  color: #3b82f6;
}

.time-text {
  color: #9ca3af;
  font-size: 13px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
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