import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Phone,
  Trash2,
  ArrowLeft,
  AlertTriangle,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';

import AppShell from '../components/layout/AppShell';
import Avatar from '../components/common/Avatar';
import Button from '../components/common/Button';
import PageLoader from '../components/common/PageLoader';
import EmptyState from '../components/common/EmptyState';
import TransactionList from '../components/customers/TransactionList';

import {
  getCustomerById,
  getCustomerTransactions,
  deleteCustomer,
} from '../api/customerService';

import { formatCurrency } from '../utils/formatters';

const TRANSACTIONS_PER_PAGE = 12;

export default function CustomerDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [customer, setCustomer] = useState(null);
  const [transactions, setTransactions] = useState([]);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [isLoading, setIsLoading] = useState(true);
  const [isTransactionsLoading, setIsTransactionsLoading] = useState(false);

  const [error, setError] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const [confirmingDelete, setConfirmingDelete] = useState(false);

  const loadCustomer = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    try {
      const customerData = await getCustomerById(id);
      setCustomer(customerData);
    } catch (err) {
      setError(
        'Could not load this customer. Check that the backend is running.'
      );
    } finally {
      setIsLoading(false);
    }
  }, [id]);

  const loadTransactions = useCallback(async () => {
    setIsTransactionsLoading(true);

    try {
      const transactionPage = await getCustomerTransactions(
        id,
        currentPage,
        TRANSACTIONS_PER_PAGE
      );

      setTransactions(transactionPage.content ?? []);
      setTotalPages(transactionPage.totalPages ?? 0);
    } catch (err) {
      setError('Could not load transactions. Please try again.');
      setTransactions([]);
      setTotalPages(0);
    } finally {
      setIsTransactionsLoading(false);
    }
  }, [id, currentPage]);

  useEffect(() => {
    loadCustomer();
  }, [loadCustomer]);

  useEffect(() => {
    loadTransactions();
  }, [loadTransactions]);

  const handlePreviousPage = () => {
    if (currentPage > 0) {
      setCurrentPage((page) => page - 1);
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages - 1) {
      setCurrentPage((page) => page + 1);
    }
  };

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
          <EmptyState
            icon={AlertTriangle}
            title="Couldn't load customer"
            description={error}
            tone="error"
          />
        </div>
      </AppShell>
    );
  }

  const customerInfo = customer?.customer ?? customer;

  return (
    <AppShell>
      <div className="max-w-2xl mx-auto px-4 sm:px-8 py-10">

        {/* Back Button */}
        <button
          onClick={() => navigate('/customers')}
          className="flex items-center gap-1.5 text-sm text-ink-soft hover:text-maroon mb-6 font-body"
        >
          <ArrowLeft size={15} />
          Back to all customers
        </button>

        {/* Customer Header */}
        <div className="surface-card p-6 sm:p-8 flex flex-col sm:flex-row sm:items-center gap-5 mb-6">

          <Avatar
            name={customerInfo?.name}
            photoUrl={customerInfo?.photoUrl}
            size="lg"
          />

          <div className="flex-1">
            <h1 className="font-display text-2xl font-semibold text-ink">
              {customerInfo?.name || 'Customer'}
            </h1>

            {customerInfo?.phone && (
              <p className="flex items-center gap-1.5 text-sm text-ink-soft mt-1">
                <Phone size={13} />
                {customerInfo.phone}
              </p>
            )}

            {typeof customer?.balance === 'number' && (
              <p
                className={`font-bold text-sm mt-2 ${
                  customer.balance < 0
                    ? 'text-debit'
                    : 'text-credit'
                }`}
              >
                {formatCurrency(customer.balance)}{' '}
                {customer.balance < 0
                  ? 'owed to you'
                  : 'advance paid'}
              </p>
            )}
          </div>

          {/* Delete Button */}
          <div>
            {!confirmingDelete ? (
              <Button
                variant="danger"
                onClick={() => setConfirmingDelete(true)}
              >
                <Trash2 size={15} />
                Delete
              </Button>
            ) : (
              <div className="flex items-center gap-2">
                <Button
                  variant="danger"
                  onClick={handleDelete}
                  disabled={isDeleting}
                >
                  {isDeleting ? 'Deleting...' : 'Confirm'}
                </Button>

                <Button
                  variant="secondary"
                  onClick={() => setConfirmingDelete(false)}
                  disabled={isDeleting}
                >
                  Cancel
                </Button>
              </div>
            )}
          </div>
        </div>

        {/* Transactions Heading */}
        <div className="mb-3">
          <h2 className="font-display text-xl font-semibold text-ink">
            Transactions
          </h2>
        </div>

        {/* Transactions */}
        {isTransactionsLoading ? (
          <PageLoader label="Loading transactions…" />
        ) : (
          <>
            <TransactionList transactions={transactions} />

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="flex items-center justify-between mt-5">

                {/* Previous Page */}
                <button
                  type="button"
                  onClick={handlePreviousPage}
                  disabled={currentPage === 0}
                  aria-label="Previous page"
                  className="flex items-center justify-center w-10 h-10 rounded-full border border-paper-line bg-paper-card text-ink transition hover:bg-paper-muted disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  <ChevronLeft size={20} />
                </button>

                {/* Page Indicator */}
                <span className="font-body text-sm text-ink-soft">
                  Page {currentPage + 1} of {totalPages}
                </span>

                {/* Next Page */}
                <button
                  type="button"
                  onClick={handleNextPage}
                  disabled={currentPage >= totalPages - 1}
                  aria-label="Next page"
                  className="flex items-center justify-center w-10 h-10 rounded-full border border-paper-line bg-paper-card text-ink transition hover:bg-paper-muted disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  <ChevronRight size={20} />
                </button>

              </div>
            )}
          </>
        )}
      </div>
    </AppShell>
  );
}