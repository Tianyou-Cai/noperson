<template>
  <div class="service-list-page">
    <!-- 顶部导航栏 -->
    <nav class="page-navbar">
      <div class="navbar-content">
        <div class="navbar-left">
          <div class="logo" @click="router.push('/')">
            <span class="logo-icon">🚁</span>
            <span class="logo-text">农翼通</span>
          </div>
        </div>
        <div class="navbar-center">
          <div class="search-box">
            <input type="text" v-model="searchKeyword" class="search-input" placeholder="搜索服务" @keyup.enter="handleSearch" />
            <button class="search-btn" @click="handleSearch">搜索</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/')">首页</button>
          <button class="nav-btn active">找服务</button>
          <button class="nav-btn" @click="router.push('/equipment-list')">租设备</button>
        </div>
      </div>
    </nav>

    <div class="main-content">
      <!-- 左侧筛选栏 -->
      <aside class="filter-sidebar">
        <h3 class="filter-title">筛选条件</h3>
        
        <!-- 服务类型 -->
        <div class="filter-section">
          <h4 class="filter-label">服务类型</h4>
          <div class="filter-options">
            <label 
              v-for="type in serviceTypes" 
              :key="type.id"
              :class="['filter-option', { active: filters.type === type.id }]"
            >
              <input 
                type="radio" 
                :value="type.id" 
                v-model="filters.type"
                @change="handleFilterChange"
              />
              <span>{{ type.name }}</span>
            </label>
          </div>
        </div>

        <!-- 价格范围 -->
        <div class="filter-section">
          <h4 class="filter-label">价格范围</h4>
          <div class="filter-options">
            <label 
              v-for="range in priceRanges" 
              :key="range.id"
              :class="['filter-option', { active: filters.priceRange === range.id }]"
            >
              <input 
                type="radio" 
                :value="range.id" 
                v-model="filters.priceRange"
                @change="handleFilterChange"
              />
              <span>{{ range.label }}</span>
            </label>
          </div>
          <div class="price-inputs">
            <input 
              type="number" 
              v-model="filters.priceMin" 
              placeholder="最低价"
              class="price-input"
              @change="handleFilterChange"
            />
            <span>-</span>
            <input 
              type="number" 
              v-model="filters.priceMax" 
              placeholder="最高价"
              class="price-input"
              @change="handleFilterChange"
            />
          </div>
        </div>

        <!-- 服务地区 -->
        <div class="filter-section">
          <h4 class="filter-label">服务地区</h4>
          <div class="filter-options">
            <label 
              v-for="area in serviceAreas" 
              :key="area.id"
              :class="['filter-option', { active: filters.area === area.id }]"
            >
              <input 
                type="radio" 
                :value="area.id" 
                v-model="filters.area"
                @change="handleFilterChange"
              />
              <span>{{ area.name }}</span>
            </label>
          </div>
        </div>

        <!-- 飞手类型 -->
        <div class="filter-section">
          <h4 class="filter-label">服务提供方</h4>
          <div class="filter-options">
            <label 
              v-for="provider in providerTypes" 
              :key="provider.id"
              :class="['filter-option', { active: filters.providerType === provider.id }]"
            >
              <input 
                type="radio" 
                :value="provider.id" 
                v-model="filters.providerType"
                @change="handleFilterChange"
              />
              <span>{{ provider.name }}</span>
            </label>
          </div>
        </div>

        <!-- 加载作业类型 -->
        <div class="filter-section">
          <h4 class="filter-label">适用作物</h4>
          <div class="filter-options">
            <label 
              v-for="crop in cropTypes" 
              :key="crop.id"
              :class="['filter-option', { active: filters.cropType === crop.id }]"
            >
              <input 
                type="radio" 
                :value="crop.id" 
                v-model="filters.cropType"
                @change="handleFilterChange"
              />
              <span>{{ crop.name }}</span>
            </label>
          </div>
        </div>

        <div class="filter-actions">
          <button class="reset-btn" @click="handleReset">重置</button>
          <button class="confirm-btn" @click="handleFilterChange">确定</button>
        </div>
      </aside>

      <!-- 右侧内容区 -->
      <div class="content-area">
        <!-- 排序和工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <span class="result-count">共找到 <strong>{{ total }}</strong> 个服务</span>
          </div>
          <div class="toolbar-right">
            <span class="sort-label">排序：</span>
            <button 
              v-for="sort in sortOptions" 
              :key="sort.id"
              :class="['sort-btn', { active: currentSort === sort.id }]"
              @click="handleSortChange(sort.id)"
            >
              {{ sort.name }}
            </button>
          </div>
        </div>

        <!-- 服务列表 -->
        <div class="services-grid">
          <div 
            v-for="service in serviceList" 
            :key="service.id"
            class="service-card"
            @click="goToDetail(service.id)"
          >
            <div class="service-image">
              <img :src="service.image" :alt="service.title" />
              <div class="service-tags">
                <span v-if="service.isCertified" class="tag certified">认证</span>
                <span v-if="service.isNew" class="tag new">新品</span>
                <span v-if="service.isHot" class="tag hot">热门</span>
              </div>
            </div>
            <div class="service-info">
              <h4 class="service-title">{{ service.title }}</h4>
              <div class="service-desc">{{ service.description }}</div>
              <div class="service-provider">
                <span class="provider-name">{{ service.providerName }}</span>
                <span :class="['provider-type', service.providerType]">
                  {{ service.providerType === 'flyer' ? '飞手' : '机主' }}
                </span>
              </div>
              <div class="service-meta">
                <span class="location">📍 {{ service.location }}</span>
                <span class="sales">已售 {{ formatNumber(service.sales) }}</span>
              </div>
              <div class="service-rating">
                <span class="rating-score">{{ service.rating }}</span>
                <div class="rating-stars">
                  <span v-for="i in 5" :key="i" :class="['star', { filled: i <= Math.floor(service.rating) }]">★</span>
                </div>
                <span class="reviews">({{ service.reviews }}条评价)</span>
              </div>
              <div class="service-footer">
                <div class="service-price">
                  <span class="currency">¥</span>
                  <span class="price">{{ service.price }}</span>
                  <span class="unit">/{{ service.unit }}</span>
                </div>
                <button class="contact-btn">立即咨询</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 30, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'ServiceList',
  setup() {
    const router = useRouter()
    const searchKeyword = ref('')
    const total = ref(156)
    const currentSort = ref('default')

    const filters = ref({
      type: '',
      priceRange: '',
      priceMin: '',
      priceMax: '',
      area: '',
      providerType: '',
      cropType: ''
    })

    const serviceTypes = ref([
      { id: '', name: '全部' },
      { id: 'spray', name: '农药喷洒' },
      { id: 'seed', name: '播种服务' },
      { id: 'fertilize', name: '施肥作业' },
      { id: 'monitor', name: '农情监测' },
      { id: 'weed', name: '除草服务' },
      { id: 'manage', name: '植保托管' }
    ])

    const priceRanges = ref([
      { id: '', name: '全部' },
      { id: '1', name: '15 元以下' },
      { id: '2', name: '15-25 元' },
      { id: '3', name: '25-35 元' },
      { id: '4', name: '35 元以上' }
    ])

    const serviceAreas = ref([
      { id: '', name: '全国' },
      { id: 'shandong', name: '山东省' },
      { id: 'henan', name: '河南省' },
      { id: 'jiangsu', name: '江苏省' },
      { id: 'anhui', name: '安徽省' },
      { id: 'hebei', name: '河北省' },
      { id: 'shaanxi', name: '陕西省' }
    ])

    const providerTypes = ref([
      { id: '', name: '全部' },
      { id: 'flyer', name: '飞手' },
      { id: 'owner', name: '机主' }
    ])

    const cropTypes = ref([
      { id: '', name: '全部' },
      { id: 'wheat', name: '小麦' },
      { id: 'rice', name: '水稻' },
      { id: 'corn', name: '玉米' },
      { id: 'cotton', name: '棉花' },
      { id: 'fruit', name: '果树' },
      { id: 'vegetable', name: '蔬菜' }
    ])

    const sortOptions = ref([
      { id: 'default', name: '综合' },
      { id: 'sales', name: '销量' },
      { id: 'price_asc', name: '价格↑' },
      { id: 'price_desc', name: '价格↓' },
      { id: 'rating', name: '好评' }
    ])

    const pagination = ref({
      currentPage: 1,
      pageSize: 20
    })

    const serviceList = ref([
      {
        id: 1,
        title: '专业小麦农药喷洒服务 - 高效均匀 快速响单',
        description: '采用大疆 T30 无人机，专业飞手团队，日均作业 500 亩+',
        image: 'https://picsum.photos/400/300?random=10',
        price: 15,
        unit: '亩',
        sales: 2340,
        rating: 4.9,
        reviews: 856,
        providerName: '绿翼植保服务队',
        providerType: 'flyer',
        location: '山东济南',
        isCertified: true,
        isHot: true,
        isNew: false
      },
      {
        id: 2,
        title: '水稻播种一条龙服务 - 精准量化 省时省力',
        description: '机主直营，设备先进，价格优惠，包您满意',
        image: 'https://picsum.photos/400/300?random=11',
        price: 25,
        unit: '亩',
        sales: 1890,
        rating: 4.8,
        reviews: 623,
        providerName: '丰收获服合作社',
        providerType: 'owner',
        location: '江苏徐州',
        isCertified: true,
        isHot: true,
        isNew: false
      },
      {
        id: 3,
        title: '玉米地施肥作业 - 科学配方 增产增收',
        description: '专业团队，快速响应，服务周到',
        image: 'https://picsum.photos/400/300?random=12',
        price: 18,
        unit: '亩',
        sales: 1567,
        rating: 4.7,
        reviews: 445,
        providerName: '天宇农业服务',
        providerType: 'flyer',
        location: '河南郑州',
        isCertified: true,
        isHot: false,
        isNew: false
      },
      {
        id: 4,
        title: '果树病虫害防治 - 靶向精准 绿色防控',
        description: '果树专家，好评如潮，经验丰富',
        image: 'https://picsum.photos/400/300?random=13',
        price: 35,
        unit: '亩',
        sales: 1234,
        rating: 4.9,
        reviews: 512,
        providerName: '惠农植保中心',
        providerType: 'owner',
        location: '陕西西安',
        isCertified: true,
        isHot: true,
        isNew: false
      },
      {
        id: 5,
        title: '大田作物综合植保 - 一站式解决方案',
        description: '性价比高，覆盖面积广，服务靠谱',
        image: 'https://picsum.photos/400/300?random=14',
        price: 20,
        unit: '亩',
        sales: 987,
        rating: 4.6,
        reviews: 328,
        providerName: '田野飞防大队',
        providerType: 'flyer',
        location: '安徽合肥',
        isCertified: false,
        isHot: false,
        isNew: true
      },
      {
        id: 6,
        title: '蔬菜基地精准施药 - 有机认证 安全高效',
        description: '蔬菜专精，绿色防控，品质保证',
        image: 'https://picsum.photos/400/300?random=15',
        price: 28,
        unit: '亩',
        sales: 876,
        rating: 4.8,
        reviews: 267,
        providerName: '绿源农业合作社',
        providerType: 'owner',
        location: '山东潍坊',
        isCertified: true,
        isHot: false,
        isNew: false
      },
      {
        id: 7,
        title: '棉花脱叶剂喷洒 - 地域专家 经验丰富',
        description: '新疆本地团队，熟悉棉花作业流程',
        image: 'https://picsum.photos/400/300?random=16',
        price: 22,
        unit: '亩',
        sales: 765,
        rating: 4.7,
        reviews: 198,
        providerName: '新疆飞防总站',
        providerType: 'flyer',
        location: '新疆乌鲁木齐',
        isCertified: true,
        isHot: false,
        isNew: false
      },
      {
        id: 8,
        title: '茶园病虫害统防统治 - 茶园专精 有机认证',
        description: '福建安溪茶园专用，保护茶叶品质',
        image: 'https://picsum.photos/400/300?random=17',
        price: 30,
        unit: '亩',
        sales: 654,
        rating: 4.9,
        reviews: 156,
        providerName: '茶乡植保服务',
        providerType: 'owner',
        location: '福建安溪',
        isCertified: true,
        isHot: true,
        isNew: true
      }
    ])

    const formatNumber = (num) => {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
      }
      return num.toString()
    }

    const handleSearch = () => {
      console.log('搜索:', searchKeyword.value)
      fetchServices()
    }

    const handleFilterChange = () => {
      console.log('筛选条件:', filters.value)
      pagination.value.currentPage = 1
      fetchServices()
    }

    const handleReset = () => {
      filters.value = {
        type: '',
        priceRange: '',
        priceMin: '',
        priceMax: '',
        area: '',
        providerType: '',
        cropType: ''
      }
      searchKeyword.value = ''
      pagination.value.currentPage = 1
      fetchServices()
    }

    const handleSortChange = (sortId) => {
      currentSort.value = sortId
      fetchServices()
    }

    const handleSizeChange = (size) => {
      pagination.value.pageSize = size
      pagination.value.currentPage = 1
      fetchServices()
    }

    const handleCurrentChange = (page) => {
      pagination.value.currentPage = page
      fetchServices()
    }

    const goToDetail = (serviceId) => {
      router.push(`/service-detail/${serviceId}`)
    }

    const fetchServices = () => {
      console.log('获取服务列表...', {
        filters: filters.value,
        sort: currentSort.value,
        page: pagination.value
      })
    }

    onMounted(() => {
      fetchServices()
    })

    return {
      searchKeyword,
      filters,
      serviceTypes,
      priceRanges,
      serviceAreas,
      providerTypes,
      cropTypes,
      sortOptions,
      currentSort,
      pagination,
      total,
      serviceList,
      formatNumber,
      handleSearch,
      handleFilterChange,
      handleReset,
      handleSortChange,
      handleSizeChange,
      handleCurrentChange,
      goToDetail
    }
  }
}
</script>

