function getToken() {
  return wx.getStorageSync('token') || ''
}

function setAuth(data) {
  const token = data && data.token ? data.token : ''
  wx.setStorageSync('token', token)
  wx.setStorageSync('userInfo', data || {})
  const app = getApp()
  if (app) {
    app.globalData.userInfo = data || null
  }
}

function clearAuth() {
  wx.removeStorageSync('token')
  wx.removeStorageSync('userInfo')
  const app = getApp()
  if (app) {
    app.globalData.userInfo = null
  }
}

function requireAuth() {
  const token = getToken()
  if (!token) {
    wx.navigateTo({
      url: '/pages/login/index'
    })
    return false
  }
  return true
}

module.exports = {
  getToken,
  setAuth,
  clearAuth,
  requireAuth
}
