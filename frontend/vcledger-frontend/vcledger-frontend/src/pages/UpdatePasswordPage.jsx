import { useNavigate } from 'react-router-dom';
import { KeyRound, ArrowLeft, Shield } from 'lucide-react';
import AppShell from '../components/layout/AppShell';
import Button from '../components/common/Button';

/**
 * UpdatePasswordPage
 * ------------------
 * Page for updating password.
 */
export default function UpdatePasswordPage() {
  const navigate = useNavigate();

  return (
    <AppShell>
      <div className="max-w-lg mx-auto px-4 sm:px-8 py-10">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-1.5 text-sm text-ink-soft hover:text-maroon mb-6 font-body"
        >
          <ArrowLeft size={15} />
          Back
        </button>

        <div className="flex items-center gap-3 mb-6">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-b from-brass-light to-brass flex items-center justify-center shadow-raised-sm border border-brass-dark/40">
            <KeyRound size={19} className="text-maroon-dark" />
          </div>

          <div>
            <h1 className="font-display text-xl font-semibold text-ink">
              Update Password
            </h1>
            <p className="text-xs text-ink-soft">
              Manage your ledger account credentials
            </p>
          </div>
        </div>

        <div className="surface-card p-6 sm:p-8 text-center space-y-4">
          <div className="w-12 h-12 rounded-full bg-brass/15 text-brass-dark flex items-center justify-center mx-auto">
            <Shield size={24} />
          </div>
          <h2 className="font-display text-lg font-semibold text-ink">
            Password Update Coming Soon
          </h2>
          <p className="font-body text-sm text-ink-soft max-w-xs mx-auto">
            Password change management endpoint is currently under development.
          </p>
          <Button variant="brass" onClick={() => navigate('/voice')}>
            Return to Ledger
          </Button>
        </div>
      </div>
    </AppShell>
  );
}

