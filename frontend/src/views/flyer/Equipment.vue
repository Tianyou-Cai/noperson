<template>
  <div class="equipment-page">
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
          <button class="nav-btn" @click="router.push('/service-list')">服务市场</button>
          <button class="nav-btn active">租设备</button>
          <button class="nav-btn" @click="router.push('/flyer/dashboard')">我的</button>
        </div>
      </div>
    </nav>

    <!-- 主内容 -->
    <div class="page-content">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-header">
          <h2>设备租赁市场</h2>
          <p>浏览并租赁机主的无人机设备</p>
        </div>
        <div class="filter-row">
          <div class="filter-group">
            <label>设备类型</label>
            <select v-model="filters.type" class="filter-select">
              <option value="">全部类型</option>
              <option value="spray">植保无人机</option>
              <option value="survey">测绘无人机</option>
              <option value="inspection">检测无人机</option>
            </select>
          </div>
          <div class="filter-group">
            <label>价格区间</label>
            <select v-model="filters.priceRange" class="filter-select">
              <option value="">不限</option>
              <option value="0-200">¥200以下</option>
              <option value="200-300">¥200-300</option>
              <option value="300-500">¥300-500</option>
              <option value="500+">¥500以上</option>
            </select>
          </div>
          <div class="filter-group">
            <label>排序方式</label>
            <select v-model="filters.sort" class="filter-select">
              <option value="default">默认排序</option>
              <option value="price-asc">价格从低到高</option>
              <option value="price-desc">价格从高到低</option>
              <option value="rating">评分最高</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 设备列表 -->
      <div class="equipment-list">
        <div class="list-header">
          <span class="result-count">共找到 {{ filteredEquipment.length }} 个设备</span>
        </div>
        
        <div v-if="filteredEquipment.length === 0" class="empty-state">
          <div class="empty-icon">🚁</div>
          <p>暂无符合条件的设备</p>
        </div>

        <div v-else class="equipment-grid">
          <div v-for="equipment in filteredEquipment" :key="equipment.id" class="equipment-card">
            <div class="card-image">
              <img :src="equipment.image" :alt="equipment.name" />
              <div class="card-tags">
                <span v-if="equipment.isCertified" class="tag certified">✓ 认证</span>
                <span v-if="equipment.isHot" class="tag hot">🔥 热门</span>
              </div>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ equipment.name }}</h3>
              <div class="card-specs">
                <span class="spec-item">{{ equipment.model }}</span>
                <span class="spec-item">{{ equipment.type }}</span>
              </div>
              <div class="card-meta">
                <span class="meta-item">📍 {{ equipment.location }}</span>
                <span class="meta-item">🏢 {{ equipment.owner }}</span>
              </div>
              <div class="card-rating">
                <span class="stars">⭐⭐⭐⭐⭐</span>
                <span class="rating-value">{{ equipment.rating }}</span>
                <span class="rating-count">({{ equipment.reviewCount }}条评价)</span>
              </div>
            </div>
            <div class="card-footer">
              <div class="price-info">
                <span class="currency">¥</span>
                <span class="price">{{ equipment.price }}</span>
                <span class="unit">/{{ equipment.unit }}</span>
              </div>
              <div class="card-actions">
                <button class="btn-contact" @click="contactOwner(equipment)">联系机主</button>
                <button class="btn-rent" @click="rentEquipment(equipment)">立即租赁</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <button class="page-btn" :disabled="currentPage === 1">上一页</button>
        <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
        <button class="page-btn" :disabled="currentPage === totalPages">下一页</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'FlyerEquipment',
  setup() {
    const router = useRouter()
    
    const currentPage = ref(1)
    const totalPages = ref(3)
    
    const filters = reactive({
      type: '',
      priceRange: '',
      sort: 'default'
    })
    
    const equipmentList = ref([
      {
        id: 1,
        name: '大疆 T40 植保无人机',
        model: 'DJI T40',
        type: '植保无人机',
        image: 'https://picsum.photos/300/200?random=1',
        price: 280,
        unit: '小时',
        location: '山东济南',
        owner: '丰收获服合作社',
        isCertified: true,
        isHot: true,
        rating: 4.9,
        reviewCount: 128
      },
      {
        id: 2,
        name: '极飞 P80 农业无人机',
        model: 'XAIRCRAFT P80',
        type: '植保无人机',
        image: 'https://picsum.photos/300/200?random=2',
        price: 320,
        unit: '小时',
        location: '河南郑州',
        owner: '惠农植保中心',
        isCertified: true,
        isHot: false,
        rating: 4.8,
        reviewCount: 86
      },
      {
        id: 3,
        name: '大疆 T20P 无人机',
        model: 'DJI T20P',
        type: '植保无人机',
        image: 'https://picsum.photos/300/200?random=3',
        price: 220,
        unit: '小时',
        location: '江苏徐州',
        owner: '天宇农业服务',
        isCertified: false,
        isHot: true,
        rating: 4.7,
        reviewCount: 65
      },
      {
        id: 4,
        name: '大疆 M300 RTK',
        model: 'DJI M300',
        type: '测绘无人机',
        image: 'https://picsum.photos/300/200?random=4',
        price: 450,
        unit: '小时',
        location: '陕西西安',
        owner: '精准测绘公司',
        isCertified: true,
        isHot: false,
        rating: 4.9,
        reviewCount: 42
      },
      {
        id: 5,
        name: '大疆 T30 植保无人机',
        model: 'DJI T30',
        type: '植保无人机',
        image: 'https://picsum.photos/300/200?random=5',
        price: 250,
        unit: '小时',
        location: '安徽合肥',
        owner: '绿翼植保服务队',
        isCertified: true,
        isHot: true,
        rating: 4.8,
        reviewCount: 95
      },
      {
        id: 6,
        name: '极飞 V50 无人机',
        model: 'XAIRCRAFT V50',
        type: '植保无人机',
        image: 'https://picsum.photos/300/200?random=6',
        price: 300,
        unit: '小时',
        location: '浙江杭州',
        owner: '智慧农业合作社',
        isCertified: false,
        isHot: false,
        rating: 4.6,
        reviewCount: 53
      }
    ])
    
    const filteredEquipment = computed(() => {
      return equipmentList.value
    })
    
    const contactOwner = (equipment) => {
      ElMessage.info(`正在联系 ${equipment.owner}...`)
    }
    
    const rentEquipment = (equipment) => {
      ElMessage.success(`已发起对 ${equipment.name} 的租赁请求`)
    }
    
    return {
      router,
      currentPage,
      totalPages,
      filters,
      equipmentList,
      filteredEquipment,
      contactOwner,
      rentEquipment
    }
  }
}
</script>

