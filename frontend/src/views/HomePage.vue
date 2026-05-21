<script setup>import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const navScrolled = ref(false);
const counters = ref({
 drones: 0,
 farmers: 0,
 area: 0
});
const targetCounters = {
 drones: 24000,
 farmers: 3800,
 area: 1200000
};
const roles = [
 { id: 'farmer', name: '农户', icon: '🌾', desc: '发布作业需求', subDesc: '简单填写地块信息，系统智能匹配飞手', tag: '免采购设备' },
 { id: 'flyer', name: '飞手', icon: '🚁', desc: '接单赚收益', subDesc: '利用技能和时间，获取稳定作业收入', tag: '可租用设备' },
 { id: 'owner', name: '机主', icon: '🏭', desc: '共享闲置设备', subDesc: '将无人机出租给飞手，持续产生收益', tag: '设备托管无忧' }
];
const steps = [
 { id: 1, title: '选择服务', desc: '喷洒/巡检', icon: '✓' },
 { id: 2, title: '发布需求', desc: '填写地块、时间、预算', icon: '📋' },
 { id: 3, title: '飞手接单', desc: '系统匹配或自由抢单', icon: '🤝' },
 { id: 4, title: '完成结算', desc: '作业完成后支付', icon: '💰' }
];
const devices = [
 { id: 1, name: '大疆 T30', price: 280, specs: ['载重30kg', '续航28分钟'], tag: '热租', image: 'https://ts1.tc.mm.bing.net/th/id/R-C.c3ce29bb0085eadcf18f3d6d674df03d?rik=R6VXpAKd%2b3IZMg&riu=http%3a%2f%2fimg95.699pic.com%2fphoto%2f50044%2f2804.jpg_wh860.jpg&ehk=sPSLK6QxnxquwF9nyKHnocakgeJSHP0TA1r727vk3T8%3d&risl=&pid=ImgRaw&r=0' },
 { id: 2, name: '极飞 P100', price: 320, specs: ['载重100kg', '续航35分钟'], tag: '热租', image: 'https://pic.52112.com/180309/180309_39/oS5h7LfTb6_small.jpg' },
 { id: 3, name: '大疆 T16', price: 180, specs: ['载重16kg', '续航25分钟'], image: 'https://www.efuav.com/wp-content/uploads/2025/06/1-65.jpg' },
 { id: 4, name: '极飞 P80', price: 260, specs: ['载重80kg', '续航30分钟'], image: 'https://ts2.tc.mm.bing.net/th/id/OIP-C.tqclm93sfUD42PK5qYeMnQHaIs?rs=1&pid=ImgDetMain&o=7&rm=3' }
];
const testimonials = [
 { id: 1, name: '王农户', role: '农户', avatar: '👨‍🌾', rating: 5, content: '以前买无人机花了好几万，现在发布需求半天就有飞手接单，太方便了！' },
 { id: 2, name: '李飞手', role: '飞手', avatar: '👨‍✈️', rating: 5, content: '平台派单稳定，收入有保障，还能租用设备，不用自己买无人机。', earnings: '月入8000+' },
 { id: 3, name: '张机主', role: '机主', avatar: '👨‍🔧', rating: 5, content: '无人机闲置时租出去，每月都有额外收入，省心又赚钱。', earnings: '月收益3000+' }
];
const advantages = [
 { icon: '🔒', title: '资金安全保障', desc: '平台担保，作业完成才结算，资金安全有保障' },
 { icon: '📍', title: '实时作业跟踪', desc: '地图查看飞手位置、作业进度，随时掌握动态' },
 { icon: '🌦️', title: '天气智能预警', desc: '对接气象数据，恶劣天气自动停单，保障作业安全' }
];
const navItems = [
 { name: '首页', href: '#hero' },
 { name: '无人机设备', href: '#devices' },
 { name: '服务流程', href: '#process' },
 { name: '飞手风采', href: '#testimonials' },
 { name: '帮助', href: '#footer' }
];
const startCounter = () => {
 const duration = 2000;
 const steps = 60;
 const interval = duration / steps;
 let currentStep = 0;
 const timer = setInterval(() => {
 currentStep++;
 const progress = currentStep / steps;
 counters.value.drones = Math.floor(targetCounters.drones * progress);
 counters.value.farmers = Math.floor(targetCounters.farmers * progress);
 counters.value.area = Math.floor(targetCounters.area * progress);
 if (currentStep >= steps) {
 clearInterval(timer);
 counters.value = { ...targetCounters };
 }
 }, interval);
};
const handleScroll = () => {
 navScrolled.value = window.scrollY > 50;
};
onMounted(() => {
 window.addEventListener('scroll', handleScroll);
 setTimeout(startCounter, 500);
});
onUnmounted(() => {
 window.removeEventListener('scroll', handleScroll);
});
const scrollToSection = (href) => {
 const element = document.querySelector(href);
 if (element) {
 element.scrollIntoView({ behavior: 'smooth' });
 }
};
</script>

