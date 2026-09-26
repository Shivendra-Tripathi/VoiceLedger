
import { useNavigate } from 'react-router-dom';
import { Users, UserPlus, AlertTriangle, Search } from 'lucide-react';

import AppShell from '../components/layout/AppShell';
import CustomerGrid from '../components/customers/CustomerGrid';
import Pagination from '../components/common/Pagination';
import PageLoader from '../components/common/PageLoader';
import EmptyState from '../components/common/EmptyState';
import Button from '../components/common/Button';
import { useCustomers } from '../hooks/useCustomers';

export default function AllCustomersPage() {
  const navigate = useNavigate();

  const {
    customers,
    page,
    setPage,
    search,
    setSearch,
    totalPages,
    totalElements,
    isLoading,
    error
  } = useCustomers(12);

  return (
    <AppShell>
      <div className="max-w-5xl mx-auto px-4 sm:px-8 py-10">

        {/* Header */}
        <div className="flex items-center justify-between flex-wrap gap-4 mb-6">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-b from-brass-light to-brass flex items-center justify-center shadow-raised-sm border border-brass-dark/40">
              <Users size={19} className="text-maroon-dark" />
            </div>

            <div>
              <h1 className="font-display text-xl font-semibold text-ink">
                All Customers
              </h1>

              <p className="text-xs text-ink-soft">
                {totalElements > 0
                  ? `${totalElements} in the ledger`
                  : 'Your customer directory'}
              </p>
            </div>
          </div>

          <Button
            variant="brass"
            icon={UserPlus}
            onClick={() => navigate('/customers/new')}
          >
            New Customer
          </Button>
        </div>

        {/* Search Bar */}
        <div className="relative mb-8">
          <Search
            size={18}
            className="absolute left-4 top-1/2 -translate-y-1/2 text-ink-soft"
          />

          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search customers by name..."
            className="
              w-full
              pl-11 pr-4 py-3
              rounded-xl
              border border-ink-soft/20
              bg-white
              text-ink
              placeholder:text-ink-soft
              outline-none
              focus:border-brass
              focus:ring-2
              focus:ring-brass/20
            "
          />
        </div>

        {/* Loading */}
        {isLoading && (
          <PageLoader label="Opening the customer directory…" />
        )}

        {/* Error */}
        {!isLoading && error && (
          <EmptyState
            icon={AlertTriangle}
            title="Couldn't load customers"
            description={error}
            tone="error"
          />
        )}

        {/* Empty */}
        {!isLoading && !error && customers.length === 0 && (
          <EmptyState
            icon={Users}
            title={search ? 'No customers found' : 'No customers yet'}
            description={
              search
                ? `No customers containing "${search}" were found.`
                : 'Add your first customer to start recording transactions.'
            }
            action={
              !search && (
                <Button
                  variant="brass"
                  icon={UserPlus}
                  onClick={() => navigate('/customers/new')}
                  className="mt-2"
                >
                  Add a customer
                </Button>
              )
            }
          />
        )}

        {/* Customers */}
        {!isLoading && !error && customers.length > 0 && (
          <>
            <CustomerGrid customers={customers} />

            <Pagination
              page={page}
              totalPages={totalPages}
              onPageChange={setPage}
            />
          </>
        )}
      </div>
    </AppShell>
  );
}

