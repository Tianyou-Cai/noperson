<template>
  <div class="order-detail-container">
    <div class="page-header">
      <el-button type="text" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h1 class="page-title">订单详情</h1>
    </div>
    
    <el-card class="detail-card">
      <div class="detail-header">
        <h2 class="order-title">{{ order.title }}</h2>
        <el-tag :type="getStatusType(order.status)" size="small">{{ getStatusLabel(order.status) }}</el-tag>
      </div>
      
      <div class="detail-info">
        <div class="info-row">
          <span class="info-label">农户名称：</span>
          <span class="info-value">
            <el-avatar size="small">{{ order.farmer.charAt(0) }}</el-avatar>
            {{ order.farmer }}
          </span>
        </div>
        <div class="info-row">
          <span class="info-label">联系电话：</span>
          <span class="info-value">{{ order.phone }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">作物类型：</span>
          <span class="info-value">{{ order.cropType }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">作业类型：</span>
          <span class="info-value">{{ order.workType }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">作业面积：</span>
          <span class="info-value">{{ order.area }} 亩</span>
        </div>
        <div class="info-row">
          <span class="info-label">作业地点：</span>
          <span class="info-value">{{ order.location }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">报价金额：</span>
          <span class="info-value price">¥{{ order.price }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">报价时间：</span>
          <span class="info-value">{{ order.bidTime }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">订单描述：</span>
          <span class="info-value description">{{ order.description }}</span>
        </div>
      </div>
      
      <div class="action-section">
        <el-button 
          v-if="order.status === 'accepted'" 
          type="primary" 
          @click="handleStart"
        >开始作业</el-button>
        <el-button 
          v-if="order.status === 'processing'" 
          type="success" 
          @click="handleComplete"
        >完成作业</el-button>
        <el-button 
          v-if="order.status === 'completed'" 
          type="default" 
          @click="handleReview"
        >评价农户</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'FlyerOrderDetail',
  components: {
    ArrowLeft
  },
  setup() {
    const router = useRouter()
    
    const order = ref({
      id: 1,
      title: '小麦喷洒作业',
      farmer: '王农户',
      phone: '138****1234',
      cropType: '小麦',
      workType: '喷洒农药',
      area: 200,
      location: '山东省济南市章丘区',
      price: 1600,
      bidTime: '2024-01-20 15:00:00',
      description: '需要对200亩小麦田进行农药喷洒作业，主要防治蚜虫和白粉病。请携带合适的农药设备，注意作业安全。',
      status: 'processing'
    })
    
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
    
    const goBack = () => {
      router.back()
    }
    
    const handleStart = () => {
      ElMessage.success('开始作业')
    }
    
    const handleComplete = () => {
      ElMessageBox.confirm('确定完成此订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        ElMessage.success('订单已完成')
      }).catch(() => {
      })
    }
    
    const handleReview = () => {
      ElMessage.info('评价功能开发中')
    }
    
    return {
      order,
      getStatusType,
      getStatusLabel,
      goBack,
      handleStart,
      handleComplete,
      handleReview
    }
  }
}
</script>

<style scoped>
.order-detail-container {
  padding: 24px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.detail-card {
  background: #fff;
  border-radius: 16px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.order-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.detail-info {
  margin-bottom: 32px;
}

.info-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f5f7fa;
}

.info-label {
  width: 120px;
  color: #6b7280;
  font-weight: 500;
}

.info-value {
  color: #1f2937;
}

.info-value.price {
  color: #10b981;
  font-weight: 600;
}

.info-value.description {
  flex: 1;
}

.action-section {
  display: flex;
  gap: 12px;
}
</style>