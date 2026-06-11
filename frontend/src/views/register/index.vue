<script setup>
import { ref } from 'vue'
import { register } from '@/api/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const form = ref({ phone: '', password: '', nickName: '', realName: '', idCard: '' })
const loading = ref(false)

async function handleRegister() {
  loading.value = true
  try {
    const res = await register(form.value)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push({ name: 'login' })
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="form-page">
    <el-card class="form-card-sm">
      <h2>用户注册</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="11位手机号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" placeholder="18位身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickName" placeholder="昵称" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="6-20位密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" style="width:100%">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <p class="form-link">
        已有账号？<router-link to="/login">去登录</router-link>
      </p>
    </el-card>
  </div>
</template>
