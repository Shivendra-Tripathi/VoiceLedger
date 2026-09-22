import { apiClient } from './client';

/**
 * Confirm a customer selection for a pending voice operation.
 *
 * POST /api/voicecommand/confirmcustomer
 *      ?operationId=...
 *      &customerId=...
 */
export async function confirmCustomer({ operationId, customerId }) {
  if (!operationId) {
    throw new Error('This entry is missing its operation id.');
  }

  if (customerId === undefined || customerId === null) {
    throw new Error('No customer was selected.');
  }

  try {
    const response = await apiClient.post(
      '/voicecommand/confirmcustomer',
      null,
      {
        params: {
          operationId,
          customerId,
        },
      }
    );

    return response.data;
  } catch (error) {
    throw new Error(getErrorMessage(error));
  }
}

/**
 * Confirm a pending transaction.
 *
 * POST /api/voicecommand/confirm
 *      ?operationId=...
 */
export async function confirmTransaction({ operationId }) {
  if (!operationId) {
    throw new Error('This entry is missing its operation id.');
  }

  try {
    const response = await apiClient.post(
      '/voicecommand/confirm',
      null,
      {
        params: {
          operationId,
        },
      }
    );

    return response.data;
  } catch (error) {
    throw new Error(getErrorMessage(error));
  }
}

/**
 * Reject a pending transaction.
 *
 * POST /api/voicecommand/reject
 *      ?operationId=...
 */
export async function rejectTransaction({ operationId }) {
  if (!operationId) {
    throw new Error('This entry is missing its operation id.');
  }

  try {
    const response = await apiClient.post(
      '/voicecommand/cancel',
      null,
      {
        params: {
          operationId,
        },
      }
    );

    return response.data;
  } catch (error) {
    throw new Error(getErrorMessage(error));
  }
}

/**
 * Extract a useful error message from an Axios error.
 */
function getErrorMessage(error) {
  if (error.response) {
    const status = error.response.status;
    const data = error.response.data;

    if (data?.message) {
      return data.message;
    }

    if (data?.error) {
      return data.error;
    }

    if (typeof data === 'string' && data.trim()) {
      return data.slice(0, 180);
    }

    return `The server rejected that request (${status}).`;
  }

  if (error.request) {
    return 'Could not reach the server. Check your connection and try again.';
  }

  return error.message || 'Something went wrong while processing the request.';
}