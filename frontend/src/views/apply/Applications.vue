<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="岗位投递记录"
      desc="展示学生投递进度与企业简历处理流程，120 条演示数据按状态均衡分布，分页和筛选效果更明显。"
      eyebrow="投递流程管理"
      icon="solar:paper-plane-bold-duotone"
    />

    <section class="apply-summary">
      <AppStatCard v-for="item in roleCards" :key="item.label" v-bind="item" />
    </section>

    <AppDataTable
      style="margin-top:18px;"
      :data="rows"
      :loading="loading"
      empty-title="暂无投递记录"
      empty-desc="可以切换状态或关键词重新筛选，演示数据会自动按分页展示。"
    >
      <template #toolbar>
        <AppToolbar>
          <el-select v-model="status" placeholder="投递状态" clearable style="width:180px;">
            <el-option label="已投递" value="PENDING" />
            <el-option label="已查看" value="VIEWED" />
            <el-option label="邀请面试" value="INTERVIEW" />
            <el-option label="已录用" value="OFFER" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
          <el-input v-model="keyword" placeholder="岗位 / 企业 / 学生 / 简历" style="width:260px;" clearable @keyup.enter="search" />
          <el-button type="primary" @click="search">查询</el-button>
          <el-button plain @click="reset">重置</el-button>
        </AppToolbar>
      </template>

      <el-table-column prop="jobName" label="岗位" min-width="190" />
      <el-table-column prop="companyName" label="企业" min-width="150" />
      <el-table-column prop="studentName" label="学生" width="100" />
      <el-table-column prop="resumeName" label="简历" min-width="160" />
      <el-table-column label="AI匹配度" width="130">
        <template #default="{ row }">
          <el-progress :percentage="row.aiMatchScore || 0" :stroke-width="8" />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.applyStatus)" effect="plain">{{ row.applyStatusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="投递时间" min-width="160" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button v-if="role === 'COMPANY'" link type="primary" @click="change(row, 'VIEWED')">标记已查看</el-button>
          <el-button v-if="role === 'COMPANY'" link type="success" @click="change(row, 'INTERVIEW')">邀请面试</el-button>
          <el-button v-if="role === 'COMPANY'" link type="danger" @click="change(row, 'REJECTED')">拒绝</el-button>
        </template>
      </el-table-column>

      <template #pagination>
        <div class="pagination-note">
          当前展示第 {{ rangeStart }}-{{ rangeEnd }} 条，共 {{ total }} 条投递
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
import AppStatCard from '../../components/common/AppStatCard.vue'
import AppToolbar from '../../components/common/AppToolbar.vue'
import request from '../../utils/request'
import { useAuthStore } from '../../stores/auth'
import { demoApplications } from '../../mock/applications'

const role = useAuthStore().role
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const status = ref('')
const keyword = ref('')
const page = reactive({ current: 1, size: 10 })

const roleCards = computed(() => {
  const pending = demoApplications.filter(item => item.applyStatus === 'PENDING').length
  const viewed = demoApplications.filter(item => item.applyStatus === 'VIEWED').length
  const interview = demoApplications.filter(item => item.applyStatus === 'INTERVIEW').length
  const offer = demoApplications.filter(item => item.applyStatus === 'OFFER').length
  if (role === 'COMPANY') {
    return [
      { label: '收到简历', value: demoApplications.length, trend: '演示总量', desc: '覆盖多个企业和岗位', tone: 'blue', icon: 'solar:document-text-bold-duotone' },
      { label: '待处理投递', value: pending + viewed, trend: '优先筛选', desc: '适合展示企业处理流程', tone: 'orange', icon: 'solar:inbox-line-bold-duotone' },
      { label: '面试推进', value: interview, trend: '邀请面试', desc: '可继续发送面试邀请', tone: 'green', icon: 'solar:calendar-mark-bold-duotone' },
      { label: '录用记录', value: offer, trend: '校招闭环', desc: '展示从投递到录用', tone: 'purple', icon: 'solar:medal-star-bold-duotone' }
    ]
  }
  return [
    { label: '我的投递', value: demoApplications.length, trend: '流程追踪', desc: '可查看完整投递进度', tone: 'blue', icon: 'solar:paper-plane-bold-duotone' },
    { label: '企业已查看', value: viewed, trend: '等待反馈', desc: '企业已进入筛选环节', tone: 'purple', icon: 'solar:eye-bold-duotone' },
    { label: '面试机会', value: interview, trend: '需确认', desc: '建议及时查看面试邀请', tone: 'green', icon: 'solar:microphone-3-bold-duotone' },
    { label: '录用推进', value: offer, trend: '校招结果', desc: '用于展示求职闭环', tone: 'orange', icon: 'solar:cup-star-bold-duotone' }
  ]
})

const rangeStart = computed(() => total.value ? (page.current - 1) * page.size + 1 : 0)
const rangeEnd = computed(() => Math.min(page.current * page.size, total.value))

function statusTag(value) {
  const map = { PENDING: 'info', VIEWED: 'primary', INTERVIEW: 'success', OFFER: 'success', REJECTED: 'danger' }
  return map[value] || 'info'
}

function search() {
  page.current = 1
  load()
}

function reset() {
  status.value = ''
  keyword.value = ''
  search()
}

async function load() {
  loading.value = true
  try {
    const url = role === 'COMPANY' ? '/company/applications' : '/student/applications'
    const res = await request.get(url, { params: { current: page.current, size: page.size, applyStatus: status.value, keyword: keyword.value } })
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
  let list = [...demoApplications]
  if (status.value) list = list.filter(item => item.applyStatus === status.value)
  if (keyword.value) {
    list = list.filter(item => [item.jobName, item.studentName, item.resumeName, item.companyName].some(value => String(value || '').includes(keyword.value)))
  }
  total.value = list.length
  const start = (page.current - 1) * page.size
  rows.value = list.slice(start, start + page.size)
}

function handleSizeChange() {
  page.current = 1
  load()
}

async function change(row, applyStatus) {
  await request.put(`/company/applications/${row.id}/status`, { applyStatus, remark: '企业已处理' })
  ElMessage.success('投递状态已更新')
  load()
}

onMounted(load)
</script>

<style scoped>
.apply-summary {
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
  .apply-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
