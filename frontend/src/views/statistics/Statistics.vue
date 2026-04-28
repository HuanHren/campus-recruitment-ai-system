<template>
  <div class="page" v-auto-animate>
    <AppPageHeader
      title="数据统计看板"
      desc="统计数据从同一批演示数据中计算，和管理员首页、岗位列表、投递记录、面试邀请保持一致。"
      eyebrow="毕业答辩数据展示"
      icon="solar:chart-2-bold-duotone"
    >
      <template #actions>
        <el-button type="primary" @click="load">刷新数据</el-button>
      </template>
    </AppPageHeader>

    <section class="statistics-grid">
      <AppStatCard v-for="item in cards" :key="item.label" v-bind="item" />
    </section>

    <section class="grid grid-2 statistics-section">
      <AppPanel title="招聘流程转化" desc="从岗位发布到录用推进的完整业务链路" :hover="false">
        <div v-for="item in recruitmentConversion" :key="item.label" class="bar-row">
          <span>{{ item.label }}</span>
          <div class="bar-track"><div class="bar-fill" :style="{ width: item.percent + '%' }" /></div>
          <strong>{{ item.value }}</strong>
        </div>
      </AppPanel>

      <AppPanel title="投递状态分布" desc="120 条投递记录均衡覆盖不同处理状态" :hover="false">
        <div v-for="item in applyStatusDistribution" :key="item.name" class="bar-row">
          <span>{{ item.name }}</span>
          <div class="bar-track"><div class="bar-fill purple" :style="{ width: statusPercent(item.value) + '%' }" /></div>
          <strong>{{ item.value }}</strong>
        </div>
      </AppPanel>
    </section>

    <section class="grid grid-2 statistics-section">
      <AppPanel title="岗位类型结构" desc="岗位以实习、校招和管培生为核心" :hover="false">
        <div v-for="item in jobCategory" :key="item.name" class="bar-row">
          <span>{{ item.name }}</span>
          <div class="bar-track"><div class="bar-fill green" :style="{ width: jobPercent(item.value) + '%' }" /></div>
          <strong>{{ item.value }}</strong>
        </div>
      </AppPanel>

      <AppPanel title="城市岗位分布" desc="城市分布更自然，避免集中在北京和上海" :hover="false">
        <div class="city-list">
          <el-tag v-for="item in cityDistribution" :key="item.name" effect="plain">
            {{ item.name }} {{ item.value }}
          </el-tag>
        </div>
      </AppPanel>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import AppPageHeader from '../../components/common/AppPageHeader.vue'
import AppPanel from '../../components/common/AppPanel.vue'
import AppStatCard from '../../components/common/AppStatCard.vue'
import {
  adminMetrics,
  applyStatusDistribution,
  cityDistribution,
  dataOverview,
  jobCategory,
  recruitmentConversion
} from '../../mock/dashboard'

const cards = computed(() => [
  ...adminMetrics.slice(0, 4),
  { label: '面试邀请', value: dataOverview.interviews, trend: '流程推进', desc: '覆盖线上、线下、电话面试', tone: 'green', icon: 'solar:calendar-mark-bold-duotone' },
  { label: '在线岗位', value: dataOverview.onlineJobs, trend: '学生可见', desc: '用于岗位列表分页展示', tone: 'purple', icon: 'solar:case-minimalistic-bold-duotone' }
])

function statusPercent(value) {
  return Math.round((value / dataOverview.applications) * 100)
}

function jobPercent(value) {
  return Math.round((value / dataOverview.jobs) * 100)
}

function load() {
  ElMessage.success('统计数据已根据演示数据刷新')
}
</script>

<style scoped>
.statistics-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.statistics-section {
  margin-top: 18px;
}

.purple {
  background: linear-gradient(90deg, #7c3aed, #a78bfa);
}

.green {
  background: linear-gradient(90deg, #10b981, #6ee7b7);
}

.city-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

@media (max-width: 1440px) {
  .statistics-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
</style>
