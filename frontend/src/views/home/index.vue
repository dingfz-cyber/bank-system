<script setup>
import { ref, onMounted } from 'vue'
import { getBannerList } from '@/api/banner'
import { getProductList } from '@/api/product'
import { useRouter } from 'vue-router'
import { IMAGE_BASE_URL } from '@/utils/constants'

const router = useRouter()
const banners = ref([])
const products = ref([])
const imageBaseUrl = IMAGE_BASE_URL

onMounted(async () => {
  const bannerRes = await getBannerList()
  if (bannerRes.code === 200) banners.value = bannerRes.data

  const productRes = await getProductList({ page: 1, pageSize: 8 })
  if (productRes.code === 200) products.value = productRes.data.list
})

function getImageUrl(path) {
  return path ? `${imageBaseUrl}${path}` : ''
}

function applyProduct(product) {
  const target = product.productType === 2 ? '/apply/card' : '/apply/loan'
  router.push({ path: target, query: { productId: product.id, productName: product.productName } })
}

function goPage(type) {
  const map = { 1: '/personal', 2: '/credit', 3: '/company', 4: '/puhui' }
  router.push(map[type] || '/')
}
</script>

<template>
  <div class="home-page">
    <!-- 轮播图 - 通栏 -->
    <div class="carousel-wrapper">
      <el-carousel height="400px" v-if="banners.length" :interval="4000">
        <el-carousel-item v-for="b in banners" :key="b.id">
          <div class="banner-item" @click="b.jumpRoute && router.push(b.jumpRoute)">
            <img :src="getImageUrl(b.imgPath)" :alt="b.imgPath" class="banner-img" />
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 四大业务卡片 -->
    <div class="section-wrapper">
      <div class="category-nav">
        <el-card class="category-card" shadow="hover" @click="goPage(1)">
          <div class="card-icon">🏦</div>
          <h3>个人业务</h3>
          <p>存款理财，稳健增值</p>
        </el-card>
        <el-card class="category-card" shadow="hover" @click="goPage(2)">
          <div class="card-icon">💳</div>
          <h3>信用卡</h3>
          <p>消费优惠，便捷生活</p>
        </el-card>
        <el-card class="category-card" shadow="hover" @click="goPage(3)">
          <div class="card-icon">🏢</div>
          <h3>公司金融</h3>
          <p>企业融资，快速发展</p>
        </el-card>
        <el-card class="category-card" shadow="hover" @click="goPage(4)">
          <div class="card-icon">🌾</div>
          <h3>普惠金融</h3>
          <p>服务大众，助力梦想</p>
        </el-card>
      </div>
    </div>

    <!-- 热门产品 -->
    <div class="section-wrapper">
      <div class="product-section">
        <h2 class="section-title">热门产品</h2>
        <el-row :gutter="24">
          <el-col :xs="12" :sm="8" :md="6" v-for="p in products" :key="p.id" class="product-col">
            <el-card class="product-card" shadow="hover">
              <img :src="getImageUrl(p.imgPath)" class="product-img" />
              <h3>{{ p.productName }}</h3>
              <p class="rate" v-if="p.rate > 0">{{ p.rate }}%</p>
              <p class="intro">{{ p.intro }}</p>
              <el-button type="primary" size="small" @click="applyProduct(p)">立即申请</el-button>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ===== 轮播通栏 ===== */
.carousel-wrapper {
  width: 100%;
}
.banner-item {
  width: 100%;
  height: 100%;
  cursor: pointer;
}
.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* ===== 分类卡片 ===== */
.section-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}
.category-nav {
  width: 100%;
  max-width: 1200px;
  display: flex;
  gap: 20px;
  padding: 40px 20px;
}
.category-card {
  flex: 1;
  cursor: pointer;
  text-align: center;
  padding: 30px 10px;
  transition: transform 0.2s;
  border-radius: 8px;
}
.category-card:hover {
  transform: translateY(-6px);
}
.card-icon {
  font-size: 40px;
  margin-bottom: 12px;
}
.category-card h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: var(--color-text-primary);
}
.category-card p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

/* ===== 热门产品 ===== */
.product-section {
  width: 100%;
  max-width: 1200px;
  padding: 0 20px 50px;
}
.section-title {
  font-size: 24px;
  color: var(--color-text-primary);
  margin-bottom: 24px;
  text-align: left;
}
</style>
