<template>
  <div class="page">
    <van-nav-bar title="邀请中心" left-arrow @click-left="$router.back()" />
    <div style="text-align:center;padding:40px 20px">
      <h2>我的邀请码</h2>
      <h1 style="color:#ee0a24;letter-spacing:4px">{{ info.inviteCode || '加载中...' }}</h1>
      <p style="color:#999">已成功邀请 <strong>{{ info.inviteCount || 0 }}</strong> 人</p>
      <p style="color:#999;margin-top:20px">
        将邀请码分享给好友，好友注册并参与秒杀后，你会自动获得邀请积分奖励。
      </p>
    </div>
  </div>
</template>

<script>
import { getInviteInfo } from '../api/coupon'

export default {
  name: 'Invite',
  data() {
    return { info: {} }
  },
  created() {
    this.fetchInfo()
  },
  methods: {
    async fetchInfo() {
      try {
        const res = await getInviteInfo()
        this.info = res.data || {}
      } catch {
        this.$toast.fail('邀请信息加载失败')
      }
    }
  }
}
</script>
