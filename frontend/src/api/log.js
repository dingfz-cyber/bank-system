import request from './request'
export function getAuditLog(lines) { return request.get('/log/audit', { params: { lines: lines || 200 } }) }
export function getMonitor() { return request.get('/log/monitor') }
