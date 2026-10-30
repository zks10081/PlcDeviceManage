<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-title">DM PLC 设备管理系统</div>
      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
        <div style="text-align: center; color: #909399; font-size: 13px">
          默认账号：admin / 123456
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  formRef.value.validate((valid) => {
    if (!valid) return
    loading.value = true
    login(form)
      .then((res) => {
        authStore.setToken(res.data.token)
        authStore.setUserInfo({
          username: res.data.username,
          nickname: res.data.nickname,
          role: res.data.role
        })
        ElMessage.success('登录成功')
        router.push('/')
      })
      .finally(() => {
        loading.value = false
      })
  })
}
</script>
