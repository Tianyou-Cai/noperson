<template>
  <div class="service-detail-page">
    <!-- 顶部导航 -->
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
            <input type="text" class="search-input" placeholder="搜索服务" />
            <button class="search-btn">搜索</button>
          </div>
        </div>
        <div class="navbar-right">
          <button class="nav-btn" @click="router.push('/')">首页</button>
          <button class="nav-btn active" @click="router.push('/service-list')">找服务</button>
          <button class="nav-btn" @click="router.push('/equipment-list')">租设备</button>
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

          <!-- 服务详情 -->
          <div class="detail-sections">
            <div class="section">
              <h3 class="section-title">服务介绍</h3>
              <div class="service-description">
                <p>{{ service.fullDescription }}</p>
              </div>
            </div>

            <div class="section">
              <h3 class="section-title">服务详情</h3>
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
                  <span class="spec-label"> service 区域</span>
                  <span class="spec-value">{{ service.serviceArea }}</span>
                </div>
              </div>
            </div>

            <div class="section">
              <h3 class="section-title">用户评价 ({{ service.reviews }})</h3>
              <div class="reviews-list">
                <div v-for="review in reviews" :key="review.id" class="review-item">
                  <div class="review-header">
                    <div class="reviewer-info">
                      <div class="reviewer-avatar">{{ reviewer.name.charAt(0) }}</div>
                      <span class="reviewer-name">{{ reviewer.name }}</span>
                    </div>
                    <div class="review-rating">
                      <span v-for="i in 5" :key="i" :class="['star', { filled: i <= review.rating }]">★</span>
                    </div>
                  </div>
                  <div class="review-content">{{ review.content }}</div>
                  <div class="review-time">{{ review.time }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧信息栏 -->
        <div class="detail-right">
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

          <div class="price-card">
            <div class="price-header">
              <div class="price-label">服务价格</div>
            </div>
            <div class="price-body">
              <div class="price-main">
                <span class="currency">¥</span>
                <span class="price">{{ service.price }}</span>
                <span class="unit">/{{ service.unit }}</span>
              </div>
              <div class="price-desc">
                <span v-if="service.minArea">最低 {{ service.minArea }}亩起</span>
                <span v-if="service.discount" class="discount">{{ service.discount }}</span>
              </div>
            </div>
            <div class="price-actions">
              <button class="book-btn" @click="handleBookNow">立即预订</button>
              <button class="consult-btn" @click="handleConsult">在线咨询</button>
            </div>
            <div class="price-guarantee">
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

          <div class="location-card">
            <h4 class="card-title">服务区域</h4>
            <div class="location-content">
              <p>📍 {{ service.serviceArea }}</p>
              <p class="location-desc">可上门服务，远程区域需协商</p>
            </div>
          </div>

          <div class="certification-card">
            <h4 class="card-title">资质认证</h4>
            <div class="certification-list">
              <div v-for="cert in service.certifications" :key="cert" class="cert-item">
                <span class="icon">✓</span>
                <span>{{ cert }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'ServiceDetail',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const currentImageIndex = ref(0)
    const isFavorite = ref(false)

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
      discount: '100 亩以上 9 折优惠',
      description: '采用大疆 T30 无人机，专业飞手团队，日均作业 500 亩+',
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
      certifications: ['AOPA 无人机驾驶证', '植保服务认证', '安全生产许可证', '实名认证']
    })

    const reviews = ref([
      {
        id: 1,
        name: '张农户',
        rating: 5,
        content: '服务非常专业，飞手技术很好，喷洒很均匀，价格也很合理，下次还会找他们！',
        time: '2024-01-15'
      },
      {
        id: 2,
        name: '李农户',
        rating: 5,
        content: '效率很高，200 亩地一天就喷完了，而且效果很好，病虫害明显减少了。',
        time: '2024-01-10'
      },
      {
        id: 3,
        name: '王农户',
        rating: 4,
        content: '整体不错，就是来的稍微晚了一点，不过作业质量没得说，很专业。',
        time: '2024-01-05'
      }
    ])

    const formatNumber = (num) => {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万'
      }
      return num.toString()
    }

    const handleContact = () => {
      ElMessage.info('联系商家功能开发中')
    }

    const toggleFavorite = () => {
      isFavorite.value = !isFavorite.value
      ElMessage.success(isFavorite.value ? '已加入收藏' : '已取消收藏')
    }

    const handleBookNow = () => {
      router.push(`/book-service/${service.value.id}`)
    }

    const handleConsult = () => {
      ElMessage.info('在线咨询功能开发中')
    }

    onMounted(() => {
      window.scrollTo(0, 0)
    })

    return {
      currentImageIndex,
      isFavorite,
      service,
      reviews,
      formatNumber,
      handleContact,
      toggleFavorite,
      handleBookNow,
      handleConsult
    }
  }
}
</script>

