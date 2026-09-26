import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import VoiceLedgerPage from './pages/VoiceLedgerPage';
import NewCustomerPage from './pages/NewCustomerPage';
import AllCustomersPage from './pages/AllCustomersPage';
import CustomerDetailPage from './pages/CustomerDetailPage';
import TransactionsPage from './pages/TransactionsPage';
import UpdateProfilePage from './pages/UpdateProfilePage';
import UpdatePasswordPage from './pages/UpdatePasswordPage';

/**
 * App
 * ----
 * Full route table: public auth pages, plus the four post-login pages
 * wrapped in ProtectedRoute (which checks AuthContext's isAuthenticated
 * and bounces to /login if there's no session).
 */
export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route
        path="/voice"
        element={
          <ProtectedRoute>
            <VoiceLedgerPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/customers"
        element={
          <ProtectedRoute>
            <AllCustomersPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/customers/new"
        element={
          <ProtectedRoute>
            <NewCustomerPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/customers/:id"
        element={
          <ProtectedRoute>
            <CustomerDetailPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/transactions"
        element={
          <ProtectedRoute>
            <TransactionsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/settings/profile"
        element={
          <ProtectedRoute>
            <UpdateProfilePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/profile"
        element={
          <ProtectedRoute>
            <UpdateProfilePage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/settings/password"
        element={
          <ProtectedRoute>
            <UpdatePasswordPage />
          </ProtectedRoute>
        }
      />

      {/* Unauthenticated visitors get bounced to /login by ProtectedRoute
          the moment they hit a protected page, so it's safe to default
          "/" and unknown paths at /voice. */}
      <Route path="/" element={<Navigate to="/voice" replace />} />
      <Route path="*" element={<Navigate to="/voice" replace />} />
    </Routes>
  );
}
