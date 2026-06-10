const { listProduct } = require('../../api/inventory')
const { requireAuth } = require('../../utils/auth')

Page({
  data: {
    inventoryId: null,
    loading: false
  },
  onLoad(options) {
    this.setData({
      inventoryId: Number(options.inventoryId) || null
    })
  },
  async handleConfirm() {
    if (!requireAuth()) return
    if (!this.data.inventoryId) {
      wx.showToast({
        title: '缺少仓库商品编号',
        icon: 'none'
      })
      return
    }
    this.setData({ loading: true })
    try {
      await listProduct({ inventoryId: this.data.inventoryId })
      wx.showToast({
        title: '上架成功',
        icon: 'success'
      })
      setTimeout(() => {
        wx.redirectTo({
          url: '/pages/seller-inventory/index'
        })
      }, 400)
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '上架失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  }
})
