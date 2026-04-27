<template>
  <div class="app-shell" :class="{ 'is-collapsed': collapsed }">
    <aside class="sidebar">
      <div class="layout-brand">
        <span class="brand-mark"><Icon icon="solar:cpu-bolt-bold-duotone" /></span>
        <span class="brand-text">
          AI 校园招聘
          <small>DeepSeek 智能平台</small>
        </span>
      </div>

      <button class="collapse-btn" type="button" @click="collapsed = !collapsed">
        <Icon :icon="collapsed ? 'solar:sidebar-code-bold-duotone' : 'solar:sidebar-minimalistic-bold-duotone'" />
        <span class="collapse-label">{{ collapsed ? '展开菜单' : '折叠菜单' }}</span>
      </button>

      <div class="menu-scroll">
        <div v-for="group in menus" :key="group.group" class="menu-group">
          <div class="menu-group-title">{{ group.group }}</div>
          <el-menu router :collapse="collapsed" :default-active="$route.path" class="side-menu">
            <el-menu-item v-for="item in group.children" :key="item.path" :index="item.path">
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
          </el-menu>
        </div>
      </div>
    </aside>

    <main class="main-panel">
      <header class="topbar">
        <div class="topbar-title">
          <strong>{{ greeting }}</strong>
          <span>{{ today }}，聚焦 AI 驱动的校园招聘全流程管理</span>
        </div>
        <div class="topbar-actions">
          <span class="ai-chip"><el-icon><MagicStick /></el-icon> DeepSeek-V4-Pro</span>
          <el-button :icon="DataAnalysis" plain @click="$router.push('/statistics')">数据看板</el-button>
          <el-dropdown>
            <span class="user-entry">
              <el-avatar :size="36" style="background:linear-gradient(135deg,#2563eb,#7c3aed);">
                {{ auth.user?.username?.slice(0, 1)?.toUpperCase() }}
              </el-avatar>
              <span>
                <strong style="display:block;color:var(--title);">{{ auth.user?.realName || auth.user?.username }}</strong>
                <small style="color:var(--muted);">{{ roleName }}</small>
              </span>
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
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'
import { useDateFormat, useNow, useWindowSize } from '@vueuse/core'
import dayjs from 'dayjs'
import { useAuthStore } from '../stores/auth'
import {
  Briefcase,
  Cpu,
  DataAnalysis,
  Document,
  HomeFilled,
  MagicStick,
  OfficeBuilding,
  Postcard,
  Promotion,
  Reading,
  Setting,
  User
} from '@element-plus/icons-vue'

const router = useRouter()
const auth = useAuthStore()
const { width } = useWindowSize()
const collapsed = ref(width.value < 1280)
const now = useNow()
const dateText = useDateFormat(now, 'YYYY年MM月DD日')

const roleMap = {
  ADMIN: '管理员',
  STUDENT: '学生',
  COMPANY: '企业',
  TEACHER: '就业老师'
}

const groupedMenus = {
  ADMIN: [
    { group: '工作台', children: [{ title: '管理员首页', path: '/admin', icon: HomeFilled }] },
    {
      group: '招聘管理',
      children: [
        { title: '岗位审核', path: '/jobs', icon: Briefcase },
        { title: '面试记录', path: '/interviews', icon: Promotion }
      ]
    },
    { group: '用户管理', children: [{ title: '数据统计', path: '/statistics', icon: DataAnalysis }] },
    { group: 'AI 能力', children: [{ title: 'AI 运行状态', path: '/statistics', icon: MagicStick }] },
    { group: '系统管理', children: [{ title: '平台配置', path: '/statistics', icon: Setting }] }
  ],
  STUDENT: [
    { group: '工作台', children: [{ title: '学生首页', path: '/student', icon: HomeFilled }] },
    {
      group: '招聘管理',
      children: [
        { title: '岗位列表', path: '/jobs', icon: Briefcase },
        { title: '投递记录', path: '/applications', icon: Postcard },
        { title: '面试邀请', path: '/interviews', icon: Promotion }
      ]
    },
    { group: '用户管理', children: [{ title: '学生信息', path: '/student-info', icon: User }, { title: '简历管理', path: '/resume', icon: Document }] },
    {
      group: 'AI 能力',
      children: [
        { title: 'AI简历优化', path: '/ai-resume', icon: MagicStick },
        { title: 'AI岗位匹配', path: '/ai-match', icon: Reading },
        { title: '模拟面试题', path: '/ai-interview', icon: Cpu }
      ]
    },
    { group: '系统管理', children: [{ title: '数据统计', path: '/statistics', icon: DataAnalysis }] }
  ],
  COMPANY: [
    { group: '工作台', children: [{ title: '企业首页', path: '/company', icon: HomeFilled }] },
    {
      group: '招聘管理',
      children: [
        { title: '岗位列表', path: '/jobs', icon: Briefcase },
        { title: '岗位发布', path: '/job-publish', icon: Promotion },
        { title: '投递记录', path: '/applications', icon: Postcard },
        { title: '面试邀请', path: '/interviews', icon: Promotion }
      ]
    },
    { group: '用户管理', children: [{ title: '企业信息', path: '/company-info', icon: OfficeBuilding }] },
    { group: 'AI 能力', children: [{ title: '生成岗位描述', path: '/job-publish', icon: MagicStick }] },
    { group: '系统管理', children: [{ title: '数据统计', path: '/statistics', icon: DataAnalysis }] }
  ],
  TEACHER: [
    { group: '工作台', children: [{ title: '就业老师首页', path: '/teacher', icon: HomeFilled }] },
    { group: '招聘管理', children: [{ title: '岗位列表', path: '/jobs', icon: Briefcase }] },
    { group: '系统管理', children: [{ title: '数据统计', path: '/statistics', icon: DataAnalysis }] }
  ]
}

const menus = computed(() => groupedMenus[auth.role] || [])
const roleName = computed(() => roleMap[auth.role] || '用户')
const greeting = computed(() => `${roleName.value}工作台`)
const today = computed(() => {
  const week = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][dayjs(now.value).day()]
  return `${dateText.value} ${week}`
})

function logout() {
  auth.logout()
  router.push('/login')
}
</script>
