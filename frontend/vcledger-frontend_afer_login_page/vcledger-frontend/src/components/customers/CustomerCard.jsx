import { useNavigate } from 'react-router-dom';
import { Phone } from 'lucide-react';
import Avatar from '../common/Avatar';
import { formatCurrency } from '../../utils/formatters';

/**
 * CustomerCard
 * -------------
 * One tile in AllCustomersPage's grid: photo/initials, name, phone, and
 * balance — negative (rust) means the customer owes money, positive
 * (teal) means they're in credit, per spec item 3.1.
 *
 * NOTE: your /customers endpoints don't return a balance field yet, so
 * this reads `customer.balance` defensively (falls back to 0 / "no
 * transactions yet") — wire the real field name through once the backend
 * returns it.
 */
export default function CustomerCard({ customer }) {
  const navigate = useNavigate();
  const hasBalance = typeof customer.balance === 'number';
  const balance = customer.balance ?? 0;
  const isDebit = balance < 0;

  return (
    <button
      type="button"
      onClick={() => navigate(`/customers/${customer.id}`)}
      className="surface-card flex flex-col items-center text-center gap-2.5 p-5 hover:-translate-y-0.5 hover:shadow-raised transition-transform duration-150 text-left"
    >
      <Avatar name={customer.name} imageUrl={customer.imageUrl} size="lg" />
      <p className="font-display font-semibold text-ink leading-tight">{customer.name}</p>

      {customer.phone && (
        <p className="flex items-center gap-1.5 text-xs text-ink-soft">
          <Phone size={12} />
          {customer.phone}
        </p>
      )}

      <div className="brass-divider w-16 my-1" />

      {hasBalance ? (
        <p className={`font-body font-bold text-sm ${isDebit ? 'text-debit' : 'text-credit'}`}>
          {formatCurrency(balance)}
          <span className="block text-[10px] font-medium text-ink-soft mt-0.5">
            {isDebit ? 'owes' : 'advance paid'}
          </span>
        </p>
      ) : (
        <p className="text-[11px] text-ink-soft/70">no transactions yet</p>
      )}
    </button>
  );
}
