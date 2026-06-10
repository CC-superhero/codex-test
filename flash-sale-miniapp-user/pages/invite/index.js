const { getInviteInfo } = require('../../api/coupon')
const { requireAuth } = require('../../utils/auth')

Page({
  data: {
    info: {}
  },
  onShow() {
    if (!requireAuth()) return
    this.fetchInfo()
  },
  async fetchInfo() {
    try {
      const res = await getInviteInfo()
      this.setData({ info: res.data || {} })
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '邀请信息加载失败',
        icon: 'none'
      })
    }
  },
  copyCode() {
    if (!this.data.info.inviteCode) return
    wx.setClipboardData({
      data: this.data.info.inviteCode
    })
  }
})