<template>
  <div class="home-page">
    <nav 
      class="navbar" 
      :class="{ scrolled: navScrolled }"
    >
      <div class="nav-content">
        <div class="nav-left">
          <div class="logo">
            <span class="logo-icon">🚁</span>
            <span class="logo-text">农翼通</span>
          </div>
        </div>
        <div class="nav-center">
          <button 
            v-for="item in navItems" 
            :key="item.name"
            class="nav-link"
            @click="scrollToSection(item.href)"
          >
            {{ item.name }}
          </button>
        </div>
        <div class="nav-right">
          <button class="btn btn-outline" @click="router.push('/login')">登录</button>
          <button class="btn btn-primary" @click="router.push('/register')">注册</button>
        </div>
      </div>
    </nav>

    <section id="hero" class="hero-section">
      <div class="hero-bg">
        <img src="https://picsum.photos/1920/1080?random=10" alt="农业无人机" />
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">让农业无人机作业像打车一样简单</h1>
        <p class="hero-subtitle">连接农户、飞手与机主，一站式共享无人机服务</p>
        <div class="hero-buttons">
          <button class="btn btn-large btn-gradient" @click="router.push('/login')">农户发布需求</button>
          <button class="btn btn-large btn-white" @click="router.push('/login')">飞手接单赚钱</button>
        </div>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-number">{{ counters.drones.toLocaleString() }}+</span>
            <span class="stat-label">专业飞手</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">{{ counters.farmers.toLocaleString() }}+</span>
            <span class="stat-label">可用设备</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">{{ (counters.area / 10000).toFixed(0) }}万</span>
            <span class="stat-label">服务农田(亩)</span>
          </div>
        </div>
      </div>
    </section>

    <section class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon-wrap">🚁</div>
            <div class="stat-value">{{ counters.drones.toLocaleString() }}</div>
            <div class="stat-label">无人机架次</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon-wrap">👨‍🌾</div>
            <div class="stat-value">{{ counters.farmers.toLocaleString() }}</div>
            <div class="stat-label">服务农户</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon-wrap">🌾</div>
            <div class="stat-value">{{ (counters.area / 10000).toFixed(0) }}万</div>
            <div class="stat-label">作业面积(亩)</div>
          </div>
        </div>
        <p class="stats-caption">平台累计数据</p>
      </div>
    </section>

    <section class="roles-section">
      <div class="container">
        <h2 class="section-title">一个平台，三种角色，各取所需</h2>
        <div class="roles-grid">
          <div 
            v-for="role in roles" 
            :key="role.id"
            class="role-card"
          >
            <div class="role-icon">{{ role.icon }}</div>
            <h3 class="role-title">{{ role.desc }}</h3>
            <p class="role-desc">{{ role.subDesc }}</p>
            <span class="role-tag">{{ role.tag }}</span>
            <button class="btn btn-primary btn-block" @click="router.push('/register')">立即加入</button>
          </div>
        </div>
      </div>
    </section>

    <section id="process" class="process-section">
      <div class="container">
        <h2 class="section-title">简单四步，完成农业作业</h2>
        <div class="process-steps">
          <div 
            v-for="(step, index) in steps" 
            :key="step.id"
            class="process-step"
          >
            <div class="step-number">{{ step.id }}</div>
            <div class="step-icon">{{ step.icon }}</div>
            <h4 class="step-title">{{ step.title }}</h4>
            <p class="step-desc">{{ step.desc }}</p>
            <div v-if="index < steps.length - 1" class="step-connector"></div>
          </div>
        </div>
      </div>
    </section>

    <section id="devices" class="devices-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">平台支持的无人机设备</h2>
          <button class="view-all">查看全部设备 →</button>
        </div>
        <div class="devices-grid">
          <div 
            v-for="device in devices" 
            :key="device.id"
            class="device-card"
          >
            <div class="device-image">
              <img :src="device.image" :alt="device.name" />
              <span v-if="device.tag" class="device-tag">{{ device.tag }}</span>
            </div>
            <div class="device-info">
              <h4 class="device-name">{{ device.name }}</h4>
              <div class="device-specs">
                <span v-for="spec in device.specs" :key="spec" class="spec-tag">{{ spec }}</span>
              </div>
              <div class="device-price">
                <span class="price-label">日租价</span>
                <span class="price-value">¥{{ device.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="testimonials" class="testimonials-section">
      <div class="container">
        <h2 class="section-title">他们已经在使用农翼通</h2>
        <div class="testimonials-grid">
          <div 
            v-for="item in testimonials" 
            :key="item.id"
            class="testimonial-card"
          >
            <div class="testimonial-header">
              <span class="testimonial-avatar">{{ item.avatar }}</span>
              <div class="testimonial-info">
                <span class="testimonial-name">{{ item.name }}</span>
                <span class="testimonial-role">{{ item.role }}</span>
              </div>
              <div class="testimonial-rating">
                <span v-for="i in item.rating" :key="i">★</span>
              </div>
            </div>
            <p class="testimonial-content">{{ item.content }}</p>
            <div v-if="item.earnings" class="testimonial-earnings">{{ item.earnings }}</div>
          </div>
        </div>
      </div>
    </section>

    <section class="advantages-section">
      <div class="container">
        <h2 class="section-title">为什么选择农翼通</h2>
        <div class="advantages-grid">
          <div 
            v-for="item in advantages" 
            :key="item.title"
            class="advantage-card"
          >
            <div class="advantage-icon">{{ item.icon }}</div>
            <h4 class="advantage-title">{{ item.title }}</h4>
            <p class="advantage-desc">{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-section">
      <div class="container">
        <h2 class="cta-title">准备好开始了吗？</h2>
        <p class="cta-subtitle">加入农翼通，开启智慧农业新时代</p>
        <div class="cta-buttons">
          <button class="btn btn-large btn-gradient" @click="router.push('/register')">立即注册</button>
          <button class="btn btn-large btn-outline">联系客服</button>
        </div>
        <div class="cta-links">
          <a href="#">常见问题</a>
          <a href="#">下载APP</a>
          <a href="#">商务合作</a>
        </div>
      </div>
    </section>

    <footer id="footer" class="footer">
      <div class="container">
        <div class="footer-grid">
          <div class="footer-col">
            <div class="footer-logo">
              <span class="logo-icon">🚁</span>
              <span class="logo-text">农翼通</span>
            </div>
            <p class="footer-desc">连接农户、飞手与机主，一站式共享无人机服务平台</p>
          </div>
          <div class="footer-col">
            <h4>关于我们</h4>
            <ul>
              <li><a href="#">公司简介</a></li>
              <li><a href="#">团队介绍</a></li>
              <li><a href="#">新闻动态</a></li>
            </ul>
          </div>
          <div class="footer-col">
            <h4>帮助中心</h4>
            <ul>
              <li><a href="#">使用指南</a></li>
              <li><a href="#">常见问题</a></li>
              <li><a href="#">联系客服</a></li>
            </ul>
          </div>
          <div class="footer-col">
            <h4>法律条款</h4>
            <ul>
              <li><a href="#">隐私政策</a></li>
              <li><a href="#">服务协议</a></li>
              <li><a href="#">免责声明</a></li>
            </ul>
          </div>
        </div>
        <div class="footer-bottom">
          <p>客服微信：nongyitong_2024</p>
          <p>客服电话：400-888-6666</p>
          <p>&copy; 2026 农翼通 版权所有</p>
        </div>
      </div>
    </footer>


  </div>
</template>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0fdf4 0%, #f8fafc 50%, #f0f9ff 100%);
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: transparent;
  transition: all 0.3s ease;
  padding: 16px 0;
}

.navbar.scrolled {
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.5);
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #2E8B57;
}

.navbar.scrolled .logo-text {
  color: #2E8B57;
}

.nav-center {
  display: flex;
  gap: 32px;
}

.nav-link {
  background: none;
  border: none;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  transition: color 0.3s;
}

.navbar.scrolled .nav-link {
  color: #333;
}

.nav-link:hover {
  color: #fff;
}

.navbar.scrolled .nav-link:hover {
  color: #2E8B57;
}

.nav-right {
  display: flex;
  gap: 12px;
}

.btn {
  padding: 10px 24px;
  border-radius: 40px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.btn:hover::before {
  left: 100%;
}

.btn-outline {
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.8);
  color: #fff;
}

.btn-outline:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: #fff;
  transform: translateY(-2px);
}

