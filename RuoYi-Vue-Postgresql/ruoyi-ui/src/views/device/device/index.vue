<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="设备序号" prop="serialNo">
        <el-input
          v-model="queryParams.serialNo"
          placeholder="请输入设备序号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="ip" prop="ip">
        <el-input
          v-model="queryParams.ip"
          placeholder="请输入ip"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最后上线时间" prop="lastOnline">
        <el-date-picker clearable
          v-model="queryParams.lastOnline"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择最后上线时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="设备类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择设备类型" clearable>
          <el-option
            v-for="dict in dict.type.device_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['device:device:add']"
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
          v-hasPermi="['device:device:edit']"
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
          v-hasPermi="['device:device:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['device:device:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="设备序号" align="center" prop="serialNo" />
      <el-table-column label="设备名称" align="center" prop="name" />
      <el-table-column label="ip" align="center" prop="ip" />
      <el-table-column label="最后上线时间" align="center" prop="lastOnline" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastOnline, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.device_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column label="关联监控id" align="center" prop="cameraId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['device:device:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['device:device:remove']"
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

    <!-- 添加或修改设备对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="设备序号" prop="serialNo">
          <el-input v-model="form.serialNo" placeholder="请输入设备序号" />
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="ip" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入ip" />
        </el-form-item>
        <el-form-item label="端口号" prop="port">
          <el-input v-model="form.port" placeholder="请输入端口号" />
        </el-form-item>
        <el-form-item label="最后上线时间" prop="lastOnline">
          <el-date-picker clearable
            v-model="form.lastOnline"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择最后上线时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="设备类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择设备类型">
            <el-option
              v-for="dict in dict.type.device_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关联监控id" prop="cameraId">
          <el-input v-model="form.cameraId" placeholder="请输入关联监控id" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-divider content-position="center">设备点位信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAddDmDeviceAttribute">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDeleteDmDeviceAttribute">删除</el-button>
          </el-col>
        </el-row>
        <el-table :data="dmDeviceAttributeList" :row-class-name="rowDmDeviceAttributeIndex" @selection-change="handleDmDeviceAttributeSelectionChange" ref="dmDeviceAttribute">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" align="center" prop="index" width="50"/>
          <el-table-column label="控制类型" prop="controlType" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.controlType" placeholder="请选择控制类型">
                <el-option
                  v-for="dict in dict.type.attribute_control_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="点位类型" prop="type" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.type" placeholder="请选择点位类型">
                <el-option
                  v-for="dict in dict.type.device_attribute_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="点位状态" prop="status" width="150">
            <template slot-scope="scope">
              <el-select v-model="scope.row.status" placeholder="请选择点位状态">
                <el-option label="请选择字典生成" value="" />
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDevice, getDevice, delDevice, addDevice, updateDevice } from "@/api/device/device"

export default {
  name: "Device",
  dicts: ['device_type', 'attribute_control_type', 'device_attribute_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 子表选中数据
      checkedDmDeviceAttribute: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 设备表格数据
      deviceList: [],
      // 设备点位表格数据
      dmDeviceAttributeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        serialNo: null,
        name: null,
        ip: null,
        lastOnline: null,
        type: null,
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
    /** 查询设备列表 */
    getList() {
      this.loading = true
      listDevice(this.queryParams).then(response => {
        this.deviceList = response.rows
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
        serialNo: null,
        name: null,
        ip: null,
        port: null,
        lastOnline: null,
        type: null,
        subtype: null,
        cameraId: null,
        remark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      }
      this.dmDeviceAttributeList = []
      this.resetForm("form")
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加设备"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getDevice(id).then(response => {
        this.form = response.data
        this.dmDeviceAttributeList = response.data.dmDeviceAttributeList
        this.open = true
        this.title = "修改设备"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.dmDeviceAttributeList = this.dmDeviceAttributeList
          if (this.form.id != null) {
            updateDevice(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addDevice(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除设备编号为"' + ids + '"的数据项？').then(function() {
        return delDevice(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
	/** 设备点位序号 */
    rowDmDeviceAttributeIndex({ row, rowIndex }) {
      row.index = rowIndex + 1
    },
    /** 设备点位添加按钮操作 */
    handleAddDmDeviceAttribute() {
      let obj = {}
      obj.name = ""
      obj.controlType = ""
      obj.type = ""
      obj.address = ""
      obj.status = ""
      obj.data = ""
      obj.plcData = ""
      obj.collectDate = ""
      obj.temp1 = ""
      obj.temp2 = ""
      obj.remark = ""
      this.dmDeviceAttributeList.push(obj)
    },
    /** 设备点位删除按钮操作 */
    handleDeleteDmDeviceAttribute() {
      if (this.checkedDmDeviceAttribute.length == 0) {
        this.$modal.msgError("请先选择要删除的设备点位数据")
      } else {
        const dmDeviceAttributeList = this.dmDeviceAttributeList
        const checkedDmDeviceAttribute = this.checkedDmDeviceAttribute
        this.dmDeviceAttributeList = dmDeviceAttributeList.filter(function(item) {
          return checkedDmDeviceAttribute.indexOf(item.index) == -1
        })
      }
    },
    /** 复选框选中数据 */
    handleDmDeviceAttributeSelectionChange(selection) {
      this.checkedDmDeviceAttribute = selection.map(item => item.index)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('device/device/export', {
        ...this.queryParams
      }, `device_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
