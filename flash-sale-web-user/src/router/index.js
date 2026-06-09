import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import ProductDetail from '../views/ProductDetail.vue'
import Pay from '../views/Pay.vue'
import Mine from '../views/Mine.vue'
import Inventory from '../views/Inventory.vue'
import SellerInventory from '../views/SellerInventory.vue'
import ListProduct from '../views/ListProduct.vue'
import Points from '../views/Points.vue'
import Coupons from '../views/Coupons.vue'
import Invite from '../views/Invite.vue'
import Market from '../views/Market.vue'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home', component: Home, meta: { title: '秒杀首页' } },
  { path: '/login', component: Login, meta: { title: '登录' } },
  { path: '/register', component: Register, meta: { title: '注册' } },
  { path: '/product/:id', component: ProductDetail, meta: { title: '商品详情' } },
  { path: '/pay/:orderId', component: Pay, meta: { title: '支付' } },
  { path: '/mine', component: Mine, meta: { title: '个人中心' } },
  { path: '/inventory', component: Inventory, meta: { title: '我的仓库' } },
  { path: '/seller-inventory', component: SellerInventory, meta: { title: '卖家仓库' } },
  { path: '/list-product/:inventoryId', component: ListProduct, meta: { title: '上架商品' } },
  { path: '/market', component: Market, meta: { title: '转卖市场' } },
  { path: '/points', component: Points, meta: { title: '积分记录' } },
  { path: '/coupons', component: Coupons, meta: { title: '我的优惠券' } },
  { path: '/invite', component: Invite, meta: { title: '邀请中心' } }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

export default router
