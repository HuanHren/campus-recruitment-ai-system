<template>
  <div class="auth-entry">
    <section class="auth-story">
      <div class="auth-brand">
        <span class="auth-brand__mark"><Icon icon="solar:case-round-bold-duotone" /></span>
        <div>
          <strong>AI 校园招聘系统</strong>
          <small>Spring Boot + Vue 毕业设计</small>
        </div>
      </div>

      <div class="auth-story__content">
        <span class="auth-kicker"><Icon icon="solar:stars-bold-duotone" /> 真实角色工作台</span>
        <h1>登录后进入对应的招聘流程</h1>
        <p>
          管理员审核企业和岗位，学生维护简历并投递岗位，企业发布岗位并处理投递。教师模块当前仅保留占位入口。
        </p>
      </div>

      <div class="workflow-list" aria-label="核心流程">
        <div v-for="item in flowSteps" :key="item.title" class="workflow-item">
          <Icon :icon="item.icon" />
          <span>{{ item.title }}</span>
        </div>
      </div>
    </section>

    <section class="auth-form-panel">
      <el-form ref="formRef" :model="form" :rules="rules" class="auth-card" label-position="top" @keyup.enter="submit">
        <div class="form-heading">
          <span class="form-heading__icon"><Icon icon="solar:user-check-rounded-bold-duotone" /></span>
          <div>
            <h2>进入工作台</h2>
            <p>使用示例账号快速体验对应角色权限。</p>
          </div>
        </div>

        <div class="role-grid" aria-label="示例账号">
          <button
            v-for="item in quickUsers"
            :key="item.username"
            type="button"
            class="role-card"
            :class="{ active: form.username === item.username }"
            @click="fill(item)"
          >
            <Icon :icon="item.icon" />
            <span>{{ item.label }}</span>
            <small>{{ item.desc }}</small>
          </button>
        </div>

        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" size="large" placeholder="请输入用户名" autocomplete="username">
            <template #prefix><Icon icon="solar:user-rounded-bold-duotone" /></template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            size="large"
            placeholder="请输入密码"
            show-password
            autocomplete="current-password"
          >
            <template #prefix><Icon icon="solar:lock-password-bold-duotone" /></template>
          </el-input>
        </el-form-item>

        <el-alert
          v-if="form.username === 'teacher'"
          class="auth-note"
          title="教师角色当前仅有占位入口，后端暂无教师业务 API。"
          type="warning"
          :closable="false"
          show-icon
        />

        <el-button type="primary" size="large" class="submit-button" :loading="loading" @click="submit">
          登录系统
        </el-button>

        <div class="auth-footer-link">
          还没有账号？
          <router-link to="/register">注册学生、企业或教师账号</router-link>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Icon } from '@iconify/vue'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '123456' })

const quickUsers = [
  { label: '管理员', username: 'admin', desc: '审核企业与岗位', icon: 'solar:shield-user-bold-duotone' },
  { label: '学生', username: 'student', desc: '简历与投递流程', icon: 'solar:graduation-cap-bold-duotone' },
  { label: '企业', username: 'company', desc: '岗位与面试管理', icon: 'solar:buildings-2-bold-duotone' },
  { label: '教师', username: 'teacher', desc: '占位入口', icon: 'solar:users-group-rounded-bold-duotone' }
]

