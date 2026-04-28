<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="面试邀请管理"
      desc="企业发送邀请、学生确认安排、管理员查看全平台记录。30 条演示邀请覆盖线上视频、线下面试和电话面试。"
      eyebrow="面试流程跟进"
      icon="solar:calendar-mark-bold-duotone"
    />

    <AppPanel v-if="role === 'COMPANY'" title="发送面试邀请" desc="答辩演示时可从投递记录选择申请 ID 后发送邀请" style="margin-top:18px;" :hover="false">
      <el-form :model="invite" inline class="invite-form">
        <el-form-item label="投递ID"><el-input-number v-model="invite.applyId" :min="1" /></el-form-item>
        <el-form-item label="时间"><el-date-picker v-model="invite.interviewTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="方式"><el-input v-model="invite.interviewType" placeholder="线上视频面试" /></el-form-item>
        <el-form-item label="地点/链接"><el-input v-model="invite.interviewAddress" style="width:260px;" /></el-form-item>
        <el-button type="primary" @click="send">发送邀请</el-button>
      </el-form>
    </AppPanel>

    <section class="interview-summary">
      <AppStatCard v-for="item in summaryCards" :key="item.label" v-bind="item" />
    </section>

    <AppDataTable
      style="margin-top:18px;"
      :data="rows"
      :loading="loading"
      empty-title="暂无面试邀请"
      empty-desc="可以调整邀请状态或关键词筛选，演示数据会按分页展示。"
    >
      <template #toolbar>
        <AppToolbar>
          <el-select v-model="status" placeholder="邀请状态" clearable style="width:180px;">
            <el-option label="待确认" value="PENDING" />
            <el-option label="已接受" value="ACCEPTED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
          <el-select v-model="interviewType" placeholder="面试方式" clearable style="width:180px;">
            <el-option label="线上视频面试" value="线上视频面试" />
            <el-option label="线下面试" value="线下面试" />
            <el-option label="电话面试" value="电话面试" />
          </el-select>
          <el-input v-model="keyword" placeholder="岗位 / 企业 / 学生" style="width:240px;" clearable @keyup.enter="search" />
          <el-button type="primary" @click="search">查询</el-button>
          <el-button plain @click="reset">重置</el-button>
        </AppToolbar>
      </template>

      <el-table-column prop="jobName" label="岗位" min-width="190" />
      <el-table-column prop="companyName" label="企业" min-width="150" />
      <el-table-column prop="studentName" label="学生" width="100" />
      <el-table-column prop="interviewTime" label="面试时间" min-width="170" />
      <el-table-column prop="interviewType" label="方式" width="130" />
      <el-table-column prop="interviewAddress" label="地点/链接" min-width="210" show-overflow-tooltip />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.invitationStatus)" effect="plain">{{ row.invitationStatusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button v-if="role === 'STUDENT' && row.invitationStatus === 'PENDING'" link type="success" @click="reply(row, 'ACCEPTED')">接受</el-button>
          <el-button v-if="role === 'STUDENT' && row.invitationStatus === 'PENDING'" link type="danger" @click="reply(row, 'REJECTED')">拒绝</el-button>
        </template>
      </el-table-column>

      <template #pagination>
        <div class="pagination-note">
          当前展示第 {{ rangeStart }}-{{ rangeEnd }} 条，共 {{ total }} 条邀请
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
import AppDataTable from '../../components/common/AppDataTable.vue'
import AppPageHeader from '../../components/common/AppPageHeader.vue'
import AppPanel from '../../components/common/AppPanel.vue'
import AppStatCard from '../../components/common/AppStatCard.vue'
import AppToolbar from '../../components/common/AppToolbar.vue'
import request from '../../utils/request'
import { useAuthStore } from '../../stores/auth'
import { demoInterviews } from '../../mock/interviews'

const role = useAuthStore().role
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const status = ref('')
const keyword = ref('')
const interviewType = ref('')
const page = reactive({ current: 1, size: 10 })
const invite = reactive({
  applyId: 1,
  interviewTime: '',
  interviewType: '线上视频面试',
  interviewAddress: '腾讯会议：123-456-789',
  contactPerson: '招聘负责人',
  contactPhone: '13900000001',
  content: '请提前准备个人项目介绍、课程设计说明和未来实习或校招规划。'
})

const summaryCards = computed(() => [
  { label: '面试邀请', value: demoInterviews.length, trend: '演示总量', desc: '覆盖三类面试方式', tone: 'blue', icon: 'solar:calendar-mark-bold-duotone' },
  { label: '待确认', value: demoInterviews.filter(item => item.invitationStatus === 'PENDING').length, trend: '学生处理', desc: '适合展示接受/拒绝流程', tone: 'orange', icon: 'solar:alarm-bold-duotone' },
  { label: '已接受', value: demoInterviews.filter(item => item.invitationStatus === 'ACCEPTED').length, trend: '面试推进', desc: '进入正式面试安排', tone: 'green', icon: 'solar:check-circle-bold-duotone' },
  { label: '已拒绝', value: demoInterviews.filter(item => item.invitationStatus === 'REJECTED').length, trend: '需改期', desc: '体现流程状态差异', tone: 'purple', icon: 'solar:close-circle-bold-duotone' }
])

const rangeStart = computed(() => total.value ? (page.current - 1) * page.size + 1 : 0)
const rangeEnd = computed(() => Math.min(page.current * page.size, total.value))

function statusTag(value) {
  const map = { PENDING: 'warning', ACCEPTED: 'success', REJECTED: 'danger' }
  return map[value] || 'info'
}

function search() {
  page.current = 1
  load()
}

function reset() {
  status.value = ''
  keyword.value = ''
  interviewType.value = ''
  search()
}

async function load() {
  loading.value = true
  try {
    const url = role === 'ADMIN' ? '/admin/interviews' : '/student/interviews'
    const res = await request.get(url, { params: { current: page.current, size: page.size, invitationStatus: status.value, keyword: keyword.value } })
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
  let list = [...demoInterviews]
  if (status.value) list = list.filter(item => item.invitationStatus === status.value)
  if (interviewType.value) list = list.filter(item => item.interviewType === interviewType.value)
  if (keyword.value) {
    list = list.filter(item => [item.jobName, item.companyName, item.studentName].some(value => String(value || '').includes(keyword.value)))
  }
  total.value = list.length
  const start = (page.current - 1) * page.size
  rows.value = list.slice(start, start + page.size)
}

function handleSizeChange() {
  page.current = 1
  load()
}

async function send() {
  await request.post('/company/interviews', invite)
  ElMessage.success('面试邀请已发送')
}

async function reply(row, invitationStatus) {
  await request.put(`/student/interviews/${row.id}/reply`, { invitationStatus, replyRemark: invitationStatus === 'ACCEPTED' ? '已确认参加' : '时间不合适' })
  ElMessage.success('回复已提交')
  load()
}

onMounted(load)
</script>

<style scoped>
.invite-form {
  gap: 8px;
}

.interview-summary {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.pagination-note {
  margin-right: auto;
  color: var(--color-muted);
  font-size: 13px;
}

@media (max-width: 1280px) {
  .interview-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
