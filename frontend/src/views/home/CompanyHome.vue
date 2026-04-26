<template>
  <DashboardHome
    title="企业首页"
    subtitle="完善企业资料，发布招聘岗位，处理投递记录并向合适学生发送面试邀请。"
    primary-text="发布岗位"
    primary-path="/job-publish"
    :stats="stats"
    :modules="modules"
    :focus="focus"
  />
</template>

<script setup>
import { Briefcase, EditPen, OfficeBuilding, Postcard } from '@element-plus/icons-vue'
import DashboardHome from '../../components/DashboardHome.vue'

const stats = [
  { label: '企业资料', value: '待完善', icon: OfficeBuilding },
  { label: '岗位发布', value: '快捷', icon: Briefcase },
  { label: '投递处理', value: '闭环', icon: Postcard },
  { label: '岗位文案', value: 'AI', icon: EditPen }
]
const modules = [
  { title: '企业信息', desc: '维护企业认证资料，等待管理员审核通过。', path: '/company-info', icon: OfficeBuilding },
  { title: '岗位发布', desc: '创建岗位并提交审核，生成适合校园招聘的岗位内容。', path: '/job-publish', icon: Briefcase },
  { title: '投递记录', desc: '查看收到的学生简历并推进面试流程。', path: '/applications', icon: Postcard }
]
const focus = {
  title: '岗位发布与简历筛选',
  desc: '企业端强调快速发布岗位、查看收到的简历、筛选合适候选人并推进面试邀请。',
  action: '处理投递记录',
  path: '/applications',
  items: [
    { label: '岗位发布', value: '70%', percent: 70 },
    { label: '简历筛选', value: '66%', percent: 66 },
    { label: '面试推进', value: '58%', percent: 58 }
  ]
}
</script>
