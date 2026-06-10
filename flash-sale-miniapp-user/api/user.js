const { request } = require('../utils/request')

function login(data) {
  return request({
    url: '/user/login',
    method: 'POST',
    data,
    auth: false
  })
}

function register(data) {
  return request({
    url: '/user/register',
    method: 'POST',
    data,
    auth: false
  })
}

function getUserInfo() {
  return request({
    url: '/user/info'
  })
}

module.exports = {
  login,
  register,
  getUserInfo
}
