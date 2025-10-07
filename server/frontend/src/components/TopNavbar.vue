<template>
  <header class="top-navbar">
    <div class="top-navbar-content">
      <div class="top-navbar-spacer"></div>

      <!-- User Menu -->
      <div class="user-section">
        <button @click="toggleUserDropdown" class="user-btn" :class="{ active: showUserDropdown }">
          <div class="user-avatar">
            <span class="user-initials">{{ getUserInitials() }}</span>
          </div>
          <div class="user-info">
            <span class="username">{{ authStore.user?.username || 'User' }}</span>
          </div>
          <i class="bi bi-chevron-down dropdown-icon"></i>
        </button>

        <transition name="dropdown-fade">
          <div class="user-dropdown" v-if="showUserDropdown">
            <div class="dropdown-header">
              <div class="user-avatar-large">
                <span class="user-initials-large">{{ getUserInitials() }}</span>
              </div>
              <div class="dropdown-user-info">
                <div class="dropdown-username">{{ authStore.user?.username || 'User' }}</div>
                <div class="dropdown-email">{{ authStore.user?.email || 'user@example.com' }}</div>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <button @click="handleLogout" class="dropdown-item logout-item">
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
  gap: 0.875rem;
  padding: 0.5rem 0.875rem;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 50px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.user-btn:hover {
  background: #f9fafb;
  border-color: #d1d5db;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.user-btn.active {
  background: #f9fafb;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.user-avatar {
  width: 40px;
  height: 40px;
  min-width: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.9rem;
  color: white;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  transition: all 0.2s ease;
}

.user-btn:hover .user-avatar {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transform: scale(1.05);
}

.user-initials {
  letter-spacing: 0.5px;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-width: 0;
}

.username {
  font-weight: 600;
  font-size: 0.875rem;
  color: #111827;
  white-space: nowrap;
  line-height: 1.2;
}

.dropdown-icon {
  font-size: 0.75rem;
  color: #9ca3af;
  transition: transform 0.2s ease, color 0.2s ease;
  margin-left: -0.25rem;
}

.user-btn:hover .dropdown-icon {
  color: #6b7280;
}

.user-btn.active .dropdown-icon {
  transform: rotate(180deg);
  color: #667eea;
}

/* User Dropdown */
.user-dropdown {
  position: absolute;
  top: calc(100% + 0.75rem);
  right: 0;
  min-width: 280px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1),
              0 10px 10px -5px rgba(0, 0, 0, 0.04);
  padding: 0;
  z-index: 1000;
  overflow: hidden;
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  border-bottom: 1px solid #e5e7eb;
}

.user-avatar-large {
  width: 48px;
  height: 48px;
  min-width: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1.1rem;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.user-initials-large {
  letter-spacing: 0.5px;
}

.dropdown-user-info {
  flex: 1;
  min-width: 0;
}

.dropdown-username {
  font-weight: 600;
  font-size: 0.95rem;
  color: #111827;
  margin-bottom: 0.25rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-email {
  font-size: 0.8rem;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-divider {
  height: 1px;
  background: #e5e7eb;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.875rem;
  width: 100%;
  padding: 0.875rem 1.25rem;
  background: none;
  border: none;
  color: #374151;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.dropdown-item:hover {
  background: #fef2f2;
  color: #dc2626;
}

.logout-item:hover {
  background: #fee2e2;
}

.dropdown-item i {
  font-size: 1.125rem;
  color: #6b7280;
  transition: color 0.2s ease;
}

.dropdown-item:hover i {
  color: #dc2626;
}

/* Animations */
.dropdown-fade-enter-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-fade-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-fade-enter-from {
  opacity: 0;
  transform: translateY(-12px) scale(0.95);
}

.dropdown-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.98);
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

  .dropdown-icon {
    display: none;
  }

  .user-btn {
    padding: 0.375rem;
    gap: 0;
  }

  .user-avatar {
    width: 36px;
    height: 36px;
    min-width: 36px;
    font-size: 0.85rem;
  }

  .user-dropdown {
    min-width: 260px;
    right: -0.5rem;
  }

  .dropdown-header {
    padding: 1rem;
  }

  .user-avatar-large {
    width: 44px;
    height: 44px;
    min-width: 44px;
    font-size: 1rem;
  }

  .dropdown-item {
    padding: 0.75rem 1rem;
  }
}
</style>
