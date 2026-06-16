import request from './request'
export function getHoldings(){return request.get('/wealth/holdings')}
export function buyWealth(data){return request.post('/wealth/buy',data)}
