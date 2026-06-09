<template>
  <div class="page">
    <van-nav-bar title="卖家仓库" left-arrow @click-left="$router.back()" />
    <van-empty v-if="list.length === 0" description="暂无上架商品" />
    <van-card
      v-for="item in list"
      :key="item.id"
      :title="item.productName"
      :price="item.price"
      :thumb="item.productImage || 'https://via.placeholder.com/100'"
    >
      <template #footer>
        <van-tag type="warning">已上架</van-tag>
        <van-button size="small" type="danger" style="margin-left:8px" @click="handleDelist(item.id)">
          下架
        </van-button>
      </template>
    </van-card>
  </div>
</template>

<script>
import { delistProduct, getSellerInventory } from '../api/inventory'

export default {
  name: 'SellerInventory',
  data() {
    return { list: [] }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      try {
        const res = await getSellerInventory()
        this.list = res.data || []
      } catch {
        this.$toast.fail('卖家仓库加载失败')
      }
    },
    async handleDelist(id) {
      try {
        await delistProduct(id)
        this.$toast.success('下架成功')
        this.fetchList()
      } catch {
        this.$toast.fail('下架失败')
      }
    }
  }
}
</script>
