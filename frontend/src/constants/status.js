export const AUDIT_STATUS = Object.freeze({
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED'
})

export const JOB_PUBLISH_STATUS = Object.freeze({
  ONLINE: 'ONLINE',
  OFFLINE: 'OFFLINE'
})

export const APPLY_STATUS = Object.freeze({
  PENDING: 'PENDING',
  VIEWED: 'VIEWED',
  INTERVIEW: 'INTERVIEW',
  REJECTED: 'REJECTED'
})

export const INVITATION_STATUS = Object.freeze({
  PENDING: 'PENDING',
  ACCEPTED: 'ACCEPTED',
  REJECTED: 'REJECTED'
})

export const STATUS_LABELS = Object.freeze({
  PENDING: '待处理',
  APPROVED: '已通过',
  REJECTED: '已拒绝',
  ONLINE: '已发布',
  OFFLINE: '已下架',
  VIEWED: '已查看',
  INTERVIEW: '邀请面试',
  ACCEPTED: '已接受'
})
