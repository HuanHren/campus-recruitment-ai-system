<template>
  <div class="page">
    <section class="hero-panel">
      <span class="ai-chip"><el-icon><OfficeBuilding /></el-icon> 企业招聘工作台</span>
      <h1 style="margin-top:14px;">从岗位发布到面试邀约，集中管理校园招聘流程</h1>
      <p>企业端聚焦岗位运营、简历筛选和候选人推进，结合 DeepSeek-V4-Pro 生成岗位描述，提高岗位内容质量与学生匹配效率。</p>
      <div class="hero-actions">
        <el-button type="primary" :icon="Promotion" @click="$router.push('/job-publish')">发布岗位</el-button>
        <el-button :icon="Postcard" plain @click="$router.push('/applications')">处理投递</el-button>
        <el-button :icon="MagicStick" plain @click="$router.push('/job-publish')">AI 生成岗位描述</el-button>
      </div>
    </section>

    <section class="grid grid-4" style="margin-top:18px;">
      <div v-for="item in companyMetrics" :key="item.label" class="stat-card">
        <div class="label">{{ item.label }}</div>
        <div class="num">{{ item.value }}</div>
        <span class="stat-trend">{{ item.trend }}</span>
      </div>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <el-card class="content-card wide-card" shadow="never">
        <div class="section-title">
          <h3>岗位数据趋势</h3>
          <el-tag effect="plain">近 7 日收到简历</el-tag>
        </div>
        <div class="bar-chart">
          <div v-for="item in companyTrend" :key="item.label" class="bar-item">
            <div class="bar-column" :style="{ height: `${item.value * 4}px` }" />
            <strong style="color:var(--title);">{{ item.value }}</strong>
            <span class="bar-label">{{ item.label }}</span>
          </div>
        </div>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>AI 岗位描述生成</h3>
          <span class="ai-chip">DeepSeek</span>
        </div>
        <p style="line-height:1.8;">输入岗位名称、薪资与技能要求，自动生成岗位职责和任职要求，减少企业 HR 编写岗位文案的时间。</p>
        <div class="tag-row">
          <el-tag effect="plain">岗位职责</el-tag>
          <el-tag effect="plain">任职要求</el-tag>
          <el-tag effect="plain">校园招聘语气</el-tag>
        </div>
        <el-button type="primary" style="margin-top:18px;" @click="$router.push('/job-publish')">进入生成</el-button>
      </el-card>
    </section>

    <section class="grid grid-2" style="margin-top:18px;">
      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>候选人列表</h3>
          <el-button link type="primary" @click="$router.push('/applications')">查看全部</el-button>
        </div>
        <div class="candidate-list">
          <div v-for="item in candidates" :key="item.name" class="candidate-item">
            <div class="candidate-head">
              <div style="display:flex;gap:12px;align-items:center;">
                <el-avatar :size="42" style="background:#eef2ff;color:#2563eb;">{{ item.name.slice(0, 1) }}</el-avatar>
                <div>
                  <h3>{{ item.name }}</h3>
                  <div class="muted">{{ item.school }} · {{ item.role }}</div>
                </div>
              </div>
              <el-tag :type="item.match > 88 ? 'success' : 'primary'" effect="plain">{{ item.status }}</el-tag>
            </div>
            <div class="progress-line">
              <div style="display:flex;justify-content:space-between;margin-bottom:8px;">
                <span class="muted">AI 匹配度</span>
                <strong style="color:var(--title);">{{ item.match }}%</strong>
              </div>
              <el-progress :percentage="item.match" :show-text="false" :stroke-width="10" />
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>招聘流程看板</h3>
        </div>
        <div class="bar-row">
          <span>简历初筛</span>
          <div class="bar-track"><div class="bar-fill" style="width:76%;" /></div>
          <strong>76%</strong>
        </div>
        <div class="bar-row">
          <span>面试邀约</span>
          <div class="bar-track"><div class="bar-fill" style="width:58%;" /></div>
          <strong>58%</strong>
        </div>
        <div class="bar-row">
          <span>学生确认</span>
          <div class="bar-track"><div class="bar-fill" style="width:44%;" /></div>
          <strong>44%</strong>
        </div>
        <div class="bar-row">
          <span>岗位补充</span>
          <div class="bar-track"><div class="bar-fill" style="width:32%;" /></div>
          <strong>32%</strong>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { MagicStick, OfficeBuilding, Postcard, Promotion } from '@element-plus/icons-vue'
import { candidates, companyMetrics, companyTrend } from '../../data/mockDashboard'
</script>
