<template>
  <div class="equipment-page">
    <div class="page-header">
      <h1 class="page-title">设备租用</h1>
      <p class="page-subtitle">租用无人机设备，满足作业需求</p>
    </div>
    
    <div class="filter-bar">
      <div class="filter-group">
        <el-select v-model="filter.type" placeholder="设备类型" class="filter-select">
          <el-option label="全部类型" value="" />
          <el-option label="植保无人机" value="crop" />
          <el-option label="测绘无人机" value="survey" />
          <el-option label="巡检无人机" value="inspection" />
        </el-select>
      </div>
      <div class="filter-group">
        <el-select v-model="filter.status" placeholder="设备状态" class="filter-select">
          <el-option label="全部状态" value="" />
          <el-option label="可租用" value="available" />
          <el-option label="租用中" value="rented" />
        </el-select>
      </div>
      <div class="filter-group">
        <el-input 
          v-model="filter.keyword" 
          placeholder="搜索设备名称" 
          class="search-input"
          prefix-icon="Search"
        />
      </div>
      <el-button type="primary" @click="handleSearch" class="search-btn">搜索</el-button>
    </div>
    
    <div class="equipment-grid">
      <div 
        v-for="equipment in equipmentList" 
        :key="equipment.id" 
        class="equipment-card"
        :class="{ 'equipment-card--available': equipment.status === 'available' }"
      >
        <div class="equipment-image">
          <img :src="equipment.image" :alt="equipment.name" />
          <div class="equipment-badge" :class="equipment.status">
            {{ equipment.status === 'available' ? '可租用' : '租用中' }}
          </div>
        </div>
        <div class="equipment-info">
          <h3 class="equipment-name">{{ equipment.name }}</h3>
          <p class="equipment-desc">{{ equipment.description }}</p>
          <div class="equipment-specs">
            <div class="spec-item">
              <span class="spec-icon">⚙️</span>
              <span>{{ equipment.type }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-icon">📦</span>
              <span>{{ equipment.weight }}kg</span>
            </div>
            <div class="spec-item">
              <span class="spec-icon">⏱️</span>
              <span>{{ equipment.duration }}min</span>
            </div>
          </div>
          <div class="equipment-price">
            <span class="price-label">租金</span>
            <span class="price-value">{{ equipment.price }}</span>
            <span class="price-unit">/天</span>
          </div>
          <el-button 
            type="primary" 
            class="rent-btn"
            :disabled="equipment.status !== 'available'"
            @click="handleRent(equipment)"
          >
            {{ equipment.status === 'available' ? '立即租用' : '已租用' }}
          </el-button>
        </div>
      </div>
    </div>
    
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 30]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
      />
    </div>
    
    <el-dialog title="租用设备" v-model="rentDialogVisible" width="480px">
      <div class="rent-form">
        <div class="rent-equipment-info">
          <img :src="selectedEquipment?.image" :alt="selectedEquipment?.name" class="rent-image" />
          <div class="rent-equipment-detail">
            <h3>{{ selectedEquipment?.name }}</h3>
            <p>{{ selectedEquipment?.description }}</p>
            <div class="rent-price">
              <span class="rent-price-label">租金：</span>
              <span class="rent-price-value">{{ selectedEquipment?.price }}</span>
              <span class="rent-price-unit">/天</span>
            </div>
          </div>
        </div>
        
        <el-form :model="rentForm" :rules="rentRules" ref="rentFormRef" class="form-content">
          <el-form-item prop="startDate" label="租用开始日期">
            <el-date-picker
              v-model="rentForm.startDate"
              type="date"
              placeholder="选择开始日期"
              class="date-picker"
            />
          </el-form-item>
          
          <el-form-item prop="endDate" label="租用结束日期">
            <el-date-picker
              v-model="rentForm.endDate"
              type="date"
              placeholder="选择结束日期"
              class="date-picker"
            />
          </el-form-item>
          
          <el-form-item prop="purpose" label="租用用途">
            <el-select v-model="rentForm.purpose" placeholder="请选择用途">
              <el-option label="农业植保" value="crop" />
              <el-option label="土地测绘" value="survey" />
              <el-option label="电力巡检" value="inspection" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
          
          <el-form-item prop="remark" label="备注">
            <el-input
              v-model="rentForm.remark"
              type="textarea"
              placeholder="请输入备注信息"
              rows="3"
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="rentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRent">确认租用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'FlyerEquipment',
  setup() {
    const filter = reactive({
      type: '',
      status: '',
      keyword: ''
    })
    
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 24
    })
    
    const rentDialogVisible = ref(false)
    const selectedEquipment = ref(null)
    const rentFormRef = ref(null)
    
    const rentForm = reactive({
      startDate: '',
      endDate: '',
      purpose: '',
      remark: ''
    })
    
    const rentRules = {
      startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
      endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
      purpose: [{ required: true, message: '请选择租用用途', trigger: 'change' }]
    }
    
    const equipmentList = ref([
      {
        id: 1,
        name: 'DJI Mavic 3',
        description: '专业级航拍无人机，配备全画幅相机，适合高精度测绘任务',
        type: '测绘无人机',
        weight: '0.8',
        duration: '46',
        price: '500',
        status: 'available',
        image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=DJI%20Mavic%203%20drone%20professional%20photography%20white%20background&image_size=landscape_4_3'
      },
      {
        id: 2,
        name: 'DJI Agras T40',
        description: '农业植保无人机，大容量药箱，高效喷洒作业',
        type: '植保无人机',
        weight: '40',
        duration: '30',
        price: '1500',
        status: 'available',
        image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=DJI%20Agras%20T40%20agricultural%20drone%20sprayer%20white%20background&image_size=landscape_4_3'
      },
      {
        id: 3,
        name: 'Autel EVO II Pro',
        description: '高性能航拍无人机，6K高清视频录制',
        type: '测绘无人机',
        weight: '1.1',
        duration: '40',
        price: '450',
        status: 'rented',
        image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Autel%20EVO%20II%20Pro%20drone%20professional%20white%20background&image_size=landscape_4_3'
      },
      {
        id: 4,
        name: 'DJI Phantom 4 RTK',
        description: '高精度测绘无人机，厘米级定位精度',
        type: '测绘无人机',
        weight: '1.4',
        duration: '30',
        price: '800',
        status: 'available',
        image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=DJI%20Phantom%204%20RTK%20drone%20mapping%20white%20background&image_size=landscape_4_3'
      },
      {
        id: 5,
        name: 'Yuneec H520',
        description: '工业级巡检无人机，六旋翼设计，稳定可靠',
        type: '巡检无人机',
        weight: '2.8',
        duration: '50',
        price: '600',
        status: 'available',
        image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=Yuneec%20H520%20industrial%20drone%20inspection%20white%20background&image_size=landscape_4_3'
      },
      {
        id: 6,
        name: 'DJI Agras T20',
        description: '紧凑型植保无人机，灵活高效，适合小面积作业',
        type: '植保无人机',
        weight: '23',
        duration: '22',
        price: '1000',
        status: 'rented',
        image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=DJI%20Agras%20T20%20agricultural%20drone%20white%20background&image_size=landscape_4_3'
      }
    ])
    
    const handleSearch = () => {
      console.log('搜索条件:', filter)
      ElMessage.info('搜索功能开发中')
    }
    
    const handleRent = (equipment) => {
      if (equipment.status !== 'available') return
      selectedEquipment.value = equipment
      rentDialogVisible.value = true
    }
    
    const submitRent = async () => {
      try {
        await rentFormRef.value.validate()
        
        const start = new Date(rentForm.startDate)
        const end = new Date(rentForm.endDate)
        
        if (end <= start) {
          ElMessage.error('结束日期必须大于开始日期')
          return
        }
        
        const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24))
        const totalPrice = days * parseFloat(selectedEquipment.value.price)
        
        ElMessage.success(`租用成功！共${days}天，总费用：¥${totalPrice}`)
        rentDialogVisible.value = false
        
        rentForm.startDate = ''
        rentForm.endDate = ''
        rentForm.purpose = ''
        rentForm.remark = ''
        selectedEquipment.value = null
      } catch (error) {
        console.error('表单验证失败:', error)
      }
    }
    
    return {
      filter,
      pagination,
      equipmentList,
      rentDialogVisible,
      selectedEquipment,
      rentForm,
      rentRules,
      rentFormRef,
      handleSearch,
      handleRent,
      submitRent
    }
  }
}
</script>

