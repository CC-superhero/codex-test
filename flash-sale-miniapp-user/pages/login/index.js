const { login } = require('../../api/user')
const { setAuth } = require('../../utils/auth')

Page({
  data: {
    nickname: '',
    loading: false
  },
  onNicknameInput(event) {
    this.setData({ nickname: event.detail.value })
  },
  async handleLogin() {
    if (!this.data.nickname.trim()) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      })
      return
    }

    this.setData({ loading: true })
    try {
      const res = await login({ nickname: this.data.nickname.trim() })
      setAuth(res.data || {})
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      })
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/mine/index'
        })
      }, 400)
    } catch (error) {
      if (error && error.silent) return
      wx.showToast({
        title: error.message || '登录失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },
  goRegister() {
    wx.navigateTo({
      url: '/pages/register/index'
    })
  }
})
