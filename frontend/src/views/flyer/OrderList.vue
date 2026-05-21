<template>
  <div class="order-list-container">
    <div class="page-header">
      <h1 class="page-title">订单列表</h1>
    </div>
    
    <div class="status-tabs">
      <el-tabs v-model="activeTab" class="status-tabs">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="待执行" name="accepted"></el-tab-pane>
        <el-tab-pane label="进行中" name="processing"></el-tab-pane>
        <el-tab-pane label="已完成" name="completed"></el-tab-pane>
      </el-tabs>
    </div>
    
    <el-card class="order-card">
      <el-table :data="filteredOrders" border class="order-table">
        <el-table-column prop="title" label="订单标题" min-width="200">
          <template #default="scope">
            <span class="order-title">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="farmer" label="农户" width="120">
          <template #default="scope">
            <el-avatar size="small">{{ scope.row.farmer.charAt(0) }}</el-avatar>
            <span>{{ scope.row.farmer }}</span>
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
        <el-table-column prop="location" label="作业地点" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="接单时间" width="140"></el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button type="text" size="small" @click="goToDetail(scope.row.id)">查看详情</el-button>
            <el-button 
              v-if="scope.row.status === 'accepted'" 
              type="primary" 
              size="small" 
              @click="handleStart(scope.row.id)"
            >
              开始作业
            </el-button>
            <el-button 
              v-if="scope.row.status === 'processing'" 
              type="success" 
              size="small" 
              @click="handleComplete(scope.row.id)"
            >
              完成作业
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'FlyerOrderList',
  setup() {
    const router = useRouter()
    
    const activeTab = ref('all')
    
    const orders = ref([
      { id: 1, title: '小麦喷洒作业', farmer: '王农户', area: 200, price: 1600, location: '山东省济南市', status: 'processing', createTime: '2024-01-20 15:00:00' },
      { id: 2, title: '玉米病虫害防治', farmer: '李农户', area: 150, price: 1125, location: '河南省郑州市', status: 'accepted', createTime: '2024-01-19 10:30:00' },
      { id: 3, title: '水稻施肥作业', farmer: '张农户', area: 300, price: 2250, location: '江苏省南京市', status: 'completed', createTime: '2024-01-18 09:00:00' },
      { id: 4, title: '果园农药喷洒', farmer: '赵农户', area: 80, price: 600, location: '陕西省西安市', status: 'accepted', createTime: '2024-01-17 14:00:00' },
      { id: 5, title: '棉花脱叶剂喷洒', farmer: '孙农户', area: 250, price: 1875, location: '新疆乌鲁木齐', status: 'completed', createTime: '2024-01-16 11:00:00' }
    ])
    
    const filteredOrders = computed(() => {
      if (activeTab.value === 'all') {
        return orders.value
      }
      return orders.value.filter(o => o.status === activeTab.value)
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
    
    const goToDetail = (id) => {
      router.push(`/order-detail/${id}`)
    }
    
    const handleStart = (id) => {
      ElMessage.success(`开始作业: 订单ID ${id}`)
    }
    
    const handleComplete = (id) => {
      ElMessageBox.confirm('确定完成此订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        ElMessage.success('订单已完成')
      }).catch(() => {
      })
    }
    
    return {
      activeTab,
      filteredOrders,
      getStatusType,
      getStatusLabel,
      goToDetail,
      handleStart,
      handleComplete
    }
  }
}
</script>

<style scoped>
.order-list-container {
  padding: 24px;
}

.page-header {
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

.order-card {
  background: #fff;
  border-radius: 16px;
}

.order-table {
  width: 100%;
}

.order-title {
  font-weight: 500;
  color: #1f2937;
}

.area-value {
  font-weight: 600;
  color: #3b82f6;
}

.price-value {
  font-weight: 600;
  color: #10b981;
}
</style>