import { apiClient } from './client';

/**
 * userService
 * -----------
 * REST endpoints for managing user / shopkeeper profiles.
 */

/**
 * Fetch current authenticated user's profile.
 * GET /users/me
 */
export async function getUserProfile() {
  const response = await apiClient.get('/users/me');
  return response.data;
}

/**
 * Update current authenticated user's profile.
 * PUT /users/me
 *
 * Sends multipart/form-data:
 *   - 'user': JSON blob containing updated user fields { name }
 *   - 'image': optional image file
 *
 * @param {Object} data
 * @param {string} data.name - Updated shopkeeper name
 * @param {File|null} [data.image] - New profile image file
 * @returns {Promise<Object>} Updated user object from backend
 */
export async function updateUserProfile({ name, image }) {
  const formData = new FormData();

  formData.append(
    'user',
    new Blob([JSON.stringify({ name })], {
      type: 'application/json',
    })
  );

  if (image) {
    formData.append('image', image);
  }

  const response = await apiClient.put('/users/me', formData);
  return response.data;
}

