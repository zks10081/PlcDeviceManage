<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="用户名称" prop="username">
        <el-input v-model="queryParams.username" placeholder="请输入用户名称/号码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户类型" prop="partition">
        <el-select v-model="queryParams.partition" placeholder="请选择用户类型" clearable filterable>
          <el-option v-for="item in partitionOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="在线状态" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择在线状态" clearable>
          <el-option label="在线" value="1" />
          <el-option label="离线" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery" v-hasPermi="['user:dmUser:query']">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery" v-hasPermi="['user:dmUser:query']">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-refresh" size="mini" :loading="syncLoading"
                   @click="handleSync" v-hasPermi="['user:dmUser:sync']">同步数据</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userList">
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="用户名称" align="center" prop="username" />
      <el-table-column label="用户号码" align="center" prop="usernum" />
      <el-table-column label="用户类型" align="center" prop="partition">
        <template slot-scope="scope">
          <span>{{ partitionLabel(scope.row.partition) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="级别" align="center" prop="level" />
      <el-table-column label="是否录音" align="center" prop="recording" />
      <el-table-column label="摄像头IP" align="center" prop="cameraIp" />
      <el-table-column label="摄像头端口" align="center" prop="cameraPort" />
      <el-table-column label="摄像头账号" align="center" prop="cameraAccount" />
      <el-table-column label="摄像头状态" align="center" prop="cameraStatus" />
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listDmUser, syncUserData } from "@/api/user/dmUser"

export default {
  name: "DmUser",
  data() {
    return {
      loading: true,
      syncLoading: false,
      showSearch: true,
      total: 0,
      userList: [],
      partitionOptions: [
        { label: '座机', value: '座机' },
        { label: '手机', value: '手机' },
        { label: '广播', value: '广播' },
        { label: '外机', value: '外机' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        username: null,
        partition: null,
        type: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    partitionLabel(value) {
      const item = this.partitionOptions.find(option => option.value === value)
      return item ? item.label : value
    },
    getList() {
      this.loading = true
      listDmUser(this.queryParams).then(response => {
        this.userList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSync() {
      this.$modal.confirm('确认从调度系统同步SIP用户数据？同步将根据远程数据新增、更新或删除本地用户。').then(() => {
        this.syncLoading = true
        return syncUserData()
      }).then(() => {
        this.syncLoading = false
        this.$modal.msgSuccess("同步成功")
        this.getList()
      }).catch(() => {
        this.syncLoading = false
      })
    }
  }
}
</script>
