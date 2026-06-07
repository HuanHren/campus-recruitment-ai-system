<template>
  <div class="admin-page">
    <section class="admin-page__header">
      <div>
        <p class="eyebrow">ADMIN 管理页</p>
        <h1>岗位管理</h1>
        <p>读取后端 <strong>GET /api/admin/jobs</strong>，仅提供后端支持的岗位审核操作。</p>
      </div>
      <el-button :loading="loading" type="primary" @click="loadJobs">刷新</el-button>
    </section>

    <section class="admin-panel filter-panel">
      <el-input v-model="filters.keyword" clearable placeholder="岗位名称 / 城市 / 学历" @keyup.enter="search" />
      <el-select v-model="filters.auditStatus" clearable placeholder="审核状态">
        <el-option label="待审核" value="PENDING" />
        <el-option label="已通过" value="APPROVED" />
        <el-option label="已驳回" value="REJECTED" />
      </el-select>
      <el-select v-model="filters.publishStatus" clearable placeholder="发布状态">
        <el-option label="已发布" value="ONLINE" />
        <el-option label="已下线" value="OFFLINE" />
      </el-select>
      <el-button type="primary" @click="search">查询</el-button>
      <el-button plain @click="reset">重置</el-button>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>岗位列表加载失败</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="loadJobs">重试</el-button>
    </section>

    <section class="admin-panel table-panel">
      <el-table v-loading="loading" :data="rows" border stripe class="admin-table">
        <template #empty>
          <div class="empty-state">
            <strong>暂无岗位信息</strong>
            <p>当前筛选条件下没有后端返回记录。</p>
          </div>
        </template>

        <el-table-column label="岗位" min-width="210">
          <template #default="{ row }">
            <strong class="primary-text">{{ row.jobName || '-' }}</strong>
            <p class="muted-text">{{ row.companyName || '-' }}</p>
          </template>
        </el-table-column>
        <el-table-column prop="city" label="城市" width="110" />
        <el-table-column prop="jobType" label="类型" width="120" />
        <el-table-column label="薪资" min-width="130">
          <template #default="{ row }">{{ salaryText(row) }}</template>
        </el-table-column>
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column prop="experience" label="经验" width="120" />
        <el-table-column prop="headcount" label="人数" width="90" />
        <el-table-column label="审核状态" width="120">
          <template #default="{ row }">
            <span class="status-tag" :class="`status-tag--${row.auditStatus || 'UNKNOWN'}`">
              {{ auditStatusText(row.auditStatus) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" min-width="180" show-overflow-tooltip />
        <el-table-column label="发布状态" width="120">
          <template #default="{ row }">
            <span class="status-tag" :class="`status-tag--${row.publishStatus || 'UNKNOWN'}`">
              {{ publishStatusText(row.publishStatus) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.updatedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" plain @click="openAudit(row, 'APPROVED')">通过</el-button>
            <el-button size="small" type="danger" plain @click="openAudit(row, 'REJECTED')">驳回</el-button>
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
    </section>

    <el-dialog v-model="auditDialogVisible" title="岗位审核" width="460px">
      <el-form label-position="top">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio-button label="APPROVED">通过</el-radio-button>
            <el-radio-button label="REJECTED">驳回</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.auditRemark" type="textarea" :rows="4" maxlength="255" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button :loading="auditLoading" type="primary" @click="submitAudit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { auditJob, getAdminJobs } from '../../api/admin.api'

const loading = ref(false)
const auditLoading = ref(false)
const errorMessage = ref('')
const rows = ref([])
const total = ref(0)
const page = reactive({ current: 1, size: 10 })
const filters = reactive({ keyword: '', auditStatus: '', publishStatus: '' })
const auditDialogVisible = ref(false)
const currentRow = ref(null)
const auditForm = reactive({ auditStatus: 'APPROVED', auditRemark: '' })

function unwrapPage(response) {
  return response?.data || response || {}
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

function salaryText(row) {
  if (row.salaryMin === null || row.salaryMin === undefined || row.salaryMax === null || row.salaryMax === undefined) {
    return '-'
  }
  return `${row.salaryMin}-${row.salaryMax}`
}

function auditStatusText(status) {
  const map = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }
  return map[status] || '未知'
}

function publishStatusText(status) {
  const map = { ONLINE: '已发布', OFFLINE: '已下线' }
  return map[status] || '未知'
}

async function loadJobs() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getAdminJobs({
      current: page.current,
      size: page.size,
      keyword: filters.keyword || undefined,
      auditStatus: filters.auditStatus || undefined,
      publishStatus: filters.publishStatus || undefined
    })
    const data = unwrapPage(response)
    rows.value = Array.isArray(data.records) ? data.records : []
    total.value = Number(data.total || 0)
  } catch (error) {
    rows.value = []
    total.value = 0
    errorMessage.value = error?.message || '请确认后端岗位管理接口是否可用。'
  } finally {
    loading.value = false
  }
}

function search() {
  page.current = 1
  loadJobs()
}

function reset() {
  Object.assign(filters, { keyword: '', auditStatus: '', publishStatus: '' })
  search()
}

function handleSizeChange() {
  page.current = 1
  loadJobs()
}

function openAudit(row, status) {
  currentRow.value = row
  auditForm.auditStatus = status
  auditForm.auditRemark = row.auditRemark || ''
  auditDialogVisible.value = true
}

async function submitAudit() {
  if (!currentRow.value?.id) return
  auditLoading.value = true

  try {
    await auditJob(currentRow.value.id, {
      auditStatus: auditForm.auditStatus,
      auditRemark: auditForm.auditRemark
    })
    ElMessage.success('岗位审核已更新')
    auditDialogVisible.value = false
    loadJobs()
  } finally {
    auditLoading.value = false
  }
}

onMounted(loadJobs)
</script>

<style scoped>
.admin-page {
  display: grid;
  gap: 18px;
}

.admin-page__header,
.admin-panel,
.state-panel {
  border: 1px solid #dbe5ef;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.05);
}

.admin-page__header {
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

h1 {
  margin: 0;
  color: #102033;
  font-size: 28px;
}

.admin-page__header p:not(.eyebrow),
.muted-text,
.empty-state p,
.state-panel p {
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
  width: 280px;
}

.filter-panel .el-select {
  width: 150px;
}

.table-panel {
  padding: 16px;
}

.admin-table {
  width: 100%;
}

.primary-text {
  color: #102033;
}

.muted-text {
  margin: 4px 0 0;
  font-size: 12px;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
}

.status-tag--APPROVED,
.status-tag--ONLINE {
  color: #047857;
  background: #ecfdf5;
}

.status-tag--REJECTED,
.status-tag--OFFLINE {
  color: #b91c1c;
  background: #fef2f2;
}

.status-tag--PENDING,
.status-tag--UNKNOWN {
  color: #a16207;
  background: #fefce8;
}

.empty-state {
  min-height: 150px;
  display: grid;
  place-items: center;
  align-content: center;
}

.pagination-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding-top: 16px;
}

.state-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px;
}

.state-panel--error {
  border-color: #fecaca;
  background: #fff7f7;
}

@media (max-width: 760px) {
  .admin-page__header,
  .state-panel,
  .pagination-row {
    align-items: stretch;
    flex-direction: column;
  }

  .filter-panel .el-input,
  .filter-panel .el-select {
    width: 100%;
  }
}
</style>
