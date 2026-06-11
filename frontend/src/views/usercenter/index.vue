<script setup>
import { ref, onMounted } from 'vue'
import { updateProfile, changePassword, setTransactionPassword, getLoginRecords } from '@/api/user'
import { getApplyList } from '@/api/apply'
import { getTransactionList } from '@/api/transaction'
import { getMyCards, freezeCard, unfreezeCard, cancelCard } from '@/api/card'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { APPLY_STATUS_MAP } from '@/utils/constants'

const userStore = useUserStore()

// 个人信息表单
const nickName = ref(userStore.nickName)

async function handleUpdateProfile() {
  const res = await updateProfile({ nickName: nickName.value })
  if (res.code === 200) {
    userStore.updateNickName(nickName.value)
    ElMessage.success('修改成功')
  }
}

// 密码修改
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const txPwdForm = ref({ idCard: '', newTxPwd: '' })

async function handleChangePassword() {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  const res = await changePassword({
    oldPassword: passwordForm.value.oldPassword,
    newPassword: passwordForm.value.newPassword
  })
  if (res.code === 200) {
    ElMessage.success('密码修改成功，请重新登录')
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    userStore.logout()
    window.location.href = '/login'
  }
}

async function handleChangeTxPwd() {
  if (!/^\d{17}[\dXx]$/.test(txPwdForm.value.idCard)) { ElMessage.error('请输入正确的18位身份证号'); return }
  if (!/^\d{6}$/.test(txPwdForm.value.newTxPwd)) { ElMessage.error('交易密码必须为6位数字'); return }
  const res = await setTransactionPassword({ newPassword: txPwdForm.value.newTxPwd })
  if (res.code === 200) { ElMessage.success('交易密码已修改'); txPwdForm.value = { idCard: '', newTxPwd: '' } }
}

// 我的申请
const applyList = ref([])
const applyPage = ref(1)
const applyTotal = ref(0)

async function fetchMyApplies() {
  const res = await getApplyList({ page: applyPage.value, pageSize: 10 })
  if (res.code === 200) {
    applyList.value = res.data.list
    applyTotal.value = res.data.total
  }
}

// 我的银行卡
const cards = ref([])
import { computed } from 'vue'
const totalBalance = computed(() => cards.value.reduce((s,c) => s + (Number(c.balance)||0), 0))
const totalCredit = computed(() => cards.value.filter(c=>c.cardType===2).reduce((s,c) => s + (Number(c.creditLimit)||0), 0))

async function fetchCards() {
  const res = await getMyCards()
  if (res.code === 200) cards.value = res.data
}

const showCardNumbers = ref(new Set())
function toggleCardNumber(id) {
  const s = new Set(showCardNumbers.value)
  s.has(id) ? s.delete(id) : s.add(id)
  showCardNumbers.value = s
}
function maskCardNumber(card) {
  const num = card.cardNumber
  if (!num || num.length < 8) return num
  if (showCardNumbers.value.has(card.id)) return num.replace(/(\d{4})(?=\d)/g, '$1 ')
  return num.slice(0, 4) + ' **** **** ' + num.slice(-4)
}

async function handleFreeze(card) {
  await ElMessageBox.confirm('确定冻结该银行卡？冻结后无法消费。', '提示')
  await freezeCard(card.id)
  ElMessage.success('已冻结')
  fetchCards()
}

async function handleUnfreeze(card) {
  await unfreezeCard(card.id)
  ElMessage.success('已解冻')
  fetchCards()
}

async function handleCancel(card) {
  await ElMessageBox.confirm('确定注销该银行卡？此操作不可恢复！', '警告', {type:'warning'})
  await cancelCard(card.id)
  ElMessage.success('已注销')
  fetchCards()
}

// 安全中心
const txLimit = ref({
  single: Number(localStorage.getItem('txLimitSingle') || 100000),
  daily: Number(localStorage.getItem('txLimitDaily') || 500000),
  doubleAuth: localStorage.getItem('txDoubleAuth') !== 'false'
})
function saveTxLimit() {
  localStorage.setItem('txLimitSingle', txLimit.value.single)
  localStorage.setItem('txLimitDaily', txLimit.value.daily)
  localStorage.setItem('txDoubleAuth', txLimit.value.doubleAuth)
  ElMessage.success('安全设置已保存')
}

const loginRecords = ref([])
async function fetchLoginRecords() {
  const res = await getLoginRecords(); if (res.code === 200) loginRecords.value = res.data
}

// 交易流水
const txList = ref([])
const txPage = ref(1)
const txTotal = ref(0)

const txLoading = ref(false)
async function fetchTx() {
  txLoading.value = true
  try {
    const res = await getTransactionList({ page: txPage.value, pageSize: 10 })
    if (res.code === 200) { txList.value = res.data?.list || []; txTotal.value = res.data?.total || 0 }
  } catch(e) { txList.value = []; txTotal.value = 0 }
  txLoading.value = false
}

