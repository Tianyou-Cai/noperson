<template>
  <div class="service-detail-page">
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
            <input type="text" class="search-input" placeholder="搜索服务、设备、飞手" />
            <button class="search-btn">🔍</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/')">首页</button>
          <button class="nav-btn active" @click="router.push('/service-list')">找服务</button>
          <button class="nav-btn" @click="router.push('/equipment-list')">租设备</button>
          <button class="nav-btn" @click="router.push('/orders')">我的订单</button>
        </div>
      </div>
    </nav>

    <div class="main-content">
      <div class="detail-container">
        <!-- 左侧详情页内容 -->
        <div class="detail-left">
          <!-- 服务图片 -->
          <div class="service-gallery">
            <div class="main-image">
              <img :src="service.image" :alt="service.title" />
            </div>
            <div class="thumbnail-list">
              <img 
                v-for="(img, index) in service.images" 
                :key="index"
                :src="img"
                :class="['thumbnail', { active: currentImageIndex === index }]"
                @click="currentImageIndex = index"
              />
            </div>
          </div>

          <!-- 商品信息卡片 -->
          <div class="product-card">
            <div class="product-header">
              <div :class="['mode-badge', service.providerType]">
                {{ service.providerType === 'flyer' ? '👤 C2C 农户直供' : '🏢 B2C 商家服务' }}
              </div>
              <div class="product-tags">
                <span v-if="service.isCertified" class="tag certified">✓ 认证商家</span>
                <span v-if="service.isHot" class="tag hot">🔥 热销</span>
                <span v-if="service.isNew" class="tag new">新品</span>
              </div>
            </div>
            
            <h1 class="product-title">{{ service.title }}</h1>
            
            <div class="product-rating-row">
              <div class="rating-info">
                <span class="rating-score">{{ service.rating }}</span>
                <div class="rating-stars">
                  <span v-for="i in 5" :key="i" :class="['star', { filled: i <= Math.floor(service.rating) }]">★</span>
                </div>
                <span class="rating-count">({{ service.reviews }}条评价)</span>
              </div>
              <span class="sales-count">已售 {{ formatNumber(service.sales) }} 单</span>
            </div>

            <div class="product-price-section">
              <div class="price-main">
                <span class="currency">¥</span>
                <span class="price">{{ service.price }}</span>
                <span class="unit">{{ service.unit }}</span>
              </div>
              <div class="price-tip">
                <span v-if="service.minArea">最低 {{ service.minArea }}亩起订</span>
                <span v-if="service.discount" class="discount-text">{{ service.discount }}</span>
              </div>
            </div>

            <div class="service-highlights">
              <h3>服务亮点</h3>
              <div class="highlights-grid">
                <div v-for="highlight in service.highlights" :key="highlight.title" class="highlight-item">
                  <span class="highlight-icon">{{ highlight.icon }}</span>
                  <div class="highlight-content">
                    <span class="highlight-title">{{ highlight.title }}</span>
                    <span class="highlight-desc">{{ highlight.desc }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 服务详情 -->
          <div class="detail-sections">
            <div class="section">
              <h3 class="section-title">📋 服务介绍</h3>
              <div class="service-description">
                <p>{{ service.fullDescription }}</p>
              </div>
            </div>

            <div class="section">
              <h3 class="section-title">📊 服务详情</h3>
              <div class="service-specs">
                <div class="spec-row">
                  <span class="spec-label">服务类型</span>
                  <span class="spec-value">{{ service.serviceType }}</span>
                </div>
                <div class="spec-row">
                  <span class="spec-label">适用作物</span>
                  <span class="spec-value">{{ service.cropTypes.join('、') }}</span>
                </div>
                <div class="spec-row">
                  <span class="spec-label">作业设备</span>
                  <span class="spec-value">{{ service.equipment }}</span>
                </div>
                <div class="spec-row">
                  <span class="spec-label">作业幅宽</span>
                  <span class="spec-value">{{ service.workWidth }}</span>
                </div>
                <div class="spec-row">
                  <span class="spec-label">作业效率</span>
                  <span class="spec-value">{{ service.efficiency }}</span>
                </div>
                <div class="spec-row">
                  <span class="spec-label">服务区域</span>
                  <span class="spec-value">{{ service.serviceArea }}</span>
                </div>
              </div>
            </div>

            <div class="section">
              <h3 class="section-title">💬 用户评价 ({{ service.reviews }})</h3>
              <div class="reviews-list">
                <div v-for="review in reviews" :key="review.id" class="review-item">
                  <div class="review-header">
                    <div class="reviewer-info">
                      <div class="reviewer-avatar">{{ review.name.charAt(0) }}</div>
                      <div class="reviewer-detail">
                        <span class="reviewer-name">{{ review.name }}</span>
                        <span class="review-time">{{ review.time }}</span>
                      </div>
                    </div>
                    <div class="review-rating">
                      <span v-for="i in 5" :key="i" :class="['star', { filled: i <= review.rating }]">★</span>
                    </div>
                  </div>
                  <div class="review-content">{{ review.content }}</div>
                  <div v-if="review.images && review.images.length" class="review-images">
                    <img v-for="(img, idx) in review.images" :key="idx" :src="img" class="review-image" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧信息栏 -->
        <div class="detail-right">
          <!-- 商家信息 -->
          <div class="provider-card">
            <div class="card-header">
              <div class="provider-avatar">👤</div>
              <div class="provider-info">
                <h4 class="provider-name">{{ service.providerName }}</h4>
                <span :class="['provider-type', service.providerType]">
                  {{ service.providerType === 'flyer' ? '认证飞手' : '认证商家' }}
                </span>
              </div>
            </div>
            <div class="provider-stats">
              <div class="stat-item">
                <span class="stat-value">{{ service.providerExperience }}</span>
                <span class="stat-label">年经验</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ formatNumber(service.sales) }}</span>
                <span class="stat-label">累计服务</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ service.rating }}</span>
                <span class="stat-label">好评率</span>
              </div>
            </div>
            <div class="provider-actions">
              <button class="contact-btn" @click="handleContact">
                <span class="icon">💬</span> 联系商家
              </button>
              <button class="favorite-btn" :class="{ active: isFavorite }" @click="toggleFavorite">
                <span class="icon">{{ isFavorite ? '❤️' : '🤍' }}</span> 
                {{ isFavorite ? '已收藏' : '收藏' }}
              </button>
            </div>
          </div>

          <!-- 购买卡片 -->
          <div class="buy-card">
            <div class="buy-header">
              <h3>立即预订</h3>
            </div>
            <div class="buy-body">
              <div class="input-group">
                <label class="input-label">作业面积 (亩)</label>
                <div class="quantity-control">
                  <button class="qty-btn" @click="decreaseQty">-</button>
                  <input type="number" v-model.number="orderQty" class="qty-input" />
                  <button class="qty-btn" @click="increaseQty">+</button>
                </div>
              </div>
              
              <div class="input-group">
                <label class="input-label">作业地点</label>
                <input type="text" v-model="orderLocation" placeholder="请输入作业地点" class="location-input" />
              </div>

              <div class="input-group">
                <label class="input-label">期望时间</label>
                <el-date-picker 
                  v-model="orderDate" 
                  type="date" 
                  placeholder="选择日期"
                  class="date-picker"
                ></el-date-picker>
              </div>

              <div class="order-summary">
                <div class="summary-row">
                  <span class="summary-label">服务费用</span>
                  <span class="summary-value">¥{{ service.price }}/{{ service.unit }}</span>
                </div>
                <div class="summary-row">
                  <span class="summary-label">作业面积</span>
                  <span class="summary-value">{{ orderQty }} 亩</span>
                </div>
                <div class="summary-row total-row">
                  <span class="summary-label">应付金额</span>
                  <span class="summary-total">¥{{ totalPrice }}</span>
                </div>
              </div>
            </div>
            <div class="buy-actions">
              <button class="cart-btn" @click="handleAddToCart">
                <span class="icon">🛒</span> 加入购物车
              </button>
              <button class="buy-now-btn" @click="handleBuyNow">立即购买</button>
            </div>
            <div class="buy-guarantee">
              <div class="guarantee-item">
                <span class="icon">✅</span>
                <span>平台担保交易</span>
              </div>
              <div class="guarantee-item">
                <span class="icon">🔒</span>
                <span>资金安全</span>
              </div>
              <div class="guarantee-item">
                <span class="icon">⭐</span>
                <span>售后无忧</span>
              </div>
            </div>
          </div>

          <!-- 资质认证 -->
          <div class="certification-card">
            <h4 class="card-title">📜 资质认证</h4>
            <div class="certification-list">
              <div v-for="cert in service.certifications" :key="cert" class="cert-item">
                <span class="icon">✓</span>
                <span>{{ cert }}</span>
              </div>
            </div>
          </div>

          <!-- 服务保障 -->
          <div class="service-guarantee-card">
            <h4 class="card-title">🛡️ 服务保障</h4>
            <div class="guarantee-list">
              <div class="guarantee-row">
                <span class="guarantee-icon">⏰</span>
                <div class="guarantee-content">
                  <span class="guarantee-title">准时服务</span>
                  <span class="guarantee-desc">按约定时间准时到达作业</span>
                </div>
              </div>
              <div class="guarantee-row">
                <span class="guarantee-icon">💰</span>
                <div class="guarantee-content">
                  <span class="guarantee-title">不满意退款</span>
                  <span class="guarantee-desc">服务不满意全额退款</span>
                </div>
              </div>
              <div class="guarantee-row">
                <span class="guarantee-icon">📞</span>
                <div class="guarantee-content">
                  <span class="guarantee-title">7×24客服</span>
                  <span class="guarantee-desc">全天候客服支持</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'ServiceDetail',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const currentImageIndex = ref(0)
    const isFavorite = ref(false)
    const orderQty = ref(50)
    const orderLocation = ref('')
    const orderDate = ref('')

    const service = ref({
      id: route.params.id,
      title: '专业小麦农药喷洒服务 - 高效均匀 快速响单',
      image: 'https://picsum.photos/800/600?random=10',
      images: [
        'https://picsum.photos/800/600?random=10',
        'https://picsum.photos/800/600?random=11',
        'https://picsum.photos/800/600?random=12',
        'https://picsum.photos/800/600?random=13'
      ],
      price: 15,
      unit: '亩',
      minArea: 50,
      discount: '100亩以上 9折优惠',
      description: '采用大疆 T30 无人机，专业飞手团队，日均作业 500亩+',
      fullDescription: `我们提供专业的小麦农药喷洒服务，采用先进的大疆 T30 植保无人机，配备高性能喷洒系统，确保药液均匀覆盖。

服务团队由多名持证飞手组成，平均从业经验 5 年以上，熟悉各种作物的植保需求。

服务流程：
1. 实地勘察：免费上门勘察地块，评估作业难度
2. 方案制定：根据作物和病虫害情况，制定科学喷洒方案
3. 作业实施：按约定时间到场作业，确保质量
4. 效果验收：作业完成后，由农户验收确认
5. 售后服务：提供 7 天效果跟踪，有问题及时处理

我们的优势：
- 设备先进：全部采用大疆最新款 T30 无人机
- 技术专业：飞手均持有 AOPA 驾驶证
- 经验丰富：累计服务面积超过 10 万亩
- 价格透明：明码标价，无隐形消费
- 售后保障：作业效果不满意，免费重喷`,
      serviceType: '农药喷洒',
      cropTypes: ['小麦', '玉米', '大豆'],
      equipment: '大疆 T30 植保无人机',
      workWidth: '9 米',
      efficiency: '日均 500-800 亩',
      serviceArea: '山东省济南市及周边区县',
      sales: 2340,
      rating: 4.9,
      reviews: 856,
      providerName: '绿翼植保服务队',
      providerType: 'flyer',
      providerExperience: 5,
      certifications: ['AOPA 无人机驾驶证', '植保服务认证', '安全生产许可证', '实名认证'],
      isCertified: true,
      isHot: true,
      isNew: false,
      highlights: [
        { icon: '🚀', title: '快速响应', desc: '24小时内上门' },
        { icon: '🎯', title: '精准作业', desc: '均匀覆盖无死角' },
        { icon: '💰', title: '透明定价', desc: '无隐形消费' },
        { icon: '✅', title: '品质保证', desc: '不满意退款' }
      ]
    })

    const reviews = ref([
      {
        id: 1,
        name: '张农户',
        rating: 5,
        content: '服务非常专业，飞手技术很好，喷洒很均匀，价格也很合理，下次还会找他们！',
        time: '2024-01-15',
        images: []
      },
      {
        id: 2,
        name: '李农户',
        rating: 5,
        content: '效率很高，200亩地一天就喷完了，而且效果很好，病虫害明显减少了。',
        time: '2024-01-10',
        images: []
      },
      {
        id: 3,
        name: '王农户',
        rating: 4,
        content: '整体不错，就是来的稍微晚了一点，不过作业质量没得说，很专业。',
        time: '2024-01-05',
        images: []
      },
      {
        id: 4,
        name: '刘农户',
        rating: 5,
        content: '服务态度很好，提前沟通到位，作业完成后还主动回访，非常满意！',
        time: '2024-01-02',
        images: []
      }
    ])

    const totalPrice = computed(() => {
      return service.value.price * orderQty.value
    })

    const formatNumber = (num) => {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
      }
      return num.toString()
    }

    const decreaseQty = () => {
      if (orderQty.value > service.value.minArea) {
        orderQty.value -= 10
      }
    }

    const increaseQty = () => {
      orderQty.value += 10
    }

    const handleContact = () => {
      ElMessage.info('正在打开聊天窗口...')
    }

    const toggleFavorite = () => {
      isFavorite.value = !isFavorite.value
      ElMessage.success(isFavorite.value ? '已加入收藏' : '已取消收藏')
    }

    const handleAddToCart = () => {
      if (!orderLocation.value) {
        ElMessage.warning('请填写作业地点')
        return
      }
      ElMessage.success('已加入购物车')
    }

    const handleBuyNow = () => {
      if (!orderLocation.value) {
        ElMessage.warning('请填写作业地点')
        return
      }
      if (!orderDate.value) {
        ElMessage.warning('请选择期望时间')
        return
      }
      router.push(`/checkout/${service.value.id}`)
    }

    onMounted(() => {
      window.scrollTo(0, 0)
    })

    return {
      currentImageIndex,
      isFavorite,
      orderQty,
      orderLocation,
      orderDate,
      service,
      reviews,
      totalPrice,
      formatNumber,
      decreaseQty,
      increaseQty,
      handleContact,
      toggleFavorite,
      handleAddToCart,
      handleBuyNow
    }
  }
}
</script>

