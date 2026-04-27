<template>
  <div class="stat-card enhanced-stat">
    <div class="enhanced-stat__top">
      <span class="enhanced-stat__icon" :style="{ background: iconBg }">
        <Icon :icon="icon" />
      </span>
      <span v-if="trend" class="stat-trend">{{ trend }}</span>
    </div>
    <div class="num">{{ value }}</div>
    <div class="label">{{ label }}</div>
    <p v-if="desc">{{ desc }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Icon } from '@iconify/vue'

const props = defineProps({
  label: String,
  value: [String, Number],
  trend: String,
  desc: String,
  icon: {
    type: String,
    default: 'solar:chart-2-bold-duotone'
  },
  tone: {
    type: String,
    default: 'blue'
  }
})

const toneMap = {
  blue: 'linear-gradient(135deg,#2563EB,#60A5FA)',
  purple: 'linear-gradient(135deg,#7C3AED,#A78BFA)',
  cyan: 'linear-gradient(135deg,#06B6D4,#67E8F9)',
  green: 'linear-gradient(135deg,#10B981,#6EE7B7)',
  orange: 'linear-gradient(135deg,#F59E0B,#FDBA74)'
}

const iconBg = computed(() => toneMap[props.tone] || toneMap.blue)
</script>

<style scoped>
.enhanced-stat__top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.enhanced-stat__icon {
  width: 42px;
  height: 42px;
  border-radius: 15px;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
}

.enhanced-stat p {
  margin: 8px 0 0;
  color: var(--color-muted);
  line-height: 1.6;
}
</style>
