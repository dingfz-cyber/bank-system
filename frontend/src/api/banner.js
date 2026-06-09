/**
 * 轮播相关 API
 */
import request from './request'

export function getBannerList() {
  return request.get('/banner/list')
}

export function addBanner(data) {
  return request.post('/banner/add', data)
}

export function updateBanner(data) {
  return request.put('/banner/update', data)
}

export function deleteBanner(id) {
  return request.delete(`/banner/delete/${id}`)
}
