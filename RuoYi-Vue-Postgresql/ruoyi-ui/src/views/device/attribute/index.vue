<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="点位名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入点位名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备名称" prop="deviceName">
        <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="设备ID" prop="deviceId">
        <el-input v-model="queryParams.deviceId" placeholder="请输入设备ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="点位类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择点位类型" clearable filterable>
          <el-option v-for="dict in dict.type.device_attribute_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="点位状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择点位状态" clearable filterable>
          <el-option v-for="dict in dict.type.device_attribute_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['device:attribute:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['device:attribute:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['device:attribute:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['device:attribute:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="attributeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
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
      <el-table-column label="点位地址" align="center" prop="address" show-overflow-tooltip width="300"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.device_attribute_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="点位数据" align="center" prop="data" />
      <el-table-column label="采集时间" align="center" prop="collectDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.collectDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['device:attribute:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['device:attribute:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改设备点位对话框（主子列表） -->
    <el-dialog :title="title" :visible.sync="open" width="1050px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="small">
        <el-divider content-position="left">主表：点位信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="点位名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入点位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备ID" prop="deviceId">
              <el-input v-model="form.deviceId" placeholder="选择设备后自动带入" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="form.deviceName" placeholder="请输入或选择设备">
                <el-button slot="append" icon="el-icon-search" @click="openDevicePicker">选择</el-button>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item prop="controlType">
              <template slot="label">控制类型</template>
              <el-select v-model="form.controlType" placeholder="请选择控制类型" clearable filterable style="width: 100%">
                <el-option v-for="dict in dict.type.attribute_control_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="点位类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择点位类型" clearable filterable style="width: 100%">
                <el-option v-for="dict in dict.type.device_attribute_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="点位地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入点位地址" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="点位状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择点位状态" clearable filterable style="width: 100%">
                <el-option v-for="dict in dict.type.device_attribute_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="点位数据" prop="data">
              <el-input v-model="form.data" placeholder="请输入点位数据" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="临时字段1" prop="temp1">
              <el-input v-model="form.temp1" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="临时字段2" prop="temp2">
              <el-input v-model="form.temp2" placeholder="请输入" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">子列表：设备点位日志</el-divider>
      <el-alert v-if="form.id == null" title="请先保存主记录，保存后即可查看该点位产生的日志数据" type="info" :closable="false" show-icon />
      <div v-else>
        <div class="sub-toolbar">
          <span>共 {{ logTotal }} 条日志（点位ID：{{ form.id }}）</span>
          <el-button size="mini" icon="el-icon-refresh" @click="loadLog(logParams.pageNum)">刷新</el-button>
        </div>
        <el-table :data="logList" v-loading="logLoading" border stripe size="mini" max-height="260">
          <!-- <el-table-column label="设备ID" align="center" prop="deviceId" /> -->
          <el-table-column label="设备名称" align="center" prop="deviceName" />
          <!-- <el-table-column label="点位ID" align="center" prop="attributeId" /> -->
          <el-table-column label="点位名称" align="center" prop="attributeName" />
          <el-table-column label="数据" align="center" prop="data" />
          <el-table-column label="采集日期" align="center" prop="collectDate" width="160">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.collectDate) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="sub-pagination" background small layout="prev, pager, next, total"
                       :total="logTotal" :current-page="logParams.pageNum" :page-size="logParams.pageSize"
                       @current-change="loadLog" />
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="saving" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 设备选择弹窗 -->
    <el-dialog title="选择设备" :visible.sync="devicePicker.open" width="800px" append-to-body :close-on-click-modal="false">
      <el-form :model="devicePicker.query" size="small" :inline="true" label-width="68px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="devicePicker.query.name" placeholder="请输入设备名称" clearable @keyup.enter.native="loadDeviceOptions(1)" />
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="devicePicker.query.type" placeholder="请选择设备类型" clearable filterable>
            <el-option v-for="dict in dict.type.device_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="loadDeviceOptions(1)">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetDeviceQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="devicePicker.list" v-loading="devicePicker.loading" border stripe size="mini" max-height="340"
                highlight-current-row @row-click="pickDeviceRow" @row-dblclick="confirmDevicePick">
        <el-table-column label="选择" width="55" align="center">
          <template slot-scope="scope">
            <el-radio v-model="devicePicker.selectedId" :label="String(scope.row.id)">&nbsp;</el-radio>
          </template>
        </el-table-column>
        <el-table-column label="设备名称" align="center" prop="name" />
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
        <el-table-column label="IP" align="center" prop="ip" />
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.device_status" :value="scope.row.status" />
          </template>
        </el-table-column>
      </el-table>
      <el-pagination class="sub-pagination" background small layout="prev, pager, next, total"
                     :total="devicePicker.total" :current-page="devicePicker.query.pageNum" :page-size="devicePicker.query.pageSize"
                     @current-change="loadDeviceOptions" />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirmDevicePick">确 定</el-button>
        <el-button @click="devicePicker.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAttribute, getAttribute, delAttribute, addAttribute, updateAttribute } from "@/api/device/attribute"
