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
          <el-button type="primary" :loading="loading" :disabled="loading" @click="submit">
            {{ loading ? 'AI 正在生成中...' : '开始优化' }}
          </el-button>
          <el-button plain :disabled="loading" @click="resumeContent = sampleResume">填入示例</el-button>
        </div>
        <el-alert
          v-if="loading"
          style="margin-top:14px;"
          type="info"
          show-icon
          :closable="false"
          title="DeepSeek-V4-Pro 正在润写简历，复杂内容可能需要 30-90 秒，请勿重复点击。"
        />
      </AppPanel>

      <AppPanel title="AI 优化结果" :hover="false">
        <template #actions>
          <el-tag v-if="source === 'mock'" type="warning" effect="plain">模拟数据</el-tag>
          <el-tag v-else effect="plain">{{ model || 'DeepSeek-V4-Pro' }}</el-tag>
        </template>
        <h3 class="result-title">{{ title }}</h3>
        <div class="ai-result">{{ result || '优化后的简历文本会显示在这里，建议先输入简历内容后点击“开始优化”。' }}</div>
        <AppTagGroup style="margin-top:16px;" :tags="suggestions.length ? suggestions : resumeAdviceTags" />
      </AppPanel>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <AppAiCard title="技能关键词提取" :desc="skillKeywords.join('、') || '等待 AI 提取关键词。'" icon="solar:hashtag-chat-bold-duotone" />
      <AppAiCard title="简历亮点总结" :desc="highlights.join('；') || '等待 AI 总结简历亮点。'" icon="solar:medal-ribbon-star-bold-duotone" />
      <AppAiCard title="推荐岗位方向" :desc="recommendedPositions.join('、') || '等待 AI 推荐岗位方向。'" icon="solar:case-round-bold-duotone" />
    </section>

    <AppPanel v-if="improvements.length" title="可改进问题" style="margin-top:18px;" :hover="false">
      <AppTagGroup :tags="improvements" />
    </AppPanel>
  </div>
</template>

<script setup>
import { onBeforeUnmount, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AppAiCard from '../../components/common/AppAiCard.vue'
import AppPageHeader from '../../components/common/AppPageHeader.vue'
import AppPanel from '../../components/common/AppPanel.vue'
import AppTagGroup from '../../components/common/AppTagGroup.vue'
import { resumeAdviceTags } from '../../mock/ai'

const loading = ref(false)
const source = ref('mock')
const model = ref('deepseek-v4-pro')
const title = ref('AI 简历优化结果')
const sampleResume = '本人熟悉 Java、Spring Boot、MySQL，做过校园招聘系统项目，负责登录认证、岗位管理、投递记录和 AI 模块。希望应聘 Java 后端开发工程师。'
const resumeContent = ref(sampleResume)
const result = ref('')
const suggestions = ref([])
const skillKeywords = ref([])
const highlights = ref([])
const improvements = ref([])
const recommendedPositions = ref([])
let resumeEventSource = null

function resetResult() {
  result.value = ''
  suggestions.value = []
  skillKeywords.value = []
  highlights.value = []
  improvements.value = []
  recommendedPositions.value = []
}

function closeResumeStream() {
  if (resumeEventSource) {
    resumeEventSource.close()
    resumeEventSource = null
  }
}

function applyFinalResult(data) {
  result.value = data.content || data.optimizedContent || result.value
  source.value = data.source || (data.mock ? 'mock' : 'deepseek')
  model.value = data.model || 'deepseek-v4-pro'
  title.value = data.title || 'AI 简历优化结果'
  suggestions.value = data.suggestions || []
  skillKeywords.value = data.keywords || data.skillKeywords || []
  highlights.value = data.highlights || []
  improvements.value = data.weaknesses || data.improvements || []
  recommendedPositions.value = data.recommendedJobs || data.recommendedPositions || []
}

function submit() {
  if (loading.value) return
  loading.value = true
  resetResult()
  closeResumeStream()

  const token = localStorage.getItem('token') || ''
  const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
  const params = new URLSearchParams({
    resumeContent: resumeContent.value,
    token
  })
  resumeEventSource = new EventSource(`${baseURL}/student/ai/resume-optimize/stream?${params.toString()}`)

  resumeEventSource.addEventListener('meta', (event) => {
    const data = JSON.parse(event.data)
    source.value = data.source || source.value
    model.value = data.model || model.value
    title.value = data.title || title.value
  })

  resumeEventSource.addEventListener('delta', (event) => {
    const data = JSON.parse(event.data)
    result.value += data.content || ''
  })

  resumeEventSource.addEventListener('done', (event) => {
    applyFinalResult(JSON.parse(event.data))
    loading.value = false
    closeResumeStream()
  })

  resumeEventSource.addEventListener('error', (event) => {
    if (event.data) {
      const data = JSON.parse(event.data)
      ElMessage.error(data.message || 'AI 生成失败，请稍后重试')
    } else if (loading.value) {
      ElMessage.error('AI 生成连接中断，请稍后重试，或切换为模拟数据演示。')
    }
    loading.value = false
    closeResumeStream()
  })
}

onBeforeUnmount(() => closeResumeStream())
</script>

<style scoped>
.result-title {
  margin: 0 0 12px;
  color: #0f172a;
  font-size: 18px;
  font-weight: 800;
}
</style>
