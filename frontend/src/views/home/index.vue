<script setup>
import { ref, onMounted, computed } from 'vue'
import { getBannerList } from '@/api/banner'
import { getProductList } from '@/api/product'
import { getNewsList } from '@/api/news'
import { useRouter } from 'vue-router'
import { IMAGE_BASE_URL } from '@/utils/constants'

const router = useRouter()
const banners = ref([])
const products = ref([])
const recommends = ref([])
const catCounts = ref({})
const latestNews = ref([])
const imageBaseUrl = IMAGE_BASE_URL

const recentHistory = ref([])

onMounted(async () => {
  const bannerRes = await getBannerList()
  if (bannerRes.code === 200) banners.value = bannerRes.data

  const productRes = await getProductList({ page: 1, pageSize: 8 })
  if (productRes.code === 200) products.value = productRes.data.list

  const newsRes = await getNewsList({ page: 1, pageSize: 5 })
  if (newsRes.code === 200) latestNews.value = newsRes.data.list

  const recRes = await getProductList({ page: 1, pageSize: 50 })
  if (recRes.code === 200) {
    recommends.value = recRes.data.list.filter(p => p.productStatus === 0).sort((a,b) => b.rate - a.rate).slice(0, 4)
    catCounts.value = {1:0,2:0,3:0,4:0}
    recRes.data.list.forEach(p => { if (catCounts.value[p.productType] !== undefined) catCounts.value[p.productType]++ })
  }

  recentHistory.value = JSON.parse(localStorage.getItem('productHistory')||'[]')
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

function clearHistory() {
  localStorage.removeItem('productHistory')
  recentHistory.value = []
}
function scrollTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
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

    <!-- 快捷入口 -->
    <div class="section-wrapper">
      <div class="quick-entries">
        <div class="quick-item" @click="goPage(1)"><span class="qi-icon">💰</span><span>存款理财</span></div>
        <div class="quick-item" @click="goPage(2)"><span class="qi-icon">💳</span><span>银行卡</span></div>
        <div class="quick-item" @click="router.push('/payment')"><span class="qi-icon">📋</span><span>生活缴费</span></div>
        <div class="quick-item" @click="router.push('/calculator')"><span class="qi-icon">🧮</span><span>贷款计算</span></div>
        <div class="quick-item" @click="router.push('/branches')"><span class="qi-icon">📍</span><span>网点查询</span></div>
      </div>
    </div>

    <!-- 四大业务卡片 -->
    <div class="section-wrapper">
      <div class="category-nav">
        <el-card class="category-card" shadow="hover" @click="goPage(1)">
          <el-badge :value="catCounts[1]||0" type="primary"><div class="card-icon">🏦</div></el-badge>
          <h3>个人业务</h3>
          <p>存款理财，稳健增值</p>
        </el-card>
        <el-card class="category-card" shadow="hover" @click="goPage(2)">
          <el-badge :value="catCounts[2]||0" type="danger"><div class="card-icon">💳</div></el-badge>
          <h3>银行卡</h3>
          <p>借记卡/信用卡/储蓄卡</p>
        </el-card>
        <el-card class="category-card" shadow="hover" @click="goPage(3)">
          <el-badge :value="catCounts[3]||0" type="warning"><div class="card-icon">🏢</div></el-badge>
          <h3>公司金融</h3>
          <p>企业融资，快速发展</p>
        </el-card>
        <el-card class="category-card" shadow="hover" @click="goPage(4)">
          <el-badge :value="catCounts[4]||0" type="success"><div class="card-icon">🌾</div></el-badge>
          <h3>普惠金融</h3>
          <p>服务大众，助力梦想</p>
        </el-card>
      </div>
    </div>

    <!-- 最新公告 -->
    <div class="section-wrapper" v-if="latestNews.length">
      <div class="news-section">
        <h2 class="section-title">最新公告</h2>
        <div class="news-list">
          <div class="news-item" v-for="item in latestNews" :key="item.id" @click="router.push({ name: 'newsDetail', query: { id: item.id } })">
            <span class="news-dot">•</span>
            <span class="news-item-title">{{ item.title }}</span>
            <span class="news-item-time">{{ item.createTime?.slice(0, 10) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 为您推荐 -->
    <div class="section-wrapper" v-if="recommends.length">
      <div class="product-section">
        <h2 class="section-title">🔥 为您推荐 <span style="font-size:13px;color:#909399;font-weight:normal">— 高收益精选</span></h2>
        <el-row :gutter="24" type="flex" align="stretch">
          <el-col :xs="12" :sm="8" :md="6" v-for="p in recommends" :key="'r'+p.id" class="product-col">
            <el-card class="product-card" shadow="hover" @click="router.push({name:'productDetail',query:{id:p.id}})">
              <img :src="getImageUrl(p.imgPath)" class="product-img" />
              <h3>{{ p.productName }} <el-tag size="small" type="danger">推荐</el-tag></h3>
              <p class="rate" v-if="p.rate > 0">{{ p.rate }}%</p>
              <p class="intro">{{ p.intro?.slice(0,30) }}...</p>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 最近浏览 -->
    <div class="section-wrapper" v-if="recentHistory.length">
      <div class="product-section">
        <h2 class="section-title">🕐 最近浏览 <span style="font-size:12px;color:#409eff;cursor:pointer;font-weight:normal" @click="clearHistory">清空</span></h2>
        <div style="display:flex;gap:12px;flex-wrap:wrap">
          <el-tag v-for="h in recentHistory" :key="h.id" size="large" style="cursor:pointer;padding:8px 14px" @click="router.push({name:'productDetail',query:{id:h.id}})">
            {{ h.name }} <span style="color:#e6a23c;margin-left:4px">{{ h.rate }}%</span>
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 热门产品 -->
    <div class="section-wrapper">
      <div class="product-section">
        <h2 class="section-title">热门产品</h2>
        <el-row :gutter="24" type="flex" align="stretch">
          <el-col :xs="12" :sm="8" :md="6" v-for="p in products" :key="p.id" class="product-col">
            <el-card class="product-card" shadow="hover" @click="router.push({name:'productDetail',query:{id:p.id}})" style="cursor:pointer">
              <img :src="getImageUrl(p.imgPath)" class="product-img" />
              <h3>{{ p.productName }} <el-tag v-if="p.productStatus===1" type="info" size="small">停售</el-tag></h3>
              <p class="rate" v-if="p.rate > 0">{{ p.rate }}%</p>
              <p class="intro">{{ (p.intro||'').slice(0,40) }}{{ (p.intro||'').length>40?'...':'' }}</p>
              <el-button type="primary" size="small" @click.stop="applyProduct(p)" :disabled="p.productStatus===1">
                {{ p.productStatus===1 ? '已停售' : '立即申请' }}</el-button>
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

/* ===== 快捷入口 ===== */
.quick-entries {
  width: 100%; max-width: 1200px; display: flex; gap: 16px;
  padding: 20px 20px 0;
}
.quick-item {
  flex: 1; text-align: center; padding: 16px; cursor: pointer;
  background: #f5f7fa; border-radius: 8px; transition: all 0.2s;
  display: flex; flex-direction: column; gap: 6px;
}
.quick-item:hover { background: var(--color-primary); color: #fff; }
.qi-icon { font-size: 28px; }

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

/* ===== 最新公告 ===== */
.news-section {
  width: 100%;
  max-width: 1200px;
  padding: 0 20px 30px;
}
.news-list {
  background: #fff;
  border-radius: 8px;
  padding: 16px 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.news-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}
.news-item:hover { color: var(--color-primary); }
.news-item:last-child { border-bottom: none; }
.news-dot { color: var(--color-primary); font-weight: bold; margin-right: 8px; }
.news-item-title { flex: 1; font-size: 14px; }
.news-item-time { font-size: 13px; color: #c0c4cc; }

/* ===== 产品卡片统一 ===== */
.product-card {
  display: flex !important; flex-direction: column; height: 100%;
}
:deep(.el-card__body) {
  flex: 1; display: flex; flex-direction: column;
}
.product-card img.product-img {
  width: 100%; height: 180px; object-fit: cover; border-radius: 4px; flex-shrink: 0;
}
.product-card h3 {
  font-size: 15px; margin: 8px 0 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.product-card .intro {
  font-size: 13px; color: #909399; line-height: 1.5; flex:1;
  overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
}
.product-card .rate { font-size: 17px; color: #e6a23c; font-weight: bold; margin: 2px 0; }

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
