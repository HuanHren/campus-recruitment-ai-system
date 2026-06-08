<template>
  <div class="company-profile-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">企业资料认证</p>
        <h1>维护企业资料</h1>
        <p class="hero-desc">
          本页只使用企业资料接口：<strong>GET /api/company/info</strong>、<strong>POST /api/company/info</strong>
          和 <strong>PUT /api/company/info</strong>。资料审核通过后，后端才允许发布岗位。
        </p>
      </div>
      <div class="status-card">
        <span>审核状态</span>
        <strong :class="statusClass(auditStatus)">{{ auditStatusText(auditStatus) }}</strong>
        <p>{{ auditRemark || '暂无审核备注' }}</p>
      </div>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>企业资料加载失败</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="loadProfile">重试</el-button>
    </section>

    <section class="profile-shell">
      <el-skeleton v-if="loading" :rows="8" animated />
      <template v-else>
        <div v-if="!exists" class="new-state">
          <strong>尚未创建企业资料</strong>
          <p>请填写企业名称和联系人信息后提交，后续由管理员审核。</p>
        </div>

        <el-form :model="form" label-position="top" class="profile-form">
          <section class="form-section">
            <div class="section-title">
              <span>01</span>
              <div>
                <h2>企业基础信息</h2>
                <p>用于后台审核、岗位展示和学生识别。</p>
              </div>
            </div>
            <div class="form-grid">
              <el-form-item label="企业名称">
                <el-input v-model="form.companyName" maxlength="100" show-word-limit />
              </el-form-item>
              <el-form-item label="统一社会信用代码">
                <el-input v-model="form.unifiedSocialCreditCode" maxlength="50" show-word-limit />
              </el-form-item>
              <el-form-item label="所属行业">
                <el-input v-model="form.industry" maxlength="100" show-word-limit />
              </el-form-item>
              <el-form-item label="企业规模">
                <el-input v-model="form.companyScale" maxlength="50" show-word-limit />
              </el-form-item>
              <el-form-item class="wide-field" label="企业地址">
                <el-input v-model="form.address" maxlength="255" show-word-limit />
              </el-form-item>
            </div>
          </section>

          <section class="form-section">
            <div class="section-title">
              <span>02</span>
              <div>
                <h2>联系人与介绍</h2>
                <p>用于投递处理、面试邀约和企业资料审核。</p>
              </div>
            </div>
            <div class="form-grid">
              <el-form-item label="联系人">
                <el-input v-model="form.contactPerson" maxlength="50" show-word-limit />
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model="form.contactPhone" maxlength="20" show-word-limit />
              </el-form-item>
              <el-form-item label="联系邮箱">
                <el-input v-model="form.contactEmail" maxlength="100" show-word-limit />
              </el-form-item>
              <el-form-item class="wide-field" label="企业简介">
                <el-input v-model="form.description" type="textarea" :rows="5" maxlength="1500" show-word-limit />
              </el-form-item>
            </div>
          </section>

          <div class="form-actions">
            <el-button type="primary" :loading="saving" @click="saveProfile">
              {{ exists ? '保存企业资料' : '提交企业资料' }}
            </el-button>
            <el-button plain @click="$router.push('/company/jobs')">进入岗位管理</el-button>
          </div>
        </el-form>
      </template>
    </section>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { createCompanyInfo, getCompanyInfo, updateCompanyInfo } from '../../api/company.api'

const profileId = ref(null)
const exists = ref(false)
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const auditStatus = ref('')
const auditRemark = ref('')

const form = reactive({
  companyName: '',
  unifiedSocialCreditCode: '',
  industry: '',
  companyScale: '',
  address: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  description: ''
})

const requestFields = Object.keys(form)

function unwrapData(response) {
  return response?.data || response || null
}

function fillForm(data) {
  requestFields.forEach((field) => {
    form[field] = data?.[field] || ''
  })
  profileId.value = data?.id || null
  auditStatus.value = data?.auditStatus || ''
  auditRemark.value = data?.auditRemark || ''
  exists.value = Boolean(data?.id)
}

function buildPayload() {
  return requestFields.reduce((payload, field) => {
    payload[field] = form[field]
    return payload
  }, {})
}

function auditStatusText(status) {
  const map = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回' }
  return map[status] || '未提交'
}

function statusClass(status) {
  return {
    'status-pending': status === 'PENDING',
    'status-approved': status === 'APPROVED',
    'status-rejected': status === 'REJECTED',
    'status-empty': !status
  }
}

async function loadProfile() {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await getCompanyInfo()
    const data = unwrapData(response)
    if (data?.id) {
      fillForm(data)
    } else {
      fillForm(null)
    }
  } catch (error) {
    fillForm(null)
    const status = error?.response?.status || error?.code
    if (status === 404) {
      errorMessage.value = ''
    } else {
      errorMessage.value = error?.message || '请确认企业资料接口是否可用。'
    }
  } finally {
    loading.value = false
  }
}

async function saveProfile() {
  saving.value = true

  try {
    const response = exists.value ? await updateCompanyInfo(buildPayload()) : await createCompanyInfo(buildPayload())
    const data = unwrapData(response)
    fillForm(data)
    ElMessage.success(exists.value ? '企业资料已保存' : '企业资料已提交')
  } catch (error) {
    ElMessage.error(error?.message || '企业资料保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.company-profile-page {
  display: grid;
  gap: 20px;
  color: #172033;
}

.page-hero,
.profile-shell {
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
  max-width: 780px;
  margin: 12px 0 0;
  color: #536174;
  line-height: 1.7;
}

.status-card {
  min-width: 220px;
  padding: 16px;
  border-radius: 14px;
  border: 1px solid #d8e0ec;
  background: #ffffff;
}

.status-card span {
  color: #657286;
  font-size: 13px;
}

.status-card strong {
  display: block;
  margin-top: 8px;
  font-size: 20px;
}

.status-card p {
  margin: 8px 0 0;
  color: #657286;
  line-height: 1.5;
}

.status-pending {
  color: #8a5c00;
}

.status-approved {
  color: #1d6f46;
}

.status-rejected {
  color: #9c2c2c;
}

.status-empty {
  color: #536174;
}

.state-panel,
.new-state {
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

.state-panel p,
.new-state p,
.section-title p {
  margin: 6px 0 0;
  color: #657286;
  line-height: 1.6;
}

.profile-shell {
  padding: 22px;
}

.new-state {
  margin-bottom: 18px;
  border: 1px dashed #cbd6e5;
  background: #f8fafc;
}

.profile-form {
  display: grid;
  gap: 18px;
}

.form-section {
  padding: 18px;
  border: 1px solid #e2e8f2;
  border-radius: 14px;
  background: #fbfcfe;
}

.section-title {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.section-title span {
  display: inline-grid;
  place-items: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #e1f1ed;
  color: #1d6357;
  font-weight: 800;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 4px 18px;
}

.wide-field {
  grid-column: 1 / -1;
}

.form-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

@media (max-width: 900px) {
  .page-hero,
  .state-panel,
  .new-state {
    flex-direction: column;
    align-items: stretch;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
