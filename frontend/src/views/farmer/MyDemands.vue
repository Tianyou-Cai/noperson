<template>
  <div class="my-demands-container">
    <div class="page-header">
      <h1 class="page-title">我的需求</h1>
      <el-button type="primary" @click="goToPublishDemand">
        <el-icon><Plus /></el-icon>
        发布需求
      </el-button>
    </div>
    
    <div class="status-tabs">
      <el-tabs v-model="activeTab" class="status-tabs">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待接单" name="pending"></el-tab-pane>
        <el-tab-pane label="进行中" name="processing"></el-tab-pane>
        <el-tab-pane label="已完成" name="completed"></el-tab-pane>
      </el-tabs>
    </div>
    
    <el-card class="demand-card">
      <el-table :data="filteredDemands" border class="demand-table">
        <el-table-column label="需求标题" min-width="200">
          <template #default="scope">
            <span class="demand-title">{{ getDemandTitle(scope.row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="landArea" label="作业面积(亩)" width="120" align="center">
          <template #default="scope">
            <span class="area-value">{{ scope.row.landArea }}</span>
          </template>
        </el-table-column>
        <el-table-column label="作业类型" width="120">
          <template #default="scope">
            <span>{{ getWorkType(scope.row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="接单状态" width="100" align="center">
          <template #default="scope">
            <span class="bid-count">{{ scope.row.flyerName ? '已接单' : '待接单' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template #default="scope">
            <span class="time-text">{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button type="text" size="small" @click="goToDetail(scope.row.demandId)">查看详情</el-button>
            <el-button 
              v-if="scope.row.status === 0" 
              type="text" 
              size="small" 
              @click="handleCancel(scope.row.demandId)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

export default {
  name: 'FarmerMyDemands',
  components: {
    Plus
  },
  setup() {
    const router = useRouter()
    
    const activeTab = ref('all')
    const demands = ref([])
    const loading = ref(false)
    
    const statusMap = {
      0: 'pending',
      1: 'processing',
      2: 'completed',
      3: 'cancelled'
    }
    
    const filteredDemands = computed(() => {
      if (activeTab.value === 'all') {
        return demands.value
      }
      const statusCode = Object.keys(statusMap).find(key => statusMap[key] === activeTab.value)
      return demands.value.filter(d => d.status === parseInt(statusCode))
    })
    
    const getStatusText = (statusCode) => {
      const map = {
        0: '待接单',
        1: '进行中',
        2: '已完成',
        3: '已取消'
      }
      return map[statusCode] || '未知'
    }
    
    const getStatusType = (statusCode) => {
      const map = {
        0: 'warning',
        1: 'primary',
        2: 'success',
        3: 'danger'
      }
      return map[statusCode] || 'info'
    }
    
    const getDemandTitle = (demand) => {
      const orderType = demand.orderType === 1 ? '喷洒' : demand.orderType === 2 ? '巡检' : '作业'
      const cropType = demand.cropType || '作物'
      return `${cropType}${orderType}需求`
    }
    
    const getWorkType = (demand) => {
      if (demand.orderType === 1) {
        return demand.pestType ? `病虫害防治(${demand.pestType})` : '喷洒农药'
      } else if (demand.orderType === 2) {
        return demand.inspectionPurpose || '地块巡检'
      }
      return '其他'
    }
    
    const loadDemands = async () => {
      loading.value = true
      try {
        const response = await axios.get('/demand/spray/farmer/list')
        if (response.code === 200 && response.data) {
          demands.value = response.data.records || []
        } else {
          ElMessage.warning('暂无需求数据')
        }
      } catch (error) {
        console.error('获取需求列表失败:', error)
        ElMessage.error('获取需求列表失败')
      } finally {
        loading.value = false
      }
    }
    
    const formatTime = (timeStr) => {
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN')
    }
    
    const goToPublishDemand = () => {
      router.push('/farmer/publish-demand')
    }
    
    const goToDetail = (id) => {
      router.push(`/farmer/demand-detail/${id}`)
    }
    
    const handleCancel = async (id) => {
      ElMessageBox.confirm('确定要取消这个需求吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await axios.post(`/demand/cancel/${id}`)
          if (response.code === 200) {
            ElMessage.success('需求已取消')
            // 更新需求状态为已取消
            const demand = demands.value.find(d => d.id === id)
            if (demand) {
              demand.status = 3
            }
          } else {
            ElMessage.error(response.msg || '取消失败')
          }
        } catch (error) {
          console.error('取消需求失败:', error)
          ElMessage.error('取消失败，请重试')
        }
      }).catch(() => {
      })
    }
    
    onMounted(() => {
      loadDemands()
    })
    
    return {
      activeTab,
      filteredDemands,
      getStatusType,
      getStatusText,
      getDemandTitle,
      getWorkType,
      formatTime,
      goToPublishDemand,
      goToDetail,
      handleCancel
    }
  }
}
</script>

<style scoped>
.my-demands-container {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.status-tabs {
  margin-bottom: 24px;
}

.demand-card {
  background: #fff;
  border-radius: 16px;
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

.bid-count {
  color: #f59e0b;
  font-weight: 600;
}

.time-text {
  color: #9ca3af;
  font-size: 13px;
}
</style>