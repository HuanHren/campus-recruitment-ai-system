<template>
  <div class="admin-dashboard">
    <section class="dashboard-hero">
      <div class="hero-copy">
        <p class="eyebrow">管理员工作台</p>
        <h1>平台运营总览</h1>
        <p class="hero-desc">
          围绕用户、学生、企业、岗位、简历、投递、面试和 AI 简历润色组织管理入口。当前统计来自后端
          <strong>/api/dashboard/overview</strong> 返回值，若后端未启用则展示稳定空态。
        </p>
      </div>

      <div class="hero-actions">
        <el-tag class="source-tag" effect="plain">演示统计 / 当前后端返回值</el-tag>
        <el-button :loading="loading" type="primary" @click="loadOverview">
          {{ loading ? '刷新中' : '刷新数据' }}
        </el-button>
      </div>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>暂时无法读取仪表盘数据</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="loadOverview">重试</el-button>
    </section>

    <section v-else-if="loading && !hasOverview" class="state-panel">
      <el-skeleton :rows="4" animated />
    </section>

    <section v-else-if="!hasOverview" class="state-panel">
      <div>
        <strong>暂无可展示的后端统计</strong>
        <p>页面已保留管理员入口和业务边界说明，等待后端 dashboard 数据接入后自动展示。</p>
      </div>
      <el-button plain @click="loadOverview">重新加载</el-button>
    </section>

    <template v-else>
      <section class="metric-grid" aria-label="平台概览">
        <article v-for="item in normalizedMetrics" :key="item.key" class="metric-card">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <p>{{ item.note }}</p>
        </article>
      </section>

      <section class="dashboard-section workflow-section">
        <div class="section-heading">
          <p>管理流程</p>
          <h2>从账号到面试的业务闭环</h2>
        </div>
        <div class="workflow-track">
          <article v-for="(item, index) in workflowItems" :key="item.title" class="workflow-item">
            <span class="workflow-index">{{ index + 1 }}</span>
            <div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.desc }}</p>
            </div>
          </article>
        </div>
      </section>

      <section class="dashboard-section">
        <div class="section-heading">
          <p>核心模块</p>
          <h2>管理员常用入口</h2>
        </div>
        <div class="module-grid">
          <article v-for="module in moduleCards" :key="module.title" class="module-card">
            <div class="module-icon">{{ module.initial }}</div>
            <div>
              <h3>{{ module.title }}</h3>
              <p>{{ module.desc }}</p>
            </div>
            <el-button text type="primary" @click="go(module.path)">进入</el-button>
          </article>
        </div>
      </section>

      <section class="dashboard-bottom">
        <article class="info-panel">
          <div class="section-heading">
            <p>后端数据</p>
            <h2>当前返回内容</h2>
          </div>
          <div v-if="activityItems.length" class="activity-list">
            <div v-for="item in activityItems" :key="item.key" class="activity-item">
              <strong>{{ item.title }}</strong>
              <span>{{ item.desc }}</span>
            </div>
          </div>
          <p v-else class="empty-copy">后端暂未提供真实运营动态，页面不展示模拟通知或伪造活动记录。</p>
        </article>

        <article class="info-panel boundary-panel">
          <div class="section-heading">
            <p>实现边界</p>
            <h2>Phase 3 约束说明</h2>
          </div>
          <ul>
            <li>不展示消息通知组件，因为当前没有真实消息 controller/table。</li>
            <li>不展示教师业务能力，因为 teacher 业务 API 尚未实现。</li>
            <li>不展示趋势图、实时在线人数、AI 准确率等无后端依据的数据。</li>
          </ul>
        </article>
      </section>
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getDashboardOverview } from '../../api/dashboard.api'

const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const overview = ref(null)

const metricLabelMap = {
  users: '用户账号',
  userAccounts: '用户账号',
  students: '学生信息',
  companies: '企业信息',
  jobs: '岗位',
  resumes: '简历',
  applications: '投递',
  interviews: '面试',
  aiResumeOptimize: 'AI 简历润色',
  aiOptimizations: 'AI 简历润色'
}

