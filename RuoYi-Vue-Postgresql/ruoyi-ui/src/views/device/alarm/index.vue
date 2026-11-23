<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="告警标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入告警标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备名称" prop="deviceName">
        <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="告警类型" prop="type">
        <el-input v-model="queryParams.type" placeholder="请输入告警类型" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="告警等级" prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择告警等级" clearable filterable>
          <el-option v-for="dict in dict.type.alarm_level" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择处理状态" clearable filterable>
          <el-option v-for="dict in dict.type.alarm_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['device:alarm:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['device:alarm:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['device:alarm:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['device:alarm:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="告警标题" align="center" prop="title" />
      <el-table-column label="告警类型" align="center" prop="type" />
      <el-table-column label="告警等级" align="center" prop="level">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_level" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="告警内容" align="center" prop="content" show-overflow-tooltip />
      <el-table-column label="设备ID" align="center" prop="deviceId" />
      <el-table-column label="设备名称" align="center" prop="deviceName" />
      <el-table-column label="点位名称" align="center" prop="attributeName" />
      <el-table-column label="报警点位值" align="center" prop="attributeData" />
      <el-table-column label="通知用户" align="center" prop="userIds" />
      <el-table-column label="查看状态" align="center" prop="userFlag" />
      <el-table-column label="报警时间" align="center" prop="alarmTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alarmTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['device:alarm:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['device:alarm:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改告警对话框（主子列表） -->
    <el-dialog :title="title" :visible.sync="open" width="1050px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="small">
        <el-divider content-position="left">主表：告警信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="告警标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入告警标题" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="告警类型" prop="type">
              <el-input v-model="form.type" placeholder="请输入告警类型" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="告警等级" prop="level">
              <el-select v-model="form.level" placeholder="请选择告警等级" clearable filterable style="width: 100%">
                <el-option v-for="dict in dict.type.alarm_level" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="处理状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择处理状态" clearable filterable style="width: 100%">
                <el-option v-for="dict in dict.type.alarm_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备ID" prop="deviceId">
              <el-input v-model="form.deviceId" placeholder="请输入设备ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="点位ID" prop="attributeId">
              <el-input v-model="form.attributeId" placeholder="请输入点位ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="点位名称" prop="attributeName">
              <el-input v-model="form.attributeName" placeholder="请输入点位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="报警点位值" prop="attributeData">
              <el-input v-model="form.attributeData" placeholder="请输入报警点位值" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="通知用户" prop="userIds">
              <el-input v-model="form.userIds" placeholder="请输入通知用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="查看状态" prop="userFlag">
              <el-input v-model="form.userFlag" placeholder="请输入查看状态" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="告警内容" prop="content">
              <el-input type="textarea" :rows="2" v-model="form.content" placeholder="请输入告警内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">子列表：告警点位快照</el-divider>
      <el-alert v-if="form.id == null" title="请先保存主记录，保存后即可查看该告警关联的点位快照" type="info" :closable="false" show-icon />
      <div v-else>
        <div class="sub-toolbar">
          <span>共 {{ snapTotal }} 条快照（告警ID：{{ form.id }}）</span>
          <el-button size="mini" icon="el-icon-refresh" @click="loadSnap(snapParams.pageNum)">刷新</el-button>
        </div>
        <el-table :data="snapList" v-loading="snapLoading" border stripe size="mini" max-height="260">
          <el-table-column label="告警ID" align="center" prop="alarmId" />
          <el-table-column label="告警标题" align="center" prop="alarmName" />
          <el-table-column label="设备ID" align="center" prop="deviceId" />
          <el-table-column label="设备名称" align="center" prop="deviceName" />
          <el-table-column label="点位名称" align="center" prop="attributeName" />
          <el-table-column label="数据" align="center" prop="data" />
          <el-table-column label="快照编号" align="center" prop="snapsshootNo" />
          <el-table-column label="告警ID集合" align="center" prop="alarmIds" />
          <el-table-column label="采集日期" align="center" prop="collectDate" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.collectDate) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="sub-pagination" background small layout="prev, pager, next, total"
                       :total="snapTotal" :current-page="snapParams.pageNum" :page-size="snapParams.pageSize"
                       @current-change="loadSnap" />
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="saving" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAlarm, getAlarm, delAlarm, addAlarm, updateAlarm } from "@/api/device/alarm"
import { listSnapshoot } from "@/api/device/snapshoot"

export default {
  name: "Alarm",
  dicts: ['alarm_level', 'alarm_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      alarmList: [],
      title: "",
      open: false,
      saving: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        deviceName: null,
        type: null,
        level: null,
        status: null
      },
      form: {},
      rules: {
        title: [{ required: true, message: "请输入告警标题", trigger: "blur" }]
      },
      snapParams: { pageNum: 1, pageSize: 5, alarmId: null },
      snapList: [],
      snapTotal: 0,
      snapLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listAlarm(this.queryParams).then(response => {
        this.alarmList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null, title: null, type: null, level: null, status: null, content: null,
        deviceId: null, deviceName: null, attributeId: null, attributeName: null,
        attributeData: null, userIds: null, userFlag: null
      }
      this.snapList = []
      this.snapTotal = 0
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增告警"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAlarm(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改告警"
        this.loadSnap(1)
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return
        this.saving = true
        const request = this.form.id != null ? updateAlarm(this.form) : addAlarm(this.form)
        request.then(() => {
          this.saving = false
          this.$modal.msgSuccess(this.form.id != null ? "修改成功" : "新增成功")
          this.open = false
          this.getList()
        }).catch(() => {
          this.saving = false
        })
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除告警编号为"' + ids + '"的数据项？').then(() => {
        return delAlarm(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('device/alarm/export', { ...this.queryParams }, `alarm_${new Date().getTime()}.xlsx`)
    },
    loadSnap(pageNum) {
      if (this.form.id == null) return
      this.snapParams.pageNum = pageNum || 1
      this.snapParams.alarmId = this.form.id
      this.snapLoading = true
      listSnapshoot(this.snapParams).then(response => {
        this.snapList = response.rows
        this.snapTotal = response.total
        this.snapLoading = false
      }).catch(() => {
        this.snapLoading = false
      })
    }
  }
}
</script>

<style scoped>
.sub-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #606266;
  font-size: 13px;
}
.sub-pagination {
  margin-top: 8px;
  text-align: right;
}
</style>
