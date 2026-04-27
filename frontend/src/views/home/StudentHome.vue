<template>
  <div class="page">
    <section class="hero-panel">
      <span class="ai-chip"><el-icon><MagicStick /></el-icon> AI 求职推荐工作台</span>
      <h1 style="margin-top:14px;">欢迎回来，继续推进你的校园求职计划</h1>
      <p>系统根据简历完整度、技能标签、城市意向和岗位要求生成推荐结果，帮助你更快完成岗位筛选、简历优化与面试准备。</p>
      <div class="hero-actions">
        <el-button type="primary" :icon="Briefcase" @click="$router.push('/jobs')">查看推荐岗位</el-button>
        <el-button :icon="MagicStick" plain @click="$router.push('/ai-resume')">优化我的简历</el-button>
        <el-button :icon="Reading" plain @click="$router.push('/ai-match')">岗位匹配分析</el-button>
      </div>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <div v-for="item in studentSummary" :key="item.label" class="stat-card">
        <div class="label">{{ item.label }}</div>
        <div class="num">{{ item.value }}</div>
        <p style="margin:0;line-height:1.6;">{{ item.desc }}</p>
      </div>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <el-card class="content-card wide-card" shadow="never">
        <div class="section-title">
          <h3>AI 推荐岗位</h3>
          <el-tag type="primary" effect="plain">按匹配度排序</el-tag>
        </div>
        <div class="job-card-list">
          <div v-for="job in recommendedJobs" :key="job.title" class="job-card">
            <div class="job-card-head">
              <div>
                <h3>{{ job.title }}</h3>
                <div class="muted">{{ job.company }} · {{ job.city }}</div>
              </div>
              <strong style="color:var(--blue);">{{ job.salary }}</strong>
            </div>
            <div class="tag-row">
              <el-tag v-for="tag in job.tags" :key="tag" effect="plain">{{ tag }}</el-tag>
            </div>
            <div class="progress-line">
              <div style="display:flex;justify-content:space-between;margin-bottom:8px;">
                <span class="muted">AI 匹配度</span>
                <strong style="color:var(--title);">{{ job.match }}%</strong>
              </div>
              <el-progress :percentage="job.match" :show-text="false" :stroke-width="10" />
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>简历优化入口</h3>
        </div>
        <div class="icon-pill"><el-icon><MagicStick /></el-icon></div>
        <h2 style="color:var(--title);margin:16px 0 8px;">DeepSeek 简历优化</h2>
        <p style="line-height:1.8;">识别简历中的项目表达、技能关键词和岗位匹配不足，生成适合校园招聘场景的优化文本。</p>
        <el-button type="primary" style="margin-top:16px;" @click="$router.push('/ai-resume')">立即优化</el-button>
      </el-card>
    </section>

    <section class="grid grid-3" style="margin-top:18px;">
      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>投递进度时间线</h3>
        </div>
        <el-timeline>
          <el-timeline-item v-for="item in applyTimeline" :key="item.title" :type="item.status" :timestamp="item.time">
            {{ item.title }}
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>热门企业</h3>
        </div>
        <div class="candidate-list">
          <div v-for="item in hotCompanies" :key="item.name" class="candidate-item">
            <div class="candidate-head">
              <div>
                <h3>{{ item.name }}</h3>
                <div class="muted">{{ item.field }}</div>
              </div>
              <el-tag effect="plain">{{ item.jobs }} 岗</el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="content-card" shadow="never">
        <div class="section-title">
          <h3>AI 面试准备</h3>
        </div>
        <p style="line-height:1.8;">根据岗位名称和任职要求生成基础题、项目题与综合题，帮助你提前组织回答思路。</p>
        <el-button :icon="Cpu" type="primary" style="margin-top:16px;" @click="$router.push('/ai-interview')">
          生成模拟面试题
        </el-button>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { Briefcase, Cpu, MagicStick, Reading } from '@element-plus/icons-vue'
import { applyTimeline, hotCompanies, recommendedJobs, studentSummary } from '../../data/mockDashboard'
</script>
