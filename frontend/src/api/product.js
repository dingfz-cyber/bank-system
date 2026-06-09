/**
 * 产品相关 API
 */
import request from './request'

export function getProductList(params) {
  return request.get('/product/list', { params })
}

export function getProductDetail(id) {
  return request.get(`/product/detail/${id}`)
}

export function addProduct(data) {
  return request.post('/product/add', data)
}

export function updateProduct(data) {
  return request.put('/product/update', data)
}

export function deleteProduct(id) {
  return request.delete(`/product/delete/${id}`)
}

export function getRecycleList(params) {
  return request.get('/product/recycle', { params })
}

export function recoverProduct(id) {
  return request.post(`/product/recover/${id}`)
}

export function wipeProduct(id) {
  return request.delete(`/product/wipe/${id}`)
}
