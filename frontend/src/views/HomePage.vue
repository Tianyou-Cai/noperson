<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const navScrolled = ref(false);
const currentBannerIndex = ref(0);

// 轮播图数据
const banners = ref([
  {
    id: 1,
    image: 'https://picsum.photos/1920/600?random=1',
    title: '专业无人机喷洒服务',
    subtitle: '高效、精准、安全，让农业更智慧',
    link: '/service-list'
  },
  {
    id: 2,
    image: 'https://picsum.photos/1920/600?random=2',
    title: '设备租赁优惠中',
    subtitle: '大牌无人机，日租仅需 180 元起',
    link: '/equipment-list'
  },
  {
    id: 3,
    image: 'https://picsum.photos/1920/600?random=3',
    title: '成为认证飞手',
    subtitle: '时间自由，收入可观，立即加入',
    link: '/register'
  }
]);

// 服务分类
const serviceCategories = ref([
  { id: 1, name: '农药喷洒', icon: '🌾', desc: '高效均匀喷洒', count: 1240 },
  { id: 2, name: '播种服务', icon: '🌱', desc: '精准量化播种', count: 856 },
  { id: 3, name: '施肥作业', icon: '💊', desc: '科学配方施肥', count: 623 },
  { id: 4, name: '农情监测', icon: '📊', desc: '遥感数据分析', count: 412 },
  { id: 5, name: '除草服务', icon: '🌿', desc: '靶向精准除草', count: 534 },
  { id: 6, name: '植保托管', icon: '🤝', desc: '全程托管服务', count: 289 }
]);

// 热门服务列表
const hotServices = ref([
  {
    id: 1,
    title: '专业小麦农药喷洒服务',
    image: 'https://picsum.photos/400/300?random=10',
    price: 15,
    unit: '元/亩',
    sales: 2340,
    rating: 4.9,
    reviews: 856,
    provider: '绿翼植保服务队',
    providerType: 'flyer',
    location: '山东济南',
    tags: ['认证飞手', '五年经验', '高效作业']
  },
  {
    id: 2,
    title: '水稻播种一条龙服务',
    image: 'https://picsum.photos/400/300?random=11',
    price: 25,
    unit: '元/亩',
    sales: 1890,
    rating: 4.8,
    reviews: 623,
    provider: '丰收获服合作社',
    providerType: 'owner',
    location: '江苏徐州',
    tags: ['机主直营', '设备先进', '价格优惠']
  },
  {
    id: 3,
    title: '玉米地施肥作业',
    image: 'https://picsum.photos/400/300?random=12',
    price: 18,
    unit: '元/亩',
    sales: 1567,
    rating: 4.7,
    reviews: 445,
    provider: '天宇农业服务',
    providerType: 'flyer',
    location: '河南郑州',
    tags: ['专业团队', '快速响应']
  },
  {
    id: 4,
    title: '果树病虫害防治',
    image: 'https://picsum.photos/400/300?random=13',
    price: 35,
    unit: '元/亩',
    sales: 1234,
    rating: 4.9,
    reviews: 512,
    provider: '惠农植保中心',
    providerType: 'owner',
    location: '陕西西安',
    tags: ['认证飞手', '果树专家', '好评如潮']
  },
  {
    id: 5,
    title: '大田作物综合植保',
    image: 'https://picsum.photos/400/300?random=14',
    price: 20,
    unit: '元/亩',
    sales: 987,
    rating: 4.6,
    reviews: 328,
    provider: '田野飞防大队',
    providerType: 'flyer',
    location: '安徽合肥',
    tags: ['性价比高', '覆盖面积广']
  },
  {
    id: 6,
    title: '蔬菜基地精准施药',
    image: 'https://picsum.photos/400/300?random=15',
    price: 28,
    unit: '元/亩',
    sales: 876,
    rating: 4.8,
    reviews: 267,
    provider: '绿源农业合作社',
    providerType: 'owner',
    location: '山东潍坊',
    tags: ['蔬菜专家', '绿色防控']
  },
  {
    id: 7,
    title: '棉花脱叶剂喷洒',
    image: 'https://picsum.photos/400/300?random=16',
    price: 22,
    unit: '元/亩',
    sales: 765,
    rating: 4.7,
    reviews: 198,
    provider: '新疆飞防总站',
    providerType: 'flyer',
    location: '新疆乌鲁木齐',
    tags: ['地域专家', '经验丰富']
  },
  {
    id: 8,
    title: '茶园病虫害统防统治',
    image: 'https://picsum.photos/400/300?random=17',
    price: 30,
    unit: '元/亩',
    sales: 654,
    rating: 4.9,
    reviews: 156,
    provider: '茶乡植保服务',
    providerType: 'owner',
    location: '福建安溪',
    tags: ['茶园专精', '有机认证']
  }
]);

