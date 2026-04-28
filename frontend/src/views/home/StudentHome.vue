<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="学生求职工作台"
      desc="根据简历完整度、技能标签、城市意向和岗位要求展示推荐结果，帮助学生完成岗位筛选、简历优化与面试准备。"
      eyebrow="AI 求职推荐工作台"
      icon="solar:graduation-cap-bold-duotone"
    >
      <template #actions>
        <el-button type="primary" @click="$router.push('/jobs')">查看推荐岗位</el-button>
        <el-button plain @click="$router.push('/ai-resume')">优化我的简历</el-button>
        <el-button plain @click="$router.push('/ai-match')">岗位匹配分析</el-button>
      </template>
    </AppPageHeader>

    <section class="grid grid-3 page-gap">
      <AppStatCard
        v-for="(item, index) in summaryCards"
        :key="item.label"
        :label="item.label"
        :value="item.value"
        :desc="item.desc"
        :trend="item.trend"
        :icon="item.icon"
        :tone="index === 0 ? 'blue' : index === 1 ? 'purple' : 'orange'"
      />
    </section>

    <section class="grid grid-3 page-gap">
      <AppPanel title="AI 推荐岗位" desc="按简历技能、城市意向和岗位要求综合排序" class="wide-card" :hover="false">
        <template #actions>
          <el-tag type="primary" effect="plain">按匹配度排序</el-tag>
        </template>
        <div class="job-card-list" v-auto-animate>
          <div v-for="job in recommendedJobs" :key="`${job.company}-${job.title}`" class="job-card">
            <div class="job-card-head">
              <div>
                <h3>{{ job.title }}</h3>
                <div class="muted">{{ job.company }} · {{ job.city }}</div>
              </div>
              <strong class="salary-text">{{ job.salary }}</strong>
            </div>
            <AppTagGroup style="margin-top:12px;" :tags="job.tags" />
            <div class="progress-line">
              <div class="row-between">
                <span class="muted">AI 匹配度</span>
                <strong>{{ job.match }}%</strong>
              </div>
              <el-progress :percentage="job.match" :show-text="false" :stroke-width="10" />
            </div>
          </div>
        </div>
      </AppPanel>

      <AppAiCard
        title="DeepSeek 简历优化"
        desc="识别简历中的项目表达、技能关键词和岗位匹配不足，生成适合校园招聘场景的优化文本。"
        icon="solar:document-text-bold-duotone"
      >
        <el-button type="primary" style="margin-top:16px;" @click="$router.push('/ai-resume')">立即优化</el-button>
      </AppAiCard>
    </section>

    <section class="grid grid-3 page-gap">
      <AppPanel title="投递进度时间线" desc="跟踪求职流程中的关键状态" :hover="false">
        <el-timeline>
          <el-timeline-item v-for="item in applyTimeline" :key="item.title" :type="item.status" :timestamp="item.time">
            {{ item.title }}
          </el-timeline-item>
        </el-timeline>
      </AppPanel>

      <AppPanel title="热门企业" desc="根据岗位数量和企业活跃度推荐" :hover="false">
        <div class="candidate-list" v-auto-animate>
          <div v-for="item in hotCompanies" :key="item.name" class="candidate-item">
            <div class="candidate-head">
              <div>
                <h3>{{ item.name }}</h3>
                <div class="muted">{{ item.field }}</div>
              </div>
              <el-tag effect="plain">{{ item.jobs }} 岗</el-tag>
            </div>
          </div>
        </div>
      </AppPanel>

      <AppAiCard
        title="AI 面试准备"
        desc="根据岗位名称和任职要求生成基础题、项目题与综合题，帮助学生提前组织回答思路。"
        icon="solar:microphone-3-bold-duotone"
      >
        <el-button type="primary" style="margin-top:16px;" @click="$router.push('/ai-interview')">
          生成模拟面试题
        </el-button>
      </AppAiCard>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import AppAiCard from '../../components/common/AppAiCard.vue'
import AppPageHeader from '../../components/common/AppPageHeader.vue'
import AppPanel from '../../components/common/AppPanel.vue'
import AppStatCard from '../../components/common/AppStatCard.vue'
import AppTagGroup from '../../components/common/AppTagGroup.vue'
import { applyTimeline, hotCompanies, studentSummary } from '../../mock/dashboard'
import { recommendedJobs } from '../../mock/jobs'

const summaryCards = computed(() => studentSummary.map((item, index) => ({
  ...item,
  trend: index === 0 ? '可优化' : index === 1 ? '智能推荐' : '待确认',
  icon: index === 0
    ? 'solar:document-text-bold-duotone'
    : index === 1
      ? 'solar:stars-bold-duotone'
      : 'solar:calendar-mark-bold-duotone'
})))
</script>

<style scoped>
.page-gap {
  margin-top: 18px;
}

.salary-text,
.row-between strong {
  color: var(--color-primary);
}

.row-between {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
</style>
