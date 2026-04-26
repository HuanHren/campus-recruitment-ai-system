<template>
  <div class="page">
    <PageHeader title="岗位发布页面" desc="填写岗位基础信息，可使用 AI 快速生成岗位职责和任职要求。" />
    <div class="grid grid-2">
      <el-card class="glass-card content-card" shadow="never">
        <el-form :model="form" label-width="104px">
          <el-form-item label="岗位名称"><el-input v-model="form.jobName" /></el-form-item>
          <el-form-item label="城市"><el-input v-model="form.city" /></el-form-item>
          <el-form-item label="岗位类型"><el-input v-model="form.jobType" /></el-form-item>
          <el-form-item label="薪资范围">
            <el-input-number v-model="form.salaryMin" :min="0" style="width:48%;" />
            <span style="width:4%;text-align:center;">-</span>
            <el-input-number v-model="form.salaryMax" :min="0" style="width:48%;" />
          </el-form-item>
          <el-form-item label="学历"><el-input v-model="form.education" /></el-form-item>
          <el-form-item label="经验"><el-input v-model="form.experience" /></el-form-item>
          <el-form-item label="人数"><el-input-number v-model="form.headcount" :min="1" /></el-form-item>
          <el-form-item label="职责"><el-input v-model="form.jobDescription" type="textarea" :rows="5" /></el-form-item>
          <el-form-item label="要求"><el-input v-model="form.jobRequirement" type="textarea" :rows="5" /></el-form-item>
          <el-form-item label="福利"><el-input v-model="form.welfare" /></el-form-item>
          <el-form-item label="联系人"><el-input v-model="form.contactPerson" /></el-form-item>
          <el-form-item label="联系电话"><el-input v-model="form.contactPhone" /></el-form-item>
          <el-button type="primary" :icon="Promotion" :loading="saving" @click="submit">发布岗位</el-button>
        </el-form>
      </el-card>
      <el-card class="glass-card content-card" shadow="never">
        <h3>AI 生成岗位描述</h3>
        <p style="color:#667085;">根据岗位名称、薪资和技能要求生成更适合校园招聘的岗位内容。</p>
        <el-input v-model="skillRequirement" type="textarea" :rows="5" placeholder="请输入技能要求，如 Java、Spring Boot、MySQL" />
        <el-button style="margin:16px 0;" type="success" :icon="MagicStick" :loading="aiLoading" @click="generate">
          AI生成
        </el-button>
        <div class="ai-result">{{ aiText || 'AI 生成内容会展示在这里。' }}</div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { MagicStick, Promotion } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'

const saving = ref(false)
const aiLoading = ref(false)
const aiText = ref('')
const skillRequirement = ref('Java、Spring Boot、MySQL')
const form = reactive({
  jobName: 'Java开发工程师', city: '杭州', jobType: '全职', salaryMin: 8000, salaryMax: 15000,
  education: '本科', experience: '不限', headcount: 3, jobDescription: '', jobRequirement: '',
  welfare: '五险一金、双休、带薪年假', contactPerson: '李经理', contactPhone: '13900000001'
})

async function generate() {
  aiLoading.value = true
  try {
    const res = await request.post('/company/ai/job-description', {
      jobName: form.jobName,
      salaryRange: `${form.salaryMin}-${form.salaryMax}`,
      skillRequirement: skillRequirement.value
    })
    form.jobDescription = res.data.responsibility
    form.jobRequirement = res.data.requirement
    aiText.value = `岗位职责：\n${res.data.responsibility}\n\n任职要求：\n${res.data.requirement}`
  } finally {
    aiLoading.value = false
  }
}

async function submit() {
  saving.value = true
  try {
    await request.post('/company/jobs', form)
    ElMessage.success('岗位已提交审核')
  } finally {
    saving.value = false
  }
}
</script>
