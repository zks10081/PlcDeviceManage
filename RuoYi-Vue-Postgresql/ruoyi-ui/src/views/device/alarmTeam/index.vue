<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="队伍名称" prop="teamName">
        <el-input v-model="queryParams.teamName" placeholder="请输入队伍名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.team_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="负责人" prop="leaderName">
        <el-input v-model="queryParams.leaderName" placeholder="请输入负责人" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['device:alarmTeam:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['device:alarmTeam:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['device:alarmTeam:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmTeamList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="队伍名称" align="center" prop="teamName" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.team_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="负责人" align="center" prop="leaderName" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['device:alarmTeam:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['device:alarmTeam:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增或修改告警队伍 -->
    <el-dialog :title="title" :visible.sync="open" width="920px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" size="small">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基础信息" name="base">
            <el-row :gutter="16">
              <el-col :span="24">
                <el-form-item label="队伍名称" prop="teamName">
                  <el-input v-model="form.teamName" placeholder="请输入队伍名称" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="form.status" placeholder="请选择状态" clearable style="width: 100%">
                    <el-option v-for="dict in dict.type.team_status" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="负责人" prop="leaderId">
                  <el-select v-model="form.leaderId" filterable clearable placeholder="请选择负责人" style="width: 100%" @change="onLeaderChange">
                    <el-option v-for="user in leaderOptions" :key="user.value" :label="user.label" :value="user.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input type="textarea" :rows="3" v-model="form.remark" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <el-tab-pane label="成员选择" name="user">
            <div class="selected-bar">
              <span class="selected-bar-label">已选用户</span>
              <div class="selected-tags">
                <el-tag v-for="tag in selectedUsers" :key="tag" closable size="small" @close="removeSelectedUser(tag)">{{ tag }}</el-tag>
                <span v-if="!selectedUsers.length" class="selected-empty">暂无已选用户</span>
              </div>
            </div>
            <div class="pane-tip">通过下方分类切换成员数据源；顶部「已选用户」始终展示全部分类的已选成员。</div>
            <div class="stat-bar">当前分类：{{ userCategory }} ｜ 全部已选：{{ selectedUsers.length }} 人 / 本类可选：{{ userOptions.length }} 人</div>
            <div class="transfer-wrap">
              <el-transfer filterable filter-placeholder="请输入成员号码" :titles="['所有用户', '已选用户']"
                           v-model="selectedUsers" :data="userOptions">
                <div slot="left-footer" class="category-footer">
                  <el-button v-for="type in userCategories" :key="type" size="mini" type="primary" style="margin: 0 2px;"
                             :plain="userCategory !== type" @click="switchUserCategory(type)">{{ type }}</el-button>
                </div>
              </el-transfer>
            </div>
          </el-tab-pane>

          <el-tab-pane label="设备选择" name="device">
            <div class="selected-bar">
              <span class="selected-bar-label">已选设备</span>
              <div class="selected-tags">
                <el-tag v-for="device in selectedDevices" :key="device.id" closable size="small" @close="removeSelectedDevice(device)">{{ device.label }}</el-tag>
                <span v-if="!selectedDevices.length" class="selected-empty">暂无已选设备</span>
              </div>
            </div>
            <div class="pane-tip">按设备类型分组，勾选设备即加入该队伍；「已关联」表示编辑前已属于本队伍。</div>
            <div class="stat-bar">当前已勾选设备：{{ checkedDeviceCount }} 台</div>
            <el-tree ref="deviceTree" class="device-tree" :data="deviceTree" :props="treeProps"
                     node-key="id" show-checkbox default-expand-all :default-checked-keys="checkedDeviceKeys"
                     @check="onDeviceCheck">
              <span class="tree-node" slot-scope="{ node, data }">
                <span>{{ node.label }}</span>
                <span v-if="!data.children" class="tree-node-tag" :class="{ linked: data.isOldChoose === 'true' }">
                  {{ data.isOldChoose === 'true' ? '已关联' : '未关联' }}
                </span>
              </span>
            </el-tree>
          </el-tab-pane>
        </el-tabs>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="saving" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listAlarmTeam, getAlarmTeam, delAlarmTeam, addAlarmTeam, updateAlarmTeam,
  getAlarmUserFlag, getAlarmDeviceFlag,
  listTransferLandline, listTransferPhone, listTransferBroadcast, listTransferOuter,
  listLeaderOptions
} from "@/api/device/alarmTeam"

export default {
  name: "AlarmTeam",
  dicts: ['team_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      alarmTeamList: [],
      title: "",
      open: false,
      saving: false,
      activeTab: "base",
      queryParams: { pageNum: 1, pageSize: 10, teamName: null, status: null, leaderName: null },
      form: {},
      rules: {
        teamName: [{ required: true, message: "请输入队伍名称", trigger: "blur" }]
      },
      leaderOptions: [],
      userCategories: ['座机', '手机', '广播', '外机'],
      userCategory: '座机',
      userOptionsByType: {},
      userTypeMap: {},
      userOptions: [],
      selectedUsers: [],
      deviceRaw: {},
      deviceTree: [],
      deviceLeafMap: {},
      selectedDevices: [],
      checkedDeviceKeys: [],
      checkedDeviceCount: 0,
      treeProps: { children: 'children', label: 'label' }
    }
  },
  created() {
    this.getList()
    this.loadLeaderOptions()
    this.loadUserOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listAlarmTeam(this.queryParams).then(response => {
        this.alarmTeamList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = { id: null, teamName: null, status: null, leaderId: null, leaderName: null, remark: null }
      this.selectedUsers = []
      this.selectedDevices = []
      this.deviceTree = []
      this.checkedDeviceKeys = []
      this.checkedDeviceCount = 0
      this.activeTab = "base"
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
      this.userCategory = "座机"
      this.applyUserCategory()
      this.open = true
      this.title = "新增告警队伍"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAlarmTeam(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改告警队伍"
        this.loadFlags(id)
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) {
          this.activeTab = "base"
          return
        }
        this.saving = true
        const payload = {
          team: {
            id: this.form.id,
            teamName: this.form.teamName,
            status: this.form.status,
            leaderId: this.form.leaderId,
            leaderName: this.form.leaderName,
            remark: this.form.remark
          },
          device: this.collectDevicePayload(),
          user: this.selectedUsers.map(String)
        }
        const request = this.form.id != null ? updateAlarmTeam(payload) : addAlarmTeam(payload)
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
      this.$modal.confirm('是否确认删除告警队伍编号为"' + ids + '"的数据项？').then(() => {
        return delAlarmTeam(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    loadLeaderOptions() {
      listLeaderOptions().then(response => {
        this.leaderOptions = response.data || []
      }).catch(() => {
        this.leaderOptions = []
      })
    },
    onLeaderChange(value) {
      const selected = this.leaderOptions.find(item => String(item.value) === String(value))
      this.$set(this.form, "leaderName", selected ? selected.label : "")
    },
    loadUserOptions() {
      const apis = {
        '座机': listTransferLandline,
        '手机': listTransferPhone,
        '广播': listTransferBroadcast,
        '外机': listTransferOuter
      }
      Promise.all(this.userCategories.map(type =>
        apis[type]().then(response => ({ type: type, list: response.data || [] }))
          .catch(() => ({ type: type, list: [] }))
      )).then(results => {
        const byType = {}
        const typeMap = {}
        results.forEach(result => {
          byType[result.type] = (result.list || []).map(item => ({
            key: String(item.key),
            label: item.label == null ? String(item.key) : String(item.label)
          }))
          byType[result.type].forEach(item => { typeMap[item.key] = result.type })
        })
        this.userOptionsByType = byType
        this.userTypeMap = typeMap
        this.applyUserCategory()
      })
    },
    switchUserCategory(type) {
      this.userCategory = type
      this.applyUserCategory()
    },
    applyUserCategory() {
      const list = (this.userOptionsByType[this.userCategory] || []).slice()
      const seen = new Set(list.map(item => item.key))
      this.selectedUsers.forEach(key => {
        const type = this.userTypeMap[key]
        if ((!type || type === this.userCategory) && !seen.has(key)) {
          list.push({ key: key, label: key })
          seen.add(key)
        }
      })
      this.userOptions = list
    },
    removeSelectedUser(tag) {
      this.selectedUsers = this.selectedUsers.filter(item => item !== tag)
      this.applyUserCategory()
    },
    loadFlags(teamId) {
      this.userCategory = "座机"
      this.selectedUsers = []
      this.applyUserCategory()
      this.deviceRaw = {}
      this.deviceTree = []
      this.deviceLeafMap = {}
      this.selectedDevices = []
      this.checkedDeviceKeys = []
      this.checkedDeviceCount = 0

      getAlarmUserFlag(teamId).then(response => {
        this.selectedUsers = (response.data || []).map(String)
        this.applyUserCategory()
      }).catch(() => {
        this.selectedUsers = []
      })

      getAlarmDeviceFlag(teamId).then(response => {
        this.deviceRaw = response.data || {}
        this.buildDeviceTree()
      }).catch(() => {
        this.deviceRaw = {}
        this.buildDeviceTree()
      })
    },
    buildDeviceTree() {
      const tree = []
      const checked = []
      const leafMap = {}
      Object.keys(this.deviceRaw).forEach((type, index) => {
        const children = (this.deviceRaw[type] || []).map(device => {
          const id = String(device.id)
          const label = device.name || id
          leafMap[id] = label
          if (String(device.isChoose) === 'true') checked.push(id)
          return { id: id, label: label, isOldChoose: String(device.isOldChoose) }
        })
        tree.push({ id: '__type_' + index, label: type + '（' + children.length + '）', children: children })
      })
      this.deviceLeafMap = leafMap
      this.deviceTree = tree
      this.checkedDeviceKeys = checked
      this.checkedDeviceCount = checked.length
      this.selectedDevices = checked.map(id => ({ id: id, label: leafMap[id] || id }))
      this.$nextTick(() => {
        if (this.$refs.deviceTree) this.$refs.deviceTree.setCheckedKeys(checked)
        this.syncSelectedDevices()
      })
    },
    syncSelectedDevices() {
      const keys = this.$refs.deviceTree
        ? this.$refs.deviceTree.getCheckedKeys(false).map(String)
        : this.checkedDeviceKeys.map(String)
      const ids = keys.filter(key => key.indexOf('__type_') !== 0)
      this.selectedDevices = ids.map(id => ({ id: id, label: this.deviceLeafMap[id] || id }))
      this.checkedDeviceCount = ids.length
    },
    onDeviceCheck() {
      this.syncSelectedDevices()
    },
    removeSelectedDevice(device) {
      if (this.$refs.deviceTree) this.$refs.deviceTree.setChecked(device.id, false, true)
      this.selectedDevices = this.selectedDevices.filter(item => item.id !== device.id)
      this.checkedDeviceCount = this.selectedDevices.length
    },
    collectDevicePayload() {
      const checked = new Set(this.$refs.deviceTree
        ? this.$refs.deviceTree.getCheckedKeys(false).map(String)
        : [])
      const payload = {}
      Object.keys(this.deviceRaw).forEach(type => {
        payload[type] = (this.deviceRaw[type] || []).map(device => ({
          id: device.id,
          name: device.name,
          count: device.count,
          isOldChoose: device.isOldChoose,
          isChoose: checked.has(String(device.id)) ? 'true' : 'false'
        }))
      })
      return payload
    }
  }
}
</script>

<style scoped>
.selected-bar {
  display: flex;
  align-items: flex-start;
  padding: 4px 0 12px;
  margin-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}
.selected-bar-label {
  flex: 0 0 76px;
  color: #303133;
  font-size: 14px;
  line-height: 32px;
}
.selected-tags {
  flex: 1;
  min-height: 32px;
}
.selected-tags .el-tag {
  margin: 0 8px 8px 0;
}
.selected-empty, .pane-tip {
  color: #909399;
  font-size: 12px;
  line-height: 32px;
}
.stat-bar {
  margin-bottom: 10px;
  color: #606266;
  font-size: 13px;
}
.transfer-wrap {
  display: flex;
  justify-content: center;
}
.transfer-wrap .el-transfer-panel {
  width: 340px;
}
/* 分类切换按钮放在左面板 footer，压缩间距确保 4 个按钮完整显示 */
.transfer-wrap /deep/ .el-transfer-panel__footer {
  padding: 8px 10px;
}
.category-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.category-footer .el-button {
  margin: 0 2px;
  padding: 6px 3px;
  flex: 1 1 0;
}
.device-tree {
  max-height: 360px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}
.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 10px;
}
.tree-node-tag {
  font-size: 12px;
  color: #909399;
}
.tree-node-tag.linked {
  color: #67c23a;
}
</style>
