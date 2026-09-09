import { useAuth } from '../context/AuthContext'

// Placeholder home screen. Replace with the real ledger view (transaction
// list, voice command entry, balances, etc.) — routing and auth are
// already wired up around this component.
export default function Dashboard() {
  const { logout } = useAuth()

  return (
    <div className="ledger-page">
      <div className="ledger-book">
        <div className="ledger-card">
          <div className="ledger-card__rule" aria-hidden="true" />
          <div className="ledger-card__body">
            <h1 className="ledger-card__title">You're in.</h1>
            <p className="ledger-card__subtitle">
              This is where the voice-controlled ledger will live — transactions,
              balances, and the microphone button.
            </p>
            <button className="ledger-button ledger-button--ghost" onClick={logout}>
              Sign out
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
