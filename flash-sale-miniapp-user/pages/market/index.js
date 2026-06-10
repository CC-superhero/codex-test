const { buyMarketInventory, getMarketInventory } = require('../../api/inventory')
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
      const res = await getMarketInventory()
      this.setData({ list: res.data || [] })
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '市场加载失败',
        icon: 'none'
      })
    }
  },
  async handleBuy(event) {
    const { id } = event.currentTarget.dataset
    this.setData({ submittingId: id })
    try {
      await buyMarketInventory(id)
      wx.showToast({
        title: '购买成功',
        icon: 'success'
      })
      this.fetchList()
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '购买失败',
        icon: 'none'
      })
    } finally {
      this.setData({ submittingId: null })
    }
  }
})
