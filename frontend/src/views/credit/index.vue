<script setup>
import ProductList from '@/components/ProductList.vue'
</script>

<template>
  <div class="page">
    <h2 class="page-title">信用卡</h2>
    <p class="page-desc">多种信用卡选择，享受消费优惠与专属权益</p>
    <ProductList :type="2" />
  </div>
</template>
