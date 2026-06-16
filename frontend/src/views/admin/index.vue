<script setup>
import { ref, onMounted, computed } from 'vue'
import {
  getProductList, addProduct, updateProduct, deleteProduct,
  getRecycleList, recoverProduct, wipeProduct
} from '@/api/product'
import { getAuditLog, getMonitor } from '@/api/log'
import request from '@/api/request'
import { getAdminGoods, addGoods, updateGoods, deleteGoods } from '@/api/points'
import { listUsers, lockUser, unlockUser, adminResetPassword } from '@/api/user'
import { getStats } from '@/api/dashboard'
import { getBannerList, addBanner, updateBanner, deleteBanner } from '@/api/banner'
import { getAdminNewsList, addNews, updateNews, deleteNews } from '@/api/news'
import { getApplyList, getApplyRecycleList, recoverApply, wipeApply, updateApply, deleteApply, approveApply } from '@/api/apply'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { IMAGE_BASE_URL, APPLY_STATUS_MAP } from '@/utils/constants'

const userStore = useUserStore()
const defTabs = { 1:'sysconfig', 2:'audit', 3:'dashboard', 4:'dashboard' }
const activeTab = ref(defTabs[userStore.roleId] || 'dashboard')
const imageBaseUrl = IMAGE_BASE_URL

// ========== 数据概览 ==========
const stats = ref({})

const lastRefresh = ref('')

async function fetchStats() {
  const res = await getStats()
  if (res.code === 200) { stats.value = res.data; lastRefresh.value = new Date().toLocaleTimeString() }
}

// ========== 用户管理 ==========
const userList = ref([])

async function fetchUsers() {
  const res = await listUsers()
  if (res.code === 200) userList.value = res.data
}

async function handleLockUser(id) {
  await ElMessageBox.confirm('确定锁定该用户？', '提示')
  await lockUser(id)
  ElMessage.success('已锁定')
  fetchUsers()
}

async function handleUnlockUser(id) {
  await unlockUser(id)
  ElMessage.success('已解锁')
  fetchUsers()
}

async function handleResetPwd(id) {
  await ElMessageBox.confirm('重置密码为 123456？', '提示')
  await adminResetPassword(id)
  ElMessage.success('密码已重置为123456')
}

// ========== 产品管理 ==========
const productLoading = ref(false)
const productPage = ref(1)
const productTotal = ref(0)
const products = ref([])
const productDialog = ref(false)
const editingProduct = ref({ productName: '', productType: 1, rate: 0, minAmount: 0, term: '', riskLevel: 1, feeDesc: '', productStatus: 0, intro: '', imgPath: '', sort: 0 })

function resetProductForm() {
  editingProduct.value = { productName: '', productType: 1, rate: 0, minAmount: 0, term: '', riskLevel: 1, feeDesc: '', productStatus: 0, intro: '', imgPath: '', sort: 0 }
}

