<template>
  <el-card class="info-card">
    <template #header>
      <span class="card-title">风险提示</span>
    </template>
    <el-alert title="理财非存款，产品有风险，投资须谨慎" type="warning" :closable="false" show-icon />
    <p style="margin-top: 12px; font-size: 13px; color: var(--color-text-secondary);">
      以上产品信息仅供参考，具体条款以实际签署合同为准。历史业绩不代表未来表现。贷款实际利率以审批结果为准。
    </p>
  </el-card>
</template>

<style scoped>
.info-card { margin-bottom: 20px; }
.card-title { font-size: 18px; font-weight: bold; }
</style>
