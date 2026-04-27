<template>
  <div class="page">
    <section class="hero-panel" style="margin-bottom:18px;">
      <span class="ai-chip"><el-icon><Cpu /></el-icon> DeepSeek-V4-Pro 模拟面试</span>
      <h1 style="margin-top:14px;">AI 模拟面试题生成</h1>
      <p>根据岗位名称和岗位要求生成 5 道面试题，并按基础题、项目题、综合题展示，方便学生答辩式演练。</p>
    </section>

    <section class="grid grid-3">
      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>生成条件</h3>
        </div>
        <el-form label-position="top">
          <el-form-item label="岗位名称">
            <el-input v-model="form.jobName" placeholder="例如：Java 后端开发工程师" />
          </el-form-item>
          <el-form-item label="岗位要求">
            <el-input v-model="form.jobRequirement" type="textarea" :rows="9" />
          </el-form-item>
          <el-button type="primary" :icon="Cpu" :loading="loading" @click="submit">生成面试题</el-button>
        </el-form>
      </el-card>

      <el-card class="content-card wide-card" shadow="never">
        <div class="section-title">
          <h3>面试题卡片</h3>
          <el-tag v-if="mock" type="warning" effect="plain">模拟数据</el-tag>
          <el-tag v-else effect="plain">DeepSeek-V4-Pro</el-tag>
        </div>
        <div class="grid grid-2">
          <div v-for="(question, index) in visibleQuestions" :key="question" class="question-card">
            <el-tag :style="{ borderColor: questionType(index).color, color: questionType(index).color }" effect="plain">
              {{ questionType(index).type }}
            </el-tag>
            <h3>第 {{ index + 1 }} 题</h3>
            <p style="line-height:1.8;margin:0;">{{ question }}</p>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { Cpu } from '@element-plus/icons-vue'
import request from '../../utils/request'
import { interviewQuestionTypes } from '../../data/mockDashboard'

const loading = ref(false)
const mock = ref(false)
const form = reactive({
  jobName: 'Java 后端开发工程师',
  jobRequirement: '熟悉 Java、Spring Boot、MySQL，具备良好的编码习惯和项目沟通能力。'
})
const questions = ref([])
const fallbackQuestions = [
  '请介绍 Java 集合框架中 List、Set、Map 的区别，并说明常见使用场景。',
  '请结合你的项目经历，说明你负责的模块、接口设计和数据库设计思路。',
  '如果接口响应变慢，你会从哪些方面排查并定位问题？',
  '请说明 Spring Boot 项目中统一返回、全局异常处理和权限控制的作用。',
  '如果入职后遇到不熟悉的技术任务，你会如何学习并推进交付？'
]

const visibleQuestions = computed(() => questions.value.length ? questions.value : fallbackQuestions)

function questionType(index) {
  if (index <= 1) return interviewQuestionTypes[0]
  if (index <= 3) return interviewQuestionTypes[1]
  return interviewQuestionTypes[2]
}

async function submit() {
  loading.value = true
  try {
    const res = await request.post('/student/ai/interview-questions', form)
    questions.value = res.data.questions || []
    mock.value = res.data.mock
  } finally {
    loading.value = false
  }
}
</script>
