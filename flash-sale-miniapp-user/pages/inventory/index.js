const { getMyInventory } = require('../../api/inventory')
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
      const res = await getMyInventory()
      this.setData({ list: res.data || [] })
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '仓库加载失败',
        icon: 'none'
      })
    }
  },
  goList(event) {
    const { id } = event.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/list-product/index?inventoryId=${id}`
    })
  }
})
