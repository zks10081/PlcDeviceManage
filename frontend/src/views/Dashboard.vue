<template>
  <div>
    <el-card>
      <template #header>系统概览</template>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-statistic title="当前用户" :value="userInfo.nickname || userInfo.username" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="角色" :value="roleText" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="日期" :value="today" />
        </el-col>
      </el-row>
      <el-alert style="margin-top: 20px" type="info" :closable="false" show-icon>
        欢迎使用 DM PLC 设备管理系统。本系统包含登录、用户管理、字典管理三大模块。
      </el-alert>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()
const userInfo = computed(() => authStore.userInfo)
const roleText = computed(() => (userInfo.value.role === 'admin' ? '管理员' : '普通用户'))
const today = new Date().toLocaleDateString('zh-CN')
</script>
