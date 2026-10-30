<template>
  <el-container style="height: 100vh">
    <el-aside width="210px" style="background: #001529">
      <div style="height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; font-weight: 600">
        DM PLC 管理系统
      </div>
      <el-menu
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409EFF"
        :default-active="activeMenu"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/dicts">
          <el-icon><Collection /></el-icon>
          <span>字典管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #eee">
        <span style="font-size: 18px; color: #303133">{{ title }}</span>
        <el-dropdown @command="handleCommand">
          <span style="cursor: pointer">
            {{ authStore.userInfo.nickname || authStore.userInfo.username }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main style="background: #f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { Odometer, User, Collection, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
const title = computed(() => route.meta.title || '')

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
  }
}
</script>
