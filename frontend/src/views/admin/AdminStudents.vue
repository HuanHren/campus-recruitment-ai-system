<template>
  <div class="admin-page">
    <section class="admin-page__header">
      <div>
        <p class="eyebrow">ADMIN 管理页</p>
        <h1>学生信息管理</h1>
        <p>读取后端 <strong>GET /api/admin/students</strong>，仅展示真实学生资料列表。</p>
      </div>
      <el-button :loading="loading" type="primary" @click="loadStudents">刷新</el-button>
    </section>

    <section class="admin-panel filter-panel">
      <el-input
        v-model="filters.keyword"
        clearable
        placeholder="姓名 / 学号 / 学校 / 专业"
        @keyup.enter="search"
      />
      <el-button type="primary" @click="search">查询</el-button>
      <el-button plain @click="reset">重置</el-button>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>学生列表加载失败</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="loadStudents">重试</el-button>
    </section>

    <section class="admin-panel table-panel">
      <el-table v-loading="loading" :data="rows" border stripe class="admin-table">
        <template #empty>
          <div class="empty-state">
            <strong>暂无学生信息</strong>
            <p>当前筛选条件下没有后端返回记录。</p>
          </div>
        </template>

        <el-table-column prop="studentNo" label="学号" min-width="130" />
        <el-table-column label="学生" min-width="150">
          <template #default="{ row }">
            <strong class="primary-text">{{ row.name || '-' }}</strong>
            <p class="muted-text">{{ row.username || '-' }}</p>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="90" />
        <el-table-column prop="school" label="学校" min-width="160" />
        <el-table-column prop="college" label="学院" min-width="150" />
        <el-table-column prop="major" label="专业" min-width="150" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column prop="phone" label="手机" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="expectedJob" label="期望岗位" min-width="170" />
        <el-table-column label="更新时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.updatedAt) }}</template>
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
          @current-change="loadStudents"
          @size-change="handleSizeChange"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getAdminStudents } from '../../api/admin.api'

const loading = ref(false)
const errorMessage = ref('')
const rows = ref([])
const total = ref(0)
const page = reactive({ current: 1, size: 10 })
const filters = reactive({ keyword: '' })

function unwrapPage(response) {
  return response?.data || response || {}
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

async function loadStudents() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getAdminStudents({
      current: page.current,
      size: page.size,
      keyword: filters.keyword || undefined
    })
    const data = unwrapPage(response)
    rows.value = Array.isArray(data.records) ? data.records : []
    total.value = Number(data.total || 0)
  } catch (error) {
    rows.value = []
    total.value = 0
    errorMessage.value = error?.message || '请确认后端学生管理接口是否可用。'
  } finally {
    loading.value = false
  }
}

function search() {
  page.current = 1
  loadStudents()
}

function reset() {
  filters.keyword = ''
  search()
}

function handleSizeChange() {
  page.current = 1
  loadStudents()
}

onMounted(loadStudents)
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

  .filter-panel .el-input {
    width: 100%;
  }
}
</style>
