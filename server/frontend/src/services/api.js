import axios from 'axios';
import router from '../router';
import { authStore } from '../stores/authStore';
import { notificationService } from './notificationService';

const api = axios.create({
  baseURL: '/api',
});

// Flag to prevent multiple simultaneous session expiration handlers
let isHandling401 = false;

// Add JWT to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 errors
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && error.response.status === 401) {
      // Prevent multiple simultaneous 401 handlers
      if (isHandling401) {
        return Promise.reject(error);
      }

      isHandling401 = true;

      try {
        // Only handle session expiration if user was authenticated
        // This prevents showing "session expired" on the login page itself
        if (authStore.isAuthenticated) {
          // Save the current path for redirect after re-login
          const currentPath = router.currentRoute.value.fullPath;
          if (currentPath && currentPath !== '/login') {
            authStore.setRedirectPath(currentPath);
          }

          // Clear authentication state
          authStore.logout();

          // Show user-friendly notification
          notificationService.warning(
            'Session Expired',
            'Your session has expired. Please log in again.',
            { duration: 5000, autoClose: true }
          );

          // Redirect to login with session expired flag
          await router.push({ name: 'Login', query: { sessionExpired: 'true' } });
        }
      } finally {
        // Reset the flag after a short delay to allow the redirect to complete
        setTimeout(() => {
          isHandling401 = false;
        }, 1000);
      }
    }

    // Let other errors propagate
    return Promise.reject(error);
  }
);

export default api;

