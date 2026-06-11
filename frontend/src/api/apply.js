/**
 * 申请相关 API
 */
import request from './request'

export function submitApply(data) {
  return request.post('/apply/submit', data)
}

export function getApplyList(params) {
  return request.get('/apply/list', { params })
}

export function getApplyRecycleList(params) {
  return request.get('/apply/recycle', { params })
}

export function updateApply(id, data) {
  return request.put(`/apply/${id}`, data)
}

export function deleteApply(id) {
  return request.delete(`/apply/${id}`)
}

export function approveApply(id, params) {
  return request.post(`/apply/approve/${id}`, null, { params })
}

export function recoverApply(id) {
  return request.post(`/apply/recover/${id}`)
}

export function wipeApply(id) {
  return request.delete(`/apply/wipe/${id}`)
}
