<template>
  <div class="page">
    <PageHeader title="AI 简历优化页面" desc="输入简历内容，AI 返回更适合校园招聘场景的优化文本；无 API Key 时自动返回模拟数据。" />
    <div class="grid grid-2">
      <el-card class="glass-card content-card" shadow="never">
        <h3>原始简历</h3>
        <el-input v-model="resumeContent" type="textarea" :rows="16" placeholder="请输入学生简历内容" />
        <el-button style="margin-top:16px;" type="primary" :icon="MagicStick" :loading="loading" @click="submit">
          开始优化
        </el-button>
      </el-card>
      <el-card class="glass-card content-card" shadow="never">
        <h3>优化结果 <el-tag v-if="mock" type="warning">模拟数据</el-tag></h3>
        <div class="ai-result">{{ result || '优化后的简历文本会显示在这里。' }}</div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { MagicStick } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'

const loading = ref(false)
const mock = ref(false)
const resumeContent = ref('本人熟悉 Java、Spring Boot、MySQL，做过校园招聘系统项目，负责登录、岗位、投递和 AI 模块。')
const result = ref('')

async function submit() {
  loading.value = true
  try {
    const res = await request.post('/student/ai/resume-optimize', { resumeContent: resumeContent.value })
    result.value = res.data.optimizedContent
    mock.value = res.data.mock
  } finally {
    loading.value = false
  }
}
</script>
