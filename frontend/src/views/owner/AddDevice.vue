<template>
  <div class="add-device-page">
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
            <input type="text" placeholder="搜索设备" class="search-input" />
            <button class="search-btn">🔍</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/equipment-list')">设备市场</button>
          <button class="nav-btn active">添加设备</button>
          <button class="nav-btn" @click="router.push('/owner/dashboard')">我的</button>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <div class="page-content">
      <div class="form-container">
        <div class="form-header">
          <h1>添加设备</h1>
          <p>添加您的无人机设备到平台，开始对外租赁</p>
        </div>

        <div class="form-body">
          <!-- 设备基本信息 -->
          <div class="form-section">
            <h3 class="section-title">📋 设备基本信息</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">设备名称 <span class="required">*</span></label>
                <input 
                  type="text" 
                  v-model="formData.name" 
                  class="form-input" 
                  placeholder="请输入设备名称"
                />
              </div>
              <div class="form-group">
                <label class="form-label">设备型号 <span class="required">*</span></label>
                <input 
                  type="text" 
                  v-model="formData.model" 
                  class="form-input" 
                  placeholder="如：DJI T40"
                />
              </div>
              <div class="form-group">
                <label class="form-label">设备类型 <span class="required">*</span></label>
                <select v-model="formData.type" class="form-select">
                  <option value="">请选择设备类型</option>
                  <option value="spray">植保无人机</option>
                  <option value="survey">测绘无人机</option>
                  <option value="inspection">检测无人机</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">品牌 <span class="required">*</span></label>
                <select v-model="formData.brand" class="form-select">
                  <option value="">请选择品牌</option>
                  <option value="dji">大疆 (DJI)</option>
                  <option value="xaircraft">极飞 (XAIRCRAFT)</option>
                  <option value="other">其他</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 租赁信息 -->
          <div class="form-section">
            <h3 class="section-title">💰 租赁信息</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">租赁价格 <span class="required">*</span></label>
                <div class="input-with-unit">
                  <input 
                    type="number" 
                    v-model="formData.price" 
                    class="form-input" 
                    placeholder="请输入价格"
                  />
                  <span class="unit-label">元/小时</span>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">设备状态</label>
                <select v-model="formData.status" class="form-select">
                  <option value="available">可租赁</option>
                  <option value="maintenance">维护中</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 设备信息 -->
          <div class="form-section">
            <h3 class="section-title">📅 设备信息</h3>
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">购买日期</label>
                <input 
                  type="date" 
                  v-model="formData.purchaseDate" 
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label class="form-label">累计使用时长</label>
                <div class="input-with-unit">
                  <input 
                    type="number" 
                    v-model="formData.totalHours" 
                    class="form-input" 
                    placeholder="累计使用时长"
                  />
                  <span class="unit-label">小时</span>
                </div>
              </div>
              <div class="form-group full-width">
                <label class="form-label">设备所在地 <span class="required">*</span></label>
                <input 
                  type="text" 
                  v-model="formData.location" 
                  class="form-input" 
                  placeholder="请输入设备所在地址"
                />
              </div>
            </div>
          </div>

          <!-- 设备描述 -->
          <div class="form-section">
            <h3 class="section-title">📝 设备描述</h3>
            <div class="form-group">
              <label class="form-label">详细描述</label>
              <textarea 
                v-model="formData.description" 
                class="form-textarea" 
                placeholder="请描述设备的详细配置、特点、使用注意事项等"
                rows="4"
              ></textarea>
            </div>
          </div>

          <!-- 设备图片 -->
          <div class="form-section">
            <h3 class="section-title">📷 设备图片 <span class="required">*</span></h3>
            <div class="upload-grid">
              <div class="upload-item main">
                <span class="upload-icon">📷</span>
                <span class="upload-text">封面图片</span>
                <span class="upload-hint">点击上传</span>
              </div>
              <div class="upload-item">
                <span class="upload-icon">📷</span>
                <span class="upload-text">图片 2</span>
                <span class="upload-hint">点击上传</span>
              </div>
              <div class="upload-item">
                <span class="upload-icon">📷</span>
                <span class="upload-text">图片 3</span>
                <span class="upload-hint">点击上传</span>
              </div>
              <div class="upload-item">
                <span class="upload-icon">📷</span>
                <span class="upload-text">图片 4</span>
                <span class="upload-hint">点击上传</span>
              </div>
            </div>
          </div>

          <!-- 认证信息 -->
          <div class="form-section">
            <h3 class="section-title">✓ 认证信息</h3>
            <div class="certification-info">
              <div class="certification-item">
                <span class="cert-icon">📜</span>
                <div class="cert-content">
                  <span class="cert-label">飞行许可证</span>
                  <span class="cert-status">未上传</span>
                </div>
                <button class="cert-btn">上传</button>
              </div>
              <div class="certification-item">
                <span class="cert-icon">🔧</span>
                <div class="cert-content">
                  <span class="cert-label">维护记录</span>
                  <span class="cert-status">未上传</span>
                </div>
                <button class="cert-btn">上传</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-footer">
          <button class="btn-secondary" @click="saveDraft">保存草稿</button>
          <button class="btn-primary" @click="submitDevice">提交审核</button>
        </div>
      </div>

      <!-- 右侧提示 -->
      <div class="form-tips">
        <div class="tips-card">
          <h4>💡 添加提示</h4>
          <ul class="tips-list">
            <li>请确保设备信息真实有效</li>
            <li>上传清晰的设备照片有助于提高曝光率</li>
            <li>合理的定价可以提高租赁成功率</li>
            <li>认证信息完善的设备更受飞手青睐</li>
          </ul>
        </div>
        <div class="tips-card">
          <h4>📋 审核标准</h4>
          <ul class="tips-list">
            <li>设备信息完整、真实</li>
            <li>设备图片清晰、可辨认</li>
            <li>租赁价格合理</li>
            <li>具备相关资质证明</li>
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
  name: 'AddDevice',
  setup() {
    const router = useRouter()
    
    const formData = reactive({
      name: '',
      model: '',
      type: '',
      brand: '',
      price: '',
      status: 'available',
      purchaseDate: '',
      totalHours: '',
      location: '',
      description: ''
    })
    
    const saveDraft = () => {
      ElMessage.success('草稿已保存')
    }
    
    const submitDevice = () => {
      if (!formData.name || !formData.model || !formData.type || !formData.brand) {
        ElMessage.warning('请填写完整的设备基本信息')
        return
      }
      if (!formData.price) {
        ElMessage.warning('请填写租赁价格')
        return
      }
      if (!formData.location) {
        ElMessage.warning('请填写设备所在地')
        return
      }
      
      ElMessage.success('设备已提交审核！')
      router.push('/owner/dashboard')
    }
    
    return {
      router,
      formData,
      saveDraft,
      submitDevice
    }
  }
}
</script>

