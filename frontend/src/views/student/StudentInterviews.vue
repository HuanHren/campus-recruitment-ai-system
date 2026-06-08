<template>
  <div class="student-interviews-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">学生求职流程</p>
        <h1>面试邀请与回复</h1>
        <p class="hero-desc">
          本页只使用学生面试邀请接口：<strong>GET /api/student/interviews</strong> 和
          <strong>PUT /api/student/interviews/{id}/reply</strong>。回复时仅提交 invitationStatus 与 replyRemark。
        </p>
      </div>
      <div class="hero-actions">
        <el-button plain @click="$router.push('/student/applications')">查看投递记录</el-button>
        <el-button type="primary" @click="$router.push('/student/jobs')">继续浏览岗位</el-button>
      </div>
    </section>

    <section class="workflow-panel">
      <div class="workflow-step">
        <span>1</span>
        <strong>查看邀请</strong>
        <p>读取后端返回的面试邀请列表。</p>
      </div>
      <div class="workflow-step">
        <span>2</span>
        <strong>阅读信息</strong>
        <p>确认岗位、时间、地点和联系人。</p>
      </div>
      <div class="workflow-step">
        <span>3</span>
        <strong>接受或拒绝</strong>
        <p>仅待回复邀请可以提交处理结果。</p>
      </div>
    </section>

    <section class="list-shell">
      <div class="toolbar">
        <div>
          <p class="eyebrow">面试邀请</p>
          <h2>邀请状态筛选</h2>
        </div>
        <div class="toolbar-controls">
          <el-select v-model="filters.invitationStatus" clearable placeholder="全部状态" @change="search">
            <el-option
              v-for="status in statusOptions"
              :key="status.value"
              :label="status.label"
              :value="status.value"
            />
          </el-select>
          <el-button plain @click="resetFilters">重置</el-button>
          <el-button type="primary" :loading="loading" @click="loadInterviews">刷新</el-button>
        </div>
      </div>

      <div v-if="errorMessage" class="state-panel state-panel--error">
        <div>
          <strong>面试邀请加载失败</strong>
          <p>{{ errorMessage }}</p>
        </div>
        <el-button plain @click="loadInterviews">重试</el-button>
      </div>

      <el-skeleton v-if="loading" :rows="7" animated />

      <div v-else-if="!interviews.length" class="empty-state">
        <strong>暂无面试邀请</strong>
        <p>当前条件下后端没有返回面试邀请。可以继续查看投递进度或浏览岗位。</p>
        <div class="empty-actions">
          <el-button plain @click="$router.push('/student/applications')">查看投递记录</el-button>
          <el-button type="primary" @click="$router.push('/student/jobs')">浏览岗位</el-button>
        </div>
      </div>

      <div v-else class="table-card">
        <el-table :data="interviews" stripe>
          <el-table-column label="岗位 / 企业" min-width="220">
            <template #default="{ row }">
              <div class="job-cell">
                <strong>{{ row.jobName || '-' }}</strong>
                <span>{{ row.companyName || '-' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="面试时间" min-width="170">
            <template #default="{ row }">{{ formatTime(row.interviewTime) }}</template>
          </el-table-column>
          <el-table-column label="方式 / 地点" min-width="190">
            <template #default="{ row }">
              <div class="muted-stack">
                <span>{{ row.interviewType || '未填写' }}</span>
                <span>{{ row.interviewAddress || '未填写' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="联系人" min-width="150">
            <template #default="{ row }">
              <div class="muted-stack">
                <span>{{ row.contactPerson || '-' }}</span>
                <span>{{ row.contactPhone || '-' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="130">
            <template #default="{ row }">
              <span class="status-tag" :class="statusClass(row.invitationStatus)">
                {{ statusText(row) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="190" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button link type="primary" @click="openDetail(row)">详情</el-button>
                <el-button link type="success" :disabled="!canReply(row)" @click="openReply(row, 'ACCEPTED')">
                  接受
                </el-button>
                <el-button link type="danger" :disabled="!canReply(row)" @click="openReply(row, 'REJECTED')">
                  拒绝
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
            @current-change="loadInterviews"
            @size-change="handleSizeChange"
          />
        </div>
      </div>
    </section>

    <el-drawer v-model="detailVisible" size="46%" title="面试邀请详情">
      <div v-if="selectedInterview" class="detail-body">
        <header>
          <p class="eyebrow">{{ selectedInterview.companyName || '-' }}</p>
          <h2>{{ selectedInterview.jobName || '-' }}</h2>
          <span class="status-tag" :class="statusClass(selectedInterview.invitationStatus)">
            {{ statusText(selectedInterview) }}
          </span>
        </header>

        <section class="detail-grid">
          <div>
            <span>面试时间</span>
            <strong>{{ formatTime(selectedInterview.interviewTime) }}</strong>
          </div>
          <div>
            <span>面试方式</span>
            <strong>{{ selectedInterview.interviewType || '-' }}</strong>
          </div>
          <div>
            <span>面试地点</span>
            <strong>{{ selectedInterview.interviewAddress || '-' }}</strong>
          </div>
          <div>
            <span>联系人</span>
            <strong>{{ selectedInterview.contactPerson || '-' }} / {{ selectedInterview.contactPhone || '-' }}</strong>
          </div>
        </section>

        <section>
          <h3>邀请说明</h3>
          <p>{{ selectedInterview.content || '后端暂未提供邀请说明。' }}</p>
        </section>
        <section>
          <h3>我的回复</h3>
          <p>{{ selectedInterview.replyRemark || '暂未提交回复备注。' }}</p>
        </section>

        <div class="drawer-actions">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button type="success" :disabled="!canReply(selectedInterview)" @click="openReply(selectedInterview, 'ACCEPTED')">
            接受邀请
          </el-button>
          <el-button type="danger" plain :disabled="!canReply(selectedInterview)" @click="openReply(selectedInterview, 'REJECTED')">
            拒绝邀请
          </el-button>
        </div>
      </div>
    </el-drawer>

    <el-dialog v-model="replyVisible" :title="replyTitle" width="460px">
      <el-form label-position="top">
        <el-form-item label="回复结果">
          <el-radio-group v-model="replyForm.invitationStatus">
            <el-radio-button label="ACCEPTED">接受</el-radio-button>
            <el-radio-button label="REJECTED">拒绝</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="回复备注">
          <el-input
            v-model="replyForm.replyRemark"
            type="textarea"
            :rows="4"
            maxlength="200"
            show-word-limit
            placeholder="可填写时间确认、拒绝原因或其他说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="replying" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { getStudentInterviews, replyStudentInterview } from '../../api/student.api'

const statusOptions = [
  { label: '待回复', value: 'PENDING' },
  { label: '已接受', value: 'ACCEPTED' },
  { label: '已拒绝', value: 'REJECTED' }
]

const statusMap = {
  PENDING: '待回复',
  ACCEPTED: '已接受',
  REJECTED: '已拒绝'
}

const loading = ref(false)
const replying = ref(false)
const errorMessage = ref('')
const interviews = ref([])
const total = ref(0)
const page = reactive({ current: 1, size: 10 })
const filters = reactive({ invitationStatus: '' })
const detailVisible = ref(false)
const replyVisible = ref(false)
const selectedInterview = ref(null)
const replyForm = reactive({
  invitationStatus: 'ACCEPTED',
  replyRemark: ''
})

const replyTitle = computed(() => (replyForm.invitationStatus === 'ACCEPTED' ? '接受面试邀请' : '拒绝面试邀请'))

function unwrapPage(response) {
  return response?.data || response || {}
}

function buildParams() {
  return {
    current: page.current,
    size: page.size,
    invitationStatus: filters.invitationStatus || undefined
  }
}

function statusText(row) {
  return row.invitationStatusText || statusMap[row.invitationStatus] || '未知状态'
}

function statusClass(status) {
  return {
    'status-tag--pending': status === 'PENDING',
    'status-tag--accepted': status === 'ACCEPTED',
    'status-tag--rejected': status === 'REJECTED'
  }
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 16)
}

function canReply(row) {
  return row?.id && row.invitationStatus === 'PENDING'
}

async function loadInterviews() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getStudentInterviews(buildParams())
    const data = unwrapPage(response)
    interviews.value = Array.isArray(data.records) ? data.records : []
    total.value = Number(data.total || 0)
  } catch (error) {
    interviews.value = []
    total.value = 0
    errorMessage.value = error?.message || '请确认学生面试邀请接口是否可用。'
  } finally {
    loading.value = false
  }
}

function search() {
  page.current = 1
  loadInterviews()
}

function resetFilters() {
  filters.invitationStatus = ''
  search()
}

function handleSizeChange() {
  page.current = 1
  loadInterviews()
}

function openDetail(row) {
  selectedInterview.value = row
  detailVisible.value = true
}

function openReply(row, status) {
  if (!canReply(row)) return
  selectedInterview.value = row
  replyForm.invitationStatus = status
  replyForm.replyRemark = row.replyRemark || ''
  replyVisible.value = true
}

async function submitReply() {
  if (!selectedInterview.value?.id) return

  replying.value = true
  try {
    await replyStudentInterview(selectedInterview.value.id, {
      invitationStatus: replyForm.invitationStatus,
      replyRemark: replyForm.replyRemark || undefined
    })
    ElMessage.success('面试邀请回复已提交')
    replyVisible.value = false
    detailVisible.value = false
    await loadInterviews()
  } catch (error) {
    ElMessage.error(error?.message || '面试邀请回复失败')
  } finally {
    replying.value = false
  }
}

onMounted(loadInterviews)
</script>

<style scoped>
.student-interviews-page {
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
  max-width: 790px;
  margin: 12px 0 0;
  color: #536174;
  line-height: 1.7;
}

.hero-actions,
.empty-actions,
.drawer-actions,
.row-actions {
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

.workflow-step p,
.state-panel p,
.empty-state p,
.detail-body p {
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

.status-tag--accepted {
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

.detail-grid {
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

@media (max-width: 900px) {
  .page-hero,
  .toolbar,
  .state-panel,
  .empty-state,
  .pagination-row {
    flex-direction: column;
    align-items: stretch;
  }

  .workflow-panel,
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .hero-actions,
  .toolbar-controls {
    justify-content: flex-start;
  }

  .toolbar-controls :deep(.el-select),
  .toolbar-controls :deep(.el-button),
  .empty-actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
