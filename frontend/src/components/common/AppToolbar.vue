<template>
  <div class="app-toolbar" v-auto-animate>
    <slot />
  </div>
</template>

<style scoped>
.app-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border-radius: 18px;
  background: linear-gradient(180deg, #fff, #f8faff);
  border: var(--glass-border);
  box-shadow: var(--soft-shadow);
}
</style>
