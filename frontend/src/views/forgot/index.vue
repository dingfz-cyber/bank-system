<script setup>
import { ref } from 'vue'
import { forgotPassword } from '@/api/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const step = ref(1)
const phone = ref('')
const code = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

function nextStep() {
  if (step.value === 1) {
    if (!/^1[3-9]\d{9}$/.test(phone.value)) {
      ElMessage.error('请输入正确的手机号')
      return
    }
    step.value = 2
  } else if (step.value === 2) {
    // 模拟验证码校验（固定 888888）
    if (code.value !== '888888') {
      ElMessage.error('验证码错误，请输入 888888')
      return
    }
    step.value = 3
  }
}

async function submitReset() {
  if (newPassword.value.length < 6) {
    ElMessage.error('密码长度至少6位')
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  const res = await forgotPassword({ phone: phone.value, password: newPassword.value })
  if (res.code === 200) {
    ElMessage.success('密码重置成功，请登录')
    router.push('/login')
  }
}
</script>

<template>
  <div class="form-page">
    <el-card class="form-card-sm">
      <h2 style="text-align:center;margin-bottom:24px">忘记密码</h2>

      <!-- 步骤1：输入手机号 -->
      <div v-if="step === 1">
        <el-form label-width="80px">
          <el-form-item label="手机号">
            <el-input v-model="phone" placeholder="请输入注册手机号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="nextStep" style="width:100%">下一步</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 步骤2：输入验证码 -->
      <div v-else-if="step === 2">
        <p style="color:#909399;margin-bottom:16px">验证码已发送至 {{ phone }}（演示：输入888888）</p>
        <el-form label-width="80px">
          <el-form-item label="验证码">
            <el-input v-model="code" placeholder="请输入验证码" maxlength="6" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="nextStep" style="width:100%">下一步</el-button>
          </el-form-item>
          <el-form-item>
            <el-button @click="step = 1" style="width:100%">返回上一步</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 步骤3：设置新密码 -->
      <div v-else>
        <el-form label-width="100px">
          <el-form-item label="新密码">
            <el-input v-model="newPassword" type="password" show-password placeholder="至少6位" />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitReset" style="width:100%">确认重置</el-button>
          </el-form-item>
          <el-form-item>
            <el-button @click="step = 2" style="width:100%">返回上一步</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>
