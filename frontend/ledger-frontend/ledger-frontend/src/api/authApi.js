import apiClient from './axiosClient'

// POST /api/auth/login
// body: { email, password }
// response: { token, tokenType }
export async function login({ email, password }) {
  const { data } = await apiClient.post('/api/auth/login', { email, password })
  return data
}

// POST /api/auth/register
// body: { username, email, password }
// response: { id, name, email, createdAt }
export async function register({ username, email, password }) {
  const { data } = await apiClient.post('/api/auth/register', {
    username,
    email,
    password,
  })
  return data
}
