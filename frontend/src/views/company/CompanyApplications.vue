<template>
  <div class="company-applications-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">企业招聘流程</p>
        <h1>投递处理与面试邀约</h1>
        <p class="hero-desc">
          本页只使用企业投递与面试创建接口：<strong>GET /api/company/applications</strong>、
          <strong>PUT /api/company/applications/{id}/status</strong> 和 <strong>POST /api/company/interviews</strong>。
          当前后端没有企业侧面试列表接口，因此不展示虚假的面试管理列表。
        </p>
      </div>
      <div class="hero-actions">
        <el-button plain @click="$router.push('/company/jobs')">岗位管理</el-button>
        <el-button type="primary" :loading="loading" @click="loadApplications">刷新投递</el-button>
      </div>
    </section>

    <section class="workflow-panel">
      <div class="workflow-step">
        <span>1</span>
        <strong>查看投递</strong>
        <p>读取企业收到的真实投递记录。</p>
      </div>
      <div class="workflow-step">
        <span>2</span>
        <strong>更新状态</strong>
        <p>标记已查看、进入面试或拒绝。</p>
      </div>
      <div class="workflow-step">
        <span>3</span>
        <strong>发起邀约</strong>
        <p>从选中的投递记录创建面试邀请。</p>
      </div>
    </section>

    <section class="filter-panel">
      <el-select v-model="filters.applyStatus" clearable placeholder="投递状态" @change="search">
        <el-option label="待查看" value="PENDING" />
        <el-option label="已查看" value="VIEWED" />
        <el-option label="进入面试" value="INTERVIEW" />
        <el-option label="已拒绝" value="REJECTED" />
      </el-select>
      <el-input-number v-model="filters.jobId" :min="1" controls-position="right" placeholder="岗位 ID" />
      <el-input v-model="filters.keyword" clearable placeholder="岗位 / 学生 / 简历" @keyup.enter="search" />
      <el-button type="primary" :loading="loading" @click="search">查询</el-button>
      <el-button plain @click="resetFilters">重置</el-button>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>投递记录加载失败</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="loadApplications">重试</el-button>
    </section>

    <section class="application-shell">
      <el-skeleton v-if="loading" :rows="7" animated />
      <div v-else-if="!applications.length" class="empty-state">
        <div>
          <strong>暂无投递记录</strong>
          <p>当前条件下后端没有返回企业收到的投递记录。</p>
        </div>
        <el-button type="primary" @click="$router.push('/company/jobs')">查看岗位管理</el-button>
      </div>

      <div v-else class="table-card">
        <el-table :data="applications" stripe>
          <el-table-column label="岗位 / 学生" min-width="240">
            <template #default="{ row }">
              <div class="main-cell">
                <strong>{{ row.jobName || '-' }}</strong>
                <span>{{ row.studentName || '-' }} / {{ row.resumeName || '简历未命名' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="联系方式" min-width="180">
            <template #default="{ row }">
              <div class="muted-stack">
                <span>{{ row.studentPhone || '-' }}</span>
                <span>{{ row.studentEmail || '-' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="岗位信息" min-width="170">
            <template #default="{ row }">
              <div class="muted-stack">
                <span>{{ row.city || '-' }} / {{ row.education || '学历不限' }}</span>
                <span>{{ salaryText(row) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <span class="status-tag" :class="statusClass(row.applyStatus)">
                {{ row.applyStatusText || statusText(row.applyStatus) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="投递时间" min-width="160">
            <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="260" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="openDetail(row)">详情</el-button>
                <el-button link type="primary" @click="openStatus(row)">改状态</el-button>
                <el-button
                  link
                  type="success"
                  :disabled="row.applyStatus === 'REJECTED'"
                  @click="openInvite(row)"
                >
                  发起面试
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
            @current-change="loadApplications"
            @size-change="handleSizeChange"
          />
        </div>
      </div>
    </section>

    <el-drawer v-model="detailVisible" size="46%" title="投递详情">
      <div v-if="selectedApplication" class="detail-body">
        <header>
          <p class="eyebrow">{{ selectedApplication.companyName || '当前企业' }}</p>
          <h2>{{ selectedApplication.jobName || '-' }}</h2>
          <span class="status-tag" :class="statusClass(selectedApplication.applyStatus)">
            {{ selectedApplication.applyStatusText || statusText(selectedApplication.applyStatus) }}
          </span>
        </header>
        <section class="detail-grid">
          <div><span>学生姓名</span><strong>{{ selectedApplication.studentName || '-' }}</strong></div>
          <div><span>简历</span><strong>{{ selectedApplication.resumeName || '-' }}</strong></div>
          <div><span>联系电话</span><strong>{{ selectedApplication.studentPhone || '-' }}</strong></div>
          <div><span>邮箱</span><strong>{{ selectedApplication.studentEmail || '-' }}</strong></div>
          <div><span>城市</span><strong>{{ selectedApplication.city || '-' }}</strong></div>
          <div><span>薪资</span><strong>{{ salaryText(selectedApplication) }}</strong></div>
        </section>
        <section>
          <h3>处理备注</h3>
          <p>{{ selectedApplication.remark || '暂无处理备注。' }}</p>
        </section>
        <div class="drawer-actions">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button type="primary" @click="openStatus(selectedApplication)">更新状态</el-button>
          <el-button type="success" :disabled="selectedApplication.applyStatus === 'REJECTED'" @click="openInvite(selectedApplication)">
            发起面试邀请
          </el-button>
        </div>
      </div>
    </el-drawer>

    <el-dialog v-model="statusVisible" title="更新投递状态" width="460px">
      <el-form label-position="top">
        <el-form-item label="投递状态">
          <el-select v-model="statusForm.applyStatus" style="width: 100%;">
            <el-option label="待查看" value="PENDING" />
            <el-option label="已查看" value="VIEWED" />
            <el-option label="进入面试" value="INTERVIEW" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input
            v-model="statusForm.remark"
            type="textarea"
            :rows="4"
            maxlength="255"
            show-word-limit
            placeholder="填写处理说明，最多 255 字"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" :loading="savingStatus" @click="submitStatus">保存状态</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="inviteVisible" title="发起面试邀请" width="560px">
      <el-form label-position="top" class="invite-form">
        <el-form-item label="面试时间">
          <el-date-picker
            v-model="inviteForm.interviewTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择面试时间"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="面试方式">
          <el-input v-model="inviteForm.interviewType" maxlength="50" show-word-limit placeholder="如：线上视频面试 / 线下面试" />
        </el-form-item>
        <el-form-item label="面试地点或会议链接">
          <el-input v-model="inviteForm.interviewAddress" maxlength="255" show-word-limit />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="联系人">
            <el-input v-model="inviteForm.contactPerson" maxlength="50" show-word-limit />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="inviteForm.contactPhone" maxlength="20" show-word-limit />
          </el-form-item>
        </div>
        <el-form-item label="邀请说明">
          <el-input
            v-model="inviteForm.content"
            type="textarea"
            :rows="4"
            maxlength="1000"
            show-word-limit
            placeholder="填写面试准备事项或其他说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inviteVisible = false">取消</el-button>
        <el-button type="primary" :loading="sendingInvite" @click="submitInvite">发送邀请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import {
  createCompanyInterview,
  getCompanyApplications,
  updateApplicationStatus
} from '../../api/company.api'

const loading = ref(false)
const savingStatus = ref(false)
const sendingInvite = ref(false)
const errorMessage = ref('')
const applications = ref([])
const total = ref(0)
const selectedApplication = ref(null)
const detailVisible = ref(false)
const statusVisible = ref(false)
const inviteVisible = ref(false)
const page = reactive({ current: 1, size: 10 })
const filters = reactive({ applyStatus: '', jobId: undefined, keyword: '' })
const statusForm = reactive({ applyStatus: 'VIEWED', remark: '' })
const inviteForm = reactive({
  interviewTime: '',
  interviewType: '',
  interviewAddress: '',
  contactPerson: '',
  contactPhone: '',
  content: ''
})

function unwrapPage(response) {
  return response?.data || response || {}
}

function buildParams() {
  return {
    current: page.current,
    size: page.size,
    applyStatus: filters.applyStatus || undefined,
    jobId: filters.jobId || undefined,
    keyword: filters.keyword || undefined
  }
}

function statusText(status) {
  return { PENDING: '待查看', VIEWED: '已查看', INTERVIEW: '进入面试', REJECTED: '已拒绝' }[status] || '未知'
}

function statusClass(status) {
  return {
    'status-tag--pending': status === 'PENDING',
    'status-tag--viewed': status === 'VIEWED',
    'status-tag--interview': status === 'INTERVIEW',
    'status-tag--rejected': status === 'REJECTED'
  }
}

function salaryText(row) {
  if (row.salaryMin === null || row.salaryMin === undefined || row.salaryMax === null || row.salaryMax === undefined) {
    return '薪资面议'
  }
  return `${row.salaryMin}-${row.salaryMax}`
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

async function loadApplications() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getCompanyApplications(buildParams())
    const data = unwrapPage(response)
    applications.value = Array.isArray(data.records) ? data.records : []
    total.value = Number(data.total || 0)
  } catch (error) {
    applications.value = []
    total.value = 0
    errorMessage.value = error?.message || '请确认企业投递记录接口是否可用。'
  } finally {
    loading.value = false
  }
}

function search() {
  page.current = 1
  loadApplications()
}

function resetFilters() {
  Object.assign(filters, { applyStatus: '', jobId: undefined, keyword: '' })
  search()
}

function handleSizeChange() {
  page.current = 1
  loadApplications()
}

function openDetail(row) {
  selectedApplication.value = row
  detailVisible.value = true
}

function openStatus(row) {
  selectedApplication.value = row
  statusForm.applyStatus = row.applyStatus || 'VIEWED'
  statusForm.remark = row.remark || ''
  statusVisible.value = true
}

async function submitStatus() {
  if (!selectedApplication.value?.id) return

  savingStatus.value = true
  try {
    await updateApplicationStatus(selectedApplication.value.id, {
      applyStatus: statusForm.applyStatus,
      remark: statusForm.remark || undefined
    })
    ElMessage.success('投递状态已更新')
    statusVisible.value = false
    detailVisible.value = false
    await loadApplications()
  } catch (error) {
    ElMessage.error(error?.message || '投递状态更新失败')
  } finally {
    savingStatus.value = false
  }
}

function openInvite(row) {
  selectedApplication.value = row
  Object.assign(inviteForm, {
    interviewTime: '',
    interviewType: '',
    interviewAddress: '',
    contactPerson: '',
    contactPhone: '',
    content: ''
  })
  inviteVisible.value = true
}

async function submitInvite() {
  if (!selectedApplication.value?.id) return

  sendingInvite.value = true
  try {
    await createCompanyInterview({
      applyId: selectedApplication.value.id,
      interviewTime: inviteForm.interviewTime,
      interviewType: inviteForm.interviewType,
      interviewAddress: inviteForm.interviewAddress,
      contactPerson: inviteForm.contactPerson,
      contactPhone: inviteForm.contactPhone,
      content: inviteForm.content
    })
    ElMessage.success('面试邀请已发送')
    inviteVisible.value = false
    detailVisible.value = false
    await loadApplications()
  } catch (error) {
    ElMessage.error(error?.message || '面试邀请发送失败')
  } finally {
    sendingInvite.value = false
  }
}

onMounted(loadApplications)
</script>

<style scoped>
.company-applications-page {
  display: grid;
  gap: 20px;
  color: #172033;
}

.page-hero,
.workflow-panel,
.filter-panel,
.application-shell {
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
    linear-gradient(135deg, rgba(240, 247, 244, 0.96), rgba(255, 255, 255, 0.98)),
    linear-gradient(90deg, #235f72, #2e7763);
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
  margin-bottom: 8px;
  font-size: 15px;
}

.hero-desc {
  max-width: 830px;
  margin: 12px 0 0;
  color: #536174;
  line-height: 1.7;
}

.hero-actions,
.row-actions,
.drawer-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.hero-actions {
  align-items: flex-start;
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
  border: 1px solid #e2e8f2;
  background: #f7f9fc;
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

.workflow-step p,
.state-panel p,
.empty-state p,
.detail-body p {
  margin: 6px 0 0;
  color: #657286;
  line-height: 1.6;
}

.filter-panel {
  display: grid;
  grid-template-columns: 170px 140px minmax(220px, 1fr) auto auto;
  gap: 10px;
  padding: 16px;
}

.application-shell {
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

.main-cell,
.muted-stack {
  display: grid;
  gap: 5px;
}

.main-cell span,
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

.detail-body {
  display: grid;
  gap: 20px;
}

.detail-body header {
  display: grid;
  gap: 10px;
}

.detail-grid,
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.detail-grid div {
  display: grid;
  gap: 6px;
  padding: 14px;
  border: 1px solid #e2e8f2;
  border-radius: 12px;
  background: #f8fafc;
}

.detail-grid span {
  color: #657286;
  font-size: 13px;
}

.invite-form {
  display: grid;
  gap: 2px;
}

@media (max-width: 980px) {
  .page-hero,
  .state-panel,
  .empty-state,
  .pagination-row {
    flex-direction: column;
    align-items: stretch;
  }

  .workflow-panel,
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
