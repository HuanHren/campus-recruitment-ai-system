<template>
  <div class="resume-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">简历管理</p>
        <h1>维护投递使用的基础简历</h1>
        <p class="hero-desc">
          本页使用 <strong>GET / POST / PUT /api/student/resume</strong>，所有字段与后端 ResumeInfoRequest 保持一致。
        </p>
      </div>
      <div class="hero-actions">
        <el-tag :type="exists ? 'success' : 'warning'" effect="plain">
          {{ exists ? '已创建简历' : '新简历待创建' }}
        </el-tag>
        <el-button plain @click="$router.push('/student-info')">学生资料</el-button>
        <el-button plain @click="$router.push('/jobs')">浏览岗位</el-button>
      </div>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>简历读取失败</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="loadResume">重试</el-button>
    </section>

    <section v-else-if="loading" class="state-panel">
      <el-skeleton :rows="5" animated />
    </section>

    <section v-else-if="!exists" class="state-panel state-panel--new">
      <div>
        <strong>还没有简历</strong>
        <p>请填写下方表单并保存，系统会调用创建接口写入第一份学生简历。</p>
      </div>
    </section>

    <section class="resume-layout">
      <el-form class="form-shell" :model="form" label-position="top">
        <section class="form-section">
          <div class="section-head">
            <span>01</span>
            <div>
              <h2>基础信息</h2>
              <p>用于简历标题、身份识别和企业联系。</p>
            </div>
          </div>
          <div class="form-grid">
            <el-form-item label="简历名称">
              <el-input v-model="form.resumeName" placeholder="请输入简历名称" />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="form.realName" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="form.gender" clearable placeholder="请选择">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </div>
        </section>

        <section class="form-section">
          <div class="section-head">
            <span>02</span>
            <div>
              <h2>教育背景与求职意向</h2>
              <p>岗位浏览、投递和 AI 简历润色都会参考这些字段。</p>
            </div>
          </div>
          <div class="form-grid">
            <el-form-item label="学校">
              <el-input v-model="form.school" placeholder="请输入学校" />
            </el-form-item>
            <el-form-item label="专业">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
            <el-form-item label="学历">
              <el-input v-model="form.education" placeholder="例如 本科" />
            </el-form-item>
            <el-form-item label="毕业年份">
              <el-input v-model="form.graduationYear" placeholder="例如 2026" />
            </el-form-item>
            <el-form-item label="期望岗位">
              <el-input v-model="form.expectedPosition" placeholder="请输入期望岗位" />
            </el-form-item>
            <el-form-item label="期望城市">
              <el-input v-model="form.expectedCity" placeholder="请输入期望城市" />
            </el-form-item>
          </div>
        </section>

        <section class="form-section">
          <div class="section-head">
            <span>03</span>
            <div>
              <h2>能力与经历</h2>
              <p>建议使用清晰的项目背景、职责、技术栈和结果描述。</p>
            </div>
          </div>
          <el-form-item label="专业技能">
            <el-input v-model="form.skills" type="textarea" :rows="4" maxlength="1000" show-word-limit />
          </el-form-item>
          <el-form-item label="项目经历">
            <el-input v-model="form.projectExperience" type="textarea" :rows="5" maxlength="2000" show-word-limit />
          </el-form-item>
          <el-form-item label="实习经历">
            <el-input v-model="form.internshipExperience" type="textarea" :rows="5" maxlength="2000" show-word-limit />
          </el-form-item>
          <el-form-item label="自我评价">
            <el-input v-model="form.selfEvaluation" type="textarea" :rows="4" maxlength="1000" show-word-limit />
          </el-form-item>
        </section>

        <section class="action-panel">
          <div>
            <strong>{{ exists ? '保存后将更新当前简历' : '保存后将创建简历' }}</strong>
            <p>AI 润色页面会读取你在这里整理出的真实简历内容。</p>
          </div>
          <div class="action-buttons">
            <el-button type="primary" :loading="saving" @click="saveResume">
              {{ saving ? '保存中' : '保存简历' }}
            </el-button>
            <el-button plain @click="$router.push('/ai-resume')">AI 简历润色</el-button>
          </div>
        </section>
      </el-form>

      <aside class="resume-aside">
        <section class="aside-card">
          <p class="eyebrow">当前表单</p>
          <h2>{{ completion }}%</h2>
          <el-progress :percentage="completion" :stroke-width="10" />
          <div class="check-list">
            <div v-for="item in checkItems" :key="item.label" class="check-item">
              <span>{{ item.label }}</span>
              <el-tag :type="item.done ? 'success' : 'info'" effect="plain">
                {{ item.done ? '已填写' : '待完善' }}
              </el-tag>
            </div>
          </div>
        </section>

        <section class="aside-card">
          <p class="eyebrow">下一步</p>
          <h3>完善后进入 AI 简历润色</h3>
          <p class="aside-copy">AI 页面会基于真实输入生成润色结果，不会在本页生成模拟内容。</p>
          <el-button type="primary" plain @click="$router.push('/ai-resume')">前往 AI 润色</el-button>
        </section>
      </aside>
    </section>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { createStudentResume, getStudentResume, updateStudentResume } from '../../api/student.api'

