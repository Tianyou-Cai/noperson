<template>
  <div class="devices-page">
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
            <input type="text" placeholder="搜索设备名称、型号" class="search-input" />
            <button class="search-btn">🔍</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/equipment-list')">设备市场</button>
          <button class="nav-btn active">我的设备</button>
          <button class="nav-btn" @click="router.push('/profile')">
            <span class="avatar">👤</span>
          </button>
        </div>
      </div>
    </nav>

    <!-- 主内容区域 -->
    <div class="page-content">
      <!-- 页面标题和操作 -->
      <div class="page-header">
        <div class="header-info">
          <h1>我的设备</h1>
          <p>管理您的无人机设备</p>
        </div>
        <button class="add-btn" @click="goToAddDevice">
          <span class="add-icon">➕</span> 添加设备
        </button>
      </div>

      <!-- 设备状态筛选 -->
      <div class="filter-section">
        <div class="filter-tabs">
          <button 
            v-for="tab in tabs" 
            :key="tab.key"
            :class="['filter-tab', { active: activeTab === tab.key }]"
            @click="activeTab = tab.key"
          >
            <span class="tab-icon">{{ tab.icon }}</span>
            <span class="tab-label">{{ tab.label }}</span>
            <span v-if="tab.count" class="tab-count">{{ tab.count }}</span>
          </button>
        </div>
      </div>

      <!-- 设备列表 -->
      <div class="devices-container">
        <div v-if="filteredDevices.length === 0" class="empty-state">
          <div class="empty-icon">🚁</div>
          <p>暂无设备</p>
          <button class="empty-btn" @click="goToAddDevice">立即添加设备</button>
        </div>
        
        <div v-else class="devices-grid">
          <div v-for="device in filteredDevices" :key="device.id" class="device-card">
            <div class="device-image-wrapper">
              <img :src="device.image" class="device-image" />
              <div v-if="device.status === 'available'" class="status-badge available">可租赁</div>
              <div v-else class="status-badge busy">使用中</div>
              <div v-if="device.isCertified" class="certified-badge">✓ 认证</div>
            </div>
            <div class="device-info">
              <h4 class="device-name">{{ device.name }}</h4>
              <div class="device-specs">
                <span class="spec-item">{{ device.type }}</span>
                <span class="spec-item">{{ device.model }}</span>
              </div>
              <div class="device-meta">
                <span class="meta-item">⏱️ 已使用 {{ device.hours }} 小时</span>
                <span class="meta-item">📅 {{ device.purchaseDate }}</span>
              </div>
              <div class="device-footer">
                <div class="device-price">
                  <span class="currency">¥</span>
                  <span class="price">{{ device.price }}</span>
                  <span class="unit">/{{ device.unit }}</span>
                </div>
                <div class="device-actions">
                  <button class="action-btn" @click="goToDeviceDetail(device.id)">详情</button>
                  <button class="action-btn primary" @click="goToEditDevice(device.id)">编辑</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-section">
        <button class="page-btn" :disabled="currentPage === 1">上一页</button>
        <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
        <button class="page-btn" :disabled="currentPage === totalPages">下一页</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'OwnerDevices',
  setup() {
    const router = useRouter()
    
    const activeTab = ref('all')
    const currentPage = ref(1)
    const totalPages = ref(2)
    
    const tabs = ref([
      { key: 'all', label: '全部设备', icon: '📦', count: 8 },
      { key: 'available', label: '可租赁', icon: '✅', count: 5 },
      { key: 'busy', label: '使用中', icon: '🚁', count: 3 }
    ])
    
    const devices = ref([
      { 
        id: 1, 
        name: '大疆 T40 植保无人机', 
        type: '植保无人机',
        model: 'DJI T40',
        image: 'https://picsum.photos/200/150?random=1',
        price: 280,
        unit: '小时',
        status: 'available',
        isCertified: true,
        hours: 120,
        purchaseDate: '2023-05-15'
      },
      { 
        id: 2, 
        name: '极飞 P80 农业无人机', 
        type: '植保无人机',
        model: 'XAIRCRAFT P80',
        image: 'https://picsum.photos/200/150?random=2',
        price: 320,
        unit: '小时',
        status: 'busy',
        isCertified: true,
        hours: 85,
        purchaseDate: '2023-08-20'
      },
      { 
        id: 3, 
        name: '大疆 T20P 无人机', 
        type: '植保无人机',
        model: 'DJI T20P',
        image: 'https://picsum.photos/200/150?random=3',
        price: 220,
        unit: '小时',
        status: 'available',
        isCertified: false,
        hours: 200,
        purchaseDate: '2022-11-10'
      },
      { 
        id: 4, 
        name: '大疆 M300 RTK', 
        type: '测绘无人机',
        model: 'DJI M300',
        image: 'https://picsum.photos/200/150?random=4',
        price: 450,
        unit: '小时',
        status: 'busy',
        isCertified: true,
        hours: 60,
        purchaseDate: '2023-03-01'
      },
      { 
        id: 5, 
        name: '大疆 T30 植保无人机', 
        type: '植保无人机',
        model: 'DJI T30',
        image: 'https://picsum.photos/200/150?random=5',
        price: 250,
        unit: '小时',
        status: 'available',
        isCertified: true,
        hours: 150,
        purchaseDate: '2023-01-15'
      },
      { 
        id: 6, 
        name: '极飞 V50 无人机', 
        type: '植保无人机',
        model: 'XAIRCRAFT V50',
        image: 'https://picsum.photos/200/150?random=6',
        price: 300,
        unit: '小时',
        status: 'available',
        isCertified: false,
        hours: 95,
        purchaseDate: '2023-06-20'
      }
    ])
    
    const filteredDevices = computed(() => {
      if (activeTab.value === 'all') {
        return devices.value
      }
      return devices.value.filter(device => device.status === activeTab.value)
    })
    
    const goToAddDevice = () => {
      router.push('/owner/devices/add')
    }
    
    const goToDeviceDetail = (deviceId) => {
      router.push(`/owner/device/${deviceId}`)
    }
    
    const goToEditDevice = (deviceId) => {
      router.push(`/owner/device/${deviceId}/edit`)
    }
    
    onMounted(() => {
      console.log('Owner devices page mounted')
    })
    
    return {
      activeTab,
      currentPage,
      totalPages,
      tabs,
      devices,
      filteredDevices,
      goToAddDevice,
      goToDeviceDetail,
      goToEditDevice
    }
  }
}
</script>

