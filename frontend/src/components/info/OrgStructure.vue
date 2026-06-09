<template>
  <el-card class="info-card">
    <template #header>
      <span class="card-title">组织架构</span>
    </template>
    <el-row :gutter="20">
      <el-col :span="8">
        <h4>公司治理</h4>
        <ul>
          <li>股东大会</li>
          <li>董事会</li>
          <li>监事会</li>
          <li>高级管理层</li>
        </ul>
      </el-col>
      <el-col :span="8">
        <h4>业务部门</h4>
        <ul>
          <li>个人金融部</li>
          <li>公司金融部</li>
          <li>信用卡中心</li>
          <li>普惠金融部</li>
        </ul>
      </el-col>
      <el-col :span="8">
        <h4>支持部门</h4>
        <ul>
          <li>风险管理部</li>
          <li>信息技术部</li>
          <li>运营管理部</li>
          <li>合规审计部</li>
        </ul>
      </el-col>
    </el-row>
  </el-card>
</template>

<style scoped>
.info-card { margin-bottom: 20px; }
.card-title { font-size: 18px; font-weight: bold; }
.info-card ul { list-style: none; padding: 0; }
.info-card ul li { padding: 6px 0; color: var(--color-text-regular); font-size: 14px; }
.info-card ul li::before { content: "• "; color: var(--color-primary); }
.info-card h4 { margin: 0 0 8px; color: var(--color-text-primary); }
</style>
