<template>
  <div class="page">
    <van-nav-bar title="积分记录" left-arrow @click-left="$router.back()" />
    <van-empty v-if="list.length === 0" description="暂无记录" />
    <van-cell v-for="item in list" :key="item.id" :title="item.remark || typeMap[item.type]">
      <template #label>{{ item.createTime }}</template>
      <template #value>
        <span :style="{ color: item.changeAmount >= 0 ? '#07c160' : '#ee0a24' }">
          {{ item.changeAmount >= 0 ? '+' : '' }}{{ item.changeAmount }}
        </span>
      </template>
    </van-cell>
  </div>
</template>

<script>
import { getPointsLog } from '../api/coupon'

export default {
  name: 'Points',
  data() {
    return {
      list: [],
      typeMap: {
        1: '秒杀奖励',
        2: '邀请奖励',
        3: '上架返还',
        4: '后台发放或积分抵扣'
      }
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      try {
        const res = await getPointsLog()
        this.list = res.data || []
      } catch {
        this.$toast.fail('积分记录加载失败')
      }
    }
  }
}
</script>
