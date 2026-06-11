import request from './request'

export function getPayeeList() { return request.get('/payee/list') }
export function addPayee(data) { return request.post('/payee/add', data) }
export function updatePayee(id, data) { return request.put(`/payee/${id}`, data) }
export function deletePayee(id) { return request.delete(`/payee/${id}`) }
