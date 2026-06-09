<template>
  <div class="page">
    <van-nav-bar title="我的仓库" left-arrow @click-left="$router.back()" />
    <van-empty v-if="list.length === 0" description="暂无商品" />
    <van-card
      v-for="item in list"
      :key="item.id"
      :title="item.productName"
      :price="item.price"
      :thumb="item.productImage || 'https://via.placeholder.com/100'"
    >
      <template #footer>
        <van-tag type="primary">{{ item.source === 1 ? '秒杀所得' : '转卖购买' }}</van-tag>
        <van-button
          size="small"
          type="warning"
          style="margin-left:8px"
          @click="$router.push('/list-product/' + item.id)"
        >
          上架
        </van-button>
      </template>
    </van-card>
  </div>
</template>

<script>
import { getMyInventory } from '../api/inventory'

export default {
  name: 'Inventory',
  data() {
    return { list: [] }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      try {
        const res = await getMyInventory()
        this.list = res.data || []
      } catch {
        this.$toast.fail('仓库加载失败')
      }
    }
  }
}
</script>
