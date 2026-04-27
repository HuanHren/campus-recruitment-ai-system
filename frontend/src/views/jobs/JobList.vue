<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="岗位列表页面"
      desc="按角色展示岗位数据：学生浏览岗位，企业管理岗位，管理员审核岗位。"
      eyebrow="招聘岗位管理"
      icon="solar:case-round-bold-duotone"
    >
      <template #actions>
        <el-button v-if="role === 'COMPANY'" type="primary" @click="$router.push('/job-publish')">
          发布岗位
        </el-button>
      </template>
    </AppPageHeader>

    <AppDataTable
      style="margin-top:18px;"
      :data="rows"
      :loading="loading"
      empty-title="暂无匹配岗位"
      empty-desc="请调整岗位名称、城市、学历或审核状态后重新查询。"
    >
      <template #toolbar>
        <AppToolbar>
          <el-input v-model="filters.keyword" placeholder="岗位名称 / 城市 / 学历" style="width:260px;" clearable />
          <el-input v-if="role === 'STUDENT'" v-model="filters.city" placeholder="城市" style="width:150px;" clearable />
          <el-input v-if="role === 'STUDENT'" v-model="filters.education" placeholder="学历" style="width:150px;" clearable />
          <el-select v-if="role !== 'STUDENT'" v-model="filters.auditStatus" placeholder="审核状态" clearable style="width:160px;">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
          <el-button type="primary" @click="load">查询岗位</el-button>
          <el-tag effect="plain">{{ roleTip }}</el-tag>
        </AppToolbar>
      </template>

      <el-table-column label="岗位名称" min-width="190">
        <template #default="{ row }">
          <strong style="color:var(--title);">{{ row.jobName }}</strong>
          <div class="muted" style="font-size:12px;margin-top:4px;">{{ row.jobType || '校园招聘' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="企业" min-width="180">
        <template #default="{ row }">
          <div style="display:flex;align-items:center;gap:9px;">
            <Icon icon="solar:buildings-2-bold-duotone" style="color:#2563EB;font-size:20px;" />
            <span>{{ row.companyName || '待展示企业' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="城市" width="110">
        <template #default="{ row }">
          <el-tag effect="plain">{{ row.city || '不限' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="薪资" width="150">
        <template #default="{ row }">
          <el-tag type="success" effect="dark">{{ row.salaryMin }} - {{ row.salaryMax }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学历" width="110">
        <template #default="{ row }">
          <el-tag type="info" effect="plain">{{ row.education || '不限' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审核" width="115">
        <template #default="{ row }">
          <el-tag :type="auditTag(row.auditStatus)" effect="plain">{{ statusText(row.auditStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布" width="115">
        <template #default="{ row }">
          <el-tag :type="row.publishStatus === 'ONLINE' ? 'success' : 'info'" effect="plain">
            {{ row.publishStatus === 'ONLINE' ? '发布中' : '已下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="230" fixed="right">
        <template #default="{ row }">
          <el-button v-if="role === 'STUDENT'" size="small" type="primary" plain @click="apply(row)">投递</el-button>
          <el-button v-if="role === 'ADMIN' && row.auditStatus === 'PENDING'" size="small" type="success" plain @click="audit(row, 'APPROVED')">通过</el-button>
          <el-button v-if="role === 'ADMIN' && row.auditStatus === 'PENDING'" size="small" type="danger" plain @click="audit(row, 'REJECTED')">驳回</el-button>
          <el-button v-if="role === 'COMPANY' && row.publishStatus === 'ONLINE'" size="small" type="warning" plain @click="offline(row)">下架</el-button>
        </template>
      </el-table-column>

      <template #pagination>
        <el-pagination layout="prev, pager, next,total" :total="total" :page-size="page.size" v-model:current-page="page.current" @current-change="load" />
      </template>
    </AppDataTable>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Icon } from '@iconify/vue'
import AppDataTable from '../../components/common/AppDataTable.vue'
import AppPageHeader from '../../components/common/AppPageHeader.vue'
import AppToolbar from '../../components/common/AppToolbar.vue'
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
  if (role === 'ADMIN') return '管理员审核视角'
  if (role === 'COMPANY') return '企业岗位运营视角'
  return '学生岗位推荐视角'
})

function auditTag(status) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

function statusText(status) {
  const map = { APPROVED: '已通过', REJECTED: '已驳回', PENDING: '待审核' }
  return map[status] || '待审核'
}

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
