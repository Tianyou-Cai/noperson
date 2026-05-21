<template>
  <div class="demand-detail-container">
    <div class="page-header">
      <el-button type="text" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h1 class="page-title">需求详情</h1>
    </div>
    
    <el-card class="detail-card">
      <div class="detail-header">
        <h2 class="demand-title">{{ demand.title }}</h2>
        <el-tag :type="getStatusType(demand.status)" size="small">{{ getStatusLabel(demand.status) }}</el-tag>
      </div>
      
      <div class="detail-info">
        <div class="info-row">
          <span class="info-label">作物类型：</span>
          <span class="info-value">{{ demand.cropType }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">作业类型：</span>
          <span class="info-value">{{ demand.workType }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">作业面积：</span>
          <span class="info-value">{{ demand.area }} 亩</span>
        </div>
        <div class="info-row">
          <span class="info-label">作业地点：</span>
          <span class="info-value">{{ demand.location }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">期望时间：</span>
          <span class="info-value">{{ demand.workTime }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">需求描述：</span>
          <span class="info-value description">{{ demand.description }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">发布时间：</span>
          <span class="info-value">{{ demand.createTime }}</span>
        </div>
      </div>
      
      <div class="bidders-section">
        <h3 class="section-title">接单申请</h3>
        <el-table :data="bidders" border class="bidders-table">
          <el-table-column prop="name" label="飞手名称" width="120">
            <template #default="scope">
              <el-avatar size="small">{{ scope.row.name.charAt(0) }}</el-avatar>
              <span>{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="联系电话" width="120"></el-table-column>
          <el-table-column prop="experience" label="从业经验" width="100" align="center">
            <template #default="scope">{{ scope.row.experience }}年</template>
          </el-table-column>
          <el-table-column prop="bidPrice" label="报价(元/亩)" width="120" align="right">
            <template #default="scope">¥{{ scope.row.bidPrice }}</template>
          </el-table-column>
          <el-table-column prop="totalPrice" label="总价" width="100" align="right">
            <template #default="scope">¥{{ scope.row.totalPrice }}</template>
          </el-table-column>
          <el-table-column prop="bidTime" label="报价时间" width="140"></el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleSelect(scope.row)">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'FarmerDemandDetail',
  components: {
    ArrowLeft
  },
  setup() {
    const router = useRouter()
    
    const demand = ref({
      id: 1,
      title: '小麦喷洒作业需求',
      cropType: '小麦',
      workType: '喷洒农药',
      area: 200,
      location: '山东省济南市章丘区',
      workTime: '2024-01-25',
      description: '需要对200亩小麦田进行农药喷洒作业，主要防治蚜虫和白粉病。请携带合适的农药设备，注意作业安全。',
      status: 'pending',
      createTime: '2024-01-20 14:30:00'
    })
    
    const bidders = ref([
      { id: 1, name: '张飞手', phone: '138****1234', experience: 5, bidPrice: 8, totalPrice: 1600, bidTime: '2024-01-20 15:00:00' },
      { id: 2, name: '李飞手', phone: '139****5678', experience: 3, bidPrice: 7.5, totalPrice: 1500, bidTime: '2024-01-20 15:30:00' },
      { id: 3, name: '王飞手', phone: '137****9012', experience: 6, bidPrice: 8.5, totalPrice: 1700, bidTime: '2024-01-20 16:00:00' }
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
    
    const goBack = () => {
      router.back()
    }
    
    const handleSelect = (bidder) => {
      ElMessage.success(`已选择飞手 ${bidder.name}`)
    }
    
    return {
      demand,
      bidders,
      getStatusType,
      getStatusLabel,
      goBack,
      handleSelect
    }
  }
}
</script>

<style scoped>
.demand-detail-container {
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

.demand-title {
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

.info-value.description {
  flex: 1;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 16px;
}

.bidders-table {
  width: 100%;
}
</style>