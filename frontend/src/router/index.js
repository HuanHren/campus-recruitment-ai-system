import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import MainLayout from '../layouts/MainLayout.vue'
import AdminHome from '../views/home/AdminHome.vue'
import StudentHome from '../views/home/StudentHome.vue'
import CompanyHome from '../views/home/CompanyHome.vue'
import TeacherHome from '../views/home/TeacherHome.vue'
import StudentInfo from '../views/student/StudentInfo.vue'
import CompanyInfo from '../views/company/CompanyInfo.vue'
import JobList from '../views/jobs/JobList.vue'
import JobPublish from '../views/jobs/JobPublish.vue'
import ResumeManage from '../views/resume/ResumeManage.vue'
import Applications from '../views/apply/Applications.vue'
import Interviews from '../views/interview/Interviews.vue'
import AiResume from '../views/ai/AiResume.vue'
import AiMatch from '../views/ai/AiMatch.vue'
import AiInterview from '../views/ai/AiInterview.vue'
import Statistics from '../views/statistics/Statistics.vue'
import Messages from '../views/notification/Messages.vue'
import { USER_ROLES } from '../constants/roles'
import { setupRouterGuards } from './guards'

const adminOnly = [USER_ROLES.ADMIN]
const studentOnly = [USER_ROLES.STUDENT]
const companyOnly = [USER_ROLES.COMPANY]
const primaryRoles = [USER_ROLES.ADMIN, USER_ROLES.STUDENT, USER_ROLES.COMPANY]

const routes = [
  { path: '/login', component: Login, meta: { public: true, title: '登录' } },
  { path: '/register', component: Register, meta: { public: true, title: '注册' } },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', meta: { title: '工作台' } },
      {
        path: 'admin',
        component: AdminHome,
        meta: { roles: adminOnly, title: '管理员首页', subtitle: '审核企业和岗位，查看平台流程数据' }
      },
      {
        path: 'student',
        component: StudentHome,
        meta: { roles: studentOnly, title: '学生首页', subtitle: '查看岗位、管理简历和跟进投递流程' }
      },
      {
        path: 'company',
        component: CompanyHome,
        meta: { roles: companyOnly, title: '企业首页', subtitle: '发布岗位、处理投递和发起面试邀约' }
      },
      {
        path: 'teacher',
        component: TeacherHome,
        meta: {
          roles: [USER_ROLES.TEACHER],
          title: '教师模块待建设',
          subtitle: '后端暂无教师业务 API，当前仅保留占位入口',
          deferred: true
        }
      },
      {
        path: 'student-info',
        component: StudentInfo,
        meta: { roles: studentOnly, title: '学生信息', subtitle: '维护学生基础资料' }
      },
      {
        path: 'company-info',
        component: CompanyInfo,
        meta: { roles: companyOnly, title: '企业信息', subtitle: '维护企业资料和入驻信息' }
      },
      {
        path: 'jobs',
        component: JobList,
        meta: { roles: primaryRoles, title: '岗位列表', subtitle: '按角色访问岗位浏览、审核或管理能力' }
      },
      {
        path: 'job-publish',
        component: JobPublish,
        meta: { roles: companyOnly, title: '发布岗位', subtitle: '创建企业校招岗位并提交审核' }
      },
      {
        path: 'resume',
        component: ResumeManage,
        meta: { roles: studentOnly, title: '简历管理', subtitle: '维护用于投递和 AI 优化的简历信息' }
      },
      {
        path: 'applications',
        component: Applications,
        meta: { roles: [USER_ROLES.STUDENT, USER_ROLES.COMPANY], title: '投递记录', subtitle: '查看学生投递或企业收到的简历' }
      },
      {
        path: 'interviews',
        component: Interviews,
        meta: { roles: primaryRoles, title: '面试管理', subtitle: '查看面试记录或处理面试邀约' }
      },
      {
        path: 'ai-resume',
        component: AiResume,
        meta: { roles: studentOnly, title: 'AI 简历优化', subtitle: '使用现有 SSE 接口流式生成优化结果' }
      },
      {
        path: 'ai-match',
        component: AiMatch,
        meta: { roles: studentOnly, title: 'AI 岗位匹配', subtitle: '基于简历和岗位要求分析匹配度' }
      },
      {
        path: 'ai-interview',
        component: AiInterview,
        meta: { roles: studentOnly, title: '模拟面试题', subtitle: '根据岗位要求生成面试练习题' }
      },
      {
        path: 'messages',
        component: Messages,
        meta: {
          roles: primaryRoles,
          title: '消息通知待接入',
          subtitle: '后端暂无消息控制器和数据表，当前页面不得视为真实消息功能',
          deferred: true
        }
      },
      {
        path: 'statistics',
        component: Statistics,
        meta: {
          roles: primaryRoles,
          title: '演示看板',
          subtitle: '当前后端 /api/dashboard/overview 返回硬编码演示数据',
          demo: true
        }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

setupRouterGuards(router)

export default router
