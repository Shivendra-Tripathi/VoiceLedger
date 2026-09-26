import axios from 'axios';

const JWT_STORAGE_KEY =
  import.meta.env.VITE_JWT_STORAGE_KEY || 'vcledger_token';

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ||
  (import.meta.env.DEV ? 'http://localhost:8080/api' : '');

/**
 * Get JWT from localStorage.
 */
export function getToken() {
  return localStorage.getItem(JWT_STORAGE_KEY);
}

/**
 * Store JWT in localStorage.
 */
export function setToken(token) {
  localStorage.setItem(JWT_STORAGE_KEY, token);
}

/**
 * Remove JWT from localStorage.
 */
export function clearToken() {
  localStorage.removeItem(JWT_STORAGE_KEY);
}

/**
 * Shared Axios client for all authenticated API requests.
 */
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
});

/**
 * Automatically attach JWT to every request.
 */
apiClient.interceptors.request.use(
  (config) => {
    const token = getToken();

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Handle authentication failures globally.
 */
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      clearToken();
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);