<template>
  <div class="page">
    <PageHeader title="企业信息页面" desc="完善企业认证资料，管理员审核通过后即可发布招聘岗位。">
      <el-tag :type="statusType">{{ auditStatus || '未提交' }}</el-tag>
    </PageHeader>
    <el-card class="glass-card content-card" shadow="never">
      <el-form :model="form" label-width="132px">
        <el-row :gutter="18">
          <el-col :md="12"><el-form-item label="企业名称"><el-input v-model="form.companyName" /></el-form-item></el-col>
          <el-col :md="12"><el-form-item label="信用代码"><el-input v-model="form.unifiedSocialCreditCode" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="行业"><el-input v-model="form.industry" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="规模"><el-input v-model="form.companyScale" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="地址"><el-input v-model="form.address" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="联系人"><el-input v-model="form.contactPerson" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="联系电话"><el-input v-model="form.contactPhone" /></el-form-item></el-col>
          <el-col :md="8"><el-form-item label="联系邮箱"><el-input v-model="form.contactEmail" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="企业简介"><el-input v-model="form.description" type="textarea" :rows="5" /></el-form-item></el-col>
        </el-row>
        <el-button type="primary" :icon="Check" :loading="loading" @click="save">提交企业信息</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import request from '../../utils/request'

const loading = ref(false)
const exists = ref(false)
const auditStatus = ref('')
const form = reactive({
  companyName: '', unifiedSocialCreditCode: '', industry: '', companyScale: '', address: '',
  contactPerson: '', contactPhone: '', contactEmail: '', description: ''
})
const statusType = computed(() => auditStatus.value === 'APPROVED' ? 'success' : auditStatus.value === 'REJECTED' ? 'danger' : 'warning')

async function load() {
  try {
    const res = await request.get('/company/info')
    Object.assign(form, res.data)
    auditStatus.value = res.data.auditStatus
    exists.value = true
  } catch {
    exists.value = false
  }
}

async function save() {
  loading.value = true
  try {
    const res = await request[exists.value ? 'put' : 'post']('/company/info', form)
    auditStatus.value = res.data.auditStatus
    exists.value = true
    ElMessage.success('企业信息已提交')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
