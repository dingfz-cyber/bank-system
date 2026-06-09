<script setup>
import { ref, onMounted } from 'vue'
import {
  getProductList, addProduct, updateProduct, deleteProduct,
  getRecycleList, recoverProduct, wipeProduct
} from '@/api/product'
import { getBannerList, addBanner, updateBanner, deleteBanner } from '@/api/banner'
import { getApplyList, getApplyRecycleList, recoverApply, wipeApply } from '@/api/apply'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IMAGE_BASE_URL } from '@/utils/constants'

const activeTab = ref('product')
const imageBaseUrl = IMAGE_BASE_URL

// ========== 产品管理 ==========
const productPage = ref(1)
const productTotal = ref(0)
const products = ref([])
const productDialog = ref(false)
const editingProduct = ref({ productName: '', productType: 1, rate: 0, intro: '', imgPath: '', sort: 0 })

function resetProductForm() {
  editingProduct.value = { productName: '', productType: 1, rate: 0, intro: '', imgPath: '', sort: 0 }
}

async function fetchProducts() {
  const res = await getProductList({ page: productPage.value, pageSize: 10 })
  if (res.code === 200) {
    products.value = res.data.list
    productTotal.value = res.data.total
  }
}

function openAddProduct() {
  resetProductForm()
  productDialog.value = true
}

function openEditProduct(p) {
  editingProduct.value = { ...p }
  productDialog.value = true
}

async function saveProduct() {
  if (editingProduct.value.id) {
    await updateProduct(editingProduct.value)
    ElMessage.success('修改成功')
  } else {
    await addProduct(editingProduct.value)
    ElMessage.success('新增成功')
  }
  productDialog.value = false
  fetchProducts()
}

async function handleDeleteProduct(id) {
  await ElMessageBox.confirm('确定删除该产品？', '提示')
  await deleteProduct(id)
  ElMessage.success('已删除')
  fetchProducts()
}

// ========== 回收站（产品） ==========
const recyclePage = ref(1)
const recycleTotal = ref(0)
const recycleList = ref([])

async function fetchRecycle() {
  const res = await getRecycleList({ page: recyclePage.value, pageSize: 10 })
  if (res.code === 200) {
    recycleList.value = res.data.list
    recycleTotal.value = res.data.total
  }
}

async function handleRecover(id) {
  await recoverProduct(id)
  ElMessage.success('已恢复')
  fetchRecycle()
  fetchProducts()
}

async function handleWipe(id) {
  await ElMessageBox.confirm('确定彻底删除？此操作不可恢复！', '警告', { confirmButtonClass: 'el-button--danger' })
  await wipeProduct(id)
  ElMessage.success('已彻底删除')
  fetchRecycle()
}

// ========== 轮播管理 ==========
const banners = ref([])
const bannerDialog = ref(false)
const editingBanner = ref({ imgPath: '', jumpRoute: '', sort: 0 })

async function fetchBanners() {
  const res = await getBannerList()
  if (res.code === 200) banners.value = res.data
}

function resetBannerForm() {
  editingBanner.value = { imgPath: '', jumpRoute: '', sort: 0 }
}

function openAddBanner() {
  resetBannerForm()
  bannerDialog.value = true
}

function openEditBanner(b) {
  editingBanner.value = { ...b }
  bannerDialog.value = true
}

async function saveBanner() {
  if (editingBanner.value.id) {
    await updateBanner(editingBanner.value)
    ElMessage.success('修改成功')
  } else {
    await addBanner(editingBanner.value)
    ElMessage.success('新增成功')
  }
  bannerDialog.value = false
  fetchBanners()
}

async function handleDeleteBanner(id) {
  await ElMessageBox.confirm('确定删除该轮播？', '提示')
  await deleteBanner(id)
  ElMessage.success('已删除')
  fetchBanners()
}

// ========== 申请管理 ==========
const applyPage = ref(1)
const applyTotal = ref(0)
const applyList = ref([])

async function fetchApplies() {
  const res = await getApplyList({ page: applyPage.value, pageSize: 10 })
  if (res.code === 200) {
    applyList.value = res.data.list
    applyTotal.value = res.data.total
  }
}

// ========== 申请回收站 ==========
const applyRecyclePage = ref(1)
const applyRecycleTotal = ref(0)
const applyRecycleList = ref([])

async function fetchApplyRecycle() {
  const res = await getApplyRecycleList({ page: applyRecyclePage.value, pageSize: 10 })
  if (res.code === 200) {
    applyRecycleList.value = res.data.list
    applyRecycleTotal.value = res.data.total
  }
}

async function handleRecoverApply(id) {
  await recoverApply(id)
  ElMessage.success('已恢复')
  fetchApplyRecycle()
  fetchApplies()
}

async function handleWipeApply(id) {
  await ElMessageBox.confirm('确定彻底删除？此操作不可恢复！', '警告', { confirmButtonClass: 'el-button--danger' })
  await wipeApply(id)
  ElMessage.success('已彻底删除')
  fetchApplyRecycle()
}

// ========== Tab 切换 ==========
function onTabChange(tab) {
  if (tab === 'product') fetchProducts()
  else if (tab === 'banner') fetchBanners()
  else if (tab === 'apply') fetchApplies()
  else if (tab === 'recycle') { fetchRecycle(); fetchApplyRecycle() }
}

onMounted(() => {
  fetchProducts()
  fetchBanners()
})
</script>

