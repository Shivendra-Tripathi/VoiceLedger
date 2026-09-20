import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// VCLedger frontend build configuration.
// Dev server runs on port 5173 by default and proxies nothing special —
// the API base URL is controlled entirely through VITE_API_BASE_URL (see .env.example).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
  },
});
