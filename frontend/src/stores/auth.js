import { defineStore } from 'pinia'
import request from '../utils/request'

const homeByRole = {
  ADMIN: '/admin',
  STUDENT: '/student',
  COMPANY: '/company',
  TEACHER: '/teacher'
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),
  getters: {
    isLogin: (state) => Boolean(state.token),
    role: (state) => state.user?.role,
    homePath: (state) => homeByRole[state.user?.role] || '/login'
  },
  actions: {
    async login(form) {
      const res = await request.post('/auth/login', form)
      this.token = res.data.token
      this.user = res.data.user
      localStorage.setItem('token', this.token)
      localStorage.setItem('user', JSON.stringify(this.user))
      return this.homePath
    },
    async register(form) {
      await request.post('/auth/register', form)
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
