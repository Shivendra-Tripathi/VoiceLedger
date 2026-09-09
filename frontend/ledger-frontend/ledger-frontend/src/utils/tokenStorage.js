// Centralises how the JWT is persisted so the rest of the app never touches
// localStorage directly. If you later move to an httpOnly cookie issued by
// the backend (the more secure option for production), this is the only
// file that needs to change.

const TOKEN_KEY = 'ledger.token'
const TOKEN_TYPE_KEY = 'ledger.tokenType'

export function saveSession({ token, tokenType }) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(TOKEN_TYPE_KEY, tokenType || 'Bearer')
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function getTokenType() {
  return localStorage.getItem(TOKEN_TYPE_KEY) || 'Bearer'
}

export function getAuthHeader() {
  const token = getToken()
  if (!token) return null
  return `${getTokenType()} ${token}`
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(TOKEN_TYPE_KEY)
}

export function hasSession() {
  return Boolean(getToken())
}