const flowSteps = [
  { title: '岗位发布', icon: 'solar:case-round-bold-duotone' },
  { title: '简历投递', icon: 'solar:paper-plane-bold-duotone' },
  { title: 'AI 简历优化', icon: 'solar:stars-bold-duotone' },
  { title: '面试邀约', icon: 'solar:calendar-mark-bold-duotone' }
]

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function fill(item) {
  form.username = item.username
  form.password = '123456'
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const path = await auth.login(form)
    ElMessage.success('登录成功')
    router.push(path)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-entry {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(420px, 0.92fr);
  padding: 28px;
  background: linear-gradient(135deg, #eaf2ff 0%, #f8fafc 50%, #eef6ff 100%);
}

.auth-story,
.auth-form-panel {
  min-height: calc(100vh - 56px);
}

.auth-story {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 40px;
  border-radius: 18px;
  color: #fff;
  background: linear-gradient(135deg, #0f172a 0%, #1d4ed8 100%);
}

.auth-brand,
.form-heading {
  display: flex;
  align-items: center;
  gap: 12px;
}

.auth-brand__mark,
.form-heading__icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
}

.auth-brand__mark {
  color: #1d4ed8;
  background: #fff;
}

.auth-brand strong,
.auth-brand small {
  display: block;
}

.auth-brand small {
  margin-top: 4px;
  color: rgba(255, 255, 255, 0.72);
  font-size: 12px;
  font-weight: 700;
}

.auth-story__content {
  max-width: 720px;
}

.auth-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 999px;
  color: #dbeafe;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.22);
  font-size: 13px;
  font-weight: 800;
}

.auth-story h1 {
  max-width: 680px;
  margin: 22px 0 0;
  font-size: clamp(34px, 5vw, 60px);
  line-height: 1.08;
  letter-spacing: 0;
  text-wrap: balance;
}

.auth-story p {
  max-width: 640px;
  margin: 22px 0 0;
  color: rgba(255, 255, 255, 0.82);
  font-size: 17px;
  line-height: 1.85;
}

.workflow-list {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.workflow-item {
  min-height: 92px;
  padding: 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.18);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  font-weight: 800;
}

.workflow-item svg {
  font-size: 24px;
}

.auth-form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.auth-card {
  width: min(480px, 100%);
  padding: 32px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid var(--color-line);
  box-shadow: var(--card-shadow);
}

.form-heading {
  margin-bottom: 22px;
}

.form-heading__icon {
  color: #fff;
  background: var(--color-primary);
}

.form-heading h2 {
  margin: 0;
  color: var(--color-title);
  font-size: 28px;
  letter-spacing: 0;
}

.form-heading p {
  margin: 5px 0 0;
  color: var(--color-muted);
  line-height: 1.6;
}

.role-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 22px;
}

.role-card {
  min-height: 88px;
  padding: 13px;
  border: 1px solid var(--color-line);
  border-radius: 14px;
  background: #f8fafc;
  color: var(--color-text);
  text-align: left;
  cursor: pointer;
  transition: transform var(--fast-transition), border-color var(--fast-transition), background-color var(--fast-transition), box-shadow var(--fast-transition);
}

.role-card svg,
.role-card span,
.role-card small {
  display: block;
}

.role-card svg {
  margin-bottom: 8px;
  color: var(--color-primary);
  font-size: 22px;
}

.role-card span {
  color: var(--color-title);
  font-weight: 900;
}

.role-card small {
  margin-top: 3px;
  color: var(--color-muted);
  font-weight: 700;
}

.role-card:hover,
.role-card.active {
  transform: translateY(-1px);
  border-color: #93c5fd;
  background: #fff;
  box-shadow: var(--soft-shadow);
}

.auth-note {
  margin-bottom: 16px;
}

.submit-button {
  width: 100%;
  height: 46px;
}

.auth-footer-link {
  margin-top: 18px;
  text-align: center;
  color: var(--color-muted);
}

.auth-footer-link a {
  color: var(--color-primary);
  font-weight: 800;
}

@media (max-width: 1080px) {
  .auth-entry {
    grid-template-columns: 1fr;
  }

  .auth-story,
  .auth-form-panel {
    min-height: auto;
  }

  .auth-story {
    gap: 34px;
  }
}

@media (max-width: 640px) {
  .auth-entry {
    padding: 14px;
  }

  .auth-story,
  .auth-card {
    padding: 24px;
  }

  .auth-form-panel {
    padding: 16px 0 0;
  }

  .workflow-list,
  .role-grid {
    grid-template-columns: 1fr;
  }
}
</style>