<style scoped>
.service-list-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-content {
  max-width: 1600px;
  margin: 0 auto;
  padding: 16px 24px;
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
  font-size: 32px;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #10b981;
}

.navbar-center {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
}

.search-box {
  display: flex;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  transition: border-color 0.3s;
}

.search-box:focus-within {
  border-color: #10b981;
}

.search-input {
  flex: 1;
  border: none;
  padding: 10px 16px;
  font-size: 14px;
  background: transparent;
  outline: none;
}

.search-btn {
  padding: 10px 24px;
  background: #10b981;
  color: #fff;
  border: none;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.search-btn:hover {
  background: #059669;
}

.navbar-right {
  display: flex;
  gap: 20px;
  align-items: center;
}

.nav-btn {
  padding: 8px 16px;
  background: transparent;
  border: none;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 6px;
}

.nav-btn:hover {
  color: #10b981;
  background: rgba(16, 185, 129, 0.05);
}

.nav-btn.active {
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
  font-weight: 600;
}

.main-content {
  max-width: 1600px;
  margin: 0 auto;
  display: flex;
  gap: 24px;
  padding: 24px;
}

.filter-sidebar {
  width: 260px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: fit-content;
  position: sticky;
  top: 100px;
}

.filter-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  color: #4b5563;
}

