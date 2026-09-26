
import { ArrowDownLeft, ArrowUpRight, Clock3, FileText } from 'lucide-react';
import { formatCurrency, formatDateTime } from '../../utils/formatters';

/**
 * TransactionInfoCard
 * -------------------
 * Displays complete information about a single transaction.
 *
 * Expected transaction shape:
 * {
 *   amount: BigDecimal/string/number,
 *   transactionType: 'CREDIT' | 'DEBIT',
 *   time: ISO timestamp,
 *   description: string
 * }
 */
export default function TransactionInfoCard({ transaction }) {
  if (!transaction) return null;

  const isCredit = transaction.transactionType === 'CREDIT';

  return (
    <article className="surface-pressed px-5 py-4">
      <div className="flex items-start gap-3">

        {/* Transaction type icon */}
        <div
          className={[
            'flex h-10 w-10 shrink-0 items-center justify-center rounded-full',
            isCredit
              ? 'bg-credit-soft text-credit'
              : 'bg-debit-soft text-debit',
          ].join(' ')}
        >
          {isCredit ? (
            <ArrowDownLeft size={18} strokeWidth={2.25} />
          ) : (
            <ArrowUpRight size={18} strokeWidth={2.25} />
          )}
        </div>

        {/* Transaction details */}
        <div className="min-w-0 flex-1">

          <div className="flex items-start justify-between gap-3">
            <div className="min-w-0">

              <p className="font-body text-sm font-semibold text-ink">
                {isCredit ? 'Credit' : 'Debit'}
              </p>

              {transaction.description && (
                <p className="mt-0.5 flex items-start gap-1.5 font-body text-xs text-ink-soft">
                  <FileText
                    size={12}
                    strokeWidth={2}
                    className="mt-0.5 shrink-0"
                  />
                  <span className="break-words">
                    {transaction.description}
                  </span>
                </p>
              )}

            </div>

            {/* Amount */}
            <p
              className={[
                'shrink-0 font-body text-base font-bold whitespace-nowrap',
                isCredit ? 'text-credit' : 'text-debit',
              ].join(' ')}
            >
              {isCredit ? '+' : '-'}
              {formatCurrency(transaction.amount)}
            </p>
          </div>

          {/* Timestamp */}
          {transaction.time && (
            <p className="mt-2 flex items-center gap-1.5 font-body text-xs text-ink-soft">
              <Clock3 size={12} strokeWidth={2} />
              {formatDateTime(transaction.time)}
            </p>
          )}

        </div>
      </div>
    </article>
  );
}