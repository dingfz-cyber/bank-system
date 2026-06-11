<script setup>
import { useRouter } from 'vue-router'
</script>

<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <div class="footer-links">
        <span>客服热线：95588（模拟）</span>
        <span>|</span>
        <span>隐私政策</span>
        <span>|</span>
        <span>免责声明</span>
        <span>|</span>
        <span>沪ICP备2024000001号-1</span>
      </div>
      <p>&copy; 2026 网银系统 版权所有 &nbsp; v3.2</p>
      <p style="font-size:11px;color:#c0c4cc">本系统为毕业设计演示项目，所有数据均为模拟数据，不涉及真实金融业务</p>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  width: 100%;
  background: var(--color-white);
  border-top: 1px solid var(--color-border);
  padding: 16px 0;
  flex-shrink: 0;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 0 20px;
}
.footer-links {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: var(--color-text-secondary);
}
.footer-inner p {
  color: var(--color-text-secondary);
  font-size: 13px;
  margin: 0;
}
</style>
