import axios from 'axios'
import { getAuthHeader, clearSession } from '../utils/tokenStorage'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export const apiClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Attach the JWT to every outgoing request, if we have one.
apiClient.interceptors.request.use((config) => {
  const authHeader = getAuthHeader()
  if (authHeader) {
    config.headers.Authorization = authHeader
  }
  return config
})

// Centralised handling for expired/invalid sessions. Any screen built on
// top of apiClient automatically benefits from this — no per-call
// try/catch boilerplate needed for the 401 case.
let onUnauthorized = () => {}
export function registerUnauthorizedHandler(handler) {
  onUnauthorized = handler
}

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      clearSession()
      onUnauthorized()
    }
    return Promise.reject(error)
  }
)

export default apiClient