const loading = ref(false)
const saving = ref(false)
const exists = ref(false)
const errorMessage = ref('')

const emptyForm = {
  resumeName: '',
  realName: '',
  gender: '',
  phone: '',
  email: '',
  school: '',
  major: '',
  education: '',
  graduationYear: '',
  expectedPosition: '',
  expectedCity: '',
  skills: '',
  projectExperience: '',
  internshipExperience: '',
  selfEvaluation: ''
}

const form = reactive({ ...emptyForm })

const checkItems = computed(() => [
  { label: '基础身份信息', done: Boolean(form.resumeName && form.realName) },
  { label: '联系方式', done: Boolean(form.phone && form.email) },
  { label: '教育背景', done: Boolean(form.school && form.major && form.education) },
  { label: '求职意向', done: Boolean(form.expectedPosition && form.expectedCity) },
  { label: '技能与项目', done: Boolean(form.skills && form.projectExperience) }
])

const completion = computed(() => Math.round((checkItems.value.filter(item => item.done).length / checkItems.value.length) * 100))

function normalizePayload(data = form) {
  return {
    resumeName: data.resumeName,
    realName: data.realName,
    gender: data.gender,
    phone: data.phone,
    email: data.email,
    school: data.school,
    major: data.major,
    education: data.education,
    graduationYear: data.graduationYear,
    expectedPosition: data.expectedPosition,
    expectedCity: data.expectedCity,
    skills: data.skills,
    projectExperience: data.projectExperience,
    internshipExperience: data.internshipExperience,
    selfEvaluation: data.selfEvaluation
  }
}

function applyResume(data) {
  Object.assign(form, emptyForm, normalizePayload(data))
}

async function loadResume() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getStudentResume()
    const data = response?.data || response
    if (data && Object.keys(data).length) {
      applyResume(data)
      exists.value = true
    } else {
      Object.assign(form, emptyForm)
      exists.value = false
    }
  } catch (error) {
    Object.assign(form, emptyForm)
    exists.value = false
    if (error?.code && error.code !== 200) {
      errorMessage.value = error.message || '暂未读取到简历，请填写后创建。'
    }
  } finally {
    loading.value = false
  }
}

async function saveResume() {
  saving.value = true

  try {
    const payload = normalizePayload()
    const response = exists.value ? await updateStudentResume(payload) : await createStudentResume(payload)
    const data = response?.data || response
    if (data && Object.keys(data).length) {
      applyResume(data)
    }
    exists.value = true
    ElMessage.success('简历已保存')
  } finally {
    saving.value = false
  }
}

onMounted(loadResume)
</script>

<style scoped>
.resume-page {
  display: grid;
  gap: 18px;
}

.page-hero,
.form-section,
.state-panel,
.action-panel,
.aside-card {
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

h2 {
  font-size: 18px;
}

.hero-desc,
.section-head p,
.state-panel p,
.action-panel p,
.aside-copy {
  margin: 8px 0 0;
  color: #64748b;
  line-height: 1.7;
}

.hero-actions,
.action-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.state-panel {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 18px;
}

.state-panel--error {
  border-color: #fecaca;
  background: #fff7f7;
}

.state-panel--new {
  background: #f8fafc;
}

.resume-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 18px;
}

.form-shell,
.resume-aside {
  display: grid;
  align-content: start;
  gap: 18px;
}

.form-section,
.action-panel,
.aside-card {
  padding: 22px;
}

.section-head {
  display: flex;
  gap: 12px;
  margin-bottom: 18px;
}

.section-head span {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  flex: 0 0 auto;
  border-radius: 10px;
  background: #ecfdf5;
  color: #047857;
  font-weight: 900;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 4px 16px;
}

.action-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.action-panel strong {
  color: #102033;
}

.aside-card h2 {
  margin-bottom: 12px;
  font-size: 36px;
}

.check-list {
  display: grid;
  gap: 10px;
  margin-top: 18px;
}

.check-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #334155;
}

.aside-card .el-button {
  margin-top: 16px;
}

@media (max-width: 1180px) {
  .resume-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 980px) {
  .form-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .page-hero,
  .state-panel,
  .action-panel {
    align-items: stretch;
    flex-direction: column;
  }

  .hero-actions,
  .action-buttons {
    justify-content: flex-start;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
