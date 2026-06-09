<template>
  <div class="page">
    <van-nav-bar title="个人中心" />
    <van-cell-group v-if="userInfo.nickname">
      <van-cell title="昵称" :value="userInfo.nickname" />
      <van-cell title="积分" :value="userInfo.points || 0" />
      <van-cell title="邀请码" :value="userInfo.inviteCode" />
    </van-cell-group>
    <van-cell-group style="margin-top:12px">
      <van-cell title="我的仓库" is-link to="/inventory" />
      <van-cell title="卖家仓库" is-link to="/seller-inventory" />
      <van-cell title="转卖市场" is-link to="/market" />
      <van-cell title="我的优惠券" is-link to="/coupons" />
      <van-cell title="积分记录" is-link to="/points" />
      <van-cell title="邀请中心" is-link to="/invite" />
    </van-cell-group>
    <div style="margin:20px 16px">
      <van-button round block type="danger" @click="handleLogout">退出登录</van-button>
    </div>
  </div>
</template>

<script>
import { getUserInfo } from '../api/user'

export default {
  name: 'Mine',
  data() {
    return { userInfo: {} }
  },
  created() {
    this.fetchUserInfo()
  },
  methods: {
    async fetchUserInfo() {
      try {
        const res = await getUserInfo()
        this.userInfo = res.data || {}
        this.$store.commit('SET_USER_INFO', res.data || {})
      } catch {
        this.$toast.fail('用户信息加载失败')
      }
    },
    handleLogout() {
      this.$store.dispatch('logout')
      this.$router.push('/login')
    }
  }
}
</script>