import { listLog } from "@/api/device/log"
import { listDevice } from "@/api/device/device"

export default {
  name: "DeviceAttribute",
  dicts: ['device_attribute_type', 'attribute_control_type', 'device_attribute_status', 'device_type', 'device_subtype', 'device_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      attributeList: [],
      title: "",
      open: false,
      saving: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        deviceName: null,
        deviceId: null,
        type: null,
        status: null
      },
      form: {},
      rules: {
        name: [{ required: true, message: "请输入点位名称", trigger: "blur" }],
        deviceId: [{ required: true, message: "请输入设备ID", trigger: "blur" }]
      },
      logParams: { pageNum: 1, pageSize: 5, attributeId: null, deviceId: null },
      logList: [],
      logTotal: 0,
      logLoading: false,
      devicePicker: {
        open: false,
        loading: false,
        query: { pageNum: 1, pageSize: 8, name: null, type: null },
        list: [],
        total: 0,
        selectedId: "",
        selectedRow: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listAttribute(this.queryParams).then(response => {
        this.attributeList = response.rows
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
        id: null, name: null, deviceId: null, deviceName: null, controlType: null,
        type: null, address: null, status: null, data: null, temp1: null, temp2: null
      }
      this.logList = []
      this.logTotal = 0
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
      this.title = "新增设备点位"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAttribute(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改设备点位"
        this.loadLog(1)
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return
        this.saving = true
        const request = this.form.id != null ? updateAttribute(this.form) : addAttribute(this.form)
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
      this.$modal.confirm('是否确认删除设备点位编号为"' + ids + '"的数据项？').then(() => {
        return delAttribute(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('device/attribute/export', { ...this.queryParams }, `attribute_${new Date().getTime()}.xlsx`)
    },
    loadLog(pageNum) {
      if (this.form.id == null) return
      this.logParams.pageNum = pageNum || 1
      this.logParams.attributeId = this.form.id
      this.logParams.deviceId = this.form.deviceId || null
      this.logLoading = true
      listLog(this.logParams).then(response => {
        this.logList = response.rows
        this.logTotal = response.total
        this.logLoading = false
      }).catch(() => {
        this.logLoading = false
      })
    },
    openDevicePicker() {
      this.devicePicker.query.name = null
      this.devicePicker.query.type = null
      this.devicePicker.selectedId = ""
      this.devicePicker.selectedRow = null
      this.devicePicker.open = true
      this.loadDeviceOptions(1)
    },
    loadDeviceOptions(pageNum) {
      this.devicePicker.query.pageNum = pageNum || 1
      this.devicePicker.loading = true
      listDevice(this.devicePicker.query).then(response => {
        this.devicePicker.list = response.rows
        this.devicePicker.total = response.total
        this.devicePicker.loading = false
      }).catch(() => {
        this.devicePicker.loading = false
      })
    },
    resetDeviceQuery() {
      this.resetForm("devicePickerForm")
      this.devicePicker.query.name = null
      this.devicePicker.query.type = null
      this.loadDeviceOptions(1)
    },
    pickDeviceRow(row) {
      this.devicePicker.selectedId = String(row.id)
      this.devicePicker.selectedRow = row
    },
    confirmDevicePick() {
      const id = this.devicePicker.selectedId
      if (!id) {
        this.$modal.msgWarning("请先选择一台设备")
        return
      }
      const row = this.devicePicker.selectedRow || this.devicePicker.list.find(item => String(item.id) === id)
      if (!row) {
        this.$modal.msgWarning("所选设备不在当前页，请重新选择")
        return
      }
      this.$set(this.form, "deviceId", row.id)
      this.$set(this.form, "deviceName", row.name || "")
      this.devicePicker.open = false
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
