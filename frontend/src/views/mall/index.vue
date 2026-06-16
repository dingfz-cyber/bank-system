<script setup>
import { ref, onMounted } from 'vue'
import { getMall, getRecords, redeem } from '@/api/points'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore=useUserStore()
const goods=ref([]); const records=ref([]); const showRecords=ref(false)

onMounted(async()=>{
  const [gR,rR]=await Promise.all([getMall(),getRecords()])
  if(gR.code===200) goods.value=gR.data
  if(rR.code===200) records.value=rR.data
})

async function doRedeem(g){
  if(userStore.points<g.cost){ ElMessage.error('积分不足'); return }
  await ElMessageBox.confirm(`确认用 ${g.cost} 积分兑换「${g.name}」？`,'积分兑换')
  await redeem(g.id); ElMessage.success('兑换成功!请联系管理员领取')
  const gR=await getMall(); if(gR.code===200) goods.value=gR.data
  userStore.points-=g.cost; localStorage.setItem('points',userStore.points)
}
</script>

<template>
  <div class="page" style="max-width:1100px;margin:0 auto;padding:20px 40px">
    <h2 style="margin-bottom:16px">积分商城<span style="font-size:14px;color:#999;margin-left:8px">当前积分 ⭐{{ userStore.points || 0 }}</span>
      <el-button size="small" style="float:right" @click="showRecords=!showRecords">{{showRecords?'返回商城':'积分明细'}}</el-button>
    </h2>

    <!-- 积分明细 -->
    <div v-if="showRecords">
      <el-table :data="records" stripe size="small">
        <el-table-column prop="createTime" label="时间" width="180"/>
        <el-table-column prop="points" label="积分" width="80">
          <template #default="scope"><span :style="{color:scope.row.points>0?'#67c23a':'#f56c6c'}">{{scope.row.points>0?'+':''}}{{scope.row.points}}</span></template>
        </el-table-column>
        <el-table-column prop="reason" label="说明"/>
      </el-table>
      <el-empty v-if="!records.length" description="暂无积分记录"/>
    </div>

    <!-- 商城商品 -->
    <el-row :gutter="16" v-else>
      <el-col :span="6" v-for="g in goods" :key="g.id" style="margin-bottom:16px">
        <el-card shadow="hover">
          <img :src="'/bankImg/'+g.image" style="width:100%;height:160px;object-fit:cover;border-radius:6px" @error="$event.target.style.display='none'" />
          <div style="font-weight:bold;margin:10px 0 4px">{{ g.name }}</div>
          <div style="display:flex;justify-content:space-between;align-items:center">
            <span style="color:#e6a23c;font-weight:bold">⭐{{ g.cost }}</span>
            <span style="font-size:12px;color:#909399">库存:{{ g.stock }}</span>
          </div>
          <el-button type="warning" size="small" style="width:100%;margin-top:8px" @click="doRedeem(g)" :disabled="g.stock<=0||userStore.points<g.cost">
            {{ g.stock<=0?'已兑完':userStore.points<g.cost?'积分不足':'立即兑换' }}</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
