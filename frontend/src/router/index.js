import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/home/index.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/personal',
    name: 'personal',
    component: () => import('@/views/personal/index.vue'),
    meta: { title: '个人业务' }
  },
  {
    path: '/credit',
    name: 'credit',
    component: () => import('@/views/credit/index.vue'),
    meta: { title: '信用卡' }
  },
  {
    path: '/company',
    name: 'company',
    component: () => import('@/views/company/index.vue'),
    meta: { title: '公司金融' }
  },
  {
    path: '/puhui',
    name: 'puhui',
    component: () => import('@/views/puhui/index.vue'),
    meta: { title: '普惠金融' }
  },
  {
    path: '/branches',
    name: 'branches',
    component: () => import('@/views/branches/index.vue'),
    meta: { title: '网点查询' }
  },
  {
    path: '/info',
    name: 'info',
    component: () => import('@/views/info/index.vue'),
    meta: { title: '信息公开' }
  },
  {
    path: '/news',
    name: 'news',
    component: () => import('@/views/news/index.vue'),
    meta: { title: '新闻公告' }
  },
  {
    path: '/news/detail',
    name: 'newsDetail',
    component: () => import('@/views/news/detail.vue'),
    meta: { title: '新闻详情' }
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/forgot-password',
    name: 'forgotPassword',
    component: () => import('@/views/forgot/index.vue'),
    meta: { title: '忘记密码' }
  },
  {
    path: '/apply/card',
    name: 'applyCard',
    component: () => import('@/views/apply/index.vue'),
    meta: { title: '信用卡申请', needLogin: true }
  },
  {
    path: '/apply/loan',
    name: 'applyLoan',
    component: () => import('@/views/apply/index.vue'),
    meta: { title: '贷款申请', needLogin: true }
  },
  {
    path: '/payment',
    name: 'payment',
    component: () => import('@/views/payment/index.vue'),
    meta: { title: '生活缴费', needLogin: true }
  },
  {
    path: '/transfer',
    name: 'transfer',
    component: () => import('@/views/transfer/index.vue'),
    meta: { title: '转账汇款', needLogin: true }
  },
  {
    path: '/product/detail',
    name: 'productDetail',
    component: () => import('@/views/product/index.vue'),
    meta: { title: '产品详情' }
  },
  {
    path: '/calculator',
    name: 'calculator',
    component: () => import('@/views/calculator/index.vue'),
    meta: { title: '贷款计算器' }
  },
  {
    path: '/user/center',
    name: 'userCenter',
    component: () => import('@/views/usercenter/index.vue'),
    meta: { title: '个人中心', needLogin: true }
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('@/views/admin/index.vue'),
    meta: { title: '后台管理', needLogin: true, needAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  // 动态设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title + ' - 网银系统'
  }
  // 需要登录但未登录
  if (to.meta.needLogin && !userStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }
  // 需要管理员但角色不是管理员
  if (to.meta.needAdmin && !userStore.isAdmin) {
    next({ name: 'home' })
    return
  }
  next()
})

export default router
