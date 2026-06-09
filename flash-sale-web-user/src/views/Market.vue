<template>
  <div class="page">
    <van-nav-bar title="转卖市场" left-arrow @click-left="$router.back()" />
    <van-empty v-if="list.length === 0" description="暂无在售商品" />
    <van-card
      v-for="item in list"
      :key="item.id"
      :title="item.productName"
      :price="item.price"
      :thumb="item.productImage || 'https://via.placeholder.com/100'"
    >
      <template #footer>
        <van-button size="small" type="danger" :loading="buyingId === item.id" @click="handleBuy(item.id)">
          立即购买
        </van-button>
      </template>
    </van-card>
  </div>
</template>

<script>
import { buyMarketInventory, getMarketInventory } from '../api/inventory'

export default {
  name: 'Market',
  data() {
    return {
      list: [],
      buyingId: null
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      try {
        const res = await getMarketInventory()
        this.list = res.data || []
      } catch {
        this.$toast.fail('市场加载失败')
      }
    },
    async handleBuy(id) {
      this.buyingId = id
      try {
        await buyMarketInventory(id)
        this.$toast.success('购买成功')
        this.fetchList()
      } catch (error) {
        const message = error?.response?.data?.message || '购买失败'
        this.$toast.fail(message)
      } finally {
        this.buyingId = null
      }
    }
  }
}
</script>
