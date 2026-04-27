<template>
  <header class="app-page-header">
    <div>
      <div v-if="eyebrow" class="ai-chip">
        <Icon :icon="icon" />
        {{ eyebrow }}
      </div>
      <h1>{{ title }}</h1>
      <p v-if="desc">{{ desc }}</p>
    </div>
    <div v-if="$slots.actions" class="app-page-header__actions">
      <slot name="actions" />
    </div>
  </header>
</template>

<script setup>
import { Icon } from '@iconify/vue'

defineProps({
  title: String,
  desc: String,
  eyebrow: String,
  icon: {
    type: String,
    default: 'solar:stars-bold-duotone'
  }
})
</script>

<style scoped>
.app-page-header {
  position: relative;
  overflow: hidden;
  padding: 28px;
  border-radius: 24px;
  background:
    radial-gradient(circle at 90% 15%, rgba(124, 58, 237, 0.14), transparent 28%),
    radial-gradient(circle at 12% 8%, rgba(37, 99, 235, 0.12), transparent 30%),
    #fff;
  border: var(--glass-border);
  box-shadow: var(--soft-shadow);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 18px;
}

.app-page-header h1 {
  margin: 14px 0 8px;
  color: var(--color-title);
  font-size: 30px;
  letter-spacing: 0;
}

.app-page-header p {
  margin: 0;
  max-width: 820px;
  color: var(--color-text);
  line-height: 1.8;
}

.app-page-header__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
}
</style>
