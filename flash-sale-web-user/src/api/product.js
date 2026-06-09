import request from './request'

export function getProductList() {
  return request.get('/user/product/list')
}

export function getProductDetail(id) {
  return request.get('/user/product/' + id)
}

export function flashBuy(data) {
  return request.post('/user/flash/buy', data)
}

export function mockPay() {
  return request.post('/user/pay/page')
}
