import request from '@/utils/request'

// 查询SIP用户列表（只读）
export function listDmUser(query) {
  return request({
    url: '/dmUser/list',
    method: 'get',
    params: query
  })
}

// 同步SIP用户数据
export function syncUserData() {
  return request({
    url: '/dmUser/syncUserData',
    method: 'post'
  })
}

// 查询告警的查看情况（告警所属用户列表）
export function queryAlarmUser(alarmId) {
  return request({
    url: '/dmUser/queryAlarmUser',
    method: 'post',
    params: { alarmId: alarmId }
  })
}
