<template>
  <div class="rental-apply-container">
    <el-card class="rental-card">
      <template #header>
        <div class="card-header">
          <h2 class="card-title">设备租借申请</h2>
        </div>
      </template>
      
      <div class="device-info-section">
        <h3>设备信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备名称">{{ deviceInfo.deviceName }}</el-descriptions-item>
          <el-descriptions-item label="设备型号">{{ deviceInfo.model }}</el-descriptions-item>
          <el-descriptions-item label="设备类型">{{ deviceInfo.deviceType }}</el-descriptions-item>
          <el-descriptions-item label="最大载重">{{ deviceInfo.maxLoad }} kg</el-descriptions-item>
          <el-descriptions-item label="续航时间">{{ deviceInfo.endurance }} 分钟</el-descriptions-item>
          <el-descriptions-item label="每小时租金">{{ deviceInfo.hourlyRent }} 元/小时</el-descriptions-item>
        </el-descriptions>
        
        <div class="device-image">
          <el-image 
            :src="getDeviceImage(deviceInfo.picture)"
            fit="contain"
            :preview-src-list="[getDeviceImage(deviceInfo.picture)]"
          />
        </div>
      </div>
      
      <div class="rental-form-section">
        <h3>租借信息</h3>
        <el-form :model="rentalForm" label-width="120px" class="rental-form">
          <el-form-item label="取设备时间" prop="pickupTime">
            <el-date-picker
              v-model="rentalForm.pickupTime"
              type="datetime"
              placeholder="选择取设备时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              :disabled-date="disabledDate"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="租借时长" prop="rentalHours">
            <el-input-number 
              v-model="rentalForm.rentalHours" 
              :min="1" 
              :max="24"
              :step="1"
              style="width: 100%"
              @change="calculateAmount"
            />
            <span class="hour-label">小时</span>
          </el-form-item>
          
          <el-form-item label="预计总费用" prop="estimatedAmount">
            <el-input 
              v-model="estimatedAmount" 
              readonly
              style="width: 100%"
            />
            <span class="amount-label">元</span>
          </el-form-item>
          
          <el-form-item label="当前账户余额" prop="currentBalance">
            <el-input 
              v-model="currentBalance" 
              readonly
              style="width: 100%"
            />
            <span class="amount-label">元</span>
          </el-form-item>
          
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="rentalForm.remark"
              type="textarea"
              rows="3"
              placeholder="请输入备注信息（选填）"
            />
          </el-form-item>
        </el-form>
      </div>
      
      <div class="action-section">
        <el-button 
          type="primary" 
          :loading="submitting"
          @click="submitRental"
          :disabled="!canSubmit"
        >
          提交租借申请
        </el-button>
        <el-button @click="goBack">取消</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from '../utils/axios'
import { ElMessage } from 'element-plus'

export default {
  name: 'RentalApply',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const deviceId = route.params.deviceId
    
    const deviceInfo = reactive({})
    const currentBalance = ref(0)
    const submitting = ref(false)
    
    const rentalForm = reactive({
      pickupTime: '',
      rentalHours: 1,
      remark: ''
    })
    
    const estimatedAmount = ref(0)
    
    // 计算是否可以提交
    const canSubmit = computed(() => {
      return rentalForm.pickupTime && rentalForm.rentalHours >= 1 && 
             parseFloat(currentBalance.value) >= parseFloat(estimatedAmount.value)
    })
    
    // 获取设备图片
    const getDeviceImage = (picture) => {
      if (!picture) {
        return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=drone%20equipment%20placeholder&image_size=square'
      }
      return picture.startsWith('http') ? picture : `/api/file/${picture}`
    }
    
    // 禁用过去的日期
    const disabledDate = (time) => {
      return time.getTime()< Date.now() - 8.64e7 // 只能选择明天及以后的日期
    }
    
    // 计算租金
    const calculateAmount = () =>{
      if (deviceInfo.hourlyRent) {
        estimatedAmount.value = (deviceInfo.hourlyRent * rentalForm.rentalHours).toFixed(2)
      }
    }
    
    // 获取设备信息和账户余额
    const fetchDeviceInfo = async () => {
      try {
        // 获取设备信息
        const deviceResponse = await axios.get(`/device/${deviceId}`)
        if (deviceResponse.code === 200) {
          Object.assign(deviceInfo, deviceResponse.data)
          calculateAmount()
        }
        
        // 获取账户余额
        const balanceResponse = await axios.get('/flyer/balance')
        if (balanceResponse.code === 200) {
          currentBalance.value = balanceResponse.data.balance || 0
        }
      } catch (error) {
        console.error('获取设备信息失败:', error)
        ElMessage.error('获取设备信息失败')
      }
    }
    
    // 提交租借申请
    const submitRental = async () => {
      if (!canSubmit.value) {
        ElMessage.warning('请填写完整信息并确保余额充足')
        return
      }
      
      submitting.value = true
      try {
        // 提交租借申请
        const response = await axios.post('/rental/create', {
          deviceId: deviceId,
          pickupTime: rentalForm.pickupTime,
          rentalHours: rentalForm.rentalHours,
          remark: rentalForm.remark
        })
        
        if (response.code === 200) {
          ElMessage.success('租借申请提交成功')
          router.push('/rental-history')
        } else {
          ElMessage.error(response.message || '提交失败')
        }
      } catch (error) {
        console.error('提交租借申请失败:', error)
        ElMessage.error('提交失败，请重试')
      } finally {
        submitting.value = false
      }
    }
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 页面加载时获取数据
    onMounted(() => {
      fetchDeviceInfo()
    })
    
    return {
      deviceInfo,
      rentalForm,
      estimatedAmount,
      currentBalance,
      submitting,
      canSubmit,
      getDeviceImage,
      disabledDate,
      calculateAmount,
      submitRental,
      goBack
    }
  }
}
</script>

<style scoped>
.rental-apply-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}

.rental-card {
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.device-info-section {
  margin-bottom: 24px;
}

.device-info-section h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.device-image {
  margin-top: 20px;
  text-align: center;
}

.device-image img {
  max-height: 200px;
  border-radius: 8px;
}

.rental-form-section {
  margin-bottom: 24px;
}

.rental-form-section h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.rental-form {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.hour-label {
  margin-left: 10px;
  color: #606266;
}

.amount-label {
  margin-left: 10px;
  color: #606266;
}

.action-section {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}
</style>
