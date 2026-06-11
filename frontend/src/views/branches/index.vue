<script setup>
import { ref, computed } from 'vue'

const city = ref('')
const keyword = ref('')

const allBranches = [
  { city:'北京', name:'总行营业部', addr:'北京市西城区金融街101号', tel:'010-95588001', time:'09:00-17:30' },
  { city:'北京', name:'朝阳支行', addr:'北京市朝阳区建国路88号', tel:'010-95588002', time:'09:00-17:00' },
  { city:'上海', name:'上海分行', addr:'上海市浦东新区陆家嘴66号', tel:'021-95588001', time:'09:00-17:30' },
  { city:'上海', name:'徐汇支行', addr:'上海市徐汇区漕溪北路333号', tel:'021-95588002', time:'09:00-17:00' },
  { city:'广州', name:'广州分行', addr:'广州市天河区珠江新城12号', tel:'020-95588001', time:'09:00-17:30' },
  { city:'深圳', name:'深圳分行', addr:'深圳市福田区华强北路1号', tel:'0755-95588001', time:'09:00-17:30' },
  { city:'杭州', name:'杭州分行', addr:'杭州市西湖区文三路200号', tel:'0571-95588001', time:'09:00-17:00' },
  { city:'成都', name:'成都分行', addr:'成都市高新区天府大道888号', tel:'028-95588001', time:'09:00-17:30' },
  { city:'武汉', name:'武汉分行', addr:'武汉市江汉区解放大道500号', tel:'027-95588001', time:'09:00-17:00' },
  { city:'南京', name:'南京分行', addr:'南京市鼓楼区中山路300号', tel:'025-95588001', time:'09:00-17:30' },
  { city:'天津', name:'天津分行', addr:'天津市和平区南京路200号', tel:'022-95588001', time:'09:00-17:30' },
  { city:'重庆', name:'重庆分行', addr:'重庆市渝中区解放碑1号', tel:'023-95588001', time:'09:00-17:00' },
  { city:'苏州', name:'苏州分行', addr:'苏州市工业园区苏绣路88号', tel:'0512-95588001', time:'09:00-17:00' },
  { city:'西安', name:'西安分行', addr:'西安市高新区科技路66号', tel:'029-95588001', time:'09:00-17:30' },
  { city:'长沙', name:'长沙分行', addr:'长沙市芙蓉区五一路300号', tel:'0731-95588001', time:'09:00-17:00' },
  { city:'郑州', name:'郑州分行', addr:'郑州市郑东新区CBD中心1号', tel:'0371-95588001', time:'09:00-17:30' },
  { city:'青岛', name:'青岛分行', addr:'青岛市市南区香港中路50号', tel:'0532-95588001', time:'09:00-17:00' },
  { city:'厦门', name:'厦门分行', addr:'厦门市思明区鹭江道100号', tel:'0592-95588001', time:'09:00-17:30' },
  { city:'合肥', name:'合肥分行', addr:'合肥市政务区天鹅湖路88号', tel:'0551-95588001', time:'09:00-17:00' },
  { city:'大连', name:'大连分行', addr:'大连市中山区人民路20号', tel:'0411-95588001', time:'09:00-17:30' },
]

const filtered = computed(() => {
  return allBranches.filter(b => {
    if (city.value && b.city !== city.value) return false
    if (keyword.value && !b.name.includes(keyword.value) && !b.addr.includes(keyword.value)) return false
    return true
  })
})

const cities = computed(() => [...new Set(allBranches.map(b => b.city))])
</script>

<template>
  <div class="page branch-page">
    <h2 class="page-title">网点查询</h2>
    <div class="search-bar">
      <el-select v-model="city" placeholder="选择城市" clearable style="width:150px">
        <el-option v-for="c in cities" :key="c" :value="c" :label="c" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索网点名称/地址" clearable style="width:260px;margin-left:12px" />
    </div>
    <div style="margin-bottom:8px;color:#909399;font-size:13px" v-if="filtered.length">共 {{ filtered.length }} 个网点</div>
    <el-table :data="filtered" stripe v-if="filtered.length">
      <el-table-column prop="name" label="网点名称" min-width="160" />
      <el-table-column prop="city" label="城市" width="80" />
      <el-table-column prop="addr" label="地址" min-width="220" />
      <el-table-column prop="tel" label="电话" width="140" />
      <el-table-column prop="time" label="营业时间" width="120" />
    </el-table>
    <el-empty v-if="!filtered.length" description="未找到网点" />
  </div>
</template>

<style scoped>
.branch-page { max-width:1000px; margin:0 auto; padding:20px 40px; }
.search-bar { margin-bottom:16px; display:flex; }
</style>
