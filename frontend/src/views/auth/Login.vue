<template>
  <div class="auth-shell">
    <section class="auth-visual">
      <div class="brand-row">
        <span class="brand-mark"><el-icon><Briefcase /></el-icon></span>
        <span>Campus Recruitment AI</span>
      </div>
      <div class="auth-title">
        <h1>让校园招聘从信息流转走向智能决策</h1>
        <p>覆盖学生、企业、管理员与就业老师的全流程招聘平台，支持岗位、简历、投递、面试与 AI 就业辅助。</p>
      </div>
      <div class="grid grid-3">
        <div class="stat-card" style="background:rgba(255,255,255,.16);color:#fff;">
          <div class="label" style="color:rgba(255,255,255,.78)">岗位管理</div>
          <div class="num">AI+</div>
        </div>
        <div class="stat-card" style="background:rgba(255,255,255,.16);color:#fff;">
          <div class="label" style="color:rgba(255,255,255,.78)">投递闭环</div>
          <div class="num">JWT</div>
        </div>
        <div class="stat-card" style="background:rgba(255,255,255,.16);color:#fff;">
          <div class="label" style="color:rgba(255,255,255,.78)">答辩演示</div>
          <div class="num">Ready</div>
        </div>
      </div>
    </section>

    <section class="auth-panel">
      <el-form ref="formRef" :model="form" :rules="rules" class="auth-card" @keyup.enter="submit">
        <h2>欢迎登录</h2>
        <p class="auth-sub">请选择默认账号或输入你的登录信息。</p>
        <el-form-item prop="username">
          <el-input v-model="form.username" size="large" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" size="large" placeholder="密码" show-password :prefix-icon="Lock" />
        </el-form-item>
        <div class="quick-users">
          <el-button v-for="item in quickUsers" :key="item.username" @click="fill(item)">
            {{ item.label }}
          </el-button>
        </div>
        <el-button type="primary" size="large" style="width:100%;" :loading="loading" @click="submit">
          登录系统
        </el-button>
        <div style="margin-top:18px;text-align:center;color:#667085;">
          还没有账号？
          <router-link to="/register" style="color:#1b6eea;font-weight:700;">立即注册</router-link>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Briefcase, Lock, User } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '123456' })
const quickUsers = [
  { label: '管理员', username: 'admin' },
  { label: '学生', username: 'student' },
  { label: '企业', username: 'company' },
  { label: '就业老师', username: 'teacher' }
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
