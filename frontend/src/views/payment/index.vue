<script setup>
import { ref, onMounted, computed } from 'vue'
import { getMyCards } from '@/api/card'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const cats=[{key:'water',name:'水费',icon:'💧'},{key:'electric',name:'电费',icon:'⚡'},{key:'gas',name:'燃气费',icon:'🔥'},{key:'phone',name:'话费',icon:'📱'},{key:'broadband',name:'宽带',icon:'🌐'},{key:'credit',name:'信用卡还款',icon:'💳'}]
const catHints={water:'水费户号',electric:'电费户号',gas:'燃气户号',phone:'输入手机号',broadband:'宽带账号',credit:'输入信用卡卡号'}

const activeCat=ref('water');const accountNo=ref('');const queryResult=ref(null)
const cards=ref([]);const selectedCard=ref(null);const myAccounts=ref([]);const history=ref([])
const loaded=ref(false)

onMounted(async()=>{
  history.value=JSON.parse(localStorage.getItem('payHistory')||'[]')
  const r=await getMyCards();if(r.code===200)cards.value=r.data.filter(c=>c.status===0)
})
async function loadAccounts(){
  if(loaded.value)return;loaded.value=true
  const r=await request.get('/utility/accounts');if(r.code===200)myAccounts.value=r.data
}
function selectAccount(a){accountNo.value=a.account_no;activeCat.value=a.type;queryResult.value=null}

async function queryBill(){
  loadAccounts()
  if(!accountNo.value){ElMessage.error('请输入户号');return}
  const r=await request.get('/utility/query',{params:{accountNo:accountNo.value,type:activeCat.value}})
  if(r.code===200)queryResult.value=r.data;else ElMessage.error(r.msg||'查询失败')
}
async function payBill(){
  if(!selectedCard.value){ElMessage.error('请选择付款卡');return}
  const r=await request.post('/utility/pay',{cardId:selectedCard.value,accountNo:queryResult.value.account_no,type:activeCat.value,amount:queryResult.value.billAmount})
  if(r.code===200){ElMessage.success('缴费成功!');const h={time:new Date().toLocaleString(),type:cats.find(c=>c.key===activeCat.value).name,amount:queryResult.value.billAmount,account:accountNo.value};history.value.unshift(h);localStorage.setItem('payHistory',JSON.stringify(history.value.slice(0,20)));queryResult.value=null;accountNo.value='';selectedCard.value=null}
}
</script>

<template>
  <div class="page payment-page">
    <h2 class="page-title">生活缴费</h2>
    <div class="cat-bar">
      <div v-for="c in cats" :key="c.key" class="cat-item" :class="{active:activeCat===c.key}" @click="activeCat=c.key;queryResult=null;accountNo=''"><span class="cat-icon">{{c.icon}}</span><span>{{c.name}}</span></div>
    </div>

    <!-- 我的户号 -->
    <el-card v-if="myAccounts.length" style="max-width:700px;margin-top:12px">
      <template #header>我的户号<el-button size="small" style="float:right" @click="loadAccounts">刷新</el-button></template>
      <div style="display:flex;gap:8px;flex-wrap:wrap">
        <el-tag v-for="a in myAccounts" :key="a.id" closable style="cursor:pointer" @click="selectAccount(a)">{{a.type==='water'?'💧':a.type==='electric'?'⚡':a.type==='gas'?'🔥':a.type==='phone'?'📱':'🌐'}} {{a.account_no}}</el-tag>
      </div>
    </el-card>

    <!-- 查询区 -->
    <el-card style="max-width:700px;margin-top:12px" @focusin="loadAccounts">
      <el-form label-width="80px">
        <el-form-item label="缴费户号">
          <div style="display:flex;gap:8px"><el-input v-model="accountNo" :placeholder="catHints[activeCat]"/><el-button type="primary" @click="queryBill">查询</el-button></div>
        </el-form-item>
      </el-form>
      <div v-if="queryResult" style="background:#f5f7fa;padding:16px;border-radius:8px;margin-top:8px">
        <el-descriptions :column="2" size="small" border>
          <el-descriptions-item label="户号">{{queryResult.account_no}}</el-descriptions-item>
          <el-descriptions-item label="户名">{{queryResult.holder_name||'户主'}}</el-descriptions-item>
          <el-descriptions-item label="应缴"><b style="color:#f56c6c">¥{{queryResult.billAmount}}</b></el-descriptions-item>
          <el-descriptions-item label="月份">{{queryResult.billPeriod}}</el-descriptions-item>
          <el-descriptions-item label="截止" :span="2">{{queryResult.dueDate}} | {{queryResult.address}}</el-descriptions-item>
        </el-descriptions>
        <el-divider/>
        <el-form-item label="付款卡"><el-select v-model="selectedCard" style="width:100%" placeholder="选择付款卡"><el-option v-for="c in cards" :key="c.id" :value="c.id" :label="'****'+c.cardNumber.slice(-4)+' (¥'+c.balance+')'"/></el-select></el-form-item>
        <el-button type="danger" @click="payBill" style="width:100%;margin-top:8px" :disabled="!selectedCard">确认缴费 ¥{{queryResult.billAmount}}</el-button>
      </div>
    </el-card>

    <!-- 缴费记录 -->
    <el-card v-if="history.length" style="max-width:700px;margin-top:12px">
      <template #header>缴费记录</template>
      <el-table :data="history" size="small" stripe><el-table-column prop="time" label="时间" width="160"/><el-table-column prop="type" label="类型" width="80"/><el-table-column label="金额" width="100"><template #default="{row}">¥{{row.amount}}</template></el-table-column><el-table-column prop="account" label="户号"/></el-table>
    </el-card>
  </div>
</template>

<style scoped>
.payment-page{max-width:800px;margin:0 auto;padding:20px 40px}
.cat-bar{display:flex;gap:12px;flex-wrap:wrap}
.cat-item{text-align:center;padding:12px 20px;background:#f5f7fa;border-radius:8px;cursor:pointer;border:2px solid transparent;display:flex;flex-direction:column;gap:4px}
.cat-item:hover,.cat-item.active{border-color:var(--color-primary);background:#ecf5ff}
.cat-icon{font-size:24px}
</style>
