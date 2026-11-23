import request from '@/utils/request'

// 查询告警队伍列表
export function listAlarmTeam(query) {
  return request({
    url: '/device/alarmTeam/list',
    method: 'get',
    params: query
  })
}

// 查询告警队伍详细
export function getAlarmTeam(id) {
  return request({
    url: '/device/alarmTeam/' + id,
    method: 'get'
  })
}

// 新增告警队伍
export function addAlarmTeam(data) {
  return request({
    url: '/device/alarmTeam',
    method: 'post',
    data: data
  })
}

// 修改告警队伍
export function updateAlarmTeam(data) {
  return request({
    url: '/device/alarmTeam',
    method: 'put',
    data: data
  })
}

// 删除告警队伍
export function delAlarmTeam(id) {
  return request({
    url: '/device/alarmTeam/' + id,
    method: 'delete'
  })
}

// 查询队伍已选用户标记
export function getAlarmUserFlag(teamId) {
  return request({
    url: '/device/alarmTeam/userFlag/' + teamId,
    method: 'get'
  })
}

// 查询队伍已选设备标记
export function getAlarmDeviceFlag(teamId) {
  return request({
    url: '/device/alarmTeam/deviceFlag/' + teamId,
    method: 'get'
  })
}

// 成员候选：座机
export function listTransferLandline() {
  return request({ url: '/device/alarmTeam/transfer/landline', method: 'get' })
}

// 成员候选：手机
export function listTransferPhone() {
  return request({ url: '/device/alarmTeam/transfer/phone', method: 'get' })
}

// 成员候选：广播
export function listTransferBroadcast() {
  return request({ url: '/device/alarmTeam/transfer/broadcast', method: 'get' })
}

// 成员候选：外机
export function listTransferOuter() {
  return request({ url: '/device/alarmTeam/transfer/outer', method: 'get' })
}

// 负责人候选用户
export function listLeaderOptions() {
  return request({ url: '/device/alarmTeam/leaderOptions', method: 'get' })
}
