const { request } = require('../utils/request')

function getProductList() {
  return request({
    url: '/user/product/list'
  })
}

function getProductDetail(id) {
  return request({
    url: `/user/product/${id}`
  })
}

function flashBuy(data) {
  return request({
    url: '/user/flash/buy',
    method: 'POST',
    data
  })
}

module.exports = {
  getProductList,
  getProductDetail,
  flashBuy
}
