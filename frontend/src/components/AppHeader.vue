<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  router.push({ name: 'home' })
}

function goLogin() {
  router.push({ name: 'login' })
}
</script>

<template>
  <header class="app-header">
    <div class="header-inner">
      <div class="header-left">
        <router-link to="/" class="logo">网银系统</router-link>
        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/personal" class="nav-item">个人业务</router-link>
          <router-link to="/credit" class="nav-item">信用卡</router-link>
          <router-link to="/company" class="nav-item">公司金融</router-link>
          <router-link to="/puhui" class="nav-item">普惠金融</router-link>
          <router-link to="/info" class="nav-item">信息公开</router-link>
        </nav>
      </div>
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <span class="user-info">欢迎，{{ userStore.nickName }}</span>
          <el-button v-if="userStore.isAdmin" type="warning" size="small" @click="router.push('/admin')">
            后台管理
          </el-button>
          <el-button type="primary" size="small" @click="handleLogout">退出</el-button>
        </template>
        <template v-else>
          <el-button type="primary" size="small" @click="goLogin">登录</el-button>
          <el-button size="small" @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  width: 100%;
  background: var(--color-white);
  border-bottom: 1px solid var(--color-border);
  height: var(--header-height);
  flex-shrink: 0;
}
.header-inner {
  max-width: 1200px;
  height: 100%;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 32px;
}
.logo {
  font-size: 22px;
  font-weight: bold;
  color: var(--color-primary);
  text-decoration: none;
  white-space: nowrap;
  flex-shrink: 0;
}
.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
}
.nav-item {
  padding: 0 14px;
  font-size: 15px;
  color: var(--color-text-regular);
  text-decoration: none;
  height: var(--header-height);
  line-height: var(--header-height);
  border-bottom: 2px solid transparent;
  transition: color 0.2s, border-color 0.2s;
  white-space: nowrap;
}
.nav-item:hover,
.nav-item.router-link-active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}
.user-info {
  font-size: 14px;
  color: var(--color-text-regular);
  white-space: nowrap;
}
</style>
