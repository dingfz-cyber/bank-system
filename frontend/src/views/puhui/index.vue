<script setup>
import ProductList from '@/components/ProductList.vue'
</script>

<template>
  <div class="page">
    <h2 class="page-title">普惠金融</h2>
    <p class="page-desc">助农贷款、创业支持、教育分期，服务千家万户</p>
    <ProductList :type="4" />
  </div>
</template>
