import request from './request'

export function getNewsList(params) {
  return request.get('/news/list', { params })
}

export function getNewsDetail(id) {
  return request.get(`/news/detail/${id}`)
}

export function getAdminNewsList(params) {
  return request.get('/news/admin/list', { params })
}

export function addNews(data) {
  return request.post('/news/add', data)
}

export function updateNews(data) {
  return request.put('/news/update', data)
}

export function deleteNews(id) {
  return request.delete(`/news/delete/${id}`)
}
