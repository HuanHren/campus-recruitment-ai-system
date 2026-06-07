import { TOKEN_STORAGE_KEY } from '../api/http'

export function createTokenizedEventSource(path, params = {}) {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY)
  const query = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      query.set(key, value)
    }
  })

  if (token) {
    query.set('token', token)
  }

  const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
  return new EventSource(`${baseURL}${path}?${query.toString()}`)
}
