import request from '@/utils/request'

// 查询告警点位快照列表
export function listSnapshoot(query) {
  return request({
    url: '/device/snapshoot/list',
    method: 'get',
    params: query
  })
}

// 查询告警点位快照详细
export function getSnapshoot(id) {
  return request({
    url: '/device/snapshoot/' + id,
    method: 'get'
  })
}

// 新增告警点位快照
export function addSnapshoot(data) {
  return request({
    url: '/device/snapshoot',
    method: 'post',
    data: data
  })
}

// 修改告警点位快照
export function updateSnapshoot(data) {
  return request({
    url: '/device/snapshoot',
    method: 'put',
    data: data
  })
}

// 删除告警点位快照
export function delSnapshoot(id) {
  return request({
    url: '/device/snapshoot/' + id,
    method: 'delete'
  })
}
