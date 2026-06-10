const { getToken, clearAuth } = require('./auth')

const BASE_URL = 'http://127.0.0.1:8080/api'

function normalizeError(body, fallbackMessage) {
  return {
    ...body,
    message: body.message || body.msg || body.errMsg || fallbackMessage
  }
}

function request({ url, method = 'GET', data, auth = true }) {
  return new Promise((resolve, reject) => {
    const headers = {
      'Content-Type': 'application/json'
    }

    if (auth) {
      const token = getToken()
      if (token) {
        headers.Authorization = `Bearer ${token}`
      }
    }

    wx.request({
      url: `${BASE_URL}${url}`,
      method,
      data,
      header: headers,
      success(res) {
        const body = res.data || {}
        if (body.code === 200) {
          resolve(body)
          return
        }
        if (body.code === 401) {
          clearAuth()
          wx.showToast({
            title: '登录已失效',
            icon: 'none'
          })
          const pages = getCurrentPages()
          const currentRoute = pages.length ? pages[pages.length - 1].route : ''
          if (currentRoute !== 'pages/login/index') {
            wx.navigateTo({
              url: '/pages/login/index'
            })
          }
          reject({
            ...normalizeError(body, '登录已失效'),
            silent: true
          })
          return
        }
        reject(normalizeError(body, '请求失败'))
      },
      fail(error) {
        reject(normalizeError(error || {}, '网络请求失败'))
      }
    })
  })
}

module.exports = {
  BASE_URL,
  request
}
