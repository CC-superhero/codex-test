const { flashBuy, getProductDetail } = require('../../api/product')
const { requireAuth } = require('../../utils/auth')

Page({
  data: {
    id: null,
    product: {},
    couponId: '',
    pointsUsed: '',
    loading: false
  },
  onLoad(options) {
    this.setData({ id: Number(options.id) || null })
  },
  onShow() {
    if (this.data.id) {
      this.fetchDetail()
    }
  },
  onInput(event) {
    const { field } = event.currentTarget.dataset
    this.setData({
      [field]: event.detail.value
    })
  },
  async fetchDetail() {
    try {
      const res = await getProductDetail(this.data.id)
      this.setData({ product: res.data || {} })
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '商品加载失败',
        icon: 'none'
      })
    }
  },
  async handleBuy() {
    if (!requireAuth()) return
    this.setData({ loading: true })
    try {
      const data = {
        productId: this.data.product.id
      }
      if (this.data.couponId) data.couponId = Number(this.data.couponId)
      if (this.data.pointsUsed) data.pointsUsed = Number(this.data.pointsUsed)
      await flashBuy(data)
      wx.showToast({
        title: '抢购成功',
        icon: 'success'
      })
      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/inventory/index'
        })
      }, 400)
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '抢购失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
      this.fetchDetail()
    }
  }
})
