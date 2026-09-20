import { ReceiptText } from 'lucide-react';
import EmptyState from '../common/EmptyState';
import { formatCurrency, formatDateTime } from '../../utils/formatters';

/**
 * TransactionList
 * -----------------
 * Renders a customer's transaction history as ledger line-items (spec
 * item 4.3). Each row expects roughly { id, amount, type, createdAt,
 * note } — adjust the field names once the transactions endpoint
 * (see customerService.getCustomerTransactions) is confirmed.
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
    <div className="surface-card divide-y divide-paper-line">
      {transactions.map((tx) => {
        const isDebit = Number(tx.amount) < 0;
        return (
          <div key={tx.id} className="flex items-center justify-between gap-4 px-5 py-4">
            <div>
              <p className="font-body text-sm text-ink font-medium">
                {tx.note || tx.type || 'Transaction'}
              </p>
              <p className="text-xs text-ink-soft mt-0.5">{formatDateTime(tx.createdAt)}</p>
            </div>
            <p className={`font-body font-bold text-sm whitespace-nowrap ${isDebit ? 'text-debit' : 'text-credit'}`}>
              {formatCurrency(tx.amount)}
            </p>
          </div>
        );
      })}
    </div>
  );
}
