<template>
  <div class="profile-container farmer-profile">
    <div class="profile-header">
      <h1 class="profile-title">农户个人资料</h1>
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
        >{{ userInfo.name ? userInfo.name.charAt(0) : '农' }}</el-avatar>
        <div class="owner-name">{{ userInfo.name || '未设置姓名' }}</div>
        <div class="owner-role">农户</div>
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
        <span class="info-label">需求数量：</span>
        <span class="info-value">{{ stats.demandCount }}</span>
      </div>
      
      <div class="info-item">
        <span class="info-label">完成订单：</span>
        <span class="info-value">{{ stats.completedCount }}</span>
      </div>
    </el-card>
    
    <el-card class="profile-card">
      <h2 class="section-title">快捷操作</h2>
      <div class="action-grid">
        <div class="action-item" @click="goToPublishDemand">
          <span class="action-icon">✏️</span>
          <span class="action-text">发布需求</span>
        </div>
        <div class="action-item" @click="goToMyDemands">
          <span class="action-icon">📝</span>
          <span class="action-text">我的需求</span>
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
  name: 'FarmerProfile',
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
      demandCount: 12,
      completedCount: 7
    })
    
    const handleEdit = () => {
      ElMessage.info('编辑功能开发中')
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
    
    return {
      userInfo,
      stats,
      handleEdit,
      goToPublishDemand,
      goToMyDemands,
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
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
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