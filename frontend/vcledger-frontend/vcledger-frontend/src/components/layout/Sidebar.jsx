import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { Mic, Users, UserPlus, BookMarked, X, LogOut, History, Settings, UserPen, KeyRound } from 'lucide-react';
import Avatar from '../common/Avatar';
import { useAuth } from '../../context/AuthContext';

/**
 * Sidebar
 * --------
 * The ChatGPT-style navigation rail, reimagined as the maroon cloth spine
 * of a ledger book. Holds the "New Customer" action (spec item 1.6) and a
 * link into the customer directory (spec item 1.5), plus the way back to
 * the voice page.
 *
 * `isOpen`/`onClose` drive the mobile off-canvas behaviour; on desktop the
 * sidebar is always visible (see AppShell).
 */
const navItemClasses = ({ isActive }) =>
  `flex items-center gap-3 rounded-xl px-3.5 py-2.5 font-body text-sm font-medium transition-colors ${
    isActive
      ? 'bg-maroon-dark/70 text-paper-card shadow-pressed'
      : 'text-paper/80 hover:bg-white/5 hover:text-paper-card'
  }`;

export default function Sidebar({ isOpen, onClose }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [settingsOpen, setSettingsOpen] = useState(false);

  const handleSignOut = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <>
      {/* Mobile scrim */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black/40 z-30 md:hidden"
          onClick={onClose}
          aria-hidden="true"
        />
      )}

      <aside
        className={`fixed md:static inset-y-0 left-0 z-40 w-72 shrink-0 bg-cloth text-paper flex flex-col transform transition-transform duration-200 ${
          isOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'
        }`}
      >
        {/* Brass corner-fitting seam down the spine */}
        <div className="absolute right-0 top-0 bottom-0 w-[3px] bg-gradient-to-b from-brass-light via-brass to-brass-dark opacity-70" />

        <div className="flex items-center justify-between px-5 pt-6 pb-4">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-lg bg-brass/90 flex items-center justify-center shadow-raised-sm border border-brass-dark/50">
              <BookMarked size={18} className="text-maroon-dark" />
            </div>
            <div>
              <p className="font-display font-semibold text-paper-card leading-tight">VCLedger</p>
              <p className="text-[11px] text-paper/60 leading-tight">the shop's khata, by voice</p>
            </div>
          </div>
          <button onClick={onClose} className="md:hidden text-paper/70 hover:text-paper-card" aria-label="Close menu">
            <X size={20} />
          </button>
        </div>

        <nav className="flex-1 px-4 space-y-1.5 overflow-y-auto">
          <NavLink to="/voice" className={navItemClasses} onClick={onClose}>
            <Mic size={17} />
            Voice Ledger
          </NavLink>

          <NavLink to="/customers/new" className={navItemClasses} onClick={onClose}>
            <UserPlus size={17} />
            New Customer
          </NavLink>

          <div className="pt-3 pb-1.5 px-3.5 text-[11px] uppercase tracking-wide text-paper/40 font-semibold">
            Directory
          </div>

          <NavLink to="/customers" className={navItemClasses} onClick={onClose}>
            <Users size={17} />
            All Customers
          </NavLink>

          <NavLink to="/transactions" className={navItemClasses} onClick={onClose}>
            <History size={17} />
            History
          </NavLink>
        </nav>

        <div className="relative px-4 py-4 border-t border-white/10 space-y-3">
          {/* User profile strip */}
          <div className="flex items-center gap-2.5 px-1">
            <Avatar name={user?.name ?? 'Shopkeeper'} photoUrl={user?.photoUrl} size="sm" />
            <div className="min-w-0 flex-1">
              <p className="font-body text-sm font-semibold text-paper-card truncate leading-snug">
                {user?.name ?? 'Shopkeeper'}
              </p>
              <p className="text-[11px] text-paper/60 truncate leading-none">
                {user?.type ?? 'Shopkeeper'}
              </p>
            </div>
          </div>

          {/* Settings popover menu (options appear above) */}
          {settingsOpen && (
            <>
              <div
                className="fixed inset-0 z-40"
                onClick={() => setSettingsOpen(false)}
                aria-hidden="true"
              />
              <div className="absolute bottom-16 left-4 right-4 z-50 bg-maroon-dark border border-brass/40 rounded-xl p-1.5 shadow-raised space-y-1">
                <div className="px-2.5 py-1 text-[10px] font-semibold text-paper/50 uppercase tracking-wider">
                  Settings
                </div>

                <button
                  type="button"
                  onClick={() => {
                    setSettingsOpen(false);
                    onClose?.();
                    navigate('/settings/profile');
                  }}
                  className="flex items-center gap-2.5 w-full rounded-lg px-2.5 py-2 text-sm font-medium text-paper hover:bg-white/10 hover:text-paper-card transition-colors text-left"
                >
                  <UserPen size={16} className="text-brass-light" />
                  Update Profile
                </button>

                <button
                  type="button"
                  onClick={() => {
                    setSettingsOpen(false);
                    onClose?.();
                    navigate('/settings/password');
                  }}
                  className="flex items-center gap-2.5 w-full rounded-lg px-2.5 py-2 text-sm font-medium text-paper hover:bg-white/10 hover:text-paper-card transition-colors text-left"
                >
                  <KeyRound size={16} className="text-brass-light" />
                  Update Password
                </button>
              </div>
            </>
          )}

          {/* Settings & Sign Out action buttons */}
          <div className="flex items-center gap-1.5 pt-0.5">
            <button
              type="button"
              onClick={() => setSettingsOpen((prev) => !prev)}
              className={`flex items-center justify-center p-2 rounded-xl transition-colors ${
                settingsOpen
                  ? 'bg-brass text-maroon-dark shadow-raised-sm'
                  : 'text-paper/70 hover:bg-white/10 hover:text-paper-card'
              }`}
              aria-label="Settings"
              title="Settings"
            >
              <Settings size={18} />
            </button>

            <button
              onClick={handleSignOut}
              className="flex-1 flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-medium text-paper/70 hover:bg-white/5 hover:text-paper-card transition-colors"
            >
              <LogOut size={16} />
              Sign out
            </button>
          </div>
        </div>
      </aside>
    </>
  );
}
