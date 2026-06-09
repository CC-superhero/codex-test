import Vue from 'vue'
import VueRouter from 'vue-router'

import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Users from '../views/Users.vue'
import UserDetail from '../views/UserDetail.vue'
import Products from '../views/Products.vue'
import ProductForm from '../views/ProductForm.vue'
import CouponGrant from '../views/CouponGrant.vue'
import PointsGrant from '../views/PointsGrant.vue'
import Settings from '../views/Settings.vue'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', component: Login, meta: { title: '管理员登录' } },
  { path: '/dashboard', component: Dashboard, meta: { title: '仪表盘' } },
  { path: '/users', component: Users, meta: { title: '用户管理' } },
  { path: '/user/:id', component: UserDetail, meta: { title: '用户详情' } },
  { path: '/products', component: Products, meta: { title: '商品管理' } },
  { path: '/product/form', component: ProductForm, meta: { title: '发布商品' } },
  { path: '/product/form/:id', component: ProductForm, meta: { title: '编辑商品' } },
  { path: '/coupon-grant', component: CouponGrant, meta: { title: '发放优惠券' } },
  { path: '/points-grant', component: PointsGrant, meta: { title: '发放积分' } },
  { path: '/settings', component: Settings, meta: { title: '系统配置' } }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

router.beforeEach((to, from, next) => {
  const token = store.getters.token
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

router.afterEach((to) => {
  if (to.path !== '/login') {
    store.dispatch('addView', {
      path: to.path,
      title: (to.meta && to.meta.title) || '',
      query: to.query,
      params: to.params
    })
  }
})

export default router
