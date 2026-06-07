export const USER_ROLES = Object.freeze({
  ADMIN: 'ADMIN',
  STUDENT: 'STUDENT',
  COMPANY: 'COMPANY',
  TEACHER: 'TEACHER'
})

export const ROLE_LABELS = Object.freeze({
  ADMIN: '管理员',
  STUDENT: '学生',
  COMPANY: '企业',
  TEACHER: '就业老师'
})

export const ROLE_HOME_PATHS = Object.freeze({
  ADMIN: '/admin',
  STUDENT: '/student',
  COMPANY: '/company',
  TEACHER: '/teacher'
})

export function getRoleHomePath(role) {
  return ROLE_HOME_PATHS[role] || '/login'
}

export function getRoleLabel(role) {
  return ROLE_LABELS[role] || '用户'
}
