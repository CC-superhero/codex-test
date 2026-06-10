const { register } = require('../../api/user')
const { setAuth } = require('../../utils/auth')

Page({
  data: {
    form: {
      nickname: '',
      inviteCode: '',
      realName: '',
      phone: '',
      address: '',
      bankCard: '',
      idCard: ''
    },
    loading: false
  },
  onInput(event) {
    const { field } = event.currentTarget.dataset
    this.setData({
      [`form.${field}`]: event.detail.value
    })
  },
  async handleRegister() {
    const { form } = this.data
    if (!form.nickname.trim() || !form.inviteCode.trim()) {
      wx.showToast({
        title: '昵称和邀请码必填',
        icon: 'none'
      })
      return
    }

    this.setData({ loading: true })
    try {
      const res = await register({
        ...form,
        nickname: form.nickname.trim(),
        inviteCode: form.inviteCode.trim()
      })
      setAuth(res.data || {})
      wx.showToast({
        title: '注册成功',
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
        title: error.message || '注册失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  }
})