// 热门设备列表
const hotEquipment = ref([
  {
    id: 1,
    name: '大疆 T30 植保无人机',
    image: 'https://picsum.photos/400/300?random=20',
    price: 280,
    unit: '元/天',
    sales: 156,
    rating: 4.9,
    reviews: 89,
    owner: '鑫达航空科技',
    specs: ['30L 载重', '28 分钟续航', '9 米幅宽'],
    tags: ['热租', '新款']
  },
  {
    id: 2,
    name: '极飞 P100 农业无人机',
    image: 'https://picsum.photos/400/300?random=21',
    price: 320,
    unit: '元/天',
    sales: 142,
    rating: 4.8,
    reviews: 76,
    owner: '极飞授权店',
    specs: ['100L 载重', '35 分钟续航', '智能喷洒'],
    tags: ['大容量', '长续航']
  },
  {
    id: 3,
    name: '大疆 T16 植保无人机',
    image: 'https://picsum.photos/400/300?random=22',
    price: 180,
    unit: '元/天',
    sales: 198,
    rating: 4.7,
    reviews: 112,
    owner: '农机租赁中心',
    specs: ['16L 载重', '25 分钟续航', '双喷头'],
    tags: ['性价比', '经济款']
  },
  {
    id: 4,
    name: '极飞 P80 农业无人机',
    image: 'https://picsum.photos/400/300?random=23',
    price: 260,
    unit: '元/天',
    sales: 123,
    rating: 4.8,
    reviews: 67,
    owner: '丰收设备租赁',
    specs: ['80L 载重', '30 分钟续航', 'RTK 定位'],
    tags: ['精准', '稳定']
  }
]);

// 角色入口
const roleEntries = ref([
  {
    id: 'farmer',
    name: '我是农户',
    icon: '🌾',
    desc: '发布作业需求，快速找到专业飞手',
    subDesc: '不用买设备，省钱又省心',
    color: '#10b981',
    link: '/register?role=farmer'
  },
  {
    id: 'flyer',
    name: '我是飞手',
    icon: '🚁',
    desc: '接单赚收益，时间自由收入高',
    subDesc: '可租设备，轻装上阵',
    color: '#3b82f6',
    link: '/register?role=flyer'
  },
  {
    id: 'owner',
    name: '我是机主',
    icon: '🏭',
    desc: '共享闲置设备，赚取稳定收益',
    subDesc: '设备托管，坐享收益',
    color: '#f59e0b',
    link: '/register?role=owner'
  }
]);

// 轮播图自动切换
let bannerTimer = null;
const startBannerTimer = () => {
  bannerTimer = setInterval(() => {
    currentBannerIndex.value = (currentBannerIndex.value + 1) % banners.value.length;
  }, 5000);
};

const goToBanner = (index) => {
  currentBannerIndex.value = index;
  if (bannerTimer) {
    clearInterval(bannerTimer);
    startBannerTimer();
  }
};

const handleScroll = () => {
  navScrolled.value = window.scrollY > 20;
};

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万';
  }
  return num.toString();
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll);
  startBannerTimer();
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  if (bannerTimer) {
    clearInterval(bannerTimer);
  }
});
</script>

