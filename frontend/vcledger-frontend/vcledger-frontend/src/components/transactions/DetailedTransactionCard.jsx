import { ArrowRight, ArrowLeft, Clock } from 'lucide-react';
import Avatar from '../common/Avatar';
import { formatDateTime } from '../../utils/formatters';
import { useAuth } from '../../context/AuthContext';

/**
 * DetailedTransactionCard
 * -----------------------
 * Displays complete transaction information in a compact, elegant ledger card.
 *
 * Visual layout:
 *   1) Left side: Shopkeeper photo & name
 *   2) Arrow: Direction of money/value movement (Shopkeeper -> Customer or Customer -> Shopkeeper)
 *   3) Mid section: Customer photo & name
 *   4) Right side: Amount involved & timestamp
 *   5) Compact sizing following the ledger design system
 *
 * @param {Object} props
 * @param {Object} props.transaction - Transaction object from backend
 */
export default function DetailedTransactionCard({ transaction }) {
  if (!transaction) return null;

  const { user } = useAuth();
  const { shopkeeper, customer, amount, moneyDirection, createdAt } = transaction;

  const isCurrentShopkeeper = !shopkeeper?.id || shopkeeper?.id === user?.id;
  const shopkeeperPhoto = (isCurrentShopkeeper && user?.photoUrl) || shopkeeper?.photoUrl;
  const shopkeeperName = (isCurrentShopkeeper && user?.name) || shopkeeper?.name || 'Shopkeeper';

  const movesToShopkeeper = moneyDirection === 'CUSTOMER_TO_SHOPKEEPER';
  const isCredit = !movesToShopkeeper; // SHOPKEEPER_TO_CUSTOMER = credit/goods given

  // Format amount or fallback gracefully if null
  const formattedAmount =
    amount !== null && amount !== undefined && !Number.isNaN(Number(amount))
      ? `₹${Number(amount).toLocaleString('en-IN')}`
      : '—';

  return (
    <article className="surface-card p-3 sm:p-4 hover:border-brass/50 transition-colors shadow-raised-sm flex flex-col sm:flex-row sm:items-center justify-between gap-3 sm:gap-4">
      {/* Parties & Movement Direction */}
      <div className="flex items-center justify-between sm:justify-start gap-2 sm:gap-5 flex-1 min-w-0">
        
        {/* 1) Shopkeeper (Left) */}
        <div className="flex items-center gap-2.5 min-w-0 max-w-[36%] sm:max-w-[200px]">
          <Avatar
            name={shopkeeperName}
            photoUrl={shopkeeperPhoto}
            size="sm"
          />
          <div className="min-w-0">
            <p className="font-body text-sm font-semibold text-ink truncate leading-snug">
              {shopkeeperName}
            </p>
            <span className="text-[11px] text-ink-soft leading-none">
              Shopkeeper
            </span>
          </div>
        </div>

        {/* 3) Movement Direction Arrow */}
        <div className="flex flex-col items-center justify-center shrink-0 px-1 sm:px-2">
          <div
            className={`w-7 h-7 rounded-full flex items-center justify-center transition-transform ${
              movesToShopkeeper
                ? 'bg-debit-soft text-debit'
                : 'bg-credit-soft text-credit'
            }`}
            title={
              movesToShopkeeper
                ? 'Payment from Customer to Shopkeeper'
                : 'Credit/Goods from Shopkeeper to Customer'
            }
          >
            {movesToShopkeeper ? (
              <ArrowLeft size={15} strokeWidth={2.25} />
            ) : (
              <ArrowRight size={15} strokeWidth={2.25} />
            )}
          </div>
          <span className="text-[10px] text-ink-soft mt-0.5 font-medium hidden sm:inline">
            {movesToShopkeeper ? 'Paid' : 'Gave Credit'}
          </span>
        </div>

        {/* 2) Customer (Mid) */}
        <div className="flex items-center gap-2.5 min-w-0 max-w-[36%] sm:max-w-[200px]">
          <Avatar
            name={customer?.name ?? 'Customer'}
            photoUrl={customer?.photoUrl}
            size="sm"
          />
          <div className="min-w-0">
            <p className="font-body text-sm font-semibold text-ink truncate leading-snug">
              {customer?.name ?? 'Customer'}
            </p>
            <span className="text-[11px] text-ink-soft leading-none">
              Customer
            </span>
          </div>
        </div>

      </div>

      {/* 4) Money Involved & Timestamp (Right) */}
      <div className="flex items-center sm:flex-col justify-between sm:justify-center sm:items-end shrink-0 pt-2 sm:pt-0 border-t sm:border-t-0 border-paper-line/50">
        <span
          className={`font-display text-base font-bold whitespace-nowrap ${
            isCredit ? 'text-credit' : 'text-debit'
          }`}
        >
          {formattedAmount}
        </span>

        {createdAt && (
          <span className="flex items-center gap-1 text-[11px] text-ink-soft mt-0.5 whitespace-nowrap">
            <Clock size={11} className="shrink-0 text-brass-dark" />
            {formatDateTime(createdAt)}
          </span>
        )}
      </div>
    </article>
  );
}

