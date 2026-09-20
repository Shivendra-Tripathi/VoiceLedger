import { useState } from 'react';
import { Menu, BookMarked } from 'lucide-react';
import Sidebar from './Sidebar';

/**
 * AppShell
 * ---------
 * Wraps every authenticated page: sidebar on the left (collapsible on
 * mobile via a hamburger topbar), page content on the right. Every page
 * component (VoiceLedgerPage, AllCustomersPage, etc.) renders inside this
 * via `children` — see App.jsx for how routes are wired to it.
 */
export default function AppShell({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="h-screen flex bg-paper overflow-hidden">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div className="flex-1 flex flex-col min-w-0">
        {/* Mobile-only topbar so the sidebar can stay off-canvas on small screens */}
        <header className="md:hidden flex items-center gap-3 px-4 py-3 bg-paper-card border-b border-paper-line">
          <button
            onClick={() => setSidebarOpen(true)}
            className="w-9 h-9 rounded-lg flex items-center justify-center border border-paper-line shadow-raised-sm"
            aria-label="Open menu"
          >
            <Menu size={18} className="text-maroon" />
          </button>
          <BookMarked size={16} className="text-brass-dark" />
          <span className="font-display font-semibold text-ink">VCLedger</span>
        </header>

        <main className="flex-1 min-h-0 overflow-y-auto">{children}</main>
      </div>
    </div>
  );
}
