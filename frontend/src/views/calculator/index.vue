<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()

const amount = ref(100000)
const years = ref(3)
const rate = ref(4.5)

const monthlyRate = computed(() => rate.value / 100 / 12)
const months = computed(() => years.value * 12)
const monthlyPayment = computed(() => {
  const r = monthlyRate.value; const n = months.value
  if (r === 0) return amount.value / n
  return (amount.value * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1)
})
const totalPayment = computed(() => monthlyPayment.value * months.value)
const totalInterest = computed(() => totalPayment.value - amount.value)

const schedule = computed(() => {
  const arr = []; const n = months.value; const r = monthlyRate.value
  let balance = amount.value
  for (let i = 1; i <= Math.min(n, 12); i++) {
    const interest = balance * r
    const principal = monthlyPayment.value - interest
    balance -= principal
    if (balance < 0) balance = 0
    arr.push({ month: i, payment: monthlyPayment.value, principal, interest, balance })
  }
  return arr
})
</script>

<template>
  <div class="page calc-page">
    <h2 class="page-title">贷款计算器</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <el-form label-width="100px">
            <el-form-item label="贷款金额(元)">
              <el-input-number v-model="amount" :min="1000" :step="10000" style="width:100%" />
            </el-form-item>
            <el-form-item label="贷款期限(年)">
              <el-input-number v-model="years" :min="1" :max="30" style="width:100%" />
            </el-form-item>
            <el-form-item label="年利率(%)">
              <el-input-number v-model="rate" :min="0.1" :max="20" :step="0.1" :precision="1" style="width:100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="router.push({name:'applyLoan',query:{amount:amount,years:years,rate:rate}})" style="width:100%">去申请贷款 →</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <el-row :gutter="16">
            <el-col :span="8"><div class="result-item"><div class="r-val">¥{{ monthlyPayment.toFixed(0) }}</div><div class="r-label">月供</div></div></el-col>
            <el-col :span="8"><div class="result-item"><div class="r-val">¥{{ totalPayment.toFixed(0) }}</div><div class="r-label">还款总额</div></div></el-col>
            <el-col :span="8"><div class="result-item"><div class="r-val">¥{{ totalInterest.toFixed(0) }}</div><div class="r-label">利息总额</div></div></el-col>
          </el-row>
        </el-card>
        <el-card style="margin-top:16px">
          <template #header>前12期还款计划</template>
          <el-table :data="schedule" stripe size="small">
            <el-table-column prop="month" label="期数" width="60"/>
            <el-table-column prop="payment" label="月供" :formatter="(r) => '¥' + r.payment.toFixed(2)"/>
            <el-table-column prop="principal" label="本金" :formatter="(r) => '¥' + r.principal.toFixed(2)"/>
            <el-table-column prop="interest" label="利息" :formatter="(r) => '¥' + r.interest.toFixed(2)"/>
            <el-table-column prop="balance" label="剩余本金" :formatter="(r) => '¥' + r.balance.toFixed(2)"/>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.calc-page { padding: 20px 40px; }
.result-item { text-align:center; padding:16px; }
.r-val { font-size:24px; font-weight:bold; color:#409eff; }
.r-label { font-size:13px; color:#909399; margin-top:4px; }
</style>
