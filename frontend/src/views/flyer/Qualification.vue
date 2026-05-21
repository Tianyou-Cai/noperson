<template>
  <div class="qualification-container">
    <div class="page-header">
      <h1 class="page-title">资质管理</h1>
    </div>
    
    <el-card class="status-card">
      <div class="status-header">
        <div class="status-icon" :class="statusClass">{{ statusIcon }}</div>
        <div class="status-info">
          <h2 class="status-title">{{ qualificationStatus }}</h2>
          <p class="status-desc">{{ statusDesc }}</p>
        </div>
      </div>
    </el-card>
    
    <el-card class="form-card">
      <h3 class="form-title">资质信息</h3>
      <el-form ref="qualificationFormRef" :model="qualificationForm" :rules="rules" label-width="120px">
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="qualificationForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="qualificationForm.idCard" placeholder="请输入身份证号"></el-input>
        </el-form-item>
        
        <el-form-item label="驾驶证类型" prop="licenseType">
          <el-select v-model="qualificationForm.licenseType" placeholder="请选择驾驶证类型">
            <el-option label="无人机驾驶证" value="drone"></el-option>
            <el-option label="民航无人机执照" value="civil"></el-option>
            <el-option label="其他资质" value="other"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="证书编号" prop="licenseNumber">
          <el-input v-model="qualificationForm.licenseNumber" placeholder="请输入证书编号"></el-input>
        </el-form-item>
        
        <el-form-item label="从业年限" prop="experience">
          <el-input v-model.number="qualificationForm.experience" placeholder="请输入从业年限" type="number"></el-input>
        </el-form-item>
        
        <el-form-item label="证书照片">
          <el-upload
            v-model:file-list="licenseFiles"
            class="upload-demo"
            action="/file/upload"
            :limit="3"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交审核</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'FlyerQualification',
  components: {
    Plus
  },
  setup() {
    const qualificationFormRef = ref(null)
    
    const qualificationForm = reactive({
      realName: '',
      idCard: '',
      licenseType: '',
      licenseNumber: '',
      experience: ''
    })
    
    const licenseFiles = ref([])
    
    const qualificationStatus = ref('审核通过')
    
    const statusDesc = computed(() => {
      const desc = {
        '审核通过': '您的资质已通过审核，可以正常接单',
        '待审核': '您的资质正在审核中，请耐心等待',
        '审核拒绝': '您的资质审核未通过，请检查信息后重新提交'
      }
      return desc[qualificationStatus.value] || ''
    })
    
    const statusIcon = computed(() => {
      const icons = {
        '审核通过': '✓',
        '待审核': '⏳',
        '审核拒绝': '✗'
      }
      return icons[qualificationStatus.value] || '?'
    })
    
    const statusClass = computed(() => {
      const classes = {
        '审核通过': 'status-success',
        '待审核': 'status-pending',
        '审核拒绝': 'status-rejected'
      }
      return classes[qualificationStatus.value] || ''
    })
    
    const rules = {
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' }
      ],
      idCard: [
        { required: true, message: '请输入身份证号', trigger: 'blur' },
        { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
      ],
      licenseType: [
        { required: true, message: '请选择驾驶证类型', trigger: 'change' }
      ],
      licenseNumber: [
        { required: true, message: '请输入证书编号', trigger: 'blur' }
      ],
      experience: [
        { required: true, message: '请输入从业年限', trigger: 'blur' },
        { type: 'number', min: 0, message: '从业年限不能为负数', trigger: 'blur' }
      ]
    }
    
    const handleSubmit = async () => {
      try {
        await qualificationFormRef.value.validate()
        ElMessage.success('资质信息提交成功，等待审核')
      } catch (error) {
        ElMessage.error('请填写完整信息')
      }
    }
    
    const handleReset = () => {
      qualificationForm.realName = ''
      qualificationForm.idCard = ''
      qualificationForm.licenseType = ''
      qualificationForm.licenseNumber = ''
      qualificationForm.experience = ''
      licenseFiles.value = []
      qualificationFormRef.value.clearValidate()
    }
    
    return {
      qualificationFormRef,
      qualificationForm,
      licenseFiles,
      qualificationStatus,
      statusDesc,
      statusIcon,
      statusClass,
      rules,
      handleSubmit,
      handleReset
    }
  }
}
</script>

<style scoped>
.qualification-container {
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

.status-card {
  background: linear-gradient(135deg, #ecf5ff 0%, #f0f9ff 100%);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
}

.status-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.status-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.status-success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.status-pending {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.status-rejected {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.status-info {
  flex: 1;
}

.status-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.status-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.form-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
}

.form-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}
</style>