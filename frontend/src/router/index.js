import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
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
import Statistics from '../views/statistics/Statistics.vue'

const routes = [
  { path: '/login', component: Login, meta: { public: true } },
  { path: '/register', component: Register, meta: { public: true } },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', redirect: '/student' },
      { path: 'admin', component: AdminHome, meta: { roles: ['ADMIN'] } },
      { path: 'student', component: StudentHome, meta: { roles: ['STUDENT'] } },
      { path: 'company', component: CompanyHome, meta: { roles: ['COMPANY'] } },
      { path: 'teacher', component: TeacherHome, meta: { roles: ['TEACHER'] } },
      { path: 'student-info', component: StudentInfo, meta: { roles: ['STUDENT'] } },
      { path: 'company-info', component: CompanyInfo, meta: { roles: ['COMPANY'] } },
      { path: 'jobs', component: JobList, meta: { roles: ['ADMIN', 'STUDENT', 'COMPANY', 'TEACHER'] } },
      { path: 'job-publish', component: JobPublish, meta: { roles: ['COMPANY'] } },
      { path: 'resume', component: ResumeManage, meta: { roles: ['STUDENT'] } },
      { path: 'applications', component: Applications, meta: { roles: ['STUDENT', 'COMPANY'] } },
      { path: 'interviews', component: Interviews, meta: { roles: ['ADMIN', 'STUDENT', 'COMPANY'] } },
      { path: 'ai-resume', component: AiResume, meta: { roles: ['STUDENT'] } },
      { path: 'ai-match', component: AiMatch, meta: { roles: ['STUDENT'] } },
      { path: 'statistics', component: Statistics, meta: { roles: ['ADMIN', 'STUDENT', 'COMPANY', 'TEACHER'] } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.public) {
    return true
  }
  if (!auth.isLogin) {
    return '/login'
  }
  const roles = to.meta.roles
  if (roles && !roles.includes(auth.role)) {
    return auth.homePath
  }
  if (to.path === '/') {
    return auth.homePath
  }
  return true
})

export default router
