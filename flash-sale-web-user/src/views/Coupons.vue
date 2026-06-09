<template>
  <div class="page">
    <van-nav-bar title="我的优惠券" left-arrow @click-left="$router.back()" />
    <van-empty v-if="list.length === 0" description="暂无优惠券" />
    <van-card v-for="item in list" :key="item.id" :title="item.name" :price="item.faceValue">
      <template #footer>
        <van-tag :type="item.status === 0 ? 'primary' : 'default'">
          {{ item.status === 0 ? '未使用' : item.status === 1 ? '已使用' : '已过期' }}
        </van-tag>
        <span style="margin-left:8px;color:#999">有效期至：{{ item.expireTime }}</span>
      </template>
    </van-card>
  </div>
</template>

<script>
import { getCoupons } from '../api/coupon'

export default {
  name: 'Coupons',
  data() {
    return { list: [] }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      try {
        const res = await getCoupons()
        this.list = res.data || []
      } catch {
        this.$toast.fail('优惠券加载失败')
      }
    }
  }
}
</script>
