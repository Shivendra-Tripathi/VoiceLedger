import { Link, useLocation } from 'react-router-dom'

export default function AuthLayout({ title, subtitle, children }) {
  const { pathname } = useLocation()

  return (
    <div className="ledger-page">
      <div className="ledger-book">
        <header className="ledger-masthead">
          <span className="ledger-masthead__mark" aria-hidden="true">
            खाता
          </span>
          <div className="ledger-masthead__text">
            <p className="ledger-masthead__title">Khatabook</p>
            <p className="ledger-masthead__tagline">Speak it, and it's written down</p>
          </div>
        </header>

        <nav className="ledger-tabs" aria-label="Account access">
          <Link
            to="/login"
            className={`ledger-tab${pathname === '/login' ? ' ledger-tab--active' : ''}`}
          >
            Sign in
          </Link>
          <Link
            to="/register"
            className={`ledger-tab${pathname === '/register' ? ' ledger-tab--active' : ''}`}
          >
            New account
          </Link>
        </nav>

        <div className="ledger-card">
          <div className="ledger-card__rule" aria-hidden="true" />
          <div className="ledger-card__body">
            <h1 className="ledger-card__title">{title}</h1>
            {subtitle ? <p className="ledger-card__subtitle">{subtitle}</p> : null}
            {children}
          </div>
        </div>
      </div>
    </div>
  )
}