<style scoped>
.service-detail-page {
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
  max-width: 1400px;
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
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.service-gallery {
  padding: 20px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.main-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-list {
  display: flex;
  gap: 12px;
}

.thumbnail {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
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

.detail-sections {
  padding: 20px;
}

.section {
  margin-bottom: 32px;
}

.section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #10b981;
}

.service-description {
  line-height: 1.8;
  color: #4b5563;
}

.service-description p {
  white-space: pre-line;
  margin-bottom: 16px;
}

.service-specs {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
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
  background: #10b981;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.reviewer-name {
  font-weight: 600;
  color: #1f2937;
}

.review-rating {
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

.review-content {
  color: #4b5563;
  line-height: 1.7;
  margin-bottom: 12px;
}

.review-time {
  font-size: 13px;
  color: #9ca3af;
}

.detail-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.provider-card {
  background: #fff;
  border-radius: 12px;
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

.price-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.price-header {
  margin-bottom: 16px;
}

.price-label {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.price-body {
  margin-bottom: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #fef2f2 0%, #fff 100%);
  border-radius: 8px;
  border: 2px solid #fecaca;
}

.price-main {
  display: flex;
  align-items: baseline;
  margin-bottom: 12px;
}

.price-main .currency {
  font-size: 20px;
  color: #ef4444;
  font-weight: 600;
}

.price-main .price {
  font-size: 42px;
  color: #ef4444;
  font-weight: 700;
}

.price-main .unit {
  font-size: 16px;
  color: #6b7280;
  margin-left: 4px;
}

.price-desc {
  font-size: 14px;
  color: #6b7280;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.discount {
  padding: 4px 12px;
  background: linear-gradient(90deg, #fbbf24 0%, #f59e0b 100%);
  color: #fff;
  border-radius: 6px;
  font-weight: 500;
}

.price-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.book-btn,
.consult-btn {
  width: 100%;
  padding: 14px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.book-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
}

.book-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
}

.consult-btn {
  background: #fff;
  color: #10b981;
  border: 2px solid #10b981;
}

.consult-btn:hover {
  background: rgba(16, 185, 129, 0.05);
}

.price-guarantee {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.guarantee-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #4b5563;
}

.guarantee-item .icon {
  font-size: 18px;
}

.location-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12px;
}

.location-content p {
  font-size: 14px;
  color: #4b5563;
  margin-bottom: 8px;
}

.location-desc {
  font-size: 13px;
  color: #9ca3af !important;
}

.certification-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.certification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cert-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #4b5563;
}

.cert-item .icon {
  color: #10b981;
  font-weight: 700;
}

@media (max-width: 1200px) {
  .detail-container {
    grid-template-columns: 1fr;
  }

  .detail-right {
    position: sticky;
    top: 100px;
  }
}

@media (max-width: 768px) {
  .navbar-center {
    display: none;
  }

  .service-specs {
    grid-template-columns: 1fr;
  }
}
</style>
