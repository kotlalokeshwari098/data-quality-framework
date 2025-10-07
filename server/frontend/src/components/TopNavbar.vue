<template>
  <header class="top-navbar">
    <div class="top-navbar-content">
      <div class="top-navbar-spacer"></div>

      <!-- User Menu -->
      <div class="user-section">
        <button @click="toggleUserDropdown" class="user-btn">
          <div class="user-avatar">
            <span class="user-initials">{{ getUserInitials() }}</span>
          </div>
          <div class="user-info">
            <span class="username">{{ authStore.user?.username || 'User' }}</span>
            <small>Online</small>
          </div>
          <i class="bi bi-chevron-down"></i>
        </button>

        <transition name="dropdown-fade">
          <div class="user-dropdown" v-if="showUserDropdown">
            <button @click="handleLogout" class="dropdown-item">
              <i class="bi bi-box-arrow-right"></i>
              <span>Sign Out</span>
            </button>
          </div>
        </transition>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { authStore } from '../stores/authStore.js'
import { notificationService } from '../services/notificationService.js'

const router = useRouter()
const showUserDropdown = ref(false)

onMounted(() => {
  // Close dropdown when clicking outside
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

const handleClickOutside = (event) => {
  if (!event.target.closest('.user-section')) {
    showUserDropdown.value = false
  }
}

const toggleUserDropdown = () => {
  showUserDropdown.value = !showUserDropdown.value
}

const handleLogout = () => {
  authStore.logout()
  notificationService.info('Signed Out', 'You have been successfully signed out.')
  router.push('/login')
  showUserDropdown.value = false
}

const getUserInitials = () => {
  const username = authStore.user?.username || 'U'
  return username.substring(0, 2).toUpperCase()
}
</script>

<style scoped>
/* Top Navigation Bar */
.top-navbar {
  position: fixed;
  top: 0;
  left: 260px;
  right: 0;
  height: 64px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  z-index: 900;
  transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.top-navbar-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1.5rem;
}

.top-navbar-spacer {
  flex: 1;
}

/* User Section in Top Bar */
.user-section {
  position: relative;
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 1rem;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-btn:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.user-avatar {
  width: 36px;
  height: 36px;
  min-width: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 2px solid #e5e7eb;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
  color: white;
}

.user-info {
  text-align: left;
}

.username {
  display: block;
  font-weight: 600;
  font-size: 0.9rem;
  color: #111827;
  white-space: nowrap;
}

.user-info small {
  color: #6b7280;
  font-size: 0.75rem;
}

.user-btn i.bi-chevron-down {
  font-size: 0.875rem;
  color: #6b7280;
  transition: transform 0.2s ease;
}

.user-btn:hover i.bi-chevron-down {
  transform: translateY(2px);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 0.5rem);
  right: 0;
  min-width: 200px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  padding: 0.5rem;
  z-index: 1000;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  padding: 0.75rem 1rem;
  background: none;
  border: none;
  border-radius: 8px;
  color: #374151;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.dropdown-item:hover {
  background: #fee2e2;
  color: #dc2626;
}

.dropdown-item i {
  font-size: 1.1rem;
  color: #6b7280;
}

.dropdown-item:hover i {
  color: #dc2626;
}

/* Animations */
.dropdown-fade-enter-active {
  transition: all 0.2s ease;
}

.dropdown-fade-leave-active {
  transition: all 0.15s ease;
}

.dropdown-fade-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}

/* Mobile Responsive */
@media (max-width: 768px) {
  .top-navbar {
    left: 0;
    height: 56px;
  }

  .top-navbar-content {
    padding: 0 4rem 0 1rem;
  }

  .user-info {
    display: none;
  }

  .user-btn i.bi-chevron-down {
    display: none;
  }

  .user-btn {
    padding: 0.5rem;
  }
}

/* Tablet Responsive */
@media (max-width: 1024px) and (min-width: 769px) {
  .top-navbar {
    left: 80px;
  }
}
</style>
