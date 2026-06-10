const { getUserInfo } = require('../../api/user')
const { clearAuth, getToken } = require('../../utils/auth')

Page({
  data: {
    userInfo: {}
  },
  onShow() {
    if (!getToken()) {
      this.setData({ userInfo: {} })
      return
    }
    this.fetchUserInfo()
  },
  async fetchUserInfo() {
    try {
      const res = await getUserInfo()
      this.setData({ userInfo: res.data || {} })
      wx.setStorageSync('userInfo', res.data || {})
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '用户信息加载失败',
        icon: 'none'
      })
    }
  },
  goLogin() {
    wx.navigateTo({
      url: '/pages/login/index'
    })
  },
  goPage(event) {
    const { url } = event.currentTarget.dataset
    wx.navigateTo({ url })
  },
  handleLogout() {
    clearAuth()
    this.setData({ userInfo: {} })
    wx.showToast({
      title: '已退出登录',
      icon: 'success'
    })
  }
})
