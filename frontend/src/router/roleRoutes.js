import { USER_ROLES } from '../constants/roles'

export const routeNames = Object.freeze({
  ADMIN_HOME: 'admin-home',
  STUDENT_HOME: 'student-home',
  COMPANY_HOME: 'company-home',
  TEACHER_HOME: 'teacher-home'
})

export const roleNavigation = Object.freeze({
  [USER_ROLES.ADMIN]: [
    {
      group: '工作台',
      children: [
        { title: '管理员首页', path: '/admin', icon: 'HomeFilled' }
      ]
    },
    {
      group: '后台管理',
      children: [
        { title: '学生信息管理', path: '/admin/students', icon: 'User' },
        { title: '企业信息管理', path: '/admin/companies', icon: 'OfficeBuilding' },
        { title: '岗位管理', path: '/admin/jobs', icon: 'Briefcase' },
        { title: '面试记录', path: '/interviews', icon: 'Promotion' }
      ]
    },
    {
      group: '平台数据',
      children: [
        { title: '演示看板', path: '/statistics', icon: 'DataAnalysis', demo: true }
      ]
    }
  ],
  [USER_ROLES.STUDENT]: [
    {
      group: '工作台',
      children: [
        { title: '学生首页', path: '/student', icon: 'HomeFilled' }
      ]
    },
    {
      group: '求职流程',
      children: [
        { title: '岗位浏览', path: '/student/jobs', icon: 'Briefcase' },
        { title: '投递记录', path: '/student/applications', icon: 'Postcard' },
        { title: '面试邀请', path: '/student/interviews', icon: 'Promotion' }
      ]
    },
    {
      group: '个人资料',
      children: [
        { title: '学生信息', path: '/student-info', icon: 'User' },
        { title: '简历管理', path: '/resume', icon: 'Document' }
      ]
    },
    {
      group: 'AI 能力',
      children: [
        { title: '简历优化', path: '/ai-resume', icon: 'MagicStick' },
        { title: '岗位匹配', path: '/ai-match', icon: 'Reading' },
        { title: '模拟面试题', path: '/ai-interview', icon: 'Cpu' }
      ]
    }
  ],
  [USER_ROLES.COMPANY]: [
    {
      group: '工作台',
      children: [
        { title: '企业首页', path: '/company', icon: 'HomeFilled' }
      ]
    },
    {
      group: '招聘流程',
      children: [
        { title: '岗位管理', path: '/company/jobs', icon: 'Briefcase' },
        { title: '投递处理', path: '/company/applications', icon: 'Postcard' }
      ]
    },
    {
      group: '企业资料',
      children: [
        { title: '企业资料', path: '/company/profile', icon: 'OfficeBuilding' }
      ]
    }
  ],
  [USER_ROLES.TEACHER]: [
    {
      group: '待建设',
      children: [
        { title: '教师占位页', path: '/teacher', icon: 'HomeFilled', deferred: true }
      ]
    }
  ]
})

export function getRoleNavigation(role) {
  return roleNavigation[role] || []
}
