import request from './request'

export function getMessages() { return request.get('/message/list') }
export function getUnreadCount() { return request.get('/message/unread-count') }
export function readMessage(id) { return request.post(`/message/read/${id}`) }
export function readAllMessages() { return request.post('/message/read-all') }
