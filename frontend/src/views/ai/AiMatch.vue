<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="AI 岗位匹配页面"
      desc="输入学生简历与岗位要求，系统返回匹配分数、匹配原因、不足建议和技能标签匹配情况。"
      eyebrow="DeepSeek-V4-Pro 岗位匹配"
      icon="solar:chart-2-bold-duotone"
    />

    <section class="grid grid-3" style="margin-top:18px;">
      <AppPanel title="岗位信息" desc="推荐岗位示例" :hover="false">
        <h2 style="color:var(--title);margin:0 0 8px;">Java 后端开发工程师</h2>
        <p class="muted">杭州云启科技有限公司 · 杭州 · 10k-16k</p>
        <AppTagGroup :tags="['Spring Boot', 'MySQL', 'Redis']" />
        <p style="line-height:1.8;margin-top:16px;">要求熟悉 Java、Spring Boot、MySQL，有项目开发经验和良好的沟通能力。</p>
      </AppPanel>

      <AppPanel title="匹配度分析输入" class="wide-card" :hover="false">
        <el-form label-position="top">
          <el-form-item label="学生简历">
            <el-input v-model="form.resumeContent" type="textarea" :rows="6" />
          </el-form-item>
          <el-form-item label="岗位要求">
            <el-input v-model="form.jobRequirement" type="textarea" :rows="6" />
          </el-form-item>
          <el-button type="primary" :loading="loading" :disabled="loading" @click="submit">
            {{ loading ? 'AI 正在生成中...' : '分析匹配度' }}
          </el-button>
          <el-alert
            v-if="loading"
            style="margin-top:14px;"
            type="info"
            show-icon
            :closable="false"
            title="正在分析简历与岗位匹配度，真实 AI 生成可能需要较长时间，请稍候。"
          />
        </el-form>
      </AppPanel>
    </section>

    <section class="grid grid-2" style="margin-top:18px;">
      <AppPanel title="匹配结果" :hover="false">
        <template #actions>
          <el-tag v-if="result.source === 'mock' || result.mock" type="warning" effect="plain">模拟数据</el-tag>
          <el-tag v-else effect="plain">{{ result.model || 'DeepSeek-V4-Pro' }}</el-tag>
        </template>
        <div class="match-score">
          <div class="score-ring" :style="{ '--score': `${result.matchScore || 0}%` }">
            <span>{{ result.matchScore || 0 }}%</span>
          </div>
          <div>
            <h3 style="color:var(--title);margin:0 0 10px;">匹配原因</h3>
            <el-tag effect="plain" style="margin-bottom:10px;">{{ result.matchLevel || '待分析' }}</el-tag>
            <p style="line-height:1.85;margin:0;">{{ result.matchReason || '分析结果会展示在这里。' }}</p>
          </div>
        </div>
      </AppPanel>

      <AppPanel title="技能标签匹配" desc="展示简历能力与岗位关键词之间的覆盖情况" :hover="false">
        <div class="tag-row">
          <el-tag v-for="item in visibleSkillTags" :key="item.label || item" :type="tagMatched(item) ? 'success' : 'info'" effect="plain">
            {{ typeof item === 'string' ? item : item.label }}{{ typeof item === 'string' ? '' : (item.matched ? ' 已覆盖' : ' 待补充') }}
          </el-tag>
        </div>
        <el-divider />
        <h3 style="color:var(--title);">不足建议</h3>
        <el-timeline>
          <el-timeline-item v-for="item in normalizedSuggestions" :key="item" type="primary">
            {{ item }}
          </el-timeline-item>
        </el-timeline>
        <el-divider />
        <h3 style="color:var(--title);">学习建议</h3>
        <AppTagGroup :tags="result.learningSuggestions || []" />
        <p v-if="result.recommendReason" style="line-height:1.8;margin-top:14px;">{{ result.recommendReason }}</p>
      </AppPanel>
    </section>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import AppPageHeader from '../../components/common/AppPageHeader.vue'
import AppPanel from '../../components/common/AppPanel.vue'
import AppTagGroup from '../../components/common/AppTagGroup.vue'
import aiRequest from '../../utils/aiRequest'
import { skillMatchTags } from '../../mock/ai'

const loading = ref(false)
const form = reactive({
  resumeContent: '熟悉 Java、Spring Boot、MySQL，做过校园招聘系统，负责后端接口、权限控制和数据库设计。',
  jobRequirement: '要求熟悉 Java、Spring Boot、MySQL，有项目开发经验和良好的沟通能力。'
})
const result = reactive({ matchScore: 0, matchReason: '', suggestions: [], mock: false })
const visibleSkillTags = computed(() => result.skillTags?.length ? result.skillTags : skillMatchTags)
const normalizedSuggestions = computed(() => {
  return result.suggestions?.length
    ? result.suggestions
    : ['补充项目成果的量化描述。', '强化岗位关键词，例如 Spring Security、MySQL 优化。', '准备与岗位要求相关的面试案例。']
})

function tagMatched(item) {
  if (typeof item === 'string') {
    return item.includes('已匹配') || item.includes('已覆盖')
  }
  return item.matched
}

async function submit() {
  loading.value = true
  try {
    const res = await aiRequest.post('/student/ai/job-match', form)
    Object.assign(result, res.data)
  } finally {
    loading.value = false
  }
}
</script>
