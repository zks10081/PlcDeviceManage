import request from '@/utils/request'

export function getUserPage(params) {
  return request.get('/users', { params })
}

export function getUser(id) {
  return request.get('/users/' + id)
}

export function createUser(data) {
  return request.post('/users', data)
}

export function updateUser(data) {
  return request.put('/users', data)
}

export function deleteUser(id) {
  return request.delete('/users/' + id)
}
