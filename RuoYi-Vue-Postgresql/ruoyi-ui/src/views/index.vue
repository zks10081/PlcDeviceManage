<template>
  <div class="app-container dashboard">
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card stat-device" shadow="hover" @click.native="goRoute('/device/device')">
          <div class="stat-inner">
            <div class="stat-icon"><i class="el-icon-monitor" /></div>
            <div class="stat-info">
              <div class="stat-label">设备数量</div>
              <div class="stat-value">{{ stats.device }}<span class="stat-unit">台</span></div>
            </div>
          </div>
          <div class="stat-foot">查看设备列表 <i class="el-icon-arrow-right" /></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card stat-attribute" shadow="hover" @click.native="goRoute('/device/attribute')">
          <div class="stat-inner">
            <div class="stat-icon"><i class="el-icon-cpu" /></div>
            <div class="stat-info">
              <div class="stat-label">点位数量</div>
              <div class="stat-value">{{ stats.attribute }}<span class="stat-unit">个</span></div>
            </div>
          </div>
          <div class="stat-foot">查看点位列表 <i class="el-icon-arrow-right" /></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card stat-alarm" shadow="hover" @click.native="goRoute('/device/alarm')">
          <div class="stat-inner">
            <div class="stat-icon"><i class="el-icon-bell" /></div>
            <div class="stat-info">
              <div class="stat-label">告警数量</div>
              <div class="stat-value">{{ stats.alarm }}<span class="stat-unit">条</span></div>
            </div>
          </div>
          <div class="stat-foot">查看告警列表 <i class="el-icon-arrow-right" /></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="16">
        <el-card class="panel-card">
          <div slot="header" class="panel-header">
            <span><i class="el-icon-warning-outline" /> 最近告警</span>
            <el-button type="text" icon="el-icon-arrow-right" @click="goRoute('/device/alarm')">查看更多</el-button>
          </div>
          <el-table :data="recentAlarms" size="small" v-loading="loading" border>
            <el-table-column label="告警标题" prop="title" min-width="140" show-overflow-tooltip />
            <el-table-column label="等级" align="center" width="110">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.alarm_level" :value="scope.row.level" />
              </template>
            </el-table-column>
            <el-table-column label="设备名称" prop="deviceName" align="center" min-width="110" show-overflow-tooltip />
            <el-table-column label="点位名称" prop="attributeName" align="center" min-width="110" show-overflow-tooltip />
            <el-table-column label="状态" align="center" width="100">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.alarm_status" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="告警时间" align="center" width="160">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.alarmTime) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="!loading && recentAlarms.length === 0" class="empty-tip">暂无告警数据</div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="panel-card">
          <div slot="header" class="panel-header"><span><i class="el-icon-data-analysis" /> 告警处理情况</span></div>
          <div class="progress-item">
            <div class="progress-head"><span>已处理</span><span>{{ alarmHandled }} / {{ stats.alarm }}</span></div>
            <el-progress :percentage="alarmHandledPercent" color="#67c23a" :stroke-width="12" />
          </div>
          <div class="progress-item">
            <div class="progress-head"><span>未处理</span><span>{{ alarmPending }} / {{ stats.alarm }}</span></div>
            <el-progress :percentage="alarmPendingPercent" color="#f56c6c" :stroke-width="12" />
          </div>
          <el-divider />
          <div class="status-title">设备状态分布</div>
          <div v-for="item in deviceStatusList" :key="item.value" class="status-row">
            <span class="status-name">{{ item.label }}</span>
            <el-progress :percentage="item.percent" :color="item.color" :stroke-width="10" class="status-bar" />
            <span class="status-count">{{ item.count }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="24">
        <el-card class="panel-card">
          <div slot="header" class="panel-header"><span><i class="el-icon-s-grid" /> 快捷入口</span></div>
          <el-row :gutter="12">
            <el-col v-for="entry in shortcuts" :key="entry.path" :xs="12" :sm="8" :md="6" :lg="4">
              <div class="shortcut-item" @click="goRoute(entry.path)">
                <div class="shortcut-icon" :style="{ background: entry.color }"><i :class="entry.icon" /></div>
                <span>{{ entry.name }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listDevice } from "@/api/device/device"
import { listAttribute } from "@/api/device/attribute"
import { listAlarm } from "@/api/device/alarm"

export default {
  name: "Index",
  dicts: ['alarm_level', 'alarm_status', 'device_status'],
  data() {
    return {
      loading: true,
      stats: { device: 0, attribute: 0, alarm: 0 },
      recentAlarms: [],
      alarmHandled: 0,
      alarmPending: 0,
      deviceRows: [],
      shortcuts: [
        { name: '设备管理', path: '/device/device', icon: 'el-icon-monitor', color: '#409eff' },
        { name: '设备点位', path: '/device/attribute', icon: 'el-icon-cpu', color: '#67c23a' },
        { name: '告警管理', path: '/device/alarm', icon: 'el-icon-bell', color: '#f56c6c' },
        { name: '告警队伍', path: '/device/alarmTeam', icon: 'el-icon-user-solid', color: '#e6a23c' },
        { name: '点位日志', path: '/device/log', icon: 'el-icon-document', color: '#909399' },
        { name: 'SIP用户', path: '/user/dmUser', icon: 'el-icon-phone', color: '#8e44ad' }
      ]
    }
  },
  computed: {
    alarmHandledPercent() {
      return this.stats.alarm ? Math.round(this.alarmHandled / this.stats.alarm * 100) : 0
    },
    alarmPendingPercent() {
      return this.stats.alarm ? Math.round(this.alarmPending / this.stats.alarm * 100) : 0
    },
    deviceStatusList() {
      const map = { '0': { label: '正常', color: '#67c23a' }, '1': { label: '关闭', color: '#909399' },
        '3': { label: '故障', color: '#f56c6c' }, '4': { label: '离线', color: '#e6a23c' } }
      const list = []
      Object.keys(map).forEach(value => {
        const count = this.deviceRows.filter(row => String(row.status) === value).length
        list.push({
          value,
          label: map[value].label,
          color: map[value].color,
          count,
          percent: this.deviceRows.length ? Math.round(count / this.deviceRows.length * 100) : 0
        })
      })
      return list
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      // 各接口独立请求，避免单个接口无权限时影响其他统计
      listDevice({ pageNum: 1, pageSize: 1000 }).then(res => {
        this.stats.device = res.total || 0
        this.deviceRows = res.rows || []
      }).catch(() => {})

      listAttribute({ pageNum: 1, pageSize: 1 }).then(res => {
        this.stats.attribute = res.total || 0
      }).catch(() => {})

      listAlarm({ pageNum: 1, pageSize: 1 }).then(res => {
        this.stats.alarm = res.total || 0
      }).catch(() => {})

      // 已处理/未处理数量用 status 过滤后的 total，避免分页条数限制
      listAlarm({ pageNum: 1, pageSize: 1, status: '1' }).then(res => {
        this.alarmHandled = res.total || 0
      }).catch(() => {})
      listAlarm({ pageNum: 1, pageSize: 1, status: '0' }).then(res => {
        this.alarmPending = res.total || 0
      }).catch(() => {})

      // 最近告警
      listAlarm({ pageNum: 1, pageSize: 8 }).then(res => {
        this.recentAlarms = [...(res.rows || [])]
          .sort((a, b) => new Date(b.alarmTime || 0) - new Date(a.alarmTime || 0))
      }).catch(() => {}).finally(() => {
        this.loading = false
      })
    },
    goRoute(path) {
      this.$router.push(path).catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard {
  .stat-row { margin-bottom: 16px; }
  .stat-card {
    cursor: pointer;
    border-radius: 10px;
    transition: transform .2s;
    &:hover { transform: translateY(-3px); }
    ::v-deep .el-card__body { padding: 20px; }
  }
  .stat-device { border-left: 4px solid #409eff; .stat-icon { background: #ecf5ff; color: #409eff; } }
  .stat-attribute { border-left: 4px solid #67c23a; .stat-icon { background: #f0f9eb; color: #67c23a; } }
  .stat-alarm { border-left: 4px solid #f56c6c; .stat-icon { background: #fef0f0; color: #f56c6c; } }
  .stat-inner { display: flex; align-items: center; }
  .stat-icon {
    width: 60px; height: 60px; border-radius: 12px;
    display: flex; align-items: center; justify-content: center;
    font-size: 30px; margin-right: 16px;
  }
  .stat-label { font-size: 14px; color: #909399; margin-bottom: 6px; }
  .stat-value { font-size: 30px; font-weight: 700; color: #303133; line-height: 1; }
  .stat-unit { font-size: 14px; font-weight: 400; color: #909399; margin-left: 4px; }
  .stat-foot {
    margin-top: 16px; padding-top: 12px; border-top: 1px solid #ebeef5;
    font-size: 12px; color: #a0a8b8;
  }

  .panel-card {
    margin-bottom: 16px;
    border-radius: 10px;
    ::v-deep .el-card__header { padding: 14px 20px; border-bottom: 1px solid #ebeef5; }
  }
  .panel-header {
    display: flex; align-items: center; justify-content: space-between;
    font-weight: 600; color: #303133;
    i { margin-right: 6px; color: #409eff; }
  }
  .empty-tip { padding: 30px 0; text-align: center; color: #c0c4cc; font-size: 13px; }

  .progress-item { margin: 12px 0 18px; }
  .progress-head {
    display: flex; justify-content: space-between;
    font-size: 13px; color: #606266; margin-bottom: 8px;
  }
  .status-title { font-size: 14px; font-weight: 600; color: #303133; margin: 16px 0 12px; }
  .status-row { display: flex; align-items: center; margin-bottom: 14px; }
  .status-name { width: 44px; font-size: 13px; color: #606266; }
  .status-bar { flex: 1; margin: 0 12px; }
  .status-count { width: 30px; text-align: right; font-size: 13px; color: #606266; }

  .shortcut-item {
    display: flex; flex-direction: column; align-items: center;
    padding: 18px 8px; margin-bottom: 4px; cursor: pointer;
    border-radius: 8px; transition: background .2s;
    &:hover { background: #f5f7fa; }
    span { margin-top: 10px; font-size: 13px; color: #606266; }
  }
  .shortcut-icon {
    width: 48px; height: 48px; border-radius: 12px;
    display: flex; align-items: center; justify-content: center;
    color: #fff; font-size: 24px;
  }
}
</style>
