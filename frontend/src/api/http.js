import axios from 'axios'
import { ElMessage } from 'element-plus'

export const TOKEN_STORAGE_KEY = 'token'
export const USER_STORAGE_KEY = 'user'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload && typeof payload.code !== 'undefined') {
      if (payload.code !== 200) {
        ElMessage.error(payload.message || '请求失败')
        return Promise.reject(payload)
      }
      return payload
    }
    return payload
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络请求异常'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default http
