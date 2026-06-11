<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
import ChatBot from '@/components/ChatBot.vue'

const showTop = ref(false)
function onScroll() { showTop.value = window.scrollY > 300 }
function toTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }
onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<template>
  <div class="app-layout">
    <AppHeader />
    <main class="app-main">
      <router-view />
    </main>
    <AppFooter />
    <ChatBot />
    <div v-show="showTop" @click="toTop" style="position:fixed;bottom:80px;right:28px;width:40px;height:40px;background:#409eff;color:#fff;border-radius:50%;display:flex;align-items:center;justify-content:center;cursor:pointer;z-index:150;font-size:20px">⬆</div>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 100%;
}
.app-main {
  flex: 1;
  background: var(--color-bg);
}
</style>
