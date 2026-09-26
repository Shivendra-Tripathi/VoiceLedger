import { BookOpen } from 'lucide-react';

/**
 * PageLoader
 * -----------
 * Full-area loading indicator, used while a page's primary data is still
 * being fetched. Keeps a hint of the ledger motif rather than a generic
 * spinner.
 */
export default function PageLoader({ label = 'Loading…' }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-24 text-ink-soft">
      <BookOpen size={28} className="animate-pulse text-brass-dark" />
      <p className="font-body text-sm">{label}</p>
    </div>
  );
}
