// NOT ROUTED. This was the placeholder home screen from the auth scaffold.
// VoiceLedgerPage (src/pages/VoiceLedgerPage.jsx) is the real post-login
// home per the app spec, so App.jsx sends /voice there instead of here.
// Left in place in case any of its markup/CSS classes are useful elsewhere.
import { useAuth } from '../context/AuthContext'

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
