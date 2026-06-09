import request from './request'

export function getMyInventory() {
  return request.get('/user/inventory/list')
}

export function getSellerInventory() {
  return request.get('/user/inventory/seller')
}

export function listProduct(data) {
  return request.post('/user/inventory/list', data)
}

export function delistProduct(id) {
  return request.delete('/user/inventory/list/' + id)
}

export function getMarketInventory() {
  return request.get('/user/market/list')
}

export function buyMarketInventory(id) {
  return request.post('/user/market/buy/' + id)
}
