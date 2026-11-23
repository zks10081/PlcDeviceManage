<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="设备名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备IP" prop="ip">
        <el-input v-model="queryParams.ip" placeholder="请输入设备IP" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择设备类型" clearable filterable>
          <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备种类" prop="subtype">
        <el-select v-model="queryParams.subtype" placeholder="请选择设备种类" clearable filterable>
          <el-option v-for="dict in dict.type.device_subtype" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择设备状态" clearable filterable>
          <el-option v-for="dict in dict.type.device_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['device:device:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['device:device:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['device:device:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['device:device:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="设备序号" align="center" prop="serialNo" />
      <el-table-column label="设备名称" align="center" prop="name" />
      <el-table-column label="IP地址" align="center" prop="ip">
        <template slot-scope="scope">
          <a v-if="scope.row.ip" :href="'http://' + scope.row.ip" target="_blank" class="link-type">{{ scope.row.ip }}</a>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="端口" align="center" prop="port" />
      <el-table-column label="设备类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.device_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="设备种类" align="center" prop="subtype">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.device_subtype" :value="scope.row.subtype" />
        </template>
      </el-table-column>
      <el-table-column label="设备状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.device_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="关联监控ID" align="center" prop="cameraId" />
      <el-table-column label="最后上线时间" align="center" prop="lastOnline" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastOnline) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['device:device:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['device:device:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改设备对话框（主子列表） -->
    <el-dialog :title="title" :visible.sync="open" width="1050px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="small">
        <el-divider content-position="left">主表：设备信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="设备序号" prop="serialNo">
              <el-input v-model="form.serialNo" placeholder="请输入设备序号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="IP地址" prop="ip">
              <el-input v-model="form.ip" placeholder="请输入IP地址" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="端口" prop="port">
              <el-input v-model="form.port" placeholder="请输入端口" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择设备类型" clearable filterable style="width: 100%">
                <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备种类" prop="subtype">
              <el-select v-model="form.subtype" placeholder="请选择设备种类" clearable filterable style="width: 100%">
                <el-option v-for="dict in dict.type.device_subtype" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择设备状态" clearable filterable style="width: 100%">
                <el-option v-for="dict in dict.type.device_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="关联监控ID" prop="cameraId">
              <el-input v-model="form.cameraId" placeholder="请输入关联监控ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8" v-if="form.id != null">
            <el-form-item label="最后上线">
              <el-input :value="parseTime(form.lastOnline)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">子列表：关联数据</el-divider>
      <el-alert v-if="form.id == null" title="请先保存主记录，保存后即可查看该设备下的点位与告警数据" type="info" :closable="false" show-icon />
      <el-tabs v-else v-model="activeSubTab" @tab-click="handleTabClick">
        <el-tab-pane label="设备点位" name="attribute">
          <div class="sub-toolbar">
            <span>共 {{ attrTotal }} 条点位数据（设备ID：{{ form.id }}）</span>
            <el-button size="mini" icon="el-icon-refresh" @click="loadAttribute(attrParams.pageNum)">刷新</el-button>
          </div>
          <el-table :data="attributeList" v-loading="attrLoading" border stripe size="mini" max-height="260">
            <el-table-column label="点位名称" align="center" prop="name" />
            <el-table-column label="设备名称" align="center" prop="deviceName" />
            <el-table-column label="控制类型" align="center" prop="controlType">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.attribute_control_type" :value="scope.row.controlType" />
              </template>
            </el-table-column>
            <el-table-column label="点位类型" align="center" prop="type">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.device_attribute_type" :value="scope.row.type" />
              </template>
            </el-table-column>
            <el-table-column label="点位地址" align="center" prop="address" />
            <el-table-column label="状态" align="center" prop="status">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.device_attribute_status" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="点位数据" align="center" prop="data" />
            <el-table-column label="PLC数据" align="center" prop="plcData" />
            <el-table-column label="采集时间" align="center" prop="collectDate" width="160">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.collectDate) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination class="sub-pagination" background small layout="prev, pager, next, total"
                         :total="attrTotal" :current-page="attrParams.pageNum" :page-size="attrParams.pageSize"
                         @current-change="loadAttribute" />
        </el-tab-pane>

        <el-tab-pane label="告警记录" name="alarm">
          <div class="sub-toolbar">
            <span>共 {{ subAlarmTotal }} 条告警数据（设备ID：{{ form.id }}）</span>
            <el-button size="mini" icon="el-icon-refresh" @click="loadSubAlarm(subAlarmParams.pageNum)">刷新</el-button>
          </div>
          <el-table :data="subAlarmList" v-loading="subAlarmLoading" border stripe size="mini" max-height="260">
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
            <el-table-column label="告警内容" align="center" prop="content" />
            <el-table-column label="点位名称" align="center" prop="attributeName" />
            <el-table-column label="点位值" align="center" prop="attributeData" />
            <el-table-column label="报警时间" align="center" prop="alarmTime" width="160">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.alarmTime) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination class="sub-pagination" background small layout="prev, pager, next, total"
                         :total="subAlarmTotal" :current-page="subAlarmParams.pageNum" :page-size="subAlarmParams.pageSize"
                         @current-change="loadSubAlarm" />
        </el-tab-pane>
      </el-tabs>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="saving" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDevice, getDevice, delDevice, addDevice, updateDevice } from "@/api/device/device"
