import { ReceiptText, History } from 'lucide-react';
import EmptyState from '../common/EmptyState';
import TransactionInfoCard from './TransactionInfoCard';

/**
 * TransactionList
 * ----------------
 * Renders a customer's transaction history using TransactionInfoCard.
 *
 * Expected transaction shape:
 * {
 *   amount,
 *   transactionType,
 *   time,
 *   description
 * }
 */
export default function TransactionList({ transactions }) {
  if (!transactions || transactions.length === 0) {
    return (
      <EmptyState
        icon={ReceiptText}
        title="No transactions yet"
        description="Once you record a voice transaction for this customer, it will appear here with a timestamp."
      />
    );
  }

  return (
    <section className="space-y-4">

      {/* Section Header */}
      <div className="flex items-center justify-between px-1">
        <div className="flex items-center gap-2.5">
          <div className="flex items-center justify-center w-9 h-9 rounded-full bg-maroon/10 text-maroon">
            <History size={17} strokeWidth={2} />
          </div>

          <div>
            <h3 className="font-display text-lg font-semibold text-ink">
              Transaction History
            </h3>

            <p className="font-body text-xs text-ink-soft mt-0.5">
              {transactions.length}{' '}
              {transactions.length === 1
                ? 'transaction'
                : 'transactions'}{' '}
              on this page
            </p>
          </div>
        </div>
      </div>

      {/* Transaction Feed */}
      <div className="relative">

        {/* Timeline */}
        <div className="absolute left-[24px] top-5 bottom-5 w-px bg-paper-line" />

        <div className="space-y-3">
          {transactions.map((transaction, index) => (
            <div
              key={transaction.id ?? index}
              className="relative pl-12"
            >
              {/* Timeline Dot */}
              <div className="absolute left-[18px] top-1/2 -translate-y-1/2 z-10">
                <div className="w-3 h-3 rounded-full bg-paper-card border-2 border-brass shadow-sm" />
              </div>

              {/* Transaction Card */}
              <div className="transition-all duration-200 hover:-translate-y-0.5 hover:shadow-raised-sm">
                <TransactionInfoCard
                  transaction={transaction}
                />
              </div>
            </div>
          ))}
        </div>
      </div>

    </section>
  );
}