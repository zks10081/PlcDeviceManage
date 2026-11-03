import request from '@/utils/request'

// 查询设备点位日志列表
export function listLog(query) {
  return request({
    url: '/device/log/list',
    method: 'get',
    params: query
  })
}

// 查询设备点位日志详细
export function getLog(id) {
  return request({
    url: '/device/log/' + id,
    method: 'get'
  })
}

// 新增设备点位日志
export function addLog(data) {
  return request({
    url: '/device/log',
    method: 'post',
    data: data
  })
}

// 修改设备点位日志
export function updateLog(data) {
  return request({
    url: '/device/log',
    method: 'put',
    data: data
  })
}

// 删除设备点位日志
export function delLog(id) {
  return request({
    url: '/device/log/' + id,
    method: 'delete'
  })
}
