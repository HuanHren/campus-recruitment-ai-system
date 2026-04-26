<template>
  <div class="page">
    <PageHeader title="投递记录页面" desc="学生查看投递进展，企业处理收到的简历和面试状态。" />
    <section class="grid grid-3" style="margin-bottom:18px;">
      <div v-for="item in roleCards" :key="item.label" class="stat-card">
        <el-icon class="icon-pill"><component :is="item.icon" /></el-icon>
        <div class="num">{{ item.value }}</div>
        <div class="label">{{ item.label }}</div>
      </div>
    </section>
    <el-card class="glass-card content-card" shadow="never">
      <div class="toolbar">
        <el-select v-model="status" placeholder="投递状态" clearable style="width:180px;">
          <el-option label="待查看" value="PENDING" />
          <el-option label="已查看" value="VIEWED" />
          <el-option label="邀请面试" value="INTERVIEW" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-input v-if="role === 'COMPANY'" v-model="keyword" placeholder="岗位 / 学生 / 简历" style="width:220px;" clearable />
        <el-button type="primary" :icon="Search" @click="load">查询</el-button>
      </div>
      <el-table :data="rows" v-loading="loading" class="soft-table">
        <template #empty>
          <div class="empty-state">
            <el-empty :description="role === 'COMPANY' ? '暂无收到的投递记录，发布岗位后等待学生投递' : '暂无投递记录，先去岗位列表寻找机会吧'" />
          </div>
        </template>
        <el-table-column prop="jobName" label="岗位" min-width="160" />
        <el-table-column prop="companyName" label="企业" min-width="150" />
        <el-table-column prop="studentName" label="学生" width="100" />
        <el-table-column prop="resumeName" label="简历" min-width="150" />
        <el-table-column prop="applyStatusText" label="状态" width="120" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button v-if="role === 'COMPANY'" link type="primary" @click="change(row, 'VIEWED')">已查看</el-button>
            <el-button v-if="role === 'COMPANY'" link type="success" @click="change(row, 'INTERVIEW')">邀请面试</el-button>
            <el-button v-if="role === 'COMPANY'" link type="danger" @click="change(row, 'REJECTED')">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;" layout="prev, pager, next,total" :total="total" :page-size="page.size" v-model:current-page="page.current" @current-change="load" />
    </el-card>
  </div>
</template>

<script setup>
import { computed, markRaw, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Filter, Postcard, Promotion, Search, TrendCharts } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'
import { useAuthStore } from '../../stores/auth'

const role = useAuthStore().role
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const status = ref('')
const keyword = ref('')
const page = reactive({ current: 1, size: 10 })
const roleCards = computed(() => role === 'COMPANY'
  ? [
      { label: '简历筛选', value: '精准', icon: markRaw(Filter) },
      { label: '待处理投递', value: '优先', icon: markRaw(Postcard) },
      { label: '面试推进', value: '闭环', icon: markRaw(Promotion) }
    ]
  : [
      { label: '投递进度', value: '跟踪', icon: markRaw(TrendCharts) },
      { label: '状态提醒', value: '清晰', icon: markRaw(Postcard) },
      { label: '面试机会', value: '联动', icon: markRaw(Promotion) }
    ])

async function load() {
  loading.value = true
  try {
    const url = role === 'COMPANY' ? '/company/applications' : '/student/applications'
    const res = await request.get(url, { params: { current: page.current, size: page.size, applyStatus: status.value, keyword: keyword.value } })
    rows.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

async function change(row, applyStatus) {
  await request.put(`/company/applications/${row.id}/status`, { applyStatus, remark: '企业已处理' })
  ElMessage.success('投递状态已更新')
  load()
}

onMounted(load)
</script>
