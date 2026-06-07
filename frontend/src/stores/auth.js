import { defineStore } from 'pinia'
import { getCurrentUser, login as loginApi, register as registerApi } from '../api/auth.api'
import { TOKEN_STORAGE_KEY, USER_STORAGE_KEY } from '../api/http'
import { getRoleHomePath } from '../constants/roles'

function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_STORAGE_KEY) || 'null')
  } catch {
    localStorage.removeItem(USER_STORAGE_KEY)
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_STORAGE_KEY) || '',
    user: readStoredUser()
  }),
  getters: {
    isLogin: (state) => Boolean(state.token),
    role: (state) => state.user?.role,
    homePath: (state) => getRoleHomePath(state.user?.role)
  },
  actions: {
    persistAuth(token, user) {
      this.token = token || ''
      this.user = user || null
      if (this.token) {
        localStorage.setItem(TOKEN_STORAGE_KEY, this.token)
      } else {
        localStorage.removeItem(TOKEN_STORAGE_KEY)
      }
      if (this.user) {
        localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(this.user))
      } else {
        localStorage.removeItem(USER_STORAGE_KEY)
      }
    },
    async login(form) {
      const res = await loginApi(form)
      this.persistAuth(res.data.token, res.data.user)
      return this.homePath
    },
    async register(form) {
      await registerApi(form)
    },
    async fetchCurrentUser() {
      if (!this.token) {
        return null
      }
      const res = await getCurrentUser()
      this.user = res.data
      localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(this.user))
      return this.user
    },
    logout() {
      this.persistAuth('', null)
    }
  }
})
