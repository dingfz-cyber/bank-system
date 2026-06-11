<script setup>
import { ref, onMounted } from 'vue'
import { getNewsList } from '@/api/news'
import { useRouter } from 'vue-router'

const router = useRouter()
const newsList = ref([])
const allNews = ref([])
const cat = ref('')
const page = ref(1)
const total = ref(0)

async function fetchData() {
  const res = await getNewsList({ page: page.value, pageSize: 100 })
  if (res.code === 200) { allNews.value = res.data.list; applyFilter() }
}
function applyFilter() {
  const filtered = cat.value ? allNews.value.filter(n => n.category === cat.value) : allNews.value
  newsList.value = filtered.slice((page.value-1)*10, page.value*10)
  total.value = filtered.length
}

function goDetail(id) {
  router.push({ name: 'newsDetail', query: { id } })
}

onMounted(fetchData)
</script>

<template>
  <div class="page news-page">
    <h2 class="page-title">新闻公告</h2>
    <div style="margin-bottom:16px">
      <el-radio-group v-model="cat" @change="page=1;applyFilter()">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="系统公告">系统公告</el-radio-button>
        <el-radio-button value="产品动态">产品动态</el-radio-button>
        <el-radio-button value="行业新闻">行业新闻</el-radio-button>
      </el-radio-group>
    </div>
    <div v-if="newsList.length">
      <el-card v-for="item in newsList" :key="item.id" class="news-item" shadow="hover" @click="goDetail(item.id)">
        <div class="news-header">
          <h3 class="news-title">{{ item.title }}</h3>
          <span class="news-time">{{ item.createTime }}</span>
        </div>
        <p class="news-summary">{{ item.summary }}</p>
      </el-card>
      <el-pagination
        v-if="total > 10"
        v-model:current-page="page"
        :page-size="10"
        :total="total"
        layout="prev, pager, next"
        class="pagination"
      />
    </div>
    <el-empty v-else description="暂无新闻公告" />
  </div>
</template>

<style scoped>
.news-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 30px 40px;
}
.news-item {
  margin-bottom: 16px;
  cursor: pointer;
}
.news-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.news-title {
  font-size: 18px;
  color: #303133;
}
.news-time {
  font-size: 13px;
  color: #909399;
}
.news-summary {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}
</style>