.navbar.scrolled .btn-outline {
  border-color: #2E8B57;
  color: #2E8B57;
}

.navbar.scrolled .btn-outline:hover {
  background: linear-gradient(135deg, #2E8B57 0%, #1E6B43 100%);
  color: #fff;
  border-color: transparent;
}

.btn-primary {
  background: linear-gradient(135deg, #2E8B57 0%, #1E6B43 100%);
  color: #fff;
  box-shadow: 0 4px 15px rgba(46, 139, 87, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 12px 30px rgba(46, 139, 87, 0.45);
}

.btn-large {
  padding: 16px 40px;
  font-size: 16px;
}

.btn-gradient {
  background: linear-gradient(135deg, #2E8B57 0%, #16A34A 50%, #3B82F6 100%);
  color: #fff;
  box-shadow: 0 6px 20px rgba(46, 139, 87, 0.4);
}

.btn-gradient:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 35px rgba(46, 139, 87, 0.5);
  background: linear-gradient(135deg, #16A34A 0%, #22C55E 50%, #3B82F6 100%);
}

.btn-white {
  background: rgba(255, 255, 255, 0.95);
  color: #2E8B57;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.btn-white:hover {
  background: #fff;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.btn-block {
  width: 100%;
}

.hero-section {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.hero-bg img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.7) 0%, rgba(0, 0, 0, 0.4) 100%);
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
  max-width: 900px;
  padding: 0 24px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 20px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.hero-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-bottom: 60px;
}

.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 48px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #4ADE80;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.3);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 24px;
}

.stats-section {
  background: #fff;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.stat-card {
  text-align: center;
  padding: 36px;
  background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 20px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(46, 139, 87, 0.1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(46, 139, 87, 0.05) 0%, transparent 70%);
  transition: transform 0.4s;
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(46, 139, 87, 0.15);
  border-color: rgba(46, 139, 87, 0.2);
}

.stat-card:hover::before {
  transform: scale(2);
}

.stat-icon-wrap {
  font-size: 48px;
  margin-bottom: 20px;
  filter: drop-shadow(0 4px 8px rgba(46, 139, 87, 0.2));
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #2E8B57 0%, #1E6B43 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 15px;
  color: #64748b;
  font-weight: 500;
}

.stats-caption {
  text-align: center;
  color: #999;
  font-size: 14px;
  margin-top: 24px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 48px;
  color: #1f2937;
}

.roles-section {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 50%, #f0f9ff 100%);
  position: relative;
  overflow: hidden;
}

.roles-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: -10%;
  width: 60%;
  height: 60%;
  background: radial-gradient(ellipse at center, rgba(46, 139, 87, 0.08) 0%, transparent 70%);
}

