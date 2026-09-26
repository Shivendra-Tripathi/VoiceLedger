import { ChevronLeft, ChevronRight } from 'lucide-react';

/**
 * Pagination
 * -----------
 * Simple prev/next + page-number control. `page` is zero-indexed to match
 * Spring's Pageable, but displayed to the shopkeeper as 1-indexed.
 */
export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const canGoBack = page > 0;
  const canGoForward = page < totalPages - 1;

  return (
    <div className="flex items-center justify-center gap-3 pt-6">
      <button
        type="button"
        disabled={!canGoBack}
        onClick={() => onPageChange(page - 1)}
        aria-label="Previous page"
        className="w-9 h-9 rounded-full flex items-center justify-center bg-paper-card border border-paper-line shadow-raised-sm disabled:opacity-40 disabled:cursor-not-allowed hover:enabled:shadow-pressed transition-shadow"
      >
        <ChevronLeft size={18} className="text-maroon" />
      </button>

      <span className="font-body text-sm text-ink-soft px-2">
        Page <span className="font-semibold text-ink">{page + 1}</span> of {totalPages}
      </span>

      <button
        type="button"
        disabled={!canGoForward}
        onClick={() => onPageChange(page + 1)}
        aria-label="Next page"
        className="w-9 h-9 rounded-full flex items-center justify-center bg-paper-card border border-paper-line shadow-raised-sm disabled:opacity-40 disabled:cursor-not-allowed hover:enabled:shadow-pressed transition-shadow"
      >
        <ChevronRight size={18} className="text-maroon" />
      </button>
    </div>
  );
}
