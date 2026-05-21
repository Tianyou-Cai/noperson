<template>
  <div class="accept-order-container">
    <div class="page-header">
      <h1 class="page-title">接单中心</h1>
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
          v-model="workTypeFilter" 
          placeholder="作业类型" 
          class="worktype-select"
        >
          <el-option label="全部" value=""></el-option>
          <el-option label="喷洒农药" value="喷洒农药"></el-option>
          <el-option label="施肥" value="施肥"></el-option>
          <el-option label="病虫害防治" value="病虫害防治"></el-option>
          <el-option label="脱叶处理" value="脱叶处理"></el-option>
        </el-select>
        <el-button type="default" @click="handleFilter">筛选</el-button>
      </div>
    </el-card>
    
    <div class="demand-list">
      <el-card 
        v-for="demand in demandList" 
        :key="demand.id" 
        class="demand-card"
      >
        <div class="demand-header">
          <h3 class="demand-title">{{ demand.title }}</h3>
          <el-tag type="warning">待接单</el-tag>
        </div>
        <div class="demand-info">
          <div class="info-row">
            <span class="info-item">
              <span class="info-icon">🌾</span>
              <span>{{ demand.cropType }}</span>
            </span>
            <span class="info-item">
              <span class="info-icon">🚁</span>
              <span>{{ demand.workType }}</span>
            </span>
            <span class="info-item">
              <span class="info-icon">📏</span>
              <span>{{ demand.area }}亩</span>
            </span>
            <span class="info-item">
              <span class="info-icon">📍</span>
              <span>{{ demand.location }}</span>
            </span>
          </div>
        </div>
        <div class="demand-desc">{{ demand.description }}</div>
        <div class="demand-footer">
          <span class="demand-time">{{ demand.createTime }}</span>
          <div class="demand-actions">
            <el-button type="text" @click="goToDetail(demand.id)">查看详情</el-button>
            <el-button type="primary" @click="handleBid(demand)">接单报价</el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <el-dialog
      v-model="bidDialogVisible"
      title="接单报价"
      width="500px"
    >
      <el-form ref="bidFormRef" :model="bidForm" :rules="bidRules" label-width="100px">
        <el-form-item label="报价(元/亩)" prop="price">
          <el-input v-model.number="bidForm.price" type="number" placeholder="请输入报价"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="bidForm.remark" type="textarea" rows="3" placeholder="请输入备注信息"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bidDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitBid">确认报价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'FlyerAcceptOrder',
  components: {
    Search
  },
  setup() {
    const router = useRouter()
    
    const searchText = ref('')
    const workTypeFilter = ref('')
    const bidDialogVisible = ref(false)
    const bidFormRef = ref(null)
    
    const currentDemand = ref(null)
    
    const bidForm = reactive({
      price: '',
      remark: ''
    })
    
    const bidRules = {
      price: [
        { required: true, message: '请输入报价', trigger: 'blur' },
        { type: 'number', min: 0.1, message: '报价必须大于0', trigger: 'blur' }
      ]
    }
    
    const demandList = ref([
      { id: 1, title: '小麦喷洒作业需求', cropType: '小麦', workType: '喷洒农药', area: 200, location: '山东省济南市', description: '需要对200亩小麦田进行农药喷洒作业，主要防治蚜虫和白粉病。', createTime: '2024-01-20 14:30:00' },
      { id: 2, title: '玉米病虫害防治', cropType: '玉米', workType: '病虫害防治', area: 150, location: '河南省郑州市', description: '玉米田病虫害防治，面积150亩，需要专业设备。', createTime: '2024-01-19 09:15:00' },
      { id: 3, title: '水稻施肥作业', cropType: '水稻', workType: '施肥', area: 300, location: '江苏省南京市', description: '水稻追肥作业，需要无人机施肥设备。', createTime: '2024-01-18 16:45:00' },
      { id: 4, title: '果园农药喷洒', cropType: '苹果', workType: '喷洒农药', area: 80, location: '陕西省西安市', description: '苹果园农药喷洒，80亩，要求精准作业。', createTime: '2024-01-17 10:20:00' }
    ])
    
    const handleFilter = () => {
      ElMessage.info('筛选功能')
    }
    
    const goToDetail = (id) => {
      router.push(`/demand-detail/${id}`)
    }
    
    const handleBid = (demand) => {
      currentDemand.value = demand
      bidForm.price = ''
      bidForm.remark = ''
      bidDialogVisible.value = true
    }
    
    const handleSubmitBid = async () => {
      try {
        await bidFormRef.value.validate()
        ElMessage.success(`已报价 ¥${bidForm.price}/亩，等待农户确认`)
        bidDialogVisible.value = false
      } catch (error) {
        ElMessage.error('请填写完整信息')
      }
    }
    
    return {
      searchText,
      workTypeFilter,
      bidDialogVisible,
      bidFormRef,
      bidForm,
      demandList,
      handleFilter,
      goToDetail,
      handleBid,
      handleSubmitBid
    }
  }
}
</script>

<style scoped>
.accept-order-container {
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

.worktype-select {
  width: 150px;
}

.demand-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.demand-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
}

.demand-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.demand-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.demand-info {
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6b7280;
}

.info-icon {
  font-size: 16px;
}

.demand-desc {
  color: #6b7280;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 16px;
}

.demand-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f5f7fa;
}

.demand-time {
  color: #9ca3af;
  font-size: 13px;
}

.demand-actions {
  display: flex;
  gap: 12px;
}
</style>