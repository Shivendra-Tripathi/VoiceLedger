import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Wraps any page that should only be reachable while logged in:
// <Route path="/voice" element={<ProtectedRoute><VoiceLedgerPage /></ProtectedRoute>} />
export default function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth()
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }
  return children
}
