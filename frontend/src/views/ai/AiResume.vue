<template>
  <div class="ai-resume-page">
    <section class="page-hero">
      <div>
        <p class="eyebrow">AI 简历润色</p>
        <h1>把已有简历整理成更适合投递的表达</h1>
        <p class="hero-desc">
          本页使用 <strong>GET /api/student/ai/resume-optimize/stream</strong> 流式返回结果，token 由现有 SSE helper 自动附加到查询参数。
        </p>
      </div>
      <div class="hero-actions">
        <el-tag v-if="resultMeta.mock" type="warning" effect="plain">后端 fallback/mock 结果</el-tag>
        <el-tag v-else-if="resultMeta.source" effect="plain">{{ resultMeta.source }}</el-tag>
        <el-button plain @click="$router.push('/resume')">返回简历管理</el-button>
      </div>
    </section>

    <section v-if="errorMessage" class="state-panel state-panel--error">
      <div>
        <strong>AI 润色失败</strong>
        <p>{{ errorMessage }}</p>
      </div>
      <el-button plain @click="errorMessage = ''">关闭</el-button>
    </section>

    <section class="workflow-grid">
      <section class="input-panel">
        <div class="panel-head">
          <div>
            <p class="eyebrow">输入</p>
            <h2>简历原文</h2>
          </div>
          <span>{{ resumeContent.length }}/6000</span>
        </div>

        <el-input
          v-model="resumeContent"
          type="textarea"
          :rows="20"
          maxlength="6000"
          show-word-limit
          placeholder="请粘贴你的真实简历内容，例如教育背景、项目经历、实习经历、技能和求职方向。"
        />

        <div class="action-row">
          <el-button type="primary" :loading="loading" :disabled="loading || !resumeContent.trim()" @click="startOptimize">
            {{ loading ? '正在润色' : '开始 AI 润色' }}
          </el-button>
          <el-button v-if="loading" plain @click="stopStream">停止生成</el-button>
          <el-button plain :disabled="loading && !resultText" @click="resetAll">清空</el-button>
        </div>

        <el-alert
          v-if="loading"
          class="inline-alert"
          type="info"
          show-icon
          :closable="false"
          title="正在接收后端 SSE 流式结果，请等待生成完成。"
        />
      </section>

      <section class="result-panel">
        <div class="panel-head">
          <div>
            <p class="eyebrow">结果</p>
            <h2>{{ resultMeta.title || '润色结果' }}</h2>
          </div>
          <el-tag v-if="resultMeta.model" effect="plain">{{ resultMeta.model }}</el-tag>
        </div>

        <div v-if="!resultText && !loading" class="empty-result">
          <strong>暂无润色结果</strong>
          <p>输入简历内容后点击“开始 AI 润色”，这里会展示后端返回的真实内容。</p>
        </div>

        <pre v-else class="result-text">{{ resultText }}</pre>

        <div class="action-row">
          <el-button plain :disabled="!resultText" @click="copyResult">复制结果</el-button>
          <el-button type="primary" plain @click="$router.push('/resume')">使用到简历管理</el-button>
        </div>
      </section>
    </section>

    <section v-if="hasStructuredResult" class="structured-grid">
      <article v-if="listFields.suggestions.length" class="info-card">
        <h3>修改建议</h3>
        <ul>
          <li v-for="item in listFields.suggestions" :key="item">{{ item }}</li>
        </ul>
      </article>

      <article v-if="listFields.keywords.length" class="info-card">
        <h3>关键词</h3>
        <div class="tag-list">
          <el-tag v-for="item in listFields.keywords" :key="item" effect="plain">{{ item }}</el-tag>
        </div>
      </article>

      <article v-if="listFields.highlights.length" class="info-card">
        <h3>简历亮点</h3>
        <ul>
          <li v-for="item in listFields.highlights" :key="item">{{ item }}</li>
        </ul>
      </article>

      <article v-if="listFields.improvements.length" class="info-card">
        <h3>待改进内容</h3>
        <ul>
          <li v-for="item in listFields.improvements" :key="item">{{ item }}</li>
        </ul>
      </article>

      <article v-if="listFields.recommendedPositions.length" class="info-card">
        <h3>后端返回岗位方向</h3>
        <div class="tag-list">
          <el-tag v-for="item in listFields.recommendedPositions" :key="item" effect="plain">{{ item }}</el-tag>
        </div>
      </article>

      <article v-if="resultMeta.reason || resultMeta.errorMessage" class="info-card">
        <h3>后端说明</h3>
        <p>{{ resultMeta.reason || resultMeta.errorMessage }}</p>
      </article>
    </section>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed, onBeforeUnmount, reactive, ref } from 'vue'
import { streamOptimizeResume } from '../../api/ai.api'

const loading = ref(false)
const resumeContent = ref('')
const resultText = ref('')
const errorMessage = ref('')
const resultMeta = reactive({
  source: '',
  model: '',
  title: '',
  mock: false,
  reason: '',
  errorMessage: ''
})
const listFields = reactive({
  suggestions: [],
  keywords: [],
  highlights: [],
  improvements: [],
  recommendedPositions: []
})

let resumeEventSource = null

const hasStructuredResult = computed(() => (
  listFields.suggestions.length ||
  listFields.keywords.length ||
  listFields.highlights.length ||
  listFields.improvements.length ||
  listFields.recommendedPositions.length ||
  resultMeta.reason ||
  resultMeta.errorMessage
))

