<script setup>
import { ref, computed } from 'vue'
import { submitApply } from '@/api/apply'
import { setTransactionPassword } from '@/api/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const isCard = computed(() => route.name === 'applyCard')
const pageTitle = computed(() => isCard.value ? '信用卡申请' : '贷款申请')

const form = ref({
  productId: Number(route.query.productId) || '',
  realName: '',
  phone: '',
  applyType: isCard.value ? 1 : 2,
  transactionPassword: ''
})
const loading = ref(false)

async function handleSubmit() {
  if (!form.value.realName || !form.value.phone) { ElMessage.warning('请填写完整信息'); return }
  if (isCard.value && !form.value.transactionPassword) { ElMessage.warning('请设置6位数字交易密码'); return }
  loading.value = true
  try {
    form.value.productId = Number(form.value.productId)
    const res = await submitApply(form.value)
    if (res.code === 200) {
      // 申请时一并设置交易密码
      if (form.value.transactionPassword) {
        await setTransactionPassword({ newPassword: form.value.transactionPassword })
      }
      ElMessage.success('申请提交成功' + (form.value.transactionPassword ? '，交易密码已设置' : ''))
      router.push({ name: 'home' })
    }
  } finally { loading.value = false }
}
</script>

<template>
  <div class="form-page">
    <el-card class="form-card">
      <h2>{{ pageTitle }}</h2>
      <p v-if="route.query.productName" class="product-name">
        产品：{{ route.query.productName }}
      </p>
      <el-form :model="form" label-width="100px">
        <el-form-item label="产品ID">
          <el-input v-model.number="form.productId" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="isCard" label="交易密码">
          <el-input v-model="form.transactionPassword" type="password" placeholder="请设置6位数字交易密码（转账/缴费使用）" maxlength="6" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            提交申请
          </el-button>
          <el-button @click="router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
