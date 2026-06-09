<script setup>
import ProductList from '@/components/ProductList.vue'
</script>

<template>
  <div class="page">
    <h2 class="page-title">个人业务</h2>
    <p class="page-desc">提供存款、理财、养老等多元化个人金融服务</p>
    <ProductList :type="1" />
  </div>
</template>
