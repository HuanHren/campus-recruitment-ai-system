<template>
  <div class="page">
    <section class="hero-band page-head">
      <div>
        <h1>{{ title }}</h1>
        <p>{{ subtitle }}</p>
      </div>
      <el-button type="success" size="large" :icon="Position" @click="$router.push(primaryPath)">
        {{ primaryText }}
      </el-button>
    </section>

    <section class="grid grid-4" style="margin-top:18px;">
      <div v-for="item in stats" :key="item.label" class="stat-card">
        <el-icon class="icon-pill"><component :is="item.icon" /></el-icon>
        <div class="num">{{ item.value }}</div>
        <div class="label">{{ item.label }}</div>
      </div>
    </section>

    <section class="role-focus" v-if="focus">
      <div class="focus-panel">
        <h2>{{ focus.title }}</h2>
        <p>{{ focus.desc }}</p>
        <el-button style="margin-top:18px;" type="success" :icon="Position" @click="$router.push(focus.path)">
          {{ focus.action }}
        </el-button>
      </div>
      <div class="progress-strip">
        <div v-for="item in focus.items" :key="item.label" class="bar-row">
          <span>{{ item.label }}</span>
          <div class="bar-track"><div class="bar-fill" :style="{ width: item.percent + '%' }" /></div>
          <strong>{{ item.value }}</strong>
        </div>
      </div>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <div v-for="item in modules" :key="item.title" class="module-card" @click="$router.push(item.path)">
        <div>
          <el-icon class="icon-pill"><component :is="item.icon" /></el-icon>
          <h3>{{ item.title }}</h3>
          <p style="color:#667085;line-height:1.7;">{{ item.desc }}</p>
        </div>
        <el-button link type="primary">进入模块</el-button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { Position } from '@element-plus/icons-vue'

defineProps({
  title: String,
  subtitle: String,
  primaryText: String,
  primaryPath: String,
  stats: Array,
  modules: Array,
  focus: Object
})
</script>
