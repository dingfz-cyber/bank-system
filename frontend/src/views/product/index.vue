<script setup>
import { ref, onMounted, watch } from 'vue'
import { getProductList } from '@/api/product'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import { IMAGE_BASE_URL } from '@/utils/constants'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(true)
const product = ref(null)
const related = ref([])
const imageBaseUrl = IMAGE_BASE_URL

async function loadProduct(id) {
  loading.value = true
  product.value = null; related.value = []
  if (id) {
    const allRes = await getProductList({ page: 1, pageSize: 100 })
    if (allRes.code === 200) {
      product.value = allRes.data.list.find(p => p.id == id)
      if (product.value) {
        const h = JSON.parse(localStorage.getItem('productHistory')||'[]')
        const f = h.filter(x => x.id !== product.value.id)
        f.unshift({id:product.value.id,name:product.value.productName,rate:product.value.rate,img:product.value.imgPath})
        localStorage.setItem('productHistory',JSON.stringify(f.slice(0,6)))
        const relRes = await getProductList({ productType: product.value.productType, pageSize: 4 })
        if (relRes.code === 200) related.value = relRes.data.list.filter(r => r.id != id).slice(0, 4)
      }
    }
  }
  loading.value = false
}

onMounted(() => loadProduct(route.query.id))
watch(() => route.query.id, (newId) => { if (newId) loadProduct(newId) })

function applyNow() {
  if (!userStore.isLoggedIn) {
    ElMessageBox.confirm('请先登录后再申请', '提示', { confirmButtonText: '去登录' })
      .then(() => router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } }))
      .catch(() => {})
    return
  }
  const target = product.value.productType === 2 ? '/apply/card' : '/apply/loan'
  router.push({ path: target, query: { productId: product.value.id, productName: product.value.productName } })
}

const riskLabels = { 1: '低风险', 2: '中风险', 3: '高风险' }
</script>

<template>
  <div v-if="loading" style="text-align:center;padding:60px"><el-icon class="is-loading" :size="32"><svg viewBox="0 0 1024 1024"><path d="M512 64a448 448 0 1 0 448 448A448 448 0 0 0 512 64z" fill="none" stroke="currentColor" stroke-width="64"/></svg></el-icon><p style="color:#999;margin-top:12px">加载中...</p></div>
  <div class="page detail-page" v-else-if="product">
    <div style="margin-bottom:12px"><el-button size="small" @click="router.back()">← 返回</el-button></div>
    <h2 class="page-title">{{ product.productName }}</h2>
    <el-row :gutter="24">
      <el-col :span="10">
        <img :src="imageBaseUrl + product.imgPath" style="width:100%;border-radius:8px" />
      </el-col>
      <el-col :span="14">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="年化利率">{{ product.rate }}%</el-descriptions-item>
          <el-descriptions-item label="起购金额">¥{{ product.minAmount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="期限">{{ product.term || '-' }}</el-descriptions-item>
          <el-descriptions-item label="风险等级">
            <el-tag :type="product.riskLevel === 1 ? 'success' : product.riskLevel === 3 ? 'danger' : 'warning'" size="small">
              {{ riskLabels[product.riskLevel] || '-' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="手续费">{{ product.feeDesc || '免手续费' }}</el-descriptions-item>
          <el-descriptions-item label="产品状态">
            <el-tag :type="product.productStatus === 0 ? 'success' : 'info'" size="small">
              {{ product.productStatus === 0 ? '正常' : '停售' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <p style="margin-top:16px;color:#909399;font-size:13px">
          ⚠ 理财非存款，产品有风险，投资需谨慎
        </p>
        <el-button type="primary" @click="applyNow" style="margin-top:16px" :disabled="product.productStatus !== 0">
          {{ product.productStatus === 0 ? '立即申请' : '已停售' }}
        </el-button>
      </el-col>
    </el-row>
    <el-card style="margin-top:20px">
      <template #header>产品介绍</template>
      <div style="line-height:1.8;white-space:pre-wrap">{{ product.intro }}</div>
    </el-card>

    <!-- 相关推荐 -->
    <div v-if="related.length" style="margin-top:24px">
      <h3 style="margin-bottom:12px">同类推荐</h3>
      <el-row :gutter="16">
        <el-col :span="6" v-for="r in related" :key="r.id">
          <el-card shadow="hover" style="cursor:pointer" @click="router.push({name:'productDetail',query:{id:r.id}})">
            <img :src="imageBaseUrl+r.imgPath" style="width:100%;height:120px;object-fit:cover;border-radius:4px" />
            <div style="font-size:14px;font-weight:bold;margin:8px 0 4px">{{ r.productName }}</div>
            <div style="color:#e6a23c;font-size:16px;font-weight:bold">{{ r.rate }}%</div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
  <el-empty v-else description="产品不存在" />
</template>

<style scoped>
.detail-page { max-width:1000px; margin:0 auto; padding:20px 40px; }
</style>
