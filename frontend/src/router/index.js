import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import MainLayout from '../layouts/MainLayout.vue'
import AdminHome from '../views/home/AdminHome.vue'
import AdminStudents from '../views/admin/AdminStudents.vue'
import AdminCompanies from '../views/admin/AdminCompanies.vue'
import AdminJobs from '../views/admin/AdminJobs.vue'
import StudentHome from '../views/home/StudentHome.vue'
import CompanyHome from '../views/home/CompanyHome.vue'
import TeacherHome from '../views/home/TeacherHome.vue'
import StudentInfo from '../views/student/StudentInfo.vue'
import StudentJobs from '../views/student/StudentJobs.vue'
import StudentApplications from '../views/student/StudentApplications.vue'
import StudentInterviews from '../views/student/StudentInterviews.vue'
import CompanyInfo from '../views/company/CompanyInfo.vue'
import CompanyProfile from '../views/company/CompanyProfile.vue'
import CompanyJobs from '../views/company/CompanyJobs.vue'
import CompanyApplications from '../views/company/CompanyApplications.vue'
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
        path: 'admin/students',
        component: AdminStudents,
        meta: { roles: adminOnly, title: '学生信息管理', subtitle: '查看学生基础资料列表' }
      },
      {
        path: 'admin/companies',
        component: AdminCompanies,
        meta: { roles: adminOnly, title: '企业信息管理', subtitle: '查看企业资料并处理审核状态' }
      },
      {
        path: 'admin/jobs',
        component: AdminJobs,
        meta: { roles: adminOnly, title: '岗位管理', subtitle: '查看岗位列表并处理岗位审核' }
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
        path: 'student/jobs',
        component: StudentJobs,
        meta: { roles: studentOnly, title: '学生岗位浏览', subtitle: '浏览岗位详情并发起投递' }
      },
      {
        path: 'student/applications',
        component: StudentApplications,
        meta: { roles: studentOnly, title: '我的投递', subtitle: '查看学生本人岗位投递状态' }
      },
      {
        path: 'student/interviews',
        component: StudentInterviews,
        meta: { roles: studentOnly, title: '面试邀请', subtitle: '查看并回复学生本人面试邀请' }
      },
      {
        path: 'company-info',
        component: CompanyInfo,
        meta: { roles: companyOnly, title: '企业信息', subtitle: '维护企业资料和入驻信息' }
      },
      {
        path: 'company/profile',
        component: CompanyProfile,
        meta: { roles: companyOnly, title: '企业资料', subtitle: '维护企业认证资料和审核状态' }
      },
      {
        path: 'company/jobs',
        component: CompanyJobs,
        meta: { roles: companyOnly, title: '岗位管理', subtitle: '发布、编辑和下架企业岗位' }
      },
      {
        path: 'company/applications',
        component: CompanyApplications,
        meta: { roles: companyOnly, title: '投递处理', subtitle: '查看投递、更新状态并发起面试邀请' }
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
