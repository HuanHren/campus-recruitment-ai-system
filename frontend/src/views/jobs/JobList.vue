<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="校园招聘岗位列表"
      desc="岗位数据已扩充为校招、实习、初级工程师和管培生方向，分页、搜索、筛选均可展示真实业务效果。"
      eyebrow="招聘岗位管理"
      icon="solar:case-round-bold-duotone"
    >
      <template #actions>
        <el-button v-if="role === 'COMPANY'" type="primary" @click="$router.push('/job-publish')">
          发布岗位
        </el-button>
      </template>
    </AppPageHeader>

    <section class="job-summary">
      <AppStatCard
        v-for="item in summaryCards"
        :key="item.label"
        v-bind="item"
      />
    </section>

    <AppDataTable
      style="margin-top:18px;"
      :data="rows"
      :loading="loading"
      empty-title="暂无匹配岗位"
      empty-desc="请调整岗位名称、城市、学历、岗位级别或审核状态后重新查询。"
    >
      <template #toolbar>
        <AppToolbar>
          <el-input v-model="filters.keyword" placeholder="岗位名称 / 企业 / 城市 / 技能" style="width:260px;" clearable @keyup.enter="search" />
          <el-select v-model="filters.city" placeholder="城市" clearable style="width:140px;">
            <el-option v-for="city in cityOptions" :key="city" :label="city" :value="city" />
          </el-select>
          <el-select v-model="filters.jobType" placeholder="岗位类型" clearable style="width:150px;">
            <el-option label="实习" value="实习" />
            <el-option label="校招" value="校招" />
            <el-option label="管培生" value="管培生" />
          </el-select>
          <el-select v-model="filters.jobLevel" placeholder="岗位级别" clearable style="width:150px;">
            <el-option label="实习生" value="实习生" />
            <el-option label="应届生" value="应届生" />
            <el-option label="初级工程师" value="初级工程师" />
            <el-option label="管培生" value="管培生" />
          </el-select>
          <el-select v-if="role !== 'STUDENT'" v-model="filters.auditStatus" placeholder="审核状态" clearable style="width:140px;">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
          <el-button type="primary" @click="search">查询岗位</el-button>
          <el-button plain @click="reset">重置</el-button>
          <el-tag effect="plain">{{ roleTip }}</el-tag>
        </AppToolbar>
      </template>

      <el-table-column label="岗位名称" min-width="220">
        <template #default="{ row }">
          <strong class="job-title">{{ row.jobName }}</strong>
          <div class="muted small-text">{{ row.jobType }} / {{ row.jobLevel }} / 招 {{ row.headcount || 1 }} 人</div>
        </template>
      </el-table-column>
      <el-table-column label="企业" min-width="180">
        <template #default="{ row }">
          <div class="company-cell">
            <Icon icon="solar:buildings-2-bold-duotone" />
            <span>{{ row.companyName || '演示企业' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="城市" width="110">
        <template #default="{ row }">
          <el-tag effect="plain">{{ row.city || '不限' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="薪资" width="130">
        <template #default="{ row }">
          <el-tag type="success" effect="dark">{{ row.salaryText || `${row.salaryMin}-${row.salaryMax}` }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="技能标签" min-width="220">
        <template #default="{ row }">
          <AppTagGroup :tags="row.tags || []" />
        </template>
      </el-table-column>
      <el-table-column label="AI热度" width="120">
        <template #default="{ row }">
          <el-progress :percentage="row.aiHeat || 0" :stroke-width="8" />
        </template>
      </el-table-column>
      <el-table-column label="审核" width="110">
        <template #default="{ row }">
          <el-tag :type="auditTag(row.auditStatus)" effect="plain">{{ statusText(row.auditStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布" width="110">
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
        <div class="pagination-note">
          当前展示第 {{ rangeStart }}-{{ rangeEnd }} 条，共 {{ total }} 条岗位
        </div>
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-sizes="[10, 20, 30]"
          @current-change="load"
          @size-change="handleSizeChange"
        />
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
import AppStatCard from '../../components/common/AppStatCard.vue'
import AppTagGroup from '../../components/common/AppTagGroup.vue'
import AppToolbar from '../../components/common/AppToolbar.vue'
import request from '../../utils/request'
import { useAuthStore } from '../../stores/auth'
import { demoJobs } from '../../mock/jobs'
import { jobCategory, jobLevelDistribution } from '../../mock/dashboard'

const auth = useAuthStore()
const role = auth.role
const loading = ref(false)
const rows = ref([])
const total = ref(0)
const page = reactive({ current: 1, size: 10 })
const filters = reactive({ keyword: '', city: '', jobType: '', jobLevel: '', auditStatus: '' })
const cityOptions = computed(() => Array.from(new Set(demoJobs.map(item => item.city))).filter(Boolean))

const roleTip = computed(() => {
  if (role === 'ADMIN') return '管理员审核视角'
  if (role === 'COMPANY') return '企业岗位运营视角'
  return '学生岗位推荐视角'
})

const summaryCards = computed(() => [
  { label: '岗位总数', value: demoJobs.length, trend: '演示数据', desc: '覆盖 13 个城市', tone: 'blue', icon: 'solar:case-round-bold-duotone' },
  { label: '实习岗位', value: jobCategory.find(item => item.name === '实习')?.value || 0, trend: '校招核心', desc: '适合在校实习生', tone: 'purple', icon: 'solar:notebook-bold-duotone' },
  { label: '应届/初级', value: (jobLevelDistribution.find(item => item.name === '应届生')?.value || 0) + (jobLevelDistribution.find(item => item.name === '初级工程师')?.value || 0), trend: '毕业生方向', desc: '薪资区间更贴近校招', tone: 'green', icon: 'solar:medal-star-bold-duotone' },
  { label: '待审核岗位', value: demoJobs.filter(item => item.auditStatus === 'PENDING').length, trend: '管理员处理', desc: '用于展示审核流程', tone: 'orange', icon: 'solar:shield-warning-bold-duotone' }
])

const rangeStart = computed(() => total.value ? (page.current - 1) * page.size + 1 : 0)
const rangeEnd = computed(() => Math.min(page.current * page.size, total.value))

function auditTag(status) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

function statusText(status) {
  const map = { APPROVED: '已通过', REJECTED: '已驳回', PENDING: '待审核' }
  return map[status] || '待审核'
}

function search() {
  page.current = 1
  load()
}

function reset() {
  Object.assign(filters, { keyword: '', city: '', jobType: '', jobLevel: '', auditStatus: '' })
  search()
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
    }
    const res = await request.get(url, { params })
    if (res.data.records?.length) {
      rows.value = res.data.records
      total.value = Number(res.data.total)
    } else {
      loadDemoRows()
    }
  } catch {
    loadDemoRows()
  } finally {
    loading.value = false
  }
}

function loadDemoRows() {
  const keyword = filters.keyword?.trim()
  let list = [...demoJobs]
  if (role === 'STUDENT') {
    list = list.filter(item => item.auditStatus === 'APPROVED' && item.publishStatus === 'ONLINE')
  }
  if (filters.auditStatus) list = list.filter(item => item.auditStatus === filters.auditStatus)
  if (filters.city) list = list.filter(item => item.city === filters.city)
  if (filters.jobType) list = list.filter(item => item.jobType === filters.jobType)
  if (filters.jobLevel) list = list.filter(item => item.jobLevel === filters.jobLevel)
  if (keyword) {
    list = list.filter(item => [
      item.jobName,
      item.companyName,
      item.city,
      item.education,
      item.jobType,
      item.jobLevel,
      ...(item.tags || [])
    ].some(value => String(value || '').includes(keyword)))
  }
  total.value = list.length
  const start = (page.current - 1) * page.size
  rows.value = list.slice(start, start + page.size)
}

function handleSizeChange() {
  page.current = 1
  load()
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

<style scoped>
.job-summary {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.job-title {
  color: var(--color-title);
}

.small-text {
  font-size: 12px;
  margin-top: 5px;
}

.company-cell {
  display: flex;
  align-items: center;
  gap: 9px;
}

.company-cell svg {
  color: #2563eb;
  font-size: 20px;
}

.pagination-note {
  margin-right: auto;
  color: var(--color-muted);
  font-size: 13px;
}

@media (max-width: 1280px) {
  .job-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
