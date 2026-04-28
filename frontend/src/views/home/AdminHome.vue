<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="管理员数据工作台"
      :desc="`今天是 ${today}。首页数据已与演示学生、企业、岗位、投递和面试邀请数据联动，适合毕业答辩时展示完整校园招聘流程。`"
      eyebrow="AI 校园招聘运营总览"
      icon="solar:chart-square-bold-duotone"
    >
      <template #actions>
        <el-button type="primary" @click="$router.push('/jobs')">处理岗位审核</el-button>
        <el-button plain @click="$router.push('/interviews')">查看面试记录</el-button>
        <el-button plain @click="$router.push('/statistics')">打开数据看板</el-button>
      </template>
    </AppPageHeader>

    <section class="grid grid-6 dashboard-stats">
      <AppStatCard v-for="item in adminMetrics" :key="item.label" v-bind="item" />
    </section>

    <section class="grid grid-3 dashboard-section">
      <AppPanel title="近 7 日投递趋势" desc="直接从 120 条投递记录中按日期聚合" class="wide-card" :hover="false">
        <VChart class="chart-box" :option="trendOption" autoresize />
      </AppPanel>

      <AppPanel title="岗位类型占比" desc="校招、实习、管培生岗位结构" :hover="false">
        <VChart class="chart-box" :option="pieOption" autoresize />
      </AppPanel>
    </section>

    <section class="grid grid-3 dashboard-section">
      <AppPanel title="企业与岗位审核进度" desc="审核数量随企业和岗位 mock 数据变化" :hover="false">
        <VChart class="chart-box small" :option="auditOption" autoresize />
      </AppPanel>

      <AppPanel title="城市岗位分布" desc="覆盖 13 个校园招聘重点城市" :hover="false">
        <VChart class="chart-box small" :option="cityOption" autoresize />
      </AppPanel>

      <AppPanel title="投递状态分布" desc="五类投递状态均衡分布，便于演示流程" :hover="false">
        <div class="status-list" v-auto-animate>
          <div v-for="item in applyStatusDistribution" :key="item.name" class="status-row">
            <span>{{ item.name }}</span>
            <el-progress :percentage="statusPercent(item.value)" :stroke-width="10" :show-text="false" />
            <strong>{{ item.value }}</strong>
          </div>
        </div>
      </AppPanel>
    </section>

    <section class="grid grid-3 dashboard-section">
      <AppPanel title="AI 系统状态" desc="DeepSeek-V4-Pro 与模拟兜底能力" :hover="false">
        <div class="todo-list" v-auto-animate>
          <div v-for="item in aiStatus" :key="item.label" class="todo-item">
            <div class="row-between">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
            </div>
            <p>{{ item.detail }}</p>
          </div>
        </div>
      </AppPanel>

      <AppPanel title="待办事项" desc="管理员需要优先处理的业务事项" class="wide-card" :hover="false">
        <div class="todo-grid" v-auto-animate>
          <div v-for="item in adminTodos" :key="item.title" class="todo-item">
            <div class="row-between">
              <strong>{{ item.title }}</strong>
              <el-tag :type="item.level === '高' ? 'danger' : item.level === '中' ? 'warning' : 'info'" size="small">
                {{ item.level }}
              </el-tag>
            </div>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </AppPanel>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import dayjs from 'dayjs'
import AppPageHeader from '../../components/common/AppPageHeader.vue'
import AppPanel from '../../components/common/AppPanel.vue'
import AppStatCard from '../../components/common/AppStatCard.vue'
import {
  adminMetrics,
  adminTodos,
  adminTrend,
  aiStatus,
  applyStatusDistribution,
  auditProgress,
  cityDistribution,
  dataOverview,
  jobCategory
} from '../../mock/dashboard'

use([CanvasRenderer, LineChart, PieChart, BarChart, GridComponent, TooltipComponent, LegendComponent])

const today = dayjs().format('YYYY年MM月DD日')

const trendOption = computed(() => ({
  color: ['#2563EB'],
  tooltip: { trigger: 'axis' },
  grid: { left: 32, right: 18, top: 28, bottom: 28 },
  xAxis: { type: 'category', data: adminTrend.map(item => item.label), axisTick: { show: false } },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EEF2F7' } } },
  series: [{
    name: '投递数',
    type: 'line',
    smooth: true,
    symbolSize: 8,
    areaStyle: { color: 'rgba(37,99,235,0.12)' },
    lineStyle: { width: 4 },
    data: adminTrend.map(item => item.value)
  }]
}))

const pieOption = computed(() => ({
  color: ['#2563EB', '#7C3AED', '#10B981', '#F59E0B'],
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [{
    name: '岗位占比',
    type: 'pie',
    radius: ['48%', '72%'],
    center: ['50%', '42%'],
    label: { formatter: '{b}\n{d}%' },
    data: jobCategory
  }]
}))

const auditOption = computed(() => ({
  color: ['#7C3AED'],
  tooltip: { trigger: 'axis' },
  grid: { left: 34, right: 18, top: 26, bottom: 42 },
  xAxis: { type: 'category', data: auditProgress.map(item => item.label), axisLabel: { interval: 0, rotate: 18 } },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EEF2F7' } } },
  series: [{ type: 'bar', barWidth: 28, itemStyle: { borderRadius: [10, 10, 0, 0] }, data: auditProgress.map(item => item.value) }]
}))

const cityOption = computed(() => ({
  color: ['#10B981'],
  tooltip: { trigger: 'axis' },
  grid: { left: 34, right: 18, top: 26, bottom: 50 },
  xAxis: { type: 'category', data: cityDistribution.map(item => item.name), axisLabel: { interval: 0, rotate: 30 } },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EEF2F7' } } },
  series: [{ type: 'bar', barWidth: 18, itemStyle: { borderRadius: [8, 8, 0, 0] }, data: cityDistribution.map(item => item.value) }]
}))

function statusPercent(value) {
  return Math.round((value / dataOverview.applications) * 100)
}
</script>

<style scoped>
.dashboard-stats,
.dashboard-section {
  margin-top: 18px;
}

.chart-box {
  width: 100%;
  height: 300px;
}

.chart-box.small {
  height: 260px;
}

.status-list,
.todo-list,
.todo-grid {
  display: grid;
  gap: 12px;
}

.todo-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.status-row {
  display: grid;
  grid-template-columns: 82px 1fr 42px;
  align-items: center;
  gap: 12px;
  color: var(--color-text);
}

.row-between {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  color: var(--color-title);
}

.todo-item p {
  margin: 8px 0 0;
  color: var(--color-muted);
  line-height: 1.7;
}

@media (max-width: 1280px) {
  .todo-grid {
    grid-template-columns: 1fr;
  }
}
</style>
