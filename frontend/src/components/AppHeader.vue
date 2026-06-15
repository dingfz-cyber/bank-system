<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUnreadCount, getMessages, readAllMessages, readMessage } from '@/api/message'

const router = useRouter()
const userStore = useUserStore()
const unread = ref(0)
const messages = ref([])

async function fetchUnread() {
  if (!userStore.isLoggedIn) return
  const res = await getUnreadCount(); if (res.code === 200) unread.value = res.data.count
}

async function loadMessages() {
  const res = await getMessages(); if (res.code === 200) messages.value = res.data
}

async function readOne(msg) {
  await readMessage(msg.id); unread.value = Math.max(0, unread.value - 1)
  const res = await getMessages(); if (res.code === 200) messages.value = res.data
}

async function readAll() { await readAllMessages(); unread.value = 0; const res = await getMessages(); if (res.code === 200) messages.value = res.data }

function handleLogout() { userStore.logout(); router.push({ name: 'home' }) }
function goLogin() { router.push({ name: 'login' }) }

const isDark = ref(localStorage.getItem('darkMode') === 'true')
function toggleDark() { isDark.value = !isDark.value; document.documentElement.classList.toggle('dark', isDark.value); localStorage.setItem('darkMode', isDark.value) }

onMounted(() => { fetchUnread(); setInterval(fetchUnread, 30000); if(localStorage.getItem('darkMode')==='true') document.documentElement.classList.add('dark') })
</script>

<template>
  <header class="app-header">
    <div class="header-inner">
      <div class="header-left">
        <router-link to="/" class="logo">网银系统</router-link>
        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/personal" class="nav-item">个人业务</router-link>
          <router-link to="/credit" class="nav-item">银行卡</router-link>
          <router-link to="/company" class="nav-item">公司金融</router-link>
          <router-link to="/puhui" class="nav-item">普惠金融</router-link>
          <router-link to="/transfer" class="nav-item">转账汇款</router-link>
          <router-link to="/payment" class="nav-item">生活缴费</router-link>
          <router-link to="/branches" class="nav-item">网点查询</router-link>
          <router-link to="/news" class="nav-item">新闻公告</router-link>
          <router-link to="/info" class="nav-item">信息公开</router-link>
        </nav>
      </div>
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <span class="user-info">欢迎，{{ userStore.nickName }}
            <el-popover v-if="userStore.roleId === 5" placement="bottom" :width="260" trigger="click">
              <template #reference>
                <span style="color:#e6a23c;font-size:12px;cursor:pointer">⭐{{ userStore.points || 0 }}</span>
              </template>
              <div style="font-size:13px;line-height:2">
                <b>积分规则</b>
                <div>📝 注册账号 +50</div>
                <div>🔐 每日登录 +5</div>
                <div>💳 办理业务 +10</div>
                <el-divider style="margin:8px 0"/>
                <div style="color:#909399">积分可兑换手续费减免等权益</div>
              </div>
            </el-popover>
          </span>

          <!-- 消息通知 -->
          <el-popover placement="bottom" :width="340" trigger="click" @show="loadMessages" :persistent="true" :hide-after="0">
            <template #reference>
              <el-badge :value="unread" :hidden="!unread" style="margin-right:8px;cursor:pointer">
                <span style="font-size:18px">🔔</span>
              </el-badge>
            </template>
            <div style="max-height:300px;overflow-y:auto">
              <div v-if="!messages.length" style="text-align:center;color:#999;padding:20px">暂无消息</div>
              <div v-for="m in messages" :key="m.id" style="padding:8px 0;border-bottom:1px solid #f0f0f0;cursor:pointer" :style="{fontWeight:m.isRead? 'normal':'bold'}" @click="readOne(m)">
                <div>{{ m.title }}</div>
                <div style="font-size:12px;color:#999">{{ m.content?.slice(0,40) }}...</div>
              </div>
            </div>
            <div v-if="messages.length" style="text-align:center;margin-top:8px"><el-button size="small" text @click="readAll">全部已读</el-button></div>
          </el-popover>

          <el-button v-if="userStore.roleId === 5" type="info" size="small" @click="router.push('/user/center')">
            个人中心
          </el-button>
          <el-button v-if="userStore.isAdmin" type="warning" size="small" @click="router.push('/admin')">
            后台管理
          </el-button>
          <span @click="toggleDark" style="cursor:pointer;font-size:18px;margin-right:8px" :title="isDark?'切换亮色':'切换深色'">{{ isDark ? '☀️' : '🌙' }}</span>
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
