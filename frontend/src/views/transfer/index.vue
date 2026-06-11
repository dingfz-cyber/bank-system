<script setup>
import { ref, onMounted } from 'vue'
import { transfer } from '@/api/transaction'
import { getMyCards } from '@/api/card'
import { getPayeeList, addPayee, deletePayee } from '@/api/payee'
import { ElMessage, ElMessageBox } from 'element-plus'

const cards = ref([])
const payees = ref([])
const payeeDialog = ref(false)
const newPayee = ref({ payeeName: '', accountNumber: '', bankName: '', remark: '' })
const form = ref({ fromCardId: null, toAccount: '', amount: '', transactionPassword: '', remark: '', transferType: 'internal', arrival: 'realtime' })

onMounted(async () => {
  const [cRes, pRes] = await Promise.all([getMyCards(), getPayeeList()])
  if (cRes.code === 200) cards.value = cRes.data.filter(c => c.status === 0)
  if (pRes.code === 200) payees.value = pRes.data
})

function selectPayee(p) {
  form.value.toAccount = p.accountNumber
}

async function savePayee() {
  if (!newPayee.value.accountNumber) { ElMessage.error('请输入收款账号'); return }
  await addPayee(newPayee.value)
  ElMessage.success('已添加')
  payeeDialog.value = false
  newPayee.value = { payeeName: '', accountNumber: '', bankName: '', remark: '' }
  const res = await getPayeeList(); if (res.code === 200) payees.value = res.data
}

async function removePayee(id) {
  await deletePayee(id)
  ElMessage.success('已删除')
  const res = await getPayeeList(); if (res.code === 200) payees.value = res.data
}

async function handleTransfer() {
  if (!form.value.fromCardId) { ElMessage.error('请选择付款卡'); return }
  if (!form.value.toAccount) { ElMessage.error('请输入收款卡号'); return }
  form.value.toAccount = form.value.toAccount.replace(/\s/g, '')
  if (!form.value.amount || Number(form.value.amount) <= 0) { ElMessage.error('请输入有效金额'); return }
  if (!form.value.transactionPassword) { ElMessage.error('请输入交易密码'); return }

  // 大额交易二次确认（≥5万元）
  if (Number(form.value.amount) >= 50000) {
    try {
      await ElMessageBox.confirm(
        `即将转账 ¥${Number(form.value.amount).toLocaleString()}，大额交易请确认信息无误。`,
        '大额交易确认', { confirmButtonText: '确认转账', type: 'warning' }
      )
    } catch { return }
  }

  const res = await transfer({ ...form.value, amount: Number(form.value.amount) })
  if (res.code === 200) {
    ElMessage.success('转账成功')
    form.value = { fromCardId: null, toAccount: '', amount: '', transactionPassword: '', remark: '' }
  }
}
</script>

<template>
  <div class="page transfer-page">
    <h2 class="page-title">转账汇款</h2>
    <!-- 常用收款人 -->
    <el-card style="max-width:700px;margin-bottom:16px" v-if="payees.length">
      <template #header><span>常用收款人</span><el-button size="small" style="float:right" @click="payeeDialog = true">+添加</el-button></template>
      <div style="display:flex;gap:8px;flex-wrap:wrap">
        <el-tag v-for="p in payees" :key="p.id" closable style="cursor:pointer" @click="selectPayee(p)" @close="removePayee(p.id)" :disable-transitions="false">
          {{ p.payeeName || '****'+p.accountNumber.slice(-4) }}
        </el-tag>
      </div>
    </el-card>

    <el-card style="max-width:700px;margin-bottom:16px" v-else>
      <el-button size="small" @click="payeeDialog = true">+ 添加常用收款人</el-button>
    </el-card>

    <!-- 转账表单 -->
    <el-card style="max-width:700px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="转账类型">
          <el-radio-group v-model="form.transferType">
            <el-radio value="internal">行内转账</el-radio>
            <el-radio value="cross">跨行转账</el-radio>
            <el-radio value="phone">手机号转账</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="到账方式">
          <el-radio-group v-model="form.arrival">
            <el-radio value="realtime">实时到账</el-radio>
            <el-radio value="normal">普通到账(2h)</el-radio>
            <el-radio value="large">大额汇款</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="付款卡">
          <el-select v-model="form.fromCardId" placeholder="选择付款卡" style="width:100%">
            <el-option v-for="c in cards" :key="c.id" :value="c.id"
              :label="'**** ' + c.cardNumber.slice(-4) + ' (余额: ¥' + c.balance + ')'" />
          </el-select>
        </el-form-item>
        <el-form-item label="收款卡号">
          <input v-model="form.toAccount" placeholder="19位卡号，如 6228 0992 8323 8151 610" maxlength="23" style="width:100%;height:36px;border:1px solid #dcdfe6;border-radius:4px;padding:0 14px;font-family:monospace;font-size:16px;box-sizing:border-box;letter-spacing:1px" />
        </el-form-item>
        <el-form-item label="转账金额">
          <el-input v-model="form.amount" placeholder="请输入金额" type="number" />
        </el-form-item>
        <el-form-item label="交易密码">
          <el-input v-model="form.transactionPassword" type="password" placeholder="6位数字交易密码" maxlength="6" show-password />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="选填" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleTransfer" style="width:100%">确认转账</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="payeeDialog" title="添加收款人" width="420px">
      <el-form :model="newPayee" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="newPayee.payeeName" /></el-form-item>
        <el-form-item label="收款账号"><el-input v-model="newPayee.accountNumber" placeholder="19位卡号" maxlength="19" /></el-form-item>
        <el-form-item label="银行"><el-input v-model="newPayee.bankName" placeholder="如：网银系统" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="newPayee.remark" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="payeeDialog=false">取消</el-button><el-button type="primary" @click="savePayee">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.transfer-page { padding: 20px 40px; }
</style>
