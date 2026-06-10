const { request } = require('../utils/request')

function getMyInventory() {
  return request({
    url: '/user/inventory/list'
  })
}

function getSellerInventory() {
  return request({
    url: '/user/inventory/seller'
  })
}

function listProduct(data) {
  return request({
    url: '/user/inventory/list',
    method: 'POST',
    data
  })
}

function delistProduct(id) {
  return request({
    url: `/user/inventory/list/${id}`,
    method: 'DELETE'
  })
}

function getMarketInventory() {
  return request({
    url: '/user/market/list'
  })
}

function buyMarketInventory(id) {
  return request({
    url: `/user/market/buy/${id}`,
    method: 'POST'
  })
}

module.exports = {
  getMyInventory,
  getSellerInventory,
  listProduct,
  delistProduct,
  getMarketInventory,
  buyMarketInventory
}
