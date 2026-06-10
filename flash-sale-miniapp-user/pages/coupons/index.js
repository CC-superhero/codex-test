const { getCoupons } = require('../../api/coupon')
const { requireAuth } = require('../../utils/auth')

Page({
  data: {
    list: []
  },
  onShow() {
    if (!requireAuth()) return
    this.fetchList()
  },
  async fetchList() {
    try {
      const res = await getCoupons()
      this.setData({ list: res.data || [] })
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '优惠券加载失败',
        icon: 'none'
      })
    }
  }
})
