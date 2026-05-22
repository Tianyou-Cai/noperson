<template>
  <div class="publish-demand-page">
    <!-- 顶部导航 -->
    <nav class="page-navbar">
      <div class="navbar-content">
        <div class="navbar-left">
          <div class="logo" @click="router.push('/')">
            <span class="logo-icon">🚁</span>
            <span class="logo-text">农翼通商城</span>
          </div>
        </div>
        <div class="navbar-center">
          <div class="search-box">
            <input type="text" placeholder="搜索服务" class="search-input" />
            <button class="search-btn">🔍</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/service-list')">找服务</button>
          <button class="nav-btn active">发布需求</button>
          <button class="nav-btn" @click="router.push('/farmer/dashboard')">我的</button>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <div class="page-content">
      <div class="form-container">
        <div class="form-header">
          <h1>发布作业需求</h1>
          <p>填写您的作业需求信息，平台将为您匹配最合适的服务商</p>
        </div>

        <div class="form-body">
          <!-- 基本信息 -->
          <div class="form-section">
            <h3 class="section-title">📋 基本信息</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">需求标题 <span class="required">*</span></label>
                <input 
                  type="text" 
                  v-model="formData.title" 
                  class="form-input" 
                  placeholder="请输入需求标题，如：小麦喷洒作业"
                />
              </div>
              <div class="form-group">
                <label class="form-label">服务类型 <span class="required">*</span></label>
                <select v-model="formData.serviceType" class="form-select">
                  <option value="">请选择服务类型</option>
                  <option value="spray">农药喷洒</option>
                  <option value="fertilize">施肥作业</option>
                  <option value="disease">病虫害防治</option>
                  <option value="inspection">作物检测</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 地块信息 -->
          <div class="form-section">
            <h3 class="section-title">📍 地块信息</h3>
            <div class="form-grid">
              <div class="form-group full-width">
                <label class="form-label">作业地址 <span class="required">*</span></label>
                <input 
                  type="text" 
                  v-model="formData.address" 
                  class="form-input" 
                  placeholder="请输入详细地址"
                />
              </div>
              <div class="form-group">
                <label class="form-label">作业面积(亩) <span class="required">*</span></label>
                <input 
                  type="number" 
                  v-model="formData.area" 
                  class="form-input" 
                  placeholder="请输入面积"
                  min="1"
                />
              </div>
              <div class="form-group">
                <label class="form-label">作物类型 <span class="required">*</span></label>
                <select v-model="formData.cropType" class="form-select">
                  <option value="">请选择作物类型</option>
                  <option value="wheat">小麦</option>
                  <option value="corn">玉米</option>
                  <option value="rice">水稻</option>
                  <option value="cotton">棉花</option>
                  <option value="fruit">果树</option>
                  <option value="vegetable">蔬菜</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 时间安排 -->
          <div class="form-section">
            <h3 class="section-title">📅 时间安排</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">期望作业日期 <span class="required">*</span></label>
                <input 
                  type="date" 
                  v-model="formData.preferredDate" 
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label class="form-label">期望时间</label>
                <select v-model="formData.preferredTime" class="form-select">
                  <option value="">请选择时间段</option>
                  <option value="morning">上午 (8:00-12:00)</option>
                  <option value="afternoon">下午 (12:00-18:00)</option>
                  <option value="anytime">时间不限</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 详细描述 -->
          <div class="form-section">
            <h3 class="section-title">📝 详细描述</h3>
            <div class="form-group">
              <label class="form-label">需求描述</label>
              <textarea 
                v-model="formData.description" 
                class="form-textarea" 
                placeholder="请详细描述您的作业需求，包括特殊要求、注意事项等"
                rows="4"
              ></textarea>
            </div>
          </div>

          <!-- 预算 -->
          <div class="form-section">
            <h3 class="section-title">💰 预算设置</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">预算金额(元)</label>
                <input 
                  type="number" 
                  v-model="formData.budget" 
                  class="form-input" 
                  placeholder="请输入预算金额"
                />
              </div>
              <div class="form-group">
                <label class="form-label">价格方式</label>
                <select v-model="formData.priceType" class="form-select">
                  <option value="perMu">按亩计价</option>
                  <option value="fixed">一口价</option>
                  <option value="negotiable">价格面议</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 图片上传 -->
          <div class="form-section">
            <h3 class="section-title">📷 田地照片（可选）</h3>
            <div class="upload-area">
              <div class="upload-placeholder">
                <span class="upload-icon">📷</span>
                <span class="upload-text">点击上传照片</span>
                <span class="upload-hint">支持 JPG、PNG 格式，最多 5 张</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-footer">
          <button class="btn-secondary" @click="saveDraft">保存草稿</button>
          <button class="btn-primary" @click="submitDemand">发布需求</button>
        </div>
      </div>

      <!-- 右侧提示 -->
      <div class="form-tips">
        <div class="tips-card">
          <h4>💡 发布提示</h4>
          <ul class="tips-list">
            <li>详细的需求描述能帮助服务商更准确地报价</li>
            <li>作业面积请以亩为单位准确填写</li>
            <li>提前预约可以获得更好的服务</li>
            <li>上传田地照片有助于服务商了解作业环境</li>
          </ul>
        </div>
        <div class="tips-card">
          <h4>🛡️ 服务保障</h4>
          <ul class="tips-list">
            <li>平台担保交易，资金安全有保障</li>
            <li>专业客服团队，随时为您服务</li>
            <li>服务质量跟踪，售后无忧</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'PublishDemand',
  setup() {
    const router = useRouter()
    
    const formData = reactive({
      title: '',
      serviceType: '',
      address: '',
      area: '',
      cropType: '',
      preferredDate: '',
      preferredTime: '',
      description: '',
      budget: '',
      priceType: 'perMu'
    })
    
    const saveDraft = () => {
      ElMessage.success('草稿已保存')
    }
    
    const submitDemand = () => {
      if (!formData.title) {
        ElMessage.warning('请填写需求标题')
        return
      }
      if (!formData.serviceType) {
        ElMessage.warning('请选择服务类型')
        return
      }
      if (!formData.address || !formData.area || !formData.cropType) {
        ElMessage.warning('请填写完整的地块信息')
        return
      }
      if (!formData.preferredDate) {
        ElMessage.warning('请选择期望作业日期')
        return
      }
      
      ElMessage.success('需求发布成功！')
      router.push('/farmer/dashboard')
    }
    
    return {
      router,
      formData,
      saveDraft,
      submitDemand
    }
  }
}
</script>

