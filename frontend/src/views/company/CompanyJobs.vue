<template>
  <div class="company-jobs-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">企业招聘流程</p>
        <h1>岗位发布与管理</h1>
        <p class="hero-desc">
          本页只使用企业岗位接口：<strong>GET /api/company/jobs</strong>、<strong>POST /api/company/jobs</strong>、
          <strong>PUT /api/company/jobs/{id}</strong> 和 <strong>PUT /api/company/jobs/{id}/offline</strong>。
        </p>
      </div>
      <div class="hero-actions">
        <el-button plain @click="$router.push('/company/profile')">维护企业资料</el-button>
        <el-button type="primary" @click="openCreate">发布岗位</el-button>
      </div>
    </section>

    <section class="filter-panel">
      <el-input v-model="filters.keyword" clearable placeholder="岗位名称或城市" @keyup.enter="search" />
      <el-select v-model="filters.auditStatus" clearable placeholder="审核状态" @change="search">
        <el-option label="待审核" value="PENDING" />
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-select v-model="filters.publishStatus" clearable placeholder="发布状态" @change="search">
        <el-option label="已发布" value="ONLINE" />
        <el-option label="已下架" value="OFFLINE" />
      </el-select>
      <el-button type="primary" :loading="loading" @click="search">查询</el-button>
      <el-button plain @click="resetFilters">重置</el-button>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>岗位列表加载失败</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="loadJobs">重试</el-button>
    </section>

    <section class="job-shell">
      <el-skeleton v-if="loading" :rows="7" animated />
      <div v-else-if="!jobs.length" class="empty-state">
        <div>
          <strong>暂无岗位</strong>
          <p>当前条件下后端没有返回企业岗位。企业资料审核通过后即可发布岗位。</p>
        </div>
        <el-button type="primary" @click="openCreate">发布岗位</el-button>
      </div>

      <div v-else class="table-card">
        <el-table :data="jobs" stripe>
          <el-table-column label="岗位" min-width="220">
            <template #default="{ row }">
              <div class="job-cell">
                <strong>{{ row.jobName || '-' }}</strong>
                <span>{{ row.city || '-' }} / {{ row.jobType || '类型未填' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="薪资 / 学历" min-width="150">
            <template #default="{ row }">
              <div class="muted-stack">
                <span>{{ salaryText(row) }}</span>
                <span>{{ row.education || '学历不限' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="招聘人数" width="100">
            <template #default="{ row }">{{ row.headcount || '-' }}</template>
          </el-table-column>
          <el-table-column label="审核" width="120">
            <template #default="{ row }">
              <span class="status-tag" :class="auditClass(row.auditStatus)">{{ auditText(row.auditStatus) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="发布" width="120">
            <template #default="{ row }">
              <span class="status-tag" :class="publishClass(row.publishStatus)">{{ publishText(row.publishStatus) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="更新时间" min-width="160">
            <template #default="{ row }">{{ formatTime(row.updatedAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="210" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="openDetail(row)">详情</el-button>
                <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
                <el-button
                  link
                  type="warning"
                  :disabled="row.publishStatus === 'OFFLINE'"
                  @click="offline(row)"
                >
                  下架
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

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
      </div>
    </section>

    <el-drawer v-model="detailVisible" size="46%" title="岗位详情">
      <el-skeleton v-if="detailLoading" :rows="8" animated />
      <div v-else-if="selectedJob" class="detail-body">
        <header>
          <p class="eyebrow">{{ selectedJob.companyName || '当前企业' }}</p>
          <h2>{{ selectedJob.jobName || '-' }}</h2>
          <div class="status-row">
            <span class="status-tag" :class="auditClass(selectedJob.auditStatus)">{{ auditText(selectedJob.auditStatus) }}</span>
            <span class="status-tag" :class="publishClass(selectedJob.publishStatus)">{{ publishText(selectedJob.publishStatus) }}</span>
          </div>
        </header>
        <section class="detail-grid">
          <div><span>城市</span><strong>{{ selectedJob.city || '-' }}</strong></div>
          <div><span>薪资</span><strong>{{ salaryText(selectedJob) }}</strong></div>
          <div><span>学历</span><strong>{{ selectedJob.education || '-' }}</strong></div>
          <div><span>经验</span><strong>{{ selectedJob.experience || '-' }}</strong></div>
          <div><span>招聘人数</span><strong>{{ selectedJob.headcount || '-' }}</strong></div>
          <div><span>联系人</span><strong>{{ selectedJob.contactPerson || '-' }} / {{ selectedJob.contactPhone || '-' }}</strong></div>
        </section>
        <section><h3>岗位描述</h3><p>{{ selectedJob.jobDescription || '后端暂未提供岗位描述。' }}</p></section>
        <section><h3>岗位要求</h3><p>{{ selectedJob.jobRequirement || '后端暂未提供岗位要求。' }}</p></section>
        <section><h3>福利待遇</h3><p>{{ selectedJob.welfare || '后端暂未提供福利信息。' }}</p></section>
        <section v-if="selectedJob.auditRemark"><h3>审核备注</h3><p>{{ selectedJob.auditRemark }}</p></section>
      </div>
    </el-drawer>

    <el-drawer v-model="formVisible" size="52%" :title="editingJobId ? '编辑岗位' : '发布岗位'">
      <el-form :model="form" label-position="top" class="job-form">
        <section class="form-section">
          <h3>岗位基础信息</h3>
          <div class="form-grid">
            <el-form-item label="岗位名称"><el-input v-model="form.jobName" maxlength="100" show-word-limit /></el-form-item>
            <el-form-item label="工作城市"><el-input v-model="form.city" maxlength="50" show-word-limit /></el-form-item>
            <el-form-item label="岗位类型"><el-input v-model="form.jobType" maxlength="50" show-word-limit /></el-form-item>
            <el-form-item label="学历要求"><el-input v-model="form.education" maxlength="30" show-word-limit /></el-form-item>
            <el-form-item label="经验要求"><el-input v-model="form.experience" maxlength="50" show-word-limit /></el-form-item>
            <el-form-item label="招聘人数"><el-input-number v-model="form.headcount" :min="1" style="width:100%;" /></el-form-item>
            <el-form-item label="最低薪资"><el-input-number v-model="form.salaryMin" :min="0" style="width:100%;" /></el-form-item>
            <el-form-item label="最高薪资"><el-input-number v-model="form.salaryMax" :min="0" style="width:100%;" /></el-form-item>
          </div>
        </section>

        <section class="form-section">
          <h3>岗位内容</h3>
          <el-form-item label="岗位描述">
            <el-input v-model="form.jobDescription" type="textarea" :rows="5" maxlength="2000" show-word-limit />
          </el-form-item>
          <el-form-item label="岗位要求">
            <el-input v-model="form.jobRequirement" type="textarea" :rows="5" maxlength="2000" show-word-limit />
          </el-form-item>
          <el-form-item label="福利待遇">
            <el-input v-model="form.welfare" maxlength="500" show-word-limit />
          </el-form-item>
        </section>

        <section class="form-section">
          <h3>联系信息</h3>
          <div class="form-grid">
            <el-form-item label="联系人"><el-input v-model="form.contactPerson" maxlength="50" show-word-limit /></el-form-item>
            <el-form-item label="联系电话"><el-input v-model="form.contactPhone" maxlength="20" show-word-limit /></el-form-item>
          </div>
        </section>

        <div class="form-actions">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="saveJob">
            {{ editingJobId ? '保存修改' : '提交岗位' }}
          </el-button>
        </div>
      </el-form>
    </el-drawer>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import {
  createCompanyJob,
  getCompanyJobDetail,
  getCompanyJobs,
  offlineCompanyJob,
  updateCompanyJob
} from '../../api/company.api'

const loading = ref(false)
const detailLoading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const jobs = ref([])
const total = ref(0)
const selectedJob = ref(null)
const detailVisible = ref(false)
const formVisible = ref(false)
const editingJobId = ref(null)
const page = reactive({ current: 1, size: 10 })
const filters = reactive({ keyword: '', auditStatus: '', publishStatus: '' })
const form = reactive(emptyForm())

function emptyForm() {
  return {
    jobName: '',
    city: '',
    jobType: '',
    salaryMin: 0,
    salaryMax: 0,
    education: '',
    experience: '',
    headcount: 1,
    jobDescription: '',
    jobRequirement: '',
    welfare: '',
    contactPerson: '',
    contactPhone: ''
  }
}

function unwrapData(response) {
  return response?.data || response || {}
}

function buildParams() {
  return {
    current: page.current,
    size: page.size,
    keyword: filters.keyword || undefined,
    auditStatus: filters.auditStatus || undefined,
    publishStatus: filters.publishStatus || undefined
  }
}

function buildPayload() {
  return Object.keys(emptyForm()).reduce((payload, field) => {
    payload[field] = form[field]
    return payload
  }, {})
}

function fillForm(job) {
  Object.assign(form, emptyForm(), {
    jobName: job?.jobName || '',
    city: job?.city || '',
    jobType: job?.jobType || '',
    salaryMin: Number(job?.salaryMin ?? 0),
    salaryMax: Number(job?.salaryMax ?? 0),
    education: job?.education || '',
    experience: job?.experience || '',
    headcount: Number(job?.headcount || 1),
    jobDescription: job?.jobDescription || '',
    jobRequirement: job?.jobRequirement || '',
    welfare: job?.welfare || '',
    contactPerson: job?.contactPerson || '',
    contactPhone: job?.contactPhone || ''
  })
}

function salaryText(job) {
  if (job.salaryMin === null || job.salaryMin === undefined || job.salaryMax === null || job.salaryMax === undefined) {
    return '薪资面议'
  }
  return `${job.salaryMin}-${job.salaryMax}`
}

function auditText(status) {
  return { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }[status] || '未知'
}

function publishText(status) {
  return { ONLINE: '已发布', OFFLINE: '已下架' }[status] || '未知'
}

function auditClass(status) {
  return {
    'status-tag--pending': status === 'PENDING',
    'status-tag--approved': status === 'APPROVED',
    'status-tag--rejected': status === 'REJECTED'
  }
}

function publishClass(status) {
  return {
    'status-tag--approved': status === 'ONLINE',
    'status-tag--offline': status === 'OFFLINE'
  }
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

async function loadJobs() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getCompanyJobs(buildParams())
    const data = unwrapData(response)
    jobs.value = Array.isArray(data.records) ? data.records : []
    total.value = Number(data.total || 0)
  } catch (error) {
    jobs.value = []
    total.value = 0
    errorMessage.value = error?.message || '请确认企业岗位列表接口是否可用。'
  } finally {
    loading.value = false
  }
}

function search() {
  page.current = 1
  loadJobs()
}

function resetFilters() {
  Object.assign(filters, { keyword: '', auditStatus: '', publishStatus: '' })
  search()
}

function handleSizeChange() {
  page.current = 1
  loadJobs()
}

function openCreate() {
  editingJobId.value = null
  fillForm(null)
  formVisible.value = true
}

async function openEdit(job) {
  editingJobId.value = job.id
  fillForm(job)
  formVisible.value = true

  try {
    const response = await getCompanyJobDetail(job.id)
    fillForm(unwrapData(response))
  } catch (error) {
    ElMessage.error(error?.message || '岗位详情加载失败')
  }
}

async function openDetail(job) {
  selectedJob.value = job
  detailVisible.value = true
  detailLoading.value = true

  try {
    const response = await getCompanyJobDetail(job.id)
    selectedJob.value = unwrapData(response)
  } catch (error) {
    ElMessage.error(error?.message || '岗位详情加载失败')
  } finally {
    detailLoading.value = false
  }
}

async function saveJob() {
  saving.value = true
  try {
    if (editingJobId.value) {
      await updateCompanyJob(editingJobId.value, buildPayload())
      ElMessage.success('岗位已保存，修改后需重新审核')
    } else {
      await createCompanyJob(buildPayload())
      ElMessage.success('岗位已提交审核')
    }
    formVisible.value = false
    await loadJobs()
  } catch (error) {
    ElMessage.error(error?.message || '岗位保存失败，请确认企业资料是否已审核通过。')
  } finally {
    saving.value = false
  }
}

async function offline(job) {
  try {
    await ElMessageBox.confirm(`确认下架岗位“${job.jobName || '-'}”？`, '下架岗位', { type: 'warning' })
    await offlineCompanyJob(job.id)
    ElMessage.success('岗位已下架')
    await loadJobs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '岗位下架失败')
    }
  }
}

onMounted(loadJobs)
</script>

<style scoped>
.company-jobs-page {
  display: grid;
  gap: 20px;
  color: #172033;
}

.page-hero,
.filter-panel,
.job-shell {
  border: 1px solid #d8e0ec;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 16px 42px rgba(30, 45, 70, 0.08);
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 26px;
  background:
    linear-gradient(135deg, rgba(239, 246, 255, 0.96), rgba(255, 255, 255, 0.98)),
    linear-gradient(90deg, #1f5d8f, #2f7d6d);
}

.eyebrow {
  margin: 0 0 8px;
  color: #2b6b73;
  font-size: 13px;
  font-weight: 700;
}

h1,
h2,
h3 {
  margin: 0;
  line-height: 1.25;
}

h1 {
  font-size: 28px;
}

h2 {
  font-size: 18px;
}

h3 {
  margin-bottom: 12px;
  font-size: 15px;
}

.hero-desc {
  max-width: 800px;
  margin: 12px 0 0;
  color: #536174;
  line-height: 1.7;
}

.hero-actions,
.row-actions,
.form-actions,
.status-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.hero-actions {
  align-items: flex-start;
  justify-content: flex-end;
}

.filter-panel {
  display: grid;
  grid-template-columns: minmax(220px, 1fr) 160px 160px auto auto;
  gap: 10px;
  padding: 16px;
}

.job-shell {
  padding: 20px;
}

.state-panel,
.empty-state {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  padding: 18px;
  border-radius: 14px;
}

.state-panel--error {
  border: 1px solid #f0c6c6;
  background: #fff6f6;
  color: #8c2f2f;
}

.state-panel p,
.empty-state p,
.detail-body p {
  margin: 6px 0 0;
  color: #657286;
  line-height: 1.6;
}

.empty-state {
  min-height: 180px;
  border: 1px dashed #cbd6e5;
  background: #f8fafc;
}

.table-card {
  border: 1px solid #e2e8f2;
  border-radius: 14px;
  overflow: hidden;
}

.job-cell,
.muted-stack {
  display: grid;
  gap: 5px;
}

.job-cell span,
.muted-stack span {
  color: #657286;
  font-size: 13px;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;
  background: #eef2f7;
  color: #4d5b6b;
}

.status-tag--pending {
  background: #fff6db;
  color: #8a5c00;
}

.status-tag--approved {
  background: #e4f6ec;
  color: #1d6f46;
}

.status-tag--rejected {
  background: #fdecec;
  color: #9c2c2c;
}

.status-tag--offline {
  background: #edf0f5;
  color: #536174;
}

.pagination-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  color: #657286;
}

.detail-body,
.job-form {
  display: grid;
  gap: 18px;
}

.detail-grid,
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.detail-grid div,
.form-section {
  padding: 14px;
  border: 1px solid #e2e8f2;
  border-radius: 12px;
  background: #f8fafc;
}

.detail-grid div {
  display: grid;
  gap: 6px;
}

.detail-grid span {
  color: #657286;
  font-size: 13px;
}

@media (max-width: 980px) {
  .page-hero,
  .empty-state,
  .pagination-row {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-panel,
  .detail-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }

  .hero-actions {
    justify-content: flex-start;
  }
}
</style>
