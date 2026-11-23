import request from '@/utils/request'

// 查询设备点位列表
export function listAttribute(query) {
  return request({
    url: '/device/attribute/list',
    method: 'get',
    params: query
  })
}

// 查询设备点位详细
export function getAttribute(id) {
  return request({
    url: '/device/attribute/' + id,
    method: 'get'
  })
}

// 新增设备点位
export function addAttribute(data) {
  return request({
    url: '/device/attribute',
    method: 'post',
    data: data
  })
}

// 修改设备点位
export function updateAttribute(data) {
  return request({
    url: '/device/attribute',
    method: 'put',
    data: data
  })
}

// 删除设备点位
export function delAttribute(id) {
  return request({
    url: '/device/attribute/' + id,
    method: 'delete'
  })
}
