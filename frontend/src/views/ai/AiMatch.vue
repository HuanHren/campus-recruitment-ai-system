<template>
  <div class="page">
    <PageHeader title="AI 岗位匹配页面" desc="输入简历与岗位要求，返回匹配分数、原因和改进建议。" />
    <div class="grid grid-2">
      <el-card class="glass-card content-card" shadow="never">
        <el-form label-position="top">
          <el-form-item label="学生简历">
            <el-input v-model="form.resumeContent" type="textarea" :rows="8" />
          </el-form-item>
          <el-form-item label="岗位要求">
            <el-input v-model="form.jobRequirement" type="textarea" :rows="8" />
          </el-form-item>
          <el-button type="primary" :icon="DataAnalysis" :loading="loading" @click="submit">分析匹配度</el-button>
        </el-form>
      </el-card>
      <el-card class="glass-card content-card" shadow="never">
        <h3>匹配结果 <el-tag v-if="result.mock" type="warning">模拟数据</el-tag></h3>
        <el-progress type="dashboard" :percentage="result.matchScore || 0" :color="colors" />
        <p style="line-height:1.8;">{{ result.matchReason || '分析结果会展示在这里。' }}</p>
        <el-timeline>
          <el-timeline-item v-for="item in result.suggestions" :key="item">{{ item }}</el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { DataAnalysis } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'

const loading = ref(false)
const form = reactive({
  resumeContent: '熟悉 Java、Spring Boot、MySQL，做过校园招聘系统，负责后端接口和数据库设计。',
  jobRequirement: '要求熟悉 Java、Spring Boot、MySQL，有项目开发经验和良好的沟通能力。'
})
const result = reactive({ matchScore: 0, matchReason: '', suggestions: [], mock: false })
const colors = [{ color: '#ef4444', percentage: 50 }, { color: '#ffb020', percentage: 75 }, { color: '#12b886', percentage: 100 }]

async function submit() {
  loading.value = true
  try {
    const res = await request.post('/student/ai/job-match', form)
    Object.assign(result, res.data)
  } finally {
    loading.value = false
  }
}
</script>
