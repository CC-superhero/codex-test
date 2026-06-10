const { delistProduct, getSellerInventory } = require('../../api/inventory')
const { requireAuth } = require('../../utils/auth')

Page({
  data: {
    list: [],
    submittingId: null
  },
  onShow() {
    if (!requireAuth()) return
    this.fetchList()
  },
  async fetchList() {
    try {
      const res = await getSellerInventory()
      this.setData({ list: res.data || [] })
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '卖家仓库加载失败',
        icon: 'none'
      })
    }
  },
  async handleDelist(event) {
    const { id } = event.currentTarget.dataset
    this.setData({ submittingId: id })
    try {
      await delistProduct(id)
      wx.showToast({
        title: '下架成功',
        icon: 'success'
      })
      this.fetchList()
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '下架失败',
        icon: 'none'
      })
    } finally {
      this.setData({ submittingId: null })
    }
  }
})
