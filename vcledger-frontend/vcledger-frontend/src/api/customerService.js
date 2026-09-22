import { apiClient } from './client';

/**
 * All REST calls related to customers live here, matching the endpoints
 * you specified:
 *
 *   POST   /customers          -> createCustomer
 *   GET    /customers          -> getCustomers (paginated)
 *   GET    /customers/:id      -> getCustomerById
 *
 * ASSUMPTIONS (not in your spec — adjust freely, they're isolated here):
 *   DELETE /customers/:id      -> deleteCustomer
 *       Your CustomerPage asks for a delete button but no delete endpoint
 *       was given, so this follows standard REST convention. Change the
 *       URL/method below if your backend does it differently.
 *
 *   GET /customers/:id/transactions -> getCustomerTransactions
 *       CustomerPage needs a transaction list but no endpoint was given
 *       for it either. This call is wrapped so a 404 is treated as
 *       "no transactions yet" rather than a hard error, so the page still
 *       works today and just needs this URL confirmed later.
 */

export async function createCustomer({ name, phone, image }) {
  const formData = new FormData();

  formData.append(
    'customer',
    new Blob(
      [JSON.stringify({ name, phone })],
      { type: 'application/json' }
    )
  );

  if (image) {
    formData.append('image', image);
  }

  const response = await apiClient.post('/customers', formData);

  return response.data;
}

export async function getCustomers({ page = 0, size = 12, search = '' } = {}) {
  const response = await apiClient.get('/customers/balances', { params: { page, size, search } });
  return response.data; // Spring Page object: { content, totalPages, number, ... }
}

// returns the CustomerBalanceData response from Backend
export async function getCustomerById(id) {
  const response = await apiClient.get(`/customers/${id}/balance`);
  return response.data;
}

export async function deleteCustomer(id) {
  await apiClient.delete(`/customers/${id}`);
}

export async function getCustomerTransactions(id, page = 0, size = 12) {
  try {
    const response = await apiClient.get(
      `/customers/${id}/transactions`,
      {
        params: {
          page,
          size,
        },
      }
    );

    return response.data;
  } catch (error) {
    if (error.response?.status === 404) {
      return {
        content: [],
        totalPages: 0,
        totalElements: 0,
        number: page,
        size,
      };
    }

    throw error;
  }
}
