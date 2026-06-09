<template>
  <div class="page">
    <van-nav-bar title="商品详情" left-arrow @click-left="$router.back()" />
    <van-image
      v-if="product.image"
      :src="product.image"
      width="100%"
      height="250"
      fit="cover"
    />
    <van-cell-group>
      <van-cell :title="product.name" :value="'¥' + (product.price || 0)" />
      <van-cell title="库存" :value="(product.remainStock || 0) + ' / ' + (product.totalStock || 0)" />
      <van-cell title="活动时间">
        <template #default>{{ product.startTime }} ~ {{ product.endTime }}</template>
      </van-cell>
    </van-cell-group>
    <div style="padding:16px">
      <van-field v-model="couponId" label="优惠券 ID" placeholder="可选" type="number" />
      <van-field v-model="pointsUsed" label="使用积分" placeholder="可选" type="number" />
    </div>
    <div style="margin:16px">
      <van-button
        round
        block
        type="danger"
        :disabled="product.status !== 1"
        :loading="buying"
        @click="handleBuy"
      >
        {{ buttonText }}
      </van-button>
    </div>
  </div>
</template>

<script>
import { getProductDetail, flashBuy } from '../api/product'

export default {
  name: 'ProductDetail',
  data() {
    return {
      product: {},
      couponId: '',
      pointsUsed: '',
      buying: false
    }
  },
  computed: {
    buttonText() {
      if (this.product.status === 1) return '立即抢购'
      if (this.product.status === 0) return '尚未开始'
      return '已结束'
    }
  },
  created() {
    this.fetchDetail()
  },
  methods: {
    async fetchDetail() {
      try {
        const res = await getProductDetail(this.$route.params.id)
        this.product = res.data || {}
      } catch {
        this.$toast.fail('商品加载失败')
      }
    },
    async handleBuy() {
      this.buying = true
      try {
        const data = { productId: this.product.id }
        if (this.couponId) data.couponId = Number(this.couponId)
        if (this.pointsUsed) data.pointsUsed = Number(this.pointsUsed)
        const res = await flashBuy(data)
        this.$toast.success(res.message || '抢购提交成功')
        this.fetchDetail()
        this.$router.push('/inventory')
      } catch (error) {
        const message = error?.response?.data?.message || '抢购失败，请重试'
        this.$toast.fail(message)
      } finally {
        this.buying = false
      }
    }
  }
}
</script>
