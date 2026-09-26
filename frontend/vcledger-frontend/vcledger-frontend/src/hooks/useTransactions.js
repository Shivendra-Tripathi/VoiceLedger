import { useCallback, useEffect, useState } from 'react';
import { getTransactions } from '../api/transactionService';

/**
 * useTransactions
 * ---------------
 * Fetches paginated transactions for the authenticated user.
 * Page numbers are zero-indexed to match Spring Data Pageable.
 *
 * @param {number} pageSize - Number of transactions per page (default: 12)
 */
export function useTransactions(pageSize = 12) {
  const [page, setPage] = useState(0);
  const [transactions, setTransactions] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchTransactions = useCallback(
    async (pageNumber) => {
      setIsLoading(true);
      setError(null);

      try {
        const data = await getTransactions({
          page: pageNumber,
          size: pageSize,
        });

        setTransactions(data.content ?? []);
        setTotalPages(data.totalPages ?? 0);
        setTotalElements(data.totalElements ?? data.content?.length ?? 0);
      } catch (err) {
        setError('Could not load transactions. Please check that the server is running.');
      } finally {
        setIsLoading(false);
      }
    },
    [pageSize]
  );

  useEffect(() => {
    fetchTransactions(page);
  }, [page, fetchTransactions]);

  return {
    transactions,
    page,
    setPage,
    totalPages,
    totalElements,
    isLoading,
    error,
    refresh: () => fetchTransactions(page),
  };
}

export default useTransactions;

