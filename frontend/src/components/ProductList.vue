<script setup>
import { ref, computed, onMounted, watch } from 'vue'
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
const keyword = ref('')
const sortOrder = ref(0) // 0=默认 1=利率升 2=利率降 3=最新上线
const compareSet = ref(new Set())
const showCompare = ref(false)
function toggleCompare(id) {
  const s = new Set(compareSet.value)
  if (s.has(id)) s.delete(id); else { if (s.size >= 4) { ElMessageBox.alert('最多对比4款'); return }; s.add(id) }
  compareSet.value = s
}
const compareProducts = computed(() => products.value.filter(p => compareSet.value.has(p.id)))

const sortedProducts = computed(() => {
  if (sortOrder.value === 0) return products.value
  if (sortOrder.value === 3) return [...products.value].sort((a, b) => b.id - a.id)
  return [...products.value].sort((a, b) => sortOrder.value === 1 ? a.rate - b.rate : b.rate - a.rate)
})

async function fetchData() {
  loading.value = true
  try {
    const params = { productType: props.type, page: page.value, pageSize: 12 }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    const res = await getProductList(params)
    if (res.code === 200) {
      products.value = res.data.list
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

function handleClear() {
  keyword.value = ''
  page.value = 1
  fetchData()
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
    <div style="margin-bottom:8px;color:#909399;font-size:13px" v-if="!loading">共 {{ total }} 款产品</div>
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索产品名称..."
        clearable
        @clear="handleClear"
        @keyup.enter="handleSearch"
        style="max-width: 300px"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
      <el-select v-model="sortOrder" style="width:130px;margin-left:12px" @change="handleSearch">
        <el-option :value="0" label="默认排序" />
        <el-option :value="1" label="利率升序" />
        <el-option :value="2" label="利率降序" />
        <el-option :value="3" label="最新上线" />
      </el-select>
    </div>
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="6" v-for="p in sortedProducts" :key="p.id" class="product-col">
        <el-card class="product-card" shadow="hover" @click="router.push({name:'productDetail',query:{id:p.id}})" style="cursor:pointer">
          <img :src="getImageUrl(p.imgPath)" class="product-img" />
          <h3>{{ p.productName }} <el-tag v-if="p.productStatus===1" type="info" size="small">停售</el-tag></h3>
          <p class="rate" v-if="p.rate > 0">{{ p.rate }}%</p>
          <p class="intro">{{ p.intro }}</p>
          <div style="display:flex;justify-content:space-between;align-items:center">
            <el-button type="primary" size="small" @click.stop="applyProduct(p)" :disabled="p.productStatus===1">
              {{ p.productStatus===1 ? '已停售' : '立即申请' }}</el-button>
            <span @click.stop><el-checkbox style="margin:0" :model-value="compareSet.has(p.id)" @change="toggleCompare(p.id)">对比</el-checkbox></span>
          </div>
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

    <!-- 对比栏 -->
    <div v-if="compareSet.size > 0" style="position:fixed;bottom:20px;left:50%;transform:translateX(-50%);background:#fff;padding:12px 24px;border-radius:8px;box-shadow:0 4px 20px rgba(0,0,0,0.15);z-index:100;display:flex;align-items:center;gap:16px">
      <span>已选 {{ compareSet.size }}/4 款</span>
      <el-button type="primary" size="small" @click="showCompare = true" :disabled="compareSet.size < 2">开始对比</el-button>
      <el-button size="small" @click="compareSet = new Set()">取消</el-button>
    </div>

    <!-- 对比弹窗 -->
    <el-dialog v-model="showCompare" title="产品对比" width="90%">
      <el-table :data="compareProducts" stripe>
        <el-table-column label="图片" width="100">
          <template #default="{ row }"><img :src="getImageUrl(row.imgPath)" style="width:80px;height:50px;object-fit:cover;border-radius:4px" /></template>
        </el-table-column>
        <el-table-column prop="productName" label="产品名" width="160" />
        <el-table-column label="年化利率"><template #default="{ row }">{{ row.rate }}%</template></el-table-column>
        <el-table-column label="起购金额"><template #default="{ row }">¥{{ row.minAmount || 0 }}</template></el-table-column>
        <el-table-column label="期限"><template #default="{ row }">{{ row.term || '-' }}</template></el-table-column>
        <el-table-column label="风险等级"><template #default="{ row }">{{ {1:'低',2:'中',3:'高'}[row.riskLevel] || '-' }}</template></el-table-column>
        <el-table-column label="手续费"><template #default="{ row }">{{ row.feeDesc || '免手续费' }}</template></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>
