<template>
  <el-card class="info-card">
    <template #header>
      <span class="card-title">服务承诺</span>
    </template>
    <el-steps :active="4" align-center>
      <el-step title="安全可靠" description="银行级安全保障" />
      <el-step title="高效便捷" description="线上办理快速响应" />
      <el-step title="收费透明" description="所有费用明码标价" />
      <el-step title="客户至上" description="7×24小时贴心服务" />
    </el-steps>
  </el-card>
</template>

<style scoped>
.info-card { margin-bottom: 20px; }
.card-title { font-size: 18px; font-weight: bold; }
</style>
