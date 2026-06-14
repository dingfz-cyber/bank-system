<script setup>
import { ref, onMounted, computed } from 'vue'
import { updateProfile, changePassword, setTransactionPassword, getLoginRecords } from '@/api/user'
import { getApplyList } from '@/api/apply'
import request from '@/api/request'
import { getTransactionList } from '@/api/transaction'
import { getMyCards, freezeCard, unfreezeCard, cancelCard } from '@/api/card'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { APPLY_STATUS_MAP } from '@/utils/constants'

const userStore = useUserStore()
const activeTab = ref('overview')

// 个人信息
const nickName = ref(userStore.nickName)
async function handleUpdateProfile() {
  const res = await updateProfile({ nickName: nickName.value })
  if (res.code === 200) { userStore.updateNickName(nickName.value); ElMessage.success('已更新') }
}

// 密码
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const txPwdForm = ref({ idCard: '', newTxPwd: '' })
async function handleChangePassword() {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) { ElMessage.error('两次密码不一致'); return }
  const res = await changePassword({ oldPassword: passwordForm.value.oldPassword, newPassword: passwordForm.value.newPassword })
  if (res.code === 200) { ElMessage.success('已修改，请重新登录'); passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }; userStore.logout(); window.location.href = '/login' }
}
async function handleChangeTxPwd() {
  if (!/^\d{17}[\dXx]$/.test(txPwdForm.value.idCard)) { ElMessage.error('请输入正确身份证号'); return }
  if (!/^\d{6}$/.test(txPwdForm.value.newTxPwd)) { ElMessage.error('交易密码须为6位数字'); return }
  const res = await setTransactionPassword({ newPassword: txPwdForm.value.newTxPwd })
  if (res.code === 200) { ElMessage.success('已修改'); txPwdForm.value = { idCard: '', newTxPwd: '' } }
}

// 安全
const txLimit = ref({ single: Number(localStorage.getItem('txLimitSingle')||100000), daily: Number(localStorage.getItem('txLimitDaily')||500000), doubleAuth: localStorage.getItem('txDoubleAuth')!=='false' })
function saveTxLimit() { localStorage.setItem('txLimitSingle',txLimit.value.single); localStorage.setItem('txLimitDaily',txLimit.value.daily); localStorage.setItem('txDoubleAuth',txLimit.value.doubleAuth); ElMessage.success('已保存') }
const loginRecords = ref([])
async function fetchLoginRecords() { const res = await getLoginRecords(); if (res.code===200) loginRecords.value = res.data }

// 银行卡
const cards = ref([])
const showCardNumbers = ref(new Set())
const totalBalance = computed(() => cards.value.reduce((s,c) => s+(Number(c.balance)||0),0))
const totalCredit = computed(() => cards.value.filter(c=>c.cardType===2).reduce((s,c) => s+(Number(c.creditLimit)||0),0))
const myCardIds = computed(() => new Set(cards.value.map(c=>c.id)))
function toggleCardNumber(id) { const s = new Set(showCardNumbers.value); s.has(id)?s.delete(id):s.add(id); showCardNumbers.value = s }
function maskCardNumber(card) { const n=card.cardNumber; if(!n||n.length<8) return n; if(showCardNumbers.value.has(card.id)) return n.replace(/(\d{4})(?=\d)/g,'$1 '); return n.slice(0,4)+' **** **** '+n.slice(-4) }
async function fetchCards() { const res = await getMyCards(); if (res.code===200) cards.value = res.data }
async function handleFreeze(card) { await ElMessageBox.confirm('确定冻结？'); await freezeCard(card.id); ElMessage.success('已冻结'); fetchCards() }
async function handleUnfreeze(card) { await unfreezeCard(card.id); ElMessage.success('已解冻'); fetchCards() }
async function handleCancel(card) { await ElMessageBox.confirm('确定注销？不可恢复！','警告',{type:'warning'}); await cancelCard(card.id); ElMessage.success('已注销'); fetchCards() }

