<template>
  <div class="page">
    <section class="hero-panel" style="margin-bottom:18px;">
      <span class="ai-chip"><el-icon><DataAnalysis /></el-icon> DeepSeek-V4-Pro 岗位匹配</span>
      <h1 style="margin-top:14px;">AI 岗位匹配页面</h1>
      <p>输入学生简历与岗位要求，系统返回匹配分数、匹配原因与不足建议，帮助学生判断投递优先级。</p>
    </section>

    <section class="grid grid-3">
      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>岗位信息</h3>
          <el-tag effect="plain">推荐岗位</el-tag>
        </div>
        <h2 style="color:var(--title);margin:0 0 8px;">Java 后端开发工程师</h2>
        <p class="muted">杭州云启科技有限公司 · 杭州 · 10k-16k</p>
        <div class="tag-row">
          <el-tag effect="plain">Spring Boot</el-tag>
          <el-tag effect="plain">MySQL</el-tag>
          <el-tag effect="plain">Redis</el-tag>
        </div>
        <p style="line-height:1.8;margin-top:16px;">要求熟悉 Java、Spring Boot、MySQL，有项目开发经验和良好的沟通能力。</p>
      </el-card>

      <el-card class="content-card wide-card" shadow="never">
        <div class="section-title">
          <h3>匹配度分析输入</h3>
        </div>
        <el-form label-position="top">
          <el-form-item label="学生简历">
            <el-input v-model="form.resumeContent" type="textarea" :rows="6" />
          </el-form-item>
          <el-form-item label="岗位要求">
            <el-input v-model="form.jobRequirement" type="textarea" :rows="6" />
          </el-form-item>
          <el-button type="primary" :icon="DataAnalysis" :loading="loading" @click="submit">分析匹配度</el-button>
        </el-form>
      </el-card>
    </section>

    <section class="grid grid-2" style="margin-top:18px;">
      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>匹配结果</h3>
          <el-tag v-if="result.mock" type="warning" effect="plain">模拟数据</el-tag>
          <el-tag v-else effect="plain">DeepSeek-V4-Pro</el-tag>
        </div>
        <div class="match-score">
          <div class="score-ring" :style="{ '--score': `${result.matchScore || 0}%` }">
            <span>{{ result.matchScore || 0 }}%</span>
          </div>
          <div>
            <h3 style="color:var(--title);margin:0 0 10px;">匹配原因</h3>
            <p style="line-height:1.85;margin:0;">{{ result.matchReason || '分析结果会展示在这里。' }}</p>
          </div>
        </div>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>不足建议</h3>
        </div>
        <el-timeline>
          <el-timeline-item v-for="item in normalizedSuggestions" :key="item" type="primary">
            {{ item }}
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { DataAnalysis } from '@element-plus/icons-vue'
import request from '../../utils/request'

const loading = ref(false)
const form = reactive({
  resumeContent: '熟悉 Java、Spring Boot、MySQL，做过校园招聘系统，负责后端接口、权限控制和数据库设计。',
  jobRequirement: '要求熟悉 Java、Spring Boot、MySQL，有项目开发经验和良好的沟通能力。'
})
const result = reactive({ matchScore: 0, matchReason: '', suggestions: [], mock: false })
const normalizedSuggestions = computed(() => {
  return result.suggestions?.length
    ? result.suggestions
    : ['补充项目成果的量化描述。', '强化岗位关键词，例如 Spring Security、MySQL 优化。', '准备与岗位要求相关的面试案例。']
})

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