<template>
  <div class="admin-page">
    <h2 class="page-title">后台管理</h2>
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <!-- 产品管理 -->
      <el-tab-pane label="产品管理" name="product">
        <div class="toolbar">
          <el-button type="primary" @click="openAddProduct">新增产品</el-button>
        </div>
        <el-table :data="products" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="productName" label="产品名" />
          <el-table-column prop="productType" label="分类" width="100">
            <template #default="{ row }">{{ ['','个人业务','信用卡','公司金融','普惠金融'][row.productType] }}</template>
          </el-table-column>
          <el-table-column prop="rate" label="利率" width="100">
            <template #default="{ row }">{{ row.rate }}%</template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" width="60" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="openEditProduct(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteProduct(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-model:current-page="productPage" :total="productTotal" :page-size="10" layout="prev,pager,next" class="pagination" />
      </el-tab-pane>

      <!-- 轮播管理 -->
      <el-tab-pane label="轮播管理" name="banner">
        <div class="toolbar">
          <el-button type="primary" @click="openAddBanner">新增轮播</el-button>
        </div>
        <el-table :data="banners" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="图片" width="120">
            <template #default="{ row }">
              <el-image :src="`${imageBaseUrl}${row.imgPath}`" style="width:80px;height:40px" fit="cover">
                <template #error><div class="img-placeholder">暂无</div></template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column prop="jumpRoute" label="跳转路由" />
          <el-table-column prop="sort" label="排序" width="60" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="openEditBanner(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteBanner(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 申请记录 -->
      <el-tab-pane label="申请记录" name="apply">
        <el-table :data="applyList" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="realName" label="姓名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="applyType" label="类型" width="80">
            <template #default="{ row }">{{ row.applyType === 1 ? '办卡' : '贷款' }}</template>
          </el-table-column>
          <el-table-column prop="createTime" label="申请时间" width="180" />
        </el-table>
        <el-pagination v-model:current-page="applyPage" :total="applyTotal" :page-size="10" layout="prev,pager,next" class="pagination" />
      </el-tab-pane>

      <!-- 回收站 -->
      <el-tab-pane label="回收站" name="recycle">
        <el-tabs type="border-card">
          <el-tab-pane label="已删产品">
            <el-table :data="recycleList" stripe>
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column prop="productName" label="产品名" />
              <el-table-column prop="updateTime" label="删除时间" width="180" />
              <el-table-column label="操作" width="200">
                <template #default="{ row }">
                  <el-button size="small" type="success" @click="handleRecover(row.id)">恢复</el-button>
                  <el-button size="small" type="danger" @click="handleWipe(row.id)">彻底删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination v-model:current-page="recyclePage" :total="recycleTotal" :page-size="10" layout="prev,pager,next" class="pagination" />
          </el-tab-pane>
          <el-tab-pane label="已删申请">
            <el-table :data="applyRecycleList" stripe>
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column prop="realName" label="姓名" />
              <el-table-column prop="phone" label="手机号" />
              <el-table-column prop="updateTime" label="删除时间" width="180" />
              <el-table-column label="操作" width="200">
                <template #default="{ row }">
                  <el-button size="small" type="success" @click="handleRecoverApply(row.id)">恢复</el-button>
                  <el-button size="small" type="danger" @click="handleWipeApply(row.id)">彻底删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination v-model:current-page="applyRecyclePage" :total="applyRecycleTotal" :page-size="10" layout="prev,pager,next" class="pagination" />
          </el-tab-pane>
        </el-tabs>
      </el-tab-pane>
    </el-tabs>

    <!-- 产品编辑弹窗 -->
    <el-dialog v-model="productDialog" :title="editingProduct.id ? '编辑产品' : '新增产品'" width="500px">
      <el-form :model="editingProduct" label-width="100px">
        <el-form-item label="产品名">
          <el-input v-model="editingProduct.productName" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editingProduct.productType">
            <el-option :value="1" label="个人业务" />
            <el-option :value="2" label="信用卡" />
            <el-option :value="3" label="公司金融" />
            <el-option :value="4" label="普惠金融" />
          </el-select>
        </el-form-item>
        <el-form-item label="年化利率">
          <el-input v-model.number="editingProduct.rate" type="number" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model.number="editingProduct.sort" type="number" />
        </el-form-item>
        <el-form-item label="图片路径">
          <el-input v-model="editingProduct.imgPath" placeholder="如 card/card01.png" />
        </el-form-item>
        <el-form-item label="产品介绍">
          <el-input v-model="editingProduct.intro" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>

    <!-- 轮播编辑弹窗 -->
    <el-dialog v-model="bannerDialog" :title="editingBanner.id ? '编辑轮播' : '新增轮播'" width="500px">
      <el-form :model="editingBanner" label-width="100px">
        <el-form-item label="图片路径">
          <el-input v-model="editingBanner.imgPath" placeholder="如 banner/banner01.png" />
        </el-form-item>
        <el-form-item label="跳转路由">
          <el-input v-model="editingBanner.jumpRoute" placeholder="如 /personal" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model.number="editingBanner.sort" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bannerDialog = false">取消</el-button>
        <el-button type="primary" @click="saveBanner">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.admin-page {
  padding: 20px 40px;
}
.page-title {
  margin: 0 0 20px;
}
.toolbar {
  margin-bottom: 16px;
}
</style>
