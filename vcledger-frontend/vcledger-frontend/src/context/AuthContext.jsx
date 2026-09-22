import { createContext, useCallback, useContext, useState } from 'react';
import { apiClient, getToken, setToken, clearToken } from '../api/client';

/**
 * AuthContext
 * ------------
 * Backs the useAuth() hook that Login.jsx, Register.jsx, ProtectedRoute.jsx
 * and Dashboard.jsx already expect. Token storage itself is delegated to
 * src/api/client.js (getToken/setToken/clearToken) so there is exactly one
 * place — VITE_JWT_STORAGE_KEY in .env — that controls where the JWT lives.
 *
 * ASSUMPTIONS TO CONFIRM (not in the original API spec, which only covered
 * /customers and /voicecommand):
 *   POST /api/auth/login    body { email, password } -> { token }
 *   POST /api/auth/register body { username, email, password } -> created user
 *
 * If your Spring Security endpoints differ — a different path, or the
 * token coming back under a different field name than `token` /
 * `accessToken` — this is the only file you need to touch.
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(() => Boolean(getToken()));

  const login = useCallback(async ({ email, password }) => {
    const response = await apiClient.post('/auth/login', { email, password });
    const token = response.data?.token ?? response.data?.accessToken;

    if (!token) {
      throw new Error('Login response did not include a token.');
    }

    setToken(token);
    setIsAuthenticated(true);
  }, []);

  const register = useCallback(async ({ username, email, password }) => {
    // Returns the created user, not a token — Register.jsx sends the
    // shopkeeper to /login afterwards rather than signing them in directly.
    await apiClient.post('/auth/register', { username, email, password });
  }, []);

  const logout = useCallback(() => {
    clearToken();
    setIsAuthenticated(false);
  }, []);

  const value = { isAuthenticated, login, register, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
