import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import LedgerField from '../components/LedgerField'
import { useAuth } from '../context/AuthContext'

const initialForm = { email: '', password: '' }

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const [form, setForm] = useState(() =>
    location.state?.email ? { email: location.state.email, password: '' } : initialForm
  )
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const redirectTo = location.state?.from?.pathname || '/dashboard'
  const justRegistered = location.state?.justRegistered

  function handleChange(event) {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
    setFieldErrors((prev) => ({ ...prev, [name]: undefined }))
  }

  function validate() {
    const errors = {}
    if (!form.email.trim()) errors.email = 'Enter the email you registered with.'
    if (!form.password) errors.password = 'Enter your password.'
    return errors
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setFormError('')

    const errors = validate()
    if (Object.keys(errors).length > 0) {
      setFieldErrors(errors)
      return
    }

    setIsSubmitting(true)
    try {
      await login(form)
      navigate(redirectTo, { replace: true })
    } catch (error) {
      setFormError(readableError(error))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <AuthLayout
      title="Sign in to your ledger"
      subtitle="Pick up right where the last entry left off."
    >
      {justRegistered ? (
        <p className="ledger-form__success">Account created. Sign in to open your ledger.</p>
      ) : null}

      <form className="ledger-form" onSubmit={handleSubmit} noValidate>
        <LedgerField
          label="Email"
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          autoComplete="email"
          error={fieldErrors.email}
        />
        <LedgerField
          label="Password"
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          autoComplete="current-password"
          error={fieldErrors.password}
        />

        {formError ? <p className="ledger-form__error">{formError}</p> : null}

        <button className="ledger-button" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Signing in…' : 'Sign in'}
        </button>
      </form>

      <p className="ledger-switch">
        New to the shop? <Link to="/register">Open an account</Link>
      </p>
    </AuthLayout>
  )
}

function readableError(error) {
  const status = error?.response?.status
  const serverMessage = error?.response?.data?.message
  if (serverMessage) return serverMessage
  if (status === 401 || status === 403) return 'That email and password don\u2019t match our records.'
  if (status === 404) return 'We couldn\u2019t reach the ledger server. Is it running on port 8080?'
  if (!error?.response) return 'Can\u2019t reach the server. Check your connection and try again.'
  return 'Something went wrong while signing in. Please try again.'
}
