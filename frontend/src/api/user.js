/**
 * 用户相关 API
 */
import request from './request'

export function register(data) {
  return request.post('/user/register', data)
}

export function login(data) {
  return request.post('/user/login', data)
}

export function smsLogin(data) {
  return request.post('/user/sms-login', data)
}

export function updateProfile(data) {
  return request.put('/user/profile', data)
}

export function changePassword(data) {
  return request.put('/user/password', data)
}

export function setTransactionPassword(data) {
  return request.put('/user/transaction-password', data)
}

export function forgotPassword(data) {
  return request.post('/user/forgot-password', data)
}

export function listUsers() {
  return request.get('/user/list')
}

export function lockUser(id) {
  return request.post(`/user/lock/${id}`)
}

export function unlockUser(id) {
  return request.post(`/user/unlock/${id}`)
}

export function getLoginRecords() {
  return request.get('/user/login-records')
}

export function adminResetPassword(id) {
  return request.post(`/user/admin/reset-password/${id}`)
}