.filter-option:hover {
  background: #f3f4f6;
}

.filter-option.active {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
  font-weight: 500;
}

.filter-option input[type="radio"] {
  margin: 0;
}

.price-inputs {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}

.price-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  outline: none;
  transition: border-color 0.3s;
}

.price-input:focus {
  border-color: #10b981;
}

.price-inputs span {
  color: #9ca3af;
}

.filter-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.reset-btn {
  flex: 1;
  padding: 10px;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.3s;
}

.reset-btn:hover {
  background: #e5e7eb;
}

.confirm-btn {
  flex: 2;
  padding: 10px;
  background: #10b981;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.confirm-btn:hover {
  background: #059669;
}

.content-area {
  flex: 1;
  min-width: 0;
}

.toolbar {
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.result-count {
  font-size: 14px;
  color: #4b5563;
}

.result-count strong {
  color: #10b981;
  font-size: 18px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-label {
  font-size: 14px;
  color: #6b7280;
}

.sort-btn {
  padding: 8px 16px;
  background: #f3f4f6;
  border: 1px solid transparent;
  border-radius: 6px;
  font-size: 14px;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.3s;
}

.sort-btn:hover {
  background: #e5e7eb;
}

.sort-btn.active {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
  border-color: #10b981;
  font-weight: 500;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.service-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

.service-image {
  position: relative;
  height: 220px;
  overflow: hidden;
  background: #f9fafb;
}

.service-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.service-card:hover .service-image img {
  transform: scale(1.1);
}

.service-tags {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  color: #fff;
}

.tag.certified {
  background: #10b981;
}

.tag.new {
  background: #3b82f6;
}

.tag.hot {
  background: #f59e0b;
}

.service-info {
  padding: 16px;
}

.service-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 44px;
  line-height: 1.4;
}

.service-desc {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.service-provider {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.provider-name {
  font-size: 13px;
  color: #6b7280;
}

.provider-type {
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 500;
}

.provider-type.flyer {
  background: #dbeafe;
  color: #1e40af;
}

.provider-type.owner {
  background: #fef3c7;
  color: #92400e;
}

.service-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 10px;
}

.service-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.rating-score {
  font-size: 16px;
  font-weight: 600;
  color: #f59e0b;
}

.rating-stars {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 12px;
  color: #d1d5db;
}

.star.filled {
  color: #fbbf24;
}

.reviews {
  font-size: 12px;
  color: #9ca3af;
  margin-left: auto;
}

.service-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.service-price {
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
  color: #ef4444;
  font-weight: 700;
}

.unit {
  font-size: 13px;
  color: #6b7280;
}

.contact-btn {
  padding: 8px 16px;
  background: #10b981;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.contact-btn:hover {
  background: #059669;
  transform: translateY(-1px);
}

.pagination-container {
  margin-top: 24px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

@media (max-width: 1200px) {
  .services-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 992px) {
  .filter-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .navbar-center {
    display: none;
  }
  
  .services-grid {
    grid-template-columns: 1fr;
  }
}
</style>
