<template>
  <div class="auth-shell">
    <section class="auth-left">
      <div class="brand-row">
        <span class="brand-mark"><el-icon><UserFilled /></el-icon></span>
        <div>
          <div>加入 AI 校园招聘系统</div>
          <small style="color: var(--muted); font-weight: 700;">统一账号 · 分角色工作台</small>
        </div>
      </div>
      <div class="auth-title">
        <span class="ai-chip"><el-icon><MagicStick /></el-icon> 智能招聘业务闭环</span>
        <h1>一份账号，连接学生求职与企业招聘</h1>
        <p>学生完善简历并获取 AI 推荐，企业发布岗位并筛选候选人，就业老师和管理员掌握招聘服务全过程。</p>
      </div>
      <div class="auth-highlights">
        <div class="auth-highlight">
          <el-icon class="icon-pill soft"><UserFilled /></el-icon>
          <strong>学生端</strong>
          <span>简历、投递、面试邀请与 AI 匹配</span>
        </div>
        <div class="auth-highlight">
          <el-icon class="icon-pill soft"><OfficeBuilding /></el-icon>
          <strong>企业端</strong>
          <span>岗位发布、简历筛选与面试安排</span>
        </div>
        <div class="auth-highlight">
          <el-icon class="icon-pill soft"><DataAnalysis /></el-icon>
          <strong>管理端</strong>
          <span>企业审核、岗位审核与数据统计</span>
        </div>
        <div class="auth-highlight">
          <el-icon class="icon-pill soft"><MagicStick /></el-icon>
          <strong>AI 能力</strong>
          <span>简历优化、岗位匹配和面试题生成</span>
        </div>
      </div>
    </section>

    <section class="auth-panel">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="auth-card">
        <div class="brand-row">
          <span class="brand-mark"><el-icon><UserFilled /></el-icon></span>
          <div>
            <div>创建账号</div>
            <small style="color:var(--muted);font-weight:700;">管理员账号由系统初始化</small>
          </div>
        </div>
        <h2>注册新用户</h2>
        <p class="auth-sub">注册支持学生、企业和就业老师，注册后会进入对应角色工作台。</p>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" size="large" show-password />
        </el-form-item>
        <el-form-item label="真实名称" prop="realName">
          <el-input v-model="form.realName" size="large" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" size="large" style="width:100%;">
            <el-option label="学生" value="STUDENT" />
            <el-option label="企业" value="COMPANY" />
            <el-option label="就业老师" value="TEACHER" />
          </el-select>
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%;height:46px;" :loading="loading" @click="submit">
          注册
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
const form = reactive({ username: '', password: '123456', realName: '', role: 'STUDENT' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实名称', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    await auth.register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>
