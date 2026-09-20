import axios from 'axios';

/**
 * Central Axios instance for every VCLedger API call.
 *
 * WHY THIS FILE EXISTS
 * ---------------------
 * Every request in this app needs the same two things: the backend's base
 * URL, and the shopkeeper's JWT attached as an Authorization header. Rather
 * than repeating that in every service file, we configure it once here and
 * import `apiClient` everywhere else.
 *
 * INTEGRATING WITH YOUR EXISTING LOGIN PAGE
 * ------------------------------------------
 * Your login page presumably already does something like:
 *   localStorage.setItem('vcledger_token', jwt);
 *
 * The key it uses MUST match VITE_JWT_STORAGE_KEY in your .env file
 * (see .env.example). If your login page stores the token somewhere other
 * than localStorage (e.g. a cookie, or React context), update ONLY the
 * `getToken()` function below — nothing else in the app needs to change.
 */

const JWT_STORAGE_KEY = import.meta.env.VITE_JWT_STORAGE_KEY || 'vcledger_token';
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export function getToken() {
  return localStorage.getItem(JWT_STORAGE_KEY);
}

export function setToken(token) {
  localStorage.setItem(JWT_STORAGE_KEY, token);
}

export function clearToken() {
  localStorage.removeItem(JWT_STORAGE_KEY);
}

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
});

// Attach the JWT to every outgoing request automatically.
apiClient.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Centralized handling for an expired/invalid session: if the backend ever
// returns 401, send the shopkeeper back to your login page. Adjust the
// redirect path to match wherever your login route actually lives.
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearToken();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
