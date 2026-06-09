<template>
  <div class="page">
    <van-nav-bar title="秒杀首页" />
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model="loading" :finished="finished" @load="fetchList">
        <van-card
          v-for="item in list"
          :key="item.id"
          :title="item.name"
          :price="item.price"
          :thumb="item.image || 'https://via.placeholder.com/100'"
          @click="$router.push('/product/' + item.id)"
        >
          <template #footer>
            <van-tag :type="item.status === 1 ? 'danger' : 'default'">
              {{ item.status === 0 ? '未开始' : item.status === 1 ? '进行中' : '已结束' }}
            </van-tag>
            <span style="margin-left:8px;color:#999">剩余 {{ item.remainStock }}</span>
          </template>
        </van-card>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script>
import { getProductList } from '../api/product'

export default {
  name: 'Home',
  data() {
    return {
      list: [],
      loading: false,
      finished: false,
      refreshing: false
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      try {
        const res = await getProductList()
        this.list = (res.data && res.data.records) || res.data || []
        this.finished = true
      } catch {
        this.$toast.fail('商品加载失败')
      }
    },
    async onRefresh() {
      this.refreshing = true
      await this.fetchList()
      this.refreshing = false
    }
  }
}
</script>