<style scoped>
.publish-demand-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #10b981;
}

.navbar-center {
  flex: 1;
  max-width: 400px;
  margin: 0 30px;
}

.search-box {
  display: flex;
  background: #f3f4f6;
  border-radius: 20px;
  overflow: hidden;
}

.search-input {
  flex: 1;
  border: none;
  padding: 8px 14px;
  font-size: 13px;
  background: transparent;
  outline: none;
}

.search-btn {
  padding: 8px 16px;
  background: #10b981;
  border: none;
  font-size: 14px;
  cursor: pointer;
}

.navbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.nav-btn {
  padding: 6px 14px;
  background: transparent;
  border: none;
  font-size: 13px;
  color: #4b5563;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.3s;
}

.nav-btn:hover {
  color: #10b981;
  background: rgba(16, 185, 129, 0.08);
}

.nav-btn.active {
  color: #10b981;
  background: rgba(16, 185, 129, 0.15);
  font-weight: 600;
}

.page-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.form-container {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.form-header {
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e5e7eb;
}

.form-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.form-header p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-section {
  padding: 20px;
  background: #f9fafb;
  border-radius: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.required {
  color: #ef4444;
}

.form-input,
.form-select,
.form-textarea {
  padding: 10px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
  outline: none;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  border-color: #10b981;
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.1);
}

.form-textarea {
  resize: vertical;
  font-family: inherit;
}

.upload-area {
  border: 2px dashed #e5e7eb;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #10b981;
  background: rgba(16, 185, 129, 0.02);
}

.upload-icon {
  display: block;
  font-size: 48px;
  margin-bottom: 12px;
}

.upload-text {
  display: block;
  font-size: 16px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 13px;
  color: #9ca3af;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-secondary,
.btn-primary {
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-secondary {
  background: #fff;
  border: 1px solid #e5e7eb;
  color: #4b5563;
}

.btn-secondary:hover {
  border-color: #10b981;
  color: #10b981;
}

.btn-primary {
  background: #10b981;
  border: 1px solid #10b981;
  color: #fff;
}

.btn-primary:hover {
  background: #059669;
}

.form-tips {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.tips-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.tips-card h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  font-size: 13px;
  color: #6b7280;
  padding: 8px 0;
  border-bottom: 1px solid #f3f4f6;
}

.tips-list li:last-child {
  border-bottom: none;
}

@media (max-width: 1024px) {
  .page-content {
    grid-template-columns: 1fr;
  }
  
  .form-tips {
    order: -1;
  }
}

@media (max-width: 768px) {
  .navbar-center {
    display: none;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .form-container {
    padding: 20px;
  }
  
  .form-footer {
    flex-direction: column;
  }
  
  .btn-secondary,
  .btn-primary {
    width: 100%;
  }
}
</style>