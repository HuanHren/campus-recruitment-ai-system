<template>
  <div class="app-shell-v2" :class="{ 'is-collapsed': collapsed }">
    <RoleSidebar :collapsed="collapsed" :role="auth.role" @toggle="collapsed = !collapsed" />
    <main class="main-panel">
      <AppTopbar
        :user="auth.user"
        :role="auth.role"
        :title="pageTitle"
        :subtitle="pageSubtitle"
        @logout="logout"
      />
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWindowSize } from '@vueuse/core'
import AppTopbar from '../components/layout/AppTopbar.vue'
import RoleSidebar from '../components/layout/RoleSidebar.vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const { width } = useWindowSize()
const collapsed = ref(width.value < 1280)

const pageTitle = computed(() => route.meta.title || '工作台')
const pageSubtitle = computed(() => route.meta.subtitle || '基于当前角色访问真实业务功能')

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-shell-v2 {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 248px 1fr;
  background: var(--color-bg);
  transition: grid-template-columns var(--normal-transition);
}

.app-shell-v2.is-collapsed {
  grid-template-columns: 86px 1fr;
}

.main-panel {
  min-width: 0;
  padding: 22px 28px 36px;
}

@media (max-width: 980px) {
  .app-shell-v2,
  .app-shell-v2.is-collapsed {
    grid-template-columns: 1fr;
  }

  .main-panel {
    padding: 16px;
  }
}
</style>
