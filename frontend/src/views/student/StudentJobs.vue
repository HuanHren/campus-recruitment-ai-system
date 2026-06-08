<template>
  <div class="student-jobs-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">学生求职流程</p>
        <h1>浏览岗位并发起投递</h1>
        <p class="hero-desc">
          本页只使用学生岗位接口：<strong>GET /api/student/jobs</strong>、
          <strong>GET /api/student/jobs/{id}</strong> 和 <strong>POST /api/student/jobs/{jobId}/apply</strong>。
        </p>
      </div>
      <el-button plain @click="$router.push('/student/applications')">查看投递记录</el-button>
    </section>

    <section class="filter-panel">
      <el-input v-model="filters.jobName" clearable placeholder="岗位名称" @keyup.enter="search" />
      <el-input v-model="filters.city" clearable placeholder="城市" @keyup.enter="search" />
      <el-input-number v-model="filters.minSalary" :min="0" placeholder="最低薪资" controls-position="right" />
      <el-input-number v-model="filters.maxSalary" :min="0" placeholder="最高薪资" controls-position="right" />
      <el-input v-model="filters.education" clearable placeholder="学历要求" @keyup.enter="search" />
      <el-button type="primary" @click="search">查询</el-button>
      <el-button plain @click="resetFilters">重置</el-button>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>岗位列表加载失败</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="loadJobs">重试</el-button>
    </section>

    <section class="job-list-panel">
      <el-skeleton v-if="loading" :rows="7" animated />
      <div v-else-if="!jobs.length" class="empty-state">
        <strong>暂无岗位</strong>
        <p>当前筛选条件下没有后端返回的可投递岗位。</p>
      </div>

      <div v-else class="job-list">
        <article v-for="job in jobs" :key="job.id" class="job-card">
          <div class="job-main">
            <div>
              <h2>{{ job.jobName || '-' }}</h2>
              <p>{{ job.companyName || '-' }}</p>
            </div>
            <span class="salary">{{ salaryText(job) }}</span>
          </div>
          <div class="job-meta">
            <span>{{ job.city || '城市不限' }}</span>
            <span>{{ job.jobType || '类型未填写' }}</span>
            <span>{{ job.education || '学历不限' }}</span>
            <span>{{ job.experience || '经验不限' }}</span>
            <span>招聘 {{ job.headcount || '-' }} 人</span>
          </div>
          <p class="job-desc">{{ shortText(job.jobRequirement || job.jobDescription) }}</p>
          <div class="job-actions">
            <el-button plain @click="openDetail(job)">查看详情</el-button>
            <el-button type="primary" @click="confirmApply(job)">投递岗位</el-button>
          </div>
        </article>
      </div>

      <div class="pagination-row">
        <span>共 {{ total }} 条</span>
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          layout="sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 30]"
          :total="total"
          @current-change="loadJobs"
          @size-change="handleSizeChange"
        />
      </div>
    </section>

    <el-drawer v-model="detailVisible" size="48%" title="岗位详情">
      <el-skeleton v-if="detailLoading" :rows="8" animated />
      <div v-else-if="selectedJob" class="detail-body">
        <header>
          <p class="eyebrow">{{ selectedJob.companyName || '-' }}</p>
          <h2>{{ selectedJob.jobName || '-' }}</h2>
          <div class="job-meta">
            <span>{{ selectedJob.city || '城市不限' }}</span>
            <span>{{ salaryText(selectedJob) }}</span>
            <span>{{ selectedJob.education || '学历不限' }}</span>
            <span>{{ selectedJob.experience || '经验不限' }}</span>
          </div>
        </header>

        <section>
          <h3>岗位描述</h3>
          <p>{{ selectedJob.jobDescription || '后端暂未提供岗位描述。' }}</p>
        </section>
        <section>
          <h3>岗位要求</h3>
          <p>{{ selectedJob.jobRequirement || '后端暂未提供岗位要求。' }}</p>
        </section>
        <section>
          <h3>福利待遇</h3>
          <p>{{ selectedJob.welfare || '后端暂未提供福利信息。' }}</p>
        </section>
        <section>
          <h3>联系方式</h3>
          <p>{{ selectedJob.contactPerson || '-' }} / {{ selectedJob.contactPhone || '-' }}</p>
        </section>

        <div class="drawer-actions">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button type="primary" @click="confirmApply(selectedJob)">投递岗位</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { applyJob, getStudentJobDetail, getStudentJobs } from '../../api/student.api'

const loading = ref(false)
const detailLoading = ref(false)
const errorMessage = ref('')
const jobs = ref([])
const total = ref(0)
const selectedJob = ref(null)
const detailVisible = ref(false)
const page = reactive({ current: 1, size: 10 })
const filters = reactive({
  jobName: '',
  city: '',
  minSalary: undefined,
  maxSalary: undefined,
  education: ''
})

