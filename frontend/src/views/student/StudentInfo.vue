<template>
  <div class="page">
    <PageHeader title="学生信息页面" desc="维护学号、专业、联系方式和求职方向，作为简历与投递的基础资料。" />
    <el-card class="glass-card content-card" shadow="never">
      <el-form :model="form" label-width="96px">
        <el-row :gutter="18">
          <el-col :md="8" :sm="12"><el-form-item label="学号"><el-input v-model="form.studentNo" /></el-form-item></el-col>
          <el-col :md="8" :sm="12"><el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item></el-col>
          <el-col :md="8" :sm="12"><el-form-item label="性别"><el-select v-model="form.gender" style="width:100%;"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item></el-col>
          <el-col :md="8" :sm="12"><el-form-item label="学校"><el-input v-model="form.school" /></el-form-item></el-col>
          <el-col :md="8" :sm="12"><el-form-item label="学院"><el-input v-model="form.college" /></el-form-item></el-col>
          <el-col :md="8" :sm="12"><el-form-item label="专业"><el-input v-model="form.major" /></el-form-item></el-col>
          <el-col :md="8" :sm="12"><el-form-item label="年级"><el-input v-model="form.grade" /></el-form-item></el-col>
          <el-col :md="8" :sm="12"><el-form-item label="学历"><el-input v-model="form.education" /></el-form-item></el-col>
          <el-col :md="8" :sm="12"><el-form-item label="手机"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :md="12" :sm="24"><el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item></el-col>
          <el-col :md="12" :sm="24"><el-form-item label="期望岗位"><el-input v-model="form.expectedJob" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="个人简介"><el-input v-model="form.selfIntro" type="textarea" :rows="4" /></el-form-item></el-col>
        </el-row>
        <el-button type="primary" :icon="Check" :loading="loading" @click="save">保存信息</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'

const loading = ref(false)
const exists = ref(false)
const form = reactive({
  studentNo: '', name: '', gender: '男', school: '', college: '', major: '', grade: '',
  education: '本科', phone: '', email: '', expectedJob: '', selfIntro: ''
})

async function load() {
  try {
    const res = await request.get('/student/info')
    Object.assign(form, res.data)
    exists.value = true
  } catch {
    exists.value = false
  }
}

async function save() {
  loading.value = true
  try {
    await request[exists.value ? 'put' : 'post']('/student/info', form)
    exists.value = true
    ElMessage.success('学生信息已保存')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