onMounted(() => {
  fetchMyApplies()
  fetchCards()
  fetchTx()
})
</script>

<template>
  <div class="page user-center">
    <h2 class="page-title">个人中心</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <!-- 个人信息 -->
        <el-card class="section-card">
          <template #header><span class="card-title">个人信息</span></template>
          <el-form label-width="80px">
            <el-form-item label="手机号">
              <el-input :model-value="userStore.phone" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="nickName" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 修改登录密码 -->
        <el-card class="section-card">
          <template #header><span class="card-title">修改登录密码</span></template>
          <el-form :model="passwordForm" label-width="100px" size="small">
            <el-form-item label="原密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
            <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
            <el-form-item label="确认密码"><el-input v-model="passwordForm.confirmPassword" type="password" show-password /></el-form-item>
            <el-form-item><el-button type="danger" @click="handleChangePassword">修改登录密码</el-button></el-form-item>
          </el-form>
        </el-card>

        <!-- 修改交易密码 -->
        <el-card class="section-card">
          <template #header><span class="card-title">修改交易密码</span></template>
          <el-form :model="txPwdForm" label-width="100px" size="small">
            <el-form-item label="身份证号"><el-input v-model="txPwdForm.idCard" placeholder="请输入身份证号验证身份" maxlength="18" /></el-form-item>
            <el-form-item label="新交易密码"><el-input v-model="txPwdForm.newTxPwd" type="password" placeholder="6位数字" maxlength="6" show-password /></el-form-item>
            <el-form-item><el-button type="warning" @click="handleChangeTxPwd">修改交易密码</el-button></el-form-item>
          </el-form>
        </el-card>

        <!-- 安全中心 -->
        <el-card class="section-card">
          <template #header><span class="card-title">安全中心</span></template>
          <el-form label-width="120px" size="small">
            <el-form-item label="单笔限额">
              <el-input-number v-model="txLimit.single" :min="1000" :step="10000" />
              <span style="margin-left:8px;color:#909399">元</span>
            </el-form-item>
            <el-form-item label="单日限额">
              <el-input-number v-model="txLimit.daily" :min="10000" :step="100000" />
              <span style="margin-left:8px;color:#909399">元</span>
            </el-form-item>
            <el-form-item label="二次验证">
              <el-switch v-model="txLimit.doubleAuth" active-text="大额需二次验证" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveTxLimit">保存设置</el-button>
            </el-form-item>
          </el-form>
          <el-divider />
          <div style="font-size:14px;font-weight:bold;margin-bottom:8px;cursor:pointer" @click="fetchLoginRecords">登录记录 <span style="font-size:12px;color:#409eff">（点击刷新）</span></div>
          <el-table :data="loginRecords" size="small" v-if="loginRecords.length">
            <el-table-column prop="loginTime" label="时间" width="160" />
            <el-table-column prop="ip" label="IP" width="120" />
            <el-table-column prop="device" label="设备" />
          </el-table>
          <div v-else style="font-size:12px;color:#999">点击"登录记录"查看</div>
        </el-card>
      </el-col>

      <!-- 我的银行卡 -->
      <el-col :span="16">
        <!-- 账户总览 -->
        <el-row :gutter="12" style="margin-bottom:16px" v-if="cards.length">
          <el-col :span="8"><el-card shadow="hover"><div style="text-align:center"><div style="font-size:24px;font-weight:bold;color:#67c23a">¥{{ totalBalance.toLocaleString() }}</div><div style="font-size:12px;color:#909399;margin-top:4px">总资产</div></div></el-card></el-col>
          <el-col :span="8"><el-card shadow="hover"><div style="text-align:center"><div style="font-size:24px;font-weight:bold;color:#409eff">{{ cards.length }}</div><div style="font-size:12px;color:#909399;margin-top:4px">银行卡数</div></div></el-card></el-col>
          <el-col :span="8"><el-card shadow="hover"><div style="text-align:center"><div style="font-size:24px;font-weight:bold;color:#e6a23c">¥{{ totalCredit.toLocaleString() }}</div><div style="font-size:12px;color:#909399;margin-top:4px">总信用额度</div></div></el-card></el-col>
        </el-row>

        <el-card class="section-card">
          <template #header><span class="card-title">我的银行卡</span></template>
          <el-row :gutter="16" v-if="cards.length">
            <el-col :span="12" v-for="card in cards" :key="card.id">
              <div class="bank-card" :class="card.status === 1 ? 'frozen' : ''">
                <div class="card-bank">网银系统</div>
                <div class="card-number" style="display:flex;align-items:center;gap:6px">
                  <span>{{ maskCardNumber(card) }}</span>
                  <span @click.stop="toggleCardNumber(card.id)" style="cursor:pointer;font-size:14px;opacity:0.7" :title="showCardNumbers.has(card.id)?'隐藏':'显示完整卡号'">👁</span>
                </div>
                <div class="card-info">
                  <span>{{ card.cardType === 2 ? '信用卡' : '借记卡' }}</span>
                  <span>{{ card.expiryDate }}</span>
                </div>
                <div class="card-status">
                  <el-tag :type="card.status === 0 ? 'success' : card.status === 1 ? 'warning' : 'info'" size="small">
                    {{ card.status === 0 ? '正常' : card.status === 1 ? '已冻结' : '已注销' }}
                  </el-tag>
                  <span v-if="card.cardType === 2" style="margin-left:8px;font-size:13px;color:#fff">
                    额度: ¥{{ card.creditLimit }}
                  </span>
                </div>
                <div class="card-actions">
                  <template v-if="card.status === 0">
                    <el-button size="small" type="warning" @click="handleFreeze(card)">冻结</el-button>
                    <el-button v-if="card.cardType===2" size="small" type="danger" @click="handleCancel(card)">注销</el-button>
                  </template>
                  <template v-else-if="card.status === 1">
                    <el-button size="small" type="success" @click="handleUnfreeze(card)">解冻</el-button>
                  </template>
                  <span v-else-if="card.status===2" style="color:#909399;font-size:12px">已注销</span>
                </div>
              </div>
            </el-col>
          </el-row>
          <el-empty v-else description="暂无银行卡，申请办卡审批通过后将自动生成" />
        </el-card>

        <el-card class="section-card">
          <template #header><span class="card-title">我的申请</span></template>
          <el-table :data="applyList" stripe v-loading="!applyList.length">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="productId" label="产品ID" width="80" />
            <el-table-column prop="applyType" label="类型" width="80">
              <template #default="{ row }">{{ row.applyType === 1 ? '办卡' : '贷款' }}</template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'" size="small">
                  {{ APPLY_STATUS_MAP[row.status] || '待审核' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="审核进度" width="200">
              <template #default="{ row }">
                <el-steps :active="row.status === 0 ? 0 : row.status === 1 ? 2 : 1" finish-status="success" align-center>
                  <el-step title="提交" />
                  <el-step :title="row.status === 2 ? '已驳回' : '审核中'" :status="row.status === 2 ? 'error' : undefined" />
                  <el-step title="完成" />
                </el-steps>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="120">
              <template #default="{ row }">
                <span v-if="row.remark">{{ row.remark }}</span>
                <span v-else style="color:#999">-</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="180" />
          </el-table>
          <el-empty v-if="!applyList.length && applyTotal === 0" description="暂无申请记录" />
          <el-pagination v-if="applyTotal > 10" v-model:current-page="applyPage" :total="applyTotal" :page-size="10" layout="prev,pager,next" class="pagination" />
        </el-card>

        <el-card class="section-card">
          <template #header><span class="card-title">交易流水</span><el-button size="small" style="float:right" @click="fetchTx">刷新</el-button></template>
          <el-table :data="txList" stripe size="small" v-loading="txLoading">
            <el-table-column prop="tradeTime" label="时间" width="180" />
            <el-table-column prop="type" label="类型" width="80">
              <template #default="scope">{{ {1:'转账',2:'缴费',3:'理财',4:'还款'}[scope.row.type] || '-' }}</template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="scope">-¥{{ scope.row.amount }}</template>
            </el-table-column>
            <el-table-column prop="toAccount" label="收款方" min-width="160">
              <template #default="scope">{{ scope.row.toAccount ? '****'+scope.row.toAccount.slice(-4) : '-' }}</template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
          </el-table>
          <el-empty v-if="!txList.length" description="暂无交易记录" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.user-center {
  padding: 20px 40px;
  max-width: 1400px; margin: 0 auto;
}
.page-title {
  margin: 0 0 16px;
}
.section-card {
  margin-bottom: 16px;
}
.bank-card {
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  border-radius: 12px;
  padding: 18px 20px;
  color: #fff;
  margin-bottom: 16px;
  min-height: 160px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: opacity 0.3s;
}
.bank-card.frozen {
  opacity: 0.6;
}
.card-bank {
  font-size: 15px;
  font-weight: bold;
  letter-spacing: 2px;
}
.card-number {
  font-size: 18px;
  letter-spacing: 2px;
  font-family: monospace;
  margin: 10px 0;
  word-break: break-all;
}
.card-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  opacity: 0.8;
}
.card-status {
  margin-top: 8px;
}
.card-actions {
  margin-top: 10px;
}
.card-title {
  font-size: 18px;
  font-weight: bold;
}
</style>
