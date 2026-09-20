import CustomerCard from './CustomerCard';

/**
 * CustomerGrid
 * -------------
 * Responsive grid of CustomerCard tiles — 12 per page, per spec item 3
 * (pagination itself lives in the page component / Pagination.jsx).
 */
export default function CustomerGrid({ customers }) {
  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
      {customers.map((customer) => (
        <CustomerCard key={customer.id} customer={customer} />
      ))}
    </div>
  );
}
