import request from './request'
export function getMall() { return request.get('/points/mall') }
export function getRecords() { return request.get('/points/records') }
export function redeem(goodsId) { return request.post(`/points/redeem/${goodsId}`) }
export function getAdminGoods() { return request.get('/points/admin/goods') }
export function addGoods(data) { return request.post('/points/admin/goods', data) }
export function updateGoods(id, data) { return request.put(`/points/admin/goods/${id}`, data) }
export function deleteGoods(id) { return request.delete(`/points/admin/goods/${id}`) }