<style scoped>
.service-detail-page {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-navbar {
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
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
  font-size: 36px;
}

.logo-text {
  font-size: 26px;
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
  background: #fff;
  border-radius: 30px;
  overflow: hidden;
  border: 2px solid #e5e7eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-input {
  flex: 1;
  border: none;
  padding: 12px 20px;
  font-size: 15px;
  background: transparent;
  outline: none;
}

.search-btn {
  padding: 12px 24px;
  background: #10b981;
  color: #fff;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.navbar-right {
  display: flex;
  gap: 16px;
  align-items: center;
}

.nav-btn {
  padding: 10px 20px;
  background: transparent;
  border: none;
  font-size: 15px;
  color: #4b5563;
  cursor: pointer;
  border-radius: 8px;
  font-weight: 500;
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

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.detail-container {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
}

.detail-left {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.service-gallery {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.main-image {
  width: 100%;
  height: 420px;
  overflow: hidden;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-list {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid #f3f4f6;
}

.thumbnail {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.thumbnail:hover {
  opacity: 0.8;
}

.thumbnail.active {
  border-color: #10b981;
}

.product-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.mode-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.mode-badge.flyer {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #1e40af;
}

.mode-badge.owner {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
}

.product-tags {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
}

.tag.certified {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.tag.hot {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.tag.new {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.product-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 16px;
  line-height: 1.4;
}

.product-rating-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.rating-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-score {
  font-size: 24px;
  font-weight: 700;
  color: #f59e0b;
}

.rating-stars {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 16px;
  color: #d1d5db;
}

.star.filled {
  color: #fbbf24;
}

.rating-count {
  font-size: 14px;
  color: #6b7280;
}

.sales-count {
  font-size: 14px;
  color: #6b7280;
}

.product-price-section {
  background: linear-gradient(135deg, #fff7ed 0%, #fff 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  border: 2px solid #fed7aa;
}

.price-main {
  display: flex;
  align-items: baseline;
  margin-bottom: 12px;
}

.price-main .currency {
  font-size: 24px;
  color: #ef4444;
  font-weight: 600;
}

.price-main .price {
  font-size: 48px;
  color: #ef4444;
  font-weight: 700;
}

.price-main .unit {
  font-size: 18px;
  color: #6b7280;
  margin-left: 6px;
}

.price-tip {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-tip span {
  font-size: 14px;
  color: #6b7280;
}

.discount-text {
  color: #ef4444 !important;
  font-weight: 600;
}

.service-highlights {
  margin-bottom: 0;
}

.service-highlights h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
}

.highlights-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.highlight-item {
  display: flex;
  gap: 10px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
}

.highlight-icon {
  font-size: 24px;
}

.highlight-content {
  display: flex;
  flex-direction: column;
}

.highlight-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.highlight-desc {
  font-size: 12px;
  color: #6b7280;
}

.detail-sections {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section {
  margin-bottom: 32px;
}

.section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #10b981;
}

.service-description {
  line-height: 1.8;
  color: #4b5563;
}

.service-description p {
  white-space: pre-line;
}

.service-specs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.spec-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.spec-label {
  font-weight: 500;
  color: #6b7280;
}

.spec-value {
  color: #1f2937;
  font-weight: 500;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: #f9fafb;
  border-radius: 12px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.reviewer-detail {
  display: flex;
  flex-direction: column;
}

.reviewer-name {
  font-weight: 600;
  color: #1f2937;
}

.review-time {
  font-size: 12px;
  color: #9ca3af;
}

.review-rating {
  display: flex;
  gap: 2px;
}

.review-content {
  color: #4b5563;
  line-height: 1.6;
  margin-bottom: 12px;
}

.review-images {
  display: flex;
  gap: 8px;
}

.review-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.detail-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.provider-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-bottom: 20px;
}

.provider-avatar {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
}

.provider-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.provider-type {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
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

.provider-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 16px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #10b981;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #9ca3af;
}

.provider-actions {
  display: flex;
  gap: 12px;
}

.contact-btn,
.favorite-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e5e7eb;
  background: #fff;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.contact-btn:hover {
  border-color: #10b981;
  color: #10b981;
}

.favorite-btn.active {
  color: #ef4444;
  border-color: #ef4444;
}

.buy-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: sticky;
  top: 100px;
}

.buy-header {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  padding: 16px 20px;
  color: #fff;
}

.buy-header h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.buy-body {
  padding: 20px;
}

.input-group {
  margin-bottom: 16px;
}

.input-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 12px;
}

.qty-btn {
  width: 40px;
  height: 40px;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.qty-btn:hover {
  background: #e5e7eb;
}

.qty-input {
  flex: 1;
  height: 40px;
  text-align: center;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
}

.qty-input:focus {
  border-color: #10b981;
}

.location-input {
  width: 100%;
  height: 40px;
  padding: 0 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}

.location-input:focus {
  border-color: #10b981;
}

.date-picker {
  width: 100%;
}

.order-summary {
  background: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  margin-top: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.summary-label {
  font-size: 14px;
  color: #6b7280;
}

.summary-value {
  font-size: 14px;
  color: #1f2937;
}

.total-row {
  padding-top: 8px;
  border-top: 1px dashed #e5e7eb;
  margin-top: 8px;
}

.summary-total {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
}

.buy-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.cart-btn {
  flex: 1;
  padding: 14px;
  background: #fff;
  border: 2px solid #10b981;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #10b981;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.cart-btn:hover {
  background: rgba(16, 185, 129, 0.08);
}

.buy-now-btn {
  flex: 2;
  padding: 14px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.buy-now-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
}

.buy-guarantee {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px 20px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
}

.guarantee-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #4b5563;
}

.certification-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
}

.certification-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cert-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #4b5563;
}

.cert-item .icon {
  color: #10b981;
  font-weight: 700;
}

.service-guarantee-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.guarantee-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.guarantee-row {
  display: flex;
  gap: 12px;
}

.guarantee-icon {
  font-size: 24px;
}

.guarantee-content {
  display: flex;
  flex-direction: column;
}

.guarantee-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.guarantee-desc {
  font-size: 12px;
  color: #6b7280;
}

@media (max-width: 1200px) {
  .detail-container {
    grid-template-columns: 1fr;
  }

  .detail-right {
    order: -1;
  }

  .buy-card {
    position: static;
  }
}

@media (max-width: 768px) {
  .navbar-center {
    display: none;
  }

  .service-specs {
    grid-template-columns: 1fr;
  }

  .highlights-grid {
    grid-template-columns: 1fr;
  }

  .navbar-right {
    gap: 8px;
  }

  .nav-btn {
    padding: 8px 12px;
    font-size: 13px;
  }
}
</style>