<style scoped>
.add-device-page {
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
  color: #f59e0b;
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
  background: #f59e0b;
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
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.08);
}

.nav-btn.active {
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.15);
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
  border-color: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
}

.form-textarea {
  resize: vertical;
  font-family: inherit;
}

.input-with-unit {
  display: flex;
  align-items: center;
  gap: 8px;
}

.input-with-unit .form-input {
  flex: 1;
}

.unit-label {
  font-size: 14px;
  color: #6b7280;
  white-space: nowrap;
}

.upload-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.upload-item {
  border: 2px dashed #e5e7eb;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-item:hover {
  border-color: #f59e0b;
  background: rgba(245, 158, 11, 0.02);
}

.upload-item.main {
  border-color: #f59e0b;
  background: rgba(245, 158, 11, 0.02);
}

.upload-icon {
  display: block;
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-text {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 4px;
}

.upload-hint {
  font-size: 12px;
  color: #9ca3af;
}

.certification-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.certification-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.cert-icon {
  font-size: 28px;
}

.cert-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.cert-label {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 4px;
}

.cert-status {
  font-size: 12px;
  color: #9ca3af;
}

.cert-btn {
  padding: 6px 16px;
  background: #f59e0b;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.cert-btn:hover {
  background: #d97706;
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
  border-color: #f59e0b;
  color: #f59e0b;
}

.btn-primary {
  background: #f59e0b;
  border: 1px solid #f59e0b;
  color: #fff;
}

.btn-primary:hover {
  background: #d97706;
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
  
  .upload-grid {
    grid-template-columns: repeat(2, 1fr);
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