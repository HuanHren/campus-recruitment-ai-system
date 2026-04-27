<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="管理员工作台"
      :desc="`今天是 ${today}，平台运行正常。当前重点关注企业认证、岗位审核、AI 调用与投递趋势。`"
      eyebrow="AI 校园招聘运营总览"
      icon="solar:chart-square-bold-duotone"
    >
      <template #actions>
        <el-button type="primary" @click="$router.push('/jobs')">处理岗位审核</el-button>
        <el-button plain @click="$router.push('/interviews')">查看面试记录</el-button>
        <el-button plain @click="$router.push('/statistics')">打开数据看板</el-button>
      </template>
    </AppPageHeader>

    <section class="grid grid-6" style="margin-top:18px;">
      <AppStatCard v-for="item in adminMetrics" :key="item.label" v-bind="item" />
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <AppPanel title="近 7 日投递趋势" desc="用于观察学生投递活跃度和招聘节奏变化" class="wide-card" :hover="false">
        <VChart class="chart-box" :option="trendOption" autoresize />
      </AppPanel>

      <AppPanel title="岗位分类占比" desc="展示当前招聘岗位结构" :hover="false">
        <VChart class="chart-box" :option="pieOption" autoresize />
      </AppPanel>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <AppPanel title="企业审核进度" desc="认证、岗位和资料补充的处理情况" :hover="false">
        <VChart class="chart-box small" :option="auditOption" autoresize />
      </AppPanel>

      <AppPanel title="AI 系统状态" desc="DeepSeek-V4-Pro 调用与兜底演示状态" :hover="false">
        <div class="todo-list" v-auto-animate>
          <div v-for="item in aiStatus" :key="item.label" class="todo-item">
            <div style="display:flex;align-items:center;justify-content:space-between;">
              <span>{{ item.label }}</span>
              <strong style="color:var(--title);">{{ item.value }}</strong>
            </div>
            <div class="muted" style="margin-top:6px;font-size:13px;">{{ item.detail }}</div>
          </div>
        </div>
      </AppPanel>

      <AppPanel title="待办事项" desc="管理员需要优先处理的业务事项" :hover="false">
        <div class="todo-list" v-auto-animate>
          <div v-for="item in adminTodos" :key="item.title" class="todo-item">
            <div style="display:flex;justify-content:space-between;gap:10px;">
              <strong style="color:var(--title);">{{ item.title }}</strong>
              <el-tag :type="item.level === '高' ? 'danger' : item.level === '中' ? 'warning' : 'info'" size="small">
                {{ item.level }}
              </el-tag>
            </div>
            <p style="margin:8px 0 0;line-height:1.7;">{{ item.desc }}</p>
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
import { adminMetrics, adminTodos, adminTrend, aiStatus, auditProgress, jobCategory } from '../../mock/dashboard'

use([CanvasRenderer, LineChart, PieChart, BarChart, GridComponent, TooltipComponent, LegendComponent])

const today = dayjs().format('YYYY年MM月DD日')

const trendOption = computed(() => ({
  color: ['#2563EB'],
  tooltip: { trigger: 'axis' },
  grid: { left: 28, right: 18, top: 28, bottom: 28 },
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
  grid: { left: 34, right: 18, top: 26, bottom: 36 },
  xAxis: { type: 'category', data: auditProgress.map(item => item.label), axisLabel: { interval: 0, rotate: 16 } },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EEF2F7' } } },
  series: [{ type: 'bar', barWidth: 28, itemStyle: { borderRadius: [10, 10, 0, 0] }, data: auditProgress.map(item => item.value) }]
}))
</script>

<style scoped>
.chart-box {
  width: 100%;
  height: 300px;
}

.chart-box.small {
  height: 260px;
}
</style>
