<script setup>
import { ref, onMounted } from 'vue'
import { getMyCards } from '@/api/card'
import { ElMessage } from 'element-plus'

const history = ref([])
onMounted(() => { history.value = JSON.parse(localStorage.getItem('payHistory')||'[]') })

const categories = [
  { key:'water', name:'水费', icon:'💧' },
  { key:'electric', name:'电费', icon:'⚡' },
  { key:'gas', name:'燃气费', icon:'🔥' },
  { key:'phone', name:'话费', icon:'📱' },
  { key:'broadband', name:'宽带', icon:'🌐' },
  { key:'credit', name:'信用卡还款', icon:'💳' },
]

const activeCat = ref('water')
const accountNo = ref('')
const queryResult = ref(null)
const cards = ref([])
const selectedCard = ref(null)
const loaded = ref(false)

async function loadCards() {
  if (loaded.value) return
  const res = await getMyCards()
  if (res.code === 200) cards.value = res.data.filter(c => c.status === 0)
  loaded.value = true
}

function queryBill() {
  if (!accountNo.value) { ElMessage.error('请输入户号'); return }
  // 模拟查询
  const amt = Math.floor(Math.random() * 500) + 50
  queryResult.value = {
    accountNo: accountNo.value,
    name: '户主',
    amount: amt,
    period: '2026-05',
    dueDate: '2026-06-30'
  }
  ElMessage.success('查询成功')
}

function payBill() {
  if (!selectedCard.value) { ElMessage.error('请选择付款卡'); return }
  ElMessage.success(`缴费成功！¥${queryResult.value.amount}`)
  const h = { time: new Date().toLocaleString(), type: categories.find(c=>c.key===activeCat.value).name, amount: queryResult.value.amount, account: accountNo.value }
  history.value.unshift(h)
  localStorage.setItem('payHistory', JSON.stringify(history.value.slice(0, 20)))
  queryResult.value = null
  accountNo.value = ''
  selectedCard.value = null
}
</script>

<template>
  <div class="page payment-page">
    <h2 class="page-title">生活缴费</h2>

    <!-- 缴费类目 -->
    <div class="cat-bar">
      <div v-for="c in categories" :key="c.key" class="cat-item" :class="{active:activeCat===c.key}" @click="activeCat=c.key;queryResult=null;accountNo=''">
        <span class="cat-icon">{{ c.icon }}</span>
        <span>{{ c.name }}</span>
      </div>
    </div>

    <!-- 查询区 -->
    <el-card style="max-width:500px;margin-top:16px">
      <el-form label-width="80px">
        <el-form-item label="缴费户号">
          <div style="display:flex;gap:8px">
            <el-input v-model="accountNo" placeholder="输入户号" @focus="loadCards" />
            <el-button type="primary" @click="queryBill">查询</el-button>
          </div>
        </el-form-item>
      </el-form>

      <!-- 查询结果 -->
      <div v-if="queryResult" style="background:#f5f7fa;padding:16px;border-radius:8px;margin-top:8px">
        <el-descriptions :column="2" size="small" border>
          <el-descriptions-item label="户号">{{ queryResult.accountNo }}</el-descriptions-item>
          <el-descriptions-item label="户名">{{ queryResult.name }}</el-descriptions-item>
          <el-descriptions-item label="应缴金额"><b style="color:#f56c6c">¥{{ queryResult.amount }}</b></el-descriptions-item>
          <el-descriptions-item label="账单月份">{{ queryResult.period }}</el-descriptions-item>
          <el-descriptions-item label="截止日期" :span="2">{{ queryResult.dueDate }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />
        <el-form-item label="付款卡">
          <el-select v-model="selectedCard" placeholder="选择付款卡" style="width:100%">
            <el-option v-for="c in cards" :key="c.id" :value="c.id"
              :label="'****' + c.cardNumber.slice(-4) + ' (¥' + c.balance + ')'" />
          </el-select>
        </el-form-item>
        <el-button type="danger" @click="payBill" style="width:100%;margin-top:8px" :disabled="!selectedCard">
          确认缴费 ¥{{ queryResult.amount }}
        </el-button>
      </div>
    </el-card>

    <!-- 缴费记录 -->
    <el-card v-if="history.length" style="max-width:500px;margin-top:16px">
      <template #header>缴费记录</template>
      <el-table :data="history" size="small" stripe>
        <el-table-column prop="time" label="时间" width="160" />
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{row}">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="account" label="户号" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.payment-page { max-width:800px; margin:0 auto; padding:20px 40px; }
.cat-bar { display:flex; gap:12px; flex-wrap:wrap; }
.cat-item { text-align:center; padding:12px 20px; background:#f5f7fa; border-radius:8px; cursor:pointer; transition:all .2s; border:2px solid transparent; display:flex; flex-direction:column; gap:4px; }
.cat-item:hover, .cat-item.active { border-color:var(--color-primary); background:#ecf5ff; }
.cat-icon { font-size:24px; }
</style>
