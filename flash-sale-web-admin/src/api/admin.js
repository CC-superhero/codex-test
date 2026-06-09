import request from './request'

export function adminLogin(data) {
  return request.post('/admin/login', data)
}

export function getUserList(params) {
  return request.get('/admin/user/list', { params })
}

export function updateUser(id, data) {
  return request.put('/admin/user/' + id, data)
}

export function banUser(id, status) {
  return request.put('/admin/user/' + id + '/ban', null, { params: { status } })
}

export function deleteUser(id) {
  return request.delete('/admin/user/' + id)
}

export function getUserInviteTree(id) {
  return request.get('/admin/user/' + id + '/invite-tree')
}

export function getUserInventory(id) {
  return request.get('/admin/user/' + id + '/inventory')
}

export function getDashboardStats() {
  return request.get('/admin/dashboard/stats')
}

export function getProductList(params) {
  return request.get('/admin/product/list', { params })
}

export function getProductDetail(id) {
  return request.get('/admin/product/' + id)
}

export function publishProduct(data) {
  return request.post('/admin/product', data)
}

export function updateProduct(id, data) {
  return request.put('/admin/product/' + id, data)
}

export function deleteProduct(id) {
  return request.delete('/admin/product/' + id)
}

export function transferInventory(data) {
  return request.put('/admin/inventory/transfer', data)
}

export function deleteInventory(id) {
  return request.delete('/admin/inventory/' + id)
}

export function grantCoupon(data) {
  return request.post('/admin/coupon/grant', data)
}

export function grantPoints(data) {
  return request.post('/admin/points/grant', data)
}

export function getConfig() {
  return request.get('/admin/config')
}

export function updateConfig(data) {
  return request.put('/admin/config', data)
}
