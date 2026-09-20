import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Phone, Trash2, ArrowLeft, AlertTriangle } from 'lucide-react';
import AppShell from '../components/layout/AppShell';
import Avatar from '../components/common/Avatar';
import Button from '../components/common/Button';
import PageLoader from '../components/common/PageLoader';
import EmptyState from '../components/common/EmptyState';
import TransactionList from '../components/customers/TransactionList';
import { getCustomerById, getCustomerTransactions, deleteCustomer } from '../api/customerService';
import { formatCurrency } from '../utils/formatters';

/**
 * CustomerDetailPage
 * ---------------------
 * Shows one customer's header info + full transaction history, with a
 * delete action (spec item 4). See customerService.js for notes on the
 * delete and transactions endpoints, which weren't in your original spec.
 */
export default function CustomerDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [customer, setCustomer] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const [confirmingDelete, setConfirmingDelete] = useState(false);

  const load = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const [customerData, transactionData] = await Promise.all([
        getCustomerById(id),
        getCustomerTransactions(id),
      ]);
      setCustomer(customerData);
      setTransactions(transactionData);
    } catch (err) {
      setError('Could not load this customer. Check that the backend is running.');
    } finally {
      setIsLoading(false);
    }
  }, [id]);

  useEffect(() => {
    load();
  }, [load]);

  const handleDelete = async () => {
    setIsDeleting(true);
    try {
      await deleteCustomer(id);
      navigate('/customers');
    } catch (err) {
      setError('Could not delete this customer. Please try again.');
      setIsDeleting(false);
      setConfirmingDelete(false);
    }
  };

  if (isLoading) {
    return (
      <AppShell>
        <PageLoader label="Opening customer page…" />
      </AppShell>
    );
  }

  if (error && !customer) {
    return (
      <AppShell>
        <div className="max-w-2xl mx-auto px-4 sm:px-8 py-10">
          <EmptyState icon={AlertTriangle} title="Couldn't load customer" description={error} tone="error" />
        </div>
      </AppShell>
    );
  }

  return (
    <AppShell>
      <div className="max-w-2xl mx-auto px-4 sm:px-8 py-10">
        <button
          onClick={() => navigate('/customers')}
          className="flex items-center gap-1.5 text-sm text-ink-soft hover:text-maroon mb-6 font-body"
        >
          <ArrowLeft size={15} /> Back to all customers
        </button>

        {/* Header (spec item 4.1) */}
        <div className="surface-card p-6 sm:p-8 flex flex-col sm:flex-row sm:items-center gap-5 mb-6">
          <Avatar name={customer.name} imageUrl={customer.imageUrl} size="lg" />
          <div className="flex-1">
            <h1 className="font-display text-2xl font-semibold text-ink">{customer.name}</h1>
            {customer.phone && (
              <p className="flex items-center gap-1.5 text-sm text-ink-soft mt-1">
                <Phone size={13} /> {customer.phone}
              </p>
            )}
            {typeof customer.balance === 'number' && (
              <p className={`font-bold text-sm mt-2 ${customer.balance < 0 ? 'text-debit' : 'text-credit'}`}>
                {formatCurrency(customer.balance)} {customer.balance < 0 ? 'owed to you' : 'advance paid'}
              </p>
            )}
          </div>

          {/* Delete (spec item 4.2) */}
          {!confirmingDelete ? (
            <Button variant="danger" icon={Trash2} onClick={() => setConfirmingDelete(true)}>
              Delete
            </Button>
          ) : (
            <div className="flex flex-col gap-2 items-stretch">
              <p className="text-xs text-debit font-medium text-center">Delete for good?</p>
              <div className="flex gap-2">
                <Button variant="danger" onClick={handleDelete} disabled={isDeleting} className="flex-1">
                  {isDeleting ? 'Deleting…' : 'Yes, delete'}
                </Button>
                <Button variant="ghost" onClick={() => setConfirmingDelete(false)} disabled={isDeleting}>
                  Cancel
                </Button>
              </div>
            </div>
          )}
        </div>

        {error && <p className="text-sm text-debit font-medium mb-4">{error}</p>}

        {/* Transaction history (spec item 4.3) */}
        <h2 className="font-display text-base font-semibold text-ink mb-3">Transaction history</h2>
        <TransactionList transactions={transactions} />
      </div>
    </AppShell>
  );
}
