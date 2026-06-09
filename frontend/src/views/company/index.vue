<script setup>
import ProductList from '@/components/ProductList.vue'
</script>

<template>
  <div class="page">
    <h2 class="page-title">公司金融</h2>
    <p class="page-desc">为企业提供贷款、结算、供应链金融等全方位服务</p>
    <ProductList :type="3" />
  </div>
</template>
