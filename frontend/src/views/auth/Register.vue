<template>
  <div class="auth-shell">
    <section class="auth-visual">
      <div class="brand-row">
        <span class="brand-mark"><el-icon><UserFilled /></el-icon></span>
        <span>加入校园招聘智能平台</span>
      </div>
      <div class="auth-title">
        <h1>一份账号，连接职业成长与校园招聘</h1>
        <p>学生完善简历、企业发布岗位、就业老师掌握就业服务节奏，平台为毕业设计答辩提供完整业务闭环。</p>
      </div>
    </section>
    <section class="auth-panel">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="auth-card">
        <h2>创建账号</h2>
        <p class="auth-sub">管理员账号由系统初始化，注册支持学生、企业和就业老师。</p>
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
        <el-button type="primary" size="large" style="width:100%;" :loading="loading" @click="submit">
          注册
        </el-button>
        <div style="margin-top:18px;text-align:center;color:#667085;">
          已有账号？
          <router-link to="/login" style="color:#1b6eea;font-weight:700;">返回登录</router-link>
        </div>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
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
