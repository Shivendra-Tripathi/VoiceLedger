import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Wrap any future page that should only be reachable while logged in:
// <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
export default function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth()
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }
  return children
}