.roles-section::after {
  content: '';
  position: absolute;
  bottom: 0;
  right: -10%;
  width: 60%;
  height: 60%;
  background: radial-gradient(ellipse at center, rgba(59, 130, 246, 0.08) 0%, transparent 70%);
}

.roles-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
  position: relative;
  z-index: 1;
}

.role-card {
  background: linear-gradient(145deg, #ffffff 0%, #fafafa 100%);
  border-radius: 24px;
  padding: 36px;
  text-align: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(46, 139, 87, 0.1);
  position: relative;
  overflow: hidden;
}

.role-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, transparent, #2E8B57, transparent);
  opacity: 0;
  transition: opacity 0.4s;
}

.role-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 25px 50px rgba(46, 139, 87, 0.18);
  border-color: rgba(46, 139, 87, 0.2);
}

.role-card:hover::before {
  opacity: 1;
}

.role-icon {
  font-size: 56px;
  margin-bottom: 20px;
  filter: drop-shadow(0 8px 16px rgba(46, 139, 87, 0.15));
  transition: transform 0.4s;
}

.role-card:hover .role-icon {
  transform: scale(1.15);
}

.role-title {
  font-size: 22px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 10px;
}

.role-desc {
  font-size: 15px;
  color: #64748b;
  margin-bottom: 20px;
  line-height: 1.6;
}

