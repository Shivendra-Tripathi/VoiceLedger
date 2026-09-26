import { History, AlertTriangle, ArrowLeftRight } from 'lucide-react';
import AppShell from '../components/layout/AppShell';
import PageLoader from '../components/common/PageLoader';
import EmptyState from '../components/common/EmptyState';
import Pagination from '../components/common/Pagination';
import DetailedTransactionCard from '../components/transactions/DetailedTransactionCard';
import { useTransactions } from '../hooks/useTransactions';

/**
 * TransactionsPage
 * ----------------
 * Dedicated page displaying all transactions done by the user (shopkeeper).
 * Implements pagination with at most 12 transactions per page.
 */
export default function TransactionsPage() {
  const {
    transactions,
    page,
    setPage,
    totalPages,
    totalElements,
    isLoading,
    error,
  } = useTransactions(12);

  return (
    <AppShell>
      <div className="max-w-4xl mx-auto px-4 sm:px-8 py-10">
        {/* Header */}
        <div className="flex items-center justify-between flex-wrap gap-4 mb-8">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-b from-brass-light to-brass flex items-center justify-center shadow-raised-sm border border-brass-dark/40">
              <History size={20} className="text-maroon-dark" />
            </div>

            <div>
              <h1 className="font-display text-xl font-semibold text-ink">
                Transaction History
              </h1>

              <p className="text-xs text-ink-soft">
                {totalElements > 0
                  ? `${totalElements} transaction${totalElements === 1 ? '' : 's'} recorded in the ledger`
                  : 'All recorded credits and debits'}
              </p>
            </div>
          </div>
        </div>

        {/* Loading State */}
        {isLoading && (
          <PageLoader label="Opening the ledger transactions…" />
        )}

        {/* Error State */}
        {!isLoading && error && (
          <EmptyState
            icon={AlertTriangle}
            title="Couldn't load transactions"
            description={error}
            tone="error"
          />
        )}

        {/* Empty State */}
        {!isLoading && !error && transactions.length === 0 && (
          <EmptyState
            icon={ArrowLeftRight}
            title="No transactions yet"
            description="Transactions recorded via voice or customer accounts will appear here."
          />
        )}

        {/* Transactions List */}
        {!isLoading && !error && transactions.length > 0 && (
          <div className="space-y-3">
            <div className="space-y-3">
              {transactions.map((tx, index) => (
                <DetailedTransactionCard
                  key={tx.id ?? `${tx.createdAt}-${index}`}
                  transaction={tx}
                />
              ))}
            </div>

            {/* Pagination (at most 12 per page) */}
            <Pagination
              page={page}
              totalPages={totalPages}
              onPageChange={setPage}
            />
          </div>
        )}
      </div>
    </AppShell>
  );
}

