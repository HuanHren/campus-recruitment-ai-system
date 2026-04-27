<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="AI 简历优化页面"
      desc="左侧输入原始简历内容，右侧展示 DeepSeek-V4-Pro 优化结果。未配置 API Key 时自动返回模拟数据，保证答辩演示稳定。"
      eyebrow="DeepSeek-V4-Pro 简历优化"
      icon="solar:document-text-bold-duotone"
    />

    <section class="ai-workspace" style="margin-top:18px;">
      <AppPanel title="原始简历" desc="建议包含专业技能、项目经历、实习经历和求职意向" :hover="false">
        <el-input v-model="resumeContent" type="textarea" :rows="18" placeholder="请输入学生简历内容" />
        <div class="hero-actions">
          <el-button type="primary" :loading="loading" @click="submit">开始优化</el-button>
          <el-button plain @click="resumeContent = sampleResume">填入示例</el-button>
        </div>
      </AppPanel>

      <AppPanel title="AI 优化结果" :hover="false">
        <template #actions>
          <el-tag v-if="mock" type="warning" effect="plain">模拟数据</el-tag>
          <el-tag v-else effect="plain">DeepSeek-V4-Pro</el-tag>
        </template>
        <div class="ai-result">{{ result || '优化后的简历文本会显示在这里，建议先输入简历内容后点击“开始优化”。' }}</div>
        <AppTagGroup style="margin-top:16px;" :tags="resumeAdviceTags" />
      </AppPanel>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <AppAiCard title="表达结构优化" desc="将零散经历整理为能力、项目、成果三层结构。" icon="solar:layers-bold-duotone" />
      <AppAiCard title="岗位关键词增强" desc="结合岗位要求突出 Java、Spring Boot、MySQL 等关键词。" icon="solar:hashtag-chat-bold-duotone" />
      <AppAiCard title="答辩演示兜底" desc="未配置 DeepSeek Key 时自动返回模拟结果，保证现场演示可用。" icon="solar:shield-check-bold-duotone" />
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AppAiCard from '../../components/common/AppAiCard.vue'
import AppPageHeader from '../../components/common/AppPageHeader.vue'
import AppPanel from '../../components/common/AppPanel.vue'
import AppTagGroup from '../../components/common/AppTagGroup.vue'
import request from '../../utils/request'
import { resumeAdviceTags } from '../../mock/ai'

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
