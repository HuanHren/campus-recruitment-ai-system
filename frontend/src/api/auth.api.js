import http from './http'

export function login(form) {
  return http.post('/auth/login', form)
}

export function register(form) {
  return http.post('/auth/register', form)
}

export function getCurrentUser() {
  return http.get('/auth/me')
}
