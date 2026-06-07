import http from './http'
import { createTokenizedEventSource } from '../utils/sse'

export function optimizeResume(body) {
  return http.post('/student/ai/resume-optimize', body, { timeout: 90000 })
}

export function streamOptimizeResume(resumeContent, handlers = {}) {
  const source = createTokenizedEventSource('/student/ai/resume-optimize/stream', { resumeContent })

  Object.entries(handlers).forEach(([eventName, handler]) => {
    if (typeof handler === 'function') {
      source.addEventListener(eventName, handler)
    }
  })

  return source
}

export function analyzeJobMatch(body) {
  return http.post('/student/ai/job-match', body, { timeout: 90000 })
}

export function generateInterviewQuestions(body) {
  return http.post('/student/ai/interview-questions', body, { timeout: 90000 })
}

export function generateJobDescription(body) {
  return http.post('/company/ai/job-description', body, { timeout: 90000 })
}
