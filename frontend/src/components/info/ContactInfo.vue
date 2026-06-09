<template>
  <el-card class="info-card">
    <template #header>
      <span class="card-title">联系方式</span>
    </template>
    <el-descriptions :column="1" border>
      <el-descriptions-item label="客服热线">400-888-8888（7×24小时）</el-descriptions-item>
      <el-descriptions-item label="信用卡专线">400-888-9999</el-descriptions-item>
      <el-descriptions-item label="投诉建议">400-888-6666</el-descriptions-item>
      <el-descriptions-item label="营业时间">周一至周五 9:00-17:00（法定节假日除外）</el-descriptions-item>
      <el-descriptions-item label="总行地址">xx市xx新区xx大道100号 网银大厦</el-descriptions-item>
      <el-descriptions-item label="官方网站">www.bank-demo.com</el-descriptions-item>
      <el-descriptions-item label="电子邮件">service@bank-demo.com</el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<style scoped>
.info-card { margin-bottom: 20px; }
.card-title { font-size: 18px; font-weight: bold; }
</style>
