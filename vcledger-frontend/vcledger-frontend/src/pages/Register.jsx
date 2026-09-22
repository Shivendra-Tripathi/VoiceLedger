import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import LedgerField from '../components/LedgerField'
import { useAuth } from '../context/AuthContext'

const initialForm = { username: '', email: '', password: '', confirmPassword: '' }

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState(initialForm)
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  function handleChange(event) {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
    setFieldErrors((prev) => ({ ...prev, [name]: undefined }))
  }

  function validate() {
    const errors = {}
    if (!form.username.trim()) errors.username = 'Give the account a name.'
    if (!form.email.trim()) errors.email = 'An email is needed to sign back in.'
    if (form.password.length < 8) errors.password = 'Use at least 8 characters.'
    if (form.confirmPassword !== form.password) {
      errors.confirmPassword = 'Passwords don\u2019t match.'
    }
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
      await register({
        username: form.username,
        email: form.email,
        password: form.password,
      })
      // Registration returns the created user, not a token, so the
      // shopkeeper signs in next with the same credentials.
      navigate('/login', {
        replace: true,
        state: { justRegistered: true, email: form.email },
      })
    } catch (error) {
      setFormError(readableError(error))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <AuthLayout
      title="Open a new account"
      subtitle="One account per shop. Takes about a minute."
    >
      <form className="ledger-form" onSubmit={handleSubmit} noValidate>
        <LedgerField
          label="Shop or owner name"
          name="username"
          value={form.username}
          onChange={handleChange}
          autoComplete="username"
          error={fieldErrors.username}
        />
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
          autoComplete="new-password"
          error={fieldErrors.password}
        />
        <LedgerField
          label="Confirm password"
          type="password"
          name="confirmPassword"
          value={form.confirmPassword}
          onChange={handleChange}
          autoComplete="new-password"
          error={fieldErrors.confirmPassword}
        />

        {formError ? <p className="ledger-form__error">{formError}</p> : null}

        <button className="ledger-button" type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Creating account…' : 'Create account'}
        </button>
      </form>

      <p className="ledger-switch">
        Already have a ledger? <Link to="/login">Sign in</Link>
      </p>
    </AuthLayout>
  )
}

function readableError(error) {
  const status = error?.response?.status
  const serverMessage = error?.response?.data?.message
  if (serverMessage) return serverMessage
  if (status === 409) return 'An account with that email already exists.'
  if (status === 400) return 'Please check the details and try again.'
  if (!error?.response) return 'Can\u2019t reach the server. Check your connection and try again.'
  return 'Something went wrong while creating the account. Please try again.'
}
