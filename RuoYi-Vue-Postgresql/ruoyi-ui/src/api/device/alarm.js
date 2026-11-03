import request from '@/utils/request'

// 查询告警列表
export function listAlarm(query) {
  return request({
    url: '/device/alarm/list',
    method: 'get',
    params: query
  })
}

// 查询告警详细
export function getAlarm(id) {
  return request({
    url: '/device/alarm/' + id,
    method: 'get'
  })
}

// 新增告警
export function addAlarm(data) {
  return request({
    url: '/device/alarm',
    method: 'post',
    data: data
  })
}

// 修改告警
export function updateAlarm(data) {
  return request({
    url: '/device/alarm',
    method: 'put',
    data: data
  })
}

// 删除告警
export function delAlarm(id) {
  return request({
    url: '/device/alarm/' + id,
    method: 'delete'
  })
}
