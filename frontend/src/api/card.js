import request from './request'

export function getMyCards() {
  return request.get('/card/list')
}

export function getCardDetail(id) {
  return request.get(`/card/detail/${id}`)
}

export function freezeCard(id) {
  return request.post(`/card/freeze/${id}`)
}

export function unfreezeCard(id) {
  return request.post(`/card/unfreeze/${id}`)
}

export function cancelCard(id) {
  return request.post(`/card/cancel/${id}`)
}

export function depositCard(id, data) {
  return request.post(`/card/deposit/${id}`, data)
}

export function withdrawCard(id, data) {
  return request.post(`/card/withdraw/${id}`, data)
}
