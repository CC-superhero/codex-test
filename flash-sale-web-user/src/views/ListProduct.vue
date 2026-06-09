<template>
  <div class="page">
    <van-nav-bar title="上架商品" left-arrow @click-left="$router.back()" />
    <div style="text-align:center;padding:80px 20px">
      <p style="color:#999">确认将该商品上架到转卖市场？</p>
      <van-button type="warning" size="large" :loading="loading" @click="handleList">确认上架</van-button>
    </div>
  </div>
</template>

<script>
import { listProduct } from '../api/inventory'

export default {
  name: 'ListProduct',
  data() {
    return { loading: false }
  },
  methods: {
    async handleList() {
      this.loading = true
      try {
        await listProduct({ inventoryId: Number(this.$route.params.inventoryId) })
        this.$toast.success('上架成功，手续费已返还为积分')
        this.$router.push('/seller-inventory')
      } catch {
        this.$toast.fail('上架失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