<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <nav class="navbar" :class="{ scrolled: navScrolled }">
      <div class="nav-content">
        <div class="nav-left">
          <div class="logo" @click="router.push('/')">
            <span class="logo-icon">🚁</span>
            <span class="logo-text">农翼通</span>
          </div>
        </div>
        <div class="nav-center">
          <div class="search-box">
            <input type="text" class="search-input" placeholder="搜索服务、设备、飞手" />
            <button class="search-btn">搜索</button>
          </div>
        </div>
        <div class="nav-right">
          <button class="nav-btn" @click="router.push('/service-list')">找服务</button>
          <button class="nav-btn" @click="router.push('/equipment-list')">租设备</button>
          <button class="nav-btn" @click="router.push('/order-list')">我的订单</button>
          <button class="nav-btn-outline" @click="router.push('/login')">登录</button>
          <button class="nav-btn-primary" @click="router.push('/register')">注册</button>
        </div>
      </div>
    </nav>

    <!-- 轮播图 -->
    <section class="banner-section">
      <div class="banner-container">
        <div class="banner-wrapper" :style="{ transform: `translateX(-${currentBannerIndex * 100}%)` }">
          <div v-for="banner in banners" :key="banner.id" class="banner-item">
            <img :src="banner.image" :alt="banner.title" />
            <div class="banner-content">
              <h2 class="banner-title">{{ banner.title }}</h2>
              <p class="banner-subtitle">{{ banner.subtitle }}</p>
              <button class="banner-btn" @click="router.push(banner.link)">立即查看</button>
            </div>
          </div>
        </div>
        <div class="banner-indicators">
          <span 
            v-for="(banner, index) in banners" 
            :key="banner.id"
            :class="['indicator', { active: currentBannerIndex === index }]"
            @click="goToBanner(index)"
          ></span>
        </div>
      </div>
    </section>

    <!-- 角色入口 -->
    <section class="roles-section">
      <div class="container">
        <h2 class="section-title">选择您的角色</h2>
        <div class="roles-grid">
          <div 
            v-for="role in roleEntries" 
            :key="role.id"
            class="role-card"
            :style="{ '--role-color': role.color }"
            @click="router.push(role.link)"
          >
            <div class="role-icon">{{ role.icon }}</div>
            <h3 class="role-title">{{ role.name }}</h3>
            <p class="role-desc">{{ role.desc }}</p>
            <p class="role-subdesc">{{ role.subDesc }}</p>
            <button class="role-btn" :style="{ background: role.color }">立即加入</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 服务分类 -->
    <section class="categories-section">
      <div class="container">
        <h2 class="section-title">服务分类</h2>
        <div class="categories-grid">
          <div 
            v-for="cat in serviceCategories" 
            :key="cat.id"
            class="category-card"
            @click="router.push(`/service-list?category=${cat.id}`)"
          >
            <div class="category-icon">{{ cat.icon }}</div>
            <h4 class="category-name">{{ cat.name }}</h4>
            <p class="category-desc">{{ cat.desc }}</p>
            <span class="category-count">{{ cat.count }} 个服务</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门服务 -->
    <section class="services-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">热门植保服务</h2>
          <button class="view-all" @click="router.push('/service-list')">
            查看全部 <span>→</span>
          </button>
        </div>
        <div class="services-grid">
          <div 
            v-for="service in hotServices" 
            :key="service.id"
            class="service-card"
            @click="router.push(`/service-detail/${service.id}`)"
          >
            <div class="service-image">
              <img :src="service.image" :alt="service.title" />
              <div class="service-tags">
                <span v-for="tag in service.tags" :key="tag" class="tag">{{ tag }}</span>
              </div>
            </div>
            <div class="service-info">
              <h4 class="service-title">{{ service.title }}</h4>
              <div class="service-provider">
                <span class="provider-name">{{ service.provider }}</span>
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
                <span class="rating-stars">★★★★★</span>
                <span class="reviews">({{ service.reviews }}条评价)</span>
              </div>
              <div class="service-price">
                <span class="price">¥{{ service.price }}</span>
                <span class="unit">/{{ service.unit }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门设备 -->
    <section class="equipment-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">热门租赁设备</h2>
          <button class="view-all" @click="router.push('/equipment-list')">
            查看全部 <span>→</span>
          </button>
        </div>
        <div class="equipment-grid">
          <div 
            v-for="equip in hotEquipment" 
            :key="equip.id"
            class="equipment-card"
            @click="router.push(`/equipment-detail/${equip.id}`)"
          >
            <div class="equipment-image">
              <img :src="equip.image" :alt="equip.name" />
              <div class="equipment-tags">
                <span v-for="tag in equip.tags" :key="tag" class="tag">{{ tag }}</span>
              </div>
            </div>
            <div class="equipment-info">
              <h4 class="equipment-name">{{ equip.name }}</h4>
              <div class="equipment-owner">
                <span class="owner-name">{{ equip.owner }}</span>
              </div>
              <div class="equipment-specs">
                <span v-for="spec in equip.specs" :key="spec" class="spec">{{ spec }}</span>
              </div>
              <div class="equipment-meta">
                <span class="sales">已租 {{ formatNumber(equip.sales) }} 次</span>
                <span class="rating">好评 {{ equip.rating }}分</span>
              </div>
              <div class="equipment-price">
                <span class="price">¥{{ equip.price }}</span>
                <span class="unit">/{{ equip.unit }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 平台优势 -->
    <section class="advantages-section">
      <div class="container">
        <h2 class="section-title">为什么选择农翼通</h2>
        <div class="advantages-grid">
          <div class="advantage-card">
            <div class="advantage-icon">🔒</div>
            <h4 class="advantage-title">资金安全保障</h4>
            <p class="advantage-desc">平台担保交易，作业完成确认后才付款，资金安全有保障</p>
          </div>
          <div class="advantage-card">
            <div class="advantage-icon">✅</div>
            <h4 class="advantage-title">专业服务团队</h4>
            <p class="advantage-desc">所有飞手经过专业认证，持证上岗，服务质量可靠</p>
          </div>
          <div class="advantage-card">
            <div class="advantage-icon">💰</div>
            <h4 class="advantage-title">透明价格体系</h4>
            <p class="advantage-desc">明码标价，无隐形消费，多种服务套餐可选</p>
          </div>
          <div class="advantage-card">
            <div class="advantage-icon">🎯</div>
            <h4 class="advantage-title">智能匹配调度</h4>
            <p class="advantage-desc">AI 智能匹配飞手和地块，快速响应，准时作业</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部 CTA -->
    <section class="cta-section">
      <div class="container">
        <h2 class="cta-title">准备好开始智慧农业之旅了吗？</h2>
        <p class="cta-subtitle">加入农翼通，开启高效、精准的现代农业新时代</p>
        <div class="cta-buttons">
          <button class="btn btn-large btn-primary" @click="router.push('/register')">立即注册</button>
          <button class="btn btn-large btn-outline" @click="router.push('/contact')">联系客服</button>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-grid">
          <div class="footer-col">
            <div class="footer-logo">
              <span class="logo-icon">🚁</span>
              <span class="logo-text">农翼通</span>
            </div>
            <p class="footer-desc">连接农户、飞手与机主，一站式共享无人机服务平台</p>
            <div class="contact-info">
              <p>📱 客服微信：nongyitong_2024</p>
              <p>☎️ 客服电话：400-888-6666</p>
            </div>
          </div>
          <div class="footer-col">
            <h4>关于我们</h4>
            <ul>
              <li><a href="#">公司简介</a></li>
              <li><a href="#">团队介绍</a></li>
              <li><a href="#">新闻动态</a></li>
              <li><a href="#">加入我们</a></li>
            </ul>
          </div>
          <div class="footer-col">
            <h4>帮助中心</h4>
            <ul>
              <li><a href="#">使用指南</a></li>
              <li><a href="#">常见问题</a></li>
              <li><a href="#">客服咨询</a></li>
              <li><a href="#">投诉建议</a></li>
            </ul>
          </div>
          <div class="footer-col">
            <h4>法律条款</h4>
            <ul>
              <li><a href="#">隐私政策</a></li>
              <li><a href="#">服务协议</a></li>
              <li><a href="#">免责声明</a></li>
              <li><a href="#">知识产权</a></li>
            </ul>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2026 农翼通 版权所有</p>
          <p>ICP 证：京 ICP 备 XXXXXXXX 号</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 导航栏 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.navbar.scrolled {
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-left {
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

/* 搜索框 */
.nav-center {
  flex: 1;
  max-width: 600px;
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

/* 导航按钮 */
.nav-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.nav-btn {
  padding: 8px 16px;
  background: transparent;
  border: none;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: color 0.3s;
}

.nav-btn:hover {
  color: #10b981;
}

.nav-btn-outline {
  padding: 8px 20px;
  background: transparent;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
}

.nav-btn-outline:hover {
  border-color: #10b981;
  color: #10b981;
}

.nav-btn-primary {
  padding: 8px 20px;
  background: #10b981;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.nav-btn-primary:hover {
  background: #059669;
  transform: translateY(-1px);
}

/* 轮播图 */
.banner-section {
  margin-top: 60px;
  position: relative;
  overflow: hidden;
}

.banner-container {
  position: relative;
  height: 500px;
  overflow: hidden;
}

.banner-wrapper {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease;
}

.banner-item {
  min-width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-content {
  position: absolute;
  bottom: 80px;
  left: 80px;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.banner-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
}

.banner-subtitle {
  font-size: 24px;
  margin-bottom: 32px;
  opacity: 0.95;
}

.banner-btn {
  padding: 14px 40px;
  background: #10b981;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s;
}

.banner-btn:hover {
  background: #059669;
  transform: translateY(-2px);
}

.banner-indicators {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.indicator.active {
  background: #fff;
  width: 32px;
  border-radius: 6px;
}

.indicator:hover {
  background: rgba(255, 255, 255, 0.8);
}

/* 通用容器 */
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 60px 24px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 48px;
  color: #1f2937;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.section-header .section-title {
  margin-bottom: 0;
  text-align: left;
}

.view-all {
  background: none;
  border: none;
  color: #10b981;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.view-all:hover {
  color: #059669;
}

/* 角色入口 */
.roles-section {
  background: #fff;
}

.roles-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.role-card {
  background: linear-gradient(145deg, #fff 0%, #f9fafb 100%);
  border-radius: 20px;
  padding: 40px 32px;
  text-align: center;
  transition: all 0.4s;
  border: 2px solid transparent;
  cursor: pointer;
}

.role-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border-color: var(--role-color);
}

.role-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.role-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 12px;
}

.role-desc {
  font-size: 15px;
  color: #6b7280;
  margin-bottom: 8px;
  line-height: 1.6;
}

.role-subdesc {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 24px;
}

.role-btn {
  padding: 12px 32px;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.role-btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

/* 服务分类 */
.categories-section {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
}

.category-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
  border: 1px solid #e5e7eb;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
  border-color: #10b981;
}

.category-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.category-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.category-desc {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 12px;
}

.category-count {
  font-size: 13px;
  color: #10b981;
  font-weight: 500;
}

/* 热门服务 */
.services-section {
  background: #fff;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.service-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  border: 1px solid #e5e7eb;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

.service-image {
  position: relative;
  height: 200px;
  overflow: hidden;
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
  flex-wrap: wrap;
}

.tag {
  padding: 4px 10px;
  background: rgba(16, 185, 129, 0.9);
  color: #fff;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
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
}

.service-provider {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
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
  gap: 6px;
  margin-bottom: 12px;
}

.rating-score {
  font-size: 14px;
  font-weight: 600;
  color: #f59e0b;
}

.rating-stars {
  color: #fbbf24;
  font-size: 12px;
}

.reviews {
  font-size: 12px;
  color: #9ca3af;
}

.service-price {
  display: flex;
  align-items: baseline;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
}

.unit {
  font-size: 13px;
  color: #6b7280;
}

/* 热门设备 */
.equipment-section {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
}

.equipment-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.equipment-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  border: 1px solid #e5e7eb;
}

.equipment-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

.equipment-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: #f9fafb;
}

.equipment-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 20px;
  transition: transform 0.3s;
}

.equipment-card:hover .equipment-image img {
  transform: scale(1.1);
}

.equipment-tags {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 8px;
}

.equipment-info {
  padding: 16px;
}

.equipment-name {
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
}

.equipment-owner {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 10px;
}

.equipment-specs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.spec {
  padding: 4px 8px;
  background: #f3f4f6;
  color: #4b5563;
  border-radius: 6px;
  font-size: 12px;
}

.equipment-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 12px;
}

.equipment-price {
  display: flex;
  align-items: baseline;
}

.equipment-price .price {
  font-size: 22px;
  font-weight: 700;
  color: #ef4444;
}

.equipment-price .unit {
  font-size: 13px;
  color: #6b7280;
}

/* 平台优势 */
.advantages-section {
  background: #fff;
}

.advantages-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.advantage-card {
  text-align: center;
  padding: 32px 24px;
  background: #f9fafb;
  border-radius: 16px;
  transition: all 0.3s;
}

.advantage-card:hover {
  background: #fff;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  transform: translateY(-4px);
}

.advantage-icon {
  font-size: 56px;
  margin-bottom: 16px;
}

.advantage-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 12px;
}

.advantage-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.7;
}

