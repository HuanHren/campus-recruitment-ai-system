<template>
  <AppPanel :hover="false" class="app-data-table">
    <div v-if="$slots.toolbar" class="app-data-table__toolbar">
      <slot name="toolbar" />
    </div>
    <el-skeleton v-if="loading" :rows="6" animated />
    <template v-else>
      <el-table :data="data" class="soft-table" style="width:100%;">
        <template #empty>
          <AppEmptyState :title="emptyTitle" :desc="emptyDesc" />
        </template>
        <slot />
      </el-table>
      <div v-if="$slots.pagination" class="app-data-table__pagination">
        <slot name="pagination" />
      </div>
    </template>
  </AppPanel>
</template>

<script setup>
import AppEmptyState from './AppEmptyState.vue'
import AppPanel from './AppPanel.vue'

defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: Boolean,
  emptyTitle: {
    type: String,
    default: '暂无列表数据'
  },
  emptyDesc: {
    type: String,
    default: '可以调整筛选条件或稍后刷新。'
  }
})
</script>

<style scoped>
.app-data-table__toolbar {
  margin-bottom: 16px;
}

.app-data-table__pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid #eef2f7;
}
</style>
