<template>
  <div class="page">
    <section class="hero-panel" style="margin-bottom:18px;">
      <span class="ai-chip"><el-icon><MagicStick /></el-icon> DeepSeek-V4-Pro 简历优化</span>
      <h1 style="margin-top:14px;">AI 简历优化页面</h1>
      <p>左侧输入原始简历内容，右侧展示 AI 优化结果。未配置 DeepSeek API Key 时系统自动返回模拟数据，确保答辩演示稳定。</p>
    </section>

    <section class="ai-workspace">
      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>原始简历</h3>
          <el-tag effect="plain">学生输入</el-tag>
        </div>
        <el-input
          v-model="resumeContent"
          type="textarea"
          :rows="18"
          placeholder="请输入学生简历内容，例如专业技能、项目经历、实习经历和自我评价"
        />
        <div class="hero-actions">
          <el-button type="primary" :icon="MagicStick" :loading="loading" @click="submit">
            开始优化
          </el-button>
          <el-button plain @click="resumeContent = sampleResume">填入示例</el-button>
        </div>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>AI 优化结果</h3>
          <el-tag v-if="mock" type="warning" effect="plain">模拟数据</el-tag>
          <el-tag v-else effect="plain">DeepSeek-V4-Pro</el-tag>
        </div>
        <div class="ai-result">{{ result || '优化后的简历文本会显示在这里，建议先输入简历内容后点击“开始优化”。' }}</div>
        <div class="suggestion-tags">
          <el-tag v-for="item in resumeAdviceTags" :key="item" effect="plain">{{ item }}</el-tag>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { MagicStick } from '@element-plus/icons-vue'
import request from '../../utils/request'
import { resumeAdviceTags } from '../../data/mockDashboard'

const loading = ref(false)
const mock = ref(false)
const sampleResume = '本人熟悉 Java、Spring Boot、MySQL，做过校园招聘系统项目，负责登录认证、岗位管理、投递记录和 AI 模块。希望应聘 Java 后端开发工程师。'
const resumeContent = ref(sampleResume)
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
