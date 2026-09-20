import { useCallback, useEffect, useState } from 'react';
import { getCustomers } from '../api/customerService';

/**
 * useCustomers
 * -------------
 * Fetches one page of customers (12 per page, per the spec) and exposes
 * simple page-navigation controls. Used by AllCustomersPage.
 */
export function useCustomers(pageSize = 12) {
  const [page, setPage] = useState(0);
  const [customers, setCustomers] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchPage = useCallback(async (pageNumber) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await getCustomers({ page: pageNumber, size: pageSize });
      setCustomers(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
      setTotalElements(data.totalElements ?? data.content?.length ?? 0);
    } catch (err) {
      setError('Could not load customers. Check that the backend is running.');
    } finally {
      setIsLoading(false);
    }
  }, [pageSize]);

  useEffect(() => {
    fetchPage(page);
  }, [page, fetchPage]);

  return {
    customers,
    page,
    setPage,
    totalPages,
    totalElements,
    isLoading,
    error,
    refresh: () => fetchPage(page),
  };
}
