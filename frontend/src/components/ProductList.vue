<script setup>
import { ref, onMounted, watch } from 'vue'
import { getProductList } from '@/api/product'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import { IMAGE_BASE_URL } from '@/utils/constants'

const props = defineProps({
  type: { type: Number, default: 0 }
})

const router = useRouter()
const userStore = useUserStore()
const products = ref([])
const loading = ref(false)
const imageBaseUrl = IMAGE_BASE_URL
const page = ref(1)
const total = ref(0)

async function fetchData() {
  loading.value = true
  try {
    const res = await getProductList({ productType: props.type, page: page.value, pageSize: 12 })
    if (res.code === 200) {
      products.value = res.data.list
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function getImageUrl(path) {
  return path ? `${imageBaseUrl}${path}` : ''
}

function applyProduct(product) {
  if (!userStore.isLoggedIn) {
    ElMessageBox.confirm('请先登录后再申请', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消'
    }).then(() => {
      router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
    }).catch(() => {})
    return
  }
  const target = product.productType === 2 ? '/apply/card' : '/apply/loan'
  router.push({ path: target, query: { productId: product.id, productName: product.productName } })
}

onMounted(fetchData)
watch(() => props.type, fetchData)
</script>

<template>
  <div class="product-list">
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6" v-for="p in products" :key="p.id" class="product-col">
        <el-card class="product-card" shadow="hover">
          <img :src="getImageUrl(p.imgPath)" class="product-img" />
          <h3>{{ p.productName }}</h3>
          <p class="rate" v-if="p.rate > 0">{{ p.rate }}%</p>
          <p class="intro">{{ p.intro }}</p>
          <el-button type="primary" size="small" @click="applyProduct(p)">立即申请</el-button>
        </el-card>
      </el-col>
      <el-empty v-if="!loading && !products.length" description="暂无产品" />
    </el-row>
    <el-pagination
      v-if="total > 0"
      v-model:current-page="page"
      :page-size="12"
      :total="total"
      layout="prev, pager, next"
      class="pagination"
    />
  </div>
</template>
