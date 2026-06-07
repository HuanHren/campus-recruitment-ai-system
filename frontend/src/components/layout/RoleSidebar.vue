<template>
  <aside class="role-sidebar" :class="{ 'is-collapsed': collapsed }">
    <div class="layout-brand">
      <span class="brand-mark">
        <el-icon><Cpu /></el-icon>
      </span>
      <span class="brand-text">
        AI 校园招聘
        <small>角色工作台</small>
      </span>
    </div>

    <button class="collapse-btn" type="button" @click="$emit('toggle')">
      <el-icon>
        <Expand v-if="collapsed" />
        <Fold v-else />
      </el-icon>
      <span class="collapse-label">{{ collapsed ? '展开菜单' : '收起菜单' }}</span>
    </button>

    <div class="menu-scroll">
      <div v-for="group in menus" :key="group.group" class="menu-group">
        <div class="menu-group-title">{{ group.group }}</div>
        <el-menu router :collapse="collapsed" :default-active="activePath" class="side-menu">
          <el-menu-item v-for="item in group.children" :key="item.path" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
            <small v-if="item.demo || item.deferred" class="nav-note">
              {{ item.deferred ? '待建设' : '演示' }}
            </small>
          </el-menu-item>
        </el-menu>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Cpu, Expand, Fold } from '@element-plus/icons-vue'
import { getRoleNavigation } from '../../router/roleRoutes'

const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  },
  role: {
    type: String,
    default: ''
  }
})

defineEmits(['toggle'])

const route = useRoute()
const menus = computed(() => getRoleNavigation(props.role))
const activePath = computed(() => route.path)
</script>

<style scoped>
.role-sidebar {
  height: 100vh;
  padding: 18px 12px;
  position: sticky;
  top: 0;
  border-right: 1px solid var(--line);
  background: var(--color-sidebar);
  display: flex;
  flex-direction: column;
  gap: 12px;
  box-shadow: 8px 0 24px rgba(15, 23, 42, 0.035);
}

.layout-brand {
  min-height: 50px;
  padding: 4px 8px 14px;
}

.brand-mark {
  font-size: 20px;
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
  transition: opacity var(--fast-transition);
}

.brand-text small {
  margin-top: 4px;
  color: var(--muted);
  font-size: 12px;
  font-weight: 600;
}

.is-collapsed .brand-text,
.is-collapsed .menu-group-title,
.is-collapsed .collapse-label,
.is-collapsed .nav-note {
  opacity: 0;
  width: 0;
  overflow: hidden;
}

.collapse-btn {
  width: 100%;
  height: 36px;
  border-radius: 12px;
  border: 1px solid var(--line);
  background: #fff;
  color: var(--text);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: transform var(--fast-transition), box-shadow var(--fast-transition), border-color var(--fast-transition);
}

.collapse-btn:hover {
  transform: translateY(-1px);
  border-color: #bfdbfe;
  box-shadow: var(--shadow-sm);
}

.menu-scroll {
  min-height: 0;
  flex: 1;
  overflow: auto;
  padding-right: 2px;
}

.menu-group {
  margin: 12px 0 18px;
}

.menu-group-title {
  margin: 0 10px 8px;
  color: var(--muted);
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0;
  white-space: nowrap;
}

.side-menu {
  border-right: 0;
  background: transparent;
}

.side-menu.el-menu--collapse {
  width: 56px;
}

.side-menu .el-menu-item {
  height: 42px;
  margin: 4px 0;
  border-radius: 12px;
  color: var(--text);
}

.side-menu .el-menu-item:hover {
  color: var(--color-primary);
  background: rgba(29, 78, 216, 0.07);
}

.side-menu .el-menu-item.is-active {
  color: #fff;
  background: var(--color-primary);
  box-shadow: 0 8px 18px rgba(29, 78, 216, 0.2);
  font-weight: 800;
}

.side-menu .el-menu-item.is-active :deep(.el-icon),
.side-menu .el-menu-item.is-active span,
.side-menu .el-menu-item.is-active .nav-note {
  color: #fff;
}

.nav-note {
  margin-left: auto;
  color: var(--muted);
  font-size: 11px;
  font-weight: 700;
}

@media (max-width: 980px) {
  .role-sidebar {
    height: auto;
    min-height: auto;
    position: relative;
  }
}
</style>
