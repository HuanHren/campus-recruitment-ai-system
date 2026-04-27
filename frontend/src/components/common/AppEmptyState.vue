<template>
  <div class="app-empty-state">
    <Icon :icon="icon" />
    <h3>{{ title }}</h3>
    <p>{{ desc }}</p>
    <slot />
  </div>
</template>

<script setup>
import { Icon } from '@iconify/vue'

defineProps({
  title: {
    type: String,
    default: '暂无数据'
  },
  desc: {
    type: String,
    default: '当前没有符合条件的记录。'
  },
  icon: {
    type: String,
    default: 'solar:inbox-archive-bold-duotone'
  }
})
</script>

<style scoped>
.app-empty-state {
  min-height: 190px;
  display: grid;
  place-items: center;
  align-content: center;
  gap: 8px;
  color: var(--color-muted);
  text-align: center;
}

.app-empty-state svg {
  font-size: 42px;
  color: var(--color-primary);
}

.app-empty-state h3 {
  margin: 4px 0 0;
  color: var(--color-title);
}

.app-empty-state p {
  margin: 0;
}
</style>
