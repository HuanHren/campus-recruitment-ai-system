import http from './http'

export function getCompanyInfo() {
  return http.get('/company/info')
}

export function createCompanyInfo(body) {
  return http.post('/company/info', body)
}

export function updateCompanyInfo(body) {
  return http.put('/company/info', body)
}

export function getCompanyJobs(params) {
  return http.get('/company/jobs', { params })
}

export function getCompanyJobDetail(id) {
  return http.get(`/company/jobs/${id}`)
}

export function createCompanyJob(body) {
  return http.post('/company/jobs', body)
}

export function updateCompanyJob(id, body) {
  return http.put(`/company/jobs/${id}`, body)
}

export function offlineCompanyJob(id) {
  return http.put(`/company/jobs/${id}/offline`)
}

export function getCompanyApplications(params) {
  return http.get('/company/applications', { params })
}

export function updateApplicationStatus(id, body) {
  return http.put(`/company/applications/${id}/status`, body)
}

export function sendInterviewInvitation(body) {
  return http.post('/company/interviews', body)
}

export function createCompanyInterview(body) {
  return http.post('/company/interviews', body)
}
