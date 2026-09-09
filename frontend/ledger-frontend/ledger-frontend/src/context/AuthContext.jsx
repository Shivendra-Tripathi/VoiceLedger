import { createContext, useContext, useEffect, useMemo, useState, useCallback } from 'react'
import { login as loginRequest, register as registerRequest } from '../api/authApi'
import { registerUnauthorizedHandler } from '../api/axiosClient'
import { saveSession, clearSession, hasSession } from '../utils/tokenStorage'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  // We don't get a user profile back from /login (only the token), so
  // isAuthenticated is derived from "do we hold a token". Swap this for
  // real user state once a /me or profile endpoint exists.
  const [isAuthenticated, setIsAuthenticated] = useState(hasSession())
  const [lastRegisteredUser, setLastRegisteredUser] = useState(null)

  useEffect(() => {
    registerUnauthorizedHandler(() => setIsAuthenticated(false))
  }, [])

  const login = useCallback(async (credentials) => {
    const data = await loginRequest(credentials)
    saveSession(data)
    setIsAuthenticated(true)
    return data
  }, [])

  const register = useCallback(async (details) => {
    const data = await registerRequest(details)
    setLastRegisteredUser(data)
    return data
  }, [])

  const logout = useCallback(() => {
    clearSession()
    setIsAuthenticated(false)
  }, [])

  const value = useMemo(
    () => ({ isAuthenticated, login, register, logout, lastRegisteredUser }),
    [isAuthenticated, login, register, logout, lastRegisteredUser]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider')
  return ctx
}
