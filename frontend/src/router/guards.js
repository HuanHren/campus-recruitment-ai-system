import { getRoleHomePath } from '../constants/roles'
import { useAuthStore } from '../stores/auth'

export function setupRouterGuards(router) {
  router.beforeEach((to) => {
    const auth = useAuthStore()

    if (to.meta.public) {
      return auth.isLogin ? auth.homePath : true
    }

    if (!auth.isLogin) {
      return {
        path: '/login',
        query: to.fullPath === '/' ? undefined : { redirect: to.fullPath }
      }
    }

    if (to.path === '/') {
      return auth.homePath
    }

    const allowedRoles = to.meta.roles
    if (allowedRoles && !allowedRoles.includes(auth.role)) {
      return getRoleHomePath(auth.role)
    }

    return true
  })
}
