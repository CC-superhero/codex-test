const { getProductList } = require('../../api/product')
const { requireAuth } = require('../../utils/auth')

Page({
  data: {
    products: [],
    loading: false
  },
  onShow() {
    if (!requireAuth()) {
      this.setData({ products: [] })
      return
    }
    this.fetchProducts()
  },
  onPullDownRefresh() {
    this.fetchProducts().finally(() => {
      wx.stopPullDownRefresh()
    })
  },
  async fetchProducts() {
    if (!requireAuth()) return
    this.setData({ loading: true })
    try {
      const res = await getProductList()
      const data = res.data && res.data.records ? res.data.records : res.data || []
      this.setData({ products: data })
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '商品加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },
  goDetail(event) {
    if (!requireAuth()) return
    const { id } = event.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/product-detail/index?id=${id}`
    })
  }
})
