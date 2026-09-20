import { Routes, Route, Navigate } from 'react-router-dom';
import VoiceLedgerPage from './pages/VoiceLedgerPage';
import NewCustomerPage from './pages/NewCustomerPage';
import AllCustomersPage from './pages/AllCustomersPage';
import CustomerDetailPage from './pages/CustomerDetailPage';

/**
 * App
 * ----
 * Route table for everything AFTER login. Your existing Login and Create
 * New Account pages are not included here — see README.md for exactly how
 * to merge this router into your existing one, and swap the "/login"
 * fallback below for your real login route.
 */
export default function App() {
  return (
    <Routes>
      <Route path="/voice" element={<VoiceLedgerPage />} />
      <Route path="/customers" element={<AllCustomersPage />} />
      <Route path="/customers/new" element={<NewCustomerPage />} />
      <Route path="/customers/:id" element={<CustomerDetailPage />} />

      {/* Land on the voice page by default, per spec item 1.4 */}
      <Route path="/" element={<Navigate to="/voice" replace />} />
      <Route path="*" element={<Navigate to="/voice" replace />} />
    </Routes>
  );
}
