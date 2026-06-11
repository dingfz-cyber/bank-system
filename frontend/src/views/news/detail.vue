<script setup>
import { ref, onMounted } from 'vue'
import { getNewsDetail } from '@/api/news'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const news = ref(null)

async function fetchData() {
  loading.value = true
  const id = route.query.id
  if (id) {
    const res = await getNewsDetail(id)
    if (res.code === 200) news.value = res.data
  }
  loading.value = false
}

onMounted(fetchData)
</script>

<template>
  <div v-if="loading" style="text-align:center;padding:60px;color:#999">加载中...</div>
  <div class="page news-detail-page" v-else-if="news">
    <div style="margin-bottom:12px"><el-button size="small" @click="router.push('/news')">← 返回公告列表</el-button></div>
    <h2 class="page-title">{{ news.title }}</h2>
    <div class="news-meta">
      <span>发布时间：{{ news.createTime }}</span>
    </div>
    <div class="news-content" v-html="news.content"></div>
  </div>
  <div class="page" v-else>
    <el-empty description="新闻不存在或已删除" />
  </div>
</template>

<style scoped>
.news-detail-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 40px;
}
.news-meta {
  font-size: 13px;
  color: #909399;
  margin: 10px 0 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}
.news-content {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
}
</style>
