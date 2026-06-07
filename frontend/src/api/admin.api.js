import http from './http'

export function getAdminStudents(params) {
  return http.get('/admin/students', { params })
}

export function getAdminCompanies(params) {
  return http.get('/admin/companies', { params })
}

export function auditCompany(id, body) {
  return http.put(`/admin/companies/${id}/audit`, body)
}

export function getAdminJobs(params) {
  return http.get('/admin/jobs', { params })
}

export function auditJob(id, body) {
  return http.put(`/admin/jobs/${id}/audit`, body)
}

export function getAdminInterviews(params) {
  return http.get('/admin/interviews', { params })
}
