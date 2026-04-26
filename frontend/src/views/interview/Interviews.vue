<template>
  <div class="page">
    <PageHeader title="面试邀请页面" desc="企业发送邀请，学生确认安排，管理员查看全平台面试记录。" />
    <el-card v-if="role === 'COMPANY'" class="glass-card content-card" shadow="never" style="margin-bottom:18px;">
      <el-form :model="invite" inline>
        <el-form-item label="投递ID"><el-input-number v-model="invite.applyId" :min="1" /></el-form-item>
        <el-form-item label="时间"><el-date-picker v-model="invite.interviewTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="方式"><el-input v-model="invite.interviewType" placeholder="线上面试" /></el-form-item>
        <el-form-item label="地点/链接"><el-input v-model="invite.interviewAddress" style="width:260px;" /></el-form-item>
        <el-button type="primary" @click="send">发送邀请</el-button>
      </el-form>
    </el-card>
    <el-card class="glass-card content-card" shadow="never">
      <div class="toolbar">
        <el-select v-model="status" placeholder="邀请状态" clearable style="width:180px;">
          <el-option label="待确认" value="PENDING" />
          <el-option label="已接受" value="ACCEPTED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
        <el-input v-if="role === 'ADMIN'" v-model="keyword" placeholder="岗位 / 企业 / 学生" style="width:220px;" clearable />
        <el-button type="primary" :icon="Search" @click="load">查询</el-button>
      </div>
      <el-table :data="rows" v-loading="loading" class="soft-table">
        <template #empty>
          <div class="empty-state">
            <el-empty description="暂无面试邀请记录，流程推进后会显示在这里" />
          </div>
        </template>
        <el-table-column prop="jobName" label="岗位" min-width="160" />
        <el-table-column prop="companyName" label="企业" min-width="150" />
        <el-table-column prop="studentName" label="学生" width="100" />
        <el-table-column prop="interviewTime" label="面试时间" min-width="170" />
        <el-table-column prop="interviewType" label="方式" width="110" />
        <el-table-column prop="invitationStatusText" label="状态" width="110" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="role === 'STUDENT' && row.invitationStatus === 'PENDING'" link type="success" @click="reply(row, 'ACCEPTED')">接受</el-button>
            <el-button v-if="role === 'STUDENT' && row.invitationStatus === 'PENDING'" link type="danger" @click="reply(row, 'REJECTED')">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'
import { useAuthStore } from '../../stores/auth'

const role = useAuthStore().role
const rows = ref([])
const loading = ref(false)
const status = ref('')
const keyword = ref('')
const invite = reactive({ applyId: 1, interviewTime: '', interviewType: '线上面试', interviewAddress: '腾讯会议：123-456-789', contactPerson: '李经理', contactPhone: '13900000001', content: '请提前准备个人项目介绍。' })

async function load() {
  loading.value = true
  try {
    const url = role === 'ADMIN' ? '/admin/interviews' : '/student/interviews'
    const res = await request.get(url, { params: { current: 1, size: 50, invitationStatus: status.value, keyword: keyword.value } })
    rows.value = res.data.records
  } finally {
    loading.value = false
  }
}

async function send() {
  await request.post('/company/interviews', invite)
  ElMessage.success('面试邀请已发送')
}

async function reply(row, invitationStatus) {
  await request.put(`/student/interviews/${row.id}/reply`, { invitationStatus, replyRemark: invitationStatus === 'ACCEPTED' ? '已确认参加' : '时间不合适' })
  ElMessage.success('回复已提交')
  load()
}

onMounted(() => {
  if (role !== 'COMPANY') load()
})
</script>