<style scoped>
.devices-page {
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-left {
  display: flex;
  align-items: center;
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

.avatar {
  font-size: 18px;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-info h1 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.header-info p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f59e0b;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.add-btn:hover {
  background: #d97706;
}

.add-icon {
  font-size: 16px;
}

.filter-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.filter-tabs {
  display: flex;
  gap: 12px;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f3f4f6;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tab:hover {
  background: #e5e7eb;
}

.filter-tab.active {
  background: #f59e0b;
  color: #fff;
}

.tab-icon {
  font-size: 16px;
}

.tab-label {
  font-weight: 500;
}

.tab-count {
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  font-size: 12px;
}

.devices-container {
  margin-bottom: 30px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  color: #6b7280;
  margin-bottom: 20px;
}

.empty-btn {
  padding: 10px 24px;
  background: #f59e0b;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.empty-btn:hover {
  background: #d97706;
}

.devices-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.device-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.device-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.device-image-wrapper {
  position: relative;
}

.device-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.available {
  background: #dcfce7;
  color: #166534;
}

.status-badge.busy {
  background: #fef3c7;
  color: #92400e;
}

.certified-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 10px;
  background: #dbeafe;
  color: #1e40af;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.device-info {
  padding: 16px;
}

.device-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.device-specs {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.spec-item {
  padding: 4px 10px;
  background: #f3f4f6;
  border-radius: 4px;
  font-size: 12px;
  color: #6b7280;
}

.device-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.meta-item {
  font-size: 12px;
  color: #9ca3af;
}

.device-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.device-price {
  display: flex;
  align-items: baseline;
}

.device-price .currency {
  font-size: 14px;
  color: #ef4444;
  font-weight: 600;
}

.device-price .price {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
}

.device-price .unit {
  font-size: 13px;
  color: #6b7280;
  margin-left: 4px;
}

.device-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
  color: #4b5563;
}

.action-btn:hover {
  border-color: #f59e0b;
  color: #f59e0b;
}

.action-btn.primary {
  background: #f59e0b;
  border-color: #f59e0b;
  color: #fff;
}

.action-btn.primary:hover {
  background: #d97706;
}

.pagination-section {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.page-btn {
  padding: 8px 20px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  background: #fff;
  color: #4b5563;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  border-color: #f59e0b;
  color: #f59e0b;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #6b7280;
}

@media (max-width: 1024px) {
  .devices-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .navbar-center {
    display: none;
  }
  
  .navbar-right {
    gap: 8px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .devices-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-tabs {
    flex-wrap: wrap;
  }
}
</style>