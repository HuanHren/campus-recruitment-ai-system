<template>
  <header class="app-topbar">
    <div class="topbar-title">
      <span class="topbar-system">校园招聘 AI 管理平台</span>
      <strong>{{ title }}</strong>
      <span>{{ subtitle }}</span>
    </div>

    <div class="topbar-actions">
      <span class="role-chip">{{ roleLabel }}</span>
      <el-dropdown>
        <span class="user-entry">
          <el-avatar :size="36" class="user-avatar">
            {{ avatarText }}
          </el-avatar>
          <span class="user-copy">
            <strong>{{ user?.realName || user?.username || '用户' }}</strong>
            <small>{{ user?.username }}</small>
          </span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$emit('logout')">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { getRoleLabel } from '../../constants/roles'

const props = defineProps({
  user: {
    type: Object,
    default: null
  },
  role: {
    type: String,
    default: ''
  },
  title: {
    type: String,
    default: '工作台'
  },
  subtitle: {
    type: String,
    default: '按角色进入真实业务流程'
  }
})

defineEmits(['logout'])

const roleLabel = computed(() => getRoleLabel(props.role))
const avatarText = computed(() => {
  const source = props.user?.realName || props.user?.username || 'U'
  return source.slice(0, 1).toUpperCase()
})
</script>

<style scoped>
.app-topbar {
  min-height: 72px;
  padding: 12px 18px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #dbeafe;
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.topbar-system {
  display: inline-flex;
  align-items: center;
  margin-bottom: 4px;
  color: var(--color-primary);
  font-size: 12px;
  font-weight: 900;
}

.topbar-title strong {
  display: block;
  color: var(--title);
  font-size: 18px;
}

.topbar-title span {
  display: block;
  margin-top: 4px;
  color: var(--muted);
  font-size: 13px;
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-chip {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  color: var(--color-primary);
  background: var(--color-primary-soft);
  border: 1px solid #bfdbfe;
  font-size: 13px;
  font-weight: 800;
}

.user-entry {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 8px;
  border-radius: 14px;
  transition: background-color var(--fast-transition);
}

.user-entry:hover {
  background: #f3f6fa;
}

.user-avatar {
  background: var(--color-primary);
}

.user-copy strong {
  display: block;
  color: var(--title);
}

.user-copy small {
  color: var(--muted);
}

@media (max-width: 980px) {
  .app-topbar {
    align-items: flex-start;
    flex-direction: column;
    height: auto;
  }

  .topbar-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
