import { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { apiClient, getToken, setToken, clearToken } from '../api/client';
import { getUserProfile } from '../api/userService';

const USER_STORAGE_KEY = 'vcledger_user';

function getStoredUser() {
  try {
    const raw = localStorage.getItem(USER_STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function setStoredUser(user) {
  try {
    if (user) {
      localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(user));
    } else {
      localStorage.removeItem(USER_STORAGE_KEY);
    }
  } catch {
    // Storage might be unavailable
  }
}

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(() => Boolean(getToken()));
  const [user, setUser] = useState(getStoredUser);

  const refreshUser = useCallback(async () => {
    if (!getToken()) return;
    try {
      const profile = await getUserProfile();
      if (profile) {
        setUser(profile);
        setStoredUser(profile);
      }
    } catch {
      // Endpoint may not be ready or network issue; retain existing stored user
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated) {
      refreshUser();
    }
  }, [isAuthenticated, refreshUser]);

  const login = useCallback(
    async ({ email, password }) => {
      const response = await apiClient.post('/auth/login', { email, password });
      const token = response.data?.token ?? response.data?.accessToken;

      if (!token) {
        throw new Error('Login response did not include a token.');
      }

      setToken(token);
      setIsAuthenticated(true);

      // If backend returned user object directly with login
      if (response.data?.user) {
        setUser(response.data.user);
        setStoredUser(response.data.user);
      } else {
        refreshUser();
      }
    },
    [refreshUser]
  );

  const register = useCallback(async ({ username, email, password }) => {
    await apiClient.post('/auth/register', { username, email, password });
  }, []);

  const updateUser = useCallback((updatedUser) => {
    setUser((prev) => {
      const next = { ...(prev || {}), ...updatedUser };
      setStoredUser(next);
      return next;
    });
  }, []);

  const logout = useCallback(() => {
    clearToken();
    setStoredUser(null);
    setUser(null);
    setIsAuthenticated(false);
  }, []);

  const value = {
    isAuthenticated,
    user,
    updateUser,
    refreshUser,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
