
import { useCallback, useEffect, useState } from 'react';
import { getCustomers } from '../api/customerService';

/**
 * useCustomers
 * ------------
 * Fetches paginated customers and supports customer-name searching.
 *
 * Search is managed internally by the hook.
 */
export function useCustomers(pageSize = 12) {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [customers, setCustomers] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchPage = useCallback(async (pageNumber, searchTerm) => {
    setIsLoading(true);
    setError(null);

    try {
      const data = await getCustomers({
        page: pageNumber,
        size: pageSize,
        search: searchTerm
      });

      setCustomers(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
      setTotalElements(
        data.totalElements ?? data.content?.length ?? 0
      );
    } catch (err) {
      setError(
        'Could not load customers. Check that the backend is running.'
      );
    } finally {
      setIsLoading(false);
    }
  }, [pageSize]);

  useEffect(() => {
    fetchPage(page, search);
  }, [page, search, fetchPage]);

  const handleSearchChange = useCallback((value) => {
    setSearch(value);
    setPage(0);
  }, []);

  return {
    customers,
    page,
    setPage,
    search,
    setSearch: handleSearchChange,
    totalPages,
    totalElements,
    isLoading,
    error,
    refresh: () => fetchPage(page, search),
  };
}

