import { apiClient } from './client';

/**
 * transactionService
 * ------------------
 * Handles transaction-related REST calls.
 *
 * GET /transactions/data
 * Automatically attaches JWT via apiClient interceptors.
 *
 * @param {Object} options
 * @param {number} options.page - Zero-indexed page number (default: 0)
 * @param {number} options.size - Items per page (default: 12)
 * @param {string} options.sort - Sort field and direction (default: 'createdAt,desc')
 * @returns {Promise<Object>} Spring Page containing content, totalPages, number, totalElements, etc.
 */
export async function getTransactions({ page = 0, size = 12, sort = 'createdAt,desc' } = {}) {
  const response = await apiClient.get('/transactions/data', {
    params: {
      page,
      size,
      sort,
    },
  });

  return response.data;
}

export { getCustomerTransactions } from './customerService';