<style scoped>
.equipment-page {
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
  color: #3b82f6;
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
  background: #3b82f6;
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
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.08);
}

.nav-btn.active {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.15);
  font-weight: 600;
}

.page-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.filter-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.filter-header {
  margin-bottom: 20px;
}

.filter-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.filter-header p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.filter-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.filter-select {
  padding: 10px 14px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
}

.filter-select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.equipment-list {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.list-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.result-count {
  font-size: 14px;
  color: #6b7280;
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
}

.equipment-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.equipment-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.equipment-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.1);
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-tags {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.tag.certified {
  background: #dbeafe;
  color: #1e40af;
}

.tag.hot {
  background: #fef3c7;
  color: #92400e;
}

.card-body {
  padding: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.card-specs {
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

.card-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}

.meta-item {
  font-size: 13px;
  color: #6b7280;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stars {
  color: #fbbf24;
}

.rating-value {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.rating-count {
  font-size: 12px;
  color: #9ca3af;
}

.card-footer {
  padding: 16px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-info {
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 14px;
  color: #ef4444;
  font-weight: 600;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
}

.unit {
  font-size: 13px;
  color: #6b7280;
  margin-left: 4px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.btn-contact,
.btn-rent {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-contact {
  background: #fff;
  border: 1px solid #e5e7eb;
  color: #4b5563;
}

.btn-contact:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.btn-rent {
  background: #3b82f6;
  border: 1px solid #3b82f6;
  color: #fff;
}

.btn-rent:hover {
  background: #2563eb;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
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
  border-color: #3b82f6;
  color: #3b82f6;
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
  .equipment-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .filter-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .navbar-center {
    display: none;
  }
  
  .equipment-grid {
    grid-template-columns: 1fr;
  }
  
  .card-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .card-actions {
    width: 100%;
  }
  
  .btn-contact,
  .btn-rent {
    flex: 1;
  }
}
</style>