// 申请
const applyList = ref([]); const applyPage = ref(1); const applyTotal = ref(0)
async function fetchMyApplies() { const res = await getApplyList({page:applyPage.value,pageSize:10}); if (res.code===200) { applyList.value=res.data.list; applyTotal.value=res.data.total } }

// 流水
const txList = ref([]); const txLoading = ref(false); const txPage = ref(1); const txTotal = ref(0)
async function fetchTx() { txLoading.value=true; try { const res = await getTransactionList({page:txPage.value,pageSize:10}); if (res.code===200) { txList.value=res.data?.list||[]; txTotal.value=res.data?.total||0 } } catch(e) { txList.value=[] } txLoading.value=false }
async function exportTx() {
  const res = await request.get('/transaction/export', { responseType: 'blob' })
  const url = URL.createObjectURL(new Blob([res]))
  const a = document.createElement('a'); a.href=url; a.download='transaction.csv'; a.click()
  URL.revokeObjectURL(url)
}

onMounted(() => { fetchMyApplies(); fetchCards(); fetchTx() })
</script>

<template>
  <div class="uc-page">
    <!-- 左侧个人卡片 -->
    <aside class="uc-sidebar">
      <div class="uc-avatar">👤</div>
      <h3>{{ userStore.nickName }}</h3>
      <p style="color:#909399;font-size:13px">{{ userStore.phone }}</p>
      <el-divider />
      <el-menu :default-active="activeTab" @select="activeTab = $event" style="border:none;background:transparent">
        <el-menu-item index="overview">🏠 账户概览</el-menu-item>
        <el-menu-item index="apply">📋 我的申请</el-menu-item>
        <el-menu-item index="tx">📊 交易流水</el-menu-item>
        <el-menu-item index="security">🔒 安全设置</el-menu-item>
      </el-menu>
      <div style="margin-top:auto;padding:12px 0">
        <el-button text size="small" @click="nickName=userStore.nickName" style="width:100%">昵称: <el-input v-model="nickName" size="small" style="width:80px" @blur="handleUpdateProfile" /></el-button>
      </div>
    </aside>

    <!-- 右侧内容区 -->
    <main class="uc-main">
      <!-- 账户概览 -->
      <div v-show="activeTab==='overview'">
        <el-row :gutter="16" style="margin-bottom:16px">
          <el-col :span="8"><div class="uc-stat"><div class="uc-stat-val" style="color:#67c23a">¥{{ totalBalance.toLocaleString() }}</div><div>总资产</div></div></el-col>
          <el-col :span="8"><div class="uc-stat"><div class="uc-stat-val" style="color:#409eff">{{ cards.length }}</div><div>银行卡</div></div></el-col>
          <el-col :span="8"><div class="uc-stat"><div class="uc-stat-val" style="color:#e6a23c">¥{{ totalCredit.toLocaleString() }}</div><div>信用额度</div></div></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12" v-for="card in cards" :key="card.id" style="margin-bottom:16px">
            <div class="bank-card" :class="[card.status===1?'frozen':'', card.cardType===2?'card-credit':'card-debit']">
              <div class="card-top">
                <span class="card-bank-name">{{ card.cardType===2?'白金信用卡':'储蓄借记卡' }}</span>
                <span class="card-type-tag">{{ card.cardType===2?'CREDIT':'DEBIT' }}</span>
              </div>
              <div class="card-num-row">
                <span class="card-num">{{ maskCardNumber(card) }}</span>
                <span class="card-eye" @click.stop="toggleCardNumber(card.id)">👁</span>
              </div>
              <div class="card-bottom">
                <div><span style="font-size:11px;opacity:0.7">有效期</span><br>{{ card.expiryDate }}</div>
                <div style="text-align:right"><span style="font-size:11px;opacity:0.7">余额</span><br><b>¥{{ card.balance }}</b></div>
              </div>
              <div class="card-actions">
                <template v-if="card.status===0">
                  <el-button size="small" type="warning" text @click="handleFreeze(card)">冻结</el-button>
                  <el-button v-if="card.cardType===2" size="small" type="danger" text @click="handleCancel(card)">注销</el-button>
                </template>
                <template v-else-if="card.status===1">
                  <el-button size="small" type="success" text @click="handleUnfreeze(card)">解冻</el-button>
                </template>
                <span v-else style="color:#909399;font-size:12px">已注销</span>
              </div>
            </div>
          </el-col>
        </el-row>
        <el-empty v-if="!cards.length" description="暂无银行卡" />
      </div>

      <!-- 我的申请 -->
      <div v-show="activeTab==='apply'">
        <el-table :data="applyList" stripe size="small">
          <el-table-column prop="id" label="ID" width="60"/>
          <el-table-column label="类型" width="80"><template #default="scope">{{ scope.row.applyType===1?'办卡':'贷款' }}</template></el-table-column>
          <el-table-column label="状态" width="90"><template #default="scope"><el-tag :type="scope.row.status===1?'success':scope.row.status===2?'danger':'warning'" size="small">{{ APPLY_STATUS_MAP[scope.row.status]||'待审核' }}</el-tag></template></el-table-column>
          <el-table-column label="进度" width="200"><template #default="scope"><el-steps :active="scope.row.status===0?0:scope.row.status===1?2:1" finish-status="success" align-center><el-step title="提交"/><el-step :title="scope.row.status===2?'驳回':'审核'" :status="scope.row.status===2?'error':undefined"/><el-step title="完成"/></el-steps></template></el-table-column>
          <el-table-column prop="remark" label="备注" min-width="120"><template #default="scope"><span v-if="scope.row.remark">{{ scope.row.remark }}</span><span v-else style="color:#999">-</span></template></el-table-column>
          <el-table-column prop="createTime" label="时间" width="180"/>
        </el-table>
        <el-empty v-if="!applyList.length" description="暂无申请" />
      </div>

      <!-- 交易流水 -->
      <div v-show="activeTab==='tx'">
        <div style="display:flex;justify-content:flex-end;margin-bottom:8px;gap:8px">
          <el-button size="small" @click="exportTx">📥 导出CSV</el-button>
          <el-button size="small" @click="fetchTx">刷新</el-button>
        </div>
        <el-table :data="txList" stripe size="small" v-loading="txLoading">
          <el-table-column prop="tradeTime" label="时间" width="180"/>
          <el-table-column label="类型" width="80"><template #default="scope">{{ {1:'转账',2:'缴费',3:'理财',4:'还款'}[scope.row.type]||'-' }}</template></el-table-column>
          <el-table-column label="金额" width="130">
            <template #default="scope">
              <span :style="{color: myCardIds.has(scope.row.fromCardId) ? '#f56c6c' : '#67c23a', fontWeight:'bold'}">
                {{ myCardIds.has(scope.row.fromCardId) ? '-' : '+' }}¥{{ scope.row.amount }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="交易对方" min-width="180">
            <template #default="scope">
              <span v-if="myCardIds.has(scope.row.fromCardId)">收款方: ****{{ scope.row.toAccount.slice(-4) }}</span>
              <span v-else>付款方: ****{{ scope.row.toAccount.slice(-4) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注"/>
        </el-table>
        <el-empty v-if="!txList.length&&!txLoading" description="暂无交易记录" />
      </div>

      <!-- 安全设置 -->
      <div v-show="activeTab==='security'">
        <el-row :gutter="16" type="flex" align="stretch">
          <el-col :span="12">
            <el-card header="修改登录密码" style="height:100%">
              <el-form :model="passwordForm" label-width="80px" size="small">
                <el-form-item label="原密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password autocomplete="off" name="oldpwd"/></el-form-item>
                <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password autocomplete="new-password" name="newpwd"/></el-form-item>
                <el-form-item label="确认密码"><el-input v-model="passwordForm.confirmPassword" type="password" show-password autocomplete="new-password" name="confirmpwd"/></el-form-item>
                <el-button type="danger" size="small" @click="handleChangePassword">修改登录密码</el-button>
              </el-form>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card header="修改交易密码" style="height:100%">
              <el-form :model="txPwdForm" label-width="100px" size="small">
                <el-form-item label="身份证号"><el-input v-model="txPwdForm.idCard" placeholder="验证身份" maxlength="18" autocomplete="off" name="idcard"/></el-form-item>
                <el-form-item label="新交易密码"><el-input v-model="txPwdForm.newTxPwd" type="password" placeholder="6位数字" maxlength="6" show-password autocomplete="new-password" name="txpwd"/></el-form-item>
                <el-button type="warning" size="small" @click="handleChangeTxPwd">修改交易密码</el-button>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
        <el-card header="安全设置" style="margin-top:12px">
          <el-form label-width="120px" size="small">
            <el-form-item label="单笔限额"><el-input-number v-model="txLimit.single" :min="1000" :step="10000"/> 元</el-form-item>
            <el-form-item label="单日限额"><el-input-number v-model="txLimit.daily" :min="10000" :step="100000"/> 元</el-form-item>
            <el-form-item label="二次验证"><el-switch v-model="txLimit.doubleAuth"/></el-form-item>
            <el-button type="primary" size="small" @click="saveTxLimit">保存</el-button>
          </el-form>
        </el-card>
        <el-card header="登录记录" style="margin-top:12px">
          <el-table :data="loginRecords" size="small" v-if="loginRecords.length">
            <el-table-column prop="loginTime" label="时间" width="160"/><el-table-column prop="ip" label="IP" width="120"/><el-table-column prop="device" label="设备"/>
          </el-table>
          <div v-else style="font-size:12px;color:#999;cursor:pointer" @click="fetchLoginRecords">点击加载</div>
        </el-card>
      </div>
    </main>
  </div>
</template>

<style scoped>
.uc-page { display:flex; gap:24px; padding:24px 40px; max-width:1400px; margin:0 auto; }
.uc-sidebar { width:200px; flex-shrink:0; background:#fff; border-radius:12px; padding:24px 0 16px; display:flex; flex-direction:column; align-items:center; box-shadow:0 1px 4px rgba(0,0,0,0.04); min-height:500px; }
.uc-avatar { width:64px; height:64px; background:#ecf5ff; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:32px; margin-bottom:8px; }
.uc-main { flex:1; min-width:0; }
.uc-stat { background:#fff; border-radius:8px; padding:16px; text-align:center; box-shadow:0 1px 4px rgba(0,0,0,0.04); }
.uc-stat-val { font-size:22px; font-weight:bold; }
.bank-card { border-radius:10px; padding:16px 20px; color:#fff; min-height:150px; display:flex; flex-direction:column; justify-content:space-between; transition:opacity .3s; }
.card-credit { background:linear-gradient(135deg,#1a1a2e,#2d1b4e); }
.card-debit { background:linear-gradient(135deg,#0f3d3d,#1a6b5a); }
.bank-card.frozen { opacity:0.55; }
.card-top { display:flex; justify-content:space-between; align-items:center; }
.card-bank-name { font-weight:bold; font-size:15px; }
.card-type-tag { font-size:11px; background:rgba(255,255,255,0.2); padding:2px 8px; border-radius:4px; }
.card-num-row { display:flex; align-items:center; justify-content:space-between; }
.card-num { font-size:18px; font-family:monospace; letter-spacing:2px; }
.card-eye { cursor:pointer; font-size:14px; opacity:0.7; }
.card-bottom { display:flex; justify-content:space-between; }
.card-actions { display:flex; gap:8px; margin-top:4px; }
.card-actions .el-button { color:#fff; }
</style>
