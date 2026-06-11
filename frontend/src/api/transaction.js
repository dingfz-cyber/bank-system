import request from './request'

export function transfer(data) {
  return request.post('/transaction/transfer', data)
}

export function getTransactionList(params) {
  return request.get('/transaction/list', { params })
}