import { listAttribute } from "@/api/device/attribute"
import { listAlarm } from "@/api/device/alarm"

export default {
  name: "Device",
  dicts: ['device_type', 'device_subtype', 'device_status', 'device_attribute_type', 'attribute_control_type', 'device_attribute_status', 'alarm_level', 'alarm_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      deviceList: [],
      title: "",
      open: false,
      saving: false,
      activeSubTab: "attribute",
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        ip: null,
        type: null,
        subtype: null,
        status: null
      },
      form: {},
      rules: {
        serialNo: [{ required: true, message: "请输入设备序号", trigger: "blur" }],
        name: [{ required: true, message: "请输入设备名称", trigger: "blur" }]
      },
      attrParams: { pageNum: 1, pageSize: 5, deviceId: null },
      attributeList: [],
      attrTotal: 0,
      attrLoading: false,
      subAlarmParams: { pageNum: 1, pageSize: 5, deviceId: null },
      subAlarmList: [],
      subAlarmTotal: 0,
      subAlarmLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listDevice(this.queryParams).then(response => {
        this.deviceList = response.rows
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
        id: null, serialNo: null, name: null, ip: null, port: null,
        type: null, subtype: null, status: null, cameraId: null
      }
      this.attributeList = []
      this.subAlarmList = []
      this.activeSubTab = "attribute"
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
      this.title = "新增设备"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getDevice(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改设备"
        this.loadAttribute(1)
        this.loadSubAlarm(1)
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return
        this.saving = true
        const request = this.form.id != null ? updateDevice(this.form) : addDevice(this.form)
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
      this.$modal.confirm('是否确认删除设备编号为"' + ids + '"的数据项？').then(() => {
        return delDevice(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('device/device/export', { ...this.queryParams }, `device_${new Date().getTime()}.xlsx`)
    },
    handleTabClick(tab) {
      if (tab.name === "attribute") this.loadAttribute(this.attrParams.pageNum)
      if (tab.name === "alarm") this.loadSubAlarm(this.subAlarmParams.pageNum)
    },
    loadAttribute(pageNum) {
      if (this.form.id == null) return
      this.attrParams.pageNum = pageNum || 1
      this.attrParams.deviceId = this.form.id
      this.attrLoading = true
      listAttribute(this.attrParams).then(response => {
        this.attributeList = response.rows
        this.attrTotal = response.total
        this.attrLoading = false
      }).catch(() => {
        this.attrLoading = false
      })
    },
    loadSubAlarm(pageNum) {
      if (this.form.id == null) return
      this.subAlarmParams.pageNum = pageNum || 1
      this.subAlarmParams.deviceId = this.form.id
      this.subAlarmLoading = true
      listAlarm(this.subAlarmParams).then(response => {
        this.subAlarmList = response.rows
        this.subAlarmTotal = response.total
        this.subAlarmLoading = false
      }).catch(() => {
        this.subAlarmLoading = false
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
.link-type {
  color: #409eff;
}
</style>
