<template>
  <div class="app-tag-group">
    <el-tag v-for="tag in tags" :key="tag" effect="plain" round>{{ tag }}</el-tag>
  </div>
</template>

<script setup>
defineProps({
  tags: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.app-tag-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
