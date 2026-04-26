<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="layout-brand">
        <span class="brand-mark"><el-icon><Briefcase /></el-icon></span>
        <span>校园招聘智能平台</span>
      </div>
      <el-menu router :default-active="$route.path" class="side-menu">
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="main-panel">
      <header class="topbar">
        <div>
          <strong>{{ greeting }}</strong>
          <div style="color:#667085;margin-top:4px;">{{ today }}，继续推进更高质量的就业服务</div>
        </div>
        <div style="display:flex;align-items:center;gap:12px;">
          <el-tag effect="dark" type="success">{{ roleName }}</el-tag>
          <el-dropdown>
            <span style="display:flex;align-items:center;gap:8px;cursor:pointer;">
              <el-avatar :size="34">{{ auth.user?.username?.slice(0, 1)?.toUpperCase() }}</el-avatar>
              {{ auth.user?.realName || auth.user?.username }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  Briefcase,
  DataAnalysis,
  Document,
  HomeFilled,
  MagicStick,
  OfficeBuilding,
  Postcard,
  Promotion,
  Reading,
  User
} from '@element-plus/icons-vue'

const router = useRouter()
const auth = useAuthStore()

const roleMap = {
  ADMIN: '管理员',
  STUDENT: '学生',
  COMPANY: '企业',
  TEACHER: '就业老师'
}

const baseMenus = {
  ADMIN: [
    { title: '管理员首页', path: '/admin', icon: HomeFilled },
    { title: '岗位列表', path: '/jobs', icon: Briefcase },
    { title: '面试邀请', path: '/interviews', icon: Promotion },
    { title: '数据统计', path: '/statistics', icon: DataAnalysis }
  ],
  STUDENT: [
    { title: '学生首页', path: '/student', icon: HomeFilled },
    { title: '学生信息', path: '/student-info', icon: User },
    { title: '岗位列表', path: '/jobs', icon: Briefcase },
    { title: '简历管理', path: '/resume', icon: Document },
    { title: '投递记录', path: '/applications', icon: Postcard },
    { title: '面试邀请', path: '/interviews', icon: Promotion },
    { title: 'AI简历优化', path: '/ai-resume', icon: MagicStick },
    { title: 'AI岗位匹配', path: '/ai-match', icon: Reading },
    { title: '数据统计', path: '/statistics', icon: DataAnalysis }
  ],
  COMPANY: [
    { title: '企业首页', path: '/company', icon: HomeFilled },
    { title: '企业信息', path: '/company-info', icon: OfficeBuilding },
    { title: '岗位列表', path: '/jobs', icon: Briefcase },
    { title: '岗位发布', path: '/job-publish', icon: Promotion },
    { title: '投递记录', path: '/applications', icon: Postcard },
    { title: '面试邀请', path: '/interviews', icon: Promotion },
    { title: '数据统计', path: '/statistics', icon: DataAnalysis }
  ],
  TEACHER: [
    { title: '就业老师首页', path: '/teacher', icon: HomeFilled },
    { title: '岗位列表', path: '/jobs', icon: Briefcase },
    { title: '数据统计', path: '/statistics', icon: DataAnalysis }
  ]
}

const menus = computed(() => baseMenus[auth.role] || [])
const roleName = computed(() => roleMap[auth.role] || '用户')
const greeting = computed(() => `${roleName.value}工作台`)
const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })

function logout() {
  auth.logout()
  router.push('/login')
}
</script>
