import request from './request'

export function getCoupons() {
  return request.get('/user/coupon/list')
}

export function getPointsLog() {
  return request.get('/user/points/log')
}

export function getInviteInfo() {
  return request.get('/user/invite/info')
}
