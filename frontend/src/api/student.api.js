import http from './http'

export function getStudentInfo() {
  return http.get('/student/info')
}

export function createStudentInfo(body) {
  return http.post('/student/info', body)
}

export function updateStudentInfo(body) {
  return http.put('/student/info', body)
}

export function getStudentJobs(params) {
  return http.get('/student/jobs', { params })
}

export function getStudentJobDetail(id) {
  return http.get(`/student/jobs/${id}`)
}

export function applyJob(jobId) {
  return http.post(`/student/jobs/${jobId}/apply`)
}

export function getStudentApplications(params) {
  return http.get('/student/applications', { params })
}

export function getStudentResume() {
  return http.get('/student/resume')
}

export function createStudentResume(body) {
  return http.post('/student/resume', body)
}

export function updateStudentResume(body) {
  return http.put('/student/resume', body)
}

export function getStudentInterviews(params) {
  return http.get('/student/interviews', { params })
}

export function replyStudentInterview(id, body) {
  return http.put(`/student/interviews/${id}/reply`, body)
}
