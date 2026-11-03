<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="告警等级" prop="level">
        <el-input
          v-model="queryParams.level"
          placeholder="请输入告警等级"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择处理状态" clearable>
          <el-option
            v-for="dict in dict.type.alarm_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="设备名称" prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="点位名称" prop="attributeName">
        <el-input
          v-model="queryParams.attributeName"
          placeholder="请输入点位名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报警时间" prop="alarmTime">
        <el-date-picker clearable
          v-model="queryParams.alarmTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择报警时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['device:alarm:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['device:alarm:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['device:alarm:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['device:alarm:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="告警标题" align="center" prop="title" />
      <el-table-column label="告警等级" align="center" prop="level">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_level" :value="scope.row.level"/>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="设备名称" align="center" prop="deviceName" />
      <el-table-column label="点位名称" align="center" prop="attributeName" />
      <el-table-column label="报警时间" align="center" prop="alarmTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alarmTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['device:alarm:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['device:alarm:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改告警对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="alarmRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="告警标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入告警标题" />
        </el-form-item>
        <el-form-item label="告警等级" prop="level">
          <el-input v-model="form.level" placeholder="请输入告警等级" />
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.alarm_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="告警内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="报警设备" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入报警设备" />
        </el-form-item>
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="点位id" prop="attributeId">
          <el-input v-model="form.attributeId" placeholder="请输入点位id" />
        </el-form-item>
        <el-form-item label="点位名称" prop="attributeName">
          <el-input v-model="form.attributeName" placeholder="请输入点位名称" />
        </el-form-item>
        <el-form-item label="报警点位值" prop="attributeData">
          <el-input v-model="form.attributeData" placeholder="请输入报警点位值" />
        </el-form-item>
        <el-form-item label="通知用户" prop="userIds">
          <el-input v-model="form.userIds" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="查看状态" prop="userFlag">
          <el-input v-model="form.userFlag" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="报警时间" prop="alarmTime">
          <el-date-picker clearable
            v-model="form.alarmTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择报警时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAlarm, getAlarm, delAlarm, addAlarm, updateAlarm } from "@/api/device/alarm"

export default {
  name: "Alarm",
  dicts: ['alarm_level', 'alarm_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 告警表格数据
      alarmList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        level: null,
        status: null,
        deviceName: null,
        attributeName: null,
        alarmTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询告警列表 */
    getList() {
      this.loading = true
      listAlarm(this.queryParams).then(response => {
        this.alarmList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        title: null,
        type: null,
        level: null,
        status: null,
        content: null,
        deviceId: null,
        deviceName: null,
        attributeId: null,
        attributeName: null,
        attributeData: null,
        userIds: null,
        userFlag: null,
        alarmTime: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      }
      this.resetForm("alarmRef")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加告警"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAlarm(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改告警"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["alarmRef"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAlarm(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAlarm(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除告警编号为"' + ids + '"的数据项？').then(function() {
        return delAlarm(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('device/alarm/export', {
        ...this.queryParams
      }, `alarm_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
