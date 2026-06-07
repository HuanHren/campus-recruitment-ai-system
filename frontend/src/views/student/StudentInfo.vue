<template>
  <div class="student-form-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">学生资料</p>
        <h1>完善个人基础信息</h1>
        <p class="hero-desc">
          本页使用 <strong>GET / POST / PUT /api/student/info</strong>，资料会作为简历、投递和面试流程的基础信息。
        </p>
      </div>
      <div class="hero-actions">
        <el-tag :type="exists ? 'success' : 'warning'" effect="plain">
          {{ exists ? '已创建资料' : '新资料待创建' }}
        </el-tag>
        <el-button plain @click="$router.push('/resume')">前往简历管理</el-button>
      </div>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>资料读取失败</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="loadProfile">重试</el-button>
    </section>

    <section v-else-if="loading" class="state-panel">
      <el-skeleton :rows="5" animated />
    </section>

    <section v-else-if="!exists" class="state-panel state-panel--new">
      <div>
        <strong>还没有学生资料</strong>
        <p>请填写下方表单并保存，系统会调用创建接口写入第一份学生基础资料。</p>
      </div>
    </section>

    <el-form class="form-shell" :model="form" label-position="top">
      <section class="form-section">
        <div class="section-head">
          <span>01</span>
          <div>
            <h2>学籍信息</h2>
            <p>用于识别学生身份，字段与后端 StudentInfoRequest 保持一致。</p>
          </div>
        </div>
        <div class="form-grid">
          <el-form-item label="学号">
            <el-input v-model="form.studentNo" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender" clearable placeholder="请选择">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="学校">
            <el-input v-model="form.school" placeholder="请输入学校" />
          </el-form-item>
          <el-form-item label="学院">
            <el-input v-model="form.college" placeholder="请输入学院" />
          </el-form-item>
          <el-form-item label="专业">
            <el-input v-model="form.major" placeholder="请输入专业" />
          </el-form-item>
          <el-form-item label="年级">
            <el-input v-model="form.grade" placeholder="例如 2023 级" />
          </el-form-item>
          <el-form-item label="学历">
            <el-input v-model="form.education" placeholder="例如 本科" />
          </el-form-item>
        </div>
      </section>

      <section class="form-section">
        <div class="section-head">
          <span>02</span>
          <div>
            <h2>联系方式与求职方向</h2>
            <p>企业查看投递和发送面试邀请时会参考这些信息。</p>
          </div>
        </div>
        <div class="form-grid">
          <el-form-item label="手机">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="期望岗位">
            <el-input v-model="form.expectedJob" placeholder="例如 Java 开发工程师" />
          </el-form-item>
          <el-form-item class="form-grid__full" label="个人简介">
            <el-input v-model="form.selfIntro" type="textarea" :rows="5" maxlength="1000" show-word-limit />
          </el-form-item>
        </div>
      </section>

      <section class="action-panel">
        <div>
          <strong>{{ exists ? '保存后将更新当前资料' : '保存后将创建学生资料' }}</strong>
          <p>不会调用编辑/删除以外的任何学生管理接口。</p>
        </div>
        <el-button type="primary" :loading="saving" @click="saveProfile">
          {{ saving ? '保存中' : '保存资料' }}
        </el-button>
      </section>
    </el-form>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { createStudentInfo, getStudentInfo, updateStudentInfo } from '../../api/student.api'

const loading = ref(false)
const saving = ref(false)
const exists = ref(false)
const errorMessage = ref('')

const emptyForm = {
  studentNo: '',
  name: '',
  gender: '',
  school: '',
  college: '',
  major: '',
  grade: '',
  education: '',
  phone: '',
  email: '',
  expectedJob: '',
  selfIntro: ''
}

const form = reactive({ ...emptyForm })

function normalizePayload(data = form) {
  return {
    studentNo: data.studentNo,
    name: data.name,
    gender: data.gender,
    school: data.school,
    college: data.college,
    major: data.major,
    grade: data.grade,
    education: data.education,
    phone: data.phone,
    email: data.email,
    expectedJob: data.expectedJob,
    selfIntro: data.selfIntro
  }
}

function applyProfile(data) {
  Object.assign(form, emptyForm, normalizePayload(data))
}

async function loadProfile() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getStudentInfo()
    const data = response?.data || response
    if (data && Object.keys(data).length) {
      applyProfile(data)
      exists.value = true
    } else {
      Object.assign(form, emptyForm)
      exists.value = false
    }
  } catch (error) {
    Object.assign(form, emptyForm)
    exists.value = false
    if (error?.code && error.code !== 200) {
      errorMessage.value = error.message || '暂未读取到学生资料，请填写后创建。'
    }
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  saving.value = true

  try {
    const payload = normalizePayload()
    const response = exists.value ? await updateStudentInfo(payload) : await createStudentInfo(payload)
    const data = response?.data || response
    if (data && Object.keys(data).length) {
      applyProfile(data)
    }
    exists.value = true
    ElMessage.success('学生资料已保存')
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.student-form-page {
  display: grid;
  gap: 18px;
}

.page-hero,
.form-section,
.state-panel,
.action-panel {
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
h2 {
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
.action-panel p {
  margin: 8px 0 0;
  color: #64748b;
  line-height: 1.7;
}

.hero-actions {
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

.form-shell {
  display: grid;
  gap: 18px;
}

.form-section {
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

.form-grid__full {
  grid-column: 1 / -1;
}

.action-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 20px;
}

.action-panel strong {
  color: #102033;
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

  .hero-actions {
    justify-content: flex-start;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
