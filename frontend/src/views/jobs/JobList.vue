<template>
  <div class="page">
    <PageHeader title="岗位列表页面" desc="按角色展示岗位数据：学生浏览岗位，企业管理岗位，管理员审核岗位。">
      <el-button v-if="role === 'COMPANY'" type="primary" :icon="Plus" @click="$router.push('/job-publish')">发布岗位</el-button>
    </PageHeader>
    <el-card class="glass-card content-card" shadow="never">
      <el-alert :title="roleTip.title" :description="roleTip.desc" :type="roleTip.type" show-icon :closable="false" style="margin-bottom:16px;" />
      <div class="toolbar">
        <el-input v-model="filters.keyword" placeholder="岗位名称 / 城市 / 学历" style="width:240px;" clearable />
        <el-input v-if="role === 'STUDENT'" v-model="filters.city" placeholder="城市" style="width:160px;" clearable />
        <el-input v-if="role === 'STUDENT'" v-model="filters.education" placeholder="学历" style="width:160px;" clearable />
        <el-select v-if="role !== 'STUDENT'" v-model="filters.auditStatus" placeholder="审核状态" clearable style="width:160px;">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="load">查询</el-button>
      </div>
      <el-table :data="rows" class="soft-table" v-loading="loading">
        <template #empty>
          <div class="empty-state">
            <el-empty description="暂时没有匹配的岗位，换个筛选条件试试" />
          </div>
        </template>
        <el-table-column prop="jobName" label="岗位名称" min-width="160" />
        <el-table-column prop="companyName" label="企业" min-width="160" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column label="薪资" width="150">
          <template #default="{ row }">{{ row.salaryMin }} - {{ row.salaryMax }}</template>
        </el-table-column>
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column prop="auditStatus" label="审核" width="100" />
        <el-table-column prop="publishStatus" label="发布" width="100" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="role === 'STUDENT'" link type="primary" @click="apply(row)">投递</el-button>
            <el-button v-if="role === 'ADMIN' && row.auditStatus === 'PENDING'" link type="success" @click="audit(row, 'APPROVED')">通过</el-button>
            <el-button v-if="role === 'ADMIN' && row.auditStatus === 'PENDING'" link type="danger" @click="audit(row, 'REJECTED')">驳回</el-button>
            <el-button v-if="role === 'COMPANY' && row.publishStatus === 'ONLINE'" link type="warning" @click="offline(row)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;" layout="prev, pager, next,total" :total="total" :page-size="page.size" v-model:current-page="page.current" @current-change="load" />
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()
const role = auth.role
const loading = ref(false)
const rows = ref([])
const total = ref(0)
const page = reactive({ current: 1, size: 10 })
const filters = reactive({ keyword: '', city: '', education: '', auditStatus: '' })
const roleTip = computed(() => {
  if (role === 'ADMIN') {
    return { type: 'warning', title: '管理员审核视角', desc: '重点关注待审核岗位，确保岗位信息真实、薪资合理、描述完整。' }
  }
  if (role === 'COMPANY') {
    return { type: 'success', title: '企业岗位运营视角', desc: '管理岗位上下架与审核状态，及时优化岗位描述以吸引合适学生。' }
  }
  return { type: 'info', title: '学生岗位推荐视角', desc: '结合城市、学历和岗位名称筛选机会，投递前建议先完善简历并使用 AI 匹配分析。' }
})

async function load() {
  loading.value = true
  try {
    let url = '/student/jobs'
    const params = { current: page.current, size: page.size }
    if (role === 'ADMIN') {
      url = '/admin/jobs'
      params.keyword = filters.keyword
      params.auditStatus = filters.auditStatus
    } else if (role === 'COMPANY') {
      url = '/company/jobs'
      params.keyword = filters.keyword
      params.auditStatus = filters.auditStatus
    } else {
      params.jobName = filters.keyword
      params.city = filters.city
      params.education = filters.education
    }
    const res = await request.get(url, { params })
    rows.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

async function audit(row, status) {
  await request.put(`/admin/jobs/${row.id}/audit`, { auditStatus: status, auditRemark: status === 'APPROVED' ? '审核通过' : '信息需完善' })
  ElMessage.success('审核已处理')
  load()
}

async function offline(row) {
  await request.put(`/company/jobs/${row.id}/offline`)
  ElMessage.success('岗位已下架')
  load()
}

async function apply(row) {
  await request.post(`/student/jobs/${row.id}/apply`)
  ElMessage.success('投递成功')
}

onMounted(load)
</script>
