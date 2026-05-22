<template>
  <div class="demand-list-container">
    <div class="page-header">
      <h1 class="page-title">需求列表</h1>
      <el-button type="primary" @click="goToPublishDemand">
        <el-icon><Plus /></el-icon>
        发布需求
      </el-button>
    </div>
    
    <el-card class="filter-card">
      <div class="filter-row">
        <el-input 
          v-model="searchText" 
          placeholder="搜索需求标题" 
          class="search-input"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select 
          v-model="statusFilter" 
          placeholder="选择状态" 
          class="status-select"
        >
          <el-option label="全部" value=""></el-option>
          <el-option label="待接单" value="pending"></el-option>
          <el-option label="进行中" value="processing"></el-option>
          <el-option label="已完成" value="completed"></el-option>
        </el-select>
        <el-button type="default" @click="handleFilter">筛选</el-button>
      </div>
    </el-card>
    
    <el-card class="demand-card">
      <el-table :data="demandList" border class="demand-table">
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
        <el-table-column prop="cropType" label="作物类型" width="120">
          <template #default="scope">
            <span>{{ scope.row.cropType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="workType" label="作业类型" width="120">
          <template #default="scope">
            <span>{{ scope.row.workType }}</span>
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
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button type="text" size="small" @click="goToDetail(scope.row.id)">查看详情</el-button>
            <el-button type="text" size="small" @click="handleApply(scope.row.id)">申请接单</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
          layout="prev, pager, next, jumper, ->, total"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'FarmerDemandList',
  components: {
    Plus,
    Search
  },
  setup() {
    const router = useRouter()
    
    const searchText = ref('')
    const statusFilter = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(50)
    
    const demandList = ref([
      { id: 1, title: '小麦喷洒作业需求', area: 200, cropType: '小麦', workType: '喷洒农药', status: 'pending', createTime: '2024-01-20 14:30:00' },
      { id: 2, title: '玉米病虫害防治', area: 150, cropType: '玉米', workType: '病虫害防治', status: 'processing', createTime: '2024-01-19 09:15:00' },
      { id: 3, title: '水稻施肥作业', area: 300, cropType: '水稻', workType: '施肥', status: 'completed', createTime: '2024-01-18 16:45:00' },
      { id: 4, title: '果园农药喷洒', area: 80, cropType: '苹果', workType: '喷洒农药', status: 'pending', createTime: '2024-01-17 10:20:00' },
      { id: 5, title: '棉花脱叶剂喷洒', area: 250, cropType: '棉花', workType: '脱叶处理', status: 'completed', createTime: '2024-01-16 15:30:00' }
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
      return date.toLocaleString('zh-CN')
    }
    
    const goToPublishDemand = () => {
      router.push('/farmer/publish-demand')
    }
    
    const goToDetail = (id) => {
      router.push(`/farmer/demand-detail/${id}`)
    }
    
    const handleApply = (id) => {
      ElMessage.info(`申请接单: 需求ID ${id}`)
    }
    
    const handleFilter = () => {
      ElMessage.info('筛选功能')
    }
    
    const handlePageChange = (page) => {
      currentPage.value = page
    }
    
    return {
      searchText,
      statusFilter,
      currentPage,
      pageSize,
      total,
      demandList,
      getStatusType,
      getStatusLabel,
      formatTime,
      goToPublishDemand,
      goToDetail,
      handleApply,
      handleFilter,
      handlePageChange
    }
  }
}
</script>

<style scoped>
.demand-list-container {
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

.filter-card {
  margin-bottom: 24px;
}

.filter-row {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input {
  width: 300px;
}

.status-select {
  width: 150px;
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

.time-text {
  color: #9ca3af;
  font-size: 13px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>