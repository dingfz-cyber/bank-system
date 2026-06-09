<script setup>
import { useRouter } from 'vue-router'
</script>

<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <p>&copy; 2026 网银系统 版权所有 | 技术支持：Claude Code</p>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  width: 100%;
  background: var(--color-white);
  border-top: 1px solid var(--color-border);
  height: var(--footer-height);
  flex-shrink: 0;
}
.footer-inner {
  max-width: 1200px;
  height: 100%;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
}
.footer-inner p {
  color: var(--color-text-secondary);
  font-size: 13px;
  margin: 0;
}
</style>
