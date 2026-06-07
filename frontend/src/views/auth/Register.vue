<template>
  <div class="auth-entry">
    <section class="auth-story">
      <div class="auth-brand">
        <span class="auth-brand__mark"><el-icon><UserFilled /></el-icon></span>
        <div>
          <strong>创建校园招聘账号</strong>
          <small>学生、企业和教师入口</small>
        </div>
      </div>

      <div class="auth-story__content">
        <span class="auth-kicker"><el-icon><MagicStick /></el-icon> 注册后从登录页进入系统</span>
        <h1>选择真实角色，进入对应权限范围</h1>
        <p>
          注册接口支持学生、企业和教师。管理员账号由系统初始化，教师业务模块当前仅保留占位入口。
        </p>
      </div>

      <div class="role-summary" aria-label="注册角色说明">
        <div v-for="item in roleOptions" :key="item.value" class="role-summary__item">
          <el-icon><component :is="item.icon" /></el-icon>
          <strong>{{ item.label }}</strong>
          <span>{{ item.desc }}</span>
        </div>
      </div>
    </section>

    <section class="auth-form-panel">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="auth-card">
        <div class="form-heading">
          <span class="form-heading__icon"><el-icon><UserFilled /></el-icon></span>
          <div>
            <h2>注册新用户</h2>
            <p>提交后返回登录页，不会自动登录。</p>
          </div>
        </div>

        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" size="large" placeholder="3 到 50 个字符" autocomplete="username" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            size="large"
            placeholder="6 到 30 个字符"
            show-password
            autocomplete="new-password"
          />
        </el-form-item>

        <el-form-item label="真实姓名或企业联系人" prop="realName">
          <el-input v-model="form.realName" size="large" placeholder="请输入真实名称" />
        </el-form-item>

        <el-form-item label="注册角色" prop="role">
          <div class="register-role-grid">
            <button
              v-for="item in roleOptions"
              :key="item.value"
              type="button"
              class="register-role-card"
              :class="{ active: form.role === item.value }"
              @click="form.role = item.value"
            >
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.label }}</span>
              <small>{{ item.desc }}</small>
            </button>
          </div>
        </el-form-item>

        <el-alert
          v-if="form.role === 'TEACHER'"
          class="auth-note"
          title="教师角色可注册，但后端暂无教师业务 API，登录后仅进入占位页。"
          type="warning"
          :closable="false"
          show-icon
        />

        <div class="optional-grid">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" size="large" placeholder="选填" autocomplete="tel" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" size="large" placeholder="选填" autocomplete="email" />
          </el-form-item>
        </div>

        <el-button type="primary" size="large" class="submit-button" :loading="loading" @click="submit">
          注册账号
        </el-button>

        <div class="auth-footer-link">
          已有账号？
          <router-link to="/login">返回登录</router-link>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataAnalysis, MagicStick, OfficeBuilding, UserFilled } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  realName: '',
  role: 'STUDENT',
  phone: '',
  email: ''
})

const roleOptions = [
  { label: '学生', value: 'STUDENT', desc: '维护简历并投递岗位', icon: UserFilled },
  { label: '企业', value: 'COMPANY', desc: '发布岗位并处理投递', icon: OfficeBuilding },
  { label: '教师', value: 'TEACHER', desc: '模块待建设', icon: DataAnalysis }
]

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度为 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度为 6 到 30 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实名称', trigger: 'blur' },
    { max: 50, message: '真实名称不能超过 50 个字符', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择注册角色', trigger: 'change' }],
  phone: [{ max: 20, message: '手机号不能超过 20 个字符', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await auth.register({
      username: form.username,
      password: form.password,
      realName: form.realName,
      role: form.role,
      phone: form.phone || undefined,
      email: form.email || undefined
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-entry {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1.02fr) minmax(460px, 0.98fr);
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
  max-width: 680px;
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
  max-width: 660px;
  margin: 22px 0 0;
  font-size: clamp(34px, 5vw, 58px);
  line-height: 1.08;
  letter-spacing: 0;
  text-wrap: balance;
}

.auth-story p {
  max-width: 620px;
  margin: 22px 0 0;
  color: rgba(255, 255, 255, 0.82);
  font-size: 17px;
  line-height: 1.85;
}

.role-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.role-summary__item {
  min-height: 124px;
  padding: 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.role-summary__item .el-icon {
  font-size: 24px;
}

.role-summary__item strong,
.role-summary__item span {
  display: block;
}

.role-summary__item strong {
  margin-top: 22px;
}

.role-summary__item span {
  margin-top: 6px;
  color: rgba(255, 255, 255, 0.74);
  line-height: 1.55;
}

.auth-form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.auth-card {
  width: min(520px, 100%);
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

.register-role-grid {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.register-role-card {
  min-height: 94px;
  padding: 13px;
  border: 1px solid var(--color-line);
  border-radius: 14px;
  background: #f8fafc;
  color: var(--color-text);
  text-align: left;
  cursor: pointer;
  transition: transform var(--fast-transition), border-color var(--fast-transition), background-color var(--fast-transition), box-shadow var(--fast-transition);
}

.register-role-card .el-icon,
.register-role-card span,
.register-role-card small {
  display: block;
}

.register-role-card .el-icon {
  margin-bottom: 8px;
  color: var(--color-primary);
  font-size: 22px;
}

.register-role-card span {
  color: var(--color-title);
  font-weight: 900;
}

.register-role-card small {
  margin-top: 3px;
  color: var(--color-muted);
  font-weight: 700;
}

.register-role-card:hover,
.register-role-card.active {
  transform: translateY(-1px);
  border-color: #93c5fd;
  background: #fff;
  box-shadow: var(--soft-shadow);
}

.optional-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
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

  .role-summary,
  .register-role-grid,
  .optional-grid {
    grid-template-columns: 1fr;
  }
}
</style>
