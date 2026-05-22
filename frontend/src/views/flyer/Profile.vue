<template>
  <div class="profile-container flyer-profile">
    <div class="profile-header">
      <h1 class="profile-title">飞手个人资料</h1>
      <el-button type="primary" @click="handleEdit">编辑</el-button>
    </div>
    
    <el-card class="profile-card">
      <div class="owner-basic-info">
        <el-avatar 
          v-if="userInfo.avatar" 
          :src="userInfo.avatar"
          size="large"
          class="owner-avatar"
        ></el-avatar>
        <el-avatar 
          v-else 
          size="large"
          class="owner-avatar"
        >{{ userInfo.name ? userInfo.name.charAt(0) : '飞' }}</el-avatar>
        <div class="owner-name">{{ userInfo.name || '未设置姓名' }}</div>
        <div class="owner-role">飞手</div>
      </div>
      
      <div class="info-item">
        <span class="info-label">手机号：</span>
        <span class="info-value">{{ userInfo.phone || '未设置' }}</span>
      </div>
      
      <div class="info-item">
        <span class="info-label">所在位置：</span>
        <span class="info-value">{{ userInfo.address || '未设置' }}</span>
      </div>
      
      <div class="info-item">
        <span class="info-label">资质状态：</span>
        <el-tag :type="qualificationStatusType" size="small">{{ qualificationStatus }}</el-tag>
      </div>
      
      <div class="info-item">
        <span class="info-label">接单数量：</span>
        <span class="info-value">{{ stats.orderCount }}</span>
      </div>
      
      <div class="info-item">
        <span class="info-label">累计收入：</span>
        <span class="info-value earnings">¥{{ stats.totalEarnings }}</span>
      </div>
    </el-card>
    
    <el-card class="profile-card">
      <h2 class="section-title">快捷操作</h2>
      <div class="action-grid">
        <div class="action-item" @click="goToQualification">
          <span class="action-icon">📋</span>
          <span class="action-text">资质管理</span>
        </div>
        <div class="action-item" @click="goToAcceptOrder">
          <span class="action-icon">🤝</span>
          <span class="action-text">接单中心</span>
        </div>
        <div class="action-item" @click="goToWallet">
          <span class="action-icon">💰</span>
          <span class="action-text">我的钱包</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'

export default {
  name: 'FlyerProfile',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    const userInfo = ref({
      name: userStore.userInfo?.name || userStore.userInfo?.username || '',
      phone: userStore.userInfo?.phone || '',
      address: userStore.userInfo?.address || userStore.userInfo?.location || '',
      avatar: userStore.userInfo?.avatar || ''
    })
    
    const stats = ref({
      orderCount: 28,
      totalEarnings: 15680
    })
    
    const qualificationStatus = ref('审核通过')
    
    const qualificationStatusType = computed(() => {
      const statusMap = {
        '审核通过': 'success',
        '待审核': 'warning',
        '未认证': 'info',
        '审核拒绝': 'danger'
      }
      return statusMap[qualificationStatus.value] || 'info'
    })
    
    const handleEdit = () => {
      ElMessage.info('编辑功能开发中')
    }
    
    const goToQualification = () => {
      router.push('/flyer/qualification')
    }
    
    const goToAcceptOrder = () => {
      router.push('/flyer/accept-order')
    }
    
    const goToWallet = () => {
      router.push('/flyer/wallet')
    }
    
    return {
      userInfo,
      stats,
      qualificationStatus,
      qualificationStatusType,
      handleEdit,
      goToQualification,
      goToAcceptOrder,
      goToWallet
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 24px;
  min-height: 100%;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.profile-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.profile-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.owner-basic-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.owner-avatar {
  margin-bottom: 12px;
}

.owner-name {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.owner-role {
  font-size: 13px;
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
  padding: 2px 10px;
  border-radius: 10px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f7fa;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #6b7280;
}

.info-value {
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
}

.info-value.earnings {
  color: #ec4899;
}

.action-grid {
  display: flex;
  gap: 16px;
}

.action-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #f9fafb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-item:hover {
  background: #f3f4f6;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.action-text {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}
</style>