<style scoped>
.equipment-page {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
}

.filter-select {
  width: 160px;
}

.search-input {
  width: 240px;
}

.search-btn {
  margin-left: auto;
}

.equipment-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.equipment-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.equipment-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.equipment-card--available {
  border: 2px solid rgba(59, 130, 246, 0.2);
}

.equipment-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.equipment-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.equipment-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.equipment-badge.available {
  background: rgba(34, 197, 94, 0.9);
  color: #fff;
}

.equipment-badge.rented {
  background: rgba(156, 163, 175, 0.9);
  color: #fff;
}

.equipment-info {
  padding: 20px;
}

.equipment-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.equipment-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 12px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.equipment-specs {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.spec-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #9ca3af;
}

.spec-icon {
  font-size: 14px;
}

.equipment-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 16px;
}

.price-label {
  font-size: 13px;
  color: #9ca3af;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
}

.price-unit {
  font-size: 13px;
  color: #9ca3af;
}

.rent-btn {
  width: 100%;
  height: 44px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
}

.pagination-container {
  display: flex;
  justify-content: center;
}

.pagination {
  margin-top: 24px;
}

.rent-form {
  padding: 16px 0;
}

.rent-equipment-info {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
  margin-bottom: 20px;
}

.rent-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.rent-equipment-detail {
  flex: 1;
}

.rent-equipment-detail h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.rent-equipment-detail p {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 12px 0;
}

.rent-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.rent-price-label {
  font-size: 13px;
  color: #6b7280;
}

.rent-price-value {
  font-size: 20px;
  font-weight: 700;
  color: #ef4444;
}

.rent-price-unit {
  font-size: 13px;
  color: #6b7280;
}

.form-content {
  padding: 0 8px;
}

.date-picker {
  width: 100%;
}

@media (max-width: 1200px) {
  .equipment-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .equipment-page {
    padding: 16px;
  }
  
  .filter-bar {
    gap: 12px;
  }
  
  .filter-select {
    width: 140px;
  }
  
  .search-input {
    width: 100%;
    max-width: 200px;
  }
  
  .search-btn {
    margin-left: 0;
    width: 100%;
  }
  
  .equipment-grid {
    grid-template-columns: 1fr;
  }
  
  .rent-equipment-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .rent-image {
    width: 150px;
    height: 150px;
  }
}
</style>