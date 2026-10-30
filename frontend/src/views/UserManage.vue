<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="query" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="query.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button v-if="authStore.isAdmin" type="success" :icon="Plus" @click="openDialog()">新增用户</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : ''">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="160" v-if="authStore.isAdmin">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 16px; justify-content: flex-end"
        layout="total, sizes, prev, pager, next"
        :total="total"
        :page-size="query.size"
        :current-page="query.page"
        :page-sizes="[10, 20, 50]"
        @current-change="(p) => { query.page = p; loadData() }"
        @size-change="(s) => { query.size = s; loadData() }"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password
                    :placeholder="form.id ? '留空则不修改' : '默认 123456'" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="Number(o.value)" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getUserPage, createUser, updateUser, deleteUser } from '@/api/user'
import { getDictDataByType } from '@/api/dict'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()
const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const statusOptions = ref([])
const statusMap = ref({})

const query = reactive({ username: '', nickname: '', page: 1, size: 10 })
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({ id: null, username: '', password: '', nickname: '', email: '', phone: '', role: 'user', status: 1 })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
}

const statusLabel = (val) => statusMap.value[val] || (val === 1 ? '启用' : '禁用')

const loadData = () => {
  loading.value = true
  getUserPage(query)
    .then((res) => {
      list.value = res.data.list
      total.value = res.data.total
    })
    .finally(() => {
      loading.value = false
    })
}

const resetQuery = () => {
  query.username = ''
  query.nickname = ''
  query.page = 1
  loadData()
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, {
      id: row.id, username: row.username, password: '', nickname: row.nickname,
      email: row.email, phone: row.phone, role: row.role, status: row.status
    })
  } else {
    Object.assign(form, { id: null, username: '', password: '', nickname: '', email: '', phone: '', role: 'user', status: 1 })
  }
  dialogVisible.value = true
}

const submit = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    submitting.value = true
    const api = form.id ? updateUser : createUser
    const payload = { ...form }
    if (form.id && !payload.password) delete payload.password
    api(payload)
      .then(() => {
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      })
      .finally(() => {
        submitting.value = false
      })
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除用户「${row.username}」？`, '提示', { type: 'warning' })
    .then(() => deleteUser(row.id))
    .then(() => {
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(() => {
  loadData()
  getDictDataByType('sys_status').then((res) => {
    statusOptions.value = res.data
    res.data.forEach((d) => { statusMap.value[d.value] = d.label })
  })
})
</script>