const fallbackMetrics = [
  { key: 'users', label: '用户账号', note: '账号与角色权限管理' },
  { key: 'students', label: '学生信息', note: '学生档案与求职资料' },
  { key: 'companies', label: '企业信息', note: '企业入驻与资质维护' },
  { key: 'jobs', label: '岗位', note: '岗位发布与状态管理' },
  { key: 'applications', label: '投递', note: '学生投递记录管理' },
  { key: 'interviews', label: '面试', note: '面试邀约与流程跟进' }
]

const workflowItems = [
  { title: '账号与角色', desc: '维护管理员、学生、企业等真实登录身份和权限边界。' },
  { title: '学生与企业资料', desc: '支撑招聘双方基础信息审核和资料完善。' },
  { title: '岗位与简历', desc: '围绕岗位发布、简历维护和 AI 简历润色形成求职基础。' },
  { title: '投递与面试', desc: '跟进投递记录、面试安排和招聘流程状态。' }
]

const moduleCards = [
  { title: '用户账号管理', desc: '管理平台账号、角色和基础权限。', path: '/admin', initial: '账' },
  { title: '学生信息管理', desc: '查看和维护学生基础资料。', path: '/student/profile', initial: '学' },
  { title: '企业信息管理', desc: '查看和维护企业入驻资料。', path: '/company/profile', initial: '企' },
  { title: '岗位管理', desc: '处理岗位发布、审核和维护。', path: '/jobs', initial: '岗' },
  { title: '简历管理', desc: '查看学生简历与求职资料。', path: '/resume', initial: '历' },
  { title: '投递管理', desc: '跟踪学生岗位投递记录。', path: '/applications', initial: '投' },
  { title: '面试管理', desc: '管理面试邀约和面试记录。', path: '/interviews', initial: '面' },
  { title: 'AI 简历润色', desc: '使用已有 AI 简历优化能力辅助学生完善简历。', path: '/ai/resume', initial: 'AI' }
]

const hasOverview = computed(() => Boolean(overview.value && Object.keys(overview.value).length))

const normalizedMetrics = computed(() => {
  const metrics = overview.value?.metrics

  if (!metrics || typeof metrics !== 'object' || Array.isArray(metrics)) {
    return fallbackMetrics.map(item => ({ ...item, value: '暂无' }))
  }

  const entries = Object.entries(metrics).filter(([, value]) => value !== null && value !== undefined)
  if (!entries.length) {
    return fallbackMetrics.map(item => ({ ...item, value: '暂无' }))
  }

  return entries.map(([key, value]) => ({
    key,
    label: metricLabelMap[key] || key,
    value,
    note: '演示统计，来自当前后端返回值'
  }))
})

const activityItems = computed(() => {
  const activities = overview.value?.activities
  if (!Array.isArray(activities)) return []

  return activities
    .filter(Boolean)
    .slice(0, 4)
    .map((item, index) => {
      if (typeof item === 'string') {
        return { key: `${index}-${item}`, title: item, desc: '当前后端返回值' }
      }

      return {
        key: item.id || item.title || item.name || index,
        title: item.title || item.name || '后端返回记录',
        desc: item.desc || item.description || item.content || '当前后端返回值'
      }
    })
})

async function loadOverview() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getDashboardOverview('ADMIN')
    overview.value = response?.data || response || null
  } catch (error) {
    overview.value = null
    errorMessage.value = error?.message || 'Dashboard 接口不可用，请确认后端是否提供 /api/dashboard/overview。'
  } finally {
    loading.value = false
  }
}

function go(path) {
  router.push(path)
}

onMounted(loadOverview)
</script>

<style scoped>
.admin-dashboard {
  display: grid;
  gap: 20px;
}

.dashboard-hero,
.state-panel,
.info-panel,
.dashboard-section {
  border: 1px solid #d9e2ec;
  background: #ffffff;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.06);
}