/* CTA */
.cta-section {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
  text-align: center;
}

.cta-section .section-title {
  color: #fff;
}

.cta-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
}

.cta-subtitle {
  font-size: 18px;
  margin-bottom: 40px;
  opacity: 0.95;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.btn {
  padding: 16px 40px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-large {
  padding: 16px 48px;
  font-size: 18px;
}

.btn-primary {
  background: #fff;
  color: #10b981;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

.btn-outline {
  background: transparent;
  border: 2px solid #fff;
  color: #fff;
}

.btn-outline:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

/* 页脚 */
.footer {
  background: #1f2937;
  color: #fff;
  padding: 60px 24px 32px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 48px;
  margin-bottom: 40px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.footer-logo .logo-icon {
  font-size: 32px;
}

.footer-logo .logo-text {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
}

.footer-desc {
  color: #9ca3af;
  font-size: 14px;
  line-height: 1.7;
  margin-bottom: 20px;
}

.contact-info p {
  color: #9ca3af;
  font-size: 14px;
  margin-bottom: 8px;
}

.footer-col h4 {
  font-size: 16px;
  margin-bottom: 20px;
  font-weight: 600;
}

.footer-col ul {
  list-style: none;
  padding: 0;
}

.footer-col li {
  margin-bottom: 12px;
}

.footer-col a {
  color: #9ca3af;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-col a:hover {
  color: #fff;
}

.footer-bottom {
  border-top: 1px solid #374151;
  padding-top: 32px;
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #6b7280;
}

/* 响应式 */
@media (max-width: 1200px) {
  .services-grid,
  .equipment-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .categories-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .advantages-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 992px) {
  .roles-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .services-grid,
  .equipment-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .banner-title {
    font-size: 36px;
  }
  
  .banner-subtitle {
    font-size: 20px;
  }
}

@media (max-width: 768px) {
  .nav-center {
    display: none;
  }
  
  .roles-grid,
  .categories-grid,
  .services-grid,
  .equipment-grid,
  .advantages-grid {
    grid-template-columns: 1fr;
  }
  
  .banner-container {
    height: 300px;
  }
  
  .banner-title {
    font-size: 28px;
  }
  
  .banner-subtitle {
    font-size: 16px;
  }
  
  .banner-btn {
    padding: 12px 24px;
    font-size: 16px;
  }
  
  .footer-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }
  
  .footer-bottom {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
}
</style>
