<template>
  <div class="publish-demand-container">
    <div class="page-header">
      <h1 class="page-title">发布需求</h1>
    </div>
    
    <el-card class="form-card">
      <el-form ref="demandFormRef" :model="demandForm" :rules="rules" label-width="120px">
        <el-form-item label="需求标题" prop="title">
          <el-input v-model="demandForm.title" placeholder="请输入需求标题"></el-input>
        </el-form-item>
        
        <el-form-item label="作物类型" prop="cropType">
          <el-select v-model="demandForm.cropType" placeholder="请选择作物类型">
            <el-option label="小麦" value="小麦"></el-option>
            <el-option label="玉米" value="玉米"></el-option>
            <el-option label="水稻" value="水稻"></el-option>
            <el-option label="棉花" value="棉花"></el-option>
            <el-option label="大豆" value="大豆"></el-option>
            <el-option label="蔬菜" value="蔬菜"></el-option>
            <el-option label="水果" value="水果"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="作业类型" prop="workType">
          <el-select v-model="demandForm.workType" placeholder="请选择作业类型">
            <el-option label="喷洒农药" value="喷洒农药"></el-option>
            <el-option label="施肥" value="施肥"></el-option>
            <el-option label="病虫害防治" value="病虫害防治"></el-option>
            <el-option label="脱叶处理" value="脱叶处理"></el-option>
            <el-option label="测绘航拍" value="测绘航拍"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="作业面积(亩)" prop="area">
          <el-input v-model.number="demandForm.area" placeholder="请输入作业面积" type="number"></el-input>
        </el-form-item>
        
        <el-form-item label="作业地点" prop="location">
          <el-input v-model="demandForm.location" placeholder="请输入作业地点"></el-input>
        </el-form-item>
        
        <el-form-item label="期望作业时间" prop="workTime">
          <el-date-picker 
            v-model="demandForm.workTime" 
            type="date" 
            placeholder="选择日期"
          ></el-date-picker>
        </el-form-item>
        
        <el-form-item label="需求描述" prop="description">
          <el-input 
            v-model="demandForm.description" 
            type="textarea" 
            rows="4" 
            placeholder="请描述详细需求..."
          ></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交需求</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'FarmerPublishDemand',
  setup() {
    const demandFormRef = ref(null)
    
    const demandForm = reactive({
      title: '',
      cropType: '',
      workType: '',
      area: '',
      location: '',
      workTime: '',
      description: ''
    })
    
    const rules = {
      title: [
        { required: true, message: '请输入需求标题', trigger: 'blur' },
        { max: 100, message: '标题不能超过100个字符', trigger: 'blur' }
      ],
      cropType: [
        { required: true, message: '请选择作物类型', trigger: 'change' }
      ],
      workType: [
        { required: true, message: '请选择作业类型', trigger: 'change' }
      ],
      area: [
        { required: true, message: '请输入作业面积', trigger: 'blur' },
        { type: 'number', min: 0.1, message: '面积必须大于0', trigger: 'blur' }
      ],
      location: [
        { required: true, message: '请输入作业地点', trigger: 'blur' }
      ],
      workTime: [
        { required: true, message: '请选择作业时间', trigger: 'change' }
      ]
    }
    
    const handleSubmit = async () => {
      try {
        await demandFormRef.value.validate()
        ElMessage.success('需求发布成功')
        handleReset()
      } catch (error) {
        ElMessage.error('请填写完整信息')
      }
    }
    
    const handleReset = () => {
      demandForm.title = ''
      demandForm.cropType = ''
      demandForm.workType = ''
      demandForm.area = ''
      demandForm.location = ''
      demandForm.workTime = ''
      demandForm.description = ''
      demandFormRef.value.clearValidate()
    }
    
    return {
      demandFormRef,
      demandForm,
      rules,
      handleSubmit,
      handleReset
    }
  }
}
</script>

<style scoped>
.publish-demand-container {
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

.form-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
}
</style>