import axios from 'axios'
import { ElMessage } from 'element-plus'

const aiRequest = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 90000
})

aiRequest.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

aiRequest.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload && typeof payload.code !== 'undefined' && payload.code !== 200) {
      ElMessage.error(payload.message || 'AI 生成失败，请稍后重试')
      return Promise.reject(payload)
    }
    return payload
  },
  (error) => {
    const isTimeout = error.code === 'ECONNABORTED' || String(error.message || '').includes('timeout')
    const message = isTimeout
      ? 'AI 生成耗时较长，请稍后重试，或切换为模拟数据演示。'
      : (error.response?.data?.message || error.message || 'AI 生成失败，请稍后重试')
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default aiRequest
