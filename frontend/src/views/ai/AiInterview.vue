<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="AI 模拟面试题生成"
      desc="根据岗位名称和岗位要求生成 5 道面试题，并按基础题、项目题、综合题展示，方便学生进行答辩式演练。"
      eyebrow="DeepSeek-V4-Pro 模拟面试"
      icon="solar:microphone-3-bold-duotone"
    >
      <template #actions>
        <el-button type="primary" :loading="loading" :disabled="loading" @click="submit">
          {{ loading ? 'AI 正在生成中...' : '重新生成' }}
        </el-button>
      </template>
    </AppPageHeader>

    <section class="grid grid-3" style="margin-top:18px;">
      <AppPanel title="生成条件" :hover="false">
        <el-form label-position="top">
          <el-form-item label="岗位名称">
            <el-input v-model="form.jobName" placeholder="例如：Java 后端开发工程师" />
          </el-form-item>
          <el-form-item label="岗位要求">
            <el-input v-model="form.jobRequirement" type="textarea" :rows="9" />
          </el-form-item>
          <el-button type="primary" :loading="loading" :disabled="loading" @click="submit">
            {{ loading ? 'AI 正在生成中...' : '生成面试题' }}
          </el-button>
          <el-alert
            v-if="loading"
            style="margin-top:14px;"
            type="info"
            show-icon
            :closable="false"
            title="正在生成基础题、项目题和综合题，真实 AI 生成可能需要 30-90 秒。"
          />
        </el-form>
      </AppPanel>

      <AppPanel title="面试题卡片" class="wide-card" :hover="false">
        <template #actions>
          <el-tag v-if="source === 'mock'" type="warning" effect="plain">模拟数据</el-tag>
          <el-tag v-else effect="plain">{{ model || 'DeepSeek-V4-Pro' }}</el-tag>
        </template>
        <div class="grid grid-2" v-auto-animate>
          <div v-for="(question, index) in visibleQuestions" :key="question" class="question-card">
            <el-tag :style="{ borderColor: typeColor(question.type || questionType(index).type), color: typeColor(question.type || questionType(index).type) }" effect="plain">
              {{ question.type || questionType(index).type }}
            </el-tag>
            <h3>第 {{ index + 1 }} 题</h3>
            <p style="line-height:1.8;margin:0;">{{ question.question || question }}</p>
            <div v-if="question.answerIdea" style="margin-top:12px;padding:12px;border-radius:14px;background:#f8faff;color:var(--color-text);line-height:1.7;">
              <strong>参考答题思路：</strong>{{ question.answerIdea }}
            </div>
            <div v-if="question.focus" style="margin-top:10px;color:var(--muted);font-size:13px;">
              考察点：{{ question.focus }}
            </div>
          </div>
        </div>
      </AppPanel>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import AppPageHeader from '../../components/common/AppPageHeader.vue'
import AppPanel from '../../components/common/AppPanel.vue'
import aiRequest from '../../utils/aiRequest'
import { interviewQuestionTypes, mockInterviewQuestions } from '../../mock/ai'

const loading = ref(false)
const source = ref('mock')
const model = ref('deepseek-v4-pro')
const form = reactive({
  jobName: 'Java 后端开发工程师',
  jobRequirement: '熟悉 Java、Spring Boot、MySQL，具备良好的编码习惯和项目沟通能力。'
})
const questions = ref([])
const fallbackQuestions = mockInterviewQuestions

const visibleQuestions = computed(() => questions.value.length ? questions.value : fallbackQuestions)

function questionType(index) {
  if (index <= 1) return interviewQuestionTypes[0]
  if (index <= 3) return interviewQuestionTypes[1]
  return interviewQuestionTypes[2]
}

function typeColor(type) {
  return interviewQuestionTypes.find(item => item.type === type)?.color || '#2563EB'
}

async function submit() {
  loading.value = true
  try {
    const res = await aiRequest.post('/student/ai/interview-questions', form)
    questions.value = res.data.questionCards || (res.data.questions || []).map(item => ({ question: item }))
    source.value = res.data.source || (res.data.mock ? 'mock' : 'deepseek')
    model.value = res.data.model || 'deepseek-v4-pro'
  } catch {
    questions.value = mockInterviewQuestions
    source.value = 'mock'
    model.value = 'deepseek-v4-pro'
  } finally {
    loading.value = false
  }
}
</script>
