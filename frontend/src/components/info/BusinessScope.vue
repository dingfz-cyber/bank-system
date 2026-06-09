<script setup>
const businessScope = [
  { category: '个人业务', content: '本外币储蓄、个人理财、大额存单、代理保险、基金代销、黄金买卖等' },
  { category: '信用卡', content: '标准信用卡、白金信用卡、车主联名卡、商旅信用卡等卡种发行与服务' },
  { category: '公司金融', content: '企业存贷款、供应链金融、票据贴现、国际结算、单位结算卡等' },
  { category: '普惠金融', content: '助农贷款、创业担保贷、教育分期、小微企业信用贷款等' },
  { category: '电子银行', content: '网上银行、手机银行、微信银行、电话银行等线上渠道服务' }
]
</script>

<template>
  <el-card class="info-card">
    <template #header>
      <span class="card-title">业务范围</span>
    </template>
    <el-table :data="businessScope" stripe>
      <el-table-column prop="category" label="业务类别" width="150" />
      <el-table-column prop="content" label="主要内容" />
    </el-table>
  </el-card>
</template>

<style scoped>
.info-card { margin-bottom: 20px; }
.card-title { font-size: 18px; font-weight: bold; }
</style>