.role-tag {
  display: inline-block;
  padding: 6px 16px;
  background: linear-gradient(135deg, rgba(46, 139, 87, 0.1) 0%, rgba(46, 139, 87, 0.05) 100%);
  color: #2E8B57;
  border-radius: 24px;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 24px;
  border: 1px solid rgba(46, 139, 87, 0.15);
}

.process-section {
  background: #fff;
}

.process-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
}

.process-step {
  text-align: center;
  flex: 1;
  position: relative;
}

.step-number {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #2E8B57 0%, #3B82F6 100%);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  margin: 0 auto 16px;
}

.step-icon {
  font-size: 28px;
  margin-bottom: 12px;
}

.step-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.step-desc {
  font-size: 13px;
  color: #666;
}

.step-connector {
  position: absolute;
  top: 24px;
  left: 50%;
  width: calc(100% - 24px);
  height: 2px;
  background: linear-gradient(90deg, #2E8B57 0%, #3B82F6 100%);
  z-index: -1;
}

.devices-section {
  background: #f8fafc;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.view-all {
  color: #2E8B57;
  font-size: 14px;
  font-weight: 500;
  background: none;
  border: none;
  cursor: pointer;
}

.devices-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.device-card {
  background: linear-gradient(145deg, #ffffff 0%, #fafafa 100%);
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.device-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border-color: rgba(46, 139, 87, 0.15);
}

.device-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.device-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 20px;
  transition: transform 0.4s;
}

.device-card:hover .device-image img {
  transform: scale(1.1);
}

.device-tag {
  position: absolute;
  top: 14px;
  right: 14px;
  padding: 5px 12px;
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  color: #fff;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.device-info {
  padding: 20px;
}

.device-name {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 10px;
  color: #1f2937;
}

.device-specs {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.spec-tag {
  padding: 4px 10px;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #475569;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
}

.device-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price-label {
  font-size: 13px;
  color: #9ca3af;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.testimonials-section {
  background: #fff;
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.testimonial-card {
  background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 20px;
  padding: 28px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}

.testimonial-card::before {
  content: '"';
  position: absolute;
  top: -10px;
  right: 10px;
  font-size: 80px;
  color: rgba(46, 139, 87, 0.05);
  font-family: serif;
}

.testimonial-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(46, 139, 87, 0.12);
  border-color: rgba(46, 139, 87, 0.1);
}

.testimonial-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 20px;
}

.testimonial-avatar {
  font-size: 40px;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
}

.testimonial-info {
  flex: 1;
}

.testimonial-name {
  font-weight: 700;
  font-size: 16px;
  color: #1f2937;
}

.testimonial-role {
  font-size: 13px;
  color: #9ca3af;
}

.testimonial-rating {
  color: #F59E0B;
  font-size: 16px;
}

.testimonial-content {
  font-size: 15px;
  color: #4b5563;
  line-height: 1.7;
  margin-bottom: 16px;
}

.testimonial-earnings {
  font-size: 15px;
  font-weight: 700;
  background: linear-gradient(135deg, #2E8B57 0%, #16A34A 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.advantages-section {
  background: linear-gradient(135deg, #2E8B57 0%, #3B82F6 100%);
  color: #fff;
}

.advantages-section .section-title {
  color: #fff;
}

.advantages-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

.advantage-card {
  text-align: center;
  padding: 32px;
}

.advantage-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.advantage-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.advantage-desc {
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.6;
}

.cta-section {
  background: #fff;
  text-align: center;
  padding: 80px 24px;
}

.cta-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #1f2937;
}

.cta-subtitle {
  font-size: 16px;
  color: #666;
  margin-bottom: 40px;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 32px;
}

.cta-links {
  display: flex;
  justify-content: center;
  gap: 32px;
}

.cta-links a {
  color: #666;
  font-size: 14px;
  text-decoration: none;
}

.cta-links a:hover {
  color: #2E8B57;
}

.footer {
  background: #1f2937;
  color: #fff;
  padding: 60px 24px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 48px;
  margin-bottom: 40px;
}

.footer-col h4 {
  font-size: 16px;
  margin-bottom: 16px;
}

.footer-col ul {
  list-style: none;
  padding: 0;
}

.footer-col li {
  margin-bottom: 8px;
}

.footer-col a {
  color: #9ca3af;
  text-decoration: none;
  font-size: 14px;
}

.footer-col a:hover {
  color: #fff;
}

.footer-desc {
  color: #9ca3af;
  font-size: 14px;
  margin-top: 12px;
  line-height: 1.6;
}

.footer-bottom {
  border-top: 1px solid #374151;
  padding-top: 24px;
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #9ca3af;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: #fff;
  border-radius: 24px;
  width: 480px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  animation: modalIn 0.3s ease;
}

@keyframes modalIn {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
  padding: 4px;
}

.modal-close:hover {
  color: #333;
}

.modal-body {
  padding: 24px;
}

.role-selector {
  margin-bottom: 24px;
}

.role-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
  display: block;
}

.role-buttons {
  display: flex;
  gap: 12px;
}

.role-btn {
  flex: 1;
  padding: 12px;
  background: #f8fafc;
  border: 2px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.role-btn.active {
  background: rgba(46, 139, 87, 0.1);
  border-color: #2E8B57;
}

.role-icon {
  font-size: 24px;
}

.role-name {
  font-size: 13px;
  font-weight: 500;
}

.role-btn.active .role-name {
  color: #2E8B57;
}

.login-form, .register-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 13px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #2E8B57;
}

.code-input-wrap {
  display: flex;
  gap: 12px;
}

.code-input-wrap input {
  flex: 1;
}

.send-code-btn {
  padding: 12px 20px;
  background: #f8fafc;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
}

.send-code-btn:hover {
  background: #f1f5f9;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.checkbox {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #666;
}

.checkbox input {
  width: auto;
  padding: 0;
}

.forgot-link, .register-link, .login-link {
  color: #2E8B57;
  text-decoration: none;
  font-size: 13px;
}

.register-link, .login-link {
  text-align: center;
  margin-top: 8px;
}

@media (max-width: 1024px) {
  .hero-title {
    font-size: 36px;
  }
  
  .stats-grid, .roles-grid, .testimonials-grid, .advantages-grid, .devices-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .footer-grid {
    grid-template-columns: 1fr 1fr;
    gap: 32px;
  }
}

@media (max-width: 768px) {
  .nav-center {
    display: none;
  }
  
  .hero-title {
    font-size: 28px;
  }
  
  .hero-subtitle {
    font-size: 16px;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 24px;
  }
  
  .stat-divider {
    display: none;
  }
  
  .stats-grid, .roles-grid, .testimonials-grid, .advantages-grid, .devices-grid {
    grid-template-columns: 1fr;
  }
  
  .process-steps {
    flex-direction: column;
    gap: 24px;
  }
  
  .step-connector {
    display: none;
  }
  
  .footer-grid {
    grid-template-columns: 1fr;
  }
  
  .footer-bottom {
    flex-direction: column;
    gap: 8px;
    text-align: center;
  }
}
</style>