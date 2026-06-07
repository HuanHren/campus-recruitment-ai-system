import http from './http'

export function getDashboardOverview(role) {
  return http.get('/dashboard/overview', { params: { role } })
}
