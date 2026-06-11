<script setup>
import { ref } from 'vue'
import { login, smsLogin } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const loginMode = ref('password')
const form = ref({ phone: '', password: '', code: '' })
const loading = ref(false)

async function doLogin(res) {
  if (res.code === 200) {
    userStore.setUser(res.data)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  }
}

async function handlePasswordLogin() {
  loading.value = true
  try { const res = await login({ phone: form.value.phone, password: form.value.password }); doLogin(res) } finally { loading.value = false }
}

async function handleSmsLogin() {
  if (!form.value.phone) { ElMessage.error('请输入手机号'); return }
  loading.value = true
  try { const res = await smsLogin({ phone: form.value.phone, password: form.value.code }); doLogin(res) } finally { loading.value = false }
}

function sendCode() {
  if (!/^1[3-9]\d{9}$/.test(form.value.phone)) { ElMessage.error('请输入正确手机号'); return }
  ElMessage.success('验证码已发送（演示：888888）')
}
</script>

<template>
  <div class="form-page">
    <el-card class="form-card-sm">
      <h2>用户登录</h2>
      <el-tabs v-model="loginMode" style="margin-bottom:8px">
        <el-tab-pane label="密码登录" name="password" />
        <el-tab-pane label="短信登录" name="sms" />
      </el-tabs>

      <!-- 密码登录 -->
      <el-form v-if="loginMode === 'password'" :model="form" label-width="80px">
        <el-form-item label="账号"><el-input v-model="form.phone" placeholder="手机号" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password placeholder="密码" /></el-form-item>
        <el-form-item><el-button type="primary" :loading="loading" @click="handlePasswordLogin" style="width:100%">登录</el-button></el-form-item>
      </el-form>

      <!-- 短信登录 -->
      <el-form v-else :model="form" label-width="80px">
        <el-form-item label="手机号"><el-input v-model="form.phone" placeholder="手机号" /></el-form-item>
        <el-form-item label="验证码">
          <div style="display:flex;gap:8px">
            <el-input v-model="form.code" placeholder="验证码" maxlength="6" />
            <el-button @click="sendCode">获取验证码</el-button>
          </div>
        </el-form-item>
        <el-form-item><el-button type="primary" :loading="loading" @click="handleSmsLogin" style="width:100%">登录</el-button></el-form-item>
      </el-form>
      <p class="form-link" style="display:flex;justify-content:space-between">
        <router-link to="/forgot-password">忘记密码？</router-link>
        <span>还没有账号？<router-link to="/register">立即注册</router-link></span>
      </p>
    </el-card>
  </div>
</template>
