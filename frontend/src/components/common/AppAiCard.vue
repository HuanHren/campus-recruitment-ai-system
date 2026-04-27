<template>
  <div class="app-ai-card hover-lift">
    <div class="app-ai-card__icon">
      <Icon :icon="icon" />
    </div>
    <div>
      <div class="ai-chip">{{ model }}</div>
      <h3>{{ title }}</h3>
      <p>{{ desc }}</p>
    </div>
    <slot />
  </div>
</template>

<script setup>
import { Icon } from '@iconify/vue'

defineProps({
  title: String,
  desc: String,
  icon: {
    type: String,
    default: 'solar:stars-bold-duotone'
  },
  model: {
    type: String,
    default: 'DeepSeek-V4-Pro'
  }
})
</script>

<style scoped>
.app-ai-card {
  padding: 20px;
  border-radius: 22px;
  background:
    radial-gradient(circle at 86% 12%, rgba(124, 58, 237, 0.16), transparent 28%),
    linear-gradient(135deg, #fff, #f8faff);
  border: var(--glass-border);
  box-shadow: var(--soft-shadow);
}

.app-ai-card__icon {
  width: 48px;
  height: 48px;
  border-radius: 17px;
  display: grid;
  place-items: center;
  color: #fff;
  font-size: 25px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-ai));
  margin-bottom: 14px;
}

.app-ai-card h3 {
  color: var(--color-title);
  margin: 14px 0 8px;
}

.app-ai-card p {
  margin: 0;
  color: var(--color-text);
  line-height: 1.75;
}
</style>
