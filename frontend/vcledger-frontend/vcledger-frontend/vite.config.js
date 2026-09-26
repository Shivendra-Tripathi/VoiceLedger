import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

// VCLedger frontend build configuration.
// Dev server runs on port 5173 by default and proxies nothing special —
// the API base URL is controlled entirely through VITE_API_BASE_URL (see .env.example).
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), 'VITE_');

  if (mode === 'production' && !env.VITE_API_BASE_URL) {
    throw new Error('VITE_API_BASE_URL must be set for production builds.');
  }

  return {
    plugins: [react()],
    server: {
      port: 5173,
      host: '127.0.0.1',
    },
    optimizeDeps: {
      include: ['lucide-react', 'react', 'react-dom', 'react-router-dom', 'axios'],
    },
  };
});
