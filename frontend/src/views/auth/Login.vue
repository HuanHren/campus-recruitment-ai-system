<template>
  <div class="auth-shell">
    <section class="auth-left">
      <div class="brand-row">
        <span class="brand-mark"><el-icon><Cpu /></el-icon></span>
        <div>
          <div>AI 校园招聘系统</div>
          <small style="color: var(--muted); font-weight: 700;">Campus Recruitment AI SaaS</small>
        </div>
      </div>

      <div class="auth-title">
        <span class="ai-chip"><el-icon><MagicStick /></el-icon> DeepSeek-V4-Pro 智能就业助手</span>
        <h1>让校园招聘从流程管理升级为智能决策</h1>
        <p>
          面向学生、企业、就业老师与管理员的现代化招聘平台，覆盖简历优化、岗位匹配、投递追踪和面试邀请全流程。
        </p>
      </div>

      <div class="auth-illustration">
        <div class="smart-board">
          <div style="display:flex;align-items:center;justify-content:space-between;">
            <strong style="color:var(--title);">岗位匹配分析</strong>
            <el-tag type="primary" effect="plain">AI 92%</el-tag>
          </div>
          <div class="smart-line"><span style="width:92%;" /></div>
          <div class="smart-line"><span style="width:78%; background:linear-gradient(90deg,var(--purple),var(--cyan));" /></div>
          <div class="smart-line"><span style="width:64%; background:linear-gradient(90deg,var(--green),var(--cyan));" /></div>
          <p style="margin:12px 0 0;color:var(--muted);font-size:13px;line-height:1.6;">
            根据技能标签、项目经历和岗位要求生成匹配建议。
          </p>
        </div>
        <div class="candidate-float">
          <div style="display:flex;align-items:center;gap:10px;">
            <el-avatar :size="38" style="background:linear-gradient(135deg,var(--blue),var(--purple));">AI</el-avatar>
            <div>
              <strong style="color:var(--title);">简历优化完成</strong>
              <div style="color:var(--muted);font-size:12px;">项目成果表达增强 4 项</div>
            </div>
          </div>
        </div>
      </div>

      <div class="auth-highlights">
        <div v-for="item in highlights" :key="item.title" class="auth-highlight">
          <el-icon class="icon-pill soft"><component :is="item.icon" /></el-icon>
          <strong>{{ item.title }}</strong>
          <span>{{ item.desc }}</span>
        </div>
      </div>
    </section>

    <section class="auth-panel">
      <el-form ref="formRef" :model="form" :rules="rules" class="auth-card" @keyup.enter="submit">
        <div class="brand-row">
          <span class="brand-mark"><el-icon><Briefcase /></el-icon></span>
          <div>
            <div>欢迎登录</div>
            <small style="color:var(--muted);font-weight:700;">毕业设计演示平台</small>
          </div>
        </div>
        <h2>进入招聘工作台</h2>
        <p class="auth-sub">选择角色示例账号，体验对应的业务工作流与 AI 能力。</p>

        <div class="role-segment" aria-label="角色切换">
          <button
            v-for="item in quickUsers"
            :key="item.username"
            type="button"
            :class="{ active: form.username === item.username }"
            @click="fill(item)"
          >
            {{ item.label }}
          </button>
        </div>

        <el-form-item prop="username">
          <el-input v-model="form.username" size="large" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" size="large" placeholder="密码" show-password :prefix-icon="Lock" />
        </el-form-item>

        <el-button type="primary" size="large" style="width:100%;height:46px;" :loading="loading" @click="submit">
          登录系统
        </el-button>
        <div class="auth-footer-link">
          还没有账号？
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { markRaw, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Briefcase, Cpu, DataAnalysis, Lock, MagicStick, Promotion, User } from '@element-plus/icons-vue'
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
const highlights = [
  { title: 'AI 简历优化', desc: '优化项目表达与关键词匹配', icon: markRaw(MagicStick) },
  { title: '智能岗位匹配', desc: '分析简历与岗位要求契合度', icon: markRaw(DataAnalysis) },
  { title: '投递进度追踪', desc: '记录待查看、面试、拒绝等状态', icon: markRaw(Promotion) },
  { title: '面试邀请管理', desc: '支持企业邀约与学生确认', icon: markRaw(Briefcase) }
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
