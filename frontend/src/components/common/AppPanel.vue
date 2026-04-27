<template>
  <section class="app-card hover-lift app-panel" :class="{ 'no-hover': !hover }">
    <div v-if="$slots.header || title" class="app-panel__header">
      <div>
        <h3 v-if="title" class="section-title">{{ title }}</h3>
        <p v-if="desc" class="app-panel__desc">{{ desc }}</p>
      </div>
      <slot name="actions" />
    </div>
    <slot />
  </section>
</template>

<script setup>
defineProps({
  title: String,
  desc: String,
  hover: {
    type: Boolean,
    default: true
  }
})
</script>

<style scoped>
.app-panel {
  padding: 22px;
}

.app-panel.no-hover:hover {
  transform: none;
}

.app-panel__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 16px;
}

.app-panel__desc {
  margin: 6px 0 0;
  color: var(--color-muted);
  line-height: 1.65;
}
</style>