function unwrapPage(response) {
  return response?.data || response || {}
}

function buildParams() {
  return {
    current: page.current,
    size: page.size,
    jobName: filters.jobName || undefined,
    city: filters.city || undefined,
    minSalary: filters.minSalary ?? undefined,
    maxSalary: filters.maxSalary ?? undefined,
    education: filters.education || undefined
  }
}

function salaryText(job) {
  if (job.salaryMin === null || job.salaryMin === undefined || job.salaryMax === null || job.salaryMax === undefined) {
    return '薪资面议'
  }
  return `${job.salaryMin}-${job.salaryMax}`
}

function shortText(value) {
  if (!value) return '后端暂未提供岗位说明。'
  return value.length > 120 ? `${value.slice(0, 120)}...` : value
}

async function loadJobs() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getStudentJobs(buildParams())
    const data = unwrapPage(response)
    jobs.value = Array.isArray(data.records) ? data.records : []
    total.value = Number(data.total || 0)
  } catch (error) {
    jobs.value = []
    total.value = 0
    errorMessage.value = error?.message || '请确认学生岗位列表接口是否可用。'
  } finally {
    loading.value = false
  }
}

function search() {
  page.current = 1
  loadJobs()
}

function resetFilters() {
  Object.assign(filters, {
    jobName: '',
    city: '',
    minSalary: undefined,
    maxSalary: undefined,
    education: ''
  })
  search()
}

function handleSizeChange() {
  page.current = 1
  loadJobs()
}

async function openDetail(job) {
  if (!job?.id) return
  detailVisible.value = true
  detailLoading.value = true
  selectedJob.value = job

  try {
    const response = await getStudentJobDetail(job.id)
    selectedJob.value = response?.data || response || job
  } catch (error) {
    ElMessage.error(error?.message || '岗位详情加载失败')
  } finally {
    detailLoading.value = false
  }
}

async function confirmApply(job) {
  if (!job?.id) return

  try {
    await ElMessageBox.confirm(
      `确认投递「${job.jobName || '该岗位'}」？`,
      '确认投递',
      {
        confirmButtonText: '确认投递',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
  } catch {
    return
  }

  await applyJob(job.id)
  ElMessage.success('投递成功')
}

onMounted(loadJobs)
</script>

<style scoped>
.student-jobs-page {
  display: grid;
  gap: 18px;
}

.page-hero,
.filter-panel,
.job-list-panel,
.state-panel,
.job-card {
  border: 1px solid #dbe5ef;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.05);
}

.page-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  padding: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #047857;
  font-size: 13px;
  font-weight: 800;
}

h1,
h2,
h3 {
  margin: 0;
  color: #102033;
}

h1 {
  font-size: 28px;
}

.hero-desc,
.state-panel p,
.empty-state p,
.job-main p,
.job-desc,
.detail-body p {
  margin: 8px 0 0;
  color: #64748b;
  line-height: 1.7;
}

.filter-panel {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 16px;
}

.filter-panel .el-input {
  width: 180px;
}

.job-list-panel {
  padding: 16px;
}

.job-list {
  display: grid;
  gap: 14px;
}

.job-card {
  padding: 18px;
  box-shadow: none;
}

.job-main,
.job-actions,
.pagination-row,
.state-panel,
.drawer-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.salary {
  color: #047857;
  font-size: 18px;
  font-weight: 900;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.job-meta span {
  padding: 4px 10px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #475569;
  font-size: 12px;
  font-weight: 700;
}

.job-actions {
  justify-content: flex-end;
  margin-top: 14px;
}

.empty-state {
  min-height: 180px;
  display: grid;
  place-items: center;
  align-content: center;
  text-align: center;
}

.pagination-row {
  padding-top: 16px;
}

.state-panel {
  padding: 18px;
}

.state-panel--error {
  border-color: #fecaca;
  background: #fff7f7;
}

.detail-body {
  display: grid;
  gap: 22px;
}

.detail-body section {
  padding-bottom: 18px;
  border-bottom: 1px solid #e2e8f0;
}

.detail-body section:last-of-type {
  border-bottom: 0;
}

.drawer-actions {
  justify-content: flex-end;
  position: sticky;
  bottom: 0;
  padding-top: 16px;
  background: #fff;
}

@media (max-width: 900px) {
  .page-hero,
  .job-main,
  .pagination-row,
  .state-panel {
    align-items: stretch;
    flex-direction: column;
  }

  .filter-panel .el-input,
  .filter-panel .el-input-number {
    width: 100%;
  }

  .job-actions,
  .drawer-actions {
    justify-content: flex-start;
  }
}
</style>
