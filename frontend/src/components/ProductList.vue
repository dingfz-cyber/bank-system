<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getProductList } from '@/api/product'
import { buyWealth } from '@/api/wealth'
import { getMyCards } from '@/api/card'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
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
const sortOrder = ref(0)
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
    if (res.code === 200) { products.value = res.data.list; total.value = res.data.total }
  } finally { loading.value = false }
}

function handleSearch() { page.value = 1; fetchData() }
function handleClear() { keyword.value = ''; page.value = 1; fetchData() }

function getImageUrl(path) { return path ? `${imageBaseUrl}${path}` : '' }

const wealthDlg=ref(false); const wealthProd=ref(null); const wealthCards=ref([]); const wealthCard=ref(null); const wealthAmount=ref('')
async function openWealth(p){
  if(!userStore.isLoggedIn){ElMessageBox.confirm('请先登录','提示',{confirmButtonText:'去登录'}).then(()=>router.push({name:'login',query:{redirect:router.currentRoute.value.fullPath}}));return}
  if(p.productType===1&&p.rate>0){ wealthProd.value=p; wealthAmount.value=p.minAmount||1000; wealthDlg.value=true; const r=await getMyCards(); if(r.code===200) wealthCards.value=r.data.filter(c=>c.status===0) }else applyProduct(p)
}
async function doBuyWealth(){
  if(!wealthCard.value){ElMessage.error('请选择付款卡');return}
  if(!wealthAmount.value||Number(wealthAmount.value)<=0){ElMessage.error('请输入有效金额');return}
  await buyWealth({cardId:wealthCard.value,productId:wealthProd.value.id,productName:wealthProd.value.productName,amount:Number(wealthAmount.value),rate:wealthProd.value.rate})
  ElMessage.success('购买成功!积分+20'); wealthDlg.value=false
}

function applyProduct(product) {
  if (!userStore.isLoggedIn) {
    ElMessageBox.confirm('请先登录后再申请', '提示', { confirmButtonText: '去登录', cancelButtonText: '取消' })
      .then(() => router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })).catch(() => {})
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
      <el-input v-model="keyword" placeholder="搜索产品名称..." clearable @clear="handleClear" @keyup.enter="handleSearch" style="max-width:300px">
        <template #append><el-button @click="handleSearch">搜索</el-button></template>
      </el-input>
      <el-select v-model="sortOrder" style="width:130px;margin-left:12px" @change="handleSearch">
        <el-option :value="0" label="默认排序" />
        <el-option :value="1" label="利率升序" />
        <el-option :value="2" label="利率降序" />
        <el-option :value="3" label="最新上线" />
      </el-select>
    </div>
    <el-row :gutter="20" v-loading="loading" type="flex">
      <el-col :span="6" v-for="p in sortedProducts" :key="p.id" class="product-col" style="display:flex">
        <el-card class="product-card" shadow="hover" @click="router.push({name:'productDetail',query:{id:p.id}})" style="cursor:pointer;width:100%">
          <div class="img-box"><img :src="getImageUrl(p.imgPath)" class="product-img" /></div>
          <h3>{{ p.productName }} <el-tag v-if="p.productStatus===1" type="info" size="small">停售</el-tag></h3>
          <p class="rate" v-if="p.rate > 0">{{ p.rate }}%</p>
          <p class="intro">{{ p.intro }}</p>
          <div style="display:flex;justify-content:space-between;align-items:center">
            <el-button type="primary" size="small" @click.stop="openWealth(p)" :disabled="p.productStatus===1">
              {{ p.productStatus===1 ? '已停售' : '立即申请' }}</el-button>
            <span @click.stop><el-checkbox style="margin:0" :model-value="compareSet.has(p.id)" @change="toggleCompare(p.id)">对比</el-checkbox></span>
          </div>
        </el-card>
      </el-col>
      <el-empty v-if="!loading && !products.length" description="暂无产品" />
    </el-row>
    <el-pagination v-if="total > 0" v-model:current-page="page" :page-size="12" :total="total" layout="prev, pager, next" class="pagination" />

    <!-- 对比栏 -->
    <div v-if="compareSet.size > 0" style="position:fixed;bottom:20px;left:50%;transform:translateX(-50%);background:#fff;padding:12px 24px;border-radius:8px;box-shadow:0 4px 20px rgba(0,0,0,0.15);z-index:100;display:flex;align-items:center;gap:16px">
      <span>已选 {{ compareSet.size }}/4 款</span>
      <el-button type="primary" size="small" @click="showCompare = true" :disabled="compareSet.size < 2">开始对比</el-button>
      <el-button size="small" @click="compareSet = new Set()">取消</el-button>
    </div>

    <!-- 对比弹窗 -->
    <el-dialog v-model="showCompare" title="产品对比" width="90%">
      <el-table :data="compareProducts" stripe>
        <el-table-column label="图片" width="100"><template #default="{ row }"><img :src="getImageUrl(row.imgPath)" style="width:80px;height:50px;object-fit:cover;border-radius:4px" /></template></el-table-column>
        <el-table-column prop="productName" label="产品名" width="160" />
        <el-table-column label="年化利率"><template #default="{ row }">{{ row.rate }}%</template></el-table-column>
        <el-table-column label="起购金额"><template #default="{ row }">¥{{ row.minAmount || 0 }}</template></el-table-column>
        <el-table-column label="期限"><template #default="{ row }">{{ row.term || '-' }}</template></el-table-column>
        <el-table-column label="风险等级"><template #default="{ row }">{{ {1:'低',2:'中',3:'高'}[row.riskLevel] || '-' }}</template></el-table-column>
        <el-table-column label="手续费"><template #default="{ row }">{{ row.feeDesc || '免手续费' }}</template></el-table-column>
      </el-table>
    </el-dialog>

    <!-- 理财购买弹窗 -->
    <el-dialog v-model="wealthDlg" title="购买理财产品" width="400px">
      <div v-if="wealthProd" style="margin-bottom:12px"><b>{{wealthProd.productName}}</b> | 年化 {{wealthProd.rate}}% | 最低 ¥{{wealthProd.minAmount||1000}}</div>
      <el-form label-width="80px" size="small">
        <el-form-item label="付款卡"><el-select v-model="wealthCard" style="width:100%" placeholder="选择付款卡"><el-option v-for="c in wealthCards" :key="c.id" :value="c.id" :label="'****'+c.cardNumber.slice(-4)+' (¥'+c.balance+')'"/></el-select></el-form-item>
        <el-form-item label="金额"><el-input v-model="wealthAmount" type="number"/></el-form-item>
      </el-form>
      <div style="font-size:12px;color:#f56c6c;margin-top:8px">⚠ 理财非存款，产品有风险，投资需谨慎</div>
      <template #footer><el-button @click="wealthDlg=false">取消</el-button><el-button type="primary" @click="doBuyWealth">确认购买(积分+20)</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.search-bar { margin-bottom: 16px; display: flex; }
.img-box { width: 100%; height: 180px; background: #f5f7fa; border-radius: 6px; margin-bottom: 8px; overflow: hidden; }
.product-img { width: 100%; height: 100%; object-fit: cover; }
</style>
