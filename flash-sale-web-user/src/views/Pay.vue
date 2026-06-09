<template>
  <div class="page">
    <van-nav-bar title="支付" left-arrow @click-left="$router.back()" />
    <div style="text-align:center;padding:80px 20px">
      <van-icon name="checked" size="60" color="#07c160" />
      <h3>支付确认</h3>
      <p style="color:#999">此为模拟支付页面，无需真实付款</p>
      <van-button type="primary" size="large" :loading="paying" @click="handlePay">确认支付（模拟）</van-button>
    </div>
  </div>
</template>

<script>
import { mockPay } from '../api/product'

export default {
  name: 'Pay',
  data() { return { paying: false } },
  methods: {
    async handlePay() {
      this.paying = true
      try {
        await mockPay()
        this.$toast.success('支付成功')
        setTimeout(() => this.$router.push('/inventory'), 1000)
      } catch {
        this.$toast.fail('支付失败')
      } finally {
        this.paying = false
      }
    }
  }
}
</script>
