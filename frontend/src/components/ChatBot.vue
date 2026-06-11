<script setup>
import { ref } from 'vue'

const show = ref(false)
const input = ref('')
const messages = ref([{role:'bot',text:'您好！我是智能客服小银，有什么可以帮您？'}])


const faq = [
  {q:'如何办卡',a:'登录后浏览信用卡产品 → 点击"立即申请" → 填写信息提交 → 等待审核。审核通过后系统自动为您生成银行卡。'},
  {q:'如何转账',a:'登录 → 点击导航栏"转账汇款" → 选择付款卡和输入收款卡号 → 输入金额和交易密码 → 确认转账。大额转账(≥5万)需二次确认。'},
  {q:'忘记密码',a:'登录页点击"忘记密码" → 输入注册手机号 → 输入验证码(演示:888888) → 设置新密码。'},
  {q:'交易密码',a:'交易密码是独立的6位数字密码，用于转账、缴费等资金操作。在"个人中心 → 修改密码"区域设置。'},
  {q:'审核进度',a:'提交申请后，管理员审核结果会以站内消息通知您。也可在"个人中心 → 我的申请"查看进度。'},
  {q:'营业时间',a:'网点营业时间一般为09:00-17:30，部分网点营业至17:00。可通过"网点查询"查看各网点具体时间。'},
  {q:'客服电话',a:'客服热线：95588（模拟），工作时间：7×24小时。'},
]

function send() {
  const q = input.value.trim(); if (!q) return
  messages.value.push({role:'user',text:q})
  const f = faq.find(x => q.includes(x.q.substring(0,2)) || x.q.includes(q.substring(0,2)))
  setTimeout(() => {
    messages.value.push({role:'bot',text: f ? f.a : '请尝试以下关键词：办卡、转账、忘记密码、交易密码、审核进度、营业时间、客服电话'})
  }, 400)
  input.value = ''
}
</script>

<template>
  <div v-if="show" style="position:fixed;bottom:80px;right:20px;width:340px;height:420px;background:#fff;border-radius:12px;box-shadow:0 4px 24px rgba(0,0,0,0.15);z-index:200;display:flex;flex-direction:column">
    <div style="background:#409eff;color:#fff;padding:12px 16px;border-radius:12px 12px 0 0;display:flex;justify-content:space-between">
      <span>🤖 智能客服</span>
      <span style="cursor:pointer" @click="show=false">✕</span>
    </div>
    <div style="flex:1;overflow-y:auto;padding:12px">
      <div v-for="(m,i) in messages" :key="i" :style="{textAlign:m.role==='user'?'right':'left',marginBottom:'10px'}">
        <span :style="{background:m.role==='user'?'#409eff':'#f0f0f0',color:m.role==='user'?'#fff':'#333',padding:'8px 12px',borderRadius:'8px',display:'inline-block',maxWidth:'80%',fontSize:'13px'}">{{ m.text }}</span>
      </div>
    </div>
    <div style="padding:8px;border-top:1px solid #eee;display:flex;gap:8px">
      <el-input v-model="input" placeholder="输入问题..." size="small" @keyup.enter="send" />
      <el-button type="primary" size="small" @click="send">发送</el-button>
    </div>
  </div>
  <div @click="show=!show" style="position:fixed;bottom:20px;right:20px;width:50px;height:50px;background:#409eff;color:#fff;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:24px;cursor:pointer;box-shadow:0 2px 12px rgba(0,0,0,0.2);z-index:200">💬</div>
</template>