.dashboard-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding: 28px;
  border-radius: 18px;
  background:
    linear-gradient(135deg, rgba(236, 253, 245, 0.86), rgba(255, 255, 255, 0.94) 46%),
    linear-gradient(90deg, #ffffff, #f8fafc);
}

.hero-copy {
  max-width: 760px;
}

.eyebrow,
.section-heading p {
  margin: 0 0 8px;
  color: #047857;
  font-size: 13px;
  font-weight: 700;
}

.dashboard-hero h1,
.section-heading h2 {
  margin: 0;
  color: #102033;
}

.dashboard-hero h1 {
  font-size: 34px;
  line-height: 1.2;
}

.hero-desc {
  margin: 14px 0 0;
  color: #526173;
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.source-tag {
  background: #f0fdf4;
  border-color: #bbf7d0;
  color: #047857;
}

.state-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 22px;
  border-radius: 14px;
}

.state-panel strong {
  color: #102033;
}

.state-panel p,
.empty-copy,
.boundary-panel li {
  margin: 8px 0 0;
  color: #64748b;
  line-height: 1.7;
}

.state-panel--error {
  border-color: #fecaca;
  background: #fff7f7;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.metric-card {
  min-height: 138px;
  padding: 20px;
  border-radius: 16px;
  border: 1px solid #dbe5ef;
  background: linear-gradient(180deg, #ffffff, #f8fafc);
}

.metric-card span {
  color: #526173;
  font-weight: 700;
}

.metric-card strong {
  display: block;
  margin-top: 16px;
  color: #102033;
  font-size: 32px;
  line-height: 1;
}

.metric-card p {
  margin: 14px 0 0;
  color: #7b8794;
  line-height: 1.6;
}

.dashboard-section,
.info-panel {
  padding: 22px;
  border-radius: 16px;
}

.section-heading {
  margin-bottom: 18px;
}

.section-heading h2 {
  font-size: 20px;
}

.workflow-track {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.workflow-item {
  display: flex;
  gap: 14px;
  padding: 16px;
  border-radius: 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.workflow-index {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  flex: 0 0 auto;
  border-radius: 10px;
  background: #0f766e;
  color: #ffffff;
  font-weight: 800;
}

.workflow-item h3,
.module-card h3 {
  margin: 0;
  color: #102033;
  font-size: 16px;
}

.workflow-item p,
.module-card p {
  margin: 8px 0 0;
  color: #64748b;
  line-height: 1.6;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.module-card {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 14px;
  min-height: 118px;
  padding: 16px;
  border: 1px solid #dbe5ef;
  border-radius: 14px;
  background: #ffffff;
}

.module-icon {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #e0f2fe;
  color: #075985;
  font-weight: 800;
}

.dashboard-bottom {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(320px, 0.8fr);
  gap: 16px;
}

.activity-list {
  display: grid;
  gap: 12px;
}

.activity-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px solid #e2e8f0;
}

.activity-item:last-child {
  border-bottom: 0;
}

.activity-item strong {
  color: #102033;
}

.activity-item span {
  color: #64748b;
  text-align: right;
}

.boundary-panel ul {
  padding-left: 18px;
  margin: 0;
}

@media (max-width: 1180px) {
  .metric-grid,
  .module-grid,
  .workflow-track {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .dashboard-bottom {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .dashboard-hero,
  .state-panel {
    align-items: stretch;
    flex-direction: column;
  }

  .dashboard-hero h1 {
    font-size: 28px;
  }

  .hero-actions {
    justify-content: flex-start;
  }

  .metric-grid,
  .module-grid,
  .workflow-track {
    grid-template-columns: 1fr;
  }

  .module-card {
    grid-template-columns: auto 1fr;
  }

  .module-card .el-button {
    grid-column: 1 / -1;
    justify-self: start;
  }

  .activity-item {
    display: grid;
  }

  .activity-item span {
    text-align: left;
  }
}
</style>
