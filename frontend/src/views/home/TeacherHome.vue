<template>
  <DashboardHome
    title="就业老师首页"
    subtitle="关注学生就业服务、岗位供给与整体招聘进展，为学院就业管理提供支撑。"
    primary-text="查看数据"
    primary-path="/statistics"
    :stats="stats"
    :modules="modules"
    :focus="focus"
  />
</template>

<script setup>
import { Briefcase, DataAnalysis, Reading, TrendCharts } from '@element-plus/icons-vue'
import DashboardHome from '../../components/DashboardHome.vue'

const stats = [
  { label: '就业观察', value: '全局', icon: TrendCharts },
  { label: '岗位供给', value: '可查', icon: Briefcase },
  { label: '学生服务', value: '跟踪', icon: Reading },
  { label: '统计分析', value: '看板', icon: DataAnalysis }
]
const modules = [
  { title: '岗位列表', desc: '查看平台岗位供给，为学生推荐合适方向。', path: '/jobs', icon: Briefcase },
  { title: '数据统计', desc: '观察系统核心数据，用于就业服务汇报。', path: '/statistics', icon: DataAnalysis },
  { title: '就业指导', desc: '结合岗位要求指导学生优化简历和面试表达。', path: '/statistics', icon: Reading }
]
const focus = {
  title: '就业指导观察台',
  desc: '就业老师端聚焦岗位供给、学生求职进展和就业服务质量，为学院就业管理提供展示入口。',
  action: '查看统计',
  path: '/statistics',
  items: [
    { label: '岗位供给', value: '78%', percent: 78 },
    { label: '学生服务', value: '70%', percent: 70 },
    { label: '就业跟踪', value: '62%', percent: 62 }
  ]
}
</script>
