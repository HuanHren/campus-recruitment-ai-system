<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="企业招聘工作台"
      desc="从岗位发布到面试邀约，集中管理企业校园招聘流程，并结合 DeepSeek-V4-Pro 提升岗位描述质量和候选人筛选效率。"
      eyebrow="企业招聘运营"
      icon="solar:buildings-2-bold-duotone"
    >
      <template #actions>
        <el-button type="primary" @click="$router.push('/job-publish')">发布岗位</el-button>
        <el-button plain @click="$router.push('/applications')">处理投递</el-button>
        <el-button plain @click="$router.push('/job-publish')">AI 生成岗位描述</el-button>
      </template>
    </AppPageHeader>

    <section class="grid grid-4" style="margin-top:18px;">
      <AppStatCard
        v-for="(item, index) in metricCards"
        :key="item.label"
        :label="item.label"
        :value="item.value"
        :trend="item.trend"
        :desc="item.desc"
        :icon="item.icon"
        :tone="item.tone"
      />
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <AppPanel title="岗位数据趋势" desc="近 7 日收到简历数量变化" class="wide-card" :hover="false">
        <div class="bar-chart">
          <div v-for="item in companyTrend" :key="item.label" class="bar-item">
            <div class="bar-column" :style="{ height: `${item.value * 4}px` }" />
            <strong style="color:var(--title);">{{ item.value }}</strong>
            <span class="bar-label">{{ item.label }}</span>
          </div>
        </div>
      </AppPanel>

      <AppAiCard
        title="AI 岗位描述生成"
        desc="输入岗位名称、薪资与技能要求，自动生成岗位职责和任职要求，减少企业 HR 编写岗位文案的时间。"
        icon="solar:pen-new-square-bold-duotone"
      >
        <AppTagGroup style="margin-top:14px;" :tags="['岗位职责', '任职要求', '校园招聘语气']" />
        <el-button type="primary" style="margin-top:18px;" @click="$router.push('/job-publish')">进入生成</el-button>
      </AppAiCard>
    </section>

    <section class="grid grid-2" style="margin-top:18px;">
      <AppPanel title="候选人列表" desc="根据简历内容和岗位要求展示候选人优先级" :hover="false">
        <template #actions>
          <el-button link type="primary" @click="$router.push('/applications')">查看全部</el-button>
        </template>
        <div class="candidate-list" v-auto-animate>
          <div v-for="item in candidates" :key="item.name" class="candidate-item">
            <div class="candidate-head">
              <div style="display:flex;gap:12px;align-items:center;">
                <el-avatar :size="42" style="background:#eef2ff;color:#2563eb;">{{ item.name.slice(0, 1) }}</el-avatar>
                <div>
                  <h3>{{ item.name }}</h3>
                  <div class="muted">{{ item.school }} · {{ item.role }}</div>
                </div>
              </div>
              <el-tag :type="item.match > 88 ? 'success' : 'primary'" effect="plain">{{ item.status }}</el-tag>
            </div>
            <div class="progress-line">
              <div style="display:flex;justify-content:space-between;margin-bottom:8px;">
                <span class="muted">AI 匹配度</span>
                <strong style="color:var(--title);">{{ item.match }}%</strong>
              </div>
              <el-progress :percentage="item.match" :show-text="false" :stroke-width="10" />
            </div>
          </div>
        </div>
      </AppPanel>

      <AppPanel title="招聘流程看板" desc="展示从简历初筛到学生确认的推进情况" :hover="false">
        <div v-for="item in processRows" :key="item.label" class="bar-row">
          <span>{{ item.label }}</span>
          <div class="bar-track"><div class="bar-fill" :style="{ width: item.percent + '%' }" /></div>
          <strong>{{ item.percent }}%</strong>
        </div>
      </AppPanel>
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
import { candidates, companyMetrics, companyTrend } from '../../mock/dashboard'

const tones = ['blue', 'purple', 'orange', 'green']
const icons = [
  'solar:case-round-bold-duotone',
  'solar:document-text-bold-duotone',
  'solar:inbox-line-bold-duotone',
  'solar:calendar-mark-bold-duotone'
]

const metricCards = computed(() => companyMetrics.map((item, index) => ({
  ...item,
  tone: tones[index],
  icon: icons[index],
  desc: index === 0 ? '覆盖技术、产品与运营岗位' : index === 1 ? '本周简历活跃增长' : index === 2 ? '需要 HR 及时推进' : '线上与现场面试并行'
})))

const processRows = [
  { label: '简历初筛', percent: 76 },
  { label: '面试邀约', percent: 58 },
  { label: '学生确认', percent: 44 },
  { label: '岗位补充', percent: 32 }
]
</script>
