import request from '@/utils/request'

export function getDictPage(params) {
  return request.get('/dicts', { params })
}

export function getDictOptions() {
  return request.get('/dicts/options')
}

export function createDict(data) {
  return request.post('/dicts', data)
}

export function updateDict(data) {
  return request.put('/dicts', data)
}

export function deleteDict(id) {
  return request.delete('/dicts/' + id)
}

export function getDictDataPage(params) {
  return request.get('/dicts/data/page', { params })
}

export function getDictDataByType(dictType) {
  return request.get('/dicts/by-type/' + dictType)
}

export function createDictData(data) {
  return request.post('/dicts/data', data)
}

export function updateDictData(data) {
  return request.put('/dicts/data', data)
}

export function deleteDictData(id) {
  return request.delete('/dicts/data/' + id)
}
