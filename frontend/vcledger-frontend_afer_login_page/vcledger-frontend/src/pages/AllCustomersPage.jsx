import { useNavigate } from 'react-router-dom';
import { Users, UserPlus, AlertTriangle } from 'lucide-react';
import AppShell from '../components/layout/AppShell';
import CustomerGrid from '../components/customers/CustomerGrid';
import Pagination from '../components/common/Pagination';
import PageLoader from '../components/common/PageLoader';
import EmptyState from '../components/common/EmptyState';
import Button from '../components/common/Button';
import { useCustomers } from '../hooks/useCustomers';

/**
 * AllCustomersPage
 * ------------------
 * Directory of every customer, 12 per page (spec item 3), each card
 * showing name/photo/balance and linking through to CustomerPage.
 */
export default function AllCustomersPage() {
  const navigate = useNavigate();
  const { customers, page, setPage, totalPages, totalElements, isLoading, error } = useCustomers(12);

  return (
    <AppShell>
      <div className="max-w-5xl mx-auto px-4 sm:px-8 py-10">
        <div className="flex items-center justify-between flex-wrap gap-4 mb-8">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-b from-brass-light to-brass flex items-center justify-center shadow-raised-sm border border-brass-dark/40">
              <Users size={19} className="text-maroon-dark" />
            </div>
            <div>
              <h1 className="font-display text-xl font-semibold text-ink">All Customers</h1>
              <p className="text-xs text-ink-soft">
                {totalElements > 0 ? `${totalElements} in the ledger` : 'Your customer directory'}
              </p>
            </div>
          </div>
          <Button variant="brass" icon={UserPlus} onClick={() => navigate('/customers/new')}>
            New Customer
          </Button>
        </div>

        {isLoading && <PageLoader label="Opening the customer directory…" />}

        {!isLoading && error && (
          <EmptyState icon={AlertTriangle} title="Couldn't load customers" description={error} tone="error" />
        )}

        {!isLoading && !error && customers.length === 0 && (
          <EmptyState
            icon={Users}
            title="No customers yet"
            description="Add your first customer to start recording transactions."
            action={
              <Button variant="brass" icon={UserPlus} onClick={() => navigate('/customers/new')} className="mt-2">
                Add a customer
              </Button>
            }
          />
        )}

        {!isLoading && !error && customers.length > 0 && (
          <>
            <CustomerGrid customers={customers} />
            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>
    </AppShell>
  );
}
