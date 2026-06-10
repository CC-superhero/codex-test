const { request } = require('../utils/request')

function getCoupons() {
  return request({
    url: '/user/coupon/list'
  })
}

function getPointsLog() {
  return request({
    url: '/user/points/log'
  })
}

function getInviteInfo() {
  return request({
    url: '/user/invite/info'
  })
}

module.exports = {
  getCoupons,
  getPointsLog,
  getInviteInfo
}
