<template>
  <DashboardHome
    title="管理员首页"
    subtitle="统一管理企业审核、岗位审核、面试邀请和平台数据，支撑毕业设计全流程演示。"
    primary-text="查看统计"
    primary-path="/statistics"
    :stats="stats"
    :modules="modules"
    :focus="focus"
  />
</template>

<script setup>
import { Briefcase, DataAnalysis, OfficeBuilding, Promotion } from '@element-plus/icons-vue'
import DashboardHome from '../../components/DashboardHome.vue'

const stats = [
  { label: '企业审核', value: '可追踪', icon: OfficeBuilding },
  { label: '岗位审核', value: '在线', icon: Briefcase },
  { label: '面试记录', value: '全量', icon: Promotion },
  { label: '数据统计', value: '实时', icon: DataAnalysis }
]
const modules = [
  { title: '岗位审核', desc: '查看企业发布的岗位，完成通过或驳回审核。', path: '/jobs', icon: Briefcase },
  { title: '面试邀请', desc: '查看所有企业发出的面试邀请和学生回复。', path: '/interviews', icon: Promotion },
  { title: '数据统计', desc: '用可视化卡片展示平台核心运营指标。', path: '/statistics', icon: DataAnalysis }
]
const focus = {
  title: '审核与数据统计中枢',
  desc: '管理员首页突出企业资料审核、岗位审核和全平台数据态势，方便答辩时演示平台治理能力。',
  action: '进入数据统计',
  path: '/statistics',
  items: [
    { label: '企业审核', value: '80%', percent: 80 },
    { label: '岗位审核', value: '72%', percent: 72 },
    { label: '流程闭环', value: '88%', percent: 88 }
  ]
}
</script>
