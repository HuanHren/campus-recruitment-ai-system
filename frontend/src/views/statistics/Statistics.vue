<template>
  <div class="page">
    <PageHeader title="数据统计页面" desc="以可视化方式呈现平台核心数据，用于毕业设计展示与答辩说明。">
      <el-button type="primary" :icon="Refresh" @click="load">刷新数据</el-button>
    </PageHeader>
    <section class="grid grid-4">
      <div v-for="item in cards" :key="item.label" class="stat-card">
        <el-icon class="icon-pill"><component :is="item.icon" /></el-icon>
        <div class="num">{{ item.value }}</div>
        <div class="label">{{ item.label }}</div>
      </div>
    </section>
    <section class="grid grid-2" style="margin-top:18px;">
      <el-card class="glass-card content-card" shadow="never">
        <h3>招聘流程转化</h3>
        <div v-for="item in bars" :key="item.label" class="bar-row">
          <span>{{ item.label }}</span>
          <div class="bar-track"><div class="bar-fill" :style="{ width: item.percent + '%' }" /></div>
          <strong>{{ item.value }}</strong>
        </div>
      </el-card>
      <el-card class="glass-card content-card" shadow="never">
        <h3>系统亮点</h3>
        <el-timeline>
          <el-timeline-item timestamp="权限控制">Spring Security + JWT 多角色访问</el-timeline-item>
          <el-timeline-item timestamp="招聘闭环">岗位、简历、投递、面试邀请全流程</el-timeline-item>
          <el-timeline-item timestamp="AI能力">无 Key 兜底模拟数据，答辩可稳定演示</el-timeline-item>
          <el-timeline-item timestamp="前端体验">渐变背景、卡片、动效和响应式布局</el-timeline-item>
        </el-timeline>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { markRaw, onMounted, reactive, ref } from 'vue'
import { Briefcase, DataAnalysis, OfficeBuilding, Postcard, Refresh } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'
import { useAuthStore } from '../../stores/auth'

const role = useAuthStore().role
const cards = reactive([
  { label: '学生资料', value: 0, icon: markRaw(DataAnalysis) },
  { label: '企业资料', value: 0, icon: markRaw(OfficeBuilding) },
  { label: '招聘岗位', value: 0, icon: markRaw(Briefcase) },
  { label: '投递记录', value: 0, icon: markRaw(Postcard) }
])
const bars = ref([
  { label: '岗位发布', value: 12, percent: 80 },
  { label: '简历投递', value: 9, percent: 65 },
  { label: '面试邀请', value: 5, percent: 42 },
  { label: 'AI辅助', value: 4, percent: 35 }
])

async function safeTotal(url, params) {
  try {
    const res = await request.get(url, { params: { current: 1, size: 1, ...params } })
    return Number(res.data.total || 0)
  } catch {
    return 0
  }
}

async function load() {
  if (role === 'ADMIN') {
    cards[0].value = await safeTotal('/admin/students')
    cards[1].value = await safeTotal('/admin/companies')
    cards[2].value = await safeTotal('/admin/jobs')
    cards[3].value = await safeTotal('/admin/interviews')
  } else if (role === 'COMPANY') {
    cards[0].label = '收到投递'
    cards[0].value = await safeTotal('/company/applications')
    cards[1].label = '企业岗位'
    cards[1].value = await safeTotal('/company/jobs')
    cards[2].label = '待处理'
    cards[2].value = await safeTotal('/company/applications', { applyStatus: 'PENDING' })
    cards[3].label = '面试推进'
    cards[3].value = await safeTotal('/company/applications', { applyStatus: 'INTERVIEW' })
  } else if (role === 'STUDENT') {
    cards[0].label = '我的投递'
    cards[0].value = await safeTotal('/student/applications')
    cards[1].label = '岗位机会'
    cards[1].value = await safeTotal('/student/jobs')
    cards[2].label = '面试邀请'
    cards[2].value = await safeTotal('/student/interviews')
    cards[3].label = 'AI能力'
    cards[3].value = 4
  }
}

onMounted(load)
</script>
