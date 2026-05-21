import { createRouter, createWebHistory } from 'vue-router'

export const ROLES = {
  FARMER: 'farmer',
  FLYER: 'flyer',
  OWNER: 'owner'
}

export const roleRoutes = {
  [ROLES.FARMER]: [
    {
      path: '/dashboard',
      name: 'FarmerDashboard',
      component: () => import('../views/farmer/Dashboard.vue'),
      meta: { requiresAuth: true, role: ROLES.FARMER, title: '农户仪表盘' }
    },
    {
      path: '/profile',
      name: 'FarmerProfile',
      component: () => import('../views/farmer/Profile.vue'),
      meta: { requiresAuth: true, role: ROLES.FARMER, title: '个人资料' }
    },
    {
      path: '/demand-list',
      name: 'FarmerDemandList',
      component: () => import('../views/farmer/DemandList.vue'),
      meta: { requiresAuth: true, role: ROLES.FARMER, title: '需求列表' }
    },
    {
      path: '/publish-demand',
      name: 'FarmerPublishDemand',
      component: () => import('../views/farmer/PublishDemand.vue'),
      meta: { requiresAuth: true, role: ROLES.FARMER, title: '发布需求' }
    },
    {
      path: '/my-demands',
      name: 'FarmerMyDemands',
      component: () => import('../views/farmer/MyDemands.vue'),
      meta: { requiresAuth: true, role: ROLES.FARMER, title: '我的需求' }
    },
    {
      path: '/demand-detail/:id',
      name: 'FarmerDemandDetail',
      component: () => import('../views/farmer/DemandDetail.vue'),
      meta: { requiresAuth: true, role: ROLES.FARMER, title: '需求详情' }
    },
    {
      path: '/chat',
      name: 'FarmerChat',
      component: () => import('../views/Chat.vue'),
      meta: { requiresAuth: true, role: ROLES.FARMER, title: '消息中心' }
    },
    {
      path: '/wallet',
      name: 'FarmerWallet',
      component: () => import('../views/owner/Wallet.vue'),
      meta: { requiresAuth: true, role: ROLES.FARMER, title: '钱包' }
    }
  ],
  [ROLES.FLYER]: [
    {
      path: '/dashboard',
      name: 'FlyerDashboard',
      component: () => import('../views/flyer/Dashboard.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '飞手仪表盘' }
    },
    {
      path: '/profile',
      name: 'FlyerProfile',
      component: () => import('../views/flyer/Profile.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '个人资料' }
    },
    {
      path: '/qualification',
      name: 'FlyerQualification',
      component: () => import('../views/flyer/Qualification.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '资质管理' }
    },
    {
      path: '/order-list',
      name: 'FlyerOrderList',
      component: () => import('../views/flyer/OrderList.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '订单列表' }
    },
    {
      path: '/accept-order',
      name: 'FlyerAcceptOrder',
      component: () => import('../views/flyer/AcceptOrder.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '接单中心' }
    },
    {
      path: '/my-orders',
      name: 'FlyerMyOrders',
      component: () => import('../views/flyer/MyOrders.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '我的订单' }
    },
    {
      path: '/order-detail/:id',
      name: 'FlyerOrderDetail',
      component: () => import('../views/flyer/OrderDetail.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '订单详情' }
    },
    {
      path: '/equipment',
      name: 'FlyerEquipment',
      component: () => import('../views/flyer/Equipment.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '设备租用' }
    },
    {
      path: '/chat',
      name: 'FlyerChat',
      component: () => import('../views/Chat.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '消息中心' }
    },
    {
      path: '/wallet',
      name: 'FlyerWallet',
      component: () => import('../views/owner/Wallet.vue'),
      meta: { requiresAuth: true, role: ROLES.FLYER, title: '钱包' }
    }
  ],
  [ROLES.OWNER]: [
    {
      path: '/dashboard',
      name: 'OwnerDashboard',
      component: () => import('../views/owner/Dashboard.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '机主仪表盘' }
    },
    {
      path: '/profile',
      name: 'OwnerProfile',
      component: () => import('../views/owner/Profile.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '个人资料' }
    },
    {
      path: '/devices',
      name: 'OwnerDevices',
      component: () => import('../views/owner/Devices.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '设备管理' }
    },
    {
      path: '/devices/add',
      name: 'OwnerAddDevice',
      component: () => import('../views/owner/AddDevice.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '添加设备' }
    },
    {
      path: '/device/:id/edit',
      name: 'OwnerEditDevice',
      component: () => import('../views/owner/EditDevice.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '编辑设备' }
    },
    {
      path: '/device/:deviceId',
      name: 'OwnerDeviceDetail',
      component: () => import('../views/owner/DeviceDetail.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '设备详情' }
    },
    {
      path: '/device-activities',
      name: 'OwnerDeviceActivities',
      component: () => import('../views/owner/DeviceActivities.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '设备动态' }
    },
    {
      path: '/rental-apply',
      name: 'OwnerRentalApply',
      component: () => import('../views/owner/RentalApply.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '租借申请' }
    },
    {
      path: '/chat',
      name: 'OwnerChat',
      component: () => import('../views/Chat.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '消息中心' }
    },
    {
      path: '/wallet',
      name: 'OwnerWallet',
      component: () => import('../views/owner/Wallet.vue'),
      meta: { requiresAuth: true, role: ROLES.OWNER, title: '钱包' }
    }
  ]
}

const publicRoutes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomePage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/service-list',
    name: 'ServiceList',
    component: () => import('../views/ServiceList.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/service-detail/:id',
    name: 'ServiceDetail',
    component: () => import('../views/ServiceDetail.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/equipment-list',
    name: 'EquipmentList',
    component: () => import('../views/farmer/DemandList.vue'), // 临时用需求列表代替，后续可创建新页面
    meta: { requiresAuth: false }
  },
  {
    path: '/test',
    name: 'Test',
    component: {
      template: `<div style="padding: 20px; text-align: center;"><h1>测试页面</h1><p>这个页面用于测试路由功能是否正常工作。</p><a href="/register">前往注册页面</a></div>`
    },
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: publicRoutes
})

export function getRoleRoutes(role) {
  return roleRoutes[role] || []
}

export function resetRouter() {
  const newRouter = createRouter({
    history: createWebHistory(),
    routes: publicRoutes
  })
  router.matcher = newRouter.matcher
}

export function addRoleRoutes(role) {
  const routes = getRoleRoutes(role)
  routes.forEach(route => {
    const existingRoute = router.getRoutes().find(r => r.path === route.path)
    if (!existingRoute) {
      router.addRoute(route)
    }
  })
}

router.beforeEach((to, from, next) => {
  if (to.path === '/test' || to.path === '/login' || to.path === '/register' || to.path === '/') {
    next()
    return
  }

  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (!token || !role) {
    next('/login')
    return
  }

  const matchedRoute = router.getRoutes().find(r => r.path === to.path)
  if (!matchedRoute) {
    addRoleRoutes(role)
    next({ ...to, replace: true })
    return
  }

  if (to.meta.role && to.meta.role !== role) {
    next('/dashboard')
    return
  }

  next()
})

export default router