async function fetchProducts() {
  productLoading.value = true
  try {
    const res = await getProductList({ page: productPage.value, pageSize: 10 })
    if (res.code === 200) {
      products.value = res.data.list
      productTotal.value = res.data.total
    }
  } finally {
    productLoading.value = false
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

async function handleToggleProduct(p) {
  const action = p.productStatus === 0 ? '停售' : '上架'
  await ElMessageBox.confirm(`确定${action}该产品？`, '提示')
  await updateProduct({ ...p, productStatus: p.productStatus === 0 ? 1 : 0 })
  ElMessage.success(`已${action}`)
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
const applyLoading = ref(false)
const applyPage = ref(1)
const applyTotal = ref(0)
const applyList = ref([])

async function fetchApplies() {
  applyLoading.value = true
  try {
    const res = await getApplyList({ page: applyPage.value, pageSize: 10 })
    if (res.code === 200) { applyList.value = res.data.list; applyTotal.value = res.data.total }
  } finally { applyLoading.value = false }
}

const applyDialog = ref(false)
const editingApply = ref({ id: null, productId: null, realName: '', phone: '', applyType: 1 })

function resetApplyForm() {
  editingApply.value = { id: null, productId: null, realName: '', phone: '', applyType: 1 }
}

function openEditApply(row) {
  editingApply.value = { ...row }
  applyDialog.value = true
}

async function saveApply() {
  await updateApply(editingApply.value.id, editingApply.value)
  ElMessage.success('修改成功')
  applyDialog.value = false
  fetchApplies()
}

async function handleDeleteApply(id) {
  await ElMessageBox.confirm('确定删除该申请记录？', '提示')
  await deleteApply(id)
  ElMessage.success('已删除')
  fetchApplies()
}

const approveDialog = ref(false)
const approvingApplyId = ref(null)
const approvingStatus = ref(1)
const approvingRemark = ref('')

function openApproveDialog(row, status) {
  approvingApplyId.value = row.id
  approvingStatus.value = status
  approvingRemark.value = ''
  approveDialog.value = true
}

async function saveApprove() {
  const params = { status: approvingStatus.value }
  if (approvingRemark.value) params.remark = approvingRemark.value
  await approveApply(approvingApplyId.value, params)
  ElMessage.success(approvingStatus.value === 1 ? '已通过' : '已拒绝')
  approveDialog.value = false
  fetchApplies()
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

// ========== 新闻管理 ==========
const newsLoading = ref(false)
const newsPage = ref(1)
const newsTotal = ref(0)
const newsData = ref([])
const newsDialog = ref(false)
const editingNews = ref({ title: '', summary: '', content: '', category: '系统公告', status: 1, sort: 0 })

function resetNewsForm() {
  editingNews.value = { title: '', summary: '', content: '', category: '系统公告', status: 1, sort: 0 }
}

async function fetchNews() {
  newsLoading.value = true
  try {
    const res = await getAdminNewsList({ page: newsPage.value, pageSize: 10 })
    if (res.code === 200) { newsData.value = res.data.list; newsTotal.value = res.data.total }
  } finally { newsLoading.value = false }
}

function openAddNews() {
  resetNewsForm()
  newsDialog.value = true
}

function openEditNews(row) {
  editingNews.value = { ...row }
  newsDialog.value = true
}

async function saveNews() {
  if (editingNews.value.id) {
    await updateNews(editingNews.value)
    ElMessage.success('修改成功')
  } else {
    await addNews(editingNews.value)
    ElMessage.success('新增成功')
  }
  newsDialog.value = false
  fetchNews()
}

async function handleDeleteNews(id) {
  await ElMessageBox.confirm('确定删除该新闻？', '提示')
  await deleteNews(id)
  ElMessage.success('已删除')
  fetchNews()
}

// ========== 积分商城管理 ==========
const pointGoods = ref([]); const pointForm=ref({name:'',cost:100,image:'',stock:50}); const pointDlg=ref(false)
const familyApps=ref([])
async function fetchFamilyApps(){const r=await request.get('/family/admin/pending');if(r.code===200)familyApps.value=r.data}
async function handleApproveFamily(fid,status){await request.post('/family/admin/approve/'+fid+'?status='+status);ElMessage.success(status===1?'已通过':'已驳回');fetchFamilyApps()}

// 数据库备份恢复
const backups=ref([]); const backupLoading=ref(false); const showRestore=ref(false); const restoreFile=ref('')
async function fetchBackups(){const r=await request.get('/db/backups');if(r.code===200)backups.value=r.data}
async function doBackup(){backupLoading.value=true;const r=await request.post('/db/backup');backupLoading.value=false;if(r.code===200){ElMessage.success('备份成功: '+r.data); fetchBackups()}else ElMessage.error(r.msg)}
async function doRestore(name){await ElMessageBox.confirm('确定恢复 '+name+'？当前数据将被覆盖！','危险操作',{type:'error',confirmButtonClass:'el-button--danger'});const r=await request.post('/db/restore',{name});if(r.code===200)ElMessage.success('恢复成功')}

const sysInfo=ref({})
const memPercent=computed(()=>{const u=(sysInfo.value.usedMemory||'').toString();const t=(sysInfo.value.totalMemory||'').toString();if(!u||!t)return 0;return Math.round(parseInt(u)/parseInt(t)*100)})
async function fetchSysMonitor(){const r=await request.get('/system/monitor');if(r.code===200)sysInfo.value=r.data}
async function fetchPointGoods(){ const r=await getAdminGoods(); if(r.code===200) pointGoods.value=r.data }
function openAddPoint(){ pointForm.value={name:'',cost:100,image:'',stock:50}; pointDlg.value=true }
function openEditPoint(g){ pointForm.value={...g}; pointDlg.value=true }
async function savePoint(){ if(pointForm.value.id){ await updateGoods(pointForm.value.id,pointForm.value) }else{ await addGoods(pointForm.value) }; pointDlg.value=false; fetchPointGoods(); ElMessage.success('已保存') }
async function handleDeletePoint(id){ await ElMessageBox.confirm('确定删除?'); await deleteGoods(id); fetchPointGoods(); ElMessage.success('已删除') }

// ========== Tab 切换 ==========
const auditLogs = ref([])
const monitorData = ref({ loginRecords: [], totalLogins: 0 })
async function fetchLogMonitor() {
  const res = await getMonitor(); if (res.code === 200) monitorData.value = res.data
}
async function fetchAuditLog() {
  const res = await getAuditLog(200)
  if (res.code === 200) auditLogs.value = res.data
}

function onTabChange(tab) {
  localStorage.setItem('adminTab', tab)
  if (tab === 'dashboard') fetchStats()
  else if (tab === 'audit') fetchAuditLog()
  else if (tab === 'opsMonitor') fetchSysMonitor()
  else if (tab === 'users') fetchUsers()
  else if (tab === 'product') fetchProducts()
  else if (tab === 'banner') fetchBanners()
  else if (tab === 'apply') fetchApplies()
  else if (tab === 'news') fetchNews()
  else if (tab === 'points') fetchPointGoods()
  else if (tab === 'familyApproval') fetchFamilyApps()
  else if (tab === 'dbmanage') fetchBackups()
  else if (tab === 'opsMonitor') fetchSysMonitor()
  else if (tab === 'recycle') { if (userStore.roleId === 3) fetchRecycle(); if (userStore.roleId === 4) fetchApplyRecycle() }
}

onMounted(() => {
  const tab = activeTab.value
  if (tab === 'dashboard') fetchStats()
  else if (tab === 'audit') fetchAuditLog()
  else if (tab === 'opsMonitor') fetchSysMonitor()
  else if (tab === 'users') fetchUsers()
  else if (tab === 'product') fetchProducts()
  else if (tab === 'banner') fetchBanners()
  else if (tab === 'apply') fetchApplies()
  else if (tab === 'news') fetchNews()
  else if (tab === 'points') fetchPointGoods()
  else if (tab === 'familyApproval') fetchFamilyApps()
  else if (tab === 'dbmanage') fetchBackups()
  else if (tab === 'opsMonitor') fetchSysMonitor()
  else if (tab === 'recycle') { if (userStore.roleId === 3) fetchRecycle(); if (userStore.roleId === 4) fetchApplyRecycle() }
  lastRefresh.value = new Date().toLocaleTimeString()
})
</script>

<template>
  <div class="admin-page">
    <h2 class="page-title">后台管理</h2>
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <!-- 数据概览 -->
      <el-tab-pane v-if="[1,3,4].includes(userStore.roleId)" label="数据概览" name="dashboard">
        <el-alert :title="'欢迎回来，' + userStore.nickName + ' — ' + ({1:'系统运维管理员',3:'业务配置管理员',4:'普通运营管理员'}[userStore.roleId]||'管理员') + '  |  ' + new Date().toLocaleDateString()" type="success" :closable="false" style="margin-bottom:12px" />
        <div style="display:flex;justify-content:flex-end;margin-bottom:12px">
          <el-button size="small" @click="fetchStats">🔄 刷新数据</el-button>
          <span v-if="lastRefresh" style="font-size:12px;color:#999;margin-left:8px">上次刷新: {{ lastRefresh }}</span>
        </div>

        <!-- 核心指标 -->
        <div class="dash-section-title">核心指标</div>
        <el-row :gutter="16">
          <el-col :span="6" v-for="(s,i) in [
            {v:stats.userCount||0,l:'用户总数',c:'#409eff',icon:'👥'},{v:stats.productCount||0,l:'产品总数',c:'#67c23a',icon:'📦'},
            {v:stats.todayApplyCount||0,l:'今日申请',c:'#e6a23c',icon:'📋'},{v:stats.pendingCount||0,l:'待审核',c:'#f56c6c',icon:'⏳'}
          ]" :key="i">
            <div class="dash-card" :style="{borderTopColor:s.c}"><div class="dash-card-num" :style="{color:s.c}">{{s.v}}</div><div class="dash-card-icon">{{s.icon}}</div><div class="dash-card-label">{{s.l}}</div></div>
          </el-col>
        </el-row>

        <!-- 申请统计 -->
        <div class="dash-section-title" style="margin-top:16px">申请统计</div>
        <el-row :gutter="16">
          <el-col :span="6" v-for="(s,i) in [
            {v:stats.approvedCount||0,l:'已通过',c:'#67c23a'},{v:stats.rejectedCount||0,l:'已拒绝',c:'#f56c6c'},
            {v:((stats.approvedCount+stats.rejectedCount)>0?Math.round(stats.approvedCount/(stats.approvedCount+stats.rejectedCount)*100):0)+'%',l:'通过率',c:'#409eff'},{v:stats.newsCount||0,l:'新闻公告',c:'#909399'}
          ]" :key="i">
            <div class="dash-card dash-card-sm" :style="{borderTopColor:s.c}"><div class="dash-card-num-sm" :style="{color:s.c}">{{s.v}}</div><div class="dash-card-label">{{s.l}}</div></div>
          </el-col>
        </el-row>

        <!-- 产品分布 -->
        <div class="dash-section-title" style="margin-top:16px">产品分布</div>
        <el-row :gutter="16">
          <el-col :span="6" v-for="(s,i) in [
            {v:stats.productType1Count||0,l:'个人业务',c:'#67c23a'},{v:stats.productType2Count||0,l:'银行卡',c:'#409eff'},
            {v:stats.productType3Count||0,l:'公司金融',c:'#e6a23c'},{v:stats.productType4Count||0,l:'普惠金融',c:'#f56c6c'}
          ]" :key="i">
            <div class="dash-card dash-card-sm" :style="{borderTopColor:s.c}"><div class="dash-card-num-sm" :style="{color:s.c}">{{s.v}}</div><div class="dash-card-label">{{s.l}}</div></div>
          </el-col>
        </el-row>

        <!-- 系统概况 -->
        <div class="dash-section-title" style="margin-top:16px">系统概况</div>
        <el-row :gutter="16">
          <el-col :span="6" v-for="(s,i) in [
            {v:stats.cardCount||0,l:'银行卡',c:'#409eff'},{v:stats.transactionCount||0,l:'交易记录',c:'#67c23a'}
          ]" :key="i">
            <div class="dash-card dash-card-sm" :style="{borderTopColor:s.c}"><div class="dash-card-num-sm" :style="{color:s.c}">{{s.v}}</div><div class="dash-card-label">{{s.l}}</div></div>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 用户管理（运维/运营可见） -->
      <el-tab-pane v-if="[1,4].includes(userStore.roleId)" label="用户管理" name="users">
        <el-table :data="userList" stripe>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="nickName" label="昵称" />
          <el-table-column label="角色" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ {1:'运维',2:'审计',3:'业务',4:'运营',5:'用户'}[row.roleId] || row.roleId }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag v-if="row.lockedAt && new Date(row.lockedAt) > new Date()" type="danger" size="small">已锁定</el-tag>
              <el-tag v-else type="success" size="small">正常</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220">
            <template #default="{ row }">
              <div style="display:flex;gap:4px;flex-wrap:wrap">
                <template v-if="row.lockedAt && new Date(row.lockedAt) > new Date()">
                  <el-button size="small" type="success" @click="handleUnlockUser(row.id)">解锁</el-button>
                </template>
                <template v-else>
                  <el-button size="small" type="danger" @click="handleLockUser(row.id)">锁定</el-button>
                </template>
                <el-button size="small" @click="handleResetPwd(row.id)">重置密码</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 系统运维（仅运维管理员可见） -->
      <el-tab-pane v-if="userStore.roleId === 1" label="系统配置" name="sysconfig">
        <el-card><template #header>系统基础配置</template>
          <el-form label-width="120px" size="small">
            <el-form-item label="系统域名"><el-input value="http://localhost:5173" disabled /></el-form-item>
            <el-form-item label="日志路径"><el-input value="backend/logs/bank-system.log" disabled /></el-form-item>
            <el-form-item label="后端端口"><el-input value="8080" disabled /></el-form-item>
            <el-form-item label="数据库端口"><el-input value="3307 (MySQL)" disabled /></el-form-item>
            <el-form-item label="JWT 有效期"><el-input value="24 小时" disabled /></el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane v-if="userStore.roleId === 1" label="系统监控" name="opsMonitor">
        <div class="dash-section-title">系统状态概览</div>
        <el-row :gutter="16">
          <el-col :span="8"><div class="dash-card"><div class="dash-card-icon">☕</div><div class="dash-card-num" style="color:#409eff">{{sysInfo.javaVersion||'-'}}</div><div class="dash-card-label">Java 版本</div></div></el-col>
          <el-col :span="8"><div class="dash-card"><div class="dash-card-icon">⏱</div><div class="dash-card-num" style="color:#67c23a">{{sysInfo.uptime||'-'}}</div><div class="dash-card-label">运行时长</div></div></el-col>
          <el-col :span="8"><div class="dash-card"><div class="dash-card-icon">💻</div><div class="dash-card-num" style="color:#e6a23c">{{sysInfo.processors||'-'}}</div><div class="dash-card-label">CPU 核心数</div></div></el-col>
        </el-row>

        <div class="dash-section-title" style="margin-top:16px">内存使用</div>
        <el-row :gutter="16">
          <el-col :span="8"><div class="dash-card"><div class="dash-card-num-sm" style="color:#409eff">{{sysInfo.totalMemory||'-'}}</div><div class="dash-card-label">总内存</div></div></el-col>
          <el-col :span="8"><div class="dash-card"><div class="dash-card-num-sm" style="color:#e6a23c">{{sysInfo.usedMemory||'-'}}</div><div class="dash-card-label">已用内存</div></div></el-col>
          <el-col :span="8"><div class="dash-card"><div class="dash-card-num-sm" style="color:#67c23a">{{sysInfo.freeMemory||'-'}}</div><div class="dash-card-label">空闲内存</div></div></el-col>
        </el-row>
        <el-progress :percentage="memPercent" :color="memPercent>80?'#f56c6c':'#409eff'" :stroke-width="16" style="margin-top:12px"/>

        <div class="dash-section-title" style="margin-top:16px">数据库信息</div>
        <el-row :gutter="16">
          <el-col :span="8"><div class="dash-card"><div class="dash-card-icon">🗄</div><div class="dash-card-num-sm" style="color:#409eff">{{sysInfo.dbVersion||'-'}}</div><div class="dash-card-label">MySQL 版本</div></div></el-col>
          <el-col :span="8"><div class="dash-card"><div class="dash-card-icon">📊</div><div class="dash-card-num-sm" style="color:#67c23a">{{sysInfo.tableCount||'-'}}</div><div class="dash-card-label">数据表数量</div></div></el-col>
          <el-col :span="8"><div class="dash-card"><div class="dash-card-icon">📝</div><div class="dash-card-num-sm" style="color:#e6a23c">{{sysInfo.totalRows||'-'}}</div><div class="dash-card-label">总数据行数</div></div></el-col>
        </el-row>
        <div style="display:flex;justify-content:flex-end;margin-top:12px">
          <span style="font-size:12px;color:#999">当前时间: {{sysInfo.currentTime||'-'}}</span>
          <el-button size="small" @click="fetchSysMonitor" style="margin-left:12px">🔄 刷新</el-button>
        </div>
      </el-tab-pane>

      <el-tab-pane v-if="userStore.roleId === 1" label="数据库管理" name="dbmanage">
        <el-card><template #header>数据库备份与恢复</template>
          <el-button type="primary" @click="doBackup" :loading="backupLoading">🔄 手动备份</el-button>
          <el-button type="danger" style="margin-left:8px" @click="showRestore=true">📂 数据恢复</el-button>
          <el-divider/>
          <div style="font-size:13px;color:#909399;margin-bottom:8px">备份文件列表 <el-button size="small" text @click="fetchBackups">刷新</el-button></div>
          <el-table :data="backups" stripe size="small">
            <el-table-column prop="name" label="文件名"/><el-table-column prop="size" label="大小" width="100"/>
            <el-table-column label="操作" width="100"><template #default="{row}"><el-button size="small" type="danger" @click="doRestore(row.name)">恢复</el-button></template></el-table-column>
          </el-table>
          <el-empty v-if="!backups.length" description="暂无备份" :image-size="60"/>
        </el-card>
        <!-- 恢复确认弹窗 -->
        <el-dialog v-model="showRestore" title="选择备份文件恢复" width="400px">
          <el-radio-group v-model="restoreFile"><el-radio v-for="b in backups" :key="b.name" :value="b.name">{{b.name}} ({{b.size}})</el-radio></el-radio-group>
          <div v-if="!backups.length" style="color:#999;text-align:center">暂无备份文件</div>
          <template #footer><el-button @click="showRestore=false">取消</el-button><el-button type="danger" @click="doRestore(restoreFile);showRestore=false" :disabled="!restoreFile">确认恢复</el-button></template>
        </el-dialog>
      </el-tab-pane>

      <el-tab-pane v-if="userStore.roleId === 1" label="运维日志" name="opslog">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
          <span style="color:#909399;font-size:13px">系统运维日志 — 服务器状态与操作记录</span>
          <el-button size="small" @click="fetchAuditLog">🔄 刷新</el-button>
        </div>
        <el-table :data="auditLogs.slice(0, 50)" stripe size="small" max-height="400">
          <el-table-column prop="time" label="时间" width="220" />
          <el-table-column prop="level" label="级别" width="70">
            <template #default="{ row }"><el-tag :type="row.level==='ERROR'?'danger':row.level==='WARN'?'warning':'info'" size="small">{{ row.level }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
        </el-table>
        <div style="font-size:12px;color:#999;margin-top:8px">
          <span>🟢 服务器状态：运行中 | Java: {{ sysInfo.javaVersion||'21' }} | 运行: {{ sysInfo.uptime||'--' }} | 内存: {{ sysInfo.usedMemory||'--' }}/{{ sysInfo.totalMemory||'--' }}</span>
        </div>
      </el-tab-pane>

      <!-- 日志审计（审计员可见） -->
      <el-tab-pane v-if="userStore.roleId === 2" label="日志审计" name="audit">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px">
          <span style="color:#909399;font-size:13px">全量操作日志（最近 200 条）— 仅查看，不可修改或删除</span>
          <el-button size="small" @click="fetchAuditLog">🔄 刷新</el-button>
        </div>
        <el-table :data="auditLogs" stripe v-loading="!auditLogs.length" size="small" max-height="500">
          <el-table-column prop="time" label="时间" width="220" />
          <el-table-column prop="level" label="级别" width="70">
            <template #default="{ row }"><el-tag :type="row.level==='ERROR'?'danger':row.level==='WARN'?'warning':'info'" size="small">{{ row.level }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="content" label="内容" min-width="400" show-overflow-tooltip />
        </el-table>
        <div style="font-size:12px;color:#999;margin-top:8px">日志来源: backend/logs/bank-system.log</div>
      </el-tab-pane>

      <!-- 操作监控（审计员独有） -->
      <el-tab-pane v-if="userStore.roleId === 2" label="操作监控" name="auditMonitor">
        <div style="display:flex;justify-content:space-between;margin-bottom:12px">
          <span style="color:#909399;font-size:13px">全量登录记录：共 {{ monitorData.totalLogins || 0 }} 次</span>
          <el-button size="small" @click="fetchLogMonitor">🔄 刷新</el-button>
        </div>
        <el-table :data="monitorData.loginRecords || []" stripe size="small">
          <el-table-column label="用户ID" width="70" prop="userId" />
          <el-table-column label="手机号" width="130" prop="phone" />
          <el-table-column label="IP" width="120" prop="ip" />
          <el-table-column label="设备" prop="device" />
          <el-table-column label="登录时间" width="180" prop="loginTime" />
        </el-table>
        <div style="margin-top:16px;padding:12px;background:#fef0f0;border-radius:4px;font-size:12px;color:#f56c6c">
          ⚠ 监控说明：此面板仅供安全审计员查看。所有管理员账号登录行为均被记录，异常登录（异地/IP突变）需人工核查。
        </div>
      </el-tab-pane>

      <!-- 产品管理 -->
      <el-tab-pane v-if="[3].includes(userStore.roleId)" label="产品管理" name="product">
        <div class="toolbar">
          <el-button type="primary" @click="openAddProduct">新增产品</el-button>
        </div>
        <el-table :data="products" stripe v-loading="productLoading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="productName" label="产品名" />
          <el-table-column prop="productType" label="分类" width="100">
            <template #default="{ row }">{{ ['','个人业务','信用卡','公司金融','普惠金融'][row.productType] }}</template>
          </el-table-column>
          <el-table-column prop="rate" label="利率" width="100">
            <template #default="{ row }">{{ row.rate }}%</template>
          </el-table-column>
          <el-table-column label="风险" width="70">
            <template #default="{ row }">
              <el-tag :type="row.riskLevel===1?'success':row.riskLevel===3?'danger':'warning'" size="small">{{ {1:'低',2:'中',3:'高'}[row.riskLevel]||'-' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" width="60" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.productStatus === 0 ? 'success' : 'info'" size="small">
                {{ row.productStatus === 0 ? '正常' : '停售' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="260">
            <template #default="{ row }">
              <el-button size="small" @click="openEditProduct(row)">编辑</el-button>
              <el-button size="small" :type="row.productStatus===0?'warning':'success'" @click="handleToggleProduct(row)">
                {{ row.productStatus===0?'停售':'上架' }}</el-button>
              <el-button size="small" type="danger" @click="handleDeleteProduct(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-model:current-page="productPage" :total="productTotal" :page-size="10" layout="prev,pager,next" class="pagination" />
      </el-tab-pane>

      <!-- 轮播管理 -->
      <el-tab-pane v-if="[3].includes(userStore.roleId)" label="轮播管理" name="banner">
        <div class="toolbar">
          <el-button type="primary" @click="openAddBanner">新增轮播</el-button>
        </div>
        <el-table :data="banners" stripe v-loading="!banners.length">
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
      <el-tab-pane v-if="[4].includes(userStore.roleId)" label="家庭审批" name="familyApproval">
        <div style="display:flex;justify-content:flex-end;margin-bottom:8px"><el-button size="small" @click="fetchFamilyApps">刷新</el-button></div>
        <el-table :data="familyApps" stripe size="small"><el-table-column prop="id" label="ID" width="60"/><el-table-column prop="creator_id" label="申请人" width="80"/><el-table-column prop="address" label="地址"/><el-table-column prop="phone" label="电话" width="130"/><el-table-column label="操作" width="180"><template #default="{row}"><el-button size="small" type="success" @click="handleApproveFamily(row.id,1)">通过</el-button><el-button size="small" type="danger" @click="handleApproveFamily(row.id,2)">驳回</el-button></template></el-table-column></el-table>
      </el-tab-pane>

      <el-tab-pane v-if="[4].includes(userStore.roleId)" label="申请记录" name="apply">
        <el-table :data="applyList" stripe v-loading="applyLoading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="realName" label="姓名" />
          <el-table-column prop="phone" label="手机号" />
          <el-table-column prop="productId" label="产品ID" width="80" />
          <el-table-column prop="applyType" label="类型" width="80">
            <template #default="{ row }">{{ row.applyType === 1 ? '办卡' : '贷款' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'" size="small">
                {{ APPLY_STATUS_MAP[row.status] || '待审核' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="申请时间" width="180" />
          <el-table-column label="操作" width="320">
            <template #default="{ row }">
              <template v-if="row.status === 0">
                <el-button size="small" type="success" @click="openApproveDialog(row, 1)">通过</el-button>
                <el-button size="small" type="danger" @click="openApproveDialog(row, 2)">拒绝</el-button>
              </template>
              <el-button size="small" @click="openEditApply(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteApply(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-model:current-page="applyPage" :total="applyTotal" :page-size="10" layout="prev,pager,next" class="pagination" />
      </el-tab-pane>

      <!-- 回收站 -->
      <!-- 新闻管理 -->
      <el-tab-pane v-if="[3].includes(userStore.roleId)" label="新闻管理" name="news">
        <div class="toolbar">
          <el-button type="primary" @click="openAddNews">新增新闻</el-button>
        </div>
        <el-table :data="newsData" stripe v-loading="newsLoading">
          <template #empty><el-empty description="暂无新闻，点击上方新增" /></template>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="category" label="分类" width="90" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
                {{ row.status === 1 ? '已发布' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sort" label="排序" width="60" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="openEditNews(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDeleteNews(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-model:current-page="newsPage" :total="newsTotal" :page-size="10" layout="prev,pager,next" class="pagination" />
      </el-tab-pane>

            <!-- 积分商城管理（业务管理员可见） -->
      <el-tab-pane v-if="userStore.roleId===3" label="积分商城" name="points">
        <div class="toolbar"><el-button type="primary" @click="openAddPoint">新增商品</el-button></div>
        <el-table :data="pointGoods" stripe size="small">
          <el-table-column prop="id" label="ID" width="60"/><el-table-column prop="name" label="名称"/>
          <el-table-column prop="cost" label="积分" width="80"/><el-table-column prop="stock" label="库存" width="80"/>
          <el-table-column label="操作" width="160">
            <template #default="{row}"><el-button size="small" @click="openEditPoint(row)">编辑</el-button><el-button size="small" type="danger" @click="handleDeletePoint(row.id)">删除</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane v-if="[4].includes(userStore.roleId)" label="回收站" name="recycle">
        <el-tabs type="border-card">
          <el-tab-pane v-if="userStore.roleId===3" label="已删产品">
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
          <el-tab-pane v-if="userStore.roleId===4" label="已删申请">
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
        <el-form-item label="起购金额">
          <el-input v-model.number="editingProduct.minAmount" type="number" placeholder="0" />
        </el-form-item>
        <el-form-item label="期限">
          <el-input v-model="editingProduct.term" placeholder="如: 3个月/1年" />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="editingProduct.riskLevel">
            <el-option :value="1" label="低风险" />
            <el-option :value="2" label="中风险" />
            <el-option :value="3" label="高风险" />
          </el-select>
        </el-form-item>
        <el-form-item label="手续费">
          <el-input v-model="editingProduct.feeDesc" placeholder="如: 免手续费" />
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

    <!-- 申请编辑弹窗 -->
    <el-dialog v-model="applyDialog" title="编辑申请记录" width="500px">
      <el-form :model="editingApply" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="editingApply.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editingApply.phone" />
        </el-form-item>
        <el-form-item label="产品ID">
          <el-input v-model.number="editingApply.productId" type="number" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editingApply.applyType">
            <el-option :value="1" label="办卡" />
            <el-option :value="2" label="贷款" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialog = false">取消</el-button>
        <el-button type="primary" @click="saveApply">保存</el-button>
      </template>
    </el-dialog>

    <!-- 申请审批弹窗 -->
    <el-dialog v-model="approveDialog" :title="approvingStatus === 1 ? '确认通过' : '确认拒绝'" width="450px">
      <el-form label-width="80px">
        <el-form-item v-if="approvingStatus === 2" label="拒绝原因">
          <el-input v-model="approvingRemark" type="textarea" :rows="3" placeholder="请填写拒绝原因（选填）" />
        </el-form-item>
        <el-form-item v-else label="备注">
          <el-input v-model="approvingRemark" placeholder="审核备注（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialog = false">取消</el-button>
        <el-button :type="approvingStatus === 1 ? 'success' : 'danger'" @click="saveApprove">
          {{ approvingStatus === 1 ? '确认通过' : '确认拒绝' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 新闻编辑弹窗 -->
    <el-dialog v-model="newsDialog" :title="editingNews.id ? '编辑新闻' : '新增新闻'" width="700px">
      <el-form :model="editingNews" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editingNews.title" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="editingNews.summary" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="正文">
          <el-input v-model="editingNews.content" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editingNews.category">
            <el-option value="系统公告" label="系统公告" />
            <el-option value="产品动态" label="产品动态" />
            <el-option value="行业新闻" label="行业新闻" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editingNews.status">
            <el-option :value="1" label="已发布" />
            <el-option :value="0" label="草稿" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input v-model.number="editingNews.sort" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="newsDialog = false">取消</el-button>
        <el-button type="primary" @click="saveNews">保存</el-button>
      </template>
    </el-dialog>

    <!-- 积分商品编辑弹窗 -->
    <el-dialog v-model="pointDlg" :title="pointForm.id?'编辑商品':'新增商品'" width="400px">
      <el-form :model="pointForm" label-width="80px" size="small">
        <el-form-item label="名称"><el-input v-model="pointForm.name"/></el-form-item>
        <el-form-item label="积分"><el-input-number v-model="pointForm.cost" :min="1"/></el-form-item>
        <el-form-item label="图片"><el-input v-model="pointForm.image" placeholder="points/xxx.png"/></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="pointForm.stock" :min="0"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="pointDlg=false">取消</el-button><el-button type="primary" @click="savePoint">保存</el-button></template>
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
.dash-section-title { font-size:15px; font-weight:bold; color:#303133; margin-bottom:10px; padding-left:4px; border-left:3px solid #409eff; }
.dash-card { background:#fff; border-radius:8px; border-top:3px solid #ddd; padding:20px 16px 14px; position:relative; margin-bottom:12px; transition:box-shadow .2s; box-shadow:0 1px 4px rgba(0,0,0,0.04); }
.dash-card:hover { box-shadow:0 4px 16px rgba(0,0,0,0.08); }
.dash-card-num { font-size:32px; font-weight:bold; }
.dash-card-icon { position:absolute; right:16px; top:16px; font-size:28px; opacity:0.3; }
.dash-card-label { font-size:13px; color:#909399; margin-top:4px; }
.dash-card-sm { padding:14px 16px 10px; }
.dash-card-num-sm { font-size:24px; font-weight:bold; }
</style>
