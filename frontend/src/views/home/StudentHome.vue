<template>
  <DashboardHome
    title="学生首页"
    subtitle="维护个人信息与简历，浏览岗位、完成投递，并使用 AI 能力提升求职质量。"
    primary-text="浏览岗位"
    primary-path="/jobs"
    :stats="stats"
    :modules="modules"
    :focus="focus"
  />
</template>

<script setup>
import { Briefcase, Document, MagicStick, Promotion } from '@element-plus/icons-vue'
import DashboardHome from '../../components/DashboardHome.vue'

const stats = [
  { label: '简历状态', value: '可优化', icon: Document },
  { label: '岗位机会', value: '精选', icon: Briefcase },
  { label: 'AI助手', value: '可用', icon: MagicStick },
  { label: '面试邀请', value: '跟踪', icon: Promotion }
]
const modules = [
  { title: '简历管理', desc: '编辑一份结构完整、可投递的个人简历。', path: '/resume', icon: Document },
  { title: 'AI简历优化', desc: '一键优化简历表达，让项目经历更有说服力。', path: '/ai-resume', icon: MagicStick },
  { title: '投递记录', desc: '查看岗位投递进度，掌握企业处理状态。', path: '/applications', icon: Promotion }
]
const focus = {
  title: 'AI 推荐与投递进度',
  desc: '学生端突出 AI 简历优化、岗位匹配分析和投递状态跟踪，帮助学生从岗位发现走到面试邀请。',
  action: '查看AI匹配',
  path: '/ai-match',
  items: [
    { label: '简历完善', value: '76%', percent: 76 },
    { label: '岗位匹配', value: '82%', percent: 82 },
    { label: '投递推进', value: '64%', percent: 64 }
  ]
}
</script>
