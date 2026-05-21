<template>
  <div class="qualification">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>机主资质管理</span>
        </div>
      </template>
      
      <!-- 状态提示 -->
      <el-alert
        v-if="auditStatus === 0"
        title="您的资质正在审核中，请耐心等待"
        type="info"
        show-icon
      />
      
      <el-alert
        v-else-if="auditStatus === 1"
        title="恭喜，您的资质已通过审核！"
        type="success"
        show-icon
      />
      
      <el-alert
        v-else-if="auditStatus === 2"
        title="审核未通过: {{ rejectReason }}"
        type="error"
        show-icon
      />
      
      <!-- 已提交资质且非编辑模式时显示资质信息卡片 -->
      <div v-if="hasSubmittedQualification && !isEditing" class="qualification-info-card">
        <el-descriptions border>
          <el-descriptions-item label="执照类型">
            {{ qualificationForm.licenseType === 'INDIVIDUAL' ? '个人执照' : '企业执照' }}
          </el-descriptions-item>
          <el-descriptions-item label="执照编号">{{ qualificationForm.licenseNumber || '未提供' }}</el-descriptions-item>
          <el-descriptions-item label="资质照片" :span="2">
            <template v-if="qualificationForm.avatar">
              <el-image
                :src="qualificationForm.avatar"
                style="width: 120px; height: 120px; margin-right: 10px"
                :preview-src-list="[qualificationForm.avatar]"
              />
            </template>
            <span v-else>未上传</span>
          </el-descriptions-item>
          <el-descriptions-item label="常用作业区域">{{ qualificationForm.commonArea || '未提供' }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="action-buttons">
          <el-button 
            type="primary" 
            @click="startEdit"
          >
            编辑资质
          </el-button>
        </div>
      </div>
      
      <!-- 未提交资质或编辑模式时显示表单 -->
      <el-form 
        v-else-if="!hasSubmittedQualification || isEditing"
        :model="qualificationForm" 
        :rules="rules" 
        ref="qualificationFormRef"
        label-width="120px"
        class="qualification-form"
      >
        <el-form-item label="执照类型" prop="licenseType">
          <el-radio-group v-model="qualificationForm.licenseType">
            <el-radio label="INDIVIDUAL">个人执照</el-radio>
            <el-radio label="ENTERPRISE">企业执照</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="执照编号" prop="licenseNumber">
          <el-input 
            v-model="qualificationForm.licenseNumber" 
            placeholder="请输入执照编号"
          />
        </el-form-item>

        <el-form-item label="资质照片" prop="avatar">
          <el-upload
            v-model:file-list="fileList"
            class="upload-demo"
            action="/file/upload"
            :headers="uploadHeaders"
            :limit="1"
            :on-success="handleLicenseFileSuccess"
            :before-remove="handleBeforeRemove"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
            <template #file="{ file }">
              <img :src="file.url" class="el-upload-list__item-thumbnail" />
              <span class="el-upload-list__item-actions">
                <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                  <el-icon><ZoomIn /></el-icon>
                </span>
                <span class="el-upload-list__item-delete" @click="handleLicenseFileRemove(file)">
                  <el-icon><Delete /></el-icon>
                </span>
              </span>
            </template>
            <template #tip>
              <div class="el-upload__tip">
                请上传资质照片，支持jpg、jpeg、png格式
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="常用作业区域" prop="commonArea">
          <el-input 
            v-model="qualificationForm.commonArea" 
            placeholder="请输入常用作业区域"
          />
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleSubmit"
            :loading="loading"
          >
            {{ isEditing ? '重新提交审核' : '提交资质资料' }}
          </el-button>
          <el-button @click="handleCancelEdit" v-if="isEditing">取消编辑</el-button>
          <el-button @click="handleReset" v-else>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible">
      <img width="100%" :src="dialogImageUrl" alt="preview" />
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import axios from '../utils/axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ZoomIn, Delete } from '@element-plus/icons-vue'

export default {
  name: 'Qualification',
  components: {
    Plus
  },
  setup() {
    const qualificationFormRef = ref(null)
    const loading = ref(false)
    const auditStatus = ref(0)
    const rejectReason = ref('')
    const fileList = ref([])
    const isEditing = ref(false)
    const hasSubmittedQualification = ref(false)
    const imagePreviewVisible = ref(false)
    const dialogImageUrl = ref('')
    
    // 上传请求头
    const uploadHeaders = computed(() => {
      const token = localStorage.getItem('token')
      return token ? {
        'Authorization': `Bearer ${token}`
      } : {}
    })
    
    const qualificationForm = reactive({
      licenseType: 'INDIVIDUAL',
      licenseNumber: '',
      avatar: '',
      commonArea: ''
    })
    
    const rules = {
      licenseType: [
        { required: true, message: '请选择执照类型', trigger: 'change' }
      ],
      licenseNumber: [
        { required: true, message: '请输入执照编号', trigger: 'blur' },
        { min: 5, max: 50, message: '执照编号长度在5-50个字符', trigger: 'blur' }
      ],
      avatar: [
        { required: true, message: '请上传资质照片', trigger: 'change' }
      ],
      commonArea: [
        { required: true, message: '请输入常用作业区域', trigger: 'blur' },
        { min: 2, max: 100, message: '常用作业区域长度在2-100个字符', trigger: 'blur' }
      ]
    }
    
    // 上传前检查
    const beforeUpload = (file) => {
      const isImage = file.type.indexOf('image/') === 0
      const isLt2M = file.size / 1024 / 1024 < 2
      
      if (!isImage) {
        ElMessage.error('只支持图片格式!')
        return false
      }
      if (!isLt2M) {
        ElMessage.error('图片大小不能超过2MB!')
        return false
      }
      
      return true
    }
    
    // 处理资质照片上传成功
    const handleLicenseFileSuccess = (response, uploadFile) => {
      if (response.code === 200) {
        qualificationForm.avatar = response.data
        // 更新文件列表，替换旧图片
        fileList.value = [{
          name: uploadFile.name,
          url: response.data
        }]
        ElMessage.success('上传成功')
      } else {
        ElMessage.error('上传失败，请重试')
      }
    }
    
    // 处理资质照片删除
    const handleLicenseFileRemove = (file) => {
      qualificationForm.avatar = ''
    }
    
    // 处理文件移除前确认
    const handleBeforeRemove = (file, fileList) => {
      return ElMessageBox.confirm(`确定移除 ${file.name}？`)
    }
    
    // 处理图片预览
    const handlePictureCardPreview = (file) => {
      dialogImageUrl.value = file.url
      imagePreviewVisible.value = true
    }
    
    // 提交资质资料
    const handleSubmit = async () => {
      // 验证表单
      await qualificationFormRef.value.validate()
      
      loading.value = true
      
      try {
        // 检查是否已经通过文件上传保存了资质信息
        // 如果已经上传了文件，直接使用已有的数据提交
        if (qualificationForm.avatar) {
          await axios.post('/owner/qualification/submit', qualificationForm)
        } else {
          // 如果没有上传文件，提示用户必须上传
          ElMessage.warning('请上传资质照片')
          return
        }
        
        ElMessage.success('资质资料提交成功，请等待审核')
        auditStatus.value = 0
        isEditing.value = false
        hasSubmittedQualification.value = true
        // 重新获取更新后的详情
        fetchOwnerDetail()
      } catch (error) {
        console.error('提交资质失败:', error)
        ElMessage.error('提交资质失败，请重试')
      } finally {
        loading.value = false
      }
    }
    
    // 开始编辑资质
    const startEdit = () => {
      isEditing.value = true
      // 复制当前数据以便取消编辑时恢复
      originalQualificationData.value = JSON.parse(JSON.stringify(qualificationForm))
      ElMessage.info('现在可以编辑您的资质信息，修改后需要重新提交审核')
    }
    
    // 取消编辑
    const handleCancelEdit = () => {
      isEditing.value = false
      // 恢复原始数据
      Object.assign(qualificationForm, originalQualificationData.value)
      // 重新初始化fileList
      fileList.value = []
      if (qualificationForm.avatar) {
        fileList.value.push({ url: qualificationForm.avatar })
      }
    }
    
    // 保存原始资质数据用于取消编辑时恢复
    const originalQualificationData = ref({})
    
    // 重置表单
    const handleReset = () => {
      qualificationFormRef.value.resetFields()
      fileList.value = []
      qualificationForm.avatar = ''
    }
    
    // 获取机主详情
    const fetchOwnerDetail = async () => {
      try {
        // 直接从后端API获取机主详细信息
        const response = await axios.get('/owner/detail')
        // 检查响应结构，可能需要从data字段获取
        let ownerInfo = response.data.data || response.data
        
        console.log('响应原始数据:', response)
        console.log('原始机主信息:', ownerInfo)
        
        // 特别处理：手动创建一个新对象，确保字段名正确映射
        // 从数据库查询结果可知，字段名为license_number
        ownerInfo = {
          ...ownerInfo,
          // 确保license_number字段存在，复制可能存在的其他字段名
          license_number: ownerInfo.license_number || ownerInfo.licenseNumber || ownerInfo.licenseNumberMasked || ''
        }
        
        console.log('处理后的机主信息:', ownerInfo)
        
        // 设置审核状态和拒绝原因
        auditStatus.value = ownerInfo.auditStatus || 0
        rejectReason.value = ownerInfo.rejectReason || ''
        
        // 严格按照数据库字段名获取执照编号
        // 数据库中字段为license_number，确保正确映射
        const licenseNumber = ownerInfo.license_number || ownerInfo.licenseNumber || ownerInfo.licenseNumberMasked || ''
        console.log('最终使用的执照编号:', licenseNumber)
        
        // 优化判断条件：只要审核通过且有数据，就认为已提交资质
        hasSubmittedQualification.value = auditStatus.value === 1 || 
                                          (ownerInfo.licenseType || licenseNumber || ownerInfo.avatar || ownerInfo.commonArea)
        
        console.log('是否已提交资质:', hasSubmittedQualification.value)
        
        // 填充表单数据
        qualificationForm.licenseType = ownerInfo.licenseType || 'INDIVIDUAL'
        qualificationForm.licenseNumber = licenseNumber
        qualificationForm.avatar = ownerInfo.avatar || ''
        qualificationForm.commonArea = ownerInfo.commonArea || ''
        
        // 处理资质照片
        fileList.value = qualificationForm.avatar ? [{ url: qualificationForm.avatar }] : []
        
        console.log('表单数据填充完成:', qualificationForm)
      } catch (error) {
        console.error('获取机主详情失败:', error)
      }
    }
    
    onMounted(() => {
      fetchOwnerDetail()
    })
    
    return {
      qualificationFormRef,
      qualificationForm,
      rules,
      loading,
      auditStatus,
      rejectReason,
      fileList,
      isEditing,
      hasSubmittedQualification,
      beforeUpload,
      uploadHeaders,
      handleLicenseFileSuccess,
      handleLicenseFileRemove,
      handleBeforeRemove,
      handlePictureCardPreview,
      imagePreviewVisible,
      dialogImageUrl,
      handleSubmit,
      handleReset,
      startEdit,
      handleCancelEdit
    }
  }
}
</script>

<style scoped>
.qualification {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.qualification-form {
  margin-top: 20px;
}

.qualification-info-card {
  margin-top: 20px;
}

.action-buttons {
  margin-top: 20px;
  text-align: right;
}

.el-upload--picture-card {
  width: 120px;
  height: 120px;
}

.el-upload-list--picture-card .el-upload-list__item {
  width: 120px;
  height: 120px;
}

.el-descriptions-item__content {
  word-break: break-all;
}
</style>