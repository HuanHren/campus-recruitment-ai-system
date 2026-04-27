<template>
  <div class="page">
    <section class="hero-panel">
      <div class="page-head" style="margin-bottom:0;">
        <div>
          <span class="ai-chip"><el-icon><DataAnalysis /></el-icon> 平台运营总览</span>
          <h1 style="margin-top:14px;">管理员首页</h1>
          <p>集中处理企业认证、岗位审核、投递趋势和 AI 能力运行状态，为校园招聘答辩演示提供完整后台视角。</p>
        </div>
        <div style="display:flex;align-items:center;gap:12px;">
          <el-avatar :size="52" style="background:linear-gradient(135deg,#2563eb,#7c3aed);">A</el-avatar>
          <div>
            <strong style="display:block;color:var(--title);">系统管理员</strong>
            <span class="muted">{{ today }}</span>
          </div>
        </div>
      </div>
      <div class="hero-actions">
        <el-button type="primary" :icon="OfficeBuilding" @click="$router.push('/jobs')">处理岗位审核</el-button>
        <el-button :icon="Promotion" plain @click="$router.push('/interviews')">查看面试记录</el-button>
        <el-button :icon="DataAnalysis" plain @click="$router.push('/statistics')">打开数据统计</el-button>
      </div>
    </section>

    <section class="grid grid-6" style="margin-top:18px;">
      <div v-for="item in adminMetrics" :key="item.label" class="stat-card">
        <span class="stat-trend">{{ item.trend }}</span>
        <div class="num">{{ item.value }}</div>
        <div class="label">{{ item.label }}</div>
      </div>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <el-card class="content-card wide-card" shadow="never">
        <div class="section-title">
          <h3>近 7 日投递趋势</h3>
          <el-tag effect="plain">今日 {{ adminTrend.at(-1).value }} 次</el-tag>
        </div>
        <div class="bar-chart">
          <div v-for="item in adminTrend" :key="item.label" class="bar-item">
            <div class="bar-column" :style="{ height: `${item.value * 2}px` }" />
            <strong style="color:var(--title);">{{ item.value }}</strong>
            <span class="bar-label">{{ item.label }}</span>
          </div>
        </div>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>岗位分类占比</h3>
        </div>
        <div class="donut"><strong>518</strong></div>
        <div class="legend-list">
          <div v-for="item in jobCategory" :key="item.label" class="legend-item">
            <span><i class="legend-dot" :style="{ backgroundColor: item.color }" />{{ item.label }}</span>
            <strong>{{ item.value }}%</strong>
          </div>
        </div>
      </el-card>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>企业审核进度</h3>
        </div>
        <div v-for="item in auditProgress" :key="item.label" class="bar-row">
          <span>{{ item.label }}</span>
          <div class="bar-track"><div class="bar-fill" :style="{ width: item.percent + '%' }" /></div>
          <strong>{{ item.value }}</strong>
        </div>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>待办事项</h3>
          <el-tag type="warning" effect="plain">3 项</el-tag>
        </div>
        <div class="todo-list">
          <div v-for="item in adminTodos" :key="item.title" class="todo-item">
            <div style="display:flex;justify-content:space-between;gap:10px;">
              <strong style="color:var(--title);">{{ item.title }}</strong>
              <el-tag :type="item.level === '高' ? 'danger' : 'warning'" size="small">{{ item.level }}</el-tag>
            </div>
            <p style="margin:8px 0 0;line-height:1.7;">{{ item.desc }}</p>
          </div>
        </div>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>AI 系统状态</h3>
          <span class="ai-chip">DeepSeek-V4-Pro</span>
        </div>
        <div class="todo-list">
          <div v-for="item in aiStatus" :key="item.label" class="todo-item">
            <div style="display:flex;align-items:center;justify-content:space-between;">
              <span>{{ item.label }}</span>
              <strong style="color:var(--title);">{{ item.value }}</strong>
            </div>
            <div class="muted" style="margin-top:6px;font-size:13px;">{{ item.detail }}</div>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { DataAnalysis, OfficeBuilding, Promotion } from '@element-plus/icons-vue'
import { adminMetrics, adminTodos, adminTrend, aiStatus, auditProgress, jobCategory } from '../../data/mockDashboard'

const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
</script>