function asList(value) {
  return Array.isArray(value) ? value.filter(Boolean) : []
}

function resetResult() {
  resultText.value = ''
  Object.assign(resultMeta, {
    source: '',
    model: '',
    title: '',
    mock: false,
    reason: '',
    errorMessage: ''
  })
  Object.assign(listFields, {
    suggestions: [],
    keywords: [],
    highlights: [],
    improvements: [],
    recommendedPositions: []
  })
}

function resetAll() {
  if (loading.value) {
    stopStream()
  }
  resumeContent.value = ''
  errorMessage.value = ''
  resetResult()
}

function closeStream() {
  if (resumeEventSource) {
    resumeEventSource.close()
    resumeEventSource = null
  }
}

function stopStream() {
  closeStream()
  loading.value = false
}

function parseEvent(event) {
  if (!event?.data) return {}
  try {
    return JSON.parse(event.data)
  } catch {
    return { content: event.data }
  }
}

function applyMeta(data) {
  resultMeta.source = data.source || resultMeta.source
  resultMeta.model = data.model || resultMeta.model
  resultMeta.title = data.title || resultMeta.title
  resultMeta.mock = Boolean(data.mock ?? resultMeta.mock)
  resultMeta.reason = data.reason || resultMeta.reason
  resultMeta.errorMessage = data.errorMessage || resultMeta.errorMessage
}

function applyFinalResult(data) {
  applyMeta(data)
  resultText.value = data.content || data.optimizedContent || resultText.value
  listFields.suggestions = asList(data.suggestions)
  listFields.keywords = [
    ...asList(data.keywords),
    ...asList(data.skillKeywords)
  ]
  listFields.highlights = asList(data.highlights)
  listFields.improvements = [
    ...asList(data.weaknesses),
    ...asList(data.improvements)
  ]
  listFields.recommendedPositions = [
    ...asList(data.recommendedJobs),
    ...asList(data.recommendedPositions)
  ]
}

function startOptimize() {
  if (loading.value || !resumeContent.value.trim()) return

  loading.value = true
  errorMessage.value = ''
  resetResult()
  closeStream()

  let streamStarted = false
  resumeEventSource = streamOptimizeResume(resumeContent.value.trim(), {
    meta: (event) => {
      streamStarted = true
      applyMeta(parseEvent(event))
    },
    delta: (event) => {
      streamStarted = true
      const data = parseEvent(event)
      resultText.value += data.content || ''
    },
    done: (event) => {
      streamStarted = true
      applyFinalResult(parseEvent(event))
      loading.value = false
      closeStream()
    },
    error: (event) => {
      const data = parseEvent(event)
      errorMessage.value = data.message || data.errorMessage || (
        streamStarted ? 'AI 润色连接中断，请稍后重试。' : 'AI 润色接口暂不可用，请确认登录状态和后端服务。'
      )
      loading.value = false
      closeStream()
    }
  })
}

async function copyResult() {
  if (!resultText.value) return
  await navigator.clipboard.writeText(resultText.value)
  ElMessage.success('润色结果已复制')
}

onBeforeUnmount(closeStream)
</script>

<style scoped>
.ai-resume-page {
  display: grid;
  gap: 18px;
}

.page-hero,
.input-panel,
.result-panel,
.info-card,
.state-panel {
  border: 1px solid #dbe5ef;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.05);
}

.page-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  padding: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #047857;
  font-size: 13px;
  font-weight: 800;
}

h1,
h2,
h3 {
  margin: 0;
  color: #102033;
}

h1 {
  font-size: 28px;
}

h2 {
  font-size: 18px;
}

.hero-desc,
.state-panel p,
.empty-result p,
.info-card p,
.info-card li {
  margin: 8px 0 0;
  color: #64748b;
  line-height: 1.7;
}

.hero-actions,
.action-row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.state-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px;
}

.state-panel--error {
  border-color: #fecaca;
  background: #fff7f7;
}

.workflow-grid {
  display: grid;
  grid-template-columns: minmax(0, 0.95fr) minmax(0, 1.05fr);
  gap: 18px;
}

.input-panel,
.result-panel,
.info-card {
  padding: 22px;
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 16px;
}

.panel-head span {
  color: #64748b;
  font-size: 13px;
}

.action-row {
  justify-content: flex-start;
  margin-top: 16px;
}

.inline-alert {
  margin-top: 14px;
}

.empty-result {
  min-height: 420px;
  display: grid;
  place-items: center;
  align-content: center;
  padding: 24px;
  border-radius: 14px;
  background: #f8fafc;
  text-align: center;
}

.empty-result strong {
  color: #102033;
}

.result-text {
  min-height: 420px;
  max-height: 620px;
  margin: 0;
  padding: 18px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-word;
  border-radius: 14px;
  background: #f8fafc;
  color: #102033;
  font-family: inherit;
  line-height: 1.8;
}

.structured-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.info-card ul {
  padding-left: 18px;
  margin: 0;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

@media (max-width: 1180px) {
  .workflow-grid,
  .structured-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .page-hero,
  .state-panel {
    align-items: stretch;
    flex-direction: column;
  }

  .hero-actions {
    justify-content: flex-start;
  }
}
</style>
