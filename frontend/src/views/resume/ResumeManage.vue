<template>
  <div class="page">
    <PageHeader title="简历管理页面" desc="维护当前投递简历，并可跳转到 AI 简历优化提升表达质量。">
      <el-button type="success" :icon="MagicStick" @click="$router.push('/ai-resume')">AI优化</el-button>
    </PageHeader>
    <el-card class="glass-card content-card" shadow="never">
      <el-form :model="form" label-width="108px">
        <el-row :gutter="18">
          <el-col :md="12"><el-form-item label="简历名称"><el-input v-model="form.resumeName" /></el-form-item></el-col>
          <el-col :md="12"><el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="性别"><el-input v-model="form.gender" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="学校"><el-input v-model="form.school" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="专业"><el-input v-model="form.major" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="学历"><el-input v-model="form.education" /></el-form-item></el-col>
          <el-col :md="12"><el-form-item label="期望岗位"><el-input v-model="form.expectedPosition" /></el-form-item></el-col>
          <el-col :md="12"><el-form-item label="期望城市"><el-input v-model="form.expectedCity" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="专业技能"><el-input v-model="form.skills" type="textarea" :rows="3" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="项目经历"><el-input v-model="form.projectExperience" type="textarea" :rows="4" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="实习经历"><el-input v-model="form.internshipExperience" type="textarea" :rows="4" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="自我评价"><el-input v-model="form.selfEvaluation" type="textarea" :rows="3" /></el-form-item></el-col>
        </el-row>
        <el-button type="primary" :loading="loading" @click="save">保存简历</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'

const loading = ref(false)
const exists = ref(false)
const form = reactive({
  resumeName: 'Java开发简历', realName: '', gender: '男', phone: '', email: '',
  school: '', major: '', education: '本科', graduationYear: '2026',
  expectedPosition: 'Java开发工程师', expectedCity: '杭州', skills: '',
  projectExperience: '', internshipExperience: '', selfEvaluation: ''
})

async function load() {
  try {
    const res = await request.get('/student/resume')
    Object.assign(form, res.data)
    exists.value = true
  } catch {
    exists.value = false
  }
}

async function save() {
  loading.value = true
  try {
    await request[exists.value ? 'put' : 'post']('/student/resume', form)
    exists.value = true
    ElMessage.success('简历已保存')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
