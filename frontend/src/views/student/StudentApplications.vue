<template>
  <div class="student-applications-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">学生求职流程</p>
        <h1>我的投递记录</h1>
        <p class="hero-desc">
          本页只读取学生本人投递记录：<strong>GET /api/student/applications</strong>。筛选条件仅提交
          current、size、applyStatus，状态和岗位信息均来自后端返回值。
        </p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" @click="$router.push('/student/jobs')">继续浏览岗位</el-button>
        <el-button plain @click="$router.push('/student/interviews')">查看面试邀请</el-button>
      </div>
    </section>

    <section class="workflow-panel">
      <div class="workflow-step">
        <span>1</span>
        <strong>提交投递</strong>
        <p>从学生专属岗位页发起投递。</p>
      </div>
      <div class="workflow-step">
        <span>2</span>
        <strong>查看状态</strong>
        <p>根据后端 applyStatus 跟踪处理进度。</p>
      </div>
      <div class="workflow-step">
        <span>3</span>
        <strong>等待或继续求职</strong>
        <p>未收到后续安排时可继续浏览岗位。</p>
      </div>
    </section>

    <section class="list-shell">
      <div class="toolbar">
        <div>
          <p class="eyebrow">投递记录</p>
          <h2>状态筛选</h2>
        </div>
        <div class="toolbar-controls">
          <el-select v-model="filters.applyStatus" clearable placeholder="全部状态" @change="search">
            <el-option
              v-for="status in statusOptions"
              :key="status.value"
              :label="status.label"
              :value="status.value"
            />
          </el-select>
          <el-button plain @click="resetFilters">重置</el-button>
          <el-button type="primary" :loading="loading" @click="loadApplications">刷新</el-button>
        </div>
      </div>

      <div v-if="errorMessage" class="state-panel state-panel--error">
        <div>
          <strong>投递记录加载失败</strong>
          <p>{{ errorMessage }}</p>
        </div>
        <el-button plain @click="loadApplications">重试</el-button>
      </div>

      <el-skeleton v-if="loading" :rows="7" animated />

      <div v-else-if="!applications.length" class="empty-state">
        <strong>暂无投递记录</strong>
        <p>当前条件下后端没有返回投递记录。可以先浏览岗位并发起投递。</p>
        <el-button type="primary" @click="$router.push('/student/jobs')">浏览岗位</el-button>
      </div>

      <div v-else class="table-card">
        <el-table :data="applications" stripe>
          <el-table-column label="岗位" min-width="220">
            <template #default="{ row }">
              <div class="job-cell">
                <strong>{{ row.jobName || '-' }}</strong>
                <span>{{ row.companyName || '-' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="城市 / 学历" min-width="150">
            <template #default="{ row }">
              <div class="muted-stack">
                <span>{{ row.city || '未填写' }}</span>
                <span>{{ row.education || '学历不限' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="薪资" min-width="130">
            <template #default="{ row }">{{ salaryText(row) }}</template>
          </el-table-column>
          <el-table-column label="简历" min-width="150">
            <template #default="{ row }">{{ row.resumeName || '-' }}</template>
          </el-table-column>
          <el-table-column label="投递状态" width="130">
            <template #default="{ row }">
              <span class="status-tag" :class="statusClass(row.applyStatus)">
                {{ statusText(row) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="投递时间" min-width="170">
            <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="备注" min-width="180">
            <template #default="{ row }">{{ row.remark || '-' }}</template>
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
            @current-change="loadApplications"
            @size-change="handleSizeChange"
          />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getStudentApplications } from '../../api/student.api'

const statusOptions = [
  { label: '待查看', value: 'PENDING' },
  { label: '已查看', value: 'VIEWED' },
  { label: '进入面试', value: 'INTERVIEW' },
  { label: '不合适', value: 'REJECTED' }
]

const statusMap = {
  PENDING: '待查看',
  VIEWED: '已查看',
  INTERVIEW: '进入面试',
  REJECTED: '不合适'
}

const loading = ref(false)
const errorMessage = ref('')
const applications = ref([])
const total = ref(0)
const page = reactive({ current: 1, size: 10 })
const filters = reactive({ applyStatus: '' })

function unwrapPage(response) {
  return response?.data || response || {}
}

function buildParams() {
  return {
    current: page.current,
    size: page.size,
    applyStatus: filters.applyStatus || undefined
  }
}

function salaryText(row) {
  if (row.salaryMin === null || row.salaryMin === undefined || row.salaryMax === null || row.salaryMax === undefined) {
    return '薪资面议'
  }
  return `${row.salaryMin}-${row.salaryMax}`
}

function statusText(row) {
  return row.applyStatusText || statusMap[row.applyStatus] || '未知状态'
}

function statusClass(status) {
  return {
    'status-tag--pending': status === 'PENDING',
    'status-tag--viewed': status === 'VIEWED',
    'status-tag--interview': status === 'INTERVIEW',
    'status-tag--rejected': status === 'REJECTED'
  }
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

async function loadApplications() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getStudentApplications(buildParams())
    const data = unwrapPage(response)
    applications.value = Array.isArray(data.records) ? data.records : []
    total.value = Number(data.total || 0)
  } catch (error) {
    applications.value = []
    total.value = 0
    errorMessage.value = error?.message || '请确认学生投递记录接口是否可用。'
  } finally {
    loading.value = false
  }
}

function search() {
  page.current = 1
  loadApplications()
}

function resetFilters() {
  filters.applyStatus = ''
  search()
}

function handleSizeChange() {
  page.current = 1
  loadApplications()
}

onMounted(loadApplications)
</script>

<style scoped>
.student-applications-page {
  display: grid;
  gap: 20px;
  color: #172033;
}

.page-hero,
.workflow-panel,
.list-shell {
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
h2 {
  margin: 0;
  line-height: 1.25;
}

h1 {
  font-size: 28px;
}

h2 {
  font-size: 18px;
}

.hero-desc {
  max-width: 760px;
  margin: 12px 0 0;
  color: #536174;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.workflow-panel {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  padding: 18px;
}

.workflow-step {
  padding: 16px;
  border-radius: 14px;
  background: #f7f9fc;
  border: 1px solid #e2e8f2;
}

.workflow-step span {
  display: inline-grid;
  place-items: center;
  width: 26px;
  height: 26px;
  margin-bottom: 10px;
  border-radius: 50%;
  background: #e1f1ed;
  color: #1d6357;
  font-weight: 800;
}

.workflow-step p {
  margin: 6px 0 0;
  color: #657286;
  line-height: 1.6;
}

.list-shell {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.toolbar-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.toolbar-controls :deep(.el-select) {
  width: 180px;
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
  margin-bottom: 16px;
  border: 1px solid #f0c6c6;
  background: #fff6f6;
  color: #8c2f2f;
}

.state-panel p,
.empty-state p {
  margin: 6px 0 0;
  color: #657286;
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

.status-tag--viewed {
  background: #e8f1ff;
  color: #1d5ea8;
}

.status-tag--interview {
  background: #e4f6ec;
  color: #1d6f46;
}

.status-tag--rejected {
  background: #fdecec;
  color: #9c2c2c;
}

.pagination-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  color: #657286;
}

@media (max-width: 900px) {
  .page-hero,
  .toolbar,
  .state-panel,
  .empty-state,
  .pagination-row {
    flex-direction: column;
    align-items: stretch;
  }

  .workflow-panel {
    grid-template-columns: 1fr;
  }

  .hero-actions,
  .toolbar-controls {
    justify-content: flex-start;
  }

  .toolbar-controls :deep(.el-select),
  .toolbar-controls :deep(.el-button) {
    width: 100%;
  }
}
</style>
